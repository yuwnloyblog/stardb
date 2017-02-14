package com.yuwnloy.stardb.cmds;

import java.util.concurrent.locks.ReentrantLock;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.ReadOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuwnloy.stardb.cmds.Constants.BinlogCommand;
import com.yuwnloy.stardb.utils.ByteUtil;

/**
 * kv cmd
 * @author xiaoguang.gao
 *
 * @date Feb 4, 2017
 */
public class KvCmd extends AbstractCmd{
	private static Logger logger = LoggerFactory.getLogger(KvCmd.class);
	
	private static KvCmd instance;
	private static ReentrantLock lock = new ReentrantLock();
	public static KvCmd getInstance(DB db, ReadOptions options, BinlogQueue binlogs){
		if(instance==null){
			lock.lock();
			try{
				if(instance==null){
					instance = new KvCmd(db,options, binlogs);
				}
			}finally{
				lock.unlock();
			}
		}
		return instance;
	}
	
	private KvCmd(DB db, ReadOptions options, BinlogQueue binlogs){
		super(db, options, binlogs);
	}
	/**
	 * set kv
	 * @param key
	 * @param value
	 * @return
	 */
	public int set(byte[] key, byte[] value){
		if(key==null||key.length==0){
			logger.error("empty key!");
			return 0;
		}
		this.startTransation();
		try{
			this.db_put(key, value, BinlogCommand.KSET);
			this.commit();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			return -1;
		}finally{
			this.endTransaction();
		}
		return 1;
	}
	/**
	 * get kv
	 * @param key
	 * @return
	 */
	public byte[] get(byte[] key){
		if(key==null||key.length==0){
			logger.error("empty key!");
		}
		return this.db_get(key);
	}
	
	/**
	 * set value for key if this key not exist.
	 * set oldvalue+value for key if this key exist.
	 * @param key
	 * @param value
	 * @return the length of value
	 */
	public int append(byte[] key, byte[] value){
		if(key==null||key.length==0){
			logger.error("empty key!");
		}
		byte[] oldValue = this.db_get(key);
		byte[] newValue = null;
		if(oldValue==null||oldValue.length==0){//set for new kv
			newValue = value;
		}else{//append value 
			newValue = ByteUtil.ByteArrayMerge(oldValue, value);
		}
		int ret = this.set(key, newValue);
		if(ret==1)
			return newValue.length;
		return 0;
	}
	/**
	 * return the value of key, and set newValue for key.
	 * @param key
	 * @param newValue
	 * @return old value
	 */
	public byte[] getset(byte[] key, byte[] newValue){
		if(key==null||key.length==0){
			logger.error("empty key!");
		}
		byte[] oldValue = this.get(key);
		this.set(key, newValue);
		return oldValue;
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	public int incr(byte[] key){
		return this.incrby(key, 1);
	}
	public int incrby(byte[] key, long increment){
		if(key==null||key.length==0){
			logger.error("empty key!");
			return 0;
		}
		byte[] value = this.get(key);
		if(value!=null&&value.length>0){
			String v = new String(value);
			long vint = 0;
			try{
				vint = Long.parseLong(v);
				vint = vint + increment;
				this.set(key, String.valueOf(vint).getBytes());
			}catch(Exception e){
				logger.warn("The value of key "+new String(key)+" not number format.");
				return 0;
			}
		}else{
			this.set(key, String.valueOf(increment).getBytes());
		}
		return 1;
	}
	/**
	 * 
	 * @param key
	 * @return
	 */
	public int decr(byte[] key){
		return this.decrby(key, 1);
	}
	/**
	 * 
	 * @param key
	 * @param decrement
	 * @return
	 */
	public int decrby(byte[] key, long decrement){
		if(key==null||key.length==0){
			logger.error("empty key!");
			return 0;
		}
		byte[] value = this.get(key);
		if(value!=null&&value.length>0){
			String v = new String(value);
			long vint = 0;
			try{
				vint = Long.parseLong(v);
				vint = vint - decrement;
				this.set(key, String.valueOf(vint).getBytes());
			}catch(Exception e){
				logger.warn("The value of key "+new String(key)+" not number format.");
				return 0;
			}
		}else{
			this.set(key, String.valueOf(decrement).getBytes());
		}
		return 1;
	}
	/**
	 * set value for key only if key not exist.
	 * @param key
	 * @param value
	 * @return
	 */
	public int setnx(byte[] key, byte[] value){
		if(key==null||key.length==0){
			logger.error("empty key!");
			return 0;
		}
		byte[] oldValue = this.db_get(key);
		if(oldValue==null||oldValue.length==0){
			return this.set(key, value);
		}else{
			return 0;
		}
	}
	
	@Override
	public byte[] encode_key(byte[] key) {
		byte[] rets = ByteUtil.append(null, Constants.DataType.KV.getByte());
		rets = ByteUtil.append(rets, key);
		return rets;
	}
	@Override
	public int decode_key(byte[] slice, byte[] key) {
		// TODO Auto-generated method stub
		return 0;
	}
}

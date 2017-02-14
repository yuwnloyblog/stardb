package com.yuwnloy.stardb.cmds;

import java.util.concurrent.locks.ReentrantLock;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.ReadOptions;

import com.yuwnloy.stardb.cmds.Constants.BinlogCommand;
import com.yuwnloy.stardb.cmds.Constants.BinlogType;

public abstract class AbstractCmd {
	private final DB db;
	private final ReadOptions options;
	private final BinlogQueue binlogs;
	private final static ReentrantLock mutex = new ReentrantLock();
	public AbstractCmd(DB db, ReadOptions options, BinlogQueue binlogs){
		this.db = db;
		this.options = options;
		this.binlogs = binlogs;
	}
	
	protected void db_put(byte[] key, byte[] value, BinlogCommand cmd){
		byte[] encodedKey = this.encode_key(key);
		this.binlogs.Put(encodedKey, value);
		this.binlogs.add_log(BinlogType.SYNC, cmd, encodedKey);
	}
	
	protected void db_del(byte[] key, byte[] value, BinlogCommand cmd){
		byte[] encodedKey = this.encode_key(key);
		this.binlogs.Delete(encodedKey);
		this.binlogs.add_log(BinlogType.SYNC, cmd, encodedKey);
	}
	
	protected void add_log(byte[] key, BinlogCommand cmd){
		byte[] encodedKey = this.encode_key(key);
		this.binlogs.add_log(BinlogType.SYNC, cmd, encodedKey);
	}
	
	protected void commit(){
		this.binlogs.commit();
	}
	
	protected byte[] db_get(byte[] key){
		return this.db.get(this.encode_key(key));
	}
	
	protected void startTransation(){
		mutex.lock();
		this.binlogs.begin();
	}
	protected void endTransaction(){
		mutex.unlock();
	}
	
	protected BinlogQueue getBinlogs(){
		return this.binlogs;
	}
	
	public abstract byte[] encode_key(byte[] key);
	public abstract int decode_key(byte[] slice, byte[] key);
}

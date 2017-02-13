package com.yuwnloy.stardb;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBException;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.WriteBatch;
import org.iq80.leveldb.impl.WriteBatchImpl;
import org.iq80.leveldb.util.Slice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuwnloy.stardb.utils.ByteUtil;

public class BinlogQueue {
	private static final Logger logger = LoggerFactory.getLogger(BinlogQueue.class);
	private DB db;
	private long min_seq;
	private long last_seq;
	private long tran_seq;
	private int capacity = 20000000;
	private WriteBatch batch;
	private boolean enabled = true;
	public ReentrantLock mutex;
	
	private volatile boolean thread_quit;
	
	public BinlogQueue(DB db, boolean enabled, int capacity){
		this.batch = new WriteBatchImpl();
		this.db = db;
		this.enabled = enabled;
		this.capacity = capacity;
		
		this.min_seq = 0;
		this.last_seq = 0;
		this.tran_seq = 0;
		
//		Binlog log = this.find_last();
//		if(log!=null){
//			this.last_seq = log.seq();
//		}
		this.last_seq = this.find_lastSeq();
		System.out.println("last_seq:"+this.last_seq);
		// 下面这段代码是可能性能非常差!
		//if(this->find_next(0, &log) == 1){
		//	this->min_seq = log.seq();
		//}
		if(this.last_seq > this.capacity){
			this.min_seq = this.last_seq - this.capacity;
		}else{
			this.min_seq = 0;
		}
		
		Binlog nextLog = this.find_next(this.min_seq);
		if(nextLog!=null){
			this.min_seq = nextLog.seq();
		}
		
		if(this.enabled){
			logger.info("binlogs capacity: "+ this.capacity + ", min: "+this.min_seq + ", max: "+this.last_seq);
			// start cleaning thread
			this.thread_quit = false;
			//log_clean_thread_func(this);
		}
		
	}
	
	public BinlogQueue(DB db, boolean enabled){
		this(db, enabled, 20000000);
	}
	public BinlogQueue(DB db, int capacity){
		this(db, true, capacity);
	}
	
	public BinlogQueue(DB db){
		this(db, true, 20000000);
	}
	
	public void close(){
		if(this.enabled){
			this.thread_quit = true;
			for(int i=0;i<100;i++){
				if(!this.thread_quit){
					break;
				}
				try {
					Thread.sleep(10 * 1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		try {
			this.db.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void begin(){
		this.tran_seq = this.last_seq;System.out.println("begin tran_seq:"+this.tran_seq);
		this.batch = new WriteBatchImpl();
	}
	public void rollback(){
		this.tran_seq = 0;
	}
	public void commit() throws DBException{
		try{
			this.batch.put(this.encode_seq_key(-1), ByteUtil.long2Bytes(this.tran_seq));
			this.db.write(this.batch);
			this.last_seq = this.tran_seq;
			this.tran_seq = 0;
		}catch(DBException e){
			logger.error("Error when commit.",e);
			throw e;
		}
	}
	
	public void Put(final byte[] key, final byte[] value){
		this.batch.put(key, value);
	}
	
	public void Delete(final byte[] key){
		this.batch.delete(key);
	}
	
	public void add_log(Constants.BinlogType type, Constants.BinlogCommand cmd, final byte[] key){
		if(!this.enabled){
			return;
		}
		this.tran_seq ++;
		Binlog log = new Binlog(this.tran_seq, type, cmd, key);System.out.println("add tran_seq:"+this.tran_seq);
		this.batch.put(this.encode_seq_key(this.tran_seq), log.repr());
	}
	public void add_log(Constants.BinlogType type, Constants.BinlogCommand cmd, final String key){
		if(!this.enabled){
			return;
		}
		this.add_log(type, cmd, ByteUtil.toBytes(key));
	}
	
	public final Binlog get(long seq){
		byte[] val = this.db.get(this.encode_seq_key(seq));
		if(val!=null){
			Binlog log = new Binlog();
			log.load(val);
			return log;
		}
		return null;
	}
	
	public void update(long seq, Constants.BinlogType type, Constants.BinlogCommand cmd, final byte[] key){
		Binlog log = new Binlog(seq, type, cmd, key);
		this.db.put(this.encode_seq_key(seq), log.repr());
	}
	
	public void flush(){
		this.del_range(this.min_seq, this.last_seq);
	}
	
	/** @returns
	 1 : log.seq greater than or equal to seq
	 0 : not found
	 -1: error
	 */
	public final Binlog find_next(long nextSeq){
		Binlog log = null;
		log = this.get(nextSeq);
		if(log!=null){
			return log;
		}
		byte[] key_str = this.encode_seq_key(nextSeq);
		DBIterator it = this.db.iterator();
		it.seek(key_str);
		if(it.hasNext()){
			Entry<byte[], byte[]> entry = it.next();
			byte[] key = entry.getKey();
			if(this.decode_seq_key(key)!=0){
				byte[] val = entry.getValue();
				log = new Binlog();
				log.load(val);
			}
		}
		try {
			it.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return log;
	}
	public final long find_lastSeq(){
		byte[] seqBytes = this.db.get(this.encode_seq_key(-1));
		if(seqBytes!=null)
			return ByteUtil.bytes2Long(seqBytes);
		else
			return 0L;
	}
	/**
	 * need to test.
	 * @return
	 */
	public final Binlog find_last(){
		Binlog log = null;
		//byte[] key_str = this.encode_seq_key(Long.MAX_VALUE);
		DBIterator it = this.db.iterator();
		//it.seek(key_str);
		it.seekToLast();
		if(it.hasPrev()){
			Entry<byte[],byte[]> entry = it.prev();
			byte[] key = entry.getKey();
			if(this.decode_seq_key(key) != 0){
				byte[] val = entry.getValue();
				log = new Binlog();
				log.load(val);
			}
		}
		try {
			it.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return log;
	}
	public final String status(){
		StringBuilder sb = new StringBuilder();
		sb.append("    capacity : " + String.valueOf(this.capacity) + "\n");
		sb.append("    min_seq  : " + String.valueOf(this.min_seq) + "\n");
		sb.append("    tran_seq : " + String.valueOf(this.tran_seq) +"\n");
		sb.append("    last_seq  : " + String.valueOf(this.last_seq) + "");
		return sb.toString();
	}
	
	private byte[] 	encode_seq_key(long seq){
		byte[] ret = ByteUtil.append(null, Constants.DataType.SYNCLOG.getByte());
		ret = ByteUtil.append(ret, seq);
		return ret;
	}
	
	private long decode_seq_key(final byte[] key){
		long seq = 0;
		if(key!=null){
			if(key.length == Constants.SIZE_OF_LONG+1 &&key[0]==Constants.DataType.SYNCLOG.getByte()){
				byte[] longBytes = ByteUtil.substring(key, 1, Constants.SIZE_OF_LONG);
				seq = ByteUtil.bytes2Long(longBytes);
			}
		}
		return seq;
	}
	
	private static void log_clean_thread_func(BinlogQueue logQueue){
		Thread thread = new Thread(new Runnable(){

			@Override
			public void run() {
				while(!logQueue.thread_quit){
					if(logQueue.db == null)
						break;
					
					if(logQueue.last_seq - logQueue.min_seq < logQueue.capacity + 10000){
						try {
							Thread.sleep(50 * 1000);
						} catch (InterruptedException e) {
						}
						continue;
					}
					
					long start = logQueue.min_seq;
					long end = logQueue.last_seq - logQueue.capacity;
					
					logQueue.del_range(start, end);
					logQueue.min_seq = end + 1;
					logger.info(String.format("clean %d logs[%d ~ %d], %d left, max: %d", end-start+1, start, end, logQueue.last_seq - logQueue.min_seq + 1, logQueue.last_seq));;
				}
				logger.debug("binlog clean_thread quit");
				logQueue.thread_quit = false;
			}});
		thread.start();
	}
	
	private void del(long seq){
		this.db.delete(this.encode_seq_key(seq));
	}
	
	private void del_range(long start, long end){
		while(start <= end){
			for(int count = 0; start <= end && count < 1000; start++, count++){
				this.batch.delete(this.encode_seq_key(start));
			}
			this.db.write(this.batch);
		}
	}
	
	// 因为老版本可能产生了断续的binlog
	// 例如, binlog-1 存在, 但后面的被删除了, 然后到 binlog-100000 时又开始存在.
	private void clean_obsolete_binlogs(){
		byte[] key_str = this.encode_seq_key(this.min_seq);
		DBIterator iterator = this.db.iterator();
		iterator.seek(key_str);
//		if(it->Valid()){
//			it->Prev();
//		}
		long count = 0;
		while(iterator.hasNext()){
			byte[] key = iterator.next().getKey();
			long seq = this.decode_seq_key(key);
			if(seq == 0){
				break;
			}
			this.del(seq);
			count++;
		}
		try {
			iterator.close();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		if(count > 0){
			logger.info("clean_obsolete_binlogs: "+ count);
		}
	}
	private void merge(){
		Map<byte[], Long> key_map = new HashMap<byte[], Long>();
		long start = this.min_seq;
		long end = this.last_seq;
		int reduce_count = 0;
		long total = end - start + 1;
		logger.trace("merge begin");
		for(;start<=end; start ++){
			Binlog log = this.get(start);
			if(log!=null){
				if(log.type() == Constants.BinlogType.NOOP){
					continue;
				}
				if(key_map.containsKey(log.key())){
					long seq = key_map.get(log.key());
					this.update(seq, Constants.BinlogType.NOOP, Constants.BinlogCommand.NONE, "".getBytes());
					reduce_count ++;
				}
				key_map.put(log.key(), log.seq());
			}
		}
		logger.trace(String.format("merge reduce %d of %d binlogs", reduce_count, total));
	}
	
}

package com.yuwnloy.stardb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.ReadOptions;

public abstract class AbstractCmd {
	private final DB db;
	private final ReadOptions options;
	public AbstractCmd(DB db, ReadOptions options){
		this.db = db;
		this.options = options;
	}
	
	protected void db_put(byte[] key, byte[] value){
		this.db.put(this.encode_key(key), value);
	}
	
	protected byte[] db_get(byte[] key){
		return this.db.get(key);
	}
	
	public abstract byte[] encode_key(byte[] key);
	public abstract int decode_key(byte[] slice, byte[] key);
}

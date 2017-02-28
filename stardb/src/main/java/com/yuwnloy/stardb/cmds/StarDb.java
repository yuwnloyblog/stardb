package com.yuwnloy.stardb.cmds;

import java.io.File;
import java.io.IOException;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;
import org.iq80.leveldb.impl.Iq80DBFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StarDb {
	private static Logger logger = LoggerFactory.getLogger(StarDb.class);
	private KvCmd kvCmd = null;
	
	public StarDb(File dataDir){
		Options options = new Options();
		options.createIfMissing(true);
		Iq80DBFactory levelDbFactory = new Iq80DBFactory();
		try {
			DB db = levelDbFactory.open(dataDir, options);
			BinlogQueue logQueue = new BinlogQueue(db, true);
			this.kvCmd = new KvCmd(db, new ReadOptions(), logQueue);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("Can not init star db.");
		}
	}

	public KvCmd getKvCmd() {
		return kvCmd;
	}
	
}

package com.yuwnloy.stardb;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;

import com.yuwnloy.stardb.utils.ByteUtil;

public class Test {

	public static void main(String[] args) {
		Options options = new Options();
		options.createIfMissing(true);
		try {
			DB db = factory.open(new File("/tmp/stardb/example"), options);
			BinlogQueue logQueue = new BinlogQueue(db, true, 10);
			//Binlog log = logQueue.find_last();
			//System.out.println(log.seq());
			logQueue.begin();
//			
//			logQueue.add_log(Constants.BinlogType.SYNC, Constants.BinlogCommand.HSET, "aaa");
//			logQueue.add_log(Constants.BinlogType.SYNC, Constants.BinlogCommand.HSET, "bbb");
//			logQueue.commit();
//			String status = logQueue.status();
//			System.out.println(status);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

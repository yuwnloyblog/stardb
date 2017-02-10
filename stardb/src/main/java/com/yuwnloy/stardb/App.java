package com.yuwnloy.stardb;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;

import static org.iq80.leveldb.impl.Iq80DBFactory.*;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

public class App {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Options options = new Options();
		options.createIfMissing(true);
		DB db = factory.open(new File("/tmp/stardb/example"), options);
		try {
		  // Use the db in here....
			db.put("xiao".getBytes(), "value".getBytes());
			DBIterator iterator = db.iterator(new ReadOptions());
			while(iterator.hasNext()){
				Entry<byte[],byte[]> entry = iterator.next();
				System.out.println(new String(entry.getKey())+":"+new String(entry.getValue()));
			}
		} finally {
		  // Make sure you close the db to shutdown the 
		  // database and avoid resource leaks.
		  db.close();
		}
	}

}

package com.yuwnloy.stardb;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.util.DbIterator;

public class DbIteratorTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Options options = new Options();
		options.createIfMissing(true);
		try {
			DB db = factory.open(new File("/tmp/stardb/example"), options);
//			db.put("aaa".getBytes(), "vafds".getBytes());
//			db.put("bbb".getBytes(), "fdsfs".getBytes());
//			db.put("ccc".getBytes(), "fsfs".getBytes());
			DBIterator it = db.iterator();
		
			while(it.hasNext()){
				Entry<byte[], byte[]> entry = it.next();
				//System.out.println(new String(entry.getKey())+","+new String(entry.getValue()));
				//break;
			}
			System.out.println("***********");
//			it.prev();
//			it.prev();
			while(it.hasPrev()){
				Entry<byte[], byte[]> entry = it.prev();
				//System.out.println(new String(entry.getKey())+","+new String(entry.getValue()));
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}

package com.yuwnloy.stardb;

import static org.iq80.leveldb.impl.Iq80DBFactory.factory;

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;

import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.iq80.leveldb.Options;
import org.iq80.leveldb.ReadOptions;

import com.yuwnloy.stardb.cmds.BinlogQueue;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import static org.iq80.leveldb.impl.Iq80DBFactory.*;

public class BinlogQueueTest extends TestCase {
	private DB db = null;
	public BinlogQueueTest(String name){
		super(name);
		Options options = new Options();
		options.createIfMissing(true);
		try {
			this.db = factory.open(new File("/tmp/stardb/example"), options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	 /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( BinlogQueueTest.class );
    }
    
    public void testQueue(){
    	BinlogQueue logQueue = new BinlogQueue(this.db, true, 10);
    	this.assertNotNull(logQueue);
    }
}

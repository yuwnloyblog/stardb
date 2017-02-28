package com.yuwnloy.stardb.test;

import org.iq80.leveldb.impl.Iq80DBFactory;

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
import org.iq80.leveldb.ReadOptions;

import com.yuwnloy.stardb.cmds.Binlog;
import com.yuwnloy.stardb.cmds.BinlogQueue;
import com.yuwnloy.stardb.cmds.KvCmd;
import com.yuwnloy.stardb.utils.ByteUtil;

public class Test {

	public static void main(String[] args) {
		Options options = new Options();
		options.createIfMissing(true);
		Iq80DBFactory factory = new Iq80DBFactory();
		try {
			DB db = factory.open(new File("/tmp/stardb/example"), options);
			BinlogQueue logQueue = new BinlogQueue(db, true);
			
			KvCmd kvCmd = new KvCmd(db, new ReadOptions(), logQueue);
			foreachBinlog(kvCmd);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void getset(KvCmd kvCmd){
		byte[] ret = kvCmd.get("abc".getBytes());
		System.out.println("get:"+new String(ret));
		ret = kvCmd.getset("abc".getBytes(), "xiaoguang".getBytes());
		System.out.println("getset:"+new String(ret));
		ret = kvCmd.get("abc".getBytes());
		System.out.println("get:"+new String(ret));
	}
	
	public static void IncrTest(KvCmd kvCmd){
		kvCmd.incr("incrTest".getBytes());
		kvCmd.incr("incrTest".getBytes());
		byte[] ret = kvCmd.get("incrTest".getBytes());
		System.out.println("incr:"+new String(ret));
		
		
		kvCmd.incrby("incrTest".getBytes(),10);
		ret = kvCmd.get("incrTest".getBytes());
		System.out.println("after add 10:"+new String(ret));
	}
	
	public static void DecrTest(KvCmd kvCmd){
		//kvCmd.decr("incrTest".getBytes());
		byte[] ret = kvCmd.get("incrTest".getBytes());
		System.out.println("decr 1 :"+new String(ret));
		
		kvCmd.decrby("incrTest".getBytes(), 10);
		ret = kvCmd.get("incrTest".getBytes());
		System.out.println("decr 10 :"+new String(ret));
	}
	public static void SetNx(KvCmd kvCmd){
		int ret = kvCmd.setnx("null".getBytes(), "fdsfs".getBytes());
		System.out.println(ret+":"+new String(kvCmd.get("null".getBytes())));
		
		ret = kvCmd.setnx("null".getBytes(), "newvalue".getBytes());
		System.out.println(ret+":"+new String(kvCmd.get("null".getBytes())));
		
		ret = kvCmd.set("null".getBytes(), "forcevalue".getBytes());
		System.out.println(ret+":"+new String(kvCmd.get("null".getBytes())));
		
	}
	public static void foreachBinlog(KvCmd kvCmd){
//		BinlogQueue binlogs = kvCmd.getBinlogs();
//		for(long seq = 0;seq<100;seq++){
//			Binlog log = binlogs.find_next(seq);
//			if(log!=null){
//				System.out.println("log:"+log.dumps());
//			}else{
//				break;
//			}
//		}
	}
}

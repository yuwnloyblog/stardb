package com.yuwnloy.stardb;

public class Transaction {
	private BinlogQueue logs;
	public Transaction(BinlogQueue logs){
		this.logs = logs;
		this.logs.mutex.lock();
		this.logs.begin();
	}
	
	public void destroy(){
		this.logs.rollback();
		this.logs.mutex.unlock();
	}
}

package com.yuwnloy.stardb;

public class Constants {
	public final static int SIZE_OF_LONG = 8;
	public final static int BINLOG_HEADER_LEN = SIZE_OF_LONG + 2;
	public static enum BinlogType{
		NOOP((byte)0),
		SYNC((byte)1),
		MIRROR((byte)2),
		COPY((byte)3);
		private byte b;
		private BinlogType(byte b){
			this.b = b;
		}
		public byte getByte(){
			return this.b;
		}
		
		public static BinlogType getItem(byte b){
			for(BinlogType t : BinlogType.values()){
				if(t.getByte() == b){
					return t;
				}
			}
			return BinlogType.NOOP;
		}
	}
	public static enum BinlogCommand{
		NONE((byte)0),
		KSET((byte)1),
		KDEL((byte)2),
		HSET((byte)3),
		HDEL((byte)4),
		ZSET((byte)5),
		ZDEL((byte)6),
		
		QPUSH_BACK((byte)10),
		QPUSH_FRONT((byte)11),
		QPOP_BACK((byte)12),
		QPOP_FRONT((byte)13),
		QSET((byte)14),
		
		BEGIN((byte)7),
		END((byte)8),
		
		NSET((byte)15),
		NDEL((byte)16);
		
		private byte b;
		private BinlogCommand(byte b){
			this.b = b;
		}
		public byte getByte(){
			return this.b;
		}
		public static BinlogCommand getItem(byte b){
			for(BinlogCommand bc : BinlogCommand.values()){
				if(bc.getByte() == b)
					return bc;
			}
			return BinlogCommand.NONE;
		}
	}
	public static enum DataType {
		SYNCLOG((byte)1),
		KV((byte)'k'),//KV
		HASH((byte)'h'),//hashmap sorted by key
		HSize((byte)'H'),// size of hash 
		LIST((byte)'l'),//list
		SET((byte)'s'),//set
		ZSET((byte)'z'),//sorted set
		ZSIZE((byte)'Z'),//size of zset
		ZSCORE((byte)'c'),//score of zset
		MIN_PREFIX((byte)'H'),
		MAX_PREFIX((byte)'z');
		private byte mark;
		private DataType(byte mark){
			this.mark = mark;
		}
		
		public byte getByte(){
			return this.mark;
		}
		
		public static DataType getItem(byte mark){
			for(DataType ct : DataType.values()){
				if(ct.getByte()==mark){
					return ct;
				}
			}
			return KV;
		}
	}

}

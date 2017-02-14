package com.yuwnloy.stardb.cmds;

import org.iq80.leveldb.util.Slice;
import com.google.common.primitives.Bytes;
import com.yuwnloy.stardb.utils.ByteUtil;

public class Binlog {
	private byte[] buf;
	
	public Binlog(){
	}
	
	public Binlog(long seq, Constants.BinlogType type, Constants.BinlogCommand cmd, final byte[] key){
		this.buf = Bytes.concat(ByteUtil.long2Bytes(seq),new byte[]{type.getByte()}, new byte[]{cmd.getByte()}, key);
	}
	
	public int load(final byte[] s){
		if(s.length<Constants.BINLOG_HEADER_LEN){
			return -1;
		}
		this.buf = s;
		return 0;
	}
	
	public final long seq(){
		return ByteUtil.bytes2Long(ByteUtil.substring(this.buf, 0, Constants.SIZE_OF_LONG));
	}
	
	public final Constants.BinlogType type(){
		byte t = this.buf[Constants.SIZE_OF_LONG];
		return Constants.BinlogType.getItem(t);
	}
	
	public final Constants.BinlogCommand cmd(){
		byte c = this.buf[Constants.SIZE_OF_LONG+1];
		return Constants.BinlogCommand.getItem(c);
	}
	
	public final byte[] key(){
		return ByteUtil.substring(this.buf, Constants.BINLOG_HEADER_LEN, this.buf.length-Constants.BINLOG_HEADER_LEN);
	}
	
	public final byte[] data(){
		return this.buf;
	}
	
	public final int size(){
		return this.buf.length;
	}
	
	public final byte[] repr(){
		return this.buf;
	}
	
	public final String dumps(){
		StringBuilder str=new StringBuilder();
		if(this.buf==null||this.buf.length<Constants.BINLOG_HEADER_LEN){
			return str.toString();
		}
		String seqStr = String.valueOf(this.seq());
		if(seqStr.length()<19){
			for(int i=0;i<19-seqStr.length();i++){
				str.append("0");
			}
		}
		str.append(seqStr).append(" ");
		
		if(this.type() == Constants.BinlogType.NOOP){
			str.append("noop ");
		}else if(this.type() == Constants.BinlogType.SYNC){
			str.append("sync ");
		}else if(this.type() == Constants.BinlogType.MIRROR){
			str.append("mirror ");
		}else if(this.type() == Constants.BinlogType.COPY){
			str.append("copy ");
		}
		
		if(this.cmd() == Constants.BinlogCommand.NONE){
			str.append("none ");
		}else if(this.cmd() == Constants.BinlogCommand.KSET){
			str.append("set ");
		}else if(this.cmd() == Constants.BinlogCommand.KDEL){
			str.append("del ");
		}else if(this.cmd() == Constants.BinlogCommand.HSET){
			str.append("hset ");
		}else{
			str.append("unknown ");
		}
		
		str.append(new String(this.key()));
		
		return str.toString();
	}
	
	public static void main(String[] args){
		//public Binlog(long seq, byte type, byte cmd, final Slice key)
		Binlog log = new Binlog(Long.MAX_VALUE,Constants.BinlogType.SYNC,Constants.BinlogCommand.KSET,"mykey".getBytes());
		
		System.out.println("seq:"+log.seq());
		System.out.println("type:"+log.type());
		System.out.println("cmd:"+log.cmd());
		System.out.println("key:"+new String(log.key()));
		System.out.println("repr:"+new String(log.repr()));
		System.out.println("dumps:"+log.dumps());
		
		Binlog l = new Binlog();
		l.load(log.repr());
		
		System.out.println(l.seq());
		System.out.println(l.dumps());
	}
}

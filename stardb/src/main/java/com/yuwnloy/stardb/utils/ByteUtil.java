package com.yuwnloy.stardb.utils;

import java.io.UnsupportedEncodingException;

import com.google.common.primitives.Bytes;

public class ByteUtil {
	private static final String CHARSET = "UTF-8";
	/**
	 * merge two byte array to one.
	 * 
	 * @param front
	 * @param back
	 * @return
	 */
	public static byte[] ByteArrayMerge(byte[] front, byte[] back) {
//		byte[] newBytes = new byte[front.length + back.length];
//		System.arraycopy(front, 0, newBytes, 0, front.length);
//		System.arraycopy(back, 0, newBytes, front.length, back.length);
		byte[] bytes = Bytes.concat(front,back);
		return bytes;
	}
	
	public static byte[] toBytes(String str){
		if(str!=null){
			try {
				return str.getBytes(CHARSET);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}
	
	public static String toString(byte[] bytes){
		if(bytes!=null&&bytes.length>0){
			try {
				return new String(bytes, CHARSET);
			} catch (UnsupportedEncodingException e) {
			}
		}
		return null;
	}

	public static byte[] long2Bytes(long num) {
		byte[] byteNum = new byte[8];
		for (int ix = 0; ix < 8; ++ix) {
			int offset = 64 - (ix + 1) * 8;
			byteNum[ix] = (byte) ((num >> offset) & 0xff);
		}
		return byteNum;
	}
	public static byte[] int2Bytes(int num) {  
//        byte[] byteNum = new byte[4];  
//        for (int ix = 0; ix < 4; ++ix) {  
//            int offset = 32 - (ix + 1) * 8;  
//            byteNum[ix] = (byte) ((num >> offset) & 0xff);  
//        }  
//        return byteNum;   
		  byte[] byteNum = new byte[4];
		  byteNum[0] = (byte) (0xff & num);
		  byteNum[1] = (byte) ((0xff00 & num) >> 8);
		  byteNum[2] = (byte) ((0xff0000 & num) >> 16);
		  byteNum[3] = (byte) ((0xff000000 & num) >> 24);
		  return byteNum;
    }  
  
    public static int bytes2Int(byte[] byteNum) {  
//        int num = 0;  
//        for (int ix = 0; ix < 4; ++ix) {  
//            num <<= 8;  
//            num |= (byteNum[ix] & 0xff);  
//        }  
//        return num;  
    	return (byteNum[0] & 0xff) |
                (byteNum[1] & 0xff) << 8 |
                (byteNum[2] & 0xff) << 16 |
                (byteNum[3] & 0xff) << 24;
    } 
	public static long bytes2Long(byte[] byteNum) {
		long num = 0;
		for (int ix = 0; ix < 8; ++ix) {
			num <<= 8;
			num |= (byteNum[ix] & 0xff);
		}
		return num;
	}

	public static byte int2OneByte(int num) {
		return (byte) (num & 0x000000ff);
	}

	public static int oneByte2Int(byte byteNum) {
		// 针对正数的int
		return byteNum > 0 ? byteNum : (128 + (128 + byteNum));
	}
	/**
	 * 
	 * @param bytes
	 * @param start
	 * @param len
	 * @return
	 */
	public static byte[] substring(byte[] bytes, int startIndex, int len){
		byte[] subBytes = new byte[len];
		System.arraycopy(bytes, startIndex, subBytes, 0, len);
		return subBytes;
	}
	
	public static byte[] append(byte[] srcBytes, byte b){
		return append(srcBytes, new byte[]{b});
	}
	public static byte[] append(byte[] srcBytes, byte[] appendBytes){
		if(srcBytes==null||srcBytes.length==0){
			return appendBytes;
		}else if(appendBytes==null||appendBytes.length==0){
			return srcBytes;
		}
		return Bytes.concat(srcBytes, appendBytes);
	}
	public static byte[] append(byte[] srcBytes, long l){
		byte[] lBytes = ByteUtil.long2Bytes(l);
		return append(srcBytes, lBytes);
	}
	public static byte[] append(byte[] srcBytes, int i){
		byte[] iBytes = ByteUtil.int2Bytes(i);
		return append(srcBytes, iBytes);
	}
	public static byte[] append(byte[] srcBytes, String str){
		if(str!=null){
			try {
				return append(srcBytes, str.getBytes(CHARSET));
			} catch (UnsupportedEncodingException e) {
				return srcBytes;
			}
		}else{
			return srcBytes;
		}
	}
}

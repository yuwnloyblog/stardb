package com.yuwnloy.stardb.servers.responses;

public class CmdResponseFactory {
	public static String resOk(){
		return "+OK\r\n";
	}
	public static String resErr(String err){
		return "-"+err+"\r\n";
	}
	public static String resNotSupport(){
		return "-not support\r\n";
	}
	/**
	 * $5\r\n
	 * VALUE\r\n
	 */
	public static String resOneValue(String value){
		if(value!=null&&!value.equals(""))
			return "$"+value.length()+"\r\n"+value+"\r\n";
		return null;
	}
	
}

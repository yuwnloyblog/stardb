package com.yuwnloy.stardb.utils;

public class Helps {
	public static final long CmdTimeout = 5*1000;
	public static int toInt(Object obj){
		int ret = 0;
		try{
			ret = Integer.parseInt(obj.toString());
		}catch(Exception e){
			ret = 0;
		}
		return ret;
	}
}

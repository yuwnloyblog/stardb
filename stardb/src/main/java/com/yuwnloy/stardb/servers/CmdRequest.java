package com.yuwnloy.stardb.servers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import com.yuwnloy.stardb.utils.Helps;

import io.netty.channel.Channel;

public class CmdRequest {
	private final int count;
	private final long timestamp = System.currentTimeMillis();
	private List<String> list = new ArrayList<String>();
	private boolean ok = false;
	private Channel clientChannel;
	private final String identy;
	private ConcurrentHashMap<String,CmdRequest> map;
	public CmdRequest(int count, Channel clientChannel, ConcurrentHashMap<String,CmdRequest> map, String identy){
		this.count = count;
		this.clientChannel = clientChannel;
		this.map = map;
		this.identy = identy;
	}
	
	public void add(String str){
		if(str!=null&&!str.trim().equals("")){
			list.add(str);
			if(list.size() == count*2){
				this.ok = true;
				this.map.remove(this.identy);
				CmdRequestProcessor.process(this);
			}
		}
	}
	
	public List<String> getList(){
		return this.list;
	}
	
	public boolean isOk(){
		return this.ok;
	}
	
	public boolean isTimeout(){
		if(System.currentTimeMillis()-this.timestamp>Helps.CmdTimeout)
			return true;
		return false;
	}
	
	public Channel getClientChannel(){
		return this.clientChannel;
	}
	
	public void writeResponse(String result){
		this.clientChannel.writeAndFlush(result);
	}
	
	public void destroy(){
		this.clientChannel.close();
	}
}

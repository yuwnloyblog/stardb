package com.yuwnloy.stardb.servers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yuwnloy.stardb.utils.Helps;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class CmdHandler extends ChannelHandlerAdapter {
	private static ConcurrentHashMap<String,CmdRequest> map = new ConcurrentHashMap<String, CmdRequest>();
	//private static ReentrantLock lock = new ReentrantLock();
	private static final Logger logger = LoggerFactory.getLogger(CmdHandler.class);
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)throws Exception{
		String identy = ctx.channel().remoteAddress().toString();
		String body = (String)msg;
		if(body.startsWith("*")){
			int count = Helps.toInt(body.substring(1));
			if(count>0){
				map.remove(identy);
				//create new cmd Request
				CmdRequest req = new CmdRequest(count, ctx.channel(), map, identy);
				map.putIfAbsent(identy, req);
			}
		}else{
			if(map.containsKey(identy)){
				CmdRequest req = map.get(identy);
				if(req!=null){
					req.add(body);
				}
			}
		}
	}
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx)throws Exception{
		ctx.flush();
	}
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
		logger.warn("Unexpected exception from downstream:" + cause.getMessage());
		ctx.close();
	}
}

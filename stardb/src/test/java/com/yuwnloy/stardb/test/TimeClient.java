package com.yuwnloy.stardb.test;

import java.util.Random;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class TimeClient {
	private Channel cf;
	static EventLoopGroup group = new NioEventLoopGroup();
	public void connect(int port, String host)throws Exception{
		
		try{
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
			.option(ChannelOption.TCP_NODELAY, true)
			.handler(new ChannelInitializer<SocketChannel>(){

				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
//					ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
//					Integer.MAX_VALUE, 0, 4, 0, 4));

					//ch.pipeline().addLast(new LengthFieldPrepender(4));
					ch.pipeline().addLast(new StringEncoder());
					ch.pipeline().addLast(new StringDecoder());
					ch.pipeline().addLast(new TimeClientHandler());
				}
				
			});
			ChannelFuture f = b.connect(host,port).sync();
			this.cf = f.channel();
			//System.out.println(f.isSuccess());
			//f.channel().close();
			//f.channel().closeFuture().sync();
		}finally{
			//group.shutdownGracefully();
		}
	}
	public void send(Object obj){
		this.cf.writeAndFlush(obj);
	}
	public void close(){
		try {
			this.cf.closeFuture().sync();
			group.shutdownGracefully();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args)throws Exception{
		TimeClient tc = new TimeClient();
		try {
			tc.connect(8888, "127.0.0.1");
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		tc.send("xixixixixxi\r\n");
		Thread.sleep(1000);
		//tc.close();
	}
}

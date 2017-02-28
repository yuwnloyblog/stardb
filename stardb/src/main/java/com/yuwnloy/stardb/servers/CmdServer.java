package com.yuwnloy.stardb.servers;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class CmdServer {
	public void bind(int port)throws Exception{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try{
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup,workerGroup).channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 1024).childHandler(new ChildChannelHandler());
			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		
		}finally{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
	private class ChildChannelHandler extends ChannelInitializer<SocketChannel>{

		@Override
		protected void initChannel(SocketChannel ch) throws Exception {
			ch.pipeline().addLast(new LineBasedFrameDecoder(1024));

			
			//ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(
            //Integer.MAX_VALUE, 0, 4, 0, 4));
			//ch.pipeline().addLast(new LengthFieldPrepender(4));
			
			//ByteBuf delimiter = Unpooled.copiedBuffer("\r\n".getBytes());
			//ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024,delimiter));
			
			
			ch.pipeline().addLast(new StringEncoder());
			ch.pipeline().addLast(new StringDecoder());
			ch.pipeline().addLast(new CmdHandler());
		}
		
	}
	public static void main(String[] args)throws Exception{
		
	}
}

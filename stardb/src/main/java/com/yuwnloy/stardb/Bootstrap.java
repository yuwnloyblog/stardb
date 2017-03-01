package com.yuwnloy.stardb;

import java.io.File;

import com.yuwnloy.stardb.cmds.StarDb;
import com.yuwnloy.stardb.servers.CmdHandler;
import com.yuwnloy.stardb.servers.CmdRequestProcessor;
import com.yuwnloy.stardb.servers.CmdServer;

import io.netty.channel.Channel;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
		
		new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					for(String k : CmdHandler.testMap.keySet()){
						Channel ch = CmdHandler.testMap.get(k);
						System.out.println("id:"+k+",\tactive:"+ch.isWritable()+"\t"+ch);
					}
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
				}
			}}).start();
		
		
		StarDb starDb = new StarDb(new File("/tmp/stardb/example"));
		CmdRequestProcessor.init(starDb);
		/**
		 * start to listen 
		 */
		int port = 8888;
		if(args!=null &&args.length>0){
			try{
				port = Integer.valueOf(args[0]);
			}catch(NumberFormatException e){
				
			}
			
		}
		new CmdServer().bind(port);
	}

}

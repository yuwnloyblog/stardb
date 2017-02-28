package com.yuwnloy.stardb;

import java.io.File;

import com.yuwnloy.stardb.cmds.StarDb;
import com.yuwnloy.stardb.servers.CmdRequestProcessor;
import com.yuwnloy.stardb.servers.CmdServer;

public class Bootstrap {

	public static void main(String[] args) throws Exception {
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

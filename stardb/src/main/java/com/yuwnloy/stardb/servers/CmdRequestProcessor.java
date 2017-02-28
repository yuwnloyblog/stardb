package com.yuwnloy.stardb.servers;

import java.util.HashMap;
import java.util.List;

import com.yuwnloy.stardb.cmds.StarDb;
import com.yuwnloy.stardb.servers.cmdprocs.ICmdProc;
import com.yuwnloy.stardb.servers.cmdprocs.kv.GetProc;
import com.yuwnloy.stardb.servers.cmdprocs.kv.SetProc;
import com.yuwnloy.stardb.servers.responses.CmdResponseFactory;

public class CmdRequestProcessor {
	public static HashMap<String,ICmdProc> processors = new HashMap<String,ICmdProc>();
	public static void init(StarDb starDb){
		if(starDb!=null){
			processors.put("set", new SetProc(starDb.getKvCmd()));
			processors.put("get", new GetProc(starDb.getKvCmd()));
		}
	}
	public static void process(CmdRequest request){
		if(request.getClientChannel()!=null&&request.getClientChannel().isActive()&&request.getClientChannel().isOpen()){
			List<String> list = request.getList();
			
			String cmd = list.get(1);
			String result = CmdResponseFactory.resNotSupport();
			
			ICmdProc proc = processors.get(cmd);
			if(proc!=null){
				String ret = proc.process(list.subList(2, list.size()));
				if(ret!=null){
					result = ret;
				}
			}
			
			request.writeResponse(result);
			request.destroy();
		}
	}
}

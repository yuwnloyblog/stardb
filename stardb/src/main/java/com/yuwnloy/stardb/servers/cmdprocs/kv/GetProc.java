package com.yuwnloy.stardb.servers.cmdprocs.kv;

import java.util.List;

import com.yuwnloy.stardb.cmds.KvCmd;
import com.yuwnloy.stardb.servers.cmdprocs.ICmdProc;
import com.yuwnloy.stardb.servers.responses.CmdResponseFactory;
import com.yuwnloy.stardb.utils.ByteUtil;

public class GetProc implements ICmdProc{
	private final KvCmd kvCmd;
	public GetProc(KvCmd kvCmd){
		this.kvCmd = kvCmd;
	}
	@Override
	public String process(List<String> list) {
		if(list!=null&&list.size()>=2){
			String key = list.get(1);
			byte[] retBytes = this.kvCmd.get(ByteUtil.toBytes(key));
			if(retBytes!=null&&retBytes.length>0){
				String value = ByteUtil.toString(retBytes);
				return CmdResponseFactory.resOneValue(value);
			}else{
				return CmdResponseFactory.resErr("not found");
			}
		}
		return CmdResponseFactory.resErr("error parameters");
	}

}

package com.yuwnloy.stardb.servers.cmdprocs.kv;

import java.util.List;

import com.yuwnloy.stardb.cmds.KvCmd;
import com.yuwnloy.stardb.servers.cmdprocs.ICmdProc;
import com.yuwnloy.stardb.servers.responses.CmdResponseFactory;
import com.yuwnloy.stardb.utils.ByteUtil;

public class SetProc implements ICmdProc{
	private final KvCmd kvCmd;
	public SetProc(KvCmd kvCmd){
		this.kvCmd = kvCmd;
	}
	@Override
	public String process(List<String> list) {
		if(list!=null&&list.size()>=4){
			String key = list.get(1);
			String value = list.get(3);
			//this.kvCmd.set(, value)
			int ret = this.kvCmd.set(ByteUtil.toBytes(key), ByteUtil.toBytes(value));
			if(ret==1){
				return CmdResponseFactory.resOk();
			}
		}
		return CmdResponseFactory.resErr("error parameters");
	}

}

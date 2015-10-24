package com.github.ipaas.ideploy.controller.proc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.agent.AgentCmd;
import com.github.ipaas.ideploy.controller.agent.AgentCmdExecutor;
import com.github.ipaas.ideploy.controller.agent.AgentCmdType;
import com.github.ipaas.ideploy.controller.agent.AgentFeed;

/** 
 * 启动文件系统监控
 * @author  wudg
 */
public class StartFileSysMonProc   extends  Proc{

	private static Logger logger = LoggerFactory.getLogger(StartFileSysMonProc.class);
	
	@Override
	public void execute() throws Exception {
		
		String agentIp=procInput.getExecTerminal();
		Map<String,Object> params=procInput.getParams();
		String servGroup=String.valueOf(params.get("servGroup"));
		
		AgentCmd agentCmd=new AgentCmd(procInput.getFlowId(),agentIp,AgentCmdType.START_FILESYS_MON);
		  
		agentCmd.addParam("servGroup", servGroup);
		
		AgentCmdExecutor  agentCmdExecutor=AgentCmdExecutor.newInstance();
		
		AgentFeed agentFeed=agentCmdExecutor.executeCmd(agentCmd);
		
		if(agentFeed.getRpsCode()==AgentFeed.FAIL){
			logger.error(agentFeed.getErrMsg());
		}
	}

}

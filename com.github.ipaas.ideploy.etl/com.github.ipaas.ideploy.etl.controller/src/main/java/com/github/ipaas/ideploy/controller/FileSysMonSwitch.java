package com.github.ipaas.ideploy.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.flow.Flow;
import com.github.ipaas.ideploy.controller.flow.FlowInput;
import com.github.ipaas.ideploy.controller.flow.SeqProcExecFlow;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;

public class FileSysMonSwitch {
	
	private static Logger logger = LoggerFactory.getLogger(FileSysMonSwitch.class);
	
	private static class StopFileSysMonFlow extends SeqProcExecFlow{
		StopFileSysMonFlow() {
			super(new String[]{ProcMeta.STOP_FILESYS_MON});
		}
	}
	
	private static class StartFileSysMonFlow extends SeqProcExecFlow{
		StartFileSysMonFlow() {
			super(new String[]{ProcMeta.START_FILESYS_MON});
		}
	}
	
	
	/**
	 * 停止一组服务器上面的文件系统监控
	 * @param deployFlowId
	 * @param servGroup
	 * @param hosts
	 */
	public static void stopFileSysMon(final String deployFlowId,final String servGroup,List<AgentHost> hosts){
		if(hosts==null||hosts.isEmpty()){
			return;
		}
		
		logger.debug("停止文件监控模块");
		
		Map<String, Object> flowParams=null;
		FlowInput flowInput=null;
		Flow flow=null;
		
		MulUnitExecutor mulUnitExecutor=MulUnitExecutor.newInstance();
		
		for(AgentHost host:hosts){
			flowParams=new HashMap<String,Object>();
			flowParams.put(CRSConstants.KEY_IN_CMD_AGENT, host.getIp());
			flowParams.put("jmxPort", host.getJmxPort());
			flowParams.put("servGroup", servGroup);
			
			flowInput=new FlowInput();
			flowInput.setFlowId(deployFlowId);
			flowInput.setParams(flowParams);
			
			flow=new StopFileSysMonFlow();
			flow.setAgent(host.getIp());
			flow.setFlowInput(flowInput);
			
			mulUnitExecutor.addUnit(host.getIp()+"_"+host.getJmxPort(), flow);
		}
		
		mulUnitExecutor.execute();
	}
	
	/**
	 * 启动一组服务器上面的文件监控
	 * @param deployFlowId
	 * @param servGroup
	 * @param hosts
	 */
	public static void startFileSysMon(String deployFlowId,final String servGroup,List<AgentHost> hosts){
		if(hosts==null||hosts.isEmpty()){
			return;
		}
		
		Map<String, Object> flowParams=null;
		FlowInput flowInput=null;
		Flow flow=null;
		
		MulUnitExecutor mulUnitExecutor=MulUnitExecutor.newInstance();
		
		for(AgentHost host:hosts){
			flowParams=new HashMap<String,Object>();
			flowParams.put(CRSConstants.KEY_IN_CMD_AGENT, host.getIp());
			flowParams.put("jmxPort", host.getJmxPort());
			flowParams.put("servGroup", servGroup);
			flowInput=new FlowInput();
			flowInput.setParams(flowParams);
			
			
			flowInput.setFlowId(deployFlowId);
			
			flow=new StartFileSysMonFlow();
			flow.setAgent(host.getIp());
			flow.setFlowInput(flowInput);
			
			mulUnitExecutor.addUnit(host.getIp()+"_"+host.getJmxPort(), flow);
		}
		
		mulUnitExecutor.execute();
		
		logger.debug("启动文件监控模块");
		
	}

}

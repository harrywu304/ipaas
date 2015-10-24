/**
* Licensed under the Apache License, Version 2.0 (the "License");  *
 you may not use this file except in compliance with the License.  *
 You may obtain a copy of the License at  *
  *
      http://www.apache.org/licenses/LICENSE-2.0  *
  *
 Unless required by applicable law or agreed to in writing, software  *
 distributed under the License is distributed on an "AS IS" BASIS,  *
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 See the License for the specific language governing permissions and  *
 limitations under the License. */

package com.github.ipaas.ideploy.controller.job;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.AgentHost;
import com.github.ipaas.ideploy.controller.AgentHostStatus;
import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.DeployFlowStatus;
import com.github.ipaas.ideploy.controller.FileSysMonSwitch;
import com.github.ipaas.ideploy.controller.MulUnitExecutor;
import com.github.ipaas.ideploy.controller.RInfo;
import com.github.ipaas.ideploy.controller.Result;
import com.github.ipaas.ideploy.controller.Utils;
import com.github.ipaas.ideploy.controller.flow.Flow;
import com.github.ipaas.ideploy.controller.flow.FlowDefine;
import com.github.ipaas.ideploy.controller.flow.FlowExecutor;
import com.github.ipaas.ideploy.controller.flow.FlowInput;
import com.github.ipaas.ideploy.controller.flow.StandardReleaseFlow;
import com.github.ipaas.ideploy.controller.proc.ProcInput;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;

/** 
 * 首台后继续安装
 * @author  wudg
 */

public class ContiAfterFirstOne implements Job {

	private static Logger logger = LoggerFactory.getLogger(ContiAfterFirstOne.class);
	 
	@Override
	public void execute(Map<String, Object> params, ExecutorService executor)
			throws Exception {
		
		String deployFlowId = String.valueOf(params.get("deployFlowId"));
		
		Integer deployFlowStatus=CommonDataUtil.getDeployFlowStatus(deployFlowId);
		if(deployFlowStatus!=DeployFlowStatus.WAIT_PREV){
			logger.info("流程["+deployFlowId+"]状态为"+deployFlowStatus+",不能执行此命令");
			return;
		}
		
		logger.info("流程:" + deployFlowId + "首台后继续安装");
		RInfo rInfo = CommonDataUtil.getRlInfo(deployFlowId);
		 
		List<AgentHost> hosts=rInfo.getServList();
		
		if(hosts==null||hosts.isEmpty()){
			return;
		}
		
		//重置状态为正在执行
		CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
		
		AgentHost firstOne=null;
		
		List<AgentHost> otherOnes=new LinkedList<AgentHost>();
		
		for(AgentHost h:hosts){ 
			if(h.getProc().equals(ProcMeta.WAITFOR_PREV_FISRT)){
				// 处于预览阶段  即第一台
				firstOne=h;
			}else{
				otherOnes.add(h);
			}
		}
		 
		
		Map<String, Object> procParams = new HashMap<String, Object>();
		procParams.putAll(params);
		procParams.putAll(rInfo.getParams());
		
		
		//注入ip及jmx端口
		procParams.put(CRSConstants.KEY_IN_CMD_AGENT, firstOne.getIp());
		procParams.put("jmxPort", firstOne.getJmxPort());
		procParams.put("hostStatus", firstOne.getHostStatus());
		
		ProcInput procInput=new ProcInput();
		procInput.setFlowId(deployFlowId);
		procInput.setParams(procParams);
		procInput.setExecTerminal(firstOne.getIp()); 
		
		int rCode=FlowExecutor.newInstance().execute(procInput, FlowDefine.CONTI_FIRST_ONE_FLOW);
		
		if(rCode!=Result.SUCC){
			return;
		}
		
		//继续后续安装
		
		Map<String, Object> flowParams=null;
		
		FlowInput flowInput=null;
		
		Flow flow = null;
		
		MulUnitExecutor mulUnitExecutor=MulUnitExecutor.newInstance();
		 
		int rdServNo = otherOnes.size();

		// 每次更新的服务器数
		int perUpServNo =Utils.getPerUpServNo(rdServNo) ;

		int startIndex = 0, endIndex = 0;
		List<AgentHost> temList = null;
 
		Integer sgId=(Integer)rInfo.getParams().get("sgId");
		String servGroup=String.valueOf(rInfo.getParams().get("servGroup"));
		Integer prevCodeId=(Integer)rInfo.getParams().get("usingCodeId");
		int codeId=(Integer)rInfo.getParams().get("codeId");
		
		while (endIndex < rdServNo) {
			endIndex = startIndex + perUpServNo;
			endIndex = endIndex > rdServNo ? rdServNo : endIndex;
			temList = otherOnes.subList(startIndex, endIndex);
			startIndex=endIndex;
			
			//清除执行结果
			mulUnitExecutor.clear();

			for (AgentHost agent : temList) {
				flowParams = new HashMap<String, Object>();
				flowParams.putAll(params);
				flowParams.putAll(rInfo.getParams());
				flowParams.put(CRSConstants.KEY_IN_CMD_AGENT, agent.getIp());
				flowParams.put("jmxPort", agent.getJmxPort());
				flowParams.put("hostStatus", agent.getHostStatus());

				flowInput = new FlowInput();

				flowInput.setFlowId(deployFlowId);
				flowInput.setParams(flowParams);

				flow = new StandardReleaseFlow();
				flow.setAgent(agent.getIp());
				flow.setFlowInput(flowInput);

				mulUnitExecutor.addUnit(agent.getIp()+"_"+agent.getJmxPort(), flow);
			} 
			
			mulUnitExecutor.execute();
			 
			//有错误退出
			if(!mulUnitExecutor.getFailProcList().isEmpty()){
				  
				//中间出现异常
				if(endIndex<rdServNo){
					CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId,sgId,prevCodeId, new Date(), DeployFlowStatus.INTER_BY_EXCEP);
				}else{
					//更新最后异常
					CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId,sgId,prevCodeId, new Date(), DeployFlowStatus.FINISH_WITH_ERR);
				} 
				return;
			} 
		}
		
		CommonDataUtil.updateHostStatus(sgId, hosts, AgentHostStatus.DEPLOYED);
		
		//更新流程状态为成功
		CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId,sgId,prevCodeId, new Date(), DeployFlowStatus.FINISH_SUCC);
		
		//更新代码版本状态 
		CommonDataUtil.updateCodeStatus4DeploySucc(sgId, codeId);
		
		//启动文件监控模块
		//FileSysMonSwitch.startFileSysMon(deployFlowId,servGroup, hosts);
		
	}

}

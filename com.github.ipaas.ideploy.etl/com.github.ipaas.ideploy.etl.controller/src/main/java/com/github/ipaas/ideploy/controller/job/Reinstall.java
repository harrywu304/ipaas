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

import com.github.ipaas.ideploy.controller.AgentDeloyStatus;
import com.github.ipaas.ideploy.controller.AgentHost;
import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.DeployFlowStatus;
import com.github.ipaas.ideploy.controller.MulUnitExecutor;
import com.github.ipaas.ideploy.controller.RInfo;
import com.github.ipaas.ideploy.controller.flow.Flow;
import com.github.ipaas.ideploy.controller.flow.FlowInput;
import com.github.ipaas.ideploy.controller.flow.ReinstallFlow;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;
import com.github.ipaas.ifw.util.JsonUtil;
  

/** 
 * 重装(失败)项目
 * @author  wudg
 */

public class Reinstall  implements Job {
	
	private static Logger logger = LoggerFactory.getLogger(Reinstall.class);
	
	
	/**
	 * 收集失败项目
	 * @param srcHosts
	 * @return
	 */
	private List<AgentHost> getFailedOnes(List<AgentHost> srcHosts){ 
		if(srcHosts!=null&&!srcHosts.isEmpty()){
			List<AgentHost> failHosts=new LinkedList<AgentHost>();
			for(AgentHost h:srcHosts){
				if(!h.getProc().equals(ProcMeta.FINISHED)&&h.getStatus()==AgentDeloyStatus.EXCEPT){
					failHosts.add(h);
				}
			}
			return failHosts;
		}else{
			return null;
		} 
	}
	
 
	@Override
	public void execute(Map<String, Object> params, ExecutorService executor)
			throws Exception {
		String deployFlowId = String.valueOf(params.get("deployFlowId"));
		
		Integer deployFlowStatus=CommonDataUtil.getDeployFlowStatus(deployFlowId);
		if(deployFlowStatus!=DeployFlowStatus.FINISH_WITH_ERR){
			logger.info("流程["+deployFlowId+"]状态为"+deployFlowStatus+",不能执行此命令");
			return;
		}
		logger.info("流程:" + deployFlowId + " 重装(失败)项目");
		
		RInfo rInfo=CommonDataUtil.getRlInfo(deployFlowId);
		
		
		Map<String,Object> deployParams=new HashMap<String,Object>();
		
		deployParams.putAll(params);
		deployParams.putAll(rInfo.getParams());
		
		//获取失败项目
		List<AgentHost> failHosts=getFailedOnes(rInfo.getServList());
		
		logger.debug("reinstall failHosts: "+JsonUtil.toJson(failHosts));
		
		
		if(failHosts==null||failHosts.isEmpty()){
			return;
		}
		
		//重置状态为正在执行
		CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
		  
		Map<String, Object> flowParams=null;
		FlowInput flowInput=null;
		Flow flow =null;
		
		MulUnitExecutor mulUnitExecutor = MulUnitExecutor.newInstance();
		  
		for (AgentHost agent : failHosts) {
			flowParams = new HashMap<String, Object>();
			flowParams.putAll(deployParams);
			flowParams.put(CRSConstants.KEY_IN_CMD_AGENT, agent.getIp());
			flowParams.put("jmxPort", agent.getJmxPort());
			flowParams.put("hostStatus", agent.getHostStatus());

			flowInput = new FlowInput();

			flowInput.setFlowId(deployFlowId);
			flowInput.setParams(flowParams);

			flow = new ReinstallFlow();
			flow.setAgent(agent.getIp());
			flow.setFlowInput(flowInput);

			mulUnitExecutor.addUnit(agent.getIp()+"_"+agent.getJmxPort(), flow);
		}

		mulUnitExecutor.execute();
		
		//全部成功
		if(mulUnitExecutor.getFailProcList().isEmpty()){
			//更新流程状态
			//更新流程状态为成功
			Integer sgId=(Integer)rInfo.getParams().get("sgId");
			Integer prevCodeId=(Integer)rInfo.getParams().get("usingCodeId");
			int codeId=(Integer)rInfo.getParams().get("codeId");
			String servGroup=String.valueOf(rInfo.getParams().get("servGroup"));
			
			CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId,sgId,prevCodeId, new Date(), DeployFlowStatus.FINISH_SUCC);
			
			//更新代码版本状态 
			CommonDataUtil.updateCodeStatus4DeploySucc(sgId, codeId);
			
			//启动文件监控模块
			//FileSysMonSwitch.startFileSysMon(deployFlowId,servGroup, rInfo.getServList()); 
			
		}else{
			
			CommonDataUtil.updateDepployEndTime(deployFlowId,new Date());
			
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.FINISH_WITH_ERR);
		}
		
	} 
}

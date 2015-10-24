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
import com.github.ipaas.ideploy.controller.Utils;
import com.github.ipaas.ideploy.controller.flow.Flow;
import com.github.ipaas.ideploy.controller.flow.FlowInput;
import com.github.ipaas.ideploy.controller.flow.StandardReleaseFlow;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;
import com.github.ipaas.ifw.util.JsonUtil;

/** 
 *忽略前面的失败项，继续安装
 * @author  wudg
 */

public class ContiFlowIgnoringErr  implements Job{
	
	private static Logger logger = LoggerFactory.getLogger(ContiFlowIgnoringErr.class);
	
	/**
	 * 获取待更新项目
	 * @param srcHosts
	 * @return
	 */
	private List<AgentHost> getWatingHosts(List<AgentHost> srcHosts){
		if(srcHosts!=null&&!srcHosts.isEmpty()){
			List<AgentHost> waitingHosts=new LinkedList<AgentHost>();
			for(AgentHost h:srcHosts){
				//完成了代码下载流程的项目
				if(h.getProc().equals(ProcMeta.DOWMLOAD_NEWCODE)&&h.getStatus()==AgentDeloyStatus.FINISHED){
					waitingHosts.add(h);
				}
			}
			return waitingHosts;
		}else{
			return null;
		} 
	}
	
	
	/**
	 * 流程是否以带错完成
	 * @param deployFlowId
	 * @param srcHosts
	 * @return
	 */
	private  boolean isFinishWithErr(String deployFlowId,List<AgentHost> srcHosts){
		boolean hasErr=false;
		boolean hasUndo=false;
		AgentDeloyStatus deloyStatus=null;
		for(AgentHost host:srcHosts){
			deloyStatus=CommonDataUtil.getAgentDeloyStatus(deployFlowId,host.getIp());
			
			if(!hasUndo&&(deloyStatus.getProc().equals(ProcMeta.DOWMLOAD_NEWCODE)&&deloyStatus.getStatus()==AgentDeloyStatus.FINISHED)){
				hasUndo=true;
			}
			if(!hasErr&&deloyStatus.getStatus()==AgentDeloyStatus.EXCEPT){
				hasErr=true;
			}
		}
		
		if(!hasUndo&&hasErr){
			return true;
		}else{
			return false;
		}
	}
	
	 
	@Override
	public void execute(Map<String, Object> params, ExecutorService executor)
			throws Exception {
		
		String deployFlowId = String.valueOf(params.get("deployFlowId"));
		
		Integer deployFlowStatus=CommonDataUtil.getDeployFlowStatus(deployFlowId);
		if(deployFlowStatus!=DeployFlowStatus.INTER_BY_EXCEP){
			logger.info("流程["+deployFlowId+"]状态为"+deployFlowStatus+",不能执行此命令");
			return;
		}
		
		logger.info("流程:" + deployFlowId + "忽略前面的失败项，继续安装");
		RInfo rInfo = CommonDataUtil.getRlInfo(deployFlowId);
		 
		List<AgentHost> hosts=rInfo.getServList();
		
		List<AgentHost> waitingHosts=getWatingHosts(hosts);
		
		logger.debug("ContiFlowIgnoringErr-waitingHosts: "+JsonUtil.toJson(waitingHosts));
		
		if(waitingHosts==null||waitingHosts.isEmpty()){
			return;
		}
		
		//重置状态为正在执行
		CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
		
		int rdServNo = waitingHosts.size();

		// 每次更新的服务器数
		int perUpServNo =Utils.getPerUpServNo(rdServNo) ;

		int startIndex = 0, endIndex = 0;
		
		Map<String, Object> deployParams=new HashMap<String,Object>();
		deployParams.putAll(params);
		deployParams.putAll(rInfo.getParams());
		
		
		Map<String, Object> flowParams=null;
		
		FlowInput flowInput=null;
		
		Flow flow = null;
		
		MulUnitExecutor mulUnitExecutor=MulUnitExecutor.newInstance();
		 
		List<AgentHost> temList = null;

		while (endIndex < rdServNo) {
			endIndex = startIndex + perUpServNo;
			endIndex = endIndex > rdServNo ? rdServNo : endIndex;
			temList = waitingHosts.subList(startIndex, endIndex);
			startIndex=endIndex;
			
			//清除执行结果
			mulUnitExecutor.clear();

			for (AgentHost agent : temList) {
				flowParams = new HashMap<String, Object>();
				flowParams.putAll(deployParams); 
				
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
				break;
			}  
		}
		
		if(isFinishWithErr(deployFlowId,hosts)){
			CommonDataUtil.updateDepployEndTime(deployFlowId,new Date());
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.FINISH_WITH_ERR);
		}else{
			CommonDataUtil.updateDepployEndTime(deployFlowId,new Date());
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.INTER_BY_EXCEP);
		} 
	}
	 
}

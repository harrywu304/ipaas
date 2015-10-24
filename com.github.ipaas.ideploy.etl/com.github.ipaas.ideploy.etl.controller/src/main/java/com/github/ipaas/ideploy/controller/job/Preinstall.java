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

import java.util.Collections;
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
import com.github.ipaas.ideploy.controller.flow.Flow;
import com.github.ipaas.ideploy.controller.flow.FlowDefine;
import com.github.ipaas.ideploy.controller.flow.FlowExecutor;
import com.github.ipaas.ideploy.controller.flow.FlowInput;
import com.github.ipaas.ideploy.controller.flow.PrepReleaseFlow;
import com.github.ipaas.ideploy.controller.proc.ProcInput;

/**
 * 预安装过程 原则过程包括 所有服务器并发下载代码
 * 
 * @author wudg
 */

public class Preinstall implements Job {

	private static Logger logger = LoggerFactory.getLogger(Preinstall.class);

	@Override
	public void execute(Map<String, Object> params, ExecutorService executor) throws Exception {
		String deployFlowId = String.valueOf(params.get("deployFlowId"));

		Integer deployFlowStatus = CommonDataUtil.getDeployFlowStatus(deployFlowId);
		if (!(deployFlowStatus == DeployFlowStatus.NO_STARTED || deployFlowStatus == DeployFlowStatus.FIRSTONE_EXCEP)) {
			logger.info("流程[" + deployFlowId + "]状态为" + deployFlowStatus + ",不能执行此命令");
			return;
		}
		logger.info("流程:" + deployFlowId + "开始安装");
		CommonDataUtil.updateDeplyFlowStatus4Start(deployFlowId, new Date());

		RInfo rInfo = CommonDataUtil.getRlInfo(deployFlowId);

		Map<String, Object> deployParams = new HashMap<String, Object>();

		deployParams.putAll(params);
		deployParams.putAll(rInfo.getParams());

		List<AgentHost> agents = rInfo.getServList();

		if (agents.isEmpty()) {
			return;
		}

		// 设置流程状态为正在执行
		CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);

		if (agents.size() > 1) {
			execute4Many(deployFlowId, agents, deployParams);
		} else {
			this.excute4One(deployFlowId, agents.get(0), deployParams);
		}

	}

	/**
	 * 只有一台机器的情况
	 * 
	 * @param flowInput
	 * @throws Exception
	 */
	public void excute4One(String deployFlowId, AgentHost agent, Map<String, Object> deployParams) throws Exception {

		Integer sgId = (Integer) deployParams.get("sgId");
		Integer prevCodeId = (Integer) deployParams.get("usingCodeId");
		int codeId = (Integer) deployParams.get("codeId");
		String servGroup = String.valueOf(deployParams.get("servGroup"));

		// 停止各目标服务器的文件监控模块
		List<AgentHost> hosts = new LinkedList<AgentHost>();
		hosts.add(agent);
		//FileSysMonSwitch.stopFileSysMon(deployFlowId, servGroup, hosts);

		boolean flag = true;
		try {
			Map<String, Object> params = new HashMap<String, Object>();

			params.putAll(deployParams);
			params.put(CRSConstants.KEY_IN_CMD_AGENT, agent.getIp());
			params.put("jmxPort", agent.getJmxPort());
			params.put("hostStatus", agent.getHostStatus());

			ProcInput procInput = new ProcInput();
			procInput.setFlowId(deployFlowId);
			procInput.setParams(params);
			procInput.setExecTerminal(agent.getIp());

			int resultCode = FlowExecutor.newInstance().execute(procInput, FlowDefine.FULL_RELEASE_FLOW);
			if (resultCode != 1) {
				flag = false;
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			flag = false;
		}
		// 更新成功后修改代码的部署状态
		if (flag) {
			// 更新机器状态
			CommonDataUtil.updateHostStatus(sgId, hosts, AgentHostStatus.DEPLOYED);
			// 更新流程状态为成功
			CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId, sgId, prevCodeId, new Date(),
					DeployFlowStatus.FINISH_SUCC);

			// 更新代码版本状态
			CommonDataUtil.updateCodeStatus4DeploySucc(sgId, codeId);

			// 启动文件监控模块
			//FileSysMonSwitch.startFileSysMon(deployFlowId, servGroup, hosts);

		} else {
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.FINISH_WITH_ERR);
		}

	}

	/**
	 * 多台的情况
	 * 
	 * @param deployFlowId
	 * @param agents
	 * @param deployParams
	 * @throws Exception
	 */
	public void execute4Many(String deployFlowId, List<AgentHost> agents, Map<String, Object> deployParams)
			throws Exception {
		Map<String, Object> flowParams = null;

		Flow flow = null;
		FlowInput flowInput = null;

		MulUnitExecutor mulUnitExecutor = MulUnitExecutor.newInstance();

		Map<String, AgentHost> hostMap = new HashMap<String, AgentHost>();

		AgentHost previewAgent = null;// 预览服务器

		for (AgentHost agent : agents) {
			flowParams = new HashMap<String, Object>();
			flowParams.putAll(deployParams);

			// 注入ip及jmx端口
			flowParams.put(CRSConstants.KEY_IN_CMD_AGENT, agent.getIp());
			flowParams.put("jmxPort", agent.getJmxPort());
			flowParams.put("hostStatus", agent.getHostStatus());
			if (agent.getPreviewFlag() == AgentHost.PREVIEW_FLAG_STATUS) {
				previewAgent = agent;
				logger.info(" 预览服务器:" + agent.getIp());
			}
			flowInput = new FlowInput(deployFlowId, flowParams);

			// 准备过程 目前只包含下载
			flow = new PrepReleaseFlow();
			flow.setAgent(agent.getIp());
			flow.setFlowInput(flowInput);

			String key = agent.getIp() + "_" + agent.getJmxPort();
			hostMap.put(key, agent);
			mulUnitExecutor.addUnit(key, flow);
		}

		mulUnitExecutor.execute();

		if (mulUnitExecutor.getSuccProcList().isEmpty()) {
			logger.warn("所有目标服务器都没有完成安装准备过程");
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.PRE_FLOW_FAIL);
			return;
		} else if (previewAgent != null
				&& !mulUnitExecutor.getSuccProcList().contains(previewAgent.getIp() + "_" + previewAgent.getJmxPort())) {
			logger.warn("预览服务器没有完成安装准备过程");
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.FIRSTONE_EXCEP);
			return;
		}

		List<String> succHostKeys = mulUnitExecutor.getSuccProcList();
		Collections.sort(succHostKeys);

		List<AgentHost> okHosts = new LinkedList<AgentHost>();
		for (String k : succHostKeys) {
			okHosts.add(hostMap.get(k));
		}

		String servGroup = String.valueOf(deployParams.get("servGroup"));

		// 停止各目标服务器的文件监控模块
		//FileSysMonSwitch.stopFileSysMon(deployFlowId, servGroup, okHosts);

		// 开始预安装第一台
		AgentHost firstOne = null;
		if (previewAgent != null) {
			firstOne = previewAgent;// 如果设置了预览服务器,先安装预览服务器,
		} else {
			firstOne = okHosts.get(0);// 否则先安装按IP排序后的第一台
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.putAll(deployParams);
		params.put(CRSConstants.KEY_IN_CMD_AGENT, firstOne.getIp());
		params.put("jmxPort", firstOne.getJmxPort());
		params.put("hostStatus", firstOne.getHostStatus());

		ProcInput procInput = new ProcInput();
		procInput.setFlowId(deployFlowId);
		procInput.setParams(params);
		procInput.setExecTerminal(firstOne.getIp());

		boolean firstOneSucc = true;
		try {
			int flag = FlowExecutor.newInstance().execute(procInput, FlowDefine.PRE_INSTALL_FIRST_ONE);
			if (flag == -1) {
				firstOneSucc = false;
			}
		} catch (Exception e) {
			logger.error("首台更新出错", e);
			firstOneSucc = false;
		}

		if (firstOneSucc) {
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.WAIT_PREV);
		} else {
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.FIRSTONE_EXCEP);
		}

	}

}

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
import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.DeployFlowStatus;
import com.github.ipaas.ideploy.controller.FileSysMonSwitch;
import com.github.ipaas.ideploy.controller.MulUnitExecutor;
import com.github.ipaas.ideploy.controller.RInfo;
import com.github.ipaas.ideploy.controller.Utils;
import com.github.ipaas.ideploy.controller.flow.Flow;
import com.github.ipaas.ideploy.controller.flow.FlowInput;
import com.github.ipaas.ideploy.controller.flow.RollbackFailedFlow;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;

/**
 * 回退失败流程 ，将服务组的代码回退到上个稳定版本 支持多次调用
 * 
 * @author wudg
 */
public class Rollback implements Job {

	private static Logger logger = LoggerFactory.getLogger(Rollback.class);

	/**
	 * 获取待回退项
	 * 
	 * @param srcHosts
	 * @return
	 */
	private List<AgentHost> getHostsToRoll(List<AgentHost> srcHosts) {
		if (srcHosts != null && !srcHosts.isEmpty()) {
			List<AgentHost> waitingHosts = new LinkedList<AgentHost>();
			for (AgentHost h : srcHosts) {
				// 当前不在下载代码阶段,即已进入更新阶段的服务器都要回退
				if (!h.getProc().equals(ProcMeta.DOWMLOAD_NEWCODE)) {
					waitingHosts.add(h);
				}
			}
			if (!waitingHosts.isEmpty()) {
				Collections.reverse(waitingHosts);
			}
			return waitingHosts;
		} else {
			return null;
		}
	}

	@Override
	public void execute(Map<String, Object> params, ExecutorService executor) throws Exception {

		String deployFlowId = String.valueOf(params.get("deployFlowId"));
		Integer deployFlowStatus = CommonDataUtil.getDeployFlowStatus(deployFlowId);
		if (!(deployFlowStatus == DeployFlowStatus.INTER_BY_EXCEP
				|| deployFlowStatus == DeployFlowStatus.FINISH_WITH_ERR
				|| deployFlowStatus == DeployFlowStatus.ROLLBACK_FAIL || deployFlowStatus == DeployFlowStatus.WAIT_PREV || deployFlowStatus == DeployFlowStatus.FIRSTONE_EXCEP)) {
			logger.info("流程[" + deployFlowId + "]状态为" + deployFlowStatus + ",不能执行此命令");
			return;
		}
		logger.info("流程:" + deployFlowId + "  回退失败流程");
		// CommonDataUtil.updateDeplyFlowStatus4Start(deployFlowId, new Date());

		RInfo rInfo = CommonDataUtil.getRlInfo(deployFlowId);

		String servGroup = String.valueOf(rInfo.getParams().get("servGroup"));

		List<AgentHost> waitingHosts = getHostsToRoll(rInfo.getServList());

		if (waitingHosts == null || waitingHosts.isEmpty()) {// 没有回退的机器,更改状态为回退成功

			CommonDataUtil.updateDepployEndTime(deployFlowId, new Date());
			CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.ROLLBACK_SUCC);

			Integer sgId = (Integer) rInfo.getParams().get("sgId");
			Integer codeId = (Integer) rInfo.getParams().get("codeId");
			// 回滚成功 更新代码版本状态
			CommonDataUtil.updateCodeStatus4RollBackSucc(sgId, codeId);
			// 启动文件监控模块
			//FileSysMonSwitch.startFileSysMon(deployFlowId, servGroup, rInfo.getServList());
			return;
		}

		CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.ROLLBACKING);

		Map<String, Object> deployParams = new HashMap<String, Object>();

		deployParams.putAll(params);

		deployParams.putAll(rInfo.getParams());

		int rdServNo = waitingHosts.size();

		// 每次更新的服务器数
		int perUpServNo = Utils.getPerUpServNo(rdServNo);

		int startIndex = 0, endIndex = 0;

		Map<String, Object> flowParams = null;

		FlowInput flowInput = null;

		Flow flow = null;

		MulUnitExecutor mulUnitExecutor = MulUnitExecutor.newInstance();

		List<AgentHost> temList = null;

		while (endIndex < rdServNo) {
			endIndex = startIndex + perUpServNo;
			endIndex = endIndex > rdServNo ? rdServNo : endIndex;
			temList = waitingHosts.subList(startIndex, endIndex);
			startIndex = endIndex;

			// 清除执行结果
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

				flow = new RollbackFailedFlow();
				flow.setAgent(agent.getIp());
				flow.setFlowInput(flowInput);

				mulUnitExecutor.addUnit(agent.getIp() + "_" + agent.getJmxPort(), flow);
			}

			mulUnitExecutor.execute();

			// 有错误退出
			if (!mulUnitExecutor.getFailProcList().isEmpty()) {
				CommonDataUtil.updateDepployEndTime(deployFlowId, new Date());
				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.ROLLBACK_FAIL);
				return;
			}
		}
		CommonDataUtil.updateDepployEndTime(deployFlowId, new Date());
		CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.ROLLBACK_SUCC);

		Integer sgId = (Integer) rInfo.getParams().get("sgId");
		Integer codeId = (Integer) rInfo.getParams().get("codeId");
		// 回滚成功 更新代码版本状态
		CommonDataUtil.updateCodeStatus4RollBackSucc(sgId, codeId);
		// 启动文件监控模块
		FileSysMonSwitch.startFileSysMon(deployFlowId, servGroup, rInfo.getServList());

	}

}

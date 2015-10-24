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

package com.github.ipaas.ideploy.controller.proc;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.agent.AgentCmd;
import com.github.ipaas.ideploy.controller.agent.AgentCmdExecutor;
import com.github.ipaas.ideploy.controller.agent.AgentCmdType;
import com.github.ipaas.ideploy.controller.agent.AgentFeed;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 停止服务
 * 
 * @author wudg
 */
public class StopServProc extends ProcWithLog {

	private static Logger logger = LoggerFactory.getLogger(StopServProc.class);

	@Override
	public boolean toExec() {
		Map<String, Object> params = procInput.getParams();

		// 需检查 restartFlag 看是否停启服务器
		Integer restartFlag = (Integer) params.get("restartFlag");
		if (restartFlag != 1) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void executeWithLog() throws Exception {

		logger.debug("停止服务器:  " + JsonUtil.toJson(procInput));

		Map<String, Object> params = procInput.getParams();

		String agentIp = procInput.getExecTerminal();

		String jmxPort = String.valueOf(params.get("jmxPort"));

		String stopCmd = String.valueOf(params.get("serverStopCmd"));

		AgentCmd agentCmd = new AgentCmd(procInput.getFlowId(), agentIp, AgentCmdType.STOP_SERVER);

		agentCmd.addParam("jmxPort", jmxPort);

		agentCmd.addParam("shell", stopCmd);

		AgentCmdExecutor agentCmdExecutor = AgentCmdExecutor.newInstance();

		AgentFeed agentFeed = agentCmdExecutor.executeCmd(agentCmd);

		if (agentFeed.getRpsCode() == AgentFeed.FAIL) {
			throw new Exception(agentFeed.getErrMsg());
		}
	}

}

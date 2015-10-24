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

import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ideploy.controller.agent.AgentCmd;
import com.github.ipaas.ideploy.controller.agent.AgentCmdExecutor;
import com.github.ipaas.ideploy.controller.agent.AgentCmdType;
import com.github.ipaas.ideploy.controller.agent.AgentFeed;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 备份代码 指定部署目录的代码，将被拷贝到 名为{备份根目录}/{服务组id}/{当前版本}
 * 
 * @author wudg
 */

public class BackupCodeProc extends ProcWithLog {

	private static Logger logger = LoggerFactory.getLogger(BackupCodeProc.class);

	@Override
	public void executeWithLog() throws Exception {

		logger.debug("开始备份代码..." + JsonUtil.toJson(procInput));

		Map<String, Object> params = procInput.getParams();

		String agentIp = procInput.getExecTerminal();

		String servGroup = String.valueOf(params.get("servGroup"));

		String deployPath = String.valueOf(params.get("deployPath"));

		String bkVer = null;

		if (params.containsKey("usingCodeVer") && params.get("usingCodeVer") != null) {
			bkVer = String.valueOf(params.get("usingCodeVer"));
		} else {
			bkVer = CRSConstants.APPBK_FIRST_NAME;
		}

		String srcPath = CRSConstants.APP_DEPLOY_ROOT_PATH + deployPath;

		String targetPath = CRSConstants.APPBK_ROOT_PATH + "/" + servGroup + "/" + bkVer;

		AgentCmd agentCmd = new AgentCmd(procInput.getFlowId(), agentIp, AgentCmdType.BACKUP_CODE);

		agentCmd.addParam("srcPath", srcPath);
		agentCmd.addParam("targetPath", targetPath);

		AgentCmdExecutor agentCmdExecutor = AgentCmdExecutor.newInstance();

		AgentFeed agentFeed = agentCmdExecutor.executeCmd(agentCmd);

		if (agentFeed.getRpsCode() == AgentFeed.FAIL) {
			throw new Exception(agentFeed.getErrMsg());
		}

		logger.debug("成功备份代码...");
	}
}

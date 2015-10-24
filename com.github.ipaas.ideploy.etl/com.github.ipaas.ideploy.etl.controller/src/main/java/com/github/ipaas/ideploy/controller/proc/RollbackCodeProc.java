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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ideploy.controller.agent.AgentCmd;
import com.github.ipaas.ideploy.controller.agent.AgentCmdExecutor;
import com.github.ipaas.ideploy.controller.agent.AgentCmdType;
import com.github.ipaas.ideploy.controller.agent.AgentFeed;
import com.github.ipaas.ifw.util.JsonUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * 回退代码
 * 
 * @author wudg
 */

public class RollbackCodeProc extends ProcWithLog {
	private static Logger logger = LoggerFactory.getLogger(RollbackCodeProc.class);

	@Override
	public void executeWithLog() throws Exception {

		Map<String, Object> params = procInput.getParams();
		logger.debug("RollbackCodeProc  Params:" + JsonUtil.toJson(params));
		// 服务组id
		String servGroup = String.valueOf(params.get("servGroup"));
		// 部署路径
		String deployPath = CRSConstants.APP_DEPLOY_ROOT_PATH + String.valueOf(params.get("deployPath"));

		String doingCodeVer = (String) params.get("codeVer");

		String usingCodeVer = (String) params.get("usingCodeVer");

		// svn库地址
		String remoteRoot = "/" + servGroup + CRSConstants.SERVER_GROUP_RC_CODEPATH + "/";

		Map<String, Object> cmdParams = new HashMap<String, Object>();
		// 正在更新版本
		cmdParams.put("doingCodeVerPath", remoteRoot + doingCodeVer);

		if (StringUtil.isNullOrBlank(usingCodeVer)) {// 没有之前使用版本,回退本地目录备份代码
			String localBkPath = CRSConstants.APPBK_ROOT_PATH + "/" + servGroup + "/" + CRSConstants.APPBK_FIRST_NAME;
			cmdParams.put("localBkPath", localBkPath);
		} else {// 回退svn备份代码
			cmdParams.put("usingCodeVerPath", remoteRoot + usingCodeVer);
			String savePath = CRSConstants.APPTMP_ROOT_PATH + "/" + servGroup + "/" + usingCodeVer;
			cmdParams.put("savePath", savePath);
		}
		// 正在使用版本
		cmdParams.put("deployPath", deployPath);

		cmdParams.put("hostStatus", params.get("hostStatus"));

		AgentCmd agentCmd = new AgentCmd(procInput.getFlowId(), procInput.getExecTerminal(), AgentCmdType.ROLLBACK_CODE);

		logger.debug("agentCmdParams" + JsonUtil.toJson(cmdParams));
		agentCmd.setParams(cmdParams);

		AgentCmdExecutor agentCmdExecutor = AgentCmdExecutor.newInstance();

		AgentFeed agentFeed = agentCmdExecutor.executeCmd(agentCmd);

		if (agentFeed.getRpsCode() == AgentFeed.FAIL) {
			logger.debug(" agentFeed ErrMsg: " + agentFeed.getErrMsg());
			throw new Exception(agentFeed.getErrMsg());
		}

	}

}

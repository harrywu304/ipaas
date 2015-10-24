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

package com.github.ipaas.ideploy.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.console.CtrlCmd;
import com.github.ipaas.ideploy.console.CtrlCmdType;
import com.github.ipaas.ideploy.controller.job.ContiAfterFirstOne;
import com.github.ipaas.ideploy.controller.job.ContiFlowIgnoringErr;
import com.github.ipaas.ideploy.controller.job.Job;
import com.github.ipaas.ideploy.controller.job.Preinstall;
import com.github.ipaas.ideploy.controller.job.Reinstall;
import com.github.ipaas.ideploy.controller.job.Rollback;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MessageHandler;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * CtrCmd处理类
 * 
 * @author wudg
 */

public class CtrCmdHandler implements MessageHandler {

	private static Logger logger = LoggerFactory.getLogger(CtrCmdHandler.class);

	private ExecutorService executor;

	public CtrCmdHandler(ExecutorService executor) {
		this.executor = executor;
	}

	@Override
	public void handle(Message arg) {
		Object obj = arg.getContent();

		CtrlCmd ctrlCmd = JsonUtil.toBean(String.valueOf(obj), CtrlCmd.class);

		logger.debug("ctrlCmd: " + obj);

		Job job = null;

		Map<String, Object> jobParam = new HashMap<String, Object>();

		jobParam.putAll(ctrlCmd.getParams());

		// 预发布
		if (ctrlCmd.getCmd().equals(CtrlCmdType.PRE_RELEASE)) {
			job = new Preinstall();
		}

		// 正式发布
		if (ctrlCmd.getCmd().equals(CtrlCmdType.STANDARD_RELEASE)) {
			job = new ContiAfterFirstOne();
		}

		// 回退
		if (ctrlCmd.getCmd().equals(CtrlCmdType.ROLLBACK)) {
			job = new Rollback();
		}

		// 忽略失败继续安装
		if (ctrlCmd.getCmd().equals(CtrlCmdType.CONTI_IGNORE_ERR)) {
			job = new ContiFlowIgnoringErr();
		}

		// 重新发布、安装
		if (ctrlCmd.getCmd().equals(CtrlCmdType.REINSTALL)) {
			job = new Reinstall();
		}

		logger.debug("jobParam: " + jobParam);

		try {
			job.execute(jobParam, executor);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

}

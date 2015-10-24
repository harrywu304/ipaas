/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */

package com.github.ipaas.ideploy.agent;

import java.util.Map;

import com.github.ipaas.ifw.mq.MqServiceManager;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MqSendService;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 任务处理器(需将处理结果反馈回Controller)
 * 
 * @author wudg
 */

public abstract class CrsCmdHandlerWithFeed {

	private static Logger logger = LoggerFactory.getLogger(CrsCmdHandlerWithFeed.class);

	public void executeCmd(AgentCmd cmd, BundleContext bundleContext) {
		AgentFeed agentFeed = new AgentFeed();
		agentFeed.setFlowId(cmd.getFlowId());
		agentFeed.setRpsor(cmd.getTargetAgent());
		agentFeed.setCmd(cmd.getCmd());

		logger.debug(JsonUtil.toJson("AgentCmd:" + JsonUtil.toJson(cmd)));

		try {
			execute(cmd.getFlowId(), cmd.getCmd(), cmd.getParams(), bundleContext);
			agentFeed.setRpsCode(AgentFeed.SUCC);
		} catch (Throwable e) {
			agentFeed.setRpsCode(AgentFeed.FAIL);
			agentFeed.setErrMsg(e.getMessage());
			logger.error(e.getMessage(), e);
		}
		MqSendService mqSendService = MqServiceManager
				.getMqSendService(Constants.CRS_APP_ID);
		Message message = mqSendService.createMessage();
		message.setContent(JsonUtil.toJson(agentFeed));
		mqSendService.sendQueue(Constants.CRS_CTRLER_AGENT_QUEUE, message);

	}

	public abstract void execute(String flowId, String cmd, Map<String, Object> params, BundleContext bundleContext)
			throws Exception;

}

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

package com.github.ipaas.ideploy.controller.agent;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MessageHandler;
import com.github.ipaas.ifw.mq.MqListenService;
import com.github.ipaas.ifw.mq.MqSendService;
import com.github.ipaas.ifw.mq.MqServiceManager;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * Agent指令执行
 * 
 * @author wudg
 */

public class AgentCmdExecutor {

	private static Logger logger = LoggerFactory.getLogger(AgentCmdExecutor.class);

	private static ConcurrentHashMap<String, BlockingQueue<AgentFeed>> QM;

	/**
	 * 
	 * @param queueName
	 * @param message
	 */
	private static void addMessage(String queueName, AgentFeed message) {
		BlockingQueue<AgentFeed> newQ = new LinkedBlockingQueue<AgentFeed>();
		BlockingQueue<AgentFeed> oldQ = QM.putIfAbsent(queueName, newQ);
		if (oldQ != null) {
			oldQ.add(message);
		} else {
			newQ.add(message);
		}

	}

	/**
	 * 
	 * @param queueName
	 * @return
	 */
	private static AgentFeed takeMessage(String queueName) {
		try {
			BlockingQueue<AgentFeed> newQ = new LinkedBlockingQueue<AgentFeed>();
			BlockingQueue<AgentFeed> oldQ = QM.putIfAbsent(queueName, newQ);
			AgentFeed m = null;
			if (oldQ != null) {
				m = oldQ.poll(CRSConstants.AGENT_RPS_TIMEOUT, TimeUnit.SECONDS);
			} else {
				m = newQ.poll(CRSConstants.AGENT_RPS_TIMEOUT, TimeUnit.SECONDS);
			}

			// 删除队列 避免内存问题
			QM.remove(queueName);
			return m;
		} catch (Exception e) {
			logger.error("获取agent反馈消息时出错", e);
		}
		return null;
	}

	private static class MessageHandlerImpl implements MessageHandler {
		@Override
		public void handle(Message arg0) {
			Object obj = arg0.getContent();
			AgentFeed agentFeed = JsonUtil.toBean(String.valueOf(obj), AgentFeed.class);
			String flowId = agentFeed.getFlowId();
			String agent = agentFeed.getRpsor();
			addMessage(flowId + "_" + agent, agentFeed);
		}
	}

	/**
	 * 初始化
	 */
	public static void init() {
		QM = new ConcurrentHashMap<String, BlockingQueue<AgentFeed>>();

		try {
			logger.debug("// 启动监听器，监听agent的消息反馈队列");
			// 启动监听器，监听agent的消息反馈队列
			MqListenService mqListenService = MqServiceManager.getMqListenService(CRSConstants.MQ_LinsenSERVICE);
			logger.debug("监听队列" + CRSConstants.CRS_CTRLER_AGENT_QUEUE);
			mqListenService.listenQueue(CRSConstants.CRS_CTRLER_AGENT_QUEUE, new MessageHandlerImpl());

			logger.info("agent指令执行组件成功初始化.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		MqListenService mqListenService = MqServiceManager.getMqListenService(CRSConstants.MQ_LinsenSERVICE);
		mqListenService.listenQueue(CRSConstants.CRS_CTRLER_AGENT_QUEUE, new MessageHandlerImpl());
	}

	public static AgentCmdExecutor newInstance() {
		return new AgentCmdExecutor();
	}

	private AgentCmdExecutor() {
	}

	/**
	 * 执行指令
	 * 
	 * @param flowId
	 * @param targetAgent
	 * @param cmd
	 * @param params
	 * @return
	 * @throws Exception
	 */
	public AgentFeed executeCmd(String flowId, String targetAgent, String cmd, Map<String, Object> params)
			throws Exception {
		AgentCmd agentCmd = new AgentCmd(flowId, targetAgent, cmd);
		agentCmd.setParams(params);
		return executeCmd(agentCmd);
	}

	public AgentFeed executeCmd(AgentCmd reqObj) throws Exception {
		MqSendService mqSendService = MqServiceManager.getMqSendService(CRSConstants.MQ_LinsenSERVICE);// ServiceFactory.getService(CRSConstants.MQ_LinsenSERVICE);
		Message message = mqSendService.createMessage();
		message.setContent(JsonUtil.toJson(reqObj));

		logger.debug("reqObj:" + JsonUtil.toJson(reqObj));
		String qName = CRSConstants.AGENT_CTRL_QUEUE + reqObj.getTargetAgent();
		mqSendService.sendQueue(qName, message);
		AgentFeed agentFeed = takeMessage(reqObj.getFlowId() + "_" + reqObj.getTargetAgent());
		if (agentFeed == null) {
			throw new Exception("既定时间内未收到agent的反馈信息");
		}
		logger.debug("agentFeed:" + JsonUtil.toJson(agentFeed));
		return agentFeed;
	}

	/**
	 * 释放资源
	 */
	public static void releaseResource() {
		QM.clear();
		QM = null;
	}

}

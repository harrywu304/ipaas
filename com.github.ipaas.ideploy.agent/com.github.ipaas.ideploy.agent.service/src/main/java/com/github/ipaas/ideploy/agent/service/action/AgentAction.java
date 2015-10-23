package com.github.ipaas.ideploy.agent.service.action;

import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.Response;

/**
 * Agent监控信息接收
 * @author Chenql
 */
public interface AgentAction {

	/**
	 * 接收信息的操作
	 * 
	 * @param message
	 *            操作命令
	 * @return 操作的结果
	 */
	public Response handle(Message message);
}

package com.github.ipaas.ideploy.agent.service;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.service.action.AgentAction;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MessageSyncHandler;
import com.github.ipaas.ifw.mq.Response;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * Agent控制信息处理
 * @author Chenql
 */
public class AgentMessageHandler implements MessageSyncHandler {

	/**
	 * 信息的module属情
	 */
	private final static String MODULENAME = "module";

	/**
	 * 日志
	 */
	private static Logger logger = LoggerFactory
			.getLogger(AgentMessageHandler.class);
	/**
	 * 环境变量
	 */
	private BundleContext context;

	/**
	 * 
	 * @param context
	 *            容器的环境变量
	 */
	public AgentMessageHandler(BundleContext context) {
		this.context = context;
	}

	@Override
	public Response handle(Message message) {
		ServiceReference[] serviceRefs = null;
		Response result = null;
		try {
			String mess = message.getContent().toString();
			logger.debug(mess);
			Map map = JsonUtil.toBean(mess, Map.class);

			logger.info("begin to handle message1 " + map.get(MODULENAME));
			serviceRefs = context.getServiceReferences(
					AgentAction.class.getName(), "(" + Constant.COMMANDNAME
							+ "=" + map.get(MODULENAME) + ")");

			if (serviceRefs == null || serviceRefs.length == 0) {
				throw new Exception("系统中没有相应的Command服务或服务不可用");
			}
			if (serviceRefs.length > 1) {
				throw new Exception("系统中对应此Command的服务超过1个");
			}
			AgentAction action = (AgentAction) context
					.getService(serviceRefs[0]);
			// 处理
			result = action.handle(message);

			logger.info("finished  handle message: " + map.get(MODULENAME));
		} catch (Exception e) {
			// 记录出错信息
			result = message.createResponse();
			result.setErrorMsg("Agent 命令处理异常: " + e.getMessage());
			logger.error(e.getMessage(), e);
		}

		return result;
	}

	/**
	 * test
	 */
//	private void logCurrentContext(Map map) {
//		logger.info(" test getAllServiceReferences");
//		try {
//
//			ServiceReference[] serviceRefs = null;
//			serviceRefs = context.getAllServiceReferences(
//					AgentAction.class.getName(), "(" + Constant.COMMANDNAME
//							+ "=" + map.get(MODULENAME) + ")");
//			// serviceRefs = context.getAllServiceReferences(
//			// AgentAction.class.getName(),null);
//			if (serviceRefs == null || serviceRefs.length == 0) {
//				logger.error("context.getAllServiceReferences error 系统中没有相应的Command服务或服务不可用");
//			}
//			if (serviceRefs.length > 1) {
//				logger.error("context.getAllServiceReferences error 系统中对应此Command的服务超过1个");
//			}
//		} catch (Exception e) {
//			logger.error("context.getAllServiceReferences error ",
//					e.getMessage());
//		}
//	}
}

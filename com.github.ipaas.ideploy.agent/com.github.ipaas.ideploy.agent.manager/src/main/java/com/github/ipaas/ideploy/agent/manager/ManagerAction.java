package com.github.ipaas.ideploy.agent.manager;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.service.action.AgentAction;
import com.github.ipaas.ideploy.agent.service.action.CustomActionCall;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.Response;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * agent管理
 * chenql
 */
public class ManagerAction implements AgentAction {

	private static Logger logger = LoggerFactory.getLogger(ManagerAction.class);

	private static String CALLNAME = "call";

	private static String CALLEXT = "Call";

	private static String PACKAGENAME = "com.github.ipaas.ideploy.agent.manager.action";

	/**
	 * bundle上下文
	 */
	private BundleContext context;

	/**
	 * 
	 * @param context
	 *            系统环境
	 */
	public ManagerAction(BundleContext context) {
		this.context = context;
	}

	@Override
	public Response handle(Message message) {
		Response result = message.createResponse();
		try {
			// 得到发送信息的对象
			String mess = message.getContent().toString();
			Map<String, Object> map = JsonUtil.toBean(mess, Map.class);
			CustomActionCall custonCall = getCustomActionCall(map);

			if (custonCall != null) {
				result.setContext(custonCall.call(context, map));
			}
		} catch (Exception e) {
			result.setErrorMsg("agent管理请求处理异常:" + e.getMessage());
			logger.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 得到mq运行实例
	 * 
	 * @param messages
	 * @return
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 */
	private CustomActionCall getCustomActionCall(Map<String, ?> messages) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException {
		StringBuilder className = new StringBuilder(PACKAGENAME).append(".").append(messages.get(CALLNAME))
				.append(CALLEXT);
		Class c = Class.forName(className.toString());
		Object obj = c.newInstance();
		if (obj != null && obj instanceof CustomActionCall) {
			return (CustomActionCall) obj;
		}
		return null;
	}

}

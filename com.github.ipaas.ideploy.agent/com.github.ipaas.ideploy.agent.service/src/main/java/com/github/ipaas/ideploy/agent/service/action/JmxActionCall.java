package com.github.ipaas.ideploy.agent.service.action;

import java.util.Map;

import javax.management.MBeanServerConnection;

/**
 * Jmx的运行管理
 * @author Chenql
 */
public interface JmxActionCall {

	/**
	 * 实现jmx的运行管理
	 * @param mbs mqMBeanServer
	 * @param messages 客户端发送过来的信息
	 * @param brokerName mq 的 brokerName
	 * @return 操作的结果
	 */
	Object call(MBeanServerConnection mbs, Map<String, ?> messages,
			Object... objs);

}

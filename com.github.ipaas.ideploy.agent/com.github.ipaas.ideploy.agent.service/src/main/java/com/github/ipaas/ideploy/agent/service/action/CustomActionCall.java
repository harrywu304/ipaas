package com.github.ipaas.ideploy.agent.service.action;

import java.util.Map;

import javax.management.MBeanServerConnection;

import org.osgi.framework.BundleContext;

/**
 * 自定义运行管理
 * @author Chenql
 */
public interface CustomActionCall {
		
	/**
	 * 自定义运行管理
	 * @param context
	 * @param map
	 * @return 操作的结果
	 */
	Object call(BundleContext context, Map<String, Object> map);
}

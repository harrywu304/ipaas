package com.github.ipaas.ideploy.agent.manager;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.service.Constant;
import com.github.ipaas.ideploy.agent.service.action.AgentAction;
import com.github.ipaas.ideploy.agent.service.monitor.AgentMonitor;

/**
 * Osgi容器日志分析
 * 
 * @author dengrq
 * 
 */
public class ManagerActivator implements BundleActivator {

	private static Logger logger = LoggerFactory.getLogger(ManagerActivator.class);

	private final static String DATATYPE = "manager";

	private ServiceRegistration serviceRegistration;

	// 运行服务
	private ServiceRegistration serviceRegistrationRuntime;

	public void start(BundleContext context) throws Exception {
		logger.info("manager 运行管理start  .");
		Dictionary props = new Hashtable();
		// service.pid
		String symbolicName = (String) context.getBundle().getHeaders().get("Bundle-SymbolicName");
		logger.info(symbolicName+   "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ ");
		props.put("service.pid", symbolicName);
		props.put(Constant.COMMANDNAME, DATATYPE);

		// 用于运行管理
		serviceRegistrationRuntime = context.registerService(AgentAction.class.getName(), new ManagerAction(context),
				props);
		logger.info(symbolicName+   "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@      manager 运行管理注册成功.");

		// 注册更新服务
		// serviceRegistration = context.registerService(
		// AgentMonitor.class.getName(), new ManagerMonitor(context),
		// props);

		logger.info("manager 监控注册成功.");

	}

	public void stop(BundleContext arg0) throws Exception {
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
			serviceRegistration = null;
		}

		if (serviceRegistrationRuntime != null) {
			serviceRegistrationRuntime.unregister();
			serviceRegistrationRuntime = null;
		}
	}

}
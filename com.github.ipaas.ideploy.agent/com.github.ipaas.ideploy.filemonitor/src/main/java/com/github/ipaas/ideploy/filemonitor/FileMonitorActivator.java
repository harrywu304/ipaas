package com.github.ipaas.ideploy.filemonitor;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Osgi容器日志分析
 * 
 * @author dengrq
 * 
 */
public class FileMonitorActivator implements BundleActivator {

	private static Logger logger = LoggerFactory.getLogger(FileMonitorActivator.class);

	private ServiceRegistration serviceRegistration;

	private FileMonitorManagedService fileMonitorManagedService;

	public void start(BundleContext context) throws Exception {

		Dictionary props = new Hashtable();
		// service.pid
		String symbolicName = (String) context.getBundle().getHeaders().get("Bundle-SymbolicName");
		props.put("service.pid", symbolicName);

		// 注册更新服务
		fileMonitorManagedService = new FileMonitorManagedService(context);
		serviceRegistration = context.registerService(ManagedService.class.getName(), fileMonitorManagedService, props);

		logger.info("文件更新系统启动成功.");
		new FileMonitorTask(context).run();

	}

	public void stop(BundleContext arg0) throws Exception {
		if (fileMonitorManagedService != null) {
			fileMonitorManagedService.stop();
		}
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}
	}

}
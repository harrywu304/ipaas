package com.github.ipaas.ideploy.agent.service;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.service.monitor.AgentMonitor;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 服务注册 COPYRIGHT.
 * @author Chenql
 */
public class ServiceActivator implements BundleActivator , ServiceListener {

	private static Logger logger = LoggerFactory
			.getLogger(ServiceActivator.class);

	private ServiceRegistration serviceRegistration;

	private ServiceManagedService serviceManagedService;
	
	// Bundle's context.
	private BundleContext mContext = null;

	@Override
	 public void start(BundleContext context) throws Exception {
		mContext = context;
		
		Dictionary props = new Hashtable();
		// service.pid
		String symbolicName = (String) mContext.getBundle().getHeaders()
				.get("Bundle-SymbolicName");
		props.put("service.pid", symbolicName);
		props.put("pid", symbolicName);

		logger.debug(symbolicName+"     注册更新服务");
		// 注册更新服务
		serviceManagedService = new ServiceManagedService(mContext);
		serviceRegistration = mContext.registerService(
				ManagedService.class.getName(), serviceManagedService, props);

		mContext.addServiceListener(this);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		
		mContext.removeServiceListener(this);

		if(serviceManagedService!=null){
			serviceManagedService.stop();
		}
		
		if (serviceRegistration != null) {
			serviceRegistration.unregister();
		}

	}
	
	@Override
	public void serviceChanged(ServiceEvent event) {
		logger.debug(" #######################   serviceChanged"+  event.getServiceReference());
		// TODO Auto-generated method stub
		try {
			Object obj = mContext.getService(event.getServiceReference());
			logger.debug(obj.getClass().getName()+"    serviceChanged:   ");
			//if(obj instanceof AgentMonitor){
				updateConfig(mContext);
			//}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

	}

	/**
	 * 更新配置
	 * 
	 * @param context
	 * @throws IOException
	 */
	private void updateConfig(BundleContext context) throws IOException {
	
		// 注册事件
		ServiceReference configurationAdminReference = context
				.getServiceReference(ConfigurationAdmin.class.getName());
		if (configurationAdminReference != null) {
			ConfigurationAdmin confAdmin = (ConfigurationAdmin) context
					.getService(configurationAdminReference);

			String symbolicName = (String) mContext.getBundle().getHeaders()
					.get("Bundle-SymbolicName");
			Configuration config = confAdmin
					.getConfiguration(symbolicName);
			Dictionary props = config.getProperties();

			if (props == null) {
				props = new Hashtable();
			}
			logger.debug(symbolicName+ "    updateConfig:  "+ JsonUtil.toJson(props));
			// update the configuration
			config.update(props);
		}
	}

}

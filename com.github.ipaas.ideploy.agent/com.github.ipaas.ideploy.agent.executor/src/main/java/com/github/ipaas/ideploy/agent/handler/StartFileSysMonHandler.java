package com.github.ipaas.ideploy.agent.handler;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.Constants;
import com.github.ipaas.ideploy.agent.CrsCmdHandlerWithFeed;
import com.github.ipaas.ideploy.console.FileSysMonitorManager;

/**
 * 启动文件异动监控
 * @author wudie
 *
 */
public class StartFileSysMonHandler  extends CrsCmdHandlerWithFeed{

	private static Logger logger = LoggerFactory.getLogger(StartFileSysMonHandler.class);
	
	@Override
	public void execute(String flowId, String cmd, Map<String, Object> params,BundleContext  bundleContext)
			throws Exception {
		try{
			String servGroup=params.containsKey("servGroup")?(""+params.get("servGroup")):null;
			if(servGroup==null||servGroup.equals("")){
				throw new Exception("缺少服务组编号参数");
			}
			
			ServiceReference[] serviceRefs=bundleContext.getAllServiceReferences(FileSysMonitorManager.class.getName(),
					"(cmdName="+Constants.FILE_ALTER_MONTOR_CLS+")");
			if(serviceRefs==null||serviceRefs.length<=0||serviceRefs.length>1){
				logger.info("不存在或存在多个名为["+Constants.FILE_ALTER_MONTOR_CLS+"]的服务");
				return;
			}
			FileSysMonitorManager fileSysMonitorManager=(FileSysMonitorManager)bundleContext.getService(serviceRefs[0]);
			fileSysMonitorManager.startMonitor(servGroup);
		}catch(Exception e){
			logger.error("启动异动监控功能出错", e);
		} 
		
	}

}

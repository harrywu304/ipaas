package com.github.ipaas.ideploy.agent.manager.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.service.action.CustomActionCall;
import com.github.ipaas.ifw.util.FileUtil;

/**
 * 查看agent的bundle装态
 * @author  Chenql
 */
public class GetBundleInfoCall implements CustomActionCall {
	private static Logger logger = LoggerFactory.getLogger(GetBundleInfoCall.class);

	@Override
	public Object call(BundleContext context, Map<String, Object> map) {
		Bundle[] bundles = context.getBundles();
		List<Map<String, Object>> bundleMaps = new Vector<Map<String, Object>>();
		logger.debug(GetBundleInfoCall.class.getClassLoader().getClass().getName());
		logger.debug(BundleContext.class.getClassLoader().getClass().getName());
		if (map.containsKey("url")) {
			logger.debug((String) map.get("url")  + "########################  " + FileUtil.getFileUrl((String) map.get("url")));
		}
			logger.debug("./config/fw_config.xml"  + "########################  " +FileUtil.getFileUrl("./config/fw_config.xml"));
			logger.debug("../config/fw_config.xml"  + "########################  " +FileUtil.getFileUrl("../config/fw_config.xml"));
			logger.debug("config/fw_config.xml"  + "########################  " +FileUtil.getFileUrl("config/fw_config.xml"));
			logger.debug("/config/fw_config.xml" + "########################  " +FileUtil.getFileUrl("/config/fw_config.xml"));
		
		for (Bundle bundle : bundles) {
			Map<String, Object> bundleMap = new HashMap<String, Object>();
			bundleMap.put("symbolicName", bundle.getSymbolicName()); // 简名
			bundleMap.put("status", Integer.valueOf(bundle.getState())); // 状态
			bundleMap.put("version", bundle.getHeaders().get("Bundle-Version"));
			bundleMap.put("name", bundle.getHeaders().get("Bundle-Name"));
			bundleMaps.add(bundleMap);
		}
		return bundleMaps;
	}

}

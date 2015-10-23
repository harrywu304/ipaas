package com.github.ipaas.ideploy.agent.manager.action;

import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

import com.github.ipaas.ideploy.agent.service.action.CustomActionCall;

/**
 * Bundle管理
 * @author  Chenql
 */
public class BundleManagerCall implements CustomActionCall {

	private final static String OPERAT_START = "start";
	private final static String OPERAT_STOP = "stop";
	private final static String OPERAT_UNINSTALL = "uninstall";

	@Override
	public Object call(BundleContext context, Map<String, Object> map) {
		Bundle[] bundles = context.getBundles();
		try {
			for (Bundle bundle : bundles) {
				if (bundle.getSymbolicName().equals(map.get("symbolicName"))) {
					String operat = (String) map.get("operat");
					if (operat.equals(OPERAT_START)) {
						bundle.start();
					} else if (operat.equals(OPERAT_STOP)) {
						bundle.stop();
					} else if (operat.equals(OPERAT_UNINSTALL)) {
						bundle.uninstall();
					}
					return bundle.getState();
				}
			}
		} catch (Exception ex) {
			throw new RuntimeException(ex.getMessage(), ex);
		}
		return null;

	}

}

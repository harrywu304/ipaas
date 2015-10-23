package com.github.ipaas.ideploy.console;

/**
 * @author wudie
 *
 */
public interface FileSysMonitorManager {
	
	public void startMonitor(String servGroup) throws Exception;
	
	public void stopMonitor(String servGroup) throws Exception;

}

package com.github.ipaas.ideploy.agent.service.monitor;

import java.util.List;
import java.util.Map;

import com.github.ipaas.ideploy.dto.SendMessageDTO;

/**
 * 监控数据
 *@author Chenql
 */
public interface AgentMonitor {
	
	/**
	 * 得到监控数据
	 * @param params
	 * @return
	 */
	List<SendMessageDTO> getMonitorData(Map<String,String> params);
}

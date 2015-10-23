package com.github.ipaas.ifw.jdbc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.github.ipaas.ifw.core.config.FwConfigService;
import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.jdbc.impl.FwDirectDbAccessService;
import com.github.ipaas.ifw.jdbc.impl.FwDirectDbConnectService;

/**
 * 数据访问服务管理器
 * 
 * @author Chenql
 */
public final class DataAccessServiceManager {

	/**
	 * 数据访问服务集合
	 */
	private static Map<String, DbAccessService> das = new ConcurrentHashMap<String, DbAccessService>();

	/**
	 * 数据库连接服务集合
	 */
	private static Map<String, DbConnectService> dbcs = new ConcurrentHashMap<String, DbConnectService>();

	/**
	 * 根据serviceId获取数据访问服务实例
	 * 
	 * @param serviceId
	 *            -- 服务ID
	 * @return -- 数据访问服务对象
	 */
	public static DbAccessService getDbAccessService(String serviceId) {

		DbAccessService dbas = das.get(serviceId);
		if (null == dbas) {
			Map<String, Object> config = FwConfigService.getConfig(serviceId);
			if (config == null || config.isEmpty()) {
				throw new FwRuntimeException("未找到" + serviceId + "配置信息");
			}
			dbas = new FwDirectDbAccessService();
			dbas.setScaleoutMapping((String) config.get("scaleoutMapping"));
			dbas.setDbConnectService(DataAccessServiceManager.getDbConnectService((String) config
					.get("dbConnectService")));
			das.put(serviceId, dbas);
		}
		return dbas;
	}

	/**
	 * 根据serviceId获取数据库连接服务实例
	 * 
	 * @param serviceId
	 *            -- 应用ID
	 * @return -- 数据库连接服务对象
	 */
	public static DbConnectService getDbConnectService(String serviceId) {

		DbConnectService dbc = dbcs.get(serviceId);
		if (null == dbc) {
			Map<String, Object> config = FwConfigService.getConfig(serviceId);
			if (config == null || config.isEmpty()) {
				throw new FwRuntimeException("未找到" + serviceId + "配置信息");
			}
			DbConnectService dbConnectService = new FwDirectDbConnectService();
			dbConnectService.setDbServerMapping((String) config.get("dbServerMapping"));
			dbConnectService.setItemLocateAlgorithm((String) config.get("itemLocateAlgorithm"));
			dbConnectService.setProxoolConfig((String) config.get("proxoolConfig"));
			dbConnectService.initializePlugin();
			dbcs.put(serviceId, dbConnectService);
		}
		return dbc;
	}
}

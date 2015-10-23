package com.github.ipaas.ifw.mq;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.config.FwConfigService;
import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.mq.activemq.FwMqListenService;
import com.github.ipaas.ifw.mq.activemq.FwMqSendService;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 消息队列服务的管理器
 * 
 * @author Chenql
 */
public final class MqServiceManager {

	private static Logger logger = LoggerFactory.getLogger(MqServiceManager.class);

	/**
	 * 信息发送访问服务集合
	 */
	private static Map<String, MqSendService> mqSendMap = new ConcurrentHashMap<String, MqSendService>();
	/**
	 * 预设的外置插件配置参数集合
	 */
	private static Map<String, Object> externalParam = new HashMap<String, Object>();

	/**
	 * 私有化MqService
	 */
	private MqServiceManager() {
	}

	/**
	 * 外部注入连接参数
	 * 
	 * @param map
	 */
	public static void setParams(Map<String, Object> map) {
		logger.info("通过外部注入参数方式使用MqService.");
		externalParam = map;
		// 标识MqServiceManager使用外部参数初始化服务对象
		// 停止原有的连接
	}

	public static void stopPooledConnectionFactory(String serviceId) {
		MqSendService mqService = mqSendMap.get(serviceId);
		logger.info(serviceId + "-----------停止连接池");
		if (mqService != null) {
			mqService.stopPooledConnectionFactory();
		}
		// 参数变化旧重新初始化连接池
		mqSendMap.remove(serviceId);// mqSendMap = new ConcurrentHashMap<String,
		// MqSendService>();
	}

	/**
	 * 根据appId获取MQ服务实例
	 * 
	 * @param serviceId
	 *            -- 应用ID
	 * @return -- 信息发送服务对象
	 */
	public static MqSendService getMqSendService(String serviceId) {
		MqSendService mqService = mqSendMap.get(serviceId);
		if (null == mqService) {
			// 设置params
			Map<String, Object> params = null;
			if (externalParam != null && !externalParam.isEmpty()) {
				// 使用预设的参数
				params = externalParam;
				logger.info("通过外部注入参数方式使用MqSendService.");
			} else {
				params = FwConfigService.getConfig(serviceId);
			}
			if (params == null || params.isEmpty())
				throw new FwRuntimeException("为找到" + serviceId + "服务的配置信息");
			// instance
			FwMqSendService fwMqService = new FwMqSendService(serviceId, params);
			mqService = fwMqService;
			mqSendMap.put(serviceId, mqService);
		}
		return mqService;
	}

	/**
	 * 信息接收
	 * 
	 * @param serviceId
	 *            -- 应用ID
	 * @return -- 信息接收服务对象
	 */
	public static MqListenService getMqListenService(String serviceId) {
		// 使用预设的参数
		// 设置params
		Map<String, Object> params = null;
		if (!externalParam.isEmpty()) {
			// 使用预设的参数
			params = externalParam;
			logger.info("通过外部注入参数方式使用MqSendService.");
		} else {
			params = FwConfigService.getConfig(serviceId);
		}
		if (params == null || params.isEmpty())
			throw new FwRuntimeException("未找到" + serviceId + "服务的配置信息");
		MqListenService mqListenService = new FwMqListenService(serviceId, params);
		return mqListenService;

	}

	/**
	 * 清空预设数据,如通过setParams方法注入并被缓存的参数、服务对象缓存等
	 */
	public static void clear() {
		mqSendMap = new ConcurrentHashMap<String, MqSendService>();
		externalParam = new HashMap<String, Object>();
	}
}

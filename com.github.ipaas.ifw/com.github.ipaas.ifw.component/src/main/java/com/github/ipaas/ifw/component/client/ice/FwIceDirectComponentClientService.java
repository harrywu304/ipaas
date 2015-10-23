/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ipaas.ifw.component.client.ice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Communicator;
import Ice.InitializationData;
import Ice.ObjectPrx;
import Ice.ObjectPrxHelperBase;
import Ice.Util;

import com.github.ipaas.ifw.component.ComponentClientService;
import com.github.ipaas.ifw.component.client.ice.usability.IceHealthCheck;
import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.mbean.Fw;
import com.github.ipaas.ifw.core.support.ThreadPools;
import com.github.ipaas.ifw.util.IPUtil;
import com.github.ipaas.ifw.util.JMXUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * ICE实现组件客户端服务 备注:使用配置直接连接ICE服务器
 * 
 * @author Chenql
 */
@SuppressWarnings("unchecked")
public class FwIceDirectComponentClientService implements
		ComponentClientService {

	private static Logger logger = LoggerFactory
			.getLogger(FwIceDirectComponentClientService.class);

	/**
	 * 随机数生成器
	 */
	private static Random random = new Random();

	private Lock initLock = new ReentrantLock();

	private long operateTimeout = 5000;

	private int connections = 30;

	private String serverUrl;
	/**
	 * 服务Id
	 */
	private String serviceId;

	private Properties config;

	/**
	 * 应用服务器端点集合
	 */
	private List<String> appEndpoints;

	/**
	 * Ice.Communicator
	 */
	private Communicator communicator;

	/**
	 * ConnectionIds
	 */
	private String[] connectionIds;

	private static ScheduledExecutorService updateConnTask;

	/**
	 * 操作超时时间,单位ms,默认是5秒
	 */
	public void setOperateTimeout(long operateTimeout) {
		this.operateTimeout = operateTimeout;
	}

	public int getConnections() {
		return connections;
	}

	/**
	 * 一次连接生命周期里创建的长连接数目，默认为30
	 */
	public void setConnections(int connections) {
		this.connections = connections;
	}

	/**
	 * 服务器url
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public Properties getConfig() {
		return config;
	}

	/**
	 * ice参数设置
	 * 
	 * @param config
	 */
	public void setConfig(Properties config) {
		this.config = config;
	}

	/**
	 * 初始化
	 */
	private void init() {
		if (communicator != null) {
			return;
		}
		try {
			initLock.lock();
			if (communicator != null) {
				return;
			}
			logger.info("FwIceClient开始初始化");
			// 初始化Ice.Communicator环境
			Ice.Properties prop = Util.createProperties();

			// 默认值
			prop.setProperty("Ice.MessageSizeMax", "1024");
			prop.setProperty("Ice.ThreadPool.Client.Size", "10");
			prop.setProperty("Ice.ThreadPool.Client.SizeMax", "200");
			prop.setProperty("Ice.ThreadPool.Client.SizeWarn", "200");
			prop.setProperty("Ice.Override.ConnectTimeout", "1000");
			prop.setProperty("Ice.TCP.RcvSize", "65535");
			prop.setProperty("Ice.TCP.SndSize", "65535");
			prop.setProperty("Ice.ACM.Client", "60");

			if (config != null) {
				for (Object c : config.keySet()) {
					String cstr = String.valueOf(c);
					prop.setProperty(cstr, config.getProperty(cstr));
				}
			}

			logger.info("Ice.Communicator 配置:{}", config);
			// 初始化Ice.Communicator对象
			InitializationData initData = new InitializationData();
			initData.properties = prop;
			communicator = Ice.Util.initialize(initData);

			String connectionLiftTimeStr = prop.getProperty("Ice.ACM.Client");
			final int connectionLiftTime = Integer
					.parseInt(connectionLiftTimeStr);
			final int clientConnections = connections;
			updateConnTask = ThreadPools.newScheduledExecutorService(1,
					"ICE update connections");
			Runnable task = new Runnable() {
				public void run() {
					try {
						String[] nc = new String[clientConnections];
						// Connection id 随机前缀
						int seed = random.nextInt(60);
						for (int i = 0; i < nc.length; i++) {
							nc[i] = String.valueOf(seed) + String.valueOf(i);
						}
						connectionIds = nc;
					} catch (Exception ex) {
						logger.error(ex.getMessage(), ex);
					} finally {
						updateConnTask.schedule(this, connectionLiftTime,
								TimeUnit.SECONDS);
					}
				}
			};
			task.run();

			// 注册ice client MBean
			initIceHealthMBean();
		} finally {
			initLock.unlock();
		}

	}

	/**
	 * 根据appId定位ice endpoint
	 * 
	 * @param appId
	 * @return
	 */
	private String locateEndpoint() {
		if (null == appEndpoints) {
			// 同步所有插件初始化过程(初始化串行化)
			synchronized (logger) {
				String[] servers = serverUrl.trim().split(";");
				appEndpoints = new ArrayList<String>();
				for (String item : servers) {
					String[] h = item.split(":");
					// 使用默认调用超时 5秒
					String endpoint = ":tcp -h " + h[0] + " -p " + h[1]
							+ " -t " + operateTimeout;
					appEndpoints.add(endpoint);
				}

				if (this.appEndpoints.size() == 0) {
					throw new FwRuntimeException("组件服务器数量为0");
				}
				logger.info("FwIceDirectComponentClientService初始化插件成功.");
			}
		}
		// 随机返回一个服务器端点并创建本地代理对象
		int next = random.nextInt(appEndpoints.size());
		String nextEndpoint = appEndpoints.get(next);
		System.out.println(nextEndpoint);
		return nextEndpoint;
	}

	/**
	 * 获取ICE代理对象
	 * 
	 * @param endpoint
	 *            -- 服务器端点
	 * @param itfcClazz
	 *            -- 接口类
	 * @return -- 接口类在服务器上的实现在本地的代理对象
	 */
	private Object getNativeProxy(String endpoint, Class itfcClazz) {

		logger.debug("端点:{}, 接口类:{}", endpoint, itfcClazz);
		String sourceIp = null;
		Map ctx = null;

		String strToProxy = itfcClazz.getName();
		ObjectPrx target = communicator.stringToProxy(strToProxy + endpoint);
		ObjectPrxHelperBase base = null;
		try {
			// 使用短连接
			int randomIndex = random.nextInt(connectionIds.length);
			target = target.ice_connectionId(connectionIds[randomIndex]);

			String helperStr = strToProxy + "Helper";
			Class helperClazz = Class.forName(helperStr);
			base = (ObjectPrxHelperBase) helperClazz.newInstance();
			base.__copyFrom((ObjectPrx) target);

			// 用IP地址设置缺省的上下文
			ctx = base.ice_getContext();
			sourceIp = IPUtil.getLocalIP(true);

			if (!StringUtil.isNullOrBlank(sourceIp)) {
				Map newCtx = new HashMap();
				if (ctx != null && ctx.size() > 0) {
					newCtx.putAll(ctx);
				}
				newCtx.put("sourceIp", sourceIp);
				base = (ObjectPrxHelperBase) base.ice_context(newCtx);
			}
			return base;
		} catch (Exception ex) {
			throw new FwRuntimeException("创建代理对象失败. 使用端点:" + endpoint
					+ ", 接口类:" + itfcClazz);
		}
	}

	/**
	 * 获取ICE代理对象
	 * 
	 * @param endpoint
	 *            -- 服务器端点
	 * @param itfcClazz
	 *            -- 接口类
	 * @param operateTimeout
	 *            -- 操作超时时间，单位毫秒
	 * @return -- 接口类在服务器上的实现在本地的代理对象
	 */
	private Object getNativeProxy(String endpoint, Class itfcClazz,
			int operateTimeout) {
		ObjectPrxHelperBase helper = (ObjectPrxHelperBase) getNativeProxy(
				endpoint, itfcClazz);
		return helper.ice_timeout(operateTimeout);
	}

	public <K> K getProxy(Class<K> clazz) {
		init();
		String nextEndpoint = locateEndpoint();
		return (K) getNativeProxy(nextEndpoint, clazz);
	}

	public <K> K getProxy(Class<K> clazz, int operateTimeout) {
		init();
		String nextEndpoint = locateEndpoint();
		return (K) getNativeProxy(nextEndpoint, clazz, operateTimeout);
	}

	/**
	 * 
	 * 初始化客户端ICE可用性检测的MBean
	 * 
	 * @return void
	 */
	public void initIceHealthMBean() {

		IceHealthCheck iceHealtheCheck;

		try {

			// 拆分主从配置
			String[] group = serverUrl.split(";");
			for (int i = 0; i < group.length; i++) {
				String[] ipPort = group[i].split(":");
				iceHealtheCheck = new IceHealthCheck(serviceId, ipPort[0],
						ipPort[1]);
				String oname = JMXUtil.createObjectNameString(
						Fw.FW_USABILITY_DOMAIN, IceHealthCheck.TYPE, serviceId,
						ipPort[0] + "-" + ipPort[1]);
				logger.info(" 注册ICE客户端MBean:[{}] 成功!", oname);
				JMXUtil.registerMBean(iceHealtheCheck, oname);
				iceHealtheCheck = null;
			}

		} catch (Throwable e) {
			logger.error("initIceHealthMBean error", e);
		}
	}
}

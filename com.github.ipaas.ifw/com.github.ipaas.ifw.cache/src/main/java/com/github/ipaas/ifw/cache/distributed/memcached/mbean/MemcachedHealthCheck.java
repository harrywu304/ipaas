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
package com.github.ipaas.ifw.cache.distributed.memcached.mbean;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.FailureMode;
import net.spy.memcached.MemcachedClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.cache.CloseUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * Memcached可用性检测MBean
 * 
 * @author Chenql
 * 
 */
public class MemcachedHealthCheck implements MemcachedHealthCheckMBean {
	private static Logger logger = LoggerFactory.getLogger(MemcachedHealthCheck.class);

	/**
	 * 可用性监控类别
	 */
	public static String TYPE = "MemcachedHealthCheck";
	/**
	 * 链接超时
	 */
	private long CONNECT_TIMEOUT = 1000L;
	/**
	 * 操作超时
	 */
	private long OPERATE_TIMEOUT = 2000L;

	private static int EXPIRY = 60;

	/**
	 * memcached健康检测的key
	 */
	private static String AMM_HEALTH_CHECK = "amm.health_check";

	/**
	 * 服务id
	 */
	private String serviceId = null;

	/**
	 * ip地址
	 */
	private String ip = null;

	/**
	 * 端口
	 */
	private String port = null;

	/**
	 * Memcached可用性检测MBean的构造函数
	 * 
	 * @param appId
	 * @param ip
	 * @param port
	 */
	public MemcachedHealthCheck(String serviceId, String ip, String port, Long contectTimeOut) {
		this.serviceId = serviceId;
		this.ip = ip;
		this.port = port;
		if (contectTimeOut != null) {
			CONNECT_TIMEOUT = contectTimeOut;
		}
	}

	@Override
	public String getTargetIp() {
		return ip;
	}

	@Override
	public String getTargetPort() {
		return port;
	}

	@Override
	public String getState() {
		String checkState = null;
		Integer intObject = Integer.valueOf(1);
		MemcachedClient memcachedClient = null;
		Integer resultObject = null;
		Future<Boolean> operationFuture = null;
		String address = ip + ":" + port;

		try {

			// memcachedClient = new MemcachedClient(new
			// InetSocketAddress(this.ip,Integer.parseInt(port)));
			memcachedClient = new MemcachedClient(new ConnectionFactoryBuilder().setOpTimeout(CONNECT_TIMEOUT)
					.setDaemon(true).setFailureMode(FailureMode.Retry).build(), AddrUtil.getAddresses(address));
			operationFuture = memcachedClient.set(AMM_HEALTH_CHECK, EXPIRY, intObject);
			// 等待任务完成，检查任务完成状态
			if (!operationFuture.get(OPERATE_TIMEOUT, TimeUnit.MILLISECONDS).booleanValue() == true) {
				checkState = "2 seconds timeout";
				return checkState;
			}
			resultObject = (Integer) memcachedClient.get(AMM_HEALTH_CHECK);
		} catch (Throwable e) {
			checkState = StringUtil.getExceptionAsStr(e);
			logger.error("Memcached check error", e);
			return checkState;
		} finally {
			CloseUtil.closeSilently(memcachedClient);
		}
		if (resultObject != null && resultObject.equals(intObject)) {
			checkState = "1";
		} else {
			checkState = "geting value is null or is not euqal to seting value";
		}
		return checkState;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.github.ipaas.ifw.core.mbean.UsabilityMBean#getUri()
	 */
	@Override
	public String getUri() {
		String className = "MemcachedHealthCheck";
		String methodName = "getState";
		String slash = "/";
		StringBuffer sb = new StringBuffer();
		sb.append(slash).append(serviceId).append(slash).append(className).append(slash).append(methodName);
		return sb.toString();
	}

	@Override
	public String getServiceId() {
		return this.serviceId;
	}

}

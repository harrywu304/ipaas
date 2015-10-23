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
package com.github.ipaas.ifw.component.client.ice.usability;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Communicator;

import com.github.ipaas.ifw.core.health.ixehealth.IxeCheckServicePrx;
import com.github.ipaas.ifw.core.health.ixehealth.IxeCheckServicePrxHelper;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * 用于检测ICE服务的可用性.
 * 
 * @author Chenql
 * 
 */
public class IceHealthCheck implements IceHealthCheckMBean {
	private static Logger logger = LoggerFactory.getLogger(IceHealthCheck.class);

	public static String TYPE = "IceHealthCheck";

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
	 * 小MBean的构造函数
	 * 
	 * @param serviceId
	 *            -- 服务id
	 * @param ip
	 *            -- ip地址
	 * @param port
	 *            -- 端口
	 */
	public IceHealthCheck(String serviceId, String ip, String port) {
		super();
		this.serviceId = serviceId;
		this.ip = ip;
		this.port = port;
	}

	/**
	 * 
	 * 用于轻量级的检测
	 * 
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	private String checkLight() {
		Ice.Communicator ic = null;
		String sendString = "ok";
		String checkState = null;
		String echoString = null;
		IxeCheckServicePrx proxy = null;
		Ice.ObjectPrx base = null;
		Ice.Connection connection = null;
		try {
			ic = Ice.Util.initialize();
			String strToProxy = IxeCheckServicePrx.class.getName();
			base = ic.stringToProxy(strToProxy + ":tcp -h " + ip + " -p " + port + " -t " + "2000");
			connection = base.ice_getConnection();
			proxy = IxeCheckServicePrxHelper.checkedCast(base);
			if (null == proxy) {
				checkState = "can not get IxeCheckServicePrx object";
				return checkState;
			}
			echoString = proxy.echo(sendString);
		} catch (Throwable e) {
			checkState = StringUtil.getExceptionAsStr(e);
			logger.error("ICE check light error", e);
			return checkState;
		} finally {
			closeSilently(connection);
			closeSilently(ic);
		}
		if (!StringUtil.isNullOrBlank(echoString) && echoString.equals(sendString)) {
			checkState = "1";
		} else {
			checkState = "sending msg is not euqal to replying msg";
		}
		return checkState;
	}

	/**
	 * 
	 * 重量级检测方法
	 * 
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	private String checkHeavy() {
		Ice.Communicator ic = null;
		String checkState = null;
		IxeCheckServicePrx proxy = null;
		Ice.ObjectPrx base = null;
		Ice.Connection connection = null;
		try {
			ic = Ice.Util.initialize();
			String strToProxy = IxeCheckServicePrx.class.getName();
			base = ic.stringToProxy(strToProxy + ":tcp -h " + ip + " -p " + port + " -t " + 2000);
			connection = base.ice_getConnection();
			proxy = IxeCheckServicePrxHelper.checkedCast(base);
			if (null == proxy) {
				checkState = "can not get IxeCheckServicePrx object";
				return checkState;
			}
			checkState = proxy.checkBiz();
			if ("1".equals(checkState)) {
				return checkState;
			} else {
				checkState = "reply: " + checkState;
				return checkState;
			}
		} catch (Throwable e) {
			checkState = StringUtil.getExceptionAsStr(e);
			logger.error("ICE check heavy error", e);
			return checkState;
		} finally {
			closeSilently(connection);
			closeSilently(ic);
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
	public String getLightState() {
		return checkLight();
	}

	@Override
	public String getHeavyState() {
		return checkHeavy();
	}

	@Override
	public String getUri() {
		String className = "IceHealthCheck";
		String slash = "/";
		StringBuffer sb = new StringBuffer();
		sb.append(slash).append(serviceId).append(slash).append(className);
		return sb.toString();
	}

	/**
	 * 关闭Ice Connection资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	private static void closeSilently(Ice.Connection rsc) {

		if (null != rsc) {
			try {
				rsc.close(false);
			} catch (Exception ex) {
				/* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭Ice Communicator资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Communicator rsc) {

		if (null != rsc) {
			try {
				rsc.shutdown();
				rsc.destroy();
			} catch (Exception ex) {
				/* 消除异常 */
			}
			rsc = null;
		}
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

}

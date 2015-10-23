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
package com.github.ipaas.ifw.server.ice;

import java.net.InetAddress;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.ConnectionI;
import Ice.Current;
import Ice.LocalObjectHolder;
import Ice.LocalObjectImpl;
import Ice.Object;
import Ice.ServantLocator;
import Ice.UserException;
import IceInternal.Transceiver;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;

/**
 * rpc代理中自定义servant定位器
 * 
 * @author Chenql
 * @memo:
 */
@SuppressWarnings({ "deprecation", "unused" })
public class ComponentServantLocator extends LocalObjectImpl implements ServantLocator {

	public static Logger logger = LoggerFactory.getLogger(ComponentServantLocator.class);

	/**
	 * 全局静态变量,线程安全
	 */
	private static AtomicLong requestCountTotal = new AtomicLong(0L);
	/**
	 * 配置信息
	 */
	private Map<String, String> config = null;

	/**
	 * 是否需要访问控制标记
	 */
	private boolean accessControl = false;

	/**
	 * 允许访问的ip集合,如果启动了访问控制才生效
	 */
	private Map<InetAddress, Boolean> permission = new ConcurrentHashMap<InetAddress, Boolean>();

	ComponentServantLocator(Map<String, String> config) {

		this.config = config;
		String ac = config.get("server.access_control");
		String as = config.get("server.access_servers");
		accessControl = ("true".equals(ac) && null != as && !"".equals(as));
		if (null != as && !"".equals(as)) {
			String[] items = as.trim().split(";");
			for (String item : items) {
				try {
					String[] host = item.split(":");
					InetAddress h = InetAddress.getByName(host[0]);
					Boolean flag = Boolean.valueOf(host[1]);
					permission.put(h, flag);
				} catch (Exception ex) {
					logger.error("无法解析访问控制项:{}", item);
				}
			}
		}
	}

	public void deactivate(String arg) {

	}

	public void finished(Current current, Object servant, java.lang.Object cookie) throws UserException {
	}

	/**
	 * 定位ice服务(servant对象)
	 */
	public Object locate(Current current, LocalObjectHolder holder) throws UserException {
		// 计算ICE SERVER的请求数
		requestCountTotal.addAndGet(1L);

		ConnectionI conn = (ConnectionI) current.con;

		// 获取当前访问的信息: 远程IP地址, 本地IP地址
		Transceiver trans = conn.getTransceiver();
		SocketChannel sc = (SocketChannel) trans.fd();
		Socket socket = sc.socket();
		InetAddress clientAddress = socket.getInetAddress();
		InetAddress localAddress = socket.getLocalAddress();

		if (logger.isDebugEnabled()) {
			logger.debug("Transceiver:" + trans);
			logger.debug("SocketChannel:" + sc);
			logger.debug("Socket:" + socket);
			logger.debug("localAddress:" + localAddress);
			logger.debug("clientAddress:" + clientAddress);
		}
		if (!checkPermission(clientAddress, localAddress)) {
			// 权限校验失败
			throw new FwRuntimeException("组件服务器进行IP访问校验失败, 客户端[" + clientAddress + "]无权访问服务器[" + localAddress + "]");
		}

		if (null != current.id.name) {
			Object target = ComponentServantProvider.getServantBean(current.id.name);
			return target;
		}
		return null;

	}

	/**
	 * 访问权限校验
	 * 
	 * @param clientAddress
	 *            -- 客户端IP
	 * @param localAddress
	 *            -- 本地接受连接的IP
	 * @return -- true, 如果允许访问(权限校验通过)
	 */
	private boolean checkPermission(InetAddress clientAddress, InetAddress localAddress) {

		if (accessControl) {
			Boolean flag = permission.get(clientAddress);
			// 如果不存在配置项,返回true, 如果存在配置项,返回配置项的值
			return (null == flag) ? true : flag.booleanValue();
		}
		return true;
	}

	/**
	 * 
	 * @return 返回ICE SERVER 的请求数
	 */
	public static long getRequestCountTotal() {
		return requestCountTotal.get();
	}

}

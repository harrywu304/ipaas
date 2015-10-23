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
package com.github.ipaas.ifw.util;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 提供跟IP相关的一些处理方法。
 * 
 * * @author Chenql
 * 
 */
public class IPUtil {

	private static final Logger logger = LoggerFactory.getLogger(IPUtil.class);

	private static List<InetAddress> localAddressList;

	static {
		// 初始化本地网络接口列表
		localAddressList = getLocalAddresses();
	}

	/**
	 * 把IP地址转换成整形
	 * 
	 * @param ip
	 * @return
	 */
	public static long convertIpToInt(String ip) {
		// 创建IP数组
		String[] ipArray = StringUtils.split(ip, '.');
		// 创建IP整形
		long ipInt = 0;
		// 对数组进行循环
		try {
			for (int i = 0; i < ipArray.length; i++) {
				if (ipArray[i] == null || ipArray[i].trim().equals("")) {
					ipArray[i] = "0";
				}
				if (new Integer(ipArray[i].toString()).intValue() < 0) {
					Double j = new Double(Math.abs(new Integer(ipArray[i].toString()).intValue()));
					ipArray[i] = j.toString();
				}
				if (new Integer(ipArray[i].toString()).intValue() > 255) {
					ipArray[i] = "255";
				}
			}
			ipInt = new Double(ipArray[0]).longValue() * 256 * 256 * 256 + new Double(ipArray[1]).longValue() * 256
					* 256 + new Double(ipArray[2]).longValue() * 256 + new Double(ipArray[3]).longValue();
			// 返回整形
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ipInt;
	}

	/**
	 * 取得本机IP（多网卡主机，默认取非内网IP，如果非内网IP也有多个，取获取到的第一个）
	 * 
	 * @return 本机IP
	 */
	public static String getLocalIP() {
		return getLocalIP(false);
	}

	/**
	 * 取得本机IP地址 （多网卡主机返回其中一张网卡的IP）
	 * 
	 * @param isInter
	 *            是否返回内网IP（默认网段为19.2.）
	 * @return 本机ip地址
	 */
	public static String getLocalIP(boolean isInter) {
		String localIP = "";
		for (InetAddress ia : localAddressList) {

			String ip = ia.getHostAddress();
			// 忽略ipv6地址和loopback地址
			if (ia instanceof Inet6Address || ip.startsWith("127")) {
				continue;
			}
			// 记录找到的第一个ipv4地址
			if (StringUtil.isNullOrBlank(localIP)) {
				localIP = ip;
			}

			if (isInter && IPUtil.isInterIp(ip)) {
				return ip;
			}
			if (!isInter && !ip.startsWith("19.")) {
				return ip;
			}
		}
		return localIP;
	}

	/**
	 * 判断是否是内网Ip A类 10.0.0.0 --10.255.255.255 　　B类 172.16.0.0--172.31.255.255
	 * 　C类 192.168.0.0--192.168.255.255
	 * 
	 * @param ip
	 * @return
	 */
	public static boolean isInterIp(String ip) {

		if (ip.startsWith("10."))
			return true;
		long ipInt = IPUtil.convertIpToInt(ip);
		if (ipInt >= IPUtil.convertIpToInt("172.16.0.0") && ipInt <= IPUtil.convertIpToInt("172.31.255.255"))
			return true;

		if (ipInt >= IPUtil.convertIpToInt("192.168.0.0") && ipInt <= IPUtil.convertIpToInt("192.168.255.255"))
			return true;
		return false;
	}

	/**
	 * 判断地址是否为IP地址
	 * 
	 * @param address
	 *            网络地址
	 * @return 是IP地址返回true，否则返回false
	 */
	public static boolean isIP(String address) {
		// 匹配ip的正则表达式
		Pattern ipPattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b");
		return ipPattern.matcher(address).matches();
	}

	/**
	 * 取得本机网络地址列表
	 * 
	 * @return
	 */
	public static List<InetAddress> getLocalAddresses() {
		if (localAddressList == null) {
			localAddressList = new ArrayList<InetAddress>();
			try {
				// 获取可用的网络接口
				Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
				while (interfaces != null && interfaces.hasMoreElements()) {
					NetworkInterface interfaceN = interfaces.nextElement();
					// 获取网络接口上的网络地址
					Enumeration<InetAddress> ienum = interfaceN.getInetAddresses();
					while (ienum.hasMoreElements()) {
						InetAddress ia = ienum.nextElement();
						// 添加网络地址到本机网络地址列表
						localAddressList.add(ia);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			logger.info("已完成本地网络接口列表初始化[{}]", localAddressList);
		}
		return localAddressList;
	}

	/**
	 * 获取用户终端的IP地址
	 * 
	 * @param request
	 *            http请求
	 * @return 用户终端的IP地址
	 */
	public static String getUserIP(HttpServletRequest request) {
		return getClientIP(request, 0);
	}

	/**
	 * 获取Wap调用的用户IP
	 * 
	 * @param request
	 * @return
	 */
	public static String getWapUserIP(HttpServletRequest request) {
		return getClientIP(request, 1);
	}

	/**
	 * 获取最接近的客户端IP
	 */
	public static String getNearestClientIP(HttpServletRequest request) {
		return getClientIP(request, 2);
	}

	/**
	 * 根据指定模式获取客户端IP
	 * 
	 * @param request
	 *            http请求
	 * @param mode
	 *            0:user ip, 1:wap user ip, 2: nearest client ip
	 * @return
	 */
	private static String getClientIP(HttpServletRequest request, int mode) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		} else {
			String[] ips = ip.split(",");
			ip = ips[0].trim();
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		} else {
			return ip.trim();
		}

		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}

		return ip.trim();
	}

	/**
	 * 
	 * 获取远程客户端端口
	 * 
	 * @param request
	 *            http请求
	 * @return int
	 */
	public static int getUserPort(HttpServletRequest request) {
		String portStr = request.getHeader("Remote_Port");
		if (StringUtil.isNullOrBlank(portStr))
			return request.getRemotePort();
		try {
			return Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			return request.getRemotePort();
		}
	}

}

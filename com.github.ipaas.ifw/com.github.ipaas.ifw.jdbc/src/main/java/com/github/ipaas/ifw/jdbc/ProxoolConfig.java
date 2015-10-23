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
package com.github.ipaas.ifw.jdbc;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.util.BeanUtil;
import com.github.ipaas.ifw.util.StringUtil;
import com.github.ipaas.ifw.util.XmlUtil;

/**
 * 用于保存 proxool 配置信息,在初始化数据库连接服务时候注入配置信息, DB可用性监控需要用到这些信息
 * 
 * @author Chenql
 * 
 */
public class ProxoolConfig {
	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ProxoolConfig.class);

	public static String DEFALUT_CONFIG = "/proxool.xml";//

	public static List<Map<String, Object>> aliasList = new ArrayList<Map<String, Object>>();

	/**
	 * 初始化,在初始化数据库连接服务时候调用
	 */
	public static void init(String proxoolFilePath) {

		if (null == proxoolFilePath || proxoolFilePath.equals("")) {
			proxoolFilePath = DEFALUT_CONFIG;
		}
		try {
			InputStream in = ProxoolConfig.class.getResourceAsStream(proxoolFilePath);
			Map proxoolElementmap = XmlUtil.toMap(in);
			aliasList = BeanUtil.wrapToList(proxoolElementmap.get("proxool"));
			logger.info("获取proxool配置文件信息,");
		} catch (Exception ex) {
			throw new FwRuntimeException("获取proxool配置文件信息异常", ex);
		}
	}

	/**
	 * 通过连接池的别名获取jdbc url
	 * 
	 * @param alias
	 *            -- 连接池别名
	 * @return jdbc url
	 */
	public static String getJdbcUrl(String alias) {
		if (StringUtil.isNullOrBlank(alias)) {
			return "";
		}
		String jdbcDriverUrl = null;
		try {

			for (int i = 0; i < aliasList.size(); i++) {
				Map<String, Object> aliasNodemap = BeanUtil.wrapToMap(aliasList.get(i));
				if (aliasNodemap.containsValue(alias)) {
					jdbcDriverUrl = (String) aliasNodemap.get("driver-url");
					break;
				}
			}
		} catch (Throwable e) {
			logger.error("getJdbcUrl error", e);
		}
		return jdbcDriverUrl;
	}

	/**
	 * 根据IP和端口串返回IP地址
	 * 
	 * @param iPPort
	 *            -- IP和端口串,例如127.0.0.1:3308
	 * @return 返回字符串形式的IP
	 */
	public static String getNodeIp(String jdbcUrl) {
		if (StringUtil.isNullOrBlank(jdbcUrl)) {
			return "";
		}
		String iPPort = getIpPortString(jdbcUrl);
		if (StringUtil.isNullOrBlank(iPPort)) {
			return "";
		}
		String[] iPPorts = iPPort.split(":");
		if (iPPorts != null && iPPorts.length > 0) {
			return iPPorts[0];
		} else {
			return "";
		}

	}

	/**
	 * 根据IP和端口串返回端口
	 * 
	 * @param iPPort
	 *            -- IP和端口串,例如127.0.0.1:3308
	 * @return 返回字符串形式的port
	 */
	public static String getNodePort(String jdbcUrl) {
		String defaultPort = "";
		if (StringUtil.isNullOrBlank(jdbcUrl)) {
			return "0";
		}
		if (jdbcUrl.contains("mysql")) {
			defaultPort = "3306";
		} else if (jdbcUrl.contains("sqlserver")) {
			defaultPort = "1433";
		}
		String ipPortString = getIpPortString(jdbcUrl);

		if (StringUtil.isNullOrBlank(ipPortString)) {
			return "0";
		}
		String[] iPPorts = ipPortString.split(":");
		if (2 != iPPorts.length) {
			logger.warn("getNodePort()提示:jdbc url没有正确配置数据库服务端口,使用默认端口:" + defaultPort + ". 请检查配置是否配置了服务端口!");
			return defaultPort;
		}
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(iPPorts[1]);
		if (!isNum.matches()) {
			logger.warn("getNodePort()提示:端口:" + iPPorts[1] + "包含非数字字符,请检查配置是否正确配置了服务端口!");
		}
		return iPPorts[1];
	}

	/**
	 * 用于从jdbc url 获取ip和端口串
	 * 
	 * @param jdbcDriverUrl
	 *            -- 数据库的jdbc连接url串
	 * @return IP和port串,例如127.0.0.1:3308
	 */
	private static String getIpPortString(String jdbcDriverUrl) {

		if (StringUtil.isNullOrBlank(jdbcDriverUrl)) {
			return "";
		}
		String[] tmp2 = null;
		String[] tmp1 = jdbcDriverUrl.split("//");
		if (null == tmp1 || tmp1.length == 0) {
			return "";
		}
		if (jdbcDriverUrl.contains("mysql")) {
			tmp2 = tmp1[1].split("/");
		} else if (jdbcDriverUrl.contains("sqlserver")) {
			tmp2 = tmp1[1].split(";");
		}
		if (null == tmp2 || tmp2.length == 0) {
			return "";
		}
		String ipPortString = tmp2[0];
		return ipPortString;
	}
}

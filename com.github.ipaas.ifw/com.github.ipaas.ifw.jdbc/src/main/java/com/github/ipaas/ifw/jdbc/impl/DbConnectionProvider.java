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

package com.github.ipaas.ifw.jdbc.impl;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.logicalcobwebs.proxool.ConnectionPoolDefinitionIF;
import org.logicalcobwebs.proxool.ProxoolFacade;
import org.logicalcobwebs.proxool.ProxoolListenerIF;
import org.logicalcobwebs.proxool.configuration.JAXPConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.util.CloseUtil;

/**
 * 数据库连接提供者
 * 
 * @author Chenql
 */
public final class DbConnectionProvider {

	private static Logger logger = LoggerFactory.getLogger(DbConnectionProvider.class);

	// /**
	// * 默认数据库连接池配置信息
	// */
	// private static Properties defaultProps = new Properties();

	/**
	 * proxool listner 见<code>ProxoolListenerIF</code>
	 */
	private static FwProxoolListener proxoolListener = new FwProxoolListener();

	// static {
	// init();
	// }

	static void init() {

		try {
			String proxoolClazz = "org.logicalcobwebs.proxool.ProxoolDriver";
			Class.forName(proxoolClazz);
			logger.info("proxoolClazz:org.logicalcobwebs.proxool.ProxoolDriver");

			// defaultProps.setProperty("proxool.simultaneous-build-throttle",
			// "10");
			// defaultProps.setProperty("proxool.test-before-use", "true");
			// defaultProps.setProperty("proxool.house-keeping-test-sql",
			// "select 0");
			// defaultProps.setProperty("proxool.house-keeping-sleep-time",
			// "60000");
			// defaultProps.setProperty("proxool.maximum-active-time",
			// "300000");
			// defaultProps.setProperty("proxool.maximum-connection-count",
			// "50");
			// defaultProps.setProperty("proxool.minimum-connection-count",
			// "1");
			// defaultProps.setProperty("proxool.maximum-connection-lifetime",
			// "1800000");
			// defaultProps.setProperty("proxool.recently-started-threshold",
			// "60000");
			// defaultProps.setProperty("proxool.overload-without-refusal-lifetime",
			// "60000");
			// defaultProps.setProperty("proxool.prototype-count", "0");
			// logger.info("proxool数据库连接池默认配置:{}", defaultProps);
			ProxoolFacade.addProxoolListener(proxoolListener); // add proxool
																// listener
		} catch (Exception ex) {

			throw new FwRuntimeException("初始化数据库连接提供者失败", ex);
			// FwErrorTrackService.getInstance().trackAndThrowRuntime("fw06001",
			// ex);
		}

	}

	/**
	 * 默认配置属性 getter
	 * 
	 * @return -- 默认配置属性
	 */
	// public static Properties getDefaultProps() {
	// return defaultProps;
	// }

	/**
	 * 默认配置属性 setter
	 * 
	 * @param defaultProps
	 *            -- 默认配置属性
	 */
	// public static void setDefaultProps(Properties defaultProps) {
	// DbConnectionProvider.defaultProps = defaultProps;
	// }

	/**
	 * 注册一个可用的proxool数据库连接池
	 * 
	 * @param alias
	 *            -- proxool 连接池别名
	 * @param driverClass
	 *            -- 驱动类
	 * @param driverUrl
	 *            -- 驱动URL
	 * @param otherProps
	 *            -- 连接池其他配置属性
	 * @return -- 返回别名, 如果没有指定别名返回拼接的URL字符串
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	// public static String registerPool(String alias, String driverClass,
	// String driverUrl, Properties otherProps) {
	//
	// String url = "proxool." + alias + ":" + driverClass + ":" + driverUrl;
	// Properties props = new Properties(); // new Properties(defaultProps);
	// props.putAll(otherProps);
	// return registerPool(url, props);
	// }

	/**
	 * 注册一个可用的proxool数据库连接池
	 * 
	 * @param url
	 *            -- proxool 的jdbc url
	 * @param props
	 *            -- 连接池配置属性
	 * @return -- 数据库连接池别名或者拼接的URL字符串
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	public static synchronized String registerPool(String url, Properties props) {

		String alias = null;
		logger.info("数据库连接URL:{}, 配置属性:{}", url, props);
		try {
			alias = ProxoolFacade.registerConnectionPool(url, props);
		} catch (Exception ex) {
			// ErrorTrackService ets = FwErrorTrackService.getInstance();
			// RuntimeAppException raex = ets.newRuntimeError("fw06002", ex);
			// raex.formatErrorMsg(alias, url, props);
			// ets.trackAndThrow(raex);
			String msg = "注册proxool数据库连接池异常. alias:%s, url:%s, props:%s";
			msg = String.format(msg, alias, url, props);
			throw new FwRuntimeException(msg, ex);
		}
		return alias;
	}

	/**
	 * 更新proxool数据库连接池
	 * 
	 * @param alias
	 *            -- 数据库连接池别名
	 * @param props
	 *            -- 更新的属性集合
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	// public static synchronized void updatePool(String alias, Properties
	// props) {
	//
	// logger.info("更新数据库[{}]连接池参数[{}]", alias, props);
	// try {
	// ProxoolFacade.updateConnectionPool("proxool." + alias, props);
	// } catch (Exception ex) {
	// // ErrorTrackService ets = FwErrorTrackService.getInstance();
	// // RuntimeAppException raex = ets.newRuntimeError("fw06028", ex);
	// // raex.formatErrorMsg(alias, props);
	// // ets.trackAndThrow(raex);
	// String msg=String.format("更新数据库连接池异常. alias:%s, props:%s",props);
	// throw new FwRuntimeException(msg,ex);
	// }
	// }

	/**
	 * 关闭指定别名的数据库连接池
	 * 
	 * @param alias
	 *            -- 数据库连接池
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	public static synchronized void removePool(String alias) {
		try {
			logger.info("关闭指定别名的数据库连接池[alias:{}]", alias);
			ProxoolFacade.removeConnectionPool(alias);
		} catch (Exception ex) {
			logger.error("关闭数据库连接池异常, 别名:" + alias, ex);
		}
	}

	/**
	 * 重建连接池
	 * 
	 * @param alias
	 *            -- 数据库别名
	 */
	public static synchronized void rebuildPool(String alias) {
		logger.info("重建数据库连接池:{}", alias);
		proxoolListener.rebuildPool(alias);
	}

	/**
	 * 检查数据库连接池是否健康
	 * 
	 * @param alias
	 *            -- 数据库别名
	 * @return -- true, 数据库连接池健康
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	public static boolean checkAlive(String alias) {

		Connection conn = null;
		boolean success = false;
		try {
			conn = getConnection(alias);
			success = (null != conn);
		} catch (Throwable thr) { /* 消除异常 */
		}
		CloseUtil.closeSilently(conn);
		logger.info("check dbpool[{}] alive: {}", alias, success);
		return success;
	}

	/**
	 * 根据xml配置文件注册数据库连接池
	 * 
	 * @param configFile
	 *            -- xml配置文件路径
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	public static synchronized void registerPoolFromXml(String configFile) {

		try {
			logger.info("尝试根据xml配置文件[{}]注册数据库连接池", configFile);
			InputStream is = DbConnectionProvider.class.getResourceAsStream(configFile);
			if (null != is) {
				InputStreamReader in = new InputStreamReader(is);
				JAXPConfigurator.configure(in, false);
			} else {
				logger.error("xml配置文件[{}]不存在.", configFile);
			}
		} catch (Exception ex) {

			throw new FwRuntimeException("通过xml配置文件注册proxool数据库连接池异常", ex);
		}
	}

	// /**
	// * 根据Properties配置文件注册数据库连接池
	// * @param configFile -- Properties配置文件路径
	// * @exception -- 运行时应用异常(RuntimeAppException)
	// */
	// public static synchronized void registerPoolFromProperty(String
	// configFile) {
	//
	// try {
	// PropertyConfigurator.configure(configFile);
	// } catch (Exception ex) {
	// //FwErrorTrackService.getInstance().trackAndThrowRuntime("fw06005", ex);
	// throw new FwRuntimeException("通过Properties配置文件注册proxool数据库连接池异常",ex);
	// }
	// }

	/**
	 * 根据Properties对象注册数据库连接池
	 * 
	 * @param properties
	 *            -- Properties对象
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	// public static synchronized void registerPoolFromProperty(Properties
	// properties) {
	//
	// try {
	// PropertyConfigurator.configure(properties);
	// } catch (Exception ex) {
	// //FwErrorTrackService.getInstance().trackAndThrowRuntime("fw06006", ex);
	// throw new FwRuntimeException("通过Properties对象注册proxool数据库连接池异常",ex);
	// }
	// }

	/**
	 * 根据连接池别名取数据库连接。
	 * 
	 * @param alias
	 *            -- 连接池别名
	 * @return -- 数据库连接
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	public static Connection getConnection(String alias) {

		Connection conn = null;
		try {
			conn = DriverManager.getConnection("proxool." + alias);
			logger.info("通过别名[{}]获取数据库连接", alias);
		} catch (Exception ex) {

			String msg = String.format("通过proxool的别名(alias):%s获取数据库连接异常", alias);
			throw new FwRuntimeException(msg, ex);
		}
		return conn;
	}

	/**
	 * proxool listerner implement class
	 * 
	 * @author zhandl (yuyoo zhandl@hainan.net)
	 * @teme 2010-5-27 下午03:38:41
	 */
	private static class FwProxoolListener implements ProxoolListenerIF {

		private static Map<String, Properties> pools = new HashMap<String, Properties>();

		public void onRegistration(ConnectionPoolDefinitionIF def, Properties completeInfo) {

			String alias = def.getAlias();
			String completeUrl = def.getCompleteUrl();
			String urlKey = getCompleteUrlHashKey(alias);
			completeInfo.setProperty(urlKey, completeUrl);
			pools.put(alias, completeInfo);
			logger.info("FwProxoolListener>>创建数据库连接池. alias:{}, proxool completeUrl:{}", alias, completeUrl);
			logger.info("FwProxoolListener>>completeInfo:{}", completeInfo);
		}

		public void onShutdown(String alias) {
			logger.info("FwProxoolListener>>关闭数据库连接池. alias:{}", alias);
		}

		void rebuildPool(final String alias) {

			try {
				Properties p = pools.get(alias);
				if (null != p) {
					String urlKey = getCompleteUrlHashKey(alias);
					String completeUrl = p.getProperty(urlKey);
					if (null != completeUrl) {
						String newAlias = registerPool(completeUrl, p);
						logger.info("FwProxoolListener>>重建数据库连接池. alias:{}, newAlias:{}", alias, newAlias);
					} else {
						logger.info("FwProxoolListener>>不存在completeUrl, 不允许重建数据库连接池.");
					}
				}
			} catch (Exception ex) {
				logger.error("FwProxoolListener>>重建数据库连接池异常.", ex);
			}
		}

		private String getCompleteUrlHashKey(String alias) {

			String urlKey = "fw_db_connect_url_";
			StringBuilder sb = new StringBuilder();
			sb.append(urlKey).append(alias).append(".").append(Integer.toString(urlKey.hashCode()));
			sb.append(".").append(Integer.toString(alias.hashCode()));
			urlKey = sb.toString();
			return sb.toString();
		}
	}
}

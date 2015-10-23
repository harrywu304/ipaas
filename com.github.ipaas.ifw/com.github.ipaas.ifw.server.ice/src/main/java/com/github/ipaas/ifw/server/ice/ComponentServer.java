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

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Communicator;
import Ice.InitializationData;
import Ice.ObjectAdapter;
import Ice.Properties;
import Ice.ServantLocator;
import Ice.Util;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.server.ice.mbean.CmptServerMonitor;
import com.github.ipaas.ifw.util.CloseUtil;
import com.github.ipaas.ifw.util.JMXUtil;
import com.github.ipaas.ifw.util.XmlUtil;

/**
 * 组件服务器
 * 
 * @author Chenql
 * @memo:可以动态加载新的服务,服务需要遵循ice的规则.
 * @changeLog: 修改代码,是代码结构与1.5.0相一致, 避免运行一段时间后, 使用socket连接ice服务器出现频繁的socket
 *             connection timeout异常.
 */
public final class ComponentServer {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(ComponentServer.class);

	/**
	 * 组件服务器实例集合
	 */
	static Map<String, ComponentServer> instances = new HashMap<String, ComponentServer>();

	/**
	 * 延迟加载classpath对象
	 */
	private LazyLoadJarClassPath lzClasspath = null;

	/**
	 * Ice.Communicator
	 */
	private Communicator ic = null;

	/**
	 * rpc代理适配器
	 */
	private ObjectAdapter adapter = null;

	/**
	 * 服务定位器
	 */
	private ServantLocator servantLocator = null;

	/**
	 * 线程池大小监听器
	 * */
	private ComponentServerThreadNotification threadNotification = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		ComponentServer inst = new ComponentServer();
		logger.debug("开始启动组件服务器......");
		inst.startup();
		instances.put("Ice.Communicator", inst);
	}

	/**
	 * 注册服务器监听mbean
	 * */
	private void registerMonitorMBean() {
		CmptServerMonitor cmptServerMonitor = new CmptServerMonitor(ic, threadNotification);
		JMXUtil.registerMBean(cmptServerMonitor, "com.github.ipaas.ifw.mbean.cmptserver:type=CmptServerMonitor");
	}

	private Map<String, String> loadComponentServerConfig() {
		InputStream ins = null;
		try {
			URL resource = ComponentServer.class.getResource("/fw_server.xml");
			ins = new FileInputStream(new File(resource.getPath()));
			Map<String, Object> fwServerConfig = XmlUtil.toMap(ins);
			Map<String, String> cServerConfig = (Map<String, String>) fwServerConfig.get("component_server");
			return cServerConfig;
		} catch (Exception e) {
			throw new FwRuntimeException("获取ICE服务器配置时出现异常", e);
		} finally {
			CloseUtil.closeSilently(ins);
		}
	}

	/**
	 * 启动服务器
	 */
	public void startup() {

		// 初始化Ice.Communicator环境
		Properties prop = Util.createProperties();
		Map<String, String> config = loadComponentServerConfig();
		for (String key : config.keySet()) {
			prop.setProperty(key, config.get(key));
		}
		logger.info("组件服务器 ice运行环境配置:{}", config);

		// 初始化Ice.Communicator对象
		InitializationData initData = new InitializationData();
		initData.properties = prop;

		// 新增监控线程池实际大小的hook
		threadNotification = new ComponentServerThreadNotification();
		initData.threadHook = threadNotification;

		ic = Ice.Util.initialize(initData);
		logger.debug("组件服务器配置Ice.Communicator对象成功.");

		String libPath = config.get("server.lazy_load_classpath");
		String interval = config.get("server.scan_interval");
		logger.info("延迟加载classpath, 动态增加jar包到classpath, 监听目录:[{}], 扫描间隔:[{}]", libPath, interval);

		lzClasspath = new LazyLoadJarClassPath(libPath, Integer.parseInt(interval));
		lzClasspath.startupScanTask();

		// ice适配器ID
		String adapterId = config.get("server.adapter_id");
		// 监听端口
		String endpoint = config.get("server.export_endpoint");
		logger.info("组件服务器当前使用适配器Id[{}], 监听请求端点[{}]", adapterId, endpoint);

		// servant定位器
		servantLocator = new ComponentServantLocator(config);

		adapter = ic.createObjectAdapterWithEndpoints(adapterId, endpoint);
		adapter.addServantLocator(servantLocator, ""); // 添加自定义servant定位器
		adapter.activate();
		// ic.waitForShutdown();

		// 初始服务上下文
		ServiceFactory.init();

		String threadName = "组件服务器启动线程[" + System.identityHashCode(this) + "]";
		logger.info(threadName);
		logger.info("组件服务器启动成功.");

		// 注册服务器监听mbean
		registerMonitorMBean();

		Thread.currentThread().setName(threadName);
	}

	/**
	 * 关闭服务器
	 */
	public void shutdown() {

		adapter.deactivate();
		adapter.destroy();
		ic.destroy();
	}
}

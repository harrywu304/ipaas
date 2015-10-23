package com.github.ipaas.ifw.core.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.util.CloseUtil;
import com.github.ipaas.ifw.util.FileUtil;
import com.github.ipaas.ifw.util.XmlUtil;

public final class FwConfigService {
	private static Logger logger = LoggerFactory.getLogger(FwConfigService.class);
	private static volatile Map<String, Object> CONFIG_CACHE;
	private static String CONFIG_FILE_PATH = "/config/fw_config.xml";
	private static String OSGI_CONFIG_FILE_PATH = "config/fw_config.xml";
	private static FwConfigService fwConfigService;

	private FwConfigService() {
	}

	/**
	 * 设置配置文件目录, 默认配置文件目录: /config/fw_config.xml
	 * 
	 * @param configFilePath
	 * @return
	 */
	public static FwConfigService setConfigFilePath(String configFilePath) {
		logger.debug("设置配置文件目录:" + configFilePath);
		fwConfigService.OSGI_CONFIG_FILE_PATH = configFilePath;
		loadConfig();
		return fwConfigService;
	}

	private static void loadConfig() {
		if (CONFIG_CACHE != null) {
			return;
		}

		InputStream ins = null;
		try {
			ins = getConfigFileInputStream();
			if (ins == null) {
				logger.warn(CONFIG_FILE_PATH + "文件不存在");
				return;
			}
			Map xmlMap = XmlUtil.toMap(ins);
			CONFIG_CACHE = xmlMap;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new FwRuntimeException(e);
		} finally {
			CloseUtil.closeSilently(ins);
		}
		CloseUtil.closeSilently(ins);
	}

	private static InputStream getConfigFileInputStream() {
		InputStream ins = FwConfigService.class.getResourceAsStream(CONFIG_FILE_PATH);
		if (ins == null) {
			URL url = FileUtil.getFileUrl(OSGI_CONFIG_FILE_PATH);
			if (url == null || !new File(url.getFile()).exists()) {
				logger.warn(OSGI_CONFIG_FILE_PATH + "文件不存在");
				return null;
			}
			try {
				return url.openStream();
			} catch (IOException e) {
				logger.warn(OSGI_CONFIG_FILE_PATH + "文件不存在");
				return null;
			}
		}
		return ins;
	}

	public static Map<String, Object> getConfig(String configName) {
		loadConfig();
		if (CONFIG_CACHE == null) {
			return null;
		}
		Map config = (Map) CONFIG_CACHE.get(configName);
		return config;
	}

	public static Config getSystemConfig() {
		Map dataMap = getConfig("system_properties");
		Config config = new XmlConfig(dataMap);
		return config;
	}

	public static Config getAppConfig() {
		Map dataMap = getConfig("app_properties");
		Config config = new XmlConfig(dataMap);
		return config;
	}
}
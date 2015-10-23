package com.github.ipaas.ideploy.filemonitor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.config.FwConfigService;
import com.github.ipaas.ifw.util.BeanUtil;
import com.github.ipaas.ifw.util.CloseUtil;
import com.github.ipaas.ifw.util.DateUtil;
import com.github.ipaas.ifw.util.FileUtil;
import com.github.ipaas.ifw.util.IPUtil;
import com.github.ipaas.ifw.util.JsonUtil;
import com.github.ipaas.ifw.util.ShellUtil;
import com.github.ipaas.ifw.util.StringUtil;
import com.github.ipaas.ifw.util.XmlUtil;

/**
 *@author Chenql
 */
public class FileMonitorTask extends java.util.TimerTask {

	private static Logger logger = LoggerFactory.getLogger(FileMonitorTask.class);

	/**
	 * 安装目录
	 */
	private static final String FILE_INSTALL_DIR = "felix.fileinstall.dir";
	private static final String FILE_INSTALL_DEFAULT_DIR = "./install";

	/**
	 * 自动更新(容器)
	 */
	private static final String SERVER_INSTALL_URL = "server_install_url"; // 容器
	private static final String SERVER_DOWNLOAD_PATH = "server_download_path"; // 容器下载到本地的目录
	private static final String BUNDLE_UPDATE_SITE = "bundle_update_site"; // BUNDLE
	private static final String SERVER_VERSION = "server_version"; // 容器version
	private static final String MUST_BUNDLES = "must_bundles"; // 必须的bundle

	/**
	 * 下载目录
	 */
	private static final String SERVER_DOWNLOAD_DIR = "fw.filedownload.dir";
	private static final String SERVER_DOWNLOAD_DEFAULT_DIR = "./temp";

	/**
	 * bundle的根节点
	 */
	private final static String BUNDLESNOTENAME = "bundles";
	/**
	 * bundle 节点名
	 */
	private final static String BUNDLENNAME = "bundle";

	/**
	 * config的根节点名
	 */
	private final static String CONFIGSNOTENAME = "configs";
	/**
	 * config的节点名
	 */
	private final static String CONFIGNAME = "config";

	/**
	 * 新安装的bundle的url
	 */
	private final static String URLNODENAME = "url";
	/**
	 * 已安装bundle的url
	 */
	private final static String OLDURLNAME = "oldurl";
	private final static String VERSION = "version";
	private final static String ARTIFACTID = "artifactId";
	private final static String GROUPID = "groupId";
	private final static String SYMBOLICNAME = "symbolicName";
	private final static String STATUS = "status";
	private final static String BUNDLE_ID = "id";

	/**
	 * 状态类型
	 */
	private static enum STATUS_TYPE {
		ADD, UPDATE, DEL, NO
	}

	private final static String FILENAME = "filename";

	/**
	 * config 的 properties 节点名
	 */
	private final static String PROPERTIES = "properties";

	private final static String PID = "pid";
	private final static String UPDATETIME = "updateTime";

	/**
	 * 配置文件的后缀
	 */
	private static String configExt = ".config";

	/**
	 * 更新文件目录,容器更新的目录(.install)
	 */
	private String installDir;

	/**
	 * ideploy_site的url
	 */
	private URL updateURL;

	/**
	 * 下革文件目录(bundle 下载到本地的目录)
	 */
	private String downloadDir;

	/**
	 * update size 的 bundles 列表
	 */
	private List<Map<String, String>> bundleMaps;

	/**
	 * update size 的 configs 列表
	 */
	private List<Map> configMaps;

	private BundleContext context;

	/**
	 * app properties setting
	 */
	Map<String, Object> appProperties;

	/**
	 * 上次更新文件
	 */
	private static String updateFile;

	public FileMonitorTask(BundleContext context) {
		appProperties = FwConfigService.setConfigFilePath("config/fw_config.xml").getAppConfig().getDataMap();
		this.context = context;
		logger.info("自动更新任务初始化.:  " + JsonUtil.toJson(appProperties));
	}

	@Override
	public void run() {
		InputStream inputStream = null;
		try {
			setParams(context);
			if (updateURL == null) {
				logger.error("没有配置系统自动更新.");
				return;
			}
			bundleMaps = new ArrayList<Map<String, String>>();
			configMaps = new ArrayList<Map>();
			// 读取XML
			URL url = null;

			inputStream = updateURL.openStream();

			// 检测是否需要检测,文件相同就不需要检测
			if (checkUpdateFile(inputStream)) {

				Map map = XmlUtil.toMap(updateFile);

				// 检测是否需要更新Agent,不需要更新Agent时,才需要检测是否更新bundle
				if (!updateServer(map)) {

					// 得到Bundle和Config
					setBundleConfig(map);

					// 更新Bundle
					updateBundles();
					
					// 更新Config 先更新配置,再更新Bundle
					updateConfigs();

					

				}
			}

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			CloseUtil.closeSilently(inputStream);
		}

	}

	/**
	 * 检测是否需要更新Agent,不需要更新Agent时,才需要检测是否更新bundle
	 * 
	 * @param map
	 * @return true为需要更新server，否之为false
	 * @throws IOException
	 */
	private boolean updateServer(Map map) {
		try {
			String version = (String) appProperties.get(SERVER_VERSION); // fw_client.xml
																			// version
			logger.debug("version: " + version);
			String configVersion = (String) map.get(VERSION); // ideploy_site.xml
																// version
																// <version>1.0.4</version>
			// 检查是否需要更新
			if (configVersion != null) {
				if (version == null || configVersion.compareTo(version) > 0) {

					logger.info(" Agent的 version是" + version + ",服务器的version是" + configVersion + ",现在进行容器更新.");

					// 下载文件
					String serverUpdateSite = (String) appProperties.get(SERVER_INSTALL_URL);
					if (serverUpdateSite == null) {
						logger.debug("没有配置server更新,不能更新server。");
						return false;
					}

					// 文件下载到本地的目录
					String installPath = (String) appProperties.get(SERVER_DOWNLOAD_PATH);
					if (installPath == null) {
						logger.debug("没有配置 文件下载到本地的目录,不能更新server。");
						return false;
					}

					FileUtil.delete(installPath.toString());
					FileUtil.newFolder(installPath.toString());

					// filename
					String[] fileNameSpile = serverUpdateSite.split("/");
					String fileSimpleName = fileNameSpile[fileNameSpile.length - 1];
					String fileFullName = installPath + "/" + fileSimpleName;

					if (FileUtil.downloadFile(serverUpdateSite, installPath, fileSimpleName)) {
						// 创建有内容的文件
						String installFileName = installPath + "/install.sh";
						String contextUrl = new StringBuilder(installPath).append("/").append(fileSimpleName)
								.append(" > ").append(installPath).append("/data.log 2>&1 &").toString();
						logger.debug(installFileName);
						logger.debug(contextUrl);
						FileUtil.createTxtFile(installFileName, contextUrl);
						// 修改文件权限
						ShellUtil.execShell("chmod 755 " + installFileName);
						ShellUtil.execShell("chmod 755 " + fileFullName);
						logger.debug("执行:  "+ installFileName);
						logger.debug("fileFullName    "+ fileFullName);
						// 运行脚本
						Thread.sleep(1000);
						ShellUtil.execShell(installFileName);
					}
					// 返回true
					return true;
				}
			}
		} catch (Exception e) {
			logger.error("读取更新Agent配置失败", e);
		}
		// 如果不需要更新就返回false
		return false;
	}

	/**
	 * 检测是否需要检测,文件相同就不需要检测
	 * 
	 * @param inputStream
	 * @return true 表示需要检测,false 表示不需要检测
	 */
	private boolean checkUpdateFile(InputStream inputStream) {
		if (inputStream == null) {
			return false;
		}
		String fileText = FileUtil.inputStreamToString(inputStream);
		if (updateFile != null && updateFile.equals(fileText)) {
			return false;
		}
		updateFile = fileText;
		return true;
	}

	/**
	 * 更新Config
	 */
	private void updateConfigs() {
		// 得到更新的bundles的信息
		setConfigStatus();

		// 更新Bundle
		if (configMaps != null) {
			for (Map config : configMaps) {
				try {
					updateConfig(config);
					Thread.sleep(1000);
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}

	}

	/**
	 * 更新单个配置
	 * 
	 * @param config
	 */
	private void updateConfig(Map config) {
		String status = (String) config.get(STATUS);
		if (status != null) {
			// 增加
			if (status.equals(STATUS_TYPE.ADD.name())) {
				logger.debug("添加配置文件:"+JsonUtil.toJson(config ));
				updateConfigForAdd(config);
			}
			// 更新
			else if (status.equals(STATUS_TYPE.UPDATE.name())) {
				logger.debug("更新配置文件:"+JsonUtil.toJson(config ));
				updateConfigForDel(config);
				updateConfigForAdd(config);

			}
			// 删除
			else if (status.equals(STATUS_TYPE.DEL.name())) {
				logger.debug("删除配置文件:"+JsonUtil.toJson(config ));
				updateConfigForDel(config);
			}
		}

	}

	/**
	 * 删除 install dir目录的文件
	 * 
	 * @param config
	 */
	private void updateConfigForDel(Map config) {
		// 删除 install dir目录的文件
		try {
			URL url = new URL("file://" + (String) config.get(URLNODENAME));
			if (FileUtil.delete(url.getFile())) {
				logger.info(url + "删除文件成功.");
			}
		} catch (MalformedURLException e) {
			logger.error(e.getMessage());
		}
	}

	/**
	 * 保存文件到 install dir 目录
	 * 
	 * @param config
	 */
	private void updateConfigForAdd(Map config) {
		// 保存文件到 install dir 目录
		Map<String, String> properties = (Map<String, String>) config.get(PROPERTIES);
		File configFile = new File((String) config.get(URLNODENAME));
		FileWriter resultFile = null;
		PrintWriter myFile = null;
		try {
			if (!configFile.exists()) {
				configFile.createNewFile();
			}
			if (configFile.exists()) {
				if (properties != null) {
					resultFile = new FileWriter(configFile);
					myFile = new PrintWriter(resultFile);

					for (Map.Entry<String, String> entry : properties.entrySet()) {
						String str = new StringBuilder(entry.getKey()).append("=").append("\"")
								.append(entry.getValue()).append("\"").toString();
						myFile.println(str);
					}
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			try {
				if (myFile != null)
					myFile.close();
				if (resultFile != null)
					resultFile.close();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
		}
		logger.info("配置文件生成成功:" + configFile.getName());
	}

	/**
	 * 得到Config的更新信息,设置status URLNODENAME等
	 */
	private void setConfigStatus() {
		// 查找installDir的config文件
		File[] fileArray = new File(installDir).listFiles();

		if (fileArray != null) {
			for (File configFile : fileArray) {
				String fn = configFile.getName();
				if (fn.length() <= 7 || (!fn.substring(fn.length() - 7).equals(configExt))) {
					continue;
				}
				logger.debug("configFileconfigFile----> "+ configFile);
				String pid = configFile.getName().substring(0, fn.length() - 7);
				logger.debug("pid----> "+ pid);
				Map configMap = checkConfig(pid);
				// 不存在
				if (configMap == null) {
					configMap = new HashMap();
					configMap.put(PID, pid);
					configMap.put(STATUS, STATUS_TYPE.DEL.name());
					// 安装的位置
					configMap.put(URLNODENAME, configFile.getAbsolutePath());
					configMaps.add(configMap);
				} else {
					// 存在,
					// 安装的位置
					configMap.put(URLNODENAME, configFile.getAbsolutePath());

					String updateTime = (String) configMap.get(UPDATETIME);
					String lastModified = DateUtil.convertDateToStr(new Date(configFile.lastModified()),
							DateUtil.DEFAULT_LONG_DATE_FORMAT);
					if (updateTime.compareTo(lastModified) < 0) {
						configMap.put(STATUS, STATUS_TYPE.UPDATE.name());
					} else {
						configMap.put(STATUS, STATUS_TYPE.NO.name());
					}
				}
			}
			
			logger.debug("  configMaps: "+ JsonUtil.toJson(configMaps));
		}

		// 设置 STATUS URLNODENAME
		if (configMaps != null) {
			for (Map configMap : configMaps) {
				if (StringUtil.isNullOrBlank((String) configMap.get(STATUS))) {
					configMap.put(STATUS, STATUS_TYPE.ADD.name());
					// URLNODENAME
					String url = new StringBuilder(installDir).append("/").append(configMap.get(PID)).append(configExt)
							.toString();
					configMap.put(URLNODENAME, url);

				}
			}
		}

	}

	/**
	 * 检查configmaps中是否包括pid
	 * 
	 * @param pid
	 * @return
	 */
	private Map checkConfig(String pid) {
		if (configMaps != null) {
			for (Map configMap : configMaps) {
				if (configMap.get(PID).equals(pid)) {
					return configMap;
				}
			}
		}
		return null;
	}

	/**
	 * 更新Bundle
	 * 
	 * @throws MalformedURLException
	 */
	private void updateBundles() throws MalformedURLException {
		// 得到更新的bundles的信息
		setBundleStatus();

		// 更新Bundle
		if (bundleMaps != null) {
			String mustBundles = (String) appProperties.get(MUST_BUNDLES);
			
			logger.debug("mustBundles:   "+ mustBundles);
			for (Map<String, String> bundle : bundleMaps) {
				try {

					// 不是必须的bundle才操作
					if (getNotMustBundle(bundle, mustBundles)) {
						logger.debug(" updateBundle 不是必须的bundle:  "+bundle);
						updateBundle(bundle);
					}

				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}

	}

	/**
	 * 
	 * @param bundle
	 * @param mustBundles
	 * @return 必须的bundle返回false,否则还回true
	 */
	private boolean getNotMustBundle(Map<String, String> bundle, String mustBundles) {
		try {
			if (mustBundles != null) {
				String[] bundleNames = mustBundles.split(",");
				for (String bundleName : bundleNames) {
					// 配置文件must_bundles中存在就是必须的bundles
					if (bundle.get(SYMBOLICNAME).equals(bundleName.trim())) {
						return false;
					}
				}
			}
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
		}
		return true;
	}

	/**
	 * 更新单个Bundle
	 * 
	 * @param bundle
	 * @throws InterruptedException
	 */
	private void updateBundle(Map<String, String> bundle) throws InterruptedException {
		String status = bundle.get(STATUS);
		String fileName = bundle.get(FILENAME);
		if (status != null) {
			// 增加
			if (status.equals(STATUS_TYPE.ADD.name())) {
				// 下载
				File newFile = null;
				if (FileUtil.downloadFile(bundle.get(URLNODENAME), downloadDir, fileName)) {
					newFile = new File(new StringBuilder(downloadDir).append("/").append(fileName).toString());
				}

				if (newFile != null) {
					// 另存
					FileUtil.move(newFile, installDir);
					Thread.sleep(1000);
				}
			}
			// 更新
			else if (status.equals(STATUS_TYPE.UPDATE.name())) {
				// 新增
				// 下载
				try {
					File newFile = null;
					if (FileUtil.downloadFile(bundle.get(URLNODENAME), downloadDir, fileName)) {
						newFile = new File(new StringBuilder(downloadDir).append("/").append(fileName).toString());
					}
					if (newFile != null) {
						// 另存
						FileUtil.move(newFile, installDir);
						Thread.sleep(1000);
					}
					// 删除旧的
					URL url = new URL(bundle.get(OLDURLNAME));
					Bundle runningBundle = this.getBundleById(bundle);
					if (runningBundle != null) {
						try {
							logger.info(" delete  bundle: " + bundle.get(SYMBOLICNAME) + "  version ["
									+ bundle.get(VERSION) + "]");
							runningBundle.stop();
							runningBundle.uninstall();
						} catch (BundleException e) {
							logger.error(" bundle 卸载异常", e);
						}
					}

					if (FileUtil.delete(url.getFile())) {
						Thread.sleep(1000);
						logger.info("删除插件{" + url + "}成功.");
					}
				} catch (MalformedURLException e) {
					logger.debug(e.getMessage(), e);
				}
			}
			// 删除
			else if (status.equals(STATUS_TYPE.DEL.name())) {
				try {
					URL url = new URL(bundle.get(OLDURLNAME));
					Bundle runningBundle = this.getBundleById(bundle);
					if (runningBundle != null) {
						try {
							logger.info(" delete  bundle: " + bundle.get(SYMBOLICNAME) + "  version ["
									+ bundle.get(VERSION) + "]");
							runningBundle.stop();
							runningBundle.uninstall();
						} catch (BundleException e) {
							logger.error(" bundle 卸载异常", e);
						}
					}

					if (FileUtil.delete(url.getFile())) {
						Thread.sleep(1000);
						logger.info("删除插件{" + url + "}成功.");
					}
				} catch (MalformedURLException e) {
					logger.debug(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 得到Bundle的更新信息
	 */
	private void setBundleStatus() {
		// 设置status fileName等
		Bundle[] bundles = context.getBundles();

		if (bundles != null) {
			for (Bundle bundle : bundles) {
				Dictionary dictionary = bundle.getHeaders();
				String symbolicName = (String) dictionary.get("Bundle-SymbolicName");
				String version = (String) dictionary.get("Bundle-Version");
				logger.debug("setBundleStatus--->symbolicName:   "+ symbolicName);
				Map<String, String> bundleMap = checkBundle(symbolicName);
				// 不存在
				if (bundleMap == null) {
					bundleMap = new HashMap<String, String>();
					bundleMap.put(SYMBOLICNAME, symbolicName);
					bundleMap.put(VERSION, version);
					bundleMap.put(STATUS, STATUS_TYPE.DEL.name());// 要删除
					// 安装的位置
					bundleMap.put(OLDURLNAME, bundle.getLocation());
					bundleMap.put(BUNDLE_ID, String.valueOf(bundle.getBundleId()));// bundleId
					bundleMaps.add(bundleMap);

				} else {
					// 存在,bundleMap的version大
					// 安装的位置
					bundleMap.put(OLDURLNAME, bundle.getLocation());

					if (version.compareTo(bundleMap.get(VERSION)) < 0) {
						bundleMap.put(STATUS, STATUS_TYPE.UPDATE.name());

					} else {
						bundleMap.put(STATUS, STATUS_TYPE.NO.name());
					}
				}
			}
		}
		// 设置 bundleMaps 的STATUS
		if (bundleMaps != null) {// 新添加的Bundle
			for (Map<String, String> bundleMap : bundleMaps) {
				if (StringUtil.isNullOrBlank(bundleMap.get(STATUS))) {
					bundleMap.put(STATUS, STATUS_TYPE.ADD.name());
				}
			}
		}

	}

	/**
	 * 检查 symbolicName是不在bundlesMap中
	 * 
	 * @param symbolicName
	 * @return
	 */
	private Map<String, String> checkBundle(String symbolicName) {
		// TODO Auto-generated method stub
		if (bundleMaps != null) {
			for (Map<String, String> bundleMap : bundleMaps) {
				if (bundleMap.get(SYMBOLICNAME).trim().equals(symbolicName)) {
					return bundleMap;
				}
			}
		}
		logger.info(symbolicName + "不存在");
		return null;
	}

	/**
	 * 得到Bundle和Config
	 * 
	 * @param map
	 */
	private void setBundleConfig(Map map) {

		String url = (String) map.get(URLNODENAME);
		if (url == null) {
			url = "";
		}

		// 得到Bundle的配置
		Map mapBundles = (Map) map.get(BUNDLESNOTENAME);
		if (mapBundles != null) {
			String bundleDir = new StringBuilder(url).append( // http://repos.ideploy.ipaas/bundles
					mapBundles.get(URLNODENAME)).toString();

			// 得到各个bundle的详细信息
			Object obj = mapBundles.get(BUNDLENNAME);
			if (obj != null) {
				if (obj instanceof List) {
					bundleMaps = (List) obj;
				} else {
					bundleMaps = new ArrayList();
					bundleMaps.add((Map<String, String>) obj);
				}
				for (Map<String, String> bundle : bundleMaps) {
					String groupUrl = bundle.get(GROUPID).replace(".", "/");
					String artifactId = bundle.get(ARTIFACTID);
					String version = bundle.get(VERSION);
					String filename = new StringBuilder(artifactId).append("-").append(version).append(".jar")
							.toString();
					bundle.put(FILENAME, filename);
					String realurl = new StringBuilder(bundleDir).append("/").append(groupUrl).append("/")
							.append(artifactId).append("/").append(version).append("/").append(filename).toString();
					bundle.put("url", realurl);
				}
			}
		}

		// 得到每个bundle的配置信息
		Map mapConfigs = (Map) map.get(CONFIGSNOTENAME);
		if (mapConfigs != null) {
			// 得到各个configs的详细信息
			Object obj = mapConfigs.get(CONFIGNAME);
			if (obj != null) {
				if (obj instanceof List) {
					configMaps = (List) obj;
				} else {
					configMaps = new ArrayList();
					configMaps.add((Map) obj);
				}
			}
		}

	}

	/**
	 * 设置参数
	 * 
	 * @param context
	 * @return
	 */
	private boolean setParams(BundleContext context) {

		logger.debug("appProperties: " + JsonUtil.toJson(appProperties));
		// installdir (本地安装的路径)
		if (context != null) {
			String fileInstallDir = context.getProperty(FILE_INSTALL_DIR);
			logger.debug("fileInstallDir: " + fileInstallDir);
			if (StringUtil.isNullOrBlank(fileInstallDir)) {
				fileInstallDir = FILE_INSTALL_DEFAULT_DIR;
			}
			File locationFile = new File(fileInstallDir);
			if (!locationFile.exists()) {
				locationFile.mkdir();
			}
			installDir = locationFile.getAbsolutePath();
			logger.debug("installDir: " + installDir);
		}

		// downloadDir (下载到本地的路径)
		if (context != null) {
			String fileDownloadDir = (String) appProperties.get(SERVER_DOWNLOAD_DIR);
			if (StringUtil.isNullOrBlank(fileDownloadDir)) {
				fileDownloadDir = SERVER_DOWNLOAD_DEFAULT_DIR;
			}
			File locationFile = new File(fileDownloadDir);
			if (!locationFile.exists()) {
				locationFile.mkdir();
			}
			downloadDir = locationFile.getAbsolutePath();
			logger.debug("downloadDir: " + downloadDir);
		}

		// update site (ideploy_site.xml)
		try {
			String updateUrlStr = (String) appProperties.get(BUNDLE_UPDATE_SITE);
			logger.debug("updateUrlStr:  " + updateUrlStr);
			// 设ip
			String path = MessageFormat.format(updateUrlStr, IPUtil.getLocalIP(true));
			updateURL = new URL(path);
		} catch (Exception ex) {
			logger.error("bundle_update_site没有配置,不能自动更新", ex);
			return false;
		}

		return true;
	}

	private Bundle getBundleById(Map<String, String> map) {
		Bundle bundle = null;
		if (map.containsKey(BUNDLE_ID)) {
			try {
				long bundleId = Long.valueOf(map.get(BUNDLE_ID));
				bundle = context.getBundle(bundleId);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}

		return bundle;
	}
}

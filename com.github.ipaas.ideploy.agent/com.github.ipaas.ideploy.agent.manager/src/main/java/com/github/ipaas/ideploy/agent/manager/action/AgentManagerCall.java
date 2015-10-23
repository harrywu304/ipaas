package com.github.ipaas.ideploy.agent.manager.action;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.service.action.CustomActionCall;
import com.github.ipaas.ifw.core.config.FwConfigService;
import com.github.ipaas.ifw.util.BeanUtil;
import com.github.ipaas.ifw.util.FileUtil;
import com.github.ipaas.ifw.util.ShellUtil;
import com.github.ipaas.ifw.util.StringUtil;

public class AgentManagerCall implements CustomActionCall {

	private static Logger logger = LoggerFactory.getLogger(AgentManagerCall.class);
	private final static String OPERAT_RESTART = "restart";
	private final static String COMMAND_RESTART = "/www/agent/agent.ideploy.ipaas/bin/osgi.sh restart";
	private final static String COMMAND_STOP = "/www/agent/agent.ideploy.ipaas/bin/osgi.sh stop";
	private final static String OPERAT_STOP = "stop";
	private final static String OPERAT_INSTALL = "install";

	/**
	 * 自动Reinsall(容器)
	 */
	private static final String SERVER_INSTALL_URL = "server_install_url"; // 容器
	private static final String SERVER_DOWNLOAD_PATH = "server_download_path"; // 容器下载到本地的目录

	@Override
	public Object call(BundleContext context, Map<String, Object> map) {
		// context.
		try {
			String operat = (String) map.get("operat");

			if (StringUtil.isNullOrBlank(operat)) {
				throw new Exception("参数错误");
			}

			logger.info("开始执行命令:" + operat);
			if (operat.equals(OPERAT_RESTART)) {
				AgentManagerCall.execQuietly(COMMAND_RESTART);
			} else if (operat.equals(OPERAT_STOP)) {
				AgentManagerCall.execQuietly(COMMAND_STOP);
			} else if (operat.equals(OPERAT_INSTALL)) {
				AgentManagerCall.reinstall();
			}
			return "success";
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return "failure";
		}

	}

	/**
	 * 执行脚本 忽略结果
	 * 
	 * @param shell
	 * @throws Exception
	 */
	public static void execQuietly(final String shell) throws Exception {
		Job job = new Job() {
			@Override
			public void execute(JobExecutionContext arg0) throws JobExecutionException {
				try {
					Thread.currentThread().sleep(1000);// 休眠一秒后停止agent
					logger.info(" exec(shell):" + shell);
					Process process = Runtime.getRuntime().exec(shell);
				} catch (Exception e) {
					logger.error("执行命令{" + shell + "}失败", e);
				}
			}
		};
		job.execute(null);
		// process.waitFor();
	}

	public static void reinstall() {

		logger.info(" Agent Reinsatll.");
		try {
			Map<String, Object> appProperties = BeanUtil.wrapToMap(FwConfigService.getAppConfig());

			// 下载文件
			String serverUpdateSite = (String) appProperties.get(SERVER_INSTALL_URL);
			if (serverUpdateSite == null) {
				logger.debug("没有配置server ,不能Reinsall  server。");
				return;
			}

			// 文件下载到本地的目录
			String installPath = (String) appProperties.get(SERVER_DOWNLOAD_PATH);
			if (installPath == null) {
				logger.debug("没有配置 文件下载到本地的目录,不能Reinsall。");
				return;
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
				String contextUrl = new StringBuilder(installPath).append("/").append(fileSimpleName).append(" > ")
						.append(installPath).append("/data.log 2>&1 &").toString();
				logger.debug(installFileName);
				logger.debug(contextUrl);
				FileUtil.createTxtFile(installFileName, contextUrl);
				// 修改文件权限
				ShellUtil.execShell("chmod 755 " + installFileName);
				ShellUtil.execShell("chmod 755 " + fileFullName);

				// 运行脚本
				Thread.sleep(1000);
				ShellUtil.execShell(installFileName);
			}
		} catch (Exception e) {
			logger.error("Agent 重装失败", e);
		}
	}
}

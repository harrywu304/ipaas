package com.github.ipaas.ideploy.filemonitor;

import java.util.Dictionary;
import java.util.Timer;
import java.util.TimerTask;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 容器或bundle更新
 * @author Chenql
 */
public class FileMonitorManagedService implements ManagedService {

	private static Logger logger = LoggerFactory.getLogger(FileMonitorManagedService.class);

	/**
	 * 调度
	 */
	private Timer timer;
	private TimerTask timerTask;

	/**
	 * 状态
	 */
	private String STATUS_START = "start";
	private String STATUS_STOP = "stop";

	/**
	 * 参数
	 */
	private String DELAY = "delay";
	private String PERIOD = "period";
	private String STATUS = "status";

	/**
	 * 上下文
	 */
	private BundleContext context;

	/**
	 * 初始化上下文
	 * 
	 * @param context
	 */
	public FileMonitorManagedService(BundleContext context) {
		this.context = context;
	}

	@Override
	public void updated(Dictionary properties) throws ConfigurationException {
		try {
			stop();
			startTask(properties, context);
		} catch (Exception ex) {
			logger.error("更新文件检测出错.", ex);
		}
	}

	/**
	 * 开启任务
	 * 
	 * @param properties
	 * @param context
	 */
	public void startTask(Dictionary properties, BundleContext context) {
		// 相差时间 1 分钟
		Integer period = 1000 * 60;
		Integer delay = 1000 * 1;
		String status = STATUS_START; // 状态
		logger.debug("开启配置文件更新任务");
		if (properties != null) {
			if (properties.get(DELAY) != null) {
				delay = Integer.valueOf((String) properties.get(DELAY));
			}
			if (properties.get(PERIOD) != null) {
				period = Integer.valueOf((String) properties.get(PERIOD));
			}
			if (properties.get(STATUS) != null) {
				status = (String) properties.get(STATUS);
			}
		}

		// 如果状态不是停止,则起动作业
		if (!status.equals(STATUS_STOP)) {
			// timer
			timer = new Timer();
			// "updatesite"
			timerTask = new FileMonitorTask(context);
			logger.debug(delay +  "    后执行配置更新任务");
			timer.schedule(timerTask, delay, period);
		}
	}

	/**
	 * 暂定调度
	 */
	public void stop() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
}

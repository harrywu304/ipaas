package com.github.ipaas.ideploy.agent.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.quartz.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.mq.MqListenService;
import com.github.ipaas.ifw.mq.MqServiceManager;
import com.github.ipaas.ifw.mq.activemq.FwMqConstant;
import com.github.ipaas.ifw.mq.activemq.FwMqListenService;
import com.github.ipaas.ifw.util.IPUtil;
import com.github.ipaas.ifw.util.JsonUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * Agent服务的启动和关闭
 * @author Chenql
 */
public class ServiceManagedService implements ManagedService {

	private final static String queueNamePrex = "ideploy.agent.";

	private static Logger logger = LoggerFactory
			.getLogger(ServiceManagedService.class);

	private static Map<String,Object> DEFAULT_PARAM;
	static{
		DEFAULT_PARAM = new HashMap<String,Object>();
		DEFAULT_PARAM.put("appIds", "ideploy,crs");
		DEFAULT_PARAM.put("connect_timeout", "1000");
		DEFAULT_PARAM.put("operate_timeout", "5000");
		DEFAULT_PARAM.put("maximum_connections", "10");
		DEFAULT_PARAM.put("maximum_active", "500");
		DEFAULT_PARAM.put("idle_timeout", "60000");
		DEFAULT_PARAM.put("listen_thread_pool_max_size", "5");
		DEFAULT_PARAM.put("serverUrl", "192.168.1.103:61616");

	}

	/**
	 * 上下文环境
	 */
	BundleContext context;

	/**
	 * consumer工厂
	 */
	MqListenService[] mqListenServices = null;

	/**
	 * 调度
	 */
	private Scheduler sched = null;

	/**
	 * 成员初始化
	 *
	 * @param context
	 */
	public ServiceManagedService(BundleContext context) {
		System.out.println("ServiceManagedService   init    "+  JsonUtil.toJson(DEFAULT_PARAM));
		this.context = context;
		// 设置消费者
		setConsumer(DEFAULT_PARAM);

		// 设置调度
		//setScheduler(DEFAULT_PARAM);
	}

	@Override
	public void updated(java.util.Dictionary properties)
			throws ConfigurationException {
		System.out.println("######################################################  Service updated:  ######################################################");

		// 设置参数
		Map<String, Object> maps = setParams(properties);
		logger.debug("Service updated maps:"+JsonUtil.toJson(maps));
		// 设置消费者
		setConsumer(maps);

		// 设置调度
		//setScheduler(maps);

	}

//	/**
//	 * 设置调度
//	 * 
//	 * @param maps
//	 * @throws SchedulerException
//	 */
//	private void setScheduler(Map<String, Object> maps) {
//		logger.debug("setScheduler");
//		// 停止调度
//		stopScheduler();
//
//		// 设置调度
//		try {
//
//			SchedulerFactory sf = new StdSchedulerFactory();
//			sched = sf.getScheduler();
//			sched.start();
//
//			// 得到可能使用的调度 (task.name="<a>,<b>")
//			// 名称生成为 <dataType>.<appId>
//			// task.a.dataType="jvm",
//			// task.a.ip="192.168.122.1",
//			// task.a.port="11010"
//			// task.a.cronSche="* * * * * * "
//			// task.a.status="start"
//
//			// 启动调度,先得到可以调度的key,然后根据每个key的设置来启动调度
//			String taskNameStr = (String) maps.get(Constant.PARAM_TASK_NAME
//					+ "name");
//			if (StringUtil.isNullOrBlank(taskNameStr)) {
//				String[] taskNames = taskNameStr.split(Constant.TASK_SPLIT);
//				if (taskNames != null) {
//					for (String taskName : taskNames) {
//						try {
//							// 设置参数
//							Map<String, String> taskParams = getTaskParams(
//									maps, taskName);
//
//							// 得到agentMonitor
//							AgentMonitor agentMonitor = getAgentMonitor(taskParams);
//							logger.info(" get the agentMonitor:   "
//									+ agentMonitor.getClass().getName());
//
//							// 如果状态不是停止,则起动作业
//							String status = taskParams
//									.get(Constant.PARAM_STATUS);
//
//							// 设置启动参数
//							// MqServiceManager.setParams(maps);
//
//							if (!status.equals(Constant.PARAM_STATUS_STOP)) {
//								startTask(taskName, taskParams, agentMonitor,
//										(String) maps.get(FwMqConstant.APPIDS));
//							}
//						} catch (Exception ex) {
//							logger.error("设置调度失败", ex);
//						}
//
//					}
//				}
//			}
//
//		} catch (Exception ex) {
//			logger.error("设置调度失败", ex);
//		}
//	}

//	/**
//	 * 起动任务
//	 * 
//	 * @param taskName
//	 * @param taskParams
//	 * @param agentMonitor
//	 * @throws SchedulerException
//	 */
//	private void startTask(String taskName, Map<String, String> taskParams,
//			AgentMonitor agentMonitor, String appId) throws SchedulerException {
//		// jobKey = job.getKey();
//		JobDetail job = newJob(AgentMonitorJob.class).withIdentity(
//				"job" + taskName, "group" + taskName).build();
//
//		// set job param
//		job.getJobDataMap().put(Constant.PARAM_NAME, taskParams);
//		job.getJobDataMap().put(Constant.PARAM_AGENTOBJECT, agentMonitor);
//		job.getJobDataMap().put(Constant.APP_ID, appId);
//
//		String cronSche = taskParams.get(Constant.PARAM_CRONSCHE);
//		// 先用每分钟来测试
//		CronTrigger trigger = newTrigger()
//				.withIdentity("trigger" + taskName, "group" + taskName)
//				.withSchedule(cronSchedule(cronSche)).build();
//		logger.info(taskName + "Start to scheduleJob    "
//				+ agentMonitor.getClass().getName());
//		sched.scheduleJob(job, trigger);
//	}

//	/**
//	 * 得到AgentMonitor
//	 * 
//	 * @param taskParams
//	 * @return
//	 * @throws InvalidSyntaxException
//	 * @throws Exception
//	 */
//	private AgentMonitor getAgentMonitor(Map<String, String> taskParams)
//			throws InvalidSyntaxException, Exception {
//		String dataType = taskParams.get(Constant.PARAM_DATA_TYPE);
//		// 得到每个任务的
//		ServiceReference[] serviceRefs = context.getServiceReferences(
//				AgentMonitor.class.getName(), "(" + Constant.COMMANDNAME + "="
//						+ dataType + ")");
//		// serviceRefs = context.getAllServiceReferences(
//		// AgentAction.class.getName(),null);
//		if (serviceRefs == null || serviceRefs.length == 0) {
//			throw new Exception("系统中没有" + dataType + "的Command服务或服务不可用");
//		}
//		if (serviceRefs.length > 1) {
//			throw new Exception("系统中" + dataType + "此Command的服务超过1个");
//		}
//		AgentMonitor agentMonitor = (AgentMonitor) context
////				.getService(serviceRefs[0]);
//		return agentMonitor;
//	}

//	/**
//	 * 得到调度的参数
//	 * 
//	 * @param maps
//	 * @param taskName
//	 * @return
//	 */
//	private Map<String, String> getTaskParams(Map<String, Object> maps,
//			String taskName) {
//		Map<String, String> result = new HashMap<String, String>();
//		if (maps != null) {
//			Set<String> params = maps.keySet();
//			String compareChar = new StringBuilder(Constant.PARAM_TASK_NAME)
//					.append(taskName).toString();
//			int length = compareChar.length();
//			for (String param : params) {
//				if (param.length() > length) {
//					if (compareChar.equals(param.substring(0, length))) {
//						// 增加到任务参数
//						String shortParam = param.substring(length + 1);
//						result.put(shortParam, (String) maps.get(param));
//					}
//				}
//			}
//		}
//		return result;
//	}

//	/**
//	 * 停止调度
//	 */
//	private void stopScheduler() {
//		try {
//			if (sched != null) {
//				logger.info(" stop Scheduler");
//				sched.shutdown();
//			}
//		} catch (Exception ex) {
//			logger.error(ex.getMessage(), ex);
//		} finally {
//			sched = null;
//		}
//	}

	/**
	 * 设置消费者
	 *
	 * @param maps
	 *            参数
	 */
	private void setConsumer(Map<String, Object> maps) {

		logger.debug("设置消费者----->"+JsonUtil.toJson(maps));
		// 停止 consumer
		stopConsumer();

		// 注册mqservice
		String[] appIds = {"ideploy","crs"};
		System.out.println("appIds------->    "+appIds);
		// 设置参数
		MqServiceManager.setParams(maps);
		System.out.println(" maps.get(appIds)-----:"+ maps.get("appIds"));

		System.out.println("MqServiceManager.setParams(maps)   "+JsonUtil.toJson(maps));

		if (appIds != null) {
			mqListenServices = new MqListenService[appIds.length];
			for (int i = 0; i < appIds.length; i++) {
				try {

					System.out.println("----------------------------------------------"+appIds[i]+"--------------------------------");

					String appId = appIds[i];
					// 停止poolconnectionFactory
					MqServiceManager.stopPooledConnectionFactory(appId);

					// 注册 service
					System.out.println("注册  getMqListenService");
					mqListenServices[i] = new FwMqListenService(appId, maps);

					// queue ideploy.agent_<ip> 运行管理的queue
					if (!appId.equals(Constant.CRS_APP_ID)) {// crs
						// Mq跟ideploy监控Mq分离,crs
						// Mq 不监听agent队列
						String queueName = queueNamePrex
								+ IPUtil.getLocalIP(true);
						logger.debug(" begin to  listenSyncQueue: " + queueName);
						mqListenServices[i].listenSyncQueue(queueName,
								new AgentMessageHandler(context));
					}
				} catch (Exception ex) {
					logger.error(ex.getMessage(), ex);
				}
			}
		}
	}

	/**
	 * 停止 consumer
	 */
	private void stopConsumer() {
		if (mqListenServices != null && mqListenServices.length > 0) {
			for (MqListenService mqListenService : mqListenServices) {
				if (mqListenService != null) {
					try {
						logger.info(" stop consumer");
						mqListenService.releaseResource();
						mqListenService = null;
					} catch (Exception ex) {
						logger.error("停止 consumer 异常: " + ex.getMessage(), ex);
					}
				}
			}
		}
		System.out.println("消费者停止成功");
		mqListenServices = null;
	}

	/**
	 * 设置参数
	 *
	 * @param properties
	 * @return
	 */
	private Map<String, Object> setParams(java.util.Dictionary properties) {
		Map<String, Object> maps = new HashMap<String, Object>();
		if (properties != null) {
			Enumeration<String> keys = properties.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				maps.put(key, properties.get(key));
			}
		}
		return maps;
	}

	/**
	 * 暂停服务
	 */
	public void stop() {
		stopConsumer();
		//stopScheduler();
	}
}

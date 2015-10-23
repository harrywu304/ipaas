package com.github.ipaas.ifw.jdbc.impl;

import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.locate.HashAlgorithms;
import com.github.ipaas.ifw.core.locate.NodeLocator;
import com.github.ipaas.ifw.core.locate.NodeLocators;
import com.github.ipaas.ifw.core.locate.NodeLocators.ConsistentHashLocator;
import com.github.ipaas.ifw.core.mbean.Fw;
import com.github.ipaas.ifw.core.support.ThreadPools;
import com.github.ipaas.ifw.jdbc.DbConnectProfile;
import com.github.ipaas.ifw.jdbc.DbConnectService;
import com.github.ipaas.ifw.jdbc.ProxoolConfig;
import com.github.ipaas.ifw.jdbc.mbean.DbHealthCheck;
import com.github.ipaas.ifw.util.JMXUtil;

/**
 * 数据库直接连接服务
 * 
 * @author Chenql
 * @authore whx
 *          修改了连接池的failover机制，增加了些节点的failover，和修改了failback的实现代码
 *          ，减少了对synchronized方法的访问 2011.04.15 Modified by  2012.08.21
 *          修改了连接池的failover机制，在rebuild连接池前检测是否连接不上(检测办法是:重新检测多次).
 */
public final class FwDirectDbConnectService implements DbConnectService {

	private static Logger logger = LoggerFactory.getLogger(FwDirectDbConnectService.class);

	// //**********注入属性，一般用于spring配置的注入
	private String proxoolConfig;
	private String dbServerMapping;
	private String itemLocateAlgorithm;
	/**
	 * 服务Id
	 */
	private String serviceId;

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	@Override
	public String getProxoolConfig() {
		return proxoolConfig;
	}

	@Override
	public void setProxoolConfig(String proxoolConfig) {
		this.proxoolConfig = proxoolConfig;
	}

	@Override
	public String getDbServerMapping() {
		return dbServerMapping;
	}

	@Override
	public void setDbServerMapping(String dbServerMapping) {
		this.dbServerMapping = dbServerMapping;
	}

	@Override
	public String getItemLocateAlgorithm() {
		return itemLocateAlgorithm;
	}

	@Override
	public void setItemLocateAlgorithm(String itemLocateAlgorithm) {
		this.itemLocateAlgorithm = itemLocateAlgorithm;
	}

	// //**********

	public FwDirectDbConnectService() {
	}

	/**
	 * 进行故障结点健康检查的线程总数
	 */
	private static AtomicInteger aliveCheckThreadCount = new AtomicInteger(0);

	/**
	 * 定时健康检查间隔
	 */
	private static int ALICE_CHECK_INTERVAL = 10000; // 数据库连接池健康检查调度间隔时间, 单位:毫秒

	/**
	 * 检测连接池是否连接不上数据库的间隔
	 */
	private final static int CHECK_DB_REPEAT = 500; // 单位:毫秒
	/**
	 * 检测连接池是否连接不上数据库的检测次数
	 */
	private final static int CHECK_DB_REPEAT_COUNT = 10;

	/**
	 * 数据库连接池健康检查调度服务
	 */
	private static ScheduledExecutorService aliveCheckService = ThreadPools.newScheduledExecutorService(1,
			"数据库连接池健康检查线程(DBACThread)");

	/**
	 * 是否已经初始化数据库
	 */
	private static volatile boolean initDbFlag = false;

	private static volatile boolean dbconfigLoadSuc = false;

	/**
	 * 保存当前失败(发生故障的从库别名集合)
	 */
	private Set<String> failSlaves = new HashSet<String>();

	/**
	 * 节点定位器
	 */
	private NodeLocator<DbMasterSlaveGroup> groupLocator = null;

	public NodeLocator<DbMasterSlaveGroup> getGroupLocator() {
		return groupLocator;
	}

	public void initializePlugin() {

		// 同步所有插件初始化过程(初始化串行化)
		synchronized (FwDirectDbConnectService.class) {

			logger.info("====初始化数据库连接服务(FwDirectDbConnectService)插件开始===");

			// 初始化数据库连接池
			if (!initDbFlag) { // 保证只执行一次
				initDbFlag = true;
				DbConnectionProvider.init();

				logger.info("dbpool配置文件初始化全局配置");
				String proxoolConfig = this.getProxoolConfig();// (String)
																// this.params.get("proxool_config");
				if (null != proxoolConfig && !"".equals(proxoolConfig)) {
					DbConnectionProvider.registerPoolFromXml(proxoolConfig);
					logger.info("加载proxool的" + proxoolConfig + "配置成功！");
				} else {
					try {
						logger.info("尝试加载在类路径加载/proxool.xml...");
						String pf = "/proxool.xml";
						URL u = this.getClass().getResource(pf);
						if (null != u) {
							DbConnectionProvider.registerPoolFromXml(pf);
						} else {
							throw new Exception("没有找到数据库配置文件");
						}
						logger.info("加载成功！");
					} catch (Exception ex) {
						throw new FwRuntimeException("加载数据库配置文件失败，请确定是否配置了数据库连接池Proxool的配置文件！", ex);

					}
				}
				logger.info("dbpool配置文件初始成功！");
				dbconfigLoadSuc = true;
				ProxoolConfig.init(proxoolConfig);
			} else {
				logger.info("dbpool配置文件已经加载，加载状态：" + dbconfigLoadSuc);
			}

			// 初始化插件上下文

			String locateAlgorithm = this.getItemLocateAlgorithm();// (String)
																	// params.get("item_locate_algorithm");

			locateAlgorithm = (null != locateAlgorithm && !"".equals(locateAlgorithm)) ? locateAlgorithm
					: "consistent-hash"; // 默认值

			String mapping = this.getDbServerMapping();//
			if (null == mapping || "".equals(mapping)) {

				throw new FwRuntimeException("必须配置dbServerMapping属性！");
			}
			// 构造分组并且分主从关系的数据库信息, 演示mapping字符串: m0:s0;s1;s2,m1:s0;s1,m2:s0;s1;s2
			if ("mod".equals(locateAlgorithm)) {
				// 初始化数据库分组 节点: 取模方式
				String[] items = mapping.trim().split(",");
				List<DbMasterSlaveGroup> groups = new ArrayList<DbMasterSlaveGroup>(items.length);
				for (String item : items) {
					DbMasterSlaveGroup group = new DbMasterSlaveGroup(item);
					groups.add(group);
				}
				groupLocator = NodeLocators.newModLocator(HashAlgorithms.DJB_HASH, groups);
			} else if ("consistent-hash".equals(locateAlgorithm)) {
				// 初始化数据库分组 节点: 一致性hash方式
				String[] items = mapping.trim().split(",");
				ConsistentHashLocator<DbMasterSlaveGroup> locator = new NodeLocators.ConsistentHashLocator<DbMasterSlaveGroup>();

				// 设置统一哈希算法
				locator.setHashAlgorithm(HashAlgorithms.KEMATA_HASH);

				long[] candidates = locator.nextCandidates(items.length);
				Map<Long, DbMasterSlaveGroup> nodes = new HashMap<Long, DbMasterSlaveGroup>();
				for (int i = 0; i < items.length; i++) {
					DbMasterSlaveGroup group = new DbMasterSlaveGroup(items[i]);
					Long groupNo = Long.valueOf(candidates[i]);
					nodes.put(groupNo, group);
				}
				locator.setNodes(nodes);
				groupLocator = locator;
			} else {

				throw new FwRuntimeException("未知hash算法！");
			}
			if (groupLocator.getNodes().size() <= 0) {

				throw new FwRuntimeException("FwDirectDbConnectService插件数据库分组数量为0");
			}

			initDbHealthMBean();
			logger.info("====初始化数据库连接服务(FwDirectDbConnectService)插件结束===");
		}
	}

	/*
	 * 获取数据库连接资料(profile)
	 * 
	 */
	public DbConnectProfile getConnectProfile(String rwMode, long identityHashCode) {

		DbMasterSlaveGroup group = (DbMasterSlaveGroup) groupLocator
				.locate(identityHashCode, NodeLocator.NULL_STRATEGY);
		DbConnectProfile profile = null;
		if ("w".equals(rwMode)) {
			profile = getConnectProfile(group.masterAlias);
		} else { // 当前实现不区分: rw 和 r模式, 相同处理
			profile = group.failoverGetSlave(this, this.failSlaves);
		}
		return profile;
	}

	/*
	 * 获取数据库连接资料(profile)列表
	 * 
	 */
	public List<DbConnectProfile> getConnectProfiles(String rwMode) {

		Map<Long, DbMasterSlaveGroup> groups = groupLocator.getNodes();
		List<DbConnectProfile> profiles = new ArrayList<DbConnectProfile>(groups.size());
		if ("w".equals(rwMode)) {
			for (Long key : groups.keySet()) {
				DbMasterSlaveGroup group = groups.get(key);
				DbConnectProfile profile = null;
				try {
					profile = getConnectProfile(group.masterAlias);
				} catch (Exception ex) {

					throw new FwRuntimeException("获取主数据库(master)连接异常," + "主库别名:" + group.masterAlias, ex);
				}
				if (null != profile) {
					profiles.add(profile);
				} else {
					throw new FwRuntimeException("获取主数据库(master)连接异常2," + "主库别名:" + group.masterAlias);
				}
			}
		} else { // 当前实现不区分: rw 和 r模式, 相同处理
			for (Long key : groups.keySet()) {
				DbMasterSlaveGroup group = groups.get(key);
				DbConnectProfile profile = group.failoverGetSlave(this, this.failSlaves);
				if (null != profile) {
					profiles.add(profile);
				} else {
					logger.error("当前数据库组没有可用的读节点, 通过故障转移功能都无法获取连接, 组信息:{}", group);
				}
			}
		}
		return profiles;
	}

	/*
	 * 获取写数据库连接池别名
	 * 
	 */
	public String getWritePoolAlias(long identityHashCode) {
		DbMasterSlaveGroup group = (DbMasterSlaveGroup) groupLocator
				.locate(identityHashCode, NodeLocator.NULL_STRATEGY);
		return group.masterAlias;
	}

	/*
	 * 根据数据库连接池别名获取数据库连接资料(profile)
	 * 
	 */
	public DbConnectProfile getConnectProfile(String alias) {
		// 检查连接池是否已在故障列表中
		if (failSlaves.contains(alias)) {
			return null;
		}
		// 获取DbConnectProfile
		try {
			long start = System.currentTimeMillis();
			Connection conn = DbConnectionProvider.getConnection(alias);
			long end = System.currentTimeMillis();
			DbConnectProfile profile = new FwDbConnectProfile(conn, alias, (end - start));
			return profile;
		} catch (Exception e) {
			aliveCheckDbPool(alias);
			logger.error("getConnectProfile获取数据库连接池[" + alias + "]的连接异常", e);
		}
		return null;
	}

	/**
	 * Modified by dengrq 2012.08.21 在rebuild连接池前检测是否连接不上(检测办法是:重新检测多次).
	 * 对故障节点的健康检查
	 * 
	 * @param alias
	 *            -- 发生故障的数据库连接池的别名
	 */
	private void aliveCheckDbPool(final String alias) {
		if (failSlaves.contains(alias)) {
			// 池已经在故障节点列表中，直接返回
			return;
		}
		// 添加故障连接池到故障列表
		failSlaves.add(alias);

		// 检测是否连接不上,如果连接不上,重建连接池
		checkDbRepeat(alias);
	}

	/**
	 * Create by dengrq 2012.08.21 检测是否连接不上,如果连接不上,重建连接池
	 * 
	 * @param alias
	 */
	private void checkDbRepeat(final String alias) {
		try {
			// 健康检查任务
			Runnable task = new Runnable() {
				public void run() {
					for (int i = 0; i < CHECK_DB_REPEAT_COUNT; i++) {
						if (DbConnectionProvider.checkAlive(alias)) {
							// 检查数据库已经正常了
							failSlaves.remove(alias);
							logger.error("故障节点{}检测结果：恢复正常，", alias);
							return;
						}
						try {
							Thread.sleep(CHECK_DB_REPEAT);
						} catch (InterruptedException e) {
							logger.error("故障节点{}的过程中出现中断，", alias);
						}
					}
					// 重建连接池
					rebuildPool(alias);
				}
			};
			task.run();
		} catch (Exception e) {
			logger.error("checkDbRepeat操作异常", e);
		}
	}

	/**
	 * Create by dengrq 2012.08.21 重建连接池
	 * 
	 * @param alias
	 */
	private void rebuildPool(final String alias) {
		try {
			// 健康检查任务
			Runnable task = new Runnable() {
				public void run() {
					// 从db pool provider中清除故障池
					DbConnectionProvider.removePool(alias);
					// 从db pool provider中重建故障池
					DbConnectionProvider.rebuildPool(alias);

					if (DbConnectionProvider.checkAlive(alias)) {
						// 检查数据库已经正常了
						failSlaves.remove(alias);
						logger.error("故障节点{}检测结果：恢复正常，仍在进行健康检查的线程总数为{}", alias, aliveCheckThreadCount.decrementAndGet());
					} else {
						aliveCheckService.schedule(this, ALICE_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
						logger.error("故障节点{}检测结果：未修复，等待下一次检查", alias);
					}
				}
			};
			aliveCheckService.schedule(task, ALICE_CHECK_INTERVAL, TimeUnit.MILLISECONDS);
			logger.error("数据库节点{}故障，添加到健康检查列表，正在进行健康检查的线程总数为{}", alias, aliveCheckThreadCount.incrementAndGet());
		} catch (Exception e) {
			logger.error("rebuildPool操作异常", e);
		}
	}

	/**
	 * 初始化数据库可用性检查MBean
	 * 
	 */
	private void initDbHealthMBean() {
		DbHealthCheck dbHealthCheck;
		DbHealthCheck dbHealthCheckTmp;

		try {
			// 拆分主从配置，根据alias，获取连接url地址
			// 从一个appid配置中拆分出主从组
			String[] masterSlaverGroup = dbServerMapping.split(",");
			// 对主从组循环，
			for (int i = 0; i < masterSlaverGroup.length; i++) {
				// 拆分出主节点，和从节点串
				String[] masterSlaver = masterSlaverGroup[i].split(":");
				// 针对同一个appid的同一个主从组，循环，
				List<DbHealthCheck> dbHealthCheckList = new ArrayList<DbHealthCheck>();
				String masterAlias = "";
				for (int j = 0; j < masterSlaver.length; j++) {
					// 拆分出每个节点，包括主节点和从节点.masterSlaver[0]是master
					String[] alias = masterSlaver[j].split(";");
					// 保存masterAlias

					if (0 == j) {
						masterAlias = alias[0];
					}

					for (int k = 0; k < alias.length; k++) {
						// 下面是实例化mbean的入口点
						// 先获取IP
						String jdbcUrl = ProxoolConfig.getJdbcUrl(alias[k]);
						String ip = ProxoolConfig.getNodeIp(jdbcUrl);
						String port = ProxoolConfig.getNodePort(jdbcUrl);
						// 构建一个临时list，在接下来的步骤中用来剔重
						dbHealthCheckTmp = new DbHealthCheck(this, serviceId, alias[k], masterAlias, ip, port);
						dbHealthCheckList.add(dbHealthCheckTmp);
						dbHealthCheckTmp = null;
					}
				}
				// 剔除重复的代码,先把第一个注册，并添加到dbHealthCheckList中，然后对其余的遍历剔除重复；
				DbHealthCheck compareObj = dbHealthCheckList.get(0);
				dbHealthCheck = new DbHealthCheck(this, serviceId, compareObj.getAlias(), compareObj.getMasterAlias(),
						compareObj.getTargetIp(), compareObj.getTargetPort());
				String oname = JMXUtil.createObjectNameString(Fw.FW_USABILITY_DOMAIN, DbHealthCheck.TYPE, serviceId,
						compareObj.getTargetIp() + "-" + compareObj.getTargetPort());
				JMXUtil.registerMBean(dbHealthCheck, oname);
				logger.info(" DbHealthCheck 注册:" + oname);
				dbHealthCheck = null;

				for (int m = 1; m < dbHealthCheckList.size(); m++) {
					DbHealthCheck tmp = dbHealthCheckList.get(m);
					if (compareObj.equals(tmp)) {
						continue;
					}
					dbHealthCheck = new DbHealthCheck(this, serviceId, tmp.getAlias(), tmp.getMasterAlias(),
							tmp.getTargetIp(), tmp.getTargetPort());
					oname = JMXUtil.createObjectNameString(Fw.FW_USABILITY_DOMAIN, DbHealthCheck.TYPE, null,
							tmp.getTargetIp() + "-" + tmp.getTargetPort());
					JMXUtil.registerMBean(dbHealthCheck, oname);
					logger.info(" DbHealthCheck 注册:" + oname);
					dbHealthCheck = null;
				}

			}

		} catch (Throwable e) {
			logger.error("initDbHealthMBean error", e);
		}

	}

}

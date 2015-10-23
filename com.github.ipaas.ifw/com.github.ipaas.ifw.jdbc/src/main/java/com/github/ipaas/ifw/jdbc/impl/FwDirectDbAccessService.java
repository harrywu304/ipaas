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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.support.ThreadPools;
import com.github.ipaas.ifw.jdbc.DbAccessRequest;
import com.github.ipaas.ifw.jdbc.DbAccessResponse;
import com.github.ipaas.ifw.jdbc.DbAccessService;
import com.github.ipaas.ifw.jdbc.DbConnectProfile;
import com.github.ipaas.ifw.jdbc.DbConnectService;
import com.github.ipaas.ifw.util.CloseUtil;
import com.github.ipaas.ifw.util.SeqUUIDUtil;

/**
 * 直接连接数据库的数据库访问服务
 * 
 * @author Chenql
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class FwDirectDbAccessService implements DbAccessService {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(FwDirectDbAccessService.class);

	/**
	 * 构造一个线程池
	 */
	// core size: 30, max size:1024, keep alive:900s
	private static ExecutorService threadPool = ThreadPools.newExecutorService(30, 1024, 900, "异步执行SQL线程(AESThread)");

	/**
	 * 构造一个 迁库检查调度服务
	 */
	private static ScheduledExecutorService scaleoutCheckService = ThreadPools.newScheduledExecutorService(1,
			"数据库 迁库检查线程(DBScaloutCThread)");

	/**
	 * 默认最大sql执行时间, 单位:毫秒
	 */
	private static int MAX_SQL_EXECUTE_TIME = 60000;

	/**
	 * 数据库连接服务
	 */
	private DbConnectService dbConnectService = null;

	/**
	 * 是否正在处于分库期间 标记
	 */
	private boolean scaleoutFlag = false;

	/**
	 * 分库期间的 源库与failover库 连接池别名的映射关系
	 */
	private String scaleoutMapping;

	private Map<String, String> scaleoutMappingMap = new HashMap<String, String>();

	@Override
	public String getScaleoutMapping() {
		return scaleoutMapping;
	}

	@Override
	public void setScaleoutMapping(String scaleoutMapping) {
		this.scaleoutMapping = scaleoutMapping;
	}

	@Override
	public DbConnectService getDbConnectService() {
		return dbConnectService;
	}

	@Override
	public void setDbConnectService(DbConnectService dbConnectService) {
		this.dbConnectService = dbConnectService;
	}

	public FwDirectDbAccessService() {

	}

	public void initializePlugin() {

		// 同步所有插件初始化过程(初始化串行化)
		synchronized (FwDirectDbAccessService.class) {

			logger.info("====初始化数据库访问服务FwDirectDbAccessService开始===");

			if (this.getDbConnectService() == null) {
				throw new FwRuntimeException("FwDirectDbAccessService初始化失败，没有设置dbConnectService属性！");
			}

			String mapping = this.getScaleoutMapping();// scaleoutConfig.get(this.pluginId);
			scaleoutFlag = (null != mapping && !"".equals(mapping));
			if (scaleoutFlag) {
				// 构建映射关系
				String[] s = mapping.split(",");
				for (String i : s) {
					String[] m = i.trim().split(":");
					scaleoutMappingMap.put(m[0], m[1]);
				}

			}

			logger.info("====初始化数据库访问服务FwDirectDbAccessService结束===");
		}
	}

	/*
	 * 通过向后台线程池提交任务, 在所有数据库上执行更新sql
	 * 
	 */
	public void asyncExecuteUpdate(final String sql) {

		Runnable task = new Runnable() {
			public void run() {
				executeUpdate(sql);
			}
		};
		threadPool.submit(task);
	}

	/*
	 * 通过向后台线程池提交任务, 在通过hash值定位的数据库上执行更新sql
	 * 
	 */
	public void asyncExecuteUpdate(final String sql, final long identityHashCode) {

		Runnable task = new Runnable() {
			public void run() {
				executeUpdate(sql, identityHashCode);
			}
		};
		threadPool.submit(task);
	}

	public void asyncExecuteUpdateBatch(final Map<String, DbAccessRequest> requests, final boolean transaction) {

		Runnable task = new Runnable() {
			public void run() {
				executeUpdateBatch(requests, transaction);
			}
		};
		threadPool.submit(task);
	}

	/*
	 * 创建带ID的数据库访问请求
	 * 
	 */
	public DbAccessRequest createRequest() {

		FwDbAccessRequest req = new FwDbAccessRequest();
		req.setId(SeqUUIDUtil.toSequenceUUID());
		return req;
	}

	/*
	 * 在多个数据库节点上执行SQL, 合并查询的结果
	 * 
	 */
	public DbAccessResponse executeQuery(final String sql) {

		long start = System.currentTimeMillis();
		final String reqId = SeqUUIDUtil.toSequenceUUID();
		final FwDbAccessRequest req = new FwDbAccessRequest();
		req.setId(reqId);
		req.setSql(sql);
		req.setRequestTimeout(MAX_SQL_EXECUTE_TIME);
		logger.info("数据库访问请求DbAccessRequest:{}", req);

		final FwDbAccessResponse res = new FwDbAccessResponse();
		res.setReqId(reqId);
		List<DbConnectProfile> profiles = this.getProfilesAndFillResponse("r", res);
		if (res.isSuccess()) { // 申请数据库连接成功
			final List<FwDbAccessResponse> ress = new LinkedList<FwDbAccessResponse>();
			final CountDownLatch latch = new CountDownLatch(profiles.size()); // 等待锁
			long maxContext = 0;
			for (final DbConnectProfile p : profiles) {
				maxContext = (p.initializedContextTime() > maxContext) ? p.initializedContextTime() : maxContext;
				Runnable task = new Runnable() { // 在指定数据库上执行的任务
					public void run() {

						FwDbAccessResponse itemRes = new FwDbAccessResponse();
						itemRes.setReqId(reqId);
						Connection conn = p.getConnection();

						try {
							executeQuery(itemRes, req, conn); // 执行查询请求
							if (!itemRes.isSuccess()) {
								res.setSuccess(false);
								res.setRaex(itemRes.getException());
							}
						} finally {
							ress.add(itemRes); // 增加到响应对象集合
							CloseUtil.closeSilently(conn);
							latch.countDown();
						}
					}
				};
				threadPool.submit(task);
			}
			long timeout = MAX_SQL_EXECUTE_TIME * 2; // 等待超时为两倍sql执行超时时间
			try {
				latch.await(timeout, TimeUnit.MILLISECONDS);
				// 合并结果集
				Map<Integer, String> resultMeta = null;
				List<Map<String, Object>> resultData = new LinkedList<Map<String, Object>>();
				int totalRows = 0;
				long maxExecuted = 0;
				for (FwDbAccessResponse item : ress) {
					maxExecuted = (item.executedTime() > maxExecuted) ? item.executedTime() : maxExecuted;
					List<Map<String, Object>> itemResultData = item.getResultData();
					if (null == resultMeta) {
						resultMeta = item.getResultMeta();
					}
					if (null != itemResultData) {
						totalRows += itemResultData.size();
						resultData.addAll(itemResultData);
					}
				}
				res.setContext(maxContext);
				res.setExecuted(maxExecuted);
				res.setRows(totalRows);
				res.setResultMeta(resultMeta);
				res.setResultData(resultData);
			} catch (Exception ex) {

				String msg = "SQL超时时限:" + timeout / 1000 + "秒";
				FwRuntimeException re = new FwRuntimeException(msg);
				res.setSuccess(false);
				res.setRaex(re);
				logger.error(msg, ex);

			}
		}
		res.setElapsed(System.currentTimeMillis() - start);
		logger.info("数据库访问响应DbAccessResponse:{}", res);
		// 执行不成功，抛出RuntimeException
		if (!res.isSuccess()) {
			throw res.getException();
		}
		return res;
	}

	/*
	 * 在经过identityHashCode哈希算法后指定数据库节点上执行SQL
	 * 
	 */
	public DbAccessResponse executeQuery(String sql, long identityHashCode) {

		FwDbAccessRequest req = new FwDbAccessRequest();
		String reqId = SeqUUIDUtil.toSequenceUUID();
		req.setId(reqId);
		req.setIdentityHashCode(identityHashCode);
		req.setSql(sql);
		req.setRequestTimeout(MAX_SQL_EXECUTE_TIME);
		logger.info("数据库访问请求DbAccessRequest:{}", req);
		return executeQuery(req);
	}

	public DbAccessResponse executeQuery(DbConnectProfile profile, String sql) {

		long start = System.currentTimeMillis();

		final String reqId = SeqUUIDUtil.toSequenceUUID();
		final FwDbAccessRequest request = new FwDbAccessRequest();
		request.setId(reqId);
		request.setSql(sql);
		request.setRequestTimeout(MAX_SQL_EXECUTE_TIME);

		FwDbAccessResponse res = new FwDbAccessResponse();

		res.setContext(profile.initializedContextTime());
		Connection conn = profile.getConnection();
		executeQuery(res, request, conn);
		CloseUtil.closeSilently(conn);
		if (res.isSuccess()) { // 执行查询sql成功,判断迁库逻辑
			if (res.affectedRows() == 0 && scaleoutFlag) { // 分库迁移处理
				String failoverAlias = scaleoutMappingMap.get(profile.getDbPoolAlias());
				if (null != failoverAlias) {
					Connection failoverConn = null;
					try {
						DbConnectProfile failover = dbConnectService.getConnectProfile(failoverAlias);
						res.setContext(failover.initializedContextTime());
						failoverConn = failover.getConnection();
						executeQuery(res, request, failoverConn);
					} catch (Exception ex) {
						/* 消除异常 */
					} finally {
						CloseUtil.closeSilently(failoverConn);
					}
				}
			}
		}

		res.setElapsed(System.currentTimeMillis() - start);
		logger.info("数据库访问响应DbAccessResponse:{}", res);
		// 执行不成功，抛出RuntimeException
		if (!res.isSuccess()) {
			throw res.getException();
		}
		return res;
	}

	/*
	 * 在经过identityHashCode哈希算法后指定数据库节点上执行SQL
	 * 
	 * @see
	 */
	public DbAccessResponse executeQuery(DbAccessRequest request) {

		long start = System.currentTimeMillis();
		FwDbAccessResponse res = new FwDbAccessResponse();
		res.setReqId(request.getId());
		DbConnectProfile profile = getProfileAndFillResponse("r", request.getIdentityHashCode(), res);
		if (res.isSuccess()) {
			res.setContext(profile.initializedContextTime());
			Connection conn = profile.getConnection();
			executeQuery(res, request, conn);
			CloseUtil.closeSilently(conn);
			if (res.isSuccess()) { // 执行查询sql成功,判断迁库逻辑
				if (res.affectedRows() == 0 && scaleoutFlag) { // 分库迁移处理
					String failoverAlias = scaleoutMappingMap.get(profile.getDbPoolAlias());
					if (null != failoverAlias) {
						Connection failoverConn = null;
						try {
							DbConnectProfile failover = dbConnectService.getConnectProfile(failoverAlias);
							res.setContext(failover.initializedContextTime());
							failoverConn = failover.getConnection();
							executeQuery(res, request, failoverConn);
						} catch (Exception ex) {
							/* 消除异常 */
						} finally {
							CloseUtil.closeSilently(failoverConn);
						}
					}
				}
			}
		}
		res.setElapsed(System.currentTimeMillis() - start);
		logger.info("数据库访问响应DbAccessResponse:{}", res);
		// 执行不成功，抛出RuntimeException
		if (!res.isSuccess()) {
			throw res.getException();
		}
		return res;
	}

	/**
	 * 批量执行查询SQL请求
	 * 
	 * @param requests
	 *            -- 请求集合
	 * @return -- 响应集合
	 */
	public Map<String, DbAccessResponse> executeQueryBatch(Map<String, DbAccessRequest> requests) {

		logger.info("数据库访问请求DbAccessRequest:{}", requests);
		final long start = System.currentTimeMillis();
		final Map<String, DbAccessResponse> responses = new LinkedHashMap<String, DbAccessResponse>();
		final CountDownLatch latch = new CountDownLatch(requests.size()); // 等待锁
		long maxTimeout = 0; // 最大超时时间
		for (final String key : requests.keySet()) {
			final DbAccessRequest req = requests.get(key);
			maxTimeout = (req.getRequestTimeout() > maxTimeout) ? req.getRequestTimeout() : maxTimeout; // 取最大的超时时间
			Runnable task = new Runnable() { // 执行查询sql任务
				public void run() {
					FwDbAccessResponse res = new FwDbAccessResponse();
					res.setReqId(req.getId());
					try {
						DbConnectProfile profile = getProfileAndFillResponse("r", req.getIdentityHashCode(), res);
						if (res.isSuccess()) {
							res.setContext(profile.initializedContextTime());
							Connection conn = profile.getConnection();
							executeQuery(res, req, conn);
							CloseUtil.closeSilently(conn);
							if (res.isSuccess()) {
								if (res.affectedRows() == 0 && scaleoutFlag) { // 分库迁移处理
									String failoverAlias = scaleoutMappingMap.get(profile.getDbPoolAlias());
									if (null != failoverAlias) {
										Connection failoverConn = null;
										try {
											DbConnectProfile failover = dbConnectService
													.getConnectProfile(failoverAlias);
											res.setContext(failover.initializedContextTime());
											failoverConn = failover.getConnection();
											executeQuery(res, req, failoverConn);
										} catch (Exception ex) {
											/* 消除异常 */
										} finally {
											CloseUtil.closeSilently(failoverConn);
										}
									}
								}
							}
						}
						res.setElapsed(System.currentTimeMillis() - start);
						responses.put(key, res);
					} finally {
						latch.countDown();
					}
				}
			};
			threadPool.submit(task);
		}
		// 等待超时为最大请求设置超时时间加上默认最大sql执行超时时间
		long timeout = maxTimeout + MAX_SQL_EXECUTE_TIME;
		try {
			latch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {

			String msg = "SQL执行超时异常,SQL超时时限:" + timeout + "";
			logger.error(msg, ex);
			FwRuntimeException raex = new FwRuntimeException(msg, ex);

			for (String key : requests.keySet()) {
				DbAccessResponse itemRes = responses.get(key);
				if (null == itemRes) { // 如果请求没有被成功执行,就没有响应对象, 给响应集合添加出错的响应对象
					DbAccessRequest itemReq = requests.get(key);
					FwDbAccessResponse res = new FwDbAccessResponse();
					res.setReqId(itemReq.getId());
					res.setSuccess(false);
					res.setRaex(raex);
					res.setElapsed(System.currentTimeMillis() - start);
					responses.put(key, res);
				}
			}
		}
		logger.info("数据库访问响应DbAccessResponse:{}", responses);
		// 执行不成功，抛出RuntimeException
		for (Map.Entry<String, DbAccessResponse> entry : responses.entrySet()) {
			if (!entry.getValue().isSuccess()) {
				throw entry.getValue().getException();
			}
			break;
		}
		return responses;
	}

	/*
	 * 多写操作，即在所有写数据库上执行更新SQL。
	 */
	public DbAccessResponse executeUpdate(String sql) {

		long start = System.currentTimeMillis();
		// 构造请求对象
		final FwDbAccessRequest req = new FwDbAccessRequest();
		String reqId = SeqUUIDUtil.toSequenceUUID();
		req.setId(reqId);
		req.setSql(sql);
		req.setRequestTimeout(MAX_SQL_EXECUTE_TIME);
		logger.info("数据库访问请求DbAccessRequest:{}", req);

		final FwDbAccessResponse res = new FwDbAccessResponse();
		res.setReqId(reqId);

		List<DbConnectProfile> profiles = this.getProfilesAndFillResponse("w", res);
		if (res.isSuccess()) { // 申请数据库连接成功
			long maxContextTime = 0; // 最大申请数据库时间
			final AtomicInteger totalRows = new AtomicInteger(0); // 总共影响行数
			final List<Object> totalKeys = new ArrayList();
			final CountDownLatch latch = new CountDownLatch(profiles.size()); // 等待锁

			for (DbConnectProfile p : profiles) {
				maxContextTime = (p.initializedContextTime() > maxContextTime) ? p.initializedContextTime()
						: maxContextTime; // 取最大的上下文时间
				final Connection conn = p.getConnection();
				Runnable task = new Runnable() { // 在指定数据库上执行的任务
					public void run() {
						try {
							long et = res.executedTime();
							executeUpdate(res, req, conn); // 执行请求
							totalRows.addAndGet(res.getRows());
							totalKeys.addAll(res.getGeneratedKeys());
							if (et > res.executedTime()) { // 设置最大的执行时间为响应的执行时间
								res.setExecuted(et);
							}

						} finally {
							CloseUtil.closeSilently(conn);
							latch.countDown();
						}
					}
				};
				threadPool.submit(task);
			}
			long timeout = MAX_SQL_EXECUTE_TIME * 2; // 等待超时为两倍sql执行超时时间
			try {
				latch.await(timeout, TimeUnit.MILLISECONDS);
			} catch (Exception ex) {
				res.setSuccess(false);

				String msg = "SQL执行超时异常,SQL超时时限:" + timeout;
				FwRuntimeException raex = new FwRuntimeException(msg, ex);
				res.setRaex(raex);
				logger.error(msg, ex);
			}
			res.setRows(totalRows.get()); // 设置影响行数为在各个数据库上执行的影响行数的和
			res.setGeneratedKeys(totalKeys); // 设置数据在各个数据库上产生的keys的集合
			res.setContext(maxContextTime); // 设置最大的申请数据库连接时间
		}
		res.setElapsed(System.currentTimeMillis() - start);
		logger.info("数据库访问响应DbAccessResponse:{}", res);
		// 执行不成功，抛出RuntimeException
		if (!res.isSuccess()) {
			throw res.getException();
		}
		return res;
	}

	/*
	 * 单写操作，在单个写数据库上执行更新SQL
	 * 
	 */
	public DbAccessResponse executeUpdate(String sql, long identityHashCode) {

		// 构造数据库访问请求
		FwDbAccessRequest req = new FwDbAccessRequest();
		String reqId = SeqUUIDUtil.toSequenceUUID();
		req.setId(reqId);
		req.setIdentityHashCode(identityHashCode);
		req.setSql(sql);
		req.setRequestTimeout(MAX_SQL_EXECUTE_TIME);
		logger.info("数据库访问请求DbAccessRequest:{}", req);

		return executeUpdate(req);
	}

	/*
	 * 单写操作，即在单个写数据库上执行更新SQL
	 * 
	 * @see
	 */
	public DbAccessResponse executeUpdate(DbAccessRequest request) {

		long start = System.currentTimeMillis();
		FwDbAccessResponse res = new FwDbAccessResponse();
		res.setReqId(request.getId());
		DbConnectProfile profile = this.getProfileAndFillResponse("w", request.getIdentityHashCode(), res);
		if (res.isSuccess()) {
			res.setContext(profile.initializedContextTime());
			Connection conn = profile.getConnection();
			executeUpdate(res, request, conn); // 执行请求
			if (res.isSuccess()) {
				if (res.affectedRows() == 0 && scaleoutFlag) { // 分库迁移处理
					String failoverAlias = scaleoutMappingMap.get(profile.getDbPoolAlias());
					if (null != failoverAlias) {
						Connection failoverConn = null;
						try {
							DbConnectProfile failover = dbConnectService.getConnectProfile(failoverAlias);
							res.setContext(failover.initializedContextTime());
							failoverConn = failover.getConnection();
							executeUpdate(res, request, failover.getConnection());
						} catch (Exception ex) {
							/* 消除异常 */
						} finally {
							CloseUtil.closeSilently(failoverConn);
						}
					}
				}
			}
			CloseUtil.closeSilently(conn);
		}
		res.setElapsed(System.currentTimeMillis() - start);
		logger.info("数据库访问响应DbAccessResponse:{}", res);
		// 执行不成功，抛出RuntimeException
		if (!res.isSuccess()) {
			throw res.getException();
		}
		return res;
	}

	/*
	 * 批量执行更新SQL 备注: 每个SQL在哪个数据库上执行由DbAccessRequest对象的identityHashCode值决定.
	 * 如果需要事务支持(transaction = true), 首先将请求按照要执行的数据库分类, 然后执行.
	 * 保证在同一个数据库上的sql同一个事务控制.
	 * 
	 */
	public Map<String, DbAccessResponse> executeUpdateBatch(Map<String, DbAccessRequest> requests, boolean transaction) {
		Map<String, DbAccessResponse> responses = (transaction ? updateBatchWithTrasaction(requests)
				: updateBatchNoTransaction(requests));
		// 执行不成功，抛出RuntimeException
		for (Map.Entry<String, DbAccessResponse> entry : responses.entrySet()) {
			if (!entry.getValue().isSuccess()) {
				throw entry.getValue().getException();
			}
			break;
		}
		return responses;
	}

	/**
	 * 批量执行更新SQL, 没有事务支持
	 * 
	 * @param requests
	 *            -- 数据库访问请求
	 * @return -- 数据库访问响应
	 */
	private Map<String, DbAccessResponse> updateBatchNoTransaction(Map<String, DbAccessRequest> requests) {

		logger.info("数据库访问请求DbAccessRequest:{}", requests);
		final long start = System.currentTimeMillis();
		long maxTimeout = 0;
		final Map<String, DbAccessResponse> responses = new LinkedHashMap<String, DbAccessResponse>();
		final CountDownLatch latch = new CountDownLatch(requests.size());
		for (final String key : requests.keySet()) {
			final DbAccessRequest req = requests.get(key);
			maxTimeout = (req.getRequestTimeout() > maxTimeout) ? req.getRequestTimeout() : maxTimeout; // 计算请求中最大超时时间
			// 并行执行sql任务
			Runnable task = new Runnable() {
				public void run() {

					Connection conn = null;
					try {
						FwDbAccessResponse res = new FwDbAccessResponse();
						res.setReqId(req.getId());
						DbConnectProfile profile = getProfileAndFillResponse("w", req.getIdentityHashCode(), res);
						if (res.isSuccess()) { // 获取数据库连接成功
							res.setContext(profile.initializedContextTime());
							conn = profile.getConnection();
							executeUpdate(res, req, conn);
						}
						res.setElapsed(System.currentTimeMillis() - start);
						responses.put(key, res);
					} finally {
						CloseUtil.closeSilently(conn);
						latch.countDown();
					}
				}
			};
			threadPool.submit(task);
		}
		// 等待请求中最大的执行超时时间再加个一个默认最大执行超时时间, 用毫秒表示
		long timeout = maxTimeout + MAX_SQL_EXECUTE_TIME;
		try {
			latch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {

			String msg = "SQL执行超时异常,SQL超时时限:" + timeout;
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			logger.error(msg, ex);

			for (String key : requests.keySet()) {
				DbAccessResponse itemRes = responses.get(key);
				if (null == itemRes) { // 如果请求没有被成功执行,就没有响应对象, 给响应集合添加出错的响应对象
					DbAccessRequest itemReq = requests.get(key);
					FwDbAccessResponse res = new FwDbAccessResponse();
					res.setReqId(itemReq.getId());
					res.setSuccess(false);
					res.setRaex(raex);
					res.setElapsed(System.currentTimeMillis() - start);
					responses.put(key, res);
				}
			}
		}
		logger.info("数据库访问响应DbAccessResponse:{}", responses);
		return responses;
	}

	/**
	 * 批量执行更新SQL, 事务支持
	 * 
	 * @param requests
	 *            -- 数据库访问请求
	 * @return -- 数据库访问响应
	 */
	private Map<String, DbAccessResponse> updateBatchWithTrasaction(Map<String, DbAccessRequest> requests) {

		logger.info("数据库访问请求DbAccessRequest:{}", requests);
		final long start = System.currentTimeMillis();
		Map<String, Map<String, DbAccessRequest>> divides = new LinkedHashMap<String, Map<String, DbAccessRequest>>();
		long maxTimeout = 0;
		// 将数据访问请求中定位数据库的hash值将请求分组;
		for (String key : requests.keySet()) {
			DbAccessRequest req = requests.get(key);
			maxTimeout = (req.getRequestTimeout() > maxTimeout) ? req.getRequestTimeout() : maxTimeout;
			String alias = this.dbConnectService.getWritePoolAlias(req.getIdentityHashCode());
			Map<String, DbAccessRequest> sameDbReqs = divides.get(alias);
			if (null == sameDbReqs) {
				sameDbReqs = new LinkedHashMap<String, DbAccessRequest>();
				divides.put(alias, sameDbReqs);
			}
			sameDbReqs.put(key, req);
		}
		final Map<String, DbAccessResponse> responses = new LinkedHashMap<String, DbAccessResponse>();
		final CountDownLatch latch = new CountDownLatch(divides.size());
		// 处理每个写数据库上的更新SQL
		for (final String key : divides.keySet()) {
			final Map<String, DbAccessRequest> sameDbReqs = divides.get(key);
			Runnable task = new Runnable() {
				public void run() {
					try {
						Map<String, FwDbAccessResponse> ress = executeTransaction(key, sameDbReqs);
						for (String item : ress.keySet()) {
							FwDbAccessResponse res = ress.get(item);
							res.setElapsed(System.currentTimeMillis() - start);
							responses.put(item, res);
						}
					} finally {
						latch.countDown();
					}
				}
			};
			threadPool.submit(task);
		}
		long timeout = maxTimeout + MAX_SQL_EXECUTE_TIME;
		try {
			latch.await(timeout, TimeUnit.MILLISECONDS);
		} catch (Exception ex) {

			String msg = "SQL执行超时异常,SQL超时时限:" + timeout;
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			logger.error(msg, ex);

			for (String key : requests.keySet()) {
				DbAccessResponse itemRes = responses.get(key);
				if (null == itemRes) { // 如果请求没有被成功执行,就没有响应对象, 给响应集合添加出错的响应对象
					DbAccessRequest itemReq = requests.get(key);
					FwDbAccessResponse res = new FwDbAccessResponse();
					res.setReqId(itemReq.getId());
					res.setSuccess(false);
					res.setRaex(raex);
					res.setElapsed(System.currentTimeMillis() - start);
					responses.put(key, res);
				}
			}
		}
		logger.info("数据库访问响应DbAccessResponse:{}", responses);
		return responses;
	}

	/**
	 * 在写数据库上执行带事务控制的多条更新SQL
	 * 
	 * @param alias
	 *            -- 数据库连接池别名
	 * @param requests
	 *            -- 数据库请求集合
	 * @return -- 数据库响应
	 */
	private Map<String, FwDbAccessResponse> executeTransaction(String alias, Map<String, DbAccessRequest> requests) {

		long start = System.currentTimeMillis();
		Map<String, FwDbAccessResponse> responses = new LinkedHashMap<String, FwDbAccessResponse>();
		// 申请数据库连接
		FwDbAccessResponse connRes = new FwDbAccessResponse(); // 请求数据库连接的响应对象
		DbConnectProfile profile = this.getProfileAndFillResponse(alias, connRes); // 如果出错,
																					// 填充lastRes;
		connRes.setContext(profile.initializedContextTime());
		Connection conn = profile.getConnection();
		if (connRes.isSuccess()) { // 如果申请数据库连接没有异常
			boolean success = true;
			FwDbAccessResponse lastRes = connRes; // 最新的响应对象
			try {
				conn.setAutoCommit(false);
				for (String key : requests.keySet()) { // 处理每个请求
					long executeStartTime = System.currentTimeMillis();
					DbAccessRequest req = requests.get(key);
					FwDbAccessResponse res = new FwDbAccessResponse();
					res.setReqId(req.getId());
					if (success) { // 如果上一个请求被成功执行了, 就接着执行当前请求
						this.executeUpdate(res, req, conn);
						success &= res.isSuccess();
					} else {
						// 如果上一个请求执行失败, 接下来的所以请求不执行,直接标记为执行不成功,
						// 并且使用上一个请求执行失败作为异常信息
						res.setSuccess(success);
						res.setRaex(lastRes.getException());
					}
					res.setContext(profile.initializedContextTime());
					res.setExecuted(System.currentTimeMillis() - executeStartTime);
					responses.put(key, res);
					lastRes = res; // 记住上一次请求对象
				}
				// 提交或者回滚事务
				if (success) {
					conn.commit();
				} else {
					conn.rollback();
					for (String key : responses.keySet()) {
						FwDbAccessResponse res = responses.get(key);
						res.setSuccess(false);
						res.setRaex(lastRes.getException());
					}
				}
			} catch (Exception ex) {

				String msg = "执行多条更新SQL, 事务控制异常";
				FwRuntimeException raex = new FwRuntimeException(msg, ex);
				logger.error(msg, ex);

				for (String key : requests.keySet()) {
					DbAccessRequest req = requests.get(key);
					FwDbAccessResponse res = new FwDbAccessResponse();
					res.setReqId(req.getId());
					res.setSuccess(false);
					res.setRaex(raex);
					res.setContext(profile.initializedContextTime());
					res.setElapsed(System.currentTimeMillis() - start);
					responses.put(key, res);
				}
			}
		} else {
			// 如果申请数据库连接异常, 所以请求直接标记为执行不成功
			// 并且使用申请数据库连接失败作为异常信息
			for (String key : requests.keySet()) {
				DbAccessRequest req = requests.get(key);
				FwDbAccessResponse res = new FwDbAccessResponse();
				res.setReqId(req.getId());
				res.setSuccess(false);
				res.setRaex(connRes.getException());
				res.setContext(profile.initializedContextTime());
				res.setElapsed(System.currentTimeMillis() - start);
				responses.put(key, res);
			}
		}
		CloseUtil.closeSilently(conn); // 关闭jdbc connection
		return responses;
	}

	/**
	 * 执行更新操作
	 * 
	 * @param res
	 *            -- 数据库访问响应(FwDbAccessResponse)
	 * @param req
	 *            -- 数据库访问请求
	 * @param conn
	 *            -- 数据库连接
	 */
	private void executeUpdate(FwDbAccessResponse res, DbAccessRequest req, Connection conn) {

		Statement sm = null;
		try {
			sm = conn.createStatement();
			executeUpdate(res, req, sm);
		} catch (Exception ex) {

			String msg = "执行数据库SQL异常," + String.format("创建JDBC Statement失败. 使用SQL[%s]", req.getSql());
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			logger.error(msg, ex);

			res.setRaex(raex);

		} finally {
			CloseUtil.closeSilently(sm);
		}
	}

	private void executeUpdate(FwDbAccessResponse res, DbAccessRequest req, Statement sm) {

		int affected = 0; // 影响行数
		List<Object> k = new ArrayList<Object>(); // getGeneratedKeys
		boolean success = true; // 是否成功标记
		try {
			sm.setQueryTimeout((int) (req.getRequestTimeout() / 1000)); // 设置sql执行超时,
																		// 毫秒转成秒

			long start = System.currentTimeMillis(); // 获取执行开始时间
			affected = sm.executeUpdate(req.getSql(), Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = null;
			try {
				/**
				 * 这里需要捕获异常是因为Micorsoft SQLServer <=2005的JDBC驱动 没有按照Sun JDBC
				 * 3.0标准方式实现, 当sql不产生keys时,对外抛出异常, 而不是返回结果行为空的结果集对象
				 */
				rs = sm.getGeneratedKeys();
				while (rs.next()) {
					k.add(rs.getObject(1));
				}
			} catch (Exception iex) { /* 消除异常 */
			}
			long end = System.currentTimeMillis();
			res.setExecuted(end - start); // 设置执行时间
		} catch (Exception ex) {
			success = false;

			String msg = "执行数据库SQL异常," + String.format("执行SQL[%s]", req.getSql());
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			logger.error(msg, ex);

			res.setRaex(raex);

		}
		// 设置执行信息
		res.setSuccess(success); // 设置是否成功
		res.setRows(affected); // 设置影响行数
		res.setGeneratedKeys(k); // 设置数据库产生的keys
	}

	/**
	 * 执行查询操作
	 * 
	 * @param res
	 *            -- 数据库访问响应(FwDbAccessResponse)
	 * @param req
	 *            -- 数据库访问请求
	 * @param conn
	 *            -- 数据库连接
	 */
	private void executeQuery(FwDbAccessResponse res, DbAccessRequest req, Connection conn) {

		Statement sm = null;
		ResultSet rs = null;
		int affected = 0; // 影响行数
		boolean success = true; // 是否成功标记
		try {
			sm = conn.createStatement();
			sm.setQueryTimeout((int) (req.getRequestTimeout() / 1000)); // 设置sql执行超时,
																		// 毫秒转成秒
			long start = System.currentTimeMillis(); // 获取执行开始时间
			rs = sm.executeQuery(req.getSql());
			Map<Integer, String> metaData = new LinkedHashMap<Integer, String>(50, 0.85f); // 保存meta数据对象
			List<Map<String, Object>> resultData = new LinkedList<Map<String, Object>>(); // 保存结果集数据对象
			// 获取结果集的meta数据
			ResultSetMetaData meta = rs.getMetaData();
			int columns = meta.getColumnCount();
			for (int i = 1; i <= columns; i++) {
				Integer columnIndex = Integer.valueOf(i);
				String columnName = meta.getColumnLabel(i).toLowerCase();
				metaData.put(columnIndex, columnName);
			}
			// 处理结果集
			while (rs.next()) {
				affected++;
				Map<String, Object> row = new LinkedHashMap<String, Object>(); // 一个map对象,代表结果集的一行,
																				// key为列名称,
																				// value为当前行在列的值
				for (int i = 1; i <= columns; i++) {
					Object columnValue = rs.getObject(i);
					String columnName = metaData.get(Integer.valueOf(i));
					row.put(columnName, columnValue);
				}
				resultData.add(row);
			}
			res.setResultMeta(metaData);
			res.setResultData(resultData);
			// 计算执行时间
			long end = System.currentTimeMillis();
			res.setExecuted(end - start);
		} catch (Exception ex) {
			success = false;

			String msg = "执行数据库SQL查询异常," + String.format("插件执行SQL[%s]", req.getSql());
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			logger.error(msg, ex);
			res.setRaex(raex);

		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(sm);
		}
		// 设置执行信息
		res.setSuccess(success);
		res.setRows(affected);
	}

	/**
	 * 获取写数据库别名, 如果出错将填充出错信息
	 * 
	 * @param hash
	 *            -- 定位hash值
	 * @param response
	 *            -- 响应对象, 如果出错, 错误信息被填充到res对象
	 * @return -- 写数据库别名
	 */
	@SuppressWarnings("unused")
	private String getWriteAliasAndFillResponse(long hash, FwDbAccessResponse response) {

		String alias = null;
		response.setSuccess(true);
		try {
			alias = this.dbConnectService.getWritePoolAlias(hash);
		} catch (Exception ex) {
			response.setSuccess(false);
			String errorCause = "定位写数据库出错,使用identityHashCode:" + hash;

			String msg = errorCause;
			FwRuntimeException raex = new FwRuntimeException(msg, ex);

			logger.error(msg, ex);

			response.setRaex(raex);
		}
		return alias;
	}

	/**
	 * 获取数据库连接资料(profile), 如果出错将填充出错信息
	 * 
	 * @param alias
	 *            -- 数据库别名
	 * @param response
	 *            -- 响应对象, 如果出错, 错误信息被填充到res对象
	 * @return -- 据库连接资料(DbConnectProfile)对象
	 */
	private DbConnectProfile getProfileAndFillResponse(String alias, FwDbAccessResponse response) {

		DbConnectProfile p = null;
		response.setSuccess(true);
		try {
			p = this.getDbConnectService().getConnectProfile(alias);
		} catch (Exception ex) {
			response.setSuccess(false);
			String errorCause = "定位数据库出错,使用数据库别名(alias):" + alias;

			String msg = errorCause;
			FwRuntimeException raex = new FwRuntimeException(msg, ex);

			logger.error(msg, ex);
			response.setRaex(raex);
		}
		return p;
	}

	/**
	 * 获取数据库连接资料(profile), 如果出错将填充出错信息
	 * 
	 * @param rwMode
	 *            -- 读写模式: w:只写, r: 只读, rw: 可读写
	 * @param identityHashCode
	 *            -- 用于hash定位数据库的hash值, 从定位的数据库获取数据库连接
	 * @param response
	 *            -- 响应对象, 如果出错, 错误信息被填充到res对象
	 * @return -- 据库连接资料(DbConnectProfile)对象
	 */
	private DbConnectProfile getProfileAndFillResponse(String rwMode, long identityHashCode, FwDbAccessResponse response) {

		DbConnectProfile p = null;
		response.setSuccess(true);
		try {
			p = this.dbConnectService.getConnectProfile(rwMode, identityHashCode);
		} catch (Exception ex) {
			response.setSuccess(false);
			String errorCause = "插件读写模式:" + rwMode + ", identityHashCode:" + identityHashCode;

			String msg = "执行数据库SQL异常," + errorCause;
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			logger.error(msg, ex);
			response.setRaex(raex);
		}
		return p;
	}

	/**
	 * 获取数据库连接资料(profile), 如果出错将填充出错信息
	 * 
	 * @param rwMode
	 *            -- 读写模式: w:只写, r: 只读, rw: 可读写
	 * @param response
	 *            -- 响应对象, 如果出错, 错误信息被填充到res对象
	 * @return -- 据库连接资料(DbConnectProfile)对象
	 */
	private List<DbConnectProfile> getProfilesAndFillResponse(String rwMode, FwDbAccessResponse response) {

		List<DbConnectProfile> ps = null;
		response.setSuccess(true);
		try {
			ps = this.dbConnectService.getConnectProfiles(rwMode);
		} catch (Exception ex) {
			response.setSuccess(false);
			String errorCause = "定位数据库出错,读写模式:" + rwMode;
			String msg = errorCause;
			FwRuntimeException raex = new FwRuntimeException(msg, ex);
			response.setRaex(raex);
		}
		return ps;
	}
}

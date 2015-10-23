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
package com.github.ipaas.ifw.cache.distributed.memcached.spy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.transcoders.Transcoder;
import net.spy.memcached.transcoders.WhalinTranscoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.cache.DistributedCacheService;
import com.github.ipaas.ifw.cache.distributed.memcached.MemcachedOperationProxy;
import com.github.ipaas.ifw.cache.distributed.memcached.mbean.MemcachedHealthCheck;
import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.locate.HashAlgorithms;
import com.github.ipaas.ifw.core.locate.NodeLocator;
import com.github.ipaas.ifw.core.locate.NodeLocators;
import com.github.ipaas.ifw.core.mbean.Fw;
import com.github.ipaas.ifw.util.JMXUtil;

/**
 * Spy Memcached客户端实现的分布式缓存服务 备注: 使用spy Memcached客户端直接连接Memcached服务器
 * 
 * Chenql
 */
@SuppressWarnings("unchecked")
public final class FwSpyDirectMemcachedService implements DistributedCacheService {

	private static Logger logger = LoggerFactory.getLogger(FwSpyDirectMemcachedService.class);

	/**
	 * 链接超时
	 */
	private long connectTimeout = 1000L;

	/**
	 * 操作超时
	 */
	private long operateTimeout = 5000L;

	/**
	 * 服务器url
	 */
	private String serverUrl;

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

	public void setConnectTimeout(long connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setOperateTimeout(long operateTimeout) {
		this.operateTimeout = operateTimeout;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	/**
	 * Memcached分组的组对象集合 备注: 一个组就是一个Memcached操作代理对象
	 */
	private List<MemcachedOperationProxy> groups;

	/**
	 * MemcachedOperationProxy定位器（组定位器）
	 */
	private NodeLocator<MemcachedOperationProxy> proxyLocator;

	/**
	 * 根据key统一哈希获取MemcachedOperationProxy对象
	 * 
	 * @param key
	 *            -- 操作的key
	 * @return -- MemcachedOperationProxy对象
	 */
	private MemcachedOperationProxy getMOP(String key) {
		if (proxyLocator == null) {
			// 同步所有插件初始化过程(初始化串行化)
			synchronized (logger) {
				if (proxyLocator == null) {
					// 设置spy的log方式
					System.setProperty("net.spy.log.LoggerImpl", "net.spy.memcached.compat.log.Log4JLogger");

					String[] groupServerUrls = serverUrl.trim().split(",");
					this.groups = new ArrayList<MemcachedOperationProxy>(groupServerUrls.length);
					for (String item : groupServerUrls) {
						MemcachedOperationProxy group = new MemcachedDistributedGroup(item);
						groups.add(group);
					}
					if (groups.size() == 0) {
						throw new FwRuntimeException("memcached分组数量为0");
					}
					// 创建Ketama统一哈希NodeLocator
					proxyLocator = NodeLocators.newKetamaConsistentHashLocator(HashAlgorithms.KEMATA_HASH, groups);
					System.out.println("##########  " + proxyLocator.hashCode());
				}
			}
		}
		return proxyLocator.locate(key, 0);
	}

	public boolean delete(String key, long expiry) {
		MemcachedOperationProxy mop = getMOP(key);
		return mop.delete(key, expiry);
	}

	public boolean delete(String key) {
		MemcachedOperationProxy mop = getMOP(key);
		return mop.delete(key);
	}

	public Object get(String key, Callable refreshSource, long expiry) {
		MemcachedOperationProxy mop = getMOP(key);
		Object value = mop.get(key);
		if (null == value) {
			try {
				value = refreshSource.call();
				if (null != value) {
					mop.set(key, value, expiry);
				}
			} catch (Exception ex) {
				throw new FwRuntimeException("保存数据到Memcached异常", ex);
			}
		}
		return value;
	}

	public Object get(String key, Callable refreshSource) {
		MemcachedOperationProxy mop = getMOP(key);
		Object value = mop.get(key);
		if (null == value) {
			try {
				value = refreshSource.call();
				if (null != value) {
					mop.set(key, value);
				}
			} catch (Exception ex) {
				throw new RuntimeException("执行刷新缓存对象失败, key:" + key, ex);
			}
		}
		return value;
	}

	public Object get(String key) {
		MemcachedOperationProxy mop = getMOP(key);
		return mop.get(key);
	}

	public boolean setReturn(String key, Object value, long expiry) {
		MemcachedOperationProxy mop = getMOP(key);
		return mop.set(key, value, expiry);
	}

	public void set(String key, Object value, long expiry) {
		MemcachedOperationProxy mop = getMOP(key);
		mop.set(key, value, expiry);
	}

	public boolean set(String key, Object value) {
		MemcachedOperationProxy mop = getMOP(key);
		return mop.set(key, value);
	}

	public boolean compareAndSet(String key, Object oldValue, Object newValue) {
		MemcachedOperationProxy mop = getMOP(key);
		Object value = mop.get(key);
		if (null != value && value.equals(oldValue)) {
			mop.set(key, newValue);
			return true;
		}
		return false;
	}

	public Object getAndSet(String key, Object newValue) {

		MemcachedOperationProxy mop = getMOP(key);
		Object value = mop.get(key);
		mop.set(key, newValue);
		return value;
	}

	public Map<String, Object> getMulti(Collection<String> keys) {

		Map<MemcachedOperationProxy, Collection<String>> s = new LinkedHashMap<MemcachedOperationProxy, Collection<String>>();
		for (String k : keys) {
			MemcachedOperationProxy p = getMOP(k);
			Collection<String> c = s.get(p);
			if (null == c) {
				c = new LinkedList<String>();
				s.put(p, c);
			}
			c.add(k);
		}
		Map<String, Object> rtv = new HashMap<String, Object>();
		for (MemcachedOperationProxy p : s.keySet()) {
			Collection<String> c = s.get(p);
			Map<String, Object> r = p.getMulti(c);
			rtv.putAll(r);
		}
		return rtv;
	}

	/**
	 * 按分布式算法组织节点的Memcached分组类 备注: 组中所有Memcached节点服务的数据组成一个大整体
	 * 
	 * @author zhandl (yuyoo zhandl@hainan.net)
	 * @teme 2010-5-15 下午02:46:07
	 */
	private class MemcachedDistributedGroup implements MemcachedOperationProxy {

		/**
		 * 组中Memcached服务器连接字符串
		 */
		private String groupServerUrl = null;

		/**
		 * 
		 */
		private MemcachedClient mcc = null;

		/**
		 * whalin方式memcached编码转换器
		 */
		private Transcoder whalinTranscoder = new WhalinTranscoder();

		/**
		 * 构造memcached组
		 * 
		 * @param groupServerUrl
		 *            -- 组中Memcached服务器连接字符串
		 */
		MemcachedDistributedGroup(String groupServerUrl) {
			this.groupServerUrl = groupServerUrl;
			String items = groupServerUrl.replaceAll(";", " ");
			try {
				mcc = new MemcachedClient(new DefaultConnectionFactory() {
					@Override
					public long getOperationTimeout() {
						return connectTimeout;
					}
				}, AddrUtil.getAddresses(items));

				initMemCachedHealthMBean(groupServerUrl);
			} catch (Exception ex) {
				throw new FwRuntimeException("初始化MemcachedClient对象失败", ex);
			}

		}

		/**
		 * 
		 * 初始化Memcached可用性检测的MBean
		 * 
		 * @param serverUrl
		 * @return void
		 */
		public void initMemCachedHealthMBean(String serverUrl) {

			MemcachedHealthCheck memCachedHealthCheck;
			try {

				String[] node = serverUrl.split(";");
				for (int i = 0; i < node.length; i++) {
					// 获取分组
					String[] group = node[i].split(",");
					for (int j = 0; j < group.length; j++) {
						String[] ipPort = group[j].split(":");
						memCachedHealthCheck = new MemcachedHealthCheck(serviceId, ipPort[0], ipPort[1], null);
						String oname = JMXUtil.createObjectNameString(Fw.FW_USABILITY_DOMAIN,
								MemcachedHealthCheck.TYPE, serviceId, ipPort[0] + "-" + ipPort[1]);
						JMXUtil.registerMBean(memCachedHealthCheck, oname);

						memCachedHealthCheck = null;

					}
				}
				logger.info("初始化Memcached可用性检测的MBean:{}", serverUrl);
			} catch (Throwable e) {
				logger.error("initMemCachedHealthMBean error", e);
			}

		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("@FwSpyDirectMemcachedService#MemcachedDistributedGroup{");
			sb.append("groupServerUrl:").append(groupServerUrl).append(",");
			String realGroup = "/" + groupServerUrl;
			realGroup = realGroup.replace(";", " /");
			sb.append("mcc:").append("Thread[Memcached IO over {MemcachedConnection to " + realGroup + "},5,main]");
			sb.append("}");
			return sb.toString();
		}

		public boolean delete(String key) {
			try {
				Future<Boolean> f = mcc.delete(key);
				return f.get(operateTimeout, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				logger.error("Distributed组删除缓存数据异常, key:" + key, e);
			}
			return false;
		}

		public boolean delete(String key, long expiry) {
			// MemcachedClient delete(key, expiry) is no longer honored, use
			// directly delete(key) instead
			return delete(key);
		}

		public Object get(String key) {
			try {
				return mcc.get(key, whalinTranscoder);
			} catch (RuntimeException ex) {
				logger.error("get key[{}] exception.", key);
				throw ex;
			}
		}

		public boolean set(String key, Object value) {
			return set(key, value, 0L);
		}

		public boolean set(String key, Object value, long expiry) {
			try {
				// 通过距离当前时间的时间间隔来设置过期时间时，过期时间不能大于 2592000秒（即30天），
				// 如果时间值大于2592000
				// ，那么memcached会把时间理解为unix时间戳格式也就是距离1970.01.01的秒数偏移量
				int expiryInSec = (int) (expiry / 1000);
				if (expiryInSec > 2592000) {
					expiryInSec += System.currentTimeMillis() / 1000;
				}
				Future<Boolean> f = mcc.set(key, expiryInSec, value, whalinTranscoder);
				return f.get(operateTimeout, TimeUnit.MILLISECONDS).booleanValue();
			} catch (Exception ex) {
				logger.error("Distributed组保存数据异常, key:" + key, ex);
			}
			return false;
		}

		public Map<String, Object> getMulti(Collection<String> keys) {
			try {
				return mcc.getBulk(keys, whalinTranscoder);
			} catch (RuntimeException ex) {
				logger.error("get keys[{}] exception.", keys);
				throw ex;
			}
		}
	}

}

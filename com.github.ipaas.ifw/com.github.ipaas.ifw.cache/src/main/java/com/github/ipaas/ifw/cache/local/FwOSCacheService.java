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
package com.github.ipaas.ifw.cache.local;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.cache.LocalCacheService;
import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;
import com.opensymphony.oscache.web.filter.ExpiresRefreshPolicy;

/**
 * 使用OSCache实现的本地缓存服务插件类
 * 
 * @author Chenql
 */
@SuppressWarnings("rawtypes")
public final class FwOSCacheService implements LocalCacheService {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory.getLogger(FwOSCacheService.class);

	/**
	 * oscache缓存管理器
	 */
	private GeneralCacheAdministrator admin = new GeneralCacheAdministrator();

	/**
	 * oscache异步刷新缓存对象服务
	 */
	private ExecutorService asyncRefreshService = Executors.newCachedThreadPool();

	/**
	 * oscache本地缓存服务插件ID, 同时是缓存组名称
	 */
	private String groupId = "defaultGroup";

	/*
	 * 清除OSCache缓存中的一个缓存组(pluginId:缓存组名称)中的缓存对象
	 * 
	 */
	public void clearAll() {
		admin.flushGroup(groupId);
	}

	/*
	 * 获取OSCache缓存中对象,如果过期就从刷新源获取
	 * 
	 * java.util.concurrent.Callable, long)
	 */
	public Object get(final String key, final Callable refreshSource, final long expiry) {

		Object value = null;
		try {
			value = admin.getFromCache(key);
		} catch (NeedsRefreshException nre) {
			Object oldValue = nre.getCacheContent();
			final String[] groups = new String[] { groupId };
			final ExpiresRefreshPolicy expirePolicy = new ExpiresRefreshPolicy((int) (expiry / 1000));

			if (null != refreshSource) { // 缓存刷新源不为null
				Runnable refreshTask = new Runnable() {
					public void run() {
						try {
							Object value = refreshSource.call(); // 从缓存刷新源获取缓存对象,并将其从新设置进缓存
							if (null != value) {
								admin.putInCache(key, value, groups, expirePolicy); // 更新缓存
							}
						} catch (Exception ex) {
							throw new FwRuntimeException("OSCache操作key[" + key + "]时出异常");
						}
					}
				};
				if (null != oldValue) {
					value = oldValue;
					admin.putInCache(key, value, groups, expirePolicy);
					asyncRefreshService.submit(refreshTask);
				} else {
					try {
						value = refreshSource.call();
						if (null != value) {
							admin.putInCache(key, value, groups, expirePolicy); // 更新缓存
						}
					} catch (Exception ex) {
						throw new FwRuntimeException("OSCache操作key[" + key + "],使用refreshSource更新值异常");
					} finally {
						if (value == null) {
							admin.cancelUpdate(key);
						}
					}
				}
			} else {
				admin.cancelUpdate(key); // 取消更新
				logger.info("OSCache操作key[{}]失败, refreshSource为空异常", key);
			}
		}
		return value;
	}

	public void set(String key, Object value, long expiry) {

		String[] groups = new String[] { this.groupId };
		ExpiresRefreshPolicy policy = new ExpiresRefreshPolicy((int) (expiry / 1000));
		admin.putInCache(key, value, groups, policy);
	}

	/**
	 * 获取OSCache中缓存对象的个数
	 */
	public int size() {
		return admin.getCache().getSize();
	}

	public boolean compareAndSet(String key, Object oldValue, Object newValue) {

		Object value = get(key);
		if (null != value && value.equals(oldValue)) {
			return set(key, newValue);
		}
		return false;
	}

	/*
	 * 只要执行了, 不发生异常就返回true
	 * 
	 */
	public boolean delete(String key) {
		admin.removeEntry(key);
		return true;
	}

	/*
	 * 根据key获取OSCache中的缓存对象 如果对象过期, 将其从OSCache中删除
	 * 
	 */
	public Object get(String key) {

		Object value = null;
		try {
			value = admin.getFromCache(key);
		} catch (NeedsRefreshException nre) {
			admin.cancelUpdate(key);
			logger.debug("OSCache对key[{}]缓存get操作, 引发NeedsRefreshException", key);
		}
		return value;
	}
	public Object get(final String key, final Callable refreshSource) {
		return get(key, refreshSource, Long.MAX_VALUE);
	}

	/*
	 * 获取OSCache的对象并对其设置新值
	 */
	public Object getAndSet(String key, Object newValue) {

		Object rtv = get(key);
		set(key, newValue);
		return rtv;
	}

	/*
	 * 保存对象到OSCache的一个组
	 * 
	 */
	public boolean set(String key, Object value) {

		String[] groups = new String[] { this.groupId };
		admin.putInCache(key, value, groups);
		return true;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}

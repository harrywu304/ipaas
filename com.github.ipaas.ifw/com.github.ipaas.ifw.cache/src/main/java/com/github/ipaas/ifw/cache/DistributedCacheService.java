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
package com.github.ipaas.ifw.cache;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 分布式缓存服务
 * 
 * @author Chenql
 */
@SuppressWarnings("rawtypes")
public interface DistributedCacheService extends CacheService {

	/**
	 * 保存对象,在设置的时长后过期
	 * 
	 * @param key
	 *            -- 缓存key
	 * @param value
	 *            -- 缓存对象
	 * @param expiry
	 *            -- 过期时长, 单位:毫秒
	 */
	void set(final String key, final Object value, long expiry);

	/**
	 * 保存对象,在设置的时长后过期
	 * 
	 * @param key
	 *            -- 缓存key
	 * @param value
	 *            -- 缓存对象
	 * @param expiry
	 *            -- 过期时长, 单位:毫秒
	 * @return -- true-成功,false-失败
	 */
	boolean setReturn(final String key, final Object value, long expiry);

	/**
	 * 根据key获取缓存对象, 并指定一个刷新源, 如果没有命中使用刷新源重新获取数据，并根据指定过期时间 重新保存到缓存中
	 * 
	 * @param key
	 *            -- 缓存key
	 * @param refreshSource
	 *            -- 刷新源
	 * @return -- 缓存对象
	 *         <p>
	 *         备注:
	 *         若调用refreshSource抛出异常，该方法会捕捉并重新抛出一个RuntimeException提示“执行刷新缓存对象失败”
	 *         等价代码: Object result = cacheServcie.get(key); if (null == result)
	 *         {
	 * 
	 *         result = refreshSource.call(); cacheServcie.set(key, result,
	 *         expiry); } return result;
	 */
	Object get(String key, Callable refreshSource, long expiry);

	/**
	 * 根据key的集合,一次获取多个key
	 * 
	 * @param keys
	 *            -- key的集合
	 * @return 缓存结果集合
	 */
	Map<String, Object> getMulti(Collection<String> keys);

	/**
	 * 在设定时长后删除缓存对象
	 * 
	 * @param key
	 *            -- 缓存key
	 * @return -- true, 删除动作执行
	 */
	boolean delete(final String key);

}

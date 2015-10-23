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

import java.util.concurrent.Callable;

/**
 * 缓存服务基础接口
 * 
 * @author Chenql
 */
@SuppressWarnings({ "rawtypes" })
public interface CacheService {

	/**
	 * 根据key,将value保存到缓存服务
	 * 
	 * @param key
	 *            -- 缓存对象的标记key
	 * @param value
	 *            -- 缓存对象，缓存对象不能为null
	 * @return -- true, 如果执行了操作, 但是不能保证真正已经保存近底层的缓存 这里需要依赖具体的实现.
	 */
	boolean set(String key, Object value);

	/**
	 * 根据key获取缓存对象
	 * 
	 * @param key
	 *            -- 缓存key
	 * @return -- 缓存对象
	 */
	Object get(String key);

	/**
	 * 根据key获取缓存对象, 并指定一个刷新源
	 * 
	 * <pre>
	 * 备注: 当根据key获取缓存对象为null时, 调用refreshSource获取最新的可缓存对象
	 * 将其保存到缓存服务.
	 * 等价代码:
	 *     Object result = cacheServcie.get(key);
	 *     if (null == result) {
	 *         
	 *         result = refreshSource.call();
	 *         cacheServcie.set(key, result);
	 *     }
	 *     return result;
	 * </pre>
	 * 
	 * @param key
	 *            -- 缓存key
	 * @param refreshSource
	 *            -- 刷新源
	 * @return -- 缓存对象
	 * @deprecated 用子接口的get(String key, Callable refreshSource, long expiry)方法代替
	 *             <p>
	 *             因为当refreshSource抛出异常时，该方法在本地缓存和分布式缓存中有着不一致的postCondition，
	 *             无法满足LSP原则
	 */
	Object get(String key, Callable refreshSource);

	/**
	 * 根据key,将缓存对象从缓存服务中删除
	 * 
	 * @param key
	 *            -- 缓存key
	 * @return -- true, 如果执行了操作,但是不能保证真正已经删除了缓存对象 这里需要依赖具体的实现.
	 */
	boolean delete(String key);

	/**
	 * 根据key获取缓存对象,同时将这个key设置成另外一个对象
	 * 
	 * <pre>
	 * 等价代码:
	 *      Object result = cacheServcie.get(key);
	 *      cacheServcie.set(key, newValue);
	 *      return result;
	 * </pre>
	 * 
	 * @param key
	 *            -- 缓存key
	 * @param newValue
	 *            -- 新的缓存对象，缓存对象不能为null
	 * @return -- 旧的缓存对象(未设置新的对象前的那个缓存对象)
	 */
	Object getAndSet(String key, Object newValue);

	/**
	 * 对特定的key, 比较缓存服务中的对象如果等于指定的对象, 用一个新的对象将其设置成新值
	 * 
	 * <pre>
	 * 等价代码:
	 *     Object cacheObj = cacheServcie.get(key);
	 *     if (null != cacheObj && cacheObj.equals(oldValue)) {
	 *     
	 *     	   return cacheServcie.set(key, newValue);
	 *     }
	 *     return false;
	 * </pre>
	 * 
	 * @param key
	 *            -- 缓存key
	 * @param oldValue
	 *            -- 用于比较的对象
	 * @param newValue
	 *            -- 如果比较相等了, 将会被设置进缓存服务的对象，缓存对象不能为null
	 * @return -- true, 如果执行了比较并设置了新值.
	 */
	boolean compareAndSet(String key, Object oldValue, Object newValue);

}

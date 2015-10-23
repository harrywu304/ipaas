package com.github.ipaas.ifw.cache;

import java.util.concurrent.Callable;
 

/**
 * 本地缓存服务.
 * <br>缓存数据存在当前jvm或在当前jvm所在的主机.
 * <pre>
 *   使用演示代码:
 *   <1>获取LocalCacheService实例
 *   LocalCacheService lcs = fac.getLocalCacheService(); // fac 是 ServiceFactory对象
 *   <2>保存对象:
 *   String key = "cache_demo_key";
 *   String value = "cache_demo_value";
 *   lcs.set(key, value);
 *   <3>保存对象并指定过期时间:
 *   String key = "cache_demo_key";
 *   String value = "cache_demo_value";
 *   long expiry = 60000L; // 60秒过期, 
 *   lcs.set(key, value, expiry);
 *   <3>获取缓存对象:
 *   String key = "cache_demo_key";
 *   String value = (String)lcs.get(key);
 *   <4>获取缓存对象,指定过期时间,指定刷新源:
 *   String key = "cache_demo_key";
 *   long expirey = 60000L; // 60秒过期
 *   Callable refrestSrc = new Callable() {
 *            public Object call() {
* 				//获取最新的数据
* 				Object rtv = getNewCacheValue(key);
* 				return rtv;
 *  		  }
 *      };
 *     String value = lcs.get(key, refreshSrc, expirey);
 *   <5> 获取缓存对象数量:
 *   int cacheSize = lcs.size();
 *   <6> 清除缓存所有缓存对象:
 *   lcs.clearAll();
 * </pre>
 * @author yuyoo (yuyoo4j@163.com)
 * @date 2010-4-28 下午06:30:01
 * @since 1.6.0
 */
@SuppressWarnings({ "rawtypes" })
public interface LocalCacheService extends CacheService {

	/**
	 * 根据key,设置缓存对象, 并指定了过期时间
	 * @param key -- 缓存key
	 * @param value -- 缓存对象
	 * @param expiry -- 过期时长,单位:毫秒
	 */
	void set(String key, Object value, long expiry);
	 
	/**
	 * 根据key获取缓存对象, 并指定一个刷新源, 如果没有命中使用刷新源重新获取数据，并根据指定过期时间
	 * 重新保存到缓存中
	 * <p>为了提高效率，针对OSCache的实现增加了一些优化处理。
	 * <p>1）当根据key获取缓存对象不存在时, 同步调用refreshSource更新缓存对象，并返回缓存对象；
	 * <p>2）当根据key获取缓存对象已存在但已过期时，异步调用refreshSource更新缓存对象，立即返回旧缓存对象；
	 * <p>3）若调用refreshSource获取新数据时抛出Exception，记录错误日志并返回旧缓存对象；
	 * @param key -- 缓存key
	 * @param refreshSource -- 刷新源
	 * @return -- 缓存对象
	 */
	Object get(String key, Callable refreshSource, long expiry);
	
	/**
	 * 清除特定缓存组内（由ServiceFactory对象的AppId决定）的所有的本地缓存项
	 */
	void clearAll();
	
	/**
	 * 获取缓存项的个数
	 * @return -- 缓存项的个数
	 */
	int size();
}

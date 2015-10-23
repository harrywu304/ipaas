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

import net.spy.memcached.MemcachedClient;

/**
 * 关闭资源工具类，资源关闭的同时也会被设置为null
 * <p>
 * 备注: 可以关闭JDK里面绝大多数需要关闭的资源对象,同时不对外抛出异常
 * <p>
 * 如果第三方需要关闭的资源类遵循jdk的接口标准也可以关闭,主要使用接口Closeable;
 * 
 * @author Chenql
 */
public final class CloseUtil {

	/**
	 * 关闭Spy MemcachedClient资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(MemcachedClient rsc) {

		if (null != rsc) {
			try {
				rsc.shutdown();
				// rsc.destroy();
			} catch (Throwable ex) {
				/* 消除异常 */
			}
			rsc = null;
		}
	}

}

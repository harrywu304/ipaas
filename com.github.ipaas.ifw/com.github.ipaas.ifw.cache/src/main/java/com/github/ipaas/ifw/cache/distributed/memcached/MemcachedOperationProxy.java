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
package com.github.ipaas.ifw.cache.distributed.memcached;

import java.util.Collection;
import java.util.Map;

/**
 * Memcached操作代理
 * 
 * @author Chenql
 */
public interface MemcachedOperationProxy {

	/**
	 * memcached get command
	 * 
	 * @param key
	 *            -- operation key
	 * @return -- return value
	 */
	Object get(String key);

	/**
	 * memcached get keys command
	 * 
	 * @param keys
	 *            -- operation keys
	 * @return -- return value
	 */
	Map<String, Object> getMulti(Collection<String> keys);

	/**
	 * memcached set command
	 * 
	 * @param key
	 *            -- operation key
	 * @param value
	 *            -- store value
	 * @return -- if success,return true,else return false
	 */
	boolean set(String key, Object value);

	/**
	 * memcached set command
	 * 
	 * @param key
	 *            -- operation key
	 * @param value
	 *            -- store value
	 * @param expiry
	 *            -- expiry time in milliseconds
	 * @return -- if success,return true,else return false
	 */
	boolean set(String key, Object value, long expiry);

	/**
	 * memcached delete commamd
	 * 
	 * @param key
	 *            -- operation key
	 * @return -- return boolean result
	 */
	boolean delete(String key);

	/**
	 * memcached delete command
	 * 
	 * @param key
	 *            -- operation key
	 * @param expiry
	 *            -- expiry time in milliseconds
	 * @return -- return boolean result
	 */
	boolean delete(String key, long expiry);
}

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

package com.github.ipaas.ifw.jdbc;

/**
 * 数据库访问请求对象
 * 
 * @author Chenql
 */
public interface DbAccessRequest {

	/**
	 * 设置请求ID 备注: 建议使用'全局唯一ID', '全局唯一ID'由应用层与具体实现类决定.它的目的用来标示一个请求, 被用于定位,跟踪请求执行
	 * 它的唯一性由调用者, 实现者都可以控制.它存在不唯一的可能性.
	 * 
	 * @param reqId
	 *            -- 请求ID
	 */
	void setId(String reqId);

	/**
	 * 获取请求ID
	 * 
	 * @return -- 数据库访问请求ID
	 */
	String getId();

	/**
	 * 设置数据访问的SQL语句
	 * 
	 * @param sql
	 *            -- SQL语句
	 */
	void setSql(String sql);

	/**
	 * 获取数据访问的SQL语句
	 * 
	 * @return -- SQL语句 String
	 */
	String getSql();

	/**
	 * 设置 用于hash定位将要执行sql的数据库的hash值
	 * 
	 * @param hashCode
	 *            -- hash值
	 */
	void setIdentityHashCode(long hashCode);

	/**
	 * 获取 用于hash定位将要执行sql的数据库的hash值
	 * 
	 * @return -- hash值
	 */
	long getIdentityHashCode();

	/**
	 * 设置请求超时时间,单位:毫秒 如果没有设置,使用默认超时时间
	 * 
	 * @param timeout
	 *            -- 超时时间
	 */
	void setRequestTimeout(long timeout);

	/**
	 * 获取请求超时时间,单位:毫秒
	 * 
	 * @return -- 超时时间
	 */
	long getRequestTimeout();
}

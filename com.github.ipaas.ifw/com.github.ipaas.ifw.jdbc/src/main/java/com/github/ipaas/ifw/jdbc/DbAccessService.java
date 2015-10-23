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

import java.util.Map;

/**
 * 数据库访问服务
 * 
 * @author Chenql
 */
public interface DbAccessService {

	/**
	 * 创建一个数据库访问请求对象
	 * 
	 * @return -- 数据库访问请求对象
	 */
	DbAccessRequest createRequest();

	/**
	 * 执行对数据查询的数据库访问请求 <br>
	 * 备注: 这里不指定特定的数据库, sql会在水平分割的所有数据库上执行并合并结果返回.
	 * 
	 * @param sql
	 *            -- SQL语句
	 * @return -- 数据库访问响应
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbAccessResponse executeQuery(String sql);

	/**
	 * 执行对数据查询的数据库访问请求 <br>
	 * 备注: 数据访问层会按特定的hash算法, 使用request的identityHashCode定位目的数据库, 并在上面执行sql语句.
	 * 
	 * @param request
	 *            -- 数据库访问请求对象
	 * @return -- 数据库访问响应
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbAccessResponse executeQuery(DbAccessRequest request);

	/**
	 * 执行对数据查询的数据库访问请求 <br>
	 * 备注: 数据访问层会按特定的hash算法, 使用identityHashCode定位目的数据库, 并在上面执行sql语句.
	 * 
	 * @param sql
	 *            -- SQL语句
	 * @param identityHashCode
	 *            -- 用于hash定位将要执行sql的数据库的hash值
	 * @return -- 数据库访问响应
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbAccessResponse executeQuery(String sql, long identityHashCode);

	/**
	 * 执行对数据更新的数据库访问请求 <br>
	 * 备注: 不指定特定的数据库, sql会在水平分割的所有数据库上执行.
	 * 
	 * @param sql
	 *            -- SQL语句
	 * @return -- 数据库访问响应
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbAccessResponse executeUpdate(String sql);

	/**
	 * 执行对数据更新的数据库访问请求 <br>
	 * 备注: 数据访问层会按特定的hash算法, 使用request的identityHashCode定位目的数据库, 并在上面执行sql语句.
	 * 
	 * @param request
	 *            -- 数据库访问请求对象
	 * @return -- 数据库访问响应
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbAccessResponse executeUpdate(DbAccessRequest request);

	/**
	 * 执行对数据更新的数据库访问请求 <br>
	 * 备注: 数据访问层会按特定的hash算法, 使用identityHashCode定位目的数据库, 并在上面执行sql语句.
	 * 
	 * @param sql
	 *            -- SQL语句
	 * @param identityHashCode
	 *            -- 用于hash定位将要执行sql的数据库的hash值
	 * @return -- 数据库访问响应
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbAccessResponse executeUpdate(String sql, long identityHashCode);

	/**
	 * 批量执行数据查询的数据库访问请求 <br>
	 * 备注: 方法会检测数据访问请求是否是查询请求
	 * 
	 * @param requests
	 *            -- 请求集合, 可以使用key定位数据访问响应
	 * @return -- 数据库访问响应集合
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	Map<String, DbAccessResponse> executeQueryBatch(Map<String, DbAccessRequest> requests);

	/**
	 * 批量执行数据更新的数据库访问请求 <br>
	 * 注意：请求被执行的顺序是无法保证的 <br>
	 * 备注:事务控制(transaction)指在分库的背景下保证在同一个数据库上执行的请求是事务控制的 <br>
	 * 事务控制需要使用的底层依赖的数据库支持事务
	 * 
	 * @param requests
	 *            -- 请求集合, 可以使用key定位数据访问响应
	 * @param transaction
	 *            -- 是否需要事务控制
	 * @return -- 数据库访问响应集合
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	Map<String, DbAccessResponse> executeUpdateBatch(Map<String, DbAccessRequest> requests, boolean transaction);

	/**
	 * 异步执行数据更新的数据库访问请求
	 * 
	 * @param sql
	 *            -- SQL语句
	 */
	void asyncExecuteUpdate(String sql);

	/**
	 * 异步执行数据更新的数据库访问请求
	 * 
	 * @param sql
	 *            -- SQL语句
	 * @param identityHashCode
	 *            -- 用于hash定位将要执行sql的数据库的hash值
	 */
	void asyncExecuteUpdate(String sql, long identityHashCode);

	/**
	 * 异步执行数据更新的数据库访问的批量请求 <br>
	 * 注意：请求被执行的顺序是无法保证的 <br>
	 * 备注:事务控制(transaction)指在分库的背景下保证在同一个数据库上执行的请求是事务控制的 <br>
	 * 事务控制需要使用的底层依赖的数据库支持事务
	 * 
	 * @param requests
	 *            -- 请求集合
	 * @param transaction
	 *            -- 是否需要事务控制
	 */
	void asyncExecuteUpdateBatch(Map<String, DbAccessRequest> requests, boolean transaction);

	/**
	 * 根据sql和profile进行查询
	 * 
	 * @param profile
	 * @param sql
	 *            ：sql语句
	 * @return：返回数据库响应
	 */
	public DbAccessResponse executeQuery(DbConnectProfile profile, String sql);

	public abstract void setDbConnectService(DbConnectService dbConnectService);

	public abstract void setScaleoutMapping(String scaleoutMapping);

	public abstract DbConnectService getDbConnectService();

	public abstract String getScaleoutMapping();

	public abstract void initializePlugin();
}

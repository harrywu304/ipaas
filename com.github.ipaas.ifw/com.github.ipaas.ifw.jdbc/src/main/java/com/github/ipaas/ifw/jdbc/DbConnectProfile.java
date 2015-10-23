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

import java.sql.Connection;

/**
 * 数据库连接资料类
 * 
 * @author Chenql
 */
public interface DbConnectProfile {

	/**
	 * 获取数据库连接池别名
	 * 
	 * @return -- 连接池别名
	 */
	String getDbPoolAlias();

	/**
	 * 获取数据库连接
	 * 
	 * @return -- 数据库连接
	 */
	Connection getConnection();

	/**
	 * 初始化数据库连接上下文时间(主要是申请数据库连接的时间),单位: 毫秒
	 * 
	 * @return -- 花费时间
	 */
	long initializedContextTime();
}

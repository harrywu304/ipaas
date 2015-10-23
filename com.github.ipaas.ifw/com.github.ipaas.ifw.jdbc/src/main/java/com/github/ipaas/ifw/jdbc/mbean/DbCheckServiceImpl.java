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
package com.github.ipaas.ifw.jdbc.mbean;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.jdbc.DbConnectProfile;
import com.github.ipaas.ifw.jdbc.DbConnectService;
import com.github.ipaas.ifw.util.CloseUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * 
 * 数据库可用性检查的实现类
 * 
 * @author Chenql
 * 
 */
public class DbCheckServiceImpl {

	private static Logger logger = LoggerFactory.getLogger(DbCheckServiceImpl.class);

	private DbConnectService dbConnectService;

	/**
	 * 发送测试SQL语句做健康检查 方法的描述
	 * 
	 * @param appid
	 *            -- 应用ID
	 * @param alias
	 *            -- 连接池别名
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	public String check(DbConnectService dbConnectService, String alias) {
		String checkState = null;

		this.setDbConnectService(dbConnectService);
		String testSql = "select 1 as \"1\"";
		DbConnectProfile dbConnectProfile = null;
		Connection connection = null;
		Statement statement = null;
		ResultSet rs = null;
		String columnValue = null;
		try {
			dbConnectProfile = dbConnectService.getConnectProfile(alias);
			if (null == dbConnectProfile) {
				checkState = "未能通过别名" + alias + "获取到数据库连接!";
				return checkState;
			}
			connection = dbConnectProfile.getConnection();
			statement = connection.createStatement();
			rs = statement.executeQuery(testSql);
			rs.next();
			columnValue = rs.getString("1");
		} catch (Throwable e) {
			checkState = StringUtil.getExceptionAsStr(e);
			logger.error("db check error", e);
			return checkState;
		} finally {
			CloseUtil.closeSilently(rs);
			CloseUtil.closeSilently(statement);
			CloseUtil.closeSilently(connection);
		}

		if (!StringUtil.isNullOrBlank(columnValue)) {
			if (columnValue.equals("1")) {
				checkState = "1";
			} else {
				checkState = "select 1 as \"1\" statement's result is not 1";
			}
		} else {
			checkState = "select 1 as \"1\" statement's result is null or blank";
		}
		return checkState;
	}

	public DbConnectService getDbConnectService() {
		return dbConnectService;
	}

	public void setDbConnectService(DbConnectService dbConnectService) {
		this.dbConnectService = dbConnectService;
	}

}

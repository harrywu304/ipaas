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

package com.github.ipaas.ifw.jdbc.impl;

import java.util.Random;
import java.util.Set;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.jdbc.DbConnectProfile;
import com.github.ipaas.ifw.jdbc.DbConnectService;

/**
 * 数据库主从分组
 * 
 * @author Chenql
 */
public class DbMasterSlaveGroup {
	private static Random random = new Random();
	/**
	 * 主节点别名
	 */
	String masterAlias = null;

	/**
	 * 从节点别名数组
	 */
	String[] slaveAliases = null;

	public DbMasterSlaveGroup(String group) {

		String[] g = group.trim().split(":");
		masterAlias = g[0];
		if (g.length == 0) {//
			slaveAliases = new String[] { masterAlias };
		} else {
			slaveAliases = g[1].split(";");
		}
	}

	/**
	 * 获取从库的数据库连接资料(DbConnectProfile), 带故障转移功能
	 * 
	 * @return -- 数据库连接资料(DbConnectProfile)
	 */
	public DbConnectProfile failoverGetSlave(DbConnectService dbConnectService, Set<String> failSlaves) {

		DbConnectProfile profile = null;
		int ss = slaveAliases.length;

		int nextStart = random.nextInt(ss);

		// 从库节点之间的failover
		for (int i = nextStart; i < nextStart + ss; i++) {
			int curr = (i >= ss) ? (i - ss) : i;
			String alias = slaveAliases[curr];
			if (failSlaves.contains(alias)) {
				continue;
			}
			profile = dbConnectService.getConnectProfile(alias);
			if (null != profile) {
				return profile;
			}
		}

		// 使用主库作为从库使用的failover
		profile = dbConnectService.getConnectProfile(masterAlias);
		if (null == profile) {
			throw new FwRuntimeException("定位数据库出错,通过Failover机制仍然无法取得应用的可用读连接");
		}
		return profile;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append("DbMasterSlaveGroup{");
		sb.append("masterAlias:").append(masterAlias).append(",");
		for (int i = 0; i < slaveAliases.length; i++) {
			sb.append("slaveAliases[").append(i).append("]:").append(slaveAliases[i]);
			if (i != slaveAliases.length) {
				sb.append(",");
			}
		}
		sb.append("}");
		return sb.toString();
	}
}
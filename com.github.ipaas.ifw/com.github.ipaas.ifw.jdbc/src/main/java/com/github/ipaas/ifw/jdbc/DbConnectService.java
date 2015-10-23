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

import java.util.List;

import com.github.ipaas.ifw.core.locate.NodeLocator;
import com.github.ipaas.ifw.jdbc.impl.DbMasterSlaveGroup;

/**
 * 数据库连接服务
 * <p>
 * 优先使用DbAccessService，当需要复杂事务或特殊需求时可通过该服务直接获取连接
 * 
 * @author Chenql
 */
public interface DbConnectService {

	/**
	 * 获取写数据库连接池别名
	 * 
	 * @param identityHashCode
	 *            -- 用于hash定位数据库的hash值, 从定位的数据库获取数据库连接
	 * @return -- 数据库连接池别名
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	String getWritePoolAlias(long identityHashCode);

	/**
	 * 根据数据库别名获取数据库连接资料(profile)
	 * 
	 * @param alias
	 *            -- 数据库连接池别名
	 * @return -- 数据库连接资料(profile)
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 *            <p>
	 *            for this method can't provide failover mechanism
	 */
	DbConnectProfile getConnectProfile(String alias);

	/**
	 * 获取数据库连接资料(profile)
	 * 
	 * @param rwMode
	 *            -- 数据库读写模式: r: 只读, w:只写, rw:可读写
	 * @param identityHashCode
	 *            -- 用于hash定位数据库的hash值, 从定位的数据库获取数据库连接
	 * @return -- 数据库连接资料(profile)
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	DbConnectProfile getConnectProfile(String rwMode, long identityHashCode);

	/**
	 * 获取数据库连接资料(profile)
	 * <p>
	 * 如果应用数据有分库，可返回多个分库组的写结点或者每个分库组的其中一个随机读结点（获取读结点时，如同一组内读节点都不可用时，
	 * 可failover到写结点）
	 * <p>
	 * 如果应用数据没有分库，那么列表只会有一个返回值
	 * 
	 * @param rwMode
	 *            -- 数据库读写模式: w:只写, r: 只读, rw:可读写
	 *            （r和rw的效果一样，因为只读在failover时也可以返回写结点）
	 * @return -- 数据库连接资料(profile)列表
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	List<DbConnectProfile> getConnectProfiles(String rwMode);

	public void setItemLocateAlgorithm(String itemLocateAlgorithm);

	public void setDbServerMapping(String dbServerMapping);

	public void setProxoolConfig(String proxoolConfig);

	public void initializePlugin();

	public String getItemLocateAlgorithm();

	public String getDbServerMapping();

	public String getProxoolConfig();

	public NodeLocator<DbMasterSlaveGroup> getGroupLocator();

}

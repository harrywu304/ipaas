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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import com.github.ipaas.ifw.jdbc.DbConnectService;

/**
 * @author Chenql
 */
public class DbHealthCheck implements DbHealthCheckMBean {

	public static String TYPE = "DbHealthCheck";

	private DbConnectService dbConnectService;

	/**
	 * 服务Id
	 */
	private String serviceId;

	/**
	 * 连接池别名
	 */
	private String alias = null;

	/**
	 * Master连接池别名
	 */
	private String masterAlias = null;

	/**
	 * IP地址
	 */
	private String ip = null;

	/**
	 * 端口
	 */
	private String port = null;

	/**
	 * 获取连接池别名
	 * 
	 * @return 返回别名
	 */
	@Override
	public String getAlias() {
		return alias;
	}

	/**
	 * 设置连接池别名
	 * 
	 * @param alias
	 *            -- 连接池别名
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}

	/**
	 * @return the masterAlias
	 */
	@Override
	public String getMasterAlias() {
		return masterAlias;
	}

	/**
	 * @param masterAlias
	 *            the masterAlias to set
	 */
	public void setMasterAlias(String masterAlias) {
		this.masterAlias = masterAlias;
	}

	/**
	 * 
	 * @param dbConnectService
	 *            -- 连接服务
	 * @param serviceId
	 *            -- 服务Id
	 * @param alias
	 *            -- 连接池别名
	 * @param masterAlias
	 *            -- 主库连接池别名
	 * @param ip
	 *            -- ip地址
	 * @param port
	 *            -- 端口
	 */
	public DbHealthCheck(DbConnectService dbConnectService, String serviceId, String alias, String masterAlias,
			String ip, String port) {
		super();
		this.serviceId = serviceId;
		this.dbConnectService = dbConnectService;
		this.alias = alias;
		this.masterAlias = masterAlias;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String getTargetIp() {
		return ip;
	}

	@Override
	public String getTargetPort() {
		// TODO Auto-generated method stub
		return port;
	}

	@Override
	public String getState() {
		DbCheckServiceImpl dbCheckSvc = new DbCheckServiceImpl();
		return dbCheckSvc.check(this.dbConnectService, alias);
	}

	/**
	 * 
	 * 比较两个对象是否相等,如果IP地址、端口和appid都相等,表示相同
	 * 
	 * @param o
	 *            -- 比较的对象
	 * 
	 */
	public boolean equals(Object o) {
		boolean res = false;

		if (o != null && DbHealthCheck.class.isAssignableFrom(o.getClass())) {
			DbHealthCheck s = (DbHealthCheck) o;
			res = new EqualsBuilder().append(ip, s.getTargetIp()).append(port, s.getTargetPort()).isEquals();
		}
		return res;
	}

	/**
	 * 
	 * 重写hashcode以比较对象是否相同
	 * 
	 * @return int 哈希码
	 */
	public int hashCode() {
		return new HashCodeBuilder(11, 39).append(alias).append(ip).append(port).toHashCode();
	}

	/**
	 * 冲洗toString方便输出
	 * 
	 * @return String对象的字符串表达
	 * 
	 */
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("ip", ip).append("port", port)
				.toString();
	}

	/*
	 * (non-Javadoc)
	 */
	@Override
	public String getUri() {
		String slash = "/";
		StringBuffer sb = new StringBuffer();
		sb.append(slash).append(serviceId).append(slash).append(alias);
		return sb.toString();
	}

	public DbConnectService getDbConnectService() {
		return dbConnectService;
	}

	public void setDbConnectService(DbConnectService dbConnectService) {
		this.dbConnectService = dbConnectService;
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}
}

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

import com.github.ipaas.ifw.jdbc.DbAccessRequest;

/**
 * 直接数据库访问的请求
 * 
 * @author Chenql
 */
public final class FwDbAccessRequest implements DbAccessRequest {

	private String id = "";

	/**
	 * hash
	 */
	private long hash = 0;

	/**
	 * request timeout, 单位: 毫秒
	 */
	private long timeout = 60000; // default 60s;

	/**
	 * SQL语句
	 */
	private String sql = null;

	public String getId() {
		return this.id;
	}

	public void setId(String reqId) {
		this.id = reqId;
	}

	public long getIdentityHashCode() {
		return this.hash;
	}

	public long getRequestTimeout() {
		return timeout;
	}

	public String getSql() {
		return sql;
	}

	public void setIdentityHashCode(long hashCode) {
		this.hash = hashCode;
	}

	public void setRequestTimeout(long timeout) {
		this.timeout = timeout;
	}

	public void setSql(String sql) {
		this.sql = sql;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(256);
		sb.append("\nFwDirectDbAccessRequest{\n");
		sb.append(" id:").append(id).append(",\n");
		sb.append(" hash:").append(hash).append(",\n");
		sb.append(" sql:").append(sql).append(",\n");
		sb.append(" timeout:").append(timeout).append("ms}");
		return sb.toString();
	}
}

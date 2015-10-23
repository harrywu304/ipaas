package com.github.ipaas.ifw.jdbc.impl;

import java.sql.Connection;

import com.github.ipaas.ifw.jdbc.DbConnectProfile;

/**
 * 数据库连接资料(profile)
 * @author zhandl (yuyoo zhandl@hainan.net)
 * @teme 2010-5-17 下午03:43:37
 */
public final class FwDbConnectProfile implements DbConnectProfile {
	
	private Connection conn = null;
	
	private String poolAlias = null;
	
	private long contextTime = -1;
	
	public FwDbConnectProfile(Connection conn, String alias, long contextTime) {
		
		this.conn = conn;
		this.poolAlias = alias;
		this.contextTime = contextTime;
	}

	public Connection getConnection() { 
		return conn;
	}

	public String getDbPoolAlias() { 
		return poolAlias;
	}

	public long initializedContextTime() { 
		return contextTime;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(128);
		sb.append("FwDbConnectProfile{");
		sb.append(" conn:").append(conn).append(",\n");
		sb.append(" poolAlias:").append(poolAlias).append(",\n");
		sb.append(" contextTime:").append(contextTime);
		sb.append("}");
		return sb.toString();
	}
}

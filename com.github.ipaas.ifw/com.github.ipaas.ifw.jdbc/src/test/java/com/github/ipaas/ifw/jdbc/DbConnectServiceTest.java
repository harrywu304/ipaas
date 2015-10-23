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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ifw.util.CloseUtil;

/**
 * 
 *
 * @author Chenql
 */
public abstract class DbConnectServiceTest {

	public abstract DbConnectService getDbConnectService();

	public abstract String getAppId();

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetConnectProfileString() {
		DbConnectProfile cp = getDbConnectService().getConnectProfile(getAppId() + "_m0_s0");
		assertNotNull(cp);
		Connection conn = cp.getConnection();
		assertNotNull(conn);
		if (conn != null) {
			CloseUtil.closeSilently(conn);
		}
	}

	@Test
	public void testGetConnectProfileStringLong() {
		DbConnectProfile cp = null;
		Connection conn = null;
		// write hash code 0
		cp = getDbConnectService().getConnectProfile("w", 0);
		assertNotNull(cp);
		assertEquals(getAppId() + "_m0", cp.getDbPoolAlias());
		conn = cp.getConnection();
		assertNotNull(conn);
		if (conn != null) {
			CloseUtil.closeSilently(conn);
		}

		// write hash code 1
		cp = getDbConnectService().getConnectProfile("w", 1);
		assertNotNull(cp);
		assertEquals(getAppId() + "_m1", cp.getDbPoolAlias());
		conn = cp.getConnection();
		assertNotNull(conn);
		if (conn != null) {
			CloseUtil.closeSilently(conn);
		}

		// read hash code 0
		cp = getDbConnectService().getConnectProfile("r", 0);
		assertNotNull(cp);
		assertEquals(getAppId() + "_m0_s0", cp.getDbPoolAlias());
		conn = cp.getConnection();
		assertNotNull(conn);
		CloseUtil.closeSilently(conn);

		// read hash code 1
		cp = getDbConnectService().getConnectProfile("r", 1);
		assertNotNull(cp);
		assertEquals(getAppId() + "_m1_s0", cp.getDbPoolAlias());
		conn = cp.getConnection();
		assertNotNull(conn);
		CloseUtil.closeSilently(conn);
	}

	@Test
	public void testGetConnectProfiles() {
		List<DbConnectProfile> cpList = null;
		// get write pools
		cpList = getDbConnectService().getConnectProfiles("w");
		assertEquals(2, cpList.size());
		// get read pools for each group
		cpList = getDbConnectService().getConnectProfiles("r");
		assertEquals(2, cpList.size());
		cpList = getDbConnectService().getConnectProfiles("rw");
		assertEquals(2, cpList.size());
	}

	@Test
	public void testGetWritePoolAlias() {
		String result = getDbConnectService().getWritePoolAlias(0);
		assertEquals(getAppId() + "_m0", result);
		result = getDbConnectService().getWritePoolAlias(1);
		assertEquals(getAppId() + "_m1", result);
	}

}

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
package com.github.ipaas.ifw.jdbc.usability;

import static org.junit.Assert.assertEquals;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.jdbc.DbConnectService;
import com.github.ipaas.ifw.jdbc.impl.FwDirectDbConnectService;
import com.github.ipaas.ifw.jdbc.mbean.DbHealthCheck;

/**
 * @author Chenql
 * 
 */
public class DbHealthCheckTest {
	private static DbHealthCheck dbHealthCheck;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DbConnectService dbConnectService = new FwDirectDbConnectService();
		dbConnectService.setDbServerMapping("fw_demo_m0:fw_demo_m0_s0,fw_demo_m1:fw_demo_m1_s0");
		dbConnectService.setItemLocateAlgorithm("consistent-hash");
		dbConnectService.setProxoolConfig("/config/proxool.xml");
		dbConnectService.initializePlugin();

		dbHealthCheck = new DbHealthCheck(dbConnectService, "fw_demo_fwDirectDbConnectService", "fw_demo_m0",
				"fw_demo_m0", "localhost", "3306");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		dbHealthCheck = null;
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * Test method for
	 */
	@Test
	public void testGetState() {
		dbHealthCheck.getState();
		assertEquals("1", dbHealthCheck.getState());
	}

}

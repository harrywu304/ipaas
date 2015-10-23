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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.github.ipaas.ifw.jdbc.DbConnectService;
import com.github.ipaas.ifw.jdbc.DbConnectServiceTest;
import com.github.ipaas.ifw.jdbc.impl.FwDirectDbConnectService;

/**
 * 
 *
 * @author Chenql
 */
public class FwDirectDbConnectServiceMssqlTest extends DbConnectServiceTest {

	private static DbConnectService ddbcs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 赋值插件初始化参数
		DbConnectService dbConnectService = new FwDirectDbConnectService();
		dbConnectService.setDbServerMapping("fw_demo_m0:fw_demo_m0_s0,fw_demo_m1:fw_demo_m1_s0");
		dbConnectService.setItemLocateAlgorithm("consistent-hash");
		dbConnectService.setProxoolConfig("/config/proxool.xml");
		dbConnectService.initializePlugin();

		ddbcs = dbConnectService;
	}

	public static void main(String[] args) throws Exception {
		setUpBeforeClass();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Override
	public DbConnectService getDbConnectService() {
		// TODO Auto-generated method stub
		return ddbcs;
	}

	@Override
	public String getAppId() {
		// TODO Auto-generated method stub
		return "fw_demo";
	}

}

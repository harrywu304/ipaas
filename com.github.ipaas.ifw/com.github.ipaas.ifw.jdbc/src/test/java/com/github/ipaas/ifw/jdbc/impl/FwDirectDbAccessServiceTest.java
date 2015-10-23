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

import org.junit.Before;
import org.junit.BeforeClass;

import com.github.ipaas.ifw.jdbc.DbAccessService;
import com.github.ipaas.ifw.jdbc.DbAccessServiceTest;
import com.github.ipaas.ifw.jdbc.DbConnectService;

/**
 * @author Chenql
 */
public class FwDirectDbAccessServiceTest extends DbAccessServiceTest {

	private static DbAccessService dbs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		// 实例化插件
		String scaleoutMapping = "fw_demo_m1:fw_demo_m0";
		dbs = new FwDirectDbAccessService();
		dbs.setScaleoutMapping(scaleoutMapping);
		DbConnectService dbConnectService = new FwDirectDbConnectService();
		dbConnectService.setDbServerMapping("fw_demo_m0:fw_demo_m0_s0");
		dbConnectService.setItemLocateAlgorithm("consistent-hash");
		dbConnectService.setProxoolConfig("/config/proxool.xml");
		dbConnectService.initializePlugin();

		dbs.setDbConnectService(dbConnectService);
		dbs.initializePlugin();
	}

	public static void main(String[] args) {
		try {
			setUpBeforeClass();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

		super.setUp();
	}

	@Override
	public DbConnectService getDbConnectService() {
		// TODO Auto-generated method stub
		return dbs.getDbConnectService();
	}

	@Override
	public DbAccessService getDbAccessService() {
		// TODO Auto-generated method stub
		return dbs;
	}

	@Override
	public String getAppID() {
		// TODO Auto-generated method stub
		return "fw_demo";
	}

}

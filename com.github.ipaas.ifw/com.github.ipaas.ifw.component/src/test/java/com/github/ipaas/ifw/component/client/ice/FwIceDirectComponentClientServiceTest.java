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
package com.github.ipaas.ifw.component.client.ice;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.github.ipaas.ifw.component.ComponentClientServiceTest;

/**
 * @author Chenql
 * 
 */
public class FwIceDirectComponentClientServiceTest extends
		ComponentClientServiceTest {

	private static FwIceDirectComponentClientService dccs;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// 实例化插件
		dccs = new FwIceDirectComponentClientService();
		dccs.setServerUrl("192.168.71.205:15212");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		ccs = dccs;
	}

	@After
	public void tearDown() throws Exception {
	}

}

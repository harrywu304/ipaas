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
package com.github.ipaas.ifw.component.client.ice.usability;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Chenql
 */
public class IceHealthCheckTest {
	private static IceHealthCheck iceHealthCheck;

	/**
	 * 方法的描述
	 * 
	 * @param 参数的描述
	 * @return 返回类型的描述
	 * @exception 异常信息的描述
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		iceHealthCheck = new IceHealthCheck("fw_demo", "192.168.71.205",
				"15212");
	}

	/**
	 * 方法的描述
	 * 
	 * @param 参数的描述
	 * @return 返回类型的描述
	 * @exception 异常信息的描述
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		iceHealthCheck = null;
	}

	/**
	 * 方法的描述
	 * 
	 * @param 参数的描述
	 * @return 返回类型的描述
	 * @exception 异常信息的描述
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * 方法的描述
	 * 
	 * @param 参数的描述
	 * @return 返回类型的描述
	 * @exception 异常信息的描述
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetLightState() {
		assertEquals("1", iceHealthCheck.getLightState());
	}

	@Test
	public void testGetHeavyState() {
		iceHealthCheck.getHeavyState();
		assertEquals("1", iceHealthCheck.getHeavyState());
	}

}

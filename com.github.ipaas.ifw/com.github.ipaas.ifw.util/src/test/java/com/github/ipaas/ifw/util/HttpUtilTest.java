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
package com.github.ipaas.ifw.util;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.util.HttpUtil;

/**
 * 
 * @author Chenql
 */
public class HttpUtilTest {
	
	private static String url = "http://localhost:8080/TestWebStuff/DemoServlet";

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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

	@Test
	public void testPost() {
		Map params = new HashMap();
		params.put("name", "中文");
		String result = HttpUtil.post(url,params,5000,5000,"utf-8");
		assertEquals("doPost,中文",result);
	}
	
	@Test
	public void testGet() {
		Map params = new HashMap();
		params.put("name", "中文");
		String result = HttpUtil.get(url,params,5000,5000,"utf-8");
		assertEquals("doGet,中文",result);
		result = HttpUtil.get(url+"?name=中文",null,5000,5000,"utf-8");
		assertEquals("doGet,中文",result);
	}
	
	@Test
	public void testGet404() {
		String result = HttpUtil.get(url+"404",null,5000,5000,"utf-8");
		assertNull(result);
	}
	
	@Test
	public void testGetReadTimeout() {
		Map params = new HashMap();
		params.put("slowly", "xxx");
		String result = HttpUtil.get(url,params,5000,1000,"utf-8");
		assertNull(result);
	}

	@Test
	public void testPut() {
		Map params = new HashMap();
		params.put("name", "中文");
		String result = HttpUtil.put(url,params,5000,5000,"utf-8");
		assertEquals("doPut,中文",result);
	}

	@Test
	public void testDelete() {
		Map params = new HashMap();
		params.put("name", "中文");
		String result = HttpUtil.delete(url,params,5000,5000,"utf-8");
		assertEquals("doDelete,中文",result);
	}

	@Test
	public void testHead() {
		Map params = new HashMap();
		params.put("name", "中文");
		String result = HttpUtil.head(url,params,5000,5000,"utf-8");
		assertEquals("",result);
	}

}

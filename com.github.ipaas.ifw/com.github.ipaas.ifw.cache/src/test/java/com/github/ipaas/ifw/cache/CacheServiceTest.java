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
package com.github.ipaas.ifw.cache;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author whx
 *
 */
public class CacheServiceTest {

	protected CacheService cs;
	protected String key = "key";

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// delete key1
		cs.delete(key);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.CacheService#set(java.lang.String, java.lang.Object)}
	 * .
	 */
	@Test
	public void testSet() {
		boolean result = cs.set(key, "value1");
		assertTrue(result);

	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.CacheService#get(java.lang.String)}.
	 */
	@Test
	public void testGetString() {
		// set key1
		cs.set(key, "value1");
		String result = (String) cs.get(key);
		assertEquals("value1", result);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.CacheService#get(java.lang.String, java.util.concurrent.Callable)}
	 * .
	 */
	@Test
	public void testGetStringCallable() {
		Callable refreshSource = new Callable() {
			public Object call() throws Exception {
				return "value1";
			}
		};
		String result = (String) cs.get(key, refreshSource);
		assertEquals("value1", result);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.CacheService#delete(java.lang.String)}.
	 */
	@Test
	public void testDelete() {
		// 设置初始值
		cs.set(key, "value1");
		boolean result = cs.delete(key);
		assertTrue(true);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.CacheService#getAndSet(java.lang.String, java.lang.Object)}
	 * .
	 */
	@Test
	public void testGetAndSet() {
		// 设置初始值
		cs.set(key, "value1");
		String result = (String) cs.getAndSet(key, "value2");
		// 校验返回值
		assertEquals("value1", result);
		// 检验缓存新值
		result = (String) cs.get(key);
		assertEquals("value2", result);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.CacheService#compareAndSet(java.lang.String, java.lang.Object, java.lang.Object)}
	 * .
	 */
	@Test
	public void testCompareAndSet() {
		cs.set(key, "value1");
		boolean result = false;
		String resultStr = null;
		// 测试用case1：旧值未变更，用新值替换旧值
		result = cs.compareAndSet(key, "value1", "value2");
		assertTrue(result);
		resultStr = (String) cs.get(key);
		assertEquals("value2", resultStr);
		// 测试用case1：旧值已变更，不改变新值
		result = cs.compareAndSet(key, "value3", "value4");
		assertFalse(result);
		resultStr = (String) cs.get(key);
		assertEquals("value2", resultStr);
	}

}

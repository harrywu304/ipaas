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
import static org.junit.Assert.assertNull;
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
public class LocalCacheServiceTest extends CacheServiceTest {

	protected LocalCacheService lcs;

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
		cs = lcs;
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testSetStringObjectLong() {
		lcs.set(key, "value1", 1000L);
		String result = null;
		result = (String) lcs.get(key);
		assertEquals("value1", result);
		try {
			Thread.currentThread().sleep(1500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = (String) lcs.get(key);
		assertEquals(null, result);
	}

	@Test
	public void testGetStringCallableLong() {
		Callable refreshSource = new Callable() {
			public Object call() throws Exception {
				return "value1";
			}
		};
		String result = (String) lcs.get(key, refreshSource, 1000L);
		assertEquals("value1", result);
		try {
			Thread.currentThread().sleep(1500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = (String) lcs.get(key);
		assertEquals(null, result);
	}

	@Test(expected = Exception.class)
	public void testGetStringCallableException() {
		Callable refreshSource = new Callable() {
			public Object call() throws Exception {
				throw new Exception("refreshSource exception");
			}
		};
		String result = (String) lcs.get(key, refreshSource, 1000L);
		assertEquals(null, result);
	}

	@Test
	public void testClearAll() {
		lcs.set(key, "value1");
		lcs.clearAll();
		assertNull(lcs.get(key));
	}

	@Test
	public void testSize() {
		assertEquals(0, lcs.size());
		lcs.set(key, "value1");
		assertEquals(1, lcs.size());
	}
	@Test
	public void testSetNull() {
		boolean result = cs.set(key, null);
		assertTrue(result);

	}

}

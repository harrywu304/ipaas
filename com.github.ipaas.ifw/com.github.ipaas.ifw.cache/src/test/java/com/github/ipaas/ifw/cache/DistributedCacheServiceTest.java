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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author whx
 *
 */
public class DistributedCacheServiceTest extends CacheServiceTest {

	protected DistributedCacheService dcs;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cs = dcs;
		super.setUp();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.DistributedCacheService#set(java.lang.String, java.lang.Object, long)}
	 * .
	 */
	@Test
	public void testSetStringObjectLong() {
		dcs.set(key, "value1", 1000L);
		String result = null;
		result = (String) dcs.get(key);
		assertEquals("value1", result);
		try {
			Thread.currentThread().sleep(1500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = (String) dcs.get(key);
		assertEquals(null, result);
	}

	@Test
	public void testSetStringObjectTooLong() {
		String key = "testSetStringObjectTooLong";
		dcs.delete(key);
		// dcs.set(key, "value1", 31 * 24 * 3600 * 1000L);
		dcs.set(key, "value1", 31 * 24 * 3600 * 1000L);
		String result = null;
		result = (String) dcs.get(key);
		assertEquals("value1", result);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.DistributedCacheService#get(java.lang.String, java.util.concurrent.Callable, long)}
	 * .
	 */
	@Test
	public void testGetStringCallableLong() {
		Callable refreshSource = new Callable() {
			public Object call() throws Exception {
				return "value1";
			}
		};
		String result = (String) dcs.get(key, refreshSource, 1000L);
		assertEquals("value1", result);
		try {
			Thread.currentThread().sleep(1500L);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		result = (String) dcs.get(key);
		assertEquals(null, result);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.LocalCacheService#get(java.lang.String, java.util.concurrent.Callable, long)}
	 * .
	 */
	@Test(expected = Exception.class)
	public void testGetStringCallableException() {
		Callable refreshSource = new Callable() {
			public Object call() throws Exception {
				throw new Exception("refreshSource exception");
			}
		};
		String result = (String) dcs.get(key, refreshSource, 1000L);
		assertEquals(null, result);
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.cache.DistributedCacheService#getMulti(java.util.Collection)}
	 * .
	 */
	@Test
	public void testGetMulti() {
		dcs.set(key, "value1");
		dcs.set(key + "2", "value2");
		Set<String> keys = new HashSet<String>();
		keys.add(key);
		keys.add(key + "2");
		Map<String, Object> results = dcs.getMulti(keys);
		assertEquals("value1", (String) results.get(key));
		assertEquals("value2", (String) results.get(key + "2"));
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.core.cache.CacheService#set(java.lang.String, java.lang.Object)}
	 * .
	 */
	@Test
	public void testSetNull() {
		boolean result = cs.set(key, null);
		assertFalse(result);

	}
}

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
package com.github.ipaas.ifw.core.locate;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.core.locate.HashAlgorithms;
import com.github.ipaas.ifw.core.locate.NodeLocator;
import com.github.ipaas.ifw.core.locate.NodeLocators;

/**
 * @author whx
 *
 */
public class ModLocatorTest {

	private NodeLocator<String> locator;

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
		String mapping = "fw_demo_m0,fw_demo_m1,fw_demo_m2,fw_demo_m3";
		String[] items = mapping.trim().split(",");
		locator = NodeLocators.newModLocator(HashAlgorithms.DJB_HASH, Arrays.asList(items));
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLocateLong() {
		String node = "";

		node = locator.locate(0L, NodeLocator.NULL_STRATEGY);
		assertEquals("fw_demo_m0", node);

		node = locator.locate(1L, NodeLocator.NULL_STRATEGY);
		assertEquals("fw_demo_m1", node);

		node = locator.locate(2L, NodeLocator.NULL_STRATEGY);
		assertEquals("fw_demo_m2", node);

		node = locator.locate(3L, NodeLocator.NULL_STRATEGY);
		assertEquals("fw_demo_m3", node);
	}
}

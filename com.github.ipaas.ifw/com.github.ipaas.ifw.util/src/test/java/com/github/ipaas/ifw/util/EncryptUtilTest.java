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

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.util.EncryptUtil;
/**
 * 
 * @author Chenql
 */
public class EncryptUtilTest {

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
	public void testEncryptMD5() {
		assertEquals("900150983cd24fb0d6963f7d28e17f72",EncryptUtil.encryptMD5("abc","utf8"));
		assertEquals("9ff5718cf6761654743d8b7e89f35cc8",EncryptUtil.encryptMD5("我很好","utf8"));
		assertEquals("68aeee40973d0fe843c47efc506114a4",EncryptUtil.encryptMD5("我很好","gbk"));
		assertEquals("b29e4afd6b3c6b5ffafc7a8fb0f958df",EncryptUtil.encryptMD5("你们，真是...nice","utf8"));
	}

}

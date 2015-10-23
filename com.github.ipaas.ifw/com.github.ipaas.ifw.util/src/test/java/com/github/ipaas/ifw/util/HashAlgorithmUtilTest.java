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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.github.ipaas.ifw.util.HashAlgorithmUtil;

/**
 * 
 * @author Chenql
 */
public class HashAlgorithmUtilTest {

	@Test
	public void type() throws Exception {

		assertThat(HashAlgorithmUtil.class, notNullValue());
	}

	@Test
	public void getDJBHashTest_A$String() throws Exception {
		assertEquals(7158330374349376324L, HashAlgorithmUtil.getDJBHash("Test Hash Code"));
		assertEquals(3001456041019677636L, HashAlgorithmUtil.getDJBHash("测试 Hash Code"));
		assertEquals(5381L, HashAlgorithmUtil.getDJBHash(""));
	}

	@Test
	public void getKemataHashTest_A$String() throws Exception {
		assertEquals(-8646359725060059141L, HashAlgorithmUtil.getKemataHash("Test Hash Code"));
		assertEquals(-8281858775405009090L, HashAlgorithmUtil.getKemataHash("测试 Hash Code"));
		assertEquals(9098107892288553193L, HashAlgorithmUtil.getKemataHash(""));
	}

	@Test
	public void getBKDRHashTest_A$String() throws Exception {
		assertEquals(3521845580675462997L, HashAlgorithmUtil.getBKDRHash("Test Hash Code"));
		assertEquals(-5049456559024570411L, HashAlgorithmUtil.getBKDRHash("测试 Hash Code"));
		assertEquals(0L, HashAlgorithmUtil.getBKDRHash(""));
	}

	@Test(expected = NullPointerException.class)
	public void getDJBHashTest_A$NULL() {
		HashAlgorithmUtil.getDJBHash(null);
	}

	@Test(expected = NullPointerException.class)
	public void getKemataHashTest_A$NULL() {
		HashAlgorithmUtil.getKemataHash(null);
	}

	@Test(expected = NullPointerException.class)
	public void getBKDRHashTest_A$NULL() {
		HashAlgorithmUtil.getBKDRHash(null);
	}

	@Test(expected = RuntimeException.class)
	public void getKemataHashTest_R$Runtime() {
		HashAlgorithmUtil.getKemataHash(null);
	}
}

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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.math.BigInteger;

import org.junit.Test;

import com.github.ipaas.ifw.util.ConvertUtil;

/**
 * 
 * @author Chenql
 */
public class ConvertUtilTest {

	@Test
	public void type() throws Exception {
		assertThat(ConvertUtil.class, notNullValue());
	}

	@Test
	public void instantiation() throws Exception {
		ConvertUtil target = new ConvertUtil();
		assertThat(target, notNullValue());
	}

	@Test
	public void toLongTest() throws Exception {
		assertEquals(0L, ConvertUtil.toLong(null));
		assertEquals(0L, ConvertUtil.toLong("test"));
		assertEquals(1L, ConvertUtil.toLong(new Short((short) 1)));
		assertEquals(1L, ConvertUtil.toLong(new Integer(1)));
		assertEquals(122778600L, ConvertUtil.toLong(new Long(122778600)));
		assertEquals(122778600L, ConvertUtil.toLong(new Double(122778600)));
		assertEquals(122778600L, ConvertUtil.toLong(BigInteger.valueOf(122778600)));
	}

	@Test
	public void toIntTest() throws Exception {
		assertEquals(0, ConvertUtil.toInt(null));
		assertEquals(0, ConvertUtil.toInt("test"));
		assertEquals(1, ConvertUtil.toInt(new Short((short) 1)));
		assertEquals(1, ConvertUtil.toInt(new Integer(1)));
		assertEquals(1, ConvertUtil.toInt(new Long(1)));
		assertEquals(1, ConvertUtil.toInt(new Double(1)));
		assertEquals(1, ConvertUtil.toInt(BigInteger.valueOf(1)));
	}

	@Test
	public void toFloatTest() throws Exception {
		assertEquals(0, Float.compare(0F, ConvertUtil.toFloat(null)));
		assertEquals(0, Float.compare(0F, ConvertUtil.toFloat("test")));
		assertEquals(0, Float.compare(1F, ConvertUtil.toFloat(new Short((short) 1))));
		assertEquals(0, Float.compare(1F, ConvertUtil.toFloat(new Integer(1))));
		assertEquals(0, Float.compare(122778600F, ConvertUtil.toFloat(new Long(122778600))));
		assertEquals(0, Float.compare(122778600F, ConvertUtil.toFloat(new Double(122778600))));
		assertEquals(0, Float.compare(122778600F, ConvertUtil.toFloat(BigInteger.valueOf(122778600))));
	}

	@Test
	public void toShortTest() throws Exception {
		assertEquals(0, ConvertUtil.toShort(null));
		assertEquals(0, ConvertUtil.toShort("test"));
		assertEquals(1, ConvertUtil.toShort(new Short((short) 1)));
		assertEquals(1, ConvertUtil.toShort(new Integer(1)));
		assertEquals(1, ConvertUtil.toShort(new Long(1)));
		assertEquals(1, ConvertUtil.toShort(new Double(1)));
		assertEquals(1, ConvertUtil.toShort(BigInteger.valueOf(1)));
	}

	@Test
	public void toDoubleTest() throws Exception {
		assertEquals(0, Double.compare(0D, ConvertUtil.toDouble(null)));
		assertEquals(0, Double.compare(0D, ConvertUtil.toDouble("test")));
		assertEquals(0, Double.compare(1D, ConvertUtil.toDouble(new Short((short) 1))));
		assertEquals(0, Double.compare(1D, ConvertUtil.toDouble(new Integer(1))));
		assertEquals(0, Double.compare(122778600D, ConvertUtil.toDouble(new Long(122778600))));
		assertEquals(0, Double.compare(122778600D, ConvertUtil.toDouble(new Double(122778600))));
		assertEquals(0, Double.compare(122778600D, ConvertUtil.toDouble(BigInteger.valueOf(122778600))));
	}

	@Test
	public void toBooleanTest() throws Exception {
		assertFalse(ConvertUtil.toBoolean(null));
		assertFalse(ConvertUtil.toBoolean("false"));
		assertTrue(ConvertUtil.toBoolean("true"));
		assertTrue(ConvertUtil.toBoolean("test"));
		assertFalse(ConvertUtil.toBoolean("0"));
		assertTrue(ConvertUtil.toBoolean("1"));
		assertTrue(ConvertUtil.toBoolean("25536"));
		assertFalse(ConvertUtil.toBoolean(new Boolean(false)));
		assertTrue(ConvertUtil.toBoolean(new Boolean(true)));
	}
}

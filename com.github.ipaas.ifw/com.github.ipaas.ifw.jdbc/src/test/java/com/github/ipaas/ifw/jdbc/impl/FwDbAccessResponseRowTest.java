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

package com.github.ipaas.ifw.jdbc.impl;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.jdbc.impl.FwDbAccessResponseRow;

/**
 * by whx
 * 
 * @author harry
 *
 */
public class FwDbAccessResponseRowTest {

	@BeforeClass
	public static void setUpOnce() {
	}

	@AfterClass
	public static void tearDownOnce() {
	}

	@Test
	public void testGetDateStringString() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "birth");
		// test normal string convert
		row.put("birth", "1981-05-07");
		assertEquals(new Date(81, 4, 7), rprow.getDate("birth", "yyyy-MM-dd"));
		// test normal date
		row.put("birth", new Date(81, 4, 7));
		assertEquals(new Date(81, 4, 7), rprow.getDate("birth", ""));
		// test null
		row.put("birth", null);
		assertEquals(null, rprow.getDate("birth", "yyyy-MM-dd"));

	}

	@Test(expected = IllegalStateException.class)
	public void testGetDateStringStringException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		// test no exist
		rprow.getDate("notexist", "yyyy-MM-dd");
	}

	@Test
	public void testGetDateIntString() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		meta.put(Integer.valueOf(1), "birth");
		Map<String, Object> row = new HashMap<String, Object>();
		row.put("birth", "1981-05-07");
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		assertEquals(new Date(81, 4, 7), rprow.getDate(1, "yyyy-MM-dd"));
		row.put("birth", new Date(81, 4, 7));
		assertEquals(new Date(81, 4, 7), rprow.getDate(1, ""));
		row.put("birth", null);
		assertEquals(null, rprow.getDate(1, "yyyy-MM-dd"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetDateIntStringException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "birth");
		row.put("birth", "1981-05-07");
		rprow.getDate(2, "yyyy-MM-dd");
	}

	@Test
	public void testGetIntString() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		meta.put(Integer.valueOf(1), "age");
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		// test normal
		row.put("age", 30);
		assertEquals(30, rprow.getInt("age"));
		// test string convert
		row.put("age", "30");
		assertEquals(30, rprow.getInt("age"));
		// test null
		row.put("age", null);
		assertEquals(0, rprow.getInt("age"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetIntStringException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "age");
		row.put("age", 30);
		// test no exist
		rprow.getInt("notexist");
	}

	@Test
	public void testGetIntInt() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		meta.put(Integer.valueOf(1), "age");
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		// test normal
		row.put("age", 30);
		assertEquals(30, rprow.getInt(1));
		// test string convert
		row.put("age", "30");
		assertEquals(30, rprow.getInt(1));
		// test null
		row.put("age", null);
		assertEquals(0, rprow.getInt(1));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetIntIntException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "age");
		row.put("age", 30);
		// test no exist
		rprow.getInt(2);
	}

	@Test
	public void testGetStringString() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "name");
		// test normal
		row.put("name", "harry");
		assertEquals("harry", rprow.getString("name"));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetStringStringException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "name");
		// test normal
		row.put("name", "harry");
		rprow.getString("notexist");
	}

	@Test
	public void testGetStringInt() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "name");
		// test normal
		row.put("name", "harry");
		assertEquals("harry", rprow.getString(1));
	}

	@Test(expected = IllegalStateException.class)
	public void testGetStringIntException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "name");
		// test normal
		row.put("name", "harry");
		rprow.getString(2);
	}

	@Test
	public void testGetFloatString() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "cost");
		// test normal
		row.put("cost", 15.6f);
		assertEquals(15.6f, rprow.getFloat("cost"), 0.1);
		// test string convert
		row.put("cost", "15.6");
		assertEquals(15.6f, rprow.getFloat("cost"), 0.1);
		// test null
		row.put("cost", null);
		assertEquals(0f, rprow.getFloat("cost"), 0.1);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetFloatStringException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		meta.put(Integer.valueOf(1), "age");
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		row.put("age", 30);
		rprow.getFloat("notexist");
	}

	@Test
	public void testGetFloatInt() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		meta.put(Integer.valueOf(1), "cost");
		// test normal
		row.put("cost", 15.6f);
		assertEquals(15.6f, rprow.getFloat(1), 0.1);
		// test string convert
		row.put("cost", "15.6");
		assertEquals(15.6f, rprow.getFloat(1), 0.1);
		// test null
		row.put("cost", null);
		assertEquals(0f, rprow.getFloat(1), 0.1);
	}

	@Test(expected = IllegalStateException.class)
	public void testGetFloatIntException() {
		Map<Integer, String> meta = new HashMap<Integer, String>();
		meta.put(Integer.valueOf(1), "age");
		Map<String, Object> row = new HashMap<String, Object>();
		FwDbAccessResponseRow rprow = new FwDbAccessResponseRow(row, meta);
		row.put("age", 30);
		rprow.getFloat(2);
	}

}

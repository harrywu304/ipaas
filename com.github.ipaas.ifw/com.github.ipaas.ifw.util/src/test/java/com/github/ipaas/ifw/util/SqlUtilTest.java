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

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ifw.util.SqlUtil;

/**
 * 
 * @author Chenql
 */
public class SqlUtilTest {
	private String tableName = "aTable";
	private String expectSelectSql = "\n select * from " + tableName + "\n where name='hutianfa'"
			+ "\n   and sex='male'\n   and age=3";
	private String expectInsertSql = "\n insert into " + tableName + " ( name,sex,age )\n"
			+ " values ( 'hutianfa','male',3 )";
	private Map<String, Object> columns = new LinkedHashMap<String, Object>();

	@Before
	public void setUp() {
		columns.put("name", "hutianfa");
		columns.put("sex", "male");
		columns.put("age", 3);
	}

	@Test
	public final void testSqlValueInt() {
		assertEquals("3", SqlUtil.sqlValue(3));
	}

	@Test
	public final void testSqlValueLong() {
		long lg = 2147483600L;
		assertEquals("2147483600", SqlUtil.sqlValue(lg));
	}

	@Test
	public final void testSqlValueShort() {
		short s = -32767;
		assertEquals("-32767", SqlUtil.sqlValue(s));
	}

	@Test
	public final void testSqlValueFloat() {
		float f = -0.12f;
		assertEquals("-0.12", SqlUtil.sqlValue(f));
	}

	@Test
	public final void testSqlValueDouble() {
		double d = -0.00000000000000000000000000000000012;
		assertEquals("-1.2E-34", SqlUtil.sqlValue(d));
	}

	/**
	 * 重点测试消除SQL注入的逻辑处理
	 */
	@Test
	public final void testSqlValueString() {
		String value = "test";
		assertEquals("'test'", SqlUtil.sqlValue(value));
		value = "test's";
		assertEquals("'test''s'", SqlUtil.sqlValue(value));
		value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = "test\\s";
		assertEquals("'test\\\\s'", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueObjectArray() {
		DemoUser aUser = new DemoUser("hutianfa", "guangzhou", "content_guangzhou");
		aUser.setId(1);
		Object[] value = new Object[] { aUser };
		assertEquals("'1,hutianfa,guangzhou,content_guangzhou,null,null'", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueStringArray() {
		String[] value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new String[] { "ipaas", "ifw" };
		assertEquals("'ipaas','ifw'", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueListOfObject() {
		List<Object> value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new ArrayList<Object>();
		value.add("ipaas");
		value.add("ifw");
		assertEquals("'ipaas','ifw'", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueIntArray() {
		int[] value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new int[] { 1, -2, 0 };
		assertEquals("1,-2,0", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueShortArray() {
		short[] value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new short[] { 1, -2, 0 };
		assertEquals("1,-2,0", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueLongArray() {
		long[] value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new long[] { 1000000000, -2000000000, 0 };
		assertEquals("1000000000,-2000000000,0", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueFloatArray() {
		float[] value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new float[] { 1000000000.0f, -2000000000.0f, 0f };
		assertEquals("1.0E9,-2.0E9,0.0", SqlUtil.sqlValue(value));
	}

	@Test
	public final void testSqlValueDoubleArray() {
		double[] value = null;
		assertEquals("null", SqlUtil.sqlValue(value));
		value = new double[] { 100000000000000.0d, -200000000000000.0d, 0d };
		assertEquals("1.0E14,-2.0E14,0.0", SqlUtil.sqlValue(value));
	}

	@Test
	public void testGetSqlStringObject() {
		assertEquals("select * from tba", SqlUtil.getSql("select * from tba"));
		assertEquals("select * from tba where id = 'abc'", SqlUtil.getSql("select * from tba where id = ?", "abc"));
		assertEquals("select * from tba where id = 199", SqlUtil.getSql("select * from tba where id = ?", 199));
		assertEquals("select * from tba where id in (100,101,102)",
				SqlUtil.getSql("select * from tba where id in (?)", new int[] { 100, 101, 102 }));
		List list = new ArrayList();
		list.add("harry");
		list.add("demo");
		assertEquals("select * from tba where name in ('harry','demo')",
				SqlUtil.getSql("select * from tba where name in (?)", list));
		assertEquals("select * from tba where level = 2 and score > 500 and sex = 'man'",
				SqlUtil.getSql("select * from tba where level = ? and score > ? and sex = ?", 2, 500, "man"));
		// 注意不能用一个?标识，传入String数值
		// assertEquals("select * from tba where name in ('harry','demo')",SqlUtil.getSql("select * from tba where name in (?)",
		// new String[]{"harry","demo"}));
	}

	@Test(expected = java.lang.ArrayIndexOutOfBoundsException.class)
	public void testGetSqlException() {
		assertEquals("select * from tba where level = 2 and score > 500 and sex = 'man'",
				SqlUtil.getSql("select * from tba where level = ? and score > ? and sex = ?", 2, 500));
	}

	@Test
	public void testGetSqlStringList() {
		List<Object> params = new ArrayList();
		params.add(new int[] { 1, 2, 3 });
		params.add("guangzhou");
		params.add(new String[] { "harry", "demo", "rick" });
		assertEquals(
				"select * from tba where level in (1,2,3) and city = 'guangzhou' and name in ('harry','demo','rick')",
				SqlUtil.getSqlByList("select * from tba where level in (?) and city = ? and name in (?)", params));
	}

	/**
	 * sqlValueDate
	 */
	@Test
	public final void testSqlValueDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date d = null;
		try {
			d = sdf.parse("2011-01-01 11:11:11");
		} catch (ParseException e) {
			e.printStackTrace();
		}

		assertEquals("'2011-01-01 11:11:11'", SqlUtil.sqlValue(d));

		SimpleDateFormat sdfShort = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		assertEquals("'2011-01-01 11:11'", SqlUtil.sqlValue(d, sdfShort));
		// d = null;
		// assertEquals(null,SqlUtil.sqlValue(d));
	}
}

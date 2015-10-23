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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.github.ipaas.ifw.util.BeanUtil;
 

/**
 * 
 * @author Chenql
 */
public class BeanUtilTest {
	private DemoUser aUser = new DemoUser("hutianfa", "guangzhou", "content_guangzhou");

	@Test
	public final void wrapToListString() {
		String str = "test_for_wrapToList";
		List<String> strList = BeanUtil.wrapToList(str);
		assertTrue(strList instanceof List);
		assertEquals(1, strList.size());
		assertEquals(str, strList.get(0));
	}
	
	@Test
	public final void wrapToListInteger() {
		Integer aInteger = new Integer(5);
		List<Integer> iList = BeanUtil.wrapToList(aInteger);
		assertTrue(iList instanceof List);
		assertEquals(1, iList.size());
		assertEquals(aInteger, iList.get(0));
	}
	
	@Test
	public final void wrapToListList(){
		List<Integer> aList = new ArrayList<Integer>();
		aList.add(1);
		aList.add(2);
		List<Integer> accList = BeanUtil.wrapToList(aList);
		assertEquals(2, accList.size());
		assertEquals(new Integer(1), accList.get(0));
		assertEquals(new Integer(2), accList.get(1));
		
	}

	@Test
	public final void wrapToMapNotMapBean() {
		String str = "notMapBean";
		Map<Integer, String> accMap = BeanUtil.wrapToMap(str);
		assertEquals(0, accMap.size());
	}
	
	@Test
	public final void warpToMapMapBean(){
		Map<Integer, String> expMap = new HashMap<Integer, String>();
		expMap.put(1, "hello");
		expMap.put(2, "world");
		Map<Integer, String> accMap = BeanUtil.wrapToMap(expMap);
		int length = expMap.size();
		assertEquals(length, accMap.size());
		for(Integer key : expMap.keySet()){
			assertEquals(expMap.get(key), accMap.get(key));
		}
	}

	@Test
	public final void testToIntObjectInt() {
		StubBean bean = new StubBean(3, "wang");
		int accInt = BeanUtil.toInt(bean, -1);
		assertEquals(bean.getId(), accInt);
	}
	
	private class StubBean{
		private int id;
		
		public StubBean(int id, String name){
			this.id = id;
		}
		
		public int getId(){
			return id;
		}
		
		public String toString(){
			return String.valueOf(id);
		}
	}

	/**
	 * TODO:Integer.parseInt(bean.toString())没有try catch，
	 * 出现异常的话，并不会返回defaultValue
	 */
	@Test
	public final void testToIntStringInt() {
		String bean = null;
		int defaultValue = 168;
		assertEquals(defaultValue, BeanUtil.toInt(bean, defaultValue));
		bean = "968";
		assertEquals(968, BeanUtil.toInt(bean, defaultValue));
		bean = "afatest";
		assertEquals(defaultValue, BeanUtil.toInt(bean, defaultValue));
	}

	@Test
	public final void testToIntIntegerInt() {
		Integer bean = null;
		int defaultValue = 168;
		assertEquals(defaultValue, BeanUtil.toInt(bean, defaultValue));
		bean = new Integer(33344444);
		assertEquals(33344444, BeanUtil.toInt(bean, defaultValue));
	}

	/**
	 * TODO:被测试代码逻辑上有问题
	 * 
	 */
	@Test
	public final void testGetKeyByValue() {
		//一定要有序的，才能控制出错的出现时机
		Map<Object, Object> map = new LinkedHashMap<Object, Object>(); 
		map.put("null", null);
		map.put("any", "justAny");
		map.put("ipaas", "fw");
		DemoUser bUser = new DemoUser("afa", "beijing", "content_beijing");
		map.put(bUser, aUser);
		assertEquals("null", BeanUtil.getKeyByValue(null, map));	
		assertEquals(null, BeanUtil.getKeyByValue("noExit", map));	
		assertEquals("ipaas", BeanUtil.getKeyByValue("fw", map));
		assertSame(bUser, BeanUtil.getKeyByValue(aUser, map));
	}

	@Test
	public final void testToNullableString() {
		DemoUser bUser = null;
		assertEquals(null, BeanUtil.toNullableString(bUser));
		
		assertEquals(aUser.toString(), BeanUtil.toNullableString(aUser));
	}

	@Test
	public final void testToNotNullString() {
		DemoUser bUser = null;
		assertEquals("", BeanUtil.toNotNullString(bUser));		
		assertEquals(aUser.toString(), BeanUtil.toNotNullString(aUser));
	}

	@Test
	public final void testIsNullOrEmpty() {
		String str = null;
		assertTrue(BeanUtil.isNullOrEmpty(str));
		str = "";
		assertTrue(BeanUtil.isNullOrEmpty(str));
		str = "a";
		assertFalse(BeanUtil.isNullOrEmpty(str));
	}

	@Test
	public final void testIsNotNullAndEmpty() {
		String str = null;
		assertFalse(BeanUtil.isNotNullAndEmpty(str));
		str = "";
		assertFalse(BeanUtil.isNotNullAndEmpty(str));
		str = "a";
		assertTrue(BeanUtil.isNotNullAndEmpty(str));
	}

}

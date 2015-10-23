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
import static org.junit.Assert.assertNotNull;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.ipaas.ifw.jdbc.BeanHandler;
import com.github.ipaas.ifw.jdbc.DbAccessResponseRow;
import com.github.ipaas.ifw.util.DateUtil;


/**
 * 
 * @author whx
 *
 */
public class FwDbAccessResponseTest {
	
	private static FwDbAccessResponse res;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		res = new FwDbAccessResponse();
		//initial meta data
		Map<Integer, String> metaData = new LinkedHashMap<Integer, String>(50, 0.85f);
		metaData.put(1, "id");
		metaData.put(2, "name");
		metaData.put(3, "address");
		metaData.put(4, "content");
		metaData.put(5, "login_time");
		metaData.put(6, "last_action_time");
		res.setResultMeta(metaData);
		//initial result data
		List<Map<String, Object>> resultData = new LinkedList<Map<String, Object>>();
		Map<String, Object> row =  new LinkedHashMap<String, Object>();
		row.put("id", 1); row.put("name", "harry");	row.put("address", "guangzhou");
		row.put("content", "hi harry"); row.put("login_time", DateUtil.convertStrToDate("2011-01-01 12:00:00", "yyyy-MM-dd HH:mm:ss")); 
		row.put("last_action_time", DateUtil.convertStrToDate("2011-01-01 13:00:00", "yyyy-MM-dd HH:mm:ss"));
		resultData.add(row);
		row =  new LinkedHashMap<String, Object>();
		row.put("id", 2); row.put("name", "demo");	row.put("address", "huadu"); 
		row.put("content", "hi demo"); row.put("login_time", DateUtil.convertStrToDate("2011-01-01 12:00:00", "yyyy-MM-dd HH:mm:ss")); 
		row.put("last_action_time", DateUtil.convertStrToDate("2011-01-01 13:00:00", "yyyy-MM-dd HH:mm:ss"));
		resultData.add(row);
		res.setResultData(resultData);
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
	public void testIterator() {
		assertNotNull(res.iterator());
	}

	@Test
	public void testIteratorClassOfK() {
		assertNotNull(res.iterator(DemoUser.class));
	}

	@Test
	public void testGetResultDataClassOfK() {
		List<DemoUser> users = res.getResultData(DemoUser.class);
		assertEquals(2,users.size());
//		for(DemoUser user:users){
//			System.out.println(user.toString());
//		}
	}
	
	@Test
	public void testGetResultDataBeanHandler() {
		BeanHandler<DemoUser> handler = new BeanHandler<DemoUser>(){
			public DemoUser handler(DbAccessResponseRow row) {
				DemoUser bean = new DemoUser();
				bean.setId(row.getInt("id"));
				bean.setName(row.getString("name"));
				bean.setAddress(row.getString("address"));
				bean.setContent(row.getString("content"));
				bean.setLoginTime(row.getDate("login_time","yyyy-MM-dd HH:mm:ss"));
				bean.setLastActionTime(row.getDate("last_action_time","yyyy-MM-dd HH:mm:ss"));
				return bean;
			}
			
		};
		List<DemoUser> users = res.getResultData(handler);
		assertEquals(2,users.size());
//		for(DemoUser user:users){
//			System.out.println(user.toString());
//		}
	}
	
	@Test
	public void testIteratorClass(){
		Iterator<DemoUser> it = res.iterator(DemoUser.class);
		int i=0;
		while(it != null && it.hasNext()){
			DemoUser user = it.next();
			i++;
			//System.out.println(user);
		}
		assertEquals(2,i);
	}
	
	public static void main(String[] args) {
		
	}
	


}

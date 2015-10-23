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
package com.github.ipaas.ifw.core.support;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;

/**
 * @author Chenql
 */
public class ThreadPoolsTest {
	// TODO:还不知道怎么测试生成的线程池的数量验证
	private ArrayList<Integer> aList;
	ArrayList<Integer> expect;
	private int length;

	@Before
	public void setUp() {
		aList = new ArrayList<Integer>();
		aList.add(1);
		aList.add(2);
		expect = new ArrayList<Integer>();
		expect.add(2);
		expect.add(4);
		length = aList.size();
	}

	/**
	 * Test method for
	 * {@link com.github.ipaas.ifw.core.support.ThreadPools#newExecutorService(int, int, int, java.lang.String)}
	 * .
	 */
	@Test
	public final void testNewExecutorService() {
		Runnable mock = new Runnable() {
			public void run() {
				for (int i = 0; i < length; i++) {
					aList.add(i, aList.remove(i) * 2);
				}
			}
		};
		ExecutorService es = ThreadPools.newExecutorService(10, 20, 10, "testpool");
		es.submit(mock);
		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < length; i++) {
			assertEquals(expect.get(i), aList.get(i));
		}
	}

	/**
	 * 
	 * Test method for
	 * {@link com.github.ipaas.ifw.core.support.ThreadPools#newScheduledExecutorService(int, java.lang.String)}
	 * .
	 */
	@Test
	public final void testNewScheduledExecutorService() {
		final ScheduledExecutorService ses = ThreadPools.newScheduledExecutorService(10, "testpool");
		Runnable mock = new Runnable() {
			public void run() {
				for (int i = 0; i < length; i++) {
					aList.add(i, aList.remove(i) * 2);
				}
				ses.schedule(this, 800, TimeUnit.MILLISECONDS);
			}
		};
		mock.run();

		try {
			Thread.currentThread().sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < length; i++) {
			assertEquals(expect.get(i), aList.get(i));
		}
	}

}

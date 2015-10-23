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

import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import mockit.Capturing;
import mockit.Expectations;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ifw.util.IPUtil;

/**
 * 
 * @author Chenql
 */
public class IPUtilTest {
	@Capturing
	HttpServletRequest request;

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}



	   @Test
	    public void testGetUserPort() {
	        new Expectations() {
	            {
	                request.getHeader("Remote_Port");
	                result = "10000";
	            }
	        };

	        int port = IPUtil.getUserPort(request);
	        assertEquals(10000, port);
	    }


	@Test
	public void testGetLocalIP_Thread() {

		// 开5个并发得到LocalIP
		List<Thread> thList = new LinkedList<Thread>();
		List<String> ips =new LinkedList<String>();

		Thread nt = null;
		for (int i = 0; i < 10; i++) {
			nt = new Thread(new IpJob(ips));
			thList.add(nt);
			nt.start();
		}

		for (Thread t : thList) {
			try {
				t.join(600);
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}

		// 检测得到的IP得是否相同
		String myip = ips.get(0);
		for(String ip:ips){
			assertEquals(myip,ip);
		}
		
	}

}

/** 
*多线程收集信息
*/
class IpJob implements Runnable{

	private List<String> ips;
	
	IpJob(List<String> ips){
		this.ips = ips;
	}
	
	
	@Override
	public void run() {
		String ip = IPUtil.getLocalIP(true);
		synchronized(ips){
			ips.add(ip);
		}
	}
	
	
}

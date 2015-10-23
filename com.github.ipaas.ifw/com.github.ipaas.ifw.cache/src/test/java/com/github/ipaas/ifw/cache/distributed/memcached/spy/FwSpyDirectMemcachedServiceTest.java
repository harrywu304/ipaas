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
package com.github.ipaas.ifw.cache.distributed.memcached.spy;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.DefaultConnectionFactory;
import net.spy.memcached.MemcachedClient;

import org.junit.Before;
import org.junit.BeforeClass;

import com.github.ipaas.ifw.cache.DistributedCacheServiceTest;

/**
 * @author Chenql
 * 
 */
public class FwSpyDirectMemcachedServiceTest extends DistributedCacheServiceTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

	}

	@Before
	public void setUp() throws Exception {
		// FwSpyDirectMemcachedService fwSpyDirectMemcachedService = new
		// FwSpyDirectMemcachedService();
		// fwSpyDirectMemcachedService.setServerUrl("localhost:11211");
		// dcs = fwSpyDirectMemcachedService;
		// super.setUp();

	}

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		MemcachedClient mcc = new MemcachedClient(new DefaultConnectionFactory() {
			@Override
			public long getOperationTimeout() {
				return 100000;
			}
		}, AddrUtil.getAddresses("localhost:11211"));

		String key = "testSetStringObjectTooLong";
		mcc.delete(key);
		// dcs.set(key, "value1", 31 * 24 * 3600 * 1000L);
		int expiryInSec = 31 * 24 * 3600;
		//expiryInSec += System.currentTimeMillis() / 1000;
		System.out.println(mcc.set(key,(int) ( System.currentTimeMillis() / 1000 + 100), "value1").get());
		String result = null;
//		Thread.currentThread().sleep(10000);
		result = (String) mcc.get(key);
		System.out.println(result);
	}

}

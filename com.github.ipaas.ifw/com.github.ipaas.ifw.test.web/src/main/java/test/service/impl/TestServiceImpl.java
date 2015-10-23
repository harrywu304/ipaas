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
package test.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import test.service.TestService;

import com.github.ipaas.ifw.cache.DistributedCacheService;
import com.github.ipaas.ifw.component.ComponentClientService;
import com.github.ipaas.ifw.component.test.TestZeroPrx;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.jdbc.DbAccessResponse;
import com.github.ipaas.ifw.jdbc.DbAccessService;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MqSendService;
import com.github.ipaas.ifw.util.DateUtil;
import com.github.ipaas.ifw.util.JsonUtil;

@Service("test.service.TestService")
public class TestServiceImpl implements TestService {

	private ComponentClientService componentClientService;

	@Resource(name = "fwIceDirectComponentClientService")
	public void setComponentClientService(
			ComponentClientService componentClientService) {
		this.componentClientService = componentClientService;
	}

	// @Override
	public String greeting(String name) {
		TestZeroPrx testZeroPrx = componentClientService
				.getProxy(TestZeroPrx.class);
		String sql = "select * from test_table limit 10";
		DbAccessService dbAccessService = ServiceFactory
				.getService("fwDirectDbAccessService");
		DbAccessResponse response = dbAccessService.executeQuery(sql);
		String content = JsonUtil.toJson(response.getResultData());

		DistributedCacheService cacheService = ServiceFactory
				.getService("fwSpyDirectMemcachedService");

		String cacheTime = (String) cacheService.get("cacheTime");
		if (cacheTime == null || cacheTime.equals("")) {
			cacheTime = DateUtil.convertDateToStr(new Date(),
					"yyyy-MM-dd HH:mm");
			cacheService.set("cacheService", cacheTime, 1000 * 60);
		}

		MqSendService mqSendService = ServiceFactory
				.getService("fwMqSendService");
		Message message = mqSendService.createMessage();
		message.setContent(content);
		mqSendService.sendQueue("amm.agent.test", message);

		return cacheTime + ": " + content + " _ " + testZeroPrx.sayHello(name);
	}
	
	public static void main(String[] args) {
		DistributedCacheService cacheService = ServiceFactory
				.getService("fwSpyDirectMemcachedService");

		String cacheTime = (String) cacheService.get("cacheTime");
		if (cacheTime == null || cacheTime.equals("")) {
			cacheTime = DateUtil.convertDateToStr(new Date(),
					"yyyy-MM-dd HH:mm");
			cacheService.set("cacheService", cacheTime, 1000 * 60);
		}
	}
}

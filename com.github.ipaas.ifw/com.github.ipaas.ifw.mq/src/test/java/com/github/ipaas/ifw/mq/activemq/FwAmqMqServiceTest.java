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

package com.github.ipaas.ifw.mq.activemq;

import org.junit.Before;

import com.github.ipaas.ifw.mq.MqServiceTest;
import com.github.ipaas.ifw.mq.activemq.FwMqListenService;
import com.github.ipaas.ifw.mq.activemq.FwMqSendService;

/**
 * @author Chenql
 */
public class FwAmqMqServiceTest extends MqServiceTest {

	/**
	 * modified by Chenql 127.0.0.1 mq 以安全模式启动,配置用户名密码 bin/activemq start
	 * xbean:conf/activemq-security.xml
	 */
	@Before
	public void setUp() {

		String hosts = "192.168.1.103:61616";

		FwMqListenService fwMqListenService = new FwMqListenService();
		fwMqListenService.setServerUrl(hosts);
		// fwMqListenService.setUserName("");
		// fwMqListenService.setPassword("");
		mqListenService = fwMqListenService;

		FwMqSendService fwMqSendService = new FwMqSendService();
		fwMqSendService.setServerUrl(hosts);

		// fwMqSendService.setUserName("");
		// fwMqSendService.setPassword("");
		mqSendService = fwMqSendService;

	}
}

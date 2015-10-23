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
package com.github.ipaas.ifw.component.test.impl;

import Ice.Current;

import com.github.ipaas.ifw.component.test._TestZeroDisp;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.jdbc.DbAccessService;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MqSendService;
import com.github.ipaas.ifw.util.SqlUtil;

/**
 *
 * @author Chenql
 */
public class TestZeroImpl extends _TestZeroDisp {

	private static final long serialVersionUID = -2898157922070182978L;

	// @Override
	public String sayHello(String name, Current __current) {
		String greeting = " hello," + name + " this is a fw ice demo ";

		DbAccessService dbAccessService = ServiceFactory.getService("fwDirectDbAccessService");
		dbAccessService.executeUpdate(SqlUtil.getSql("insert into   test.test_table(message) values(?) ", greeting));

		MqSendService mqSendService = ServiceFactory.getService("fwMqSendService");
		Message message = mqSendService.createMessage();
		message.setContent(greeting);
		mqSendService.sendQueue("testZeroIceQueue", message);
		return greeting;
	}

}

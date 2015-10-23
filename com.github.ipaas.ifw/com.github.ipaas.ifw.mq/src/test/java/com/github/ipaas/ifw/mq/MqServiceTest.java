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

package com.github.ipaas.ifw.mq;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.github.ipaas.ifw.util.IPUtil;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 消息队列服务测试类
 * 
 * @author Chenql
 * 
 */
public class MqServiceTest {

	protected static Logger logger = Logger.getLogger(MqServiceTest.class);

	protected MqListenService mqListenService;

	protected MqSendService mqSendService;

	@Test
	public void testCreateMessage() {
		Message msg = mqSendService.createMessage();
		assertFalse(msg.isAutoJsonConvert());
		assertTrue(msg.isPersist());
		assertEquals(4, msg.getPriority());
		assertEquals(0, msg.getTimeToLive());
	}

	@Test
	public void testBeanMessageToContent() {
		Message msg = mqSendService.createMessage();

		TestBeanSerial testSerialBean = new TestBeanSerial();
		testSerialBean.id = 100;
		testSerialBean.content = "Test Serial Bean Content";

		msg.setContent(testSerialBean);

		TestBeanSerial transferBean = msg.getContent(TestBeanSerial.class);
		assertEquals(testSerialBean, transferBean);

		TestBeanNoSerial testNoSerialBean = new TestBeanNoSerial();
		testNoSerialBean.id = 100;
		testNoSerialBean.content = "Test No Serial Bean Content";

		String jsonString = JsonUtil.toJson(testNoSerialBean);
		msg.setContent(jsonString);

		TestBeanNoSerial transferBean2 = msg.getContent(TestBeanNoSerial.class);
		assertEquals(testNoSerialBean, transferBean2);
	}

	/**
	 * send queue with string then listen
	 */
	@Test
	public void testSendAndListenQueueWithString() {
		String queueName = "testSendStringListenQueue";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenQueue(queueName, listener);

		Message msg = mqSendService.createMessage();
		msg.setContent("testSendTextListenQueue content");
		msg.setTimeToLive(100000L);

		mqSendService.sendQueue(queueName, msg);
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
		}

		Message recvMsg = listener.getReceivedMessage();
		assertEquals("testSendTextListenQueue content", recvMsg.getContent());
	}

	/**
	 * send queue with bean no serial and autoJsonConvert then listen
	 */
	@Test
	public void testSendAndListenQueueWithBeanSerialNoJsonCvt() {
		String queueName = "testSendAndListenQueueWithBeanSerialNoJsonCvt";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenQueue(queueName, listener);

		TestBeanSerial bean = new TestBeanSerial(1, "Test Bean");
		Message msg = mqSendService.createMessage();
		msg.setId("1");
		msg.setContent(bean);
		// msg.setTimeToLive(100000L);

		mqSendService.sendQueue(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals(bean, recvMsg.getContent());
	}

	/**
	 * send queue with bean no serial and autoJsonConvert then listen
	 */
	@Test(expected = java.lang.RuntimeException.class)
	public void testSendAndListenQueueWithBeanNoSerialNoJsonCvt() {
		String queueName = "testSendAndListenQueueWithBeanNoSerialNoJsonCvt";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenQueue(queueName, listener);

		TestBeanNoSerial bean = new TestBeanNoSerial(1, "Test Bean");
		Message msg = mqSendService.createMessage();
		msg.setId("1");
		msg.setContent(bean);

		mqSendService.sendQueue(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals(bean, recvMsg.getContent());
	}

	/**
	 * send queue with bean no serial and autoJsonConvert then listen
	 */
	@Test
	public void testSendAndListenQueueWithBeanNoSerialAutoJsonCvt() {
		String queueName = "testSendAndListenQueueWithBeanNoSerialAutoJsonCvt";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenQueue(queueName, listener);

		TestBeanNoSerial bean = new TestBeanNoSerial(1, "Test Bean");
		Message msg = mqSendService.createMessage();
		msg.setId("1");
		msg.setContent(bean);
		msg.setAutoJsonConvert(true);
		mqSendService.sendQueue(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		TestBeanNoSerial recvBean = JsonUtil.toBean((String) recvMsg.getContent(), TestBeanNoSerial.class);
		assertEquals(bean, recvBean);
	}

	@Test
	public void testSendAndListenQueueWithMap() {
		String queueName = "testSendAndListenQueueWithMap";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subject", "subject001");
		params.put("content", "this is content");
		params.put("labels", new String[] { "lb001", "lb002" });

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenQueue(queueName, listener);

		Message msg = mqSendService.createMessage();
		msg.setContent(params);
		mqSendService.sendQueue(queueName, msg);
		Message recvMsg = listener.getReceivedMessage();
		Map<String, Object> paramsAct = (Map<String, Object>) recvMsg.getContent();
		assertEquals(params.get("subject"), paramsAct.get("subject"));
		assertEquals(params.get("content"), paramsAct.get("content"));
		assertEquals(((String[]) params.get("labels")).length, ((String[]) paramsAct.get("labels")).length);
	}

	/**
	 * ==============the following test is for topic=============
	 */

	/**
	 * send topic with string then listen
	 */
	@Test
	public void testSendAndListenTopicWithString() {
		String queueName = "testSendStringListenTopic";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenTopic(queueName, listener);

		Message msg = mqSendService.createMessage();
		msg.setContent("testSendTextListenTopic content");
		// msg.setTimeToLive(100000L);

		mqSendService.sendTopic(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals("testSendTextListenTopic content", recvMsg.getContent());
	}

	/**
	 * send topic with bean no serial and autoJsonConvert then listen
	 */
	@Test
	public void testSendAndListenTopicWithBeanSerialNoJsonCvt() {
		String queueName = "testSendAndListenTopicWithBeanSerialNoJsonCvt";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenTopic(queueName, listener);

		TestBeanSerial bean = new TestBeanSerial(1, "Test Bean");
		Message msg = mqSendService.createMessage();
		msg.setId("1");
		msg.setContent(bean);
		// msg.setTimeToLive(100000L);

		mqSendService.sendTopic(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals(bean, recvMsg.getContent());
	}

	/**
	 * send topic with bean no serial and autoJsonConvert then listen
	 */
	@Test(expected = java.lang.RuntimeException.class)
	public void testSendAndListenTopicWithBeanNoSerialNoJsonCvt() {
		String queueName = "testSendAndListenTopicWithBeanNoSerialNoJsonCvt";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenTopic(queueName, listener);

		TestBeanNoSerial bean = new TestBeanNoSerial(1, "Test Bean");
		Message msg = mqSendService.createMessage();
		msg.setId("1");
		msg.setContent(bean);

		mqSendService.sendTopic(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals(bean, recvMsg.getContent());
	}

	/**
	 * send topic with bean no serial and autoJsonConvert then listen
	 */
	@Test
	public void testSendAndListenTopicWithBeanNoSerialAutoJsonCvt() {
		String queueName = "testSendAndListenTopicWithBeanNoSerialAutoJsonCvt";

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenTopic(queueName, listener);

		TestBeanNoSerial bean = new TestBeanNoSerial(1, "Test Bean");
		Message msg = mqSendService.createMessage();
		msg.setId("1");
		msg.setContent(bean);
		msg.setAutoJsonConvert(true);
		mqSendService.sendTopic(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		TestBeanNoSerial recvBean = JsonUtil.toBean((String) recvMsg.getContent(), TestBeanNoSerial.class);
		assertEquals(bean, recvBean);
	}

	@Test
	public void testSendAndListenTopicWithMap() {
		String queueName = "testSendAndListenTopicWithMap";

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("subject", "subject001");
		params.put("content", "this is content");
		params.put("labels", new String[] { "lb001", "lb002" });

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenTopic(queueName, listener);

		Message msg = mqSendService.createMessage();
		msg.setContent(params);
		mqSendService.sendTopic(queueName, msg);

		Message recvMsg = listener.getReceivedMessage();
		Map<String, Object> paramsAct = (Map<String, Object>) recvMsg.getContent();
		assertEquals(params.get("subject"), paramsAct.get("subject"));
		assertEquals(params.get("content"), paramsAct.get("content"));
		assertEquals(((String[]) params.get("labels")).length, ((String[]) paramsAct.get("labels")).length);
	}

	@Test
	public void testListenTopicDurable() {
		String topicName = "testListenTopicDurable";

		TestMessageHandler listener = new TestMessageHandler();
		String subscriptionName = mqListenService.listenTopicDurable(topicName, listener);
		assertEquals("testListenTopicDurable_" + IPUtil.getLocalIP(true), subscriptionName);

		Message msg = mqSendService.createMessage();
		msg.setContent("testListenTopicDurable content");
		mqSendService.sendTopic(topicName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals("testListenTopicDurable content", recvMsg.getContent());
	}

	@Test
	public void testListenTopicDurableForSubScriptionName() {
		String topicName = "testListenTopicDurableForSubScriptionName";
		String subscriptionName = topicName;

		TestMessageHandler listener = new TestMessageHandler();
		mqListenService.listenTopicDurable(topicName, listener, subscriptionName);

		Message msg = mqSendService.createMessage();
		msg.setContent("testListenTopicDurableForSubScriptionName content");
		mqSendService.sendTopic(topicName, msg);

		Message recvMsg = listener.getReceivedMessage();
		assertEquals("testListenTopicDurableForSubScriptionName content", recvMsg.getContent());
	}
//
//	@Test
//	public void sendQueueByRpc() {
//		String queueName = "testSendQueueByRpc";
//
//		TestMessageHandlerRpc listener = new TestMessageHandlerRpc();
//		mqListenService.listenSyncQueue(queueName, listener);
//
//		Message msg = mqSendService.createMessage();
//		msg.setContent("testSendQueueByRpc content");
//		msg.setTimeToLive(100000L);
//		String result = (String) mqSendService.sendQueueByRpc(queueName, msg).getContext();
//		assertEquals("TestMessageHandlerRpc testSendQueueByRpc content", result);
//	}

}

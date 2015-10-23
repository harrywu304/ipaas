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

package com.github.ipaas.ifw.mq.mbean;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.util.JMSUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * MQ可用性检测的实现类
 * 
 * @author Chenql
 * 
 */
public class MqCheckServiceImpl {
	private static Logger logger = LoggerFactory.getLogger(MqCheckServiceImpl.class);
	private static String CHECK_QUEUE = "amm.mq_healthcheck_queue";
	private static String ONE = "1";
	private static int TIME_OUT = 2000;
	private static long TIME_TO_LIVE = 60000;

	public String check(String ip, String port, String userName, String password) {
		String brokerUrl = null;
		brokerUrl = "tcp://" + ip + ":" + port + "?connectionTimeout=2000";

		String state = null;
		state = checkQueue(brokerUrl, userName, password);
		return state;
	}

	/**
	 * 
	 * 检查mq消息队列的可用性
	 * 
	 * @param brokerUrl
	 *            -- mq的连接串
	 * @param userName
	 *            安全认证用户名
	 * @param password
	 *            安全认证密码
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	private String checkQueue(String brokerUrl, String userName, String password) {
		String sendResult = null;
		String acceptResult = null;

		// 发送消息到queue
		sendResult = sendToQueue(brokerUrl, userName, password);

		// 发送不成功直接 返回不成功的信息
		if (!ONE.equals(sendResult)) {
			return sendResult;
		}
		// 发送成功,检验接收是否成功
		acceptResult = acceptFromQueue(brokerUrl, userName, password);
		return acceptResult;
	}

	/**
	 * 
	 * 发送消息到队列
	 * 
	 * @param brokerUrl
	 *            -- mq连接串
	 * @param userName
	 *            安全认证用户名
	 * @param password
	 *            安全认证密码
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	private String sendToQueue(String brokerUrl, String userName, String password) {
		String state = null;
		Session session = null;
		Connection connection = null;
		MessageProducer producer = null;
		try {
			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
			if (userName != null) {
				connectionFactory.setUserName(userName);
			}
			if (password != null) {
				connectionFactory.setPassword(password);
			}
			// Create a Connection
			connectionFactory.setSendTimeout(TIME_OUT);
			connection = connectionFactory.createConnection();
			connection.start();

			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(CHECK_QUEUE);

			// Create a MessageProducer from the Session to the Topic or Queue
			producer = session.createProducer(destination);
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			// producer.setTimeToLive(TIME_TO_LIVE);
			// Create a messages
			String text = "echo";
			TextMessage message = session.createTextMessage(text);
			// Tell the producer to send the message
			producer.send(message);
			state = "1";
		} catch (Throwable e) {
			state = "sending message is not success," + StringUtil.getExceptionAsStr(e);
			logger.error("Mq check send msg to queue error", e);
			return state;
		} finally {
			// Clean up
			JMSUtil.closeSilently(producer);
			JMSUtil.closeSilently(session);
			JMSUtil.closeSilently(connection);
		}
		return state;
	}

	/**
	 * 
	 * 从队列接受消息
	 * 
	 * @param brokerUrl
	 *            -- mq连接串
	 * @param userName
	 *            安全认证用户名
	 * @param password
	 *            安全认证密码
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	private String acceptFromQueue(String brokerUrl, String userName, String password) {
		String state = null;
		Session session = null;
		Connection connection = null;
		MessageConsumer consumer = null;

		try {

			// Create a ConnectionFactory
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
			if (userName != null) {
				connectionFactory.setUserName(userName);
			}
			if (password != null) {
				connectionFactory.setPassword(password);
			}
			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// Create the destination (Topic or Queue)
			Destination destination = session.createQueue(CHECK_QUEUE);
			// Create a MessageConsumer from the Session to the Topic or Queue
			consumer = session.createConsumer(destination);
			// Wait for a message
			Message message = consumer.receive(TIME_OUT);

			TextMessage textMessage = (TextMessage) message;
			if (textMessage == null) {
				state = "receiving  textMessage message is null";
				return state;
			}
			String text = textMessage.getText();
			if (!StringUtil.isNullOrBlank(text) && "echo".equals(text)) {
				state = "1";
			} else {
				state = "received msg is null/Blank or is not equal to sending msg";
			}
		} catch (Throwable e) {
			state = "receiving message is not success," + StringUtil.getExceptionAsStr(e);
			logger.error("Mq check accept queue error", e);
			return state;
		} finally {
			// Clean up
			JMSUtil.closeSilently(consumer);
			JMSUtil.closeSilently(session);
			JMSUtil.closeSilently(connection);
		}
		return state;
	}

}

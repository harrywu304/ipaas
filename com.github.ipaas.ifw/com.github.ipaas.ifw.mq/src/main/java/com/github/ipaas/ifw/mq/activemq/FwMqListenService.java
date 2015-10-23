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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.mbean.Fw;
import com.github.ipaas.ifw.mq.FwMessage;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MessageHandler;
import com.github.ipaas.ifw.mq.MessageSyncHandler;
import com.github.ipaas.ifw.mq.MqListenService;
import com.github.ipaas.ifw.mq.Response;
import com.github.ipaas.ifw.mq.mbean.MqHealthCheck;
import com.github.ipaas.ifw.util.IPUtil;
import com.github.ipaas.ifw.util.JMXUtil;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 *
 * @author Chenql
 */
public class FwMqListenService implements MqListenService {

	private static Logger logger = LoggerFactory.getLogger(FwMqListenService.class);

	/**
	 * 保存需要关闭的session
	 */
	private List<Session> closeSessions = new ArrayList<Session>();

	/**
	 * 保存需要关闭的connetion
	 */
	private List<Connection> closeConnections = new ArrayList<Connection>();

	/**
	 * 消费信息的brokerUrl
	 */
	private List<String> brokerForConsumer;

	/**
	 * 认证和授权的用户名
	 */
	private String userName;
	/**
	 * 认证和授权的密码
	 */
	private String password;

	/**
	 * 连接超时时间,单位ms,默认是1秒
	 */
	private int connectTimeout = 1000;

	/**
	 * 操作超时时间,单位ms,默认操作超时时间是5秒
	 */
	private int operateTimeout = 5000;

	/**
	 * 侦听线程池，默认是100
	 */
	private int listenThreadPoolMaxSize = 100;

	/**
	 * 服务器列表
	 */
	private String serverUrl;
	/**
	 * 服务Id
	 */
	private String serviceId;

	/**
	 * 通信方式 sync同步，async异步
	 */
	public static enum COMMUNICATION_MODULE {
		SYNC, ASYNC
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public void setOperateTimeout(int operateTimeout) {
		this.operateTimeout = operateTimeout;
	}

	public void setListenThreadPoolMaxSize(int listenThreadPoolMaxSize) {
		this.listenThreadPoolMaxSize = listenThreadPoolMaxSize;
	}

	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public FwMqListenService() {

	}

	/**
	 * java bean 方式初始化服务
	 * 
	 * @param serviceId
	 * @param params
	 *            mq参数配置Map:serverUrl(必须)、userName、password、
	 *            listenThreadPoolMaxSize、operateTimeout
	 *            、listenThreadPoolMaxSize、connectTimeout
	 */
	public FwMqListenService(String serviceId, Map<String, Object> params) {
		System.out.println("666666666666666666666666666666");
		this.serviceId = serviceId;
		if (params.containsKey("serverUrl")) {
			serverUrl = (String) params.get("serverUrl");
		}
		if (params.containsKey("userName")) {
			userName = (String) params.get("userName");
		}
		if (params.containsKey("password")) {
			password = (String) params.get("password");
		}
		System.out.println("666666666666666666666666666666");
		if (params.containsKey("listenThreadPoolMaxSize")) {
			listenThreadPoolMaxSize = Integer.parseInt((String) params.get("listenThreadPoolMaxSize"));
		}
		if (params.containsKey("operateTimeout")) {
			operateTimeout = Integer.parseInt((String) params.get("operateTimeout"));
		}
		System.out.println("666666666666666666666666666666");
		if (params.containsKey("connectTimeout")) {
			connectTimeout = Integer.parseInt((String) params.get("connectTimeout"));
		}
		System.out.println("777777777777777777777777777777777777777777777");
		setBrokerForConsumer();
	}

	/**
	 * 构造MQ组件客户端的BrokerUrl
	 * 
	 * @param connectTimeout
	 * @param operateTimeout
	 * @param hosts
	 */
	private void setBrokerForConsumer() {
		System.out.println("888888888888888888888888888888");
		if (brokerForConsumer == null) {
			synchronized (this) {
				if (brokerForConsumer == null) {
					if (serverUrl == null || serverUrl.trim().equals("")) {
						throw new FwRuntimeException("未设置服务器地址serverUrl");
					}

					// 设置brokerForQuery
					String[] hostArr = serverUrl.split(",");
					System.out.println("  ....................  " + hostArr);
					brokerForConsumer = new ArrayList<String>();
					StringBuilder brokerUrlSb = null;
					for (String host : hostArr) {
						brokerUrlSb = new StringBuilder();
						brokerUrlSb.append("failover:(");
						brokerUrlSb.append("tcp://" + host + "?connectionTimeout=" + connectTimeout);
						brokerUrlSb.append(")?timeout=" + operateTimeout);
						brokerForConsumer.add(brokerUrlSb.toString());
					}
					initMqHealthMBean();
				}
			}
		}
	}

	/**
	 * durable topic的 consumer
	 * 
	 * @param topicName
	 * @param handler
	 * @param brokerUrl
	 */
	private void listenTopicDurableByBrokerUrl(String topicName, MessageHandler handler, String brokerUrl,
			String subscriptionName) {
		Session session = null;
		try {
			Connection connection = getConnectionDurable(brokerUrl, subscriptionName);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			Topic topic = session.createTopic(topicName);
			MessageConsumer consumer = session.createDurableSubscriber(topic, subscriptionName, "", false);

			// 设置消息侦听器
			consumer.setMessageListener(new ConcurrentMessageListener(handler));

			logger.info("对Topic[{}]的持久收听成功, 订阅标识是:{}", topicName, subscriptionName);
		} catch (Exception e) {
			throw new RuntimeException("对Topic的持久收听失败  " + "[" + topicName + "]", e);
		}
	}

	/**
	 * 根据传输类型进行不同方式的侦听处理
	 * 
	 * @param brokerUrl
	 * @param name
	 *            Queue/Topic的名字
	 * @param listener
	 *            消息侦听器
	 * @param type
	 *            传输类型
	 */
	private void listen(String name, final MessageHandler handler, TRANSPORT_MODEL type, String brokerUrl) {
		Session session = null;
		try {
			Connection connection = getConnection(brokerUrl);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 保存会话,关闭时候用
			closeSessions.add(session);
			closeConnections.add(connection);
			logger.info("activemq closeSessions.size: " + closeSessions.size());
			// 根据传输模式创建Destination
			Destination dest = null;
			if (type == TRANSPORT_MODEL.QUEUE) {
				dest = session.createQueue(name);
			} else if (type == TRANSPORT_MODEL.TOPIC) {
				dest = session.createTopic(name);
			}
			// 创建Consumer
			MessageConsumer consumer = session.createConsumer(dest);

			// 设置消息侦听器
			consumer.setMessageListener(new ConcurrentMessageListener(handler));
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new RuntimeException("收听消息失败  " + type + "[" + name + "]", e);
		}

	}

	/**
	 * 取得普通连接，每个Service实例中该连接唯一，用于Consumer
	 * 
	 * @param brokerUrl
	 *            发送的broker url
	 * @return
	 * @throws JMSException
	 */
	private Connection getConnection(String brokerUrl) throws JMSException {
		ActiveMQConnectionFactory connectionFactory = FwMqServiceHelper.createConnectionFactory(brokerUrl, userName,
				password);

		Connection conn = connectionFactory.createConnection();
		// 设置客户端ID，用于支持持久化订阅。此操作必须在取得连接后进行，否则会抛出异常 为什么设置IP为 clientId?
		// conn.setClientID(IPUtil.getLocalIP(true));
		return conn;
	}

	/**
	 * 取得持久化连接连接，每个Service实例中该连接唯一，用于Consumer,需要设置clientId
	 * (topicName+'_'+<server_ip>)
	 * 
	 * @param brokerUrl
	 *            发送的broker url
	 * @return
	 * @throws JMSException
	 */
	private Connection getConnectionDurable(String brokerUrl, String clientId) throws JMSException {
		ActiveMQConnectionFactory connectionFactory = FwMqServiceHelper.createConnectionFactory(brokerUrl, userName,
				password);

		Connection conn = connectionFactory.createConnection();
		// 设置客户端ID，用于支持持久化订阅。此操作必须在取得连接后进行，否则会抛出异常 为什么设置IP为 clientId?
		conn.setClientID(clientId);
		return conn;
	}

	/**
	 * 并发消息侦听器
	 * 
	 * @author whx
	 * 
	 */
	private class ConcurrentMessageListener implements MessageListener {

		/**
		 * jms会话
		 */
		private Session session;
		/**
		 * 服务线程池
		 */
		private ExecutorService executor = null;

		/**
		 * 消息处理器
		 */
		private MessageHandler handler;

		/**
		 * 同步消息处理器
		 */
		private MessageSyncHandler syncHandler = null;

		/**
		 * 通信方式
		 */
		private COMMUNICATION_MODULE communic;

		private ConcurrentMessageListener(MessageHandler handler) {
			this.communic = COMMUNICATION_MODULE.ASYNC;
			this.handler = handler;
			executor = new ThreadPoolExecutor(listenThreadPoolMaxSize, listenThreadPoolMaxSize, 0L, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
		}

		public ConcurrentMessageListener(MessageSyncHandler handler, Session session) {
			this.communic = COMMUNICATION_MODULE.SYNC;
			this.session = session;
			this.syncHandler = handler;
			// 初始化侦听线程池，默认是100
			executor = new ThreadPoolExecutor(listenThreadPoolMaxSize, listenThreadPoolMaxSize, 0L, TimeUnit.SECONDS,
					new SynchronousQueue<Runnable>(), new ThreadPoolExecutor.CallerRunsPolicy());
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
		 */
		public void onMessage(javax.jms.Message msg) {
			// 异步
			if (this.communic == COMMUNICATION_MODULE.ASYNC) {
				executor.execute(new MessageHandlerAdapter(msg, handler));
			}
			// 同步
			else if (this.communic == COMMUNICATION_MODULE.SYNC) {
				executor.execute(new MessageSyncHandlerAdapter(msg, syncHandler, session));
			}
		}
	}

	/**
	 * 串行消息侦听器
	 * 
	 * @author whx
	 * 
	 */
	@SuppressWarnings("unused")
	private class SerialMessageListener implements MessageListener {

		/**
		 * 消息处理器
		 */
		private MessageHandler handler;

		private SerialMessageListener(MessageHandler handler) {
			this.handler = handler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
		 */
		public void onMessage(javax.jms.Message msg) {
			new MessageHandlerAdapter(msg, handler).run();
		}
	}

	/**
	 * 消息处理适配器
	 * 
	 * @author flyu
	 * 
	 */
	private class MessageHandlerAdapter implements Runnable {

		/**
		 * jms消息
		 */
		private javax.jms.Message jmsMsg;

		/**
		 * 消息处理器
		 */
		private MessageHandler handler;

		public MessageHandlerAdapter(javax.jms.Message jmsMsg, final MessageHandler handler) {
			this.jmsMsg = jmsMsg;
			this.handler = handler;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				// 获取消息内容
				Object obj = null;
				if (jmsMsg instanceof TextMessage) {
					obj = ((TextMessage) jmsMsg).getText();
				} else if (jmsMsg instanceof ObjectMessage) {
					obj = ((ObjectMessage) jmsMsg).getObject();
				} else {
					logger.error("非法的消息类型 {}", jmsMsg.getClass());
					return;
				}
				// 创建框架消息对象
				Message msg = FwMqServiceHelper.createMessage();
				msg.setId(jmsMsg.getStringProperty("MessageID"));
				msg.setContent(obj);
				logger.debug("调用MessageHandler的handle接口，进行相应的业务处理:" + handler.getClass());
				// 调用MessageHandler的handle接口，进行相应的业务处理
				handler.handle(msg);
			} catch (Exception e) {
				logger.error("消息处理失败，抛出Exception", e);
			} catch (Error er) {
				logger.error("消息处理失败，抛出Error", er);
				throw er;
			}
		}

	}

	/**
	 * 消息处理适配器
	 * 
	 * @author flyu
	 * 
	 */
	private class MessageSyncHandlerAdapter implements Runnable {

		/**
		 * jms会话
		 */
		private Session session;
		/**
		 * jms消息
		 */
		private javax.jms.Message jmsMsg;

		/**
		 * 消息处理器
		 */
		private MessageSyncHandler syncHandler;

		public MessageSyncHandlerAdapter(javax.jms.Message jmsMsg, final MessageSyncHandler syncHandler, Session session) {
			this.jmsMsg = jmsMsg;
			this.syncHandler = syncHandler;
			this.session = session;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			try {
				// 获取消息内容
				String obj = null;
				if (!(jmsMsg instanceof TextMessage)) {
					logger.error("非法的消息类型 {}", jmsMsg.getClass());
					return;
				}
				obj = ((TextMessage) jmsMsg).getText();
				// 创建框架消息对象
				Message msg = createMessage();
				msg.setId(jmsMsg.getStringProperty("MessageID"));
				msg.setContent(obj);

				// 调用MessageHandler的handle接口，进行相应的业务处理
				Response replyMess = syncHandler.handle(msg);

				// 回复
				javax.jms.Message tm = null;
				if (replyMess == null) {
					throw new RuntimeException("同步消息不能返回空对象");
				}

				// replyMess 的json对象
				if (msg.isAutoJsonConvert()) {
					tm = session.createTextMessage((String) JsonUtil.toJson(replyMess));
				} else {
					tm = session.createObjectMessage((java.io.Serializable) replyMess);
				}

				tm.setJMSCorrelationID(jmsMsg.getJMSCorrelationID());
				Queue reply = (Queue) jmsMsg.getJMSReplyTo();
				MessageProducer sender = session.createProducer(reply);
				sender.send(tm);

			} catch (Exception e) {
				logger.error("消息处理失败 ", e);
			}
		}

	}

	/**
	 * 取消durable的注册
	 * 
	 * @param subscriptionName
	 * @param brokerUrl
	 */
	private void unlistenTopicDurable(String subscriptionName, String brokerUrl) {
		Session session = null;
		try {
			Connection connection = getConnection(brokerUrl);
			connection.start();
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 保存会话,关闭时候用
			closeSessions.add(session);
			closeConnections.add(connection);
			if (null != session) {
				session.unsubscribe(subscriptionName);
			}

			logger.info("取消对{}的持久收听成功", subscriptionName);
		} catch (JMSException e) {
			throw new RuntimeException("取消对Topic的持久收听失败  " + subscriptionName, e);
		}

	}

	@Override
	public void listenQueue(String queueName, MessageHandler listener) {
		setBrokerForConsumer();
		for (String brokerUrl : brokerForConsumer) {
			logger.info("brokerUrl=" + brokerUrl);
			listen(queueName, listener, TRANSPORT_MODEL.QUEUE, brokerUrl);
		}
	}

	@Override
	public void listenTopic(String topicName, MessageHandler listener) {
		setBrokerForConsumer();
		for (String brokerUrl : brokerForConsumer) {
			logger.info("brokerUrl=" + brokerUrl);
			listen(topicName, listener, TRANSPORT_MODEL.TOPIC, brokerUrl);
		}
	}

	@Override
	public String listenTopicDurable(String topicName, MessageHandler handler) {
		String subscriptionName = topicName + "_" + IPUtil.getLocalIP(true);
		setBrokerForConsumer();
		for (int i = 0; i < brokerForConsumer.size(); i++) {
			String brokerUrl = brokerForConsumer.get(i);
			logger.debug("listenTopicDurable brokerUrl=" + brokerUrl);
			listenTopicDurableByBrokerUrl(topicName, handler, brokerUrl, subscriptionName);
		}
		return subscriptionName;
	}

	@Override
	public void listenTopicDurable(String topicName, MessageHandler handler, String subscriptionName) {
		setBrokerForConsumer();
		for (int i = 0; i < brokerForConsumer.size(); i++) {
			String brokerUrl = brokerForConsumer.get(i);
			logger.debug("listenTopicDurable brokerUrl=" + brokerUrl);
			listenTopicDurableByBrokerUrl(topicName, handler, brokerUrl, subscriptionName);
		}
	}

	@Override
	public void unlistenTopicDurable(String subscriptionName) {
		for (String brokerUrl : brokerForConsumer) {
			logger.debug("unlistenTopicDurable brokerUrl=" + brokerUrl);
			unlistenTopicDurable(subscriptionName, brokerUrl);
		}
	}

	/**
	 * 
	 * 初始化MQ可用性检测的MBean
	 * 
	 * @return void
	 */
	private void initMqHealthMBean() {
		MqHealthCheck mqHealthCheck;
		try {
			// 拆分主从配置
			String[] group = this.serverUrl.split(",");
			for (int i = 0; i < group.length; i++) {
				String[] ipPort = group[i].split(":");
				mqHealthCheck = new MqHealthCheck(serviceId, ipPort[0], ipPort[1], userName, password);
				String oname = JMXUtil.createObjectNameString(Fw.FW_USABILITY_DOMAIN, MqHealthCheck.TYPE, serviceId,
						ipPort[0] + "-" + ipPort[1]);
				JMXUtil.registerMBean(mqHealthCheck, oname);
				logger.info("registerMBean for MqHealthCheck " + group[i]);
				mqHealthCheck = null;
			}

		} catch (Throwable e) {
			logger.error("initMqHealthMBean error", e);
		}

	}

	@Override
	public void listenSyncQueue(String queueName, MessageSyncHandler handler) {
		setBrokerForConsumer();
		for (String brokerUrl : brokerForConsumer) {
			if (brokerUrl != null) {
				logger.info("brokerUrl=" + brokerUrl);
				listenSync(queueName, handler, TRANSPORT_MODEL.QUEUE, brokerUrl);
			}
		}

	}

	private void listenSync(String queueName, MessageSyncHandler handler, TRANSPORT_MODEL queue, String brokerUrl) {
		try {
			Connection connection = getConnection(brokerUrl);
			connection.start();

			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			// 保存会话,关闭时候用
			closeSessions.add(session);
			closeConnections.add(connection);

			// 根据传输模式创建Destination
			Destination dest = session.createQueue(queueName);

			// 创建Consumer
			MessageConsumer consumer = session.createConsumer(dest);

			// 设置消息侦听器
			consumer.setMessageListener(new ConcurrentMessageListener(handler, session));
		} catch (Exception e) {
			throw new RuntimeException("收听消息失败  [" + queueName + "]", e);
		}

	}

	/**
	 * 仓建消息对象
	 * 
	 * @return
	 */
	private Message createMessage() {
		Message msg = new FwMessage();
		// 默认不做持久化
		msg.setPersist(true);
		// 默认优先级是4
		msg.setPriority(javax.jms.Message.DEFAULT_PRIORITY);
		// 默认最大存活时间是永不过期
		msg.setTimeToLive(0L);
		// 默认不做自动Json转换
		msg.setAutoJsonConvert(false);
		return msg;
	}

	@Override
	public void releaseResource() {
		try {
			if (null != closeSessions) {
				for (Session session : closeSessions) {
					if (null != session) {
						session.close();
						session = null;
					}
				}
			}
			closeSessions = new ArrayList<Session>();
			if (null != closeConnections) {
				logger.info("activeMq 需要关闭的连接数:" + closeConnections.size());
				for (Connection connection : closeConnections) {
					if (null != connection) {
						connection.close();
						connection = null;
					}
				}
			}
			closeConnections = new ArrayList<Connection>();
		} catch (Exception e) {
			logger.error("releaseResource exception cause: ", e);
		}

	}
}

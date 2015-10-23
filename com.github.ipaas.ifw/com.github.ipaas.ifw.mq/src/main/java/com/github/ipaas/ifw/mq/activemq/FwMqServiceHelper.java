package com.github.ipaas.ifw.mq.activemq;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.ActiveMQPrefetchPolicy;

import com.github.ipaas.ifw.mq.FwMessage;
import com.github.ipaas.ifw.mq.Message;

public class FwMqServiceHelper {

	/**
	 * 创建信息对象
	 * 
	 * @return
	 */
	public static Message createMessage() {
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

	/**
	 * 建立连接工厂
	 * 
	 * @param brokerUrl
	 *            连接参数
	 * @return
	 */
	public static ActiveMQConnectionFactory createConnectionFactory(String brokerUrl, String userName, String password) {
		
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(brokerUrl);
		
		ActiveMQPrefetchPolicy prefetchPolicy = new ActiveMQPrefetchPolicy();
		// 预抓取消息数为1
		prefetchPolicy.setAll(1);
		connectionFactory.setPrefetchPolicy(prefetchPolicy);

		// To enable the consumer threading optimization, set the
		// alwaysSessionAsync option to false
		connectionFactory.setAlwaysSessionAsync(true);
		// disable asynchronous dispatching for fast consumers
		connectionFactory.setDispatchAsync(false);
		// 关闭使用同步发送
		// connectionFactory.setAlwaysSyncSend(false);
		// 打开异步发送
		connectionFactory.setUseAsyncSend(true);
		// 优化应答，会导致批量应答
		// connectionFactory.setOptimizeAcknowledge(true);
		// 压缩消息，会增加CPU负担，提高网络性能
		connectionFactory.setUseCompression(true);

		// 用户
		if (userName != null) {
			connectionFactory.setUserName(userName);
		}

		// 密码
		if (password != null) {
			connectionFactory.setPassword(password);
		}
		return connectionFactory;
	}

}

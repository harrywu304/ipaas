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

/**
 * @author wudie
 * MQ
 */
public interface MqListenService {
	/**
	 * 收听Queue消息.
	 * @param queueName queue名字
	 * @param listener 消息处理器
	 */
	public void listenQueue(String queueName, MessageHandler handler);
	
	/**
	 * 收听Topic消息.
	 * @param topicName topic名字
	 * @param listener 消息处理器
	 */
	public void listenTopic(String topicName, MessageHandler handler);
	
	/**
	 * 持久收听Topic消息.
	 * <p>持久收听跟普通收听的区别是，持久消费者的ID会在MQ的Broker中注册，如果某一消息的持久化消费者有任何一个不在线，
	 * <p>那么在消息过期时间内，该消息会为暂时不在线的持久化消费者保留，待消费者上线后重发；而普通的Topic收听，不会在broker中
	 * <p>注册，消息不会为消费者保留。持久化收听主要用于需要更好地保证Topic广播消息送达的场景。
	 * @param topicName topic名字
	 * @param listener 消息处理器
	 * <p>
	 * @return 该订阅的标识，由框架内部生成，规则是：topicName+'_'+<server_ip> （一台主机对一个topic只能有一个durable订阅）
	 */
	public String listenTopicDurable(String topicName, MessageHandler handler);
	
	/**
	 * 持久收听Topic消息.
	 * <p>持久收听跟普通收听的区别是，持久消费者的ID会在MQ的Broker中注册，如果某一消息的持久化消费者有任何一个不在线，
	 * <p>那么在消息过期时间内，该消息会为暂时不在线的持久化消费者保留，待消费者上线后重发；而普通的Topic收听，不会在broker中
	 * <p>注册，消息不会为消费者保留。持久化收听主要用于需要更好地保证Topic广播消息送达的场景。
	 * @param topicName topic名字
	 * @param handler 消息处理器
	 * @param subscriptionName 订阅的标识
	 */
	public void listenTopicDurable(String topicName, MessageHandler handler,
			String subscriptionName);
	
	/**
	 * 取消对Topic的持久收听.
	 * @param subscriptionName 该消息订阅的标识
	 */
	void unlistenTopicDurable(String subscriptionName);
	
	

	/**
	 * 收听同步Queue信息
	 * 
	 * @param queueName
	 *            queue名字
	 * @param handler
	 *            消息处理器
	 */
	void listenSyncQueue(String queueName, MessageSyncHandler handler);
	
	
	/**
	 * 安全的释放资源
	 */
	void releaseResource();
}

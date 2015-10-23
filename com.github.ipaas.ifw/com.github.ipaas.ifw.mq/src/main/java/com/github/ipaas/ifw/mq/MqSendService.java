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
 * 
 *
 * @author Chenql
 */
public interface MqSendService {
	/**
	 * 创建消息对象.
	 * <p>
	 * 消息对象默认持久化，存活时间是永不过期，优先级是4，发送不进行自动的Json转换
	 * 
	 * @return
	 */
	public Message createMessage();

	/**
	 * 发送Queue消息.
	 * 
	 * @param queueName
	 *            queue名字
	 * @param message
	 *            消息
	 */
	public void sendQueue(String queueName, Message message);

	/**
	 * 发送Topic消息.
	 * 
	 * @param topicName
	 *            topic名字
	 * @param message
	 *            消息
	 */
	public void sendTopic(String topicName, Message message);

	/**
	 * 发送同步的Queue消息
	 * 
	 * @param queueName
	 *            queue名字
	 * @param message
	 *            消息
	 * @return 消息的返回结果
	 */
	Response sendQueueByRpc(String queueName, Message message);

	/**
	 * 停止PooledConnectionFactory
	 */
	void stopPooledConnectionFactory();

}

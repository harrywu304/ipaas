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
package com.github.ipaas.ifw.util;

import javax.jms.Connection;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

/**
 * JMS 工具类
 *
 * @author Chenql
 */
public final class JMSUtil {

	/**
	 * 
	 * 关闭JMS的Connection资源对象
	 * 
	 * @param jmsrsc
	 *            -- JMS连接
	 * @return 返回类型的描述
	 */
	public static void closeSilently(Connection rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭JMS MessageConsumer资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(MessageConsumer rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭JMS Session资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Session rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭JMS MessageProducer资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(MessageProducer rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

}

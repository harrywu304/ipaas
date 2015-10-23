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

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MessageHandler;

/**
 * @author Chenql
 * 
 */
public class TestMessageHandler implements MessageHandler {

	protected static Logger logger = Logger.getLogger(TestMessageHandler.class);

	private static AtomicInteger threadCount = new AtomicInteger();

	private Message msg = null;

	/**
	 * @param mqServiceTest
	 */
	public TestMessageHandler() {
	}

	// 等待接收数据，最多等3秒
	public Message getReceivedMessage() {
		synchronized (this) {
			try {
				wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	public void handle(Message message) {
		// count threads running
		int count = threadCount.incrementAndGet();
		System.out.println("======TestMessageHandler threadCount:" + count);
		// try {
		// Thread.sleep(10);
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		msg = message;
		logger.debug("Received Message[" + message.getContent().toString() + "]");
		threadCount.decrementAndGet();
		synchronized (this) {
			notify();
		}

	}
}

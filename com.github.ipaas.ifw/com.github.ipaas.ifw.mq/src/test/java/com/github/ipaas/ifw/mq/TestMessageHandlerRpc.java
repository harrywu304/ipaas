/**
 */
package com.github.ipaas.ifw.mq;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

/**
 * @author Chenql
 * 
 */
public class TestMessageHandlerRpc implements MessageSyncHandler {

	protected static Logger logger = Logger.getLogger(TestMessageHandlerRpc.class);

	private static AtomicInteger threadCount = new AtomicInteger();

	private Message msg = null;

	/**
	 * @param mqServiceTest
	 */
	public TestMessageHandlerRpc() {
	}

	// 等待接收数据，最多等3秒
	public Message getReceivedMessage() {
		synchronized (this) {
			try {
				wait(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return msg;
	}

	/*
	 * (non-Javadoc)
	 * 
	 */
	public Response handle(Message message) {
		// count threads running
		int count = threadCount.incrementAndGet();
		logger.debug("======TestMessageHandler threadCount:" + count);
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
		Response response = message.createResponse();
		response.setContext("TestMessageHandlerRpc " + message.getContent().toString());
		return response;

	}
}

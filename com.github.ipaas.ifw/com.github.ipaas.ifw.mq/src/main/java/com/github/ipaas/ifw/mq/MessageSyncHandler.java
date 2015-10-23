package com.github.ipaas.ifw.mq;


/**
 * 同步消息处理
 */
public interface MessageSyncHandler {
	/**
	 * 同步消息处理
	 * @param message 
	 * @return 
	 */
	public Response handle(Message message);
}

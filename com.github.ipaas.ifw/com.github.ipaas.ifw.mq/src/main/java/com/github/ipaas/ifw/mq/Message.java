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
 * Message对象
 * @author Chenql
 */

public interface Message {

	/**
	 * 获取消息ID.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * 设置消息ID.
	 * 
	 * @param id
	 */
	public void setId(String id);

	/**
	 * 获取消息内容.
	 * 
	 * @return
	 */
	public Object getContent();

	/**
	 * 设置消息内容.
	 * <p>
	 * 1)内容为String类型，发送不进行Json
	 * <p>
	 * 2)内容为Object类型，autoJsonConvert为true，Object无序列化要求
	 * <p>
	 * 3)内容为Object类型，autoJsonConvert为false，Object要求可序列化，需实现Serializable接口
	 * 
	 * @param content
	 */
	public void setContent(Object content);

	/**
	 * 是否持久化.
	 * 
	 * @return
	 */
	public boolean isPersist();

	/**
	 * 设置是否持久化.
	 * 
	 * @param isPersist
	 */
	public void setPersist(boolean isPersist);

	/**
	 * 获取优先级.
	 * 
	 * @return
	 */
	public int getPriority();

	/**
	 * 设置优先级，0最小9最大，客户端应该使用0～4作为普通优先级，5～9作为快递优先级.
	 * 
	 * @param priority
	 */
	public void setPriority(int priority);

	/**
	 * 获取过期时间.
	 * 
	 * @return 单位为毫秒
	 */
	public long getTimeToLive();

	/**
	 * 设置过期时间.
	 * 
	 * @param timeToLive 单位为毫秒
	 */
	public void setTimeToLive(long timeToLive);

	/**
	 * 是否需要自动转化成Json格式传输.
	 * 
	 * @return
	 */
	public boolean isAutoJsonConvert();

	/**
	 * 设置是否需要自动转化成Json格式传输.
	 * 
	 * @param isJson
	 */
	public void setAutoJsonConvert(boolean autoJsonConvert);

	/**
	 * content字段如果是String，自动进行Json的String to Bean转换；如果是其他类型，则进行强制类型转换 
	 * 
	 * @param klass
	 * @return 转换后的对象
	 * @throws ClassCastException 强制类型转换出错
	 */
	public <T> T getContent(Class<T> klass);
	
	/**
	 * 仓建返回的信息
	 * @return
	 */
	public Response createResponse();

}

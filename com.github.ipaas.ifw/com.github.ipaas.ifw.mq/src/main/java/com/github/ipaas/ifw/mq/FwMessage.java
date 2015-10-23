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

import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 消息对象
 * 
 */
public class FwMessage implements Message {

	private String id;

	private Object content;

	private boolean isPersist;

	private int priority;

	private long timeToLive;

	private boolean isJson;

	public FwMessage() {

	}

	/**
	 * content字段如果是String，自动进行Json的String to Bean转换；如果是其他类型，则进行强制类型转换
	 * 注意会抛出ClassCastException
	 * 
	 * @param klass
	 * @return 转换后的对象
	 */
	public <T> T getContent(Class<T> klass) {
		if (content instanceof String) {
			return JsonUtil.toBean((String) content, klass);
		} else {
			return klass.cast(content);
		}
	}

	public String getId() {
		return id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Object getContent() {
		return content;
	}

	public void setContent(final Object content) {
		this.content = content;
	}

	public boolean isPersist() {
		return isPersist;
	}

	public void setPersist(final boolean isPersist) {
		this.isPersist = isPersist;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(final int priority) {
		this.priority = priority;
	}

	public long getTimeToLive() {
		return timeToLive;
	}

	public void setTimeToLive(final long timeToLive) {
		this.timeToLive = timeToLive;
	}

	public boolean isAutoJsonConvert() {
		return isJson;
	}

	public void setAutoJsonConvert(boolean isJson) {
		this.isJson = isJson;
	}

	@Override
	public Response createResponse() {
		FwResponse response = new FwResponse();
		response.setRequestId(this.getId());
		response.setStatusCode(FwResponse.SUCCESS_CODE);
		return response;
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append("id:" + id + ",");
		result.append("isPersist:" + isPersist + ",");
		result.append("isJson:" + isJson + ",");
		result.append("priority:" + priority + ",");
		result.append("timeToLive:" + timeToLive + ",");
		result.append("content:" + content);
		return result.toString();
	}

}

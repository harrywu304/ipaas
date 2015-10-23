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

package com.github.ipaas.ifw.server.ice;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Chenql
 *  ICE线程池实际线程数计数器
 */

public class ComponentServerThreadNotification implements Ice.ThreadNotification {

	private AtomicInteger threadCount = new AtomicInteger();

	public Integer getThreadCount() {
		return threadCount.get();
	}

	/**
	 * ICE线程池创建线程时该方法被调用
	 */
	public void start() {
		threadCount.incrementAndGet();
	}

	/**
	 * ICE线程池销毁线程时该方法被调用
	 */
	public void stop() {
		threadCount.decrementAndGet();
	}
}

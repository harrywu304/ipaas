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
package com.github.ipaas.ifw.server.ice.mbean;

/**
 * Ice监控接口
 *
 * @author Chenql
 */
public interface CmptServerMonitorMBean {

	/**
	 * 返回 Ice.Util.stringVersion
	 * 
	 * @return ice version
	 */
	public String getIceVersion();

	/**
	 * 返回 Ice.MessageSizeMax
	 * 
	 * @return 指定参数
	 */
	public long getMessageSizeMax();

	/**
	 * 返回ice的总请求数
	 * 
	 * @return ice的总请求数
	 */
	public long getIceRequestCountTotal();

	/**
	 * 返回 Ice.ThreadPool.Server.Serialize
	 * 
	 * @return 指定参数
	 */
	public int getThreadPoolSerialize();

	/**
	 * 返回 Ice.ThreadPool.Server.Size，最小线程数
	 * 
	 * @return 指定参数
	 */
	public int getThreadPoolSize();

	/**
	 * 返回 Ice.ThreadPool.Server.SizeMax，最大线程数
	 * 
	 * @return 指定参数
	 */
	public int getThreadPoolSizeMax();

	/**
	 * 返回 Ice.ThreadPool.Server.SizeWarn，警告线程数
	 * 
	 * @return 指定参数
	 */
	public int getThreadPoolSizeWarn();

	/**
	 * 返回 Ice.ThreadPool.Server.StackSize
	 * 
	 * @return 指定参数
	 */
	public int getThreadPoolStackSize();

	/**
	 * 返回线程池中实际线程数
	 * 
	 * @return 指定参数
	 */
	public int getThreadPoolSizeAct();

	/**
	 * 返回 Ice.TCP.RcvSize
	 * 
	 * @return 指定参数
	 */
	public long getTCPRcvSize();

	/**
	 * 返回 Ice.TCP.SndSize
	 * 
	 * @return 指定参数
	 */
	public long getTCPSndSize();

	/**
	 * 返回 Ice.TCP.RcvSize
	 * 
	 * @return 指定参数
	 */
	public long getUDPRcvSize();

	/**
	 * 返回 Ice.TCP.SndSize
	 * 
	 * @return 指定参数
	 */
	public long getUDPSndSize();

	/**
	 * 返回Ice.GC.Interval 垃圾回收器运行时间间隔
	 * 
	 * @return 指定参数
	 */
	public int getGCInterval();

}

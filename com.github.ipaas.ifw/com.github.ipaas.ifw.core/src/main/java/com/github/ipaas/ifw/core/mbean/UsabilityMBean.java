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
package com.github.ipaas.ifw.core.mbean;

/**
 * 可用性检测MBean的基类借口
 * 
 * @author Chenql
 * 
 */
public interface UsabilityMBean {

	/**
	 * 获取服务Id
	 * 
	 * @return
	 */
	public String getServiceId();

	/**
	 * 
	 * 获取目标IP地址
	 * 
	 * @return 返回目标IP地址
	 */
	public String getTargetIp();

	/**
	 * 
	 * 获取目标端口
	 * 
	 * @return 返回目标端口
	 */
	public String getTargetPort();

	/**
	 * 
	 * 获取 检测URI地址.
	 * 
	 * @return Uri字符串
	 */
	public String getUri();

}

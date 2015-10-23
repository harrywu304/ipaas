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
package com.github.ipaas.ifw.component.client.ice.usability;

import com.github.ipaas.ifw.core.mbean.UsabilityMBean;

/**
 * @author Chenql
 *
 */
public interface IceHealthCheckMBean extends UsabilityMBean {

	/**
	 * 
	 * 获取轻量级检测的可用性状态
	 * 
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	public String getLightState();

	/**
	 * 
	 * 获取重量级检测状态
	 * 
	 * @return 返回字符串表示的可用性状态。1表示可用，其他表示不可用的信息
	 */
	public String getHeavyState();
}

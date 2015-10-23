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

import com.github.ipaas.ifw.core.Version;

/**
 * 获取框架的版本
 * 
 * @author Chenql
 */
public class Fw implements FwMBean {

	/**
	 * 可用性监控domain
	 */
	public static String FW_USABILITY_DOMAIN = "com.github.ipaas.ifw.mbean.usability";

	@Override
	public String getVersion() {
		return Version.VERSION;
	}

}

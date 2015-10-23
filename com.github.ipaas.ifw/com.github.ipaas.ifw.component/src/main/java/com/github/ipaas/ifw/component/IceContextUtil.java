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
package com.github.ipaas.ifw.component;

import Ice.Current;

/**
 * 提供ICE上下文数据存储方法
 * 
 * @author Chenql
 *
 */
public class IceContextUtil {

	/**
	 * 从Current取得ICE客户端的IP内网地址
	 * 
	 * @param current
	 *            -- 上下文
	 * @return 客户端ip
	 */
	public static String getClientIP(Current current) {
		String sourceip = current.ctx.get("sourceIp");
		return sourceip;
	}
}

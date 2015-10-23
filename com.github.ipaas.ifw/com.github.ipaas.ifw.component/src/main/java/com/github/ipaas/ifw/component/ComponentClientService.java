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

/**
 * 组件客户端服务
 * @author Chenql
 */
public interface ComponentClientService {

	 
	
	/**
	 * 获取指定组件服务类型(clazz)的本地代理对象
	 * @param clazz -- 组件服务代理类，如ice接口为DemoZero,则该参数为DemoZeroPrx
	 * @return -- 本地代理对象
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	<K> K getProxy(Class<K> clazz);
	
	/**
	 * 获取指定组件服务类型(clazz)的本地代理对象，指定特定的操作过期时间
	 * @param clazz -- 组件服务代理类，如ice接口为DemoZero,则该参数为DemoZeroPrx
	 * @param operateTimeout 指定的操作超时时间，单位毫秒
	 * @return -- 本地代理对象
	 * @exception -- 运行时应用异常(RuntimeAppException)
	 */
	<K> K getProxy(Class<K> clazz, int operateTimeout);

}

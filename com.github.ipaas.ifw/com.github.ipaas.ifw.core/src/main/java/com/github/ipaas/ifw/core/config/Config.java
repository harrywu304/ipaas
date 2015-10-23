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
package com.github.ipaas.ifw.core.config;

import java.util.Map;

/**
 * 配置对象
 *
 * @author Chenql
 */
public interface Config {

	/**
	 * 获取配置项
	 * 
	 * @param <T>
	 *            -- 多态类型
	 * @param name
	 *            -- 配置项名称
	 * @return -- 配置值
	 */
	<T> T getItem(String name);

	/**
	 * 获取配置项
	 * 
	 * @param <T>
	 *            -- 多态类型
	 * @param name
	 *            -- 配置项名称
	 * @param defaultValue
	 *            -- 默认值,如果没有找到配置,返回默认值
	 * @return -- 配置值
	 */
	<T> T getItem(String name, T defaultValue);

	Map<String, Object> getDataMap();

}

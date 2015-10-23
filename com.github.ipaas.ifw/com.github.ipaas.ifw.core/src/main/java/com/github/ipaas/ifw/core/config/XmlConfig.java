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
 * xml config实现
 * 
 * @author wudie
 */
public class XmlConfig implements Config {

	private Map<String, Object> data;

	public XmlConfig(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public <T> T getItem(String name) {
		if (data == null) {
			return null;
		}
		return (T) data.get(name);
	}

	@Override
	public <T> T getItem(String name, T defaultValue) {
		T v = (T) getItem(name);
		if (v == null) {
			v = defaultValue;
		}
		return v;
	}

	@Override
	public String toString() {
		return "XmlConfig [data=" + data + "]";
	}

	@Override
	public Map<String, Object> getDataMap() {
		return data;
	}
}

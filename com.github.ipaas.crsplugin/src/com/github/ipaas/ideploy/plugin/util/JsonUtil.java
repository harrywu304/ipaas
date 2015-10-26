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

package com.github.ipaas.ideploy.plugin.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 
 * JSON 工具类
 * 
 * @author Chenql  
 */
public final class JsonUtil {

	private static JsonFactory jsonFactory = new JsonFactory();

	private static ObjectMapper mapper = null;

	static {
		jsonFactory.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
		jsonFactory.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
		mapper = new ObjectMapper(jsonFactory);
	}

	/**
	 * 获取jackson json lib的ObjectMapper对象
	 * 
	 * @return -- ObjectMapper对象
	 */
	public static ObjectMapper getMapper() {
		return mapper;
	}

	/**
	 * 获取jackson json lib的JsonFactory对象
	 * 
	 * @return -- JsonFactory对象
	 */
	public static JsonFactory getJsonFactory() {
		return jsonFactory;
	}

	/**
	 * 将json转成java bean
	 * 
	 * @param <T>
	 *            -- 多态类型
	 * @param json
	 *            -- json字符串
	 * @param clazz
	 *            -- java bean类型(Class)
	 * @return -- java bean对象
	 */
	public static <T> T toBean(String json, Class<T> clazz) {

		T rtv = null;
		try {
			rtv = mapper.readValue(json, clazz);
		} catch (Exception ex) {
			throw new IllegalArgumentException("json字符串转成java bean异常", ex);
		}
		return rtv;
	}

	/**
	 * 将java bean转成json
	 * 
	 * @param bean
	 *            -- java bean
	 * @return -- json 字符串
	 */
	public static String toJson(Object bean) {

		String rtv = null;
		try {
			rtv = mapper.writeValueAsString(bean);
		} catch (Exception ex) {
			throw new IllegalArgumentException("java bean转成json字符串异常", ex);
		}
		return rtv;
	}
}
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

package com.github.ipaas.ifw.front.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 后端数据提供仿真工具类
 * 
 * @author wuxh
 * @date Nov 29, 2013 9:43:34 AM
 */
public class FisModelSimulator {

	private static Map<String, Map<String, Object>> cache = new HashMap<String, Map<String, Object>>();

	private static final Logger logger = LoggerFactory.getLogger(FisModelSimulator.class);

	/**
	 * 
	 * 读取json文件的数据做为模拟数据
	 * 
	 * @param jsonFile
	 *            json文件,例如./data/test_data.json
	 * @return 返回类型的描述
	 * @exception 异常信息的描述
	 */
	public static Map<String, Object> getJSON(String jsonFile) {
		if (cache.containsKey(jsonFile)) {
			return cache.get(jsonFile);
		}
		Map<String, Object> data = new HashMap<String, Object>();
		// 加载配置数据
		BufferedReader br = null;
		try {
			String fileName = FisModelSimulator.class.getClassLoader().getResource(jsonFile).getPath();
			File dataFile = new File(fileName);

			ObjectMapper mapper = new ObjectMapper();
			data.putAll(mapper.readValue(dataFile, HashMap.class));
			cache.put(jsonFile, data);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("read json file error", e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}
		}
		return data;
	}
}

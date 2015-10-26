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

import java.io.IOException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * 
 * 类或接口作用描述
 * 
 * @author Chenql  
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class XmlUtil {

	public static String ASSEMBLY_ELEMENT = "build.plugins.plugin.configuration.descriptors.descriptor";

	public static String BUILD_NAME_ELEMENT = "build.finalName";
	public static String ARTIFACTID_ELEMENT = "artifactId";
	public static String VERSION_ELEMENT = "version";

	public static String ICE_CONTAINER = "ice";

	/**
	 * 获取指定节点的值
	 * 
	 * @param XML配置文件
	 * @param elementNames
	 *            元素名 多层元素用 . 分割
	 * @param dufaultValue
	 *            缺省值
	 * @return
	 */
	public static String getString(String fileName, String elementNames, String defaultValue) {
		XMLConfiguration config;
		try {
			config = new XMLConfiguration(fileName);
		} catch (ConfigurationException e) {
			ConsoleHandler.error("读取配置文件[" + fileName + "]出错:" + e.getMessage());
			return null;
		}
		// 对于单独元素的话，可以直接通过标签名获取值
		String str = config.getString(elementNames, defaultValue);
		return str;
	}

	public static String getString(String fileName, String elementNames) {
		XMLConfiguration config;
		try {
			config = new XMLConfiguration(fileName);
		} catch (ConfigurationException e) {
			ConsoleHandler.error("读取配置文件[" + fileName + "]出错:" + e.getMessage());
			return null;
		}
		// 对于单独元素的话，可以直接通过标签名获取值
		String str = config.getString(elementNames);
		return str;
	}

	public static void main(String[] args) throws IOException {
		System.out.println(XmlUtil.getString(
				"D:/Users/TY-Chenql/runtime-EclipseApplication/crs_mave_ice/script/assembly.xml", "id", "sc"));
		try {
			XMLConfiguration config = new XMLConfiguration("D:/Users/TY-Chenql/workspace/crs_mave_ice/pom.xml");
			// 对于单独元素的话，可以直接通过标签名获取值
			String str = config.getString("build.plugins.plugin.configuration.descriptors.descriptor");
			// System.out.println(str);
			// // 对于循环出现的嵌套元素，可以通过父元素.子元素来获取集合值
			// List<Object> names = config.getList("student.name");
			// System.out.println(Arrays.toString(names.toArray()));
			// // 对于一个单独元素包含的值有多个的话如：a,b,c,d 可以通过获取集合
			// List<Object> titles = config.getList("title");
			// System.out.println(Arrays.toString(titles.toArray()));
			// // 对于标签元素的属性，可以通过 标签名[@属性名] 这样的方式获取
			// String size = config.getString("ball[@size]");
			// System.out.println(size);
			// // 对于嵌套的标签的话，想获得某一项的话可以通过 标签名(索引名) 这样方式获取
			// String id = config.getString("student(1)[@id]");
			// System.out.println(id);
			//
			// String go = config.getString("student.name(0)[@go]");
			// System.out.println(go);
			// /**
			// * 依次输出结果为 tom [lily, lucy] [abc, cbc, bbc, bbs] 20 2 common1
			// *
			// */
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}

	}
}

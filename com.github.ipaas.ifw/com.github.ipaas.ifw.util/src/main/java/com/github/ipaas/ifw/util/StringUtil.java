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
package com.github.ipaas.ifw.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URLEncoder;

/**
 * StringUtil.java
 *
 * @author Chenql
 */
public class StringUtil {

	/**
	 * 取得hash值，可能返回负数 备注:使用{@link HashAlgorithmUtil}.getDJBHash代替此方法
	 * 
	 * @param str
	 * @return
	 */
	@Deprecated
	public static long getDJBHash(String str) {
		long hash = 5381;
		for (int i = 0; i < str.length(); i++) {
			hash = (hash << 5) + hash + str.charAt(i);
		}
		return hash;
	}

	/**
	 * 字符串编码函数。
	 * 
	 * @param str
	 * @param srcCode
	 * @param targetCode
	 * @return
	 */
	public static String encodeStr(String str, String targetCode) {
		try {
			if (str == null) {
				return null;
			}
			byte[] bytesStr = str.getBytes();
			return new String(bytesStr, targetCode);
		} catch (Exception ex) {
			return str;
		}
	}

	/**
	 * 字符串编码函数。
	 * 
	 * @param str
	 * @param srcCode
	 * @param targetCode
	 * @return
	 */
	public static String encodeStr(String str, String srcCode, String targetCode) {
		try {
			if (str == null) {
				return null;
			}

			byte[] bytesStr = str.getBytes(srcCode);
			return new String(bytesStr, targetCode);
		} catch (Exception ex) {
			return str;
		}
	}

	/**
	 * 判断字符串是否为空字符。
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isBlank(String value) {
		boolean ret = false;
		if (value != null && value.equals("")) {
			ret = true;
		}
		return ret;
	}

	/**
	 * 判断字符串是否为null。
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNull(String value) {
		return value == null ? true : false;
	}

	/**
	 * 判断字符串是否为空字符串或者null。
	 * 
	 * @param value
	 * @return
	 */
	public static boolean isNullOrBlank(String value) {
		return isNull(value) || isBlank(value);
	}

	/**
	 * 截取字符串前面部分字符,后面加省略号.
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String trimWords(String str, int length) {
		String wordStr = str;

		if (wordStr == null) {
			return "";
		}
		if (wordStr.length() <= length) {
			return wordStr;
		}

		wordStr = wordStr.substring(0, length);
		wordStr += "...";
		return wordStr;
	}

	/**
	 * 编码带有中文名称Url。
	 * 
	 * @param url
	 *            url中的中文
	 * @return
	 */
	public static String encodeUrl(String url) {
		return encodeUrl(url, "gbk");
	}

	/**
	 * 编码带有中文名称Url。
	 * 
	 * @param url
	 *            url中的中文
	 * @param targetCode
	 *            目标字符
	 * @return
	 */
	public static String encodeUrl(String url, String targetCode) {
		String encodeUrl = "";
		if (StringUtil.isNullOrBlank(url)) {
			return "";
		}
		// 编码并转换空格
		try {
			encodeUrl = URLEncoder.encode(url, targetCode);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return encodeUrl;
	}

	/**
	 * 用指定的分隔符合并数组为单个字符串
	 * 
	 * @param array
	 *            要合并的数组
	 * @param separator
	 *            分隔符
	 * @return
	 */
	public static String joinArray(Object[] array, String separator) {
		if (array == null) {
			return "";
		}
		int startIndex = 0;
		int endIndex = array.length;
		int bufSize = endIndex - startIndex;
		if (bufSize <= 0) {
			return "";
		}

		bufSize *= (array[startIndex] == null ? 16 : array[startIndex].toString().length()) + 1;
		StringBuffer buf = new StringBuffer(bufSize);

		for (int i = startIndex; i < endIndex; i++) {
			if (i > startIndex) {
				buf.append(separator);
			}
			if (array[i] != null) {
				buf.append(array[i]);
			}
		}
		return buf.toString();
	}

	/**
	 * 过滤内容中的标点符号、空白字符（包括全角空格）
	 * 
	 * @param content
	 *            被过滤内容
	 * @return 过滤后内容
	 */
	public static String filterPunctuation(String content) {
		if (StringUtil.isNullOrBlank(content)) {
			return content;
		}
		// 过滤标点符号：!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
		// 过滤空白字符：[ \t\n\x0B\f\r] 和 　全角空格
		return content.replaceAll("\\p{Punct}|\\p{Space}|　", "");
	}

	/**
	 * get camel style string，eg. last_action_time -> lastActionTime
	 * 
	 * @param str
	 * @return
	 */
	public static String getCamelCaseString(String str) {
		if (str == null) {
			return "";
		}
		str = str.toLowerCase();
		StringBuilder result = new StringBuilder(str.length());
		boolean toCapitalize = false;
		for (int i = 0; i < str.length(); i++) {
			char c = str.charAt(i);
			if (c >= 'a' && c <= 'z' || c >= '0' && c <= '9') {
				if (toCapitalize) {
					result.append(Character.toUpperCase(c));
					toCapitalize = false;
				} else {
					result.append(c);
				}
			} else {
				toCapitalize = true;
			}
		}
		return result.toString();
	}

	/**
	 * 
	 * 将异常堆栈转换成字符串.
	 * 
	 * @param t
	 *            -- 异常
	 * @return 返回异常堆栈的字符串表示
	 * 
	 */
	public static String getExceptionAsStr(Throwable t) {
		String exptStr = null;
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		exptStr = sw.toString();
		if (null != sw) {
			try {
				sw.close();
			} catch (Exception e) {
				/* 忽略异常 */
			}
		}
		if (null != pw) {
			pw.close();
		}
		return exptStr;
	}

	/**
	 * 字符串折分
	 * 
	 * @param str
	 *            原字符
	 * @param split
	 *            折分标志
	 * @return 折分的字符数组
	 */
	public static String[] split(String str, String split) {
		if (str != null) {
			String[] splitStr = str.split(split);
			if (splitStr != null) {
				for (int i = 0; i < splitStr.length; i++) {
					splitStr[i] = splitStr[i].trim();
				}
			}
			return splitStr;
		}
		return null;
	}
}

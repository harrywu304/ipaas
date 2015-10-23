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

import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import org.apache.log4j.Logger;

/**
 * 
 * Cookie操作类，封装cookie操作，支持key-value格式的cookie。
 * 
 * @author Chenql
 */
public class CookieUtil {

	private static Logger logger = Logger.getLogger(CookieUtil.class);

	public static String getCookieValue(Cookie[] cookies, String cookieName) {
		if (cookies == null) {
			return "";
		}

		String value = null;
		for (int i = 0; i < cookies.length; i++) {
			Cookie cookie = cookies[i];
			if (cookieName.equals(cookie.getName())) {
				value = cookie.getValue();
				value = unescape(value);
				break;
			}
		}

		if (value == null) {
			return "";
		}

		return value;
	}

	/**
	 * 根据cookie name 获得其值 cookie格式为:cookieName=w=xxx&id=xxx
	 * 
	 * @param cookies
	 * @param cookieName
	 * @return
	 */
	public static String getCookieValue(Cookie[] cookies, String cookieName, String key) {
		if (cookies == null) {
			return "";
		}

		String cookieStr = null;
		String value = "";
		try {
			for (int i = 0; i < cookies.length; i++) {
				Cookie cookie = cookies[i];
				if (cookieName.equals(cookie.getName())) {
					cookieStr = cookie.getValue();
					cookieStr = unescape(cookieStr);
					break;
				}
			}

			if (cookieStr == null || "".equals(cookieStr)) {
				return "";
			}

			String validKey = key + "=";

			// 对cookieStr进行解析
			StringTokenizer st = new StringTokenizer(cookieStr, "&");
			while (st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.indexOf(validKey) != -1) {
					value = token.substring(token.indexOf("=") + 1, token.length());

					value = unescape(value);
					break;
				}

			}
		} catch (Exception ex) {
			logger.error("解析cookie出错:cookieStr=" + cookieStr, ex);
			value = "";
		}

		return value;
	}

	/**
	 * 实现对应的JavaScript escape函数．
	 * 
	 * @param src
	 *            要编码的字符串。
	 * @return
	 */
	public static String escape(String src) {
		if (src == null) {
			return null;
		}

		int i;
		char j;
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length() * 6);
		for (i = 0; i < src.length(); i++) {
			j = src.charAt(i);
			if (j < 256) { // 1字节
				// 字母或者数字
				if (Character.isLetterOrDigit(j)) {
					tmp.append(j);
				} else {
					tmp.append("%u00");

					// 0.5个字节
					if (j < 16) {
						tmp.append("0");
					}

					tmp.append(Integer.toString(j, 16));
				}
			} else {
				tmp.append("%u");

				// 1.5个字节
				if (j < 0x1000) {
					tmp.append("0");
				}

				tmp.append(Integer.toString(j, 16));
			}
		}
		return tmp.toString();
	}

	/**
	 * 实现对应的JavaScript unescape函数．
	 * 
	 * @param src
	 *            要解码的字符串。
	 * @return
	 */
	public static String unescape(String src) {
		if (src == null) {
			return null;
		}

		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}

		return tmp.toString();
	}
}
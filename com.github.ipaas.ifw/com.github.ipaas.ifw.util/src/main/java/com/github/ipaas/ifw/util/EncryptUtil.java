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

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密有关的工具类
 * 
 * @author whx
 *
 */
public class EncryptUtil {

	/**
	 * 获取内容md5加密后的字符串
	 * 
	 * @param content
	 *            加密内容
	 * @param charset
	 *            字符集
	 * @return
	 */
	public static String encryptMD5(String content, String charset) {
		StringBuilder result = new StringBuilder();
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			md.update(content.getBytes(charset));
			byte[] byteDigest = md.digest();
			for (int i = 0; i < byteDigest.length; i++) {
				String tmpStr = Integer.toHexString(0xFF & byteDigest[i]);
				if (tmpStr.length() == 1) {
					result.append("0").append(tmpStr);
				} else
					result.append(tmpStr);
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Charset not supported", e);
		}
		return result.toString();
	}

}

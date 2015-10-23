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
 * hash算法工具类
 * 
 * @author Chenql
 */
public final class HashAlgorithmUtil {

	/**
	 * 获取DJB hash算法的hash值 备注: DJB hash function，俗称'Times33'算法 特点:
	 * 算法简单,性能高(cpu不敏感)
	 * 
	 * @param key
	 *            -- 被hash的key
	 * @return -- hash值
	 */
	public static long getDJBHash(String key) {

		long hash = 5381;
		for (int i = 0; i < key.length(); i++) {
			hash = ((hash << 5) + hash) + key.charAt(i);
		}
		return hash;
	}

	/**
	 * 获取Ketama hash算法的hash值 备注: Ketama hash算法:key进行md5,然后取最高八个字节作为long类型的hash值
	 * 特点: 优先保证hash的分布均匀性
	 * 
	 * @param key
	 *            -- 被hash的key
	 * @return -- hash值
	 */
	public static long getKemataHash(String key) {

		MessageDigest md5 = null;
		try {
			md5 = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("MD5 not supported", e);
		}

		byte[] rtv = null;
		synchronized (md5) { // md5 implement is un-thread-safty
			md5.reset();
			byte[] codes = null;
			try {
				codes = key.getBytes("UTF-8");
			} catch (UnsupportedEncodingException ex) {
				new RuntimeException(ex);
			}
			md5.update(codes);
			rtv = md5.digest();
		}
		return ((long) rtv[rtv.length - 1] << 56) + ((long) (rtv[rtv.length - 2] & 255) << 48)
				+ ((long) (rtv[rtv.length - 3] & 255) << 40) + ((long) (rtv[rtv.length - 4] & 255) << 32)
				+ ((long) (rtv[rtv.length - 5] & 255) << 24) + ((rtv[rtv.length - 6] & 255) << 16)
				+ ((rtv[rtv.length - 7] & 255) << 8) + ((rtv[rtv.length - 8] & 255) << 0);
	}

	/**
	 * 获取BKDR hash算法的hash值 备注: BKDRHash算法，比DJBHash的冲突率更小 特点: 算法简单,性能高(cpu不敏感)
	 * 
	 * @param key
	 *            -- 被hash的key
	 * @return -- hash值
	 */
	public static long getBKDRHash(String key) {
		long hash = 0;
		int seed = 131; // 31 131 1313 13131 131313 etc..
		for (int i = 0; i < key.length(); i++) {
			hash = hash * seed + key.charAt(i);
		}
		return hash;
	}

	/**
	 * 防止非法实例化
	 */
	private HashAlgorithmUtil() {
	}

}

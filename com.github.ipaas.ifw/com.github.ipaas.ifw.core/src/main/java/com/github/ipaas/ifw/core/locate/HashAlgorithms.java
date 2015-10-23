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

package com.github.ipaas.ifw.core.locate;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * hash算法工厂 备注: 提供多种hash算法的实现
 * 
 * @author Chenql
 */
public final class HashAlgorithms {

	/**
	 * java内置hash算法
	 */
	public static final HashAlgorithm JAVA_NATIVE_HASH = new JavaNativeHash();

	/**
	 * ketama hash算法: key进行md5,然后取最高八个字节作为long类型的hash值
	 */
	public static final HashAlgorithm KEMATA_HASH = new KemataHash();

	/**
	 * DJB hash算法: DJB hash function，俗称'Times33'算法
	 */
	public static final HashAlgorithm DJB_HASH = new DJBHash();

	/**
	 * 一致性hash算法, 值范围[0, 2^32)=[0, 4294967295]
	 */
	public static final HashAlgorithm CONSISTENT_HASH = new ConsistentHash(KEMATA_HASH);

	/**
	 * 防止非法实例化
	 */
	private HashAlgorithms() {
	}

	/**
	 * java内置hash算法
	 * 
	 * @author yuyoo (yuyoo4j@163.com)
	 * @date 2009-12-11 上午09:28:56
	 */
	public static class JavaNativeHash implements HashAlgorithm {

		public long hash(String key) {

			return key.hashCode();
		}
	}

	/**
	 * kemata hash 算法
	 * 
	 * @author yuyoo (yuyoo4j@163.com)
	 * @date 2009-12-11 上午09:39:28
	 */
	public static class KemataHash implements HashAlgorithm {

		private MessageDigest md5 = null;

		private KemataHash() {

			try {
				md5 = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e) {
				throw new RuntimeException("MD5 not supported", e);
			}
		}

		public long hash(String key) {

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
	}

	/**
	 * DJB hash 算法
	 * 
	 * @author yuyoo (yuyoo4j@163.com)
	 * @date 2009-12-11 上午09:40:32
	 */
	public static class DJBHash implements HashAlgorithm {

		public long hash(String key) {

			long hash = 5381;
			for (int i = 0; i < key.length(); i++) {
				hash = ((hash << 5) + hash) + key.charAt(i);
			}
			return hash;
		}
	}

	/**
	 * 一致性hash 算法 <br>
	 * 备注: 一致性hash的值范围[0, 2^32)=[0, 4294967295]
	 * 
	 * @author zhandl(zhandl@hainan.net)
	 * @time 2009-12-24 下午04:34:26
	 */
	public static class ConsistentHash implements HashAlgorithm {

		private HashAlgorithm inner = null;

		public ConsistentHash(HashAlgorithm inner) {
			this.inner = inner;
		}

		/*
		 * 经过hash后再逻辑右移(>>>)32bit,控制值范围[0, 2^32)=[0, 4294967295]
		 * 
		 */
		public long hash(String key) {
			return inner.hash(key) & 0xffffffff;
		}
	}
}

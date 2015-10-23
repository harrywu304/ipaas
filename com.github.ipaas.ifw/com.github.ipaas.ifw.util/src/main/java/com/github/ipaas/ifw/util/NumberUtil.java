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

import java.text.DecimalFormat;

/**
 * 提供一些跟数字相关的方法。
 * 
 * @author Chenql
 */
public class NumberUtil {

	/**
	 * 将字节转化为合适的单位输出保留两位有效数字
	 * 
	 * @param long
	 * @return String
	 */
	public static String convertByteToGMKB(long bytes) {

		String gmkb = null;
		DecimalFormat df = new DecimalFormat("0.##");
		if (bytes < 1024) {
			gmkb = String.valueOf(bytes) + "B";
		} else if (bytes >= 1024 && bytes < 1024 * 1024) {
			double kb = (double) bytes / 1024;
			gmkb = df.format(kb) + "K";
		} else if (bytes >= 1024 * 1024 && bytes < 1024 * 1024 * 1024) {
			double mb = (double) bytes / 1024 / 1024;
			gmkb = df.format(mb) + "M";
		} else if (bytes >= 1024 * 1024 * 1024) {
			double gb = (double) bytes / 1024 / 1024 / 1024;
			gmkb = df.format(gb) + "G";
		}
		return gmkb;
	}

	/**
	 * 将数据以千为单位格式花输出
	 * 
	 * @param long
	 * @return String
	 */
	public static String formatLong(long dataLong) {

		DecimalFormat df = new DecimalFormat("###,###");
		return df.format(dataLong);

	}
}

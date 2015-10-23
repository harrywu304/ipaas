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

import java.math.BigInteger;

/**
 * convert util
 * 
 * @author Chenql
 *
 */
public class ConvertUtil {

	/**
	 * 将value对象转成long
	 * 
	 * @param value
	 *            -- 数字类型的对象
	 * @return -- long值
	 */
	public static long toLong(Object value) {

		long val = 0;
		if (Long.class.isInstance(value)) {
			Long l = (Long) value;
			val = l.longValue();
		} else if (Integer.class.isInstance(value)) {
			Integer i = (Integer) value;
			val = i.longValue();
		} else if (BigInteger.class.isInstance(value)) {
			BigInteger bi = (BigInteger) value;
			val = bi.longValue();
		} else if (Double.class.isInstance(value)) {
			Double d = (Double) value;
			val = d.longValue();
		} else if (Short.class.isInstance(value)) {
			Short s = (Short) value;
			val = s.longValue();
		}
		return val;
	}

	/**
	 * 将value对象转成int
	 * 
	 * @param value
	 *            -- 数字类型的对象
	 * @return -- int值
	 */
	public static int toInt(Object value) {

		int val = 0;
		if (Long.class.isInstance(value)) {
			Long l = (Long) value;
			val = l.intValue();
		} else if (Integer.class.isInstance(value)) {
			Integer i = (Integer) value;
			val = i.intValue();
		} else if (BigInteger.class.isInstance(value)) {
			BigInteger bi = (BigInteger) value;
			val = bi.intValue();
		} else if (Double.class.isInstance(value)) {
			Double d = (Double) value;
			val = d.intValue();
		} else if (Short.class.isInstance(value)) {
			Short s = (Short) value;
			val = s.intValue();
		}
		return val;
	}

	/**
	 * 将value对象转成float
	 * 
	 * @param value
	 *            -- 数字类型的对象
	 * @return -- float值
	 */
	public static float toFloat(Object value) {

		float val = 0;
		if (Long.class.isInstance(value)) {
			Long l = (Long) value;
			val = l.floatValue();
		} else if (Integer.class.isInstance(value)) {
			Integer i = (Integer) value;
			val = i.floatValue();
		} else if (BigInteger.class.isInstance(value)) {
			BigInteger bi = (BigInteger) value;
			val = bi.floatValue();
		} else if (Double.class.isInstance(value)) {
			Double d = (Double) value;
			val = d.floatValue();
		} else if (Short.class.isInstance(value)) {
			Short s = (Short) value;
			val = s.floatValue();
		}
		return val;
	}

	/**
	 * 将value对象转成short
	 * 
	 * @param value
	 *            -- 数字类型的对象
	 * @return -- short值
	 */
	public static short toShort(Object value) {

		short val = 0;
		if (Long.class.isInstance(value)) {
			Long l = (Long) value;
			val = l.shortValue();
		} else if (Integer.class.isInstance(value)) {
			Integer i = (Integer) value;
			val = i.shortValue();
		} else if (BigInteger.class.isInstance(value)) {
			BigInteger bi = (BigInteger) value;
			val = bi.shortValue();
		} else if (Double.class.isInstance(value)) {
			Double d = (Double) value;
			val = d.shortValue();
		} else if (Short.class.isInstance(value)) {
			Short s = (Short) value;
			val = s.shortValue();
		}
		return val;
	}

	/**
	 * 将value对象转成double
	 * 
	 * @param value
	 *            -- 数字类型的对象
	 * @return -- double值
	 */
	public static double toDouble(Object value) {

		double val = 0;
		if (Long.class.isInstance(value)) {
			Long l = (Long) value;
			val = l.doubleValue();
		} else if (Integer.class.isInstance(value)) {
			Integer i = (Integer) value;
			val = i.doubleValue();
		} else if (BigInteger.class.isInstance(value)) {
			BigInteger bi = (BigInteger) value;
			val = bi.doubleValue();
		} else if (Double.class.isInstance(value)) {
			Double d = (Double) value;
			val = d.doubleValue();
		} else if (Short.class.isInstance(value)) {
			Short s = (Short) value;
			val = s.doubleValue();
		}
		return val;
	}

	/**
	 * 将value对象转成boolean
	 * 
	 * @param value
	 *            -- Boolean类型的对象
	 * @return -- boolean值
	 */
	public static boolean toBoolean(Object value) {

		if (null == value) {
			return false;
		} else if (Boolean.class.isInstance(value)) {
			Boolean b = (Boolean) value;
			return b.booleanValue();
		} else {
			String s = value.toString();
			return !"false".equals(s) && !"0".equals(s);
		}
	}

}

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

package com.github.ipaas.ifw.jdbc;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

/**
 * 数据库访问响应行
 * 
 * @author Chenql
 */
public interface DbAccessResponseRow {

	/**
	 * 获取结果行的列表的元数据(meta) <br>
	 * 备注:结果行索引(index)从1开始
	 * 
	 * @return -- 元数据(meta)map对象
	 */
	Map<Integer, String> getResultMeta();

	/**
	 * 根据列名获取对应的字符串表示的值
	 * 
	 * @param column
	 *            -- 列名
	 * @return -- 字符串的值
	 */
	String getString(String column);

	/**
	 * 根据列索引获取对应的字符串表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return -- 字符串的值
	 */
	String getString(int columnIndex);

	/**
	 * 根据列名的值
	 * 
	 * @param column
	 *            -- 列名
	 * @return -- Object的值
	 */
	Object getObject(String column);

	/**
	 * 根据列索引获取对应的Object表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return -- Object的值
	 */
	Object getObject(int columnIndex);

	/**
	 * 根据列名获取对应的整数表示的值
	 * 
	 * @param column
	 *            -- 列名
	 * @return -- 整数表示的值
	 */
	int getInt(String column);

	/**
	 * 根据列索引获取对应的整数表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return -- 整数表示的值
	 */
	int getInt(int columnIndex);

	/**
	 * 根据列索引获取对应的长整数表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return -- 长整数表示的值
	 */
	long getLong(int columnIndex);

	/**
	 * 根据列索引获取对应的长整数表示的值
	 * 
	 * @param column
	 *            -- 列名
	 * @return -- 长整数表示的值
	 */
	long getLong(String column);

	/**
	 * 根据列索引获取对应的浮点数表示的值
	 * 
	 * @param column
	 *            -- 列名
	 * @return -- 浮点数表示的值
	 */
	float getFloat(String column);

	/**
	 * 根据列索引获取对应的浮点数表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return -- 浮点数表示的值
	 */
	float getFloat(int columnIndex);

	/**
	 * 根据列索引获取对应的双精度浮点数表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return -- 双精度浮点数表示的值
	 */
	double getDouble(int columnIndex);

	/**
	 * 根据列索引获取对应的双精度浮点数表示的值
	 * 
	 * @param column
	 *            -- 列名
	 * @return -- 双精度浮点数表示的值
	 */
	double getDouble(String column);

	/**
	 * 根据列名获取对应的时间表示的值
	 * 
	 * @param column
	 *            -- 列名
	 * @param format
	 *            -- 时间格式字符串
	 * @return -- 时间表示的值
	 */
	Date getDate(String column, String format);

	/**
	 * 根据列索引获取对应的时间表示的值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @param format
	 *            -- 时间格式字符串
	 * @return -- 时间表示的值
	 */
	Date getDate(int columnIndex, String format);

	/**
	 * 根据列索引获取对应的BigDecimal类型的列值
	 * 
	 * @param columnIndex
	 *            -- 列索引, 从1开始
	 * @return --列值
	 */
	BigDecimal getBigDecimal(int columnIndex);

	/**
	 * 按字段名称获取BigDecimal类型的列值
	 * 
	 * @param column
	 *            列名
	 * @return --列值
	 */
	BigDecimal getBigDecimal(String column);
}

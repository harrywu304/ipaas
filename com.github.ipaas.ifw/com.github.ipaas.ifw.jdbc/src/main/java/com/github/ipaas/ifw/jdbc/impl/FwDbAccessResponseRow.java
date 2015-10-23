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

package com.github.ipaas.ifw.jdbc.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

import com.github.ipaas.ifw.jdbc.DbAccessResponseRow;
import com.github.ipaas.ifw.util.DateUtil;

/**
 * 数据库响应行
 * 
 * @author Chenql
 */
public final class FwDbAccessResponseRow implements DbAccessResponseRow {

	private Map<Integer, String> meta = null;

	private Map<String, Object> row = null;

	public FwDbAccessResponseRow(Map<String, Object> row, Map<Integer, String> meta) {
		this.row = row;
		this.meta = meta;
	}

	public Date getDate(String column, String format) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = row.get(column);
		Date d = null;
		if (null != target) {
			d = (Date.class.isInstance(target)) ? (Date) target : DateUtil.convertStrToDate(target.toString(), format);
		}
		return d;
	}

	public Date getDate(int columnIndex, String format) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getDate(meta.get(Integer.valueOf(columnIndex)), format);
	}

	public int getInt(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = row.get(column);
		Integer t = null;
		if (null != target) {
			t = (Integer.class.isInstance(target)) ? (Integer) target : Integer.valueOf(target.toString());
			return t.intValue();
		}
		return 0;
	}

	public int getInt(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getInt(meta.get(Integer.valueOf(columnIndex)));
	}

	public long getLong(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getLong(meta.get(Integer.valueOf(columnIndex)));
	}

	public long getLong(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = row.get(column);
		Long t = null;
		if (null != target) {
			t = Long.class.isInstance(target) ? (Long) target : Long.valueOf(target.toString());
			return t.longValue();
		}
		return 0L;
	}

	public Map<Integer, String> getResultMeta() {
		return meta;
	}

	public String getString(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = this.row.get(column);
		if (null != target) {
			return target.toString();
		}
		return null;
	}

	public String getString(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getString(meta.get(Integer.valueOf(columnIndex)));
	}

	public float getFloat(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = row.get(column);
		Float t = null;
		if (null != target) {
			t = (Float.class.isInstance(target)) ? (Float) target : Float.valueOf(target.toString());
			return t.floatValue();
		}
		return 0f;
	}

	public float getFloat(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getFloat(meta.get(Integer.valueOf(columnIndex)));
	}

	public double getDouble(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getDouble(meta.get(Integer.valueOf(columnIndex)));
	}

	public double getDouble(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = row.get(column);
		Double t = null;
		if (null != target) {
			t = Double.class.isInstance(target) ? (Double) target : Double.valueOf(target.toString());
			return t.doubleValue();
		}
		return 0d;
	}

	public BigDecimal getBigDecimal(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getBigDecimal(meta.get(Integer.valueOf(columnIndex)));
	}

	public BigDecimal getBigDecimal(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		Object target = row.get(column);
		BigDecimal t = null;
		if (null != target) {
			t = BigDecimal.class.isInstance(target) ? (BigDecimal) target : new BigDecimal(target.toString());
			return t;
		}
		return null;
	}

	@Override
	public Object getObject(String column) {
		column = column.toLowerCase();
		if (!row.containsKey(column)) {
			throw new IllegalStateException("没有找到数据列[" + column + "]");
		}
		return this.row.get(column);
	}

	@Override
	public Object getObject(int columnIndex) {
		if (!meta.containsKey(Integer.valueOf(columnIndex))) {
			throw new IllegalStateException("没有找到数据列[" + columnIndex + "]");
		}
		return getObject(meta.get(Integer.valueOf(columnIndex)));
	}
}

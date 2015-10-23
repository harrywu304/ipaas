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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.jdbc.BeanHandler;
import com.github.ipaas.ifw.jdbc.DbAccessResponse;
import com.github.ipaas.ifw.jdbc.DbAccessResponseRow;

/**
 * 数据库直接访问响应
 * 
 * @author Chenql
 */
public final class FwDbAccessResponse implements DbAccessResponse {

	private String reqId = "";

	private int rows = 0;

	private long elapsed = 0L;

	private long executed = 0L;

	private long context = 0L;

	private boolean success = false;

	private FwRuntimeException raex = null;

	private Map<Integer, String> resultMeta = null;

	private List<Map<String, Object>> resultData = null;

	private List<Object> generatedKeys = null;

	public String getReqId() {
		return reqId;
	}

	public int affectedRows() {
		return this.rows;
	}

	public long elapsedTime() {
		return this.elapsed;
	}

	public long executedTime() {
		return this.executed;
	}

	public FwRuntimeException getException() {
		return this.raex;
	}

	public long initializedContextTime() {
		return this.context;
	}

	public boolean isSuccess() {
		return this.success;
	}

	public Iterator<DbAccessResponseRow> iterator() {
		return new MapRowIterator<DbAccessResponseRow>();
	}

	public <K> Iterator<K> iterator(Class<K> clazz) {
		return new BeanRowIterator<K>(clazz);
	}

	public void setReqId(String reqId) {
		this.reqId = reqId;
	}

	public int getRows() {
		return rows;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public long getElapsed() {
		return elapsed;
	}

	public void setElapsed(long elapsed) {
		this.elapsed = elapsed;
	}

	public long getExecuted() {
		return executed;
	}

	public void setExecuted(long executed) {
		this.executed = executed;
	}

	public long getContext() {
		return context;
	}

	public void setContext(long context) {
		this.context = context;
	}

	public FwRuntimeException getRaex() {
		return raex;
	}

	public void setRaex(FwRuntimeException raex) {
		this.raex = raex;
	}

	public Map<Integer, String> getResultMeta() {
		return resultMeta;
	}

	public void setResultMeta(Map<Integer, String> resultMeta) {
		this.resultMeta = resultMeta;
	}

	public List<Map<String, Object>> getResultData() {
		return resultData;
	}

	public <K> List<K> getResultData(Class<K> clazz) {

		if (null == resultData) {
			return null;
		}
		try {
			List<K> rd = new ArrayList<K>(this.resultData.size());

			Iterator<DbAccessResponseRow> it = iterator();
			BeanHandler<K> beanHandler = new DefaultBeanHandler<K>(clazz);
			while (it.hasNext()) {
				DbAccessResponseRow row = it.next();
				K bean = beanHandler.handler(row);
				rd.add(bean);
			}

			return rd;
		} catch (Exception ex) {
			// FwErrorTrackService.getInstance().trackAndThrowRuntime("fw06027",
			// ex);
			throw new FwRuntimeException("将数据库记录转化成java bean异常", ex);
		}
		// return null;
	}

	/**
	 * 自定义Handler将结果集Mapping到java bean
	 * 
	 * @param <K>
	 *            Bean的类型
	 * @param beanHandler
	 * @return
	 */
	public <K> List<K> getResultData(BeanHandler<K> beanHandler) {
		if (null == resultData) {
			return null;
		}
		try {
			List<K> rd = new ArrayList<K>(resultData.size());
			Iterator<DbAccessResponseRow> it = iterator();
			while (it.hasNext()) {
				DbAccessResponseRow row = it.next();
				K bean = beanHandler.handler(row);
				rd.add(bean);
			}
			return rd;
		} catch (Exception ex) {
			// FwErrorTrackService.getInstance().trackAndThrowRuntime("fw06027",
			// ex);
			throw new FwRuntimeException("将数据库记录转化成java bean异常", ex);
		}
		// return null;
	}

	public void setResultData(List<Map<String, Object>> resultData) {
		this.resultData = resultData;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public void setGeneratedKeys(List<Object> gk) {
		this.generatedKeys = gk;
	}

	public List<Object> getGeneratedKeys() {
		return generatedKeys;
	}

	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder(256);
		sb.append("\nFwDbAccessResponse{\n");
		sb.append(" reqId:").append(reqId).append(",\n");
		sb.append(" elapsed:").append(elapsed).append(",");
		sb.append(" context:").append(context).append(",");
		sb.append(" executed:").append(executed).append(",\n");
		sb.append(" success:").append(success).append(",");
		sb.append(" rows:").append(rows).append(",\n");
		sb.append(" resultMeta:").append(resultMeta).append(",\n");
		sb.append(" resultData:").append(resultData).append(",\n");
		sb.append(" generatedKeys:").append(generatedKeys).append(",\n");
		sb.append(" exception:").append(raex);
		sb.append("}");
		return sb.toString();
	}

	/**
	 * 数据行迭代器
	 * 
	 * @author zhandl (yuyoo zhandl@hainan.net)
	 * @teme 2010-5-17 下午06:52:46
	 * @param <DbAccessResponseRow>
	 */
	@SuppressWarnings("hiding")
	private class MapRowIterator<DbAccessResponseRow> implements Iterator<DbAccessResponseRow> {

		private int currentIndex = 0;

		public boolean hasNext() {
			return ((null != resultData) && (currentIndex < resultData.size()));
		}

		@SuppressWarnings("unchecked")
		public DbAccessResponseRow next() {
			DbAccessResponseRow nextRow = (DbAccessResponseRow) new FwDbAccessResponseRow(resultData.get(currentIndex),
					resultMeta);
			currentIndex++;
			return nextRow;
		}

		public void remove() {
			throw new UnsupportedOperationException("FwDbAccessResponse$MapRowIterator不支持移除操作");
		}
	}

	private class BeanRowIterator<K> implements Iterator<K> {

		private int currentIndex = 0;

		private Class<K> clazz = null;

		private BeanHandler<K> beanHandler = null;

		BeanRowIterator(Class<K> c) {
			this.clazz = c;
			this.beanHandler = new DefaultBeanHandler<K>(clazz);
		}

		public boolean hasNext() {
			return ((null != resultData) && (currentIndex < resultData.size()));
		}

		public K next() {

			try {
				DbAccessResponseRow nextRow = (DbAccessResponseRow) new FwDbAccessResponseRow(
						resultData.get(currentIndex), resultMeta);
				K bean = beanHandler.handler(nextRow);
				currentIndex++;
				return bean;
			} catch (Exception ex) {
				// FwErrorTrackService.getInstance().trackAndThrowRuntime("fw06027",
				// ex);
				throw new FwRuntimeException("将数据库记录转化成java bean异常", ex);
			}
			// return null;
		}

		public void remove() {
			throw new UnsupportedOperationException("FwDbAccessResponse$BeanRowIterator不支持移除操作");
		}
	}

	@Override
	public Object getSingleValue() {
		if (null == resultData || resultData.size() < 1) {
			return null;
		}
		return new FwDbAccessResponseRow(resultData.get(0), resultMeta);
	}
}

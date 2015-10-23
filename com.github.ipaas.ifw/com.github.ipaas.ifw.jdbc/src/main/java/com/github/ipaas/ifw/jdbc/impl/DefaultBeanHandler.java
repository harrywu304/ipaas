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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.github.ipaas.ifw.jdbc.BeanHandler;
import com.github.ipaas.ifw.jdbc.DbAccessResponseRow;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * 默认的BeanHandler，用于动态的进行DbAccessResponseRow到Bean的Mapping <br/>
 * <br/>
 * 使用动态Mapping需要满足以下条件： <br/>
 * 1)进行mapping的每一个field都需要在bean中存在相应的set方法； <br/>
 * 2)field名和set方法名，需要基本遵循Camel风格，即last_action_time字段对应的就是setLastActionTime； <br/>
 * 3)为了增加mapping的兼容度，field名和set名不严格按照Camel风格匹配，采用了大小写不敏感的做法，
 * 即idWriter字段可对应setIdwriter/setidwriter方法; <br/>
 * 对于找不到对应set方法的field会直接被忽略，不会抛出异常或记录错误日志； * @author whx
 *
 * @author Chenql
 */
public class DefaultBeanHandler<T> implements BeanHandler<T> {
	/**
	 * db field name to bean set method name mapping
	 */
	private Map<String, String> fieldNameMap;

	/**
	 * method name to method mapping
	 */
	private Map<String, Method> methodMap;

	/**
	 * bean类型
	 */
	private Class<T> beanType;

	public DefaultBeanHandler(Class<T> beanType) {
		this.beanType = beanType;
		this.methodMap = new HashMap<String, Method>(10);
	}

	public T handler(DbAccessResponseRow row) throws Exception {
		// 获取并缓存，数据库fieldName和bean fieldName的映射
		if (fieldNameMap == null) {
			Collection<String> values = row.getResultMeta().values();
			fieldNameMap = new HashMap<String, String>(values.size());
			Iterator<String> it = values.iterator();
			while (it.hasNext()) {
				String fieldName = it.next();
				fieldNameMap.put(fieldName, StringUtil.getCamelCaseString("set_" + fieldName));
			}
		}

		// 创建bean对象
		T bean = beanType.newInstance();
		// 获取bean的所有方法
		Method[] methods = beanType.getMethods();
		// 遍历DbAccessResponseRow
		Set<String> fieldNames = fieldNameMap.keySet();
		for (String fieldName : fieldNames) {
			// 根据方法名获取set方法
			Method method = getMethod(methods, fieldNameMap.get(fieldName));
			if (method != null) {
				// 赋值bean对象
				setValue(bean, fieldName, method, row);
			}
		}
		return bean;
	}

	/**
	 * 根据fieldName给bean赋值
	 * 
	 * @param bean
	 * @param fieldName
	 * @param method
	 * @param row
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private void setValue(T bean, String fieldName, Method method, DbAccessResponseRow row)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		// 获取方法的参数类型
		Class[] paramTypes = method.getParameterTypes();
		if (paramTypes != null && paramTypes.length == 1) {
			// 根据参数类型获取参数值
			Object param = null;
			Class paramType = paramTypes[0];
			if (String.class.equals(paramType)) {
				param = row.getString(fieldName);
			} else if (int.class.equals(paramType) || Integer.class.equals(paramType)) {
				param = row.getInt(fieldName);
			} else if (float.class.equals(paramType) || Float.class.equals(paramType)) {
				param = row.getFloat(fieldName);
			} else if (long.class.equals(paramType) || Long.class.equals(paramType)) {
				param = row.getLong(fieldName);
			} else if (short.class.equals(paramType) || Short.class.equals(paramType)) {
				param = Short.parseShort(row.getString(fieldName));
			} else if (double.class.equals(paramType) || Double.class.equals(paramType)) {
				param = row.getDouble(fieldName);
			} else if (boolean.class.equals(paramType)) {
				param = Boolean.parseBoolean(row.getString(fieldName));
			} else if (Date.class.equals(paramType)) {
				param = row.getDate(fieldName, "yyyy-MM-dd HH:mm:ss");
			} else if (java.math.BigDecimal.class.equals(paramType)) {
				param = row.getBigDecimal(fieldName);
			}
			// 调用bean的set方法赋值
			method.invoke(bean, param);
		}
	}

	/**
	 * 根据方法名获取对应的方法，忽略大小写
	 * 
	 * @param methods
	 *            方法列表
	 * @param name
	 *            方法名
	 * @return
	 */
	private Method getMethod(Method[] methods, String name) {
		if (methodMap.containsKey(name)) {
			return methodMap.get(name);
		} else {
			for (Method method : methods) {
				if (method.getName().equalsIgnoreCase(name)) {
					methodMap.put(name, method);
					return method;
				}
			}
		}
		return null;
	}

}

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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;

/**
 * 数据库访问响应类
 * 
 * @author Chenql
 */
public interface DbAccessResponse {

	/**
	 * 获取数据库访问请求ID
	 * 
	 * @return -- 数据库访问请求ID
	 */
	String getReqId();

	/**
	 * 总的花费时间,单位:毫秒
	 * 
	 * @return -- 花费时间
	 */
	long elapsedTime();

	/**
	 * 初始化执行上下文时间, 单位:毫秒 主要包括定位数据库,和获取数据库连接的时间
	 * 
	 * @return -- 花费时间
	 */
	long initializedContextTime();

	/**
	 * SQL执行时间
	 * 
	 * @return -- 花费时间
	 */
	long executedTime();

	/**
	 * 数据访问执行影响的行数
	 * 
	 * @return -- 行数
	 */
	int affectedRows();

	/**
	 * SQL执行是否成功
	 * 
	 * @return -- true, 成功执行了SQL
	 */
	boolean isSuccess();

	/**
	 * 获取执行发生的异常, 如果没有异常就返回null;
	 * 
	 * @return -- 运行时应用异常
	 */
	FwRuntimeException getException();

	/**
	 * 获取数据行的迭代器
	 * 
	 * @return -- 数据行的迭代器
	 */
	Iterator<DbAccessResponseRow> iterator();

	/**
	 * 获取数据行的迭代器,指定迭代器中的对象的类型
	 * 
	 * @param <K>
	 *            -- 迭代器中的对象的类型
	 * @param clazz
	 *            -- 迭代器中的对象的类型的Class
	 * @return -- 数据行的迭代器
	 */
	<K> Iterator<K> iterator(Class<K> clazz);

	/**
	 * 获取结果行的列表的元数据(meta) <br>
	 * 备注:结果行索引(index)从1开始
	 * 
	 * @return -- 元数据(meta)map对象
	 */
	Map<Integer, String> getResultMeta();

	/**
	 * 获取结果行的列表, 一个map表示一行
	 * 
	 * @return -- 结果行的列表
	 */
	List<Map<String, Object>> getResultData();

	/**
	 * 根据clazz类型返回结果行的列表 <br/>
	 * 使用动态Mapping需要满足以下条件： <br/>
	 * 1)进行mapping的每一个field都需要在bean中存在相应的set方法； <br/>
	 * 2)field名和set方法名，需要基本遵循Camel风格，即last_action_time字段对应的就是setLastActionTime； <br/>
	 * 3)为了增加mapping的兼容度，field名和set名不严格按照Camel风格匹配，采用了大小写不敏感的做法，
	 * 即idWriter字段可对应setIdwriter/setidwriter方法; <br/>
	 * 对于找不到对应set方法的field会直接被忽略，不会抛出异常或记录错误日志；
	 * 
	 * @param <K>
	 *            -- 多态类型
	 * @param clazz
	 *            -- 表示一行的java bean的类型(Class)
	 * @return -- Bean对象列表
	 */
	<K> List<K> getResultData(Class<K> clazz);

	/**
	 * 自定义Handler将结果集Mapping到java bean
	 * 
	 * @param <K>
	 *            Bean类型
	 * @param beanHandler
	 *            自定义的BeanHandler
	 * @return Bean对象列表
	 */
	public <K> List<K> getResultData(BeanHandler<K> beanHandler);

	/**
	 * 获取数据库生成的keys
	 * 
	 * @return -- 数据库生成的keys列表
	 */
	List<Object> getGeneratedKeys();

	public Object getSingleValue();
}

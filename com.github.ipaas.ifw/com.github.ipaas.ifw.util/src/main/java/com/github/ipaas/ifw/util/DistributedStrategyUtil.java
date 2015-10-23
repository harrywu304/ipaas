package com.github.ipaas.ifw.util;

import java.util.List;
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
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * 分布策略类
 *
 * @author Chenql
 */
public class DistributedStrategyUtil {

	private static final Logger logger = Logger.getLogger(DistributedStrategyUtil.class);

	/**
	 * 根据key的hash值返回List中的一个节点
	 * 
	 * @param <T>
	 *            节点类型
	 * @param items
	 *            节点集合
	 * @param key
	 *            生成hash的字符串
	 * @return 节点对象
	 */
	public static <T> T hashGetItem(List<T> items, String key) {

		if (null == items || items.size() == 0) {
			throw new IllegalArgumentException("节点集合不能为空或者空集合");
		}

		long hash = Math.abs(StringUtil.getDJBHash(key));
		int index = Integer.parseInt(String.valueOf(((hash % items.size()))));
		if (logger.isDebugEnabled()) {
			logger.debug("hashGetItem>items:" + items);
			logger.debug("hashGetItem>key:" + key);
			logger.debug("hashGetItem>hash:" + hash);
			logger.debug("hashGetItem>index:" + index);
		}
		return items.get(index);
	}

	/**
	 * 根据key的hash值返回List中的一个节点
	 * 
	 * @param <T>
	 *            节点类型
	 * @param items
	 *            节点集合
	 * @param key
	 *            生成hash的字符串
	 * @return 节点对象
	 */
	public static <T> T hashGetItem(T[] items, String key) {

		if (null == items || items.length == 0) {
			throw new IllegalArgumentException("节点集合不能为空或者空集合");
		}

		long hash = Math.abs(StringUtil.getDJBHash(key));
		int index = Integer.parseInt(String.valueOf(((hash % items.length))));
		if (logger.isDebugEnabled()) {
			logger.debug("hashGetItem>items:" + items);
			logger.debug("hashGetItem>key:" + key);
			logger.debug("hashGetItem>hash:" + hash);
			logger.debug("hashGetItem>index:" + index);
		}
		return items[index];
	}

	/**
	 * 根据key和节点总数获取节点
	 * 
	 * @param itemsSize
	 *            节点总数
	 * @param key
	 *            生成hash的字符串
	 * @return 节点索引
	 */
	public static int hashGetItem(int itemsSize, String key) {
		if (itemsSize < 1) {
			throw new IllegalArgumentException("节点集合不能为空");
		}
		long hash = Math.abs(StringUtil.getDJBHash(key));
		int index = Integer.parseInt(String.valueOf(((hash % itemsSize))));

		if (logger.isDebugEnabled()) {
			logger.debug("randomGetItem>itemsSize:" + itemsSize);
			logger.debug("randomGetItem>index:" + index);
		}
		return index;
	}

	/**
	 * 随机返回节点集合中的一个节点
	 * 
	 * @param <T>
	 *            节点类型
	 * @param items
	 *            节点集合
	 * @return 节点对象
	 */
	public static <T> T randomGetItem(List<T> items) {

		if (null == items || items.size() == 0) {
			throw new IllegalArgumentException("节点集合不能为空或者空集合");
		}

		Random random = new Random();
		int index = random.nextInt(items.size());

		if (logger.isDebugEnabled()) {
			logger.debug("randomGetItem>items:" + items);
			logger.debug("randomGetItem>index:" + index);
		}
		return items.get(index);
	}

	/**
	 * 随机返回节点集合中的一个节点
	 * 
	 * @param <T>
	 *            节点类型
	 * @param items
	 *            节点集合
	 * @return 节点对象
	 */
	public static <T> T randomGetItem(T[] items) {

		if (null == items || items.length == 0) {
			throw new IllegalArgumentException("节点集合不能为空或者空集合");
		}

		Random random = new Random();
		int index = random.nextInt(items.length);
		return items[index];
	}

	/**
	 * 私有构造方法,防止创建实例
	 */
	private DistributedStrategyUtil() {
	}
}
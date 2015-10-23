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

package com.github.ipaas.ifw.server.ice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;

 
  /**
   * @author Chenql
   */
@SuppressWarnings({"rawtypes"})
public class ComponentServantProvider {

	private static Logger logger = LoggerFactory.getLogger(ComponentServantProvider.class);
	
	/**
	 * 组件服务缓存
	 */
	private static Map<String, Ice.Object> servantCache = new ConcurrentHashMap<String, Ice.Object>(200, 0.75f, 256);
	
	/**
	 * 根据代理字符串(strToProxy)创建ice服务(servant对象) 
	 * @param strToProxy 代理字符串(strToProxy)
	 * @return servant对象
	 */
	public static Ice.Object getServantBean(String strToProxy) {
 
		Ice.Object servant = servantCache.get(strToProxy); 	
		if (null == servant) {
			String itfc = buildInterfaceStr(strToProxy);
			String impl = buildImplementStr(strToProxy);
			if (logger.isDebugEnabled()) {
				logger.debug("查找ice服务(servant对象), 使用标识(strToProxy):" + strToProxy);
				logger.debug("使用ice服务接口类[" + itfc + "]创建服务.");
				logger.debug("使用ice服务实现类[" + impl + "]提供服务.");
			}
			servant = loadServantInstance(itfc, impl);
			servantCache.put(strToProxy, servant);
		} 
		return servant; 
	}
	
	/**
	 * 通过接口的名称字符串构造一个对应实现的类名称字符串:
	 * @param strToProxy 代理字符串(strToProxy)
	 * @return 组件服务实现类全名称
	 */
	private static String buildImplementStr(String strToProxy) {
		 
		int prxIndex = strToProxy.lastIndexOf("Prx");
		if (prxIndex <= 0) { 
			throw new FwRuntimeException("检查出无效代理字符串(strToProxy):"+strToProxy);
		} 
		
		// 通过接口的名称字符串构造一个对应实现的类名称字符串:
		int lastPointIndex = strToProxy.lastIndexOf(".");
		String lastSub = strToProxy.substring(lastPointIndex + 1);
		if (!lastSub.endsWith("Prx")) {
			throw new FwRuntimeException("检查出无效代理字符串(strToProxy):"+strToProxy);
		} 
		
		String shortClazzName = lastSub.substring(0, lastSub.length() - 3);
		
		StringBuilder sb = new StringBuilder(strToProxy.length() + 10);
		sb.append(strToProxy.substring(0, lastPointIndex));
		sb.append(".impl.").append(shortClazzName).append("Impl");
		String impl = sb.toString();
		return impl;
	}
	
	/**
	 * 通过接口的名称字符串构造一个对应组件服务接口类名称字符串
	 * @param strToProxy 代理字符串(strToProxy)
	 * @return 组件服务接口类全名称
	 */
	private static String buildInterfaceStr(final String strToProxy) { 
		int prxIndex = strToProxy.lastIndexOf("Prx");
		String itfc = strToProxy.substring(0, prxIndex); 
		return itfc;
	} 
	
	/**
	 * 加载组件服务实例失败
	 * @param itfc -- 接口类型
	 * @param impl -- 实现类型
	 * @return -- 实现实例
	 */
	private static Ice.Object loadServantInstance(final String itfc, final String impl) { 
		try {
			Class clazz = Class.forName(impl);
			Object target = clazz.newInstance();
			return (Ice.Object) target;
		} catch (Throwable thr) { 
			throw new FwRuntimeException("加载组件服务实例失败. itfc:"+itfc+", impl:"+impl); 
		}
	} 
}

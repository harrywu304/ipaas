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
package com.github.ipaas.ifw.server.mq.consumer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.core.exception.FwRuntimeException;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.mq.MessageHandler;
import com.github.ipaas.ifw.mq.MqListenService;
import com.github.ipaas.ifw.util.CloseUtil;
import com.github.ipaas.ifw.util.XmlUtil;


/**
 *  Consumer Server
 *
 * @author Chenql
 */
public class ConsumerServer {

	/**
	 * logger
	 */
	private static Logger logger = LoggerFactory
			.getLogger(ConsumerServer.class);

	
	/**
	 * 从配置文件中获取consummer配置
	 * @return
	 */
	private  Map<String, Map> loadConsumerConfig(){
		InputStream ins=null;
		 try{
			 URL resource=ConsumerServer.class.getResource("/fw_server.xml"); 
			 ins=new FileInputStream(new File(resource.getPath()));
			 Map<String,Object> fwServerConfig=XmlUtil.toMap(ins); 
			 Map<String,Map> cServerConfig=(Map<String,Map>)fwServerConfig.get("consumer_server");
			 return cServerConfig;
		 }catch(Exception e){
			 throw new FwRuntimeException("获取ICE服务器配置时出现异常",e);
		 }finally{
			 CloseUtil.closeSilently(ins);
		 }
	}
	
	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) {
		ConsumerServer inst = new ConsumerServer();
		logger.info("开始启动Consumer服务器......");
		inst.startup();
		logger.info("成功启动Consumer服务器......");
		
		//保持主线程不退出，这样在broker断开的情况下consumer就不会退出
        Object lock = new Object();
        synchronized (lock) {
            try {
				lock.wait();
			} catch (InterruptedException e) {
				logger.info("ConsumerServer关闭中...");
			}
        }
	}

	/**
	 * 启动consumers
	 */
	public void startup() {
		//获取应用ID和consumer配置映射表
		Map<String, Map> appConsumerMapping = loadConsumerConfig();
		if (appConsumerMapping != null && appConsumerMapping.size() > 0) {
			//遍历App配置
			Set<String> apps = (Set<String>) appConsumerMapping.keySet();
			for (String appid : apps) {
				//根据appId启动关联的consumer
				Map<String, Map> appConfig = appConsumerMapping.get(appid);
				startConsumerForApp(appid,appConfig);
			}
		}
	}
	
	/**
	 * 为指定应用启动关联的consumer
	 * @param appId
	 * @param appConfig
	 */
	private void startConsumerForApp(String appId, Map<String, Map> appConfig){
		//根据appId创建ServiceFactory
		MqListenService mqsc = ServiceFactory.getService(appId);
		
		//启动queue的consumer
		Map<String, String> queues = appConfig.get("queue");
		startConsumerByTransferModel(mqsc,"queue",queues);
		
		//启动topic的consumer
		Map<String, String> topics = appConfig.get("topic");
		startConsumerByTransferModel(mqsc,"topoic",topics);
		
		//启动topic durable的consumer
		Map<String, String> topicDurables = appConfig.get("topic_durable");
		startConsumerByTransferModel(mqsc,"topic_durable",topicDurables);
	}
	
	/**
	 * 根据消息传输模式启动Consumer
	 * @param mqsc
	 * @param modelType 消息传输模式，queue|topic|topic_durable
	 * @param dests
	 */
	private void startConsumerByTransferModel(MqListenService mqsc, String modelType, Map<String,String> dests){
		if (dests != null && dests.size() > 0) {
			Set<String> names = dests.keySet();
			for(String name:names){
				String handlerKlassName = dests.get(name);
				MessageHandler handler = getHandlerInstance(handlerKlassName);
				if("queue".equals(modelType)){
					mqsc.listenQueue(name, handler);
				} else if("topic".equals(modelType)){
					mqsc.listenTopic(name, handler);
				} else if("topic_durable".equals(modelType)){
					mqsc.listenTopicDurable(name, handler);
				}
				logger.info("{}[{}]的消费者侦听成功启动...", modelType, name);
			}
		}
	}

	/**
	 * 获取消息处理器器实例对象
	 * 
	 * @param pluginId
	 *            -- 插件ID
	 * @return -- 插件ID对应的监听器实例对象
	 */
	private MessageHandler getHandlerInstance(String handlerKlass) {
		try {
			Class klass = Class.forName(handlerKlass);
			return (MessageHandler) klass.newInstance();
		} catch (Exception e) {
			logger.error("获取消息处理器器实例对象失败["+handlerKlass+"]", e);
		}
		return null;
	}

}

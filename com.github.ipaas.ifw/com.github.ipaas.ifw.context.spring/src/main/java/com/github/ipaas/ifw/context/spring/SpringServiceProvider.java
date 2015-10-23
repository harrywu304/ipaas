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
package com.github.ipaas.ifw.context.spring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.github.ipaas.ifw.core.config.Config;
import com.github.ipaas.ifw.core.config.FwConfigService;
import com.github.ipaas.ifw.core.extension.Extensions;
import com.github.ipaas.ifw.core.service.ServiceProvider;

/**
 * @author wudie ServiceProvider spring实现
 */
public class SpringServiceProvider implements ServiceProvider {

	private static Logger logger = LoggerFactory.getLogger(SpringServiceProvider.class);

	private ApplicationContext appContext;

	public SpringServiceProvider() {
	}

	public SpringServiceProvider(ApplicationContext appContext) {
		this.appContext = appContext;
	}

	@Override
	public void init() {
		if (appContext == null) {
			logger.info("开始构建spring上下文");
			Config sysConfig = FwConfigService.getSystemConfig();
			String serviceConfig = sysConfig.getItem("service.config", "fw_context.xml");
			String[] configs = serviceConfig.split(",");
			appContext = new ClassPathXmlApplicationContext(configs);
			logger.info("成功构建spring上下文,配置文件->" + serviceConfig);
		}
	}

	@Override
	public <T> T getService(String serviceID) {
		return (T) appContext.getBean(serviceID);
	}

	public static void main(String[] args) {
		Extensions.loadExtension(ServiceProvider.class);
	}

}

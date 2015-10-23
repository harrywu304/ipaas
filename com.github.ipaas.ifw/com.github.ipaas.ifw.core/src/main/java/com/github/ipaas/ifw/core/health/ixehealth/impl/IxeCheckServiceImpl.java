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

package com.github.ipaas.ifw.core.health.ixehealth.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Ice.Current;

import com.github.ipaas.ifw.core.config.Config;
import com.github.ipaas.ifw.core.config.FwConfigService;
import com.github.ipaas.ifw.core.health.BizCheck;
import com.github.ipaas.ifw.core.health.ixehealth._IxeCheckServiceDisp;
import com.github.ipaas.ifw.util.BeanUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * 用于检查的Servant.
 * 
 * @author Chenql
 */
public class IxeCheckServiceImpl extends _IxeCheckServiceDisp {
	/**
	 * 版本字段.
	 */
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(IxeCheckServiceImpl.class);

	@Override
	public String echo(String echoString, Current __current) {
		return echoString;
	}

	@Override
	public String checkBiz(Current __current) {
		BizCheck bizCheck = null;
		String checkState = null;
		String className = null;
		try {
			Config config = FwConfigService.getAppConfig();
			Map<String, Object> sp = BeanUtil.wrapToMap(config.getItem("app_properties"));
			logger.debug("sp = " + sp.toString());
			if (null == sp || sp.size() == 0) {
				className = "com.github.ipaas.ifw.core.health.GenericBizCheckImpl";
			} else {
				className = (String) sp.get("health_check");
				if (StringUtil.isNullOrBlank(className)) {
					className = "com.github.ipaas.ifw.core.health.GenericBizCheckImpl";
				}
			}
			logger.debug("className = " + className);
			bizCheck = (BizCheck) BeanUtil.createBean(className);
			checkState = bizCheck.checkBiz();
			bizCheck = null;
			sp = null;
			config = null;
		} catch (Throwable e) {
			checkState = StringUtil.getExceptionAsStr(e);
			logger.error("ICE check biz servant error", e);
			return checkState;
		}
		return checkState;
	}

}

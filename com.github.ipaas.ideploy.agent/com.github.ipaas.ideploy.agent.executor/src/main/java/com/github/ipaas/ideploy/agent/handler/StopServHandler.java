/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */

package com.github.ipaas.ideploy.agent.handler;

import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.CrsCmdHandlerWithFeed;
import com.github.ipaas.ideploy.agent.util.ServiceCheckUtil;
import com.github.ipaas.ideploy.agent.util.ShellUtil;
import com.github.ipaas.ifw.util.IPUtil;

/**
 * 停止服务器
 * 
 * @author wudg
 */

public class StopServHandler extends CrsCmdHandlerWithFeed {

	private static Logger logger = LoggerFactory.getLogger(StopServHandler.class);

	@Override
	public void execute(String flowId, String cmd, Map<String, Object> params, BundleContext bundleContext)
			throws Exception {
		logger.debug("停止服务器: flowId:" + flowId + "  cmd:" + cmd + "  params:" + params);
		String shell = String.valueOf(params.get("shell"));
		String ip = IPUtil.getLocalIP(true);
		String jmxPort = String.valueOf(params.get("jmxPort"));

		boolean servRunning = ServiceCheckUtil.isRunning(ip, jmxPort);
		boolean isShutdown = false;
		for (int i = 1; i < 16 && servRunning && !isShutdown; i++) {
			logger.info("第" + i + "次执行脚本,shell{" + shell + "}");
			ShellUtil.execQuietly(shell);
			isShutdown = ServiceCheckUtil.waitForShutdown(ip, jmxPort, 12 * ServiceCheckUtil.CHECK_INTERVAL);
			// 5分钟内未停止
			if (isShutdown) {
				break;
			}
		}
		// 15分钟内未停止
		if (!isShutdown) {
			logger.error("服务未能成功停止,shell{" + shell + "}");
			throw new Exception("服务未能成功停止,shell{" + shell + "}");
		}
		logger.info("停止服务器成功...");

	}

}
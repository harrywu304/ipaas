/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */

package com.github.ipaas.ideploy.agent.handler;

import java.io.File;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.agent.CrsCmdHandlerWithFeed;
import com.github.ipaas.ideploy.agent.util.ShellUtil;
import com.github.ipaas.ideploy.agent.util.UpdateCodeUtil;

/**
 * 更新代码
 * 
 * @author wudg
 */

public class UpdateCodeHandler extends CrsCmdHandlerWithFeed {

	private static String CHOWN_COMMAND = "chown -R  nobody  ";
	private static Logger logger = LoggerFactory.getLogger(UpdateCodeHandler.class);

	@Override
	public void execute(String flowId, String cmd, Map<String, Object> params, BundleContext bundleContext)
			throws Exception {

		logger.debug("更新代码: flowId:" + flowId + "  cmd:" + cmd + "  params:" + params);

		String srcPath = String.valueOf(params.get("srcPath"));
		String targetPath = String.valueOf(params.get("targetPath"));

		UpdateCodeUtil.updateCode(srcPath, targetPath);

		// 删除缓存文件
		FileUtils.deleteQuietly(new File(srcPath));

		try {
			// 更新代码目录 owner
			logger.info("更新代码目录 owner 为nobody");
			ShellUtil.execQuietly(CHOWN_COMMAND + targetPath);
		} catch (Exception e) {
			logger.error("更新代码目录 owner 失败");
		}                    

		logger.debug("更新代码成功...");
	}
}

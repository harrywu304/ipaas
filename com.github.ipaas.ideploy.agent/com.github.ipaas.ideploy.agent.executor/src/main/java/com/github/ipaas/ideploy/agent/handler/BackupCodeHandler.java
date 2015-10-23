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

/**
 * 代码备份
 * 
 * @author wudg
 */

public class BackupCodeHandler extends CrsCmdHandlerWithFeed {

	private static Logger logger = LoggerFactory.getLogger(BackupCodeHandler.class);

	@Override
	public void execute(String flowId, String cmd, Map<String, Object> params, BundleContext bundleContext)
			throws Exception {
		logger.debug("代码备份  flowId:" + flowId + "  cmd:" + cmd + "  params:" + params);

		String srcPath = String.valueOf(params.get("srcPath"));
		String targetPath = String.valueOf(params.get("targetPath"));

		File srcDir = new File(srcPath);

		// 被备份的目录不存在
		if (!srcDir.exists()) {
			return;
		}

		File destDir = new File(targetPath);

		// 创建备份目录
		if (destDir.exists() || destDir.getParentFile().exists()) {
			FileUtils.cleanDirectory(destDir.getParentFile());
		} else {
			FileUtils.forceMkdir(destDir);
		}

		// 备份文件
		FileUtils.copyDirectory(srcDir, destDir);

		logger.debug("代码备份成功...");
	}

}

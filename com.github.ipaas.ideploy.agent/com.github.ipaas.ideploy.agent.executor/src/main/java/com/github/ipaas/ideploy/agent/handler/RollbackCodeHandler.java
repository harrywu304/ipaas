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
import org.tmatesoft.svn.core.wc.SVNRevision;

import com.github.ipaas.ideploy.agent.CrsCmdHandlerWithFeed;
import com.github.ipaas.ideploy.agent.util.SVNUtil;
import com.github.ipaas.ideploy.agent.util.UpdateCodeUtil;
import com.github.ipaas.ifw.util.JsonUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * 回滚至旧代码
 * 
 * @author wudg
 */

public class RollbackCodeHandler extends CrsCmdHandlerWithFeed {

	private static Logger logger = LoggerFactory.getLogger(RollbackCodeHandler.class);

	@Override
	public void execute(String flowId, String cmd, Map<String, Object> params, BundleContext bundleContext)
			throws Exception {

		String doingCodeVerPath = (String) params.get("doingCodeVerPath");
		String usingCodeVerPath = (String) params.get("usingCodeVerPath");

		String deployPath = (String) params.get("deployPath");
		logger.debug(" params:" + JsonUtil.toJson(params));
		if (StringUtil.isNullOrBlank(usingCodeVerPath)) {// 机器是首次安装,代码服务器上没有备份代码,回退本地备份的代码
			File localBkFile = new File(String.valueOf(params.get("localBkPath")));
			File appRootFile = new File(deployPath);
			if (appRootFile.exists()) {
				FileUtils.cleanDirectory(appRootFile);// 清理部署目录
			}
			if (!localBkFile.exists()) {
				logger.error("没有找到本地备份代码:" + localBkFile.getAbsolutePath());
				throw new Exception("没有本地备份代码,不能回退!");
			}
			FileUtils.copyDirectory(localBkFile, appRootFile);
			logger.debug("回退本地代码完成");
			return;
		}

		String savePath = String.valueOf(params.get("savePath"));
		long headRev = SVNRevision.HEAD.getNumber();

		Integer hostStatus = Integer.valueOf("" + params.get("hostStatus"));

		if (hostStatus == 1) {// 新加入的机器,全量复制SVN上的代码
			SVNUtil.getDeta4UpdateAll(usingCodeVerPath, headRev, savePath);
		} else {
			// 获取代码 获取正在更新版本回退到正在使用版本的差异
			SVNUtil.getDelta(doingCodeVerPath, headRev, usingCodeVerPath, headRev, savePath);
		}

		// 更新代码
		UpdateCodeUtil.updateCode(savePath, deployPath);
	}
}

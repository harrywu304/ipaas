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
 * 启动服务器
 * @author  wudg
 */

public class StartServHandler  extends CrsCmdHandlerWithFeed{
	
	private static Logger logger = LoggerFactory.getLogger(StartServHandler.class);
	 
	 
	@Override
	public void execute(String flowId, String cmd,Map<String, Object> params,BundleContext  bundleContext) throws Exception {
		logger.debug("启动服务器: flowId:"+flowId+"  cmd:"+cmd+"  params:"+params);
		String shell=String.valueOf(params.get("shell")); 
		String ip=IPUtil.getLocalIP(true);
		String jmxPort=String.valueOf(params.get("jmxPort")); 
		
		boolean servRunning=ServiceCheckUtil.isRunning(ip, jmxPort);
		//服务已停止
		if(!servRunning){
			//停止服务
			ShellUtil.execQuietly(shell);
			
			
			servRunning=ServiceCheckUtil.waitForStart(ip, jmxPort, 60*ServiceCheckUtil.CHECK_INTERVAL);
			// 5分钟内未启动
			if(!servRunning){
				throw new Exception("服务未能成功启动,shell{"+shell+"}");
			}
		}
		
		logger.debug("启动服务器成功...");
		
	}
}

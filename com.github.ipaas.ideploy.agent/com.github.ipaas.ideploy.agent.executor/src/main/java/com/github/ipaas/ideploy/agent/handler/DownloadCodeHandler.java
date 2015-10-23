/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent.handler;
 
 
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 
import org.tmatesoft.svn.core.wc.SVNRevision;

 

import com.github.ipaas.ideploy.agent.CrsCmdHandlerWithFeed;
import com.github.ipaas.ideploy.agent.util.SVNUtil;
 

/** 
 * 下载代码
 * @author  wudg
 */

public class DownloadCodeHandler extends CrsCmdHandlerWithFeed{
	
	private static Logger logger = LoggerFactory.getLogger(DownloadCodeHandler.class);
 
	@Override
	public void execute(String flowId, String cmd,Map<String, Object> params,BundleContext  bundleContext) throws Exception {
		
		logger.debug("代码下载: flowId:"+flowId+"  cmd:"+cmd+"  params:"+params);
		
		long headRev=SVNRevision.HEAD.getNumber();
		
		//正在安装版本
		String doingCodeVerPath=String.valueOf(params.get("doingCodeVerPath"));
		
		//正在使用版本
		String usingCodeVerPath=null;
		if(params.containsKey("usingCodeVerPath")&&params.get("usingCodeVerPath")!=null){
			usingCodeVerPath=String.valueOf(params.get("usingCodeVerPath"));
		}
		 
		//本地保存目录
		String savePath=String.valueOf(params.get("savePath"));
		
		int updateAll=Integer.valueOf(""+params.get("updateAll"));
		
		Integer hostStatus=Integer.valueOf(""+params.get("hostStatus"));
		 
		
		//更新全部或新机器加入
		if(updateAll==1||hostStatus==1){
			SVNUtil.getDeta4UpdateAll(doingCodeVerPath, headRev, savePath);
		}else{
			SVNUtil.getDelta(usingCodeVerPath, headRev, doingCodeVerPath, headRev, savePath);
		} 
		logger.debug("代码获取成功...");
	} 
	
	
}

/**
* Licensed under the Apache License, Version 2.0 (the "License");  *
 you may not use this file except in compliance with the License.  *
 You may obtain a copy of the License at  *
  *
      http://www.apache.org/licenses/LICENSE-2.0  *
  *
 Unless required by applicable law or agreed to in writing, software  *
 distributed under the License is distributed on an "AS IS" BASIS,  *
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 See the License for the specific language governing permissions and  *
 limitations under the License. */

package com.github.ipaas.ideploy.controller.proc;
 
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  

import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ideploy.controller.agent.AgentCmd;
import com.github.ipaas.ideploy.controller.agent.AgentCmdExecutor;
import com.github.ipaas.ideploy.controller.agent.AgentCmdType;
import com.github.ipaas.ideploy.controller.agent.AgentFeed;

/** 
* 下载代码  
* @author  wudg
*/

public class DownLoadCodeProc extends ProcWithLog {
	
	private static Logger logger = LoggerFactory.getLogger(DownLoadCodeProc.class);
	  
	 
	@Override
	public void executeWithLog() throws Exception { 
		
		
		Map<String,Object> params=procInput.getParams();
		
		logger.debug("下载代码:  "+params);
		
		//服务组id
		String servGroup=String.valueOf(params.get("servGroup"));
				
		String doingCodeVer=String.valueOf(params.get("codeVer"));
		
		//部署路径
		String deployPath=String.valueOf(params.get("deployPath"));
		
		
		//正要安装版本
		Long svnVerDoing=(Long)params.get("svnVerDoing");
		
		int updateAll=Integer.valueOf(""+params.get("updateAll"));
		 
		
		//svn库地址
		String remoteRoot="/"+servGroup+CRSConstants.SERVER_GROUP_RC_CODEPATH+"/";
		
		String savePath=CRSConstants.APPTMP_ROOT_PATH+"/"+servGroup+"/"+doingCodeVer;
		
		String appDeployPath=CRSConstants.APP_DEPLOY_ROOT_PATH+deployPath;
		
		
		Map<String,Object> cmdParams=new HashMap<String,Object>();
		 

		cmdParams.put("savePath", savePath);
		cmdParams.put("appDeployPath",appDeployPath);
		cmdParams.put("doingCodeVerPath", remoteRoot+doingCodeVer);
		cmdParams.put("svnVerDoing", svnVerDoing);
		cmdParams.put("updateAll", updateAll);
		cmdParams.put("hostStatus", params.get("hostStatus"));
		
		if(params.get("usingCodeVer")!=null){
			String usingCodeVer=String.valueOf(params.get("usingCodeVer"));
			cmdParams.put("usingCodeVerPath", remoteRoot+usingCodeVer);
		}
		
		if(params.get("svnVerUsing")!=null){
			//正在使用的svn版本
			Long svnVerUsing=(Long)params.get("svnVerUsing");
			cmdParams.put("svnVerUsing", svnVerUsing);
		}
		
		//向agent 下发指令 
		AgentCmd agentCmd=new AgentCmd(procInput.getFlowId(),procInput.getExecTerminal(),AgentCmdType.DOWMLOAD_NEWCODE);
			
		agentCmd.setParams(cmdParams);
			
		AgentCmdExecutor  agentCmdExecutor=AgentCmdExecutor.newInstance();
			
		AgentFeed agentFeed=agentCmdExecutor.executeCmd(agentCmd);
		
		
		
			//检查agent返回信息
		if(agentFeed.getRpsCode()==AgentFeed.FAIL){
			throw new Exception(agentFeed.getErrMsg());
		}
		 
	}
	 

	
}

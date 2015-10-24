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
 * 更新代码 
 * @author  wudg
 */

public class UpdateCodeProc  extends ProcWithLog {
	
	private static Logger logger = LoggerFactory.getLogger(UpdateCodeProc.class);
	 
	@Override
	public void executeWithLog() throws Exception {
		 
		Map<String,Object> params=procInput.getParams();
		 
		String agentIp=procInput.getExecTerminal();
		
		
		String servGroup=String.valueOf(params.get("servGroup"));
		String codeVer=String.valueOf(params.get("codeVer"));
		String deployPath=String.valueOf(params.get("deployPath"));
		
		//正要安装版本
		Long svnVerDoing=(Long)params.get("svnVerDoing");
						
		//正在使用的svn版本
		Long svnVerUsing=(Long)params.get("svnVerUsing");
		
		String fileName="/"+servGroup+"/"+codeVer;
		 
		//更新文件位置   /www/apptmp/testweb/testweb_201401010101
		String updatePath=CRSConstants.APPTMP_ROOT_PATH+"/"+fileName;
		
		//被更新即部署目录位置  /www/apptmp/testweb_dly
 		String targetPath=CRSConstants.APP_DEPLOY_ROOT_PATH+deployPath;
		 
		Map<String,Object> cmdParam=new HashMap<String,Object>();
		  
		cmdParam.put("srcPath", updatePath);
		cmdParam.put("targetPath", targetPath);
		  
 		AgentCmd agentCmd=new AgentCmd(procInput.getFlowId(),agentIp,AgentCmdType.UPDATE_CODE);
 		 
 		agentCmd.setParams(cmdParam);
 		
		AgentCmdExecutor  agentCmdExecutor=AgentCmdExecutor.newInstance();
		
		AgentFeed agentFeed=agentCmdExecutor.executeCmd(agentCmd);
		
		if(agentFeed.getRpsCode()==AgentFeed.FAIL){
			throw new Exception(agentFeed.getErrMsg());
		}
	}

}

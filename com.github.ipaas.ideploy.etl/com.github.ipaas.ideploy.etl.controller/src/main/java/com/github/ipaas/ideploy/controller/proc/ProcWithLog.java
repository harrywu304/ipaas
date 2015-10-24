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

import java.util.Date; 

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.AgentDeloyEvt;
import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.DeployProcStatus;
import com.github.ipaas.ideploy.controller.Result;

/** 
 * 带日志记录的proc
 * @author  wudg
 */

public abstract class ProcWithLog   extends  Proc {
	 
	private static Logger logger = LoggerFactory.getLogger(ProcWithLog.class);
	 
	protected String  succLog;
	  
	 
	
	
	
	public void setSuccLog(String succLog) {
		this.succLog = succLog;
	}
 
	public void execute() throws Exception {
		 
		//新事件日志----
		AgentDeloyEvt evt=new AgentDeloyEvt();
		evt.setCreateTime(new Date());
		evt.setDeployFlowId(procInput.getFlowId());
		evt.setEvtDescr("正在"+getProcMeta().getDescr());
		evt.setAgentIp(procInput.getExecTerminal());
		evt.setProc(getProcMeta().getKey());
		evt.setStatus(DeployProcStatus.UNDERWAY);
		CommonDataUtil.createAgentEvtLog(evt);
		 
		ProcResult procResult=new ProcResult();
		try{
			//开始执行具体逻辑
			executeWithLog();
			procResult.setResultCode(Result.SUCC); 
			if(succLog==null){
				succLog=getProcMeta().getDescr()+"完成";
			}
			  
			//新事件日志
			evt.setEvtDescr(succLog);
			evt.setStatus(DeployProcStatus.FINISHED);
			 
		}catch(Exception e){
			logger.error(getProcMeta().getDescr()+"失败", e);
			procResult.setResultCode(Result.FAIL);
			procResult.setRemark(e.getMessage());
			   
			//新事件日志
			evt.setEvtDescr(getProcMeta().getDescr()+"失败");
			evt.setEvtAttach(procResult.getRemark());
			evt.setStatus(DeployProcStatus.EXCEPT);
			 
		} 
		   
		//新事件日志
		evt.setCreateTime(new Date());
		CommonDataUtil.createAgentEvtLog(evt);
		
		//出错时抛出异常
		if(procResult.getResultCode()==Result.FAIL){
			throw new Exception(procResult.getRemark());
		}
		 
	}
	
	public abstract void executeWithLog() throws Exception;
	
	
}

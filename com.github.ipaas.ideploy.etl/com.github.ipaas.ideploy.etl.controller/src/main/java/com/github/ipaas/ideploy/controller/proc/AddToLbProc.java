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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/** 
 * 将服务加入负载均衡
 * @author  wudg
 */

public class AddToLbProc  extends ProcWithLog{
	
	private static Logger logger = LoggerFactory.getLogger(AddToLbProc.class);
	
	@Override
	public boolean toExec() {
 
		Map<String,Object> params=procInput.getParams();
		
		Integer lbFlag=(Integer)params.get("lbFlag"); 
		
		// 需检查 lbFlag 看是否操作负载均衡
		if(lbFlag!=1){ 
			return false;
		}else{
			return true;
		}
	}
	  
	@Override
	public void executeWithLog() throws Exception {
		Map<String,Object> params=procInput.getParams();
		 
		String agentIp=procInput.getExecTerminal();
		 
		Thread.sleep(3000); 
	}
	
	

}

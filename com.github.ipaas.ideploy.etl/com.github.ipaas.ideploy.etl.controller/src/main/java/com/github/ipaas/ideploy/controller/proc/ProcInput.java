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

/** 
 * proc输入参数
 * @author  wudg
 */

public class ProcInput {
	
	//流程id
	private String flowId;
	
	private String execTerminal;
	
	//参数
	private Map<String,Object> params;
	
	 
	public ProcInput() {}
	
	public ProcInput(String flowId,String execTerminal, Map<String, Object> params) {
		this.flowId = flowId;
		this.execTerminal=execTerminal;
		this.params = params;
	}
  
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	 
	public String getExecTerminal() {
		return execTerminal;
	}

	public void setExecTerminal(String execTerminal) {
		this.execTerminal = execTerminal;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	} 

}

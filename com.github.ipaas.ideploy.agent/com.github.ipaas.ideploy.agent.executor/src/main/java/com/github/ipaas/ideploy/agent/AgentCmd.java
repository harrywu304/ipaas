/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.ipaas.ideploy.agent;

import java.util.HashMap;
import java.util.Map;

/** 
 * CRS 下发到agent的指令
 * @author  wudg
 */

public class AgentCmd {
	 
	/**
	 * 流程号
	 */
	private String flowId;
	
	
	/**
	 * 目标agent
	 */
	private String targetAgent;
	
	/**
	 * 指令
	 */
	private String cmd;
	
	/**
	 * 参数
	 */
	private Map<String,Object> params;
	
	
	
	public AgentCmd() {}
	
	

	public AgentCmd(String flowId,String targetAgent, String cmd) { 
		this.flowId = flowId;
		this.targetAgent=targetAgent;
		this.cmd = cmd;
	}
	
	public void addParam(String name,Object value){
		if(params==null){
			params=new HashMap<String,Object>(); 
		}
		params.put(name, value);
	}



	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}
	 
	public String getTargetAgent() {
		return targetAgent;
	}
 
	public void setTargetAgent(String targetAgent) {
		this.targetAgent = targetAgent;
	}
 
	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

}

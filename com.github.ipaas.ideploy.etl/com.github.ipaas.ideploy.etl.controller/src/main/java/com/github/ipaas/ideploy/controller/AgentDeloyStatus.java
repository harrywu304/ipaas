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

package com.github.ipaas.ideploy.controller;

/** 
 * agent代码 发布(进度)状态
 * @author  wudg
 */

public class AgentDeloyStatus {
	
	/**
	 * 正在执行
	 */
	public final static int UNDERWAY=1;
	
	/**
	 * 已经结束
	 */
	public final static int FINISHED=2;
	
	/**
	 * 执行过程出现异常
	 */
	public final static int EXCEPT=3;

	
	private String agentIp;
	
	private String proc;
	
	private int status;
	
	private String deployFlowId;
	
	public AgentDeloyStatus(){}
	 
	public AgentDeloyStatus(String agentIp, String proc, int status,
			String deployFlowId) { 
		this.agentIp = agentIp;
		this.proc = proc;
		this.status = status;
		this.deployFlowId = deployFlowId;
	}




	public String getAgentIp() {
		return agentIp;
	}

	public void setAgentIp(String agentIp) {
		this.agentIp = agentIp;
	}

	public String getProc() {
		return proc;
	}

	public void setProc(String proc) {
		this.proc = proc;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDeployFlowId() {
		return deployFlowId;
	}

	public void setDeployFlowId(String deployFlowId) {
		this.deployFlowId = deployFlowId;
	}
	
	
}

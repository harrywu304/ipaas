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

/** 
 * 每个agent所处的流程状态
 * @author  wudg
 */

public class AgentProcStatus {
	 
	/**
	 * 流程id
	 */
	private String flowId;
	
	/**
	 * agent(ip)
	 */
	private String agent;
	
	/**
	 * 代表流程的字符串
	 */
	private String proc;
	
	/**
	 * 状态  1-正在进行;2-执行成功;3-执行失败
	 */
	private int status;
	 
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
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
	
}

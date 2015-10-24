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

import java.util.Date;

/** 
 * 类或接口作用描述 
 * @author  wudg
 */

public class AgentDeloyEvt {
	 
	private int evtId;
	
	private String agentIp;
	
	private String proc;
	
	private int status;
	
	private String evtDescr;
	
	private String evtAttach;
	
	private Date createTime;
	
	private String deployFlowId;

	public int getEvtId() {
		return evtId;
	}

	public void setEvtId(int evtId) {
		this.evtId = evtId;
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

	public String getEvtDescr() {
		return evtDescr;
	}

	public void setEvtDescr(String evtDescr) {
		this.evtDescr = evtDescr;
	}

	public String getEvtAttach() {
		return evtAttach;
	}

	public void setEvtAttach(String evtAttach) {
		this.evtAttach = evtAttach;
	}
 
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getDeployFlowId() {
		return deployFlowId;
	}

	public void setDeployFlowId(String deployFlowId) {
		this.deployFlowId = deployFlowId;
	}
	
	

}

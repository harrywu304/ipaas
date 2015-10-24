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

import java.util.List;
import java.util.Map;

/** 
 * 用于代码发布的基本信息
 * @author  wudg
 */

public class RInfo {
	
	private String deployId;
	
	private Map<String,Object> params;
	
	private List<AgentHost> servList;

	public String getDeployId() {
		return deployId;
	}

	public void setDeployId(String deployId) {
		this.deployId = deployId;
	}

	public Map<String, Object> getParams() {
		return params;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public List<AgentHost> getServList() {
		return servList;
	}

	public void setServList(List<AgentHost> servList) {
		this.servList = servList;
	} 
}

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

package com.github.ipaas.ideploy.controller.flow;

import java.util.concurrent.Callable;

import com.github.ipaas.ideploy.controller.Result;

/**
 * 流程(父类)
 * 
 * @author wudg
 */

public abstract class Flow implements Callable<Result> {

	private FlowInput flowInput;

	private String agent;
	
	public FlowInput getFlowInput() {
		return flowInput;
	}

	public void setFlowInput(FlowInput flowInput) {
		this.flowInput = flowInput;
	}

	public String getAgent() {
		return agent;
	}

	public void setAgent(String agent) {
		this.agent = agent;
	}
	
	

}

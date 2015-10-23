/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent;

import java.util.Map;

/** 
 * CRS agent的反馈
 * @author  wudg
 */

public class AgentFeed {
	
public static final int SUCC=1;
	
	public static final int FAIL=-1;
	
	
	/**
	 * 流程号
	 */
	private String flowId;
	
	/**
	 * 指令
	 */
	private String cmd;
	
	/**
	 * 反馈的agent ip
	 */
	private String rpsor;
	
	/**
	 * 1-正常;-1-代表异常
	 */
	private int rpsCode;
	
	/**
	 * 携带异常信息
	 */
	private String errMsg;
	
	/**
	 *附加数据 
	 */
	private Map<String,Object> attachData;
	
	
	public String getFlowId() {
		return flowId;
	}

	public void setFlowId(String flowId) {
		this.flowId = flowId;
	}

	public String getCmd() {
		return cmd;
	}

	public void setCmd(String cmd) {
		this.cmd = cmd;
	}

	public int getRpsCode() {
		return rpsCode;
	}

	public void setRpsCode(int rpsCode) {
		this.rpsCode = rpsCode;
	}
	 
	public String getRpsor() {
		return rpsor;
	}

	public void setRpsor(String rpsor) {
		this.rpsor = rpsor;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public Map<String, Object> getAttachData() {
		return attachData;
	}

	public void setAttachData(Map<String, Object> attachData) {
		this.attachData = attachData;
	} 
	
}

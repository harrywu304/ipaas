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
 * agent所在主机相关信息
 * 
 * @author wudg
 */

public class AgentHost implements Comparable<AgentHost> {
	/**
	 * 预览服务器, 1表示是预览服务器, -1 表示是非预览服务器
	 */
	public static int PREVIEW_FLAG_STATUS = 1;
	/**
	 * ip
	 */
	private String ip;

	/**
	 * jmx端口
	 */
	private int jmxPort;

	/**
	 * 预览服务器, 1表示是预览服务器, -1 表示是非预览服务器
	 */
	private int previewFlag;
	/**
	 * 预览服务器, 1表示是预览服务器, -1 表示是非预览服务器
	 */
	public int getPreviewFlag() {
		return previewFlag;
	}
	/**
	 * 预览服务器, 1表示是预览服务器, -1 表示是非预览服务器
	 */
	public void setPreviewFlag(int previewFlag) {
		this.previewFlag = previewFlag;
	}

	/**
	 * 所在阶段
	 */
	private String proc;

	/**
	 * 运行状态
	 */
	private int status;

	/**
	 * 服务器状态 1-未部署(过代码);2-部署
	 */
	private int hostStatus;

	public AgentHost() {
	}

	public AgentHost(String ip, int jmxPort) {
		this.ip = ip;
		this.jmxPort = jmxPort;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getJmxPort() {
		return jmxPort;
	}

	public void setJmxPort(int jmxPort) {
		this.jmxPort = jmxPort;
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

	public int getHostStatus() {
		return hostStatus;
	}

	public void setHostStatus(int hostStatus) {
		this.hostStatus = hostStatus;
	}

	@Override
	public int compareTo(AgentHost o) {
		return this.getIp().compareTo(o.getIp());
	}

}

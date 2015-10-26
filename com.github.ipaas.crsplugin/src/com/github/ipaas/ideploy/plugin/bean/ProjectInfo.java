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
package com.github.ipaas.ideploy.plugin.bean;

import java.util.List;
import java.util.Map;

import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;

/**
 * 项目信息
 * 
 * @author Chenql  
 */
public class ProjectInfo {
	private List<String> projectList;
	private List<ServGroup> servGroupList;
	private Integer userId;


	/**
	 * 获取服务组的容器类型
	 * 
	 * @param id
	 *            服务组Id
	 * @return
	 */
	public String getSgType(String id) {
		String sgType = "";
		ServGroup sg = this.getServGroup(id);
		if (sg != null) {
			sgType = sg.getActName();
		}
		return sgType;
	}


	public List<String> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<String> projectList) {
		this.projectList = projectList;
	}

	public List<ServGroup> getServGroupList() {
		return servGroupList;
	}

	public void setServGroupList(List<ServGroup> servGroupList) {
		this.servGroupList = servGroupList;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public ServGroup getServGroup(String id) {
		if (this.servGroupList == null) {
			ConsoleHandler.error("没有服务组信息");
			return null;
		}
		for (ServGroup servGroup : this.servGroupList) {
			if (servGroup.getId().equals(id)) {
				return servGroup;
			}
		}
		return null;
	}
}

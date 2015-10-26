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

/**
 * 服务组
 * 
 * @author Chenql  
 */
public class ServGroup {
	private int sgId;
	private String id;
	private String realName;
	private int appRowId;
	private int codeId;
	private String actName;// 容器类型

	public String getActName() {
		return actName;
	}

	public void setActName(String actName) {
		this.actName = actName;
	}

	public int getSgId() {
		return sgId;
	}

	public void setSgId(int sgId) {
		this.sgId = sgId;
	}

	/**
	 * 自定义服务组ID
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	/**
	 * 自定义服务组ID
	 */
	public void setId(String id) {
		this.id = id;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public int getAppRowId() {
		return appRowId;
	}

	public void setAppRowId(int appRowId) {
		this.appRowId = appRowId;
	}

	public int getCodeId() {
		return codeId;
	}

	public void setCodeId(int codeId) {
		this.codeId = codeId;
	}
}

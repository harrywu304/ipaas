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

import java.io.Serializable;

/**
 * 界面信息保存
 * 
 * @author Chenql  
 */
public class PathInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String groupId;
	private String savePath;
	private String srcPath;
	private String targetPath;

	private String sourceCodeSvnUrl;// svn版本号
	private Long sourceCodeSvnRevision;// svn地址

	public String getSourceCodeSvnUrl() {
		return sourceCodeSvnUrl;
	}

	public void setSourceCodeSvnUrl(String sourceCodeSvnUrl) {
		this.sourceCodeSvnUrl = sourceCodeSvnUrl;
	}

	public Long getSourceCodeSvnRevision() {
		return sourceCodeSvnRevision;
	}

	public void setSourceCodeSvnRevision(Long sourceCodeSvnRevision) {
		this.sourceCodeSvnRevision = sourceCodeSvnRevision;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}

	public String getSrcPath() {
		return srcPath;
	}

	public void setSrcPath(String srcPath) {
		this.srcPath = srcPath;
	}

	public PathInfo() {
		super();
	}

	public PathInfo(String groupId, String savePath, String srcPath) {
		super();
		this.groupId = groupId;
		this.savePath = savePath;
		this.srcPath = srcPath;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

}

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

import java.util.TreeSet;

public class TreeNode implements Comparable<Object> {

	private String relativePath;
	private String nodeValue;
	private int nodeType;// 1表示目录,2表示文件
	public int getNodeType() {
		return nodeType;
	}

	public void setNodeType(int nodeType) {
		this.nodeType = nodeType;
	}

	private TreeSet<TreeNode> childSet = new TreeSet<TreeNode>();

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getNodeValue() {
		return nodeValue;
	}

	public void setNodeValue(String nodeValue) {
		this.nodeValue = nodeValue;
	}

	public TreeSet<TreeNode> getChildSet() {
		return childSet;
	}

	public void setChildSet(TreeSet<TreeNode> childSet) {
		this.childSet = childSet;
	}

	public TreeNode(String relativePath, String nodeValue, TreeSet<TreeNode> set) {
		super();
		this.relativePath = relativePath;
		this.nodeValue = nodeValue;
		this.childSet = set;
	}

	public TreeNode() {

	}

	@Override
	public int compareTo(Object arg0) {
		return this.relativePath.compareTo(((TreeNode) arg0).relativePath);
	}

	@Override
	public boolean equals(Object another) {
		return this.getRelativePath().equals(
				((TreeNode) another).getRelativePath());
	}
}

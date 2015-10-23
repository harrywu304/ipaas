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
package com.github.ipaas.ifw.jdbc.impl;

import java.util.Date;

/**
 * @author Chenql
 */
public class DemoUser {
	private int id;
	private String name;
	private String address;
	private String content;
	private Date loginTime;
	private Date lastActionTime;

	public DemoUser() {

	}

	public DemoUser(String name, String address, String content) {
		super();
		this.name = name;
		this.address = address;
		this.content = content;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getLoginTime() {
		return lastActionTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	public Date getLastActionTime() {
		return lastActionTime;
	}

	public void setLastActionTime(Date lastActionTime) {
		this.lastActionTime = lastActionTime;
	}

	@Override
	public String toString() {
		return id + "," + name + "," + address + "," + content + "," + loginTime + "," + lastActionTime;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		return this.toString().equals(obj.toString());
	}

	public static void main(String[] args) {

	}
}

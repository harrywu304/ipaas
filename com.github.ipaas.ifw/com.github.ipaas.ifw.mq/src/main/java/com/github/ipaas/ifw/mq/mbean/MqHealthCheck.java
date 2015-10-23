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

package com.github.ipaas.ifw.mq.mbean;

/**
 * MQ可用性检测MBean,小MBean
 * 
 * @author Chenql
 */
public class MqHealthCheck implements MqHealthCheckMBean {

	public static String TYPE = "MqHealthCheck";

	/**
	 * 服务ID
	 */
	private String serviceId = null;
	private String userName = null;

	private String password = null;
	/**
	 * ip地址
	 */
	private String ip = null;

	/**
	 * 端口
	 */
	private String port = null;

	/**
	 * @param serviceId
	 * @param ip
	 * @param port
	 * @param userName
	 * @param password
	 */
	public MqHealthCheck(String serviceId, String ip, String port, String userName, String password) {
		super();
		this.serviceId = serviceId;
		this.userName = userName;
		this.password = password;
		this.ip = ip;
		this.port = port;
	}

	@Override
	public String getTargetIp() {
		return ip;
	}

	@Override
	public String getTargetPort() {
		return port;
	}

	@Override
	public String getState() {
		MqCheckServiceImpl mqCheckServiceImpl = new MqCheckServiceImpl();
		return mqCheckServiceImpl.check(ip, port, userName, password);
	}

	@Override
	public String getUri() {
		String className = "MqHealthCheck";
		String methodName = "getState";
		String slash = "/";
		StringBuffer sb = new StringBuffer();
		sb.append(slash).append(serviceId).append(slash).append(className).append(slash).append(methodName);
		return sb.toString();
	}

	@Override
	public String getServiceId() {
		return serviceId;
	}

}

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

import java.util.ResourceBundle;

/**
 * 常量
 * 
 * @author wudg
 */

public class CRSConstants {

	private static ResourceBundle resourceBundle;

	/**
	 * 获取配置属性
	 * 
	 * @param propName
	 * @return
	 */
	private static String getProperty(String propName) {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle("controller_config");
		}
		String v = resourceBundle.getString(propName);
		if (v != null) {
			v = v.trim();
		}
		return v;
	}

	public static String DB_CONNECT_SERVICE = "dbConnectService";

	public static String DB_ACCESS_SERVICE = "dbAccessService";

	//public static String MEMCACHED_SERVICE = "memcachedService";

	public static String MQ_SEND_SERVICE = "mqSendService";

	public static String MQ_LinsenSERVICE = "mqListenService";

	public static final String CONTROLLER_ALIAS = "controller";

	/**
	 * Controller接收Agent消息的MQ队列 CRS_CTRLER_AGENT_QUEUE
	 */
	public static final String CRS_CTRLER_AGENT_QUEUE = "crs.ctrler.agent.queue";

	/**
	 * agent接收Controller指令的 (MQ)队列 CRS_AGENT_{agent_ip}
	 */
	public static final String AGENT_CTRL_QUEUE = "crs.agent.";

	/**
	 * 每次更新的服务器数占总数的比例
	 */
	public static final float PER_UP_AGENT_NO = Float.valueOf(getProperty("PER_UP_AGENT_NO"));

	/**
	 * KEY_IN_CMD_* 指令中主要参数的名称
	 */

	/**
	 * agent 在crs中用一般用ip来对应一台服务器, 如指令对象中有 agent:"127.0.0.1"
	 * 表示消息发向或发至ip为127.0.0.1的服务器的agent
	 */
	public static final String KEY_IN_CMD_AGENT = "agent";

	/**
	 * 指令参数名称
	 */
	public static final String KEY_IN_CMD_CMDTYPE = "cmd";

	/**
	 * 流程号
	 */
	public static final String KEY_IN_CMD_FLOWID = "flowId";

	/**
	 * 启动服务器命令参数名
	 */
	public static final String PARAM_START_SERVER_CMD = "start_cmd";

	/**
	 * 停止服务器命令参数名
	 */
	public static final String PARAM_STOP_SERVER_CMD = "stop_cmd";

	/**
	 * svn上 服务组代码发布目录
	 */
	public static final String SERVER_GROUP_RC_CODEPATH = getProperty("SERVER_GROUP_RC_CODEPATH");

	/**
	 * 应用部署根目录
	 */
	public static final String APP_DEPLOY_ROOT_PATH = getProperty("APP_DEPLOY_ROOT_PATH");

	/**
	 * 应用日志根目录
	 */
	public static final String APPLOG_ROOT_PATH = getProperty("APPLOG_ROOT_PATH");

	/**
	 * 应用备份路径根目录
	 */
	public static final String APPBK_ROOT_PATH = getProperty("APPBK_ROOT_PATH");

	/**
	 * 应用代码下载临时目录
	 */
	public static final String APPTMP_ROOT_PATH = getProperty("APPTMP_ROOT_PATH");

	/**
	 * 第一个安装版本之前本地备份名称
	 */
	public static final String APPBK_FIRST_NAME = getProperty("APPBK_FIRST_NAME");

	/**
	 * agent反馈信息超时时间(长整型,单位:s)
	 */
	public static final long AGENT_RPS_TIMEOUT = Long.valueOf(getProperty("AGENT_RPS_TIMEOUT"));

}

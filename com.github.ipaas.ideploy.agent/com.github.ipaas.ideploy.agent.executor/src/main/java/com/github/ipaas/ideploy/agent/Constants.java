/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */

package com.github.ipaas.ideploy.agent;

import java.util.ResourceBundle;

/**
 * 类或接口作用描述
 * 
 * @author wudg
 */

public class Constants {

	private static ResourceBundle resourceBundle;

	/**
	 * 获取配置属性
	 * 
	 * @param propName
	 * @return
	 */
	private static String getProperty(String propName) {
		if (resourceBundle == null) {
			resourceBundle = ResourceBundle.getBundle("crs_config");
		}
		String v = resourceBundle.getString(propName);
		if (v != null) {
			v = v.trim();
		}
		return v;
	}

//	public static String MQ_SEND_SERVICE = "mqSendService";
//
//	public static String MQ_LinsenSERVICE = "mqListenService";

	/**
	 * appId
	 */

	public static final String CRS_APP_ID = getProperty("APP_ID");
	/**
	 * Controller接收Agent消息的MQ队列
	 */
	public static final String CRS_CTRLER_AGENT_QUEUE = getProperty("CRS_CTRLER_AGENT_QUEUE");

	/**
	 * agent接收Controller指令的 (MQ)队列 CRS_CTRL_{agent_ip}
	 */
	public static final String AGENT_CTRL_QUEUE = getProperty("AGENT_CTRL_QUEUE");

	/**
	 * 代码库地址
	 */
	public static final String CRS_REPOS = getProperty("CRS_REPOS");

	/**
	 * 代码库访问用户
	 */
	public static final String CRS_REPOS_USER = getProperty("CRS_REPOS_USER");

	/**
	 * 代码库访问密码
	 */
	public static final String CRS_REPOS_PWD = getProperty("CRS_REPOS_PWD");

	/**
	 * 代码更新包中代码目录
	 */
	public static final String UPDPKG_CODEDIR = getProperty("UPDPKG_CODEDIR");

	/**
	 * 代码更新包中更新细节文件名
	 */
	public static final String UPDPKG_UPDTXT = getProperty("UPDPKG_UPDTXT");

	/**
	 * 应用备份路径根目录
	 */
	public static final String APPBK_ROOT_PATH = getProperty("APPBK_ROOT_PATH");

	/**
	 * 文件异动监控实现类
	 */
	public static final String FILE_ALTER_MONTOR_CLS = "com.ipaas.ideploy.mon.node.CRSFileAlterationMonitor";







	/**
	 * crs svn库位置
	 */
	public static String CRS_REPOS_ROOT = getProperty("CRS_REPOS_ROOT");

	/**
	 * crs svn用户名
	 */
	public static String SVN_NAME = getProperty("SVN_NAME");

	/**
	 * crs svn密码
	 */
	public static String SVN_PASS = getProperty("SVN_PASS");

}

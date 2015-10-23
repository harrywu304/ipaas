/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent;

/** 
 * agent控制指令
 * @author  wudg
 */

public class AgentCmdType {
	  
	
	/**
	 * 下载新代码
	 */
	public static String DOWMLOAD_NEWCODE="DOWMLOAD_NEWCODE";
	
	 
	
	/**
	 * 备份代码
	 */
	public static String BACKUP_CODE="BACKUP_CODE";
	
	
	/**
	 * 回退至上个版本
	 */
	public static String ROLLBACK_CODE="ROLLBACK_CODE";
	
	
	/**
	 * 更新代码
	 */
	public static String UPDATE_CODE="UPDATE_CODE";
	
	
	/**
	 * 启动服务器
	 */
	public static String START_SERVER="START_SERVER";
	
	 
	/**
	 * 停止服务
	 */
	public static String STOP_SERVER="STOP_SERVER";
	
	
	/**
	 * 启动文件系统监控(代码异动)
	 */
	public static String START_FILESYS_MON="START_FILESYS_MON";
	
	/**
	 * 停止文件系统监控(代码异动)
	 */
	public static String STOP_FILESYS_MON="STOP_FILESYS_MON";


}

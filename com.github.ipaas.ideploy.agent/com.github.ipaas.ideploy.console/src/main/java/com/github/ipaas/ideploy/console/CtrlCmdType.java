/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.console;

/** 
 * 全局控制指令类型 
 * @author  wudg
 */

public class CtrlCmdType {
	
	/**
	 * 预发布,预发布到其中一台服务器 
	 */
	public final static String PRE_RELEASE="PRE_RELEASE";
	
	
	/**
	 *正式发布
	 */
	public final static String STANDARD_RELEASE="STANDARD_RELEASE";
	 
	/**
	 *回退
	 */
	public final static String ROLLBACK="ROLLBACK";
	
	
	/**
	 * 重装
	 */
	public final static String REINSTALL="REINSTALL";
	
	
	/**
	 * 忽略异常，继续安装
	 */
	public final static String CONTI_IGNORE_ERR="CONTI_IGNORE_ERR";
	
	
	
}

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

/** 
 * 发布流程状态 
 * @author  wudg
 */

public class DeployProcStatus {

	/**
	 * 正在执行
	 */
	public final static int UNDERWAY=1;
	
	/**
	 * 已经结束
	 */
	public final static int FINISHED=2;
	
	/**
	 * 执行过程出现异常
	 */
	public final static int EXCEPT=3;
}

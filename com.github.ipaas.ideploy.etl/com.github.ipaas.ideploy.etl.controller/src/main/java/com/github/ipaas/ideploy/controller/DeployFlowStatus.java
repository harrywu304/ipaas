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
 * 
 * @author wudg
 */

public class DeployFlowStatus {

	/**
	 * 未开始
	 */
	public static int NO_STARTED = 1;

	/**
	 * 正在进行
	 */
	public static int UNDERWAY = 2;

	/**
	 * 成功结束
	 */
	public static int FINISH_SUCC = 3;

	/**
	 * 流程结束，但中间有错
	 */
	public static int FINISH_WITH_ERR = 4;

	/**
	 * 因异常而中断
	 */
	public static int INTER_BY_EXCEP = 5;

	/**
	 * 第一台失败
	 */
	public static int FIRSTONE_EXCEP = 6;

	/**
	 * 正在回退
	 */
	public static int ROLLBACKING = 7;

	/**
	 * 回退失败
	 */
	public static int ROLLBACK_FAIL = 8;

	/**
	 * 等待用户预览
	 */
	public static int WAIT_PREV = 9;

	/**
	 * 准备过程失败
	 */
	public static int PRE_FLOW_FAIL = 10;

	/**
	 * 已经回退
	 */
	public static int ROLLBACK_SUCC = 11;

	/**
	 * 安装异常,手动中止流程
	 */
	public static int INTERRUPT_BY_MAN = 12;

}

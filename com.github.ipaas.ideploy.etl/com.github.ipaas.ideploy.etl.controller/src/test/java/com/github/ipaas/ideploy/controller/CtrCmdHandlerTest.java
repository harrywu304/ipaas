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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;

import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ideploy.console.CtrlCmd;
import com.github.ipaas.ideploy.console.CtrlCmdType;
import com.github.ipaas.ideploy.controller.job.ContiAfterFirstOne;
import com.github.ipaas.ideploy.controller.job.ContiFlowIgnoringErr;
import com.github.ipaas.ideploy.controller.job.Preinstall;
import com.github.ipaas.ideploy.controller.job.Reinstall;
import com.github.ipaas.ideploy.controller.job.Rollback;
import com.github.ipaas.ifw.mq.FwMessage;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * CtrCmd处理测试
 * 
 * @author Chenql
 */
public class CtrCmdHandlerTest {

	@Mocked
	final CommonDataUtil util = null;
	@Mocked
	Preinstall preinstall;
	@Mocked
	ContiAfterFirstOne contiAfterFirstOne;
	@Mocked
	Reinstall reinstall;
	@Mocked
	Rollback rollback;
	@Mocked
	ContiFlowIgnoringErr contiFlowIgnoringErr;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {

	}

	@Test
	public void handleTest() throws Exception {
		CtrlCmd cmd = new CtrlCmd();
		cmd.setParams(new HashMap<String, Object>());
		Message msg = new FwMessage();
		CtrCmdHandler handler = new CtrCmdHandler(null);

		// ----------------预安装
		new NonStrictExpectations() {
			{
				preinstall.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};
		cmd.setCmd(CtrlCmdType.PRE_RELEASE);
		msg.setContent(JsonUtil.toJson(cmd));
		handler.handle(msg);
		new Verifications() {
			{
				preinstall.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};

		// ----------------继续安装
		new NonStrictExpectations() {
			{
				contiAfterFirstOne.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1;
			}
		};
		cmd.setCmd(CtrlCmdType.STANDARD_RELEASE);
		msg.setContent(JsonUtil.toJson(cmd));
		handler.handle(msg);
		new Verifications() {
			{
				contiAfterFirstOne.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1;
			}
		};

		// ----------------忽略前面的失败项，继续安装
		new NonStrictExpectations() {
			{
				contiFlowIgnoringErr.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};
		cmd.setCmd(CtrlCmdType.CONTI_IGNORE_ERR);
		msg.setContent(JsonUtil.toJson(cmd));
		handler.handle(msg);

		new Verifications() {
			{
				contiFlowIgnoringErr.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};

		// ----------------重装(失败)项目
		new NonStrictExpectations() {
			{
				reinstall.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};
		cmd.setCmd(CtrlCmdType.REINSTALL);
		msg.setContent(JsonUtil.toJson(cmd));
		handler.handle(msg);
		new Verifications() {
			{
				reinstall.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};

		// ----------------重装(失败)项目
		new NonStrictExpectations() {
			{
				rollback.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};
		cmd.setCmd(CtrlCmdType.ROLLBACK);
		msg.setContent(JsonUtil.toJson(cmd));
		handler.handle(msg);
		new Verifications() {
			{
				rollback.execute((Map<String, Object>) withAny(null), (ExecutorService) withAny(null));
				times = 1; // 预期安装调用一次
			}
		};
	}
}

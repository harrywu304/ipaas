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
package com.github.ipaas.ideploy.controller.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mockit.Expectations;
import mockit.Mocked;

import org.junit.Test;

import com.github.ipaas.ideploy.controller.AgentHost;
import com.github.ipaas.ideploy.controller.AgentHostStatus;
import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.DeployFlowStatus;
import com.github.ipaas.ideploy.controller.FileSysMonSwitch;
import com.github.ipaas.ideploy.controller.MulUnitExecutor;
import com.github.ipaas.ideploy.controller.RInfo;
import com.github.ipaas.ideploy.controller.Result;
import com.github.ipaas.ideploy.controller.Utils;
import com.github.ipaas.ideploy.controller.flow.FlowExecutor;
import com.github.ipaas.ideploy.controller.job.ContiAfterFirstOne;
import com.github.ipaas.ideploy.controller.proc.ProcInput;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;

/**
 * 首台后继续安装测试类
 * 
 * @author Chenql
 */
public class ContiAfterFirstOneTest {
	@Mocked
	final CommonDataUtil util = null;

	@Mocked
	final Utils utils = null;
	@Mocked
	final FileSysMonSwitch fileSysMonSwitch = null;

	final MulUnitExecutor mulUnitExecutor = MulUnitExecutor.newInstance();

	final String deployFlowId = "junitTest";

	FlowExecutor flowExecutor = FlowExecutor.newInstance();

	final ContiAfterFirstOne contiAfterFirstOne = new ContiAfterFirstOne();

	@Test
	public void executeTest() throws Exception {
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("deployFlowId", deployFlowId);

		/********** 不允许安装 ***********/
		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				returns(DeployFlowStatus.FINISH_SUCC);

				CommonDataUtil.getRlInfo(deployFlowId);
				times = 0;
			}
		};
		contiAfterFirstOne.execute(params, null);

		/********** 允许安装 ***********/

		final RInfo rInfo = new RInfo();
		rInfo.setServList(new ArrayList<AgentHost>());
		rInfo.setParams(new HashMap<String, Object>());

		final Map<String, Object> deployParams = new HashMap<String, Object>();
		final Integer sgId = 1;
		final Integer prevCodeId = 2;
		final Integer codeId = 3;
		final String servGroup = "servGroup";
		deployParams.put("usingCodeId", prevCodeId);
		deployParams.put("codeId", codeId);
		deployParams.put("sgId", sgId);
		deployParams.put("servGroup", servGroup);
		rInfo.getParams().putAll(deployParams);

		// 没有机器
		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.WAIT_PREV;

				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;
			}
		};
		contiAfterFirstOne.execute(params, null);

		AgentHost host1 = new AgentHost();
		host1.setIp("host1");
		host1.setProc(ProcMeta.WAITFOR_PREV_FISRT);
		rInfo.getServList().add(host1);
		AgentHost host2 = new AgentHost();
		host2.setProc(ProcMeta.CONTI4FIRSTONE);
		host1.setIp("host2");
		rInfo.getServList().add(host2);

		// 全部安装成功
		new Expectations(MulUnitExecutor.class, FlowExecutor.class) {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.WAIT_PREV;
				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;
				// 重置状态为正在执行
				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
				times = 1;

				// 用户预览后首台继续执行的收尾流程
				flowExecutor.execute((ProcInput) any, (String[]) any);
				times = 1;
				result = Result.SUCC;

				Utils.getPerUpServNo(1);
				times = 1;
				result = 1;

				// 执行更新
				mulUnitExecutor.execute();
				times = 1;

				// 全部更新成功
				mulUnitExecutor.getFailProcList();
				times = 1;
				result = new ArrayList<String>();
				CommonDataUtil.updateHostStatus(sgId, (List<AgentHost>) any, AgentHostStatus.DEPLOYED);
				times = 1;

				// 更新流程状态为成功
				CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId, sgId, prevCodeId, (Date) any,
						DeployFlowStatus.FINISH_SUCC);
				times = 1;

				// 更新代码版本状态
				CommonDataUtil.updateCodeStatus4DeploySucc(sgId, codeId);
				times = 1;

				// 启动文件监控模块
//				FileSysMonSwitch.startFileSysMon(deployFlowId, servGroup, (List<AgentHost>) any);
//				times = 1;

			}
		};

		contiAfterFirstOne.execute(params, null);

		// 首台继续安装失败
		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.WAIT_PREV;
				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;
				// 重置状态为正在执行
				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
				times = 1;
				// 用户预览后首台继续执行的收尾流程
				flowExecutor.execute((ProcInput) any, (String[]) any);
				times = 1;
				result = Result.FAIL;
			}
		};

		contiAfterFirstOne.execute(params, null);

		// 更新最后异常
		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.WAIT_PREV;
				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;
				// 重置状态为正在执行
				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
				times = 1;

				// 用户预览后首台继续执行的收尾流程
				flowExecutor.execute((ProcInput) any, (String[]) any);
				times = 1;
				result = Result.SUCC;

				Utils.getPerUpServNo(1);
				times = 1;
				result = 1;

				// 执行更新
				mulUnitExecutor.execute();
				times = 1;

				// 全部更新成功
				mulUnitExecutor.getFailProcList();
				times = 1;
				List<String> failureList = new ArrayList<String>();
				failureList.add("host1");
				result = failureList;

				CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId, sgId, prevCodeId, (Date) any,
						DeployFlowStatus.FINISH_WITH_ERR);
				times = 1;

			}
		};

		contiAfterFirstOne.execute(params, null);

		AgentHost host3 = new AgentHost();
		host3.setProc(ProcMeta.CONTI4FIRSTONE);
		host1.setIp("host3");
		rInfo.getServList().add(host3);
		// 中间出现异常
		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.WAIT_PREV;
				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;
				// 重置状态为正在执行
				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
				times = 1;

				// 用户预览后首台继续执行的收尾流程
				flowExecutor.execute((ProcInput) any, (String[]) any);
				times = 1;
				result = Result.SUCC;

				Utils.getPerUpServNo(2);
				times = 1;
				result = 1;

				// 执行更新
				mulUnitExecutor.execute();
				times = 1;

				// 全部更新成功
				mulUnitExecutor.getFailProcList();
				times = 1;
				List<String> failureList = new ArrayList<String>();
				failureList.add("host1");
				result = failureList;
				CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId, sgId, prevCodeId, (Date) any,
						DeployFlowStatus.INTER_BY_EXCEP);
				times = 1;

			}
		};

		contiAfterFirstOne.execute(params, null);
	}
}

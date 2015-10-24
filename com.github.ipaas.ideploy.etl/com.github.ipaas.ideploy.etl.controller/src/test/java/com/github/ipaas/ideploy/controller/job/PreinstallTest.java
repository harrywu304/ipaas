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

import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ideploy.controller.AgentHost;
import com.github.ipaas.ideploy.controller.AgentHostStatus;
import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.DeployFlowStatus;
import com.github.ipaas.ideploy.controller.FileSysMonSwitch;
import com.github.ipaas.ideploy.controller.MulUnitExecutor;
import com.github.ipaas.ideploy.controller.RInfo;
import com.github.ipaas.ideploy.controller.flow.FlowExecutor;
import com.github.ipaas.ideploy.controller.job.Preinstall;
import com.github.ipaas.ideploy.controller.proc.ProcInput;

/**
 * 类或接口作用描述
 * 
 * @author Chenql
 */
public class PreinstallTest {
	@Mocked
	final CommonDataUtil util = null;

	final Preinstall preinstall = new Preinstall();

	final MulUnitExecutor mulUnitExecutor = MulUnitExecutor.newInstance();

	@Mocked
	final FileSysMonSwitch fileSysMonSwitch = null;

	final String deployFlowId = "junitTest";

	FlowExecutor flowExecutor = FlowExecutor.newInstance();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void executeTest() throws Exception {

		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("deployFlowId", deployFlowId);

		/********** 不允许安装 ***********/
		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.PRE_FLOW_FAIL;
			}
		};
		preinstall.execute(params, null);

		/********** 没有机器 ***********/
		final RInfo rInfo = new RInfo();
		rInfo.setServList(new ArrayList<AgentHost>());
		rInfo.setParams(new HashMap<String, Object>());

		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.FIRSTONE_EXCEP;
				CommonDataUtil.updateDeplyFlowStatus4Start(deployFlowId, (Date) withAny(null));
				times = 1;
				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;
			}
		};

		preinstall.execute(params, null);

		/********** 只有一台机器 ***********/
		AgentHost host = new AgentHost();
		rInfo.getServList().add(host);
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

		new Expectations(new Preinstall()) {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.FIRSTONE_EXCEP;
				CommonDataUtil.updateDeplyFlowStatus4Start(deployFlowId, (Date) withAny(null));
				times = 1;

				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;

				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
				times = 1;

				preinstall.excute4One((String) any, (AgentHost) any, (Map<String, Object>) any);
				times = 1;
			}
		};
		preinstall.execute(params, null);

		/********** 有多台机器 ***********/
		AgentHost host2 = new AgentHost();
		rInfo.getServList().add(host2);

		new Expectations() {
			{
				CommonDataUtil.getDeployFlowStatus(deployFlowId);
				times = 1;
				result = DeployFlowStatus.FIRSTONE_EXCEP;
				CommonDataUtil.updateDeplyFlowStatus4Start(deployFlowId, (Date) withAny(null));
				times = 1;

				CommonDataUtil.getRlInfo(deployFlowId);
				times = 1;
				result = rInfo;

				CommonDataUtil.updateDeplyFlowStatus(deployFlowId, DeployFlowStatus.UNDERWAY);
				times = 1;

				preinstall.execute4Many((String) any, (List<AgentHost>) any, (Map<String, Object>) any);
				times = 1;
			}
		};
		preinstall.execute(params, null);
	}

	@Test
	public void excute4OneTest() throws Exception {
		final List<AgentHost> hosts = new ArrayList<AgentHost>();
		AgentHost host = new AgentHost();
		hosts.add(host);
		final Map<String, Object> deployParams = new HashMap<String, Object>();
		final Integer sgId = 1;
		final Integer prevCodeId = 2;
		final Integer codeId = 3;
		final String servGroup = "servGroup";
		deployParams.put("usingCodeId", prevCodeId);
		deployParams.put("codeId", codeId);
		deployParams.get("usingCodeId");
		deployParams.put("sgId", sgId);
		deployParams.put("servGroup", servGroup);
		new Expectations(FlowExecutor.class) {
			{
				// FileSysMonSwitch.stopFileSysMon((String) any, servGroup,
				// hosts);
				// times = 1;

				flowExecutor.execute((ProcInput) any, (String[]) any);
				times = 1;
				result = 1;
				// 更新机器状态
				CommonDataUtil.updateHostStatus(sgId, hosts, AgentHostStatus.DEPLOYED);
				// 更新流程状态为成功
				CommonDataUtil.updateDeplyFlowStatus4Finish(deployFlowId, sgId, prevCodeId, (Date) any,
						DeployFlowStatus.FINISH_SUCC);
				times = 1;

				// 更新代码版本状态
				CommonDataUtil.updateCodeStatus4DeploySucc(sgId, codeId);
				times = 1;
				// 启动文件监控模块
				// FileSysMonSwitch.startFileSysMon(deployFlowId, servGroup,
				// hosts);
				// times = 1;
			}
		};

		preinstall.excute4One(deployFlowId, host, deployParams);

	}

	/********** 有多台机器 ,先停止文件监控,全部机器下载完代码,然后后安装第一台机器, ***********/
	@Test
	public void excute4Many() throws Exception {
		final List<AgentHost> hosts = new ArrayList<AgentHost>();
		AgentHost host1 = new AgentHost();
		host1.setIp("host1");
		hosts.add(host1);
		AgentHost host2 = new AgentHost();
		host2.setIp("host2");
		hosts.add(host2);

		final Map<String, Object> deployParams = new HashMap<String, Object>();
		final Integer sgId = 1;
		final Integer prevCodeId = 2;
		final Integer codeId = 3;
		final String servGroup = "servGroup";
		deployParams.put("usingCodeId", prevCodeId);
		deployParams.put("codeId", codeId);
		deployParams.get("usingCodeId");
		deployParams.put("sgId", sgId);
		deployParams.put("servGroup", servGroup);
		new Expectations(MulUnitExecutor.class, FlowExecutor.class) {
			{
				mulUnitExecutor.execute();
				times = 1;

				mulUnitExecutor.getSuccProcList();
				times = 2;
				List<String> ipList = new ArrayList<String>();
				ipList.add("host1_0");
				ipList.add("host2_0");
				result = ipList;

				// FileSysMonSwitch.stopFileSysMon((String) any, servGroup,
				// (List<AgentHost>) any);
				// times = 1;

				flowExecutor.execute((ProcInput) any, (String[]) any);
				times = 1;
				result = 1;

				CommonDataUtil.updateDeplyFlowStatus((String) any, DeployFlowStatus.WAIT_PREV);
				times = 1;
			}
		};

		preinstall.execute4Many(deployFlowId, hosts, deployParams);

	}
}

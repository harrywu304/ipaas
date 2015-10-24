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
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.console.CmdTarget;
import com.github.ipaas.ideploy.console.CtrlCmd;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MqListenService;
import com.github.ipaas.ifw.mq.MqSendService;
import com.github.ipaas.ifw.mq.MqServiceManager;

/**
 * Controller主启动类
 * 
 * @author wudg
 */

public class Controller {

	private static Logger logger = LoggerFactory.getLogger(Controller.class);

	private static ExecutorService executor;

	public static void start() {
		if (executor == null) {
			executor = Executors.newCachedThreadPool();
		}
		try {
			MqListenService mqListenService = MqServiceManager.getMqListenService(CRSConstants.MQ_LinsenSERVICE);
			mqListenService.listenQueue(CmdTarget.CRS_MAN_CTRLER_QUEUE, new CtrCmdHandler(executor));
			logger.info("CRS Controller成功启动.");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException {
		Long currentTime = System.currentTimeMillis();
		System.out.println(currentTime);
		long i = 0;
		while (true) {

			// System.out.println(System.currentTimeMillis() - currentTime);
			// System.out.println(System.currentTimeMillis() - next);
			// next = System.currentTimeMillis();
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("deployFlowId", String.valueOf(i));
			CtrlCmd ctrlCmd = new CtrlCmd();
			ctrlCmd.setCmd("test");
			ctrlCmd.setParams(params);

			MqSendService mqSendService = MqServiceManager.getMqSendService(CRSConstants.MQ_SEND_SERVICE);
			Message message = mqSendService.createMessage();
			message.setContent(String.valueOf(i));
			System.out.println(i++);
			Thread.sleep(200);
			// System.out.println(CmdTarget.CRS_MAN_CTRLER_QUEUE +
			// "   sendInstallCmd:" + JsonUtil.toJson(ctrlCmd));
			try {
				mqSendService.sendQueue(CmdTarget.CRS_MAN_CTRLER_QUEUE, message);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
	}

	/**
	 * 获取当前 jvm 的内存信息
	 * 
	 * @return
	 */
	public static String toMemoryInfo() {

		Runtime currRuntime = Runtime.getRuntime();
		int nFreeMemory = (int) (currRuntime.freeMemory() / 1024 / 1024);
		int nTotalMemory = (int) (currRuntime.totalMemory() / 1024 / 1024);
		return nFreeMemory + "M/" + nTotalMemory + "M(free/total)";
	}

	/**
	 * 停止 Controller
	 */
	public static void stop() {
		// 关闭线程池
		executor.shutdown();
		executor = null;
		logger.info("Controller程序成功停止.");
	}

}

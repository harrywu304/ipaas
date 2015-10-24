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

package test;

import java.util.HashMap;
import java.util.Map;

import com.github.ipaas.ideploy.console.CmdTarget;
import com.github.ipaas.ideploy.console.CtrlCmd;
import com.github.ipaas.ideploy.console.CtrlCmdType;
import com.github.ipaas.ideploy.controller.CRSConstants;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.mq.Message;
import com.github.ipaas.ifw.mq.MqSendService;
import com.github.ipaas.ifw.util.JsonUtil;

/**
 * 类或接口作用描述
 * 
 * @author wudg
 */

public class InvokeInstallProcc {

	public static void main(String[] args) {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("deployFlowId", "o18qs32c9hfWMdnN8kQLgat4awJZDj6s");
		CtrlCmd ctrlCmd = new CtrlCmd();
		ctrlCmd.setCmd(CtrlCmdType.CONTI_IGNORE_ERR);

		ctrlCmd.setParams(params);

		MqSendService mqSendService = ServiceFactory.getService(CRSConstants.MQ_SEND_SERVICE);
		Message message = mqSendService.createMessage();
		message.setContent(JsonUtil.toJson(ctrlCmd));

		String qName = CmdTarget.CRS_MAN_CTRLER_QUEUE;

		mqSendService.sendQueue(qName, message);

		System.out.println("已发送指令");

	}

}

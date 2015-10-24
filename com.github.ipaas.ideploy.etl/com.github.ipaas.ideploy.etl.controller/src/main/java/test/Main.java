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

import com.github.ipaas.ideploy.controller.CommonDataUtil;
import com.github.ipaas.ideploy.controller.RInfo;
import com.github.ipaas.ifw.util.JsonUtil;

/** 
 * 测试类入口
 * @author  wudg
 */

public class Main {

	public static void main(String[] args) throws Exception {
		RInfo rInfo = CommonDataUtil.getRlInfo("o18q53r42j3qEKHJKAA9W8W7e3lGfBPI");
		
		System.out.println(JsonUtil.toJson(rInfo));
	}
}

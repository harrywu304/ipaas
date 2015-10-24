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

package com.github.ipaas.ideploy.controller.proc;

import java.util.concurrent.Callable;


/** 
 * 当出现更新过程某个或某些服务器出现错误时等待用户指令 用户可选择继续安装或回退
 * @author  wudg
 */

public class AwaitUserCmd4FailOnesProc  implements Callable<ProcResult>{
 
	@Override
	public ProcResult call() throws Exception {
		 
		return null;
	}

}

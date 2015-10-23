/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent;
  
import com.github.ipaas.ideploy.agent.handler.BackupCodeHandler;
import com.github.ipaas.ideploy.agent.handler.DownloadCodeHandler;
import com.github.ipaas.ideploy.agent.handler.RollbackCodeHandler;
import com.github.ipaas.ideploy.agent.handler.StartFileSysMonHandler;
import com.github.ipaas.ideploy.agent.handler.StartServHandler;
import com.github.ipaas.ideploy.agent.handler.StopFileSysMonHandler;
import com.github.ipaas.ideploy.agent.handler.StopServHandler;
import com.github.ipaas.ideploy.agent.handler.UpdateCodeHandler;
/** 
 * 事件响应类工厂
 * @author  wudg
 */

public class CrsCmdHandlerFactory {
	
	private CrsCmdHandlerFactory(){}
	
	public static CrsCmdHandlerWithFeed getCmdHandler(String cmd){ 
		
		//下载代码
		if(AgentCmdType.DOWMLOAD_NEWCODE.equals(cmd)){
			return new DownloadCodeHandler();
		}
		
		//启动服务器
		if(AgentCmdType.START_SERVER.equals(cmd)){
			return new StartServHandler();
		}
		
		//停止服务器
		if(AgentCmdType.STOP_SERVER.equals(cmd)){
			return new StopServHandler();
		}
		
		//更新代码
		if(AgentCmdType.UPDATE_CODE.equals(cmd)){
			return new UpdateCodeHandler();
		}
		
		//备份代码
		if(AgentCmdType.BACKUP_CODE.equals(cmd)){
			return new BackupCodeHandler();
		}
		
		//回滚代码
		if(AgentCmdType.ROLLBACK_CODE.equals(cmd)){
			return new RollbackCodeHandler();
		}
		
		//启动文件异动监控
		if(AgentCmdType.START_FILESYS_MON.equals(cmd)){
			return new StartFileSysMonHandler();
		}
		
		//停止文件异动监控
		if(AgentCmdType.STOP_FILESYS_MON.equals(cmd)){
			return new StopFileSysMonHandler();
		}
		
		return null;
	}

}

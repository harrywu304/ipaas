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

package com.github.ipaas.ideploy.controller.flow;

  
import static com.github.ipaas.ideploy.controller.proc.ProcMeta.*;

/** 
 * 流程定义 
 * @author  wudg
 */

public class FlowDefine {
	
	
	
	
	/**
	 * 标准安装过程-准备
	 * */
	public final static String[] STANDARD_RELEASE_PRE_FLOW={DOWMLOAD_NEWCODE};
	
	
	/**
	 * 首台预安装
	 */
	public final static String[] PRE_INSTALL_FIRST_ONE={DEL_FROM_LB,STOP_SERVER,BACKUP_CODE,UPDATE_CODE,START_SERVER,WAITFOR_PREV_FISRT};
	 
	
	/**
	 * 用户预览后首台继续执行的收尾流程
	 */
	public final static String[] CONTI_FIRST_ONE_FLOW={CONTI4FIRSTONE,ADD_TO_LB,FINISHED};
	
	/**
	 * 包含下载代码的完整安装过程  无预安装过程
	 */
	public final static String[] FULL_RELEASE_FLOW={DOWMLOAD_NEWCODE,DEL_FROM_LB,STOP_SERVER,BACKUP_CODE,UPDATE_CODE,START_SERVER,ADD_TO_LB,FINISHED};
	
	
	/**
	 * 主安装主流程(不含下载代码)
	 */
	public final static String[] STANDARD_RELEASE_FLOW={DEL_FROM_LB,STOP_SERVER,BACKUP_CODE,UPDATE_CODE,START_SERVER,ADD_TO_LB,FINISHED};
	
	 
	
	/**
	 * 重装(失败)主流程
	 */
	public final static String[] REINSTALL_FLOW={DOWMLOAD_NEWCODE,DEL_FROM_LB,STOP_SERVER,UPDATE_CODE,START_SERVER,ADD_TO_LB,FINISHED};
	 
	 
	  
	
	/**
	 * 回退流程
	 */
	public final static String[] ROLLBACK_FLOW={START_TO_ROLLBACK,DEL_FROM_LB,STOP_SERVER,ROLLBACK_CODE,START_SERVER,ADD_TO_LB,ROLLBACK_FINISH}; 
}

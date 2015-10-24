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

import com.github.ipaas.ideploy.controller.Result;
import com.github.ipaas.ideploy.controller.proc.Proc;
import com.github.ipaas.ideploy.controller.proc.ProcInput;
import com.github.ipaas.ideploy.controller.proc.ProcMeta;

/** 
 * 
 * @author  wudg
 */

public class FlowExecutor {
	
	private FlowExecutor(){}
	
	public static FlowExecutor newInstance(){
		return new FlowExecutor();
	}
	
	/**
	 * 获取指定名称的类实例
	 * @param clsName
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private static <T> T getInstance(String clsName) throws Exception{ 
		Class<?> cls=Class.forName(clsName); 
		return (T)cls.newInstance();
		
	}
	
	
	/**
	 * 以procInput 为起始输入参数 按 procs 定义的顺序执行响应的单元
	 * @param procInput
	 * @param procs
	 * @return 1成功结束;2被取消;-1无法执行
	 * @throws Exception
	 */
	public  int  execute(ProcInput procInput,String[] procs) throws Exception{
		Proc proc=null;
		ProcMeta m=null; 
		
		if(procs.length==0){
			return Result.FAIL;
		}
		for(String p:procs){
			m=ProcMeta.PROC_META_MAP.get(p);
			if(m==null){
				continue;
			}
			
			proc=getInstance(m.getCls()); 
			proc.setProcMeta(m);
			proc.setProcInput(procInput); 
			proc.call();
			
			if(!proc.isConti()){
				return Result.CANCELED;
			}
			 
		}
		return Result.SUCC;
		
	}

}

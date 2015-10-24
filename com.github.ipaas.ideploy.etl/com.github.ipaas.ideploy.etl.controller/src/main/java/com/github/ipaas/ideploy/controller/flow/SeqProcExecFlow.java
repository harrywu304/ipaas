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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.Result;
import com.github.ipaas.ideploy.controller.proc.ProcInput;

/** 
 * 该类用于按照指定的顺序执行一系列的单元 
 * @author  wudg
 */

public class SeqProcExecFlow  extends Flow{
	
	private static Logger logger = LoggerFactory.getLogger(SeqProcExecFlow.class);
	
	protected String[] procs;
	
	/**
	 * @param procs 每项对应一个可执行的proc单元  
	 */
	public SeqProcExecFlow(String[] procs){
		this.procs=procs;
	}
	 
	@Override
	public Result call() throws Exception {
		try{
			ProcInput procInput=new ProcInput();
			procInput.setFlowId(getFlowInput().getFlowId());
			procInput.setParams(getFlowInput().getParams());
			procInput.setExecTerminal(getAgent());  
			int result=FlowExecutor.newInstance().execute(procInput, procs); 
			return new Result(result);
		}catch(Exception e){
			logger.error(e.getMessage(),e);  
			return new Result(Result.FAIL,e.getMessage());
		} 
	}

}

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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.util.JsonUtil;



/** 
 * 类或接口作用描述 
 * @author  wudg
 */

public abstract class Proc  {
	
	private static Logger logger = LoggerFactory.getLogger(Proc.class);

	protected ProcInput  procInput;
	
	protected ProcMeta   procMeta;
	
	protected boolean conti=true;
	  

	public ProcInput getProcInput() {
		return procInput;
	}

	public void setProcInput(ProcInput procInput) {
		this.procInput = procInput;
	}

	public ProcMeta getProcMeta() {
		return procMeta;
	}

	public void setProcMeta(ProcMeta procMeta) {
		this.procMeta = procMeta;
	}
	  
	public boolean isConti() {
		return conti;
	}

	public void setConti(boolean conti) {
		this.conti = conti;
	}
	
	public boolean toExec(){
		return true;
	}
	
	public  final void call() throws Exception{
		
		logger.debug("Proc.call:  procMeta -- "+JsonUtil.toJson(procMeta)+"  procInput  --  "+JsonUtil.toJson(procInput));
		
		if(toExec()){
			execute();
		}
	}
	
	public  abstract void execute() throws Exception;
	
	
	 

}

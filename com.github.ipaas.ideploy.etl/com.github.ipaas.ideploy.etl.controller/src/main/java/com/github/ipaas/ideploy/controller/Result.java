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

/** 
 * 结果
 * @author  wudg
 */

public class Result {
	
	/**
	 * 成功
	 */
	public final static int SUCC=1;
	
	/**
	 * 被取消
	 */
	public final static int CANCELED=2;
	
	/**
	 * 失败
	 */
	public final  static int FAIL=-1;
	
	public Result(){}
	
	public Result(int resultCode){
		this.resultCode=resultCode;
	}
	
	public Result(int resultCode,String remark){
		this.resultCode=resultCode;
		this.remark=remark;
	} 
	
	
	/**
	 * 标志位
	 */
	protected int resultCode;
	
	/**
	 * 备注
	 */
	protected String remark;
	
	
	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	

}

/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */  
package com.github.ipaas.ifw.jdbc.impl;

import java.math.BigDecimal;

/**
 * 用于测试BigDecimal类型字段
 * @author wudie
 *
 */
public class DeciBean {
	
	private Integer id;
	
	private BigDecimal decim1;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public BigDecimal getDecim1() {
		return decim1;
	}

	public void setDecim1(BigDecimal decim1) {
		this.decim1 = decim1;
	}
	
	

}

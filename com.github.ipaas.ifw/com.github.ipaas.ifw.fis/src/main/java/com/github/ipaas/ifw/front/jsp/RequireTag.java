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

package com.github.ipaas.ifw.front.jsp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;

import com.github.ipaas.ifw.front.FisResource;

/**
 *
 * @author Chenql
 */
public class RequireTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8694856279353734532L;

	private String id;

	public int doStartTag() {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		FisResource resource = (FisResource) request.getAttribute(FisResource.CONTEXT_ATTR_NAME);
		try {
			resource.require(this.id);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SKIP_BODY;
	}

	public void setId(String id) {
		this.id = id;
	}

}

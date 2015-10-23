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
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.github.ipaas.ifw.front.FisResource;

/**
 *
 * @author Chenql
 */
public class ScriptTag extends BodyTagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2298651716053938808L;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() {
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		FisResource resource = (FisResource) request.getAttribute(FisResource.CONTEXT_ATTR_NAME);
		BodyContent body = this.getBodyContent();
		String code = body.getString();
		resource.addScript(code);
		return EVAL_PAGE;
	}
}

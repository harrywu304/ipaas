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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.github.ipaas.ifw.front.FisException;
import com.github.ipaas.ifw.front.FisResource;

/**
 *
 * @author Chenql
 */
public class HtmlTag extends BodyTagSupport {

	private FisResource resource;

	/**
	 * 
	 */
	private static final long serialVersionUID = -612383582047754887L;

	public int doStartTag() throws JspException {
		JspWriter out = pageContext.getOut();
		try {
			out.append("<html>");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		try {
			resource = new FisResource();
		} catch (FisException e) {
			throw new JspException(e);
		}
		request.setAttribute(FisResource.CONTEXT_ATTR_NAME, resource);
		return EVAL_BODY_BUFFERED;
	}

	public int doEndTag() {
		BodyContent body = this.getBodyContent();
		String html = body.getString() + "</html>";
		try {
			html = resource.replace(html);
		} catch (FisException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		JspWriter out = pageContext.getOut();
		try {
			out.write(html);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return EVAL_PAGE;
	}
}
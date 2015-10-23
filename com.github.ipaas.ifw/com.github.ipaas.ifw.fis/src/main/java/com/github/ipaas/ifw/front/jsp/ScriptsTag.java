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

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import com.github.ipaas.ifw.front.FisResource;

/**
 *
 * @author Chenql
 */
public class ScriptsTag extends TagSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8981028602817984686L;

	public int doStartTag() {
		JspWriter out = pageContext.getOut();
		try {
			out.append(FisResource.SCRIPT_PLACEHOLDER);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return SKIP_BODY;
	}
}

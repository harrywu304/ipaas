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
 */package com.github.ipaas.ifw.front.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.github.ipaas.ifw.front.FisException;
import com.github.ipaas.ifw.front.FisResource;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateScalarModel;

/**
 * 
 *
 * @author Chenql
 */
public class HtmlDirective implements TemplateDirectiveModel {

	private static final String FRAMEWORK_NAME = "framework";

	private FisResource fisRes;

	public HtmlDirective(FisResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		String framework = "";
		TemplateModel paramValue = (TemplateModel) params.get(FRAMEWORK_NAME);
		if (paramValue != null) {
			framework = ((TemplateScalarModel) paramValue).getAsString();
		}

		String tagBeginHtml = tagBegin(params);

		Writer out = env.getOut();
		if (body != null) {
			try {
				if (StringUtils.isNotBlank(framework)) {
					this.fisRes.setFramework(this.fisRes.getResourceUri(framework));
				}
				out.write(tagBeginHtml);

				StringWriter sOut = new StringWriter();
				body.render(sOut);

				out.write(this.fisRes.renderResponse(sOut.getBuffer().toString()));
			} catch (FisException e) {
				throw new TemplateException(e.getMessage(), e, env);
			}
			out.write("\n</html>");
		}

	}

	private String tagBegin(Map params) {
		String attrs = "";
		Set<String> keys = params.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			if (key.equals(FRAMEWORK_NAME)) {
				continue;
			}
			attrs += " " + key + "=\"" + params.get(key) + "\";";
		}
		return "<html " + attrs + ">\n";
	}

}

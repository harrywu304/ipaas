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

import com.github.ipaas.ifw.front.FisResource;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 *
 * @author Chenql
 */
public class HeadDirective implements TemplateDirectiveModel {
	private FisResource fisRes;

	public HeadDirective(FisResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		String tagBeginHtml = tagBegin(params);

		if (loopVars.length > 1) {
			// todo
			throw new TemplateModelException("At most one loop variable is allowed.");
		}

		// ---------------------------------------------------------------------
		// 真正开始处理输出内容
		Writer out = env.getOut();
		if (body != null) {
			out.write(tagBeginHtml);

			StringWriter sOut = new StringWriter();
			// 执行标签内容(same as <#nested> in FTL).
			body.render(sOut);

			out.write(sOut.getBuffer().toString());
			out.write(FisResource.getCssHook());
			out.write("\n</head>\n");
		}

	}

	private String tagBegin(Map params) {
		String attrs = "";
		Set<String> keys = params.keySet();
		for (Iterator<String> it = keys.iterator(); it.hasNext();) {
			String key = it.next();
			attrs += " " + key + "=\"" + params.get(key) + "\";";
		}
		return "<head " + attrs + ">\n";
	}
}

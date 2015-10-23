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
package com.github.ipaas.ifw.front.directive;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

import com.github.ipaas.ifw.front.FisException;
import com.github.ipaas.ifw.front.FisResource;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

/**
 *
 * @author Chenql
 */
public class WidgetDirective implements TemplateDirectiveModel {

	private static final String PARAM_NAME_NAME = "name";
	private static final String PARAM_NAME_CALL = "call";

	private FisResource fisRes;

	public WidgetDirective(FisResource fisRes) {
		super();
		this.fisRes = fisRes;
	}

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		// ---------------------------------------------------------------------
		// 处理参数

		String name = "";
		String call = "";

		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();

			String paramName = (String) ent.getKey();
			TemplateModel paramValue = (TemplateModel) ent.getValue();

			if (paramName.equals(PARAM_NAME_NAME)) {
				if (!(paramValue instanceof TemplateScalarModel)) {
					throw new TemplateModelException("The \"" + PARAM_NAME_NAME + "\" parameter " + "must be a string.");
				}
				name = ((TemplateScalarModel) paramValue).getAsString();
			} else if (paramName.equals(PARAM_NAME_CALL)) {
				if (!(paramValue instanceof TemplateScalarModel)) {
					throw new TemplateModelException("The \"" + PARAM_NAME_CALL + "\" parameter " + "must be a string.");
				}
				call = ((TemplateScalarModel) paramValue).getAsString();
			}
		}

		String ftl_uri;
		try {
			ftl_uri = fisRes.require(name);
		} catch (FisException e) {
			throw new TemplateException(e.getMessage(), e, env);
		}

		// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
		Template template = env.getConfiguration().getTemplate(ftl_uri, "UTF-8");
		env.include(template);

	}

}

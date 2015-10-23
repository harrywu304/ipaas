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
import java.io.Writer;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * @author Chenql
 */
public class OverrideDirective implements TemplateDirectiveModel {

	public final static String DIRECTIVE_NAME = "override";

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		String name = DirectiveUtils.getRequiredParam(params, "name");
		String overrideVariableName = DirectiveUtils.getOverrideVariableName(name);

		TemplateDirectiveBodyOverrideWraper override = DirectiveUtils.getOverrideBody(env, name);
		TemplateDirectiveBodyOverrideWraper current = new TemplateDirectiveBodyOverrideWraper(body, env);
		if (override == null) {
			env.setVariable(overrideVariableName, current);
		} else {
			DirectiveUtils.setTopBodyForParentBody(env, current, override);
		}
	}

	static class TemplateDirectiveBodyOverrideWraper implements TemplateDirectiveBody, TemplateModel {
		private TemplateDirectiveBody body;
		public TemplateDirectiveBodyOverrideWraper parentBody;
		public Environment env;

		public TemplateDirectiveBodyOverrideWraper(TemplateDirectiveBody body, Environment env) {
			super();
			this.body = body;
			this.env = env;
		}

		public void render(Writer out) throws TemplateException, IOException {
			if (body == null)
				return;
			TemplateDirectiveBodyOverrideWraper preOverridy = (TemplateDirectiveBodyOverrideWraper) env
					.getVariable(DirectiveUtils.OVERRIDE_CURRENT_NODE);
			try {
				env.setVariable(DirectiveUtils.OVERRIDE_CURRENT_NODE, this);
				body.render(out);
			} finally {
				env.setVariable(DirectiveUtils.OVERRIDE_CURRENT_NODE, preOverridy);
			}
		}
	}

}

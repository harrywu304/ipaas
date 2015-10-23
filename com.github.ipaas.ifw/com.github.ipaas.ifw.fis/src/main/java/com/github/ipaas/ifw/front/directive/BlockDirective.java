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
import java.util.Map;

import com.github.ipaas.ifw.front.directive.OverrideDirective.TemplateDirectiveBodyOverrideWraper;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * @author Chenql
 */
public class BlockDirective implements TemplateDirectiveModel {
	public final static String DIRECTIVE_NAME = "block";

	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		String name = DirectiveUtils.getRequiredParam(params, "name");
		TemplateDirectiveBodyOverrideWraper overrideBody = DirectiveUtils.getOverrideBody(env, name);
		if (overrideBody == null) {
			if (body != null) {
				body.render(env.getOut());
			}
		} else {
			DirectiveUtils.setTopBodyForParentBody(env, new TemplateDirectiveBodyOverrideWraper(body, env),
					overrideBody);
			overrideBody.render(env.getOut());
		}
	}

}

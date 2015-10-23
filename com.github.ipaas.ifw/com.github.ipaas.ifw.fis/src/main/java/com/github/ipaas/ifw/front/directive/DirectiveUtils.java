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

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.github.ipaas.ifw.front.directive.OverrideDirective.TemplateDirectiveBodyOverrideWraper;

import freemarker.core.Environment;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * @author Chenql
 */
public class DirectiveUtils {

	public static String BLOCK = "__ftl_override__";
	public static String OVERRIDE_CURRENT_NODE = "__ftl_override_current_node";

	public static String getOverrideVariableName(String name) {
		return BLOCK + name;
	}

	public static void exposeRapidMacros(Configuration conf) {
		conf.setSharedVariable(BlockDirective.DIRECTIVE_NAME, new BlockDirective());
		conf.setSharedVariable(ExtendsDirective.DIRECTIVE_NAME, new ExtendsDirective());
		conf.setSharedVariable(OverrideDirective.DIRECTIVE_NAME, new OverrideDirective());
		conf.setSharedVariable(SuperDirective.DIRECTIVE_NAME, new SuperDirective());
	}

	static String getRequiredParam(Map params, String key) throws TemplateException {
		Object value = params.get(key);
		if (value == null || StringUtils.isEmpty(value.toString())) {
			throw new TemplateModelException("not found required parameter:" + key + " for directive");
		}
		return value.toString();
	}

	static String getParam(Map params, String key, String defaultValue) throws TemplateException {
		Object value = params.get(key);
		return value == null ? defaultValue : value.toString();
	}

	static TemplateDirectiveBodyOverrideWraper getOverrideBody(Environment env, String name)
			throws TemplateModelException {
		TemplateDirectiveBodyOverrideWraper value = (TemplateDirectiveBodyOverrideWraper) env
				.getVariable(DirectiveUtils.getOverrideVariableName(name));
		return value;
	}

	static void setTopBodyForParentBody(Environment env, TemplateDirectiveBodyOverrideWraper topBody,
			TemplateDirectiveBodyOverrideWraper overrideBody) {
		TemplateDirectiveBodyOverrideWraper parent = overrideBody;
		while (parent.parentBody != null) {
			parent = parent.parentBody;
		}
		parent.parentBody = topBody;
	}
}

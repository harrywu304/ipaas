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

package com.github.ipaas.ifw.front.util;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import javax.servlet.ServletContext;

import com.github.ipaas.ifw.front.FisResource;
import com.github.ipaas.ifw.front.directive.BlockDirective;
import com.github.ipaas.ifw.front.directive.BodyDirective;
import com.github.ipaas.ifw.front.directive.ExtendsDirective;
import com.github.ipaas.ifw.front.directive.HeadDirective;
import com.github.ipaas.ifw.front.directive.HtmlDirective;
import com.github.ipaas.ifw.front.directive.OverrideDirective;
import com.github.ipaas.ifw.front.directive.RequireDirective;
import com.github.ipaas.ifw.front.directive.ScriptDirective;
import com.github.ipaas.ifw.front.directive.WidgetDirective;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * 
 * Freemarker模板工具类
 * 
 * @author wuxh
 * @date Nov 29, 2013 9:44:19 AM
 */
public class FtlUtil {
	/**
	 * 获取模板
	 * 
	 * @param templatePath
	 *            模板文件存放目录
	 * @param templateName
	 *            模板文件名称
	 * @param templateEncoding
	 *            模板文件的编码方式
	 * @throws IOException
	 */
	public static Template getTemplate(String templatePath, String templateName, String templateEncoding)
			throws IOException {

		Configuration config = new Configuration();
		File file = new File(templatePath);
		// 设置要解析的模板所在的目录，并加载模板文件
		config.setDirectoryForTemplateLoading(file);
		// 设置包装器，并将对象包装为数据模型
		config.setObjectWrapper(new DefaultObjectWrapper());

		// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
		Template template = config.getTemplate(templateName, templateEncoding);

		return template;
	}

	/**
	 * 获取模板
	 * 
	 * @param context
	 *            ServletContext
	 * @param templatePath
	 *            模板文件存放目录
	 * @param templateName
	 *            模板文件名称
	 * @param templateEncoding
	 *            模板文件的编码方式
	 * @throws IOException
	 */
	public static Template getTemplate(ServletContext context, String templatePath, String templateName,
			String templateEncoding) throws IOException {

		Configuration config = new Configuration();
		File file = new File(templatePath);
		// 设置要解析的模板所在的目录，并加载模板文件
		config.setDirectoryForTemplateLoading(file);
		// 设置包装器，并将对象包装为数据模型
		config.setObjectWrapper(new DefaultObjectWrapper());

		// 获取模板,并设置编码方式，这个编码必须要与页面中的编码格式一致
		Template template = config.getTemplate(templateName, templateEncoding);

		return template;
	}

	/**
	 * 解析模板，合并数据，并输出
	 * 
	 * @param template
	 *            模板对象
	 * @param root
	 *            数据模型根对象
	 * @param out
	 *            输出流
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static void processTemplate(Template template, Map<String, Object> root, Writer out)
			throws TemplateException, IOException {
		template.process(root, out);
	}

	/**
	 * fis的freemarker模板解析方法
	 * 
	 * @param template
	 *            模板对象
	 * @param root
	 *            数据模型根对象
	 * @param out
	 *            输出流
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static void processTemplate(Template template, Map<String, Object> root, Writer out, FisResource fisRes)
			throws TemplateException, IOException {
		Configuration cfg = template.getConfiguration();

		// 添加自定义标签
		cfg.setSharedVariable("block", new BlockDirective());
		cfg.setSharedVariable("extends", new ExtendsDirective());
		cfg.setSharedVariable("override", new OverrideDirective());

		// 状态相关的需要放到root里，多线程执行环境下不会出错。
		root.put("html", new HtmlDirective(fisRes));
		root.put("head", new HeadDirective(fisRes));
		root.put("body", new BodyDirective(fisRes));
		root.put("require", new RequireDirective(fisRes));
		root.put("script", new ScriptDirective(fisRes));
		root.put("widget", new WidgetDirective(fisRes));

		processTemplate(template, root, out);
	}

	/**
	 * 解析模板，合并数据，并输出
	 * 
	 * @param templatePath
	 *            模板文件存放目录
	 * @param templateName
	 *            模板文件名称
	 * @param root
	 *            数据模型根对象
	 * @param templateEncoding
	 *            模板文件的编码方式
	 * @param out
	 *            输出流
	 * @throws IOException
	 * @throws TemplateException
	 */
	public static void processTemplate(String templatePath, String templateName, String templateEncoding,
			Map<String, Object> root, Writer out) throws IOException, TemplateException {
		Template template = getTemplate(templatePath, templateName, templateEncoding);
		processTemplate(template, root, out);
	}
}
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

package com.github.ipaas.ifw.front.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.github.ipaas.ifw.front.FisException;
import com.github.ipaas.ifw.front.FisResource;
import com.github.ipaas.ifw.front.rewrite.FisRewrite;
import com.github.ipaas.ifw.front.rewrite.FisRewriteRule;
import com.github.ipaas.ifw.front.util.FisModelSimulator;
import com.github.ipaas.ifw.front.util.FtlUtil;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 *
 * @author Chenql
 */
public class FisFreemarkerServlet extends HttpServlet {
	private Configuration cfg;
	private FisRewrite fr;

	public FisFreemarkerServlet() {
		super();
	}

	public void init() {
		// 初始化FreeMarker配置
		// 创建一个Configuration实例
		cfg = new Configuration();
		// 设置FreeMarker的模版文件位置
		cfg.setServletContextForTemplateLoading(getServletContext(), "template");
		// 设置包装器，并将对象包装为数据模型
		cfg.setObjectWrapper(new DefaultObjectWrapper());
		try {
			fr = FisRewrite.getInstance();
		} catch (FisException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String requestUri = request.getRequestURI();
		FisRewriteRule frRule = fr.findRule(requestUri);
		if (frRule == null) {
			response.sendError(404);
		}
		try {
			// 取得模版文件
			Template t = cfg.getTemplate(frRule.getTemplateFile(), "utf-8");

			// 建立数据模型
			Map<String, Object> root = new HashMap<String, Object>();
			List<String> dataFiles = frRule.getDataFiles();
			for (int i = 0, len = dataFiles.size(); i < len; i++) {
				// 放入对应数据key value
				Map<String, Object> data = FisModelSimulator.getJSON(dataFiles.get(i));
				root.putAll(data);
			}

			// 开始准备生成输出
			// 使用模版文件的charset作为本页面的charset
			// 使用text/html MIME-type
			System.out.println("contentType:" + frRule.getContentType());
			response.setContentType(frRule.getContentType() + "; charset=" + t.getEncoding());
			PrintWriter out = response.getWriter();

			FisResource fisRes = new FisResource();
			// 合并数据模型和模版，并将结果输出到out中
			FtlUtil.processTemplate(t, root, out, fisRes);

		} catch (TemplateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}

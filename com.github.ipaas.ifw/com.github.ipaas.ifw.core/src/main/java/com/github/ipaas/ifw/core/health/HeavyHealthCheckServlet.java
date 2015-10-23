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

package com.github.ipaas.ifw.core.health;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.util.BeanUtil;
import com.github.ipaas.ifw.util.StringUtil;

/**
 * Web可用性的重量级检测
 * 
 * @author Chenql
 */
@SuppressWarnings("serial")
public class HeavyHealthCheckServlet extends HttpServlet {
	private static final Logger logger = LoggerFactory.getLogger(HeavyHealthCheckServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		BizCheck bizCheck = null;
		String checkState = null;
		String className = null;
		try {
			// Map<String, Object> sp =
			// BeanUtil.wrapToMap(config.getItem("app_properties"));
			// logger.debug("sp = " + sp.toString());
			// if(null == sp || sp.size() == 0) {
			className = "com.github.ipaas.ifw.core.health.GenericBizCheckImpl";
			logger.debug("className = " + className);
			bizCheck = (BizCheck) BeanUtil.createBean(className);
			checkState = bizCheck.checkBiz();
		} catch (Throwable e) {
			checkState = StringUtil.getExceptionAsStr(e);
			logger.error("health-biz-check error", e);
		}
		out.println(checkState);
		out.flush();
		out.close();
	}

}

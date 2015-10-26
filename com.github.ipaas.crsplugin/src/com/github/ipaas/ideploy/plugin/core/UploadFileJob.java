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
package com.github.ipaas.ideploy.plugin.core;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.widgets.Display;

import com.github.ipaas.ideploy.plugin.bean.PathInfo;
import com.github.ipaas.ideploy.plugin.bean.ProjectInfo;
import com.github.ipaas.ideploy.plugin.bean.ServGroup;
import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.ui.popup.ConfirmCreateVersionDialog;
import com.github.ipaas.ideploy.plugin.ui.preference.CrsPreferencePage;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.JsonUtil;
import com.github.ipaas.ideploy.plugin.util.MavenUtil;

/**
 * 代码包上传操作
 * 
 * @author Chenql 
 */
public class UploadFileJob extends Job {
	private PathInfo pathInfo;
	private ProjectInfo projectInfo;
	private String sourceLocation;

	/**
	 * 
	 * @param name
	 *            job名字
	 * @param pathInfo
	 *            路径信息
	 * @param projectInfo
	 *            项目信息
	 * @param sourceLocation
	 *            源代码目录 为空直接上传代码,不编译
	 */
	public UploadFileJob(String name, PathInfo pathInfo, ProjectInfo projectInfo, String sourceLocation) {
		super(name);
		this.pathInfo = pathInfo;
		this.projectInfo = projectInfo;
		this.sourceLocation = sourceLocation;
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.jobs.Job#run(org.eclipse.core.runtime.
	 * IProgressMonitor)
	 */
	@Override
	protected IStatus run(IProgressMonitor arg0) {
		if (this.sourceLocation != null && !this.sourceLocation.equals("")) {
			try {
				MavenUtil.runInstall(sourceLocation);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ConsoleHandler.error("编译失败!");
				return Status.OK_STATUS;
			}
		}

		UserInfo userInfo = CrsPreferencePage.getUserInfo();
		String filePath = Comparetor.doAtion(pathInfo, userInfo);
		if (filePath != null && !filePath.trim().equals("")) {
			ConsoleHandler.info(filePath);
			try {
				ServGroup servGroup = projectInfo.getServGroup(pathInfo.getGroupId());
				if (servGroup == null) {
					ConsoleHandler.error("没有找到服务组信息。");
					return Status.OK_STATUS;
				}
				String uploadResult = RequestAcion.uploadFile(userInfo.getUrl() + "/crs_code/upload", userInfo,
						filePath, projectInfo.getServGroup(pathInfo.getGroupId()).getAppRowId(), true);
				this.handlerResult(uploadResult, filePath, userInfo);
			} catch (Exception e) {
				e.printStackTrace();
				ConsoleHandler.error("上传代码失败:" + e.getMessage());
			} finally {
				FileUtils.delete(new File(filePath));
			}
		}
		return Status.OK_STATUS;
	}

	public void handlerResult(String uploadResult, String filePath, UserInfo userInfo) {
		Map<String, Object> map = JsonUtil.toBean(uploadResult, Map.class);
		String optSta = (String) map.get("optSta");
		if (optSta.equals("success")) {
			Map<String, Object> responseData = (Map<String, Object>) map.get("responseData");
			String uuIdPath = (String) responseData.get("uuIdPath");
			String deployDetail = (String) responseData.get("deployDetail");
			final String detial = this.showUpdetial(deployDetail);
			final Map<String, String> param = new HashMap<String, String>();

			String fileName = StringUtils.removeSuffix(filePath.substring(filePath.lastIndexOf("/") + 1), ".zip");// 上传文件名
			param.put("sgId", String.valueOf(projectInfo.getServGroup(pathInfo.getGroupId()).getSgId()));// 服务组主键
			param.put("deployDetail", deployDetail);// 更新细节
			param.put("uuIdPath", uuIdPath);// 上传文件路径uuid
			param.put("fileName", fileName);// 文件名
			param.put("id", pathInfo.getGroupId());// 服务组Id
			param.put("creator", String.valueOf(projectInfo.getUserId()));
			param.put("appRowId", String.valueOf(projectInfo.getServGroup(pathInfo.getGroupId()).getAppRowId()));
			param.put("userName", userInfo.getEmail());
			param.put("password", userInfo.getPassword());
			Display.getDefault().asyncExec(new Runnable() {
				@Override
				public void run() {
					new ConfirmCreateVersionDialog(null, param, detial).open();
				}
			});

		} else {
			ConsoleHandler.error((String) map.get("optTxt"));
		}
	}

	private String showUpdetial(String deployDetail) {
		StringBuilder strBuiler = new StringBuilder("更新内容:");
		ConsoleHandler.info("更新内容:");
		if (deployDetail.equals("all")) {
			ConsoleHandler.info("	all");
			return "all";
		} else {
			List<String> list = Arrays.asList(deployDetail.split("<p>"));
			for (String str : list) {
				String newStr = " " + StringUtils.removeSuffix(str, "</p>");
				ConsoleHandler.info(newStr);
				strBuiler.append(newStr).append("\n");
			}
		}
		return strBuiler.toString();
	}
}

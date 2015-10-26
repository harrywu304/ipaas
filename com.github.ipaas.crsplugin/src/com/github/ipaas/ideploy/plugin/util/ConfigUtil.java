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

package com.github.ipaas.ideploy.plugin.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.jface.preference.IPreferenceStore;

import com.github.ipaas.ideploy.plugin.Activator;
import com.github.ipaas.ideploy.plugin.bean.PathInfo;
import com.github.ipaas.ideploy.plugin.bean.ProjectInfo;
import com.github.ipaas.ideploy.plugin.bean.ServGroup;
import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.core.RequestAcion;
import com.github.ipaas.ideploy.plugin.ui.preference.CrsPreferencePage;

public class ConfigUtil {
	private static final String PATHINFO_PREFIX = "pathinfo_";

	private static final String OUTUT_PATH = "outputPath";
	private static IPreferenceStore store = Activator.getDefault().getPreferenceStore();

	public static PathInfo getPathInfo(String name) {

		String json = store.getString(PATHINFO_PREFIX + name);
		PathInfo pathinfo = null;
		if (json != null && !json.equals("")) {
			try {
				System.out.println(json);
				pathinfo = JsonUtil.toBean(json, PathInfo.class);
			} catch (Exception e) {
				ConsoleHandler.error("获取配置信息失败:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return pathinfo;
	}

	public static void cleanPathInfo(String name) {
		store.setValue(PATHINFO_PREFIX + name, null);
	}

	public static ProjectInfo getProjectInfo() throws Exception {

		UserInfo userInfo = CrsPreferencePage.getUserInfo();
		ProjectInfo projectInfo = new ProjectInfo();
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		if (userInfo.getPassword() == null || userInfo.getPassword().equals("")) {
			throw new Exception(" AMM帐号密码为空!");
		}
		if (userInfo.getEmail() == null || userInfo.getEmail().equals("")) {
			throw new Exception(" AMM帐号密码为空!");
		}
		params.add(new BasicNameValuePair("userName", userInfo.getEmail()));
		params.add(new BasicNameValuePair("password", userInfo.getPassword()));
		String json = RequestAcion.post(userInfo.getUrl() + "/crs_code/project_list", params, true);
		System.out.println(json);
		if (json != null && !json.equals("")) {
			Map<String, String> result = JsonUtil.toBean(json, Map.class);
			if (result.get("status") != null && result.get("status").equals("success")) {
				try {
					List<String> list = null;
					if (result.containsKey("list")) {
						list = JsonUtil.toBean(result.get("list"), List.class);
					}
					projectInfo.setProjectList(list);
					if (result.containsKey("groupInfoList")) {
						List<Map<String, Object>> groupInfoList = JsonUtil.toBean(result.get("groupInfoList"),
								List.class);
						List<ServGroup> servGroupList = new ArrayList<ServGroup>();
						for (Map<String, Object> groupInfoMap : groupInfoList) {
							ServGroup group = new ServGroup();
							group.setAppRowId((Integer) groupInfoMap.get("appRowId"));
							group.setId((String) groupInfoMap.get("id"));
							group.setSgId((Integer) groupInfoMap.get("sgId"));
							group.setActName((String) groupInfoMap.get("actName"));
							servGroupList.add(group);
						}
						projectInfo.setServGroupList(servGroupList);
					}

					projectInfo.setUserId(Integer.valueOf(result.get("userId")));
					if (projectInfo.getProjectList().size() < 1) {
						ConsoleHandler.error("未找到服务组信息!");
					}
				} catch (Exception e) {
					ConsoleHandler.error("获取服务组失败:" + result.get("list"));
				}
			} else {
				ConsoleHandler.error("获取服务组失败:" + result.get("info"));
			}
		}
		return projectInfo;
	}

	public static void savePathInfo(String name, PathInfo pathInfo) {

		if (pathInfo == null) {
			store.setValue(PATHINFO_PREFIX + name, "");
		} else {
			store.setValue(PATHINFO_PREFIX + name, JsonUtil.toJson(pathInfo));
		}
	}

	public static String getSavePath() {
		return store.getString(OUTUT_PATH);
	}

	public static void saveOutputPath(String outputPath) {
		store.setValue(OUTUT_PATH, outputPath);
	}
}

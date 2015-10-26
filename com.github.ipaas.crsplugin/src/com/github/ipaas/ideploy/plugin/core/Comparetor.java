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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import com.github.ipaas.ideploy.plugin.bean.PathInfo;
import com.github.ipaas.ideploy.plugin.bean.TreeNode;
import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.util.CharUtil;
import com.github.ipaas.ideploy.plugin.util.CompressedFileUtil;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.FileUtil;
import com.github.ipaas.ideploy.plugin.util.JsonUtil;

/**
 * 
 * 文件对比执行类
 * 
 * @author Chenql  
 */
public class Comparetor {

	/**
	 * 返回 生成的代码包路径,返回空则没有生成
	 * 
	 * @param pathInfo
	 * @param userInfo
	 * @return
	 */
	public static String doAtion(PathInfo pathInfo, UserInfo userInfo) {

		if (userInfo.getUrl() == null || userInfo.getUrl().equals("")) {
			ConsoleHandler.error("没有配置服务器");
			return null;
		} else if (userInfo.getEmail() == null || userInfo.getEmail().equals("")) {
			ConsoleHandler.error("没有配置服务器帐号");
			return null;
		} else if (userInfo.getPassword() == null || userInfo.getPassword().equals("")) {
			ConsoleHandler.error("没有配置服务器密码");
			return null;
		}
		ConsoleHandler.info("用户:" + userInfo.getEmail());
		ConsoleHandler.info("目标服务组:" + pathInfo.getGroupId());

		TreeNode srcRoot = getSrcTree(pathInfo);
		TreeNode targetRoot = getTargetTree(userInfo, pathInfo);
		if (srcRoot != null && targetRoot != null) {
			List<String> result = null;
			if (targetRoot.getChildSet() == null || targetRoot.getChildSet().size() == 0) {
				result = new ArrayList<String>();
				result.add("all");
			} else {
				result = FileTreeNodeComparetor.compare(srcRoot, targetRoot);
			}
			Collections.sort(result, new Comparator<String>() {
				public int compare(String str1, String str2) {
					return str1.compareTo(str2);
				}
			});

			if (result != null && result.size() > 0) {
				ConfigFileFilter filter = new ConfigFileFilter(userInfo.getPatternJsonList());
				result = filter.filterResult(result);
			}

			if (result != null && result.size() > 0) {
				String path = genZipPackage(result, pathInfo);
				ConsoleHandler.info("代码包  " + path + " 生成完毕!");
				return path;
			} else {
				ConsoleHandler.info("未发现有更新的文件!");
			}
		} else {
			ConsoleHandler.info("获取代码信息失败!");
		}
		return null;
	}

	/**
	 * 根据对比结果生成差异文件包
	 * 
	 * @param result
	 *            文件对 比结果
	 * @param pathInfo
	 * @return 生成包路径
	 */
	private static String genZipPackage(List<String> result, PathInfo pathInfo) {
		ConsoleHandler.info("正在生成代码包...");
		try {
			String timeStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			// 源代码路径
			String srcPath = pathInfo.getSrcPath().replaceAll("\\\\", "/");
			// 保存代码包路径
			String savePath = pathInfo.getSavePath().replaceAll("\\\\", "/");
			String tempRootFolder = savePath + "/" + pathInfo.getGroupId() + timeStr;
			String tempCodeFolder = tempRootFolder + "/code";
			FileUtil.newFolder(tempRootFolder);

			StringBuilder updateBuilder = new StringBuilder();
			if (result.size() == 1 && result.get(0).equals("all")) {// 全量更新
				updateBuilder.append("all");
				FileUtils.copyDirectory(new File(srcPath), new File(tempCodeFolder));// 文件夹拷贝
				ChineseFileNameFilter.deleteChineseNameFile(savePath, new File(tempRootFolder));// 删除中文名文件
			} else {// 增量更新
				for (String filePath : result) {
					if (filePath == null || filePath.trim().length() <= 1) {
						continue;
					}
					if (CharUtil.isChinese(filePath)) {
						ConsoleHandler.error(" 忽略中文名文件: "+ filePath);
						continue;// /忽略中文文件
					}
					updateBuilder.append(filePath.replaceAll("\\\\", "/")).append("\r\n");
					if (filePath.startsWith("-")) {
						continue;
					}
					String fileName = filePath.substring(filePath.indexOf("/"), filePath.length());
					File srcFile = new File(new StringBuilder().append(srcPath).append(fileName).toString());
					if (srcFile.exists() && srcFile.isDirectory()) {// 如果是文件夹
						FileUtil.newFolder(tempCodeFolder + fileName);
					} else if (srcFile.exists()) {
						String folderName = fileName.substring(0, fileName.lastIndexOf("/") + 1);
						if (folderName != null && folderName.length() > 0) {
							FileUtil.newFolder(tempCodeFolder + folderName);
						}
						File destFile = FileUtil.createFile(tempCodeFolder + fileName);
						FileUtils.copyFile(srcFile, destFile);
					}
				}
			}

			if (pathInfo.getSourceCodeSvnUrl() != null && !pathInfo.getSourceCodeSvnUrl().equals("")
					&& pathInfo.getSourceCodeSvnRevision() != null) {// 记录SVN信息
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("sourceCodeSvnRevision", pathInfo.getSourceCodeSvnRevision());
				map.put("sourceCodeSvnUrl", pathInfo.getSourceCodeSvnUrl());
				FileUtil.createTxtFile(tempRootFolder + "/svnInfo.txt", JsonUtil.toJson(map));
			}
			FileUtil.createTxtFile(tempRootFolder + "/update.txt", updateBuilder.toString());
			CompressedFileUtil.compressedFile(tempRootFolder, savePath);
			FileUtil.deleteDirectory(tempRootFolder);// 删掉临时文件
			return tempRootFolder + ".zip";
		} catch (Exception e) {
			e.printStackTrace();
			ConsoleHandler.error("生成文件出错:" + e.getMessage());
		}
		return "";
	}

	public static void main(String[] args) throws IOException {
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
//		UserInfo userInfo   = new UserInfo();
//		userInfo.setEmail("chenql");
//		userInfo.setPassword("");
//		userInfo.setUrl("");
//		params.add(new BasicNameValuePair("userName", userInfo.getEmail()));
//		params.add(new BasicNameValuePair("password", userInfo.getPassword()));
//		params.add(new BasicNameValuePair("id", "uphone"));
//		String json = RequestAcion.post(userInfo.getUrl() + "/crs_code/code_info", params, true);
//		System.out.println(json);
//		
		List<String> list = FileUtils.readLines(new File("D://update.txt"));
		for(String str:list){
			if(CharUtil.isChinese(str)){
				System.out.println(str);
			}
		}
	}
	@SuppressWarnings("unchecked")
	private static TreeNode getTargetTree(UserInfo userInfo, PathInfo pathInfo) {
		ConsoleHandler.info("正在读取服务器代码信息...");

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("userName", userInfo.getEmail()));
		params.add(new BasicNameValuePair("password", userInfo.getPassword()));
		params.add(new BasicNameValuePair("id", pathInfo.getGroupId()));
		String json = RequestAcion.post(userInfo.getUrl() + "/crs_code/code_info", params, true);
		System.out.println(json);
		try {
			if (json != null && !json.equals("")) {
				Map<String, String> result = JsonUtil.toBean(json, Map.class);
				if (result.get("status") != null && result.get("status").equals("success")) {

					if (result.containsKey("treeNode")) {
						String treeNodStr = result.get("treeNode");
						TreeNode targetNode = JsonUtil.toBean(treeNodStr, TreeNode.class);
						return targetNode;
					} else {
						return new TreeNode();
					}

				} else {
					ConsoleHandler.error("获取代码信息失败:" + result.get("info"));
				}
			}
		} catch (Exception e) {
			ConsoleHandler.error("获取代码信息失败:" + json);
		}
		return null;
	}

	private static TreeNode getSrcTree(PathInfo pathInfo) {
		String targetPath = pathInfo.getSrcPath();
		File file = new File(targetPath);
		if (!file.exists()) {
			ConsoleHandler.error(" File No Exist:" + targetPath);
			return null;
		}
		TreeNode root = FileTreeNodeGenerator.buildNode(targetPath);
		return root;
	}
}

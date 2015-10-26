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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.tools.ant.util.StringUtils;

import com.github.ipaas.ideploy.plugin.util.CharUtil;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;

/**
 * 配置文加过滤器
 * 
 * @author Chenql  
 */
public class ChineseFileNameFilter {

	public List<String> filterResult(List<String> compareResult) {
		List<String> result = new ArrayList<String>();
		for (String strName : compareResult) {
			if (CharUtil.isChinese(strName)) {
				ConsoleHandler.error("存在中文名文件:" + strName.substring(2) + "  已忽略！");
			} else {
				result.add(strName);
			}
		}
		return result;
	}

	public static void deleteChineseNameFile(String prePath, File rootFile) {
		if (CharUtil.isChinese(rootFile.getName())) {
			try {
				if (rootFile.isDirectory()) {
					ConsoleHandler.error("存在中文名文件:"
							+ StringUtils.removePrefix(rootFile.getAbsolutePath().replaceAll("\\\\", "/"), prePath)
							+ "  已忽略！");
					FileUtils.deleteDirectory(rootFile);
				} else {
					ConsoleHandler.error("存在中文名文件:"
							+ StringUtils.removePrefix(rootFile.getAbsolutePath().replaceAll("\\\\", "/"), prePath)
							+ "  已忽略！");
					FileUtils.forceDelete(rootFile);
				}
			} catch (IOException e) {
				ConsoleHandler.error("删除临时文件失败:" + e.getMessage());
			}
		}
		if (rootFile.isDirectory()) {
			File[] files = rootFile.listFiles();
			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i].getAbsolutePath());
				deleteChineseNameFile(prePath, files[i]); // 递归处理
			}
		}
	}
}

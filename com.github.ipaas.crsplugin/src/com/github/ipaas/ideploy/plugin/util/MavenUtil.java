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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 类或接口作用描述
 * 
 * @author Chenql 
 */
public class MavenUtil {

	public static void runInstall(String path) throws Exception {
		if (path == null || path.trim().equals("")) {
			throw new Exception("代码编译错误,没有路径信息  " + path);
		}
		String driver = path.trim().substring(0, 2);

		System.out.println("driver: " + driver);
		System.out.println("path: " + path);
		String shell = "cmd.exe /c " + driver + " &&  cd " + path + " && mvn clean install -Dmaven.test.skip=true";
		System.out.println(" shell: " + shell);
		ConsoleHandler.info("执行编译...");
		try {
			Process process = Runtime.getRuntime().exec(shell);
			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = br.readLine()) != null) {
				System.out.println(new String(line.getBytes()));
				ConsoleHandler.info(new String(line.getBytes()));
			}
		} catch (IOException e) {
			e.printStackTrace();
			throw new Exception("代码编译失败");
		}
	}
}

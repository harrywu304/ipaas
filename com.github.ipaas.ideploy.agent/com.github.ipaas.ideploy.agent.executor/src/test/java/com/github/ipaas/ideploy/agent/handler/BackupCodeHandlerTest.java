/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */
package com.github.ipaas.ideploy.agent.handler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ideploy.agent.handler.BackupCodeHandler;

/**
 * 类或接口作用描述
 * 
 * @author Chenql
 */
public class BackupCodeHandlerTest {

	private String SRC_PATH = "/www/appbk_src/testbk";
	private String TARGET_PATH = "/www/appbk/testbk_target";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		File srcFile = new File(SRC_PATH);
		if (srcFile.exists()) {
			FileUtils.cleanDirectory(srcFile);
		} else {
			srcFile.mkdirs();
		}
		File targetFile = new File(TARGET_PATH);
		if (targetFile.exists()) {
			FileUtils.cleanDirectory(targetFile);
		} else {
			targetFile.mkdirs();
		}

		for (int i = 0; i < 10; i++) {
			FileUtils.writeStringToFile(new File(SRC_PATH + "/" + i), String.valueOf(i));
		}
		for (int i = 10; i < 20; i++) {
			new File(SRC_PATH + "/" + i).mkdir();
		}
	}

	@Test
	public void executeTest() throws Exception {
		BackupCodeHandler handler = new BackupCodeHandler();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("srcPath", SRC_PATH);
		params.put("targetPath", TARGET_PATH);
		// 执行配分
		handler.execute(null, null, params, null);

		for (int i = 0; i < 20; i++) {
			Assert.assertTrue(new File(TARGET_PATH + "/" + i).exists());
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		File srcFile = new File(SRC_PATH);
		if (srcFile.exists()) {
			FileUtils.cleanDirectory(srcFile);
		}
		File targetFile = new File(TARGET_PATH);
		if (targetFile.exists()) {
			FileUtils.cleanDirectory(targetFile);
		}
	}

}

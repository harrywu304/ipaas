/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */
package com.github.ipaas.ideploy.agent.handler;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.ipaas.ideploy.agent.CrsWebSvnUtil;
import com.github.ipaas.ifw.util.FileUtil;
import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.github.ipaas.ideploy.agent.handler.DownloadCodeHandler;

/**
 * 下载代码测试类
 * 
 * @author Chenql
 */
public class DownloadCodeHandlerTest {


	private static String contextPath = FileUtil.getFile("com.github.ipaas.ideploy.agent.executor/target/test-classes/").getAbsolutePath();

	private CrsWebSvnUtil util;

	private String testDataRoot = "/testData/";

	public static final String TEST_GROUP = "/unit_test_dont_delete";
	public static final String TAGS = "/tags";
	public static final String USING_VERSION = "test_20140710162244";
	public static final String DOING_VERSION = "test_20140722140850";


	@Before
	public void setUp() throws Exception {

		util = CrsWebSvnUtil.newInstance();
		util.importDir(contextPath + testDataRoot + TEST_GROUP, TEST_GROUP);

	}



	@After
	public void clean() throws Exception {
		util.delete(TEST_GROUP);
	}

	/**
	 * 先全量下载代码,然后增量下载代码,两次对比
	 * 
	 * @throws Exception
	 */
	@Test
	public void downloadCodeTest() throws Exception {
		String usingRoot = "/www/apptemp/" + USING_VERSION;
		String doingRoot = "/www/apptemp/" + DOING_VERSION;
		DownloadCodeHandler downLoadHandler = new DownloadCodeHandler();
		File usingfile = new File(usingRoot);
		if (usingfile.exists()) {
			FileUtils.forceDelete(usingfile);
		}
		File doingFile = new File(doingRoot);
		if (doingFile.exists()) {
			FileUtils.forceDelete(doingFile);
		}

		String flowId = "flowIdNotNeed";
		String cmd = "cmdNotNeed";
		Integer hostStatus4New = 1;
		Integer updateAll = 1;
		Map<String, Object> firstDownLoadParams = new HashMap<String, Object>();
		firstDownLoadParams.put("doingCodeVerPath", TEST_GROUP + TAGS + "/" + USING_VERSION);
		firstDownLoadParams.put("hostStatus", hostStatus4New);
		firstDownLoadParams.put("savePath", usingRoot);// 本地代码保存路径
		firstDownLoadParams.put("updateAll", updateAll);
		downLoadHandler.execute(flowId, cmd, firstDownLoadParams, null);

		Assert.assertTrue(new File(usingRoot + "/update.txt").exists());


		Integer notUpdateAll = 2;
		Integer hostStatus4Old = 2;
		Map<String, Object> secondDownLoadParams = new HashMap<String, Object>();
		secondDownLoadParams.put("doingCodeVerPath", TEST_GROUP + TAGS + "/" + DOING_VERSION);
		secondDownLoadParams.put("usingCodeVerPath", TEST_GROUP + TAGS + "/" + USING_VERSION);
		secondDownLoadParams.put("hostStatus", hostStatus4Old);
		secondDownLoadParams.put("savePath", doingRoot);// 本地代码保存路径
		secondDownLoadParams.put("updateAll", notUpdateAll);
		downLoadHandler.execute(flowId, cmd, secondDownLoadParams, null);
		File updateFile = new File(doingRoot + "/update.txt");

		Assert.assertTrue(updateFile.exists());

		// 判断文件是否正确下载
		//String do
		List<String> updateList = FileUtils.readLines(updateFile);
		for (String str : updateList) {
			if (str.startsWith("+")) {
				Assert.assertTrue(new File(doingRoot + "/code" + StringUtils.removeStart(str, "+").trim()).exists());
			} else if (str.startsWith("-")) {
				String f = usingRoot + "/code" + StringUtils.removeStart(str, "-").trim();
				Assert.assertTrue(new File(f).exists());
			}
		}

		if (usingfile.exists()) {
			FileUtils.forceDelete(usingfile);
		}
		if (doingFile.exists()) {
			FileUtils.forceDelete(doingFile);
		}
	}
}

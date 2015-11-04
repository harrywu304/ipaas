/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */
package com.github.ipaas.ideploy.agent.handler;

import static org.junit.Assert.*;

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
import com.github.ipaas.ideploy.agent.handler.RollbackCodeHandler;

/**
 * 类或接口作用描述
 * 
 * @author Chenql
 */
public class RollbackCodeHandlerTest {

	public static final String TEST_GROUP = "/unit_test_dont_delete";
	public static final String TAGS = "/tags";
	public static final String ROLLBACKTO_VERSION = "test_20140710162244";
	public static final String USING_VERSION = "test_20140722140850";
	public static final String APP_NAME = "/rollback_test";

	private static String contextPath = FileUtil.getFile("com.github.ipaas.ideploy.agent.executor/target/test-classes/").getAbsolutePath();

	private CrsWebSvnUtil util;

	private String testDataRoot = "/testData/";
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
	 * 回退测试,先下载 USING_VERSION版本到本地,然后回退到 ROLLBACKTO_VERSION 版本
	 * 
	 * @throws Exception
	 */
	@Test
	public void rollbackCodeTest() throws Exception {
		String deployRoot = "/www/app" + APP_NAME;

		File usingfile = new File(deployRoot);

		RollbackCodeHandler rollback = new RollbackCodeHandler();

		String localBkPath = "/www/appbk" + APP_NAME + "/firstVer0";
		Map<String, Object> cantRollBackParams = new HashMap<String, Object>();
		cantRollBackParams.put("localBkPath", localBkPath);
		cantRollBackParams.put("deployPath", deployRoot);
		try {
			rollback.execute(null, null, cantRollBackParams, null); // 异常测试:
																	// 第一次安装,没有本地备份代码,不能回退!
			fail("Created fraction 1/0! That's undefined!");
		} catch (Exception e) {
			assertEquals("没有本地备份代码,不能回退!", e.getMessage());
		}
		File localBkFile = new File(localBkPath);
		if (!localBkFile.exists()) {
			localBkFile.mkdirs();
		}
		rollback.execute(null, null, cantRollBackParams, null); 
		
		Integer hostStatus4New = 1;
		String savePath = "/www/apptemp/" + USING_VERSION;
		Map<String, Object> firstRollbackParams = new HashMap<String, Object>();
		firstRollbackParams.put("usingCodeVerPath", TEST_GROUP + TAGS + "/" + USING_VERSION);
		firstRollbackParams.put("hostStatus", hostStatus4New);
		firstRollbackParams.put("savePath", savePath);
		firstRollbackParams.put("deployPath", deployRoot);
		rollback.execute(null, null, firstRollbackParams, null); // 回退到正在使用的版本,新机器没有部署过,全量回退

		DownloadCodeHandler downLoadHandler = new DownloadCodeHandler();
		Integer notUpdateAll = 2;
		Integer hostStatus4Old = 2;
		Map<String, Object> secondDownLoadParams = new HashMap<String, Object>();
		secondDownLoadParams.put("doingCodeVerPath", TEST_GROUP + TAGS + "/" + ROLLBACKTO_VERSION);
		secondDownLoadParams.put("usingCodeVerPath", TEST_GROUP + TAGS + "/" + USING_VERSION);
		secondDownLoadParams.put("hostStatus", hostStatus4Old);
		secondDownLoadParams.put("savePath", "/www/apptemp/rollback_test_vaild");// 本地代码保存路径
		secondDownLoadParams.put("updateAll", notUpdateAll);
		downLoadHandler.execute(null, null, secondDownLoadParams, null);
		File updateFile = new File("/www/apptemp/rollback_test_vaild/update.txt");
		List<String> updateList = FileUtils.readLines(updateFile);// 获取回退需要更新的内容,验证用

		String savePath2 = "/www/apptemp/" + ROLLBACKTO_VERSION;
		Map<String, Object> secondRollbackParams = new HashMap<String, Object>();
		secondRollbackParams.put("usingCodeVerPath", TEST_GROUP + TAGS + "/" + ROLLBACKTO_VERSION);
		secondRollbackParams.put("doingCodeVerPath", TEST_GROUP + TAGS + "/" + USING_VERSION);
		secondRollbackParams.put("hostStatus", hostStatus4Old);
		secondRollbackParams.put("savePath", savePath2);
		secondRollbackParams.put("deployPath", deployRoot);
		rollback.execute(null, null, secondRollbackParams, null); // 回退到正在使用的版本,

		// 判断是否正确回退
		for (String str : updateList) {
			if (str.startsWith("+")) {
				// 添加的文件
				Assert.assertTrue(new File(deployRoot + StringUtils.removeStart(str, "+").trim()).exists());
			} else if (str.startsWith("-")) {
				// 删除的文件
				String f = deployRoot + StringUtils.removeStart(str, "-").trim();
				Assert.assertFalse(new File(f).exists());
			}
		}

		FileUtils.cleanDirectory(usingfile);
		FileUtils.forceDelete(localBkFile);
		FileUtils.cleanDirectory(new File("/www/apptemp"));
	}
}

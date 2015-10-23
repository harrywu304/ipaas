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
package com.github.ipaas.ifw.jdbc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.jdbc.impl.DeciBean;
import com.github.ipaas.ifw.unitest.DbTestCase;
import com.github.ipaas.ifw.unitest.util.DbUnitUtil;
import com.github.ipaas.ifw.unitest.util.TestUtil;
import com.github.ipaas.ifw.util.SqlUtil;

/**
 * @author whx
 *
 */
public abstract class DbAccessServiceTest extends DbTestCase {

	private static Logger logger = LoggerFactory.getLogger(DbAccessServiceTest.class);

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	@Test
	public void testCreateRequest() throws Exception {
		logger.info("start...testCreateRequest()");
		DbAccessRequest request = this.getDbAccessService().createRequest();
		assertNotNull(request);

	}

	@Test
	public void testExecuteQueryString() throws Exception {
		logger.info("start...testExecuteQueryString()");
		testExecuteQuery(0);
	}

	// @Test
	public void testExecuteQueryDbAccessRequest() throws Exception {
		logger.info("start...testExecuteQueryDbAccessRequest()");
		testExecuteQuery(1);
	}

	@Test
	public void testExecuteQueryStringLong() throws Exception {
		logger.info("start...testExecuteQueryStringLong()");
		testExecuteQuery(2);
	}

	/**
	 * 数据库同步查询测试
	 * 
	 * @param type
	 *            0:normal, 1:DbAccessRequest, 2:with hashcode
	 */
	private void testExecuteQuery(int type) throws Exception {
		logger.info("start...testExecuteQuery(" + type + ")");
		// 加载预设表数据
		loadPreTableData();
		String sql = "select id, name, address,loginTime from demo_user";
		DbAccessResponse res = null;
		if (type == 0) {
			logger.info("start...testExecuteQuery(0)");
			res = this.getDbAccessService().executeQuery(sql);

			assertEquals(this.getDbAccessService().getDbConnectService().getGroupLocator().getNodes().size(),
					res.affectedRows());
			return;
		} else if (type == 1) {
			logger.info("start...testExecuteQuery(1)");
			DbAccessRequest request = this.getDbAccessService().createRequest();
			request.setSql(sql);
			res = this.getDbAccessService().executeQuery(request);
		} else if (type == 2) {
			logger.info("start...testExecuteQuery(2)");
			res = this.getDbAccessService().executeQuery(sql, 0);
		}
		String expUserData = "test/testdata/exp_user.xml";
		assertEqualsResponse(new File(expUserData), res.getResultData(), "demo_user");
	}

	@Test
	public void testExecuteUpdateString() throws Exception {
		logger.info("start...testExecuteUpdateString()");
		testExecuteUpdate(0);
	}

	@Test
	public void testExecuteUpdateDbAccessRequest() throws Exception {
		logger.info("start...testExecuteUpdateDbAccessRequest()");
		testExecuteUpdate(1);
	}

	@Test
	public void testExecuteUpdateStringLong() throws Exception {
		logger.info("start...testExecuteUpdateStringLong()");
		testExecuteUpdate(2);
	}

	/**
	 * 数据库同步更新测试
	 * 
	 * @param type
	 *            0:normal, 1:DbAccessRequest, 2:with hashcode
	 */
	private void testExecuteUpdate(int type) throws Exception {
		logger.info("start...testExecuteUpdate(" + type + ")");
		String sql = SqlUtil.getSql("insert into demo_user(id,name,address,content, loginTime) values(?,?,?,?,?)", 1,
				"adminbj", "beijing", "content_beijing", "2011-01-01 00:00:00");
		DbAccessResponse res = null;
		if (type == 0) {
			res = this.getDbAccessService().executeUpdate(sql);
		} else if (type == 1) {
			DbAccessRequest request = this.getDbAccessService().createRequest();
			request.setSql(sql);
			res = this.getDbAccessService().executeUpdate(request);

		} else if (type == 2) {
			res = this.getDbAccessService().executeUpdate(sql, 0);
		}
		assertEquals(1, res.affectedRows());
		String expUserData = "test/testdata/exp_user.xml";
		assertEqualsTable(new File(expUserData), "demo_user");
	}

	@Test
	public void testExecuteQueryBatch() throws Exception {
		logger.info("start...testExecuteQueryBatch()");
		Map<String, DbAccessRequest> requests = new HashMap<String, DbAccessRequest>();
		DbAccessRequest request = null;
		// 创建request实例req1
		request = this.getDbAccessService().createRequest();
		request.setSql("select * from demo_user where id = 1");
		requests.put("req1", request);
		// 创建request实例req2
		request = this.getDbAccessService().createRequest();
		request.setSql("select * from demo_user where name='adminbj'");
		requests.put("req2", request);
		Map<String, DbAccessResponse> reses = this.getDbAccessService().executeQueryBatch(requests);
		assertEquals(2, reses.size());
	}

	@Test
	public void testExecuteUpdateBatch() throws Exception {
		logger.info("start...testExecuteUpdateBatch()");
		Map<String, DbAccessRequest> requests = new HashMap<String, DbAccessRequest>();
		DbAccessRequest request = null;
		// 创建request实例req1
		request = this.getDbAccessService().createRequest();
		request.setSql(SqlUtil.getSql("insert into demo_user(id,name,address,content,loginTime) values(?,?,?,?,?)", 1,
				"adminbj", "beijing", "content_beijing", "2011-01-01 00:00:00"));
		requests.put("req1", request);
		// 创建request实例req2
		request = this.getDbAccessService().createRequest();
		request.setSql(SqlUtil.getSql("insert into demo_user(id,name,address,content,loginTime) values(?,?,?,?,?)", 2,
				"adminsh", "shanghai", "content_shanghai", "2011-01-01 00:00:00"));
		requests.put("req2", request);
		Map<String, DbAccessResponse> reses = this.getDbAccessService().executeUpdateBatch(requests, true);
		assertEquals(2, reses.size());
	}

	@Test
	public void testAsyncExecuteUpdateString() throws Exception {
		logger.info("start...testAsyncExecuteUpdateString()");
		testAsyncExecuteUpdate(0);
	}

	@Test
	public void testAsyncExecuteUpdateStringLong() throws Exception {
		logger.info("start...testAsyncExecuteUpdateStringLong()");
		testAsyncExecuteUpdate(1);
	}

	@Test
	public void testAsyncExecuteUpdateBatch() throws Exception {
		logger.info("start...testAsyncExecuteUpdateBatch()");
		Map<String, DbAccessRequest> requests = new HashMap<String, DbAccessRequest>();
		DbAccessRequest request = null;
		// 创建request实例req1
		request = this.getDbAccessService().createRequest();
		request.setSql(SqlUtil.getSql("insert into demo_user(id,name,address,content,loginTime) values(?,?,?,?,?)", 1,
				"adminbj", "beijing", "content_beijing", "2011-01-01 00:00:00"));
		requests.put("req1", request);
		// 创建request实例req2
		request = this.getDbAccessService().createRequest();
		request.setSql(SqlUtil.getSql("insert into demo_user(id,name,address,content,loginTime) values(?,?,?,?,?)", 2,
				"adminsh", "shanghai", "content_shanghai", "2011-01-01 00:00:00"));
		requests.put("req2", request);
		this.getDbAccessService().asyncExecuteUpdateBatch(requests, true);
		TestUtil.sleep(500);
		String expUserData = "test/testdata/exp_user_batch.xml";
		assertEqualsTable(new File(expUserData), "demo_user");
	}

	private void testAsyncExecuteUpdate(int type) throws Exception {
		logger.info("start...testAsyncExecuteUpdate(" + type + ")");
		String sql = SqlUtil.getSql("insert into demo_user(id,name,address,content,loginTime) values(?,?,?,?,?)", 1,
				"adminbj", "beijing", "content_beijing", "2011-01-01 00:00:00");
		if (type == 0) {
			try {
				this.getDbAccessService().asyncExecuteUpdate(sql);
			} catch (Exception e) {
			}
		} else if (type == 1) {
			this.getDbAccessService().asyncExecuteUpdate(sql, 0);
		}
		TestUtil.sleep(500);
		String expUserData = "test/testdata/exp_user.xml";
		assertEqualsTable(new File(expUserData), "demo_user");
	}

	@Override
	protected Connection getConnection() throws Exception {
		Connection conn = this.getDbConnectService().getConnectProfile(this.getAppID() + "_m0").getConnection();
		return conn;
	}

	@Override
	protected File getPreDataFile() throws Exception {
		String preUserData = "test/testdata/pre_user.xml";
		return new File(preUserData);
	}

	// backup or export exist table data
	public void backupData() {
		try {
			DbUnitUtil.backupData(getConnection(), new String[] { "demo_user" }, new File(
					"test/testdata/backup_user.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract DbConnectService getDbConnectService();

	public abstract DbAccessService getDbAccessService();

	public abstract String getAppID();

	/**
	 * 测试BigDecimal类型字段的获取
	 */
	@Test
	public void testDecimalColumn() {
		try {
			DbUnitUtil.loadData(getConnection(), new File("test/testdata/pre_test_decim_tb.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		BigDecimal expectedColData = new BigDecimal("10.10020");
		String sql = "select id,decim1 from test_decim_tb";
		DbAccessResponse rps = getDbAccessService().executeQuery(sql);
		Iterator<DbAccessResponseRow> rowIt = rps.iterator();
		DbAccessResponseRow row = rowIt.next();
		BigDecimal decim1 = row.getBigDecimal("decim1");
		assertEquals(expectedColData, decim1);
		List<DeciBean> resultList = rps.getResultData(DeciBean.class);
		DeciBean dbean = resultList.get(0);
		assertNotNull(dbean.getDecim1());
		assertEquals((Integer) 1, dbean.getId());
		assertEquals(expectedColData, dbean.getDecim1());

	}

}

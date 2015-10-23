/**
*/ 
package com.github.ipaas.ifw.unitest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;

/**
 * 数据库测试基础类
 * <p>建议不要直接继承DBUnit的DatabaseTestCase测试类，会导致TestBase的@BeforeClass注解无效
 *
 */
public abstract class DbTestCase {
	
	/**
	 * 数据库连接
	 */
	private IDatabaseConnection conn;
	/**
	 * 预设的数据库数据
	 */
	private IDataSet preDataSet;
	
	@Before
	public void setUp() throws Exception {
		//创建连接
		conn = new DatabaseConnection(getConnection());
		//初始化dataset
		if(preDataSet == null){
			preDataSet = getDataSet();
		}
		//清空表数据
		DatabaseOperation.TRUNCATE_TABLE.execute(conn, preDataSet);
	}

	@After
	public void tearDown() throws Exception {
		//关闭连接
		if(conn != null){
			conn.close();
		}
	}
	
	/**
	 * 获取dbunit的数据集
	 * @return
	 * @throws Exception
	 */
	private IDataSet getDataSet() throws Exception{
		return new FlatXmlDataSetBuilder().build(getPreDataFile());
	}
	
	/**
	 * 比较期待数据与查询返回数据
	 * @param expectData 期待的数据集
	 * @param actualData 实际的数据集
	 * @param tableName 表名
	 * @throws Exception
	 */
	public void assertEqualsResponse(File expectData, List<Map<String, Object>> actualData, String tableName){
		try{
			//获取期待数据集
			IDataSet expectDataSet = new FlatXmlDataSetBuilder().build(expectData);
			ITable expectTable = expectDataSet.getTable(tableName);	
			//比较大小
			assertEquals(expectTable.getRowCount(), actualData.size());
			//比较结果值
			Set<String> meta = null;
			for (int i = 0; i < actualData.size(); i++) {
				Map<String, Object> record = actualData.get(i);
				if(meta == null){
					meta = record.keySet();
				}
				for (String column : meta) {
					assertEquals(expectTable.getValue(i, column), record.get(column).toString());
				}
			}
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 加载预设的表数据
	 */
	public void loadPreTableData(){
		try {
			DatabaseOperation.CLEAN_INSERT.execute(conn, preDataSet);
		} catch (Exception e) {
			e.printStackTrace();
			fail("loadPreTableData exception");
		}
	}
	
	/**
	 *  比较期待数据与数据库表数据
	 * @param expectData 期待的数据集
	 * @param tableName 表名
	 * @throws Exception
	 */
	public void assertEqualsTable(File expectData, String tableName) {
		try{
			//获取期待数据集
			IDataSet expectDataSet = new FlatXmlDataSetBuilder().build(expectData);
			ITable expectTable = expectDataSet.getTable(tableName);	
			//获取数据库表数据
			IDataSet databaseDataSet = conn.createDataSet();
			ITable actualTable = databaseDataSet.getTable(tableName);
			//比较数据集
			Assertion.assertEquals(expectTable, actualTable);
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 * @throws Exception
	 */
	protected abstract Connection getConnection() throws Exception;
	
	/**
	 * 获取预设的XML表数据文件
	 * @return
	 * @throws Exception
	 */
	protected abstract File getPreDataFile() throws Exception;
	
}

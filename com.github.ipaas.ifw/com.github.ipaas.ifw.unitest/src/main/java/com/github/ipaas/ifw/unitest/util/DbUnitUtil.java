package com.github.ipaas.ifw.unitest.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;

import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.database.QueryDataSet;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;

/**
 * dbunit tools
 * 
 * @author whx
 */
public class DbUnitUtil {
	
	/**
	 * 备份数据库数据
	 * @param conn 数据库连接
	 * @param backupTables 进行备份的表名
	 * @param backupFile 存储备份数据的文件名
	 * @return
	 */
	public static boolean backupData(Connection conn, String[] backupTables, File backupFile){
		IDatabaseConnection connection = null;
		try {
			connection = new DatabaseConnection(conn);
			QueryDataSet backupDataSet = new QueryDataSet(connection);
			for(String tableName:backupTables){
				backupDataSet.addTable(tableName);
			}
			FlatXmlDataSet.write(backupDataSet, new FileOutputStream(backupFile));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;
	}
	
	/**
	 * 往数据库加载数据
	 * @param conn
	 * @param dataFile
	 * @return
	 */
	public static boolean loadData(Connection conn, File dataFile){
		IDatabaseConnection connection = null;
		try {
			connection = new DatabaseConnection(conn);
			IDataSet dataSet = new FlatXmlDataSetBuilder()
					.build(new FileInputStream(dataFile));
			DatabaseOperation.CLEAN_INSERT.execute(connection, dataSet);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;			
	}
	
	/**
	 * 清空数据表数据
	 * @param conn
	 * @param tables
	 * @return
	 */
	public static boolean truncateData(Connection conn, String[] tables){
		IDatabaseConnection connection = null;
		try {
			connection = new DatabaseConnection(conn);
			QueryDataSet dataSet = new QueryDataSet(connection);
			for(String tableName:tables){
				dataSet.addTable(tableName);
			}
			DatabaseOperation.TRUNCATE_TABLE.execute(connection, dataSet);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return false;			
	}	
	
	/**
	 * 从数据文件获取预期表数据
	 * @param dataFile 数据文件
	 * @param tableName 表名
	 * @return
	 */
	public static ITable getITableFromFile(File dataFile,String tableName){
		try{
			IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(dataFile);
			ITable expectedTable = expectedDataSet.getTable(tableName);		
			return expectedTable;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 从数据库获取实际表数据
	 * @param conn 数据库连接
	 * @param tableName 表名
	 * @return
	 */
	public static ITable getITableFromDb(Connection conn, String tableName){
		IDatabaseConnection connection = null;
		try{
			connection = new DatabaseConnection(conn);
			ITable actualTable = connection.createTable(tableName);
			return actualTable;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}
	
	/**
	 * 从数据库获取实际表数据
	 * @param conn 数据库连接
	 * @param tableName 表名
	 * @param sql 数据库查询sql
	 * @return
	 */
	public static ITable getTableFromDb(Connection conn, String tableName, String sql){
		IDatabaseConnection connection = null;
		try{
			connection = new DatabaseConnection(conn);
			ITable actualTable = connection.createQueryTable(tableName,sql);
			return actualTable;
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}	
	

}

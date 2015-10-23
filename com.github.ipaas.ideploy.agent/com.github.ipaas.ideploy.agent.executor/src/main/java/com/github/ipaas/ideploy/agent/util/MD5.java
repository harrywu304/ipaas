package com.github.ipaas.ideploy.agent.util;

import java.io.File;
import java.io.InputStream;

import java.io.FileInputStream;

import java.io.IOException;
import java.math.BigInteger;

import java.security.MessageDigest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MD5 {

	private static Logger logger = LoggerFactory.getLogger(MD5.class); 
	  
	/**
	 * 获取MD5摘要
	 * @param is  输入流
	 * @return MD5
	 * @throws Exception
	 */
	public static String getMD5(InputStream is) throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] buffer = new byte[2048];
		int length = -1;
		while ((length = is.read(buffer)) != -1) {
			md.update(buffer, 0, length);
		} 
		BigInteger bigInt = new BigInteger(1, md.digest());
		return bigInt.toString(16);

	}

	/**
	 * 对文件全文生成MD5摘要 
	 * @param file 获取摘要的文件
	 * @return MD5摘要码
	 */
	public static String getMD5(File file) {
		FileInputStream fis = null;
		try {
			 
			fis = new FileInputStream(file); 
			return getMD5(fis);
			// 16位加密
		} catch (Exception ex) {
			logger.error(ex.getMessage(), ex);
			return null;
		} finally {
			try {
				fis.close();
			} catch (IOException ex) {
				logger.error(ex.getMessage(), ex);
			}
		}
	}
}
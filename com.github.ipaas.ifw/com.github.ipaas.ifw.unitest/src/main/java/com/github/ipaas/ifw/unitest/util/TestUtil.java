package com.github.ipaas.ifw.unitest.util;

/**
 * 测试相关Util方法
 * @author whx
 *
 */
public class TestUtil {
	
	/**
	 * sleep millis
	 * @param millis 单位毫秒
	 */
	public static void sleep(long millis){
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}

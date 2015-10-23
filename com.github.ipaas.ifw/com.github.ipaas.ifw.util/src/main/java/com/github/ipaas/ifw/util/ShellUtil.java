package com.github.ipaas.ifw.util;

/**
 * Java调用shell脚本
 * 
 * @author Chenql
 */
public class ShellUtil {

	/**
	 * 运行shell脚本
	 * 
	 * @param shell
	 *            需要运行的shell脚本
	 */
	public static void execShell(String shell) {
		try {
			Runtime rt = Runtime.getRuntime();
			rt.exec(shell);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}

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
package com.github.ipaas.ifw.util;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Scanner;
import java.util.zip.ZipFile;

/**
 * 关闭资源工具类，资源关闭的同时也会被设置为null
 * <p>
 * 备注: 可以关闭JDK里面绝大多数需要关闭的资源对象,同时不对外抛出异常
 * <p>
 * 如果第三方需要关闭的资源类遵循jdk的接口标准也可以关闭,主要使用接口Closeable;
 * 
 * @author Chenql
 */
public final class CloseUtil {

	/**
	 * 关闭Connection资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Connection rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭Statement资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Statement rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭ResultSet资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(ResultSet rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭InputStream资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(InputStream rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭OutputStream资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(OutputStream rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭Closeable资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Closeable rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭ServerSocket资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(ServerSocket rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭Socket资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Socket rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭DatagramSocket资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(DatagramSocket rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭ZipFile资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(ZipFile rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

	/**
	 * 关闭Scanner资源对象 备注: 如果资源对象不为null, 关闭资源,不抛出任何异常
	 * 
	 * @param rsc
	 *            -- 资源对象
	 */
	public static void closeSilently(Scanner rsc) {

		if (null != rsc) {
			try {
				rsc.close();
			} catch (Exception ex) { /* 消除异常 */
			}
			rsc = null;
		}
	}

}

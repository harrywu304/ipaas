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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 文件管理工具类
 *
 * @author Chenql
 */
public class FileUtil {

	private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * 新建一个文件夹
	 * 
	 * @param folderPath
	 *            文件夹名称
	 */
	public static void newFolder(String folderPath) {
		String filePath = folderPath;
		File myFilePath = new File(filePath);
		if (!myFilePath.exists()) {
			myFilePath.mkdir();
		}
	}

	/**
	 * 新建文件
	 * 
	 * @param fileName
	 *            路径+文件名
	 * @return
	 * @throws IOException
	 */
	public static File createFile(String fileName) throws IOException {
		File myFileName = new File(fileName);
		if (!myFileName.exists()) {
			if (myFileName.createNewFile()) {
				return myFileName;
			}
		} else {
			return myFileName;
		}
		return null;
	}

	/**
	 * 删除单个文件或文件夹
	 * 
	 * @param fileName
	 *            文件或文件夹名
	 * @return 文件删除成功返回true,否则返回false
	 */
	public static boolean delete(String fileName) {
		File file = new File(fileName);
		if (!file.exists()) {
			return false;
		} else {
			if (file.isFile()) {
				return deleteFile(fileName);
			} else {
				return deleteDirectory(fileName);
			}
		}
	}

	/**
	 * 删除单个文件
	 * 
	 * @param fileName
	 *            被删除文件的文件名
	 * @return 单个文件删除成功返回true,否则返回false
	 */
	public static boolean deleteFile(String fileName) {
		File file = new File(fileName);
		if (file.isFile() && file.exists()) {
			file.delete();
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 删除目录（文件夹）以及目录下的文件
	 * 
	 * @param dir
	 *            被删除目录的文件路径
	 * @return 目录删除成功返回true,否则返回false
	 */
	public static boolean deleteDirectory(String dir) {
		// 如果dir不以文件分隔符结尾，自动添加文件分隔符
		if (!dir.endsWith(File.separator)) {
			dir = dir + File.separator;
		}
		File dirFile = new File(dir);
		// 如果dir对应的文件不存在，或者不是一个目录，则退出
		if (!dirFile.exists() || !dirFile.isDirectory()) {
			return false;
		}
		boolean flag = true;
		// 删除文件夹下的所有文件(包括子目录)
		File[] files = dirFile.listFiles();
		for (int i = 0; i < files.length; i++) {
			// 删除子文件
			if (files[i].isFile()) {
				flag = deleteFile(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
			// 删除子目录
			else {
				flag = deleteDirectory(files[i].getAbsolutePath());
				if (!flag) {
					break;
				}
			}
		}

		if (!flag) {
			return false;
		}

		// 删除当前目录
		if (dirFile.delete()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 复制文件
	 * 
	 * @param src
	 *            源文件流
	 * @param dst
	 *            目标文件流
	 */
	public static void copyFile(InputStream src, File dst) {
		int BUFFER_SIZE = 16 * 1024;
		try {

			InputStream in = null;
			OutputStream out = null;
			try {
				in = new BufferedInputStream(src, BUFFER_SIZE);
				out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
				byte[] buffer = new byte[BUFFER_SIZE];
				while (in.read(buffer) > 0) {
					out.write(buffer);
				}
			} finally {
				if (null != in) {
					in.close();
				}
				if (null != out) {
					out.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 得到资源文件的URL
	 * 
	 * <pre>
	 *     "abc.txt",
	 *     "com/todaytech/ddd.xml
	 * </pre>
	 * 
	 * @param resource
	 * @return
	 */
	public static URL getResource(String resource) {
		ClassLoader classLoader = null;
		URL url = null;
		try {
			
			// 以当前类所在位置查找资源,系统采用这种方式
			classLoader = Thread.currentThread().getContextClassLoader();
			while (classLoader != null) {
				url = classLoader.getResource(resource);
				if (url != null) {
					return url;
				}
				classLoader = classLoader.getParent();
			}
			// 以FileHelper类查找资源
			classLoader = FileUtil.class.getClassLoader();
			if (classLoader != null) {
				url = classLoader.getResource(resource);
				if (url != null) {
					return url;
				}
			}
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
		// 采用系统的环境变量定义的路径
		return ClassLoader.getSystemResource(resource);
	}

	/**
	 * 通过文件路径得到文件信息
	 * 
	 * @param resource
	 * @return
	 */
	public static File getFile(String resource) {
		File file = new File(resource);
		return file;
	}

	/**
	 * 得到文件的URL
	 * 
	 * @param resource
	 * @return
	 */
	public static URL getFileUrl(String resource) {
		StringBuilder path = new StringBuilder("file:///").append(FileUtil.getFile(resource).getAbsoluteFile());
		URL url = null;
		try {
			url = new URL(path.toString());
		} catch (MalformedURLException e) {
		}
		return url;

	}

	/**
	 * inputStream 转成 string
	 * 
	 * @param in
	 *            inputStream
	 * @return
	 */
	public static String inputStreamToString(InputStream in) {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		try {
			for (int n; (n = in.read(b)) != -1;) {
				out.append(new String(b, 0, n));
			}
		} catch (IOException e) {
			new RuntimeException(e);
		}
		return out.toString();
	}

	/**
	 * 文件移动
	 * 
	 * @param srcFile
	 *            原文件
	 * @param destPath
	 *            目标目录
	 * @return
	 */
	public static boolean move(File srcFile, String destPath) {
		// Destination directory
		File dir = new File(destPath);
		// Move file to new directory
		boolean success = srcFile.renameTo(new File(dir, srcFile.getName()));
		return success;
	}

	/**
	 * 文件移动
	 * 
	 * @param srcFile
	 *            原文件
	 * @param destPath
	 *            目标目录
	 * @return 是否移动成功
	 */
	public static boolean move(String srcFile, String destPath) {
		// File (or directory) to be moved
		File file = new File(srcFile);
		// Destination directory
		File dir = new File(destPath);
		// Move file to new directory
		boolean success = file.renameTo(new File(dir, file.getName()));
		return success;
	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            资源的URL
	 * @param path
	 *            下载的路径
	 * @param fileName
	 *            下载的文件名
	 * @return 已下载的文件
	 */
	public static boolean downloadFile(URL url, String path, String fileName) {

		int bytesum = 0;
		int byteread = 0;

		boolean result = false;
		try {
			URLConnection conn = url.openConnection();
			InputStream inStream = conn.getInputStream();
			FileOutputStream fs = new FileOutputStream(new StringBuilder(path).append("/").append(fileName).toString());
			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			result = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}

		return result;

	}

	/**
	 * 下载文件
	 * 
	 * @param url
	 *            资源的URL
	 * @param path
	 *            下载的路径
	 * @param fileName
	 *            下载的文件名
	 * @return 已下载的文件
	 * @throws
	 */
	public static boolean downloadFile(String url, String path, String fileName) {

		int bytesum = 0;
		int byteread = 0;

		boolean result = false;
		InputStream inStream = null;
		URLConnection conn = null;
		FileOutputStream fs = null;
		try {

			conn = new URL(url).openConnection();
			inStream = conn.getInputStream();
			fs = new FileOutputStream(new StringBuilder(path).append("/").append(fileName).toString());
			byte[] buffer = new byte[1204];
			while ((byteread = inStream.read(buffer)) != -1) {
				bytesum += byteread;
				System.out.println(bytesum);
				fs.write(buffer, 0, byteread);
			}
			result = true;
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		} finally {
			if (inStream != null)
				try {
					inStream.close();
				} catch (IOException e) {
				}
			if (fs != null)
				try {
					fs.close();
				} catch (IOException e) {
				}
		}
		return result;
	}

	/**
	 * 创建包括内容的txt文件
	 * 
	 * @param fileName
	 *            文件名
	 * @param context
	 *            内容
	 * @return true创建成功,false创建不成功
	 * @throws IOException
	 */
	public static boolean createTxtFile(String fileName, String context) throws IOException {
		File file = createFile(fileName);
		if (file != null) {
			appendToFile(context, file, System.getProperty("file.encoding"));
			return true;
		}
		return false;
	}

	/**
	 * append文件内容
	 * 
	 * @param content
	 *            内容
	 * @param file
	 *            文件
	 * @param encoding
	 *            编码
	 * @throws IOException
	 */
	public static void appendToFile(String content, File file, String encoding) throws IOException {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true), encoding));
			writer.write(content);
		} finally {
			if (writer != null)
				writer.close();
		}
	}

}

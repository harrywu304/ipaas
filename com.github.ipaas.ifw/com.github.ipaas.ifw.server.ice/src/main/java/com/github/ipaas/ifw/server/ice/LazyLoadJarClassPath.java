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
package com.github.ipaas.ifw.server.ice;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 延迟加载的java class path类
 * <p>
 * 备注: 类实例定时扫描一个目录,如果发现存在新增加的jar文件, 如果此jar文件没有被加载过就将其加入当前jvm的classpath中
 * 
 * @author Chenql
 */
@SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
public class LazyLoadJarClassPath {

	private static final Logger logger = LoggerFactory.getLogger(LazyLoadJarClassPath.class);

	/**
	 * 当前jvm的系统类加载器
	 */
	private static URLClassLoader sysloader = (URLClassLoader) ClassLoader.getSystemClassLoader();

	/**
	 * 系统类加载器 类型
	 */
	private static Class sysclass = URLClassLoader.class;

	/**
	 * 系统类加载器 的 addURL 方法 备注:此方法默认是非公开,通过反射机制被修改为可访问:method.setAccessible(true);
	 */
	private static Method method = null;

	/**
	 * 扫描目录间隔时间,默认使用long的最大值
	 */
	protected long period = Long.MAX_VALUE;

	/**
	 * 被定时扫描的目录(File对象表示)
	 */
	protected File libPath = null;

	/**
	 * 已经查找到并加入classpath的jar文件集合
	 */
	private Set<File> seekOut = null;

	/**
	 * 调度扫描目录行为的定时器
	 */
	private Timer timer = null;

	protected static FileFilter filter = new FileFilter() {
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".jar");
		}
	};

	/**
	 * 构造方法
	 * 
	 * @param scanPath
	 *            -- 扫描目录
	 * @param period
	 *            -- 扫描间隔, 单位: 秒
	 */
	public LazyLoadJarClassPath(String scanPath, int periodSecond) {

		this.period = periodSecond * 1000;
		File lib = new File(scanPath);
		if (lib.isDirectory() && period > 0) {
			this.libPath = lib;
			this.seekOut = new HashSet<File>(100, 0.9f);
		}
	}

	/**
	 * 开始调度任务
	 */
	public void startupScanTask() {

		StringBuilder timerName = new StringBuilder();
		timerName.append(System.identityHashCode(this));
		timerName.append("@LazyLoadJarClassPath{");
		timerName.append(" period:").append(period).append(",");
		timerName.append(" libPath:").append(libPath).append(",");
		timerName.append("}");
		logger.debug("InnerTimerTask:{}", timerName.toString());
		timer = new Timer(timerName.toString(), true);
		timer.schedule(new InnerTimerTask(), 0L, period);
	}

	public synchronized void scan() {
		File[] files = libPath.listFiles(filter);
		for (File item : files) {
			if (!seekOut.contains(item)) {
				try {
					addURL(item.toURL());
					seekOut.add(item);
					logger.debug("加载jar[{}]", item.toURL());
				} catch (MalformedURLException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static void addURL(URL url) {
		try {
			if (null == method) {
				Class cs[] = { URL.class };
				method = sysclass.getDeclaredMethod("addURL", cs);
				method.setAccessible(true);
			}
			method.invoke(sysloader, new Object[] { url });
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("Error, could not add URL to system classloader");
		}
	}

	/**
	 * 内部调度执行定时类
	 * 
	 * @author zhandl (yuyoo zhandl@hainan.net)
	 * @teme 2010-5-16 上午06:43:07
	 */
	private class InnerTimerTask extends TimerTask {

		@Override
		public void run() {
			scan();
		}
	}
}

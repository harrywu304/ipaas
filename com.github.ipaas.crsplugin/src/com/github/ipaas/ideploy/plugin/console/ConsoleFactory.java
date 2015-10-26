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

package com.github.ipaas.ideploy.plugin.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;

public class ConsoleFactory implements IConsoleFactory {

	private static MessageConsole console = new MessageConsole("Ideploy Tool", null);
	static boolean exists = false;

	/**
	 * get console
	 * 
	 * @return
	 */
	public static MessageConsole getConsole() {
		showConsole();
		console.activate();
		return console;
	}

	/**
	 * 描述:显示控制台
	 * */
	private static void showConsole() {
		if (console != null) {
			// 得到默认控制台管理器
			IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
			// 得到所有的控制台实例
			IConsole[] existing = manager.getConsoles();
			exists = false;
			// 新创建的MessageConsole实例不存在就加入到控制台管理器，并显示出来
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i])
					exists = true;
			}
			if (!exists) {
				manager.addConsoles(new IConsole[] { console });
				manager.showConsoleView(console);
			}

		}
	}

	/**
	 * open console
	 * */
	public void openConsole() {
		showConsole();
	}

	/** */
	/**
	 * 描述:关闭控制台
	 * */
	public static void closeConsole() {
		IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
		if (console != null) {
			manager.removeConsoles(new IConsole[] { console });
		}
	}

}

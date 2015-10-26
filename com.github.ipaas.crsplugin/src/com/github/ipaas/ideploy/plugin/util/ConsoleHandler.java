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

package com.github.ipaas.ideploy.plugin.util;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

import com.github.ipaas.ideploy.plugin.console.ConsoleFactory;

public class ConsoleHandler {
	private static MessageConsoleStream consoleStream;

	public static void info(final String _message) {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageConsole console = ConsoleFactory.getConsole();
				if (console != null) {
					consoleStream = ConsoleFactory.getConsole().newMessageStream();
					consoleStream.println(new SimpleDateFormat("HH:mm:ss ").format(new Date()) + "[INFO]" + _message);
					try {
						consoleStream.flush();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void cleanConsole() {
		Display.getDefault().syncExec(new Runnable() {
			@Override
			public void run() {
				MessageConsole console = ConsoleFactory.getConsole();
				if (console != null) {
					console.clearConsole();
				}
			}
		});
	}

	public static void error(final String _message) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				consoleStream = ConsoleFactory.getConsole().newMessageStream();
				consoleStream.setColor(new Color(null, 255, 0, 0));
				consoleStream.println(new SimpleDateFormat("HH:mm:ss ").format(new Date()) + "[ERROR] " + _message);
			}
		});
	}

	public static void closeConsole() {
		ConsoleFactory.closeConsole();
	}
}
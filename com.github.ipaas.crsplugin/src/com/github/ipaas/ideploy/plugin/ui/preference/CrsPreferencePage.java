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

package com.github.ipaas.ideploy.plugin.ui.preference;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.github.ipaas.ideploy.plugin.Activator;
import com.github.ipaas.ideploy.plugin.bean.FilterPattern;
import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.DESPlus;
import com.github.ipaas.ideploy.plugin.util.JsonUtil;

/**
 * 
 * CRS首选页设置
 * 
 * @author Chenql  
 */
public class CrsPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private static UserInfo user = new UserInfo();
	private StringFieldEditor host;
	private StringFieldEditor account;
	private StringFieldEditor password;
	private Table table;
	private FilterPatternDialog editDialog;
	private Shell shell;

	public CrsPreferencePage() {
		super();
		shell = new Shell();
		editDialog = new FilterPatternDialog(null, shell);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse
	 * .swt.widgets.Composite)
	 */
	@Override
	protected Control createContents(Composite parent) {
		GridLayout gloableLable = new GridLayout();
		gloableLable.numColumns = 2;
		parent.setLayout(gloableLable);

		Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		host =  new StringFieldEditor("host", "Ideploy Host:", group);
		account = new StringFieldEditor("email", "Ideploy Account:", group);
		password = new StringFieldEditor("password", "Ideploy Password:", group) {
			@Override
			protected void doFillIntoGrid(Composite parent, int numColumns) {
				super.doFillIntoGrid(parent, numColumns);
				getTextControl().setEchoChar('*');
			}
		};
		new Label(parent, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(parent, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		new Label(parent, SWT.NONE).setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// create a table
		table = new Table(parent, SWT.BORDER | SWT.FULL_SELECTION | SWT.CHECK);
		table.setLayoutData(new GridData(GridData.FILL_VERTICAL));

		TableColumn checkCln = new TableColumn(table, SWT.CENTER);
		TableColumn nameCln = new TableColumn(table, SWT.CENTER);
		TableColumn locationCln = new TableColumn(table, SWT.CENTER);
		TableColumn enableCln = new TableColumn(table, SWT.CENTER);
		checkCln.setText("");
		nameCln.setText("Name");
		locationCln.setText("Parnter");
		enableCln.setText("Enable");
		checkCln.setWidth(30);
		nameCln.setWidth(100);
		locationCln.setWidth(150);
		enableCln.setWidth(80);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		table.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {
				for (int i = 0; i < table.getItemCount(); i++) {
					TableItem item = table.getItem(i);
					item.setText(3, item.getChecked() ? "Enable" : "Disable");
				}
			}
		});

		Group buttonGroup = new Group(parent, SWT.NONE);

		GridData buttonData = new GridData(GridData.FILL_VERTICAL);
		buttonGroup.setLayoutData(buttonData);
		GridLayout buttonLayout = new GridLayout(1, true);
		buttonGroup.setLayout(buttonLayout);

		final Button addBtn = new Button(buttonGroup, SWT.PUSH);
		addBtn.setText("  Add   ");
		addBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				shell.setData("nameText", "");
				shell.setData("patternText", "");
				editDialog.open();
				if (shell.getData("nameText") != null && shell.getData("patternText") != null) {
					addTableItem(true, shell.getData("nameText").toString(), shell.getData("patternText").toString(),
							table);
				}
			}
		});

		final Button editBtn = new Button(buttonGroup, SWT.PUSH);
		editBtn.setText("  Edit   ");
		editBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selectItems = table.getSelection();
				if (selectItems.length > 0 && selectItems[0] != null) {
					shell.setData("nameText", selectItems[0].getText(1));
					shell.setData("patternText", selectItems[0].getText(2));
					editDialog.open();
					String name = (String) shell.getData("nameText");
					String pattern = (String) shell.getData("patternText");
					editTableItem(name, pattern, table);
				}

			}
		});

		final Button rmBtn = new Button(buttonGroup, SWT.PUSH);
		rmBtn.setText("Remove");
		rmBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = table.getSelection();
				if (items.length >= 0) {
					table.remove(table.getSelectionIndex());
				}
			}
		});
		table.select(0);
		initialize();
		return null;
	}

	protected void initialize() {
		// super.initialize();
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String hostStr = store.getString("host");
		host.setStringValue(hostStr);
		
		String email = store.getString("email");
		account.setStringValue(email);
		try {
			DESPlus plus = new DESPlus(email);// 账户名做密钥
			String pwdValue = store.getString("password");
			password.setStringValue(plus.decrypt(pwdValue));// 解密
		} catch (Exception e) {
			ConsoleHandler.error("获取配置错误:" + e.getMessage());
		}

		String patternsJson = store.getString("patternsJson");
		if (patternsJson != null || !patternsJson.trim().equals("")) {
			List<String> jsonList = JsonUtil.toBean(patternsJson, List.class);
			for (String json : jsonList) {
				FilterPattern pattern = JsonUtil.toBean(json, FilterPattern.class);
				this.addTableItem(pattern.isChecked(), pattern.getName(), pattern.getPattern(), table);
			}
		}

		// for (int i = 0; i <= 4; i++) {
		// this.addTableItem(true, "配置文件" + String.valueOf(i), "config" +
		// String.valueOf(i), table);
		// }
	}

	/**
	 * 表格添加行
	 * 
	 * @param checked
	 * @param name
	 * @param location
	 * @param table
	 */
	private void addTableItem(boolean checked, String name, String location, Table table) {
		TableItem tableItem = new TableItem(table, SWT.NONE);
		String enable = checked ? "Enable" : "Disable";
		tableItem.setText(new String[] { "", name, location, enable });
		tableItem.setChecked(checked);
		// tableItem.
	}

	private void editTableItem(String name, String location, Table table) {
		TableItem tableItem = table.getSelection()[0];
		if (tableItem != null) {
			String enable = tableItem.getChecked() ? "Enable" : "Disable";
			tableItem.setText(new String[] { "", name, location, enable });
			tableItem.setChecked(tableItem.getChecked());
		}
	}

	/**
	 * 初始化,设置默认值
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String host = store.getString("host");
		ConsoleHandler.info(host);
		if (host == null || host.trim().equals("")) {
			store.setValue("host", "ideploy.ipaas.com");
		}
		// 设置默认配置文件
		List<String> defaultPatterns = new ArrayList<String>();
		defaultPatterns.add(JsonUtil.toJson(new FilterPattern("resinTmp", "WEB-INF/tmp", true)));
		defaultPatterns.add(JsonUtil.toJson(new FilterPattern("resinWork", "WEB-INF/work", true)));
		defaultPatterns.add(JsonUtil.toJson(new FilterPattern("resinConf", "WEB-INF/classes/config", false)));
		defaultPatterns.add(JsonUtil.toJson(new FilterPattern("iceConf", "resources/config", false)));
		defaultPatterns.add(JsonUtil.toJson(new FilterPattern("iceStart", "bin/ice.sh", false)));
		defaultPatterns.add(JsonUtil.toJson(new FilterPattern("iceStart", "bin/ice.sh", false)));
		String patternsJson = store.getString("patternsJson");
		if (patternsJson == null || patternsJson.trim().equals("")) {
			store.setValue("patternsJson", JsonUtil.toJson(defaultPatterns));
		}
	}

	// 从preferrence中获取信息
	public static UserInfo getUserInfo() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		String email = store.getString("email");
		user.setEmail(email);
		try {
			DESPlus plus = new DESPlus(email);// 账户名做密钥
			String pwdValue = store.getString("password");
			user.setPassword(plus.decrypt(pwdValue));// 解密
		} catch (Exception e) {
			ConsoleHandler.error("获取配置错误:" + e.getMessage());
		}
		user.setUrl(store.getString("host"));
		String patternsJson = store.getString("patternsJson");
		if (patternsJson != null || !patternsJson.trim().equals("")) {
			user.setPatternJsonList(JsonUtil.toBean(patternsJson, List.class));
		} else {
			user.setPatternJsonList(new ArrayList<String>());
		}
		return user;
	}

	@Override
	public boolean performOk() {
		String hostStr = host.getStringValue();
		String userName = account.getStringValue();
		String pwd = password.getStringValue();
		if (hostStr == null || hostStr.trim().equals("")) {
			ConsoleHandler.error("Ideploy web 地址不能为空");
			return false;
		}
		if (userName == null || userName.trim().equals("") || pwd == null || pwd.trim().equals("")) {
			ConsoleHandler.error("帐号密码不能为空");
			return false;
		}
		
		
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setValue("email", userName);
		store.setValue("host", hostStr);
		try {
			DESPlus plus = new DESPlus(userName);// 账户名做密钥
			String pwdValue = plus.encrypt(pwd);// 加密密码
			store.setValue("password", pwdValue);
		} catch (Exception e) {
			ConsoleHandler.error("保存错误:" + e.getMessage());
		}
		List<String> partternJson = new ArrayList<String>();
		for (TableItem item : table.getItems()) {
			partternJson.add(JsonUtil.toJson(new FilterPattern(item.getText(1), item.getText(2), item.getChecked())));
		}
		store.setValue("patternsJson", JsonUtil.toJson(partternJson));
		return true;
	}
}
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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 添加配置项对话框
 * 
 * @author Chenql  
 */
public class FilterPatternDialog extends Dialog {

	/**
	 * @param parentShell
	 */
	private Text nameText;
	private Text patternText;
	private Button cancleBtn;
	private Button OKBtn;
	private Shell preferenceShell;

	// private boolean uploadFlag = false;

	public FilterPatternDialog(Shell parentShell, Shell preferenceShell) {
		super(parentShell);
		this.preferenceShell = preferenceShell;
		// this.param = param;
		// this.detial = detial;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		RowLayout layout = new RowLayout();

		layout.marginTop = 10;
		layout.marginBottom = 1;
		layout.spacing = 5;// 组件之间间隔
		container.setLayout(layout);
		Label infoLable = new Label(container, SWT.NONE);
		infoLable.setLayoutData(new RowData(60, 20));
		infoLable.setText(" Name:");

		nameText = new Text(container, SWT.BORDER | SWT.WRAP);
		nameText.setLayoutData(new RowData(300, 20));
		// new Label(container, SWT.NONE).setLayoutData(new RowData(200, 20));

		Label patternLable = new Label(container, SWT.NONE);
		patternLable.setLayoutData(new RowData(60, 20));
		patternLable.setText(" Pattern:");
		patternText = new Text(container, SWT.BORDER | SWT.WRAP);
		patternText.setLayoutData(new RowData(300, 20));

		new Label(container, SWT.NONE).setLayoutData(new RowData(150, 20));
		OKBtn = new Button(container, SWT.NONE);// createButton(parent,
												// IDialogConstants.OK_ID,
												// "Generate", true);
		OKBtn.setText("OK");
		OKBtn.setLayoutData(new RowData(100, 25));
		OKBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				preferenceShell.setData("nameText", nameText.getText());
				preferenceShell.setData("patternText", patternText.getText());
				close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		cancleBtn = new Button(container, SWT.NONE);
		cancleBtn.setText("Cancle");
		cancleBtn.setLayoutData(new RowData(100, 25));
		cancleBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				preferenceShell.setData("nameText", null);
				preferenceShell.setData("patternText", null);
				close();
			}
		});

		if (preferenceShell.getData("nameText") != null) {
			this.nameText.setText(preferenceShell.getData("nameText").toString());
		}
		if (preferenceShell.getData("patternText") != null) {
			this.patternText.setText(preferenceShell.getData("patternText").toString());
		}
		return container;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add Filter Pattern");
	}

	@Override
	protected Point getInitialSize() {
		return new Point(400, 170);
	}

}

package com.github.ipaas.ideploy.plugin.ui.popup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
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

import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.core.RequestAcion;
import com.github.ipaas.ideploy.plugin.ui.preference.CrsPreferencePage;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.JsonUtil;

/**
 * 创建版本确认框
 * 
 * @author Chenql  
 */
public class ConfirmCreateVersionDialog extends Dialog {

	/**
	 * @param parentShell
	 */
	private Map<String, String> param;
	private Text remarkText;
	private Text detialText;
	private Button detialBtn;
	private Button generateBtn;
	private String detial;
	private boolean uploadFlag = false;

	public ConfirmCreateVersionDialog(Shell parentShell, Map<String, String> param, String detial) {
		super(parentShell);
		this.param = param;
		this.detial = detial;
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
		infoLable.setLayoutData(new RowData(450, 20));
		infoLable.setText("   代码包上传成功,输入版本描述信息:");

		new Label(container, SWT.NONE).setLayoutData(new RowData(2, 50));

		remarkText = new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL);
		remarkText.setLayoutData(new RowData(400, 50));
		new Label(container, SWT.NONE).setLayoutData(new RowData(200, 20));

		generateBtn = new Button(container, SWT.NONE);// createButton(parent,
														// IDialogConstants.OK_ID,
														// "Generate", true);
		generateBtn.setText("OK");
		generateBtn.setLayoutData(new RowData(100, 25));
		generateBtn.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				uploadFlag = true;
				final List<NameValuePair> paramList = new ArrayList<NameValuePair>();
				String remark = remarkText.getText();
				Map<String, String> crJson = new HashMap<String, String>();
				crJson.put("remark", remark);
				for (String name : param.keySet()) {
					if (name.equals("userName") || name.equals("password")) {
						paramList.add(new BasicNameValuePair(name, param.get(name)));
					} else {
						crJson.put(name, param.get(name));
					}
				}
				paramList.add(new BasicNameValuePair("crJson", JsonUtil.toJson(crJson)));
				close();
				Job job = new Job("Generate new version...") {
					@Override
					protected IStatus run(IProgressMonitor arg0) {
						UserInfo userInfo = CrsPreferencePage.getUserInfo();
						ConsoleHandler.info("正在创建版本,请稍等...");
						String result = RequestAcion.post(userInfo.getUrl() + "/crs_code/create", paramList, true);
						try {
							Map<String, Object> map = JsonUtil.toBean(result, Map.class);
							if (map.get("optSta").equals("failure")) {
								ConsoleHandler.error((String) map.get("optTxt"));
							} else {
								ConsoleHandler.info((String) map.get("optTxt"));
							}
						} catch (Exception exp) {
							ConsoleHandler.error("创建版本失败:" + exp.getMessage());
							exp.printStackTrace();
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();

			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});

		detialBtn = new Button(container, SWT.NONE);
		detialBtn.setText("Detial >>");
		detialBtn.setLayoutData(new RowData(100, 25));
		detialBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (detialBtn.getText().equals("<< Detial")) {
					detialBtn.setText("Detial >>");
					detialText.setVisible(false);
					getShell().setSize(450, 160);
				} else {
					detialText.setVisible(true);
					detialBtn.setText("<< Detial");
					getShell().setSize(450, 280);
				}

			}
		});
		new Label(container, SWT.NONE).setLayoutData(new RowData(20, 28));
		new Label(container, SWT.NONE).setLayoutData(new RowData(2, 50));
		detialText = new Text(container, SWT.BORDER | SWT.WRAP | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		detialText.setLayoutData(new RowData(400, 100));
		detialText.setVisible(false);
		detialText.setEditable(false);
		detialText.setText(detial);

		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("创建新版本");
		newShell.addDisposeListener(new DisposeListener() {

			@Override
			public void widgetDisposed(DisposeEvent arg0) {
				if (!uploadFlag) {
					UserInfo userInfo = CrsPreferencePage.getUserInfo();
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("uuIdPath", param.get("uuIdPath")));
					RequestAcion.post(userInfo.getUrl() + "/crs_code/delete_tmp_file", params, false);
				}
			}
		});
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		this.getButtonBar().setLayoutData(new RowData(400, 1));
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return parent;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 160);
	}

}

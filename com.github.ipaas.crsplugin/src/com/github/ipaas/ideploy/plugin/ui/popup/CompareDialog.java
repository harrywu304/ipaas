package com.github.ipaas.ideploy.plugin.ui.popup;

import org.apache.tools.ant.util.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.github.ipaas.ideploy.plugin.bean.PathInfo;
import com.github.ipaas.ideploy.plugin.bean.ProjectInfo;
import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.core.Comparetor;
import com.github.ipaas.ideploy.plugin.core.UploadFileJob;
import com.github.ipaas.ideploy.plugin.ui.preference.CrsPreferencePage;
import com.github.ipaas.ideploy.plugin.util.ConfigUtil;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;

public class CompareDialog extends Dialog {
	private ProjectInfo projectInfo;// 项目信息
	private Text outputPathText;// 代码保存路径
	private Text sourcePathText;// 源代码路径
	private Button generateBtn;// 执行按钮
	private Combo projecCombo;// 项目名下拉列表
	private String sourceLocation;
	private boolean loadProFlag = false;
	private String UPLOAD_TMP_FILE_PATH = "/tmp/zip";
	private boolean uploadBtnSelected = false;
	private Button outputBtn;

	public CompareDialog(Shell parentShell, String sourceLocation) {
		super(parentShell);
		this.sourceLocation = sourceLocation;

	}

	@Override
	protected Control createDialogArea(Composite parent) {
		ConsoleHandler.cleanConsole();

		ModifyListener pathInfoListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				boolean sourceFlag = false;
				boolean proFlag = false;

				if (sourcePathText.getText() != null && !sourcePathText.getText().equals("")) {
					sourceFlag = true;
					sourcePathText.setToolTipText(null);
				} else {
					sourcePathText.setToolTipText("此文件目录不存在!");
				}
				if (projecCombo.getText() != null && !projecCombo.getText().equals("")
						&& !projecCombo.getText().equals("请选择服务组")) {
					proFlag = true;
					sourcePathText.setToolTipText(null);
				} else {
					projecCombo.setToolTipText("请选择服务组");
				}
				if (generateBtn != null) {
					generateBtn.setEnabled(sourceFlag && proFlag);
				}
			}
		};

		getShell().setText("CRS代码发布工具");
		getShell().setMinimumSize(550, 225);
		getShell().setDragDetect(true);
		Composite container = (Composite) super.createDialogArea(parent);

		RowLayout layout = new RowLayout();

		layout.marginTop = 30;
		layout.spacing = 20;// 组件之间间隔
		container.setLayout(layout);

		// 常用的布局
		final RowData lableLayout = new RowData(100, 20);
		final RowData pathLayout = new RowData(230, 20);

		this.addHideCompent(container, 20, null);

		Label srcPathLabel = new Label(container, SWT.NONE);
		srcPathLabel.setLayoutData(lableLayout);
		srcPathLabel.setText("Source directory:");
		sourcePathText = new Text(container, SWT.BORDER);// 保存路径
		sourcePathText.setLayoutData(pathLayout);
		sourcePathText.addModifyListener(pathInfoListener);

		Button srcBtn = new Button(container, SWT.NONE);
		srcBtn.setText("browse");
		srcBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(getParentShell(), SWT.NONE);
				String srcPath = dlg.open();
				sourcePathText.setText(srcPath);
			}
		});

		this.addHideCompent(container, 20, null);

		this.addHideCompent(container, 20, null);

		Label serverGroupLabel = new Label(container, SWT.NONE);
		serverGroupLabel.setLayoutData(lableLayout);
		serverGroupLabel.setText("Server Group:");
		// 项目下拉列表
		projecCombo = new Combo(container, SWT.NONE);
		projecCombo.setLayoutData(new RowData(220, 20));
		projecCombo.addModifyListener(pathInfoListener);
		projecCombo.addMouseListener(new MouseListener() {
			@Override
			public void mouseUp(MouseEvent arg0) {
			}

			@Override
			public void mouseDown(MouseEvent arg0) {
				// System.out.println(projectInfo.getProjectList());
				if (!loadProFlag && projectInfo != null) {
					for (String name : projectInfo.getProjectList()) {
						projecCombo.add(name);
					}
				}
				loadProFlag = true;
			}

			@Override
			public void mouseDoubleClick(MouseEvent arg0) {
			}
		});
		Job job = new Job("get group...") {// 异步获取服务组
			@Override
			protected IStatus run(IProgressMonitor arg0) {
				try {
					projectInfo = ConfigUtil.getProjectInfo();
				} catch (Exception e) {
					// projecCombo.setToolTipText(e.getMessage());
					ConsoleHandler.error(e.getMessage());
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();

		this.addHideCompent(container, 100, null);
		this.addHideCompent(container, 20, null);
		final Button uploadRadio = new Button(container, SWT.RADIO);
		uploadRadio.setText("Upload");
		this.addHideCompent(container, 100, null);

		final Button exportRadio = new Button(container, SWT.RADIO);
		exportRadio.setText("Export");

		this.addHideCompent(container, 200, null);

		this.addHideCompent(container, 20, null);
		Label savePathLable = new Label(container, SWT.NONE);
		savePathLable.setLayoutData(lableLayout);
		savePathLable.setText("Output directory:");
		outputPathText = new Text(container, SWT.BORDER);// 输出路径
		outputPathText.setLayoutData(pathLayout);
		outputPathText.addModifyListener(pathInfoListener);
		outputBtn = new Button(container, SWT.NONE);
		outputBtn.setText("browse");
		outputBtn.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dlg = new DirectoryDialog(getParentShell(), SWT.NONE);
				String savePath = dlg.open();
				outputPathText.setText(savePath);
			}
		});

		this.addHideCompent(container, 550, 10);
		outputPathText.setText(ConfigUtil.getSavePath());
		sourcePathText.setText(this.sourceLocation);
		PathInfo preInfo = ConfigUtil.getPathInfo(this.sourceLocation);
		// if (preInfo != null && preInfo.getGroupId() != null) {
		// projecCombo.setText(preInfo.getGroupId());
		// } else {
		projecCombo.setText("请选择服务组");
		// }

		uploadRadio.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				uploadBtnSelected = uploadRadio.getSelection();
				this.renderView(uploadBtnSelected);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				uploadBtnSelected = uploadRadio.getSelection();
				this.renderView(uploadBtnSelected);
			}

			private void renderView(boolean isSelected) {
				if (isSelected) {
					getShell().setSize(550, 225);
				} else {
					getShell().setSize(550, 280);
				}
				outputPathText.setEnabled(!isSelected);
				outputBtn.setEnabled(!isSelected);
			}
		});
		return container;
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		generateBtn = createButton(parent, IDialogConstants.OK_ID, "OK", true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Cancel", false);
		if (projecCombo.getText() == null || projecCombo.getText().equals("请选择服务组") || projecCombo.getText().equals("")) {
			generateBtn.setEnabled(false);
		}
	}

	private Label addHideCompent(Composite container, Integer width, Integer height) {
		if (width == null)
			width = 60;
		if (height == null)
			height = 20;
		RowData rowData = new RowData(width, height);
		Label lable = new Label(container, SWT.NONE);
		lable.setLayoutData(rowData);
		return lable;
	}

	@Override
	protected Point getInitialSize() {
		return new Point(450, 225);
	}

	@Override
	protected void cancelPressed() {
		this.close();
	}

	@Override
	protected void okPressed() {
		String sourcePath = sourcePathText.getText();
		if (sourcePath.endsWith("/")) {
			sourcePath = StringUtils.removeSuffix(sourcePathText.getText(), "/");
		}
		if (uploadBtnSelected) {// 上传代码
			final PathInfo pathInfo = new PathInfo();
			pathInfo.setSavePath(UPLOAD_TMP_FILE_PATH);
			pathInfo.setSrcPath(sourcePath);
			pathInfo.setTargetPath(sourcePath);
			pathInfo.setGroupId(projecCombo.getText());
			ConfigUtil.savePathInfo(pathInfo.getSrcPath(), pathInfo);
			ConfigUtil.saveOutputPath(outputPathText.getText());
			this.close();
			ConsoleHandler.info("开始代码上传....");
			Job job = new UploadFileJob("crs uploading file ...", pathInfo, projectInfo, null);
			job.schedule();
		} else {// 生成代码包
			final PathInfo pathInfo = new PathInfo();
			pathInfo.setSavePath(outputPathText.getText());
			pathInfo.setSrcPath(sourcePath);
			pathInfo.setTargetPath(sourcePath);
			pathInfo.setGroupId(projecCombo.getText());
			ConfigUtil.savePathInfo(pathInfo.getSrcPath(), pathInfo);
			ConfigUtil.saveOutputPath(outputPathText.getText());
			this.close();
			super.okPressed();
			Job job = new Job("crs comparing...") {
				@Override
				protected IStatus run(IProgressMonitor arg0) {
					UserInfo userInfo = CrsPreferencePage.getUserInfo();
					Comparetor.doAtion(pathInfo, userInfo);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}

	}
}
package com.github.ipaas.ideploy.plugin.ui.popup;

import java.io.File;

import org.eclipse.core.internal.resources.Project;
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
import com.github.ipaas.ideploy.plugin.util.JsonUtil;
import com.github.ipaas.ideploy.plugin.util.MavenUtil;
import com.github.ipaas.ideploy.plugin.util.XmlUtil;

public class CompileDialog extends Dialog {
	private ProjectInfo projectInfo;// 项目信息
	private Text outputPathText;// 代码保存路径
	private Text targetPathText;// 源代码路径
	private Button generateBtn;// 执行按钮
	private Combo projecCombo;// 项目名下拉列表
	private String sourceLocation;
	private boolean loadProFlag = false;
	private String UPLOAD_TMP_FILE_PATH = "/tmp/zip";
	private boolean uploadBtnSelected = false;
	private Button outputBtn;
	// private Project project;
	private String projectName;
	private long svnRevision;
	private String svnUrl;

	public CompileDialog(Shell parentShell, String sourceLocation, Project project, long svnRevesion, String svnUrl) {
		super(parentShell);
		this.sourceLocation = sourceLocation;
		this.projectName = XmlUtil.getString(sourceLocation + "\\pom.xml", XmlUtil.BUILD_NAME_ELEMENT);// 读取pom配置文件设置的
																										// finalName
		if (this.projectName == null || this.projectName.equals("")) {// 如果没有设置finalName
			this.projectName = XmlUtil.getString(sourceLocation + "\\pom.xml", XmlUtil.ARTIFACTID_ELEMENT) + "-"
					+ XmlUtil.getString(sourceLocation + "\\pom.xml", XmlUtil.VERSION_ELEMENT);// 读取pom配置文件设置的artifactId和version
		}
		this.svnRevision = svnRevesion;
		this.svnUrl = svnUrl;
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		ModifyListener projectInfoListener = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent arg0) {
				boolean proFlag = false;

				if (projecCombo.getText() != null && !projecCombo.getText().equals("")
						&& !projecCombo.getText().equals("请选择服务组")) {
					proFlag = true;
					// System.out.println(projecCombo.getText());
					PathInfo pathInfo = ConfigUtil.getPathInfo(projecCombo.getText());
					// System.out.println("pathInfo:  " +
					// JsonUtil.toJson(pathInfo));
					if (pathInfo != null && pathInfo.getTargetPath() != null) {// 从上次记录中获取配置信息
						targetPathText.setText(pathInfo.getTargetPath());
					} else {// 没有记录信息
						String sgActName = projectInfo.getSgType(projecCombo.getText());// 获取服务组容器类型
						String targetPath = "target/" + projectName;// maven
																	// 生成二进制文件默认目录
						if (sgActName.equals(XmlUtil.ICE_CONTAINER)) {// 获取Ice项目代码输出路径
							String assembylFile = XmlUtil.getString(sourceLocation + "\\pom.xml",
									XmlUtil.ASSEMBLY_ELEMENT, "script\\assembly.xml");
							// System.out.println("assemblyFile:" +
							// assembylFile);
							String adsolutePath = sourceLocation + "\\" + assembylFile;
							if (new File(adsolutePath).exists()) {
								String id = XmlUtil.getString(adsolutePath, "id", "bin");
								System.out.println("id    " + id);
								targetPath += "-" + id;
							}
							targetPath += "/" + projectName;
						}
						targetPathText.setText(targetPath);
					}
					targetPathText.setToolTipText(null);
				} else {
					projecCombo.setToolTipText("请选择服务组");
				}
				if (generateBtn != null) {
					generateBtn.setEnabled(proFlag);
				}
			}
		};

		getShell().setText("CRS代码编译部署");
		getShell().setMinimumSize(500, 325);
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
		Label svnUrlLabel = new Label(container, SWT.NONE);
		svnUrlLabel.setLayoutData(lableLayout);
		svnUrlLabel.setText("    Svn Url:");
		Text svnUrlText = new Text(container, SWT.BORDER);// 保存路径
		svnUrlText.setLayoutData(pathLayout);
		svnUrlText.setText(this.svnUrl);
		svnUrlText.setEditable(false);
		this.addHideCompent(container, 40, null);

		this.addHideCompent(container, 20, null);
		Label svnRevesionLabel = new Label(container, SWT.NONE);
		svnRevesionLabel.setLayoutData(lableLayout);
		svnRevesionLabel.setText("  Svn Revesion:");
		Text svnRevesionText = new Text(container, SWT.BORDER);// 保存路径
		svnRevesionText.setLayoutData(pathLayout);
		svnRevesionText.setText(String.valueOf(this.svnRevision));
		svnRevesionText.setEditable(false);
		this.addHideCompent(container, 40, null);

		this.addHideCompent(container, 20, null);
		Label targetPathLabel = new Label(container, SWT.NONE);
		targetPathLabel.setLayoutData(lableLayout);
		targetPathLabel.setText("Target directory:");
		targetPathText = new Text(container, SWT.BORDER);// 保存路径
		targetPathText.setLayoutData(pathLayout);
		this.addHideCompent(container, 40, null);

		this.addHideCompent(container, 20, null);

		Label serverGroupLabel = new Label(container, SWT.NONE);
		serverGroupLabel.setLayoutData(lableLayout);
		serverGroupLabel.setText("Server Group:");
		// 项目下拉列表
		projecCombo = new Combo(container, SWT.NONE);
		projecCombo.setLayoutData(new RowData(220, 20));
		projecCombo.addModifyListener(projectInfoListener);
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

		this.addHideCompent(container, 80, null);
		this.addHideCompent(container, 20, null);
		final Button uploadRadio = new Button(container, SWT.RADIO);
		uploadRadio.setText("Upload");
		this.addHideCompent(container, 100, null);

		final Button exportRadio = new Button(container, SWT.RADIO);
		exportRadio.setText("Export");

		this.addHideCompent(container, 40, null);

		this.addHideCompent(container, 20, null);
		Label savePathLable = new Label(container, SWT.NONE);
		savePathLable.setLayoutData(lableLayout);
		savePathLable.setText("Output directory:");
		outputPathText = new Text(container, SWT.BORDER);// 输出路径
		outputPathText.setLayoutData(pathLayout);
		outputPathText.addModifyListener(projectInfoListener);
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

		this.addHideCompent(container, 500, 10);
		outputPathText.setText(ConfigUtil.getSavePath());

		PathInfo preInfo = ConfigUtil.getPathInfo(this.projectName);
		if (preInfo != null && preInfo.getGroupId() != null) {// 从记录中获取路径信息
			projecCombo.setText(preInfo.getGroupId());
			targetPathText.setText(preInfo.getTargetPath());
		} else {
			projecCombo.setText("请选择服务组");
			targetPathText.setText("target/" + this.projectName);
		}

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
					getShell().setSize(500, 325);
				} else {
					getShell().setSize(500, 370);
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
		return new Point(450, 325);
	}

	@Override
	protected void cancelPressed() {
		this.close();
	}

	@Override
	protected void okPressed() {

		if (!this.sourceLocation.endsWith("\\")) {
			this.sourceLocation = this.sourceLocation + "\\";
		}
		String targetPath = this.sourceLocation + targetPathText.getText();
		targetPath = targetPath.replaceAll("/", "\\\\");
		System.out.println("targetPath    " + targetPath);
		final PathInfo pathInfo = new PathInfo();
		pathInfo.setSourceCodeSvnRevision(svnRevision);
		pathInfo.setSourceCodeSvnUrl(svnUrl);
		pathInfo.setSavePath(outputPathText.getText());
		pathInfo.setTargetPath(targetPathText.getText());
		pathInfo.setSrcPath(targetPath);
		pathInfo.setGroupId(projecCombo.getText());
		System.out.println("savePathInfo: " + JsonUtil.toJson(pathInfo));
		ConfigUtil.savePathInfo(projecCombo.getText(), pathInfo);
		ConfigUtil.saveOutputPath(outputPathText.getText());
		super.okPressed();
		this.close();

		if (uploadBtnSelected) {// 上传代码
			ConsoleHandler.info("代码编译中....");
			Job job = new UploadFileJob("crs uploading file ...", pathInfo, projectInfo, sourceLocation);
			job.schedule();
		} else {// 生成代码包

			Job job = new Job("crs comparing...") {
				@Override
				protected IStatus run(IProgressMonitor arg0) {
					try {
						MavenUtil.runInstall(sourceLocation);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						ConsoleHandler.error("编译失败!");
						return Status.OK_STATUS;
					}
					UserInfo userInfo = CrsPreferencePage.getUserInfo();
					Comparetor.doAtion(pathInfo, userInfo);
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}

	}

	public static void main(String[] args) {
		String str = "D:\\Users\\TY-Chenql\\runtime-EclipseApplication\\crs_mave_ice\\target/crs_mave_ice-bin/crs_mave_ice";
		System.out.println(str.replaceAll("/", "\\\\"));
	}
}
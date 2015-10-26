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
package com.github.ipaas.ideploy.plugin.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.github.ipaas.ideploy.plugin.bean.UserInfo;
import com.github.ipaas.ideploy.plugin.core.Comparetor;
import com.github.ipaas.ideploy.plugin.ui.popup.CompileDialog;
import com.github.ipaas.ideploy.plugin.ui.popup.LocationUtil;
import com.github.ipaas.ideploy.plugin.ui.preference.CrsPreferencePage;
import com.github.ipaas.ideploy.plugin.util.ConsoleHandler;
import com.github.ipaas.ideploy.plugin.util.CrsSvnUtil;
import com.github.ipaas.ideploy.plugin.util.MavenUtil;

public class CompileAction extends LocationUtil implements IActionDelegate, IObjectActionDelegate,
		IPropertyChangeListener {

	protected IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	protected Shell shell;
	protected ISelection currentSelection;

	/**
	 * Constructor for Action1.
	 */
	public CompileAction() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		shell = targetPart.getSite().getShell();
	}

	/**
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		ConsoleHandler.cleanConsole();
		String location = this.getLocation(action, this.currentSelection);
		String projectName = "";

		System.out.println("locaction: " + location);
		File projectFile = new File(location);

		// ConsoleHandler.cleanConsole();

		ConsoleHandler.info("获取SVN信息...");

		if (this.currentSelection instanceof IStructuredSelection) {
			IStructuredSelection treeSelection = (IStructuredSelection) this.currentSelection;
			Object cuttentSelection = treeSelection.getFirstElement();
			Project project = null;
			if ((cuttentSelection instanceof Project)) {
				project = (Project) cuttentSelection;
				projectName = project.getName();

				try {
					if (!SVNWCUtil.isWorkingCopyRoot(new File(location))) {
						ConsoleHandler.error("svn: '" + location + "' is not a working copy");
						return;
					}
					List<String> unCommitList = new ArrayList<String>();
					List<String> unUpdateList = new ArrayList<String>();
					CrsSvnUtil svnUtil = CrsSvnUtil.newInstance();
					long svnRevesion = svnUtil.checkFileSvnStatus(projectFile, unCommitList, unUpdateList);
					if (unCommitList.size() > 0) {
						ConsoleHandler.error("检测到有未提交的文件,请先同步SVN库");
						return;
					} else if (unUpdateList.size() > 0) {
						ConsoleHandler.error("检测到有未提交的文件,请先同步SVN库");
						return;
					} else {
						String svnUrl = svnUtil.getSvnPath(projectFile);
						CompileDialog dialog = new CompileDialog(shell, location, project, svnRevesion, svnUrl);
						dialog.open();
					}
				} catch (SVNException e) {
					ConsoleHandler.error("获取项目SVN信息失败：" + e.getMessage());
					return;
				} catch (InterruptedException e) {
					ConsoleHandler.error("获取项目SVN信息失败：" + e.getMessage());
					return;
				}
			} else {
				ConsoleHandler.error("svn: '" + location + "' is not a working copy");
				System.out.println(location + "  is not a project");
				return;
			}
		}

	}

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {

	}

}
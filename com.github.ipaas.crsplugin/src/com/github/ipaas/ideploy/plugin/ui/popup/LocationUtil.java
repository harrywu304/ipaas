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

package com.github.ipaas.ideploy.plugin.ui.popup;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;

import com.github.ipaas.ideploy.plugin.util.OperatingSystem;

/**
 * 
 * 获取当前选中的文件夹的路径
 * 
 * @author Chenql  
 */
public class LocationUtil {

	public LocationUtil() {
	}

	/**
	 * 获取当前选中的文件夹的路径
	 * 
	 * @param action
	 * @param selection
	 * @return
	 */
	public String getLocation(IAction action, ISelection selection) {
		String location = null;
		if (selection == null || selection.isEmpty()) {
			return location;
		}
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection treeSelection = (IStructuredSelection) selection;
			Object path = treeSelection.getFirstElement();
			IResource resource = null;
			if ((path instanceof IResource)) {
				resource = (IResource) path;
			}
			if (resource != null) {
				location = resource.getLocation().toOSString();
				if ((resource instanceof IProject)) {
					location = ((IProject) resource).getLocation().toOSString();
					if (OperatingSystem.INSTANCE.isWindows()) {
						location = ((IProject) resource).getLocation().toOSString();
					}
				} else if ((resource instanceof IFolder)) {
					location = ((IFolder) resource).getLocation().toOSString();
					if (OperatingSystem.INSTANCE.isWindows()) {
						location = ((IFolder) resource).getLocation().toOSString();
					}
				}
			}
		}
		return location;
	}
}

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.ISVNStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * SVN操作工具类
 * 
 * @author Chenql 
 */
public class CrsSvnUtil {

	public static CrsSvnUtil newInstance() {
		return new CrsSvnUtil();
	}

	public static void main(String[] args) throws SVNException, InterruptedException {
		CrsSvnUtil svnUtil = new CrsSvnUtil();

		List<String> unCommitedList = new ArrayList<String>();
		List<String> unUpdateList = new ArrayList<String>();

		System.out.println(svnUtil.checkFileSvnStatus(new File("D:/Users/TY-Chenql/workspace/test.web"),
				unCommitedList, unUpdateList));

	}

	class MySVNStatusHandler implements ISVNStatusHandler {
		private List<String> unCommitedList = null;
		private List<String> unUpdateList = null;

		public MySVNStatusHandler(List<String> changeList, List<String> unUpdateList) {
			this.unCommitedList = changeList;
			this.unUpdateList = unUpdateList;
		}

		@Override
		public void handleStatus(SVNStatus status) throws SVNException {
			System.out.println("-----------------  " + status.getFile().getAbsolutePath());
			System.out.println("getContentsStatus   " + status.getContentsStatus());
			System.out.println("getNodeStatus   " + status.getNodeStatus());
			System.out.println("getCombinedRemoteNodeAndContentsStatus              "
					+ status.getCombinedRemoteNodeAndContentsStatus());
			System.out.println();
			if (status.getContentsStatus().equals(SVNStatusType.STATUS_MODIFIED)) {
				this.unCommitedList.add(status.getFile().getAbsolutePath());
			}
			if (status.getCombinedRemoteNodeAndContentsStatus().equals(SVNStatusType.STATUS_MODIFIED)) {
				this.unUpdateList.add(status.getFile().getAbsolutePath());
			}
		}
	}

	/**
	 * 检测文件目录的SVN状态 并返回SVN的最新修订版本号
	 * 
	 * @param path
	 * @param unCommitedList
	 *            保存未提交的文件列表
	 * @param unUpdatedList
	 *            保存未更新的文件列表
	 * @return
	 * @throws SVNException
	 * @throws InterruptedException
	 */
	public long checkFileSvnStatus(File path, List<String> unCommitedList, List<String> unUpdatedList)
			throws SVNException, InterruptedException {
		if (!SVNWCUtil.isWorkingCopyRoot(path)) {
			System.out.println(" No Svn  project");
			return -1;
		}
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options);
		SVNStatusClient statusClient = clientManager.getStatusClient();
		ISVNStatusHandler handler = new MySVNStatusHandler(unCommitedList, unUpdatedList);
		long actualRevision = statusClient.doStatus(path, SVNRevision.HEAD, SVNDepth.INFINITY, true, false, false,
				false, handler, null);
		clientManager.dispose();
		return actualRevision;
	}

	public String getSvnPath(File path) throws SVNException {
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance(options);
		SVNStatusClient statusClient = clientManager.getStatusClient();
		SVNStatus svnStatus = statusClient.doStatus(path, true);
		clientManager.dispose();
		return svnStatus.getURL().toDecodedString();
	}
}

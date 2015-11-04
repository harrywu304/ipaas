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

package com.github.ipaas.ideploy.agent;

import org.apache.commons.io.FilenameUtils;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;

/**
 * SVN操作工具类
 * 
 * @author wudg
 */

public class CrsWebSvnUtil {


	private SVNRepository repos;
	private SVNRepository nodeKindRepos;

	public static CrsWebSvnUtil newInstance() {
		return new CrsWebSvnUtil();
	}

	/**
	 * 获取SVNClientManager对象
	 * 
	 * @return
	 */
	private SVNClientManager getClientManager() {
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		SVNClientManager clientManager = SVNClientManager.newInstance((DefaultSVNOptions) options,
				Constants.SVN_NAME, Constants.SVN_PASS);
		return clientManager;
	}

	/**
	 * 获取库对象
	 *
	 * @param url
	 *            库的根路径地址
	 * @return
	 * @throws SVNException
	 */
	private SVNRepository getRepository(String url) throws SVNException {
		if (repos == null) {
			SVNClientManager svnClientManager = getClientManager();
			repos = svnClientManager.createRepository(SVNURL.parseURIEncoded(url), true);
		}
		return repos;
	}



	/**
	 * 获取库对象
	 *
	 * @param url
	 *            库的根路径地址
	 * @return
	 * @throws SVNException
	 */
	public SVNRepository getNodeKindRepos(String url) throws SVNException {
		if (nodeKindRepos == null) {
			ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
			SVNClientManager svnClientManager = SVNClientManager.newInstance((DefaultSVNOptions) options,
					Constants.SVN_NAME, Constants.SVN_PASS);
			nodeKindRepos = svnClientManager.createRepository(SVNURL.parseURIEncoded(url), true);
		}
		return nodeKindRepos;
	}

	/**
	 * 获取CommitEditor对象
	 *
	 * @return
	 * @throws SVNException
	 */
	private ISVNEditor getEditor() throws SVNException {
		SVNRepository repos = getRepository(Constants.CRS_REPOS_ROOT);
		ISVNEditor editor = repos.getCommitEditor("amm code release", null, true, null);

		return editor;
	}

	/**
	 * 指定路径的节点是否存在
	 *
	 * @param path
	 * @return
	 * @throws SVNException
	 */
	public boolean isPathExist(String path) throws SVNException {
		SVNNodeKind nodeKind = this.getSvnNodeKind(path);
		if (nodeKind == SVNNodeKind.NONE) {
			return false;
		}
		return true;
	}

	public SVNNodeKind getSvnNodeKind(String path) throws SVNException {
		long headVerNum = SVNRevision.HEAD.getNumber();
		SVNRepository repos = getNodeKindRepos(Constants.CRS_REPOS_ROOT);
		SVNNodeKind nodeKind = repos.checkPath(path, headVerNum);
		repos.closeSession();
		return nodeKind;
	}


	/**
	 * 删除指定路径的节点
	 * 
	 * @param targetPath
	 * @throws SVNException
	 */
	public void delete(String targetPath) throws Exception {
		if (!isPathExist(targetPath)) {
			return;
		}
		long headVerNum = SVNRevision.HEAD.getNumber();
		String parentPath = FilenameUtils.getFullPathNoEndSeparator(targetPath);
		ISVNEditor editor = getEditor();
		try {
			editor.openRoot(headVerNum);
			editor.openDir(parentPath, headVerNum);
			editor.deleteEntry(targetPath, headVerNum);
			editor.closeDir();
		} catch (Exception e) {
			editor.abortEdit();
			throw e;
		} finally {
			editor.closeEdit();
		}
	}

	/**
	 * 将本地路径localPath上的内容导入到SVN的destPath路径下
	 * 
	 * @param localPath
	 *            本地绝对路径
	 * @param destPath
	 *            远程路径(相对与repos根路径)
	 * @throws Exception
	 */
	public void importDir(String localPath, String destPath) throws Exception {
		File path = new File(localPath);
		SVNURL dstURL = SVNURL.parseURIEncoded(Constants.CRS_REPOS_ROOT + destPath);
		getClientManager().getCommitClient().doImport(path, dstURL, "add all code file", null, true, false,
				SVNDepth.INFINITY);
	}

}

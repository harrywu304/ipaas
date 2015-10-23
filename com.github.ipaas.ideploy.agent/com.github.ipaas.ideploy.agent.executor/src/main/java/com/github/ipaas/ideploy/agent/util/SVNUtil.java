/**
 * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
 */

package com.github.ipaas.ideploy.agent.util;
  
import java.io.ByteArrayOutputStream;
import java.io.File;  
import java.io.FileOutputStream; 
import java.util.Collection; 
import java.util.HashMap; 
import java.util.LinkedList; 
import java.util.List;
import java.util.Map; 

import org.apache.commons.io.FileUtils; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;  
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;  
import org.tmatesoft.svn.core.io.SVNRepository; 
import org.tmatesoft.svn.core.wc.ISVNDiffStatusHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager; 
import org.tmatesoft.svn.core.wc.SVNDiffStatus;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.github.ipaas.ideploy.agent.Constants;

/**
 * SVN工具
 * 
 * @author wudg
 */

public class SVNUtil {
	
	private static Logger logger = LoggerFactory.getLogger(SVNUtil.class);
	
	/**
	 * 从下载文件到本地 
	 * @param repository  svn repos对象
	 * @param remotePath  下载目标路径，相对与svn根路径
	 * @param savePath    保存位置
	 * @param revision    版本
	 * @throws Exception
	 */
	private static void getFile(SVNRepository repository,String remotePath,File savePath,long revision) throws Exception{
		//删除已有
		FileUtils.deleteQuietly(savePath);
		
		//创建上级目录
		savePath.getParentFile().mkdirs();
		
		//创建目标文件
		savePath.createNewFile();
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream( );
		FileOutputStream fos=new FileOutputStream(savePath);
		try{
			repository.getFile( remotePath ,revision , null , baos); 
			baos.writeTo(fos);
		}finally{
			if(fos!=null){
				fos.close();
			} 
			if(baos!=null){
				baos.close();
			}
		} 
	} 
	/**
	 * 获取svn日志
	 * @param repository  repos对象
	 * @param path   svn目录(相对与repos根目录)
	 * @param datumRevision    基准版本
	 * @param expectRevision  目标版本
	 * @return 日志记录集合
	 * @throws Exception
	 */
	private static Collection<SVNLog> getLog(SVNRepository repository,String path,long datumRevision,long expectRevision) throws Exception{
		Collection<SVNLogEntry> coll=new LinkedList<SVNLogEntry>(); 
		repository.log(new String[]{path} , coll, datumRevision+1, expectRevision, true, true); 
		Map<String,SVNLog> logMap=new HashMap<String,SVNLog>(); 
		for(SVNLogEntry e:coll){
			Map<String,SVNLogEntryPath> map=e.getChangedPaths();
			if(map!=null){
				for(SVNLogEntryPath p:map.values()){ 
					String sPath=p.getPath(); 
					if(sPath.startsWith(path)){
						sPath=sPath.substring(path.length());
					}
					//操作类型
					String opt=null;
					
					if(p.getType()==SVNLogEntryPath.TYPE_DELETED){
						opt="-";
					}
					
					if(p.getType()==SVNLogEntryPath.TYPE_ADDED||p.getType()==SVNLogEntryPath.TYPE_MODIFIED||p.getType()==SVNLogEntryPath.TYPE_REPLACED){
						opt="+";
					}
					
					int entryKind=0;
					
					if(p.getKind()==SVNNodeKind.DIR){
						entryKind=SVNLog.ENTRY_DIR;
					}
					 
					if(p.getKind()==SVNNodeKind.FILE){
						entryKind=SVNLog.ENTRY_FILE;
					} 
					
					logMap.put(sPath, new SVNLog(e.getRevision(),sPath,opt,entryKind));
				}  
			}
		}
		return logMap.values();
	}

	/**
	 * 根据日志获取现在"添加、修改"的项目
	 * @param repository
	 * @param remoteRoot
	 * @param logs
	 * @param savePath
	 * @throws Exception
	 */
	private static void getDelta4Add(SVNRepository repository,String remoteRoot,Collection<SVNLog> logs,String savePath) throws Exception{
		SVNNodeKind nodeKind =null;
		if(logs==null||logs.isEmpty()){
			return;
		}
		 
		File localRoot=new File(savePath);
		
		//清空或创建目录
		if(localRoot.exists()){
			FileUtils.cleanDirectory(localRoot);
		}else{
			localRoot.mkdirs();
		} 
		
		String remotePath=null;
		String localPath=null;
		File localEntry=null;
		for(SVNLog l:logs){
			if(l.getType()==SVNLog.TYPE_DEL){
				continue;
			}
			remotePath=remoteRoot+l.getPath();
			localPath=savePath+l.getPath();
			nodeKind=repository.checkPath(remotePath, l.getRevision()); 
			if(nodeKind==SVNNodeKind.DIR){
				localEntry=new File(localPath);
				if(!localEntry.exists()){
					localEntry.mkdirs();
				} 
			}
			
			if(nodeKind==SVNNodeKind.FILE){
				localEntry=new File(localPath);
				getFile(repository, remotePath, localEntry, l.getRevision());
			} 
		} 
	}
	
	
	/**
	 * 将日志写入文件
	 * @param logs
	 * @param destFile
	 */
	private static void writeLogToFile(Collection<SVNLog> logs,File destFile) throws Exception{
		if(logs==null||logs.isEmpty()){
			return;
		}
		if(!destFile.exists()){
			destFile.createNewFile();
		}  
		List<String> contents=new LinkedList<String>();
		for(SVNLog e:logs){
			contents.add(e.toSimpleString());
		}
		
		FileUtils.writeLines(destFile, contents, false);
	}
	
	
	/**
	 * 从svn上获取变更
	 * @param path  svn目录
	 * @param datumRevision   基准版本
	 * @param expectRevision  目标版本
	 * @param localPath      保存路径
	 * @throws Exception
	 */
	public static void getDelta(String path,long datumRevision,long expectRevision,String localPath)  throws Exception{
		DefaultSVNOptions options =SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(Constants.CRS_REPOS_USER, Constants.CRS_REPOS_PWD);
		SVNClientManager svnClientManager=null;
		try{
			svnClientManager=SVNClientManager.newInstance(options, authManager); 
			 
			SVNRepository repository=svnClientManager.createRepository(SVNURL.parseURIEncoded(Constants.CRS_REPOS), true); 
			 
			Collection<SVNLog> logs=getLog(repository, path, datumRevision, expectRevision);
			
			//创建本地目录
			File localDict=new File(localPath);
			if(!localDict.exists()){
				localDict.mkdirs(); 
			}else{
				FileUtils.cleanDirectory(localDict);
			}
			 
			// 获取添加、替换项
			getDelta4Add(repository, path, logs,localPath+Constants.UPDPKG_CODEDIR );
			
			//将日志写入文件
			writeLogToFile(logs, new File(localPath+Constants.UPDPKG_UPDTXT));
			
		}finally{
			if(svnClientManager!=null){
				svnClientManager.dispose();
			}
		} 
	}
	  
	
	/**
	 * 将svn上removePath路径下的内容导出到本地的localPath路径下  支持文件下载及目录导出
	 * @param svnClientManager svn客户端操作对象
	 * @param remotePath  svn远程目录
	 * @param revision  目标版本 
	 * @param localPath    本地保存目录
	 
	 */
	public static void export(SVNClientManager svnClientManager,String remotePath,long revision,String localPath) throws Exception{  
		File localF=new File(localPath);  
		if(!localF.exists()){
			localF.mkdirs();
		}else{
			FileUtils.cleanDirectory(localF);
		}
		
		SVNUpdateClient svnUpdateClient=svnClientManager.getUpdateClient();
		svnUpdateClient.doExport(SVNURL.parseURIEncoded(Constants.CRS_REPOS+remotePath), new File(localPath), SVNRevision.HEAD, SVNRevision.create(revision), null, true, SVNDepth.INFINITY);
	}
	
	
	/**
	 * 获取 path2/revision2  相对于path1/revision1的变更日志  
	 * @param svnClientManager svn客服端管理
	 * @param path1  
	 * @param revision1
	 * @param path2
	 * @param revision2
	 * @return 变更列表
	 * @throws Exception
	 */
	public static Collection<SVNLog>  diff(SVNClientManager svnClientManager,final String path1,final long revision1,final String path2,final long revision2) throws Exception{
		
		final Collection<SVNLog> diffs=new LinkedList<SVNLog>();
		
		SVNURL url1=SVNURL.parseURIEncoded(Constants.CRS_REPOS+path1);
		SVNURL url2=SVNURL.parseURIEncoded(Constants.CRS_REPOS+path2);
		
		svnClientManager.getDiffClient().doDiffStatus(url1, SVNRevision.HEAD,url2, SVNRevision.HEAD, SVNDepth.UNKNOWN, false, new ISVNDiffStatusHandler(){

			@Override
			public void handleDiffStatus(SVNDiffStatus diffStatus) throws SVNException {
				 
				boolean logging=false;
				SVNLog svnLog=new SVNLog();
				SVNStatusType svnStatusType=diffStatus.getModificationType();
				if(svnStatusType==SVNStatusType .CHANGED||svnStatusType==SVNStatusType.STATUS_ADDED||svnStatusType==SVNStatusType.STATUS_MODIFIED){
					svnLog.setType(SVNLog.TYPE_ADD);
					svnLog.setRevision(revision2);
					logging=true;
				}
				
				if(svnStatusType==SVNStatusType.STATUS_DELETED){
					svnLog.setType(SVNLog.TYPE_DEL);
					logging=true;
				} 
				
				if(logging){
					if(diffStatus.getKind()==SVNNodeKind.DIR){
						svnLog.setEntryKind(SVNLog.ENTRY_DIR);
					}
					if(diffStatus.getKind()==SVNNodeKind.FILE){
						svnLog.setEntryKind(SVNLog.ENTRY_FILE);
					} 
					svnLog.setPath("/"+diffStatus.getPath());
					diffs.add(svnLog);
				} 
			} 
		}); 
		
		return diffs; 
	}
	
	
	 
	/**
	 * 
	 * 获取path2相对与path1的变更
	 * @param path1   
	 * @param revision1  path1版本号
	 * @param path2
	 * @param revision2  path2版本号
	 * @param localPath   保存变更内容的临时路径路径
	 * @param deployPath    正式部署代码的路径
	 * @throws Exception
	 */
	public static void getDelta(final String path1,final long revision1,final String path2,final long revision2,String localPath) throws Exception{
		DefaultSVNOptions options =SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(Constants.CRS_REPOS_USER, Constants.CRS_REPOS_PWD);
		SVNClientManager svnClientManager=null;
		try{
			//创建本地目录
			File localDict=new File(localPath);
			if(!localDict.exists()){
				localDict.mkdirs(); 
			}else{
				FileUtils.cleanDirectory(localDict);
			}
			
			svnClientManager=SVNClientManager.newInstance(options, authManager);
			
			SVNRepository repository=svnClientManager.createRepository(SVNURL.parseURIEncoded(Constants.CRS_REPOS), true);
			
			Collection<SVNLog> logs=null;
			
			boolean update4All=false;
			  
			
			//没有正在使用的版本或前后版本相同
			if(path1==null||path1.equals("")||path1.equals(path2)){
				update4All=true;
			}
			
			if(update4All){
				 //更新所有文件
				 export(svnClientManager, path2, revision2, localPath+Constants.UPDPKG_CODEDIR);
				 logs=new LinkedList<SVNLog>();
				 logs.add(new SVNLog4All());
			}else{
				//增量获取俩个版本的变更
				logs=diff(svnClientManager, path1, revision1, path2, revision2); 
				
				if(logs!=null&&!logs.isEmpty()){
					// 获取添加、替换项
					getDelta4Add(repository, path2, logs,localPath+Constants.UPDPKG_CODEDIR );
				}else{
					//没有变更项目时 已更新全部处理
					export(svnClientManager, path2, revision2, localPath+Constants.UPDPKG_CODEDIR);
					logs=new LinkedList<SVNLog>();
					logs.add(new SVNLog4All());
				}
			}  
			
			//将日志写入文件
			writeLogToFile(logs, new File(localPath+Constants.UPDPKG_UPDTXT)); 
		}finally{
			if(svnClientManager!=null){
				svnClientManager.dispose();
			}
		}	 
	} 
	
	
	public static void getDeta4UpdateAll(final String path,final long revision,String localPath) throws Exception{
		
		DefaultSVNOptions options =SVNWCUtil.createDefaultOptions(true);
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(Constants.CRS_REPOS_USER, Constants.CRS_REPOS_PWD);
		SVNClientManager svnClientManager=null;
		
		try{
			//创建本地目录
			File localDict=new File(localPath);
			if(!localDict.exists()){
				localDict.mkdirs(); 
			}else{
				FileUtils.cleanDirectory(localDict);
			}
			
			svnClientManager=SVNClientManager.newInstance(options, authManager);
			 
			Collection<SVNLog> logs=new LinkedList<SVNLog>();
			logs.add(new SVNLog4All());
			
			//更新所有文件
			export(svnClientManager, path, revision, localPath+Constants.UPDPKG_CODEDIR);
			
			//将日志写入文件
			writeLogToFile(logs, new File(localPath+Constants.UPDPKG_UPDTXT));
		}finally{
			if(svnClientManager!=null){
				svnClientManager.dispose();
			}
		}
	} 
}

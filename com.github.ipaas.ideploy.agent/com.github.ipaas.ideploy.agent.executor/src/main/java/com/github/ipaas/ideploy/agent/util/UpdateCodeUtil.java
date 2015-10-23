package com.github.ipaas.ideploy.agent.util;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;

import com.github.ipaas.ideploy.agent.Constants;

public class UpdateCodeUtil {
	/**
	 * 更新代码
	 * @param updatePath  新代码位置
	 * @param targetPath
	 * @throws Exception
	 */
	public static   void updateCode(String updatePath,String targetPath) throws Exception{
		String updTxt = updatePath + Constants.UPDPKG_UPDTXT;

		String codePath = updatePath +Constants.UPDPKG_CODEDIR;
 
		LineIterator it = FileUtils.lineIterator(new File(updTxt), "UTF-8");
		 
		try {
			String line = null;
			String p=null;
			while (it.hasNext()) {
				line = it.nextLine();
				
				//全量更新标志
				if(line.equals(SVNLog4All.KEY)){
					File src=new File(codePath);
					File dist=new File(targetPath);
					if(!dist.exists()){
						dist.mkdirs();
					}
					FileUtils.cleanDirectory(dist);
					FileUtils.copyDirectory(src, dist);
					return;
				}
				
				//更新指定文件
				if (line.startsWith(SVNLog.TYPE_ADD)) {
					p=StringUtils.trim(StringUtils.removeStart(line, SVNLog.TYPE_ADD)); 
					File codeNode=new File(codePath + p);
					File targetNode=new File(targetPath+p);
					if(codeNode.isDirectory()&&!targetNode.exists()){
						targetNode.mkdirs();
					}else{
						if(codeNode.isFile()){
							FileUtils.copyFile(codeNode,targetNode);
						}else{
							FileUtils.copyDirectory(codeNode, targetNode);
						}
					}
				} 
				//删除指定文件
				if (line.startsWith(SVNLog.TYPE_DEL)) { 
					p=StringUtils.trim(StringUtils.removeStart(line, SVNLog.TYPE_DEL));
					FileUtils.deleteQuietly(new File(targetPath+p));
				} 
			}
		} finally {
			it.close();
		}
	} 
}

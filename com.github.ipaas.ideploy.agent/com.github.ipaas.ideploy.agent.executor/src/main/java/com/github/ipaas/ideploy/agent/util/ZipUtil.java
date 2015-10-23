/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

/** 
 * 类或接口作用描述 
 * @author  wudg
 */

public class ZipUtil {
	
	/**
	 * 将压缩文件解压到当前目录
	 * @param zipFile
	 * @throws Exception
	 */
	public static void unZip(String zipFile) throws Exception{
		String targetDir=new File(zipFile).getParent();
		unZip(zipFile,targetDir);
	}
	
	/**
	 * 解压文件
	 * @param srcFile  压缩文件位置
	 * @param targetDir 解压目标目录
	 * @throws Exception
	 */
	public static void unZip(String zipFile,String targetDir) throws Exception{ 
        ZipFile zipfile = new ZipFile(zipFile); 
        try { 
            Enumeration<ZipEntry> entries = zipfile.getEntries(); 
            if (entries == null || !entries.hasMoreElements()) { 
                return; 
            } 
            // 创建目标文件目录 
            FileUtils.forceMkdir(new File(targetDir));
 
            // 遍历所有文件

            while (entries.hasMoreElements()) { 
                ZipEntry zipEntry = entries.nextElement(); 
                String fname = zipEntry.getName();
                
                // 创建目录

                if (zipEntry.isDirectory()) { 
                    String fpath = FilenameUtils.normalize(targetDir + "/" + fname); 
                    FileUtils.forceMkdir(new File(fpath)); 
                    continue;

                } 
                // 复制文件目录

                if (StringUtils.contains(fname, "/")) { 
                    String tpath = StringUtils.substringBeforeLast(fname, "/"); 
                    String fpath = FilenameUtils.normalize(targetDir + "/" + tpath); 
                    FileUtils.forceMkdir(new File(fpath)); 
                } 
                // 复制文件内容 
                InputStream input = null; 
                OutputStream output = null;

                try { 
                    input = zipfile.getInputStream(zipEntry); 
                    String file = FilenameUtils.normalize(targetDir + "/" + fname); 
                    output = new FileOutputStream(file); 
                    IOUtils.copy(input, output); 
                } finally { 
                    IOUtils.closeQuietly(input); 
                    IOUtils.closeQuietly(output); 
                } 
            } 
        } finally { 
            ZipFile.closeQuietly(zipfile); 
        }

		
	} 
	
}

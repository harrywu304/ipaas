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
package com.github.ipaas.ifw.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

// 将一个字符串按照zip方式压缩和解压缩
public class ZipUtil {

	// 压缩
	public static String compress(String str){
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = null;
		GZIPOutputStream gzip = null;
		try{
			out=new ByteArrayOutputStream(); 
			gzip=new GZIPOutputStream(out);
			gzip.write(str.getBytes());
			gzip.close();
			return out.toString("ISO-8859-1");
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(out!=null){
				try{
					out.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			if(gzip!=null){
				try{
					gzip.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	// 解压缩
	public static String deCompress(String str) {
		if (str == null || str.length() == 0) {
			return str;
		}
		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		GZIPInputStream gunZip = null;
		try{
			out=new ByteArrayOutputStream();
			in=new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
			gunZip=new GZIPInputStream(in);;
			byte[] buffer = new byte[256];
			int n;
			while ((n = gunZip.read(buffer)) >= 0) {
				out.write(buffer, 0, n);
			}
			return out.toString();
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			if(out!=null){
				try{
					out.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
			if(in!=null){ 
				try{
					in.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}	
			if(gunZip!=null){
				try{
					gunZip.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			} 
		}
		return null; 
	}
}
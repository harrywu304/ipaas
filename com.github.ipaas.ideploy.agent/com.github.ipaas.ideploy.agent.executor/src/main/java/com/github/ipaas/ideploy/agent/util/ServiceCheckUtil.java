/**
* Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *    *      http://www.apache.org/licenses/LICENSE-2.0  *    * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License..
*/

package com.github.ipaas.ideploy.agent.util;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.management.remote.JMXConnector;
import javax.management.remote.rmi.RMIConnector;
import javax.management.remote.rmi.RMIServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ifw.util.CloseUtil;


/** 
 * 类或接口作用描述 
 * @author  wudg
 */

public class ServiceCheckUtil {
	
	private static Logger logger = LoggerFactory
			.getLogger(ServiceCheckUtil.class);
	
	/**
	 * 检查时间间隔  20s
	 */
	public static long CHECK_INTERVAL=5000;
	
	
	
	/**
	 * 通过jmx检查服务是否正在运行
	 * @param ip  ip
	 * @param jmxPort jmx端口
	 * @return
	 */
	public static boolean isRunning(String ip,String jmxPort){
		boolean flag=false;
		JMXConnector jmxConnector=null;
		try{
			
			Registry registry = LocateRegistry.getRegistry(ip,
					Integer.parseInt(jmxPort));
			RMIServer stub = (RMIServer) registry.lookup("jmxrmi");
			jmxConnector = new RMIConnector(stub, null);
			jmxConnector.connect();
			flag=true;
		}catch(Throwable e){
			flag=false;
		}finally{
			 CloseUtil.closeSilently(jmxConnector);
		}
		return flag;
	}
	
	/**
	 * 轮询检查服务是否启动
	 * @param ip  ip
	 * @param jmxPort jmx端口
	 * @param deplay  检测时长 
	 * @return
	 */
	public static boolean waitForStart(String ip,String jmxPort,long deplay){
		boolean running=false;
		long times=deplay/CHECK_INTERVAL;
		if((deplay%CHECK_INTERVAL)!=0){
			times+=1;
		}
		long i=0;
		while(i<times){ 
			try {
				Thread.sleep(CHECK_INTERVAL);
			} catch (InterruptedException e) {}
			running=isRunning(ip, jmxPort);
			logger.info("running="+running);
			if(running){
				break;
			}
			++i;
			
		}
		return running;
	}
	
	
	/**
	 * 轮询检查服务是否已停止
	 * @param ip  ip
	 * @param jmxPort jmx端口
	 * @param deplay  检测时长 
	 * @return
	 */
	public static boolean waitForShutdown(String ip,String jmxPort,long deplay){
		boolean isShutdown=false;
		boolean running=true;
		long times=deplay/CHECK_INTERVAL;
		if((deplay%CHECK_INTERVAL)!=0){
			times+=1;
		}
		long i=0;
		while(i<times){ 
			try {
				Thread.sleep(CHECK_INTERVAL);
			} catch (InterruptedException e) {} 
			
			running=isRunning(ip, jmxPort);
			logger.info("running="+running);
			if(!running){
				break;
			}
			++i;
		}
		isShutdown=!running;
		return isShutdown;
	}
	

}

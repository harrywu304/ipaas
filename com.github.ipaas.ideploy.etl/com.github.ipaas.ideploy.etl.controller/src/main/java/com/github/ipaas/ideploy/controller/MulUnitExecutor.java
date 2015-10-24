/**
* Licensed under the Apache License, Version 2.0 (the "License");  *
 you may not use this file except in compliance with the License.  *
 You may obtain a copy of the License at  *
  *
      http://www.apache.org/licenses/LICENSE-2.0  *
  *
 Unless required by applicable law or agreed to in writing, software  *
 distributed under the License is distributed on an "AS IS" BASIS,  *
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
 See the License for the specific language governing permissions and  *
 limitations under the License. */

package com.github.ipaas.ideploy.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
  

/** 
 * 用于并行执行多个单元并获取结果集
 * @author  wudg
 */

public class MulUnitExecutor {
	
	private static Logger logger = LoggerFactory.getLogger(MulUnitExecutor.class);
	
	public static int INNER_THREAD_POOL_SIZE=10;
	
	public static MulUnitExecutor newInstance(){
		return new MulUnitExecutor();
	} 
	
	private Map<String,Callable<? extends Result>> taskMap;
	
	private  Map<String,Result> resultMap;
	
	
	private List<String> succTaskList;
	
	private List<String> failTaskList;
	
	 
	
	private MulUnitExecutor(){
		this.taskMap=new LinkedHashMap<String,Callable<? extends Result>>();
		this.resultMap=new HashMap<String,Result>();
		this.succTaskList=new LinkedList<String>();
		this.failTaskList=new LinkedList<String>();
		 
	}
	
	/**
	 * 根据执行结果判断任务是否执行成功
	 * @param result
	 * @return
	 */
	private boolean isSucc(Result result){
		if(result.getResultCode()==Result.SUCC){
			return true;
		}else{
			return false; 
		} 
	}
	
	
	
	 
	
	/**
	 * 添加proc
	 * @param key
	 * @param proc
	 */
	public  void addUnit(String key,Callable<? extends Result> task){
		taskMap.put(key, task);
	}
	
	/**
	 * 执行任务
	 */
	public void execute(){
		if(taskMap.isEmpty()){
			return;
		}
		
		ExecutorService executor=Executors.newFixedThreadPool(taskMap.size());
		
		try{
			Map<String,Future<? extends Result>> futureMap=new HashMap<String,Future<? extends Result>>();
			Future<? extends Result> f=null;
			
			Iterator<String> it=null;
			it=taskMap.keySet().iterator();
			String key=null;
			while(it.hasNext()){
				key=it.next();
				f=executor.submit(taskMap.get(key));
				futureMap.put(key,f);
			}
			
			Result r=null;
			boolean noExecept=true;
			it=futureMap.keySet().iterator();
			while(it.hasNext()){
				key=it.next();
				f=futureMap.get(key);
				try{
					r=f.get();
					noExecept=true;
				}catch(Exception e){
					logger.error(e.getMessage(), e);
					noExecept=false;
				}
				
				resultMap.put(key, r);
				
				if(noExecept&&isSucc(r)){
					succTaskList.add(key);
				}else{
					failTaskList.add(key);
				}
			}
		}catch(Exception e){
			logger.error("多任务执行出错", e);
		}finally{
			if(executor!=null){
				executor.shutdown();
			}
		}
		
		
		
		
		
	}
	
	/**
	 * 清除缓存
	 */
	public void clear(){
		this.taskMap.clear();
		this.resultMap.clear();
		this.succTaskList.clear();
		this.failTaskList.clear();
	}
	
	/**
	 * 获取成功项目
	 * @return
	 */
	public List<String> getSuccProcList(){
		return succTaskList;
	}
	
	/**
	 * 获取失败项目
	 * @return
	 */
	public List<String> getFailProcList(){
		return failTaskList;
	}
	
	/**
	 * 获取所有结果
	 * @return
	 */
	public Map<String,Result> getResultMap(){
		return resultMap;
	}

}

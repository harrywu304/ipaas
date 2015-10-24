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

package com.github.ipaas.ideploy.controller.proc;


import java.util.HashMap;
import java.util.Map;

/** 
 * 过程定义 
 * @author  wudg
 */

public class ProcMeta {
	
	/**
	 * 未开始 
	 */
	public final static String  NO_STARTED ="NO_STARTED";
	
	/**
	 * 已结束  
	 */
	public final static String  FINISHED ="FINISHED";
	
	/**
	 * 下载代码
	 */
	public final static String  DOWMLOAD_NEWCODE="DOWMLOAD_NEWCODE";
	
	
	/**
	 * 正等待用户确认第1台
	 */
	public final static String  WAITFOR_PREV_FISRT="WAITFOR_PREV_FISRT";
	
	
	/**
	 * 将服务从负载均衡中删除
	 */
	public final static String  DEL_FROM_LB="DEL_FROM_LB";
	
	/**
	 * 停止服务器
	 */
	public final static String  STOP_SERVER="STOP_SERVER";
	
	/**
	 * 备份代码
	 */
	public final static String  BACKUP_CODE="BACKUP_CODE";
	 
	/**
	 * 更新代码
	 */
	public final static String  UPDATE_CODE="UPDATE_CODE";
	
	/**
	 * 启动服务器
	 */
	public final static String  START_SERVER="START_SERVER";
	
	/**
	 * 将服务添加至负载均衡
	 */
	public final static String  ADD_TO_LB="ADD_TO_LB";
	
	
	
	/**
	 * 第一台预览后继续
	 */
	public final static String CONTI4FIRSTONE="CONTI_4_FIRSTONE";
	
	
	
	
	/**
	 * 开始回退
	 */
	public final static String START_TO_ROLLBACK="START_TO_ROLLBACK";
	
	 
	/**
	 * 回滚代码
	 */
	public final static String ROLLBACK_CODE="ROLLBACK_CODE";
	
	
	/**
	 * 回退完成
	 */
	public final static String ROLLBACK_FINISH="ROLLBACK_FINISH";
	 
	
	/**
	 * 启动文件系统监控(代码异动)
	 */
	public static String START_FILESYS_MON="START_FILESYS_MON";
	
	/**
	 * 停止文件系统监控(代码异动)
	 */
	public static String STOP_FILESYS_MON="STOP_FILESYS_MON";

	 
	/**
	 * 标识
	 */
	private String key;
	
	/**
	 * 描述
	 */
	private String descr;
	
	/**
	 * 类名
	 */
	private String cls;

	
	public ProcMeta(){}
	
	
	public ProcMeta(String key, String descr, String cls) {
		this.key = key;
		this.descr = descr;
		this.cls = cls;
	}



	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	 
	public String getDescr() {
		return descr;
	}

	public void setDescr(String descr) {
		this.descr = descr;
	}

	public String getCls() {
		return cls;
	}

	public void setCls(String cls) {
		this.cls = cls;
	}
	
	public final static Map<String,ProcMeta> PROC_META_MAP=new HashMap<String,ProcMeta>();
	
	public final static String PROC_CLS_PACKAGE="com.github.ipaas.ideploy.controller.proc.";
	
	static{
		
		PROC_META_MAP.put(DOWMLOAD_NEWCODE, new ProcMeta(DOWMLOAD_NEWCODE,"下载代码",PROC_CLS_PACKAGE+"DownLoadCodeProc"));
		
		PROC_META_MAP.put(DEL_FROM_LB, new ProcMeta(DEL_FROM_LB,"将服务从均衡服务中剔除",PROC_CLS_PACKAGE+"DelFromLbProc"));
		
		PROC_META_MAP.put(STOP_SERVER, new ProcMeta(STOP_SERVER,"停止服务器",PROC_CLS_PACKAGE+"StopServProc"));
		
		PROC_META_MAP.put(BACKUP_CODE, new ProcMeta(BACKUP_CODE,"备份旧代码",PROC_CLS_PACKAGE+"BackupCodeProc"));
		 
		PROC_META_MAP.put(UPDATE_CODE,  new ProcMeta(UPDATE_CODE,"更新代码到部署目录",PROC_CLS_PACKAGE+"UpdateCodeProc"));
		
		PROC_META_MAP.put(START_SERVER,new ProcMeta(START_SERVER,"启动服务器",PROC_CLS_PACKAGE+"StartServProc"));
		
		PROC_META_MAP.put(ADD_TO_LB, new ProcMeta(ADD_TO_LB,"将服务加入负载均衡",PROC_CLS_PACKAGE+"AddToLbProc"));
		
		PROC_META_MAP.put(WAITFOR_PREV_FISRT, new ProcMeta(WAITFOR_PREV_FISRT,"等待用户预览",PROC_CLS_PACKAGE+"AwaitUserCmd4FirstOneProc"));
		
		PROC_META_MAP.put(FINISHED, new ProcMeta(FINISHED,"完成安装流程",PROC_CLS_PACKAGE+"TheEndProc"));
		
		PROC_META_MAP.put(CONTI4FIRSTONE, new ProcMeta(CONTI4FIRSTONE,"用户预览",PROC_CLS_PACKAGE+"Conti4FirstOne"));
		
		PROC_META_MAP.put(START_TO_ROLLBACK, new ProcMeta(START_TO_ROLLBACK,"回退流程",PROC_CLS_PACKAGE+"StartToRollback"));
		
		PROC_META_MAP.put(ROLLBACK_CODE, new ProcMeta(ROLLBACK_CODE,"回滚代码",PROC_CLS_PACKAGE+"RollbackCodeProc"));
		
		PROC_META_MAP.put(ROLLBACK_FINISH, new ProcMeta(ROLLBACK_FINISH,"完成回退流程",PROC_CLS_PACKAGE+"TheEndProc"));
		
		
		PROC_META_MAP.put(START_FILESYS_MON, new ProcMeta(START_FILESYS_MON,"启动部署目录监控",PROC_CLS_PACKAGE+"StartFileSysMonProc"));
		
		PROC_META_MAP.put(STOP_FILESYS_MON, new ProcMeta(STOP_FILESYS_MON,"停止部署目录监控",PROC_CLS_PACKAGE+"StopFileSysMonProc"));
	 
	}

}

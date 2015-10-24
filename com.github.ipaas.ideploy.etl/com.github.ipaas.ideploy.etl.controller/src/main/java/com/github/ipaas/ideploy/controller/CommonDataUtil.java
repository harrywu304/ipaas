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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ipaas.ideploy.controller.proc.ProcMeta;
import com.github.ipaas.ifw.core.service.ServiceFactory;
import com.github.ipaas.ifw.jdbc.DataAccessServiceManager;
import com.github.ipaas.ifw.jdbc.DbAccessResponseRow;
import com.github.ipaas.ifw.jdbc.DbAccessService;
import com.github.ipaas.ifw.util.SqlUtil;

/**
 * 数据库操作类
 * 
 * @author wudg
 */

public class CommonDataUtil {

	private static Logger logger = LoggerFactory.getLogger(CommonDataUtil.class);

	public static AgentDeloyStatus getAgentDeloyStatus(String deployFlowId, String agent) {

		AgentDeloyStatus agentDeloyStatus = new AgentDeloyStatus();
		agentDeloyStatus.setDeployFlowId(deployFlowId);
		agentDeloyStatus.setAgentIp(agent);
		agentDeloyStatus.setProc(ProcMeta.NO_STARTED);
		agentDeloyStatus.setStatus(AgentDeloyStatus.UNDERWAY);

		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		String sql = SqlUtil
				.getSql("SELECT proc,status  FROM   crs_agent_deploy_evt  where deploy_flow_id=? and agent_ip=?  order by evt_id  desc limit 1 ",
						deployFlowId, agent);

		Iterator<DbAccessResponseRow> rowIt = dbas.executeQuery(sql).iterator();

		if (rowIt.hasNext()) {
			DbAccessResponseRow row = rowIt.next();
			agentDeloyStatus.setProc(row.getString("proc"));
			agentDeloyStatus.setStatus(row.getInt("status"));
		}
		return agentDeloyStatus;
	}

	/**
	 * 获取代码部署的基本信息 如部署路径、启停服务命令、负载均衡参数、服务组中包含的服务器(ip)列表等
	 * 
	 * @param deployFlowId
	 *            发布(流程)id
	 * @return 返回相关信息
	 */
	public static RInfo getRlInfo(String deployFlowId) {

		int codeId = 0, restartFlag = 0, lbFlag = 0, sgId = 0, actId = 0;

		String deployPath = null, codeVer = null, codeFileMD5 = null, lbIp = null, lbMode = null, serverStartCmd = null, serverStopCmd = null, servGroup = null;

		int updateAll = 0;

		// 正要安装版本
		long svnVerDoing = 0;

		// 正在使用的svn版本
		long svnVerUsing = 0;

		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);

		Map<String, Object> params = new HashMap<String, Object>();

		String sql = null;

		Iterator<DbAccessResponseRow> rowIt = null;
		DbAccessResponseRow row = null;

		// 读取发布相关设置如发布流程号、 是否重启服务器、是否操作负载均衡
		sql = SqlUtil.getSql("select  code_id,restart_flag,lb_flag from crs_deploy_flow  where deploy_flow_id=? ",
				deployFlowId);
		rowIt = dbas.executeQuery(sql).iterator();

		if (rowIt.hasNext()) {
			row = rowIt.next();
			codeId = row.getInt("code_id");
			restartFlag = row.getInt("restart_flag");
			lbFlag = row.getInt("lb_flag");
		} else {
			return null;
		}

		// 读取即将发布的代码相关信息
		sql = SqlUtil
				.getSql("select code_ver,sg_id,backup_status,codefile_md5,deploy_detail,svn_revision   from  crs_code_register where code_id=? ",
						codeId);
		rowIt = dbas.executeQuery(sql).iterator();
		if (rowIt.hasNext()) {
			row = rowIt.next();
			sgId = row.getInt("sg_id");
			codeVer = row.getString("code_ver");
			if ("all".equalsIgnoreCase(("" + row.getString("deploy_detail")).trim())) {
				updateAll = 1;
			}
			codeFileMD5 = row.getString("codefile_md5");
			svnVerDoing = row.getLong("svn_revision");
		} else {
			return null;
		}

		// 读取代码发布目标服务组信息
		sql = SqlUtil.getSql("select id,deploy_path,lb_mode,lb_ip,act_id   from  crs_serv_group where sg_id=? ", sgId);
		rowIt = dbas.executeQuery(sql).iterator();
		if (rowIt.hasNext()) {
			row = rowIt.next();
			servGroup = row.getString("id");
			deployPath = row.getString("deploy_path");
			lbMode = row.getString("lb_mode");
			lbIp = row.getString("lb_ip");
			actId = row.getInt("act_id");
		} else {
			return null;
		}

		// 读取服务组容器类型参数
		String actName = null, actVersion = null;
		sql = SqlUtil.getSql("select act_name,act_version from  app_container_type  where act_id=? ", actId);
		rowIt = dbas.executeQuery(sql).iterator();
		if (rowIt.hasNext()) {
			row = rowIt.next();
			actName = row.getString("act_name");
			actVersion = row.getString("act_version");
		}

		sql = SqlUtil.getSql(
				"select param_name,param_value  from  app_container_param where act_id=?  and param_name in ("
						+ SqlUtil.sqlValue(new String[] { CRSConstants.PARAM_START_SERVER_CMD,
								CRSConstants.PARAM_STOP_SERVER_CMD }) + ")", actId);
		rowIt = dbas.executeQuery(sql).iterator();
		while (rowIt.hasNext()) {
			row = rowIt.next();
			String paramN = row.getString("param_name");
			String paramV = row.getString("param_value");
			if (paramN.equals(CRSConstants.PARAM_START_SERVER_CMD)) {
				serverStartCmd = paramV;
			}
			if (paramN.equals(CRSConstants.PARAM_STOP_SERVER_CMD)) {
				serverStopCmd = paramV;
			}
		}

		List<AgentHost> hostList = new LinkedList<AgentHost>();
		AgentHost ah = null;
		sql = SqlUtil
				.getSql("select ip,jmx_port,status,preview_flag  from  crs_serv_group_member where sg_id=? ", sgId);
		rowIt = dbas.executeQuery(sql).iterator();
		while (rowIt.hasNext()) {
			row = rowIt.next();
			ah = new AgentHost(row.getString("ip"), row.getInt("jmx_port"));
			ah.setHostStatus(row.getInt("status"));
			ah.setPreviewFlag(row.getInt("preview_flag"));
			AgentDeloyStatus deployStatus = getAgentDeloyStatus(deployFlowId, ah.getIp());

			ah.setStatus(deployStatus.getStatus());
			ah.setProc(deployStatus.getProc());
			hostList.add(ah);
		}

		// 当前服务组正在使用的版本
		int currentUsedCodeId = 0;
		String currentUsedCodeVer = null;
		sql = SqlUtil
				.getSql("select code_id,code_ver,svn_revision from crs_code_register  where sg_id=? and deploy_status=2 ",
						sgId);
		rowIt = dbas.executeQuery(sql).iterator();
		if (rowIt.hasNext()) {
			row = rowIt.next();
			currentUsedCodeId = row.getInt("code_id");
			currentUsedCodeVer = row.getString("code_ver");
			svnVerUsing = row.getLong("svn_revision");
		}

		// 获取上一个版本
		int prevUsedCodeId = 0;

		sql = SqlUtil.getSql(
				"select prev_code_id from  crs_deploy_flow where code_id=? order by end_time desc limit 1 ",
				currentUsedCodeId);
		rowIt = dbas.executeQuery(sql).iterator();
		if (rowIt.hasNext()) {
			row = rowIt.next();
			prevUsedCodeId = row.getInt("prev_code_id");
		}
		String prevUsedCodeVer = null;
		if (prevUsedCodeId != 0) {
			sql = SqlUtil.getSql("select code_ver from crs_code_register where code_id=? ", prevUsedCodeId);
			rowIt = dbas.executeQuery(sql).iterator();
			if (rowIt.hasNext()) {
				row = rowIt.next();
				prevUsedCodeVer = row.getString("code_ver");
			}
		}

		RInfo rInfo = new RInfo();
		rInfo.setDeployId(deployFlowId);
		rInfo.setParams(params);
		params.put("codeId", codeId);
		params.put("restartFlag", restartFlag);
		params.put("lbFlag", lbFlag);
		params.put("codeVer", codeVer);
		params.put("updateAll", updateAll);
		params.put("codeFileMD5", codeFileMD5);
		params.put("sgId", sgId);
		params.put("servGroup", servGroup);
		params.put("deployPath", deployPath);
		params.put("lbMode", lbMode);
		params.put("lbIp", lbIp);

		params.put("actName", actName);
		params.put("actVersion", actVersion);
		params.put("serverStartCmd", serverStartCmd);
		params.put("serverStopCmd", serverStopCmd);

		params.put("usingCodeId", currentUsedCodeId);
		params.put("usingCodeVer", currentUsedCodeVer);

		params.put("prevUsedCodeId", prevUsedCodeId);
		params.put("prevUsedCodeVer", prevUsedCodeVer);

		params.put("svnVerDoing", svnVerDoing);
		params.put("svnVerUsing", svnVerUsing);

		rInfo.setServList(hostList);

		return rInfo;
	}

	/**
	 * 开始时更新流程状态
	 * 
	 * @param deployFowId
	 *            流程号
	 * @param startTime
	 *            开始时间
	 * @throws Exception
	 */
	public static void updateDeplyFlowStatus4Start(String deployFowId, Date startTime) throws Exception {
		String sql = SqlUtil.getSql("update crs_deploy_flow  set start_time=?,status=? where deploy_flow_id=? ",
				startTime, DeployFlowStatus.UNDERWAY, deployFowId);
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		dbas.executeUpdate(sql);
	}

	/**
	 * 结束时更新流程状态
	 * 
	 * @param deployFowId
	 * @param sgId
	 *            服务组id
	 * @param prevCodeId
	 *            上衣版本id
	 * @param endTime
	 * @param result
	 *            3-成功 4-失败
	 * @throws Exception
	 */
	public static void updateDeplyFlowStatus4Finish(String deployFowId, int sgId, int prevCodeId, Date endTime,
			int result) throws Exception {
		String sql = SqlUtil.getSql(
				"update crs_deploy_flow  set end_time=?,prev_code_id=?,status=? where deploy_flow_id=? ", endTime,
				prevCodeId, result, deployFowId);
		logger.debug("[updateDeplyFlowStatus4Finish]---> " + sql);
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		dbas.executeUpdate(sql);
	}

	/**
	 * 代码发布成功时 更新版本使用状态
	 * 
	 * @param sgId
	 *            服务组id
	 * @param codeId
	 * @throws Exception
	 */
	public static void updateCodeStatus4DeploySucc(int sgId, int codeId) throws Exception {
		// deployStatus:1-未使用;2-正在使用;3-正在安装;4-旧版本
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		String sql = null;
		// 将正在使用更新成旧版本
		sql = SqlUtil.getSql("update crs_code_register  set deploy_status=4  where sg_id=? and deploy_status=2 ", sgId);

		logger.debug("[updateCodeStatus4DeploySucc 1]---> " + sql);

		dbas.executeUpdate(sql);

		sql = SqlUtil.getSql("update crs_code_register  set deploy_status=2  where sg_id=? and code_id=? ", sgId,
				codeId);

		logger.debug("[updateCodeStatus4DeploySucc 2]---> " + sql);

		dbas.executeUpdate(sql);
	}

	/**
	 * 代码回退成功时 更新版本使用状态为未使用
	 * 
	 * @param sgId
	 *            服务组id
	 * @param codeId
	 * @throws Exception
	 */
	public static void updateCodeStatus4RollBackSucc(int sgId, int codeId) throws Exception {
		// deployStatus:1-未使用;2-正在使用;3-正在安装
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		String sql = null;
		sql = SqlUtil.getSql(
				"update crs_code_register  set deploy_status=1  where sg_id=? and code_id = ? and deploy_status=3 ",
				sgId, codeId);

		logger.debug("[updateCodeStatus4RollBackSucc]---> " + sql);

		dbas.executeUpdate(sql);
	}

	/**
	 * 更新部署流程状态
	 * 
	 * @param deployFlowId
	 * @param status
	 * @throws Exception
	 */
	public static void updateDeplyFlowStatus(String deployFlowId, int status) throws Exception {
		String sql = SqlUtil.getSql("update crs_deploy_flow set status=?  where  deploy_flow_id=? ", status,
				deployFlowId);
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		dbas.executeUpdate(sql);
	}

	/**
	 * 创建agent发布事件日志
	 * 
	 * @param agentDeloyEvt
	 * @throws Exception
	 */
	public static void createAgentEvtLog(AgentDeloyEvt agentDeloyEvt) throws Exception {
		Map<String, Object> colMap = new HashMap<String, Object>();
		colMap.put("agent_ip", agentDeloyEvt.getAgentIp());
		colMap.put("proc", agentDeloyEvt.getProc());
		colMap.put("status", agentDeloyEvt.getStatus());
		colMap.put("evt_descr", agentDeloyEvt.getEvtDescr());
		colMap.put("evt_attach", agentDeloyEvt.getEvtAttach());
		colMap.put("create_time", agentDeloyEvt.getCreateTime());
		colMap.put("deploy_flow_id", agentDeloyEvt.getDeployFlowId());
		String sql = SqlUtil.getInsertSql("crs_agent_deploy_evt", colMap);
		logger.debug("createAgentEvtLog-->[" + sql + "]");
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		dbas.executeUpdate(sql);
	}

	/**
	 * 删除指定流程日志
	 * 
	 * @param flowId
	 *            流程id
	 */
	public static void deleteDeployFlowLog(String flowId) {
		try {
			DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
			dbas.executeUpdate(SqlUtil.getSql(" delete from  crs_agent_deploy_evt    where deploy_flow_id=? ", flowId));
			dbas.executeUpdate(SqlUtil.getSql(" delete from  crs_deploy_flow    where deploy_flow_id=? ", flowId));
		} catch (Exception e) {
			logger.error("删除流程日志出错", e);
		}
	}

	/**
	 * 获取流程状态
	 * 
	 * @param flowId
	 * @return
	 */
	public static Integer getDeployFlowStatus(String flowId) {
		String sql = SqlUtil.getSql("select status from crs_deploy_flow where deploy_flow_id=? ", flowId);
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);

		Iterator<DbAccessResponseRow> rowIt = dbas.executeQuery(sql).iterator();
		if (rowIt.hasNext()) {
			DbAccessResponseRow row = rowIt.next();
			return row.getInt("status");
		} else {
			return 0;
		}
	}

	/**
	 * 更新服务器状态
	 */
	public static void updateHostStatus(int servGroupId, List<AgentHost> hosts, int newStatus) throws Exception {
		if (hosts == null || hosts.isEmpty()) {
			return;
		}

		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		String sql = null;
		for (AgentHost host : hosts) {
			sql = SqlUtil.getSql("update crs_serv_group_member set status=? where sg_id=?  and ip=?  ", newStatus,
					servGroupId, host.getIp());
			logger.debug("updateHostStatus-sql-->  " + sql);
			dbas.executeUpdate(sql);
		}
	}

	/**
	 * 更新流程结束时间
	 * 
	 * @param deployFlowId
	 *            流程id
	 * @param endTime
	 *            时间
	 * @throws Exception
	 */
	public static void updateDepployEndTime(String deployFlowId, Date endTime) throws Exception {
		String sql = SqlUtil.getSql("update  crs_deploy_flow set end_time=? where deploy_flow_id=? ", endTime,
				deployFlowId);
		DbAccessService dbas = DataAccessServiceManager.getDbAccessService(CRSConstants.DB_ACCESS_SERVICE);
		dbas.executeUpdate(sql);
	}

}

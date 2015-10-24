package com.github.ipaas.ideploy.agent.service;

/**
 * 公共常数对象
 * @author Chenql
 */
public interface Constant {
	
	//参数对象
	public final static String PARAM_NAME = "taskParams";
	public final static String PARAM_AGENTOBJECT = "agentObject";
	
	//参数列表
	public final static String APP_ID = "appid";
	

	public final static String PARAM_IP = "ip";
	public final static String PARAM_PORT = "port";
    public final static String PARAM_SERVICE_PORT = "servicePort";	
	public final static String PARAM_BROKER = "brokerName";
	public final static String PARAM_STATUS = "status";
	public final static String PARAM_STATUS_STOP = "stop";
	public final static String PARAM_STATUS_START = "start";
	public final static String PARAM_CRONSCHE = "cronSche";
	public final static String PARAM_OPTIONS = "options";
	public final static String QUEUENAME = "queueName";
	
	//参数分
	public final static String TASK_SPLIT = ",";
	

	//命令的前缀
	public final static String COMMANDNAME = "command";
	
	//常用端口
	public final static String DEFAULT_PORT = "9999";
	
	public final static String RESIN_PORT = "11010";
	
	public final static String MQ_PORT = "11030";
	
	public final static String ICE_PORT = "11020";
	
	public final static String JAVA_PORT = "11050";
	
	public final static String AGENT_PORT = "11099";
	
}

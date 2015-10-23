package com.github.ipaas.ifw.mq.activemq;

/**
 * MQ变量的参数
 */
public interface FwMqConstant {
	//参数名称列表
	public final static String APPIDS = "appIds";
	public final static String CONNECT_TIME = "connect_timeout";
	public final static String OPERATE_TIMEOUT = "operate_timeout";
	public final static String LISTEN_THREAD_POOL_MAX_SIZE = "listen_thread_pool_max_size";
	
	public final static String MAXIMUM_CONNECTIONS = "maximum_connections";
	public final static String MAXIMUN_ACTIVE = "maximum_active";
	public final static String IDLE_TIMEOUT = "idle_timeout";
	
	public final static String APPID_SPLIT = ",";
	public final static String USER_NAME = "user_name";
	public final static String PASSWORD = "password";
	
}

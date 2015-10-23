package com.github.ipaas.ifw.server.ice.mbean;

import com.github.ipaas.ifw.server.ice.ComponentServantLocator;
import com.github.ipaas.ifw.server.ice.ComponentServerThreadNotification;

import Ice.Communicator;

/**
 * 
 * @author Chenql
 */
public class CmptServerMonitor implements CmptServerMonitorMBean {

	/**
	 * ICE通讯接口
	 */
	private Communicator communicator;

	/**
	 * 线程池监听器
	 */
	private ComponentServerThreadNotification threadNotification;

	public CmptServerMonitor(Communicator communicator, ComponentServerThreadNotification threadNotification) {
		this.communicator = communicator;
		this.threadNotification = threadNotification;
	}

	@Override
	public String getIceVersion() {
		return Ice.Util.stringVersion();
	}

	@Override
	public long getMessageSizeMax() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsInt("Ice.MessageSizeMax");
	}

	@Override
	public long getIceRequestCountTotal() {
		return ComponentServantLocator.getRequestCountTotal();
	}

	@Override
	public int getThreadPoolSerialize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.ThreadPool.Server.Serialize", 0);
	}

	@Override
	public int getThreadPoolSize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.ThreadPool.Server.Size", 0);

	}

	@Override
	public int getThreadPoolSizeMax() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.ThreadPool.Server.SizeMax", 0);

	}

	@Override
	public int getThreadPoolSizeWarn() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.ThreadPool.Server.SizeWarn", 0);
	}

	@Override
	public int getThreadPoolStackSize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.ThreadPool.Server.StackSize", 0);

	}

	@Override
	public int getThreadPoolSizeAct() {
		if (threadNotification == null) {
			return 0;
		}
		return threadNotification.getThreadCount();
	}

	@Override
	public long getTCPRcvSize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.TCP.RcvSize", 65535);
	}

	@Override
	public long getTCPSndSize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.TCP.SndSize", 65535);

	}

	@Override
	public long getUDPRcvSize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.UDP.RcvSize", 65535);

	}

	@Override
	public long getUDPSndSize() {
		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.UDP.RcvSize", 65535);
	}

	@Override
	public int getGCInterval() {

		if (communicator == null) {
			return 0;
		}
		return communicator.getProperties().getPropertyAsIntWithDefault("Ice.GC.Interval", 0);
	}

}

package com.github.ipaas.ideploy.dto;

/**
 * 服务标识
 *@author Chenql
 */
public class ServiceDTO {
	
	/**
	 * 服务IP
	 */
	private String ip;
	
	/**
	 * 服务端口
	 */
	private String port;
	
	public ServiceDTO(){
		
	}
	
	public ServiceDTO(String ip,String port){
		this.ip = ip;
		this.port = port;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}

	@Override
	public int hashCode() {
		int result = 31;
        result = ip.hashCode();
        result = 31 * result + port.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        ServiceDTO that = (ServiceDTO) obj;

        if (!ip.equals(that.ip)) return false;

        if (!port.equals(that.port)) return false;

		return true;
	}
	
	
	
	

}

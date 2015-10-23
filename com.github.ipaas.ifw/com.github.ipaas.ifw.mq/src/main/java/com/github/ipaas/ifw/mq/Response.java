package com.github.ipaas.ifw.mq;

/**
 * 同步信息返回模型 C
 * 
 * @author Chenql
 */
public interface Response {

	/**
	 * 请求ID
	 * 
	 * @return
	 */
	public String getRequestId();

	public void setRequestId(String requestId);

	/**
	 * 返回内容
	 * 
	 * @return
	 */
	public Object getContext();

	public void setContext(Object context);

	/**
	 * 返回的状态码
	 * 
	 * @return
	 */
	public Integer getStatusCode();

	public void setStatusCode(Integer statusCode);

	/**
	 * 错误信息
	 * 
	 * @return
	 */
	public String getErrorMsg();

	public void setErrorMsg(String errorMsg);

}

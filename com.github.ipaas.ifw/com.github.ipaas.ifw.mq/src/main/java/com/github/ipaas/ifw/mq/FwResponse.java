package com.github.ipaas.ifw.mq;

import java.io.Serializable;

/**
 * Author: dengrq
 */
public class FwResponse implements Response, Serializable {

	public static final int ERROR_CODE = 500;
	public static final int SUCCESS_CODE = 200;

	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 请求ID
	 */
	private String requestId;

	/**
	 * 返回内容
	 */
	private Object context;

	/**
	 * 返回的状态码
	 */
	private Integer statusCode;

	/**
	 * 错误信息
	 */
	private String errorMsg;

	/**
	 * 请求时间
	 */
	private Long requestTimestamp;

	/**
	 * 到达时间
	 */
	private Long reachTimestamp;

	/**
	 * 回复时间
	 */
	private Long replyTimestamp;

	/**
	 * 返回时间
	 */
	private Long receiveTimestamp;

	public FwResponse() {

	}

	/**
	 * 正确返回
	 * 
	 * @param requestId
	 * @param context
	 */
	public FwResponse(String requestId, Object context) {
		this.requestId = requestId;
		this.context = context;
		this.statusCode = 200;
	}

	/**
	 * 错误返回
	 * 
	 * @param requestId
	 * @param errorMsg
	 */
	public FwResponse(String requestId, String errorMsg) {
		this.requestId = requestId;
		this.errorMsg = errorMsg;
		this.statusCode = 500;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public Object getContext() {
		return context;
	}

	public void setContext(Object context) {
		this.context = context;
	}

	public Integer getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(Integer statusCode) {
		this.statusCode = statusCode;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public Long getRequestTimestamp() {
		return requestTimestamp;
	}

	public void setRequestTimestamp(Long requestTimestamp) {
		this.requestTimestamp = requestTimestamp;
	}

	public Long getReachTimestamp() {
		return reachTimestamp;
	}

	public void setReachTimestamp(Long reachTimestamp) {
		this.reachTimestamp = reachTimestamp;
	}

	public Long getReplyTimestamp() {
		return replyTimestamp;
	}

	public void setReplyTimestamp(Long replyTimestamp) {
		this.replyTimestamp = replyTimestamp;
	}

	public Long getReceiveTimestamp() {
		return receiveTimestamp;
	}

	public void setReceiveTimestamp(Long receiveTimestamp) {
		this.receiveTimestamp = receiveTimestamp;
	}

}

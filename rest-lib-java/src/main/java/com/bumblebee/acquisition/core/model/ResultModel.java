package com.bumblebee.acquisition.core.model;

import java.io.Serializable;

/**
 * 公共的返回模板对象，所有对外服务接口，必须使用该模板
 * 
 * @author renhua.zhang
 */
public class ResultModel<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1998430451066165823L;

	/**
	 * 成功
	 */
	public static final String SUCCESS = "0";

	/**
	 * 失败
	 */
	public static final String FAILD = "1";

	/**
	 * 状态代码，0成功，1失败
	 */
	private String statusCode;

	/**
	 * 提示信息
	 */
	private String message;

	/**
	 * 返回数据
	 */
	private T data;

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public ResultModel(String statusCode, String message, T data) {
		super();
		this.statusCode = statusCode;
		this.message = message;
		this.data = data;
	}

	public ResultModel() {
		super();
	}

	public ResultModel(String statusCode, T data) {
		super();
		this.statusCode = statusCode;
		this.data = data;
	}

	public ResultModel(String statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}

	public ResultModel(String statusCode) {
		super();
		this.statusCode = statusCode;
	}

	@Override
	public String toString() {
		return "ResultModule [statusCode=" + statusCode + ", message=" + message + ", data=" + data + "]";
	}

}

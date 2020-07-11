package com.sink.comm.vo;

import java.io.Serializable;

/**
 * 方法返回信息传递(用于事务方法)
 *
 * @author Administrator
 *
 */
public class ReturnArgs implements Serializable {
	private static final long serialVersionUID = -2168187092522355281L;

	/** 返回编码 **/
	private String code = "0000";
	/** 返回描述 **/
	private String desc;
	/** 返回值 **/
	private Object returnObj;

	public String getCode() {
		return code;
	}

	public String getDesc() {
		return desc;
	}

	public Object getReturnObj() {
		return returnObj;
	}

	public void setReturnObj(Object returnObj) {
		this.returnObj = returnObj;
	}

	public ReturnArgs setFailDesc(String code, String desc) {
		this.code = code;
		this.desc = desc;
		return this;
	}

	/**
	 * 初始化
	 */
	public void init(){
		this.code = "0000";
		this.desc = "";
		this.returnObj = null;
	}

	public boolean isSuccess(){
		return this.code.equals("0000");
	}
}

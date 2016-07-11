package com.xunfei.robot.entity;
/**
 * @author houen.bao
 * @date Jul 11, 2016 10:36:11 AM
 * "code":" UNRESOLVABLE_CONDITION",
 * "message": "缺少必要的查询条件"
 */
public class Error {

	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

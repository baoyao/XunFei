package com.xunfei.robot.entity;

/**
 * @author houen.bao
 * @date Jul 8, 2016 3:44:05 PM
 */
public class App {
	private String rc;
	private String text;
	private String service;
	private String operation;
	private Semantic semantic;

	public String getRc() {
		return rc;
	}

	public void setRc(String rc) {
		this.rc = rc;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public Semantic getSemantic() {
		return semantic;
	}

	public void setSemantic(Semantic semantic) {
		this.semantic = semantic;
	}

}

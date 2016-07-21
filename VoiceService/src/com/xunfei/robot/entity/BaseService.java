package com.xunfei.robot.entity;
/**
 * @author houen.bao
 * @date Jul 8, 2016 5:38:07 PM
 */
public class BaseService {

	private String rc;
	private String text;
	private String service;
	private String operation;

	private Semantic semantic;

	private Answer answer;
	
	private Data data;
	
	private WebPage webPage;

	private com.xunfei.robot.entity.Error error;

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

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	
	public Data getData() {
		return data;
	}
	
	public void setData(Data data) {
		this.data = data;
	}

	public WebPage getWebPage() {
		return webPage;
	}

	public void setWebPage(WebPage webPage) {
		this.webPage = webPage;
	}

	public com.xunfei.robot.entity.Error getError() {
		return error;
	}

	public void setError(com.xunfei.robot.entity.Error error) {
		this.error = error;
	}

}

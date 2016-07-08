package com.xunfei.robot.entity;

/**
 * @author houen.bao
 * @date Jul 8, 2016 5:43:15 PM
 */
public class Cookbook extends BaseService {
	private Semantic semantic;
	private Data data;

	public Semantic getSemantic() {
		return semantic;
	}

	public void setSemantic(Semantic semantic) {
		this.semantic = semantic;
	}

	public Data getData() {
		return data;
	}

	public void setData(Data data) {
		this.data = data;
	}

}

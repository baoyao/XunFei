package com.robot.voice.entity;

/**
 * @author houen.bao
 * @date Jul 8, 2016 5:09:04 PM
 */
public class ResultAction {

	private String result;
	private boolean isIntercept;
	private boolean isShowErrorMessage;

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public boolean isIntercept() {
		return isIntercept;
	}

	public void setIntercept(boolean isIntercept) {
		this.isIntercept = isIntercept;
	}

	public boolean isShowErrorMessage() {
		return isShowErrorMessage;
	}

	public void setShowErrorMessage(boolean isShowErrorMessage) {
		if(isShowErrorMessage){
			this.isIntercept = true;
		}
		this.isShowErrorMessage = isShowErrorMessage;
	}

}

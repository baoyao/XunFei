package com.robot.voice.entity;

/**
 * @author houen.bao
 * @date Jul 12, 2016 11:20:39 AM
 * 
 * "header":"为您找到下面的结果",
 * "url":"http:\/\/kcgz.openspeech.cn\/service\/iss?wuuid=721f34c27954bec03912b59329539816&ver=2.0&method=webPage&uuid=dab846026f3475e0ff699376e8d40845query"
 * 
 */
public class WebPage {

	private String header;
	private String url;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}

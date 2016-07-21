package com.robot.voice.entity;
/**
 * @author houen.bao
 * @date Jul 11, 2016 1:40:45 PM
 * 
 * "type": "DT_BASIC",
 * "date": "2013-11-11",
 * "dateOrig": "明天",
 * "time": "06:00:00",
 * "timeOrig": "6 点"
 * 
 */
public class Datetime {
	private String type;
	private String date;
	private String dateOrig;
	private String time;
	private String timeOrig;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDateOrig() {
		return dateOrig;
	}

	public void setDateOrig(String dateOrig) {
		this.dateOrig = dateOrig;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTimeOrig() {
		return timeOrig;
	}

	public void setTimeOrig(String timeOrig) {
		this.timeOrig = timeOrig;
	}

}

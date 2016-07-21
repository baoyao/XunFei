package com.robot.voice.entity;
/**
 * @author houen.bao
 * @date Jul 11, 2016 2:04:44 PM
 * 
 * "type": "DT_INTERVAL",
 * "date": "2013-11-21",
 * "dateOrig": "明天",
 * "time": "18:00:00",
 * "timeOrig": "晚上",
 * "endDate": "2013-11-21",
 * "endTime": "22:00:00"
 * 
 */
public class StartTime {
	private String type;
	private String date;
	private String dateOrig;
	private String time;
	private String timeOrig;
	private String endDate;
	private String endTime;

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

	public String getEndDate() {
		return endDate;
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

}

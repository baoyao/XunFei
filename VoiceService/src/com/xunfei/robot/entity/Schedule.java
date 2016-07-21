package com.xunfei.robot.entity;

/**
 * @author houen.bao
 * @date Jul 13, 2016 10:14:15 AM
 */
public class Schedule {

	private String title;
	private String name;
	private String description;
	private String beginDate;
	private String beginTime;
	private String endDate;
	private String endTime;

	private int[] eventBeginDate;
	private int[] eventBeginTime;
	private int[] eventEndDate;
	private int[] eventEndTime;

	private int reminderMinutes;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getBeginDate() {
		return beginDate;
	}
	
	private void setBeginDate(int[] date) {
		String dateStr="";
		for(int i = 0;i<date.length;i++){
			if(i!=(date.length-1)){
				dateStr+=date[i]+"-";
			}else{
				dateStr+=date[i];
			}
		}
		this.beginDate = dateStr;
	}

	public String getBeginTime() {
		return beginTime;
	}

	private void setBeginTime(int[] time) {
		String timeStr="";
		for(int i = 0;i<time.length;i++){
			if(i!=(time.length-1)){
				timeStr+=time[i]+":";
			}else{
				timeStr+=time[i];
			}
		}
		this.beginTime = timeStr;
	}

	public String getEndDate() {
		return endDate;
	}

	private void setEndDate(int[] date) {
		String dateStr="";
		for(int i = 0;i<date.length;i++){
			if(i!=(date.length-1)){
				dateStr+=date[i]+"-";
			}else{
				dateStr+=date[i];
			}
		}
		this.endDate = dateStr;
	}

	public String getEndTime() {
		return endTime;
	}

	private void setEndTime(int[] time) {
		String timeStr="";
		for(int i = 0;i<time.length;i++){
			if(i!=(time.length-1)){
				timeStr+=time[i]+":";
			}else{
				timeStr+=time[i];
			}
		}
		this.endTime = timeStr;
	}

	public int getReminderMinutes() {
		return reminderMinutes;
	}

	public void setReminderMinutes(int reminderMinutes) {
		this.reminderMinutes = reminderMinutes;
	}

	public int[] getEventBeginDate() {
		return eventBeginDate;
	}

	public void setEventBeginDate(int[] eventBeginDate) {
		this.eventBeginDate = eventBeginDate;
		setBeginDate(eventBeginDate);
	}

	public int[] getEventBeginTime() {
		return eventBeginTime;
	}

	public void setEventBeginTime(int[] eventBeginTime) {
		this.eventBeginTime = eventBeginTime;
		setBeginTime(eventBeginTime);
	}

	public int[] getEventEndDate() {
		return eventEndDate;
	}

	public void setEventEndDate(int[] eventEndDate) {
		this.eventEndDate = eventEndDate;
		setEndDate(eventEndDate);
	}

	public int[] getEventEndTime() {
		return eventEndTime;
	}

	public void setEventEndTime(int[] eventEndTime) {
		this.eventEndTime = eventEndTime;
		setEndTime(eventEndTime);
	}

	public void setBeginDate(String beginDate) {
		this.beginDate = beginDate;
		String[] tempDate=beginDate.split("-");
		int[] date = new int[tempDate.length];
		for(int i = 0;i<tempDate.length;i++){
			date[i]=Integer.parseInt(tempDate[i]);
		}
		setEventBeginDate(date);
	}

	public void setBeginTime(String beginTime) {
		this.beginTime = beginTime;
		String[] tempTime=beginTime.split(":");
		int[] time = new int[tempTime.length];
		for(int i = 0;i<tempTime.length;i++){
			time[i]=Integer.parseInt(tempTime[i]);
		}
		setEventBeginTime(time);
	}

	public void setEndDate(String endDate) {
		this.endDate = endDate;
		String[] tempDate=endDate.split("-");
		int[] date = new int[tempDate.length];
		for(int i = 0;i<tempDate.length;i++){
			date[i]=Integer.parseInt(tempDate[i]);
		}
		setEventEndDate(date);
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
		String[] tempTime=endTime.split(":");
		int[] time = new int[tempTime.length];
		for(int i = 0;i<tempTime.length;i++){
			time[i]=Integer.parseInt(tempTime[i]);
		}
		setEventEndTime(time);
	}
	
}

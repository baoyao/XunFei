package com.xunfei.robot.entity;
/**
 * @author houen.bao
 * @date Jul 11, 2016 10:27:46 AM
 * 
 * "type": "LOC_POI",
 * "city": "合肥市",
 * "cityAddr": "合肥",
 * "poi": "新桥机场"
 * "area": "包河区",
 * "areaAddr": "包河",
 * 
 */
public class Location {
	private String type;
	private String city;
	private String cityAddr;
	private String poi;
	
	private String area;
	private String areaAddr;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCityAddr() {
		return cityAddr;
	}

	public void setCityAddr(String cityAddr) {
		this.cityAddr = cityAddr;
	}

	public String getPoi() {
		return poi;
	}

	public void setPoi(String poi) {
		this.poi = poi;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getAreaAddr() {
		return areaAddr;
	}

	public void setAreaAddr(String areaAddr) {
		this.areaAddr = areaAddr;
	}
	
	

}

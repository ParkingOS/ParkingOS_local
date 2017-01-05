package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DepartureInfo implements Serializable{
	//	{"count":1,"price":1280.0,"info":[{"id":"238705","total":"1280.0",
	//	"duration":"已停 13天 8小时44分钟","carnumber":"京JA6036","rightbottom":"200",
	//	"btime":"2014-10-28 10:47","picurl":"carpics/1197_238705_1415601668.jpg",
	//	"lefttop":"30"}]}
	private String id;
	private String total;
	private String duration;
	private String carnumber;
	private String rightbottom;
	private String btime;
	private String picurl;
	private String lefttop;
	private String width;
	private String height;
	
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getRightbottom() {
		return rightbottom;
	}
	public void setRightbottom(String rightbottom) {
		this.rightbottom = rightbottom;
	}
	public String getBtime() {
		return btime;
	}
	public void setBtime(String btime) {
		this.btime = btime;
	}
	public String getPicurl() {
		return picurl;
	}
	public void setPicurl(String picurl) {
		this.picurl = picurl;
	}
	public String getLefttop() {
		return lefttop;
	}
	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}
	@Override
	public String toString() {
		return "DepartureInfo [id=" + id + ", total=" + total + ", duration="
				+ duration + ", carnumber=" + carnumber + ", rightbottom="
				+ rightbottom + ", btime=" + btime + ", picurl=" + picurl
				+ ", lefttop=" + lefttop + ", width=" + width + ", height="
				+ height + "]";
	}
	
}

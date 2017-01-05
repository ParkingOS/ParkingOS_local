package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UploadImg implements Serializable {
	/*
	 * id 
	 * account 账户
	 * orderid 订单id
	 * lefttop 图片左上角x坐标
	 * rightbottom 图片左上角y坐标
	 * type 通道类型
	 * width 图片宽
	 * height 图片高
	 * imghomepath 入场图片路径
	 * imgexitpath 出场图片路径
	 */
	private int id;
	private String account;
	private String orderid;
	private String lefttop;
	private String rightbottom;
	private String type;
	private String width;
	private String height;
	private String imghomepath;
	private String imgexitpath;
	private String carnumber;
	private String homeimgup;
	private String exitimgup;
	public String getHomeimgup() {
		return homeimgup;
	}
	public void setHomeimgup(String homeimgup) {
		this.homeimgup = homeimgup;
	}
	public String getExitimgup() {
		return exitimgup;
	}
	public void setExitimgup(String exitimgup) {
		this.exitimgup = exitimgup;
	}
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getLefttop() {
		return lefttop;
	}
	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}
	public String getRightbottom() {
		return rightbottom;
	}
	public void setRightbottom(String rightbottom) {
		this.rightbottom = rightbottom;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
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
	public String getImghomepath() {
		return imghomepath;
	}
	public void setImghomepath(String imghomepath) {
		this.imghomepath = imghomepath;
	}
	public String getImgexitpath() {
		return imgexitpath;
	}
	public void setImgexitpath(String imgexitpath) {
		this.imgexitpath = imgexitpath;
	}
	@Override
	public String toString() {
		return "UploadImg [id=" + id + ", account=" + account + ", orderid="
				+ orderid + ", lefttop=" + lefttop + ", rightbottom="
				+ rightbottom + ", type=" + type + ", width=" + width
				+ ", height=" + height + ", imghomepath=" + imghomepath
				+ ", imgexitpath=" + imgexitpath + ", carnumber=" + carnumber
				+ ", homeimgup=" + homeimgup + ", exitimgup=" + exitimgup + "]";
	}

	public String toEasyString() {
		return "orderid="
				+ orderid + ", imghomepath=" + imghomepath
				+ ", imgexitpath=" + imgexitpath + ", carnumber=" + carnumber
				+ ", homeimgup=" + homeimgup + ", exitimgup=" + exitimgup + "]";
	}

}

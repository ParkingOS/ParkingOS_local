package com.zld.bean;

import java.io.Serializable;

public class MyLedInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
//	05-16 16:27:13.085: E/ChooseWorkstationActivity(21297): 获取leds信息为[
//	{id=10, ledip=192.168.199.114, ledport=8888, leduid=41, movemode=9,
//	movespeed=1, dwelltime=1, ledcolor=1, showcolor=0, typeface=1, typesize=1,
//	matercont=停车宝	VIP速通, passid=28}]

	private String id;
	private String ledip;
	private String ledport;
	private String leduid;
	private String movemode;
	private String movespeed;
	private String dwelltime;
	private String ledcolor;
	private String showcolor;
	private String typeface;
	private String typesize;
	private String matercont;
	private String passid;
	private String passtype;
	private String passname;
	private String width;
	private String height;
	private String type;
	private String rsport;

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getLedip() {
		return ledip;
	}
	public void setLedip(String ledip) {
		this.ledip = ledip;
	}
	public String getLedport() {
		return ledport;
	}
	public void setLedport(String ledport) {
		this.ledport = ledport;
	}
	public String getLeduid() {
		return leduid;
	}
	public void setLeduid(String leduid) {
		this.leduid = leduid;
	}
	public String getMovemode() {
		return movemode;
	}
	public void setMovemode(String movemode) {
		this.movemode = movemode;
	}
	public String getMovespeed() {
		return movespeed;
	}
	public void setMovespeed(String movespeed) {
		this.movespeed = movespeed;
	}
	public String getDwelltime() {
		return dwelltime;
	}
	public void setDwelltime(String dwelltime) {
		this.dwelltime = dwelltime;
	}
	public String getLedcolor() {
		return ledcolor;
	}
	public void setLedcolor(String ledcolor) {
		this.ledcolor = ledcolor;
	}
	public String getShowcolor() {
		return showcolor;
	}
	public void setShowcolor(String showcolor) {
		this.showcolor = showcolor;
	}
	public String getTypeface() {
		return typeface;
	}
	public void setTypeface(String typeface) {
		this.typeface = typeface;
	}
	public String getTypesize() {
		return typesize;
	}
	public void setTypesize(String typesize) {
		this.typesize = typesize;
	}
	public String getMatercont() {
		return matercont;
	}
	public void setMatercont(String matercont) {
		this.matercont = matercont;
	}
	public String getPassid() {
		return passid;
	}
	public void setPassid(String passid) {
		this.passid = passid;
	}
	public String getPasstype() {
		return passtype;
	}
	public void setPasstype(String passtype) {
		this.passtype = passtype;
	}
	public String getPassname() {
		return passname;
	}
	public void setPassname(String passname) {
		this.passname = passname;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getRsport() {
		return rsport;
	}
	public void setRsport(String rsport) {
		this.rsport = rsport;
	}

	@Override
	public String toString() {
		return "MyLedInfo [id=" + id + ", ledip=" + ledip + ", ledport="
				+ ledport + ", leduid=" + leduid + ", movemode=" + movemode
				+ ", movespeed=" + movespeed + ", dwelltime=" + dwelltime
				+ ", ledcolor=" + ledcolor + ", showcolor=" + showcolor
				+ ", typeface=" + typeface + ", typesize=" + typesize
				+ ", matercont=" + matercont + ", passid=" + passid
				+ ", passtype=" + passtype + ", passname=" + passname
				+ ", width=" + width + ", height=" + height + ", type=" + type
				+ ", rsport=" + rsport + "]";
	}
	public MyLedInfo() {
		super();
		// TODO Auto-generated constructor stub
	}

}
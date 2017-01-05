package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MyControlInfo implements Serializable {
	private String id;
	private String brake_name;
	private String serial;
	private String ip;
	private String passid;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getBrake_name() {
		return brake_name;
	}
	public void setBrake_name(String brake_name) {
		this.brake_name = brake_name;
	}
	public String getSerial() {
		return serial;
	}
	public void setSerial(String serial) {
		this.serial = serial;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPassid() {
		return passid;
	}
	public void setPassid(String passid) {
		this.passid = passid;
	}
	@Override
	public String toString() {
		return "MyControlInfo [id=" + id + ", brake_name=" + brake_name
				+ ", serial=" + serial + ", ip=" + ip + ", passid=" + passid
				+ "]";
	}
}

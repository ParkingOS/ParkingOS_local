package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class MyCameraInfo implements Serializable {
	
	private String id;
	private String camera_name;
	private String cameraid;
	private String ip;
	private String port;
	private String cusername;
	private String cpassword;
	private String manufacturer;
	private String passid;
	/* passtype   0:入口车道下的摄像头       1:出口车道下的摄像头  */
	private String passtype;
	private String passname;
	
	public String getPasstype() {
		return passtype;
	}
	public void setPasstype(String passtype) {
		this.passtype = passtype;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCamera_name() {
		return camera_name;
	}
	public void setCamera_name(String camera_name) {
		this.camera_name = camera_name;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public String getCusername() {
		return cusername;
	}
	public void setCusername(String cusername) {
		this.cusername = cusername;
	}
	public String getCpassword() {
		return cpassword;
	}
	public void setCpassword(String cpassword) {
		this.cpassword = cpassword;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getPassid() {
		return passid;
	}
	public void setPassid(String passid) {
		this.passid = passid;
	}
	public String getPassname() {
		return passname;
	}
	public void setPassname(String passname) {
		this.passname = passname;
	}
	public String getCameraid() {
		return cameraid;
	}
	public void setCameraid(String cameraid) {
		this.cameraid = cameraid;
	}
	@Override
	public String toString() {
		return "MyCameraInfo [id=" + id + ", camera_name=" + camera_name
				+ ", cameraid=" + cameraid + ", ip=" + ip + ", port=" + port
				+ ", cusername=" + cusername + ", cpassword=" + cpassword
				+ ", manufacturer=" + manufacturer + ", passid=" + passid
				+ ", passtype=" + passtype + ", passname=" + passname + "]";
	}
	
}

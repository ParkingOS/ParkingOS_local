package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class EnterClose implements Serializable{
	private String id;
	private String worksite_id;
	private String passname;
	private String passtype;
	private String description;
	private String comid;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getWorksite_id() {
		return worksite_id;
	}
	public void setWorksite_id(String worksite_id) {
		this.worksite_id = worksite_id;
	}
	public String getPassname() {
		return passname;
	}
	public void setPassname(String passname) {
		this.passname = passname;
	}
	public String getPasstype() {
		return passtype;
	}
	public void setPasstype(String passtype) {
		this.passtype = passtype;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getComid() {
		return comid;
	}
	public void setComid(String comid) {
		this.comid = comid;
	}
	@Override
	public String toString() {
		return "EnterClose [id=" + id + ", worksite_id=" + worksite_id
				+ ", passname=" + passname + ", passtype=" + passtype
				+ ", description=" + description + ", comid=" + comid + "]";
	}
}

package com.zld.bean;

public class FreeResons {
	private String value_no;
	private String value_name;
	
	public String getID() {
		return value_no;
	}
	public void setID(String value_no) {
		this.value_no = value_no;
	}
	public String getName() {
		return value_name;
	}
	public void setName(String value_name) {
		this.value_name = value_name;
	}
	@Override
	public String toString() {
		return "FreeResons [value_no=" + value_no + ", value_name=" + value_name + "]";
	}
	
	
}

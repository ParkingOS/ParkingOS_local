package com.zld.utils;

public class NoList {
	public String getValue_no() {
		return value_no;
	}
	public void setValue_no(String value_no) {
		this.value_no = value_no;
	}
	public String getValue_name() {
		return value_name;
	}
	public void setValue_name(String value_name) {
		this.value_name = value_name;
	}
	private String value_no;
	private String value_name;
	public NoList(String value_no,String value_name){
		this.value_no = value_no;
		this.value_name = value_name;
	}
}

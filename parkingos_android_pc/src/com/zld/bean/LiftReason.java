package com.zld.bean;

public class LiftReason {
	private int value_no; // 抬杆原因编号
	private String value_name; // 抬杆原因名字
	
	public int getValue_no() {
		return value_no;
	}
	public void setValue_no(int value_no) {
		this.value_no = value_no;
	}
	public String getValue_name() {
		return value_name;
	}
	public void setValue_name(String value_name) {
		this.value_name = value_name;
	}
	@Override
	public String toString() {
		return "LiftReason [value_no=" + value_no + ", value_name=" + value_name + "]";
	}
	
	
}

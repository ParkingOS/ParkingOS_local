package com.zld.local.db;

public class MonthCard_tb {

	public String uin;
	public String car_number;
	public String e_time;

	public MonthCard_tb() {
		super();
	}

	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}
	
	public String getCar_number() {
		return car_number;
	}

	public void setCar_number(String car_number) {
		this.car_number = car_number;
	}

	public String getE_time() {
		return e_time;
	}

	public void setE_time(String e_time) {
		this.e_time = e_time;
	}

	@Override
	public String toString() {
		return "MonthCard_tb [uin=" + uin + ", car_number=" + car_number + ", e_time="
				+ e_time + "]";
	}

}

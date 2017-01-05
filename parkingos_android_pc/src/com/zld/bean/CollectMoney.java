package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CollectMoney implements Serializable{
	private String mobilepay;
	private String cashpay;
	private String start_time;
	
	public CollectMoney(){
		super();
	}
	
	public String getMobilepay() {
		return mobilepay;
	}

	public void setMobilepay(String mobilepay) {
		this.mobilepay = mobilepay;
	}

	public String getCashpay() {
		return cashpay;
	}

	public void setCashpay(String cashpay) {
		this.cashpay = cashpay;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	@Override
	public String toString() {
		return "CollectMoney [mobilepay=" + mobilepay + ", cashpay=" + cashpay
				+ ", start_time=" + start_time + "]";
	}

}

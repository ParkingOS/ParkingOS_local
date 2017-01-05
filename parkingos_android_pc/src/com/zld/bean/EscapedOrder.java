package com.zld.bean;

import java.io.Serializable;


@SuppressWarnings("serial")
public class EscapedOrder implements Serializable{

	private String orderid;
	private String info;
	public EscapedOrder() {
		super();
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return "EscapedOrder [info=" + info + ", orderid=" + orderid + "]";
	}
	
	
	
}

package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CarNumberMadeOrder implements Serializable{

	String info;//0 -生成订单失败！
	String orderid;
	String own;//自己车场逃单
	String other;//别的车场逃单
	String ismonthuser;
	String preorderid;
	public String getIsmonthuser() {
		return ismonthuser;
	}
	public void setIsmonthuser(String ismonthuser) {
		this.ismonthuser = ismonthuser;
	}
	public CarNumberMadeOrder() {
		super();
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getOwn() {
		return own;
	}
	public void setOwn(String own) {
		this.own = own;
	}
	public String getOther() {
		return other;
	}
	public void setOther(String other) {
		this.other = other;
	}
	public String getPreorderid() {
		return preorderid;
	}
	public void setPreorderid(String preorderid) {
		this.preorderid = preorderid;
	}
	@Override
	public String toString() {
		return "CarNumberMadeOrder [info=" + info + ", orderid=" + orderid
				+ ", own=" + own + ", other=" + other + ", ismonthuser" + ismonthuser+ ", preorderid=" + preorderid +"]";
	}
}

package com.zld.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CurrentOrder implements Serializable{

	private String count;
	private String price;
	private String isauto;//搜索出的车牌号是否自动结算掉 1结 0不结
	private ArrayList<AllOrder> info;
	public CurrentOrder() {
		super();
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public ArrayList<AllOrder> getInfo() {
		return info;
	}
	public void setInfo(ArrayList<AllOrder> info) {
		this.info = info;
	}
	public String getIsauto() {
		return isauto;
	}
	public void setIsauto(String isauto) {
		this.isauto = isauto;
	}
	@Override
	public String toString() {
		return "CurrentOrder [count=" + count + ", price=" + price
				+ ", isauto=" + isauto + ", info=" + info + "]";
	}
	
}

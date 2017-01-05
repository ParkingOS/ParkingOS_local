package com.zld.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class CurrentHistoryOrder implements Serializable{

	private String count;
	private String price;
	private ArrayList<HistoryOrder> info;
	public CurrentHistoryOrder() {
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
	public ArrayList<HistoryOrder> getInfo() {
		return info;
	}
	public void setInfo(ArrayList<HistoryOrder> info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return "CurrentHistoryOrder [count=" + count + ", price=" + price
				+ ", info=" + info + "]";
	}
	
}

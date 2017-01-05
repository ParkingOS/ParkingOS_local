package com.zld.bean;

import java.io.Serializable;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class AutoDepartureOrder implements Serializable{
	//	{"count":1,"price":1280.0,"info":[{"id":"238705","total":"1280.0",
	//	"duration":"已停 13天 8小时44分钟","carnumber":"京JA6036","rightbottom":"200",
	//	"btime":"2014-10-28 10:47","picurl":"carpics/1197_238705_1415601668.jpg",
	//	"lefttop":"30"}]}

	private String count;
	private String price;
	private ArrayList<DepartureInfo> info;
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
	public ArrayList<DepartureInfo> getInfo() {
		return info;
	}
	public void setInfo(ArrayList<DepartureInfo> info) {
		this.info = info;
	}
	@Override
	public String toString() {
		return "AutoDepartureOrder [count=" + count + ", price=" + price
				+ ", info=" + info + "]";
	}
	public AutoDepartureOrder() {
		super();
		// TODO Auto-generated constructor stub
	}
}

package com.zld.bean;

import java.io.Serializable;

//<?xml version="1.0" encoding="gb2312"?><content><total>26</total><free>17</free><busy>9</busy></content>
@SuppressWarnings("serial")
public class ShaerUiInfo implements Serializable {
	private String total;
	private String  free;
	private String  busy;
	private String result;
	public ShaerUiInfo() {
		super();
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getFree() {
		return free;
	}
	public void setFree(String free) {
		this.free = free;
	}
	public String getBusy() {
		return busy;
	}
	public void setBusy(String busy) {
		this.busy = busy;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	@Override
	public String toString() {
		return "ShaerUiInfo [total=" + total + ", free=" + free + ", busy="
				+ busy + ", result=" + result + "]";
	}
	

	
}

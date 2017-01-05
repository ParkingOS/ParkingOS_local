package com.zld.bean;

import java.io.Serializable;

import com.google.gson.JsonObject;

//{"mtype":2,"info":{"total":"1.00","duration":"null","carnumber":"¾©F8KR99",
//"etime":"20:08","state":"1","btime":"20:03","orderid":"1506"}}
@SuppressWarnings("serial")
public class PullMessage implements Serializable {
	private String  mtype;//
	private JsonObject info;
	
	
	public PullMessage() {
		super();
	}
	
	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public JsonObject getInfo() {
		return info;
	}

	public void setInfo(JsonObject info) {
		this.info = info;
	}

	@Override
	public String toString() {
		return "PullMessage [mtype=" + mtype + ", info=" + info + "]";
	}
}

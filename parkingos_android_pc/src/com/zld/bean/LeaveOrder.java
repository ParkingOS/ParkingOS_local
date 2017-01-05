package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class LeaveOrder implements Serializable {
//
//	<content id='1'>
//	<info>
//	<id>142</id>
//	<issale>1</issale>
//	<total>12.00</total>
//	<carnumber>京A54321</carnumber>
//	<etime>14:17</etime>
//	<btime>10:46</btime>
//	<state>0</state>
//	<mtype>1</mtype>
//	<orderid>47</orderid>
//	</info>
//	<info>
//	{"mtype":2,"info":{"total":"1.00","duration":"null","carnumber":"京F8KR99","etime":"20:08","state":"1",
//	"btime":"20:03","orderid":"1506"}}
	private String state;// 0未支付.1.已支付.2.现金支付3.支付中；
	private String carnumber;// 车牌
	private String total;// 金额
	private String btime;// 开始时间
	private String etime;// 开始时间
	private  int   id;// 消息编号
	private String orderid;// 订单号
	private String mtype;// 消息类型 -1 token无效 0 离场订单；
	private String issale;// 是否优惠 0否.1是
	private int maxid;// 当前最大的curri；

	public String getState() {
		return state;
	}

	public int getMaxid() {
		return maxid;
	}

	public void setMaxid(int maxid) {
		this.maxid = maxid;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCarnumber() {
		return carnumber;
	}

	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getBtime() {
		return btime;
	}

	public void setBtime(String btime) {
		this.btime = btime;
	}

	public String getEtime() {
		return etime;
	}

	public void setEtime(String etime) {
		this.etime = etime;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getMtype() {
		return mtype;
	}

	public void setMtype(String mtype) {
		this.mtype = mtype;
	}

	public String getIssale() {
		return issale;
	}

	public void setIssale(String issale) {
		this.issale = issale;
	}

	public LeaveOrder() {
		super();
	}

	@Override
	public String toString() {
		return "LeaveOrder [state=" + state + ", carnumber=" + carnumber
				+ ", total=" + total + ", btime=" + btime + ", etime=" + etime
				+ ", id=" + id + ", orderid=" + orderid + ", mtype=" + mtype
				+ ", issale=" + issale + ", maxid=" + maxid + "]";
	}

}

package com.zld.bean;

import java.io.Serializable;

@SuppressWarnings("serial")
public class CarNumberOrder implements Serializable{
//	{"total":"4.0","carnumber":"川ZL1Z11","duration":"21分钟","etime":"19:58","btime":"19:37","uin":"-1","orderid":"48260","collect":"2.0","discount":"2.0"}
	
	public String total;//总金额
	public String carnumber;//车牌号
	public String etime;//结束时间
	public String btime;//开始时间
	public String orderid;//订单编号
	public String localid;//本地订单编号
	public String collect;//结算金额
	public String discount;//减免金额
	public String ctype; // 进场方式标识  5是月卡用户  7是月卡第二辆车

	// -------------
	public String befcollect;//减免之前的停车费总金额
	public String distotal;//减免券抵扣的金额
	public String shopticketid;//减免券ID，用于判断有没有使用减免券
	public String tickettype;//减免券类型 3：减时券 4：全免券
	public String tickettime;//减时券时长
	// -----------------
	public String duration;//时长
	public String uin;//-1未绑定 其他都是绑定
	public String hascard;//是否有车牌
	private String ismonthuser;//包月用户标识	
	private String prepay;//预支付
	private String limitday;// 月卡到期时间
	
	private String lefttop;
	private String width;
	private String height;
	private String rightbottom;
	private String car_type;
	private String state;
	
	public String getLefttop() {
		return lefttop;
	}

	public String getLocalid() {
		return localid;
	}

	public void setLocalid(String localid) {
		this.localid = localid;
	}

	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}
	
	public String getCtype() {
		return ctype;
	}

	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

	public String getWidth() {
		return width;
	}

	public void setWidth(String width) {
		this.width = width;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getRightbottom() {
		return rightbottom;
	}

	public void setRightbottom(String rightbottom) {
		this.rightbottom = rightbottom;
	}

	public String getIsmonthuser() {
		return ismonthuser;
	}

	public void setIsmonthuser(String ismonthuser) {
		this.ismonthuser = ismonthuser;
	}

	public CarNumberOrder() {
		super();
	}
	
	public String getHascard() {
		return hascard;
	}

	public void setHascard(String hascard) {
		this.hascard = hascard;
	}

	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getCarnumber() {
		return carnumber;
	}
	public void setCarnumber(String carnumber) {
		this.carnumber = carnumber;
	}
	public String getEtime() {
		return etime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}
	public String getBtime() {
		return btime;
	}
	public void setBtime(String btime) {
		this.btime = btime;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getCollect() {
		return collect;
	}
	public void setCollect(String collect) {
		this.collect = collect;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	public String getDuration() {
		return duration;
	}
	public String getBefcollect() {
		return befcollect;
	}

	public void setBefcollect(String befcollect) {
		this.befcollect = befcollect;
	}

	public String getDistotal() {
		return distotal;
	}

	public void setDistotal(String distotal) {
		this.distotal = distotal;
	}

	public String getShopticketid() {
		return shopticketid;
	}

	public void setShopticketid(String shopticketid) {
		this.shopticketid = shopticketid;
	}

	public String getTickettype() {
		return tickettype;
	}

	public void setTickettype(String tickettype) {
		this.tickettype = tickettype;
	}

	public String getTickettime() {
		return tickettime;
	}

	public void setTickettime(String tickettime) {
		this.tickettime = tickettime;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getUin() {
		return uin;
	}
	public void setUin(String uin) {
		this.uin = uin;
	}
	
	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	
	public String getPrepay() {
		return prepay;
	}

	public void setPrepay(String prepay) {
		this.prepay = prepay;
	}

	public String getLimitday() {
		return limitday;
	}

	public void setLimitday(String limitday) {
		this.limitday = limitday;
	}
	
	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	@Override
	public String toString() {
		return "CarNumberOrder [total=" + total + ", carnumber=" + carnumber + ", etime=" + etime + ", btime=" + btime
				+ ", orderid=" + orderid + ", localid=" + localid + ", collect=" + collect + ", discount=" + discount
				+ ", ctype=" + ctype + ", befcollect=" + befcollect + ", distotal=" + distotal + ", shopticketid="
				+ shopticketid + ", tickettype=" + tickettype + ", tickettime=" + tickettime + ", duration=" + duration
				+ ", uin=" + uin + ", hascard=" + hascard + ", ismonthuser=" + ismonthuser + ", prepay=" + prepay
				+ ", limitday=" + limitday + ", lefttop=" + lefttop + ", width=" + width + ", height=" + height
				+ ", rightbottom=" + rightbottom + ", car_type=" + car_type + ", state=" + state + "]";
	}

	public CarNumberOrder(String total, String carnumber, String etime,
			String btime, String orderid, String collect, String discount, 
			String befcollect, String distotal,String shopticketid,
			String tickettype, String tickettime,
			String duration, String uin, String hascard, String ismonthuser,
			String prepay, String lefttop, String width, String height,
			String rightbottom, String car_type ,String state) {
		super();
		this.total = total;
		this.carnumber = carnumber;
		this.etime = etime;
		this.btime = btime;
		this.orderid = orderid;
		this.collect = collect;
		this.discount = discount;
		this.befcollect = befcollect;
		this.distotal = distotal;
		this.shopticketid = shopticketid;
		this.tickettype = tickettype;
		this.tickettime = tickettime;
		this.duration = duration;
		this.uin = uin;
		this.hascard = hascard;
		this.ismonthuser = ismonthuser;
		this.prepay = prepay;
		this.lefttop = lefttop;
		this.width = width;
		this.height = height;
		this.rightbottom = rightbottom;
		this.car_type = car_type;
		this.state = state;
	}

}

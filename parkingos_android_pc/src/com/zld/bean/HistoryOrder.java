/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年6月1日 
 * 
 *******************************************************************************/ 
package com.zld.bean;

/**
 * <pre>
 * 功能说明: 
 * 日期:	2015年6月1日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年6月1日
 * </pre>
 */
public class HistoryOrder {
	/*[{"total":"1.41","carnumber":"京FF6203","ismonthuser":"0","width":"265","state":"1",
		"btime":"2015-05-26 14:30","car_type":"0","id":"787323","duration":"停车 5天 20小时57分钟",
		"height":"105","rightbottom":"527","lefttop":"600","ptype":"1"},*/
	private String total;//金额
	private String carnumber;//车牌号
	private String ismonthuser;//月卡
	private String width;//小图宽
	private String state;//结算状态
	private String btime;//入场时间
	private String car_type;//车辆类型
	private String id;//订单号
	private String duration;//停车时长
	private String height;//高
	private String rightbottom;//右底
	private String lefttop;//左高
	private String ptype;//通道类型,1为出口
	private String ctype;
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
	public String getIsmonthuser() {
		return ismonthuser;
	}
	public void setIsmonthuser(String ismonthuser) {
		this.ismonthuser = ismonthuser;
	}
	public String getWidth() {
		return width;
	}
	public void setWidth(String width) {
		this.width = width;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getBtime() {
		return btime;
	}
	public void setBtime(String btime) {
		this.btime = btime;
	}
	public String getCar_type() {
		return car_type;
	}
	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
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
	public String getLefttop() {
		return lefttop;
	}
	public void setLefttop(String lefttop) {
		this.lefttop = lefttop;
	}
	public String getPtype() {
		return ptype;
	}
	public void setPtype(String ptype) {
		this.ptype = ptype;
	}

	@Override
	public String toString() {
		return "HistoryOrder [total=" + total + ", carnumber=" + carnumber
				+ ", ismonthuser=" + ismonthuser + ", width=" + width
				+ ", state=" + state + ", btime=" + btime + ", car_type="
				+ car_type + ", id=" + id + ", duration=" + duration
				+ ", height=" + height + ", rightbottom=" + rightbottom
				+ ", lefttop=" + lefttop + ", ptype=" + ptype + ", ctype="
				+ ctype + "]";
	}
	/**
	 * @return the ctype
	 */
	public String getCtype() {
		return ctype;
	}
	/**
	 * @param ctype the ctype to set
	 */
	public void setCtype(String ctype) {
		this.ctype = ctype;
	}

}

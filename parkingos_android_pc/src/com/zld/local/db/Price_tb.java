//package com.zld.local.db;
//
//public class Price_tb {
//	// price_tb 价格表
//	// id bigint NOT NULL,
//	// comid bigint,
//	// price numeric(10,2) DEFAULT 0,
//	// state bigint DEFAULT 0, -- 0正常，1注销
//	// unit integer,
//	// pay_type integer, -- 0:按时段，1：按次
//	// create_time bigint,
//	// b_time integer,
//	// e_time integer,
//	// is_sale integer DEFAULT 0, -- 是否打折 0否，1是
//	// first_times integer DEFAULT 0, -- 首优惠时段，单位分钟
//	// fprice numeric(10,2) DEFAULT 0, -- 首优惠价格
//	// countless integer DEFAULT 0, -- 零头计费时长，单位分钟
//	// free_time integer DEFAULT 0, -- 免费时长，单位:分钟
//	// fpay_type integer DEFAULT 0, -- 超免费时长计费方式，1:免费 ，0:收费
//	// isnight integer DEFAULT 0, -- 夜晚停车，0:支持，1不支持
//	// isedit integer DEFAULT 0, -- 是否可编辑价格，目前只对日间按时价格生效,0否，1是，默认0
//	// car_type integer DEFAULT 0, -- 0：通用，1：小车，2：大车
//	// is_fulldaytime integer DEFAULT 0, -- 是否补足日间时长
//	// update_time bigint,
//
//	public String id;
//	public String comid;
//	public String price;
//	public String state;
//	public String unit;
//	public String pay_type;
//	public String create_time;
//	public String b_time;
//	public String e_time;
//	public String is_sale;
//	public String first_times;
//	public String fprice;
//	public String countless;
//	public String free_time;
//	public String fpay_type;
//	public String isnight;
//	public String isedit;
//	public String car_type;
//	public String is_fulldaytime;
//	public String update_time;
//
//	public Price_tb() {
//		super();
//	}
//
//	public Price_tb(String id, String comid, String price, String state, String unit, String pay_type, String create_time,
//			String b_time, String e_time, String is_sale, String first_times, String fprice, String countless, String free_time,
//			String fpay_type, String isnight, String isedit, String car_type, String is_fulldaytime, String update_time) {
//		super();
//		this.id = id;
//		this.comid = comid;
//		this.price = price;
//		this.state = state;
//		this.unit = unit;
//		this.pay_type = pay_type;
//		this.create_time = create_time;
//		this.b_time = b_time;
//		this.e_time = e_time;
//		this.is_sale = is_sale;
//		this.first_times = first_times;
//		this.fprice = fprice;
//		this.countless = countless;
//		this.free_time = free_time;
//		this.fpay_type = fpay_type;
//		this.isnight = isnight;
//		this.isedit = isedit;
//		this.car_type = car_type;
//		this.is_fulldaytime = is_fulldaytime;
//		this.update_time = update_time;
//	}
//
//	@Override
//	public String toString() {
//		return "Price_tb [id=" + id + ", comid=" + comid + ", price=" + price + ", state=" + state + ", unit=" + unit
//				+ ", pay_type=" + pay_type + ", create_time=" + create_time + ", b_time=" + b_time + ", e_time=" + e_time
//				+ ", is_sale=" + is_sale + ", first_times=" + first_times + ", fprice=" + fprice + ", countless=" + countless
//				+ ", free_time=" + free_time + ", fpay_type=" + fpay_type + ", isnight=" + isnight + ", isedit=" + isedit
//				+ ", car_type=" + car_type + ", is_fulldaytime=" + is_fulldaytime + ", update_time=" + update_time + "]";
//	}
//
//
//
//}

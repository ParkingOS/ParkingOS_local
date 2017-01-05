//package com.zld.local.db;
//
//public class Order_tb {
//
//	// order_tb 订单表
//	// id bigint NOT NULL,
//	// create_time bigint,
//	// comid bigint NOT NULL,
//	// uin bigint NOT NULL,--车主账号；
//	// total numeric(30,2),
//	// state integer, -- 0未支付 1已支付 2:逃单.6断网结算
//	// end_time bigint,
//	// auto_pay integer DEFAULT 0, -- 自动结算，0：否，1：是
//	// pay_type integer DEFAULT 0, -- 0:帐户支付,1:现金支付,2:手机支付 3月卡8免费
//	// nfc_uuid character varying(36),
//	// c_type integer DEFAULT 1, -- 0:NFC,1:IBeacon,2:照牌 3通道照牌 4直付 5月卡用户
//	// uid bigint DEFAULT (-1), -- 收费员帐号
//	// car_number character varying(50), -- 车牌
//	// imei character varying(50), -- 手机串号
//	// pid integer DEFAULT (-1), --价格
//	// 计费方式：0按时(0.5/15分钟)，1按次（12小时内10元,前1/30min，后每小时1元）
//	// car_type integer DEFAULT 0, -- 0：通用，1：小车，2：大车
//	// pre_state integer DEFAULT 0, -- 预支付状态 0 无，1预支付中，2等待车主支付完成
//	// in_passid bigint DEFAULT (-1), -- 进口通道id
//	// out_passid bigint DEFAULT (-1), -- 出口通道id
//
//	public String id;
//	public String create_time;
//	public String comid;
//	public String uin;
//	public String total;
//	public String prepay;
//	public String state;
//	public String end_time;
//	public String auto_pay;
//	public String pay_type;
//	public String nfc_uuid;
//	public String c_type;
//	public String uid;
//	public String car_number;
//	public String imei;
//	public String pid;
//	public String car_type;
//	public String pre_state;
//	public String in_passid;
//	public String out_passid;
//	public String localid;
//
//	public Order_tb() {
//		super();
//	}
//
//	/**
//	 * @param id 订单编号
//	 * @param create_time 创建时间
//	 * @param comid 车场编号
//	 * @param state 订单状态 0未结
//	 * @param nfc_uuid 
//	 * @param c_type 0:NFC,1:IBeacon,2:照牌 3通道照牌 4直付 5月卡用户
//	 * @param uid 收费员编号
//	 * @param car_number 车牌
//	 * @param imei 手机串号
//	 */
//	public Order_tb(String id, String create_time, String comid, String state, String nfc_uuid, String c_type, String uid,
//			String car_number, String imei) {
//		super();
//		this.id = id;
//		this.create_time = create_time;
//		this.comid = comid;
//		this.state = state;
//		this.nfc_uuid = nfc_uuid;
//		this.c_type = c_type;
//		this.uid = uid;
//		this.car_number = car_number;
//		this.imei = imei;
//	}
//	/**
//	 * 
//	 * @param id orderid
//	 * @param create_time
//	 * @param comid
//	 * @param uin 车主账号
//	 * @param total
//	 * @param state 订单状态
//	 * @param end_time
//	 * @param auto_pay  自动结算，0：否，1：是
//	 * @param pay_type DEFAULT 0, -- 0:帐户支付,1:现金支付,2:手机支付 3月卡8免费
//	 * @param nfc_uuid 
//	 * @param c_type 0:NFC,1:IBeacon,2:照牌 3通道照牌 4直付 5月卡用户
//	 * @param uid 收费员编号
//	 * @param car_number 车牌号
//	 * @param imei 手机串号
//	 * @param pid 价格 按时 按次
//	 * @param car_type 0：通用，1：小车，2：大车
//	 * @param pre_state 预支付状态 0 无，1预支付中，2等待车主支付完成
//	 * @param in_passid 进口通道id
//	 * @param out_passid 出口通道id
//	 */
//	public Order_tb(
//			String id,String localid, String create_time, String comid, String uin,
//			String total, String prepay, String state, String end_time,String auto_pay,
//			String pay_type, String nfc_uuid, String c_type, String uid, String car_number,
//			String imei,String pid, String car_type, String pre_state, String in_passid,
//			String out_passid) {
//		super();
//		this.id = id;
//		this.create_time = create_time;
//		this.comid = comid;
//		this.uin = uin;
//		this.total = total;
//		this.prepay = prepay;
//		this.state = state;
//		this.end_time = end_time;
//		this.auto_pay = auto_pay;
//		this.pay_type = pay_type;
//		this.nfc_uuid = nfc_uuid;
//		this.c_type = c_type;
//		this.uid = uid;
//		this.car_number = car_number;
//		this.imei = imei;
//		this.pid = pid;
//		this.car_type = car_type;
//		this.pre_state = pre_state;
//		this.in_passid = in_passid;
//		this.out_passid = out_passid;
//		this.localid = localid;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getCreate_time() {
//		return create_time;
//	}
//
//	public void setCreate_time(String create_time) {
//		this.create_time = create_time;
//	}
//
//	public String getComid() {
//		return comid;
//	}
//
//	public void setComid(String comid) {
//		this.comid = comid;
//	}
//
//	public String getUin() {
//		return uin;
//	}
//
//	public void setUin(String uin) {
//		this.uin = uin;
//	}
//
//	public String getTotal() {
//		return total;
//	}
//
//	public void setTotal(String total) {
//		this.total = total;
//	}
//
//	public String getPrepay() {
//		return prepay;
//	}
//
//	public void setPrepay(String prepay) {
//		this.prepay = prepay;
//	}
//
//	public String getState() {
//		return state;
//	}
//
//	public void setState(String state) {
//		this.state = state;
//	}
//
//	public String getEnd_time() {
//		return end_time;
//	}
//
//	public void setEnd_time(String end_time) {
//		this.end_time = end_time;
//	}
//
//	public String getAuto_pay() {
//		return auto_pay;
//	}
//
//	public void setAuto_pay(String auto_pay) {
//		this.auto_pay = auto_pay;
//	}
//
//	public String getPay_type() {
//		return pay_type;
//	}
//
//	public void setPay_type(String pay_type) {
//		this.pay_type = pay_type;
//	}
//
//	public String getNfc_uuid() {
//		return nfc_uuid;
//	}
//
//	public void setNfc_uuid(String nfc_uuid) {
//		this.nfc_uuid = nfc_uuid;
//	}
//
//	public String getC_type() {
//		return c_type;
//	}
//
//	public void setC_type(String c_type) {
//		this.c_type = c_type;
//	}
//
//	public String getUid() {
//		return uid;
//	}
//
//	public void setUid(String uid) {
//		this.uid = uid;
//	}
//
//	public String getCar_number() {
//		return car_number;
//	}
//
//	public void setCar_number(String car_number) {
//		this.car_number = car_number;
//	}
//
//	public String getImei() {
//		return imei;
//	}
//
//	public void setImei(String imei) {
//		this.imei = imei;
//	}
//
//	public String getPid() {
//		return pid;
//	}
//
//	public void setPid(String pid) {
//		this.pid = pid;
//	}
//
//	public String getCar_type() {
//		return car_type;
//	}
//
//	public void setCar_type(String car_type) {
//		this.car_type = car_type;
//	}
//
//	public String getPre_state() {
//		return pre_state;
//	}
//
//	public void setPre_state(String pre_state) {
//		this.pre_state = pre_state;
//	}
//
//	public String getIn_passid() {
//		return in_passid;
//	}
//
//	public void setIn_passid(String in_passid) {
//		this.in_passid = in_passid;
//	}
//
//	public String getOut_passid() {
//		return out_passid;
//	}
//
//	public void setOut_passid(String out_passid) {
//		this.out_passid = out_passid;
//	}
//
//	public String getLocalid() {
//		return localid;
//	}
//
//	public void setLocalid(String localid) {
//		this.localid = localid;
//	}
//
//	@Override
//	public String toString() {
//		return "Order_tb [id=" + id + ", create_time=" + create_time
//				+ ", comid=" + comid + ", uin=" + uin + ", total=" + total
//				+ ", prepay=" + prepay + ", state=" + state + ", end_time="
//				+ end_time + ", auto_pay=" + auto_pay + ", pay_type="
//				+ pay_type + ", nfc_uuid=" + nfc_uuid + ", c_type=" + c_type
//				+ ", uid=" + uid + ", car_number=" + car_number + ", imei="
//				+ imei + ", pid=" + pid + ", car_type=" + car_type
//				+ ", pre_state=" + pre_state + ", in_passid=" + in_passid
//				+ ", out_passid=" + out_passid + ", localid=" + localid + "]";
//	}
//
//}

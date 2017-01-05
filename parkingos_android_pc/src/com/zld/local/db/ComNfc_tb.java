//package com.zld.local.db;
//
//public class ComNfc_tb {
//
//	// CREATE TABLE com_nfc_tb
//	// (
//	// id bigint NOT NULL,
//	// nfc_uuid character varying(35),
//	// comid bigint,
//	// create_time bigint,
//	// state bigint, -- 0正常，1禁用
//	// use_times integer,
//	// uin bigint DEFAULT (-1), -- -- 车主帐号
//	// uid bigint DEFAULT (-1), -- 收费员编号
//	// update_time bigint DEFAULT 0, -- 更新时间,绑定用户时间
//	// nid bigint DEFAULT 0, -- 扫描NFC的二维码号
//	// qrcode character varying, -- 二维码
//	// )
//
//	public String id;
//	public String nfc_uuid;
//	public String comid;
//	public String create_time;
//	public String state;
//	public String use_times;
//	public String uin;
//	public String uid;
//	public String update_time;
//	public String nid;
//	public String qrcode;
//
//	public ComNfc_tb() {
//		super();
//	}
//
//	public ComNfc_tb(String id, String nfc_uuid, String comid, String create_time, String state, String use_times, String uin,
//			String uid, String update_time, String nid, String qrcode) {
//		super();
//		this.id = id;
//		this.nfc_uuid = nfc_uuid;
//		this.comid = comid;
//		this.create_time = create_time;
//		this.state = state;
//		this.use_times = use_times;
//		this.uin = uin;
//		this.uid = uid;
//		this.update_time = update_time;
//		this.nid = nid;
//		this.qrcode = qrcode;
//	}
//
//	@Override
//	public String toString() {
//		return "ComNfc_tb [id=" + id + ", nfc_uuid=" + nfc_uuid + ", comid=" + comid + ", create_time=" + create_time
//				+ ", state=" + state + ", use_times=" + use_times + ", uin=" + uin + ", uid=" + uid + ", update_time="
//				+ update_time + ", nid=" + nid + ", qrcode=" + qrcode + "]";
//	}
//
//}

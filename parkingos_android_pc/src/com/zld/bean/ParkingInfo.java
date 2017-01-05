package com.zld.bean;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class ParkingInfo implements Serializable {

	private String service;//人工服务
	private String parkingtotal;// 总车位数
	private String phone; // 车场电话
	private String parktype; // 车场类型
	private String price; // 车场价格
	private String address; // 地址
	private String name; // 车场名称
	private String mobile; // 手机号
	private String stoptype; // 停车类型
	private String timebet; // 开放时间段
	private String id; // 公司编号
	private String picurls;
	private String resume; // 车场描述
	private String isfixed;// -- 0:未定位 1已定位
	private String longitude;// 经度
	private String latitude;// 纬度
	private String car_type = "";//是否区分大小车
	private int passfree;//是否支持免费
	private String ishidehdbutton;//是否显示结算订单按钮，1显示 0隐藏
	private String issuplocal;//是否支持本地化,1支持 0不支持
	private List<CarType> allCarTypes; // 所有车辆类型
	private List<FreeResons> freereasons; // 所有免费类型
	private List<LiftReason> liftreason; // 手动抬杆原因
	private String ishdmoney;  // 是否显示收费累加金额 0是显示 1是隐藏
	private String fullset;		//车位已满能否进场
	private String leaveset;	//车场识别识别抬杆设置  （有的月卡车场没人收费（不收费））

	public List<LiftReason> getLiftReason() {
		return liftreason;
	}

	public void setLiftReason(List<LiftReason> liftReason) {
		this.liftreason = liftReason;
	}

	public List<FreeResons> getFreeResons() {
		return freereasons;
	}

	public void setFreeResons(List<FreeResons> freereasons) {
		this.freereasons = freereasons;
	}

	public List<CarType> getAllCarTypes() {
		return allCarTypes;
	}

	public void setAllCarTypes(List<CarType> allCarTypes) {
		this.allCarTypes = allCarTypes;
	}

	public String getIssuplocal() {
		return issuplocal;
	}

	public void setIssuplocal(String issuplocal) {
		this.issuplocal = issuplocal;
	}

	public String getIshidehdbutton() {
		return ishidehdbutton;
	}

	public void setIshidehdbutton(String ishidehdbutton) {
		this.ishidehdbutton = ishidehdbutton;
	}

	public int getPassfree() {
		return passfree;
	}

	public void setPassfree(int passfree) {
		this.passfree = passfree;
	}

	public ParkingInfo() {
		super();
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getPicurls() {
		return picurls;
	}

	public void setPicurls(String picurls) {
		this.picurls = picurls;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParkingtotal() {
		return parkingtotal;
	}

	public void setParkingtotal(String parkingtotal) {
		this.parkingtotal = parkingtotal;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getParktype() {
		return parktype;
	}

	public void setParktype(String parktype) {
		this.parktype = parktype;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getStoptype() {
		return stoptype;
	}

	public void setStoptype(String stoptype) {
		this.stoptype = stoptype;
	}

	public String getTimebet() {
		return timebet;
	}

	public void setTimebet(String timebet) {
		this.timebet = timebet;
	}

	public String getResume() {
		return resume;
	}

	public void setResume(String resume) {
		this.resume = resume;
	}

	public String getIsfixed() {
		return isfixed;
	}

	public void setIsfixed(String isfixed) {
		this.isfixed = isfixed;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getCar_type() {
		return car_type;
	}

	public void setCar_type(String car_type) {
		this.car_type = car_type;
	}

	public String getIsShowMoney() {
		return ishdmoney;
	}

	public void setIsHDShowMoney(String isShowMoney) {
		this.ishdmoney = isShowMoney;
	}

	

	@Override
	public String toString() {
		return "ParkingInfo [service=" + service + ", parkingtotal="
				+ parkingtotal + ", phone=" + phone + ", parktype=" + parktype
				+ ", price=" + price + ", address=" + address + ", name="
				+ name + ", mobile=" + mobile + ", stoptype=" + stoptype
				+ ", timebet=" + timebet + ", id=" + id + ", picurls="
				+ picurls + ", resume=" + resume + ", isfixed=" + isfixed
				+ ", longitude=" + longitude + ", latitude=" + latitude
				+ ", car_type=" + car_type + ", passfree=" + passfree
				+ ", ishidehdbutton=" + ishidehdbutton + ", issuplocal="
				+ issuplocal + ", allCarTypes=" + allCarTypes
				+ ", freereasons=" + freereasons + ", liftreason=" + liftreason
				+ ", ishdmoney=" + ishdmoney + ", fullset=" + fullset
				+ ", leaveset=" + leaveset + "]";
	}

	public String getFullset() {
		return fullset;
	}

	public void setFullset(String fullset) {
		this.fullset = fullset;
	}

	public String getLeaveset() {
		return leaveset;
	}

	public void setLeaveset(String leaveset) {
		this.leaveset = leaveset;
	}

}

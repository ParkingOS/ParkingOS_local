/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月15日 
 * 
 *******************************************************************************/ 
package com.zld.bean;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.zld.lib.util.SharedPreferencesUtils;


/**
 * <pre>
 * 功能说明: 登录车场获取的信息
 * 日期:	2015年4月15日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月15日
 * </pre>
 */
public class AppInfo {

	private String name;			//收费员
	private String token; 			//token
	private String comid; 			//车场id
	private String parkName;        //车场名字
	private String uid;				//用户账号
	private boolean parkBilling; 	//区分大小车
	private boolean passfree;		//是否免费
	private boolean ishdmoney;      //是否显示收费累计金额 0是显示 1是隐藏
	private String ishidehdbutton; 	//是否显示结算订单按钮
	private String issuplocal;		//是否支持本地化
	private String equipmentModel;	//设备
	private String imei;
	private String stname;			//工作站名称
	private List<CarType> allCarTypes; // 所有车辆类型
	private List<FreeResons> freeResons; // 车辆免费类型
	private List<LiftReason> liftreason;       //抬杆原因 
	private String fullset;		//车位已满能否进场
	private String leaveset;	//车场识别识别抬杆设置  （有的月卡车场没人收费（不收费））

	//AppInfo实例  
	private static AppInfo appInfo = new AppInfo(); 

	/** 保证只有一个AppInfo实例 */  
	private AppInfo() {  
	}  

	/** 获取AppInfo实例 ,单例模式 */  
	public static AppInfo getInstance() {
		
		return appInfo;  
	}

	
	public boolean isIshdmoney() {
		return ishdmoney;
	}

	public void setIshdmoney(boolean ishdmoney) {
		this.ishdmoney = ishdmoney;
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

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getComid() {
		return comid;
	}

	public void setComid(String comid) {
		this.comid = comid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public boolean isParkBilling() {
		return parkBilling;
	}

	public void setParkBilling(boolean parkBilling) {
		this.parkBilling = parkBilling;
	}

	public boolean isPassfree() {
		return passfree;
	}

	public void setPassfree(boolean passfree) {
		this.passfree = passfree;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEquipmentModel() {
		return equipmentModel;
	}

	public void setEquipmentModel(String equipmentModel) {
		this.equipmentModel = equipmentModel;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getStname() {
		return stname;
	}

	public void setStname(String stname) {
		this.stname = stname;
	}

	public String getIshidehdbutton() {
		return ishidehdbutton;
	}

	public void setIshidehdbutton(String ishidehdbutton) {
		this.ishidehdbutton = ishidehdbutton;
	}

	public String getIssuplocal() {
		return issuplocal;
	}

	public void setIssuplocal(String issuplocal) {
		this.issuplocal = issuplocal;
	}

	public static AppInfo getAppInfo() {
		return appInfo;
	}

	public static void setAppInfo(AppInfo appInfo) {
		AppInfo.appInfo = appInfo;
	}

	/**
	 * Service里是直接获取
	 * @param activity
	 * @return
	 */
	public boolean getIsLocalServer(Activity activity){
		//是否是本地服务器
		boolean isLocalServer = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(), "nettype","isLocalServer", false);
		return isLocalServer;
	}
	public List<CarType> getAllCarTypes() {
		return allCarTypes;
	}

	public void setAllCarTypes(List<CarType> allCarTypes) {
		Log.e("", "生成订单url---------------->>>>>" + AppInfo.getInstance().getAllCarTypes());
		this.allCarTypes = allCarTypes;
	}

	public List<FreeResons> getFreeResons() {
		return freeResons;
	}

	public void setFreeResons(List<FreeResons> freeResons) {
		this.freeResons = freeResons;
	}
	
	public List<LiftReason> getLiftreason() {
		return liftreason;
	}

	public void setLiftreason(List<LiftReason> liftreason) {
		this.liftreason = liftreason;
	}

	public boolean getIsShowhdmoney() {
		return ishdmoney;
	}

	public void setIsShowhdmoney(boolean ishdmoney) {
		this.ishdmoney = ishdmoney;
	}
	
	public String getParkName() {
		return parkName;
	}

	public void setParkName(String parkName) {
		this.parkName = parkName;
	}


	@Override
	public String toString() {
		return "AppInfo [name=" + name + ", token=" + token + ", comid=" + comid + ", parkName=" + parkName + ", uid="
				+ uid + ", parkBilling=" + parkBilling + ", passfree=" + passfree + ", ishdmoney=" + ishdmoney
				+ ", ishidehdbutton=" + ishidehdbutton + ", issuplocal=" + issuplocal + ", equipmentModel="
				+ equipmentModel + ", imei=" + imei + ", stname=" + stname + ", allCarTypes=" + allCarTypes
				+ ", freeResons=" + freeResons + ", liftreason=" + liftreason + ", fullset=" + fullset + ", leaveset="
				+ leaveset + "]";
	}

	public boolean getIsLocalServer(Context context) {
		// TODO Auto-generated method stub
		//是否是本地服务器
		boolean isLocalServer = SharedPreferencesUtils.getParam(
				context.getApplicationContext(), "nettype","isLocalServer", false);
		return isLocalServer;
	}

}

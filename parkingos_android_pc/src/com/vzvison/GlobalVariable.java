//package com.vzvison;
//
//
//
//import com.vzvison.database.SnapImageTable;
//import com.vzvison.database.plateCallbackInfoTable;
//import com.vzvison.device.DeviceSet;
//import com.vzvison.vz.WlistVehicle;
//import com.zld.application;
//
//import android.app.Application;
//
//public class GlobalVariable extends application{
//    
//	private plateCallbackInfoTable  plateTable =null;
//	private DeviceSet               devSet = null;
//	private SnapImageTable          snapImageTable = null;
//	private WlistVehicle          wlistVechile = null;
//	
//	@Override
//	public void onCreate() {
//		super.onCreate();
//	}
//	
//	void setplateCallbackInfoTable(plateCallbackInfoTable table)
//	{
//		plateTable = table;
//	}
//	
//	SnapImageTable getSnapImageTable()
//	{
//		return snapImageTable;
//	}
//	
//	void setSnapImageTable(SnapImageTable table)
//	{
//		snapImageTable = table;
//	}
//	
//	plateCallbackInfoTable getplateCallbackInfoTable()
//	{
//		return plateTable;
//	}
//	
//	void setDeviceSet(DeviceSet ds)
//	{
//		devSet = ds;
//	}
//	
//	DeviceSet getDeviceSet()
//	{
//		return devSet;
//	}
//	
//	void setWlistVehicle(WlistVehicle ds)
//	{
//		wlistVechile = ds;
//	}
//	
//	WlistVehicle getWlistVehicle()
//	{
//		return wlistVechile;
//	}
//}

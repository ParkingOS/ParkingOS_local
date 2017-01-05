package com.zld.bean;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

public class WifiAdmin {
	private WifiManager wifiManager;
	private List<WifiConfiguration> configuratedList;
	
	public WifiAdmin(Context context) {
		// TODO Auto-generated constructor stub
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}
	public List<WifiConfiguration> getConfiguratedList(){
		configuratedList = wifiManager.getConfiguredNetworks();
		return configuratedList;
	}
	public void connectWifi(int netId){
		wifiManager.enableNetwork(netId, true);
		
	} 
	
	public boolean getPingResult(){
		
		return wifiManager.pingSupplicant();
	}
	
	public boolean isWifiEnable(){
		return wifiManager.isWifiEnabled();
	}
	
	public List<ScanResult> getScanResultList(){
		wifiManager.startScan();
		return wifiManager.getScanResults();
	}
	
	public WifiInfo getCurrentWifiInfo(){
		return wifiManager.getConnectionInfo();
	}

}

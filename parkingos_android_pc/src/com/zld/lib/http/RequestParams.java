package com.zld.lib.http;

import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;

public class RequestParams {
	private String urlHeader;
	private String TAG = "RequestParams";
	private HashMap<String, String> paramsMap = new HashMap<String, String>();

	public void setUrlHeader(String urlHeader){
		this.urlHeader = urlHeader;
		Log.e(TAG, urlHeader);
	}
	
	public void clearParams(){
		paramsMap.clear();
	}

	public void setUrlParams(String key, String value){
		paramsMap.put(key, value);
	}
	
	public void setUrlParams(String key, int value){
		paramsMap.put(key, value+"");
	}

	@SuppressWarnings("rawtypes")
	public String getRequstUrl(){
		String url = urlHeader;
		Iterator iterator = paramsMap.keySet().iterator();

		while(iterator.hasNext()){
			String key = (String) iterator.next();
			if(paramsMap.get(key) != null ){
				System.out.println(paramsMap.get(key));
				url = url + "&" + key + "=" +paramsMap.get(key);
			}
			Log.e(TAG, key + ":" + paramsMap.get(key));
		}

		return url;
	}

}

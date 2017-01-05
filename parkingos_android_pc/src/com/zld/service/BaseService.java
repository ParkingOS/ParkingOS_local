package com.zld.service;

import com.zld.bean.LoginInfo;
import com.zld.lib.http.HttpCallBack;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BaseService extends Service  implements HttpCallBack {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String str) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String str1, String str2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer,
			String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String username,
			String password, LoginInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, boolean isSingle, String passid,
			String object, int i, String object2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, String username2,
			String password2, LoginInfo info2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, byte[] buffer, String username2,
			String password2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeout(String url, String str) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void timeout(String url, String str, String str2) {
		// TODO Auto-generated method stub
		
	}
}

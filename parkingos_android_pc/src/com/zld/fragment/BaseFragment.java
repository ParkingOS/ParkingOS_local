/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

//import com.androidquery.callback.AjaxStatus;
import com.zld.application;
import com.zld.bean.LoginInfo;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.lib.http.HttpCallBack;
import com.zld.lib.util.ImageUitls;
//import com.zld.local.db.LocalOrderDBManager;
import com.zld.ui.ZldNewActivity;

/**
 * <pre>
 * 功能说明: 
 * 日期:	2015年4月13日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月13日
 * </pre>
 */
public class BaseFragment extends Fragment implements HttpCallBack{
	public static final String TAG = "BaseFragment";
	public ZldNewActivity activity;
	SqliteManager sqliteManager;
//	public LocalOrderDBManager loDBManager;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.activity = (ZldNewActivity) activity;
//		getLocalOrderDBManager();
	}
	
	/**
	 * 初始化本地订单数据库管理
	 */
//	protected LocalOrderDBManager getLocalOrderDBManager() {
//		// TODO Auto-generated method stub
//		if(loDBManager == null){
//			application application = (application)getActivity().getApplication();
//			loDBManager = application.getLocalOrderDBManager(activity);
//		}
//		return loDBManager;
//	}

	/**
	 * 初始化数据库管理
	 */
	 SqliteManager getSqliteManager() {
		if (sqliteManager == null) {
			sqliteManager = 
					((application) activity.getApplication()).getSqliteManager(activity);
		}
		return sqliteManager;
	}
	
	/**
	 * 查询数据库,删除图片文件及数据库图片信息
	 * @param orderId
	 */
	protected void deleteOrderIamgeInfo(String orderId) {
		if(orderId == null){
			return;
		}
		UploadImg selectImage = getSqliteManager().selectImage(orderId);
		Log.e(TAG,"删除图片的信息："+selectImage.toString());
		if (selectImage != null) {
			String imgexitpath = selectImage.getImgexitpath();
			String imghomepath = selectImage.getImghomepath();
			if (imgexitpath != null) {
				ImageUitls.deleteImageFile(imgexitpath);
			}
			if(imghomepath!=null){
				ImageUitls.deleteImageFile(imghomepath);
			}
			getSqliteManager().deleteData(orderId);
		}
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public boolean doFailure(String url, AjaxStatus status) {
//		// TODO Auto-generated method stub
//		return false;
//	}

	/* (non-Javadoc)
	 * @see com.zld.lib.http.HttpCallBack#doSucess(java.lang.String, boolean, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public boolean doSucess(String url, boolean isSingle, String passid,
			String object, int i, String object2) {
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

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}
}

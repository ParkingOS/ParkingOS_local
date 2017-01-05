package com.zld.view;


import java.util.ArrayList;

import com.zld.R;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.bean.AppInfo;
import com.zld.bean.CarType;
import com.zld.fragment.BaseFragment;
import com.zld.fragment.OrderDetailsFragment;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

@SuppressLint({ "NewApi", "ValidFragment" })
public class SelectCarType extends BaseFragment{
	public static final int ENTRANCE = 0;
	public static final int EXIT = 1;
	public static final int HOMEEXIT = 2;
	private View parent;
	private Toast mToast;
	private String orderID;
	private Context activity;
	private OrderDetailsFragment orderDetailsFragment;
	

	final static String regularEx = "|";
	public EditText et_login_password;
	public AutoCompleteTextView at_login_username;

	private ListView listview;
	private PopupWindow popupWindow;
	public static String token = null;
	public static String comid = null; 
	private static final String TAG = "SelectCarType";

	public SelectCarType() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectCarType(ZldNewActivity activity, View parent,OrderDetailsFragment odf) {
		this.parent = parent;
		this.activity = activity;
		this.orderDetailsFragment = odf;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
	}
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	@SuppressLint("NewApi")
	public void showSwitchAccountView(String orderID) {
		showPopupWindow(parent);
		this.orderID = orderID;
	}
	public void closePop() {
		if (popupWindow != null) {
			popupWindow.dismiss();	
		}
	}
	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(activity.getApplicationContext(), text,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	/**
	 * 展示切换账号登录框
	 * @param parent
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void showPopupWindow(View parent) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
		if(listview == null){
			listview = (ListView) LayoutInflater.from(activity).inflate(R.layout.account_droplist, null); 
		}
		int count = AppInfo.getInstance().getAllCarTypes().size();
		final ArrayList<String> selectAllAccount = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			CarType type = AppInfo.getInstance().getAllCarTypes().get(i);
			selectAllAccount.add(type.getCarTypeName());
		}
		//目的是显示用户名列表底部的添加账号
		if(selectAllAccount.size() != 0){
			AccountDropListAdapter adapter = new AccountDropListAdapter(activity, selectAllAccount,true);
			listview.setAdapter(adapter);
		}
		if (popupWindow == null) {  
			popupWindow = new PopupWindow(listview,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			DisplayMetrics dm = new DisplayMetrics();
			//获取屏幕信息
			((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
//			popupWindow.setWidth((int)(screenWidth/8));
			popupWindow.setWidth((int)parent.getWidth());
		}  
		popupWindow.setFocusable(true);  
		popupWindow.setOutsideTouchable(true);  
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());  
		final int[] location = new int[2];  
		parent.getLocationOnScreen(location);  
		popupWindow.showAsDropDown(parent);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String account = selectAllAccount.get(position);
				Log.e(TAG,"选择车场点击的是："+account);
				if(account == null){
					return;
				}
				int count = AppInfo.getInstance().getAllCarTypes().size();
				final ArrayList<String> selectAllAccount = new ArrayList<String>();
				for (int i = 0; i < count; i++) {
					String name = AppInfo.getInstance().getAllCarTypes().get(i).getCarTypeName();
					if (name.equals(account)) {
						String carID = AppInfo.getInstance().getAllCarTypes().get(i).getCarTypeID();
						changeCarType(carID);
						popupWindow.dismiss();
						break;
					}
				}
			
			}
		});
		
	}
	/**
	 * 修改订单大小车计费策略 car_type=0 、通用 1、小车 2、大车
	 */
	private void changeCarType(String carType){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.CHANGE_CAR_TYPE);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("orderid", orderID);
		params.setUrlParams("car_type", carType);
		String url = params.getRequstUrl();
		Log.e(TAG, "修改订单车辆类型计费策略的url是--->"+url);
		HttpManager.requestGET(this.activity, url, this);
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		if ("1".equals(object)){
			((ZldNewActivity) activity).showToast("修改成功！");
			((ZldNewActivity) activity).cashOrder();
		}else {
			((ZldNewActivity) activity).showToast("修改失败！");
		}

		return false;
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		
	}
}

package com.zld.view;

import java.util.ArrayList;

import com.zld.R;
import com.zld.application;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.bean.AppInfo;
import com.zld.bean.FreeResons;
import com.zld.fragment.BaseFragment;
import com.zld.fragment.CashFragment;
import com.zld.fragment.ParkinfoFragment;
import com.zld.lib.constant.Constant;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

@SuppressLint({ "NewApi", "ValidFragment" })
public class SelectParkrNumber extends BaseFragment{
	private View parent;
	private Toast mToast;
	private ZldNewActivity activity;
	final static String regularEx = "|";
	public EditText et_login_password;
	public AutoCompleteTextView at_login_username;
    
	private ParkinfoFragment parkInfoFragment;
	private ListView listview;
	private PopupWindow popupWindow;
	public static String comid = null; 
	private static final String TAG = "SelectCarType";

	public SelectParkrNumber() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectParkrNumber(ZldNewActivity activity, View parent,ParkinfoFragment parkInfoFragment) {
		this.parent = parent;
		this.activity = activity;
		this.parkInfoFragment = parkInfoFragment;
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
	public void showView() {
		showPopupWindow(parent);
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
	 * 显示免费类型选择列表
	 * @param parent
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void showPopupWindow(View parent) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
		if(listview == null){
			listview = (ListView) LayoutInflater.from(activity).inflate(R.layout.account_droplist, null); 
		}
		int count = 5;
		final ArrayList<String> selectAllAccount = new ArrayList<String>();
		for (int i = 1; i < count; i++) {
			selectAllAccount.add("加"+i*2+"");
		}
		selectAllAccount.add("默认");
		selectAllAccount.add("已满");
		for (int i = 1; i < count; i++) {
			selectAllAccount.add("减"+i*2+"");
		}

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
			popupWindow.setWidth((int)(screenWidth/5));
//			popupWindow.setWidth((int)parent.getWidth());
		}  
		popupWindow.setFocusable(true);  
		popupWindow.setOutsideTouchable(true);  
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());  
		final int[] location = new int[2];  
		parent.getLocationOnScreen(location);  
		popupWindow.showAsDropDown(parent,0,-(int)parent.getHeight());
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String name = selectAllAccount.get(position);
				if(name == null){
					return;
				}
				
				if (name.equals("已满")) {
					activity.setHaveFreeCarNumber(0);
				} else if (name.equals("默认")) {
					activity.setHaveFreeCarNumber(-1);
				} else {
					if (position < 4) {
						activity.setHaveFreeCarNumber(Integer.valueOf((position + 1) * 2));
					} else if (position > 5) {
						activity.setHaveFreeCarNumber(Integer.valueOf(-(position - 5) * 2));
					}
				}
				parkInfoFragment.showChargeNumber(name);
				popupWindow.dismiss();
		
			}
		});
		
	}
}

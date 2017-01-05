package com.zld.view;


import java.util.ArrayList;

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

import com.zld.R;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.bean.AppInfo;
import com.zld.bean.FreeResons;
import com.zld.fragment.BaseFragment;
import com.zld.fragment.CashFragment;
import com.zld.ui.ZldNewActivity;

@SuppressLint({ "NewApi", "ValidFragment" })
public class SelectFreeCar extends BaseFragment{
	private View parent;
	private Toast mToast;
	private Context activity;
	final static String regularEx = "|";
	public EditText et_login_password;
	public AutoCompleteTextView at_login_username;
    
	private CashFragment cashFragment;
	private ListView listview;
	private PopupWindow popupWindow;
	public static String comid = null; 
	private static final String TAG = "SelectCarType";

	public SelectFreeCar() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SelectFreeCar(ZldNewActivity activity, View parent,CashFragment cashFragment) {
		this.parent = parent;
		this.activity = activity;
		this.cashFragment = cashFragment;
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
	public void showFreeTypeView(String orderID) {
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
		int count = AppInfo.getInstance().getFreeResons().size();
		final ArrayList<String> selectAllAccount = new ArrayList<String>();
		selectAllAccount.add("选择免费原因");
		for (int i = 0; i < count; i++) {
			FreeResons type = AppInfo.getInstance().getFreeResons().get(i);
			selectAllAccount.add(type.getName());
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
				String account = selectAllAccount.get(position);
				Log.e(TAG,"选择免费类型是："+account);
				if(account == null){
					return;
				}
				int count = AppInfo.getInstance().getFreeResons().size();
				final ArrayList<String> selectAllAccount = new ArrayList<String>();
				for (int i = 0; i < count; i++) {
					String name = AppInfo.getInstance().getFreeResons().get(i).getName();
					if (name.equals(account)) {
						String reasonID = AppInfo.getInstance().getFreeResons().get(i).getID();
						cashFragment.freeActionHandle(false,reasonID);
						popupWindow.dismiss();
						break;
					}
				}
			
			}
		});
		
	}
}

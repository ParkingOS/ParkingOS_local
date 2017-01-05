/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月15日 
 * 
 *******************************************************************************/ 
package com.zld.view;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zld.R;
import com.zld.adapter.MyViewPagerAdapter;
import com.zld.adapter.ProvinceGridViewAdapter;
import com.zld.bean.AppInfo;
import com.zld.fragment.CashFragment;
import com.zld.fragment.OrderDetailsFragment;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.ui.ZldNewActivity;

/**
 * <pre>
 * 功能说明: 
 * 日期:	2015年4月15日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月15日
 * </pre>
 */
public class DiscountViewPager  implements OnClickListener{

	private Activity activity;
	private Resources resources;
	private View mView;
	private String direction;
	//-------------------减免
	private View mThirtyView;
	private View mFiftyView;
	private View mOneHundredView;
	private View mTwoHundredView;
	private Button button_ok_jianman_time;
	private Button pic_image_update;
	private PopupWindow mPopupWindow;

	private EditText etCopies ;
	private int type_esc = 0;
	private int time = -1 ;
	private CashFragment cashFragment;

	public DiscountViewPager(Activity activity, boolean isModifyOrder,CashFragment cashFragment) {
		super();
		this.activity = activity;
		this.cashFragment = cashFragment;
		if(resources == null){
			resources = activity.getResources();
		}
		init_jianmian();
	}
	
	TextWatcher mTextWatcher = new TextWatcher() {
		private CharSequence temp;  
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,int after) {
			Log.e("tag", "beforeTextChanged");
			temp = s; 
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,int count) {
			
			Log.e("tag", "onTextChanged");
		}

		@Override
		public void afterTextChanged(Editable s) {
			Log.e("tag", "afterTextChanged");
//			editStart = etCopies.getSelectionStart();  
//			editEnd = etCopies.getSelectionEnd();  
//			String ed_text = etCopies.getText().toString().trim();
			Editable asdf = etCopies.getText();
			if(temp.toString().trim().equals("全免券")){
				return;
			}else if(temp.toString().trim()!=null && !temp.toString().trim().equals("")){
				if(!isNum(temp.toString().trim())){
					((ZldNewActivity) activity).showToast("请输入整数，不要输入其它字符");
					return;
				}

				if (temp.toString().length() > 2){  
					((ZldNewActivity) activity).showToast("输入长度达到上限,请重新输入");
					return;
				}else if(Integer.parseInt(temp.toString())>24){
					((ZldNewActivity) activity).showToast("减免超过一天,请重新输入");
					return;
				}else if(Integer.parseInt(temp.toString())==0){
					((ZldNewActivity) activity).showToast("减免时长为0,请重新输入");
					return;
				}
				if (Integer.parseInt(temp.toString()) != time && Integer.parseInt(temp.toString()) != 2 && Integer.parseInt(temp.toString()) != 4) {
					clearSelection();
				}
			}
			 
			
		}  
	};
	
	public static boolean isNum(String str){
//		return str.matches(("[0-9]+"));
		
		 Pattern pattern = Pattern.compile("[0-9]*"); 
		   Matcher isNum = pattern.matcher(str);
		   if( !isNum.matches() ){
		       return false; 
		   } 
		   return true;
	}

	private void clearSelection() {
		mThirtyView.setSelected(false);
		mFiftyView.setSelected(false);
		mOneHundredView.setSelected(false);
		mTwoHundredView.setSelected(false);
	}
	
	@Override
	public void onClick(View v) {
		clearSelection() ;
		switch (v.getId()) {
//		case R.id.onclick_button_jianmian:
//			Log.e("tag","onclick_button_jianmian"+"false");
//			updateViewOrder(false);
//			etCopies.setText("");
//			break;
		case R.id.back_orderdetail_:
			mPopupWindow.dismiss();
			break;
		case R.id.tv_recharge_thirty:
			v.setSelected(true);
			String onehouse = ((TextView) v).getText().toString();
			Log.e("tag", "点击:"+onehouse);
			time = 1;
			type_esc = 3;//3减免券 4：全免券
			etCopies.setText("1");
			etCopies.setFocusableInTouchMode(true);
			etCopies.requestFocus();
			break;
		case R.id.tv_recharge_fifty:
			v.setSelected(true);
			String twohouse = ((TextView) v).getText().toString();
			Log.e("tag", "点击:"+twohouse);
			time = 2;
			type_esc = 3;//3减免券 4：全免券
			etCopies.setText("2");
			etCopies.setFocusableInTouchMode(true);
			etCopies.requestFocus();
			break;
		case R.id.tv_recharge_onehundred:
			v.setSelected(true);
			String fourhouse = ((TextView) v).getText().toString();
			Log.e("tag", "点击:"+fourhouse);
			time = 4;
			type_esc = 3;//3减免券 4：全免券
			etCopies.setText("4");
			etCopies.setFocusableInTouchMode(true);
			etCopies.requestFocus();
			break;
		case R.id.tv_recharge_twohundred:
			String allhouse = ((TextView) v).getText().toString();
			Log.e("tag", "点击:"+allhouse);
			time = -1;
			type_esc = 4;//3减免券 4：全免券
			etCopies.setText("全免券");
			etCopies.setFocusable(false);
			etCopies.clearFocus();
			v.setSelected(true);
			Log.e("tag", "type_esc:"+type_esc);
			break;
		case R.id.button_ok_jianman_time:
			if(etCopies.getText().toString().trim()!=null){
				String numstr=etCopies.getText().toString().trim();
				if(numstr.trim().equals("全免券")){
					this.cashFragment.disCountAfterComplete("","4");
					mPopupWindow.dismiss();
					return ;
				}
				if(numstr!=null&&!numstr.equals("")&&!numstr.equals("null")&&isNum(numstr) && numstr.toString().length() <= 2){
					if(Integer.parseInt(numstr) > 0 && Integer.parseInt(numstr)<25){
						this.cashFragment.disCountAfterComplete(numstr,"3");
						mPopupWindow.dismiss();
					}else{
						((ZldNewActivity) activity).showToast("减免时长超过0-24小时范围");
						return;
					}
				}else{
					((ZldNewActivity) activity).showToast("信息有误,请重新操作或取消");
					return;
				}
			}else{
				((ZldNewActivity) activity).showToast("请输入优惠停车时长");
			}
			break;
		case R.id.pic_image_update:
			mPopupWindow.dismiss();
			break;
		default:
			break;
		}
	}
	
	private void init_jianmian(){
		mView = (View) LayoutInflater.from(activity).inflate(R.layout.discount_page, null);  

		etCopies = (EditText) mView.findViewById(R.id.etPrintCopies);
		etCopies.addTextChangedListener(mTextWatcher);  
		mThirtyView =mView.findViewById(R.id.tv_recharge_thirty);
		mThirtyView.setOnClickListener(this);
		mFiftyView = mView.findViewById(R.id.tv_recharge_fifty);
		mFiftyView.setOnClickListener(this);
		mOneHundredView =mView.findViewById(R.id.tv_recharge_onehundred);
		mOneHundredView.setOnClickListener(this);
		mTwoHundredView = mView.findViewById(R.id.tv_recharge_twohundred);
		mTwoHundredView.setOnClickListener(this);
		button_ok_jianman_time = (Button) mView.findViewById(R.id.button_ok_jianman_time);
		button_ok_jianman_time.setOnClickListener(this);
		pic_image_update = (Button) mView.findViewById(R.id.pic_image_update);
		pic_image_update.setOnClickListener(this);
		etCopies.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					// 此处为得到焦点时的处理内容
				} else {
					// 此处为失去焦点时的处理内容
					hideInputAll(v);
				}
			}
		});
		
		
	}
	
	/*
	 * //强制隐藏键盘
	 */
	public void hideInputAll(View view){
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);  
		imm.showSoftInput(view,InputMethodManager.SHOW_FORCED);  
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	/**
	 * 隐藏PopupWindow
	 */
	public void hidePopupWindow() {
		if(mPopupWindow != null){
			mPopupWindow.dismiss();
		}
	}

	/**
	 * 选择减免券界面
	 * @param parent
	 */
	@SuppressWarnings("deprecation")
	public void showPopupWindow(View parent,int viewHeight) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
//			initView();
//			InitImageView(activity);
			mPopupWindow = new PopupWindow(mView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			DisplayMetrics dm = new DisplayMetrics();
			//获取屏幕信息
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
			mPopupWindow.setWidth((int)(screenWidth*0.6));
			mPopupWindow.setHeight(viewHeight + 2);
			mPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
			mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  
		final int[] location = new int[2];  
		parent.getLocationOnScreen(location);

		if(direction.equals("left")){
			//显示在父控件左边
			mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY,
					0, location[1]);  
		}else if(direction.equals("right")){
			//显示在父控件右边 加上一个大数100的目的是保证此视图在屏幕最右边
			mPopupWindow.showAtLocation(parent, Gravity.NO_GRAVITY,
					location[0]+parent.getWidth() + 100, location[1]); 
		}

		mPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				//				mEditText.setHint(resources.getString(R.string.please_search_plate));
			}
		});
		//直接调用setCurrentItem(0)，pager不会变化，因为默认pager在第一页，当系统发现程序猿调用此方法，就不去理会，所以才这样写，看着别扭实现了就ok了
//		viewPager.setCurrentItem(1);
//		viewPager.setCurrentItem(0);
	}

//	private void initView() {
//		viewPager = (ViewPager) mView.findViewById(R.id.viewpager);
//		views = new ArrayList<View>();
//		LayoutInflater inflater = activity.getLayoutInflater();
//	}
}

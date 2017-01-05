/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月15日 
 * 
 *******************************************************************************/ 
package com.zld.view;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;

import com.zld.R;
import com.zld.adapter.MyViewPagerAdapter;
import com.zld.adapter.ProvinceGridViewAdapter;
import com.zld.fragment.OrderDetailsFragment;

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
public class KeyboardViewPager {

	private Activity activity;
	private Resources resources;
	private ImageView imageView;
	private View mView;
	public PopupWindow mPopupWindow;
	private ViewPager viewPager;// 页卡内容
	private TextView textView1, textView2, textView3;
	private List<View> views;// Tab页面列表
	private int offset = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int bmpW;// 动画图片宽度
	private View view1, view2, view3;// 各个页卡
	private EditText et_carnumber;
	private String direction;
	private boolean isModifyOrder;
	private String TAG = "KeyboardViewPager";

	public KeyboardViewPager(Activity activity, boolean isModifyOrder) {
		super();
		this.activity = activity;
		this.isModifyOrder = isModifyOrder;
		if(resources == null){
			resources = activity.getResources();
		}
	}

	public void setEt_carnumber(EditText et_carnumber) {
		this.et_carnumber = et_carnumber;
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
	 * 2 * 初始化动画，这个就是页卡滑动时，下面的横线也滑动的效果，在这里需要计算一些数据
	 */
	private void InitImageView(Activity activity) {
		Display display = activity.getWindowManager().getDefaultDisplay();
		/* 获取屏幕的宽度 */
		@SuppressWarnings("deprecation")
		int width = display.getWidth();
		/* 根据权重计算键盘区占屏幕的宽度 屏幕权重4、6分 键盘区占0.6 */
		int itemWidth = (int) (width * 0.6);
		imageView = (ImageView) mView.findViewById(R.id.cursor);
		bmpW = BitmapFactory.decodeResource(resources,
				R.drawable.shade_item).getWidth();// 获取图片宽度
		/* 子标题的宽度 */
		offset = itemWidth / 3 + itemWidth % 3;
		Matrix matrix = new Matrix();
		/* 设置动画初始位置 */
		matrix.postTranslate((offset - bmpW) / 4, 0);
		imageView.setImageMatrix(matrix);// 设置动画初始位置
		Log.e(TAG, width + ":" + itemWidth+ ":" + offset);
	}

	/**
	 * 头标点击监听 3
	 */
	private class MyOnClickListener implements OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}

		public void onClick(View v) {
			viewPager.setCurrentItem(index);
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		int one = offset;// 页卡1 -> 页卡2 偏移量
		int two = one * 2;// 页卡1 -> 页卡3 偏移量

		public void onPageScrollStateChanged(int arg0) {

		}

		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		public void onPageSelected(int arg0) {
			Log.e(TAG,arg0 + " --------------------------");
			Animation animation = new TranslateAnimation(one * currIndex, one
					* arg0, 0, 0);
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(300);
			if(imageView != null){
				imageView.startAnimation(animation);
			}
			switch (viewPager.getCurrentItem()) {
			case 0:
				textView1.setTextColor(resources.getColor(
						R.color.white)); 
				textView2.setTextColor(resources.getColor(R.color.dark_grenn));
				textView3.setTextColor(resources.getColor(R.color.dark_grenn));
				textView1.setBackgroundColor(
						activity.getResources().getColor(R.color.dark_grenn));
				textView2.setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				textView3.setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				break;
			case 1:
				textView2.setTextColor(resources.getColor(
						R.color.white));
				textView1.setTextColor(resources.getColor(R.color.dark_grenn));
				textView3.setTextColor(resources.getColor(R.color.dark_grenn));
				textView2.setBackgroundColor(
						activity.getResources().getColor(R.color.dark_grenn));
				textView1.setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				textView3.setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				break;
			case 2:
				textView3.setTextColor(resources.getColor(
						R.color.white));
				textView1.setTextColor(resources.getColor(R.color.dark_grenn));
				textView2.setTextColor(resources.getColor(R.color.dark_grenn));
				textView3.setBackgroundColor(
						activity.getResources().getColor(R.color.dark_grenn));
				textView2.setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				textView1.setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				break;
			}
		}
	}

	public void setView1() {
		GridView gv_province = (GridView) view1
				.findViewById(R.id.gridview_province);
		gv_province.setSelector(new ColorDrawable(Color.TRANSPARENT));
		final String[] province = new String[] { "京", "沪", "浙", "苏", "粤", "鲁",
				"晋", "冀", "豫", "川", "渝", "辽", "吉", "黑", "皖", "鄂", "湘", "赣",
				"闽", "陕", "甘", "宁", "蒙", "津", "贵", "云", "桂", "琼", "青", "新",
				"藏", "港", "澳", "使" ,""};
		final ArrayList<String> provinces = new ArrayList<String>();
		for (int i = 0; i < province.length; i++) {
			provinces.add(province[i]);
		}
		ProvinceGridViewAdapter adapter = new ProvinceGridViewAdapter(activity,
				provinces, false);
		gv_province.setAdapter(adapter);

		gv_province.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == provinces.size() - 1){
					gridBtnDeleteOnItemClick();
					return;
				}
				gridOnItemClick(province, position);
				viewPager.setCurrentItem(1);
			}
		});
	}

	public void setView2() {
		GridView gv_number = (GridView) view2
				.findViewById(R.id.gridview_number);
		gv_number.setSelector(new ColorDrawable(Color.TRANSPARENT));
		final String[] number = new String[] { "0", "1", "A", "B", "C", "D",
				"E", "2", "3", "F", "G", "H", "J", "K","4", "5", "L", "M",
				"N", "P", "Q", "6", "7", "R", "S", "T", "U", "V", "8", "9",
				"W", "X", "Y", "Z", ""};
		final ArrayList<String> numbers = new ArrayList<String>();
		for (int i = 0; i < number.length; i++) {
			numbers.add(number[i]);
		}
		ProvinceGridViewAdapter adapter = new ProvinceGridViewAdapter(activity,
				numbers, true);
		gv_number.setAdapter(adapter);
		gv_number.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == numbers.size() - 1){
					gridBtnDeleteOnItemClick();
					return;
				}
				gridOnItemClick(number, position);
			}
		});
	}

	public void sheView3() {
		GridView gv_police = (GridView) view3
				.findViewById(R.id.gridview_police);
		gv_police.setSelector(new ColorDrawable(Color.TRANSPARENT));
		final String[] police = new String[] { "军", "空", "海", "北", "沈", "兰",
				"济", "南", "广", "成", "WJ", "警", "消", "边", "水", "电", "林", "通", "", ""};
		final ArrayList<String> polices = new ArrayList<String>();
		for (int i = 0; i < police.length; i++) {
			polices.add(police[i]);
		}

		ProvinceGridViewAdapter adapter = new ProvinceGridViewAdapter(activity,
				polices, false);
		gv_police.setAdapter(adapter);
		gv_police.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == polices.size() - 1){
					gridBtnDeleteOnItemClick();
					return;
				}
				gridOnItemClick(police, position);
			}
		});
	}

	private void gridOnItemClick(final String[] number, int position) {
		final int index = et_carnumber.getSelectionStart();
		final Editable editable = et_carnumber.getText();
		editable.insert(index, number[position]);
		if (isModifyOrder){
			Intent intent = new Intent();
			intent.setAction(OrderDetailsFragment.action);
			intent.putExtra("state", OrderDetailsFragment.modifyorder_action);
			activity.sendBroadcast(intent);
		}
	}

	private void gridBtnDeleteOnItemClick() {
		final int index = et_carnumber.getSelectionStart();
		final Editable editable = et_carnumber.getText();
		/*int len = editable.length();*/
		if (index > 0){
			editable.delete(index-1, index);
		}
	}

	/**
	 * 初始化头标
	 */
	private void InitTextView(Activity activity) {
		textView1 = (TextView) mView.findViewById(R.id.text1);
		textView2 = (TextView) mView.findViewById(R.id.text2);
		textView3 = (TextView) mView.findViewById(R.id.text3);
		textView1.setOnClickListener(new MyOnClickListener(0));
		textView2.setOnClickListener(new MyOnClickListener(1));
		textView3.setOnClickListener(new MyOnClickListener(2));
	}

	/**
	 * 数字键盘
	 * @param parent
	 */
	@SuppressWarnings("deprecation")
	public void showPopupWindow(View parent) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
			mView = (View) LayoutInflater.from(activity).inflate(R.layout.keyboard_page_gridview_zone, null);  
			initView();
			InitImageView(activity);
			InitTextView(activity);
			PopupWindow mPopupWindow = new PopupWindow(mView,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			DisplayMetrics dm = new DisplayMetrics();
			//获取屏幕信息
			activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
			mPopupWindow.setWidth((int)(screenWidth*0.6));
			mPopupWindow.setHeight((int)(screenHeight*0.55));
		
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
		setView1();
		setView2();
		sheView3();
		//直接调用setCurrentItem(0)，pager不会变化，因为默认pager在第一页，当系统发现程序猿调用此方法，就不去理会，所以才这样写，看着别扭实现了就ok了
		viewPager.setCurrentItem(1);
		viewPager.setCurrentItem(0);
	}

	private void initView() {
		viewPager = (ViewPager) mView.findViewById(R.id.viewpager);
		views = new ArrayList<View>();
		LayoutInflater inflater = activity.getLayoutInflater();
		view1 = inflater.inflate(R.layout.input_car_number_province, null);
		view2 = inflater.inflate(R.layout.input_car_number_number, null);
		view3 = inflater.inflate(R.layout.input_car_number_police, null);
		views.add(view1);
		views.add(view2);
		views.add(view3);
		viewPager.setAdapter(new MyViewPagerAdapter(views));
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}
}

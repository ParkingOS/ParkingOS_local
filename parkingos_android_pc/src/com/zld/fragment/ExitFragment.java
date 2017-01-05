/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.zld.R;
import com.zld.bean.CarBitmapInfo;
import com.zld.bean.MyCameraInfo;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.BitmapUtil;
import com.zld.lib.util.InputUtil;
import com.zld.view.KeyboardViewPager;
import com.zld.view.SelectLiftPole;

/**
 * <pre>
 * 功能说明: 出口Fragment
 * 日期:	2015年4月13日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月13日
 * </pre>
 */
public class ExitFragment extends BaseFragment implements OnClickListener, OnTouchListener{
	private static final String TAG = "ExitFragment";
	private ImageView iv_exit;
	private Button btn_search;
	private Button btn_nullPlate;
	private SelectLiftPole selectLiftPole;
	private EditText et_carnumber;
	private ImageView iv_plate_show;
	private Button tv_exit_open_pole;
	private Boolean outPoleEnable = true; // 抬杆按钮是否可用

	public CarBitmapInfo exitCarBmpInfo;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.exit_page, container,false);
		initView(rootView);
		onClickEvent();
		getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	/**
	 * 初始化控件
	 */
	private void initView(View rootView) {
		iv_exit = (ImageView) rootView.findViewById(R.id.iv_exit);
		tv_exit_open_pole = (Button) rootView.findViewById(R.id.tv_exit_open_pole);
		iv_plate_show = (ImageView) rootView.findViewById(R.id.iv_plate_show);
		et_carnumber = (EditText)rootView.findViewById(R.id.et_carnumber);
		btn_search = (Button) rootView.findViewById(R.id.btn_search);
		btn_nullPlate = (Button)rootView.findViewById(R.id.btn_nullPlate);
		InputUtil.hideTypewriting(activity, et_carnumber);
		selectLiftPole = new SelectLiftPole(activity, tv_exit_open_pole, this);
	}

	/**
	 * 控件点击事件
	 */
	private void onClickEvent() {
		tv_exit_open_pole.setOnClickListener(this);
		btn_search.setOnClickListener(this);
		btn_nullPlate.setOnClickListener(this);
		et_carnumber.setOnTouchListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_exit_open_pole:
			//抬杆
			if (!outPoleEnable) {
				activity.showToast("请先手动结算订单，或与管理员联系");
				return;
			}
			activity.controlExitPole();
			if (activity.selectCameraOut.size()>0) {
				MyCameraInfo myCameraInfo = activity.selectCameraOut.get(0);
				
				selectLiftPole.showLiftPoleView(myCameraInfo.getIp(),false);
			}
			break;
		case R.id.btn_search:
			//搜索
			String carNumber = et_carnumber.getText().toString().trim();
			if(carNumber.equals("")){
				activity.showToast("请输入您要搜索的车牌号");
			}else{
				Log.e(TAG, "搜索的车牌号："+carNumber);
				activity.changeRadioBtnColor(0);
				activity.listFragment.searchCarNumber(carNumber, OrderListState.HAND_SEARCH_STATE);
			}
			break;
		case R.id.btn_nullPlate:
			activity.changeRadioBtnColor(0);
			activity.listFragment.searchCarNumber("无", OrderListState.HAND_SEARCH_STATE);
			break;
		default:
			break;
		}
	}
	
	/* (non-Javadoc)
	 * @see android.view.View.OnTouchListener#onTouch(android.view.View, android.view.MotionEvent)
	 */
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		editOnTouch(v);//触摸显示软键盘			
		return false;
	}

	/**
	 *清空EditText
	 */
	@SuppressWarnings("unused")
	private void clearEditText() {
		// TODO Auto-generated method stub
		et_carnumber.setHint("");
	}

	/**
	 * 显示车牌字母键盘
	 */
	private void editOnTouch(View v) {
		// TODO Auto-generated method stub
		KeyboardViewPager kvp = new KeyboardViewPager(activity, false);
		kvp.setEt_carnumber(et_carnumber);
		kvp.setDirection("right");
		kvp.showPopupWindow(btn_search);
	}

	/**
	 * 出口图片
	 * @param resultBitmap
	 */
	public void refreshImageBitmap(Bitmap resultBitmap) {
		// TODO Auto-generated method stub
		iv_exit.setImageBitmap(resultBitmap);
	}

	/**
	 * 更新小车牌图片
	 * @param bitmap
	 */
	public void refreshCarPlate(Bitmap bitmap){
		iv_plate_show.setImageBitmap(bitmap);
	}
	/**
	 * 更新输入车牌号
	 * @param text
	 */
	public void refreshEditText(String text){
		et_carnumber.setText(text);
	}
	
	/**
	 * 隐藏搜索按钮
	 * @return
	 */
	public void hideSearch(){
		if(btn_search.getVisibility() == View.VISIBLE){
			btn_search.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * 显示搜索按钮
	 * @return
	 */
	public void showSearch(){
		if(btn_search.getVisibility() == View.INVISIBLE){
			btn_search.setVisibility(View.VISIBLE);
		}
	}
	
	public Boolean getOpenPoleTouchEnable() {
		return outPoleEnable;
	}

	public void setOpenPoleTouchEnable(Boolean openPoleTouchEnable) {
		this.outPoleEnable = openPoleTouchEnable;
	}
	
	public CarBitmapInfo getExitCarInfo(){
		return exitCarBmpInfo == null ? null : exitCarBmpInfo;
	}

	@SuppressWarnings("static-access")
	public void refreshAllView(CarBitmapInfo bitmapInfo){
		exitCarBmpInfo = bitmapInfo;
		Bitmap resultBitmap = BitmapUtil.zoomImg(bitmapInfo.getBitmap(), 1280, 720);
		iv_exit.setImageBitmap(resultBitmap);
		int x = bitmapInfo.getxCoordinate();
		int width = bitmapInfo.getCarPlatewidth();
		int y = bitmapInfo.getyCoordinate();
		int height = bitmapInfo.getCarPlateheight();
		Log.e(TAG, "x="+x+" width="+width+" y="+y+" height="+height);
		if(x+width <= resultBitmap.getWidth()&&
				y+height <= resultBitmap.getHeight()){
			if(x<10&&y<10&&width<10&&height<10){
				iv_plate_show.setImageResource(R.drawable.plate_sample);
			}else{
				Log.e(TAG, "其中某个参数可能为负数");
				if(x>0&&y>0&&width>0&&height>0){
				Bitmap smallCarPlateBmp = 
						resultBitmap.createBitmap(resultBitmap, x, y, width, height);
				iv_plate_show.setImageBitmap(smallCarPlateBmp);
				}
			}
		}else{
			iv_plate_show.setImageResource(R.drawable.plate_sample);
		}
		//过滤掉(-无-)
		if (bitmapInfo.getCarPlate() != null && bitmapInfo.getCarPlate().length() > 3){
			et_carnumber.setText(bitmapInfo.getCarPlate());
		}else{
			et_carnumber.setText("");
		}
	}
}

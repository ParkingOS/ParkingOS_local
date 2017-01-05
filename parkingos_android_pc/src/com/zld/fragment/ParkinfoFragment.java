/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import java.util.Date;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.zld.R;
import com.zld.bean.AppInfo;
import com.zld.bean.CollectMoney;
import com.zld.bean.ShaerUiInfo;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.service.ShareUiService;
import com.zld.view.SelectParkrNumber;
import com.zld.view.SwitchAccount;

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
public class ParkinfoFragment extends BaseFragment{

	private static final String TAG = "ParkinfoFragment";
	private TextView tv_idle_carport_num;
	private TextView tv_work_time;
	private Button btn_charge_number;
	private Button btn_charge_people;
	private TextView tv_tcb_pay_money;
	private TextView tv_tcb_pay;
	private TextView tv_cash_pay;
	private TextView tv_cash_pay_money;
	private TextView tv_show_money;
	private SelectParkrNumber selectParkNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.park_info, container,
				false);
		initView(rootView);
		//收费员
		initChargeName();
		//收费金额
		/*getChargePeopleInfo();*/
		return rootView;
	}

	private void initChargeName() {
		if(AppInfo.getInstance().getName() != null){
			setChargePeople(AppInfo.getInstance().getName());
		}else{
			String name = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "userinfo", "name", "收费员");
			AppInfo.getInstance().setName(name);
			setChargePeople(name);
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView(View rootView) {
		tv_idle_carport_num = (TextView) rootView.findViewById(R.id.tv_idle_carport_num);
		tv_work_time = (TextView) rootView.findViewById(R.id.tv_work_time);
		tv_tcb_pay = (TextView) rootView.findViewById(R.id.tv_tcb_pay);
		tv_tcb_pay_money = (TextView) rootView.findViewById(R.id.tv_tcb_pay_money);
		tv_cash_pay = (TextView) rootView.findViewById(R.id.tv_cash_pay);
		tv_cash_pay_money = (TextView) rootView.findViewById(R.id.tv_cash_pay_money);
		btn_charge_people = (Button) rootView.findViewById(R.id.btn_charge_people);
		btn_charge_number = (Button) rootView.findViewById(R.id.btn_charge_number);
		btn_charge_number.setText("默认");
		tv_show_money = (TextView) rootView.findViewById(R.id.tv_show_money);
		selectParkNumber = new SelectParkrNumber(activity, btn_charge_number, this);
		if (AppInfo.getInstance().getIsShowhdmoney()) {
			tv_tcb_pay.setVisibility(View.INVISIBLE);
			tv_cash_pay.setVisibility(View.INVISIBLE);
			tv_tcb_pay_money.setVisibility(View.INVISIBLE);
			tv_cash_pay_money.setVisibility(View.INVISIBLE);
			tv_show_money.setVisibility(View.INVISIBLE);
		}
		
		btn_charge_people.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startServiceSync();
				//切换账号
				SwitchAccount sa = new SwitchAccount(activity,
						btn_charge_people, SwitchAccount.ENTRANCE);
				sa.showSwitchAccountView();
			}
		});
		btn_charge_number.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				selectParkNumber.showView();
			}
		});
		//显示和隐藏金额
		tv_show_money.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(tv_show_money.getText().equals("隐藏")){
					tv_show_money.setText("显示");
					tv_tcb_pay_money.setVisibility(View.INVISIBLE);
					tv_cash_pay_money.setVisibility(View.INVISIBLE);
				}else if(tv_show_money.getText().equals("显示")){
					tv_show_money.setText("隐藏");
					tv_tcb_pay_money.setVisibility(View.VISIBLE);
					tv_cash_pay_money.setVisibility(View.VISIBLE);
				}
			}
		});
	}

	/**
	 * 当前收费员
	 * @param currentChargePeople
	 */
	public void setChargePeople(String currentChargePeople){
		btn_charge_people.setText(currentChargePeople);
	}

	/**
	 * 获取收费员收费的信息
	 */
	public void getChargePeopleInfo(){
		//是否是本地服务器
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
			//本地化相关
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(), "nettype", "isLocal", false);
			Log.e("isLocal","ParkinfoFragment getChargePeopleInfo get isLocal "+param);
//			if(param||AppInfo.getInstance().getIssuplocal().equals("1")){
//				getLocalMoney();
//				return;
//			}
		}
		//在线实时获取
		getTollManMoney();
	}
	/**
	 * 点击收费员重新开启service同步金额和车位数
	 */
	private void startServiceSync() {
		Intent intent = new Intent(activity, ShareUiService.class);
		intent.putExtra("refresh", "refresh");
		activity.startService(intent);
	}

	/**
	 * 本地获取收费员金额
	 */
	public void getLocalMoney() {
		try{
			String cashpay = getLocalCashPay();
			getLocalMobilePay();
			//本地金额为0
			if(cashpay.equals("0")||cashpay.equals("0.0")){
				Log.e(TAG,"本地没有,则在线实时获取");
				getTollManMoney();
			}
		}catch(Exception e){
			Log.e(TAG,"parseDouble有问题");
		}
	}

	private void getLocalMobilePay() {
		String mobilepay = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(), "userinfo", "mobilepay", "0");
		Log.e(TAG,"本地获取收费员金额mobilepay:"+mobilepay);
		if(Double.parseDouble(mobilepay)<0){
			mobilepay = "0";
		}
		tv_tcb_pay_money.setText(mobilepay);
	}

	private String getLocalCashPay() {
		String cashpay = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(), "userinfo", "cashpay", "0");
		Log.e(TAG,"本地获取收费员金额cashpay:"+cashpay);
		if(Double.parseDouble(cashpay)<0){
			cashpay = "0";
		}
		tv_cash_pay_money.setText(cashpay);
		return cashpay;
	}

	/**
	 * 网络请求获取收费员金额
	 */
	public void getTollManMoney() {
		if(AppInfo.getInstance().getToken() == null){
			return;
		}
		String worksiteId = SharedPreferencesUtils.getParam(activity.getApplicationContext(),
				"set_workStation", "workstation_id", "");
		String etime = TimeTypeUtil.getTodayDate(new Date());
		String firstLoginTime = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "autologin", "logontime", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.COLLECTOR_INFO);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		if(!TextUtils.isEmpty(firstLoginTime)){
			params.setUrlParams("logontime", firstLoginTime);
		}else{
			params.setUrlParams("logontime", etime);
		}
		params.setUrlParams("btime", etime);
		params.setUrlParams("etime", etime);
		params.setUrlParams("worksite_id", worksiteId);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		String url = params.getRequstUrl();
		Log.e(TAG, "收费员当日收费金额信息的url是--->"+url);
		HttpManager.requestGET(getActivity(), url, this);
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.COLLECTOR_INFO)){
			try{
				Log.e(TAG,  Constant.COLLECTOR_INFO+"------------->"+ object);
				Gson gson = new Gson();
				CollectMoney info = gson.fromJson(object,CollectMoney.class);
				if(info != null){
					if(info.getStart_time() != null){
						setTime(TimeTypeUtil.getStringTime(Long.valueOf(info.getStart_time()+"000"))); 
					}
					if(info.getMobilepay() != null){
						tv_tcb_pay_money.setText(StringUtils.removeZero(info.getMobilepay())); 
						SharedPreferencesUtils.setParam(
								activity.getApplicationContext(),"userinfo", "mobilepay", info.getMobilepay());
					}
					if(info.getCashpay() != null){
						tv_cash_pay_money.setText(StringUtils.removeZero(info.getCashpay())); 
						SharedPreferencesUtils.setParam(
								activity.getApplicationContext(),"userinfo", "cashpay", info.getCashpay());
					}
				}
			}catch(JsonSyntaxException e){

			}
		}
		return super.doSucess(url, object);
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.COLLECTOR_INFO)){
			HttpManager.requestGET(getActivity(), url, this);
		}
		
		return super.doFailure(url, status);
	}
	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		// 请求超时 获取本地金额显示
		if (url.contains(Constant.COLLECTOR_INFO)){
			HttpManager.requestGET(getActivity(), url, this);
//			getLocalCashPay();
//			getLocalMobilePay();
		}
//		super.timeout(url);
	}

	public void setTime(String time){
		tv_work_time.setText(time); 
	}

	public String getTime() {
		return (String) tv_work_time.getText();
	}
	
	/**
	 * 空闲车位
	 */
	public void setShare(ShaerUiInfo info) {
		// TODO Auto-generated method stub
		if(info!=null&&info.getFree() != null){
			if (Integer.parseInt(info.getFree()) >= 0) {
				tv_idle_carport_num.setText(info.getFree());
			} else {
				tv_idle_carport_num.setText("0");
			}
		}
	}

	public void showChargeMoney(String money){
		tv_cash_pay_money.setText(money); 
	}
	
	public void showChargeNumber(String number){
		btn_charge_number.setText(number); 
	}

}

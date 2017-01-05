/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


import com.google.gson.Gson;
import com.zld.R;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.PrePayOrder;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.ComeInCarState;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.InputUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.lib.util.VoicePlayer;
import com.zld.service.PollingService;
import com.zld.view.KeyboardViewPager;
import com.zld.view.SelectCarType;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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
public class OrderDetailsFragment extends BaseFragment implements OnClickListener, OnTouchListener{
	private static final String TAG = "OrderDetailsFragment";
	public static final String action = "REFRESH_STATE";
	public static final int cashorder_action = 1;
	public static final int modifyorder_action = 2;
	private EditText et_car_num;//车牌号码
	private ImageView iv_car_image;//车牌小样
	private Button btn_bigsmall_switch;//大小车
	private Button btn_car_type; //车辆类型
	private TextView tv_account_type;//账户类型
	private TextView tv_entrance_time;//入场时间
	private TextView tv_park_duration;//停车时长
	private Button btn_clear_order;//结算订单
	private KeyboardViewPager kvp;
	public CarNumberOrder currenOrder;
	public ComeInCarState comeInCarState;
	private int billingType = 0;//默认不区分大小车
	public boolean isHideClearOrderBtn = false;//是否隐藏结算订单按钮
	//保存当前结算的订单的id和车牌号，用于图片回调时的快慢
//	private String ordid = "";
//	private String carPlate = "";
	
	private SelectCarType selectCarType;
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.DELAY_UPLOAD:
				String orderid = (String) msg.obj;
				exitCarBmpUpload(orderid);
				break;

			default:
				break;
			}
			super.handleMessage(msg);  
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.order_details, container,
				false);
		initView(rootView);
		onClickEvent();
		isShowClearOrder();
//		setShowBillButton();
		registerBr();
		initComeInCarState();
		return rootView;
	}

	/**
	 * 1显示结算订单按钮,0隐藏
	 */
	private void isShowClearOrder() {
		// TODO Auto-generated method stub
		String ishidehdbutton = AppInfo.getInstance().getIshidehdbutton();
		if(ishidehdbutton == null){
			String param = SharedPreferencesUtils.getParam(activity.getApplicationContext(),"zld_config", "ishidehdbutton", Constant.sZero);
			AppInfo.getInstance().setIshidehdbutton(param);
		}
		if(ishidehdbutton != null){
			if(ishidehdbutton.equals(Constant.sZero)){
				isHideClearOrderBtn = false;
				btn_clear_order.setVisibility(View.VISIBLE);
			}else if(ishidehdbutton.equals(Constant.sOne)){
				isHideClearOrderBtn = true;
				btn_clear_order.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView(View rootView) {
		iv_car_image =(ImageView) rootView.findViewById(R.id.iv_car_image);
		et_car_num = (EditText)rootView.findViewById(R.id.et_car_num);
//		btn_bigsmall_switch = (Button) rootView.findViewById(R.id.btn_bigsmall_switch);
		btn_car_type = (Button) rootView.findViewById(R.id.btn_car_type);
		tv_account_type = (TextView) rootView.findViewById(R.id.tv_account_type);
		tv_entrance_time = (TextView) rootView.findViewById(R.id.tv_entrance_time);
		tv_park_duration = (TextView) rootView.findViewById(R.id.tv_park_duration);
		btn_clear_order = (Button) rootView.findViewById(R.id.btn_clear_order);
		selectCarType = new SelectCarType(activity,btn_car_type,this);
		InputUtil.hideTypewriting(activity, et_car_num);
	}

	/**
	 * 控件点击事件
	 */
	private void onClickEvent() {
//		btn_bigsmall_switch.setOnClickListener(this);
		btn_car_type.setOnClickListener(this);
		btn_clear_order.setOnClickListener(this);
		et_car_num.setOnTouchListener(this);
	}

	/**
	 * 订单详情广播
	 */
	private void registerBr() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(action);
		if(activity != null){
			activity.registerReceiver(new OrderDetailsReceiver(), intentFilter);
		}
	}

	private void initComeInCarState() {
		if(comeInCarState == null){
			comeInCarState = new ComeInCarState();
		}
	}

	/**
	 * 显示车牌字母键盘
	 */
	private void editOnTouch(View v) {
		// TODO Auto-generated method stub
		if(kvp == null){
			kvp = new KeyboardViewPager(activity, true);
		}
		kvp.setEt_carnumber(et_car_num);
		// 键盘显示在搜索按钮右边
		kvp.setDirection("left");
		kvp.showPopupWindow(et_car_num);
	}

	public void hidePopupWindow(){
		if(kvp != null){
			kvp.hidePopupWindow();
		}
	}

	/**
	 * 隐藏结算订单按钮
	 */
	public void hideBtn(){
		if(btn_clear_order.getVisibility() == View.VISIBLE){
			btn_clear_order.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示结算订单按钮
	 */
	public void showBtn(){
		if(btn_clear_order.getVisibility() == View.INVISIBLE){
			btn_clear_order.setVisibility(View.VISIBLE);
		}
	}	

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
//		case R.id.btn_bigsmall_switch:
//			setSwitchBtnStyle(billingType);
//			/* 修改订单大小车计费策略  */
//			alertCarType();
//			break;
		case R.id.btn_car_type:
			//展开车辆类型
			if (activity.getItemOrder() != null) {
				selectCarType.showSwitchAccountView(activity.getItemOrder().getId());
			}	
			break;
		case R.id.btn_clear_order:
			int action = getOrderAction();
			//结算订单or修改车牌号
			if (action == cashorder_action){
				if(OrderListState.getInstance().isOrderFinishState()){
					activity.controlExitCamera();
				}
				if(isHideClearOrderBtn){
					hideBtn();
				}
				selectClearOder(true);
			}else if(action == modifyorder_action){
				modifyOrder();
				Log.e(TAG, "修改车牌号完成,状态置为当前订单状态");
				activity.setCurrentOrderState();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		editOnTouch(v);//触摸显示软键盘	
		return false;
	}

	/**
	 * 显示详情
	 * @param order
	 */
	public void refreshView(CarNumberOrder order){
		if (order == null){
			clearView();
			return;
		}
		activity.hideSealBtn();
		/* 隐藏预支付,显示车费,显示结算按钮*/
		activity.hidePrepay();
		activity.showCost();

		/*出口来车,手动搜索后; 或自动搜索状态;或后台设置为显示;则显示结算按钮*/
		System.out.println("isExit:"+activity.isExitComeinCar+
				"==isHand:"+OrderListState.getInstance().isHandSearchState()+
				"==isAuto"+OrderListState.getInstance().isAutoSearchState()+
				"==isHide"+!isHideClearOrderBtn);
		System.out.println("====="+(activity.isExitComeinCar&&OrderListState.getInstance().isHandSearchState()));

		if(((activity.isExitComeinCar&&OrderListState.getInstance().isHandSearchState())||
				OrderListState.getInstance().isAutoSearchState()||!isHideClearOrderBtn)
				&&!OrderListState.getInstance().isParkOutState()){
			showBtn();
		}else{
			hideBtn();
			activity.isExitComeinCar = false;
		}

		if(!OrderListState.getInstance().isHandSearchState()){
			activity.hideFreeAndChargeBtn();
		}
		
		if (order.getState() != null && order.getState().equals(Constant.sOne)) {
			activity.showFreeAndChargeBtn();
		}
		currenOrder = order;
		Log.e(TAG, "订单显示前的详情"+order.toString());
		if (!"-1".equals(order.getUin())){
			tv_account_type.setText(getResources().getString(R.string.account_type_tingchebao));
		}else{
			tv_account_type.setText(getResources().getString(R.string.account_type_content));
		}
		if ("5".equals(order.getCtype())){
			tv_account_type.setText("月卡用户");
			Log.e(TAG, "详情来车状态："+comeInCarState.getState());
			if(comeInCarState.getState() == ComeInCarState.EXIT_COME_IN_CAR_STATE ){
				VoicePlayer.getInstance(activity).playVoice("此车为月卡用户");
			}
		}
		et_car_num.setText(order.getCarnumber());
		String btime = order.getBtime();
		tv_entrance_time.setText(btime);
		if(order.getDuration().substring(0, 2).equals("已停")){
			tv_park_duration.setText(order.getDuration().substring(2));
		}else{
			tv_park_duration.setText(order.getDuration());
		}
		iv_car_image.setImageResource(R.drawable.plate_sample);
//		setSwitchBtnStyle(order);
		setSwitchCarType(order);
	}

//	private String getBtime(CarNumberOrder order) {
//		String btime = order.getBtime();
//		// 网络超时状态
//		try{
//			long longtime = Long.parseLong(btime);
//			if(longtime > 1400562840){
//				if(btime!=null&&btime.length()>9){
//					btime = TimeTypeUtil.getEasyStringTime(Long.parseLong(btime)*1000);
//				}
//			}
//		}catch(NumberFormatException e){
//			Log.e(TAG,"网络超时状态,入场时间本地生成");
//		}
//		Log.e(TAG, "详情显示时间："+btime);
//		// 2015-12-13 12:13
//		if(btime.matches("\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}")){
//			Log.e(TAG,"btime:"+btime);
//			Long stringTime = TimeTypeUtil.getLongTime(btime);
//			Log.e(TAG,"stringTime:"+stringTime);
//			btime = TimeTypeUtil.getEasyStringTime(stringTime);
//		}else if(btime.matches("\\d{1,2}-\\d{1,2}\\s\\d{2}:\\d{2}")){
//			btime = btime.substring(6, btime.length());
//		}
//		return btime;
//	}

	/**
	 * 选择结算方式
	 * 预支付方式和正常方式
	 */
	public void selectClearOder(boolean isclick){
		/*显示结算方式前,先显示免费和收费完成按钮
		 * 这里有问题，应该要等到结算结果拿到以后，再显示免费和收费完成
		 * */
//		activity.showFreeAndChargeBtn();
		FileUtil.writeSDFile("出场结算流程", "：选择结算方式   OrderListState.getInstance().isOrderFinishState()= "+OrderListState.getInstance().isOrderFinishState()+"   (activity.getItemOrder() == null)="+(activity.getItemOrder() == null));
		if(activity.getItemOrder() == null){
			activity.showToast("未选中订单！");
			return;
		}

		if (!OrderListState.getInstance().isOrderFinishState()) {
			activity.showToast("请执行其他操作");
			return;
		}
		
		String prepay = activity.getItemOrder().getPrepay();
		Log.e(TAG, "选择结算方式"+activity.getItemOrder());
		FileUtil.writeSDFile("出场结算流程", "：activity.getItemOrder() = "+activity.getItemOrder());
		if(prepay != null){
			Double prepayNum = Double.valueOf(prepay);
		    
			if(prepayNum > 0){
				prePayOrder();
			}else{
				/*如果是警车,点击结算,直接免费刷新,播报一下警车一路顺风*/
				if(activity.getItemOrder() != null){
					String carnumber = activity.getItemOrder().getCarnumber();
					if(carnumber != null){
						
//						SharedPreferences sp = getActivity().getSharedPreferences("policeman", Context.MODE_PRIVATE);
						boolean sir = SharedPreferencesUtils.getParam(getActivity(), "zld_config", "yessir", true);
						if(sir&&StringUtils.isPolice(carnumber)){
							activity.clickFreeOrder(true);
						}else{
							FileUtil.writeSDFile("出场结算流程", "：selectClearOder  OrderListState.getInstance().isOrderFinishState():"+ OrderListState.getInstance().isOrderFinishState()+"  isclick:"+isclick);
							if(OrderListState.getInstance().isOrderFinishState()){
								completeOrder(isclick);
							}else{
								activity.showToast("请执行其他操作");
							}
						}
					}
				}
			}
		}else{
			completeOrder(isclick);
		}
	}

//	/**
//	 * 修改订单大小车计费策略 car_type=0 、通用 1、小车 2、大车
//	 */
//	private void alertCarType(){
//		RequestParams params = new RequestParams();
//		params.setUrlHeader(Constant.requestUrl + Constant.CHANGE_CAR_TYPE);
//		params.setUrlParams("comid", AppInfo.getInstance().getComid());
//		params.setUrlParams("orderid", activity.getItemOrder().getId());
//		params.setUrlParams("car_type", ""+billingType);
//		String url = params.getRequstUrl();
//		Log.e(TAG, "修改订单大小车计费策略的url是--->"+url);
//		HttpManager.requestGET(getActivity(), url, this);
//	}
	
	/**
	 * 车牌识别-请求结算订单URL---->>
	 */
	private void completeOrder(boolean isclick){
		//是否是本地服务器
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念		
			/*本地化*/
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(),"nettype","isLocal", false);
			Log.e("isLocal","OrderDetailFragment completeOrder get isLocal "+param);
//			if(param||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)){
//				localCompleteOrder();
//				return;
//			}
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.COMPLETE_ORDER);
		/**list选中的item里拿参数，保证出口来车后结算的是正确的订单   */
		params.setUrlParams("orderid", activity.getItemOrder().getId());
		if (isMonthCardUser()){
			params.setUrlParams("collect", 0);
		}else{
			if(activity.getItemOrder() != null){
				params.setUrlParams("collect", activity.getItemOrder().getTotal());
			}
		}
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("imei", AppInfo.getInstance().getImei());
		params.setUrlParams("passid", activity.passid);
		params.setUrlParams("isclick", isclick+"");
		String url = params.getRequstUrl();
		Log.e(TAG, "请求结算订单的url是--->"+url);
		FileUtil.writeSDFile("LOG", "流程：completeOrder开始请求  url:"+ url);
//		HttpManager.UpLogs(getActivity(),"流程：completeOrder开始请求  url:"+ url);
		HttpManager.requestGET(getActivity(), url, this);
	}

	/**
	 * 车牌识别-预支付请求结算订单URL---->>
	 * http://s.tingchebao.com/zld/
	 * nfchandle.do?action=doprepayorder&orderid=**&collect=*&uid=&comid=&passid=
	 */
	public void prePayOrder(){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.PRE_PAY);
		/**list选中的item里拿参数，保证出口来车后结算的是正确的订单   */
		params.setUrlParams("orderid", activity.getItemOrder().getId());
		if (isMonthCardUser()){
			params.setUrlParams("collect", 0);
		}else{
			params.setUrlParams("collect", activity.getItemOrder().getTotal());
		}
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("passid", activity.passid);
		String url = params.getRequstUrl();
		Log.e(TAG, "请求结算订单的url是--->"+url);
		HttpManager.requestGET(getActivity(), url, this);
	}
	int failureCount =  -1;
	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.CHANGE_CAR_TYPE)){
			doChargeCarTypeResult(object);
		}else if(url.contains(Constant.COMPLETE_ORDER)){
			boolean isclick= url.contains("true");
//			PollingService.sendMessage(url+"\n"+object, getActivity());
//			HttpManager.UpLogs(getActivity(),"流程结束<<<<<<<<<<：completeOrder成功    url:"+ url+"  object:"+object);
			FileUtil.writeSDFile("LOG", "流程：completeOrder成功    url:"+ url+"  object:"+object);
			activity.showFreeAndChargeBtn();
			if(object.startsWith("{")){
				doPrePayOrderResult(object);
			}else{
				doCompleteOrderResult(object,isclick);
			}
			failureCount = 0;
		}else if(url.contains(Constant.MODIFY_ORDER)){
			doModifyOrderResult(object);
		}else if(url.contains(Constant.PRE_PAY)){
			doPrePayOrderResult(object);
		}
		return super.doSucess(url, object);
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.CHANGE_CAR_TYPE)){
			Log.e(TAG,"CHANGE_CAR_TYPE_URL请求5S失败："+status);
		}else if(url.contains(Constant.COMPLETE_ORDER)){
			if (failureCount <3) {
				failureCount ++;
				activity.showToast("订单结算失败，正在重新提交！");
				HttpManager.requestGET(getActivity(), url, this);
			}else{
				activity.showToast("订单结算失败，请人工确认！");
			}
			Log.e(TAG,"COMPLETE_ORDER_URL请求5S失败："+status);
		}else if(url.contains(Constant.MODIFY_ORDER)){
			Log.e(TAG,"MODIFY_ORDER_URL请求5S失败："+status);
		}else if(url.contains(Constant.PRE_PAY)){
			Log.e(TAG,"PRE_PAY_URL请求5S失败："+status);
		}
		return super.doFailure(url, status);
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		Log.e(TAG,"订单请求超时："+url);
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.CHANGE_CAR_TYPE)){
			//doChargeCarTypeTimeOut();
		}else if(url.contains(Constant.COMPLETE_ORDER)){
			if (failureCount <3) {
				failureCount ++;
				activity.showToast("订单结算超时，正在重新提交！");
				HttpManager.requestGET(getActivity(), url, this);
			}else {
				activity.showToast("订单结算超时，请人工确认");
			}
			
//			localCompleteOrder();
		}else if(url.contains(Constant.MODIFY_ORDER)){
//			doModifyOrderTimeOut();
		}else if(url.contains(Constant.PRE_PAY)){
			//doPrePayOrderTimeOut();
		}
		super.timeout(url);
	}

	/**
	 * 改变大小车请求结果处理 
	 * @param object 
	 */
	private void doChargeCarTypeResult(String object) {
		if (Constant.sOne.equals(object)){
			activity.showToast("修改成功！");

			if (billingType == 1){
				btn_bigsmall_switch.setSelected(false);
				billingType = 2;
			}else if (billingType == 2){
				btn_bigsmall_switch.setSelected(true);
				billingType = 1;
			}
			activity.cashOrder();
		}
	}

	/**
	 * 结算订单结果处理
	 * @param object
	 */
	private void doCompleteOrderResult(String object,Boolean isclick) {
		// TODO Auto-generated method stub
		if (Constant.sOne.equals(object)||"4".equals(object)||
				"2".equals(object)||"3".equals(object)) {
			if(currenOrder == null){
				activity.showToast("此订单已结算！");
				return;
			}
			//本地化
//			getLocalOrderDBManager().deleteOrderLocalByid(currenOrder.getOrderid());
			/*将订单列表状态改为结算完成状态*/
			OrderListState.getInstance().
			setState(OrderListState.ORDER_FINISH_STATE);
			activity.cashFragment.setChargeFinishBtn(currenOrder);
			AllOrder itemOrder = activity.getItemOrder();
			if(itemOrder == null){
				return;
			}
			
			if (itemOrder.getCtype().equals("7")) {
				VoicePlayer.getInstance(this.getActivity()).playVoice("此车为月卡第二辆车");
				activity.showToast("此车为月卡第二辆车");
			}
			
			String collect = itemOrder.getTotal();
			String carnumber = itemOrder.getCarnumber();
			String limitday = itemOrder.getLimitday();
			String duration = itemOrder.getDuration();
			String showCollect1 = null;
			String showCollect2 = null;
			String content = null;
			if(TextUtils.isEmpty(collect)){
				activity.showToast("结算金额有误！");
				return;
			}
			if(collect.equals("")||collect.equals("\"null\"")||collect == null){
				activity.showToast("结算金额有误！");
				return;
			}
			if(isMonthCardUser()){
				Log.e(TAG,"月卡自动放行");
				collect = Constant.sZero;
					if (activity.getExitledinfo() != null && Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						showCollect2 = "月卡	一路顺风";
					}else {
						showCollect2 = "一路顺风";
					}
				showCollect1 = carnumber;
				content = "月卡	一路顺风";
				Log.e(TAG, "月卡有效期："+limitday);
				if (TimeTypeUtil.isMthUserExpire(limitday)){
					content = "月卡有效期至"+ TimeTypeUtil.getFutureDate(Integer.parseInt(limitday), "MM月dd日") + "一路顺风";
					activity.showToast(content);
				}
				activity.controlExitPole();
				/*修改为收费完成状态为了让订单为月卡时,入口来车有自动刷新*/
				OrderListState.getInstance().
				setState(OrderListState.CLEAR_FINISH_STATE);
				/*设置来车状态为结算完自动刷新状态,这样自动刷新之后,获取的列表第一条为月卡用户的话,就不播报了*/
				comeInCarState.setState(ComeInCarState.AUTO_REFRESH_ORDER_LIST);
				/*月卡用户下一辆车来不用免费*/
				activity.cashFragment.setFree(false);
				/*直接刷新,不用等入口有来车*/
				activity.refreshListOrder();
			}else{
				if(Double.parseDouble(collect) == 0.00f){
					Log.e(TAG,"0元自动放行");
					content = /**"停车时长"+duration+*/"停车费"+0+"元	一路顺风";
					collect = Constant.sZero;
					if (activity.getExitledinfo() != null && Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						showCollect2 = carnumber+"	0元";
					}else {
					    showCollect1 = carnumber;
						showCollect2 = "	0元";
					}

					activity.controlExitPole();
					// 不用缴费就抬杆放行，如果使用了减免券，就不要刷新，不然收费员看不到咋了车就跑了以为坏了呢
					String ticketID = activity.getItemOrder().getShopticketid();
					if (ticketID != null) {
						activity.cashFragment.hideCost();
						activity.cashFragment.showPrepay();
						activity.cashFragment.setPrepayed(null);
						activity.cashFragment.hideFreeBtn();
						OrderListState.getInstance().setState(OrderListState.ORDER_FINISH_UPPOLE_STATE);
					}else {
						/*修改为收费完成状态为了让订单为免费时,有自动刷新*/
						OrderListState.getInstance().
						setState(OrderListState.CLEAR_FINISH_STATE);
						/*设置来车状态为结算完自动刷新状态,这样自动刷新之后,获取的列表第一条为月卡用户的话,就不播报了*/
						comeInCarState.setState(ComeInCarState.AUTO_REFRESH_ORDER_LIST);
						/*0元用户下一辆车来不用免费*/
						activity.cashFragment.setFree(false);
						/*直接刷新,不用等入口有来车*/
						activity.refreshListOrder();
					}
				}else if(Double.parseDouble(collect) > 0.00f){
//					AllOrder order = activity.getItemOrder();
					StringBuilder sb = new StringBuilder();
					sb.append("************出场联************\n");
					sb.append("车场名称："+AppInfo.getInstance().getParkName()+"\n");
					sb.append("收费员："+AppInfo.getInstance().getName()+"\n");
					sb.append("车牌号："+activity.getItemOrder().getCarnumber()+"\n");
					sb.append("进场时间："+activity.getItemOrder().getBtime()+"\n");
					sb.append("出场时间："+(activity.getItemOrder().getEnd()==null?TimeTypeUtil.getNowTime():(activity.getItemOrder().getEnd()))+"\n");
					sb.append("停车时长："+activity.getItemOrder().getDuration()+"\n");
					sb.append("收费金额："+collect+"元\n");
					sb.append("******************************\n\n\n");
					PollingService.sendMessage(sb.toString(), getActivity());
					
					String ticketID = activity.getItemOrder().getShopticketid();
					if (ticketID != null) {
						activity.cashFragment.hideCost();
						activity.cashFragment.showPrepay();
						activity.cashFragment.setPrepayed(null);
					}
					
					if (collect.endsWith(Constant.sZero)) {
						Log.e(TAG,"以点零结尾");
						collect = collect.substring(0,collect.length()-2);
					}
					content = /**"停车时长"+duration+*/"请交费"+collect+"元";
					if (activity.getExitledinfo() != null && Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						showCollect2 = carnumber+"	"+collect+"元";
						
					}else {
					    showCollect1 = carnumber;
						showCollect2 = collect+"元";
					}
				}
			}
			Log.e(TAG,"累加收费员金额");
			activity.addTollmanMoney(collect,itemOrder.getPrepay(),true);
			Log.e(TAG,"LED显示");
			activity.sendLedShow(showCollect1,showCollect2,content);
			Log.e(TAG,"上传出口图片");
			//手动结算，当结算成功先回调时，图片未回调过来，延迟6s再上传图片
			uploadExitPhoto(currenOrder.getOrderid(),isclick);
			activity.showToast("结算订单成功");
			if(isHideClearOrderBtn){
				hideBtn();
			}
		}else if("-5".equals(object)){
			activity.showToast("请重新点击订单结算！");
		}else if("-6".equals(object)){
			activity.showToast("请重新点击订单结算！");
		}else{
			activity.showToast("结算订单失败！");
		}
	}

	/**
	 * 上传出口照片
	 */
	private void uploadExitPhoto(String orderid,Boolean isclick){
		Log.e(TAG,"exitFragment是否为null："+activity.exitFragment);
		if(activity.exitFragment != null){
			Log.e(TAG,"exitCarBmpInfo是否为null："+activity.exitFragment.exitCarBmpInfo);
			if(activity.exitFragment.exitCarBmpInfo != null&&!isclick){
				exitCarBmpUpload(orderid);
			}else{
				final String orderId = orderid;
			    new Handler().postDelayed(new Runnable(){      
			        public void run() {    
			        	exitCarBmpUpload(orderId);
			        }      
			     }, 3500);  
			  
			}
		}
	}

	private void exitCarBmpUpload(String orderid) {
		if(activity.exitFragment != null){
			if(activity.exitFragment.exitCarBmpInfo != null){
				String netType = 
						SharedPreferencesUtils.getParam(
								activity.getApplicationContext(),"nettype", "netType", null);
				InputStream bitmapToInputStream = 
						ImageUitls.getBitmapInputStream(netType,
								activity.exitFragment.exitCarBmpInfo.getBitmap());
				activity.upload(bitmapToInputStream,
						orderid, Constant.EXIT_PHOTOTYPE);
			}
		}
	}

	/**
	 * 更新小车牌图片
	 * @param bitmap
	 */
	public void refreshCarPlate(Bitmap bitmap){
		if(bitmap == null){
			iv_car_image.setImageResource(R.drawable.plate_sample);
		}else{
			iv_car_image.setImageBitmap(bitmap);
		}
	}

	/**
	 * 结算订单按钮文字
	 */
	private void setOrderAction(String action){
		btn_clear_order.setText(action);
	}

	private int getOrderAction(){
		String action =  btn_clear_order.getText().toString().trim();
		if (activity.getResources().getString(R.string.clear_order).equals(action)){
			return cashorder_action;
		}else{
			return modifyorder_action;
		}
	}

//	/**
//	 * 显示是否显示大小车计费按钮
//	 */
//	private void setShowBillButton(){
//		if (AppInfo.getInstance().isParkBilling()){
//			btn_bigsmall_switch.setVisibility(View.VISIBLE);			
//		}else{
//			btn_bigsmall_switch.setVisibility(View.GONE);
//		}
//	}

//	/**
//	 * 设置大小车切换按钮的样式 
//	 * @param order
//	 */
//	private void setSwitchBtnStyle(CarNumberOrder order){
//		if (order != null){
//			String carType = order.getCar_type();
//			if (carType != null){
//				if (carType.equals(Constant.sZero) || carType.equals(Constant.sOne)){
//					btn_bigsmall_switch.setSelected(false);
//					billingType = 2;
//				}else{
//					btn_bigsmall_switch.setSelected(true);
//					billingType = 1;
//				}
//			}
//		}
//	}

//	/**
//	 * 设置大小车切换按钮的样式 
//	 * @param billingType
//	 */
//	private void setSwitchBtnStyle(int billingType){
//		if (billingType == 1){
//			btn_bigsmall_switch.setSelected(false);
//			billingType = 2;
//		}else if (billingType == 2){
//			btn_bigsmall_switch.setSelected(true);
//			billingType = 1;
//		}
//	}
	
	private void setSwitchCarType(CarNumberOrder order){
		selectCarType.closePop();
		if (order != null){
			String carType = order.getCar_type();
			String carTypeName = null;
			if(AppInfo.getInstance()==null)
				return;
			int count = AppInfo.getInstance().getAllCarTypes().size();
			for (int i = 0; i < count; i++) {
				String name = AppInfo.getInstance().getAllCarTypes().get(i).getCarTypeID();
				if (name.equals(carType)) {
					carTypeName = AppInfo.getInstance().getAllCarTypes().get(i).getCarTypeName();
					btn_car_type.setText(carTypeName);
					break;
				}
			}

			if (carTypeName == null){
				carTypeName = AppInfo.getInstance().getAllCarTypes().get(0).getCarTypeName();
				btn_car_type.setText(carTypeName);
			}
			btn_car_type.setEnabled(true);
		}
	}

	private void clearView(){
		currenOrder = null;
		iv_car_image.setImageBitmap(null);
		tv_account_type.setText("");
		et_car_num.setText("");
		tv_entrance_time.setText("");
		tv_park_duration.setText("");
		btn_clear_order.setVisibility(View.INVISIBLE);
		setOrderAction(activity.getString(R.string.clear_order));
		btn_car_type.setEnabled(false);
	}

	private String getInput(){
		return et_car_num.getText().toString().trim();
	}

	private boolean isInputLegal(){
		return TextUtils.isEmpty(getInput())? false : true;
	}

	private boolean isMonthCardUser(){
		return Constant.sOne.equals(activity.listFragment.getItemOrder().getIsmonthuser());
	}

	public CarNumberOrder getCurrenOrder() {
		return currenOrder;
	}

	/**
	 * 修改车牌号
	 */
	private void modifyOrder() {
		if (!isInputLegal()){
			return;
		}
		//是否是本地服务器
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
			//本地化
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(), "nettype", "isLocal", false);
			Log.e("isLocal","OrderDetailsFragment modifyOrder get isLocal "+param);
//			if(param||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)){
////				doModifyOrderTimeOut();
//				return;
//			}
		}
		String carnumber = "";
		try {
			carnumber = URLEncoder.encode(URLEncoder.encode(getInput(), "utf-8"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.MODIFY_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("orderid", currenOrder.getOrderid());
		params.setUrlParams("carnumber", carnumber);
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "url---------------->>" + url); 
		DialogManager.getInstance().showProgressDialog(getActivity(),
				"修改当前订单...");
		HttpManager.requestGET(getActivity(), url, this);
	}

//	private void doModifyOrderTimeOut() {
//		String carnumber = getInput();
//		String orderid = currenOrder.getOrderid();
//		String localid = currenOrder.getLocalid();
//		//本地修改
////		getLocalOrderDBManager().updateOrderCarplateLocalCash(orderid,carnumber,localid);
//		//修改车牌号刷新显示
//		modifyCarPlateAfter();
//	}

	/**
	 * 修改车牌号 刷新显示
	 */
	private void modifyCarPlateAfter() {
		/*保存后隐藏结算订单按钮*/
		if(isHideClearOrderBtn){
			btn_clear_order.setVisibility(View.INVISIBLE);
		}else{
			setOrderAction(activity.getResources().getString(R.string.clear_order));
		}
		Intent intent = new Intent();
		intent.setAction(OrderListFragment.REFRESH_ITEM);
		intent.putExtra("carNumber", getInput());
		activity.sendBroadcast(intent);
	}

	/**
	 * 修改车牌号结果
	 * @param object
	 */
	private void doModifyOrderResult(String object) {
		Log.e(TAG, object);
		if (Constant.sOne.equals(object)){
			modifyCarPlateAfter();
		}else if(Constant.sZero.equals(object)){
			activity.showToast("修改失败,车牌号已存在。");
		}
	}
	public void payBack(String orderid){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.PAY_BACK);
		params.setUrlParams("orderid", orderid);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		String url = params.getRequstUrl();
		Log.e(TAG, "离场支付回调url---------------->>" + url);
		HttpManager.requestGET(getActivity(), url, this);
	}	
	/**
	 * 预支付结果处理
	 * @param object
	 */
	private void doPrePayOrderResult(String object) {
		// TODO Auto-generated method stub
		Log.e(TAG, "获取到当前订单为" + object);
		Gson gson = new Gson();
		PrePayOrder prePayOrder = gson.fromJson(object, PrePayOrder.class);
		AllOrder itemOrder = activity.getItemOrder();
		if(itemOrder == null){
			return;
		}
		
		double waitCollect = -1;
		if (itemOrder.getShopticketid() != null) {
			waitCollect = Double.parseDouble(itemOrder.getBefcollect()) - Double.parseDouble(itemOrder.getDistotal());
		}
		
		if (prePayOrder.getResult().equals(Constant.sOne) || waitCollect == 0) {//成功
			String carnumber = itemOrder.getCarnumber();
			String total = itemOrder.getTotal();
			activity.carUserPayed(carnumber,total);
			payBack(itemOrder.getId());//离场支付回调
			uploadExitPhoto(currenOrder.getOrderid(),false);
			//累加收费员金额
			activity.addTollmanMoney(null,total,true);
		}else if(prePayOrder.getResult().equals("2") || waitCollect >0){//需要补差价
			activity.cashFragment.hideCost();
			activity.cashFragment.showPrepay();
			String prefee = prePayOrder.getPrefee();
			if(prefee != null){
				activity.cashFragment.setPrepayed(prePayOrder);
				VoicePlayer.getInstance(activity).playVoice("车主已支付"+prefee+"元");
			}
			uploadExitPhoto(itemOrder.getId(),false);
			//累加收费员金额
			String total = itemOrder.getTotal();
			Float money = 0F;
			if(total != null&&prefee!=null){
				money = Float.parseFloat(total)-Float.parseFloat(prefee);
			}
			activity.addTollmanMoney(""+money,prefee,true);
		}else if(prePayOrder.getResult().equals("-1")){//失败
			activity.showToast("车主预支付结算失败！请联系云车牌系统客服！");
		}
	}

	private class OrderDetailsReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String intentAction = intent.getAction();
			if (action.equals(intentAction)){
				int state = intent.getIntExtra("state", cashorder_action);
				if (state == cashorder_action){
					setOrderAction(activity.getResources().getString(R.string.clear_order));
				}else if (state == modifyorder_action){
					if(OrderListState.getInstance().isParkOutState()){
						return;
					}
					Log.e(TAG, "当前状态设置为修改状态");
					activity.setModifyState();
					btn_clear_order.setVisibility(View.VISIBLE);
					setOrderAction(activity.getResources().getString(R.string.save_change));
				}
			}
		}

	}

	/**
	 * 本地化相关
	 */
//	private void localCompleteOrder() {
//		// TODO Auto-generated method stub
//		Log.e(TAG,"本地化结算");
//		if(currenOrder == null){
//			return ;
//		}
//		if(activity.getItemOrder() == null){
//			return;	
//		}
//		boolean police = false;
//		boolean isOpen = false;
//		String pay_type = Constant.sOne;
//		String c_type = "3";
//		Long currentTime = System.currentTimeMillis()/1000;
//		Log.e(TAG, "结算订单,ItemOrder数据插入数据库"+activity.getItemOrder().toString());
//		Log.e(TAG, "结算订单,currenOrder数据插入数据库"+currenOrder.toString());
//		// 根据车牌号查询是否有本地入场订单
//		final String carnumber = activity.getItemOrder().getCarnumber();
//		String total = activity.getItemOrder().getTotal();
//		String prepay = activity.getItemOrder().getPrepay();
//		final String orderid = activity.getItemOrder().getId();
//		String localid = activity.getItemOrder().getLocalid();
//
//		if(currenOrder.getIsmonthuser()!=null){
//			if(currenOrder.getIsmonthuser().equals(Constant.sOne)){
//				total = Constant.sZero;
//				pay_type = "3";
//				c_type = "5";
//				isOpen = true;
//			}
//			//是否是警车，
//			if(StringUtils.isPolice(carnumber)){
//				//是
//				total = Constant.sZero;
//				police = true;
//			}
//		}
//		if(getLocalOrderDBManager().selectOrderIsCash(orderid,localid)){
//			//本地自动结算
//			getLocalOrderDBManager().updateOrderLocalCash(orderid,localid,pay_type,c_type,
//					AppInfo.getInstance().getUid(),total,""+currentTime,activity.passid);
//
//			carPlate = carnumber;
//			ordid = orderid;
//			Log.e(TAG,""+carnumber + "==" + orderid);
//			//结算完成,保存图片
//			activity.setTakePhotoLinster(new TakePhotoLinster() {
//
//				@Override
//				public void setTakePhotoLinster(Bitmap bitmap) {
//					// TODO Auto-generated method stub
//					if(carPlate.equals(carnumber)){
//						Log.e(TAG,"true carPlate:"+carPlate+" ordid:"+ordid+" carnumber:"+carnumber+" orderid:"+orderid);
//						activity.saveImage(activity.resultBitmap, carnumber, orderid);
//					}else{
//						Log.e(TAG,"false carPlate:"+carPlate+" ordid:"+ordid+" carnumber:"+carnumber+" orderid:"+orderid);
//
//						activity.saveImage(bitmap,carPlate, ordid);
//					}
//				}
//			});
//
//			//累加收费员金额
//			activity.addTollmanMoney(total,prepay,true);
//			activity.showToast("结算订单成功");
//			Log.e(TAG,"=警车："+police+"==月卡："+isOpen);
//			if(total==null){
//				return;
//			}
//			if(police||isOpen||(total!=null&&total.equals(Constant.sZero))||total.equals("0.0")){
//				if(isOpen){
//					VoicePlayer.getInstance(activity).playVoice("月卡一路顺风");
//				}
//				if(police){
//					VoicePlayer.getInstance(activity).playVoice("警车已免费");
//				}
//				activity.controlExitPole();
//				//activity.sendLedShow(exitledinfo,Constant.sZero,"一路顺风");
//				activity.hideFreeAndChargeBtn();
//				OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
//				activity.refreshListOrder();
//			}else{
//				OrderListState.getInstance().setState(OrderListState.ORDER_FINISH_STATE);
//			}
//		}else
//		{
//			activity.showToast("订单已结算");
//		}
//		return;
//	}

}

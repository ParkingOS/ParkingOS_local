/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/
package com.zld.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zld.R;
import com.zld.adapter.CurrentOrderAdapter;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.CarBitmapInfo;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.CurrentOrder;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.dialog.ToastManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.ComeInCarState;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.lib.util.VoicePlayer;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

//import com.zld.local.bean.LocalCurrentOrder;

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
public class OrderListFragment extends BaseFragment implements OnClickListener {
	public static final String REFRESH_ITEM = "REFRESH_ITEM";
	private static final String TAG = "OrderListFragment";
	private OrderListListener myListener;
	private View mainView;
	private int page = 1;
	private int size = 10;
	private AllOrder itemOrder;
	private boolean auto=false;
	private CurrentOrder orders;
	private boolean isShowFirstItem;
	private CurrentOrderAdapter adapter;
	public  OrderListState orderListState;
	private PullToRefreshListView lv_current_order;
	private RadioButton[] rbs = new RadioButton[2];
	private int listFlag = 0;//listview 显示的是在场还是离场的标记
	//自动搜索的车牌号，记录下来的原因是如果匹配不上还要匹配中间的五位字符
	private String searchCarNumber;
	private int j=0;//重试订单列表失败

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.LIST_REFRESH:
				@SuppressWarnings("unchecked")
				ArrayList<AllOrder> localorders  = (ArrayList<AllOrder>) msg.obj;
				int type = msg.arg1;
				localOrderShow(localorders,type);
				break;

			default:
				break;
			}
		}
	};
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mainView = inflater.inflate(
				R.layout.pulltorefreshlistview, container,false);
		initView();
		registerBr();
		initComid();
		return mainView;
	}

	private void initComid() {
		String comid = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(), "zld_config", "comid", null);
		AppInfo.getInstance().setComid(comid);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		rbs[0].performClick();
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		myListener = (OrderListListener) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
	}

	private void initView() {
		rbs[0] = (RadioButton) mainView.findViewById(R.id.in_park_cars);
		rbs[1] = (RadioButton) mainView.findViewById(R.id.out_park_cars);
		for (int i = 0; i < rbs.length; i++) {
			rbs[i].setTag(i);
			rbs[i].setOnClickListener(this);
		}
		orderListState = OrderListState.getInstance();
		adapter = new CurrentOrderAdapter(getActivity(), null);
		lv_current_order = (PullToRefreshListView) mainView
				.findViewById(R.id.pulltorefreshListView);
		lv_current_order.setMode(PullToRefreshBase.Mode.BOTH);
		lv_current_order.setAdapter(adapter);
		lv_current_order.setOnRefreshListener(
				new PullToRefreshBase.OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (listFlag == 0){
							/*手动刷新,切换播报月卡用户状态,出口来车后,手动刷新不播报*/
							activity.setDetailInCarState(ComeInCarState.MANUAL_REFRESH_ORDER_LIST);
							getOrderInPark(true);
						}else if (listFlag == 1){
							getHistoryOrder(true);
						}
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						if (listFlag == 0){
							getOrderInPark(false);
						}else if (listFlag == 1){
							getHistoryOrder(false);
						}
					}
				});
		lv_current_order.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				if(position == 0){
					activity.showToast("请刷新列表");
					return;
				}
				itemOrder = adapter.getAllOrders(position);
				adapter.highLightSelectedItem(position);
				Log.e("OrderListState", "当前状态为："+orderListState.getState());
				if(OrderListState.getInstance().isParkOutState()){
					setOrderDetails();
				}else{
					if(itemOrder != null){
						clearOrthersFragment();
						cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
					}else{
						activity.showToast("请刷新列表");
					}
				}
			}
		});
	}

	public AllOrder getItemOrder() {
		return itemOrder;
	}

	public void setItemOrder(AllOrder itemOrder) {
		this.itemOrder = itemOrder;
	}

	public boolean isAuto() {
		return auto;
	}

	public void setAuto(boolean auto) {
		this.auto = auto;
	}

	/**
	 * 设置订单详情
	 */
	private void setOrderDetails() {
		CarNumberOrder carNumberOrder = allOrderChangeCarNumber();
		myListener.refreshCashView(carNumberOrder);
		myListener.refreshDetailView(carNumberOrder);
		myListener.refreshRecordView(carNumberOrder);
	}

	/**
	 * 清空其它fragment的view
	 */
	public void clearOrthersFragment(){
		myListener.refreshCashView(null);
		myListener.refreshDetailView(null);
		myListener.refreshRecordView(null);
	}

	/**
	 * 订单列表广播
	 */
	private void registerBr() {
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(REFRESH_ITEM);
		activity.registerReceiver(new ListReceiver(), intentFilter);
	}

	/**
	 * 在场离场选项卡
	 * @param position
	 */
	private void setRadioChecked(int position) {
		for (int i = 0; i < rbs.length; i++) {
			rbs[i].setChecked(i == position);
		}
		switch (position) {
		case 0:
			//当前内容在场列表
			listFlag = 0;
			changeRadioBtnColor(position);
			/*手动刷新,切换播报月卡用户状态,出口来车后,手动刷新不播报*/
			activity.setDetailInCarState(ComeInCarState.MANUAL_REFRESH_ORDER_LIST);
			getOrderInPark(true);
			break;
		case 1:
			//当前内容离场列表
			listFlag = 1;
			changeRadioBtnColor(position);
			getHistoryOrder(true);
			break;
		}
		Log.e(TAG, "列表切换时,根据当前状态,显示or隐藏按钮");
		activity.showOrHideBtn();
	}

	/**
	 * 改变RadioBtn颜色
	 * @param position
	 */
	public void changeRadioBtnColor(int position) {
		for (int i = 0; i < rbs.length; i++) {
			if(i == position){
				rbs[i].setBackgroundColor(
						activity.getResources().getColor(R.color.white));
				rbs[i].setTextColor(this.getResources().getColor(R.color.dark_grenn));
			}else{
				rbs[i].setBackgroundColor(
						activity.getResources().getColor(R.color.dark_grenn));
				rbs[i].setTextColor(this.getResources().getColor(R.color.white));
			}
		}
	}

	/**
	 * 手动搜索和出口识别后自动搜索结算公用方法，需要传入状态给listview
	 * @param carnumber
	 * @param orderListState
	 */
	public void searchCarNumber(String carnumber, int orderListState) {
		FileUtil.writeSDFile("出场结算流程", "：searchCarNumber  "+carnumber +"  orderListState:"+orderListState);
		Log.e(TAG, "出口Fragment搜索的车牌号："+carnumber);
		searchCarNumber = carnumber;
		this.orderListState.setState(orderListState);
		try {
			if(carnumber == null){
				activity.showToast("请输入你要搜索的车牌号！");
				return;
			}
			searchCarNumberOrder(carnumber);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 从网络上获取当前订单；
	 * @param isRrefrech 是否刷新
	 */
	public void getOrderInPark(boolean isRrefrech) {
		if(this.orderListState != null){
			this.orderListState.setState(OrderListState.PARK_IN_STATE);
		}
		isShowFirstItem = isRrefrech;
		if (isRrefrech) {
			page = 1;
			adapter.removeOrders();
			adapter.setSelectedPosition(0);
		} else {
			page++;
		}
		//是否是本地服务器
		Log.e(TAG,"===getIsLocalServer:"+!AppInfo.getInstance().getIsLocalServer(activity));
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
			boolean isLocal = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(),"nettype", "isLocal", false);
			Log.e("isLocal","OrderListFragment getOrderInPark get isLocal "+isLocal);
//			Log.e(TAG,"本地订单列表："+AppInfo.getInstance().getIssuplocal());
//			if (isLocal||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)) {
//				doGetOrdersTimeOut(0);
//				return;
//			}
		}

		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.GET_CURRORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("page", page);
		params.setUrlParams("size", size);
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "在场车辆订单列表：url---------------->>" + url);
		DialogManager.getInstance().showProgressDialog(activity,
				"获取在场订单...");
		HttpManager.requestGET(getActivity(), url, this);
	}

	/**
	 * 从网络上获取历史订单;
	 * @param isRrefrech
	 */
	private void getHistoryOrder(boolean isRrefrech) {
		if(this.orderListState != null){
			this.orderListState.setState(OrderListState.PARK_OUT_STATE);
		}
		isShowFirstItem = isRrefrech;
		if (isRrefrech) {
			page = 1;
			adapter.removeOrders();
			adapter.setSelectedPosition(0);
		} else {
			page++;
		}

		//		是否是本地服务器
		//		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
		//		// 本地化
		//		boolean param = SharedPreferencesUtils.getParam(
		//		activity.getApplicationContext(),"nettype", "isLocal", false);
		//		Log.e("isLocal","OrderListFragment getHistoryOrder get isLocal "+param);
		//		if (param) {
		//			Log.e(TAG,"历史订单列表");
		//			return;
		//		}
		//	}

		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.ORDER_HISTORY);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("page", page);
		params.setUrlParams("size", size);
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("day", "today");
		params.setUrlParams("ptype", 1);
		params.setUrlParams("out", "json");
		String url = params.getRequstUrl();
		Log.e(TAG, "离场车辆订单列表：url---------------->>" + url);
		DialogManager.getInstance().showProgressDialog(getActivity(),
				"获取离场车场订单列表...");
		HttpManager.requestGET(getActivity(), url, this);
	}

	/**
	 * 根据输入车牌号,查询符合的订单
	 * @param carNumber
	 * @throws UnsupportedEncodingException 
	 */
	public void searchCarNumberOrder(String carNumber) throws UnsupportedEncodingException{
		//是否是本地服务器
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
			//本地化
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(), "nettype", "isLocal", false);
			Log.e("isLocal","OrderListFragment searchCarNumberOrder get isLocal "+param);
			if(param||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)){
				Log.e("OrderListState", "当前状态2为手动,3为自动："+orderListState.getState());
				if(orderListState.getState() == OrderListState.HAND_SEARCH_STATE){
//					doHandSearchTimeOut(carNumber);
				}else if(orderListState.getState() == OrderListState.AUTO_SEARCH_STATE){
//					activity.doAutoSearchTimeOut(activity.resultBitmap,0, carNumber);
				}
				return;
			}
		}
		String ecodeCarNumber = URLEncoder.encode(URLEncoder.encode(carNumber, "utf-8"),"utf-8");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl +  Constant.QUERY_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("carnumber", ecodeCarNumber);
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("through", 3);
		params.setUrlParams("search", 2);//1.2.4版本为0  1.2.5版本以上为2
		String url = params.getRequstUrl();
		Log.e(TAG, "根据车牌号查询订单：url---------------->>" + url);
		DialogManager.getInstance().showProgressDialog(getActivity(),
				"搜索订单...");
		FileUtil.writeSDFile("出场结算流程", "：searchCarNumberOrder   carNumber:"+ carNumber +" url:"+url);
//		HttpManager.UpLogs(getActivity(),"流程：searchCarNumberOrder   carNumber:"+ carNumber +" url:"+url);
		HttpManager.requestGET(getActivity(), url,carNumber, this);			
	}

//	private void doHandSearchTimeOut(String carNumber) {
//		//本地修改
//		ArrayList<AllOrder> orders = loDBManager.queryLocalLikeAllOrderBycarNumber(0,carNumber,null);
//		//搜索出的订单列表刷新显示
//		if (orders == null||orders.size() == 0){
//			adapter.removeOrders();
//			clearOrthersFragment();
//			itemOrder = null;
//			ToastManager.getInstance().showToast(activity, "无此订单...", Toast.LENGTH_LONG);
//			return;
//		}else{
//			Log.e(TAG, "本地搜索出的订单："+orders.toString());
//			orders = selectMonthUser(orders);
//			adapter.removeOrders();
//			adapter.addOrders(orders);
//			adapter.setSelectedPosition(0);
//			if(orders.get(0)!= null){
//				itemOrder = orders.get(0);
//				cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
//			}
//		}
//	}

	/**
	 * 免费订单
	 */
	private void freeOrder() {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.FREE_ORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("orderid", itemOrder.getId());
		params.setUrlParams("passid", activity.passid);
		params.setUrlParams("isPolice", 1);
		String url = params.getRequstUrl();
		Log.e(TAG, "免费订单的url是--->"+url);
		HttpManager.requestGET(getActivity(), url, this);
	}


	/**
	 * 把订单号提交给服务器获取订单详情;
	 */
	public void cashOrder(int type,String orderid,String localid){
		//是否是本地服务器
		if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
			boolean param = SharedPreferencesUtils.getParam(
					activity.getApplicationContext(),"nettype", "isLocal", false);
			Log.e("isLocal","OrderListFragment cashOrder get isLocal "+param);
			if (param||AppInfo.getInstance().getIssuplocal().equals(Constant.sOne)) {
				// 本地化相关
//				doCashOrderTimeOut(type,orderid,localid);
				return;
			}
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.CAT_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("orderid", orderid);
		params.setUrlParams("ptype", 1);
		String url = params.getRequstUrl();
		Log.e(TAG, "根据订单号获取订单详情url---------------->>" + url);
		DialogManager.getInstance().showProgressDialog(getActivity(),
				"获取订单详情...");
		FileUtil.writeSDFile("出场结算流程", "：doAutoSearchResult且只对应了一个订单开始调接口cashOrder/catorder   orderid:"+ orderid +"  localid"+localid+" url:"+url);
//		HttpManager.UpLogs(getActivity(),"流程：doAutoSearchResult且只对应了一个订单开始调接口cashOrder/catorder   orderid:"+ orderid +"  localid"+localid+" url:"+url);
		HttpManager.requestGET(getActivity(), url,orderid,localid, this);		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.in_park_cars:
			setRadioChecked((Integer) v.getTag());
			break;
		case R.id.out_park_cars:
			setRadioChecked((Integer) v.getTag());
			break;
		default:
			break;
		}
	}

	@Override
	public boolean doSucess(String url, String object){
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.GET_CURRORDER)) {
			j=0;
			doGetOrdersResult(object);
		}else if(url.contains(Constant.ORDER_HISTORY)){
			doGetOrdersResult(object);
		}else if (url.contains(Constant.FREE_ORDER)){
			doFreeOrderResult(object);
		}
		return true;
	}

	@Override
	public boolean doSucess(String url, String object, String str) {
		// TODO Auto-generated method stub
//		HttpManager.UpLogs(getActivity(),"流程：searchCarNumberOrder成功   Constant.QUERY_ORDER:"+ Constant.QUERY_ORDER+"  orderListState.getState()="+orderListState.getState() +" url:"+url+ "  object:"+object);
		FileUtil.writeSDFile("出场结算流程", "：searchCarNumberOrder成功   Constant.QUERY_ORDER:"+ Constant.QUERY_ORDER +"  orderListState.getState()="+orderListState.getState()+" url:"+url+ "  object:"+object);
		if(url.contains(Constant.QUERY_ORDER)){
			//当前状态为手动搜索
			Log.e("OrderListState", "当前状态为："+orderListState.getState());
			if(orderListState.getState() == OrderListState.HAND_SEARCH_STATE){
				Log.e(TAG,  "收到输入车牌搜索状态订单："+Constant.QUERY_ORDER+"------------->"+ object);
				doHandSearchResult(object);
				//当前状态车识别后自动搜索 
				Log.e("OrderListState", "当前状态为："+orderListState.getState());
			}else if(orderListState.getState() == OrderListState.AUTO_SEARCH_STATE){
				Log.e(TAG,  "出口来车自动搜索后状态订单："+Constant.QUERY_ORDER+"------------->"+ object);
				doAutoSearchResult(object);
			}
		}
		return true;
	}

	@Override
	public boolean doSucess(String url, String object, String str1, String str2) {
		// TODO Auto-generated method stub
//		HttpManager.UpLogs(getActivity(),"流程：cashOrder成功   Constant.CAT_ORDER:"+ Constant.CAT_ORDER +" url:"+url+ "  object:"+object);
		if(url.contains(Constant.CAT_ORDER)){
			FileUtil.writeSDFile("出场结算流程", "：cashOrder成功   Constant.CAT_ORDER:"+ Constant.CAT_ORDER +" url:"+url+ "  object:"+object);
			Log.e(TAG, "当前订单详情："+Constant.CAT_ORDER+" ------------->"+ object);
			doCashOrderResult(object);
		}
		return true;
	}

	@Override
	public void timeout(String url) {
//		// TODO Auto-generated method stub
//		DialogManager.getInstance().dissMissProgressDialog();
//		if (url.contains(Constant.GET_CURRORDER)) {
//			Log.e(TAG,"超时再获取_当前订单列表");
//			getOrderInPark(true);
//		}else if(url.contains(Constant.ORDER_HISTORY)){
//			Log.e(TAG, "超时再获取——历史订单："+Constant.ORDER_HISTORY);
//			getHistoryOrder(true);
//		}else if (url.contains(Constant.FREE_ORDER)){
//			//doFreeOrderTimeOut();
//		}
	}

	@Override
	public void timeout(String url, String str) {
		// TODO Auto-generated method stub
		if(url.contains(Constant.QUERY_ORDER)){
			Log.e("OrderListState", "当前状态为："+orderListState.getState());
			if(orderListState.getState() == OrderListState.HAND_SEARCH_STATE){
				Log.e(TAG,  "收到输入车牌搜索状态订单："+Constant.QUERY_ORDER);
				// str 为carnumber
//				doHandSearchTimeOut(str);
			}else if(orderListState.getState() == OrderListState.AUTO_SEARCH_STATE){
				Log.e(TAG,  "出口来车自动搜索后状态订单："+Constant.QUERY_ORDER);
				// str 为carnumber
//				activity.doAutoSearchTimeOut(activity.resultBitmap,0, str);
			}
		}
	}

	@Override
	public void timeout(String url, String str, String str2) {
		// TODO Auto-generated method stub
		if(url.contains(Constant.CAT_ORDER)){
			Log.e(TAG, "超时当前订单详情："+Constant.CAT_ORDER);
			// str为orderid str2为localid
//			doCashOrderTimeOut(0,str,str2);
			FileUtil.writeSDFile("出场结算流程", "：catorder超时了  ");
		}
	}

	private void doFreeOrderResult(String result){
		Log.e(TAG, "免费订单的网络返回结果是---------------》" + result);
		if (Constant.sOne.equals(result)){
			VoicePlayer.getInstance(activity).playVoice("此车已免费");
			/* 开启出口道闸  */
			activity.controlExitPole();
			orderListState.setState(OrderListState.CLEAR_FINISH_STATE);
			activity.sendLedShow("",null,"一路顺风");
			activity.refreshListOrder();
		}
	}

	private void doCashOrderResult(String object) {
		Gson gson = new Gson();
		CarNumberOrder order = gson.fromJson(object, CarNumberOrder.class);
		if(!object.equals("-1")){
			if(itemOrder == null){
				activity.showToast("此订单已结算！");
				return;
			}
			if (!itemOrder.getId().equals(order.getOrderid())) {
				activity.showToast("数据刷新错误，请重试！");
				return;
			}
			
			if(order != null){
				order.setLefttop(itemOrder.getLefttop());
				order.setRightbottom(itemOrder.getRightbottom());
				order.setHeight(itemOrder.getHeight());
				order.setWidth(itemOrder.getWidth());
				order.setCtype(itemOrder.getCtype());
				//更新当前选中条目的结算金额
				itemOrder.setTotal(order.getCollect());
				//是否减免 减免券ID，用于判断有没有使用减免券 3：减时券 4：全免券
				if (order.getShopticketid() != null && Integer.valueOf(order.getShopticketid()) != -1) {
					itemOrder.setBefcollect(order.getBefcollect());
					itemOrder.setDistotal(order.getDistotal());
					itemOrder.setShopticketid(order.getShopticketid());
					itemOrder.setTickettime(order.getTickettime());
					itemOrder.setTickettype(order.getTickettype());
				}
				//是否预支付>0为表示有预支付
				itemOrder.setPrepay(order.getPrepay());
				itemOrder.setLimitday(order.getLimitday());
				itemOrder.setDuration(order.getDuration());
				Log.i(TAG, "当前订单详情网络响应："+order.toString());
				Log.i(TAG, "之前订单详情网络响应："+itemOrder.toString());
				myListener.refreshCashView(order);
				myListener.refreshDetailView(order);
				myListener.refreshRecordView(order);
				FileUtil.writeSDFile("出场结算流程", "：doCashOrderResult  auto:"+ auto +" orders:"+order.toString());
				if(auto){
					System.out.println("获取到当前订单选择结算" + orders);
					activity.detailsFragment.selectClearOder(false);
					setAuto(false);
				}
			}
		}
	}

	/**
	 * 手动查询订单的结果处理
	 */
	private void doHandSearchResult(String object) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		CurrentOrder orders = gson.fromJson(object, CurrentOrder.class);
		if(orders != null){
			Log.e(TAG, "手动查询到对应车牌号订单-->>"+orders.toString());
			ArrayList<AllOrder> orderinfos = orders.getInfo();
			//下方显示订单详情
			if (orderinfos != null) {
				//获取服务器返回的数量
				int count = StringUtils.stringToInt(orders.getCount());
				if (count == 0){
					adapter.removeOrders();
					clearOrthersFragment();
					itemOrder = null;
					ToastManager.getInstance().showToast(activity, "无此订单...", Toast.LENGTH_LONG);
					return;
				}else{
//					orderinfos = selectMonthUser(orderinfos);
					adapter.removeOrders();
					adapter.addOrders(orders.getInfo());
					if(orders.getInfo().get(0) != null){
						itemOrder = orders.getInfo().get(0);
						if(itemOrder == null){
							return;
						}
						adapter.setSelectedPosition(0);
						String orderid = orders.getInfo().get(0).getId();
						String localid = orders.getInfo().get(0).getLocalid();
						cashOrder(0,orderid,localid);
					}
				}
			}
		}
	}

	/**
	 * 自动搜索订单的结果处理
	 */
	@SuppressWarnings("null")
	private void doAutoSearchResult(String object) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		CurrentOrder orders = gson.fromJson(object, CurrentOrder.class);
		Log.e(TAG, "获取到当前订单为" + object+"  orders:"+orders);
		FileUtil.writeSDFile("出场结算流程", "：dowutosearchResult  gson解析后的order："+orders.toString());
		if(orders == null&&orders.getInfo() == null&&orders.getInfo().get(0) ==null){
			return;
		}
		Log.e(TAG, "orders.getinfo:" + orders.getInfo());
		//月卡无订单记录,则自动放行
		if(Integer.valueOf(orders.getCount()) == 0&& orders.getInfo().get(0).getIsmonthuser().equals(Constant.sOne)){
			String limitday = orders.getInfo().get(0).getExptime();
			//月卡订单处理
			FileUtil.writeSDFile("出场结算流程", "：处理月卡无订单记录自动放行   "+limitday+"   carnumber"+orders.getInfo().get(0).getCarnumber());
			monthuserDeal(limitday,orders.getInfo().get(0).getCarnumber());
			return;
		}
		if (orders.getCount() != " "&&orders.getCount() != null) {
			//清空列表内容
			adapter.removeOrders();
			//清空显示详情
			clearOrthersFragment();
			//更新当前订单
			itemOrder = orders.getInfo().get(0);
			String isAuto = orders.getIsauto();
			//获取服务器返回的数量
			int count = StringUtils.stringToInt(orders.getCount());
			Log.e(TAG,"count:"+count);
			FileUtil.writeSDFile("出场结算流程", "：获取服务器返回的数量  "+count+"  isauto= "+isAuto);
			if (count == 0){
				if(SharedPreferencesUtils.getParam(
						activity, "zld_config", "leaveset", Constant.sZero).equals(Constant.sOne)){
					activity.controlExitPole();
					getOrderInPark(true);
				}else{
					orderListState.setState(OrderListState.NO_ORDER_STATE);
					clearOrthersFragment();
					/**无订单记录用户下一辆车来不用免费*/
					activity.cashFragment.setFree(false);
					/** 如果是军警车,则自动抬杆,搜索不到走生成结算,再设置自动刷新*/
					int ntype = activity.exitFragment.exitCarBmpInfo.getNtype();
					boolean policePole = activity.isPolicePole(ntype);
					/** 如果是军警车自动抬杆了,就不播报以下信息*/
					Log.e(TAG,"policePole:"+policePole);
					if(!policePole){
						VoicePlayer.getInstance(activity).playVoice(activity.getString(R.string.no_order_tip));
						ToastManager.getInstance().showToast(activity, "无此订单...", Toast.LENGTH_LONG);
					}
					activity.showFreeHideChargeFinish();
					return;
				}
			}
			/* 如果只有一个订单匹配上了则自动结算掉此订单  */
			else if ((count == 1&&isAuto == null)||(count == 1&&isAuto != null&&isAuto.equals(Constant.sOne))){
				System.out.println("获取到当前订单的集合的长度" + orders.getInfo().size());
				Log.e(TAG, orders.getInfo().toString());
				FileUtil.writeSDFile("出场结算流程", "：只有一个订单匹配了就自动结算  "+orders.getInfo().toString());
				adapter.addOrders(orders.getInfo());
				adapter.setSelectedPosition(0);
				/*先判断是否是军警车,如果是的话,就免单,return,不用获取详情显示*/
				int nType = 0;
				CarBitmapInfo exitCarInfo = activity.exitFragment.getExitCarInfo();
				if(exitCarInfo != null){
					Log.e(TAG,"获取出口的信息："+exitCarInfo.toString());
					nType = exitCarInfo.getNtype();
				}
				Log.e(TAG,"获取出口的nType："+nType);
				/* 军警车流程 */
				if (nType == Constant.LT_ARMPOL || nType == Constant.LT_ARMPOL2 ||
						nType == Constant.LT_ARMPOL2_ZONGDUI || nType == Constant.LT_ARMPOL_ZONGDUI || 
						nType == Constant.LT_ARMY || nType == Constant.LT_ARMY2 || nType == Constant.LT_POLICE){
					freeOrder();
					return;
				}
				/* 月卡车牌识别对,匹配错误流程处理 */
				if(orders.getInfo().get(0).getIsmonthuser().equals(Constant.sOne)){
					String collect = "月卡	一路顺风";
					String content = "月卡	一路顺风";
//					VoicePlayer.getInstance(activity).playVoice("此车为月卡用户");
//					activity.sendLedShow(collect,content);
					//activity.detailsFragment.selectClearOder();
					//freeOrder();
					//setAuto(false);
					//return;
				}
				/* 此操作主要是刷新其它fragment的view */
				setAuto(true);
				cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
			}
			/* 如果多个订单匹配上了，只列出列表内容，结算动作交给收费员   */			
			else{
				activity.showToast("请手动选择结算订单");
				adapter.addOrders(orders.getInfo());
				adapter.setSelectedPosition(0);
				AllOrder allOrder = orders.getInfo().get(0);
				if(allOrder!=null&&allOrder.getId()!=null){
					cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
				}
				VoicePlayer.getInstance(activity).playVoice("请手动选择结算订单");
				Log.e(TAG, SharedPreferencesUtils.getParam(
						activity, "zld_config", "leaveset", Constant.sZero));
				if(SharedPreferencesUtils.getParam(
						activity, "zld_config", "leaveset", Constant.sZero).equals(Constant.sOne)){
					activity.controlExitPole();
				}
			}
		}
	}

	@SuppressWarnings("null")
	private void monthuserDeal(String limitday,String carnumber) {
		Log.e(TAG,"月卡自动放行");
		String collect1 = "月卡	一路顺风";
		String collect2 = "一路顺风";
		String content = "月卡	一路顺风";
		if(limitday != null&&!limitday.equals("-1")){
			Log.e(TAG, "月卡有效期："+limitday);
			if (TimeTypeUtil.isMthUserExpire(limitday)){
				content = "月卡有效期至"+ TimeTypeUtil.getFutureDate(Integer.parseInt(limitday), "MM月dd日") + "一路顺风";
				activity.showToast(content);
			}
		}
		activity.controlExitPole();
		/*修改为收费完成状态为了让订单为月卡时,入口来车有自动刷新*/
		OrderListState.getInstance().
		setState(OrderListState.CLEAR_FINISH_STATE);
		/*设置来车状态为结算完自动刷新状态,这样自动刷新之后,获取的列表第一条为月卡用户的话,就不播报了*/
		activity.setDetailInCarState(ComeInCarState.AUTO_REFRESH_ORDER_LIST);
		/*直接刷新,不用等入口有来车*/
		activity.refreshListOrder();
		if (activity.getExitledinfo() != null) {
			if (Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
				activity.sendLedShow(carnumber,collect1,content);
			}else {
				activity.sendLedShow(carnumber,collect2,content);
			}
		}

		/*无订单记录时,返回为月卡的话,没有orderid则不用上传图片*/
		/*uploadExitPhoto(currenOrder.getOrderid());*/
		activity.showToast("结算订单成功");
	}

	/**
	 * 获取车场当前订单的结果处理
	 * 
	 * @param object
	 */
	private void doGetOrdersResult(String object) {
		lv_current_order.onRefreshComplete();
		if (object.length() > 30) {
			Gson gson = new Gson();
			orders = gson.fromJson(object, CurrentOrder.class);
			if (orders == null || orders.getInfo().size() == 0) {
				page--;
				return;
			}
			adapter.addOrders(orders.getInfo());
			Log.e(TAG, "是否显示第一个:"+isShowFirstItem+" 显示列表Order详情"+orders.getInfo().get(0));
			Log.e(TAG, "OrderListState的状态:"+orderListState.getState());
			itemOrder = orders.getInfo().get(0);
			if(isShowFirstItem&&orderListState.getState() == OrderListState.PARK_IN_STATE){
				cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
			}else if(isShowFirstItem&&OrderListState.getInstance().isParkOutState()){
				setOrderDetails();
			}
		}else{
			activity.showToast("没有当天的订单记录！");
		}
	}

	public String getOrderid(){
		if(itemOrder != null){
			return itemOrder.getId();
		}else{
			return null;
		}
	}

	public interface OrderListListener{
		public void refreshDetailView(CarNumberOrder order);
		public void refreshCashView(CarNumberOrder order);
		public void refreshRecordView(CarNumberOrder order);
	}

	private class ListReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String intentAction = intent.getAction();
			if (REFRESH_ITEM.equals(intentAction)){
				String carNumber = intent.getStringExtra("carNumber");
				adapter.changeItemContent(carNumber);
			}
		}
	}


	/**
	 * 将allOrder转为CarNumberOrder,
	 * 在获取的离场车辆列表里包含了订单详情,不用再次根据orderid去获取详情了
	 * @return
	 */
	private CarNumberOrder allOrderChangeCarNumber() {
		if(itemOrder == null){
			activity.showToast("请重新点击订单！");
			return null;
		}
		CarNumberOrder carNumberOrder = new CarNumberOrder();
		String begindate = "";
		if(itemOrder.getBtime() != null){
			//是否是本地服务器
			if(!AppInfo.getInstance().getIsLocalServer(activity)){//是本地服务器则没有平板本地化的感念
				boolean param = SharedPreferencesUtils.getParam(
						activity.getApplicationContext(), "nettype", "isLocal", false);
				Log.e("isLocal","OrderListFragment allOrderChangeCarNumber get isLocal "+param);
				if(param){
					begindate = itemOrder.getBtime();
				}else{
					begindate = TimeTypeUtil.getEasyStringTime(
							TimeTypeUtil.getLongTime(itemOrder.getBtime()));
				}
			}else{
				begindate = TimeTypeUtil.getEasyStringTime(
						TimeTypeUtil.getLongTime(itemOrder.getBtime()));
			}
		}
		if(itemOrder.getDuration() != null&&itemOrder.getDuration().substring(0, 2) != null){
			if(itemOrder.getDuration().substring(0, 2).equals("停车")){
				carNumberOrder.setDuration(itemOrder.getDuration().substring(2));
			}else{
				carNumberOrder.setDuration(itemOrder.getDuration());
			}
		}else{
			carNumberOrder.setDuration("停车时长未知");
		}
		carNumberOrder.setBtime(begindate);
		carNumberOrder.setCar_type(itemOrder.getCar_type());
		carNumberOrder.setCarnumber(itemOrder.getCarnumber());
		carNumberOrder.setCollect(itemOrder.getTotal());

		carNumberOrder.setHeight(itemOrder.getHeight());
		carNumberOrder.setCtype(itemOrder.getCtype());
		carNumberOrder.setLefttop(itemOrder.getLefttop());
		carNumberOrder.setOrderid(itemOrder.getId());
		carNumberOrder.setLocalid(itemOrder.getLocalid());
		carNumberOrder.setPrepay(itemOrder.getPrepay());
		carNumberOrder.setRightbottom(itemOrder.getRightbottom());
		carNumberOrder.setTotal(itemOrder.getTotal());
		carNumberOrder.setWidth(itemOrder.getWidth());
		carNumberOrder.setUin(itemOrder.getUin());//-1未绑定 其他都是绑定

		/*carNumberOrder.setDiscount(itemOrder.get);优惠金额*/
		/*carNumberOrder.setEtime(itemOrder.get);结束时间*/
		/*carNumberOrder.setHascard(itemOrder.get);是否有车牌*/
		return carNumberOrder;
	}

	/**
	 * 无网 本地化模式  查询数据库 显示
	 * @param type 0在场 1离场
	 */
	public void doGetOrdersTimeOut(final int type) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
//				ArrayList<AllOrder> localorders = getLocalOrderThread(type);
				Message msg = new Message();
				msg.what = Constant.LIST_REFRESH;
				msg.arg1 = type;
//				msg.obj = localorders;
				activity.handler.sendMessage(msg);
			}
		}).start();

	}

	public void localOrderShow(ArrayList<AllOrder> localorders,final int type) {
		if(localorders == null){
			return;
		}
		Log.e(TAG,"----------数据库返回订单数量："+localorders.size());
		adapter.addOrders(localorders);
		Log.e(TAG, "isShowFirstItem:"+isShowFirstItem+"当前状态为："+orderListState.getState()
				+"type:"+type);
		if(isShowFirstItem&&orderListState.getState() == OrderListState.PARK_IN_STATE){
			if(type == 0){
				itemOrder = localorders.get(0);
				if(itemOrder != null){
					Log.e(TAG, "itemOrder0："+itemOrder.toString());
					cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
				}
			}
		}else if(isShowFirstItem&&orderListState.isParkOutState()){
			if(type == 1){
				itemOrder = localorders.get(0);
				if(itemOrder != null){
					Log.e(TAG, "itemOrder1："+itemOrder.toString());
					cashOrder(1,itemOrder.getId(),itemOrder.getLocalid());
				}
			}
		}
	}

//	private ArrayList<AllOrder> getLocalOrderThread(int type) {
//		LocalCurrentOrder currentOrder = getLocalOrderDBManager().getLocalCurrOrder(page,type);
//		if(currentOrder == null){
//			return null;
//		}
//		Log.e(TAG,"currentOrder:"+currentOrder.toString());
//		ArrayList<AllOrder> localorders = currentOrder.getInfo();
//		lv_current_order.postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				lv_current_order.onRefreshComplete();
//			}
//		}, 3000);
//		if (localorders == null || localorders.size() == 0) {
//			if(page==1){
//				Message obtainMessage = activity.handler.obtainMessage(Constant.CLEAR_ORDER);
//				activity.handler.sendMessage(obtainMessage);
//			}
//			page--;
//			Looper.prepare();
//			activity.showToast("没有新的订单记录！");
//			Looper.loop();
//			return null;
//		}
//
//
//		localorders = selectMonthUser(localorders);
//		return localorders;
//	}

	/**
	 * 查询设置是否是月卡
	 * @return 
	 */
//	public ArrayList<AllOrder> selectMonthUser(ArrayList<AllOrder> localorders) {
//		if(localorders != null&&localorders.size()>0){
//			for(int i=0;i<localorders.size();i++){
//				String carnumber = localorders.get(i).getCarnumber();
//				int  isMonthCard = getLocalOrderDBManager().queryOrderCtype(carnumber);
//				if(isMonthCard == 5){
//					localorders.get(i).setIsmonthuser(Constant.sOne);
//				}else{
//					localorders.get(i).setIsmonthuser(Constant.sZero);
//				}
//			}
//		}
//		return localorders;
//	}



	/**
	 * 本地化,查询数据库是否有订单的详情
	 * @param orderid
	 */
//	private void doCashOrderTimeOut(int type,String orderid,String localid) {
//		CarNumberOrder order = getLocalOrderDBManager().queryLocalCarNumberOrderDetailsBycarNumber(type,orderid,localid);
//		if(order != null){
//			Log.e(TAG, "数据库订单详情："+order);
//			localShowDetail(order);
//		}
//	}

	/**
	 * 本地显示详情
	 * @param loDBManager
	 * @param order
	 */
//	public void localShowDetail(CarNumberOrder order) {
//		if(order != null){
//			// 查询是否为在有效期内的月卡--是否是月卡，
//			int  isMonthCard = getLocalOrderDBManager().queryOrderCtype(order.getCarnumber());
//			Log.e(TAG,"查询是否是月卡："+isMonthCard);
//			if(isMonthCard == 5){
//				order.setIsmonthuser(Constant.sOne);
//			}else{
//				order.setIsmonthuser(Constant.sZero);
//			}
//			String collect = order.getTotal();
//			if(OrderListState.getInstance().isParkOutState()&&collect !=null&&!collect.equals("null")){
//
//			}else{
//				collect = localCashRefreshView(order);
//			}
//			order.setCollect(collect);
//			order.setTotal(collect);
//			itemOrder.setTotal(collect);
//			Log.i(TAG, "数据库查询的订单详情："+order.toString());
//			myListener.refreshCashView(order);
//			myListener.refreshDetailView(order);
//			myListener.refreshRecordView(order);
//		}
//	}

	/**
	 * 本地查询车牌号显示 
	 */
	public void localAddOrders(ArrayList<AllOrder> allOrders){
		if(allOrders !=null){
			adapter.removeOrders();
			adapter.addOrders(allOrders);
			if(allOrders.get(0) != null){
				itemOrder = allOrders.get(0);
				cashOrder(0,itemOrder.getId(),itemOrder.getLocalid());
			}
		}
	}
	/**
	 * 清空订单
	 */
	public void localClearOrder(){
		adapter.removeOrders();
		clearOrthersFragment();
	}

	/**
	 * 本地订单详情里的价格
	 * @param order
	 */
//	private String localCashRefreshView(CarNumberOrder order) {
//		String collect = Constant.sZero;
//		if(order!=null){
//			Log.e(TAG,"计算价格："+order.toString());
//			//月卡或者警车价格设置为0
//			if(order.getIsmonthuser()==null||order.getCarnumber()==null||
//					order.getIsmonthuser().equals(Constant.sOne)||StringUtils.isPolice(order.getCarnumber())){
//				collect = Constant.sZero;
//			}else{
//				if(order.getCar_type()!=null&&!order.getCar_type().equals("null")){
////					Long orderStartTime = getLocalOrderDBManager().queryOrderTime("2",order.getOrderid());
//					collect = calculatePrice(Integer.parseInt(order.getCar_type()), order.getCarnumber(),orderStartTime);
//				}
//			}
//		}
//		return collect;
//	}
	/**
	 * 计算价格
	 * @param billingType
	 * @param carPlate
	 * @param currentTime
	 * @param loDBManager
	 * @return
	 */
//	@SuppressWarnings("rawtypes")
//	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
//	@SuppressLint("NewApi")
//	private String calculatePrice(int billingType, String carPlate,Long orderStartTime ) {
//		Long currentTime = System.currentTimeMillis()/1000;
//		CountPrice countPrice = new CountPrice();
//		String comid = AppInfo.getInstance().getComid();
//		Long count = (long) loDBManager.queryCountByComid(""+billingType);
//		double minPriceUnit = loDBManager.queryMinPriceUnitByComid(comid);
//
//		Log.e(TAG,"=AppInfo.getInstance().isParkBilling():"+AppInfo.getInstance().isParkBilling());
//		if(AppInfo.getInstance().isParkBilling()){//区分大小车
//			if(billingType == 0){
//				billingType =1;
//			}else{
//				billingType = 2;
//			}
//		}else{//不区分
//			billingType = 0;
//		}
//		List<List<Map>> priceList = getLocalOrderDBManager().getPriceList(billingType,comid);
//		String total  = null;
//		Log.e(TAG,"价格："+priceList+"=="+comid+"=="+billingType);
//		if(priceList != null&&priceList.size()>0){
//			List<Map> list = null; 
//			List<Map> list2 = null;
//			if(priceList.size()>0){
//				list = priceList.get(0);
//			}
//			if(priceList.size()>=1){
//				list2 = priceList.get(1);
//			}
//			if(orderStartTime != null){
//				total = countPrice.getPrice(
//						count, list, list2,minPriceUnit,orderStartTime, currentTime, 
//						Long.parseLong(AppInfo.getInstance().getComid()), billingType);
//			}
//		}else{
//			total = "null";
//		}
//		Log.e(TAG,"本地计算价格："+total);
//		return total;
//	}
}
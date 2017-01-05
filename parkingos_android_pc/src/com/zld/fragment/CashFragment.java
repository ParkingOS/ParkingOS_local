/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/
package com.zld.fragment;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.google.gson.Gson;
import com.zld.R;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberMadeOrder;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.PrePayOrder;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.OrderListState;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.VoicePlayer;
import com.zld.view.DiscountViewPager;
import com.zld.view.SelectFreeCar;

import android.R.string;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * <pre>
 * 功能说明: 收费操作Fragment
 * 日期:	2015年4月13日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月13日
 * </pre>
 */
public class CashFragment extends BaseFragment implements OnClickListener {
	private static final String TAG = "CashFragment";
	private Button btn_free;// 免费
	private Button btn_discount;// 减免
	private View rootView;
	private RelativeLayout rl_cost;
	private TextView tv_total;// 总金额
	private TextView tv_collect;// 差价
	private TextView tv_prefee;// 预支付
	private TextView tv_discount;// 减免费用
	private TextView tv_park_cost;// 车费
	private Button btn_charge_finish;// 收费完成
	private TextView tv_mobile_payment;// 手机支付-盖章？
	private RelativeLayout rl_pay_before;
	private SelectFreeCar selectFreeCar;
	@SuppressWarnings("unused")
	private CarNumberOrder currentOrder;
	private String orderId;// 生成后立马结算掉的订单id
	/** 点击免费、收费完成为false,当上一辆为普通车未做操作时为true; */
	private boolean isFree = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.cash_operation, container, false);
		initView(rootView);
		onClickEvent();
		return rootView;
	}

	/**
	 * 初始化控件
	 */
	private void initView(View rootView) {
		rl_cost = (RelativeLayout) rootView.findViewById(R.id.rl_cost);
		tv_park_cost = (TextView) rootView.findViewById(R.id.tv_park_cost);
		tv_mobile_payment = (TextView) rootView.findViewById(R.id.tv_mobile_payment);
		rl_pay_before = (RelativeLayout) rootView.findViewById(R.id.rl_pay_before);
		tv_collect = (TextView) rootView.findViewById(R.id.tv_collect);
		tv_total = (TextView) rootView.findViewById(R.id.tv_total);
		tv_prefee = (TextView) rootView.findViewById(R.id.tv_prefee);
		tv_discount = (TextView) rootView.findViewById(R.id.tv_prediscount);
		btn_free = (Button) rootView.findViewById(R.id.btn_free);
		btn_charge_finish = (Button) rootView.findViewById(R.id.btn_charge_finish);
		btn_discount = (Button) rootView.findViewById(R.id.btn_discount);
		selectFreeCar = new SelectFreeCar(activity, btn_free, this);
		this.rootView = rootView;
	}

	/**
	 * 控件点击事件
	 */
	private void onClickEvent() {
		btn_free.setOnClickListener(this);
		btn_charge_finish.setOnClickListener(this);
		btn_discount.setOnClickListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_free:
			if (AppInfo.getInstance().getFreeResons() != null && AppInfo.getInstance().getFreeResons().size() > 0) {
				if (activity.getItemOrder() != null) {
					selectFreeCar.showFreeTypeView(activity.getItemOrder().getId());
				}
				return;
			}
			freeActionHandle(false, null);
			break;
		case R.id.btn_discount:
			showDiscountPage();
			return;
		// break;
		case R.id.btn_charge_finish:
			chargeFinish();
			break;
		default:
			break;
		}
		hideSeal();
		hidePrepay();
		isFree = false;
		activity.hideFreeAndChargeBtn();
	}

	public void showDiscountPage() {
		DiscountViewPager dvp = new DiscountViewPager(activity, false, this);
		dvp.setDirection("left");
		dvp.showPopupWindow(btn_discount, rootView.getHeight());
	}

	public void showFreePage() {
		DiscountViewPager dvp = new DiscountViewPager(activity, false, this);
		dvp.setDirection("left");
		dvp.showPopupWindow(btn_discount, rootView.getHeight());
	}

	/**
	 * 收费完成按钮的操作
	 */
	public void chargeFinish() {
		ShowCost();
		if (btn_charge_finish.getText().equals("知道了")) {
			activity.refreshListOrder();
		} else if (btn_charge_finish.getText().equals("收费完成")) {
			if (!(OrderListState.getInstance().isOrderFinishUppoleState()
					|| OrderListState.getInstance().isOrderFinishState())) {
				activity.controlExitPole();
				if (activity.getExitledinfo() != null) {
					if (activity.getExitledinfo() != null
							&& Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
						activity.sendLedShow(activity.getExitledinfo().getMatercont(), "		一路顺风", "一路顺风");
					} else {
						activity.sendLedShow(activity.getExitledinfo().getMatercont(), "一路顺风", "一路顺风");
					}
				}
			}
			OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
			activity.refreshListOrder();
			double cost = 0;
			try {
				cost = Double.parseDouble(tv_park_cost.getText().toString());
			} catch (Exception e) {
				e.printStackTrace();
				cost = 1;
			}
			if (cost > 0)
				activity.getChargeInfo();
		}
		btn_charge_finish.setText("收费完成");
		btn_discount.setText("减免");
	}

	@SuppressWarnings("static-access")
	public void refreshView(CarNumberOrder order) {
		if (order == null) {
			clearView();
			return;
		}
		// 出口来车没有搜索到，强制显示免费按钮，隐藏收费完成按钮
		if (OrderListState.getInstance().isNoOrderState()) {
			if (AppInfo.getInstance().getInstance().isPassfree()) {
				btn_free.setVisibility(View.VISIBLE);
			}
			activity.detailsFragment.hideBtn();
			btn_charge_finish.setVisibility(View.INVISIBLE);
		} else if (OrderListState.getInstance().isParkOutState()) {

		}
		currentOrder = order;
		/* 是否为月卡用户-是否为0元 */
		setChargeFinishBtn(order);
	}

	/**
	 * 设置收费完成按钮
	 * 
	 * @param order
	 */
	public void setChargeFinishBtn(CarNumberOrder order) {
		if (order == null) {
			return;
		}
		try {
			if ((order.getCtype() != null && order.getCtype().equals("5"))
					|| (order.getShopticketid() == null && order.getCollect() != null
							&& !order.getCollect().equals("null") && Double.parseDouble(order.getCollect()) == 0f)) {
				tv_park_cost.setText("0.0");
				Log.e("OrderListState", "当前状态为：" + OrderListState.getInstance().getState());
				if (OrderListState.getInstance().isClearFinishState()) {
					btn_charge_finish.setText("知道了");
				}
			} else {
				if (order.getCollect() != null) {
					if (order.getCollect().equals("null")) {
						activity.showToast("请设置后台大小车价格.");
					}
					boolean sir = SharedPreferencesUtils.getParam(getActivity(), "zld_config", "yessir", true);
					if (sir && StringUtils.isPolice(order.getCarnumber())) {
						/* 军警车 显示为0元 */
						tv_park_cost.setText("0.0");
					} else {
						// 用了减免券，collect是结算金额，beforecollect是总金额
						if (order.getBefcollect() != null) {
							tv_park_cost.setText(order.getBefcollect());
						} else {
							tv_park_cost.setText(order.getCollect());
						}
					}
					btn_charge_finish.setText("收费完成");
				}
			}
		} catch (NumberFormatException e) {
			tv_park_cost.setText("价格未知");
			Log.e("OrderListState", "当前状态为：" + OrderListState.getInstance().getState());
			if (OrderListState.getInstance().isClearFinishState()) {
				btn_charge_finish.setText("知道了");
			}
		}
	}

	private void clearView() {
		tv_park_cost.setText("");
	}

	/**
	 * 隐藏车费
	 */
	public void hideCost() {
		if (rl_cost.getVisibility() == View.VISIBLE) {
			rl_cost.setVisibility(View.GONE);
		}
	}

	/**
	 * 显示车费
	 */
	public void ShowCost() {
		if (rl_cost.getVisibility() == View.GONE) {
			rl_cost.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏免费和收费完成按钮
	 */
	public void hideFreeAndChargeBtn() {
		btn_free.setVisibility(View.INVISIBLE);
		btn_charge_finish.setVisibility(View.INVISIBLE);
		btn_discount.setVisibility(View.INVISIBLE);
	}

	/**
	 * 隐藏免费按钮
	 */
	public void hideFreeBtn() {
		btn_free.setVisibility(View.INVISIBLE);
		btn_discount.setVisibility(View.INVISIBLE);
	}

	/**
	 * 显示免费和收费完成按钮
	 */
	public void showFreeAndChargeBtn() {
		if (AppInfo.getInstance().isPassfree()) {
			btn_free.setVisibility(View.VISIBLE);
		} else {
			btn_free.setVisibility(View.INVISIBLE);
		}
		if (activity.getItemOrder() != null && activity.getItemOrder().getPrepay() != null
				&& activity.getItemOrder().getPrepay().equals("0.0")
				&& activity.getItemOrder().getShopticketid() == null) {
			btn_discount.setVisibility(View.VISIBLE);
		}
		btn_charge_finish.setVisibility(View.VISIBLE);
	}

	/**
	 * 显示免费按钮
	 */
	public void showFree() {
		if (AppInfo.getInstance().isPassfree()) {
			btn_free.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏收费完成按钮
	 */
	public void hideChargeBtn() {
		if (btn_charge_finish.getVisibility() == View.VISIBLE) {
			btn_charge_finish.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示已支付印章
	 */
	public void showSeal() {
		if (tv_mobile_payment.getVisibility() == View.INVISIBLE) {
			tv_mobile_payment.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏已支付印章
	 */
	public void hideSeal() {
		if (tv_mobile_payment.getVisibility() == View.VISIBLE) {
			tv_mobile_payment.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 显示预支付金额
	 */
	public void showPrepay() {
		activity.cashFragment.hideCost();
		if (rl_pay_before.getVisibility() == View.GONE) {
			rl_pay_before.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 隐藏预支付金额
	 */
	public void hidePrepay() {
		if (rl_pay_before.getVisibility() == View.VISIBLE) {
			rl_pay_before.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置车主预支付金额
	 */
	public void setPrepayed(PrePayOrder prePayOrder) {
		showPrepay();

		if (activity.getItemOrder().getShopticketid() != null) {
			double waitCollect = StringUtils.formatDouble(Double.parseDouble(activity.getItemOrder().getBefcollect())
					- Double.parseDouble(activity.getItemOrder().getDistotal())
					- Double.parseDouble(activity.getItemOrder().getPrepay()));
			if (waitCollect > 0) {
				tv_collect.setText("" + waitCollect);
			} else {
				tv_collect.setText("0.0");
			}
		} else if (prePayOrder != null && prePayOrder.getCollect() != null) {
			tv_collect.setText(prePayOrder.getCollect());
		}

		if (activity.getItemOrder().getShopticketid() != null) {
			double waitCollect = StringUtils.formatDouble(Double.parseDouble(activity.getItemOrder().getBefcollect())
					- Double.parseDouble(activity.getItemOrder().getDistotal())
					- Double.parseDouble(activity.getItemOrder().getPrepay()));
			if (waitCollect < 0) {
				double showValue = StringUtils
						.formatDouble(Double.parseDouble(activity.getItemOrder().getPrepay()) + waitCollect);
				tv_prefee.setText("" + showValue); // 预支付多了直接退给车主，不在收费员端显示多的部分
			} else {
				tv_prefee.setText(activity.getItemOrder().getPrepay());
			}

		} else if (prePayOrder != null && prePayOrder.getPrefee() != null) {
			tv_prefee.setText(prePayOrder.getPrefee());
		}

		if (activity.getItemOrder().getShopticketid() != null) {
			tv_total.setText(activity.getItemOrder().getBefcollect());
		} else if (prePayOrder != null && prePayOrder.getTotal() != null) {
			tv_total.setText(prePayOrder.getTotal());
		}

		if (activity.getItemOrder().getShopticketid() != null) {
			tv_discount.setText(activity.getItemOrder().getDistotal());
		} else {
			if (prePayOrder.getDiscount() != null) {
				tv_discount.setText(prePayOrder.getDiscount());
			} else {
				tv_discount.setText("0");
			}
		}
	}

	@SuppressWarnings("static-access")
	public void showFreeHideChargeFinish() {
		// 出口来车没有搜索到，强制显示免费按钮，隐藏收费完成按钮
		if (OrderListState.getInstance().isNoOrderState()) {
			if (AppInfo.getInstance().getInstance().isPassfree()) {
				btn_free.setVisibility(View.VISIBLE);
			}
			activity.detailsFragment.hideBtn();
			btn_charge_finish.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 点击免费按钮的处理
	 */
	public void freeActionHandle(boolean isPolice, String freeReason) {
		// 出口来车没有匹配的情况下，点击免费的处理
		Log.e("OrderListState", "freeActionHandle当前状态为：" + OrderListState.getInstance().getState());
		if (OrderListState.getInstance().isNoOrderState()) {
			activity.detailsFragment.showBtn();
			madeOrder();
		} else {
			hideSeal();
			hidePrepay();
			isFree = false;
			activity.hideFreeAndChargeBtn();
			freeOrder(isPolice, freeReason);
		}
	}

	public void disCountAfterComplete(String time, String type) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.HD_DERATE);
		params.setUrlParams("orderid", activity.getItemOrder().getId());
		params.setUrlParams("type", type); // 3 jianshi 4 quanmian
		params.setUrlParams("time", time);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		String url = params.getRequstUrl();
		Log.e(TAG, "请求减免的url是--->" + url);
		HttpManager.requestGET(activity, url, this);
	}

	/**
	 * 免费订单 isPolice 是否是军警车,为了让军警免费车,不计入后台免费统计里
	 */
	public void freeOrder(boolean isPolice, String freeReason) {
		AllOrder itemOrder = null;
		if (activity != null) {
			itemOrder = activity.getItemOrder();
		}
		// 是否是本地服务器
		if (!AppInfo.getInstance().getIsLocalServer(activity)) {// 是本地服务器则没有平板本地化的感念
			// 本地化免费操作
			boolean param = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "nettype", "isLocal",
					false);
			Log.e("isLocal", "CashFragment freeOrder get isLocal " + param);
			if (param || AppInfo.getInstance().getIssuplocal().equals("1")) {
				localFree(itemOrder);
				return;
			}
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.FREE_ORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		/* 是否是无订单状态,生成一个立刻又结算掉 */
		if (OrderListState.getInstance().isNoOrderState()) {
			params.setUrlParams("orderid", orderId);
		} else {
			/* 点击订单，然后免费 */
			if (itemOrder != null) {
				params.setUrlParams("orderid", itemOrder.getId());
			}
		}
		params.setUrlParams("passid", activity.passid);
		if (isPolice) {
			params.setUrlParams("isPolice", 1);
		}
		if (freeReason != null) {
			params.setUrlParams("freereasons", freeReason);
		}
		String url = params.getRequstUrl();
		Log.e(TAG, "免费订单的url是--->" + url);
		HttpManager.requestGET(activity, url, this);
	}

	/**
	 * 生成订单
	 */
	public void madeOrder() {
		String token = AppInfo.getAppInfo().getToken();
		if (token != null && token.equals("false")) {
			return; // 订单提交没有token，但是这里做个检查防止出服务没有死掉的情况
		}
		String carNumber = "";
		try {
			carNumber = StringUtils.encodeString(activity.exitFragment.exitCarBmpInfo.getCarPlate(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.MADE_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("carnumber", carNumber);
		params.setUrlParams("through", 3);
		params.setUrlParams("from", 0);
		params.setUrlParams("car_type", -1);
		params.setUrlParams("passid", activity.passid);
		String url = params.getRequstUrl();
		Log.e(TAG, "生成订单的url是--->" + url);
		HttpManager.requestGET(activity, url, this);
	}

	/**
	 * collectorrequest.do?action=invalidorders&invalid_order=-1
	 * &token=198f697eb27de5515e91a70d1f64cec7 空闲车位数加减1
	 */
	private void changeFreePark(String value) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.CHANG_INVALIDORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("invalid_order", value);
		String url = params.getRequstUrl();
		HttpManager.requestGET(activity, url, this);
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		Log.e(TAG, "网络返回的结果为" + object);
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.FREE_ORDER)) {
			doFreeOrderResult(object);
		} else if (url.contains(Constant.MADE_ORDER)) {
			doMadeOrderResult(object);
		} else if (url.contains(Constant.CHANG_INVALIDORDER)) {
			doChangInvalidOrderResult(object);
		} else if (url.contains(Constant.HD_DERATE)) {
			PrePayOrder preOrder = new PrePayOrder();
			Map<String, String> discount = StringUtils.getMapForJson(object);
			preOrder.setCollect(discount.get("collect"));
			preOrder.setPrefee("0");
			preOrder.setResult(discount.get("result"));
			preOrder.setTotal(discount.get("befcollect"));
			preOrder.setDiscount(discount.get("distotal"));
			setPrepayed(preOrder);
			btn_discount.setVisibility(View.INVISIBLE);
			if (discount.get("collect").equals("0.0")) {
				btn_free.setVisibility(View.INVISIBLE);
			}
		}
		return true;
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.FREE_ORDER)) {
			localFree(activity.getItemOrder());
		} else if (url.contains(Constant.MADE_ORDER)) {

		}
	}

	private void doChangInvalidOrderResult(String object) {
		Log.e(TAG, "doChangInvalidOrderResult--------------->" + object);
	}

	private void doMadeOrderResult(String object) {
		CarNumberMadeOrder info = new Gson().fromJson(object, CarNumberMadeOrder.class);
		if ("1".equals(info.getInfo())) {
			orderId = info.getOrderid();
		}
		freeOrder(false, null);
	}

	/**
	 * 免费订单结果处理
	 * 
	 * @param object
	 */
	private void doFreeOrderResult(String object) {
		Log.e(TAG, "免费订单的网络返回结果是---------------》" + object + " isFree：" + isFree);
		if ("1".equals(object)) {
			if (activity.exitFragment.exitCarBmpInfo == null) {
				return;
			}
			freeCar();
			showLedAndPole();
		}
	}

	/**
	 * 显示led 和 开闸
	 */
	private void showLedAndPole() {
		if (!isFree) {
			/* 开启出口道闸 */
			activity.controlExitPole();
			if (activity.getExitledinfo() != null) {
				if (Integer.parseInt(activity.getExitledinfo().getWidth()) > 64) {
					activity.sendLedShow(activity.getExitledinfo().getMatercont(), "		一路顺风", "一路顺风");
				} else {
					activity.sendLedShow(activity.getExitledinfo().getMatercont(), "一路顺风", "一路顺风");
				}
			}

			// 如果是出口没有匹配上的，需要增加一个垃圾订单数（增加一个空闲车位数）
			Log.e("OrderListState", "当前状态为：" + OrderListState.getInstance().getState());
			if (OrderListState.getInstance().isNoOrderState()) {
				changeFreePark("1");
			}
			OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
			activity.refreshListOrder();
		}
	}

	/**
	 * 免费车播报处理
	 */
	private void freeCar() {
		if (activity.exitFragment == null || activity.exitFragment.exitCarBmpInfo == null) {
			return;
		}
		String carnumber = activity.exitFragment.exitCarBmpInfo.getCarPlate();
		if (carnumber != null) {
			if (StringUtils.isPolice(carnumber)) {
				VoicePlayer.getInstance(activity).playVoice("军警车已免费");
				isFree = false;
			} else {
				VoicePlayer.getInstance(activity).playVoice("此车已免费");
			}
		}
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public void setFocus() {
		btn_charge_finish.callOnClick();
	}

	/**
	 * 本地化免费
	 */
	private void localFree(AllOrder itemOrder) {
		Log.e(TAG, "本地化免费activity.getItemOrder():" + itemOrder);
		if (itemOrder != null) {
			String orderid = itemOrder.getId();
			String localid = itemOrder.getLocalid();
			String total = itemOrder.getTotal();
			String prepay = itemOrder.getPrepay();
			// 减去累加的收费员金额
			activity.addTollmanMoney(total, prepay, false);
			// loDBManager.updateOrderTotalLocalFree(orderid,localid);
			// 语音播报、LED显示、开闸
			freeCar();
			showLedAndPole();
		}
		OrderListState.getInstance().setState(OrderListState.CLEAR_FINISH_STATE);
		activity.refreshListOrder();
		double cost = 0;
		try {
			cost = Double.parseDouble(tv_park_cost.getText().toString());
		} catch (Exception e) {
			e.printStackTrace();
			cost = 1;
		}
		if (cost > 0)
			activity.getChargeInfo();
		
	}
}

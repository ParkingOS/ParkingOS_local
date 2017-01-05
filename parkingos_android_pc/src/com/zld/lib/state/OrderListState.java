package com.zld.lib.state;

import android.util.Log;

public class OrderListState {
	private int state = PARK_IN_STATE;
	public static final int PARK_IN_STATE = 0;//在场车辆状态
	public static final int PARK_OUT_STATE = 1;//离场车辆状态
	public static final int HAND_SEARCH_STATE = 2;//手动搜索后状态
	public static final int AUTO_SEARCH_STATE = 3;//出口来车自动搜索后状态
	public static final int MODIFY_ORDER_STATE = 4;//修改订单状态
	public static final int CLEAR_ORDER_STATE = 5;//正在结算订单状态
	public static final int CLEAR_FINISH_STATE = 6;//结算完成状态
	public static final int ORDER_FINISH_STATE = 7;//结算完成状态，但没有点击收费完成或免费，此时的状态入口来车不刷新列表
	public static final int NO_ORDER_STATE = 8;//出口来车自动搜索但没有搜索到，此时置为此状态，点击免费按钮的逻辑会用到
	public static final int ORDER_FINISH_UPPOLE_STATE = 9;//结算完成并且，已经抬杆
	
	/* AppInfo实例  */
	private static OrderListState orderListState = new OrderListState(); 
	
	/* 保证只有一个OrderListState实例 */  
	private OrderListState() {  
	}  

	/* 获取OrderListState实例 ,单例模式 */  
	public static OrderListState getInstance() {  
		return orderListState;  
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * 是否在场车辆状态
	 * @return
	 */
	public boolean isParkInState() {
		Log.e("OrderListState", "在场车辆——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.PARK_IN_STATE;
	}

	
	/**
	 * 是否无订单状态
	 * @return
	 */
	public boolean isNoOrderState() {
		Log.e("OrderListState", "无订单——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.NO_ORDER_STATE;
	}

	/**
	 * 是否结算订单状态
	 * @return
	 */
	public boolean isClearFinishState() {
		Log.e("OrderListState", "结算订单——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.CLEAR_FINISH_STATE;
	}
	
	/**
	 * 是否离场车辆状态
	 * @return
	 */
	public boolean isParkOutState() {
		Log.e("OrderListState", "离场车辆——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.PARK_OUT_STATE;
	}
	
	/**
	 * 是否处于"正在结算订单"状态
	 * @return
	 */
	public boolean isClearOrderState() {
		Log.e("OrderListState", "正在结算订单状态——当前状态：" + orderListState.getState());
		return orderListState.getState() != OrderListState.CLEAR_ORDER_STATE;
	}	
	
	
	
	/**
	 * 是否处于"结算完成状态，但没有点击收费完成或免费，此时的状态入口来车不刷新列表"状态
	 * @return
	 */
	public boolean isOrderFinishState() {
		Log.e("OrderListState", "结算完成状态，但没有点击收费完成或免费，此时的状态入口来车不刷新列表——当前状态：" + orderListState.getState());
		return orderListState.getState() != OrderListState.ORDER_FINISH_STATE;
	}
	
	/**
	 * 是否处于"结算完成并且，已经抬杆"状态
	 * @return
	 */
	public boolean isOrderFinishUppoleState() {
		Log.e("OrderListState", "结算完成并且，已经抬杆——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.ORDER_FINISH_UPPOLE_STATE;
	}
	
	/**
	 * 是否处于"手动搜索"状态
	 * @return
	 */
	public boolean isHandSearchState() {
		Log.e("OrderListState", "手动搜索——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.HAND_SEARCH_STATE;
	}
	
	/**
	 * 是否处于"出口来车自动搜索后"状态
	 * @return
	 */
	public boolean isAutoSearchState() {
		Log.e("OrderListState", "出口来车自动搜索后——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.AUTO_SEARCH_STATE;
	}
	
	/**
	 * 是否处于"修改订单状态"状态
	 * @return
	 */
	public boolean isModifyOrderState() {
		Log.e("OrderListState", "修改订单状态——当前状态：" + orderListState.getState());
		return orderListState.getState() == OrderListState.MODIFY_ORDER_STATE;
	}
}

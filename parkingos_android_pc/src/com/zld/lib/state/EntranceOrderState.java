/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月20日 
 * 
 *******************************************************************************/ 
package com.zld.lib.state;

/**
 * <pre>
 * 功能说明: 生成订单状态
 * 日期:	2015年4月20日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月20日
 * </pre>
 */
public class EntranceOrderState {
	private int state = AUTO_COME_IN_STATE;
	public static final int AUTO_COME_IN_STATE = 0;//来车自动生成订单状态
	public static final int ADD_CAR_ORDER_STATE = 1;//补录来车生成订单状态

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

}

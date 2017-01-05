/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月20日 
 * 
 *******************************************************************************/ 
package com.zld.lib.state;


/**
 * <pre>
 * 功能说明: 生成订单状态
 * 		       入口来车状态,详情里不播报"此车为月卡用户"
 * 		       当出口来车状态时,收费员手动刷新,则置为刷新列表状态,不然也会播报.
 * 		       当上一辆车为免费车时,免费完自动刷新,则置为刷新列表状态,不然的话，还是会播报.
 * 日期:	2015年4月20日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月20日
 * </pre>
 */
public class ComeInCarState {
	private int state = ENTRANCE_COME_IN_CAR_STATE;
	/**入口来车状态*/
	public static final int ENTRANCE_COME_IN_CAR_STATE = 0;
	/**出口来车生成订单状态*/
	public static final int EXIT_COME_IN_CAR_STATE = 1;
	/**手动刷新列表状态*/
	public static final int MANUAL_REFRESH_ORDER_LIST = 3;
	/**自动刷新列表状态*/
	public static final int AUTO_REFRESH_ORDER_LIST = 4;
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	/**
	 * 是否处于"出口来车状态"状态
	 * @return
	 */
	public boolean isAutoSearchState() {
		return this.getState() == ComeInCarState.EXIT_COME_IN_CAR_STATE;
	}
}

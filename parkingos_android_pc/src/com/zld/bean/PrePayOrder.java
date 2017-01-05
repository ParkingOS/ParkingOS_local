/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年5月23日 
 * 
 *******************************************************************************/ 
package com.zld.bean;

/**
 * <pre>
 * 功能说明: 预支付结果
 * 日期:	2015年5月23日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年5月23日
 * </pre>
 */
public class PrePayOrder {
	
	private String result;//1成功-1失败2补差价
	private String prefee;//预支付金额
	private String total;//总金额
	private String collect;//差价金额
	private String discount;// 减免金额
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getPrefee() {
		return prefee;
	}
	public void setPrefee(String prefee) {
		this.prefee = prefee;
	}
	public String getTotal() {
		return total;
	}
	public void setTotal(String total) {
		this.total = total;
	}
	public String getCollect() {
		return collect;
	}
	public void setCollect(String collect) {
		this.collect = collect;
	}
	public String getDiscount() {
		return discount;
	}
	public void setDiscount(String discount) {
		this.discount = discount;
	}
	@Override
	public String toString() {
		return "PrePayOrder [result=" + result + ", prefee=" + prefee + ", total=" + total + ", collect=" + collect
				+ ", discount=" + discount + "]";
	}

}

package com.vzvison.monitor.player;

import java.util.Vector;

/**
 * 数据�??
 * 实时视频流中可以分成若干个Packet�??
 * @author Administrator
 */
public class Packet {
	private int amount; //数据包的数量
	private Vector<Integer> positionList;	//每个数据包的起始位置的列�??

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public Vector<Integer> getPositionList() {
		return positionList;
	}

	public void setPositionList(Vector<Integer> positionList) {
		this.positionList = positionList;
	}
}

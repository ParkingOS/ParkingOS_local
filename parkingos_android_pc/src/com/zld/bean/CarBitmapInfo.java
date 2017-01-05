package com.zld.bean;

import android.graphics.Bitmap;

public class CarBitmapInfo {
	private String carPlate;
	private Bitmap bitmap;
	private int carPlateheight;
	private int carPlatewidth;
	private int xCoordinate;
	private int yCoordinate;
	private int ntype;
	
	public int getNtype() {
		return ntype;
	}
	public void setNtype(int ntype) {
		this.ntype = ntype;
	}
	
	public String getCarPlate() {
		return carPlate;
	}
	public void setCarPlate(String carPlate) {
		this.carPlate = carPlate;
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public int getCarPlateheight() {
		return carPlateheight;
	}
	public void setCarPlateheight(int carPlateheight) {
		this.carPlateheight = carPlateheight;
	}
	public int getCarPlatewidth() {
		return carPlatewidth;
	}
	public void setCarPlatewidth(int carPlatewidth) {
		this.carPlatewidth = carPlatewidth;
	}
	public int getxCoordinate() {
		return xCoordinate;
	}
	public void setxCoordinate(int xCoordinate) {
		this.xCoordinate = xCoordinate;
	}
	public int getyCoordinate() {
		return yCoordinate;
	}
	public void setyCoordinate(int yCoordinate) {
		this.yCoordinate = yCoordinate;
	}
	@Override
	public String toString() {
		return "CarBitmapInfo [carPlate=" + carPlate + ", bitmap=" + bitmap
				+ ", carPlateheight=" + carPlateheight + ", carPlatewidth="
				+ carPlatewidth + ", xCoordinate=" + xCoordinate
				+ ", yCoordinate=" + yCoordinate + ", ntype = " + ntype + "]";
	}
	
}

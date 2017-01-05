package com.zld.decode;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zld.lib.constant.Constant;
import com.zld.photo.DecodeManager;

public class DecodeThread {

	private static final String TAG = "DecodeThread";
	private String AllcarPlate;
	private InputStream is;
	public static Handler handler;
	public static String cameraIp;

	public DecodeThread() {
		super();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("static-access")
	public DecodeThread(Handler handler) {
		super();
		this.handler = handler;
	}

	//读取照片数据中的返回结果；读到的结果转码后返回String类型结果；
	private String readImageData(byte[] bytes) {
		int i;
		for(i=0; i<bytes.length; i++){
			if(bytes[i] == 0)
			{
				break;
			}
		}

		byte[] temp = new byte[i];

		Log.e(TAG, "车牌号数组长度："+bytes.length+"");
		for (int j = 0;  j < temp.length;  j++) {
			temp[j] = bytes[j];
			Log.e(TAG, temp[j]+"");
		}
		String result = "FAIL";
		try {
			result = new String(temp, "gbk");// 这里写转换后的编码方式
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * The interface c calls java
	 * 
	 * @param bitmap
	 * @param pPath
	 */
	@SuppressWarnings("unused")
	private void saveResult(Bitmap bitmap, byte[] carPlate, int height, int width, int left, int top) {
		Log.e(TAG,"saveResult");
		String AllcarPlate = readImageData(carPlate);
		String newcarPlate = AllcarPlate.substring(1, AllcarPlate.length());
		Log.e(TAG,"车牌号："+AllcarPlate);
		Message msg1 = handler.obtainMessage();
		Bundle bd = new Bundle();
		bd.putInt("carPlateheight", height);
		bd.putInt("carPlatewidth", width);
		bd.putInt("xCoordinate", left);
		bd.putInt("yCoordinate", top);
		bd.putString("carPlate", AllcarPlate);
		msg1.setData(bd);
		msg1.what = Constant.COMECAR_MSG;
		msg1.obj = bitmap;

		handler.sendMessage(msg1);		
		Log.i(TAG, height + "   " + width + "  " + left + "  " + top + "");
	}
	
	/**
	 * The interface c calls java
	 * 
	 * @param bitmap
	 * @param pPath
	 */
	@SuppressWarnings("unused")
	private void yitijiSaveResult(String cameraIp,byte[] carPlate, byte[] img, int height, int width, int left, int top, int resType, int carPlateColor, int nType) {
		Log.e(TAG,"yitijiSaveResult----》》》saveResult");
	    AllcarPlate = readImageData(carPlate);
	    is = new ByteArrayInputStream(img); 
	   Bitmap srcbmp = BitmapFactory.decodeStream(is);
		if (srcbmp != null){
			Message msg1 = handler.obtainMessage();
			Bundle bd = new Bundle();
			bd.putString("cameraIp", cameraIp);
			bd.putInt("carPlateheight", height);
			bd.putInt("carPlatewidth", width);
			bd.putInt("xCoordinate", left);
			bd.putInt("yCoordinate", top);
			bd.putString("carPlate", AllcarPlate);
			bd.putInt("resType", resType);
			bd.putInt("nType", nType);
			/* 黄牌车 */
			if (carPlateColor  == 2){
				bd.putInt("billingType", 2);
			/* 蓝牌车  */	
			}else if (carPlateColor == 1){
				bd.putInt("billingType", 1);
			}
			msg1.setData(bd);
			msg1.what = Constant.COMECAR_MSG;
			msg1.obj = srcbmp;
			handler.sendMessage(msg1);
			Log.i(TAG, srcbmp.getHeight() + "   " + srcbmp.getWidth() + "  " + left + "  " + top + "");
		}
		is = null;
		srcbmp = null;
		AllcarPlate = null;
	}

	@SuppressWarnings("unused")
	private synchronized void saveFrame(Bitmap bitmap, int height, int width) {
		//		Log.i("ffmpeg", "java saveframe function");
		//		Message msg1 = new Message();
		Message msg1 = handler.obtainMessage();
		msg1.what = Constant.SHOWVIDEO_MSG;
		msg1.arg1 = height;
		msg1.arg2 = width;
		msg1.obj = bitmap;
		handler.sendMessage(msg1);
	}
	
	@SuppressWarnings("unused")
	private void openCameraSuccess(int ret){
		if (ret == 1){
			Message msg1 = handler.obtainMessage();
			msg1.what = 61;
			handler.sendMessage(msg1);
		}
	}
	
	private void getKeepAlive(int ret){
		Log.e(TAG, "心跳回复："+ret);
		if(handler != null){
			Message msg1 = handler.obtainMessage();
			msg1.what = Constant.KEEPALIVE;
			handler.sendMessage(msg1);
		}
	}


	public void decodeThread(final String rtspUrl, final String carNumbers) {
		new Thread(new Runnable() {

			@SuppressWarnings("static-access")
			@Override
			public void run() {
				String result = DecodeManager.getinstance().initDecode(rtspUrl, carNumbers);
				if (result.equals("success")){
					handler.sendEmptyMessage(Constant.OPENCAMERA_SUCCESS_MSG);
					Log.e("ffmpeg", "java init ffmpeg sucess");
				}else {
					handler.sendEmptyMessage(Constant.OPENCAMERA_FAIL_MSG);
					return;
				}
				DecodeManager.getinstance().runDecode(DecodeThread.this, rtspUrl);
			}
		}).start();
	}
	
	@SuppressWarnings("static-access")
	public void yitijiThread(final String cameraIp) {
		this.cameraIp = cameraIp;		
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg1 = handler.obtainMessage();
				msg1.what = Constant.OPENCAMERA_FAIL_MSG;
				String ret = null;
				ret = DecodeManager.getinstance().runYitiji(DecodeThread.this, cameraIp);
				if (ret.equals("recvfailed")){
					msg1.arg1 = -1;
				}else if (ret.equals("connecterro")){
					msg1.arg2 = 0;
				}
				handler.sendMessage(msg1);
			}
		}).start();
	}
	
	public void reOpenCamera(){
		new Thread(new Runnable() {

			@Override
			public void run() {
				Message msg1 = handler.obtainMessage();
				msg1.what = 64;
				String ret = null;
				System.out.println("摄像头ＩＰ：cameraIp:"+cameraIp);
				ret = DecodeManager.getinstance().runYitiji(DecodeThread.this, cameraIp);
				if (ret.equals("recvfailed")){
					msg1.arg1 = -1;
				}else if (ret.equals("connecterro")){
					msg1.arg2 = 0;
				}
				handler.sendMessage(msg1);
			}
		}).start();
	}
}

package com.zld.photo;

import android.os.Handler;

import com.zld.decode.DecodeThread;

/**
 * 车牌识别调用类；
 */
public class DecodeManager {
	private Handler handler;
	public final static int openPole = 1;
	public final static int closePole = 0;
	private static DecodeManager manager = new DecodeManager();
	/**
	 * Load dynamic library
	 */
	static {
		System.loadLibrary("YITIJI");
	}

	private DecodeManager() {
	}

	public static DecodeManager getinstance(){
		return manager;
	}
	public native String initDecode(String rtspUrl, String carNumbers);

	public native String destroyAllMemery();

	public native static String runDecode(DecodeThread pObject, String rtspUrl);

	public native void stopDecode();
	
	public native String runYitiji(DecodeThread pObject, String cameraIp);
	
	public native void controlPole(int cmd,String ip);
	
	public native void stopYitiji();
	
	public native String getOneImg(String ip);
	
	public native int getConfidenceLevel();
	
	public native void setConfidenceLevel(int level);
}

package com.media;

import android.util.Log;

public class RTSP {
	
	 private static RTSP uniqueInstance = null;
	 
	    private RTSP() {
	       // Exists only to defeat instantiation.
	    }
	 
	    public static RTSP getInstance() {
	       if (uniqueInstance == null) {
	           uniqueInstance = new RTSP();
	       }
	       return uniqueInstance;
	    }
	

	public native int startPlay(String url);
	public native void stopPlay(int handle);
	
	public native void setOnDataReceiver( int handle ,OnDataReceiver onDataReceiver);
	public native void resetOnDataReceiver(OnDataReceiver onDataReceiver);
	
	
	public interface OnDataReceiver {
		void onDataReceive(byte[] data, int length, int width, int height,int fps);
	}
	
	static {
		try
		{
			System.loadLibrary("RTSP");
		}
		catch( UnsatisfiedLinkError  e )
		{
			Log.d("rtsp", "load failed");
		}
		
	}
	
}



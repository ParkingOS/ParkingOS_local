package com.zld.lib.util;

import android.os.Handler;

import com.zld.decode.DecodeThread;

public class CameraManager {
	public static void openCamera(Handler handler, String cameraIp){
		new DecodeThread(handler).yitijiThread(cameraIp);
	}
	public static void reOpenCamera(){
		new DecodeThread().reOpenCamera();
	}
}

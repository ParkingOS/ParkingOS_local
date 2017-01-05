package com.zld.lib.dialog;

import android.content.Context;
import android.widget.Toast;

public class ToastManager {
	private static ToastManager toastManager = new ToastManager();
	private ToastManager(){};
	
	public static ToastManager getInstance(){
		return toastManager;
	}
	
	public void showToast(Context context, String content, int duration){
		Toast.makeText(context, content, duration).show();
	}
}

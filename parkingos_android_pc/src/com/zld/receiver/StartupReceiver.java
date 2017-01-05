package com.zld.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zld.ui.HelloActivity;

public class StartupReceiver extends BroadcastReceiver {
	static final String action_boot="android.intent.action.BOOT_COMPLETED"; 
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.e("StartupReceiver", "收到开机广播，启动应程序---");
		if (intent.getAction().equals(action_boot)){ 
			Intent i = new Intent(context,HelloActivity.class);  
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			context.startActivity(i);  
		}
	}
}

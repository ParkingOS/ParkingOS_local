package com.zld.lib.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

@SuppressLint("HandlerLeak")
public class TimeUtil  implements Runnable{
	
	private Handler handler;
	/**
	 * 更新时间显示
	 */
	public void updateData(final TextView view){ 
		handler = new Handler() {
			@SuppressLint("HandlerLeak")
			public void handleMessage(Message msg) {
				view.setText((String)msg.obj);
			}
		};
		new Thread(this).start();
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(true){
				//yyyy-MM-dd   HH:mm:ss"
				SimpleDateFormat sdf=new SimpleDateFormat("HH:mm");
				String str=sdf.format(new Date());
				handler.sendMessage(handler.obtainMessage(100,str));
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

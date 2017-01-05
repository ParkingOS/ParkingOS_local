package com.zld.view;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.zld.R;
import com.zld.lib.constant.Constant;

/**
 * 
 * <pre>
 * 功能说明: 摄像头出错,重启计时对话框
 * 日期:	2015年10月14日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年10月14日
 * </pre>
 */
public class RestartDialog extends Dialog {
	private int i = 5;
	private Button bt_ok;
	private Button bt_after;
	private TextView tv_timing;
	private Handler handler;
	private Timer timer;
	private boolean isOk = true;
	private int type;//0 出口ZldNewActivity 1入口HomeExitPageService
	@SuppressLint("HandlerLeak")
	final Handler mHandler = new Handler(){  
		public void handleMessage(Message msg) {  
			switch (msg.what) {      
			case 1: 
//				Log.e("life", "一秒一次"+i);
				tv_timing.setText(""+i--);
//				Log.e("life", "计时器的："+i);
				if(isOk){
					if(i==0){
						//if(timer!=null){
						//timer.cancel();	//关闭掉计时器
						//}
						restart();		//重启程序
					}
				}else{
					cancel();
				}
				break;  
			case 2:
				if(isOk){
					Message message = new Message();
					message.what = Constant.KEEPALIVE_TIME;
					Log.e("life","RestartDialog弹框");
					handler.sendMessage(message);
				}
			}      
			super.handleMessage(msg);  
		}
	};  
	public RestartDialog(Context context) {
		super(context);
	}

	public RestartDialog(Context context,int theme,Handler handler,int type) {
		super(context,theme);
		this.handler = handler;
		this.type = type;
	}

	public void setI(int i) {
		this.i = i;
	}

	public boolean isOk() {
		return isOk;
	}

	public void setOk(boolean isOk) {
		this.isOk = isOk;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.dialog_restart);
		initView();
		setVeiw();
	}

	public void initTimer() {
		// TODO Auto-generated method stub
		if(timer == null){
			timer= new Timer();
		}
	}

	public void initView() {
		tv_timing = (TextView) findViewById(R.id.tv_timing);
		bt_after = (Button) findViewById(R.id.bt_after);
		bt_ok = (Button) findViewById(R.id.bt_ok);
	}

	public void setVeiw() {
		bt_after.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(timer!=null){
					timer.cancel();	//关闭掉计时器
				}
				if(type == 0){//出口
					setOk(false);
					exitDelayedSet();	//延时重启前,阻止摄像头错误时再弹出对话框
					afterRestart();		//延时再弹框
				}else{
					homeDelayedSet();	
				}
				RestartDialog.this.dismiss();
			}
		});
		bt_ok.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				if(timer!=null){
					timer.cancel();	//关闭掉计时器
				}
				RestartDialog.this.dismiss();
				restart();
			}
		});
		RestartDialog.this.dismiss();
	}

	private void restart() {
		if(handler != null){
			Log.e("life","restart");
			Message message = new Message();
			message.what = Constant.RESTART_YES;
			handler.sendMessage(message);
			if(timer != null){
				timer.cancel();
			}
		}
	}   

	/**
	 * 出口延时弹框
	 */
	protected void exitDelayedSet() {
		// TODO Auto-generated method stub
		if(handler != null){
			Log.e("life","exitdelayedSet_restart");
			Message message = new Message();
			message.what = Constant.EXIT_DELAYED_TIME;
			handler.sendMessage(message);
		}
	}
	/**
	 * 入口延时弹框
	 */
	protected void homeDelayedSet() {
		// TODO Auto-generated method stub
		if(handler != null){
			Log.e("life","homedelayedSet_restart");
			Message message = new Message();
			message.what = Constant.HOME_DELAYED_TIME;
			handler.sendMessage(message);
		}
	}
	/**
	 * 稍后重启
	 */
	private void afterRestart() {
		if(handler != null){
			Log.e("life","afterRestart");
			Message message = new Message();
			message.what = 2;
			mHandler.sendMessageDelayed(message, 60000);
		}
	}   


	/**
	 * 执行定时任务
	 */
	public void satrtTiming(){
		TimerTask task = new TimerTask(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
			}
		};
		timer.schedule(task,0,1000); //1秒一次
	}

	public void cancle(){
		timer.cancel();
		this.cancel();
	}
}

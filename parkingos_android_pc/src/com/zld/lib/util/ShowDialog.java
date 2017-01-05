package com.zld.lib.util;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.widget.BaseAdapter;

import com.zld.R;
import com.zld.lib.constant.Constant;

public class ShowDialog {

	static Dialog buildDialog;
	static AlertDialog dialog;
	static Timer timer = new Timer();
	static TimerTask task = new TimerTask() {
		public void run() {
			dialog.dismiss();
			timer.cancel();
			//			使用中的时候断网了的话，3秒后提示开启本地化吗？还是自动就本地化了，
			//			自动本地化的话，考虑到一些Service的关闭，分享的车位数的续接

		}
	};

	public static ProgressDialog getdialog(Context context,String message){
		ProgressDialog dialog = new ProgressDialog(context,R.style.dialog);
		dialog.setMessage(message);
		return dialog;
	}

	/**
	 * 生成对话框
	 * @param activity
	 * @param msg
	 * @param title
	 * @return
	 */
	public static AlertDialog.Builder buildDialog(final Context context,
			String msg, String title) {
		AlertDialog.Builder builder = new Builder(context);
		builder.setMessage(msg);
		builder.setTitle(title);
		return builder;
	}

	static Builder buildDialog2 = null;
	static AlertDialog create = null;
	@SuppressWarnings("deprecation")
	public static void showBuildDialog(final Context context,
			String msg, String title,final Handler handler){
		if(buildDialog2 == null){
			buildDialog2 = buildDialog(context, msg, title);
		}
		if(create == null){
			create = buildDialog2.create();
		}
		create.setButton("重启", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if(handler != null){
					Message msg1 = handler.obtainMessage();
					msg1.what = Constant.RESTART_YES;
					handler.sendMessage(msg1);	
					}
				create.dismiss();
			}
		});
		if(!create.isShowing()){
			create.show();
		}
	}

	/**
	 * 免单确认框
	 */
	public static void buildeSelectDialog(Activity activity,String msg,String title,
			final BaseAdapter adapter, final int selectedPosition){
		AlertDialog.Builder builder = buildDialog(activity, msg, title);
		buildDialogSelect(activity, adapter, selectedPosition, builder);
		builder.create().show();
	}

	/**
	 * 免单确认框
	 */
	public static void buildeChooseDialog(Activity activity,String msg,String title){
		AlertDialog.Builder builder = buildDialog(activity, msg, title);
		buildDialogSelect(builder);
		builder.create().show();
	}

	/**
	 * 免单确认框
	 */
	public static void buildeChooseDialog(Service service,String msg,String title){
		AlertDialog.Builder builder = buildDialog(service, msg, title);
		buildDialogSelect(builder);
		builder.create().show();
	}

	private static void buildDialogSelect(final Activity activity,
			final BaseAdapter adapter, final int selectedPosition,
			AlertDialog.Builder builder) {
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	public static void buildDialogSelect(AlertDialog.Builder builder) {
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
	}

	/**
	 * 有逃单提示对话框；
	 * @param hr 
	 * 
	 * @param warn
	 */
	public void LostOrderDialog(final Activity activity,
			String warn,final String carNumber,final String comid,final String uid) {
		Builder builder = new Builder(activity);
		builder.setIcon(R.drawable.app_icon_32);
		builder.setTitle("订单尚未生成");
		builder.setMessage(warn);
		builder.setCancelable(false);
		builder.setPositiveButton("取消生成", null);
		builder.setNegativeButton("继续生成订单",   
				new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				//				try {
				//					hr.addOrder(carNumber, comid, uid);
				//				} catch (UnsupportedEncodingException e) {
				//					((BaseActivity) activity).showToast("提交车牌字符转码异常！");
				//					e.printStackTrace();
				//				}
			}
		});
		AlertDialog dialog = builder.create();
		dialog.show();
	}

//	public static void showSetTimeDialog(final Context context){
//		AlertDialog.Builder builder = new Builder(context);
//		builder.setTitle("提示");
//		builder.setMessage("请设置时间与网络同步！");
//		builder.setPositiveButton("去设置", new  DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				Intent intent =  new Intent(Settings.ACTION_DATE_SETTINGS);  
//				context.startActivity(intent);
//			}
//		});
//		builder.setNegativeButton("不需要", new DialogInterface.OnClickListener() {
//
//			@Override
//			public void onClick(DialogInterface dialog, int which) {
//				// TODO Auto-generated method stub
//				dialog.dismiss();
//			}
//		});
//		builder.create().show();
//	}

	public static void startSetLocalDialog(final Context context){
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("当前已断网,准备开启本地化模式！");
		dialog = builder.create();
		dialog.show();
		timer.schedule(task, 5000);
	}
	
	public static void checkUpdateDialog(final Context context){
		AlertDialog.Builder builder = new Builder(context);
		builder.setTitle("提示");
		builder.setMessage("获取检查更新数据...");
		dialog = builder.create();
		dialog.show();
//		timer.schedule(task, 5000);
	}
}


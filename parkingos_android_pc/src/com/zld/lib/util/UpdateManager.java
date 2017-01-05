package com.zld.lib.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Date;

import com.zld.ui.HelloActivity;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class UpdateManager {

	private Context mContext;
	private long lastModified = 0;

	public UpdateManager(Context context) {
		this.mContext = context;
	}

	// 安装apk；
	private void install(File file) {
		// 旧方法，安装完以后直接退出
		// Intent intent = new Intent();
		// intent.setAction(Intent.ACTION_VIEW);
		// intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
		// mContext.startActivity(intent);
		// 安装完以后提示现在打开 by xulu
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());

	}

	/**
	 * 新版本APK下载
	 * 
	 * @author Clare
	 * 
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)

	public class DownLoadApkAsyncTask extends AsyncTask<String, Integer, File> {

		private ProgressDialog pd;
		private NotificationManager mNotificationManager;
		private NotificationCompat.Builder mBuilder;

		@Override
		protected void onPreExecute() {
			pd = new ProgressDialog(mContext);
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMessage("正在下载");
			pd.setCancelable(false);
			pd.setProgressNumberFormat("%1dKB / %2dKB");
			pd.setButton(DialogInterface.BUTTON_POSITIVE, "后台下载", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					mNotificationManager = (NotificationManager) mContext
							.getSystemService(Context.NOTIFICATION_SERVICE);
					mBuilder = new NotificationCompat.Builder(mContext);
					mBuilder.setContentTitle("正在下载")
							// 设置通知栏标题
							.setContentText("已下载：0%")
							// 设置通知栏显示内容
							// .setNumber(number) //设置通知集合的数量
							.setTicker("正在后台下载，下拉可查看进度...") // 通知首次出现在通知栏，带上升动画效果的
							.setWhen(System.currentTimeMillis())// 通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
							.setPriority(Notification.PRIORITY_DEFAULT) // 设置该通知优先级
							// .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
							.setOngoing(true);// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
					// .setDefaults(Notification.DEFAULT_LIGHTS)//
					// 向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
					// Notification.DEFAULT_ALL
					// Notification.DEFAULT_SOUND 添加声音 //
					// requires VIBRATE permission
					// .setSmallIcon(R.);// 设置通知小ICON
					mNotificationManager.notify(0, mBuilder.build());
					pd.dismiss();
					if (mContext.getClass().equals(HelloActivity.class)) {
						HelloActivity activity = (HelloActivity) mContext;
						activity.loadMainUI();
					}
				}
			});
			pd.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					UpdateManager.DownLoadApkAsyncTask.this.cancel(true);
					Toast.makeText(mContext, "下载已取消！", Toast.LENGTH_SHORT).show();
					pd.dismiss();
					if (mContext.getClass().equals(HelloActivity.class)) {
						HelloActivity activity = (HelloActivity) mContext;
						activity.loadMainUI();
					}
				}
			});
			pd.show();
			super.onPreExecute();
		}

		@SuppressWarnings("resource")
		@Override
		protected File doInBackground(String... params) {
			String downloadUrl = params[0];
			if (TextUtils.isEmpty(downloadUrl) || !downloadUrl.endsWith(".apk")) {
				return null;
			}
			InputStream is = null;
			FileOutputStream fos = null;
			HttpURLConnection conn = null;
			try {
				URL url = new URL(downloadUrl);
				conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(5000);
				if (conn.getResponseCode() == 200) {
					File file = createTargetFile();
					lastModified = conn.getLastModified();
					int length = conn.getContentLength();
					if (checkIfDownloaded(file, lastModified, length)) {
						if (mContext.getClass().equals(HelloActivity.class)) {
							HelloActivity activity = (HelloActivity) mContext;
							activity.loadMainUI();
						}
						return file;
					}
					if (pd.isShowing()) {
						pd.setMax(formatFileSize(length));
					}
					is = conn.getInputStream();
					fos = new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int len = 0;
					int total = 0;
					while ((len = is.read(buffer)) != -1) {
						fos.write(buffer, 0, len);
						total += len;
						if (mBuilder != null) {
							String percent = new DecimalFormat("##%").format((total * 1.0 / (length * 1.0)));
							mBuilder.setProgress(length, total, false).setContentText("已完成：" + percent);// 通知栏显示下载进度条
							mNotificationManager.notify(0, mBuilder.build());
						}
						if (pd.isShowing()) {
							pd.setProgress(formatFileSize(total));
						}
						if (DownLoadApkAsyncTask.this.isCancelled()) {
							return null;
						}
					}
					fos.flush();
					return file;
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (is != null) {
						is.close();
					}
					if (fos != null) {
						fos.close();
					}
					if (conn != null) {
						conn.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			return null;
		}

		@Override
		protected void onPostExecute(File result) {
			if (result != null && lastModified != 0) {
				Log.e("UpdateManager", "设置本地文件最后更新时间：--->> " + result.setLastModified(lastModified));
			}
			if (mBuilder != null) {
				if (result != null) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_VIEW);
					intent.setDataAndType(Uri.fromFile(result), "application/vnd.android.package-archive");
					PendingIntent pIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
					mBuilder.setContentTitle("点我安装新版本").setProgress(100, 100, false).setOngoing(false)
							.setContentText("新版本已下载至：\n.../sdcard/Download/tingchebao.apk").setContentIntent(pIntent)
							.setTicker("下载完成，点击安装。");
					mNotificationManager.notify(0, mBuilder.build());
				} else {
					mBuilder.setContentTitle("下载出错").setProgress(0, 0, false).setContentText("下载的文件损坏，请稍后重试下载。")
							.setTicker("下载出错。").setAutoCancel(false).setOngoing(false);
					mNotificationManager.notify(0, mBuilder.build());
				}
				return;
			}
			if (pd.isShowing()) {
				pd.dismiss();
			}
			if (result != null) {
				install(result);
			} else {
				Toast.makeText(mContext, "下载出现未知错误！", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

	// 检查新版本是否已经下载完毕
	private File createTargetFile() {
		File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
		File file = null;
		if (!downloadDir.exists()) {
			downloadDir.mkdirs();
		}
		File[] downloadedFiles = downloadDir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String filename) {
				return filename.contains("tingchebao");
			}
		});
		if (downloadedFiles != null && downloadedFiles.length > 0) {
			return downloadedFiles[0];
		} else {
			file = new File(downloadDir, "tingchebao.apk");
			try {
				file.createNewFile();
			} catch (IOException e) {
				Log.e("UpdateManager", "--->> 创建临时下载文件失败！！！");
				e.printStackTrace();
			}
		}
		return file;
	}

	private boolean checkIfDownloaded(File target, long lastModified, long length) {
		if (target != null && target.exists()) {
			long targetFileLength = target.length();
			long targetFileLastModified = target.lastModified();
			Log.e("UpdateManager",
					"本地文件更新时间：--->> " + new Date(targetFileLastModified).toString() + ",大小：--->> " + targetFileLength
							+ "\n服务器文件更新时间：--->> " + new Date(lastModified).toString() + ",大小：--->> " + length);
			return targetFileLength == length && targetFileLastModified >= lastModified;
		}
		return false;
	}

	// 将字节转换为MB单位
	private int formatFileSize(int length) {
		return length / 1024;
	}
}

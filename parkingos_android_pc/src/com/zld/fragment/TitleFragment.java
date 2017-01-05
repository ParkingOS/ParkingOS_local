/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.UpdataInfo;
import com.zld.engine.UpdataInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.IsNetWork;
import com.zld.lib.util.OkHttpUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.TimeUtil;
import com.zld.lib.util.UpdateManager;
import com.zld.service.DetectionServerService;
import com.zld.service.HomeExitPageService;
import com.zld.ui.ChooseWorkstationActivity;



/**
 * <pre>
 * 功能说明: 
 * 日期:	2015年4月13日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月13日
 * </pre>
 */
public class TitleFragment extends BaseFragment implements OnClickListener{
	private TextView tv_tcb;//名字
	private TextView tv_tcb_version;//版本号
	private TextView tv_update_time;//时间
	private TextView tv_tcb_workstation;//工作站
	private Button btn_more;//更多
	private Button btn_Restart;//重启
	private Button btn_update;//手动升级
	private String versiontext;//版本号
	private UpdataInfo info;
	private UpdateManager manager;
	private ImageView iv_home_page_icon;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.title, container,
				false);
		initView(rootView);
		onClickEvent();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState); 
		String linelocal = SharedPreferencesUtils.getParam(this.activity.getApplicationContext(),"nettype", "linelocal", "local");
		Log.e("linelocal", "linelocal:"+linelocal);
		if (AppInfo.getInstance().getIsLocalServer(this.activity)) {
			if(linelocal.equals("local")){
				tv_tcb_version.setText("V."+AppInfoUtil.getVersionName(activity)+"_本地连接");
			}else {
				tv_tcb_version.setText("V."+AppInfoUtil.getVersionName(activity)+"_云端连接");
			}
		}else {
			tv_tcb_version.setText("V."+AppInfoUtil.getVersionName(activity)+"_云端连接");
		}
		
		
		new TimeUtil().updateData(tv_update_time);
		String workstation = SharedPreferencesUtils.getParam(activity.getApplicationContext(),
				"set_workStation", "staname", "工作站");
		tv_tcb_workstation.setText(workstation);
	}

	/**
	 * 初始化控件
	 */
	private void initView(View rootView) {
		tv_tcb_version =(TextView) rootView.findViewById(R.id.tv_tcb_version);
		tv_update_time = (TextView) rootView.findViewById(R.id.tv_update_time);
		tv_tcb_workstation = (TextView) rootView.findViewById(R.id.tv_tcb_workstation);
		btn_more = (Button) rootView.findViewById(R.id.btn_more);
		btn_Restart = (Button) rootView.findViewById(R.id.btn_Restart);
		btn_update = (Button) rootView.findViewById(R.id.btn_update);
		
		iv_home_page_icon =(ImageView) rootView.findViewById(R.id.iv_home_page_icon);
		List list = ImageUitls.getLOGO();
		if(list!=null&&list.get(0)!=null){
			Bitmap bitmap = (Bitmap) list.get(0);
			iv_home_page_icon.setImageBitmap(bitmap);
			if(list.get(1)!=null){
				tv_tcb =(TextView) rootView.findViewById(R.id.tv_tcb);
				tv_tcb.setText((""+list.get(1)).split("\\.")[0]);
			}
			
		}
		manager = new UpdateManager(activity);
	}

	/**
	 * 控件点击事件
	 */
	private void onClickEvent() {
		btn_more.setOnClickListener(this);
		btn_Restart.setOnClickListener(this);
		btn_update.setOnClickListener(this);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_more:
			//更多
			Intent intent = new Intent(getActivity(),ChooseWorkstationActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_Restart:
			//主动关闭掉HomeExitPageService，不关掉底层的算法也关不掉，重启后底层跑着多个进程了就。
			closeRemotService();
			//重启
			restartApp(activity);
			break;
		case R.id.btn_update:
			//主动关闭掉HomeExitPageService，不关掉底层的算法也关不掉，重启后底层跑着多个进程了就。
//			closeRemotService();
			versiontext = AppInfoUtil.getVersionCode(this.getActivity());
			long lasttime = 0;

            if (System.currentTimeMillis() - lasttime >= 2000) {
                isNeedUpdate(versiontext);
            }
            lasttime = System.currentTimeMillis();
			break;
		default:
			break;
		}
	}

	public void closeRemotService() {
		if(activity != null){
			Intent intent = new Intent(activity, HomeExitPageService.class);
			Bundle bundle = new Bundle();
			bundle.putString(Constant.INTENT_KEY, "closeService");
			intent.putExtras(bundle);
			activity.startService(intent);
		}
	}

	public void restartApp(Activity activity) {
		/*Intent intent = new Intent(activity, HelloActivity.class);  
		PendingIntent restartIntent = PendingIntent.getActivity(
				activity, 0, intent,Intent.FLAG_ACTIVITY_NEW_TASK);
		//退出程序                                          
		AlarmManager mgr = (AlarmManager)activity.getSystemService(Context.ALARM_SERVICE);    
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 1000,    
				restartIntent); // 1秒钟后重启应用   
		 */		
		if(activity != null){
			Intent i = activity.getBaseContext().getPackageManager()  
					.getLaunchIntentForPackage(activity.getBaseContext().getPackageName());  
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			startActivity(i);
			((application) activity.getApplication()).closeActivity();
		}
	}
	
	public void closeAndRestart() {
		//关闭DetectionServerService--张云飞（解决重启后服务还在后台检测）
		Intent intent = new Intent(activity,DetectionServerService.class);
		activity.stopService(intent);
		Log.e("shuyu", "杀服务");
		//主动关闭掉HomeExitPageService，不关掉底层的算法也关不掉，重启后底层跑着多个进程了就。
		closeRemotService();
		//重启
		restartApp(activity);
	}
//     AlertDialog dialog;
	ProgressDialog dialog;
	 private void isNeedUpdate(final String versiontext) {
		    String url = Constant.getUpdateUrlHand();		
	        System.out.println("访问更新信息的url--------->>>>>>" + url);
	        if (IsNetWork.IsHaveInternet(activity)) {
	        	dialog = ProgressDialog.show(activity, "加载中...", "获取检查更新数据...", true, true);
//	        	dialog = new AlertDialog.Builder(activity)
//	        			.setTitle("加载中...")
//	        			.setMessage("获取检查更新数据...")
//	        			.setCancelable(false)
//	        			.create();
//	        	dialog.show();
	        	//	            ShowDialog.checkUpdateDialog(this.activity.getApplicationContext());
	            Request request = new Request.Builder().url(url).build();
			    OkHttpUtil.enqueue(request, new Callback() {
					
					@Override
					public void onResponse(Call arg0, Response arg1) throws IOException {
						// TODO Auto-generated method stub
						byte[] object = arg1.body().bytes();
						Message m = new Message();
						m.obj = object;
						handle.sendMessage(m);
					}
					
					@Override
					public void onFailure(Call arg0, IOException arg1) {
						// TODO Auto-generated method stub
					}
				});
	        } else {
	        	Log.e(TAG, "没有网络, 进入主界面");
	            activity.showToast("请检查网络!");
	        }

	    }
	 Handler handle = new Handler(){
		 public void handleMessage(android.os.Message msg) {
			 byte[] object = (byte[]) msg.obj;
			 if (object != null) {
                 dialog.dismiss();
                 InputStream is = new ByteArrayInputStream(object);
                 try {
                     info = UpdataInfoParser.getUpdataInfo(is);
                     Log.e("SetActivity", "获取的升级信息是：" + info.toString());
                     is.close();
                     String version = info.getVersion();
                     String versionBeta = info.getVersionBeta();
                     Log.e("SetActivity", "服务器端的版本为" + version);
                     Log.e("SetActivity", "客户端的版本为" + versiontext);
                     boolean hasFormal = false;
                     if (version == null || version.equals("")) {
                     	Log.e(TAG, "获取服务端版本号异常！");
                         activity.showToast("获取服务端版本号异常!");
                     } else {
                     	if(Integer.parseInt(versiontext) < Integer.parseInt(version)){
                         	Log.e(TAG, "版本不同,需要升级");
                             showUpdataDialog(info.getDescription());
                             hasFormal = true;
                     	}
                     }
                     if(!hasFormal){
                     	if (versionBeta == null || versionBeta.equals("")) {
                          	Log.e(TAG, "获取服务端版本号异常！");
                              activity.showToast("获取服务端版本号异常!");
                          } else {
                          	if(Integer.parseInt(versiontext) < Integer.parseInt(versionBeta)){
                              	Log.e(TAG, "版本不同,需要升级");
                                 showUpdataDialog(info.getDescriptionBeta());
                          	}else{
                          		activity.showToast("已是最新版本,不需要升级！");
                          	}
                          }
                     }
                 } catch (Exception e) {
                     // TODO Auto-generated catch block
                     e.printStackTrace();
                     activity.showToast("获取更新异常!");
                 }
             } else {
                 dialog.dismiss();
                 Log.e(TAG, "获取更新超时，进入主界面");
                 activity.showToast("获取更新超时!");
             }
		 };
	 };
	   // 需要更新时弹出升级对话框；
	    private void showUpdataDialog(String message) {
	        AlertDialog.Builder builder = new Builder(activity);
	        builder.setIcon(R.drawable.app_icon_32);
	        builder.setTitle("升级提醒");
//	        builder.setMessage(info.getDescription());
	        builder.setMessage(message);
	        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Log.e(TAG, "下载真来电apk文件" + info.getApkurl());
	            	if(FileUtil.getSDCardPath() == null){
						activity.showToast("sd卡不可用或存储已满!");
						return;
					}
//	                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
	                    manager.new DownLoadApkAsyncTask().execute(info.getApkurl());
//	                } else {
//	                    Toast.makeText(activity.getApplicationContext(), "sd卡不可用", 1).show();
//	                    return;
//	                }
	            }
	        });
	        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

	            @Override
	            public void onClick(DialogInterface dialog, int which) {
	            	Log.e(TAG, "用户取消进入登陆界面");
	            }
	        });
	        builder.setCancelable(false).create().show();
	    }
	 
}

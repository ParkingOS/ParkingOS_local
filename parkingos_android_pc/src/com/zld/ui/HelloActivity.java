package com.zld.ui;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//import com.blueware.agent.android.BlueWare;
import com.networkbench.agent.impl.NBSAppAgent;
//import com.oneapm.agent.android.OneApmAgent;
import com.umeng.analytics.MobclickAgent;
import com.vzvison.MainActivity;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.UpdataInfo;
import com.zld.engine.UpdataInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.service.UpdateService;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("SdCardPath")
public class HelloActivity extends BaseActivity {

	private static final String TAG = "HelloActivity";
	private TextView tv_hello_version;
	private LinearLayout ll_hello_main;
	private UpdataInfo info;
	private String versiontext;
	private Handler handler;
	private final int UPDATE = 888;

	@SuppressLint("HandlerLeak")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); 
		setContentView(R.layout.hello_activity);
		MobclickAgent.updateOnlineConfig(this);// 友盟发送策略；
//		BlueWare.withApplicationToken("5106067A94F46EB853A5042CF1F00C4E64").start(this.getApplication());
//		OneApmAgent.init(this.getApplicationContext()).setToken("5106067A94F46EB853A5042CF1F00C4E64").start();
		NBSAppAgent.setLicenseKey("a04ad42a66984f4391c6a23596a9dc9c")
		.withLocationServiceEnabled(true).start(this);
		((application) getApplication()).setHelloActivity(this);
		initview();
		setVersion();
		saveVersion();
		initAnimation();
		//模拟进入关闭本地化
		SharedPreferencesUtils.setParam(
				getApplicationContext(), "nettype", "isLocal", false);
		Log.e("isLocal","HelloActivity onCreate set isLocal false");
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case UPDATE:
					if(FileUtil.getSDCardPath() == null){
						showToast("sd卡不可用");
						loadMainUI();
						return;
					}

					String version_num = SharedPreferencesUtils.getParam(
							getApplicationContext(), "version", "new_version", "111");

					System.out.println("客户端版本号:"+Integer.parseInt(versiontext)+
							"本地保存的新版本号:"+Integer.parseInt(version_num));

					Intent intentact = new Intent(HelloActivity.this, LoginActivity.class);
					if(Integer.parseInt(versiontext) < Integer.parseInt(version_num)){
						//客户端版本号小于新版本号,选择的下次安装
						File tempFile = new File(FileUtil.getSDCardPath(),"/tingchebaohd_hd.apk");
						if(tempFile.exists()){
							Uri fromFile = Uri.fromFile(tempFile);
						    Bundle bundle = new Bundle();
							bundle.putParcelable("installUri", fromFile);
							intentact.putExtras(bundle);
						}else{
							Log.i(TAG, "下载真来电apk文件" + info.getApkurl());
							Intent intent = new Intent(HelloActivity.this,UpdateService.class);
							intent.putExtra("urlpath", info.getApkurl());
							intent.putExtra("version", info.getVersion());
							startService(intent);
						}
					}else if(Integer.parseInt(versiontext) == Integer.parseInt(version_num)){

					}else{
						//没有升级文件时，下载
						Log.i(TAG, "下载真来电apk文件" + info.getApkurl());
						Intent intent = new Intent(HelloActivity.this,UpdateService.class);
						intent.putExtra("urlpath", info.getApkurl());
						intent.putExtra("version", info.getVersion());
						startService(intent);
					}
					startActivity(intentact);
					break;
				}
			}
		};
		isNeedUpdate();			// 检查更新；
//		initImageLoader();  	// 初始化imageLoader；
		FileUtil.buildFolder();	// 创建文件夹
	}

	private void initAnimation() {
		AlphaAnimation aa = new AlphaAnimation(0.0f, 1.0f);
		aa.setDuration(2000);
		ll_hello_main.startAnimation(aa);
	}

	private void saveVersion() {
		SharedPreferencesUtils.setParam(getApplicationContext(),
				"version", "old_version", versiontext);
	}

	private void setVersion() {
		versiontext = AppInfoUtil.getVersionCode(this);
		tv_hello_version.setText(versiontext);
	}

	private void initview() {
		ll_hello_main = (LinearLayout) findViewById(R.id.ll_hello);
		tv_hello_version = (TextView) findViewById(R.id.tv_hello_version);
	}

	public void isNeedUpdate() {
		new Thread(runnable).start();
	}

	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			HttpURLConnection conn = null;
			InputStream inputStream = null;
			try {
				URL url = new URL(Constant.UPDATE_URL);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(10000);
				conn.setReadTimeout(8000);
				conn.setDoInput(true);
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Content-Type", "text/html");
				conn.setRequestProperty("Accept-Charset", "utf-8");
				conn.setRequestProperty("contentType", "utf-8");
				inputStream = conn.getInputStream();
				byte[] buffer = null;
				if (conn.getResponseCode() == 200) {
					SharedPreferencesUtils.setParam(
							getApplicationContext(), "nettype", "isLocal", false);
					Log.e("isLocal","HelloActivity Runnable set isLocal false");
					buffer = new byte[1024];
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					int len;
					while ((len = inputStream.read(buffer)) != -1) {
						out.write(buffer, 0, len);
					}
					buffer = out.toByteArray();
				}
				InputStream is = new ByteArrayInputStream(buffer);
				info = UpdataInfoParser.getUpdataInfo(is);
				String version = info.getVersion();
				if (version == null || version.equals("")) {
					Log.i(TAG, "获取服务端版本错误，进入主界面");
					loadMainUI();
				} else {
					if (Integer.parseInt(versiontext) >= Integer.parseInt(version)) {
						Log.i(TAG, "已是最新版,无需升级, 进入主界面");
						loadMainUI();
					} else {
						Log.i(TAG, "版本不同,需要升级");
						Message message = new Message();
						message.what = UPDATE;
						handler.sendMessage(message);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				//是否是本地服务器
				if(!AppInfo.getInstance().getIsLocalServer(HelloActivity.this)){//是本地服务器则没有平板本地化的感念
					//无网,网络超时  开启本地化
//					SharedPreferencesUtils.setParam(
//							getApplicationContext(), "nettype", "isLocal", true);
//					Log.e("isLocal","HelloActivity Runnable set isLocal false");
				}
				loadMainUI();
			} finally {
				try {
					if (inputStream != null) {
						inputStream.close();
					}
					if (conn != null) {
						conn.disconnect();
					}
				} catch (IOException e) {
					e.printStackTrace();
					Log.e("HelloActivity", "释放资源出错");
				}
			}
		}
	};

	public void loadMainUI() {
		Intent intent = new Intent(this, LoginActivity.class);
//		Intent intent = new Intent(this, MainActivity.class);

		startActivity(intent);
		finish(); 
	}

//	public void onResume() {
//		super.onResume();
//		MobclickAgent.updateOnlineConfig(this);// 友盟发送策略；
//		MobclickAgent.onResume(this);
//	}
//
//	public void onPause() {
//		super.onPause();
//		MobclickAgent.onPause(this);
//	}

	
}

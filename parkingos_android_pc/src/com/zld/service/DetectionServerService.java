package com.zld.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources.Theme;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.LoginInfo;
import com.zld.bean.SmAccount;
import com.zld.db.SqliteManager;
import com.zld.engine.LoginInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.IsNetWork;
import com.zld.lib.util.MD5Utils;
import com.zld.lib.util.SharedPreferencesUtils;

/**
 * PING 地址
 * 
 * @author HZC
 *
 */
public class DetectionServerService extends BaseService {

	Thread thread;
	private String token;
	int i = 0;
	private Context context;
	private SqliteManager sqliteManager;
	private static final String TAG = "DetectionServerService";
	boolean isLocalServer;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		context = this;
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					Log.e(TAG, "每10s执行一次ping " + Constant.PING_TEST_LOCAL);
					token = AppInfo.getInstance().getToken();
					boolean ping = IsNetWork.ping();
					Log.e(TAG, "ping:" + ping + " i的值：" + i + "==" + Constant.PING_TEST_LOCAL);
					if (ping) {
                        // 是否是本地服务器
//                        boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype",
//                                "isLocalServer", false);
                        Log.e(TAG, "DetectionService isLocalServer是true是本地服务器:" + isLocalServer);
                        if (isLocalServer) {// 是本地服务器,本地Ping通

                            if (Constant.requestUrl.contains("s.tingchebao.com")) {// 此时线上状态,Ping通本地,提示重启登录本地
                                i = 0;
                                SharedPreferencesUtils.setParam(getApplicationContext(), "nettype", "linelocal", true);
                                SharedPreferencesUtils.setParam(getApplicationContext(), "nettype", "isLocalServer", true);
                                reStart(false);// 本地服务器畅通,确定切换到本地吗？
                            } else {// 此时本地状态,能Ping通就不用管
                            }
                        } else {
                            // 如果是ping通了线上地址，就把ping地址换成本地的，尝试ping本地服务器
                            String localip = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype",
                                    "localip", null);
                            if (!TextUtils.isEmpty(localip)) {
                                Constant.pingUrl(localip);
                                isLocalServer = true;
//                                SharedPreferencesUtils.setParam(getApplicationContext(), "nettype", "isLocalServer",
//                                        true);
                            }
                            Log.e(TAG, "  token:" + token);
                            if (token == null) {
                                login();
                            }
                        }
                        i = 0;
                    } else {// Ping不通
                        if (Constant.requestUrl.contains("s.tingchebao.com")) {
                            // 此时线上状态,Ping不通本地,不做动作
                        } else {
                            // 此时本地状态,Ping不通就准备切线上
                            i++;
                            if (i > 2) {
                                // 是否是本地服务器
                                boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(),
                                        "nettype", "isLocalServer", false);
                                Log.e(TAG, "DetectionService isLocalServer是true:" + isLocalServer);
                                if (isLocalServer) {// 是本地服务器
                                    String linelocal = SharedPreferencesUtils.getParam(getApplicationContext(),
                                            "nettype", "linelocal", "local");
                                    Log.e(TAG, "是本地服务器,Ping不通,当前状态：" + linelocal);
                                    if (linelocal.equals("line")) {// 本地服务器在线上时Ping本地Ping不通,不用处理
                                        i = 0;
                                    } else {// 本地服务器在本地时Ping不通,重新登录

                                        SharedPreferencesUtils.setParam(getApplicationContext(), "nettype",
                                                "isLocalServer", false);
                                        Log.e(TAG, "设置了false");
//                                        FileUtil.writeSDFile("切换步骤", "  设置了false");

                                        reStart(true);// 本地服务器异常,确定切换到线上服务器吗？
                                    }
                                } else {// 不是本地服务器,Ping不通,开启平板本地化
                                    // 连续3次没Ping通 则开启本地化模式
                                    Log.e(TAG, "DetectionService onStart set isLocal true");
                                }
                            }
                        }
                    }
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		IBinder result = null;
		if (null == result)
			result = new ServiceBinder();
		Toast.makeText(this, "onBind", Toast.LENGTH_LONG);
		return result;
	}

	@Override
	public void onStart(Intent intent, int startId) {

	}

	/**
	 * 服务器登录时切换
	 * 
	 * @param isLine
	 *            true线下切线上 false 线上切线下
	 */
	private void reStart(boolean isLine) {
		// 发送广播重启就可以,登录时,默认登录本地,登录不上,自动切换为线上
		Intent inte = new Intent();
		Bundle bundle = new Bundle();
		bundle.putBoolean("isLine", isLine);
		inte.putExtras(bundle);
		inte.setAction("com.zld.action.restartservicereceiver");
		sendBroadcast(inte);
	}

	private void login() {
		String uid = AppInfo.getInstance().getUid();
		Log.e(TAG, "账号：" + uid);
		if (uid != null) {
			SmAccount selectAccount = sqliteManager.selectAccountByUid(uid);
			if (selectAccount != null) {
				String username = selectAccount.getUsername();
				String password = selectAccount.getPassword();
				Log.e(TAG, "数据库中保存的用户信息：" + username + "---123456:" + password);
				if (username != null) {
					String md5password = null;
					try {
						md5password = MD5Utils.MD5(MD5Utils.MD5(password) + "zldtingchebao201410092009");
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					longinSuccess(username, md5password, password);
				}
			}
		}
	}

	/**
	 * 切换账号这里的登录加上worksite_id字段，为了交接班 把用户输入的账号和密码提交给服务器，验证账户和密码是否正确；
	 */
	public void longinSuccess(final String username, final String MD5password, final String password) {
		String worksiteId = SharedPreferencesUtils.getParam(getApplicationContext(), "set_workStation",
				"workstation_id", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.LOGIN);
		params.setUrlParams("username", username);
		params.setUrlParams("password", MD5password);
		params.setUrlParams("version", AppInfoUtil.getVersionName(this));
		params.setUrlParams("worksite_id", worksiteId);
		String url = params.getRequstUrl();
		Log.e(TAG, "登录的URL---------------->>" + url);
		HttpManager.requestLoginTest(this, url, this, username, password);
	}

	@Override
	public boolean doSucess(String url, byte[] buffer, String username, String password) {
		if (url.contains(Constant.LOGIN)) {
			Log.e(TAG, "获取登录信息为：" + Constant.LOGIN + "---------------->>" + buffer);
			doLoginResult(buffer, username, password);
		}
		return true;
	}

	private void doLoginResult(byte[] buffer, String username, String password) {
		// TODO Auto-generated method stub
		if (buffer != null) {
			try {
				Log.e(TAG, "登陆的返回信息是---" + new String(buffer, "utf-8"));
				InputStream is = new ByteArrayInputStream(buffer);
				LoginInfo info = LoginInfoParser.getLoginInfo(is);
				if (null != info) {
					if (null != info.getState() && info.getState().equals("1")) {
						SharedPreferencesUtils.delete(context, "autologin", info.getName());
						sqliteManager.deleteAccountData(info.getName());
						return;
					}
					Log.e(TAG, "解析登录信息：" + info.toString() + "==username==" + username);
					if (info.getInfo().equals("success")) {
						token = info.getToken();
						Log.e(TAG, "success的token：" + token);
						if (token != null) {
							AppInfo.getInstance().setToken(token);
							AppInfo.getInstance().setUid(username);
						}
						/* 保存登录时间 */
						saveLongOnTime(username, info);
						SmAccount selectAccount = sqliteManager.selectAccountByUsrName(username);
						if (selectAccount == null) {
							sqliteManager.insertAccountData(info.getName(), username, password);
						} else {
							if (!selectAccount.getPassword().equals(password)
									|| !selectAccount.getAccount().equals(info.getName())) {
								sqliteManager.updateAccountData(info.getName(), username, password);
							}
						}
						// 发送广播
						Intent intent = new Intent();
						intent.setAction("com.zld.action.startservicereceiver");
						sendBroadcast(intent);
					} else {
						Log.e(TAG, "登录失败：" + info.getInfo());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** 之前的从没有过登录账号,或登录的账号跟之前的不一致,则保存登录时间,便于查询交接班金额信息 */
	private void saveLongOnTime(final String username, LoginInfo info) {
		String beforeUser = SharedPreferencesUtils.getParam(context.getApplicationContext(), "autologin", "account",
				"");
		Log.e(TAG, "beforeUser:" + beforeUser + "	username:" + username);
		if (TextUtils.isEmpty(beforeUser) || !beforeUser.equals(username)) {
			Log.e(TAG, "为null或不同则保存" + info.getLogontime());
			SharedPreferencesUtils.setParam(context.getApplicationContext(), "autologin", "logontime",
					info.getLogontime());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "销毁DetectionServerService ");
	}

	// 此方法是为了可以在Acitity中获得服务的实例
	public class ServiceBinder extends Binder {
		public DetectionServerService getService() {
			return DetectionServerService.this;
		}
	}
}

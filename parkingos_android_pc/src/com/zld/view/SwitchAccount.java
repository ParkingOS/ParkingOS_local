package com.zld.view;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.Gson;
import com.zld.R;
import com.zld.application;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.bean.AppInfo;
import com.zld.bean.LoginInfo;
import com.zld.bean.ParkingInfo;
import com.zld.bean.SmAccount;
import com.zld.db.SqliteManager;
import com.zld.engine.LoginInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpCallBack;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.InputUtil;
import com.zld.lib.util.MD5Utils;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.ShowDialog;
import com.zld.lib.util.StringDesUtils;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

@SuppressLint("NewApi")
public class SwitchAccount implements HttpCallBack{
	public static final int ENTRANCE = 0;
	public static final int EXIT = 1;
	public static final int HOMEEXIT = 2;
	private View parent;
	private Toast mToast;
	private View loginView;
	private Context activity;
	private Activity mainactivity;
	private Button bt_login;
	private ProgressDialog dialog;
	@SuppressWarnings("unused")
	private ArrayList<String> accounts;
	final static String regularEx = "|";
	private LinearLayout rv_login_activity;
	public EditText et_login_password;
	public AutoCompleteTextView at_login_username;
	private Set<String> users = new HashSet<String>();

	@SuppressWarnings("unused")
	private int stationType;
	private ListView listview;
	private PopupWindow popupWindow;
	private PopupDialog popupDialog;
	public static String token = null;
	public static String comid = null; 
	private SqliteManager sqliteManager;
	private static final String TAG = "SwitchAccount";

	public SwitchAccount() {
		super();
		// TODO Auto-generated constructor stub
	}

	public SwitchAccount(ZldNewActivity activity, View parent, int stationType) {
		this.parent = parent;
		this.activity = activity;
		mainactivity = activity;
		this.stationType = stationType;
		if (sqliteManager == null) {
			sqliteManager = ((application) activity.getApplicationContext()).getSqliteManager(activity);
		}
		if (dialog == null) {
			dialog = new ProgressDialog(activity);
			dialog.setMessage("登录中...");
		}
	}

	@SuppressLint("NewApi")
	public void showSwitchAccountView() {
		showPopupWindow(parent);
	}

	public void showSwitchAccount() {
		initView();
		setView();
		popupDialog = new PopupDialog(activity);
		popupDialog.setContentView(loginView);
		popupDialog.showAsDropDown(parent);
		popupDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				// TODO Auto-generated method stub
				// 输入法是否弹出
				Log.e("SwitchAccount", "Dialog隐藏！！！");
				InputUtil.closeInputMethod(activity);
			}
		});
	}

	public void initView() {
		loginView = LayoutInflater.from(activity).inflate(
				R.layout.relogin_activity, null);
		rv_login_activity = (LinearLayout) loginView.
				findViewById(R.id.rv_login_activity);
		at_login_username = (AutoCompleteTextView) loginView
				.findViewById(R.id.at_login_account);
		et_login_password = (EditText) loginView
				.findViewById(R.id.et_login_password);
		bt_login = (Button) loginView.findViewById(R.id.bt_longin_login);
	}

	public void setView() {
		rv_login_activity.setBackground(activity.getResources().getDrawable(R.drawable.small_login_bg));
		bt_login.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String username = at_login_username.getText().toString().trim();
				String password = et_login_password.getText().toString().trim();
				if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
					showToast("账号或密码不能为空");
				} else {
					try {
						String md5password = MD5Utils.MD5(MD5Utils
								.MD5(password) + "zldtingchebao201410092009");
						longinSuccess(username, md5password, password);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						Log.e(TAG, "MD5加密异常！");
						e.printStackTrace();
					}

				}
			}

		});	
		//恢复编辑框输入
		at_login_username.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				at_login_username.setFocusableInTouchMode(true);
				et_login_password.setFocusableInTouchMode(true);
				return false;
			}
		});
	}

	/**
	 * 切换账号这里的登录加上worksite_id字段，为了交接班
	 * 把用户输入的账号和密码提交给服务器，验证账户和密码是否正确；
	 */
	public void longinSuccess(final String username,
			final String MD5password,final String password) {
		if(popupWindow != null){
			popupWindow.dismiss();
		}
		if(popupDialog != null){
			popupDialog.dismiss();
		}

		// 为本地化时间准一点，需设置网络时间；
//		setInternetTime();
		/*// 无网 本地化模式
		if (SharedPreferencesUtils.getParam(
		activity.getApplicationContext(),"nettype", "isLocal", false)) {
			localLoginTimeOut(username, password);
			return;
		}*/

		String worksiteId = SharedPreferencesUtils.getParam(
				activity.getApplicationContext(),"set_workStation", "workstation_id", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.LOGIN);
		params.setUrlParams("username", username);
		params.setUrlParams("password", MD5password);
		params.setUrlParams("version", AppInfoUtil.getVersionName(activity));
		params.setUrlParams("worksite_id", worksiteId);
		String url = params.getRequstUrl();
		Log.e(TAG, "登录的URL---------------->>" + url);
		dialog.show();
		HttpManager.requestLoginGET(activity, url,this,username,password);	
	}

	/**
	 * 如果为自定义时间,则设置为网络时间
	 */
//	private void setInternetTime() {
//		ContentResolver cv = activity.getContentResolver();
//		String isAutoTime = android.provider.Settings.System.getString(cv, Global.AUTO_TIME);
//		if ("0".equals(isAutoTime)) {//1=yes, 0=no
//			ShowDialog.showSetTimeDialog(activity);
//			return;
//		}
//	}

	/**
	 * 获取车场信息 获取comid信息
	 * 
	 * @param username
	 * @param password
	 * @param users
	 * @param info
	 */
	public void getParkingInfo(final String username, final String password,
			final Set<String> users, final LoginInfo info) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.COMINFO);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("out", "json");
		String url = params.getRequstUrl();
		Log.e(TAG, "获取车场信息的URL---------------->>" + url);
		HttpManager.requestCominfoGET(activity, url,this,username,password,info);	
	}

	private void loginChoose(final String username, final String password,
			final Set<String> users, LoginInfo info) {
		// 自动登录；
		SharedPreferences userinfo = activity.getSharedPreferences("userinfo",
				Context.MODE_PRIVATE);
		SharedPreferences autologin = activity.getSharedPreferences(
				"autologin", Context.MODE_PRIVATE);
		Editor autoEdit = autologin.edit();
		Editor userInfoedit = userinfo.edit();
		autoEdit.putString("account", username);
		autoEdit.putString("passwd", password);
		try {
			String encode = new StringDesUtils().encrypt(password);
			userInfoedit.putString(username, encode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userInfoedit.commit();
		autoEdit.commit();
		// 自动账号提示记录
		if (!users.contains(username)) {
			Log.e("要插入的set集合数据为---", users.toString());
			SharedPreferences sp1 = activity.getSharedPreferences("usernames",Context.MODE_PRIVATE);
			Editor edit = sp1.edit();
			users.add(username);
			SharedPreferencesUtils.putStringSet(edit, "usernames", users).commit();
		}
		if (popupDialog != null){
			popupDialog.dismiss();
		}
		((ZldNewActivity)activity).setUserName();
	}

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(activity.getApplicationContext(), text,
					Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	/**
	 * 展示切换账号登录框
	 * @param parent
	 */
	@SuppressWarnings({ "deprecation", "unused" })
	private void showPopupWindow(View parent) {
		int screenHeight = 0; 
		// TODO Auto-generated method stub
		if(listview == null){
			listview = (ListView) LayoutInflater.from(activity).inflate(R.layout.account_droplist, null); 
		}
		final ArrayList<String> selectAllAccount = sqliteManager.selectAllAccount();
		//目的是显示用户名列表底部的添加账号
		selectAllAccount.add("添加账号");
		selectAllAccount.add("下班");
		if(selectAllAccount.size() != 0){
			AccountDropListAdapter adapter = new AccountDropListAdapter(activity, selectAllAccount,false);
			listview.setAdapter(adapter);
		}
		if (popupWindow == null) {  
			popupWindow = new PopupWindow(listview,LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			DisplayMetrics dm = new DisplayMetrics();
			//获取屏幕信息
			((Activity) activity).getWindowManager().getDefaultDisplay().getMetrics(dm);
			int screenWidth = dm.widthPixels;
			screenHeight = dm.heightPixels;
			popupWindow.setWidth((int)(screenWidth/5));
		}  
		popupWindow.setFocusable(true);  
		popupWindow.setOutsideTouchable(true);  
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景  
		popupWindow.setBackgroundDrawable(new BitmapDrawable());  
		final int[] location = new int[2];  
		parent.getLocationOnScreen(location);  
		popupWindow.showAsDropDown(parent);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				String account = selectAllAccount.get(position);
				Log.e(TAG,"切换账号点击的是："+account);
				if(account == null||account.equals(AppInfo.getInstance().getName())){
					return;
				}

				SmAccount selectAccount = sqliteManager.selectAccount(account);
				if(selectAccount != null){
					String username = selectAccount.getUsername();
					String password = selectAccount.getPassword();
					Log.e(TAG, "数据库中保存的用户信息："+username+"---123456:"+password);
					if (username != null) {
						String md5password = null;
						try {
							md5password = MD5Utils.MD5(MD5Utils.MD5(password)
									+ "zldtingchebao201410092009");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						longinSuccess(username, md5password, password);
					}
				}else{
					if("添加账号".equals(account)){
						popupWindow.dismiss();
						showSwitchAccount();
					}else if("下班".equals(account)){
						afterWorkDialog("确认下班吗？","下班");
					}
				}
			}
		});
	}

	/**选择下班对话框*/
	private void afterWorkDialog(String msg,String title) {
		Builder buildDialog = ShowDialog.buildDialog(activity,msg,title);
		buildDialog.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				afterWork();
			}
		});
		buildDialog.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		buildDialog.show();
	}

	/**之前的从没有过登录账号,或登录的账号跟之前的不一致,则保存登录时间,便于查询交接班金额信息*/
	private void saveLongOnTime(final String username, LoginInfo info) {
		String beforeUser = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "autologin", "account", "");
		Log.e(TAG,"beforeUser:"+beforeUser+"	username:"+username);
		if(TextUtils.isEmpty(beforeUser)||!beforeUser.equals(username)){
			Log.e(TAG,"为null或不同则保存"+info.getLogontime());
			if(activity!=null){
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "logontime", info.getLogontime());
				SharedPreferencesUtils.setParam(activity.getApplicationContext(),"userinfo", "mobilepay", "0");
				SharedPreferencesUtils.setParam(activity.getApplicationContext(),"userinfo", "cashpay", "0");
				Message msg = new Message();
				msg.obj = info.getLogontime();
				handle.sendMessage(msg);
			}
		}
	}
	Handler handle = new Handler(){
		public void handleMessage(android.os.Message msg) {
			String obj = (String) msg.obj;
			((ZldNewActivity) activity).setMoneyAndTime(obj);
		};
	};
	/**只有一个收费员白天上班晚上休息的情况，需要点击下班按钮，重新计费
	 * collectorlogin.do?action=gooffwork&worksiteid=3&uid=1197*/
	private void afterWork() {
		// TODO Auto-generated method stub
		String worksiteId = SharedPreferencesUtils.getParam(activity.getApplicationContext(),
				"set_workStation", "workstation_id", "");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.AFTER_WORK);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		params.setUrlParams("worksiteid", worksiteId);
		String url = params.getRequstUrl();
		Log.e(TAG, "下班的url是--->"+url);
		HttpManager.requestGET(activity, url, this);
	}

	@Override
	public boolean doSucess(String url, String object,String username,String password,LoginInfo info) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if(url.contains(Constant.COMINFO)){
			doGetComInfo(object,username,password,info);
		}
		return true;
	}

	@Override
	public boolean doSucess(String url, byte[] buffer, String username, String password){
		dialog.dismiss();
		if(url.contains(Constant.LOGIN)){
			Log.e(TAG, "获取登录信息为："+Constant.LOGIN+"---------------->>" + buffer);
			doLoginResult(buffer,username,password);
		}
		return true;
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		if (url.contains(Constant.AFTER_WORK)){
			doAfterWorkResult(object);
		}
		return true;
	}

	/**
	 * @param object
	 */
	private void doAfterWorkResult(String object) {
		// TODO Auto-generated method stub
		Log.e(TAG,"下班结果："+object);
		if("1".equals(object)){
			//清除本地金额和上班时间
			SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "cashpay", "0");
			SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "mobilepay", "0");
			SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "logontime", "0");
			SharedPreferences autologin = 
					activity.getSharedPreferences("autologin",Context.MODE_PRIVATE);
			autologin.edit().putString("passwd", "").commit();
			SharedPreferences firstSetSPF = activity.getSharedPreferences("set_workStation",
					Context.MODE_PRIVATE);
			firstSetSPF.edit().putBoolean("is_first", true).commit();
//			android.os.Process.killProcess(android.os.Process.myPid());
//			System.exit(0);
			mainactivity.finish();
//			ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//			am.
//			mainactivity.getTaskId();
//			ActivityManager.
		}else if("-1".equals(object)){
			afterWorkDialog("确认下班吗？", "重新确认下班");
		}
	}

	private void doLoginResult(byte[] buffer,String username,String password) {
		// TODO Auto-generated method stub
		if (buffer != null) {
			try {
				Log.e(TAG, "登陆的返回信息是---" + new String(buffer, "utf-8"));
				InputStream is = new ByteArrayInputStream(buffer);
				LoginInfo info = LoginInfoParser.getLoginInfo(is);
				if(null != info){
					if(null != info.getState()&&info.getState().equals("1")){
						showToast("此收费员已经被删除");
						SharedPreferencesUtils.delete(activity, "autologin", info.getName());
						sqliteManager.deleteAccountData(info.getName());
						return;
					}
					Log.e(TAG, "解析登录信息：" + info.toString()+"==username=="+username);
					if (info.getInfo().equals("success")) {
						token = info.getToken();
						Log.e(TAG, "success的token：" + token);
						if (token != null) {
							AppInfo.getInstance().setToken(token);
							AppInfo.getInstance().setUid(username);
						}
						/*保存登录时间*/
						saveLongOnTime(username, info);
						SmAccount selectAccount = sqliteManager.selectAccountByUsrName(username);
						if(selectAccount == null){
							sqliteManager.insertAccountData(info.getName(),username, password);
						}else {
							if (!selectAccount.getPassword().equals(password) || !selectAccount.getAccount().equals(info.getName())){
								sqliteManager.updateAccountData(info.getName(), username, password);
							}
						}
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "account", username);
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "passwd", password);
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "name", info.getName());
						SharedPreferencesUtils.setParam(activity.getApplicationContext(), "autologin", "role", info.getRole());
						getParkingInfo(username, password, users, info);
					} else {
						popupWindow.dismiss();
						showSwitchAccount();
						showToast("登录失败：" + info.getInfo());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}

	private void doGetComInfo(String object,String username,String password,LoginInfo info) {
		// TODO Auto-generated method stub
		Gson gson = new Gson();
		ParkingInfo parkingInfo = gson.fromJson(object,ParkingInfo.class);
		if (parkingInfo != null) {
			Log.e(TAG, "解析的车场信息为" + parkingInfo.toString());
			if (parkingInfo.getParkingtotal() != null) {
				if (parkingInfo.getParkingtotal() != null) {
					comid = parkingInfo.getId();
					AppInfo.getInstance().setComid(comid);
					if(info != null){
						AppInfo.getInstance().setName(info.getName());
					}
					if(parkingInfo != null){
						AppInfo.getInstance().setIshidehdbutton(
								parkingInfo.getIshidehdbutton());
					}
					loginChoose(username, password, users, info);
				}
			}
		}	
	}

	/* (non-Javadoc)
	 * @see com.zld.lib.http.HttpCallBack#doSucess(java.lang.String, boolean, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public boolean doSucess(String url, boolean isSingle, String passid,
			String object, int i, String object2) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.zld.lib.http.HttpCallBack#doFailure(java.lang.String, com.androidquery.callback.AjaxStatus)
	 */
	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String worksiteId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String str1, String str2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, byte[] buffer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String requestUrl, String username2,
			String password2, LoginInfo info2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer,
			String username, String password) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeout(String url, String str) {
		// TODO Auto-generated method stub

	}

	@Override
	public void timeout(String url, String str, String str2) {

		// TODO Auto-generated method stub
		dialog.dismiss();
		if(url.contains(Constant.LOGIN)){
			Builder buildDialog = ShowDialog.buildDialog(activity,"请稍后登录","网络故障");
			buildDialog.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					afterWork();
				}
			});
			buildDialog.show();
			return;
			//本地超时切换账号
			//localLoginTimeOut(str, str2);
		}
	}

	/**
	 * 无网本地化登录
	 * @param username
	 * @param password
	 */
	@SuppressWarnings("unused")
	private void localLoginTimeOut(final String username, final String password) {
		// 查询数据库中是否有账号 和密码, 有的话则跳转、无的话 提示
		SmAccount selectAccount = sqliteManager.selectUsername(username);
		if(selectAccount != null){
			Log.e(TAG,"输入的账户："+selectAccount.getAccount()+"=="+username+"=获取保存的密码："+selectAccount.getPassword()+"=输入的密码："+password);
			if(selectAccount.getPassword()!=null&&password.equals(selectAccount.getPassword())){
				String comid = SharedPreferencesUtils.getParam(activity.getApplicationContext(), "zld_config", "comid", null);
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "name", selectAccount.getAccount());
				AppInfo.getInstance().setComid(comid);
				AppInfo.getInstance().setUid(username);
				AppInfo.getInstance().setName(selectAccount.getAccount());
				((ZldNewActivity)activity).setUserName();
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "cashpay", "0");
				SharedPreferencesUtils.setParam(activity.getApplicationContext(), "userinfo", "mobilepay", "0");
			}
		}else{
			showToast("请输入正确的账号和密码");
		}
	}

}

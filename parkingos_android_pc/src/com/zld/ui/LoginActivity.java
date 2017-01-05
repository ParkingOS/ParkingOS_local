package com.zld.ui;

import android.app.ActionBar.LayoutParams;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.zld.R;
import com.zld.adapter.AccountDropListAdapter;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.CarType;
import com.zld.bean.FreeResons;
import com.zld.bean.LiftReason;
import com.zld.bean.LoginInfo;
import com.zld.bean.MyCameraInfo;
import com.zld.bean.MyLedInfo;
import com.zld.bean.ParkingInfo;
import com.zld.bean.SmAccount;
import com.zld.bean.WorkStationDevice;
import com.zld.db.SqliteManager;
import com.zld.engine.LoginInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.IsNetWork;
import com.zld.lib.util.MD5Utils;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringDesUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LoginActivity extends BaseActivity {

    private static final String TAG = "LoginActivity";
    private Button bt_login;
    private ImageView iv_droplist;
    private TextView tv_version_name;
    private TextView et_ip;
//    private ArrayList<String> accounts;
    private ProgressDialog dialog;
    private EditText et_login_password;
//    final static String regularEx = "|";
    private AutoCompleteTextView at_login_username;
    private Set<String> users = new HashSet<String>();

    private Uri uri = null;
    private ListView listview;
    private PopupWindow popupWindow;
    private SqliteManager sqliteManager;
    @SuppressWarnings("unused")
    private String username;
    @SuppressWarnings("unused")
    private String password;
    @SuppressWarnings("unused")
    private LoginInfo info;
    // private ImageLoader imageLoader;

    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        IsNetWork.IsHaveInternet(LoginActivity.this);
        Intent intent = initStallUri();
//		((application) getApplication()).setLoginActivity(this);
        initSqliteManager();
        initView();
        setView();
        initDialog();
        tokenOperation(intent);
    }

    boolean isLogin = false;

    private Intent initStallUri() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            uri = bundle.getParcelable("installUri");
        }
        return intent;
    }

    private void tokenOperation(Intent intent) {
        Log.e(TAG, "获取到token");
        String token = intent.getStringExtra("token");
        if (token != null && token.equals("false")) {
            showToast("你的账号或者工作站在别处登录，请修改密码确保安全！");
        } else {
            getAutoLoginInfo();
        }
    }

    private void initDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(LoginActivity.this);
            dialog.setMessage("登录中...");
        }
    }

    private void initSqliteManager() {
        if (sqliteManager == null) {
//            sqliteManager = new SqliteManager(LoginActivity.this);
//            sqliteManager = ((application)getApplication()).getSqliteManager();
            sqliteManager = ((application) getApplication()).getSqliteManager(LoginActivity.this);
        }
    }

    /**
     * 获取登录信息
     */
    public void getAutoLoginInfo() {
        SharedPreferences autologin = getSharedPreferences("autologin", Context.MODE_PRIVATE);
        if (!(autologin.getString("account", "").equals("") && autologin.getString("password", "").equals(""))) {
            String username = autologin.getString("account", "");
            String password = autologin.getString("passwd", "");
            autologin(username, password);
        }
    }

    /**
     * 自动登录
     *
     */
    private void autologin(String username, String password) {
        FileUtil.writeSDFile("登录步骤","自动登录  是否是本地服务器："+AppInfo.getInstance().getIsLocalServer(LoginActivity.this));
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
            at_login_username.setText(username);
            et_login_password.setText(password);
            Log.e(TAG, "自动登录获取getIsLocalServer:" + AppInfo.getInstance().getIsLocalServer(LoginActivity.this));
            // 是否是本地服务器
            if (AppInfo.getInstance().getIsLocalServer(LoginActivity.this)) {// 是本地服务器
                // 获取输入的Ip
                Log.e("linelocal", "是本地服务器：");
                String localip = SharedPreferencesUtils.getParam(LoginActivity.this, "nettype", "localip", null);
                if (localip != null) {
                    et_ip.setText(localip);
                }
            }

            try {
                String md5password = MD5Utils.MD5(MD5Utils.MD5(password) + "zldtingchebao201410092009");
                // uri为null说明本地没有新版本，直接登录；
                if (uri != null) {
                    Instanll(uri);
                } else {
                    /* 为本地化时间准一点，需设置网络时间 */
//					ContentResolver cv = LoginActivity.this.getContentResolver();
//					String isAutoTime = android.provider.Settings.System.getString(cv, Global.AUTO_TIME);
//					if (Constant.sZero.equals(isAutoTime)) {// 1=yes, 0=no
//						ShowDialog.showSetTimeDialog(LoginActivity.this);
//						return;
//					}
                    longinSuccess(username, md5password, password, users);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "MD5加密异常！");
                e.printStackTrace();
            }
        }
    }

    public void initView() {
        at_login_username = (AutoCompleteTextView) findViewById(R.id.at_login_account);
        et_login_password = (EditText) findViewById(R.id.et_login_password);
        tv_version_name = (TextView) findViewById(R.id.tv_version_name);
        bt_login = (Button) findViewById(R.id.bt_longin_login);
        iv_droplist = (ImageView) findViewById(R.id.iv_droplist);
        et_ip = (TextView) findViewById(R.id.et_ip);

        tv_version_name.setText("当前版本：V" + getVersions());
        SharedPreferences prefs = getSharedPreferences("usernames", Context.MODE_PRIVATE);
        users = SharedPreferencesUtils.getStringSet(prefs, "usernames", users);
        users.remove("");
        ArrayList<String> accounts = new ArrayList<String>();
        accounts.addAll(users);
        ArrayAdapter<String> av = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, accounts);
        at_login_username.setAdapter(av);
        at_login_username.setThreshold(0);
        FileUtil.writeSDFile("登录步骤","accounts.size()="+accounts.size()+"  是否是本地服务器"+SharedPreferencesUtils.getParam(LoginActivity.this, "nettype", "isLocalServer", false)+"  localip:"+SharedPreferencesUtils.getParam(LoginActivity.this, "nettype", "localip", null));
        if (accounts.size() > 0) {
            at_login_username.setText(accounts.get(0));
            // 是否是本地服务器
            if (SharedPreferencesUtils.getParam(LoginActivity.this, "nettype", "isLocalServer", false)) {
                String localip = SharedPreferencesUtils.getParam(LoginActivity.this, "nettype", "localip", null);
                if (!TextUtils.isEmpty(localip)) {
                    et_ip.setText(localip);
                }
            }

        }
        SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "linelocal", "local");
    }

    public void setView() {
        bt_login.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 登录操作
                loginOperation();
            }
        });
        iv_droplist.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPopupWindow(at_login_username);
            }
        });
    }

    /**
     * 功能说明：登录操作
     */
    private void loginOperation() {
        // 获取到输入的账号密码。与服务器对比；
        String username = at_login_username.getText().toString().trim();
        String password = et_login_password.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            showToast("账号或密码不能为空");
        } else {
            try {
                String md5password = MD5Utils.MD5(MD5Utils.MD5(password) + "zldtingchebao201410092009");
                Log.e(TAG, "获得md5");
                longinSuccess(username, md5password, password, users);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                Log.e(TAG, "MD5加密异常！");
                e.printStackTrace();
            }

        }
    }

    /**
     * 把用户输入的账号和密码提交给服务器，验证账户和密码是否正确；
     *
     */
    public void longinSuccess(final String username, final String MD5password, final String password,
                              final Set<String> users) {
        // 在登录之前判断ip是否为null
        String ip = et_ip.getText().toString().trim();
        FileUtil.writeSDFile("登录步骤","loginsuccess  ip:"+ip);
        if (!ip.isEmpty()) {// 输入ip不为null
            Log.e("linelocal", "输入ip不为null:" + ip);
            Log.e("linelocal", "设置isLocalServer为true");
			/* 不需要同步订单,不开启Service */
            SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "isLocalServer", true);
            String linelocal = SharedPreferencesUtils.getParam(LoginActivity.this, "nettype", "linelocal",
                    "local");
            Log.e("linelocal", "linelocal:" + linelocal);
            if (linelocal.equals("local")) {
				/* 设置mserver的为线上和本地服务器 */
                Constant.requestUrl(ip);
                Constant.serverUrl(ip);
            } else {
                // Constant.requestUrl的值为线上、
                Constant.requestUrl = "http://s.tingchebao.com/zld/";
//                Constant.requestUrl = "http://yxiudongyeahnet.vicp.cc:50803/zld/";
                Constant.serverUrl = "http://s.tingchebao.com/mserver/";

                // 老姚测试
                // Constant.requestUrl = "http://192.168.199.240/zld/";
                // Constant.serverUrl = "http://192.168.199.240/mserver/";

                // haixiang测试
                // Constant.requestUrl = "http://192.168.199.239/zld/";
                // Constant.serverUrl = "http://192.168.199.239/mserver/";

                // //荣辉测试
                // Constant.requestUrl = "http://192.168.199.156:8088/zld/";
                // Constant.serverUrl = "http://192.168.199.156:8088/mserver/";

                // 预上线

                // Constant.requestUrl = "http://180.150.188.224:8080/zld/";
                // Constant.serverUrl = "http://180.150.188.224:8080/mserver/";
            }
            Constant.pingUrl(ip);
            Log.e("linelocal", "Constant.requestUrl:" + Constant.requestUrl + "===linelocal:" + linelocal);
            Log.e("linelocal", "Constant.serverUrl:" + Constant.serverUrl);

			/* 保存输入的ip */
            SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "localip", ip);
        } else {// 输入ip为null
			/* 需要同步订单,开启Service */
            Constant.requestUrl = "http://s.tingchebao.com/zld/";
//            Constant.requestUrl = "http://yxiudongyeahnet.vicp.cc:50803/zld/";
            Constant.serverUrl = "http://s.tingchebao.com/mserver/";
            SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "isLocalServer", false);
            Log.e("linelocal", "设置isLocalServer为false");
        }

		/*
		 * // 无网 本地化模式或10秒超时
		 *  if (SharedPreferencesUtils.getParam(
		 * LoginActivity.this,"nettype", "isLocal", false)) {
		 * loginTimeOut(username, password); return; }
		 */
        String worksiteId = SharedPreferencesUtils.getParam(LoginActivity.this, "set_workStation",
                "workstation_id", "");
        RequestParams params = new RequestParams();
        params.setUrlHeader(Constant.requestUrl + Constant.LOGIN);
        params.setUrlParams("username", username);
        params.setUrlParams("password", MD5password);
        params.setUrlParams("version", getVersions());
        params.setUrlParams("worksite_id", worksiteId);
        String url = params.getRequstUrl();
        Log.e(TAG, "登录的URL---------------->>" + url);
        loginurl = url;
        loginname = username;
        loginpass = password;
        if (IsNetWork.IsHaveInternet(LoginActivity.this)) {
//			Log.e(TAG, "登录的URL---------------->>开始请求");
//			Looper.prepare();
//			dialog.show();
//			Looper.loop();
//			Log.e(TAG, "登录的URL---------------->>显示dialog");
            HttpManager.requestLoginGET(LoginActivity.this, url, LoginActivity.this, username, password);
        } else {
//			Log.e(TAG, "登录的URL---------------->>发handler");
//			Looper.prepare();
            loginHandler = new LoginHandler(LoginActivity.this);
            Message m = new Message();
            m.what = 831;
            m.obj = "nonet";
//            loginH.sendMessage(m);
            loginHandler.sendMessage(m);
            Toast.makeText(this, "请先检查网络!", Toast.LENGTH_SHORT).show();
//			Looper.loop();
        }
    }
    private static String loginurl, loginname, loginpass;
//    private Handler loginH = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//            Log.e("---","handler 831 消息"+ (i++));
//            if (msg.what == 831) {
//                if (msg.obj.toString().equals("net")) {
//                    HttpManager.requestLoginGET(LoginActivity.this, loginurl, LoginActivity.this, loginname, loginpass);
//                } else {
//                    Message m = new Message();
//                    m.what = 831;
//                    if (IsNetWork.IsHaveInternet(LoginActivity.this)) {
//                        m.obj = "net";
//                    } else {
//                        m.obj = "nonet";
//                    }
//                    loginH.removeMessages(831);
//                    loginH.sendMessageDelayed(m, 5000);
//                }
//
//            } else if (msg.what == 832) {
////				loginOperation();
//            }
//        }
//
//    };
    static int i;
    private static LoginHandler loginHandler;
    private static class LoginHandler extends Handler{
        WeakReference<LoginActivity> weakReference;
        LoginHandler(LoginActivity activity){
            weakReference = new WeakReference<LoginActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Log.e("---","handler 831 消息"+ (i++));
            LoginActivity login = weakReference.get();
            if (msg.what == 831) {
                if (msg.obj.toString().equals("net")) {
                    HttpManager.requestLoginGET(login, loginurl, login, loginname, loginpass);
                } else {
                    Message m = new Message();
                    m.what = 831;
                    if (IsNetWork.IsHaveInternet(login)) {
                        m.obj = "net";
                    } else {
                        m.obj = "nonet";
                    }
                    loginHandler.removeMessages(831);
                    loginHandler.sendMessageDelayed(m, 5000);
                }
            }
        }
    }

    // private void loginTimeOut(final String username, final String password) {
    // // 查询数据库中是否有账号 和密码, 有的话则跳转、无的话 提示
    // // showToast("登录超时开启无网本地化模式");
    // SmAccount selectAccount = sqliteManager.selectUsername(username);
    // Log.e(TAG,"账户："+username+" selectAccount:"+selectAccount);
    // if(selectAccount != null){
    // Log.e(TAG,"输入的账户："+username+"=获取保存的密码："+selectAccount.getPassword()+"=输入的密码："+password);
    // if(selectAccount.getPassword()!=null&&password.equals(selectAccount.getPassword())){
    // String account = selectAccount.getAccount();
    // setCominfo(username, account);
    // }
    // }else{
    // showToast("本地化模式本地无此账号和密码");
    // }
    // }

    /**
     * 设置车位信息并跳转到主界面
     *
     */
    private void setCominfo(final String username, String account) {
        if (username != null) {
            AppInfo.getInstance().setUid(username);
        }
        if (account != null) {
            SharedPreferencesUtils.setParam(LoginActivity.this, "userinfo", "name", account);
        }
        String comid = SharedPreferencesUtils.getParam(LoginActivity.this, "zld_config", "comid", null);
        boolean passfree = SharedPreferencesUtils.getParam(LoginActivity.this, "zld_config", "passfree", true);
        // String issuplocal = SharedPreferencesUtils.getParam(
        // LoginActivity.this, "zld_config", "issuplocal", null);
        // AppInfo.getInstance().setIssuplocal(issuplocal); //为1支持本地化
        Log.e(TAG, "comid是：" + comid);
        AppInfo.getInstance().setPassfree(passfree);
        AppInfo.getInstance().setComid(comid);
        intentInfo();
    }

    /**
     * 之前的从没有过登录账号,或登录的账号跟之前的不一致,则保存登录时间,便于查询交接班金额信息
     */
    private void saveLongOnTime(final String username, LoginInfo info) {
        String beforeUser = SharedPreferencesUtils.getParam(LoginActivity.this, "autologin", "account", "");
        if (TextUtils.isEmpty(beforeUser) || !beforeUser.equals(username)) {
            SharedPreferencesUtils.setParam(LoginActivity.this, "autologin", "logontime", info.getLogontime());
        }
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setInfo(LoginInfo info) {
        this.info = info;
    }

    /**
     * 登录选择
     *
     */
    @SuppressWarnings("static-access")
    private void loginChoose(final String username, final String password, final Set<String> users, LoginInfo info) {
        String encode = null;
        try {
            encode = new StringDesUtils().encrypt(password);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        SharedPreferencesUtils.setParam(LoginActivity.this, "userinfo", "username", encode);
        // 自动账号提示记录
        if (!users.contains(username)) {
            if (users != null) {
                Log.e(TAG, "要插入的set集合数据为---" + users.toString());
                SharedPreferences sp1 = getSharedPreferences("usernames", Context.MODE_PRIVATE);
                Editor edit = sp1.edit();
                users.add(username);
                SharedPreferencesUtils.putStringSet(edit, "usernames", users).commit();
            }
        }
        SharedPreferences firstSetSPF = LoginActivity.this.getSharedPreferences("set_workStation",
                Context.MODE_PRIVATE);
        boolean is_first = firstSetSPF.getBoolean("is_first", true);
        if (is_first) {
            SharedPreferencesUtils.setParam(LoginActivity.this, "cameraParam", "auto", true);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "showsoftkeyboard", true);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "showfuzysearch", true);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "isshowmonthcard", true);
            // 如果是第一次登录,清空掉保存的工作站名称,
            // 保存的工作站名称为了在主界面,点击更多，选择同一个工作站时，不做操作
            SharedPreferencesUtils spu = new SharedPreferencesUtils(LoginActivity.this, "set_workStation");
            spu.delete(LoginActivity.this, "set_workStation", "staname");
            Intent intent = new Intent(LoginActivity.this, ChooseWorkstationActivity.class);
            intent.putExtra("from", "login");
            isLogin = true;
            startActivity(intent);
			/* 加finish是为了不让第一次进入应用点击下班会重启 */
            finish();
//            AnimSlide();
        } else {// 选择过工作站之后 应该是走这里
            /** 数据库存在通道信息,则先删除,再获取 */
            Log.e(TAG, "获取通道信息");
            ArrayList<MyCameraInfo> selectCamera = sqliteManager.selectCamera(SqliteManager.PASSTYPE_ALL);
            if (selectCamera.size() > 0) {
                sqliteManager.deleteCameraData();
                sqliteManager.deleteLedData();
            }
            String worksiteId = SharedPreferencesUtils.getParam(LoginActivity.this, "set_workStation",
                    "workstation_id", "");
            getWorksiteAllInfo(worksiteId);
        }
    }

    /**
     * 获取当前应用程序的版本号
     *
     * @return
     */
    private String getVersions() {
        try {
            PackageManager manager = getPackageManager();
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            return String.valueOf(info.versionName);
        } catch (Exception e) {
            e.printStackTrace();
            return "版本号未知";
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
        }
        if(loginHandler!=null){
            loginHandler.removeMessages(831);
        }
//		if(loginTime!=null){
//			loginTime.cancel();
//		}
    }

    /**
     * 获取车场信息
     *
     * @param info2
     * @param password2
     * @param username2
     */
    public void getParkingInfo(String username2, String password2, LoginInfo info2) {
        RequestParams params = new RequestParams();
        params.setUrlHeader(Constant.requestUrl + Constant.COMINFO);
        params.setUrlParams("token", AppInfo.getInstance().getToken());
        params.setUrlParams("out", "json");
        String url = params.getRequstUrl();
        Log.e(TAG, "获取车场信息url---------------->>" + url);
//		DialogManager.getInstance().showProgressDialog(this, "获取车场信息...");
        HttpManager.requestCominfoGET(this, url, this, username2, password2, info2);
    }

    @Override
    public boolean doSucess(String url, String object) {
        // TODO Auto-generated method stub
        DialogManager.getInstance().dissMissProgressDialog();
        Log.e(TAG, "doSucess---------------->>" + url);
        if (url.contains(Constant.WORKINFO)) {
            doGetWorkInfoResult(object);
        }
        return true;
    }

    @Override
    public boolean doSucess(String url, String object, String username, String password, LoginInfo info) {
        // TODO Auto-generated method stub
        DialogManager.getInstance().dissMissProgressDialog();
        if (url.contains(Constant.COMINFO)) {
            Log.e(TAG, "获取车场信息为：" + Constant.COMINFO + "---------------->>" + object);
            doGetParkInfoResult(object, username, password, info);
        }
        return true;
    }

    @Override
    public boolean doSucess(String url, byte[] buffer, String username, String password) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        if (url.contains(Constant.LOGIN)) {
            Log.e("linelocal", "登录成功获取isLocalServer为：" + AppInfo.getInstance().getIsLocalServer(LoginActivity.this));
            if (AppInfo.getInstance().getIsLocalServer(LoginActivity.this)) {
                if (url.contains(Constant.LINE_LOCAL)) {
                    Log.e("linelocal", "保存linelocal为：line");
                    // 保存状态值为line
                    SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "linelocal", "line");
                } else {
                    Log.e("linelocal", "保存linelocal为：local");
                    // 保存状态值为local
                    SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "linelocal", "local");
                }
            } else {
                SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "isLocal", false);
                Log.e("isLocal", "LoginActivity doSucess set isLocal false");
            }
            doLoginResult(buffer, username, password);
        }
        return true;
    }

    @Override
    public void timeout(String url) {
        // TODO Auto-generated method stub
        Log.e(TAG, "网络请求超时：" + url);
        DialogManager.getInstance().dissMissProgressDialog();
        if (url.contains(Constant.COMINFO) || url.contains(Constant.WORKINFO)) {
            setCominfo(null, null);
        }
        super.timeout(url);
    }

    @Override
    public void timeout(String url, String username, String password) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        if (url.contains(Constant.LOGIN)) {
            Log.e("linelocal", "Constant.LOGIN超时:" + !AppInfo.getInstance().getIsLocalServer(LoginActivity.this));
            // 是否是本地服务器
            if (AppInfo.getInstance().getIsLocalServer(LoginActivity.this)) {// 是本地服务器则没有平板本地化的感念
                // SharedPreferencesUtils.setParam(
                // LoginActivity.this, "nettype", "isLocal", true);
                // Log.e("isLocal","LoginActivity timeout set isLocal true");
                // Log.e(TAG,"登录超时isLocal为true");
                // loginTimeOut(username, password);
                if (url.contains(Constant.LINE_LOCAL)) {
                    Log.e("ronghui", "登陆线上失败");
                    // 保存状态值为line
                    SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "linelocal", "local");
                    String ip = et_ip.getText().toString().trim();
                    Constant.requestUrl(ip);
                    Constant.serverUrl(ip);
                } else {
                    Log.e("ronghui", "登陆线下失败");
                    // 保存状态值为local
                    SharedPreferencesUtils.setParam(LoginActivity.this, "nettype", "linelocal", "line");
                    Constant.requestUrl = "http://s.tingchebao.com/zld/";
                    Constant.serverUrl = "http://s.tingchebao.com/mserver/";
                }
            }
            loginOperation();
            // else{
            // Log.e("linelocal", "Constant.LOGIN本地登录超时,保存状态值为Line");
            // //本地登录超时,保存状态值为Line
            // SharedPreferencesUtils.setParam(LoginActivity.this,"nettype",
            // "linelocal", "line");
            // Constant.requestUrl = "http://s.tingchebao.com/zld/";
            // Constant.serverUrl = "http://s.tingchebao.com/mserver/";
            // //再重新登录一次
            // loginOperation();
            // }
        }
        super.timeout(url);
    }

    private void doLoginResult(byte[] buffer, String username, String password) {
        // TODO Auto-generated method stub
        dialog.dismiss();
        if (buffer != null) {
            try {
                Log.e(TAG, "登陆的返回信息是---" + new String(buffer, "utf-8"));
                InputStream is = new ByteArrayInputStream(buffer);
                LoginInfo info = LoginInfoParser.getLoginInfo(is);
                if (info != null) {
                    if (null != info.getState() && info.getState().equals(Constant.sOne)) {
                        showToast("此收费员已经被删除");
                        SharedPreferencesUtils.delete(LoginActivity.this, "autologin", info.getName());
                        sqliteManager.deleteAccountData(info.getName());
                        return;
                    }
                    Log.e(TAG, "解析登录信息：" + info.toString());
                    if (info.getInfo().equals("success")) {
                        String token = info.getToken();
                        AppInfo.getInstance().setToken(token);
                        AppInfo.getInstance().setUid(username);
                        AppInfo.getInstance().setName(info.getName());
						/* 保存登录时间 */
                        saveLongOnTime(username, info);
                        SmAccount selectAccount = sqliteManager.selectAccount(info.getName());
                        if (selectAccount == null) {
							/* 未登录的账号则保存 */
                            sqliteManager.insertAccountData(info.getName(), username, password);
                        }
                        SharedPreferencesUtils.setParam(LoginActivity.this, "autologin", "account", username);
                        SharedPreferencesUtils.setParam(LoginActivity.this, "autologin", "passwd", password);
                        SharedPreferencesUtils.setParam(LoginActivity.this, "autologin", "name", info.getName());
                        SharedPreferencesUtils.setParam(LoginActivity.this, "autologin", "role", info.getRole());
                        SharedPreferencesUtils.setParam(LoginActivity.this, "userinfo", "name", info.getName());
                        getParkingInfo(username, password, info);
                        this.username = username;
                        this.password = password;
                        this.info = info;
                    } else {
                        Looper.prepare();
                        new AlertDialog.Builder(LoginActivity.this).setIcon(R.drawable.app_icon_32).setTitle("提示")
                                .setMessage("" + info.getInfo()).setPositiveButton("确定", null).create().show();
                        // showToast(""+info.getInfo());
                        Looper.loop();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            dialog.dismiss();
            return;
        }
    }

    /**
     * 车场信息结果
     *
     * @param info
     * @param password
     * @param username
     */
    private void doGetParkInfoResult(String object, String username, String password, LoginInfo info) {
        Gson gson = new Gson();
        ParkingInfo parkingInfo = gson.fromJson(object, ParkingInfo.class);
        if (parkingInfo != null) {
            Log.e(TAG, "解析的车场信息为:" + parkingInfo.toString());
            String comid = parkingInfo.getId();
            int passfree = parkingInfo.getPassfree(); // 是否允许免费结算订单
            String parkBilling = parkingInfo.getCar_type(); // 车场大小车计费
            String issuplocal = parkingInfo.getIssuplocal(); // 是否支持本地化
            String ishidehdbutton = parkingInfo.getIshidehdbutton();// 是否显示结算订单
            List<LiftReason> liftreason = parkingInfo.getLiftReason(); // 抬杆原因
            List<CarType> allcarTypes = parkingInfo.getAllCarTypes();
            List<FreeResons> freeResons = parkingInfo.getFreeResons();
            String ishdmoney = parkingInfo.getIsShowMoney();
            String fullset = parkingInfo.getFullset(); // 车位满进场设置
            String leaveset = parkingInfo.getLeaveset(); // 出场设置（是非需要收费）
            AppInfo appInfo = AppInfo.getInstance();
            appInfo.setParkName(parkingInfo.getName());
            appInfo.setComid(comid);
            appInfo.setIsShowhdmoney(TextUtils.isEmpty(ishdmoney) ? true : ishdmoney.endsWith(Constant.sOne));
            appInfo.setPassfree(passfree == 0); // 为0允许免费
            appInfo.setIssuplocal(issuplocal); // 为1支持本地化

            appInfo.setIshidehdbutton(ishidehdbutton);// 为1显示结算订单按钮
            appInfo.setParkBilling(parkBilling.equals(Constant.sOne));// 为1区分大小车
            appInfo.setAllCarTypes(allcarTypes);
            appInfo.setLiftreason(liftreason);
            appInfo.setFreeResons(freeResons);
            appInfo.setFullset(fullset);
            appInfo.setLeaveset(leaveset);
            Log.e(TAG, "appinfo:" + appInfo.toString());
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "comid", comid);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "passfree", (passfree == 0));
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "issuplocal", issuplocal);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "parkBilling",
                    parkBilling.equals(Constant.sOne));
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "fullset", fullset);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "leaveset", leaveset);
            SharedPreferencesUtils.setParam(LoginActivity.this, "zld_config", "ishidehdbutton", ishidehdbutton);

            if (username != null && password != null && info != null) {
                // getlog
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        List list = ImageUitls.getLOGO();// (bitmap,
                        // FileUtil.getSDCardPath()+"/tcb/log.jpg");
                        if (list == null) {
                            getLogImage();
                        }
                    }
                }).start();
                loginChoose(username, password, users, info);
            }
        }
    }

    public void getLog() {

    }

    /**
     * 从网络上获取对应订单的图片；
     */
    public void getLogImage() {
        RequestParams params = new RequestParams();
        params.setUrlHeader(Constant.requestUrl + Constant.DOWNLOAD_LOGO_IMAGE);
        params.setUrlParams("comid", AppInfo.getInstance().getComid());
        params.setUrlParams("orderid", 0);
        params.setUrlParams("type", 0);
        String uri = params.getRequstUrl();
        Log.e(TAG, "照片的uri地址是-->>" + uri);
        // 保存图片，保存图片信息
        try {
            ImageUitls.getBitmapAndSave(uri);
            // bitmap = ImageUitls.getBitmap(uri);
            // ImageUitls.saveFrameToPath(bitmap,
            // FileUtil.getSDCardPath()+"/tcb/logo.jpg");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void showPopupWindow(View parent) {
        int screenHeight;
        // TODO Auto-generated method stub
        if (listview == null) {
            listview = (ListView) LayoutInflater.from(this).inflate(R.layout.account_droplist, null);
        }
        final ArrayList<String> selectAllAccount = sqliteManager.selectAllAccount();
        if (selectAllAccount.size() != 0) {
            AccountDropListAdapter adapter = new AccountDropListAdapter(LoginActivity.this, selectAllAccount, false);
            listview.setAdapter(adapter);
        }
        if (popupWindow == null) {
            popupWindow = new PopupWindow(listview, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            DisplayMetrics dm = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(dm);
            int screenWidth = dm.widthPixels;
            screenHeight = dm.heightPixels;
            popupWindow.setWidth((int) (screenWidth / 4));
            popupWindow.setHeight(screenHeight / 3);
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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // TODO Auto-generated method stub
                SmAccount selectAccount = sqliteManager.selectAccount(selectAllAccount.get(position));
                if (selectAccount != null) {
                    Log.e(TAG,
                            "数据库中保存的用户信息：" + selectAccount.getUsername() + "---123456:" + selectAccount.getPassword());
                    autologin(selectAccount.getUsername(), selectAccount.getPassword());
                }
            }
        });
    }

    /**
     * 监听键盘Enter键
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_ENTER:
                // Enter登录
                loginOperation();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 安装下载后的apk文件,应该提示立即安装,下次进入安装
     *
     * @param fromFile
     */
    private void Instanll(Uri fromFile) {
        System.out.println("执行安装程序");
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(fromFile, "application/vnd.android.package-archive");
        startActivity(intent);
    }

    private void intentInfo() {
        Intent intent = new Intent(this, ZldNewActivity.class);
        isLogin = true;
        this.startActivity(intent);
        finish();
//        AnimSlide();
    }

    /**
     * 获取工作站下的所有摄像头和LED信息
     */
    public void getWorksiteAllInfo(String worksiteId) {
        RequestParams params = new RequestParams();
        params.setUrlHeader(Constant.requestUrl + Constant.WORKINFO);
        params.setUrlParams("worksite_id", worksiteId);// 工作站id
        params.setUrlParams("comid", AppInfo.getInstance().getComid());
        String url = params.getRequstUrl();
        Log.e(TAG, "获取工作站下的所有摄像头和LED信息url---------------->>" + url);
        HttpManager.requestGET(this, url, this);
    }

    /**
     * 工作站下的信息结果
     */
    private void doGetWorkInfoResult(String object) {
        if (object.equals("[]") || object.equals("-1")) {
            SharedPreferences firstSetSPF = LoginActivity.this.getSharedPreferences("set_workStation",
                    Context.MODE_PRIVATE);
            firstSetSPF.edit().putBoolean("is_first", true).commit();
            Log.e(TAG, "清空数据");
            return;
        }
        Gson gson = new Gson();
        Type typeToken = new TypeToken<List<WorkStationDevice>>() {
        }.getType();
        ArrayList<WorkStationDevice> workStationDevices = gson.fromJson(object, typeToken);

        if (workStationDevices != null && workStationDevices.size() != 0) {
            Log.e(TAG, "解析的工作站下的所有信息为" + workStationDevices.toString());
            for (int i = 0; i < workStationDevices.size(); i++) {
                WorkStationDevice workStationDevice = workStationDevices.get(i);
                String passtype = workStationDevice.getPasstype();
                String passname = workStationDevice.getPassname();
                MyCameraInfo[] cameras = workStationDevice.getCameras();
                MyLedInfo[] leds = workStationDevice.getLeds();
                if (cameras != null && cameras.length != 0) {
                    for (int j = 0; j < cameras.length; j++) {
                        MyCameraInfo cameraInfo = cameras[j];
                        cameraInfo.setPasstype(passtype);
                        cameraInfo.setPassname(passname);
                        sqliteManager.insertCameraData(cameras[j]);
                    }
                }
                if (leds != null && leds.length != 0) {
                    for (int k = 0; k < leds.length; k++) {
                        MyLedInfo ledInfo = leds[k];
                        ledInfo.setPasstype(passtype);
                        ledInfo.setPassname(passname);
                        sqliteManager.insertLedData(leds[k]);
                    }
                }
            }
            intentInfo();
        }
    }
}

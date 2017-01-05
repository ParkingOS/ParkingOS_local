package com.zld.ui;

import com.networkbench.agent.impl.NBSAppAgent;
import com.umeng.analytics.MobclickAgent;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.LoginInfo;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpCallBack;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.InputUtil;
import com.zld.lib.util.IsNetWork;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;
//import com.zld.local.db.LocalOrderDBManager;

public class BaseActivity extends FragmentActivity implements HttpCallBack{
	
	private Toast mToast;
	private String equipmentModel;
//	private LocalOrderDBManager loDBManager;
	private static final String TAG = "BaseActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);	
		((application) getApplication()).setBaseActivity(this);
		MobclickAgent.setDebugMode(true);
		MobclickAgent.updateOnlineConfig(BaseActivity.this);
		//注册听云服务
		NBSAppAgent.setLicenseKey(getResources().getString(R.string.tingyun_id)).withLocationServiceEnabled(true).start(this);
//		initIssUpLocal();
//		getLocalOrderDBManager();
	}

	public void changeEquipment(EditText edit,boolean boo) {
		if(equipmentModel == null){
			equipmentModel = AppInfoUtil.getEquipmentModel();
		}
		if(!equipmentModel.equals("MI PAD")){//小米平板
			Log.e(TAG, "电视棒");
			//编辑框屏蔽输入法
			getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
			InputUtil.hideTypewriting(this, edit);
			if(boo){
				edit.setFocusable(false);
			}
		}
		//		Log.e(TAG, "小米平板");
		//		if(equipmentModel.equals("rk31sdk")||equipmentModel.equals("rk3188")||equipmentModel.equals("MI PAD")){//为电视棒MI PAD==rk31sdk
		//		}else 
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void finish() {
		super.finish();
		MobclickAgent.onKillProcess(this);
	}

	public void showToast(String text) {
		if (mToast == null) {
			mToast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
			mToast.setDuration(Toast.LENGTH_SHORT);
		}
		mToast.show();
	}

	/**
	 * collectorrequest.do?action=invalidorders&invalid_order=-1
	 * &token=198f697eb27de5515e91a70d1f64cec7
	 * 空闲车位数加减1
	 */
	public void changeFreePark(String path,String value) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl +Constant.CHANG_INVALIDORDER);
		params.setUrlParams("invalid_order", value);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		String url = params.getRequstUrl();
		Log.e(TAG, "空闲车位数差值加减1状态-------------->>" + url);
		HttpManager.requestGET(this,url,this);
	}

//	/**
//	 * 初始化本地订单数据库管理
//	 */
//	protected LocalOrderDBManager getLocalOrderDBManager() {
//		// TODO Auto-generated method stub
//		if(loDBManager == null){
//			loDBManager = ((application) getApplication()).
//					getLocalOrderDBManager(this);
//		}
//		return loDBManager;
//	}

	public void isInternet(Context context) {
		if (!IsNetWork.IsHaveInternet(context)) {
//			ShowDialog.buildeChooseDialog((Activity)context, "当前网络不可用", "停车宝提示");
			return;
		}
	}

	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see com.zld.lib.http.HttpCallBack#doSucess(java.lang.String, boolean, java.lang.String, java.lang.String, int, java.lang.String)
	 */
	@Override
	public boolean doSucess(String url, boolean isSingle, String passid,
			String object, int i, String object2) {
		// TODO Auto-generated method stub
		Log.e(TAG, "doSucess---------------->>" + url);
		if (url.contains(Constant.CHANG_INVALIDORDER)){
			Log.e(TAG, "空闲车位数差值加减1状态为："+Constant.CHANG_INVALIDORDER+"---------->>" + object);
			if(object.equals("1")) {
				Log.e(TAG, "空闲车位数差值修改成功");
			}
		}
		return false;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean doSucess(String url, String object, String str) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, String str1, String str2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean doSucess(String url, String object, byte[] buffer) {
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
	public boolean doSucess(String url, String object, String username,
			String password, LoginInfo info) {
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
	public boolean doSucess(String requestUrl, byte[] buffer, String username2,
			String password2) {
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
		
	}
	
//	private void initIssUpLocal() {
//		// TODO Auto-generated method stub
//		if(issuplocal == null){
//			issuplocal = SharedPreferencesUtils.getParam(
//					this.getApplicationContext(), "zld_config", "issuplocal", "");
//			Log.e("isLocal","BaseActivity initIssUpLocal get issuplocal "+issuplocal);
//		}
//	}
}


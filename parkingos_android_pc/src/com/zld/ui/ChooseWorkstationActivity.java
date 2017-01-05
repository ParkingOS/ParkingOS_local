package com.zld.ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.EnterClose;
import com.zld.bean.MyCameraInfo;
import com.zld.bean.MyLedInfo;
import com.zld.bean.WifiAdmin;
import com.zld.bean.WorkStation;
import com.zld.bean.WorkStationDevice;
import com.zld.db.SqliteManager;
import com.zld.lib.constant.Constant;
import com.zld.lib.dialog.DialogManager;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.SharedPreferencesUtils;
//import com.zld.photo.DecodeManager;
import com.zld.service.HomeExitPageService;
import com.zld.view.WorkStationPicker;
import com.zld.view.WorkStationPicker.OnWorkStationChangedListener;
/**
 * 设置工作站
 * @author HZC
 *
 */
public class ChooseWorkstationActivity extends BaseActivity{

	private static final String TAG = "ChooseWorkstationActivity";
	private TextView tv_close, tv_pole_worktype, tv_softkeyboard, tv_fuzy_search,tv_month_card;
	private CheckBox cb_pole_worktype, cb_softkeyboard, cb_fuzy_search,cb_month_card;
	private EditText et_wifi,et_confidence_level;
	private RelativeLayout rl_pole_worktype, rl_softkeyboard, rl_fuzy_search,rl_month_card;
	private WorkStationPicker np_work;
	private Button btn_into_background;
	private Button btn_yes;
	private int id;
	private String value;
	private String token;
	private String comid;
	/*如果有其他不同类型的通道，则passItem为通道集合中的第几个*/
	private int passItem = 0;
	private String intentStaname;
	private String preActivityName;
	private String workStation[] = null;
	private List<EnterClose> enterCloseList;
	private ArrayList<WorkStation> workStations;

	private WifiAdmin wifiAdmin;
	private boolean showMonthCard;
	private String configurateSsid;
	private SqliteManager sqliteManager;
	private List<ScanResult> scanResultList;
	private BroadcastReceiver wifiConnectReceiver;
	private List<WifiConfiguration> configuratedList;
	/*当前连接的wifi*/
	private WifiInfo currentWifiInfo;
	/*判断是否连接上制定的wifi*/
	private boolean isConnected = false;
	private int networkId;
	/*表示是否在可连接范围内*/
	private boolean canConnectable = false;
	/*表示wifi是否已经配置过了*/
	private boolean isConfigurated = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 设置无标题
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
		super.onCreate(savedInstanceState);
		setContentView(R.layout.more_set_workstation);
		initView();
		onClickEvent();
		initSqliteManager();
		token = AppInfo.getInstance().getToken();
		comid = AppInfo.getInstance().getComid();	
		super.changeEquipment(et_confidence_level,false);
		initIntentStaname();
		initWifi();
	}

	private void initView() {
		// TODO Auto-generated method stub
		tv_close = (TextView) findViewById(R.id.tv_close);
		tv_pole_worktype = (TextView) findViewById(R.id.tv_pole_worktype);
		tv_softkeyboard = (TextView) findViewById(R.id.tv_softkeyboard);
		tv_month_card = (TextView) findViewById(R.id.tv_month_card);
		tv_fuzy_search = (TextView) findViewById(R.id.tv_fuzy_search);
		np_work = (WorkStationPicker) findViewById(R.id.np_work);
		btn_into_background = (Button) findViewById(R.id.btn_into_background);
		btn_yes = (Button) findViewById(R.id.btn_yes);
		rl_pole_worktype = (RelativeLayout) findViewById(R.id.rl_pole_worktype);
		rl_softkeyboard = (RelativeLayout) findViewById(R.id.rl_softkeyboard);
		rl_fuzy_search = (RelativeLayout) findViewById(R.id.rl_fuzy_search);
		rl_month_card = (RelativeLayout) findViewById(R.id.rl_month_card);
		cb_pole_worktype = (CheckBox) findViewById(R.id.cb_pole_worktype);
		cb_softkeyboard = (CheckBox) findViewById(R.id.cb_softkeyboard);
		cb_fuzy_search = (CheckBox) findViewById(R.id.cb_fuzy_search);
		cb_month_card = (CheckBox) findViewById(R.id.cb_month_card);
		et_confidence_level = (EditText) findViewById(R.id.et_confidence_level);
		et_wifi = (EditText) findViewById(R.id.et_wifi);
		/*获取上次保存的置信度,设置置信度*/
		int confidence_level = SharedPreferencesUtils.getParam(getApplicationContext(), "cameraParam", "confidenceLevel",65);
		//设置出口的置信度
//		DecodeManager.getinstance().setConfidenceLevel(confidence_level);
		//设置入口的置信度
		setInCameraLevel(confidence_level);
		
//		et_confidence_level.setText(String.valueOf(DecodeManager.getinstance().getConfidenceLevel()));
		if(getWifiName() != null){
			et_wifi.setText(getWifiName().replace("\"", ""));
		}
		if (preActivityName != null && preActivityName.equals("ExitPageActivity")){
			rl_pole_worktype.setVisibility(View.GONE);
		}
		if (isPoleAutoWorking()){
			cb_pole_worktype.setChecked(true);
			tv_pole_worktype.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_pole_worktype.setChecked(false);
		}
		if (isShowSoftKeyBoard()){
			cb_softkeyboard.setChecked(true);
			tv_softkeyboard.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_softkeyboard.setChecked(false);
		}
		if (isShowFuzySearch()){
			cb_fuzy_search.setChecked(true);
			tv_fuzy_search.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_fuzy_search.setChecked(false);
		}
		showMonthCard = isShowMonthCard();
		if (showMonthCard){
			cb_month_card.setChecked(true);
			tv_month_card.setTextColor(getResources().getColor(R.color.occupy_red));
		}else {
			cb_month_card.setChecked(false);
		}
	}

	private void onClickEvent() {
		// TODO Auto-generated method stub
		//关闭
		tv_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ChooseWorkstationActivity.this.finish();
			}
		});
		//工作站Numpicker
		np_work.setOnWorkStationChangedListener(new OnWorkStationChangedListener() {

			@Override
			public void onWorkStationChanged(String value,int id) {
				// TODO Auto-generated method stub
				ChooseWorkstationActivity.this.value = value;
				ChooseWorkstationActivity.this.id = id;
			}
		});
		//进入后台
		btn_into_background.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//判断是否为管理员账号，是，跳转至管理员的WebView；否，提示框；
				ChooseWorkstationActivity.this.startActivity(
						new Intent(ChooseWorkstationActivity.this,BackgroundActivity.class));
			}
		});
		//确认
		btn_yes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//设置车牌识别的置信度
				if (!TextUtils.isEmpty(et_confidence_level.getText().toString())){
					SharedPreferencesUtils.setParam(getApplicationContext(), "cameraParam", "confidenceLevel", Integer.parseInt(et_confidence_level.getText().toString()));
					//设置出口的置信度
//					DecodeManager.getinstance().setConfidenceLevel(Integer.parseInt(et_confidence_level.getText().toString()));
					//设置入口的置信度
					setInCameraLevel(Integer.parseInt(et_confidence_level.getText().toString()));					
				}
				if (cb_pole_worktype.isChecked()){
					SharedPreferencesUtils.setParam(getApplicationContext(), "cameraParam", "auto", true);
				}else {
					SharedPreferencesUtils.setParam(getApplicationContext(), "cameraParam", "auto", false);
				}
				if (cb_softkeyboard.isChecked()){
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showsoftkeyboard", true);
				}else {
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showsoftkeyboard", false);
				}
				if (cb_fuzy_search.isChecked()){
//					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showfuzysearch", true);
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "yessir", true);
				}else {
//					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "showfuzysearch", false);
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "yessir", false);
				}
				if (cb_month_card.isChecked()){
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isshowmonthcard", true);
				}else {
					SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isshowmonthcard", false);
				}
				//设置连接指定wifi
				if (!TextUtils.isEmpty(et_wifi.getText().toString())){
					String wifiName = et_wifi.getText().toString().trim();
					connectWifi(wifiName);
				}
				String param = SharedPreferencesUtils.getParam(getApplicationContext(),
						"set_workStation", "staname", "工作站");
				if(param.equals(value)){
					if(showMonthCard != cb_month_card.isChecked()){
						SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isupdate", true);
					}else{
						SharedPreferencesUtils.setParam(getApplicationContext(), "zld_config", "isupdate", false);	
					}
					finish();
					return;
				}
				System.out.println("更多设置的自动抬杆："+cb_pole_worktype.isChecked());
				Log.e(TAG, "workStations:"+workStations+"---->>ID:"+id);
				AppInfo.getInstance().setStname(value);
				if(workStations != null){
					if(workStations.size() != 0){
						WorkStation workStation2 = workStations.get(id);
						if(workStation2.getId() != null&&workStation2.getId() != ""){
							getEnterclose(workStation2.getId());
							if(workStation2.getNet_type() !=null){
								SharedPreferencesUtils.setParam(getApplicationContext(),
										"nettype", "netType", workStation2.getNet_type());
								SharedPreferencesUtils.setParam(getApplicationContext(),
										"set_workStation", "workstation_id", workStation2.getId());
							}
						}
					}else{
						showToast("请用管理员账号添加工作站！");
					}
				}else{
					showToast("请检查网络设置！");
					finish();
				}
			}
		});

		rl_pole_worktype.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_pole_worktype.isChecked()){
					cb_pole_worktype.setChecked(false);
					tv_pole_worktype.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_pole_worktype.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_pole_worktype.setChecked(true);					
				}
			}
		});

		rl_softkeyboard.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_softkeyboard.isChecked()){
					cb_softkeyboard.setChecked(false);
					tv_softkeyboard.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_softkeyboard.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_softkeyboard.setChecked(true);					
				}
			}
		});

		rl_fuzy_search.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_fuzy_search.isChecked()){
					cb_fuzy_search.setChecked(false);
					tv_fuzy_search.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_fuzy_search.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_fuzy_search.setChecked(true);					
				}
			}
		});

		rl_month_card.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (cb_month_card.isChecked()){
					cb_month_card.setChecked(false);
					tv_month_card.setTextColor(getResources().getColor(R.color.gray));
				}else{
					tv_month_card.setTextColor(getResources().getColor(R.color.occupy_red));
					cb_month_card.setChecked(true);					
				}
			}
		});
	}
	/**
	 * 设置入口的摄像机的置信度
	 */
	private void setInCameraLevel(int confidence_level){
		Intent intent = new Intent();
		intent.setClass(getApplicationContext(), HomeExitPageService.class);
		Bundle bundle = new Bundle();
		bundle.putInt("confidenceLevel", confidence_level);
		bundle.putString("intentkey", "setConfidenceLevel");
		intent.putExtras(bundle);
		startService(intent);
	}

	private void initSqliteManager() {
		if (sqliteManager == null) {
			sqliteManager = ((application) getApplication())
					.getSqliteManager(ChooseWorkstationActivity.this);
		}
	}

	/**
	 * 获取工作站名称
	 */
	private void initIntentStaname() {
		if(intentStaname == null){
			intentStaname = SharedPreferencesUtils.getParam(
					getApplicationContext(), "set_workStation", "staname", "未知工作站");
		}
	}


	/**
	 * 获取工作站的接口：
	 * http://192.168.199.239/zld/
	 * worksiteinfo.do?comid=0&action=queryworksite
	 */
	public void getWorkStation() {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.QUERY_WORKSITE);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("out", "json");
		String url = params.getRequstUrl();
		Log.e(TAG, "获取工作站信息url---------------->>" + url);
		DialogManager.getInstance()
		.showProgressDialog(this,"获取工作站信息...");
		HttpManager.requestGET(this, url,this);	
	}

	/**
	 * 获取工作站通道的接口：
	 * http://192.168.199.239/zld/worksiteinfo.do?
	 * worksite_id=4&action=querypass&comid=1749
	 */
	public void getEnterclose(final String worksiteId) {
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.QUERY_PASS_INFO);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("worksite_id", worksiteId);
		params.setUrlParams("uid", AppInfo.getInstance().getUid());
		String url = params.getRequstUrl();
		Log.e(TAG, "获取工作站通道URL---------------->>" + url);
		DialogManager.getInstance()
		.showProgressDialog(this,"获取工作站通道...");
		HttpManager.requestGET(this, url,worksiteId,this);	
	}

	/**
	 * 获取工作站下的所有摄像头和LED信息
	 * http://192.168.199.239/zld/worksiteinfo.do?
	 * action=getpassinfo&worksite_id=29&comid=1749
	 */
	public void getWorksiteAllInfo(String worksiteId){
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl +Constant.WORKINFO);
		params.setUrlParams("worksite_id", worksiteId);//工作站id
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		String url = params.getRequstUrl();
		Log.e(TAG, "获取工作站下的所有摄像头和LED信息url---------------->>" + url);
		HttpManager.requestGET(this,url,this);
	}

	@Override
	public boolean doSucess(String url,String object) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		Log.e(TAG, "doSucess---------------->>" + url);
		if (url.contains(Constant.WORKINFO)){
			doGetWorkInfoResult(object);
		}else if(url.contains(Constant.QUERY_WORKSITE)){
			doQueryWorkSite(object);
		}
		return true;
	}
	
	@Override
	public boolean doSucess(String url, String object, String worksiteId) {
		// TODO Auto-generated method stub
		DialogManager.getInstance().dissMissProgressDialog();
		Log.e(TAG, "doSucess---------------->>" + url);
		if(url.contains(Constant.QUERY_PASS_INFO)){
			doQueryPassInfo(object,worksiteId);
		}
		return true;
	}

	/**
	 * 工作站下的信息结果
	 */
	private void doGetWorkInfoResult(String object) {
		Gson gson = new Gson();
		Type typeToken = new TypeToken<List<WorkStationDevice>>() {}.getType();
		ArrayList<WorkStationDevice> workStationDevices = gson.fromJson(object, typeToken);

		if(workStationDevices!=null&&workStationDevices.size()!=0){
			Log.e(TAG, "解析的工作站下的所有信息为"+ workStationDevices.toString());
			for(int i=0;i<workStationDevices.size();i++){
				WorkStationDevice workStationDevice = workStationDevices.get(i);
				String passtype = workStationDevice.getPasstype();
				String passname = workStationDevice.getPassname();
				MyCameraInfo[] cameras = workStationDevice.getCameras();
				MyLedInfo[] leds = workStationDevice.getLeds();
				if(cameras!=null&&cameras.length!=0){
					for(int j=0;j<cameras.length;j++){
						MyCameraInfo cameraInfo = cameras[j];
						cameraInfo.setPasstype(passtype);
						cameraInfo.setPassname(passname);
						sqliteManager.insertCameraData(cameras[j]);
					}
				}
				if(leds!=null&&leds.length!=0){
					for(int k=0;k<leds.length;k++){
						MyLedInfo ledInfo = leds[k];
						ledInfo.setPasstype(passtype);
						ledInfo.setPassname(passname);
						sqliteManager.insertLedData(leds[k]);
					}
				}
			}
			/**保存完信息后,执行跳转操作*/
			startIntent(enterCloseList);
		}
	}

	/**
	 * 获取工作站信息的结果
	 * @param object
	 */
	private void doQueryWorkSite(String object) {
		// TODO Auto-generated method stub
		int index = 0;
		if (object == null) {
			showToast("请重新获取工作站信息");
			return;
		}
			Log.e(TAG,"获取到工作站为"+object);
			Gson gson = new Gson();
			Type type = new TypeToken<List<WorkStation>>() {}.getType();
			workStations = gson.fromJson(object, type);
			workStation = new String[workStations.size()];
			System.out.println("获取到工作站的集合的长度" + workStations.size());
			if (workStations == null || workStations.size() == 0){
				showToast("没有工作站信息！");
			}else{
				Log.e(TAG,"解析到工作站为-->>"+workStations.toString());
				for(int i=0; i<workStations.size();i++){
					System.out.println("********"+ workStations.get(i).getWorksite_name());
					workStation[i] = workStations.get(i).getWorksite_name();
					if (intentStaname != null){
						Log.e(TAG, "workStation[i] + i---->>"+workStation[i] + i);
						if (workStation[i].equals(intentStaname)){
							index = i;
						}
					}
				}
				np_work.setValue(index);
				np_work.setData(0, workStation.length-1, workStation);
				value = workStation[index];
				id = index;												
			}
		}

	/**
	 * 获取对应工作站的通道信息
	 * @param object
	 */
	private void doQueryPassInfo(String object, String worksiteId) {
		// TODO Auto-generated method stub
		Log.e(TAG,"获取到工作站通道为:"+object);
		Gson gson = new Gson();
		Type type = new TypeToken<List<EnterClose>>() {}.getType();
		enterCloseList = gson.fromJson(object, type);
		if (enterCloseList == null || enterCloseList.size() == 0){
			showToast("没有工作站通道信息！");
		}else{
			/*数据库存在通道信息,则先删除,再获取*/
			ArrayList<MyCameraInfo> selectCamera = 
					sqliteManager.selectCamera(SqliteManager.PASSTYPE_ALL);
			if(selectCamera.size()>0){
				sqliteManager.deleteCameraData();
				sqliteManager.deleteLedData();
			}
			getWorksiteAllInfo(worksiteId);
		}
	}
	
	private void startIntent(List<EnterClose>  enterClose) {
		//该工作站下只有一个通道
		if(enterClose != null){
			if(enterClose.size()==1){
				if(enterClose.get(0) != null){
					savePass(enterClose);
				}
				//通道超过至少2个;
			}else if(enterClose.size()>1){
				EnterClose enterClo= enterClose.get(0);
				if(enterClo != null){
					String passtype0 = enterClo.getPasstype();
					boolean twoPassType = isTwoPassType(enterClose, passtype0);//集合中是否有跟第一个通道，不同类型的通道
					Log.e(TAG, "第一个通道类型："+passtype0+"=是否有双通道类型："+twoPassType);
					if(twoPassType){
						saveFirstRecord("出入口", value,enterClo.getId(),enterClose.get(passItem).getId());
					}else{
						savePass(enterClose);
					}
				}
			}
		}
		intentInfo();
	}

	private void intentInfo(){
		/* 如果切换了工作站 */
		//主动关闭掉HomeExitPageService，不关掉底层的算法也关不掉，重启后底层跑着多个进程了就。
		ZldNewActivity zldNewActivity = ((application)getApplication()).getZldNewActivity();
		if(zldNewActivity!=null){
			if(zldNewActivity.titleFragment != null){
				//重启
				zldNewActivity.titleFragment.closeRemotService();
				zldNewActivity.titleFragment.restartApp(this);
			}
		}else{
			Intent intent = new Intent(this,ZldNewActivity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 通道保存
	 * @param intent
	 * @param enterClose
	 * @return
	 */
	private void savePass(List<EnterClose> enterClose) {
		Log.e(TAG,"=="+enterClose.get(0).getPasstype()+"=="+enterClose.get(0).toString());
		if(enterClose.get(0).getPasstype().equals("0")){
			saveFirstRecord("入口", value,enterClose.get(0).getId(),null);

		}else if(enterClose.get(0).getPasstype().equals("1")){
			saveFirstRecord("出口", value,enterClose.get(0).getId(),null);
		}
	}

	/**是否有其他通道*/
	private boolean isTwoPassType(List<EnterClose> enterClose, String passtype0) {
		for(int i = 0; i < enterClose.size(); i++ ){
			String passtype = enterClose.get(i).getPasstype();//获取通道类型
			if(!passtype0.equals(passtype)){
				passItem = i;
				return true;
			}
		}
		return false;
	}

	/**
	 * 保存第一次进入设置工作站的记录
	 */
	private void saveFirstRecord(String workStationType,
			String staname,String passOneId,String passTwoId) {
		Log.e(TAG, staname + " this is saveFirstRexord stanme ");
		SharedPreferences spf = getApplicationContext().
				getSharedPreferences("set_workStation",Context.MODE_PRIVATE);
		Editor spfEdit = spf.edit();
		spfEdit.putBoolean("is_first", false);
		spfEdit.putString("workStationType", workStationType);
		spfEdit.putString("staname", staname);
		spfEdit.putString("passOneId", passOneId);
		spfEdit.putString("passTwoId", passTwoId);
		spfEdit.commit();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(token != null && comid != null){
			getWorkStation();
		}
	}

	private boolean isPoleAutoWorking(){
		return SharedPreferencesUtils.getParam(getApplicationContext(), "cameraParam", "auto", false);
	}

	private boolean isShowSoftKeyBoard(){
		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "showsoftkeyboard", false);
	}

	private boolean isShowFuzySearch(){
//		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "showfuzysearch", false);
		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "yessir", true);
	}
	private boolean isShowMonthCard(){
		return SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "isshowmonthcard", false);
	}
	/**
	 * 初始化wifi操作对象
	 */
	private void initWifi() {
		if(wifiAdmin == null){
			wifiAdmin = new WifiAdmin(this);
		}
		if(wifiConnectReceiver == null){
			wifiConnectReceiver = new WifiConnectReceiver();
		}
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		this.registerReceiver(wifiConnectReceiver, filter);
	}

	/**
	 * 获取当前连接的wifi名
	 * @return
	 */
	private String getWifiName() {
		WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifiMgr.getConnectionInfo();
		String wifiId = info != null ? info.getSSID() : null;
		return wifiId;
	}

	/**
	 * 连接选定的wifi
	 */
	private void connectWifi(String wifiName){
		configuratedList = wifiAdmin.getConfiguratedList();
		scanResultList = wifiAdmin.getScanResultList();
		if (scanResultList != null) {
			for (int i = 0; i < scanResultList.size(); i++) {
				System.out.println("scanResultList" + i + "----->"
						+ scanResultList.get(i).SSID);
				if (scanResultList.get(i).SSID.equals(wifiName)) {
					canConnectable = true;
					break;
				}
			}
			if (canConnectable) {
				if (configuratedList != null) {
					for (int j = 0; j < configuratedList.size(); j++) {
						System.out.println("configuratedList" + j
								+ "------->"+ configuratedList.get(j).SSID);
						configurateSsid = "\"" + wifiName + "\"";
						if (configuratedList.get(j).SSID
								.equals(configurateSsid)) {
							isConfigurated = true;
							networkId = configuratedList.get(j).networkId;
							break;
						}
						// isConfigurated = false;
					}
					System.out.println("%%%%%%%%%%%%%%%%%%%%%%"+ isConfigurated);
					if (isConfigurated) {
						System.out.println("^^^^^^^连接前");
						wifiAdmin.connectWifi(networkId);
						System.out.println("#########连接后");
						System.out.println("isConnected--------->" + isConnected);
						while (!isConnected) {
							System.out
							.println("*************连接上指定wifi*************");
							break;
						}
						System.out.println("*****************************");
					}
				}
			} else {
				System.out.println("$$$$$$$$$$不在可连接范围内$$$$$$$$$$$$$");
			}
		}
	}

	class WifiConnectReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
				NetworkInfo notewokInfo = manager.getActiveNetworkInfo();
				if (notewokInfo != null) {
					System.out.println("notewokInfo.getExtraInfo()------->"
							+ notewokInfo.getExtraInfo());
					currentWifiInfo = wifiAdmin.getCurrentWifiInfo();
					System.out.println("currentWifiInfo.getSSID()----->"
							+ currentWifiInfo.getSSID());
					if (currentWifiInfo.getSSID().equals(configurateSsid)) {
						isConnected = true;
					}
				} else {
					System.out.println("notewokInfo is null");
				}
			}
		}

	}

	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver(wifiConnectReceiver);
	}
}

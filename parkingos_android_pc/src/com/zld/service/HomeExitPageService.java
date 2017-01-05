package com.zld.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberMadeOrder;
import com.zld.bean.EscapedOrder;
import com.zld.bean.MyCameraInfo;
import com.zld.db.SqliteManager;
import com.zld.fragment.EntranceFragment;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.state.EntranceOrderState;
import com.zld.lib.util.CameraManager;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.lib.util.StringUtils;
import com.zld.lib.util.TimeTypeUtil;
import com.zld.lib.util.VoicePlayer;
import com.zld.photo.DecodeManager;
import com.zld.photo.UpLoadImage;
import com.zld.ui.ZldNewActivity;
import com.zld.view.LineLocalRestartDialog;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeExitPageService extends BaseService {

	private static final String TAG = "HomeExitPageService";
	private static final String INTENT_KEY = "intentkey";

	private Intent intent;
	public Bundle mBundle;
	int xCoordinate;
	int yCoordinate;
	int carPlateheight;
	int carPlatewidth;
//	private int resType;
	private String cameraIp;
	private Context context;
	private Bitmap resultBitmap;
	private String netType = Constant.sZero;
	private String carPlate = "";
	private String isPole;
	private String poleRecordID;
	int i = 0;
	private String uid;
	private Toast mToast;
	private String comid;
	private String passid;
	private long time = 0;
	private Timer timer;
	public String issuplocal;
	private boolean isCameraOk = false;
	private int confidenceLevel;
	ArrayList<MyCameraInfo> selectCamera;
	public ArrayList<MyCameraInfo> selectCameraIn;
//	/** 获取入口同一通道下的cameraip和MyLedInfo */
//	public HashMap<String, MyLedInfo> selectIpIn;

//	boolean flag = true;// 用于停止线程
	private SqliteManager sqliteManager;
	private EntranceFragment entranceFragment;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			switch (msg.what) {
			case Constant.COMECAR_MSG:
				FileUtil.writeSDFile("入场抬杆流程", "");
				if (msg.obj instanceof Bitmap) {
					Log.e(TAG, "Service图片返回+isPole+" + isPole);
					if (isPole != null && isPole.equals("TRUE")) {
						Log.e(TAG, "Service进入抬杆图片进度");
						resultBitmap = (Bitmap) msg.obj;
						mBundle = msg.getData();
						mBundle.putString("POLEID", poleRecordID);
						byte[] bitmapByte = ImageUitls.bitmapByte(resultBitmap);
						mBundle.putByteArray("bitmap", bitmapByte);
						sendKey(ZldNewActivity.POLE_UP_IMAGE, null, null, null);
						isPole = "false";
						break;
					} else {
						Log.e(TAG, "Handler：65 获取到图片");
						callbackBitmap(msg);
					}
				}
				break;
			case Constant.SHOWVIDEO_MSG:
				callbackBitmap(msg);
				break;
			case Constant.OPENCAMERA_SUCCESS_MSG:
				Log.e(TAG, "Service打开网络摄像头成功");
				DecodeManager.getinstance().setConfidenceLevel(confidenceLevel);
				Toast.makeText(getApplicationContext(), "入口打开网络摄像头成功", Toast.LENGTH_LONG).show();
				sendKey(ZldNewActivity.CANCEL_HOME_CAMERA_ERROR_DIALOG, null, null, null);
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (selectCameraIn.size() > 0)
//							uploadCameraState(selectCameraIn.get(0), Constant.CAMERA_STATE_SUCCESS);
//					}
//				}).start();
				break;
			case Constant.OPENCAMERA_FAIL_MSG:
				Log.e(TAG, "Service打开网络摄像头失败");
				if (msg.arg1 == -1) {
					showToast("入口摄像头连接出错");
					CameraManager.reOpenCamera();
				} else if (msg.arg2 == 0) {
					// Toast.makeText(getApplicationContext(), "入口打开网络摄像头失败",
					// 1).show();
					Log.e("--","入口打开网络摄像头失败");
				}
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (selectCameraIn.size() > 0)
//							uploadCameraState(selectCameraIn.get(0), Constant.CAMERA_STATE_FAILE);
//					}
//				}).start();
				break;
			case Constant.RESTART_YES:
				sendKey(ZldNewActivity.RESTART, null, null, null);
				break;
			case Constant.KEEPALIVE:
				Log.e("-----", "服务的KEEPALIVEKEEPALIVE");
				time = System.currentTimeMillis();
				sendKey(ZldNewActivity.CANCEL_HOME_CAMERA_ERROR_DIALOG, null, null, null);
				break;
			case Constant.KEEPALIVE_TIME:
				Log.e(TAG, "入口摄像头连接断开出错" + isCameraOk);
				showToast("入口摄像头连接断开出错");
				// if(isCameraOk){//出口摄像头连接没问题
				sendKey(ZldNewActivity.HOME_CAMERA_ERROR_DIALOG, null, null, null);
//				new Thread(new Runnable() {
//					@Override
//					public void run() {
//						if (selectCameraIn.size() > 0)
//							uploadCameraState(selectCameraIn.get(0), Constant.CAMERA_STATE_FAILE);
//					}
//				}).start();
				break;
			}
		}

		private void callbackBitmap(Message msg) {
			FileUtil.writeSDFile("入场抬杆流程", "callbackBitmap");
			String numbers = Constant.sZero;
			String tsNumber = SharedPreferencesUtils.getParam(context, "carNumber", "carNumber", numbers);
			int carNumber = Integer.valueOf(tsNumber);
			if (carNumber <= 0) {
				String fullset = SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "fullset", Constant.sZero);
				if (fullset.equals(Constant.sOne)) {
					showToast("车位已满");
					sendKey(ZldNewActivity.FULL, null, "车位已满", "车位已满");
					return;
				}
			}
			resultBitmap = (Bitmap) msg.obj;
			mBundle = msg.getData();

			int resType = mBundle.getInt("resType");// 资源类型
			carPlateheight = mBundle.getInt("carPlateheight");
			carPlatewidth = mBundle.getInt("carPlatewidth");
			xCoordinate = mBundle.getInt("xCoordinate");
			yCoordinate = mBundle.getInt("yCoordinate");
			carPlate = mBundle.getString("carPlate");
			cameraIp = mBundle.getString("cameraIp");

			byte[] bitmapByte = ImageUitls.bitmapByte(resultBitmap);
			mBundle.putByteArray("bitmap", bitmapByte);
			if (intent == null) {
				intent = new Intent("android.intent.action.exit");
			}
			try {
				passid = getPassid(cameraIp);
				if (resType == 4) {
					String carnumber = StringUtils.buildCarNumber(context);
					carPlate = "无" + (carnumber);
				}

				// 是否是本地服务器
				boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype",
						"isLocalServer", false);
				if (!isLocalServer) {// 不是本地服务器
					// 本地化相关
					boolean param = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "isLocal",
							false);
					Log.e("isLocal", "HomeExitPageService callbackBitmap get isLocal " + param);
				}
				FileUtil.writeSDFile("入场抬杆流程", "resType="+resType);
				if (resType == 8) {
					madeOrder(carPlate, 0);
				} else if (resType == 4) {
					Log.e(TAG, "手动触发出口Service生成订单：补录为1");
					madeOrder(carPlate, 1);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};
//	ZldNewActivity zldNewActivity;
	@Override
	public void onCreate() {
		super.onCreate();
//		zldNewActivity = ZldNewActivity.getInstance();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		context = this;
		initTimer();
		init(intent);
		return super.onStartCommand(intent, flags, startId);
	}

	/**
	 * 初始化计时器
	 */
	private void initTimer() {
		// TODO Auto-generated method stub
		if (timer == null) {
			timer = new Timer();
		}
	}

	/**
	 * 
	 * 功能说明：初始化 日期: 2015年3月13日
	 *
	 */
	private void init(Intent intent) {
		if (intent != null) {
			Bundle extras = intent.getExtras();
			String intent_key = extras.getString(INTENT_KEY);
			if (intent_key.equals("catchimage")) {
				isPole = extras.getString("POLE");
			}

			poleRecordID = extras.getString("POLEID");
			if (intent_key.equals("init")) {
				// 初始跳转传递的信息
				initGetInfo(extras);
			} else if (intent_key.equals("catchimage")) {
				// 调用摄像头抓取图片
				Log.e(TAG, "补录来车调用摄像头地址：" + extras.getString("cameraip"));
				String res = DecodeManager.getinstance().getOneImg(extras.getString("cameraip"));
				Log.e(TAG, "补录来车一体机返回的结果是:   " + res);
			} else if (intent_key.equals("addcar")) {
				// 车牌号生成订单 补录来车直接生成了
				Log.e("--","车牌号生成订单 补录来车直接生成了");
			} else if (intent_key.equals("updatecar")) {
				// 车牌号修改订单
				String token = extras.getString("token");
				String comid = extras.getString("comid");
				String orderid = extras.getString("orderid");
				String carNumber = extras.getString("carnumber");
				try {
					alterCarNumber(token, comid, orderid, carNumber);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else if (intent_key.equals("setConfidenceLevel")) {
				confidenceLevel = extras.getInt("confidenceLevel");
				Log.e(TAG, "confidenceLevel = " + confidenceLevel);
				DecodeManager.getinstance().setConfidenceLevel(confidenceLevel);
			} else if (intent_key.equals("openPole")) {
				controlHomePole(extras.getString("poleip"));
			} else if (intent_key.equals("closeService")) {
				this.onDestroy();
			} else if (intent_key.equals("updateuid")) {
				uid = extras.getString("uid");
			} else if (intent_key.equals("exitcamerastate")) {
				isCameraOk = extras.getBoolean("isCameraOk");
			} else if (intent_key.equals("updatetime")) {
				Log.e("-----", "updatetimeupdatetime");
				time = System.currentTimeMillis() + 30000;
			}
		}
	}

	private void initGetInfo(Bundle extras) {
		uid = extras.getString("uid");
		comid = extras.getString("comid");
		AppInfo.getInstance().setUid(uid);
		AppInfo.getInstance().setComid(comid);
//		isEnableMutiBill = extras.getBoolean("isEnableMutiBill");
		if (sqliteManager == null) {
//			sqliteManager = new SqliteManager(HomeExitPageService.this);
//			sqliteManager = zldNewActivity.sqliteManager;
//			sqliteManager = ((application)getApplication()).getSqliteManager();
			sqliteManager = ((application) getApplication()).getSqliteManager(HomeExitPageService.this);
		}
		// initSqliteManager();
		selectCameraIn = sqliteManager.selectCamera(SqliteManager.PASSTYPE_IN);
		// 获取设置的当前网络类型
		netType = SharedPreferencesUtils.getParam(HomeExitPageService.this, "nettype", "netType", Constant.sZero);
		// 摄像机置信度
		confidenceLevel = SharedPreferencesUtils.getParam(context, "cameraParam", "confidenceLevel", 80);
		boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "isLocalServer",
				false);
		if (isLocalServer) {
			String linelocal = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "linelocal",
					"local");
			Log.e("linelocal", "HomeExitPageService的linelocal:" + linelocal);
			if (linelocal.equals("local")) {
				String ip = SharedPreferencesUtils.getParam(getApplicationContext(), "nettype", "localip", null);
				if (ip != null) {
					/* 设置mserver的为线上和本地服务器 */
					Constant.requestUrl(ip);
					Constant.serverUrl(ip);
				} else {
					// Constant.requestUrl的值默认为线上
					Log.e("linelocal", "Constant.requestUrl的值默认为线上");
				}
			}
		}
		// 开启入口摄像头
		setCameraConn();
		satrtTiming();
		initIssUpLocal();
	}

	/**
	 * 开启入口摄像头
	 */
	private void setCameraConn() {
		/** 获取通道类型为0入口的所有摄像头信息 */
		selectCamera = sqliteManager.selectCamera(SqliteManager.PASSTYPE_IN);
		Log.e(TAG, "获取的摄像头：" + selectCamera);
		if (i < selectCamera.size()) {
			handler.postDelayed(runnable, 50);
		}
	}

	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (i < selectCamera.size()) {
				Log.e(TAG, "i的值：" + i + selectCamera.get(i).getIp());
				CameraManager.openCamera(handler, selectCamera.get(i).getIp());
			} else {
				handler.removeCallbacks(runnable);
			}
			handler.postDelayed(this, 10000);
			i++;
			if (i == selectCamera.size()) {
				handler.removeCallbacks(runnable);
			}
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		stopSelf();
		DecodeManager.getinstance().stopYitiji();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 
	 * 功能说明：按车牌生成订单 日期: 2015年3月13日 开发者:
	 *
	 * @param carNumber
	 *            车牌号码
	 * @param iType
	 *            0为扫牌;1为补录
	 * @throws UnsupportedEncodingException
	 */
	public void madeOrder(final String carNumber, int iType) throws UnsupportedEncodingException {
		String token = AppInfo.getAppInfo().getToken();
		if (token != null && token.equals("false")) {
			return; // 订单提交没有token，但是这里做个检查防止出服务没有死掉的情况
		}
		final String carnumber = URLEncoder.encode(carNumber, "utf-8");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.MADE_ORDER);
		params.setUrlParams("comid", comid);
		params.setUrlParams("uid", uid);
		params.setUrlParams("carnumber", URLEncoder.encode(carnumber, "utf-8"));
		params.setUrlParams("through", 3);
		params.setUrlParams("from", iType); // 0:通道扫牌自动生成订单，1：补录车牌生成订单
		// 之前为车牌颜色，现在传车辆类型
		params.setUrlParams("car_type", -1);
		params.setUrlParams("passid", passid);
		
		String url = params.getRequstUrl();
		Log.e(TAG, "生成订单url---------------->>" + url);
		FileUtil.writeSDFile("入场抬杆流程", "发送生成订单的请求"+url);
		HttpManager.requestGET(this, url, carNumber, this);
	}

//	/**
//	 * 上传摄像头状态
//	 *
//	 * @param cameraid
//	 *            摄像头id
//	 * @param camerastate
//	 *            摄像头状态
//	 */
//	public void uploadCameraState(MyCameraInfo cameraid, String camerastate) {
////		RequestParams params = new RequestParams();
////		params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_CAMERA_STATE);
////		Log.e(TAG, "上传摄像头id" + cameraid.getId());
////		params.setUrlParams("cameraid", cameraid.getCameraid());
////		params.setUrlParams("state", camerastate);
////		String url = params.getRequstUrl();
////		Log.e(TAG, "上传摄像头状态url---------------->>" + url);
////		HttpManager.requestGET(this, url, this);
//	}
//
//	/**
//	 * 上传道闸状态
//	 *
//	 * @param cameraid
//	 *            摄像头id
//	 * @param brakestate
//	 *            摄像头状态
//	 */
//	public void uploadBrakeState(MyCameraInfo cameraid, String brakestate) {
////		RequestParams params = new RequestParams();
////		params.setUrlHeader(Constant.requestUrl + Constant.UPLOAD_BRAKE_STATE);
////		params.setUrlParams("passid", cameraid.getPassid());
////		params.setUrlParams("cameraid", cameraid.getCameraid());
////		params.setUrlParams("state", brakestate);
////		String url = params.getRequstUrl();
////		Log.e(TAG, "上传道闸状态url---------------->>" + url);
////		HttpManager.requestGET(this, url, this);
//	}

	private StringBuffer buildStr(final String carNumber, int parseInt, int num) {
		StringBuffer sb = new StringBuffer();
		sb.append(carNumber);
		sb.append("有");
		sb.append(num);
		sb.append("笔逃单,在您的车场逃单");
		sb.append(parseInt);
		sb.append("次!");
		return sb;
	}

	/**
	 * 
	 * 功能说明：生成订单后的操作 注：补录来车不自动抬杆 日期: 2015年3月13日
	 *
	 */
	private void buildOrderAfter(final String carnumber, CarNumberMadeOrder info) {
		FileUtil.writeSDFile("入场抬杆流程", "buildOrderAfter="+carnumber);
		String orderid = info.getOrderid();
		getEntranceFragment();
		// 如果当前状态为自动来车状态,则自动抬杆,为补录状态,则不抬杆
		// stateOpenPole(info);
		// 上面的状态一直是进不去的，所以为了实现返回后抬杆功能，暂时在外面调用抬杆，后期再修改
		FileUtil.writeSDFile("入场抬杆流程", "isPoleAutoWorking()="+isPoleAutoWorking()+"  cameraIp="+cameraIp);
		if (isPoleAutoWorking()) {
			controlHomePole(cameraIp);
		}
		// 保存图片
		saveImage(carnumber, orderid);
		// 保存数据库
		// save(carnumber,orderid);
		// 修改入场订单状态
		setEntranceOrderState();
		// 刷新界面
		sendKey(ZldNewActivity.REFRESH, null, null, null);
	}
	/**
	 * 获取EntranceFragment
	 */

	private void getEntranceFragment() {
		if (((application) getApplicationContext()).getZldNewActivity() != null) {
			entranceFragment = ((application) getApplicationContext()).getZldNewActivity().entranceFragment;
			Log.e(TAG, "获取到entranceFragment:" + entranceFragment);
		}
	}

	/**
	 * 修改入场订单状态
	 */
	private void setEntranceOrderState() {
		if (entranceFragment != null && entranceFragment.entranceOrderState != null) {
			entranceFragment.entranceOrderState.setState(EntranceOrderState.AUTO_COME_IN_STATE);
		}
	}

	/**
	 * 
	 * 功能说明：保存图片和刷新界面 日期: 2015年3月13日
	 *
	 */
	public void saveImage(final String carnumber, String orderid) {
		// 本地保存图片及图片信息和上传图片
		if (resultBitmap != null) {
			saveAndUpload(carnumber, orderid);
		}
	}

	/**
	 * 
	 * 功能说明：保存和上传图片 日期: 2015年3月13日
	 *
	 */
	private void saveAndUpload(final String carnumber, String orderid) {
		// 本地保存图片及图片信息---原图
		ImageUitls.SaveImageInfo(sqliteManager, resultBitmap, uid, carnumber, orderid, xCoordinate + "",
				yCoordinate + "", Constant.HOME_PHOTOTYPE + "", carPlatewidth + "", carPlateheight + "");
		Log.e(TAG, "获取到保存的netType:" + netType);
		// netType 上传图片网络类型：0：表示流量---需要压缩图片; 1：表示宽带---不需要压缩
		InputStream bitmapToInputStream = ImageUitls.getBitmapInputStream(netType, resultBitmap);
		upload(bitmapToInputStream, orderid, xCoordinate + "", yCoordinate + "", carPlatewidth + "",
				carPlateheight + "", carnumber);
	}

	/**
	 * 
	 * 功能说明：上传图片 日期: 2015年3月13日
	 * 
	 * @param bitmapToInputStream 图片流
	 * @param orderid 订单号
	 * @param lefttop 1
	 * @param rightbottom 2
	 * @param width 3
	 * @param height 4
	 * @param carNumber 6
	 */
	private void upload(InputStream bitmapToInputStream, String orderid, String lefttop, String rightbottom,
			String width, String height, String carNumber) {
		// TODO Auto-generated method stub
		UpLoadImage upLoadImage = new UpLoadImage();
		upLoadImage.setComid(comid);
		upLoadImage.setmHandler(handler);
		upLoadImage.setPhotoType(Constant.HOME_PHOTOTYPE);
		upLoadImage.upload(bitmapToInputStream, orderid, lefttop, rightbottom, width, height, carNumber);

	}

	/**
	 * 
	 * 功能说明：提示信息 日期: 2015年3月13日
	 *
	 */
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
	 * 按车牌生成订单; 强制直接生成订单
	 * http://192.168.199.239/zld/cobp.do?action=addorder&comid=10&uid=1000028&
	 * carnumber=aaabebdd
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void addOrder(final String carNumber) throws UnsupportedEncodingException {
		String carnumber = URLEncoder.encode(carNumber, "utf-8");
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.ADD_CAR);
		params.setUrlParams("comid", comid);
		params.setUrlParams("uid", uid);
		params.setUrlParams("carnumber", URLEncoder.encode(carnumber, "utf-8"));
		params.setUrlParams("out", "json");
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "强制生成订单url---------------->>" + url);
		FileUtil.writeSDFile("入场抬杆流程", "强制生成"+url);
//		HttpManager.requestGET(this, url, this);111
		HttpManager.requestGET(this, url, carNumber, this);
	}

	/**
	 * 修改车牌号；
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void alterCarNumber(final String token, String comid, final String orderid, String carNumber)
			throws UnsupportedEncodingException {
		Log.e("--","token="+token);
		if (comid != null && orderid != null) {
			String carnumber = URLEncoder.encode(carNumber, "utf-8");
			RequestParams params = new RequestParams();
			params.setUrlHeader(Constant.requestUrl + Constant.MODIFY_ORDER);
			params.setUrlParams("comid", comid);
			params.setUrlParams("orderid", orderid);
			params.setUrlParams("carnumber", URLEncoder.encode(carnumber, "utf-8"));
			params.setUrlParams("through", 3);
			String url = params.getRequstUrl();
			Log.e(TAG, "修改车牌号url---------------->>" + url);
			HttpManager.requestGET(this, url, this);
		}
	}

	/**
	 * 免费接口
	 */
	public void freeOrder(String token, String orderid) {
		Log.e("--","token="+token);
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.FREE_ORDER);
		params.setUrlParams("token", AppInfo.getInstance().getToken());
		params.setUrlParams("orderid", orderid);
		params.setUrlParams("passid", passid);
		String url = params.getRequstUrl();
		Log.e(TAG, "免费url---------------->>" + url);
		HttpManager.requestGET(this, url, this);
	}

	/**
	 * 
	 * 功能说明：刷新界面 日期: 2015年3月14日
	 */
	public void sendKey(int receiver_key, String orderid, String ledContent, String collect) {
		if (intent == null) {
			intent = new Intent("android.intent.action.exit");
		}
		if (mBundle == null) {
			mBundle = new Bundle();
		}
		mBundle.putInt("receiver_key", receiver_key);
		if (ledContent != null) {
			mBundle.putString("led_content", ledContent);
		}

		if (collect != null) {
			mBundle.putString("led_collect", collect);
		}

		if (orderid != null) {
			mBundle.putString("orderid", orderid);
		}
		intent.putExtras(mBundle);
		sendBroadcast(intent);// Activity里显示图片
	}

	/**
	 * 
	 * 功能说明：是否自动抬杆 日期: 2015年3月13日
	 *
	 */
	private boolean isPoleAutoWorking() {
		boolean param = SharedPreferencesUtils.getParam(getApplicationContext(), "cameraParam", "auto", true);
		System.out.println("设置的是否自动抬杆：" + param);
		return param;
	}

	/**
	 * 入口抬杆
	 */
	public void controlHomePole(final String ip) {
		Log.e(TAG, "抬杆了");
		DecodeManager.getinstance().controlPole(DecodeManager.openPole, ip);
		new Thread(new Runnable() {
			// 遇到了抬杆偶然失效的问题，先加个重发机制，后面用确认发送成功来条换掉这个
			public void run() {
				try {
					Thread.sleep(2000);
					DecodeManager.getinstance().controlPole(DecodeManager.openPole, ip);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
		/**
		 * 留着下次做 Log.e("taigan","抬杆结果:"+result); new Thread(new Runnable() {
		 * 
		 * @Override public void run() {
		 *           uploadBrakeState(selectCameraIn.get(0),result+""); }
		 *           }).start();
		 */
	}

	@Override
	public boolean doSucess(String url, String object, String token, String orderid) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.FREE_ORDER)) {
			doFreeOrderResult(object);
		} else if (url.contains(Constant.MODIFY_ORDER)) {
			doModifyOrderResult(object, token, orderid);
		}
		return true;
	}

	/**
	 * 免费订单的结果
	 *
	 */
	private void doFreeOrderResult(String object) {
		if (object.equals(Constant.sOne)) {
			showToast("免费成功");
		}
	}

	/**
	 * 修改车牌号
	 *
	 */
	private void doModifyOrderResult(String object, String token, String orderid) {
		// TODO Auto-generated method stub
		// 成功-刷新界面显示修改过的当前订单
		if (object.equals(Constant.sOne)) {
			showToast("修改车牌成功");
			// 刷新界面
			// sendKey(HomeExitPageActivity.REFRESH,null,null);
		} else if (object.equals(Constant.sZero)) {
			// 当前订单中已经存在打算修改的车牌号，则将当前的订单免费掉
			freeOrder(token, orderid);
		} else {
			showToast("修改车牌失败");
		}
	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.ADD_CAR) || url.contains(Constant.MADE_ORDER)) {
			FileUtil.writeSDFile("入场抬杆流程", madeOrderFailureCount + "   " + TimeTypeUtil.getNowTime() + "订单生成超时  url:" + url);
			if (madeOrderFailureCount < 3) {
				madeOrderFailureCount++;
				showToast("订单生成失败,正在重现添加！");
				HttpManager.requestGET(this, url, this);
			} else {
				showToast("订单生成失败,请人工确认！");
			}
		}
		super.timeout(url);
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		if (url.contains(Constant.ADD_CAR) || url.contains(Constant.MADE_ORDER)) {
			FileUtil.writeSDFile("入场抬杆流程", madeOrderFailureCount + "  " + TimeTypeUtil.getNowTime() + "订单生成失败+status:"
					+ status + "  url:" + url);
			if (madeOrderFailureCount < 3) {
				madeOrderFailureCount++;
				showToast("订单生成失败,正在重现添加！");
				HttpManager.requestGET(this, url, this);
			} else {
				showToast("订单生成失败,请人工确认！");
			}
		}
		return super.doFailure(url, status);
	}

	int madeOrderFailureCount = -1;

	@Override
	public boolean doSucess(String url, String object, String carnumber) {
		// TODO Auto-generated method stub
		Log.e(TAG, "doSucess---------------->>" + url);
		FileUtil.writeSDFile("入场抬杆流程", "返回成功"+carnumber);
		if (url.contains(Constant.MADE_ORDER)) {
//			j = 0;
			try {
				doMadeOrderResult(object, carnumber);
				madeOrderFailureCount = 0;
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (url.contains(Constant.ADD_CAR)) {
			doAddCarResult(object, carnumber);
		}
		return true;
	}
	/**
	 * 强制生成订单的结果
	 *
	 */
	private void doAddCarResult(String object, String carNumber) {
		// TODO Auto-generated method stub
		EscapedOrder info = new Gson().fromJson(object, EscapedOrder.class);
		FileUtil.writeSDFile("入场抬杆流程", "强制生成订单"+info.toString());
		if (info.getInfo().equals(Constant.sOne)) {
			showToast("订单生成,可在当前订单查看！");
//			// 打开道闸
//			 DecodeManager.getinstance().controlPole(DecodeManager.openPole);
//			// 保存图片
//			saveImage(carNumber, info.getOrderid());
//			// 刷新界面
//			sendKey(ZldNewActivity.REFRESH, null, null, null);
			
			showToast("订单生成,可在当前订单别查看！");
			sendKey(ZldNewActivity.SHOW, null, carPlate + "欢迎光临", null);
			Log.e(TAG, "订单生成的Orderid：" + info.getOrderid());
			CarNumberMadeOrder infos = new CarNumberMadeOrder();
			infos.setOrderid(info.getOrderid());
			buildOrderAfter(carNumber, infos);
		} else {
			showToast("订单生成失败" + object);
		}
	}

	/**
	 * 生成订单的结果信息
	 *
	 * @throws UnsupportedEncodingException
	 */
	private void doMadeOrderResult(String object, String carNumber) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		Log.e(TAG, "车牌识别生成订单的carNumber--->" + carNumber);
		Log.e(TAG, "车牌识别生成订单的结果--->" + object);
		CarNumberMadeOrder info = new Gson().fromJson(object, CarNumberMadeOrder.class);
		Log.e(TAG, "车牌识别生成订单--->" + info.toString());
		FileUtil.writeSDFile("入场抬杆流程", "车牌识别生成订单--->" + info.toString());
		if (info.getInfo().equals(Constant.sOne)) {
			showToast("订单生成,可在当前订单别查看！");
			sendKey(ZldNewActivity.SHOW, null, carPlate + "欢迎光临", null);
			Log.e(TAG, "订单生成的Orderid：" + info.getOrderid());
			buildOrderAfter(carNumber, info);
		} else if (info.getInfo().equals(Constant.sZero)) {// 逃单
			if (info.getOther() != null && info.getOwn() != null) {
				int parseInt = Integer.parseInt(info.getOwn());
				int parseInt2 = Integer.parseInt(info.getOther());
				int num = parseInt + parseInt2;
				StringBuffer sb = buildStr(carNumber, parseInt, num);
				// warning 逃单多少次
				mBundle.putString("warning", sb.toString());
				mBundle.putString("carNumber", carNumber);
				mBundle.putString("comid", comid);
				mBundle.putString("uid", uid);
				// sendKey(HomeExitPageActivity.SHOW_DIALOG,null,null);
				//[info=0,orderid=null,own=0,other=1,ismonthuser=0,proorderid=null]
				//对比正常订单，所以要强制生成订单
				//[info=1,orderid=24707902,own=0,other=0,ismonthuser=0,proorderid=null]
				FileUtil.writeSDFile("入场抬杆流程", "是逃单要强制生成");
				addOrder(carNumber);
			} else {
				showToast("查询逃单信息服务器错误！");
			}
		} else if (info.getInfo().equals("-1")) {
			showToast("车场编号错误！");
		} else if (info.getInfo().equals("-2")) {
			showToast(carNumber + "存在未结算订单,请先结算！");
		} else if (info.getInfo().equals("-4")) {
			VoicePlayer.getInstance(this).playVoice("月卡第二辆车禁止入内");
			showToast(carNumber + "月卡第二辆车禁止入内");
			sendKey(ZldNewActivity.SPEAK, null, "月卡第二辆车禁止入内", "月卡占用");
			sendKey(Constant.REFRESH_NOMONTHCAR2_IMAGE, null, null, null);
			Message m = new Message();
//			m.what = 1221;
//			m.obj = carNumber + "月卡第二辆车禁止入内";
//			handler.sendMessage(m);
			sendKey(1221,carNumber + "月卡第二辆车禁止入内",null,null);
//			LineLocalRestartDialog dialog = new LineLocalRestartDialog(context,com.zld.R.style.nfcnewdialog,null,carNumber + "月卡第二辆车禁止入内","取消","抬杆");
//			dialog.show();
		} else if (info.getInfo().equals("-3")) {
			VoicePlayer.getInstance(this).playVoice("非月卡禁止入内");
//			showToast("非月卡禁止入内");1
			sendKey(ZldNewActivity.SPEAK, null, "非月卡禁止入内", "非月卡车");
			sendKey(Constant.REFRESH_NOMONTHCAR_IMAGE, null, null, null);
//			LineLocalRestartDialog dialog = new LineLocalRestartDialog(context,com.zld.R.style.nfcnewdialog,null,"非月卡禁止入内","取消","抬杆");
//			dialog.show();
//			Message m = new Message();
//			m.what = 1221;
//			m.obj = "非月卡禁止入内";
//			handler.sendMessage(m);
			sendKey(1221, "非月卡禁止入内",null,null);
		} else if (info.getInfo().equals("-5")) {
			VoicePlayer.getInstance(this).playVoice("月卡过期禁止入内");
//			showToast("月卡过期禁止入内");
			sendKey(ZldNewActivity.SPEAK, null, "月卡过期禁止入内", "月卡过期");
			sendKey(Constant.HOME_CAR_OUTDATE_ICON, null, null, null);
//			LineLocalRestartDialog dialog = new LineLocalRestartDialog(context,com.zld.R.style.nfcnewdialog,null, "月卡过期禁止入内","取消","抬杆");
//			dialog.show();
//			Message m = new Message();
//			m.what = 1221;
//			m.obj = "月卡过期禁止入内";
//			handler.sendMessage(m);
			sendKey(1221, "月卡过期禁止入内",null,null);
		}
	}
	
	private String getPassid(String cameraIp) {
		if (selectCameraIn.size() > 0) {
			for (int i = 0; i < selectCameraIn.size(); i++) {
				MyCameraInfo myCameraInfo = selectCameraIn.get(i);
				Log.e(TAG, "摄像头回调cameraIp:" + cameraIp + "		数据库保存ip：" + myCameraInfo.getIp());
				if (cameraIp.equals(myCameraInfo.getIp())) {
					passid = myCameraInfo.getPassid();
					Log.e(TAG, "摄像头对应的passid:" + passid);
					return passid;
				}
			}
		}
		return null;
	}
	/**
	 * 执行定时任务
	 */
	private void satrtTiming() {
		TimerTask task = new TimerTask() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Log.e("-----", "runnnnnnnnnnnnnn");
				long currentTimeMillis = System.currentTimeMillis();
				if (time == 0 || (currentTimeMillis - time) > 30000) {// 当前时间减心跳回复的时间time
																		// 大于30秒
					Message message = new Message();
					message.what = Constant.KEEPALIVE_TIME;
					handler.sendMessage(message);
				}
			}
		};
		timer.schedule(task, 20000, 10000); // 20秒后执行，20秒一次
	}

	private void initIssUpLocal() {
		// TODO Auto-generated method stub
		if (issuplocal == null) {
			issuplocal = SharedPreferencesUtils.getParam(this.getApplicationContext(), "zld_config", "issuplocal", "");
			Log.e("isLocal", "BaseActivity initIssUpLocal get issuplocal " + issuplocal);
		}
	}
}

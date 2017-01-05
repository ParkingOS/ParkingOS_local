package com.zld.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.ShaerUiInfo;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.engine.ShareUiInfoParser;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.AppInfoUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.SharedPreferencesUtils;
import com.zld.photo.UpLoadImage;
import com.zld.ui.LoginActivity;
import com.zld.ui.ZldNewActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class ShareUiService extends BaseService{

	private static final String TAG = "ShareUiService";
	SqliteManager sm;
	long startTime = 0;
	private String netType;
	private String token = null;
	private ZldNewActivity zldNewActivity;
//	private LocalOrderDBManager loDBManager;
	private boolean isFirst = true;/*登录后,即下载一次本地需要的信息*/
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case Constant.SHOWPIC_ONRIGHT_MSG:
				//上传图片成功回调
				String orderid = (String) msg.obj;
				Log.e(TAG,"图片上传成功orderid："+orderid);
				if(orderid!=null){
					//修改图片上传状态
					int ishomeexitup = msg.arg1;
					Log.e(TAG,"修改图片上传状态-ishomeexitup为0是入口："+ishomeexitup+" orderid:"+orderid);
					if(ishomeexitup==0){
						//入口上传成功
						sm.updateOrderImg(orderid, "1", true);
					}else{
						//出口上传成功
						sm.updateOrderImg(orderid, "1", false);
					}
					UploadImg selectImage = sm.selectImage(orderid);
					if(selectImage!=null){
						//出入口图片上传状态都是1的话,则本地删除
						String imghomepath = selectImage.getImghomepath();
						String homeimgup = selectImage.getHomeimgup();
						String exitimgup = selectImage.getExitimgup();

						if((imghomepath==null&&exitimgup.equals("1"))||
								(homeimgup.equals("1")&&exitimgup.equals("1"))){
							//传完删除本地图片和数据库图片信息
							deleteOrderIamgeInfo(orderid);
						}
					}
				}
				break;
			case Constant.PICUPLOAD_FILE:
				//上传图片失败回调
				Log.e(TAG, "查询数据库,删除图片文件及数据库图片信息");
				//deleteOrderIamgeInfo((String) msg.obj);
				break;
			}
			recursionUpload();
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
//		initSqliteManager();
		startTime = System.currentTimeMillis();
		netType = SharedPreferencesUtils.getParam(ShareUiService.this, "nettype", "netType", "0");
	}

//	private LocalOrderDBManager initSqliteManager() {
//		// TODO Auto-generated method stub
//		if(loDBManager == null){
//			application application = ((application) getApplication());
//			loDBManager = application.getLocalOrderDBManager(ShareUiService.this);
//		}
//		return loDBManager;
//	}

	@Override
	public void onStart(Intent intent, int startId) {
		zldNewActivity = ((application) getApplication()).getZldNewActivity();
		token = AppInfo.getInstance().getToken();
		if (token == null || zldNewActivity == null) {
			//stopService();
			return;
		}
		getShareInfo();
		boolean isLocalServer = SharedPreferencesUtils.getParam(getApplicationContext(),"nettype", "isLocalServer", false);
		if(!isLocalServer){//为false,不是本地服务器,开启同步订单
			if(isFirst){
				/*同步车场时间月卡*/
//				doSynchronize();
				/*订单表是否为空，为空下载，不为空更新*/
//				startUpdataLocalData();
				isFirst = false;
				startTime = System.currentTimeMillis();
			}
			if(intent != null){
				String refresh = intent.getStringExtra("refresh");
				if(refresh != null&&refresh.equals("refresh")){
					startTime = startTime - Constant.time;
				}
			}
//			startFiveMinuteLocal();
		}else{//为true表示有本地服务器,不同步订单。
		}
//		fileRegularDelete();
	}

//	private void initIssUpLocal() {
//		// TODO Auto-generated method stub
//		if(issuplocal == null){
//			issuplocal = SharedPreferencesUtils.getParam(
//					getApplicationContext(), "zld_config", "issuplocal", "");
//			Log.e("isLocal","BaseFragment initIssUpLocal get issuplocal "+issuplocal);
//		}
//	}

	public void getShareInfo() {
		//获取安卓主机的状态
		if(zldNewActivity!=null){
			String passid = zldNewActivity.passid;
			Log.e("taigan","获取车位信息是传的passid:"+passid);
			RequestParams params = new RequestParams();
			params.setUrlHeader(Constant.serverUrl + Constant.GET_SHARE);
			AppInfoUtil.displayBriefMemory(zldNewActivity);
			params.setUrlParams("comid", AppInfo.getInstance().getComid());
			params.setUrlParams("passid", passid);
			params.setUrlParams("type", 1);//新接口
			params.setUrlParams("token",AppInfo.getInstance().getToken());
			params.setUrlParams("equipmentmodel", AppInfoUtil.getEquipmentModel());
			params.setUrlParams("memoryspace",(AppInfoUtil.getAvailableMemory(zldNewActivity)/1024/1024)+"_"+(AppInfoUtil.getTotalMemorySize(zldNewActivity)/1024/1024));
			params.setUrlParams("internalspace", +AppInfoUtil.getAvailableInternalMemorySize()+"_"+AppInfoUtil.getTotalInternalMemorySize());
			String url = params.getRequstUrl();
			Log.e(TAG, "获取分享车位数信息："+url);
			HttpManager.requestShareGET(this, url,this);	
		}
	}

	/**---------------------本地化逻辑-------------------------*/
//	private void startFiveMinuteLocal() {
//		Log.e(TAG,"当前时间差："+(System.currentTimeMillis()-startTime));
//		if(System.currentTimeMillis()-startTime>Constant.time){
//			//当状态不是"结算完成状态"5
//			//当状态不是"正在结算订单状态"6
//			//当状态不是 "结算完成状态，但没有点击收费完成或免费，此时的状态入口来车不刷新列表"7
//
//			Log.e(TAG, "当状态"+OrderListState.getInstance().getState());
//			Log.e(TAG, "isClearFinishState:"+OrderListState.getInstance().isClearFinishState());
//			Log.e(TAG, "isClearOrderState:"+OrderListState.getInstance().isClearOrderState());
//			Log.e(TAG, "isOrderFinishState:"+OrderListState.getInstance().isOrderFinishState());
//			Log.e(TAG, "isHandSearchState:"+OrderListState.getInstance().isHandSearchState());
//			Log.e(TAG, "isAutoSearchState:"+OrderListState.getInstance().isAutoSearchState());
//			Log.e(TAG, "isModifyOrderState:"+OrderListState.getInstance().isModifyOrderState());
//			if(!OrderListState.getInstance().isClearFinishState()&&
//					OrderListState.getInstance().isClearOrderState()&&
//					OrderListState.getInstance().isOrderFinishState()&&
//					!OrderListState.getInstance().isModifyOrderState()&&
//					!OrderListState.getInstance().isAutoSearchState()&&
//					!OrderListState.getInstance().isHandSearchState()){
////				startUpdataLocalData();
//				startTime =System.currentTimeMillis();
//			}else{
//				startTime =System.currentTimeMillis();
//			}
//		}
//	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

//	private void startUpdataLocalData() {
//		new Thread(new Runnable() {
//
//			@Override
//			public void run() {
//				// 去完成一次同步订单操作；
//				updateLocalData();
//			}
//		}).start();
//	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG,"ShareUiService------onDestroy");
	}

	@Override
	public boolean doSucess(String url,byte[] buffer) {
		// TODO Auto-generated method stub
		if(url.contains(Constant.GET_SHARE)){
			doGetShareInfo(buffer);
		}
		return true;
	}

	/**
	 * 实时的车位分享信息
	 * @param object
	 */
	private void doGetShareInfo(byte[] object) {
		// TODO Auto-generated method stub
		InputStream is = new ByteArrayInputStream(object);
		try {
			ShaerUiInfo info = ShareUiInfoParser.getUpdataInfo(is);
			is.close();
			Log.e(TAG, "获取到得分享信息为"+info.toString());
			if(info.getResult()!=null&&"fail".equals(info.getResult())){
				Log.e(TAG, "检查token的状态--token无效");
				Message msg = new Message();
				msg.what = 4;//token失效
				if(zldNewActivity != null){
					zldNewActivity.finish();
				}
				Intent intent = new Intent(ShareUiService.this, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("token", "false");
				startActivity(intent);
				stopSelf();
			}else{
				Message msg = new Message();
				msg.what = Constant.PARKING_NUMS_MSG;
				msg.obj = info;
				if(zldNewActivity != null){
					zldNewActivity.handler.dispatchMessage(msg);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 登录后的同步
	 */
//	public void doSynchronize(){
//		long lineTime = SharedPreferencesUtils.getParam(
//				getApplicationContext(), "zld_config", "linetime", 0L);
//		if (0 == lineTime) {
//			new Thread(new Runnable() {// 去同服务器时间；
//				@Override
//				public void run() {
//					Log.e(TAG,"同步服务器时间");
//					MySynchronizeUitls.SynchronizeTime(ShareUiService.this);
//				}
//			}).start();
//		}
//		new Thread(new Runnable() {// 去同步车场的价格；
//			@Override
//			public void run() {
//				Log.e(TAG,"同步车场价格");
//				MySynchronizeUitls.SynchronizePrice(loDBManager,ShareUiService.this);
//			}
//		}).start();
//		new Thread(new Runnable() {// 下载月卡车牌号
//			@Override
//			public void run() {
//				Log.e(TAG,"同步车场月卡");
//				MySynchronizeUitls.SynchronizeMonthCard(loDBManager,ShareUiService.this);
//			}
//		}).start();
//	}

	/**
	 * 去同步线上数据库的当前订单数据；
	 */
//	public void updateLocalData(){
//		if (!IsNetWork.IsHaveInternet(ShareUiService.this)) {
//			return;
//		}
//		if (loDBManager.isOrdertbEmpty()) {
//			//去同步服务端今日所有当前订单（插入到本地数据库）；记录订单的最大编号;
//			Log.e(TAG, "订单表为空！！！！");
//			downloadOrder(this);
//		}else {
//			updateOrder(this);
//			//数据库中订单时间大于当前时间五天的 就删除
//			deleteSqliteOrder();
//		}
//	}

//	private void deleteSqliteOrder() {
//		// TODO Auto-generated method stub
//		if(loDBManager != null){
//			loDBManager.deleteSqliteOrder();
//		}
//	}

	/**
	 * 访问服务器获得服务器时间。
	 * 因操作数据库需要在子线程中运行！！
	 * @param context
	 */
//	public void downloadOrder(final Context context) {
//		String url = 
//				Constant.requestUrl+"local.do?action=firstDownloadOrder&token="+
//						AppInfo.getInstance().getToken();
//		Log.e(TAG, "请求下载服务器订单的url："+url);
//		AQuery aq = new AQuery(context);
//		aq.ajax(url, String.class, new AjaxCallback<String>() {
//			@Override
//			public void callback(String url, String object, AjaxStatus status) {
//				super.callback(url, object, status);
//				if (!TextUtils.isEmpty(object)) {
//					Log.e(TAG,	 "服务器返回的要同步订单是："+object);
//					try {
//						JSONObject json = new JSONObject(object);
//						String maxid = json.getString("maxid");
//						SharedPreferencesUtils.setParam(
//								getApplicationContext(), "zld_config", "maxid", maxid);
//						Gson gson = new Gson();
//						List<Order_tb> list  = gson.fromJson(json.getString("orders"), new TypeToken<List<Order_tb>>() {}.getType());
//						loDBManager.addMoreOrder(list);
//						Log.e(TAG, "当前订单下载完毕：---------------!!!");
//						zldNewActivity.getMoney();
//						zldNewActivity.refreshListOrder();
//					} catch (JSONException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				}
//			}
//		});
//	}

	/**
	 *返回新生成的订单,本地订单号替换,最大订单编号,要删除的订单编号；
	 * @param context
	 */
//	public void updateOrder(final Context context){
//		if(sm == null){
//			sm = ((application) getApplicationContext()).getSqliteManager(this);
//		}
//		//StringBuffer Ids = loDBManager.getcurrOrderIds();//当前未结算的订单号集合；
//		JSONArray updateOrders = loDBManager.getUpdateOrders();//本地操作过的订单；
//		String maxid = SharedPreferencesUtils.getParam(getApplicationContext(), "zld_config", "maxid", "0");
//		String url = Constant.requestUrl+Constant.SYNCHRO_ORDER;
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put("token", AppInfo.getInstance().getToken());
//		params.put("maxid", maxid);
//		//params.put("ids", Ids.toString());
//		params.put("orders", "{\"data\":"+updateOrders+"}");
//		Log.e(TAG,"======"+updateOrders.toString());
//		Log.e(TAG, "请求同步服务器订单的url："+url+params.toString());
//		FileUtil.writeSDFile("保存到文件", ""+url+params.toString());
//		HttpManager.requestSynchronizeOrder(context, url, params, ShareUiService.this);
//	}

//	@Override
//	public boolean doSucess(String url, String object) {
//		// TODO Auto-generated method stub
//		if(url.contains(Constant.SYNCHRO_ORDER)){
//			if (!TextUtils.isEmpty(object)) {
//				Gson gson = new Gson();
//				synchroUpdateInfo info = gson.fromJson(object,synchroUpdateInfo.class);
//				Log.e(TAG, "解析同步服务器订单的结果："+info.toString());
//				if (info.getOrders() != null && info.getOrders().size() > 0) {
////					loDBManager.addMoreOrder(info.getOrders());
//				}
//				ArrayList<String> lineList = null;
//				if (info.getRelation() != null && info.getRelation().size() > 0) {
//					lineList = new ArrayList<String>();
//					for(int i=0;i<info.getRelation().size();i++){
//						Relation relation = info.getRelation().get(i);
//						if(relation != null){
//							lineList.add(relation.getLine());
//						}
//					}
////					loDBManager.updateOrder(info.getRelation());
//					sm.updateImgOrderid(info.getRelation());
//				}
//				if (!TextUtils.isEmpty(info.getMaxid())) {
//					SharedPreferencesUtils.setParam(
//							getApplicationContext(), "zld_config", "maxid",info.getMaxid());
//				}
//				//orderid长度小于30的集合
//				recursionUpload();
//				if (!TextUtils.isEmpty(info.getDelOrderIds())) {
//					String[] split = info.getDelOrderIds().split(",");
//					if(split!=null&&split.length>0){
////						loDBManager.deleteMoreOrder(split);
//					}
//				}
//				Log.e(TAG, "完成一次同步数据过程--删除已结算状态的订单！");
//				if(zldNewActivity!=null){
//					// 同步后,获取收费金额
//					zldNewActivity.getMoney();
//					//离场列表状态,不支持本地化的车场 不刷新
//					if(!OrderListState.getInstance().isParkOutState()&&
//							AppInfo.getInstance().getIssuplocal().equals("1")){
//						// 若为当前列表状态,则刷新
//						zldNewActivity.refreshListOrder();
//					}
//				}
//			}
//		}
//		return true;
//	}

	@Override
	public void timeout(String url) {
		// TODO Auto-generated method stub
		super.timeout(url);
	}
	/**
	 * 查询数据库有信息 上传
	 */
	private void recursionUpload() {
		int i = 0;
		boolean isHave = true;
		while(isHave){
			ArrayList<UploadImg> selectOrderid = sm.selectOrderid();
			if(selectOrderid!=null&&selectOrderid.size()>0){
				if(i>=selectOrderid.size()){
					isHave = false;
					break;
				}
				UploadImg uploadImg = selectOrderid.get(i);
				if(uploadImg!=null){
					String orderid = uploadImg.getOrderid();
					String lefttop = uploadImg.getLefttop();
					String rightbottom = uploadImg.getRightbottom();
					String width = uploadImg.getWidth();
					String height = uploadImg.getHeight();
					String carnumber = uploadImg.getCarnumber();
					String imghomepath = uploadImg.getImghomepath();
					String homeimgup = uploadImg.getHomeimgup();
					String exitimgup = uploadImg.getExitimgup();
					//有入口图片
					if(imghomepath!=null&&homeimgup!=null&&homeimgup.equals("0")){
						Log.e(TAG,"orderid长度小于30的入口图片信息："+uploadImg.toEasyString());
						Bitmap bitmap = BitmapFactory.decodeFile(imghomepath);
						if(bitmap != null){
							InputStream bitmapToInputStream = ImageUitls.getBitmapInputStream(netType,bitmap);
							upload(bitmapToInputStream, orderid, Constant.HOME_PHOTOTYPE, lefttop, rightbottom, width, height, carnumber);	
						}
						break;
					}
					String imgexitpath = uploadImg.getImgexitpath();
					//有出口图片
					if(imgexitpath!=null&&exitimgup!=null&&exitimgup.equals("0")){
						Log.e(TAG,"orderid长度小于30的出口图片信息："+uploadImg.toEasyString());
						Bitmap bitmap = BitmapFactory.decodeFile(imgexitpath);
						if(bitmap != null){
							InputStream bitmapToInputStream = ImageUitls.getBitmapInputStream(netType,bitmap);
							upload(bitmapToInputStream, orderid, Constant.EXIT_PHOTOTYPE, lefttop, rightbottom, width, height, carnumber);	
						}
						break;
					}
					i++;
				}else{
					i++;
					break;
				}
			}else{
				isHave = false;
				break;
			}
		}
	}

	/**
	 * 上传图片到服务器
	 */
	public void upload(
			InputStream bitmapToInputStream, String orderId,
			int type,String x, String y,String width, String height,String carPlate) {
		UpLoadImage upLoadImage = new UpLoadImage();
		upLoadImage.setPhotoType(type);
		upLoadImage.setmHandler(handler);
		upLoadImage.setComid(AppInfo.getInstance().getComid());
		upLoadImage.upload(bitmapToInputStream, orderId,
				x + "",	y + "", width + "",	height + "", carPlate);

	}

	/**
	 * 查询数据库,删除图片文件及数据库图片信息
	 * @param orderId
	 */
	private void deleteOrderIamgeInfo(String orderId) {
		if(orderId == null){
			return;
		}
		UploadImg selectImage = sm.selectImage(orderId);
		if (selectImage != null) {
			String imgpath = selectImage.getImgexitpath();
			if (imgpath != null) {
				Log.e(TAG, "删除sd卡图片" + imgpath);
				ImageUitls.deleteImageFile(imgpath);
				Log.e(TAG, "数据库对应订单信息" + orderId);
				sm.deleteData(orderId);
			}
		}
	}

//	/**
//	 * 文件定期删除
//	 */
//	private void fileRegularDelete() {
//		File file = new File(Constant.FRAME_DUMP_FOLDER_PATH);
//		if(file != null){
//			File[] listFiles = file.listFiles();
//			if(listFiles == null){
//				return;
//			}
//			int listFilesLength = listFiles.length;
//			//如果文件个数大于3500个,5天，最大一天700辆车,图片300k一个，相当于1G
//			if(listFilesLength > Constant.DELETE_IMAGE){
//				long currentTime = System.currentTimeMillis();
//				ArrayList<File> deleList = new ArrayList<File>();
//				for(int i=0;i<listFilesLength;i++){
//					if(listFiles[i].isFile()){
//						long lastModified = listFiles[i].lastModified();
//						if((currentTime - Constant.ONEDAYTAMP) >lastModified){
//							Log.e(TAG,"删除文件名："+listFiles[i].getName());
//							deleList.add(listFiles[i]);
//						}
//					}
//				}
//				int size = deleList.size();
//				if(size>0){
//					for(int i=0;i<size;i++){
//						deleList.get(i).delete();
//					}
//				}
//			}
//		}
//	}

	/**
	 * 数据库信息定期删除
	 */
//	private void dbDataRegularDelete(){
//		if(loDBManager == null){
//			loDBManager = initSqliteManager();
//		}
//		long time = System.currentTimeMillis();
//		time = time - 1296000;
//		Log.e(TAG, "删除数据库信息的时间："+time);
//		boolean selectOrder = loDBManager.selectOrder(""+time);
//		Log.e(TAG,"查询数据库的时间："+time);
//		//		deleteOrderIamgeInfo
//	}




}

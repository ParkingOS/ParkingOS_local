package com.zld.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zld.application;
import com.zld.bean.AllOrder;
import com.zld.bean.AppInfo;
import com.zld.bean.AutoDepartureOrder;
import com.zld.bean.CurrentOrder;
import com.zld.bean.DepartureInfo;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.HttpManager;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.BitmapUtil;
import com.zld.lib.util.ImageUitls;
import com.zld.lib.util.PollingUtils;
import com.zld.lib.util.TimeTypeUtil;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

public class DownLoadService extends BaseService {

	private String uid;
	private String token;
	private SqliteManager sm;
	private ImageLoader imageLoader;
	private static final String TAG = "DownLoadService";

	// 订单时间小于1小时 的则下载
	private static long FIVETIMESTAMP = 1*60*60*1000;// 60分 以内的毫秒
	
//	// 判断最后修改日期是否小于五天前，是则删除
//	private static long FIVEDAYTAMP = 6*60*1000;
//	// 订单时间小于10分钟 的则下载
//	private static long FIVETIMESTAMP = 1*10*60*1000;
	private String comid;
	private int PHOTOTYPE = 0;
	private ArrayList<String> orderidList;
	private ArrayList<String> sameList;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		token = AppInfo.getInstance().getToken();
		if (token == null) {
			PollingUtils.stopPollingService(this,DownLoadService.class,"com.zld.service.DownLoadImage_Service");
			stopSelf();
			return;
		}
		if(imageLoader == null){
			imageLoader = ((application)getApplication()).getImageLoader();
		}
		if(sm == null){
			sm = ((application)getApplication()).getSqliteManager(DownLoadService.this);
		}
		uid = AppInfo.getInstance().getUid();
		comid = AppInfo.getInstance().getComid();
//		getOrder();
//		fileRegularDelete();
	}

	/**	
	 * 获取1500个订单信息 比较本地数据库，没有的则下载。本地数据库多的就删除
	 * page与size为null
	 */
//	private void getOrder(){
//		RequestParams params = new RequestParams();
//		params.setUrlHeader(Constant.requestUrl +Constant.GET_CURRORDER);
//		params.setUrlParams("comid", AppInfo.getInstance().getComid());
//		params.setUrlParams("page", "");
//		params.setUrlParams("size", "1000");
//		params.setUrlParams("through", 3);
//		String url = params.getRequstUrl();
//		Log.e(TAG, "获取车场所有当前订单url---------------->>" + url);
//		HttpManager.requestGET(this,url,this);
//	}

	private void deleteImage(UploadImg selectImage) {
		String imgpath = selectImage.getImghomepath();
		if(imgpath != null){
			ImageUitls.deleteImageFile(imgpath);
		}
	}

	/**
	 * 查询对应订单信息；
	 * http://192.168.199.239/zld/cobp.do?action=queryorder&comid=1197&carnumber=%be%a9JA6036 
	 * @param carNumber
	 * @throws UnsupportedEncodingException 
	 */
	public void queryCarNumberOrder(String carNumber) throws UnsupportedEncodingException{
		RequestParams params = new RequestParams();
		params.setUrlHeader(Constant.requestUrl + Constant.QUERY_ORDER);
		params.setUrlParams("comid", AppInfo.getInstance().getComid());
		params.setUrlParams("carnumber", URLEncoder.encode(carNumber, "utf-8"));
		params.setUrlParams("through", 3);
		String url = params.getRequstUrl();
		Log.e(TAG, "车牌查询订单的URL---------------->>" + url);
		HttpManager.requestGET(this, url,this);	
	}

	/**
	 * 从网络上获取对应订单的图片；
	 */
	public void getCarPhoto(final DepartureInfo info) {
		if(info != null){
			String orderid = info.getId();
			if (info.getWidth() != null&&info.getHeight() != null&&
					info.getLefttop() != null &&info.getRightbottom() != null){
				RequestParams params = new RequestParams();
				params.setUrlHeader(Constant.requestUrl + Constant.DOWNLOAD_IMAGE);
				params.setUrlParams("comid", AppInfo.getInstance().getComid());
				params.setUrlParams("orderid", orderid);
				params.setUrlParams("type", 0);
				String uri = params.getRequstUrl();
				Log.e(TAG, "照片的uri地址是-->>"+uri);
				imageLoader.loadImage(uri, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Start");
					}

					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Fail");
					}

					@SuppressLint("NewApi")
					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Complete");
						if(info.getWidth() != null&&info.getHeight() != null
								&&info.getLefttop() != null&& info.getRightbottom() != null){
							if (!info.getWidth().equals("null") && !info.getHeight().equals("null") &&
									!info.getLefttop().equals("null") && !info.getRightbottom().equals("null")){
								Bitmap bitmap = BitmapUtil.zoomImg(arg2, 1280, 720);
								//保存图片，保存图片信息
								ImageUitls.SaveImageInfo(sm,bitmap, uid, info.getCarnumber(),info.getId(), info.getLefttop(),
										info.getRightbottom(), Constant.HOME_PHOTOTYPE+"", info.getWidth(), info.getHeight());
							}
						}else{
							Log.e(TAG, "--->>"+"Complete--图片大小未知");
						}
					}
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						Log.e(TAG, "--->>"+"Cancelle");
					}
				});
			}else{
				Log.e(TAG, "--->>"+"Complete--图片信息未知");
			}
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("DownLoadImage_Service----:onDestroy");
	}
	
	@Override
	public boolean doSucess(String url, String object) {
		// TODO Auto-generated method stub
		Log.e(TAG, "doSucess---------------->>" + url);
		if (url.contains(Constant.GET_CURRORDER)){
			Log.e(TAG, "获取订单信息为："+Constant.GET_CURRORDER+"---------------->>" + object);
			doGetOrderResult(object);
		}else if(url.contains(Constant.QUERY_ORDER)){
			Log.e(TAG, "获取车牌查询订单为："+Constant.QUERY_ORDER+"---------------->>" + object);
			doQueryOrderResult(object);
		}
		return false;
	}


	/**
	 * 获取车场所有订单结果
	 * @param object
	 */
	private void doGetOrderResult(String object) {
		// TODO Auto-generated method stub
		if(object.equals("-1")){
			return;
		}
		Gson gson = new Gson();
		CurrentOrder orders = gson.fromJson(object, CurrentOrder.class);
		ArrayList<AllOrder> info = orders.getInfo();
		int infosize = info.size();
		if (orders == null || infosize == 0) {
			return;
		}
		 Log.e(TAG, "解析车场所有当前订单为-->>"+orders.toString());
		ArrayList<String> smOrderidList = sm.selectAllOrderid();
		if(smOrderidList == null){
			return;
		}
		int smOrderidSize = smOrderidList.size();
		 Log.e(TAG, "数据库当前订单数量为-->>"+smOrderidSize);
		// 所有订单Orderid的集合
		orderidList = new ArrayList<String>();
		// 数据库中与所有订单相同Orderid的集合
		sameList = new ArrayList<String>();
		if(smOrderidSize >= 0){
			for(int i = 0;i < infosize;i++){
				String orderid  = info.get(i).getId();
				orderidList.add(orderid);
				for(int j = 0;j < smOrderidSize;j++){
					if(smOrderidList.get(j).equals(orderid)){
						sameList.add(smOrderidList.get(j));
					}
				}
			}
			 Log.e(TAG, "数据库信息："+smOrderidList.toString());
		}
		 Log.e(TAG, "网络上获取到的订单信息:"+orderidList.toString());
		 Log.e(TAG, "相同的订单信息:"+sameList.toString());
		ArrayList<String> netDifList = orderidList;
		ArrayList<String> smDifList = smOrderidList;
		//网络获取的订单，去除与数据库相同的订单；然后获取5分钟以内的--下载
		netDifList.removeAll(sameList);
		//数据库获取的订单，去除与网络相同的订单；然后删除
		smDifList.removeAll(sameList);
		for(int j = 0;j < netDifList.size();j++){
			String orderid = netDifList.get(j);
			for(int i = 0;i < infosize;i++){
				AllOrder allOrder = info.get(i);
				if(allOrder.getId() == orderid){
					String btime = allOrder.getBtime();
					Long longTime = TimeTypeUtil.getLongTime(btime);
					long restTime = System.currentTimeMillis() - longTime;
					UploadImg selectImage = sm.selectImage(allOrder.getId());
					if(selectImage == null){
						//时间小于五分钟
						if (restTime < FIVETIMESTAMP) {
							try {
								queryCarNumberOrder(URLEncoder.encode(
										allOrder.getCarnumber(), "utf-8"));
							} catch (UnsupportedEncodingException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		for(int l = 0;l<smDifList.size();l++){
			for(int k = 0;k <smOrderidList.size();k++){
				if(smDifList.get(l) == smOrderidList.get(k)){
					UploadImg selectImage = sm.selectImage(smOrderidList.get(k));
					if(selectImage != null&&selectImage.getOrderid()!=null){
						//此处不直接删除，是因为本地化生成的订单，orderid在线上是没有的
						Log.e(TAG, "数据库中有的图片,线上订单没有的图片信息："+selectImage.toString());
						if(selectImage.getOrderid().length()<30){
							deleteImage(selectImage);
							sm.deleteData(smOrderidList.get(k));
						}
					}
				}
			}
		}
	}
	
	

	/**
	 *  搜索车牌号
	 */
	private void doQueryOrderResult(String object) {
		Gson gson = new Gson();
		AutoDepartureOrder orders = gson.fromJson(object, AutoDepartureOrder.class);
		if(orders != null){
			//获取对应入口订单图片，显示对应订单图片
			getCarPhoto(orders.getInfo().get(0));
		}
	}

	@Override
	public boolean doFailure(String url, String status) {
		// TODO Auto-generated method stub
		return false;
	}
}

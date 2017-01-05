/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月13日 
 * 
 *******************************************************************************/ 
package com.zld.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.zld.R;
import com.zld.application;
import com.zld.bean.AppInfo;
import com.zld.bean.CarNumberOrder;
import com.zld.bean.UploadImg;
import com.zld.db.SqliteManager;
import com.zld.lib.constant.Constant;
import com.zld.lib.http.RequestParams;
import com.zld.lib.util.BitmapUtil;
import com.zld.lib.util.FileUtil;
import com.zld.lib.util.ImageUitls;

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
public class ParkrecordFragment extends Fragment{
	private ParkRecordListener myRecordListener;
	private FrameLayout fl_tcb_loading_right;
	private ImageView iv_park_record;
	@SuppressWarnings("unused")
	private TextView tv_park_record;
	private ImageLoader imageLoader;
	private application application;
	private SqliteManager sqliteManager;
	private String TAG = "ParkrecordFragment";
	private int HOME_PHOTOTYPE = 0;
	@SuppressWarnings("unused")
	private int EXIT_PHOTOTYPE = 1;
	private String orderid;
	private DisplayImageOptions options;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.park_record, container,
				false);
		initView(rootView);
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		myRecordListener = (ParkRecordListener) activity;
	}

	/**
	 * 初始化控件
	 */
	private void initView(View rootView) {
		iv_park_record =(ImageView) rootView.findViewById(R.id.iv_park_record);
		tv_park_record = (TextView) rootView.findViewById(R.id.tv_park_record);
		fl_tcb_loading_right = (FrameLayout) rootView.findViewById(R.id.fl_tcb_loading_right);
		application = (application)getActivity().getApplication();
		sqliteManager = application.getSqliteManager(getActivity());
	}

	private void clearView(){
		iv_park_record.setImageBitmap(null);
	}

	public void refreshView(CarNumberOrder order){
		if (order == null){
			clearView();
			return;
		}
		getCarPhoto(order);
	}

	/**
	 * 从网络上获取对应订单的图片；
	 */
	@SuppressLint("NewApi")
	public void getCarPhoto(final CarNumberOrder info) {
		Log.e(TAG, "getCarPhoto");
		if(imageLoader == null){
			imageLoader = application.getImageLoader();
		}
		fl_tcb_loading_right.setVisibility(View.INVISIBLE);
		if(options == null){
			options = new DisplayImageOptions.Builder()  
			.showImageOnLoading(R.drawable.home_car_icon)
			.showImageForEmptyUri(R.drawable.home_car_icon)
			.showImageOnFail(R.drawable.home_car_icon)
			.cacheInMemory(true)
			.build();
		}

		if(info != null){
			orderid = info.getOrderid();
			final String carnumber = info.getCarnumber();
			final UploadImg selectImage = sqliteManager.selectImage(orderid);
			if(selectImage == null){
				RequestParams params = new RequestParams();
				params.setUrlHeader(Constant.requestUrl + "carpicsup.do?action=downloadpic");
				params.setUrlParams("comid", AppInfo.getInstance().getComid());
				params.setUrlParams("orderid", orderid);
				params.setUrlParams("type", HOME_PHOTOTYPE);
				String uri = params.getRequstUrl();
				Log.e(TAG, "照片的uri地址是-->>"+uri);

				imageLoader.displayImage(uri, iv_park_record,options, new ImageLoadingListener() {
					@Override
					public void onLoadingStarted(String arg0, View arg1) {
						// TODO Auto-generated method stub
						fl_tcb_loading_right.setVisibility(View.VISIBLE);
					}

					@SuppressWarnings("unused")
					@Override
					public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
						// TODO Auto-generated method stub
						fl_tcb_loading_right.setVisibility(View.INVISIBLE);
						//iv_plate_show.setImageResource(R.drawable.plate_sample);
						if(selectImage != null){
							sqliteManager.deleteData(selectImage.getOrderid());
						}
					}

					@Override
					public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
						fl_tcb_loading_right.setVisibility(View.INVISIBLE);
						// TODO Auto-generated method stub
						if(info.getWidth() != null &&info.getHeight() != null
								&&info.getLefttop() != null&&info.getRightbottom() != null){
							if(!(info.getWidth().equals("null" )) && !(info.getHeight().equals("null"))
									&&(!info.getLefttop().equals("null"))&&(!info.getRightbottom().equals("null"))){
								iv_park_record.setDrawingCacheEnabled(true);
								Bitmap obmp = iv_park_record.getDrawingCache();
								obmp = BitmapUtil.zoomImg(obmp, 1280, 720);
								iv_park_record.setDrawingCacheEnabled(false);

								int x = Integer.parseInt(info.getLefttop());
								int y =  Integer.parseInt(info.getRightbottom());
								int width =  Integer.parseInt(info.getWidth());
								int height =  Integer.parseInt(info.getHeight());
								if(obmp != null){
									saveImage(carnumber, obmp, ""+x, ""+y, ""+width, ""+height);
									if(x+width <= obmp.getWidth()&&
											y+height <= obmp.getHeight()){
										myRecordListener.refreshSmallCarPlate(null);
										if(x<10&&y<10&&width<10&&height<10){
											myRecordListener.refreshSmallCarPlate(null);
										}else{
											Log.e(TAG, "其中某个参数可能为负数");
											if(x>0&&y>0&&width>0&&height>0){
												Bitmap smallBitmap = Bitmap.createBitmap(obmp, x, y, width, height);
												myRecordListener.refreshSmallCarPlate(smallBitmap);
											}
										}
									}else{
										myRecordListener.refreshSmallCarPlate(null);
									}
								}									
							}
						}else{
							fl_tcb_loading_right.setVisibility(View.INVISIBLE);
						}
					}
					@Override
					public void onLoadingCancelled(String arg0, View arg1) {
						// TODO Auto-generated method stub
						fl_tcb_loading_right.setVisibility(View.INVISIBLE);
					}
				});
			}else{
				Log.e(TAG, "数据库存在对应orderid的信息");
				final UploadImg uploadImg = selectImage;
				if(FileUtil.getSDCardPath() != null){
					if(uploadImg.getImghomepath() != null){
						imageLoader.displayImage("file://"+ uploadImg.getImghomepath(), iv_park_record,options,new ImageLoadingListener() {

							@Override
							public void onLoadingStarted(String arg0, View arg1) {
								// TODO Auto-generated method stub
								Log.e(TAG, "加载数据库图片:start");
							}

							@Override
							public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
								// TODO Auto-generated method stub
								Log.e(TAG, "加载数据库图片:fail");
								if(selectImage != null){
									sqliteManager.deleteData(selectImage.getOrderid());
								}
							}

							@Override
							public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
								// TODO Auto-generated method stub
								Log.e(TAG, "加载数据库图片:complete");
								Log.e(TAG, "arg2.width:"+arg2.getWidth()+"arg2.height:"+arg2.getHeight());
								iv_park_record.setDrawingCacheEnabled(true);
								Bitmap obmp = iv_park_record.getDrawingCache();
								obmp = BitmapUtil.zoomImg(obmp, 1280, 720);
								iv_park_record.setDrawingCacheEnabled(false);

								int x = Integer.parseInt(uploadImg.getLefttop());
								int y = Integer.parseInt(uploadImg.getRightbottom());
								int width = Integer.parseInt(uploadImg.getWidth());
								int height = Integer.parseInt(uploadImg.getHeight());
								Log.e(TAG, "本地x:"+x+" y:"+y+" width:"+width+" height"+height);
								if(obmp != null&&x+width <= obmp.getWidth()&&
										y+height <= obmp.getHeight()){
									myRecordListener.refreshSmallCarPlate(null);
									if(x<10&&y<10&&width<10&&height<10){
										myRecordListener.refreshSmallCarPlate(null);
									}else{
										Log.e(TAG, "其中某个参数可能为负数");
										if(x>0&&y>0&&width>0&&height>0){
											Bitmap smallBitmap = Bitmap.createBitmap(obmp,x,y,width,height);
											myRecordListener.refreshSmallCarPlate(smallBitmap);
										}
									}
								}else{
									myRecordListener.refreshSmallCarPlate(null);
								} 
							}

							@Override
							public void onLoadingCancelled(String arg0, View arg1) {
								// TODO Auto-generated method stub
								Log.e(TAG, "加载数据库图片:cancell");
							}
						});
					}
				}
			}
		}
	}

	/**
	 * 
	 * 功能说明：保存图片
	 * 日期:	2015年3月13日
	 * @param carnumber
	 * @param orderid
	 */
	private void saveImage(final String carnumber,
			Bitmap bitmap,String x,String y,String width,String height) {
		//本地保存图片及图片信息---原图
		Log.e(TAG, "carnumber:"+carnumber+" orderid:"+orderid);
		ImageUitls.SaveImageInfo(sqliteManager,bitmap,
				AppInfo.getInstance().getUid(),carnumber, orderid, x,y, ""+Constant.HOME_PHOTOTYPE, width, height);
	}

	public interface ParkRecordListener{
		public void refreshSmallCarPlate(Bitmap bitmap);
	}
}

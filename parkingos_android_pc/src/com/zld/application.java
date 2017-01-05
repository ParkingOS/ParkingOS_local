package com.zld;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.vzvison.database.SnapImageTable;
import com.vzvison.database.plateCallbackInfoTable;
import com.vzvison.device.DeviceSet;
import com.vzvison.vz.WlistVehicle;
import com.zld.db.SqliteManager;
import com.zld.decode.CrashHandler;
import com.zld.ui.BaseActivity;
import com.zld.ui.HelloActivity;
import com.zld.ui.LoginActivity;
import com.zld.ui.ZldNewActivity;

public class application extends Application {

	private BaseActivity baseActivity;
	private HelloActivity helloActivity;
	private LoginActivity loginActivity;
	private ZldNewActivity zldNewActivity;
	private SqliteManager mSqliteManager = null;
//	private LocalOrderDBManager localOrderDBManager = null;
	private ImageLoader mImageLoader;
	private static application sInstance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		//错误日志 保存本地
		initImageLoader();
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		sInstance = this;
	}
	public static Context getAppContext() {
		return sInstance;
	}
	public ZldNewActivity getZldNewActivity() {
		return zldNewActivity;
	}
	
	public void setZldNewActivity(ZldNewActivity zldNewActivity) {
		this.zldNewActivity = zldNewActivity;
	}

	public SqliteManager getSqliteManager(Context mContext){
		if(mSqliteManager == null){
			mSqliteManager = new SqliteManager(mContext);
		}
		return mSqliteManager;
	}
	
//	public LocalOrderDBManager getLocalOrderDBManager(Context mContext){
//		if(localOrderDBManager == null){
//			localOrderDBManager = new LocalOrderDBManager(mContext);
//		}
//		return localOrderDBManager;
//	}

	public ImageLoader getImageLoader(){
		mImageLoader = ImageLoader.getInstance(); 
		DisplayImageOptions options = new DisplayImageOptions.Builder()  
		.showImageOnLoading(R.drawable.home_car_icon) //设置图片在下载期间显示的图片  
		.showImageForEmptyUri(R.drawable.home_car_icon)//设置图片Uri为空或是错误的时候显示的图片  
		.showImageOnFail(R.drawable.home_car_icon)  //设置图片加载/解码过程中错误时候显示的图片
		.cacheInMemory(true)//设置下载的图片是否缓存在内存中  
		.build();//构建完成  
		////创建默认的ImageLoader配置参数  	线程池为5		打印log信息 
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.defaultDisplayImageOptions(options).threadPoolSize(5).writeDebugLogs().build();
		//初始化ImageLoader
		mImageLoader.init(config);
		return mImageLoader;
	}
	private void initImageLoader() {
		// TODO Auto-generated method stub
		DisplayImageOptions options = new DisplayImageOptions.Builder()
		.resetViewBeforeLoading(false).cacheInMemory(false)
		.cacheOnDisk(false).considerExifParams(true)
		.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
		.bitmapConfig(Bitmap.Config.ARGB_8888)
		.displayer(new SimpleBitmapDisplayer()).build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
		.memoryCacheExtraOptions(800, 1080)
		.threadPoolSize(3)
		.threadPriority(Thread.MIN_PRIORITY)
		.tasksProcessingOrder(QueueProcessingType.LIFO)
		.denyCacheImageMultipleSizesInMemory()
		.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		.memoryCacheSize(2 * 1024 * 1024)
		.memoryCacheSizePercentage(15)
		.imageDownloader(new BaseImageDownloader(getApplicationContext()))
		.imageDecoder(new BaseImageDecoder(true))
		.defaultDisplayImageOptions(options).build();
		ImageLoader.getInstance().init(config);
	}

	public HelloActivity getHelloActivity() {
		return helloActivity;
	}
	public void setHelloActivity(HelloActivity helloActivity) {
		this.helloActivity = helloActivity;
	}

	public LoginActivity getLoginActivity() {
		return loginActivity;
	}

	public void setLoginActivity(LoginActivity loginActivity) {
		this.loginActivity = loginActivity;
	}

	public BaseActivity getBaseActivity() {
		return baseActivity;
	}

	public void setBaseActivity(BaseActivity baseActivity) {
		this.baseActivity = baseActivity;
	}

	public void closeActivity(){
		if(loginActivity != null){
			loginActivity.finish();
		}
		if(zldNewActivity != null){
			zldNewActivity.finish();
		}
		android.os.Process.killProcess(android.os.Process.myPid());  
		System.exit(0);
	}
	
	private plateCallbackInfoTable  plateTable =null;
	private DeviceSet               devSet = null;
	private SnapImageTable          snapImageTable = null;
	private WlistVehicle          wlistVechile = null;
	

	
	public void setplateCallbackInfoTable(plateCallbackInfoTable table)
	{
		plateTable = table;
	}
	
	public	SnapImageTable getSnapImageTable()
	{
		return snapImageTable;
	}
	
	public	void setSnapImageTable(SnapImageTable table)
	{
		snapImageTable = table;
	}
	
	public	plateCallbackInfoTable getplateCallbackInfoTable()
	{
		return plateTable;
	}
	
	public	void setDeviceSet(DeviceSet ds)
	{
		devSet = ds;
	}
	
	public	DeviceSet getDeviceSet()
	{
		return devSet;
	}
	
	public	void setWlistVehicle(WlistVehicle ds)
	{
		wlistVechile = ds;
	}
	
	public	WlistVehicle getWlistVehicle()
	{
		return wlistVechile;
	}

}

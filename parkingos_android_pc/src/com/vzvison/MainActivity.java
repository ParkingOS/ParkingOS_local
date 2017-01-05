package com.vzvison;


import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vz.PlateResult;
import com.vz.tcpsdk;
import com.vzvison.database.DeviceInfoTable;
import com.vzvison.database.SnapImageTable;
import com.vzvison.database.plateCallbackInfoTable;
import com.vzvison.database.plateHelper;
import com.vzvison.device.DeviceInfo;
import com.vzvison.device.DeviceManage;
import com.vzvison.device.DeviceSet;
import com.vzvison.device.VedioSetVeiw;
import com.vzvison.monitor.player.MediaPlayer;
import com.zld.R;
import com.zld.application;
import com.zld.lib.constant.Constant;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ActionBar.LayoutParams;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements tcpsdk.OnDataReceiver {
	

 
	
	public static final String deviceNameLabel = "DeviceName";
	public static final String deviceIpLabel = "DeviceIp";
	public static final String devicePortLabel = "DevicePort";
	public static final String UserNameLabel = "UserName";
	public static final String UserPasswordLabel = "UserPassowrd";
	
  
	private DisplayMetrics dm;
	private android.widget.GridLayout     layout;
	
	private LayoutInflater mInflater;
	
	private List<MediaPlayer>  mediaPlayerGroup;
	 
//	private SlideMenu slideMenu;
	
//	private RelativeLayout layout_vedio;
	private DeviceManage   deviceManage;
	
	private int            selectId;
	
	private application m_gb = null;
	
	private RelativeLayout  mainLayout; 
	private CellLayout      celllayout;
	
	private Map<Integer,DeviceSet>  vedioGroup;
	
	private BussionPopWindow mPop = null;
	
 
  
	private DeviceInfoTable   m_DeviceInfoTable = null;
	private boolean          m_zoomInFlag = false;
	private LinearLayout      m_ImgGroup;

	  
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		 System.out.print(" System.out.print(viewid);");
		
		setContentView(R.layout.activity_main);
		
	//	deviceManage = new DeviceManage();
		  
		 tcpsdk.getInstance().setup();
		    
		//requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉标题栏
		
		 plateHelper so = new plateHelper(MainActivity.this,"yitijiDatabase.db",null,1);
		 plateCallbackInfoTable pct = new plateCallbackInfoTable();
		 pct.setDataBaseHelper(so);
		   
		 
		 SnapImageTable sit = new SnapImageTable();
		 sit.setDataBaseHelper(so);
		   
		 android.app.Application ct = (android.app.Application)getApplicationContext();
		 
		 m_gb =  (application) ct;
		 
		 m_gb.setplateCallbackInfoTable(pct);
		 m_gb.setSnapImageTable(sit);
		 
		 m_DeviceInfoTable = new DeviceInfoTable();
		 m_DeviceInfoTable.setDataBaseHelper(so);
		 
		 
			m_gb.getplateCallbackInfoTable().ClearAll();
			m_gb.getSnapImageTable().ClearAll();
			
		//	m_DeviceInfoTable.ClearAll();
			
		int count=	m_DeviceInfoTable.getRowCount();
			
			
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
 	
		try{
			celllayout = /*(android.widget.GridLayout)*/(CellLayout) this.findViewById(R.id.gridLayoutMain);
		}catch(Exception e)
		{
           int a;
           a = 0;
		}
		
		m_ImgGroup = (LinearLayout)findViewById(R.id.linerLayout_main_ImgGroup);
		
		 
		ImageView image = (ImageView)findViewById(R.id.imageView_Trigger);
	//	image.setId(DeviceID );
		image.setOnClickListener(clickListener);
		
//		image = (ImageView)findViewById(R.id.ImageView_PlateInfo);
//		image.setId(PlateInfoID );
//		image.setOnClickListener(clickListener);
		
		image = (ImageView)findViewById(R.id.ImageView_Serial);
	//	image.setId(SerialID );
		image.setOnClickListener(clickListener);
		
		image = (ImageView)findViewById(R.id.ImageView_SnapPicture);
	//	image.setId(CpaturePicID );
		image.setOnClickListener(clickListener);
		
		image = (ImageView)findViewById(R.id.ImageView_voice);
	//	image.setId(VoiceID );
		image.setOnClickListener(clickListener);
		
		image = (ImageView)findViewById(R.id.ImageView_WhiteList);
	//	image.setId(WhlistID );
		image.setOnClickListener(clickListener);
		
		image = (ImageView)findViewById(R.id.ImageView_videoConfig);
	//	image.setId(ConfigVedioID );
		image.setOnClickListener(clickListener);
		image.setVisibility(View.GONE);
		
		image = (ImageView)findViewById(R.id.ImageView_openGate);
		//	image.setId(ConfigVedioID );
		image.setOnClickListener(clickListener);
		
		
        
//		slideMenu = (SlideMenu) findViewById(R.id.slide_menu);
//		ImageView menuImg = (ImageView) findViewById(R.id.title_bar_menu_btn);
//		menuImg.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				switch (v.getId()) {
//				case R.id.title_bar_menu_btn:
//					if (slideMenu.isMainScreenShowing()) {
//						slideMenu.openMenu();
//					} else {
//						slideMenu.closeMenu();
//					}
//					break;
//				}
//				
//			}
//		});
//		
//		TextView plateInfoView = (TextView)slideMenu.findViewById(R.id.TextView_PlateInfo);
//	//	plateInfoView.setId(PlateInfoID );
//		plateInfoView.setOnClickListener(clickListener);
//		
//		TextView snapView = (TextView)slideMenu.findViewById(R.id.TextView_CpaturePicInfo);
//	//	snapView.setId(SnapImageID );
//		snapView.setOnClickListener(clickListener);
//		
//		
//		TextView aboutView = (TextView)slideMenu.findViewById(R.id.TextView_about);
//	//	snapView.setId(SnapImageID );
//		aboutView.setOnClickListener(clickListener);
		
//		layout.setColumnCount(2);
//		layout.setRowCount(2);
//	 
		
//		RelativeLayout.LayoutParams  tempParams =  new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.MATCH_PARENT);
//		WindowManager wm = (WindowManager) MainActivity.this.getSystemService(Context.WINDOW_SERVICE);
//		 tempParams.height = dm.widthPixels *3 /4 ;
//		 tempParams.topMargin = 5;
//        int row,column;
//        
//        //mediaPlayerGroup = new List<MediaPlayer>();
// 
//		 for(int i = 0 ; i < 4; i++)
//		 {  
//			 GridLayout.LayoutParams param = new GridLayout.LayoutParams();  
//			 
//			 row = i / 2;
//			 column = i - row *2;
//			 
//			 param.columnSpec = GridLayout.spec(column);  
//			 param.rowSpec = GridLayout.spec(row); 
//			// param.setMargins(5, 5, 5, 5);
//			 
//			 param.width =  ( dm.widthPixels - 20)/2;
//			 param.height =  (dm.heightPixels - 120- 90) /2;//
//			 param.setGravity( Gravity.FILL);
////			 param.width =  ViewGroup.LayoutParams.FILL_PARENT;
////			 param.height = ViewGroup.LayoutParams.FILL_PARENT;
//			 
//			 MediaPlayer mediaPlayer =  new MediaPlayer(MainActivity.this);
//			// mediaPlayer.setId(id);
//			 //mediaPlayer.setLayoutParams( param);
//			 //mediaPlayer.setPadding(5, 5, 5, 5);
//			 mediaPlayer.setOnClickListener(mediaClickListener);
//			 
//			// mediaPlayerGroup.add(mediaPlayer);
//			 // layout.findViewById(id)
//			 
//			 layout.addView(mediaPlayer,param);
//			 
//			 
//		 }  
	  
		 mainLayout = (RelativeLayout)findViewById(R.id.MainLayout);
		   
		 //mainLayout.setVisibility(View.INVISIBLE);
		 
		 vedioGroup = new HashMap<Integer,DeviceSet>();
//		 for(int i = 0 ; i < 3; i++)
//		 {
//			 MediaPlayer mediaPlayer =  new MediaPlayer(MainActivitfy.this);
// 
//			// mediaPlayer.setPadding(5, 5, 5, 5);
//			 mediaPlayer.setOnClickListener(mediaClickListener);
			  int i=0;
			 DeviceInfo di = new DeviceInfo(10+i);
		 
			 m_DeviceInfoTable.GetCallbackInfo(10+i, di);
			 
             VedioSetVeiw vsv = new VedioSetVeiw(MainActivity.this);
			 
			 vsv.sethandle(handler);
			 vsv.setId(di.id);
			 
		 
			 
			 DeviceSet ds = new DeviceSet( di ,vsv);
			 
			 ds.setPlateInfoCallBack(this, 1);
			 
			 
//			 celllayout.addView(vsv, i);
			 LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
			 vsv.setLayoutParams(lp);
			 mainLayout.addView(vsv);
			 
			 vedioGroup.put(10+i,ds);
//		 }
		 
		 
//		layout_vedio  = (RelativeLayout)findViewById(R.id.LayoutVedio1);
//		 for(int i = 0 ; i < 4; i++)
//		 {
//			 LayoutParams lp  = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
//			 MediaPlayer mediaPlayer =  new MediaPlayer(MainActivity.this);
//			 mediaPlayer.setId(i);
//			 
//			 mediaPlayer.setLayoutParams( lp);
//			 layout_vedio.addView( mediaPlayer);
//		 }
		     
	   
		 
		
//		GridView gridView=(GridView)findViewById(R.id.gridView);  
//		
//		gridView.setAdapter(new ImageAdapter(this));  
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() 
	{
		super.onStart();
		
		
	}
	@Override
	protected void onPause() {
		super.onPause();
		
//		java.util.Iterator it = vedioGroup.entrySet().iterator();
//		while(it.hasNext()){
//		   java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
//		   DeviceSet ds = (DeviceSet)entry.getValue();
//		    
//		   if( (ds != null) && ds.open())
//			{
//			   ds.pause();
//			}
//		}
		
	}
	@Override
	protected void onResume() {
		super.onResume();
		
//		java.util.Iterator it = vedioGroup.entrySet().iterator();
//		while(it.hasNext()){
//		   java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
//		   DeviceSet ds = (DeviceSet)entry.getValue();
//		    
//		   if( (ds != null) && ds.open())
//			{
//			   ds.resum();
//			}
//		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		tcpsdk.getInstance().cleanup();
		 
		m_gb.getplateCallbackInfoTable().ClearAll();
		m_gb.getSnapImageTable().ClearAll();
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig)
	{
		super.onConfigurationChanged(newConfig);
		 
//		int width = layout.getWidth();
//		int heiht = layout.getHeight();
		
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {   //横屏
			m_ImgGroup.setVisibility(View.GONE);
			 
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT){   //竖屏
			m_ImgGroup.setVisibility(View.VISIBLE);  
		} 
	}
	
	@Override
	public boolean onKeyDown(int keyCode,KeyEvent event) {
		
		if( mPop != null && mPop.isShowing())
		{
			mPop.dismiss();
			return true;
		}
		
		
		if( keyCode ==  KeyEvent.KEYCODE_BACK )
		{ 
			showDailog("是否要退出一体机工具?");
			 
		}
		
		return super.onKeyDown(keyCode,event);
	}
	
	private void showDailog(String msg) {  
        AlertDialog.Builder builder = new AlertDialog.Builder(this);  
        builder.setIcon(android.R.drawable.ic_dialog_alert);  
        builder.setTitle("确认退出");  
        builder.setMessage(msg);  
//      builder.setCancelable(false);  
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
  		  public void onClick(DialogInterface dialog, int which) {  
              // TODO Auto-generated method stub  
  			  
  			 
  			java.util.Iterator it = vedioGroup.entrySet().iterator();
			while(it.hasNext()){
			   java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
			   DeviceSet ds = (DeviceSet)entry.getValue();
			      ds.stopVideo();
			      ds.close();
			      
			      DeviceInfo di = ds.getDeviceInfo();
			    
			      if(di != null )
			         m_DeviceInfoTable.put(di.id, di.DeviceName, di.ip, di.port, di.username, di.userpassword);
			      
			    }
  				  
  			     finish();  
  		     }
          }  );  
        builder.setNegativeButton("取消", null);  
        builder.create().show();  
    } 
	
	public void onDataReceive(int handle,PlateResult plateResult,int uNumPlates,int eResultType,
			byte[] pImgFull,int nFullSize, byte[] pImgPlateClip,int nClipSize)
	{
		try
		{
			DeviceSet ds = this.getDeviceSetFromHandle(handle);
			
			if(ds ==null)
			{
				Toast.makeText(MainActivity.this, "车牌回调数据失败:未找到设备", Toast.LENGTH_SHORT).show();
			} 
			
			DeviceInfo di = ds.getDeviceInfo();
			
			String dateText = new String() ;
			
			dateText += plateResult.struBDTime.bdt_year;
			dateText +="/";
			
			dateText += plateResult.struBDTime.bdt_mon;
			dateText +="/";
			 
			dateText += plateResult.struBDTime.bdt_mday;
			dateText +=" ";
			
			dateText += plateResult.struBDTime.bdt_hour;
			dateText +=":";
			
			dateText += plateResult.struBDTime.bdt_min;
			dateText +=":";
			
			dateText += plateResult.struBDTime.bdt_sec;
			 
			String plateText = new String(plateResult.license,"GBK");
			
			if(!m_gb.getplateCallbackInfoTable().addCallbackInfo(di.DeviceName,plateText, dateText, pImgFull, pImgPlateClip))
			{
				Toast.makeText(MainActivity.this, "添加车牌回调数据失败", Toast.LENGTH_SHORT).show();
			}
			
			Log.i("visizion", "decodeByteArray begin");
			
			BitmapFactory.Options options = new BitmapFactory.Options(); 
            options.inSampleSize = 4;//图片宽高都为原来的二分之一，即图片为原来的四分之一 
            options.inInputShareable  = true;
            Bitmap bmp = null;
            try
            {
            	
            	bmp = BitmapFactory.decodeByteArray(pImgFull, 0, pImgFull.length,options);  
               if( bmp != null )
       			{
       				Message msg= new Message();
       				
       				msg.what = Constant.PlateImage;
       				
       				msg.arg1 = ds.getDeviceInfo().id;
       				msg.obj = bmp;
       				Bundle data = new Bundle();
       				data.putString("plate", plateText);
       				msg.setData(data );
       				
       				handler.sendMessage(msg);
       			}
            }
            catch (OutOfMemoryError e)
            {
            	  Log.e("Map", "Tile Loader (241) Out Of Memory Error " + e.getLocalizedMessage());
            	  System.gc();
             }
            catch(Exception e)
            {
            	
            }
            finally
            {
            	Log.i("visizion", "decodeByteArray end");
            }
         
		
		}
		catch (UnsupportedEncodingException e)
		{

			Toast.makeText(MainActivity.this, "不支持的解码异常", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	 
	
	 @SuppressLint("HandlerLeak")
		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				case Constant.SelectVedio:
				{ 
					int vediosetid = msg.arg1;
					 
					java.util.Iterator it = vedioGroup.entrySet().iterator();
					while(it.hasNext()){
					   java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
					   DeviceSet ds = (DeviceSet)entry.getValue();
					   
					  if( (Integer)entry.getKey() == vediosetid )
					  {
						  MainActivity.this.selectId = vediosetid;
						  ds.select();
					  }
					  else
					  {
						  ds.unselect();
						  //vsv.setVisibility(View.GONE);
					  }
					}
				}
					break;
				case Constant.DClickVedio:
				{
					int vediosetid = msg.arg1;
					
					DeviceSet ds = getDeviceSetFromId(vediosetid);
					
					ViewSetInnerType type = (ViewSetInnerType)msg.obj;
					 
					if(!m_zoomInFlag)
					{
						celllayout.ZoomIn(vediosetid-9 );
						
						if(ds != null)
						{
							if(type == ViewSetInnerType.Vedio)
							{
								ds.ZoomInVedio();
							}
							else
							{
								ds.ZoomInImage();
							}
						}
							
						
						m_zoomInFlag = true;
					}
					else
					{
						if(ds != null)
						   if(type == ViewSetInnerType.Vedio)
							{
								ds.ZoomOutVedio();
							}
							else
							{
								ds.ZoomOutImage();
							}
							
						
						celllayout.recover();
						m_zoomInFlag = false;
					}
					  
				}
				break;
			 
				case Constant.ConfigDeivce:
				{
//					int vediosetid = msg.arg1;
//					
//					DeviceSet ds = getDeviceSetFromId(vediosetid);
//					 
//				    if(ds != null )
//					{
//					   Intent  intent = new Intent( MainActivity.this,DeviceActivity.class); 
//						     
//					   intent.putExtra(deviceNameLabel, ds.getDeviceInfo().DeviceName);
//					   intent.putExtra(deviceIpLabel, ds.getDeviceInfo().ip);
//					   intent.putExtra(devicePortLabel, ds.getDeviceInfo().port);
//					   intent.putExtra(UserNameLabel, ds.getDeviceInfo().username);
//						intent.putExtra(UserPasswordLabel, ds.getDeviceInfo().userpassword);
//						     
//						      MainActivity.this.startActivityForResult(intent, 0);
//						      break;
//					}
					
				}
				case Constant.StopVedio:
				{
					int vediosetid = msg.arg1;
					
					DeviceSet ds = MainActivity.this.getDeviceSetFromId(vediosetid);
					if(ds != null)
					{
						ds.stopVideo();
					}
					
				}
				   break;
				case Constant.StartVedio:
				{
                    int vediosetid = msg.arg1;
					
					DeviceSet ds = MainActivity.this.getDeviceSetFromId(vediosetid);
					if(ds != null)
					{
						ds.playVideo();
					}
				}
				   break;
				case Constant.PlateImage:
				{
					Bitmap bmp = (Bitmap)msg.obj;
					
					DeviceSet ds = MainActivity.this.getDeviceSetFromId(msg.arg1);
					
					if(bmp != null)
					{
						
						
						ds.setPlateImage(bmp);
					}
					
					Bundle bundle =  msg.getData();
					
					ds.setTrriglePlateText(bundle.getString("plate"));
					
					
				}
					break;
				default:
					Toast.makeText(MainActivity.this, "未知消息", Toast.LENGTH_SHORT).show();
					break;
				}
				
			};
		};
		
	protected void onActivityResult(int requestCode, int resultCode, Intent intent)
	{
		if( RESULT_OK == resultCode )
		{ 
			Bundle bundle = intent.getExtras();
			
			String devicename = bundle.getString(MainActivity.deviceNameLabel);
			String deviceip = bundle.getString(MainActivity.deviceIpLabel);
			String deviceport = bundle.getString(MainActivity.devicePortLabel);
			String userName = bundle.getString(MainActivity.UserNameLabel);
			String userPassword = bundle.getString(MainActivity.UserPasswordLabel);
			
		 
			DeviceSet ds = this.getDeviceSetFromId(selectId);
			
			if(ds != null)
			{
				DeviceInfo di = ds.getDeviceInfo();
				
//				di.DeviceName = devicename;
//				di.ip = deviceip;
//				di.port = Integer.parseInt(deviceport);
//				di.username = userName;
//				di.userpassword = userPassword;
			    
			  //  if(di.ip != deviceip)
			     {
			    	 
			    	ds.stopVideo();
			        ds.close();
			         
					   
					   if(ds.open(devicename, deviceip, Integer.parseInt( deviceport), userName, userPassword))
					   { 
						   Log.e("------------------", "打开了");
						   ds.isconn();
						  ds.playVideo();
						
					   }
						   
				   }
			}
			 
		}
	 
		 super.onActivityResult(requestCode, resultCode, intent);
	}
	
    private View.OnClickListener clickListener =  new View.OnClickListener(){
		@Override
		public void onClick(View view)
		{
			int id = view.getId();
			switch(id)
			{
//			case R.id.TextView_CpaturePicInfo:
//			{
//				 Intent intent = new Intent(MainActivity.this,SnapImageActivity.class);
//					
//				MainActivity.this.startActivity(intent);
//			}
//			break;
//			case R.id.TextView_PlateInfo:
//			{
//                Intent intent = new Intent(MainActivity.this,PlateActivity.class);
//				
//				MainActivity.this.startActivity(intent);
//			}
//				
//				
//				break;
			case R.id.ImageView_WhiteList:
			{
//				 DeviceSet ds = MainActivity.this.getDeviceSetFromId(selectId);
//				  
//				   if(  ds != null && ds.getopenFlag()  )
//					{
//					   
//					   m_gb.setDeviceSet(ds);
//					   
//					   Intent intent = new Intent(MainActivity.this,WlistActivity.class);
//						
//						MainActivity.this.startActivity(intent);
//					    
//					   break;
//					}
//				   else
//					   Toast.makeText(MainActivity.this, "请先选中或打开设备", Toast.LENGTH_SHORT).show();
				
              
			}
                
				break;
			case R.id.ImageView_voice:
			{
 		
//				if( mPop != null && mPop.isShowing())
//				{
//					mPop.dismiss();
//				
//				}
//				 
//				   DeviceSet ds = MainActivity.this.getDeviceSetFromId(selectId);
//				  
//				   if(  ds != null && ds.getopenFlag()  )
//					{
//					   mPop =  new VoicePopWindow( MainActivity.this,ds);
//					   
//					   mPop.show();
//					   
//					  
//					   break;
//					}
//				   else
//					   Toast.makeText(MainActivity.this, "请先选中或打开设备", Toast.LENGTH_SHORT).show();
			 
					
			}
				
				break;
			case R.id.ImageView_Serial:
			{
				
//				if( mPop != null && mPop.isShowing())
//				{
//					mPop.dismiss();
//				
//				}
//				
//				 
//				   DeviceSet ds = MainActivity.this.getDeviceSetFromId(selectId);
//				  
//				   if(  ds != null && ds.getopenFlag() )
//					{
//					   mPop =  new SerialPopWindow( MainActivity.this,ds);
//					   
//					   mPop.show();
//					  
//					   break;
//					}
//				   else
//					  Toast.makeText(MainActivity.this, "请先选中或打开设备", Toast.LENGTH_SHORT).show();
			}
				break;
			case R.id.imageView_Trigger:
			{
				DeviceSet ds = MainActivity.this.getDeviceSetFromId(selectId);
				
				   if(  ds != null && ds.getopenFlag()  )
					{
					   ds.isconn();
					   ds.forceTrigger();
					   Toast.makeText(MainActivity.this, "触发成功", Toast.LENGTH_SHORT).show();
					   break;
					}
				   else
					   Toast.makeText(MainActivity.this, "请先选中或打开设备", Toast.LENGTH_SHORT).show();
			}
				
				break;
			case R.id.ImageView_openGate:
			{
//				if( mPop != null && mPop.isShowing())
//				{
//					mPop.dismiss();
//				
//				}
//				 
//				   DeviceSet ds = MainActivity.this.getDeviceSetFromId(selectId);
//				  
//				   if(  ds != null && ds.getopenFlag()  )
//					{
//					   mPop =  new GatePopWindow( MainActivity.this,ds);
//					   
//					   mPop.show();
//					   
//					  
//					   break;
//					}
//				   else
//					   Toast.makeText(MainActivity.this, "请先选中或打开设备", Toast.LENGTH_SHORT).show();
			 
			}
				break;
		 
			case R.id.ImageView_SnapPicture:
			{
				DeviceSet ds = getDeviceSetFromId(MainActivity.this.selectId);
				
				if(ds != null && ds.getopenFlag())
				{
					byte[] imgBuffer = new byte[1024*20];
					
					int snapImgLength = ds.getSnapImageData(imgBuffer, imgBuffer.length) ;
					
					if( snapImgLength > 0)   //成功
					{
						Date now = new Date(); 
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//可以方便地修改日期格式
	 
						String currentTime = dateFormat.format( now ); 
						byte[] realimgBuffer = new byte[snapImgLength];
						
						System.arraycopy(imgBuffer, 0, realimgBuffer, 0, snapImgLength);
						
						m_gb.getSnapImageTable().add(currentTime, realimgBuffer);
						Toast.makeText(MainActivity.this, "截图成功", Toast.LENGTH_SHORT).show();
					}
					else
					{
						Toast.makeText(MainActivity.this, "截图失败", Toast.LENGTH_SHORT).show();
					}
				}
				else 
				  Toast.makeText(MainActivity.this, "请先选中或者打开设备", Toast.LENGTH_SHORT).show();
				
				
			} 
			     break;
				
			case R.id.ImageView_videoConfig:
			{
//				DeviceSet ds = getDeviceSetFromId(selectId);
//				
//				if(ds == null || !ds.getopenFlag())
//				{
//					 Toast.makeText(MainActivity.this, "请先选中或者打开设备", Toast.LENGTH_SHORT).show();
//					break;
//				}
//				
//                Intent intent = new Intent(MainActivity.this,VideoConfigActivity.class);
//				 
//				 m_gb.setDeviceSet(ds);
//				  
//				MainActivity.this.startActivity(intent);
				
			}
				 
				break;
//			case R.id.TextView_about:
//			{
//				 Intent intent = new Intent(MainActivity.this,AboutActivity.class);
//					
//				 MainActivity.this.startActivity(intent);
//			}
//			break;
			 
			}
		}
	};
	
    public DeviceSet getDeviceSetFromId(int id)
    {
    	
    	
    	java.util.Iterator it = vedioGroup.entrySet().iterator();
		while(it.hasNext()){
		   java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
		   DeviceSet ds = (DeviceSet)entry.getValue();
		    
		   if( (Integer)entry.getKey() == id )
			{
			    return ds;
			}
		}
		
		
		return null;
    }
    
    public DeviceSet getDeviceSetFromHandle(int handle)
    {
    	
    	
    	java.util.Iterator it = vedioGroup.entrySet().iterator();
		while(it.hasNext()){
		   java.util.Map.Entry entry = (java.util.Map.Entry)it.next();
		   DeviceSet ds = (DeviceSet)entry.getValue();
		    
		   if( (ds != null) && ( handle == ds.getDeviceInfo().handle ) )
			{
			    return ds;
			}
		}
		
		
		return null;
    }
	
}


 
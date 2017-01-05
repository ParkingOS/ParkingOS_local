package com.vzvison.device;



import javax.crypto.*;

import java.security.*;

import javax.crypto.spec.*;

import com.vzvison.vz.WlistVehicle;
import com.vz.tcpsdk;

import java.io.UnsupportedEncodingException;
import java.nio.charset.*;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;






import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.View;

public class DeviceSet {
	
	private DeviceInfo device_info = null;
	private VedioSetVeiw vedio_set = null;
	
	private boolean     serialOneFlag = false;
	private boolean     serialTwoFlag = false;
	private  boolean    openFlag = false;
	private tcpsdk.OnDataReceiver m_plateReciver=null ;
	private int         m_bEnableImg = 0;
	private tcpsdk.onWlistReceiver  m_wlistRecevier =null;
	 
	public void isconn(){
		new Thread(){
			public void run() {
				Log.e("------------------", "执行了");
				while (true) {
					boolean iscon = tcpsdk.getInstance().isConnected(device_info.handle);
					Log.e("------------------", iscon+"---");
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
		}.start();
	}
	public  DeviceSet( DeviceInfo di, VedioSetVeiw vs )
	{
		device_info = di;
		vedio_set = vs;
		
		
		vedio_set.setDeviceName(device_info.DeviceName);
	}
	public boolean open( )
	{ 
	   return open(device_info.DeviceName,device_info.ip,device_info.port,device_info.username,device_info.userpassword);
	}
	
	public boolean open(String deviceName,String ip,int port,String username,String userpassword)
	{ 
		if(device_info.handle >0)
			return false;
		
		
		vedio_set.setDeviceName(deviceName);
		
		int res = tcpsdk.getInstance().open(ip.getBytes(),ip.length() , port, username.getBytes(), 
 			   username.length(), userpassword.getBytes(), userpassword.length()); 
		 
		if(res > 0)
		{
			device_info.handle = res;
			device_info.DeviceName = deviceName;
			device_info.ip = ip;
			device_info.port = port;
			device_info.username = username;
			device_info.userpassword = userpassword;
			
			String rtspText = "rtsp://VisionZenith:147258369@";
			  rtspText += device_info.ip;
			  rtspText += ":8557/h264"; ;
			  
			  
			vedio_set.setUrl(rtspText );
			vedio_set.setErrorTextIsVisible(false);
			
			if(m_plateReciver != null)
			   tcpsdk.getInstance().setPlateInfoCallBack(device_info.handle,m_plateReciver,m_bEnableImg);
			
//			if(m_wlistRecevier != null)
//			   tcpsdk.getInstance().setWlistInfoCallBack(device_info.handle,m_wlistRecevier);
			 
			openFlag = true;
			return true;
		}
		else
		{
			if(vedio_set != null)
			{
				vedio_set.setErrorText("打开设备失败");
				vedio_set.setErrorTextIsVisible(true);
			}
		}
		 
		 return false;
	}
	
	
	public void close()
	{
		if(serialOneFlag || serialTwoFlag)
		{
			tcpsdk.getInstance().serialStop(device_info.handle);
			serialOneFlag = false;
			serialTwoFlag = false;
		}
		
	  
		if(device_info.handle > 0)
		{
			tcpsdk.getInstance().close(device_info.handle);
			device_info.handle = 0;
		}
		
		
		openFlag = false;
	}
	
	public void playVideo()
	{ 
		if(!openFlag && !open())
			return ;
		vedio_set.StartPlay();
		vedio_set.setErrorTextIsVisible(false);
	}
	public void stopVideo()
	{
		if(!openFlag)
			return ;
		vedio_set.StopPlay();
		
		vedio_set.setErrorText("无视频");
		vedio_set.setErrorTextIsVisible(true);
	}
	
	
	public DeviceInfo getDeviceInfo()
	{
		return device_info;
	}
	
	public void setPlateInfoCallBack( tcpsdk.OnDataReceiver reciver ,int bEnableImg)
	{
//		if(!openFlag)
//			return ;
		
		m_plateReciver = reciver;
		m_bEnableImg  = bEnableImg;
	    tcpsdk.getInstance().setPlateInfoCallBack(device_info.handle,reciver,1);
	}
	
	public boolean serialSend(int serialNum,byte[] pData, long uSizeData)
	{
		if(!openFlag)
			return false;
		
		if(serialNum < 0 || serialNum > 1)
		{
			return false;
		}
		
		if( serialNum == 0 )  // 串口1
		{
			if(!serialOneFlag)
			{
				if(tcpsdk.getInstance().serialStart(device_info.handle, serialNum) != 0)
					return false;
				serialOneFlag = true;
			}
			
		}
		else
		{
			if(!serialTwoFlag)
			{
				if(tcpsdk.getInstance().serialStart(device_info.handle, serialNum) != 0)
					return false;
				serialTwoFlag = true;
			}
		}
		
		if(tcpsdk.getInstance().serialSend(device_info.handle, serialNum, pData, uSizeData) != 0)
			return false;
		else
			return true;
	}
	
	
	public int getSnapImageData(byte[] imgBuffer, int imgBufferMaxLength)
	{
		if(!openFlag)
			return -1;
		
		return tcpsdk.getInstance().getSnapImageData(device_info.handle, imgBuffer, imgBufferMaxLength);
	}
	 
	
	public int getRtspUrl( byte[] url, int urlMaxLength)
	{
		if(!openFlag)
			return -1;
		return tcpsdk.getInstance().getSnapImageData(device_info.handle, url, urlMaxLength);
	}
	
	public int playVoice(  byte[] voice, int interval, int volume, int male)
	{
		if(!openFlag)
			return -1;
		
		return tcpsdk.getInstance().playVoice(device_info.handle, voice, interval, volume, male);
	}
	
	public void select()
	{
		vedio_set.Select();
	}
	public void unselect()
	{
		vedio_set.unSelect();
	}
	
	public boolean getopenFlag()
	{
		return openFlag;
	}
	
	 public   void setWlistInfoCallBack(tcpsdk.onWlistReceiver recevier)
	 {
		 if(!openFlag)
				return ;
			  tcpsdk.getInstance().setWlistInfoCallBack(device_info.handle, recevier);
		 
		// m_wlistRecevier = recevier;
		 
	 }
	   public   int  importWlistVehicle(WlistVehicle wllist)
	   {
		   if(!openFlag)
				return -1;
			return tcpsdk.getInstance().importWlistVehicle(device_info.handle, wllist);
	   }
	   public   int  deleteWlistVehicle(byte[] plateCode)
	   {
		   if(!openFlag)
				return -1;
			return tcpsdk.getInstance().deleteWlistVehicle(device_info.handle, plateCode);
	   }
	   public   int  queryWlistVehicle(byte[] plateCode)
	   {
		   if(!openFlag)
				return -1;
			return tcpsdk.getInstance().queryWlistVehicle(device_info.handle, plateCode);
	   }
	   public  int forceTrigger( )
	   {
		   return tcpsdk.getInstance().forceTrigger(device_info.handle);
	   }
	   public int  setIoOutputAuto( short uChnId, int nDuration)
	   {
		   return tcpsdk.getInstance().setIoOutputAuto(device_info.handle,uChnId,nDuration);
	   }
	   
		public void pause()
		{
			vedio_set.pause();
			 
		}
		public void resum()
		{
			vedio_set.resum();
		}
	 
		public void setPlateImage(Bitmap bmp)
		{
			vedio_set.setPlateImage(bmp);
		}
		public void setTrriglePlateText(String plateText)
		{
			vedio_set.setTrriglePlateText(plateText);
		}
		
	    public void ZoomOutVedio()
	    {
	    	vedio_set.ZoomOutVedio();
	    }
	    public void ZoomInVedio ()
	    {
	    	vedio_set.ZoomInVedio();
	    }
	    public void ZoomOutImage()
	    {
	    	vedio_set.ZoomOutImage();
	    }
	    public void ZoomInImage ()
	    {
	    	vedio_set.ZoomInImage();
	    }
//	public VedioSetVeiw getVedioSetVeiw()
//	{
//		return vedio_set;
//	}
	
	//获取分辨率
	public boolean getFrameSize(StringBuffer rate)   
	{
		//192.168.3.30/vb.htm?paratest=bitrate.0
//		String url = device_info.ip + "/vb.htm";
//		String param = "paratest=videosizexy.0";
		
//		String url = "http://"+ device_info.ip + "/login.php";
//		String param = device_info.username +":"+device_info.userpassword;
//		
//		byte [] decodeData = encrypt(param,"天天");//
//		Charset cs = Charset.forName ("GBK");
//	      ByteBuffer bb = ByteBuffer.allocate (decodeData.length);
//	      bb.put (decodeData);
//	        bb.flip ();
//	       CharBuffer cb = cs.decode (bb);
//	       
//	   char [] tempData =    cb.array();
//		
//		
//		String tempParam = String.valueOf(tempData);
//		tempParam = "HgAAAAUAAAChBEKzzjoXVHZdSQ==";
//		 
//		
//		String resText = HttpRequest.sendPost(url, tempParam);
//		
//		 url = "http://"+device_info.ip + "/vb.htm";
//		 param = "paratest=mainstreamsupport.0"; //"paratest=bitrate.0";
//		
//		 resText = HttpRequest.sendGet(url, param);
		
		return true;
	}
	
 
	 
	 @SuppressLint("HandlerLeak")
		private Handler handler = new Handler() {
			public void handleMessage(android.os.Message msg) {
				switch (msg.what) {
				
				}
			}
	 };

	 
	 /** 
	  * 加密 
	  *  
	  * @param content 需要加密的内容 
	  * @param password  加密密码 
	  * @return 
	  */  
	 public static byte[] encrypt(String content, String password) {  
	         try {             
	                 KeyGenerator kgen = KeyGenerator.getInstance("AES");  
	                 kgen.init(128, new SecureRandom(password.getBytes()));  
	                 SecretKey secretKey = kgen.generateKey();  
	                 byte[] enCodeFormat = secretKey.getEncoded();  
	                 SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");  
	                 Cipher cipher = Cipher.getInstance("AES");// 创建密码器   
	                 byte[] byteContent = content.getBytes("utf-8");  
	                 cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化   
	                 byte[] result = cipher.doFinal(byteContent);  
	                 return result; // 加密   
	         } catch (NoSuchAlgorithmException e) {  
	                 e.printStackTrace();  
	         } catch (NoSuchPaddingException e) {  
	                 e.printStackTrace();  
	         } catch (InvalidKeyException e) {  
	                 e.printStackTrace();  
	         } catch (UnsupportedEncodingException e) {  
	                 e.printStackTrace();  
	         } catch (IllegalBlockSizeException e) {  
	                 e.printStackTrace();  
	         } catch (BadPaddingException e) {  
	                 e.printStackTrace();  
	         }  
	         return null;  
	 }
}

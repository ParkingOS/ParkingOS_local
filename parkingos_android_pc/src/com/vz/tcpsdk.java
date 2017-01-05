package com.vz;
import android.util.Log;

public class tcpsdk {
	
	private static tcpsdk  m_tcpsdk = null;
	
	private void tcpsdk()
	{
		
	}
	
	public static tcpsdk getInstance()
	{
		if(m_tcpsdk == null)
			m_tcpsdk = new tcpsdk();
		return m_tcpsdk;
	}
	
	/**
	*  @brief 全局初始化
	*  @return 0表示成功，-1表示失败
	*  @note 在所有接口调用之前调用
	*  @ingroup group_global
	*/
    public native int   setup();
    /**
    *  @brief 全局释放
    *  @note 在程序结束时调用，释放SDK的资源
    *  @ingroup group_global
    */
    public native void  cleanup();
     
    /**
    *  @brief 打开一个设备
    *  @param  [IN] ip 设备的IP地址
    *  @param  [IN] ipLength 设备的IP地址长度
    *  @param  [IN] port 设备的端口号
    *  @param  [IN] username 访问设备所需用户名
    *  @param  [IN] userpassword 访问设备所需密码
    *  @return 返回设备的操作句柄，当打开失败时，返回0
    *  @ingroup group_device
    */
    public native int   open(byte[] ip,int ipLength,int port,byte[] username,int userLength,byte[] userpassword ,int passwordLenth);
    
    /**
    *  @brief 关闭一个设备
    *  @param  [IN] handle 由VzLPRTcp_Open函数获得的句柄
    *  @return 0表示成功，-1表示失败
    *  @ingroup group_device
    */
    public native int   close(int tcphandle);
   public native  int  setIoOutput(int handle, short uChnId, int nOutput);
   public native  int   getIoOutput(int  handle,  int  uChnId , int[] nOutput);
   public native  int  setIoOutputAuto(int handle, short uChnId, int nDuration);
   
   public native  boolean  isConnected(int handle);
   
   public native int setPlateInfoCallBack( int handle,  OnDataReceiver  onDataReceiver ,int bEnableImage);
 //  public native int setPlateInfoCallBack( OnDataReceiver  onDataReceiver );
   
   public native int forceTrigger(int handle);
   
   public native int serialStart(int handle, int nSerialPort);
   public native int  serialSend(int handle, int nSerialPort, byte[] pData, long uSizeData);
   public native int serialStop(int handle);
   public native int  getSnapImageData(int handle, byte[] imgBuffer, int imgBufferMaxLength);
   public native int  getRtspUrl(int handle,  byte[] url, int urlMaxLength);
   public native int playVoice( int handle, byte[] voice, int interval, int volume, int male);
   
   public native int setWlistInfoCallBack(int handle,onWlistReceiver recevier);
   public native int  importWlistVehicle(int handle,com.vzvison.vz.WlistVehicle wllist);
   public native int  deleteWlistVehicle(int handle,byte[] plateCode);
   public native int  queryWlistVehicle(int handle,byte[] plateCode);
   
	public interface OnDataReceiver {
		
		void onDataReceive(int handle,PlateResult plateResult,int uNumPlates,int eResultType,
				byte[] pImgFull,int nFullSize, byte[] pImgPlateClip,int nClipSize  );
//		void onDataReceive(int handle,byte[] szPlateData,int plateLength,int plateConfidence,int plateType,byte[] bdTimeData,int timeLength,
//				byte[] pImgFull,int nFullSize, byte[] pImgPlateClip,int nClipSize);
		
		
	}
	
	public interface onWlistReceiver {
		void onWlistReceive(int handle, WlistVehicle wlist  );
	}
    static {
    	try {
    		//System.loadLibrary("log");
    		System.loadLibrary("vztcpsdk_dynamic");
            System.loadLibrary("tcpsdk");
    	}
    	catch(UnsatisfiedLinkError e) {
			// fatal error, we can't load some our libs
			Log.d("tcpsdk", "Couldn't load lib: " + " - " + e.getMessage());
			
		}
    }
}

package com.vzvison.device;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;



import com.vz.tcpsdk;
import com.vz.tcpsdk.OnDataReceiver;

public class DeviceManage {
	

  
  private tcpsdk m_tcpsdk;
  private List<DeviceInfo>  m_DeviceInfoGroup = null;
   
   public DeviceManage()
   {
	   m_DeviceInfoGroup = new ArrayList<DeviceInfo>();
	   m_tcpsdk = new tcpsdk();
	   int a =m_tcpsdk.setup();
	   
	    
   }
   
   protected void finalize() throws Throwable 
   {
       Iterator<DeviceInfo> it = m_DeviceInfoGroup.iterator();
	   
	   while(it.hasNext())
	   {
		   m_tcpsdk.close( it.next().handle ) ;
	   }
	   
	   m_tcpsdk.cleanup();
   }
   
   public int open(String ip,int port,String username,String userpassword)
   {
	   int res = -1;
	 //  Integer handle = new Integer(res);
	   int[] handle ={ -1};  
       if(find( ip,port,handle ) )
       {
    	  // res = handle[0];//handle.intValue();
    	   res = -1;
       }
       else
       {
    	   try
    	   { 
    		   res = m_tcpsdk.open(ip.getBytes(),ip.length() , port, username.getBytes(), 
        			   username.length(), userpassword.getBytes(), userpassword.length()); 
    	   }
    	   catch(Exception e)
    	   {
    		   int a;
    		   a = 0;
    	   }
    	 
    	   
           if(res != -1)
           {
        	   DeviceInfo di = new DeviceInfo(0);
        	   di.handle = res;
        	   di.ip = ip;
        	   di.port = port;
        	   di.username = username;
        	   di.userpassword = userpassword;
        	   
        	   m_DeviceInfoGroup.add(di);
           } 
       }
    	 
	   
	  return res;
   }
   
   public boolean find(String ip,int port, int[]  handle)
   {
	   boolean bFindFlag = false;
	   Iterator<DeviceInfo> it = m_DeviceInfoGroup.iterator();
	   
	   while(it.hasNext())
	   {
		   DeviceInfo di =  it.next();
		  if( di.ip == ip && di.port ==  port  )
		  {
			  bFindFlag = true;
			  
			  //handle = di.handle;
			  handle[0] = di.handle;
			  break;
		  }
	   }
	   
	   return bFindFlag;
   }
   public boolean find( int handle)
   {
	   DeviceInfo di = GetDeviceInfo(handle);
	   if( di != null )
	   {
		   return true;
	   }
	   else
	   {
		   return false;
	   }
   }
   private DeviceInfo GetDeviceInfo( int handle )
   {
       Iterator<DeviceInfo> it = m_DeviceInfoGroup.iterator();
	   
	   while(it.hasNext())
	   {
		   DeviceInfo di = it.next() ;
		  if(  di.handle == handle  )
		  {
			   
			   return  di;
		  }
	   }
	   
	   return null;
   }
   
   public   int   close(int handle)
   {
	   return m_tcpsdk.close(handle);
   }
   
   public   String   GetRstpAddr(int handle)
   {
	   DeviceInfo di = GetDeviceInfo(handle);
	    
	   if( di != null )
	   {
		  String rtspText = "rtsp://VisionZenith:147258369@";
		  rtspText += di.ip;
		  rtspText += ":8557/h264"; ;
		  
		  return rtspText;
	   }
	   else
	   {
		   return "";
	   }
   }
   
   
   public   int setPlateInfoCallBack( int handle,  OnDataReceiver  onDataReceiver ,int bEnableImage)
   {
	  return m_tcpsdk.setPlateInfoCallBack(handle, onDataReceiver, bEnableImage);
   }
}

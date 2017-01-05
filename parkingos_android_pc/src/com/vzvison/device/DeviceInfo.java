package com.vzvison.device;

 

public class DeviceInfo{
	  public int handle = 0;
	  public int id = 0;
	  public String DeviceName = "…Ë±∏1";
	   public String ip = "192.168.199.101";
	   public int port = 8131;
	   public String username = "admin";
	   public String userpassword = "admin"; 
	   
	   public int getHandle() {
		return handle;
	}


	public void setHandle(int handle) {
		this.handle = handle;
	}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getDeviceName() {
		return DeviceName;
	}


	public void setDeviceName(String deviceName) {
		DeviceName = deviceName;
	}


	public String getIp() {
		return ip;
	}


	public void setIp(String ip) {
		this.ip = ip;
	}


	public int getPort() {
		return port;
	}


	public void setPort(int port) {
		this.port = port;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getUserpassword() {
		return userpassword;
	}


	public void setUserpassword(String userpassword) {
		this.userpassword = userpassword;
	}


	public DeviceInfo(int idInit)
	   {
		   id = idInit;
	   }
	   
	   
	   public boolean equals(Object object)
	   {
		   if(object == this)
			   return true;
		   
		   if(object != null && getClass() == object.getClass()  )
		   {
			   DeviceInfo other = (DeviceInfo)object;
			   
			   if( handle == other.handle &&
					   ip == other.ip &&
					   port == other.port &&
					   username == other.username &&
					   userpassword == other.userpassword)
			   {
				   return true;
			   }
		   }
		    
         
		   return false;
	   }
	   
	   public int hashCode()
	   {
         return handle;
	   }
}
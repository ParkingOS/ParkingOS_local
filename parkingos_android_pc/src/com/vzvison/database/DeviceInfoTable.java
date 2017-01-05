package com.vzvison.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.vzvison.device.DeviceInfo;

public class DeviceInfoTable {
private  plateHelper m_helper = null;
    
    
    
	public DeviceInfoTable()
	{
		
	}
	
	public void setDataBaseHelper(plateHelper helper  )
	{
		m_helper = helper;
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
			//创建数据库
	        // 创建表  
	         String sql = "create table if not exists  DeviceInfoTable("  
	             + "id integer primary key ,devicename varchar(20),"  //autoincrement
	        	 + "ip varchar(20),port integer,"	 
	             + "username varchar(20),userpassword varchar(20))";  
	         
	         try
	         {
	        	  db.execSQL(sql);  
	         }
	         catch( SQLException e )
	         {
	        	 int a;
	        	 a = 0;
	         }
		}
	}
	
	
	public boolean put(int num,String devicename,String ip,int  port, String username,String userpassword )
	{
		long res = 0;
		boolean resFlag =false;
		
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
			 
			 Cursor cursor = db.rawQuery("select * from DeviceInfoTable where id = ?",new String[]{Integer.toString(num+1)});
			 
			 if( cursor != null)
			 {
				 int  rowCount = cursor.getCount();
				 // 创建表  
				 ContentValues values = new ContentValues();  
				 values.put("id", num+1);
				 values.put("devicename", devicename);
				 values.put("ip", ip);
				 values.put("port", port);
				 values.put("username", username);
				 values.put("userpassword", userpassword);
				 
				 if(rowCount == 0)
				 {
					 res  =  db.insert("DeviceInfoTable", null, values);  
						
					 
					 if(res != -1)
						resFlag= true;
					 
				 }
				 else
				 {
					 db.update("DeviceInfoTable", values, "id = ?", new String[]{Integer.toString(num+1)});
					 if(res != -1)
					    resFlag = true;
				 }
			 
				
				  cursor.close(); 
			 }
			 
		}
		
		return resFlag;
	}
	
	public int getRowCount( )
	{
		int rowCount = 0;
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
		 
			  Cursor c = db.rawQuery("select * from DeviceInfoTable", null);  
			  rowCount = c.getCount();
			  c.close();
		}
	    return rowCount;
	}
	
	public boolean GetCallbackInfo(int num, DeviceInfo ele )
	{
		boolean bFindFlag = false;
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getReadableDatabase();
	         Cursor cursor = db.rawQuery("select * from DeviceInfoTable where id = ?",new String[]{Integer.toString(num+1)});
	       
	         if(cursor != null)
	         {
	        	// if( ( cursor.getCount() > num ) && cursor.moveToPosition(num))
	        	 if( cursor.moveToPosition(0))
		         {
		        	 ele.DeviceName = cursor.getString(cursor.getColumnIndex("devicename"));
		    	      ele.ip = cursor.getString(cursor.getColumnIndex("ip"));
		    	      ele.port = cursor.getInt(cursor.getColumnIndex("port"));
		              
		    	      ele.username = cursor.getString( cursor.getColumnIndex("username") );
		    	      ele.userpassword = cursor.getString( cursor.getColumnIndex("userpassword") );
		    	      
		    	      bFindFlag = true;
		         }
		          
		        cursor.close(); 
		       
	         }
	         
		}
		 
		 return  bFindFlag;	
	
	}
	
	public void ClearAll()
	{
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
			
		     db.delete("DeviceInfoTable",null,null);
		   
		}
	}
	
	 
}

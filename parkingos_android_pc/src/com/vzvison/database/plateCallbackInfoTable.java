package com.vzvison.database;

import android.database.sqlite.*;
import android.content.*;
import android.database.Cursor;
import android.database.SQLException;

public class plateCallbackInfoTable {
    private  plateHelper m_helper = null;
    
    
    
	public plateCallbackInfoTable()
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
	         String sql = "create table if not exists  plateCallbackInfoTable("  
	             + "id integer primary key autoincrement,devicename varchar(20),"  
	        	 + "plateNumber varchar(20),"	 
	             + "recongnizetime varchar(20),imgBig BLOB,imgSmall BLOB)";  
	         
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
	
	
	public boolean addCallbackInfo(String devicename,String plateNumber,String recongnizetime,byte [] ImageBigData,byte [] ImageSmallData )
	{
		long res = 0;
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
			//创建数据库
	        // 创建表  
			 ContentValues values = new ContentValues();  

			 values.put("devicename", devicename);
			 values.put("plateNumber", plateNumber);
			 values.put("recongnizetime", recongnizetime);
			 values.put("imgBig", ImageBigData);
			 values.put("imgSmall", ImageSmallData);
			 
			 res  =  db.insert("plateCallbackInfoTable", null, values);  
			
			 if(res != -1)
	            return true;
		}
		
		return false;
	}
	
	public int getRowCount( )
	{
		int rowCount = 0;
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
		 
			  Cursor c = db.rawQuery("select * from plateCallbackInfoTable", null);  
			  rowCount = c.getCount();
			  c.close();
		}
	    return rowCount;
	}
	
	public boolean GetCallbackInfo(int num, plateCallbackElement ele )
	{
		boolean bFindFlag = false;
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getReadableDatabase();
	         Cursor cursor = db.rawQuery("select * from plateCallbackInfoTable",null); // where id = ?",new String[]{Integer.toString(id)});
	       
	         if(cursor != null)
	         {
	        	 if(cursor.moveToPosition(num))
		         {
		        	 ele.devicename = cursor.getString(cursor.getColumnIndex("devicename"));
		    	      ele.plateNumber = cursor.getString(cursor.getColumnIndex("plateNumber"));
		    	      ele.recongnizetime = cursor.getString(cursor.getColumnIndex("recongnizetime"));
		              
		    	      ele.ImageBigData = cursor.getBlob( cursor.getColumnIndex("imgBig") );
		    	      ele.ImageSmallData = cursor.getBlob( cursor.getColumnIndex("imgSmall") );
		         }
		          
		        cursor.close(); 
		        
		        return true;
	         }
	         
		}
		 
		 return  bFindFlag;	
	
	}
	
	public void ClearAll()
	{
		if(m_helper != null)
		{
			SQLiteDatabase db = m_helper.getWritableDatabase();
			
		     db.delete("plateCallbackInfoTable",null,null);
		   
		}
	}
	
	public class plateCallbackElement
	{
		public String devicename = "";
		public String plateNumber= "";
		public String recongnizetime= "";
		
		public byte [] ImageBigData= null;
		public byte [] ImageSmallData= null;
	}
}

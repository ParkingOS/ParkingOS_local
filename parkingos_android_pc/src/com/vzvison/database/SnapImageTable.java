package com.vzvison.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;


public class SnapImageTable {
	 private  plateHelper m_helper = null;
	    
	    
	    
		public SnapImageTable()
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
		         String sql = "create table if not exists  SnapImageTable("  
		             + "id integer primary key autoincrement,date varchar(20),"  
		        	 + "img BLOB)";  
		         
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
		
		
		public boolean add(String date,byte [] ImageData )
		{
			if(m_helper != null)
			{
				SQLiteDatabase db = m_helper.getWritableDatabase();
				//创建数据库
		        // 创建表  
				 ContentValues values = new ContentValues();  

				 values.put("date", date);
				 values.put("img", ImageData);
				 
				 
				 db.insert("SnapImageTable", null, values);  
				 
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
			 
				  Cursor c = db.rawQuery("select * from SnapImageTable", null);  
				  rowCount = c.getCount();
				  c.close();
			}
		    return rowCount;
		}
		
		public boolean get(int num, SnapImageElement sie )
		{
			boolean bFindFlag = false;
			if(m_helper != null)
			{
				SQLiteDatabase db = m_helper.getReadableDatabase();
		         Cursor cursor = db.rawQuery("select * from SnapImageTable ",null);//where id = ?",new String[]{Integer.toString(id)});
		         
		         if( cursor != null && cursor.moveToPosition(num))
		         {
		        	 sie.date = cursor.getString(cursor.getColumnIndex("date"));
			    	 sie.ImageData = cursor.getBlob(cursor.getColumnIndex("img"));
			    	  
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
			 
				 
					 db.delete("SnapImageTable",null,null);
				 
			}
		}
		
		public class SnapImageElement
		{
			public String date = "";
		 
			
			public byte [] ImageData= null;
		 
		}
}

package com.vzvison.database;

import android.database.sqlite.*;
import android.content.*;


public class plateHelper extends SQLiteOpenHelper {   
    
 public static final String TB_NAME = "PlateTable";   
 public static final String ID = "_id";   
 public static final String COUNTRY = "country";   
 public static final String CODE = "code";   
    
    
 public plateHelper(Context context, String name,    
		 SQLiteDatabase.CursorFactory factory,int version) {   
     super(context, name, factory, version);   
 }   

 public void onCreate(SQLiteDatabase db) {   
        
//     db.execSQL("CREATE TABLE IF NOT EXISTS "    
//             + TB_NAME + " ("    
//             + ID + " INTEGER PRIMARY KEY,"    
//             + COUNTRY + " VARCHAR,"  
//             + CODE + " INTEGER)");   
 }   

 public void onUpgrade(SQLiteDatabase db,    
         int oldVersion, int newVersion) {   
     //TODO 删除数据库之前 做数据备份
     db.execSQL("DROP TABLE IF EXISTS "+TB_NAME);   
     onCreate(db);   
 }   
}  
package com.zld.local.db;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

@SuppressLint("NewApi")
public class LocalDBHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "local_order.db";
	private static final int DATABASE_VERSION = 1;
	
	public LocalDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, myerrorHandler);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//创建一个表
		String sql1 = 
				"create table if not exists price_tb("
				+ "id integer primary key autoincrement NOT NULL,"
				+ "comid integer,price real(10,2) DEFAULT 0,"
				+ "state integer DEFAULT 0,"
				+ "unit integer,pay_type integer,create_time integer,"
				+ "b_time integer,e_time integer,is_sale integer DEFAULT 0,"
				+ "first_times integer DEFAULT 0,fprice real(10,2) DEFAULT 0,"
				+ "countless integer DEFAULT 0,free_time integer DEFAULT 0,fpay_type integer DEFAULT 0,"
				+"isnight integer DEFAULT 0,isedit integer DEFAULT 0,car_type integer DEFAULT 0,"
				+ "is_fulldaytime integer DEFAULT 0,update_time integer)";
		
		String sql2 = 
				"create table if not exists order_tb("
				+ "id varchar varying(50) primary key NOT NULL,"
				+ "localid varchar varying(50),"
				+ "create_time integer,comid integer NOT NULL, uin integer NOT NULL,"
				+ "total real(30,2),prepay real(30,2),state integer,end_time integer, "
				+ "auto_pay integer DEFAULT 0, pay_type integer DEFAULT 0,nfc_uuid character varying(36),"
				+ "c_type integer DEFAULT 1, uid integer DEFAULT (-1), car_number varchar varying(50), "
				+ "imei character varying(50), pid integer DEFAULT (-1), car_type integer DEFAULT 0, "
				+ "pre_state integer DEFAULT 0, in_passid bigint DEFAULT (-1), out_passid bigint DEFAULT (-1))";
		
		String sql3 = 
				"create table if not exists com_nfc_tb("
				+ "id bigint NOT NULL,nfc_uuid character varying(36),comid bigint,"
				+ "create_time bigint,state bigint,use_times bigint,"
				+ "uin bigint DEFAULT (-1),uid bigint DEFAULT (-1),update_time bigint DEFAULT 0, "
				+ "nid bigint DEFAULT 0, qrcode character varying)";
		
		String sql4 = 
				"create table if not exists com_info_tb("
				+ "id bigint NOT NULL,minprice_unit real(5,2) DEFAULT 0)";
	
		String sql5 = 
				"create table if not exists monthcard_tb("
				+ "id integer PRIMARY KEY AUTOINCREMENT,uin bigint DEFAULT (-1),car_number varchar varying(50),e_time bigint)";
		
		db.execSQL(sql1);
		db.execSQL(sql2);
		db.execSQL(sql3);
		db.execSQL(sql4);
		db.execSQL(sql5);
		Log.i("LocalDBHelper", "onCreate---创建数据库表！");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS " + "price_tb");
		db.execSQL("DROP TABLE IF EXISTS " + "order_tb");
		db.execSQL("DROP TABLE IF EXISTS " + "com_nfc_tb");
		db.execSQL("DROP TABLE IF EXISTS " + "com_info_tb");
		db.execSQL("DROP TABLE IF EXISTS " + "monthcard_tb");
		onCreate(db);
		Log.i("LocalDBHelper", "onUpgrade---升级数据库表！");
	}

	public static DatabaseErrorHandler myerrorHandler = new DatabaseErrorHandler() {

		@Override
		public void onCorruption(SQLiteDatabase dbObj) {
			Log.i("LocalDBHelper", "DatabaseErrorHandler---数据库创建失败！！！！！！！！！！");

		}
	};

}

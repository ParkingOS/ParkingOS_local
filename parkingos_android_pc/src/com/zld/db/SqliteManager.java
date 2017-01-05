package com.zld.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.zld.bean.MyCameraInfo;
import com.zld.bean.MyLedInfo;
import com.zld.bean.SmAccount;
import com.zld.bean.UploadImg;
import com.zld.local.bean.Relation;
/**
 * 
 * <pre>
 * 功能说明: 数据库管理类-增删改查
 * 日期:	2014-9-5
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2014-9-5
 * </pre>
 */
public class SqliteManager {

	private Context mContext;
	private SQLiteDatabase db;
	private DBHelper dbHelper;
	@SuppressWarnings("unused")
	private static final String ID = "id";
	private static final String TAG= "SqliteManager";
	private static final String ACCOUNT = "account";
	private static final String CARNUMBER = "carnumber";
	private static final String ORDERID = "orderid";
	private static final String LEFTTOP = "lefttop";
	private static final String RIGHTBOTTOM = "rightbottom";
	private static final String TYPE = "type";
	private static final String WIDTH = "width";
	private static final String HEIGHT = "height";
	private static final String IMGHOMEPATH = "imghomepath";
	private static final String IMGEXITPATH = "imgexitpath";
	private static final String HOMEIMGUP = "homeimgup";//0为未上传，1
	private static final String EXITIMGUP = "exitimgup";//0为未上传，1

	public static final int PASSTYPE_ALL = 1;
	public static final int PASSTYPE_IN = 2;
	public static final int PASSTYPE_OUT = 3;

	public SqliteManager(Context context) {
		mContext = context;
		dbHelper = new DBHelper(mContext);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 *  9 个字段
	 * id 
	 * account 账户
	 * orderid 订单id
	 * lefttop 图片左上角x坐标
	 * rightbottom 图片左上角y坐标
	 * type 通道类型
	 * width 图片宽
	 * height 图片高
	 * imgpath 图片路径
	 * @return 
	 */
	public void insertData(String account, String carNumber, String orderid, String lefttop,
			String rightbottom,String type,String width,String height,
			String imghomepath,String imgexitpath,String homeimgup,String exitimgup) {
		ContentValues cv = new ContentValues();
		cv.put(ACCOUNT, account);
		cv.put(CARNUMBER, carNumber);
		cv.put(ORDERID, orderid);
		cv.put(LEFTTOP, lefttop);
		cv.put(RIGHTBOTTOM, rightbottom);
		cv.put(TYPE, type);
		cv.put(WIDTH, width);
		cv.put(HEIGHT, height);
		cv.put(IMGHOMEPATH, imghomepath);
		cv.put(IMGEXITPATH, imgexitpath);
		cv.put(HOMEIMGUP, homeimgup);
		cv.put(EXITIMGUP, exitimgup);

		long insert = db.insert(DBHelper.IMAGE_TABLE, null, cv);
		Log.e(TAG, "插入数据库返回码："+insert);
	}

	/**
	 * 删除对应orderid的数据
	 */
	public void deleteData(String oid) {  
		Log.e(TAG,"删除图片："+oid);
		String sql = "delete from "+DBHelper.IMAGE_TABLE+" where orderid="+oid;
		db.execSQL(sql);
	}

	/**
	 * 删除所有数据
	 */
	public void deleteAllData() {  
		String sql = "delete from "+DBHelper.IMAGE_TABLE;
		db.execSQL(sql);
	}

	/**
	 * 查询对应Orderid的图片信息
	 */
	public UploadImg selectImage(String oid) {

		Cursor cursor=null;
		UploadImg uploadImg = null;
		try {
			String sql = "select * from "+DBHelper.IMAGE_TABLE+" where orderid='"+oid+"'";
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String account = cursor.getString(cursor.getColumnIndex("account"));
				String carnumber = cursor.getString(cursor.getColumnIndex("carnumber"));
				String orderid = cursor.getString(cursor.getColumnIndex("orderid"));
				String lefttop = cursor.getString(cursor.getColumnIndex("lefttop"));
				String rightbottom = cursor.getString(cursor.getColumnIndex("rightbottom"));
				String type = cursor.getString(cursor.getColumnIndex("type"));

				String width = cursor.getString(cursor.getColumnIndex("width"));
				String height = cursor.getString(cursor.getColumnIndex("height"));
				String imghomepath = cursor.getString(cursor.getColumnIndex("imghomepath"));
				String imgexitpath = cursor.getString(cursor.getColumnIndex("imgexitpath"));
				String homeimgup = cursor.getString(cursor.getColumnIndex("homeimgup"));
				String exitimgup = cursor.getString(cursor.getColumnIndex("exitimgup"));

				uploadImg = new UploadImg();
				uploadImg.setId(id);
				uploadImg.setAccount(account);
				uploadImg.setCarnumber(carnumber);
				uploadImg.setOrderid(orderid);
				uploadImg.setLefttop(lefttop);
				uploadImg.setRightbottom(rightbottom);
				uploadImg.setType(type);
				uploadImg.setWidth(width);
				uploadImg.setHeight(height);
				uploadImg.setImghomepath(imghomepath);
				uploadImg.setImgexitpath(imgexitpath);
				uploadImg.setHomeimgup(homeimgup);
				uploadImg.setExitimgup(exitimgup);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			};
		}
		return uploadImg;
	}


	/**
	 * 更新出口图片路径
	 */
	public void updateSelectImage(String orderid,String imgExitPath){
		String sql = "update "+DBHelper.IMAGE_TABLE+
				" set imgexitpath = '"+imgExitPath+"' where orderid = '" + orderid + "'";
		db.execSQL(sql);
	}

	/**
	 * 查询所有的需要上传的图片信息
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public ArrayList<String> selectAllOrderid() {

		Cursor cursor=null;
		ArrayList orderidList = new ArrayList<String>();
		try {
			String sql = "select orderid from "+DBHelper.IMAGE_TABLE;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("orderid"));
				System.out.println("获取的id："+id);
				orderidList.add(id+"");
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return orderidList;
	}

	/**
	 * 查询所有的orderid长度小于30的图片信息
	 */
	public ArrayList<UploadImg> selectOrderid() {

		Cursor cursor=null;
		UploadImg uploadImg = null;
		ArrayList<UploadImg> orderidList = new ArrayList<UploadImg>();
		try {
			String sql = "select * from "+DBHelper.IMAGE_TABLE+" where length(orderid) < 30 ";
			cursor = db.rawQuery(sql, null);
			if (cursor.moveToFirst()) {
				for (int i = 0; i < cursor.getCount(); i++) {
					int id = cursor.getInt(cursor.getColumnIndex("id"));
					String orderid = cursor.getString(cursor.getColumnIndex("orderid"));
					String account = cursor.getString(cursor.getColumnIndex("account"));
					String carnumber = cursor.getString(cursor.getColumnIndex("carnumber"));
					String lefttop = cursor.getString(cursor.getColumnIndex("lefttop"));
					String rightbottom = cursor.getString(cursor.getColumnIndex("rightbottom"));
					String type = cursor.getString(cursor.getColumnIndex("type"));
					String width = cursor.getString(cursor.getColumnIndex("width"));
					String height = cursor.getString(cursor.getColumnIndex("height"));
					String imghomepath = cursor.getString(cursor.getColumnIndex("imghomepath"));
					String imgexitpath = cursor.getString(cursor.getColumnIndex("imgexitpath"));
					String homeimgup = cursor.getString(cursor.getColumnIndex("homeimgup"));
					String exitimgup = cursor.getString(cursor.getColumnIndex("exitimgup"));
					uploadImg = new UploadImg();
					uploadImg.setId(id);
					uploadImg.setOrderid(orderid);
					uploadImg.setAccount(account);
					uploadImg.setCarnumber(carnumber);
					uploadImg.setLefttop(lefttop);
					uploadImg.setRightbottom(rightbottom);
					uploadImg.setType(type);
					uploadImg.setWidth(width);
					uploadImg.setHeight(height);
					uploadImg.setImghomepath(imghomepath);
					uploadImg.setImgexitpath(imgexitpath);
					uploadImg.setHomeimgup(homeimgup);
					uploadImg.setExitimgup(exitimgup);
					orderidList.add(uploadImg);
					cursor.moveToNext();
				}
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return orderidList;
	}

	/**
	 * 更新订单图片本地orderid
	 * @param relations
	 */
	public void updateImgOrderid(List<Relation> relations){
		try {
			db.beginTransaction();
			for (int i = 0; i < relations.size(); i++) {
				String sql = "update "+DBHelper.IMAGE_TABLE+" set orderid = "+relations.get(i).getLine()+" where orderid = '"+relations.get(i).getLocal()+"'";
				Log.e("LocalOrderDBManager","执行完毕:"+sql);
				db.execSQL(sql);
			}
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		} 
	}

	/**
	 * 更新订单本地图片
	 * @param orderid
	 * @param imgup 图片是否上传
	 * @param boo 为true 修改入口上传状态
	 */
	public void updateOrderImg(String orderid,String imgup,boolean boo){
		String sql = null;
		try {
			db.beginTransaction();
			if(boo){
				sql =  "update "+DBHelper.IMAGE_TABLE+" set homeimgup = "+
						imgup+" where orderid = '"+orderid+"'";
			}else{
				sql = "update "+DBHelper.IMAGE_TABLE+" set exitimgup = "+
						imgup+" where orderid = '"+orderid+"'";
			}
			db.execSQL(sql);
			db.setTransactionSuccessful();
		} finally {
			db.endTransaction();
		} 
	}


	/**
	 * 关闭数据库
	 */
	public void close() {
		db.close();
	}

	/** 
	 * 2个字段
	 * 插入用户名和密码数据 -无返回值
	 */
	public void insertAccountData(String account,String username,String password) {
		String sql = "insert into "+DBHelper.ACCOUNT_INFO+
				" (account,username,password) values (?,?,?)";
		db.execSQL(sql, new Object[] {account, username, password});
	}
	/** 
	 * 2个字段
	 * 删除用户名和密码数据 -无返回值
	 */
	public void deleteAccountData(String account) {
		String sql = "delete from "+DBHelper.ACCOUNT_INFO+
				" where account='"+account+"'";
		db.execSQL(sql);
	}

	/**
	 * 更新用户名密码数据
	 */
	public void updateAccountData(String account,String username,String password){
		String sql = "update "+DBHelper.ACCOUNT_INFO+
				" set account = '"+account + "', password= '" + password + "' where username = '" + username + "'";
		db.execSQL(sql);
	}

	/**
	 * 查询对应Account信息
	 */
	public SmAccount selectAccount(String account) {

		Cursor cursor=null;
		SmAccount smAccount = null;
		try {
			String sql = "select * from "+DBHelper.ACCOUNT_INFO+" where account='"+account+"'";
			Log.e("LoginActivity", sql);
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex("username"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				smAccount = new SmAccount(account, username, password);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return smAccount;
	}

	/**
	 * 查询对应Account信息
	 */
	public SmAccount selectAccountByUid(String username) {

		Cursor cursor=null;
		SmAccount smAccount = null;
		try {
			String sql = "select * from "+DBHelper.ACCOUNT_INFO+" where username='"+username+"'";
			Log.e("SqliteManager", sql);
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String account = cursor.getString(cursor.getColumnIndex("account"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				smAccount = new SmAccount(account, username, password);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return smAccount;
	}

	/**
	 * 查询对应Account信息
	 */
	public SmAccount selectUsername(String name) {
		Cursor cursor=null;
		SmAccount smAccount = null;
		try {
			String sql = "select * from "+DBHelper.ACCOUNT_INFO+" where username='"+name+"'";
			Log.e("LoginActivity", sql);
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String account = cursor.getString(cursor.getColumnIndex("account"));
				String username = cursor.getString(cursor.getColumnIndex("username"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				smAccount = new SmAccount(account, username, password);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return smAccount;
	}

	/**
	 * 查询对应Account信息
	 */
	public SmAccount selectAccountByUsrName(String name) {

		Cursor cursor=null;
		SmAccount smAccount = null;
		try {
			String sql = "select * from "+DBHelper.ACCOUNT_INFO+" where username='"+name+"'";
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String username = cursor.getString(cursor.getColumnIndex("username"));
				String password = cursor.getString(cursor.getColumnIndex("password"));
				String account = cursor.getString(cursor.getColumnIndex("account"));
				smAccount = new SmAccount(account, username, password);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return smAccount;
	}

	/**
	 * 查询所有的用户名信息
	 */
	public ArrayList<String> selectAllAccount() {

		Cursor cursor=null;
		ArrayList<String> accountList = new ArrayList<String>();
		try {
			String sql = "select account from "+DBHelper.ACCOUNT_INFO;
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String account = cursor.getString(cursor.getColumnIndex("account"));
				System.out.println("获取的account："+account);
				accountList.add(account);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return accountList;
	}

	/**
	 * 保存Camera信息
	 * @param cameraip
	 * @param cameraname
	 * @param passtype
	 */
	public void insertCameraData(MyCameraInfo camera) {
		ContentValues cv = new ContentValues();
		cv.put("cameraid", camera.getId());
		cv.put("cameraip", camera.getIp());
		cv.put("cameraname", camera.getCamera_name());
		cv.put("passtype", camera.getPasstype());
		cv.put("passname", camera.getPassname());
		cv.put("passid", camera.getPassid());
		long insert = db.insert(DBHelper.CAMERA_INFO, null, cv);
		Log.e("SqliteManager", "保存摄像头到数据库返回码："+insert);
	}

	/**
	 * 查询所有的Camera信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
	public ArrayList<MyCameraInfo> selectCamera(int passtypeNum) {

		Cursor cursor=null;
		ArrayList cameraList = new ArrayList<MyCameraInfo>();
		String sql = "";
		try {
			switch (passtypeNum) {
			case PASSTYPE_ALL:
				sql = "select * from "+DBHelper.CAMERA_INFO;
				break;
			case PASSTYPE_IN:
				sql = "select * from "+DBHelper.CAMERA_INFO+" where passtype=0";
				break;
			case PASSTYPE_OUT:
				sql = "select * from "+DBHelper.CAMERA_INFO+" where passtype=1";
				break;
			}
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String cameraid = cursor.getString(cursor.getColumnIndex("cameraid"));
				String cameraip = cursor.getString(cursor.getColumnIndex("cameraip"));
				String cameraname = cursor.getString(cursor.getColumnIndex("cameraname"));
				String passtype = cursor.getString(cursor.getColumnIndex("passtype"));
				String passname = cursor.getString(cursor.getColumnIndex("passname"));
				String passid = cursor.getString(cursor.getColumnIndex("passid"));
				MyCameraInfo cameraInfo = new MyCameraInfo();
				cameraInfo.setCameraid(cameraid);
				cameraInfo.setIp(cameraip);
				cameraInfo.setCamera_name(cameraname);
				cameraInfo.setPasstype(passtype);
				cameraInfo.setPassname(passname);
				cameraInfo.setPassid(passid);
				cameraList.add(cameraInfo);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return cameraList;
	}

	/**
	 * 保存LED信息
	 * @param ledip
	 * @param passtype
	 */
	public void insertLedData(MyLedInfo ledinfo) {
		ContentValues cv = new ContentValues();
		cv.put("ledid", ledinfo.getId());
		cv.put("ledip", ledinfo.getLedip());
		cv.put("ledport", ledinfo.getLedport());
		cv.put("leduid", ledinfo.getLeduid());
		cv.put("movemode", ledinfo.getMovemode());
		cv.put("movespeed", ledinfo.getMovespeed());
		cv.put("dwelltime", ledinfo.getDwelltime());
		cv.put("ledcolor", ledinfo.getLedcolor());
		cv.put("showcolor", ledinfo.getShowcolor());
		cv.put("typeface", ledinfo.getTypeface());
		cv.put("typesize", ledinfo.getTypesize());
		cv.put("matercont", ledinfo.getMatercont());
		cv.put("passid", ledinfo.getPassid());
		cv.put("passtype", ledinfo.getPasstype());
		cv.put("passname", ledinfo.getPassname());
		cv.put("width", ledinfo.getWidth());
		cv.put("height", ledinfo.getHeight());
		cv.put("type", ledinfo.getType());
		cv.put("rsport", ledinfo.getRsport());
		long insert = db.insert(DBHelper.LED_INFO, null, cv);
		Log.e("SqliteManager", "保存LED到数据库返回码："+insert);
	}

	/**
	 * 查询所有的LED信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<MyLedInfo> selectLed(int passtypeNum) {

		Cursor cursor=null;
		ArrayList ledList = new ArrayList<MyLedInfo>();
		String sql = "";
		try {
			switch (passtypeNum) {
			case PASSTYPE_ALL:
				sql = "select * from "+DBHelper.LED_INFO;
				break;
			case PASSTYPE_IN:
				sql = "select * from "+DBHelper.LED_INFO+" where passtype=0";
				break;
			case PASSTYPE_OUT:
				sql = "select * from "+DBHelper.LED_INFO+" where passtype=1";
				break;
			}
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String ledid = cursor.getString(cursor.getColumnIndex("ledid"));
				String ledip = cursor.getString(cursor.getColumnIndex("ledip"));
				String ledport = cursor.getString(cursor.getColumnIndex("ledport"));
				String leduid = cursor.getString(cursor.getColumnIndex("leduid"));
				String movemode = cursor.getString(cursor.getColumnIndex("movemode"));
				String movespeed = cursor.getString(cursor.getColumnIndex("movespeed"));
				String dwelltime = cursor.getString(cursor.getColumnIndex("dwelltime"));
				String ledcolor = cursor.getString(cursor.getColumnIndex("ledcolor"));
				String showcolor = cursor.getString(cursor.getColumnIndex("showcolor"));
				String typeface = cursor.getString(cursor.getColumnIndex("typeface"));
				String typesize = cursor.getString(cursor.getColumnIndex("typesize"));
				String matercont = cursor.getString(cursor.getColumnIndex("matercont"));
				String passid = cursor.getString(cursor.getColumnIndex("passid"));
				String passtype = cursor.getString(cursor.getColumnIndex("passtype"));
				String passname = cursor.getString(cursor.getColumnIndex("passname"));
				String width = cursor.getString(cursor.getColumnIndex("width"));
				String height = cursor.getString(cursor.getColumnIndex("height"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				String rsport = cursor.getString(cursor.getColumnIndex("rsport"));

				MyLedInfo ledInfo = new MyLedInfo();
				ledInfo.setId(ledid);
				ledInfo.setLedip(ledip);
				ledInfo.setLedport(ledport);
				ledInfo.setLeduid(leduid);
				ledInfo.setMovemode(movemode);
				ledInfo.setMovespeed(movespeed);
				ledInfo.setDwelltime(dwelltime);
				ledInfo.setLedcolor(ledcolor);
				ledInfo.setShowcolor(showcolor);
				ledInfo.setTypeface(typeface);
				ledInfo.setTypesize(typesize);
				ledInfo.setMatercont(matercont);
				ledInfo.setPassid(passid);
				ledInfo.setPassname(passname);
				ledInfo.setPasstype(passtype);
				ledInfo.setWidth(width);
				ledInfo.setHeight(height);
				ledInfo.setType(type);
				ledInfo.setRsport(rsport);
				ledList.add(ledInfo);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return ledList;
	}
	/**
	 *根据address查询LED信息
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ArrayList<MyLedInfo> selectLedByAddress(String ip) {

		Cursor cursor=null;
		ArrayList ledList = new ArrayList<MyLedInfo>();
		try{
		String sql = "select * from "+DBHelper.LED_INFO+" where ledip='"+ip+"'";
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String ledid = cursor.getString(cursor.getColumnIndex("ledid"));
				//暂时用不到  用到哪个直接去掉注释就行   先留着
//				String ledip = cursor.getString(cursor.getColumnIndex("ledip"));
//				String ledport = cursor.getString(cursor.getColumnIndex("ledport"));
//				String leduid = cursor.getString(cursor.getColumnIndex("leduid"));
//				String movemode = cursor.getString(cursor.getColumnIndex("movemode"));
//				String movespeed = cursor.getString(cursor.getColumnIndex("movespeed"));
//				String dwelltime = cursor.getString(cursor.getColumnIndex("dwelltime"));
//				String ledcolor = cursor.getString(cursor.getColumnIndex("ledcolor"));
//				String showcolor = cursor.getString(cursor.getColumnIndex("showcolor"));
//				String typeface = cursor.getString(cursor.getColumnIndex("typeface"));
//				String typesize = cursor.getString(cursor.getColumnIndex("typesize"));
//				String matercont = cursor.getString(cursor.getColumnIndex("matercont"));
//				String passid = cursor.getString(cursor.getColumnIndex("passid"));
//				String passtype = cursor.getString(cursor.getColumnIndex("passtype"));
//				String passname = cursor.getString(cursor.getColumnIndex("passname"));
//				String width = cursor.getString(cursor.getColumnIndex("width"));
//				String height = cursor.getString(cursor.getColumnIndex("height"));
//				String type = cursor.getString(cursor.getColumnIndex("type"));
//				String rsport = cursor.getString(cursor.getColumnIndex("rsport"));

				MyLedInfo ledInfo = new MyLedInfo();
				ledInfo.setId(ledid);
//				ledInfo.setLedip(ledip);
//				ledInfo.setLedport(ledport);
//				ledInfo.setLeduid(leduid);
//				ledInfo.setMovemode(movemode);
//				ledInfo.setMovespeed(movespeed);
//				ledInfo.setDwelltime(dwelltime);
//				ledInfo.setLedcolor(ledcolor);
//				ledInfo.setShowcolor(showcolor);
//				ledInfo.setTypeface(typeface);
//				ledInfo.setTypesize(typesize);
//				ledInfo.setMatercont(matercont);
//				ledInfo.setPassid(passid);
//				ledInfo.setPassname(passname);
//				ledInfo.setPasstype(passtype);
//				ledInfo.setWidth(width);
//				ledInfo.setHeight(height);
//				ledInfo.setType(type);
//				ledInfo.setRsport(rsport);
				ledList.add(ledInfo);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return ledList;
	}
	/**
	 * 查询同一个入口通道下的ledip和cameraip
	 * @return
	 */
	public HashMap<String,MyLedInfo> selectIp(int passtypeNum){
		HashMap<String,MyLedInfo> hashMap = new HashMap<String, MyLedInfo>();
		String sql = "";
		switch (passtypeNum) {
		case PASSTYPE_IN:
			sql = "select ledinfo.*,camerainfo.cameraip from ledinfo,camerainfo"
					+ " where ledinfo.passname=camerainfo.passname and camerainfo.passtype=0";
			break;
		case PASSTYPE_OUT:
			sql = "select ledinfo.*,camerainfo.cameraip from ledinfo,camerainfo"
					+ " where ledinfo.passname=camerainfo.passname and camerainfo.passtype=1";
			break;
		}
		Cursor cursor=null;
		try {
			cursor = db.rawQuery(sql, null);
			while (cursor.moveToNext()) {
				String ledid = cursor.getString(cursor.getColumnIndex("ledid"));
				String ledip = cursor.getString(cursor.getColumnIndex("ledip"));
				String ledport = cursor.getString(cursor.getColumnIndex("ledport"));
				String leduid = cursor.getString(cursor.getColumnIndex("leduid"));
				String movemode = cursor.getString(cursor.getColumnIndex("movemode"));
				String movespeed = cursor.getString(cursor.getColumnIndex("movespeed"));
				String dwelltime = cursor.getString(cursor.getColumnIndex("dwelltime"));
				String ledcolor = cursor.getString(cursor.getColumnIndex("ledcolor"));
				String showcolor = cursor.getString(cursor.getColumnIndex("showcolor"));
				String typeface = cursor.getString(cursor.getColumnIndex("typeface"));
				String typesize = cursor.getString(cursor.getColumnIndex("typesize"));
				String matercont = cursor.getString(cursor.getColumnIndex("matercont"));
				String passid = cursor.getString(cursor.getColumnIndex("passid"));
				String passtype = cursor.getString(cursor.getColumnIndex("passtype"));
				String passname = cursor.getString(cursor.getColumnIndex("passname"));
				String cameraip = cursor.getString(cursor.getColumnIndex("cameraip"));
				String width = cursor.getString(cursor.getColumnIndex("width"));
				String height = cursor.getString(cursor.getColumnIndex("height"));
				String type = cursor.getString(cursor.getColumnIndex("type"));
				String rsport = cursor.getString(cursor.getColumnIndex("rsport"));

				MyLedInfo ledInfo = new MyLedInfo();
				ledInfo.setId(ledid);
				ledInfo.setLedip(ledip);
				ledInfo.setLedport(ledport);
				ledInfo.setLeduid(leduid);
				ledInfo.setMovemode(movemode);
				ledInfo.setMovespeed(movespeed);
				ledInfo.setDwelltime(dwelltime);
				ledInfo.setLedcolor(ledcolor);
				ledInfo.setShowcolor(showcolor);
				ledInfo.setTypeface(typeface);
				ledInfo.setTypesize(typesize);
				ledInfo.setMatercont(matercont);
				ledInfo.setPassid(passid);
				ledInfo.setPassname(passname);
				ledInfo.setPasstype(passtype);
				ledInfo.setWidth(width);
				ledInfo.setHeight(height);
				ledInfo.setType(type);
				ledInfo.setRsport(rsport);
				hashMap.put(cameraip, ledInfo);
			}
		} catch (Exception e) {
			if(cursor != null){
				cursor.close();
			}
		}finally{
			if(cursor != null){
				cursor.close();
			}
		}
		return hashMap;
	}

	/**
	 * 删除所有Camera数据
	 */
	public void deleteCameraData() {  
		String sql = "delete from "+DBHelper.CAMERA_INFO;
		db.execSQL(sql);
	}

	/**
	 * 删除所有LED数据
	 */
	public void deleteLedData() {  
		String sql = "delete from "+DBHelper.LED_INFO;
		db.execSQL(sql);
	}
}

package com.zld.utils;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.zld.service.DataBaseService;

/**
 * 停车场缓存
 * 
 * @author Administrator
 */
public class ParkingMap {

	private static Map<Long, Long> LaLaMap = new HashMap<Long, Long>();
	private static Map<Long, String> PARKIDNAMEMAP = new HashMap<Long, String>();
	private static Map<Long, String> USERIDNAMEMAP = new HashMap<Long, String>();
	private static Map<String,Long> NFCUINMAP = new  HashMap<String, Long>();
	private static Long LastTime = System.currentTimeMillis()/1000;

	/**
	 * 判断是否是在15分钟后拉拉
	 * 
	 * @param uin
	 * @return
	 */
	public synchronized static boolean isCanRecordLaLa(Long uin) {
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		if (hour >= 20 || hour < 7) {// 07-20计算lala成绩
			return false;
		}
		Long time = System.currentTimeMillis() / 1000;
		boolean result = true;
		if (LaLaMap.get(uin) != null && time < (LaLaMap.get(uin) + 15 * 60))
			result = false;
		Long ltime = LaLaMap.get(uin) == null ? time : LaLaMap.get(uin);
		System.err.println("lala record,uin=" + uin + ",time="
				+ TimeTools.getTime_yyyyMMdd_HHmmss(time * 1000) + ",isrecord:"
				+ result + ",last record:"
				+ TimeTools.getTime_yyyyMMdd_HHmmss(ltime * 1000));
		if (result)
			LaLaMap.put(uin, time);
		return result;
	}

	public synchronized static void setLastLalaTime(Long uin, Long time) {
		LaLaMap.put(uin, time);
	}
	
	public synchronized static void putParkName(Long id,String name){
		PARKIDNAMEMAP.put(id, name);
	}
	
	public static String getParkName(Long id){
		return PARKIDNAMEMAP.get(id);
	}
	
	public synchronized static void putUserName(Long id,String name){
		USERIDNAMEMAP.put(id, name);
	}
	
	public static String getUserName(Long id){
		return USERIDNAMEMAP.get(id);
	}
	
	public synchronized static void  putNfcUid(String uuid,Long uin){
		NFCUINMAP.put(uuid, uin);
	}
	public synchronized static Long  getNfcUid(String uuid,DataBaseService dService){
		Long nowTime = System.currentTimeMillis()/1000;
		if(NFCUINMAP.size()==0||(nowTime-LastTime>5*60)){
			LastTime=nowTime;
			List<Map<String, Object>> list = dService.getAll("select nfc_uuid,uin from com_nfc_tb where uin>?",new Object[]{0});
			System.err.println(">>>>>>>>>>>>>>>初始化绑定NFC用户数："+list.size());
			if(list!=null&&list.size()>0){
				for(Map<String, Object> map : list){
					NFCUINMAP.put(map.get("nfc_uuid")+"",(Long)map.get("uin"));
				}
			}
		}
		System.err.println(">>>>>>>>>>>>>>>绑定NFC用户数："+NFCUINMAP.size());
		return NFCUINMAP.get(uuid);
	}
	
	
	public static void clearMap(){
		PARKIDNAMEMAP.clear();
		USERIDNAMEMAP.clear();
	}
	/*
	 * private static List<Map<String,Object>> parkingMap = null;
	 * 
	 * public static List<Map<String,Object>> getAllParking(){ return
	 * parkingMap; }
	 * 
	 * public static Integer getParkingNumber(){ return parkingMap.size(); }
	 * 
	 * public static void init(DataBaseService daService) { if(parkingMap!=null)
	 * parkingMap.clear(); parkingMap=daService.getAll(
	 * "select * from com_info_tb where state=? order by update_time desc",new
	 * Object[]{0}); }
	 *//**
	 * 更新
	 * 
	 * @param parkMap
	 */
	/*
	 * public static void updateParkingMap(Map<String,Object> parkMap){
	 * if(parkingMap==null||parkMap==null) return ; Long cid =
	 * (Long)parkMap.get("id"); for(Map map : parkingMap){ Long id =
	 * (Long)map.get("id"); if(id.intValue()==cid.intValue()){
	 * parkingMap.remove(map); parkingMap.add(parkMap); break; } } }
	 *//**
	 * 添加
	 * 
	 * @param parkMap
	 */
	/*
	 * public static void addParkingMap(Map<String,Object> parkMap){
	 * if(parkingMap==null||parkMap==null) return ; parkingMap.add(parkMap); }
	 *//**
	 * 删除
	 * 
	 * @param comId
	 */
	/*
	 * public static void deleteParkingMap(Long comId){
	 * if(parkingMap==null||comId==null) return ; for(Map parkMap : parkingMap){
	 * Long id = (Long)parkMap.get("id"); if(id.intValue()==comId.intValue()){
	 * parkingMap.remove(parkMap); break; } } }
	 */
}

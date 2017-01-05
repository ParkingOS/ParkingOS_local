package com.zld.utils;

import java.util.List;
import java.util.Map;

import org.json.JSONObject;


/**
 * json的工具类
 */
public class JsonUtil {
	
	public static String Map2Json(List mapList ,int page,long total,String fieldsstr,String id) {
		String json = "{\"page\":1,\"total\":0,\"rows\":[]}";
		if(mapList != null && !mapList.isEmpty()) {
			StringBuffer sb = new StringBuffer("");
			sb.append("{\"page\":"+ page +",\"total\":"+total+",\"rows\": [");
			if(!"".equals(fieldsstr)) {
				String[] fieldsstrArray = fieldsstr.split("\\_\\_");
				for (int i = 0; i < mapList.size(); i++) {
					if(i != 0)
						sb.append(",");
					sb.append(Map2Json((Map)mapList.get(i), fieldsstrArray,id) + "]}");
				}
			}
			sb.append("]}");
			json = sb.toString();
		}
		return json;
	}
	public static String Map2Json(Map map,String[] fieldsstrArray,String id) {
		StringBuffer json = new StringBuffer("");
		if(map != null) {
			json.append("{\"id\":\""+map.get(id)+"\",\"cell\":[");
			for (int j = 0; j < fieldsstrArray.length; j++) {
				String fieldName = fieldsstrArray[j];
				String _filedStr = "";
				Object _filedObject = map.get(fieldName);
				_filedStr = _filedObject==null?"":_filedObject.toString();
				_filedStr = _filedStr.trim();
				if((fieldName.equals("limitday")||fieldName.equals("distru_date")
						||fieldName.equals("limit_day")||fieldName.indexOf("time")!=-1)
						&&Check.isLong(_filedStr)){
					if(_filedStr.length()>5){//
						_filedStr = TimeTools.getTime_yyyyMMdd_HHmmss(Long.valueOf(_filedStr)*1000);
						if(fieldName.equals("limitday")&&_filedStr.length()>10)
							_filedStr = _filedStr.substring(0,10);
					}
				}else if(fieldName.equals("auth_flag")){
					if(_filedStr.equals("2"))
						_filedStr="收费员";
					else if(_filedStr.equals("3"))
						_filedStr="财务";
					else if(_filedStr.equals("5"))
						_filedStr="市场专员";
				}else if(fieldName.equals("duration")){
					Long start = (Long)map.get("create_time");
					Long end = (Long)map.get("end_time");
					if(start!=null&&end!=null)
						_filedStr=StringUtils.getTimeString(start, end);
					//map.put("duration", StringUtils.getTimeString(start, end));
//					if(start!=null&&end!=null){
//						Long duration = StringUtils.getHour(Long.valueOf(start+""),Long.valueOf(end+""));
//						_filedStr=duration+"";
//						Object price = map.get("price");
//						if(price!=null){
//							map.put("total", Math.round(Double.valueOf(price+"")*Double.valueOf(duration))+".00");
//						}
//					}
				}
				if(j == fieldsstrArray.length -1)
					json.append("\""+ _filedStr +"\"");
				else
					json.append("\""+ _filedStr +"\",");
			}
		}
		return json.toString();
	}
	
	public static String anlysisMap2Json(List mapList ,int page,long total,String fieldsstr,String id,String money) {
		String json = "{\"page\":1,\"total\":0,\"money\":\""+money+"\",\"rows\":[]}";
		if(mapList != null && !mapList.isEmpty()) {
			StringBuffer sb = new StringBuffer("");
			sb.append("{\"page\":"+ page +",\"total\":"+total+",\"money\":\""+money+"\",\"rows\": [");
			if(!"".equals(fieldsstr)) {
				String[] fieldsstrArray = fieldsstr.split("\\_\\_");
				for (int i = 0; i < mapList.size(); i++) {
					if(i != 0)
						sb.append(",");
					sb.append(Map2Json((Map)mapList.get(i), fieldsstrArray,id) + "]}");
				}
			}
			sb.append("]}");
			json = sb.toString();
		}
		return json;
	}
	/**
	 * json分析
	 * @param rescontent
	 * @param key
	 * @return
	 */
	public static String getJsonValue(String rescontent, String key) {
		JSONObject jsonObject;
		String v = null;
		try {
			jsonObject = new JSONObject(rescontent);
			v = jsonObject.getString(key);
		} catch (Exception e) {
			return "-100";
		}
		return v;
	}
	
	/**
	 * json分析
	 * @param rescontent
	 * @param key
	 * @return
	 */
	public static JSONObject getJsonObjValue(String rescontent, String key) {
		JSONObject jsonObject;
		JSONObject v = null;
		try {
			jsonObject = new JSONObject(rescontent);
			v = jsonObject.getJSONObject(key);
		} catch (Exception e) {
			return null;
		}
		return v;
	}
	
	public static String AuthMap2Json(List mapList ,int page,long total,String fieldsstr,String id) {
		String json = "{\"page\":1,\"total\":0,\"rows\":[]}";
		if(mapList != null && !mapList.isEmpty()) {
			StringBuffer sb = new StringBuffer("");
			sb.append("{\"page\":"+ page +",\"total\":"+total+",\"rows\": [");
			if(!"".equals(fieldsstr)) {
				String[] fieldsstrArray = fieldsstr.split("\\_\\_");
				for (int i = 0; i < mapList.size(); i++) {
					if(i != 0)
						sb.append(",");
					sb.append(AuthMap2Json((Map)mapList.get(i), fieldsstrArray,id) + "]}");
				}
			}
			sb.append("]}");
			json = sb.toString();
		}
		return json;
	}
	
	public static String AuthMap2Json(Map map,String[] fieldsstrArray,String id) {
		StringBuffer json = new StringBuffer("");
		if(map != null) {
			json.append("{\"id\":\""+map.get(id)+"\",\"cell\":[");
			for (int j = 0; j < fieldsstrArray.length; j++) {
				String fieldName = fieldsstrArray[j];
				String _filedStr = "";
				Object _filedObject = map.get(fieldName);
				_filedStr = _filedObject==null?"":_filedObject.toString();
				if((fieldName.equals("reg_time")||fieldName.equals("create_time")||fieldName.equals("logon_time")
						||fieldName.equals("end_time")||fieldName.equals("update_time")||fieldName.equals("limitday")
						||fieldName.equals("distru_date")||fieldName.equals("b_time")||fieldName.equals("e_time"))
						&&Check.isLong(_filedStr)){
					if(_filedStr.length()>5){//
						_filedStr = TimeTools.getTime_yyyyMMdd_HHmmss(Long.valueOf(_filedStr)*1000);
						if(fieldName.equals("limitday")&&_filedStr.length()>10)
							_filedStr = _filedStr.substring(0,10);
					}
				}else if(fieldName.equals("duration")){
					Long start = (Long)map.get("create_time");
					Long end = (Long)map.get("end_time");
					if(start!=null&&end!=null)
						_filedStr=StringUtils.getTimeString(start, end);
				}
				if(j == fieldsstrArray.length -1)
					json.append("\""+ _filedStr +"\"");
				else
					json.append("\""+ _filedStr +"\",");
			}
		}
		return json.toString();
	}
}

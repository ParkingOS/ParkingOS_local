package com.mserver.utils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

public class StringUtils {

	
	public static boolean isNotNull(String value){
		if(value==null||value.equals(""))
			return false;
		return true;
	}
	
	public static boolean isNumber(String value){
		if(value==null||value.equals(""))
			return false;
		try {
			Long a = Long.valueOf(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static boolean isDouble(String value){
		if(value==null||value.equals(""))
			return false;
		try {
			Double a = new Double(value);
		} catch (Exception e) {
			return false;
		}
		return true;
	}
	
	public static String _2null(String value){
		if("".equals(value))
			return null;
		return value;
	}
	
	public static Double getDoubleValue(String value){
		Double double1 = null;
		try {
			double1 = Double.valueOf(value);
		} catch (Exception e) {
			double1 =0.0d;
		}
		return double1;
	}
	
	public static double mul(double d1,double d2){ 
        BigDecimal bd1 = new BigDecimal(Double.toString(d1)); 
        BigDecimal bd2 = new BigDecimal(Double.toString(d2)); 
        return bd1.multiply(bd2).doubleValue(); 
	} 
	
	public static Long getLongMilliSecondFromStrDate(String strDate, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		long millSeconds = new GregorianCalendar().getTimeInMillis();
		try {
			millSeconds = sdf.parse(strDate).getTime();
		} catch (Exception e) {
			// logger.error("---------get seconds error:"+e.getMessage());
		}
		return new Long(millSeconds);
	}
	
	public static String getPre (String value){
		for(int i= 0;i<value.length();i++){
			char a = value.charAt(i);
			if(!String.valueOf(a).equals("0"))
				return value.substring(0,i);
		}
		return "";
	}
	
	public static Long getHour(Long start,Long end){
		if(end!=null&&start!=null){
			Long hours = (end-start)/3600;
			if((end-start)%60!=0)
				hours+=1;
			return hours;
		}
		return 0L;
	}
	
	/**
	 * 生成 xml文件流
	 */
	public static String createXML(Map<String, Object > info) {// 获取最大访客数XML
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		xml.append("<content>");
		for(String key : info.keySet()){
			xml.append("<"+key+">"+info.get(key)+"</"+key+">");
		}
		xml.append("</content>");
		return xml.toString();
	}
	
	
	/**
	 * 生成 xml文件流
	 */
	public static String createXML(Map<String, Object > info,String flag) {// 获取最大访客数XML
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		xml.append("<content id='"+flag+"'>");
		for(String key : info.keySet()){
			xml.append("<"+key+">"+info.get(key)+"</"+key+">");
		}
		xml.append("</content>");
		return xml.toString();
	}
	
	/**
	 * 生成 xml文件流
	 */
	public static String createXML(List<Map<String, Object >> info,String flag) {// 获取最大访客数XML
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		xml.append("<content id='"+flag+"'>");
		for(Map<String, Object > map : info){
			xml.append("<info>");
			for(String key : map.keySet()){
				xml.append("<"+key+">"+map.get(key)+"</"+key+">");
			}
			xml.append("</info>");
		}
		xml.append("</content>");
		return xml.toString();
	}
	public static String getAccount(Long start,Long end,Double price){
		if(start!=null&&end!=null){
			Long duration = getHour(start,end);
			return Math.round(Double.valueOf(price+"")*Double.valueOf(duration))+".00";
		}
		return "";
	}
	
	public static String createJson(List<Map<String, Object >> info){
		String json = "[";
		int i=0;
		int j=0;
		if(info!=null&&info.size()>0){
			for(Map<String, Object > map : info){
				if(i!=0)
					json +=",";
				json+="{";
				for(String key : map.keySet()){
					if(j!=0)
						json +=",";
					json +="\""+key+"\":\""+map.get(key)+"\"";
					j++;
				}
				json+="}";
				i++;
				j=0;
			}
			
		}
		json +="]";
		return json;
	}
	public static String createJson(Map<String, Object > info){
		String json = "";
		int j=0;
		if(info!=null&&info.size()>0){
			json+="{";
			for(String key : info.keySet()){
				//System.out.println(key);
				if(j!=0)
					json +=",";
				if(info.get(key)!=null&&info.get(key).toString().startsWith("["))
					json +="\""+key+"\":"+info.get(key);
				else {
					json +="\""+key+"\":\""+info.get(key)+"\"";
				}	
				j++;
			}
			json+="}";
		}else {
			json="{}";
		}
		return json;
	}
}

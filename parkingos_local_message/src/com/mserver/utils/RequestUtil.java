package com.mserver.utils;

import javax.servlet.http.HttpServletRequest;

public class RequestUtil {

	public static String processParams(HttpServletRequest request,String param){
		if(request.getParameter(param)!=null)
			return request.getParameter(param);
		return "";
	}
	
	public static Integer getInteger(HttpServletRequest request ,String param,Integer defaultvalue){
		String value = processParams(request, param);
		if(value.equals(""))
			return defaultvalue;
		else {
			try {
				Integer integer = Integer.valueOf(value);
				return integer;
			} catch (Exception e) {
				return defaultvalue;
			}
		}
	}
	public static Long getLong(HttpServletRequest request ,String param,Long defaultvalue){
		String value = processParams(request, param);
		if(value.equals(""))
			return defaultvalue;
		else {
			try {
				Long lvalue = Long.valueOf(value);
				return lvalue;
			} catch (Exception e) {
				return defaultvalue;
			}
		}
	}
	public static Double getDouble(HttpServletRequest request ,String param,Double defaultvalue){
		String value = processParams(request, param);
		if(value.equals(""))
			return defaultvalue;
		else {
			try {
				Double dvalue = Double.valueOf(value);
				return dvalue;
			} catch (Exception e) {
				return defaultvalue;
			}
		}
	}
}

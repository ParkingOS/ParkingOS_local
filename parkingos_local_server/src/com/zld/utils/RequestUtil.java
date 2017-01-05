package com.zld.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.zld.AjaxUtil;

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
	
	public static String getString(HttpServletRequest request ,String param){
		return  processParams(request, param);
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
	
	
	/**
	 * 
	 * @param request
	 * @param table
	 * @param tableAlia
	 * @param exculdeFiled ÅÅ³ýµÄ×Ö¶Î
	 * @return
	 */
	public static SqlInfo customSearch(HttpServletRequest request,String table,String tableAlia,String[] exculdeFiled) {
		String fieldStr = RequestUtil.processParams(request, "fieldsstr");
		String [] fields = fieldStr.split("\\_\\_");
		List<JdbcFieldUtils> jflist = new ArrayList<JdbcFieldUtils>();
		
		for(String f:fields){
			boolean isExculde = false;
			if(exculdeFiled!=null){
				for(String ef : exculdeFiled){
					if(ef.equals(f)){
						isExculde=true;
						break;
					}
				}
			}
			if(isExculde)
				continue;
			int type = GetFieldType.getFieldType(table, f);
			String value = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f));
			if(!value.equals("")){
				if(type==12){
					jflist.add(new JdbcFieldUtils(JdbcTypeOperate.TYPE_STRING, f, JdbcTypeOperate.OPER_LK, value, null, null));
				}else {
					String operType = RequestUtil.processParams(request, f);
					String time_start = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_start"));
					String time_end = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_end"));
					List<JdbcFieldUtils> subSql =operRequestParam(f, operType, time_start, time_end, type);
					jflist.addAll(subSql);
				}
			}else if(type==93){
				String time_start = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_start"));
				String time_end = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_end"));
				if(!time_end.equals("")&&!time_start.equals("")){
					List<JdbcFieldUtils> subSql =operRequestParam(f, "between", time_start, time_end, type);
					jflist.addAll(subSql);
				}
			}else if(type!=12){
				value = RequestUtil.processParams(request, f+"_start");
				if(value!=""&&Check.isLong(value)){
					if(!(f.equals("parking_type")&&value.equals("-1"))){
						jflist.add(new JdbcFieldUtils(type, f, JdbcTypeOperate.OPER_EQ, Long.valueOf(value), null, null));
					}
				}
			}
		}
		JdbcCreateSqlUtil jcsuCreateSqlUtil = new JdbcCreateSqlUtil();
		jcsuCreateSqlUtil.setTbaleAlia(tableAlia);
		jcsuCreateSqlUtil.setFieldLists(jflist);
		SqlInfo sqlInfo= jcsuCreateSqlUtil.CreateSearchSql();
		return sqlInfo;
	}

	public static SqlInfo customSearch(HttpServletRequest request,String table) {
		String fieldStr = RequestUtil.processParams(request, "fieldsstr");
		String [] fields = fieldStr.split("\\_\\_");
		List<JdbcFieldUtils> jflist = new ArrayList<JdbcFieldUtils>();
		
		for(String f:fields){
			int type = GetFieldType.getFieldType(table, f);
			String value = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f));
			if(!value.equals("")){
				if(type==12){
					jflist.add(new JdbcFieldUtils(JdbcTypeOperate.TYPE_STRING, f, JdbcTypeOperate.OPER_LK, value, null, null));
				}else {
					String operType = RequestUtil.processParams(request, f);
					String time_start = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_start"));
					String time_end = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_end"));
					List<JdbcFieldUtils> subSql =operRequestParam(f, operType, time_start, time_end, type);
					jflist.addAll(subSql);
				}
			}else if(type==93){
				String time_start = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_start"));
				String time_end = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, f+"_end"));
				if(!time_end.equals("")&&!time_start.equals("")){
					List<JdbcFieldUtils> subSql =operRequestParam(f, "between", time_start, time_end, type);
					jflist.addAll(subSql);
				}
			}else if(type!=12){
				value = RequestUtil.processParams(request, f+"_start");
				if(value!=""&&Check.isLong(value)){
					if(!(f.equals("parking_type")&&value.equals("-1"))){
						jflist.add(new JdbcFieldUtils(type, f, JdbcTypeOperate.OPER_EQ, Long.valueOf(value), null, null));
					}
				}
			}
		}
		JdbcCreateSqlUtil jcsuCreateSqlUtil = new JdbcCreateSqlUtil();
		jcsuCreateSqlUtil.setFieldLists(jflist);
		SqlInfo sqlInfo= jcsuCreateSqlUtil.CreateSearchSql();
		return sqlInfo;
	}


	/**
	 * @param request
	 * @param name
	 */
	// ´¦Àí²éÑ¯£¬·µ»ØsqlÓï¾ä
	public static List<JdbcFieldUtils> operRequestParam(String fileName,
			String operType, String time_start, String time_end,Integer inputType) {
		// 2012-08-05 10:00:00
		List<JdbcFieldUtils> jfuList = new ArrayList<JdbcFieldUtils>();
		int fieldType = JdbcTypeOperate.TYPE_INT;
		if (operType.equals("null")) {
			jfuList.add(new JdbcFieldUtils(fieldType, fileName,
					JdbcTypeOperate.OPER_NU, "", null, null));
		} else if (operType.equals("notnull")) {
			jfuList.add(new JdbcFieldUtils(fieldType, fileName,
					JdbcTypeOperate.OPER_NN, "", null, null));
		} else {
			if (time_start != null && !time_start.equals("")) {
				if (operType.equals("between") && time_end != null
						&& !time_end.equals("")) {
					if (inputType==4 && Check.isLong(time_start)
							&& Check.isLong(time_end)) {
						jfuList.add(new JdbcFieldUtils(fieldType, fileName,
								JdbcTypeOperate.OPER_BT, null, Long.valueOf(time_start), Long.valueOf(time_end)));
					}else if(inputType==3&& Check.isDouble(time_start)&&Check.isDouble(time_end)){
						jfuList.add(new JdbcFieldUtils(inputType, fileName,
								JdbcTypeOperate.OPER_BT, null, Double.valueOf(time_start), Double.valueOf(time_end)));
					}else {
						jfuList.add(new JdbcFieldUtils(fieldType,	fileName,JdbcTypeOperate.OPER_BT,
								null,TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(time_start),
								TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(time_end)));
					}
				} else {
					int operate = 0;
					Long value = null;
					Double dvalue = null;
					if (operType.equals("1")) {
						operate = JdbcTypeOperate.OPER_GE;
					} else if (operType.equals("2")) {
						operate = JdbcTypeOperate.OPER_LE;
					} else if (operType.equals("3")) {
						operate = JdbcTypeOperate.OPER_EQ;
					}
					if (inputType!=4&&inputType!=3)
						value = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(time_start);
					else if (inputType==4&&Check.isLong(time_start)) {
						value = Long.valueOf(time_start);
					}else if(inputType==3&&Check.isDouble(time_start)){
						dvalue=Double.valueOf(time_start);
					}
					if(inputType==3){
						jfuList.add(new JdbcFieldUtils(fieldType, fileName,
								operate, dvalue, null, null));
					}else {
						jfuList.add(new JdbcFieldUtils(fieldType, fileName,
								operate, value, null, null));
					}
					
				}
			}else {
				if(operType.length()>2&&Check.isNumber(operType))
					jfuList.add(new JdbcFieldUtils(fieldType, fileName,
						JdbcTypeOperate.OPER_EQ, Integer.valueOf(operType), null, null));
			}
		}
		return jfuList;
	}
}

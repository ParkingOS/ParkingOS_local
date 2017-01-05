package com.zld.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public class GetTableFields {

	/**
	 * 取得表格字段
	 * @param tqbleName
	 * @param type
	 * @return
	 */
	public static String getFieddsByTableName(String tableName,String type){
		if(tableName.equals("com_info")){
			if(type.equals("query")){
				return getComInfoQueryFields();
			}
		}
		return null;
	}
	
	private static String getComInfoQueryFields(){
		List<TableFields> list = new ArrayList<TableFields>();
		List<NoList> typeList = new ArrayList<NoList>();
		typeList.add(new NoList("-1", "全部"));
		typeList.add(new NoList("0", "付费"));
		typeList.add(new NoList("1", "免费"));
		List<NoList> parkTypeList = new ArrayList<NoList>();
		parkTypeList.add(new NoList("-1", "全部"));
		parkTypeList.add(new NoList("0", "地面"));
		parkTypeList.add(new NoList("1", "地下"));
		parkTypeList.add(new NoList("2", "占道"));
		list.add(new TableFields("编号", "id", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("名称", "company_name", "", "text", 200, null, false, false, false, false, true,null));
		list.add(new TableFields("车场类型", "type", "", "select", 50, null, false, false, false, false, true,typeList));
		list.add(new TableFields("登录帐号", "strid", "", "text", 50, null, false, false, false, false, false,null));
		list.add(new TableFields("详细地址", "address", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("电话", "phone", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("手机", "moblie", "", "text", 50, null, false, false, false, false, true,null));
		list.add(new TableFields("联系人", "nickname", "", "text", 50, null, false, false, false, false, false,null));
		Map<String, List<TableFields>> map = new HashMap<String, List<TableFields>>();
		map.put("root", list);
		JSONObject json= JSONObject.fromObject(map);
		return json.toString();
	}
	public static void main(String[] args) {
		System.out.println(getComInfoQueryFields());
	}
}

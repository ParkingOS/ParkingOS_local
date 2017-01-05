package com.zld.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.jxpath.JXPathContext;

import com.zld.service.DataBaseService;

public class JdbcUtils {
	@SuppressWarnings("unchecked")
	public static List createObject(Class<?> c,List<Map<String, String>> resultList) throws Exception{
		
		List list= new ArrayList();
		for(Map<String, String> m : resultList){
			Iterator<String> keys = m.keySet().iterator();
			Object t =  c.newInstance();
			JXPathContext jxpcontext = JXPathContext.newContext(t);
			while(keys.hasNext()){
				String key = keys.next().toLowerCase();
				if(key.equals("my_rownum"))//排除分页字段
					continue;
				try {
					jxpcontext.setValue(key, m.get(key.toUpperCase()));
				} catch (Exception e) {
					System.err.println("error:method="+key);
					//e.printStackTrace();
				}
			}
			list.add(t);
		}
		return list;
	}
	private static int initData(JSONObject comJo, Map<String,String> columnsList,String tablename,DataBaseService daService) {
		StringBuffer insertsql = new StringBuffer("insert into "+tablename +" (");
		StringBuffer valuesql = new StringBuffer(" values(");
		ArrayList values = new ArrayList();
		for (Map.Entry<String, String> entry : columnsList.entrySet()) {
			System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			try{
				if(comJo.getString(entry.getKey())!=null&&!"null".equals(comJo.getString(entry.getKey()))){
					insertsql.append(entry.getKey());
					valuesql.append("?,");
					if(entry.getValue().startsWith("bigint")){
						values.add(comJo.getLong(entry.getKey()));
					}else if(entry.getValue().startsWith("numeric")){
						values.add(comJo.getDouble(entry.getKey()));
					}else if(entry.getValue().startsWith("integer")){
						values.add(comJo.getInt(entry.getKey()));
					}else if(entry.getValue().startsWith("charact")){
						values.add(comJo.getString(entry.getKey()));
					}
				}
			}catch (Exception e) {
				System.out.println("0-----------------"+e.getMessage());
			}
		}
		String sql = "";
		int r = 0;
		if(insertsql.toString().endsWith(",")&&valuesql.toString().endsWith(",")){
			sql = insertsql.substring(0,insertsql.length()-1)+") "+valuesql.substring(0,insertsql.length()-1)+")";
			r = daService.update(sql, values);
		}
		return r;
	}

	/**
	 * 根据表获取所有的字段名和字段类型
	 * @param tablename
	 * @return
	 */
	public static Map getColumns(String tablename,DataBaseService daService){
		HashMap<String, String> hashMap = new HashMap<String,String>();
		List list = daService.getAll("select column_name,data_type from information_schema.columns where table_schema='public' and table_name= ? ", new Object[]{"order_tb"});
		for (Object object : list) {
			Map map = (Map)object;
			hashMap.put(map.get("column_name")+"",map.get("data_type")+"");
		}
		return hashMap;
	}
	public static int syncdatajo(String tablename,DataBaseService daService,JSONObject comJo){
		Map<String,String> columns = getColumns(tablename, daService);
		int r = initData(comJo,columns,tablename,daService);
		return r;
	}
	public static int syncdataja(String tablename,DataBaseService daService,JSONArray ja){
		Map<String,String> columns = getColumns(tablename, daService);
		int r =0;
		for (int i = 0; i < ja.size(); i++) {
			JSONObject jo  = ja.getJSONObject(i);
			r = initData(jo,columns,tablename,daService);
			
		}
		return r;
	}
}

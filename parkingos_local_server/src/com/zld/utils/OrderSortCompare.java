package com.zld.utils;

import java.util.Comparator;
import java.util.Map;

public class OrderSortCompare implements Comparator<Map<String, Object>>{
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		Long t1 = (Long)o1.get("create_time");
		Long t2 = (Long)o2.get("create_time");
		if(t1!=null&&t2!=null){
			return Long.valueOf((t2-t1)).intValue();
		}
		return 0;
	}
 }
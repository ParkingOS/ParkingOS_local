package com.zld.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemMemcache {
	
	public static Map<Long,List<Map<String, Object>>> PriceMap = new HashMap<Long, List<Map<String,Object>>>();
	
	public static List<Map<String, Object>> getPriceByComid(Long comid){
		if(PriceMap!=null)
			return PriceMap.get(comid);
		return null;
	}
}

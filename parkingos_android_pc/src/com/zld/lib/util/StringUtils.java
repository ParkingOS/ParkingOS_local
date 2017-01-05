package com.zld.lib.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import com.zld.lib.constant.Constant;
import com.zld.util.Check;

import android.content.Context;

public class StringUtils {

	/**
	 * 将字符串拆分为集合
	 * @param str
	 * @return
	 */
	public static List<String> transformList(String str) {
		String[] split = str.split("");
		List<String>  list = Arrays.asList(split);
		if(list.size()>7){
			list = list.subList(1, 8);
		}
		return list;
	}

	/**
	 * 将集合合并成字符串
	 * @param c
	 * @return
	 */
	public static String transformString(@SuppressWarnings("rawtypes") Collection c) {  
		if (c == null || c.size() == 0) {  
			return "";  
		}  
		@SuppressWarnings("rawtypes")
		Iterator it = c.iterator();  
		StringBuilder sb = new StringBuilder();  
		while (it.hasNext()) {  
			sb.append(it.next());  
		}  
		return sb.toString();  
	}

	/**
	 * 车牌号正则
	 * @param plate
	 * @return 
	 * @return
	 */
	public static  boolean isCarPlate(String plate) {
		String check = "^[\\u4e00-\\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{4}[a-zA-Z_0-9_\\u4e00-\\u9fa5]$|^[a-zA-Z]{2}\\d{7}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(plate);
		boolean isMatched = matcher.matches();
		return isMatched;
	}

	/**
	 * 获取两个List的不同元素     
	 * @param list1     
	 * @param list2     
	 * @return     
	 * List<String> listDifferent = StringUtils.getListDifferent(smOrderidList,orderidList);
	 */    
	public static List<String> getListDifferent(ArrayList<String> list1, ArrayList<String> list2) {
		//		long st = System.nanoTime();
		Map<String,Integer> map = new HashMap<String,Integer>(list1.size()+list2.size());
		List<String> diff = new ArrayList<String>();
		List<String> maxList = list1;
		List<String> minList = list2;
		if(list2.size()>list1.size()) {  
			maxList = list2; 
			minList = list1; 
		}       
		for (String string : maxList) {     
			map.put(string, 1);   
		}      
		for (String string : minList) {  
			Integer cc = map.get(string);   
			if(cc!=null){              
				map.put(string, ++cc);    
				continue;    
			}          
			map.put(string, 1);   
		}      
		for(Map.Entry<String, Integer> entry:map.entrySet()){    
			if(entry.getValue()==1){    
				diff.add(entry.getKey()); 
			}     
		}       
		//		System.out.println("getDiffrent4 total times "+(System.nanoTime()-st));
		return diff;     
	}  

	//	public static String subDecNum(float a) {
	//		DecimalFormat   df   =new  DecimalFormat("#.0");  
	//		String format = df.format(a);
	//		return format;
	//	}

	/**
	 * 去除末尾点零
	 * @param str
	 * @return
	 */
	public static String removeZero(String str) {
		int indexOf = str.indexOf(".");
		if(indexOf != -1){
			String substring = str.substring(0,indexOf+2);
			String substring2 = str.substring(indexOf+1,indexOf+2);
			if(substring2.equals("0")){
				String substring3 = str.substring(0,indexOf);
				return substring3;
			}
			return substring;
		}
		return str;
	}

	public static int stringToInt(String data){
		return Integer.parseInt(data);
	}

	public static String subString(String content, int start, int end){
		int lenth = content.length();
		if (start > lenth || end > lenth){
			throw new RuntimeException();
		}else{
			return content.substring(start, end);
		}
	}

	public static String encodeString(String content, String encode) throws UnsupportedEncodingException{
		return URLEncoder.encode(URLEncoder.encode(content, encode),encode);
	}
	
	/**军警车的判断*/
	public static boolean isPolice(String carNumber){
		if(carNumber.contains("军")||carNumber.contains("空")
				||carNumber.contains("海")||carNumber.contains("北")
				||carNumber.contains("沈")||carNumber.contains("兰")
				||carNumber.contains("济")||carNumber.contains("南")
				||carNumber.contains("广")||carNumber.contains("成")
				||carNumber.contains("警")||carNumber.contains("消")
				||carNumber.contains("边")||carNumber.contains("水")
				||carNumber.contains("电")||carNumber.contains("林")
				||carNumber.contains("通")||carNumber.startsWith("WJ")){
			return true;
		}else{
			return false;
		}

	}

	public static String buildCarNumber(Context context) {
//		String carnumber = SharedPreferencesUtils.getParam(
//				context.getApplicationContext(), "CarNumberInfo", "carnumber", "0");
//		carnumber = ""+(Integer.parseInt(carnumber)+1);
//		if(carnumber.length()<5){
//			int i = 5-carnumber.length();
//			for(int j = 0;j < i; j++){
//				carnumber = "0"+carnumber;
//			}
//		}
//		SharedPreferencesUtils.setParam(
//				context.getApplicationContext(), "CarNumberInfo", "carnumber", carnumber);
		Long time = System.currentTimeMillis();
		String timeStr = time.toString();
		String carnumber = timeStr.substring(timeStr.length()-6);
		return carnumber;
	}
	
    //把double类型的字符串保留两位小数返回；
	public static Float formatFloat(Object value) {
		if (isFloat(value + "")) {
			DecimalFormat df = new DecimalFormat("#.00");
			String dv = df.format(Float.valueOf(value + ""));
			if (isFloat(dv))
				return Float.valueOf(dv);
		}
		return 0.0f;
	}

	//判断是否是double类型的数据；
	public static boolean isFloat(String value) {
		if (value == null)
			return false;
		try {
			@SuppressWarnings("unused")
			Float d = Float.valueOf(value);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**军警车的判断*/
	public static boolean isPolice(String carNumber,int nType){
		if(carNumber.contains("军")||carNumber.contains("空")
				||carNumber.contains("海")||carNumber.contains("北")
				||carNumber.contains("沈")||carNumber.contains("兰")
				||carNumber.contains("济")||carNumber.contains("南")
				||carNumber.contains("广")||carNumber.contains("成")
				||carNumber.contains("警")||carNumber.contains("消")
				||carNumber.contains("边")||carNumber.contains("水")
				||carNumber.contains("电")||carNumber.contains("林")
				||carNumber.contains("通")||carNumber.startsWith("WJ")
				||nType == Constant.LT_ARMPOL 
				||nType == Constant.LT_ARMPOL2 
				||nType == Constant.LT_ARMPOL2_ZONGDUI 
				||nType == Constant.LT_ARMPOL_ZONGDUI 
				||nType == Constant.LT_ARMY 
				||nType == Constant.LT_ARMY2 
				||nType == Constant.LT_POLICE){
			return true;
		}else{
			return false;
		}

	}
	
	/** 
	   * Json 转成 Map<> 
	   * @param jsonStr 
	   * @return 
	*/  
	public static Map<String, String> getMapForJson(String jsonStr) {
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(jsonStr);
			Iterator<String> keyIter = jsonObject.keys();
			String key;
			String value;
			Map<String, String> valueMap = new HashMap<String, String>();
			while (keyIter.hasNext()) {
				key = keyIter.next();
				value = (String) jsonObject.get(key);
				valueMap.put(key, value);
			}
			return valueMap;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}

	public static Double formatDouble(Object value){
		if(Check.isDouble(value+"")){
			DecimalFormat df=new DecimalFormat("#.00"); 
			String dv = df.format(Double.valueOf(value+""));
			if(Check.isDouble(dv))
				return Double.valueOf(dv);
		}
		return 0.0d;
	}

	  public static boolean isDouble(String value){
	    	if(value==null)
	    		return false;
			try {
				Double d = Double.valueOf(value);
				return true;
			} catch (Exception e) {
				return false;
			}
	    }
	
}

package com.zld.utils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.math.RandomUtils;

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
	
	public static Double formatDouble(Object value){
		if(Check.isDouble(value+"")){
			DecimalFormat df=new DecimalFormat("#.00"); 
			String dv = df.format(Double.valueOf(value+""));
			if(Check.isDouble(dv))
				return Double.valueOf(dv);
		}
		return 0.0d;
	}
	
	public static Long getLongMilliSecondFromStrDate(String strDate, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		long millSeconds = new GregorianCalendar().getTimeInMillis();
		try {
			millSeconds = sdf.parse(strDate).getTime();
		} catch (Exception e) {
			// logger.info("---------get seconds error:"+e.getMessage());
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
	
/*	*//**
	 * 生成 xml文件流
	 *//*
	public static String createXML(Map<String, String > info) {// 获取最大访客数XML
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		xml.append("<content>");
		for(String key : info.keySet()){
			xml.append("<"+key+">"+info.get(key)+"</"+key+">");
		}
		xml.append("</content>");
		return xml.toString();
	}
	*/
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
	public static String createXML(List<Map<String, Object >> info) {// 获取最大访客数XML
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		xml.append("<content>");
		if(info!=null&&info.size()>0){
			for(Map<String, Object > map : info){
				xml.append("<info>");
				for(String key : map.keySet()){
					xml.append("<"+key+">"+map.get(key)+"</"+key+">");
				}
				xml.append("</info>");
			}
		}else {
			xml.append("<info>");
			xml.append("没有数据");
			xml.append("</info>");
		}
		xml.append("</content>");
		return xml.toString();
	}
	/**
	 * 生成 xml文件流
	 */
	public static String createXML(List<Map<String, Object >> info,Long size) {// 获取最大访客数XML
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"gb2312\"?>");
		xml.append("<content count=\""+size+"\">");
		if(info!=null&&info.size()>0){
			for(Map<String, Object > map : info){
				xml.append("<info>");
				for(String key : map.keySet()){
					xml.append("<"+key+">"+map.get(key)+"</"+key+">");
				}
				xml.append("</info>");
			}
		}else {
			xml.append("<info>");
			xml.append("没有数据");
			xml.append("</info>");
		}
		xml.append("</content>");
		return xml.toString();
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
					Object v = map.get(key);
					if(v!=null)
						v = v.toString().trim();
//					if(v instanceof Long||v instanceof Integer)
//						json +="\""+key+"\":"+map.get(key);
//					else {
					json +="\""+key+"\":\""+v+"\"";
//					}
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
	
	public static String createJson2(List<Map<String, Object >> info){
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
					Object v = map.get(key);
					boolean startsWith = false;
					if(v!=null){
						v = v.toString().trim();
						startsWith = v.toString().startsWith("[");
					}
//					if(v instanceof Long||v instanceof Integer)
//						json +="\""+key+"\":"+map.get(key);
//					else {
					
					if(startsWith){
						json +="\""+key+"\":"+v+"";
					}else{
						json +="\""+key+"\":\""+v+"\"";
					}
//					}
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
	
	public static String getJson(List<Map<String, Object >> info){
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
					Object v = map.get(key);
					//System.out.println(v);
					if(v.toString().startsWith("["))
						json +="\""+key+"\":"+map.get(key);
					else {
						json +="\""+key+"\":\""+map.get(key)+"\"";
					}
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
				Object value = info.get(key);
				if(value!=null&&(value.toString().startsWith("[")||value.toString().startsWith("{")))
					json +="\""+key+"\":"+value;
				else {
					json +="\""+key+"\":\""+value+"\"";
				}	
				j++;
			}
			json+="}";
		}else {
			json="{}";
		}
		return json;
	}
	/**
	 * 计算停车费
	 * @param start
	 * @param end
	 * @param price
	 * @return
	 */
	public static String getAccount(Long start,Long end,Double price){
		if(start!=null&&end!=null){
			Long duration = getHour(start,end);
			return Math.round(Double.valueOf(price+"")*Double.valueOf(duration))+".00";
		}
		return "";
	}

	
	public static String getTimeString(Long start,Long end){
		Long hour = (end-start)/3600;
		Long minute = ((end-start)%3600)/60;
		if(hour==0&&minute==0)
			minute=1L;
		String result = "";
		int day = 0;
		if(hour==0)
			result =minute+"分钟";
		else 
			result =hour+"小时"+minute+"分钟";
		if(hour>24){
			day = hour.intValue()/24;
			hour = hour%24;
			result = day+"天 "+hour+"小时"+minute+"分钟";
		}
		//System.out.println(">>>>>>>>>>>>b:"+start+",e:"+end+",duration:"+result);
		return result;
	}
	
	public static String getDayString(Long start,Long end){
		Long hour = (end-start)/3600;
		String result = "";
		int day = 0;
		day = hour.intValue()/24;
		result = day+"";
		//System.out.println(">>>>>>>>>>>>b:"+start+",e:"+end+",duration:"+result);
		return result;
	}
	
	public static String getTimeString(Long duartion){
		Long hour = duartion/3600;
		Long minute = (duartion%3600)/60;
		String result = "";
		int day = 0;
		if(hour==0)
			result =minute+"分钟";
		else 
			result =hour+"小时"+minute+"分钟";
		if(hour>24){
			day = hour.intValue()/24;
			hour = hour%24;
			result = day+"天 "+hour+"小时"+minute+"分钟";
		}
		return result;
	}
	
	
	public static String objArry2String(Object[] values){
		StringBuffer rBuffer = new StringBuffer();
		if(values!=null&&values.length>0){
			for(Object o : values){
				rBuffer.append(o+",");
			}
		}
		return rBuffer.toString();
	}
	
	public static String [] list2Array(List<String> list){
		if(list!=null){
			String [] arrays = new String[list.size()];
			for(int i=0;i< list.size();i++)
				arrays[i]=list.get(i);
			return arrays;
		}
		return new String[0];
	}
	
	public static List<String> array2List(String [] arrays){
		if(arrays!=null){
			List<String> list = new ArrayList<String>();
			for(int i=0;i< arrays.length;i++)
				list.add(arrays[i]);
			return list;
		}
		return new ArrayList<String>();
	}
	
//	public static void main(String[] args) {
//		String s = null;
//		try {
//			s = MD5("laoyao11111140888993");
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		System.out.println(s);
//	}
	public static String getMondayOfThisWeek() {
		  Calendar c = Calendar.getInstance();
		  int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		  if (day_of_week == 0)
		   day_of_week = 7;
		  c.add(Calendar.DATE, -day_of_week + 1);
		  SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		  return df2.format(c.getTime()); 
	}
	 public static String getFistdayOfMonth() {
		 Date nowTime=new Date(System.currentTimeMillis());//取系统时间
		 try{       
			 SimpleDateFormat sformat=new SimpleDateFormat("yyyy-MM-01");
			 return sformat.format(nowTime);  
		 }catch(Exception   ex){    
			 ex.printStackTrace();       
		 }
		 return null;
	}
	 
	 public static String getLastFistdayOfMonth() {
		 Calendar c = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		 c.add(Calendar.MONTH, -1);
		 Date nowTime=new Date(c.getTimeInMillis());//取系统时间
		 try{       
			 SimpleDateFormat sformat=new SimpleDateFormat("yyyy-MM-01");
			 return sformat.format(nowTime);  
		 }catch(Exception   ex){    
			 ex.printStackTrace();       
		 }
		 return null;
	}
	 
	 /**
	  * 计算两地的距离，返回单位：公里
	  * @param _Longitude1
	  * @param _Latidute1
	  * @param _Longitude2
	  * @param _Latidute2
	  * @return
	  */
	 public static double distanceByLnglat(double _Longitude1, double _Latidute1,
				double _Longitude2, double _Latidute2) {
	    	//0.09446
			double radLat1 = _Latidute1 * Math.PI / 180;
			double radLat2 = _Latidute2 * Math.PI / 180;
			double a = radLat1 - radLat2;
			double b = _Longitude1 * Math.PI / 180 - _Longitude2 * Math.PI / 180;
			double s = 2 * Math.atan(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
					+ Math.cos(radLat1) * Math.cos(radLat2)
					* Math.pow(Math.sin(b / 2), 2)));
			s = s * 6378137.0;// 取WGS84标准参考椭球中的地球长半径(单位:m)
			s = Math.round(s * 10000) / 10000;
			s = (s / 1000) * 0.621371192;
			//int result = (int) Math.ceil(s);
//			 System.out.println(_Longitude1+","+_Latidute1+","+_Longitude2+","+_Latidute2);
//			 System.out.println(s);
			return s;
	}
	 /**
		 * 计算地球上任意两点(经纬度)距离		 * 
		 * @param long1		 *            第一点经度
		 * @param lat1		 *            第一点纬度
		 * @param long2		 *            第二点经度
		 * @param lat2		 *            第二点纬度
		 * @return 返回距离 单位：米
		 */
		public static double distance(double long1, double lat1, double long2,
				double lat2) {
			double a, b, R;
			R = 6378137; // 地球半径
			lat1 = lat1 * Math.PI / 180.0;
			lat2 = lat2 * Math.PI / 180.0;
			a = lat1 - lat2;
			b = (long1 - long2) * Math.PI / 180.0;
			double d;
			double sa2, sb2;
			sa2 = Math.sin(a / 2.0);
			sb2 = Math.sin(b / 2.0);
			d = 2* R* Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1)* Math.cos(lat2) * sb2 * sb2));
			return d;
		}
	 
	
	/**
	 * 生成MD5
	 */
	public static String MD5(String s) throws Exception {
		MessageDigest messagedigest = MessageDigest.getInstance("MD5");
		messagedigest.reset();
		byte abyte0[] = messagedigest.digest(s.getBytes());
		return byteToString(abyte0);
	}
	private static String byteToString(byte abyte0[]) {
		int i = abyte0.length;
		char ac[] = new char[i * 2];
		int j = 0;
		for (int k = 0; k < i; k++) {
			byte byte0 = abyte0[k];
			ac[j++] = hexDigits[byte0 >>> 4 & 0xf];
			ac[j++] = hexDigits[byte0 & 0xf];
		}

		return new String(ac);
	}
	private static final char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f' };
	
	
	public static String encodingFileName(String fileName) {
		String returnFileName = "";
		try {
			returnFileName = new String(fileName.getBytes("gb2312"), "ISO8859-1");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return returnFileName;
	}
	
	public static String replaceEnter(String str){
        Pattern CRLF = Pattern.compile("(\r\n|\r|\n|\n\r)");  
        Matcher m = CRLF.matcher(str);  
        if (m.find()) {  
        	str = m.replaceAll("<br>");  
        }  
        return str;  
    }
	
	/**
	 * 验证车牌号:注意将小写转为大写再做匹配
	 * 
	 * @param plate
	 * @return
	 */
	public static boolean checkPlate(String plate) {
		if (plate==null||plate.equals("")) {
			return false;
		}
		String[] province = new String[] { "京", "沪", "浙", "苏", "粤", "鲁",
				"晋", "冀", "豫", "川", "渝", "辽", "吉", "黑", "皖", "鄂", "湘", "赣",
				"闽", "陕", "甘", "宁", "蒙", "津", "贵", "云", "桂", "琼", "青", "新",
				"藏", "港", "澳", "使", "军", "空", "海", "北", "沈", "兰","济", "南", "广", "成", "WJ", "警", "消", "边","水", "电", "林", "通" };
		Pattern p = Pattern.compile("^[\\u4e00-\\u9fa5]{1}[A-Z_0-9]{5}[A-Z_0-9_\\u4e00-\\u9fa5]$|^WJ\\d{7}$");
		Matcher m = p.matcher(plate);
		return m.matches();
	}
	
	static double generateRandomNumber() {
		// generate random number based on normal distribution
		double r = new Random().nextDouble();
		return new Random().nextDouble()*r;
	}

	public static Double[] processBonus(Double totalMoney, Integer personNum) {
		if(totalMoney==null||totalMoney==0||personNum==null||personNum==0)
			return null;
		Double[] results = new Double[personNum];
		for (int i = 0; i < personNum; i++) {
			results[i] = generateRandomNumber();
		}
		for(double s : results){
			System.err.println(s);
		}
		double ratio = totalMoney / sum(results);
		for (int i = 0; i < personNum; i++) {
			results[i] = StringUtils.formatDouble(results[i] * ratio);
			if(results[i]==0)
				results[i]=0.01d;
		}
		return results;
	}

	static Double sum(Double[] results) {
		double sum = 0d;
		for (double d : results) {
			sum += d;
		}
		return sum;
	}
	public static List<Integer> getBonusIngteger(Integer total,int bum,int max){
		//8,3  18,8 
		//拿到四个随机数，可以做个池什么的每次取四个来提升效率
		Double[] results = new Double[bum];
		for (int i = 0; i < bum; i++) {
			results[i] = generateRandomNumber();
		}
//		//排序
//		r.sort(new Comparator<Double>() {
//			@Override
//			public int compare(Double o1, Double o2) {
//				return o1 < o2 ? -1 : 1;
//			}
//		});
		//用这四个随机数来打断一个数，来取得五份分解之后的数
		List<Integer> out = new ArrayList<Integer>();
		double ratio = total / sum(results);
		int _total = 0;
		for (int i = 0; i < bum; i++) {
			int c = (int) (results[i] * ratio);
			if(c==0)
				c=1;
			_total +=c;
			out.add(c);
		}
		if(_total<total){
			out.add(bum-1,out.get(bum-1)+(total-_total));
			out.remove(bum);
		}else if(_total>total){
			System.out.println(out);
			for(int i =0;i<out.size();i++){
				if(out.get(i)>(_total-total+1)){
					Integer old = out.get(i);
					out.remove(i);
					out.add(i,old-(_total-total));
					break;
				}
			}
		}
		
		Integer lastTotal=0;
		for(int i =0;i<out.size();i++){
			Integer in = out.get(i);
			if(in>max){
				out.remove(i);
				out.add(i,max);
				lastTotal += in-max;
			}
		}
		//System.out.println(out);
		//System.out.println(lastTotal);
		Integer stotal = 0;
		if(lastTotal>0){
			for(int i =0;i<out.size();i++){
				int old = out.get(i);
				if(old<max&&lastTotal>0){
					out.remove(i);
					if(lastTotal>1){
						if((old+lastTotal)<max){
							out.add(i,lastTotal+old);
							lastTotal=0;
						}else {
							out.add(i,max);
							lastTotal=lastTotal-(max-old);
						}
					}else {
						out.add(i,old+1);
						lastTotal=lastTotal-1;
					}
				}
				if(lastTotal==0)
					break;
			}
		}
		for(Integer integer : out){
			stotal +=integer;
		}
		Collections.sort(out);
		System.out.println(out+":"+stotal);
		return out;
	}
	 public static void main(String[] args) {
		 getBonusIngteger(100,25,12);
//			System.out.println(distance(116.306970,40.042474,116.316416,40.042474));
			//double d1 = 0.002346;
			//double d2 = 0.001792;
//			double d1 = 0.009446*2*1.243;//0.023482756
//			double d2 = 0.007232*2*1.243;//0.017978752
//			System.out.println(distanceByLnglat(116.316416,40.042474,116.325862,40.042474));
			//0.007232
//			System.out.println(">>>"+distance(116.313572,40.041845,116.627951,39.933272));
			//double lon = 116.306970;
			//double lat = 40.042474;
////			
			//System.out.println(distance(lon,lat,lon+d1,lat));
			//System.out.println(distance(lon,lat,lon,lat+d2));
//			
//			lon = lon+1;
//			lat = lat+10;
			
//			System.out.println(distanceByLnglat(lon,lat,lon,lat+0.02892366));
////			                        
//			System.out.println(distanceByLnglat(lon,lat,lon+0.03777455,lat));
//		 System.out.println(formatDouble("0.06399"));
		/*try {
			String pass = "guilin0316";
			 pass =StringUtils.MD5(pass);
			 pass = StringUtils.MD5(pass +"zldtingchebao201410092009");
			 System.out.println(pass);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		}
	 
	 public static String getWeek(int week){
			switch (week) {
			case 2:
				return "一";
			case 3:
				return "二";
			case 4:
				return "三";
			case 5:
				return "四";
			case 6:
				return "五";
			case 7:
				return "六";
			case 1:
				return "日";
			}
			return "";
	}
	 
	 public static String [] getGRCode(Long []ids){
	    String[] ss = new String[ids.length]; 
        for(int i=0;i<ids.length;i++){ 
        	String vInteger = ids[i]+"";
        	if(vInteger.length() < 6){
        		for(int k=0;k<6 - vInteger.length();k++){
        			vInteger = "0" + vInteger;
        		}
        	}
        	String c = UUID.randomUUID().toString();
        	
            c = c.substring(c.lastIndexOf("-")+1); 
            StringBuffer nc = new StringBuffer();
        	Integer charIndex=RandomUtils.nextInt(2);
        	String stuf = "zd";
        	c = stuf.charAt(charIndex)+c;
        	System.out.println(c);
        	for(int j=0;j<c.length();j++){
        		Character chara = c.charAt(j);
        		if(j>1&&j<9)
        			chara = chara.toUpperCase(chara);
        		nc.append(chara);
        		if(j==1)
        			nc.append(vInteger.charAt(0));
        		else if(j==3){
        			nc.append(vInteger.charAt(1));
        		}else if(j==5){
        			nc.append(vInteger.charAt(2));
        		}else if(j==6){
        			nc.append(vInteger.charAt(3));
        		}else if(j==9){
        			nc.append(vInteger.charAt(4));
        		}else if(j==11){
        			nc.append(vInteger.charAt(5));
        		}
        	}
        	String result = nc.toString();
        	//System.out.println(result.charAt(2)+""+result.charAt(5)+result.charAt(8)+""+result.charAt(10)+""+result.charAt(14)+""+result.charAt(17));
        	ss[i]=nc.toString();
        } 
	    return ss; 
	 }

	 public static String getParkUserPass(){
		 String []passes = new String[]{"333666","999666","111222","333444","555666","454545","858585","989898","777333","222444","999111","000222","555000","525252","676767","919191","020202","353535","646464","828282","111444","666555","222555","666333","333777","999888","888555","666444","111999","222555","000222","135135","124124","258258","147147","369369","963963","321321","654654","987987","120120","320320","210210","258258","595959","535353","575757","545454","151515","525252","626262","202020","555222","626262","303030","989898","969696","939393","929292","949494","979797","848484","828282","838383","868686"};
		 int rang = new Random().nextInt(passes.length);
		 return passes[rang];
	 }
}

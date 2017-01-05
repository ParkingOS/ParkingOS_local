package com.zld;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.ibatis.common.resources.Resources;
import com.sun.corba.se.spi.orbutil.fsm.State;
import com.sun.org.apache.bcel.internal.generic.NEW;

/**
 * 读取配置文件
 * @author Administrator
 *
 */
public class CustomDefind {

	
	Logger logger = Logger.getLogger(CustomDefind.class);
	//private static String PATH = ;
	
	public static String CUSTOMPARKIDS = getValue("CUSTOMPARKIDS");
	public static String ISLOTTERY = getValue("ISLOTTERY");
	public static String MONGOADDRESS = getValue("MONGOADDRESS");
	public static String SENDTICKET = getValue("SENDTICKET");
	public static String PARKBACK = getValue("PARKBACK");
	public static String COMID = getValue("COMID");
	public static String TOMCAT = getValue("TOMCAT");
	public static String PIC = getValue("PIC");
	public static String TOMCATHOMT = getValue("TOMCATHOMT");
	public static String SECRET = getValue("SECRET");
	public static String SYNCTO = getValue("SYNCTO");
	public static String SYNCFROM = getValue("SYNCFROM");
	public static String AUTO = getValue("AUTO");
	public static String DOMAIN = getValue("DOMAIN");
	public static Map<String, Long> CARLIENCE  =new HashMap<String, Long>();
	public static String getValue(String key){
		String fileName ="config.properties";
		//System.out.println(">>>00>>>>config file path:"+fileName);
		Properties properties = new Properties();
		try {
			File file = Resources.getResourceAsFile(fileName);
			properties.load(new FileInputStream(file));
			return properties.getProperty(key);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "0";
	}
	
	public static synchronized void setCarMap(String key,Long time){
		CARLIENCE.put(key, time);
	}
	public static synchronized Long getCarMap(String key){
		return CARLIENCE.get(key);
	}
	public static synchronized void clearCarMap (){
		 Map<String, Long> tempMap = new HashMap<String, Long>();
		 Long ntime = System.currentTimeMillis()/1000;
		 for(String key : CARLIENCE.keySet()){
			 Long t = CARLIENCE.get(key);
			 if(ntime-t<3)
				 tempMap.put(key, t);
		 }
		 CARLIENCE = tempMap;
	}
	
	public void reSetConfig() {
		CUSTOMPARKIDS = getValue("CUSTOMPARKIDS");
		ISLOTTERY = getValue("ISLOTTERY");
	}
	/**
	 * 刷新properties属性的值
	 * @param name 属性名
	 */
	public static void reSet() {
		COMID = getValue("COMID");
		SECRET = getValue("SECRET");
	}
	//停车费，停车券最高抵扣金额
	/**
	 * @param totle
	 * @param type 0 根据金额查券，1根据券查使用金额
	 * @return
	 */
	public static Integer getUseMoney(Double totle,Integer type){
		//Map<Integer, Integer> totalTicketMap = new HashMap<Integer, Integer>();
		Double dfeeTop = Math.ceil(totle);
		Integer feeTop = dfeeTop.intValue();
		//普通券  X：车费金额满 (total) Y：可用券抵扣金额 (common_distotal) 算法：X=Y+2+Y/3 上限是uplimit
		//Double common_distotal = Math.ceil((feeTop - 2)*(3.0/4.0));//向上取整
		Double common_distotal = Math.floor((feeTop - 1)/3.0);//向上取整
		//Double common_distotal = Math.floor((feeTop - 1)/2.0);//向上取整
		if(common_distotal<0)
			return 0;
//		if(common_distotal>12)
//			return 12;
		if(type==0)
			return common_distotal.intValue();
		else {
			return Double.valueOf(Math.floor(3*totle+1)).intValue();
			//return Double.valueOf(Math.floor(totle+1+totle/1.0)).intValue();
			//return Double.valueOf(Math.floor(totle+2+totle/3.0)).intValue();
		}
		/*totalTicketMap.put(3, 1);
		totalTicketMap.put(4, 2);
		totalTicketMap.put(5, 3);
		totalTicketMap.put(6, 3);
		totalTicketMap.put(7, 4);
		totalTicketMap.put(8, 5);
		totalTicketMap.put(9, 6);
		totalTicketMap.put(10, 6);
		totalTicketMap.put(11, 7);
		totalTicketMap.put(12, 8);
		totalTicketMap.put(13, 9);
		totalTicketMap.put(14, 9);
		totalTicketMap.put(15, 10);
		totalTicketMap.put(16, 11);
		totalTicketMap.put(17, 12);
		totalTicketMap.put(18, 12);
		Integer limit =0;
		if(type==0){
			if(feeTop<3)
				return 0;
			if(totle>18)
				return feeTop-1;
			limit = totalTicketMap.get(feeTop);
		}else {
			if(feeTop>12)
				return 18;
			if(feeTop==11)
				limit=17;
			else if(feeTop==8)
				limit=13;
			else if(feeTop==5)
				limit=9;
			else if(feeTop==2){
				limit=5;
			}else {
				for(Integer key : totalTicketMap.keySet()){
					if(feeTop==totalTicketMap.get(key)){
						limit=key;
						break;
					}
				}
			}
		}
		return limit;*/
	}

	public static void main(String[] args) {
		/*totalTicketMap.put(3, 1);
		totalTicketMap.put(4, 2);
		totalTicketMap.put(5, 2);
		totalTicketMap.put(6, 3);
		totalTicketMap.put(7, 4);
		totalTicketMap.put(8, 5);
		totalTicketMap.put(9, 5);
		totalTicketMap.put(10, 6);
		totalTicketMap.put(11, 7);
		totalTicketMap.put(12, 8);
		totalTicketMap.put(13, 8);
		totalTicketMap.put(14, 9);
		totalTicketMap.put(15, 10);
		totalTicketMap.put(16, 11);
		totalTicketMap.put(17, 11);
		totalTicketMap.put(18, 12);*/
		/*System.err.println("37:"+getUseMoney(37.0,0));
		System.err.println("36:"+getUseMoney(36.0,0));
		System.err.println("35:"+getUseMoney(35.0,0));
		System.err.println("34:"+getUseMoney(34.0,0));
		System.err.println("33:"+getUseMoney(33.0,0));
		System.err.println("32:"+getUseMoney(32.0,0));
		System.err.println("31:"+getUseMoney(31.0,0));
		System.err.println("30:"+getUseMoney(30.0,0));
		System.err.println("29:"+getUseMoney(29.0,0));
		System.err.println("28:"+getUseMoney(28.0,0));
		System.err.println("27:"+getUseMoney(27.0,0));
		System.err.println("26:"+getUseMoney(26.0,0));
		System.err.println("25:"+getUseMoney(25.0,0));
		System.err.println("24:"+getUseMoney(24.0,0));
		System.err.println("23:"+getUseMoney(23.0,0));
		System.err.println("22:"+getUseMoney(22.0,0));
		System.err.println("21:"+getUseMoney(21.0,0));
		System.err.println("20:"+getUseMoney(20.0,0));
		System.err.println("19:"+getUseMoney(19.0,0));
		System.err.println("18:"+getUseMoney(18.0,0));
		System.err.println("17:"+getUseMoney(17.0,0));
		System.err.println("16:"+getUseMoney(16.0,0));
		System.err.println("15:"+getUseMoney(15.0,0));
		System.err.println("14:"+getUseMoney(14.0,0));
		System.err.println("13:"+getUseMoney(13.0,0));
		System.err.println("12:"+getUseMoney(12.0,0));
		System.err.println("11:"+getUseMoney(11.0,0));
		System.err.println("10:"+getUseMoney(10.0,0));
		System.err.println("9:"+getUseMoney(9.0,0));
		System.err.println("8:"+getUseMoney(8.0,0));
		System.err.println("7:"+getUseMoney(7.0,0));
		System.err.println("6:"+getUseMoney(6.0,0));
		System.err.println("5:"+getUseMoney(5.0,0));
		System.err.println("4:"+getUseMoney(4.0,0));
		System.err.println("3:"+getUseMoney(3.0,0));
		System.err.println("2:"+getUseMoney(2.0,0));
		System.err.println("1:"+getUseMoney(1.0,0));
		
		System.err.println("25:"+getUseMoney(25.0,1));
		System.err.println("24:"+getUseMoney(24.0,1));
		System.err.println("23:"+getUseMoney(23.0,1));
		System.err.println("22:"+getUseMoney(22.0,1));
		System.err.println("21:"+getUseMoney(21.0,1));
		System.err.println("20:"+getUseMoney(20.0,1));
		System.err.println("19:"+getUseMoney(19.0,1));
		System.err.println("18:"+getUseMoney(18.0,1));
		System.err.println("17:"+getUseMoney(17.0,1));
		System.err.println("16:"+getUseMoney(16.0,1));
		System.err.println("15:"+getUseMoney(15.0,1));
		System.err.println("14:"+getUseMoney(14.0,1));
		System.err.println("13:"+getUseMoney(13.0,1));
		System.err.println("12:"+getUseMoney(12.0,1));
		System.err.println("11:"+getUseMoney(11.0,1));
		System.err.println("10:"+getUseMoney(10.0,1));
		System.err.println("9:"+getUseMoney(9.0,1));
		System.err.println("8:"+getUseMoney(8.0,1));
		System.err.println("7:"+getUseMoney(7.0,1));
		System.err.println("6:"+getUseMoney(6.0,1));
		System.err.println("5:"+getUseMoney(5.0,1));
		System.err.println("4:"+getUseMoney(4.0,1));
		System.err.println("3:"+getUseMoney(3.0,1));
		System.err.println("2:"+getUseMoney(2.0,1));
		System.err.println("1:"+getUseMoney(1.0,1));*/
	}
	
	
}

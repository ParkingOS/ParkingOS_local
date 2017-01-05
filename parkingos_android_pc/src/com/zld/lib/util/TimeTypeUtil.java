package com.zld.lib.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

@SuppressLint("SimpleDateFormat")
public class TimeTypeUtil {

	// 计算出两个时间差
	public static String processTwo(long startMil, long endMil) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(startMil);
		Calendar c2 = Calendar.getInstance();
		c2.setTimeInMillis(endMil);
		StringBuilder time = new StringBuilder();
		int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		if (year != 0) {
			time.append(year).append("年");
		}
		int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		if (month != 0) {
			time.append(month).append("月");
		}
		int day = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH);
		if (day != 0) {
			time.append(day).append("日");
		}
		int hour = c2.get(Calendar.HOUR_OF_DAY) - c1.get(Calendar.HOUR_OF_DAY);
		time.append(hour).append("小时");
		int min = c2.get(Calendar.MINUTE) - c1.get(Calendar.MINUTE);
		time.append(min).append("分");
		int sec = c2.get(Calendar.SECOND) - c1.get(Calendar.SECOND);
		time.append(sec).append("秒");
		return time.toString();
	}

	// 传入一个时间毫秒值--计算出传入时间和当前时间相差的时间；
	public static String process(long startMil) {
		Calendar c1 = Calendar.getInstance();
		c1.setTimeInMillis(startMil);
		Calendar c2 = Calendar.getInstance();
		StringBuilder time = new StringBuilder();
		int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		if (year != 0) {
			time.append(year).append("年");
		}
		int month = c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH);
		if (month != 0) {
			time.append(month).append("月");
		}
		int day = c2.get(Calendar.DAY_OF_MONTH) - c1.get(Calendar.DAY_OF_MONTH);
		if (day != 0) {
			time.append(day).append("日");
		}
		int hour = c2.get(Calendar.HOUR_OF_DAY) - c1.get(Calendar.HOUR_OF_DAY);
		time.append(hour).append("小时");
		int min = c2.get(Calendar.MINUTE) - c1.get(Calendar.MINUTE);
		time.append(min).append("分");
		int sec = c2.get(Calendar.SECOND) - c1.get(Calendar.SECOND);
		time.append(sec).append("秒");
		return time.toString();
	}

	public static String getTimeString(Long start,Long end){
		Long date = (end - start)/(3600*24);
		//		Log.e("TimeTypeUtil", "停车的天数为："+ date);
		Long hour = ((end-start)%86400)/3600;
		Long minute = ((end-start)%3600)/60;
		String result = "";
		if (date == 0) {
			if(hour==0)
				if(minute == 0){
					result = 1+"分钟";
				}else{
					result = minute+"分钟";
				}
			else 
				result =hour+"小时"+minute+"分钟";
		}else{
			result = date+"天"+hour+"小时"+minute+"分钟";
		}
		return result;
	}
	public static String getTime(Long start){
		//Date date = new Date(System.currentTimeMillis());
		Long now =System.currentTimeMillis()/1000; 
		Long date = (now - start)/(3600*24);
		//		Log.e("TimeTypeUtil", "停车的天数为："+ date);
		Long hour = ((now-start)%86400)/3600;
		Long minute = ((now-start)%3600)/60;
		String result = "";
		if (date == 0) {
			if(hour==0)
				result =minute+"分钟";
			else 
				result =hour+"小时"+minute+"分钟";
		}else{
			result =date+"天"+hour+"小时"+minute+"分钟";
		}
		return result;
	}

	@SuppressLint("SimpleDateFormat")
	public static String getStringTime(Long time){

		//		SimpleDateFormat dateaf = new SimpleDateFormat("yyyy.MM.dd"); 
		//		SimpleDateFormat timef = new SimpleDateFormat("HH:mm"); 
		SimpleDateFormat dateaf = new SimpleDateFormat("MM-dd"); 
		SimpleDateFormat timef = new SimpleDateFormat("HH:mm"); 
		String date = dateaf.format(time);
		String times = timef.format(time);
		String result = date+" "+times;
		return result;
	}

	public static String getEasyStringTime(Long time){
		SimpleDateFormat timef = new SimpleDateFormat("HH:mm"); 
		String times = timef.format(time);
		String result = ""+times;
		return result;
	}

	public static Long getLongTime(String user_time) { 
		String re_time = null; 
		long parseLong = 0L;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		Date d; 
		try { 
			d = sdf.parse(user_time); 
			long l = d.getTime(); 
			re_time = String.valueOf(l); 
		} catch (ParseException e) { 
			// TODO Auto-generated catch block 
			e.printStackTrace(); 
		} 
		if(re_time != null){
			parseLong = Long.parseLong(re_time);
		}
		return parseLong; 
	} 

	public static String getTodayDate(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String str=sdf.format(date);
		return str;
	}

	public static String getTodayTime(Date date){
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm");
		String str=sdf.format(date);
		return str;
	}

	public static String getComplexStringTime(Long time){
		SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
		String times = timef.format(time);
		String result = ""+times;
		return result;
	}

	public static String getFutureDate(int days, String format){
		SimpleDateFormat formatDate = new SimpleDateFormat(format);  //字符串转换
		Calendar c = Calendar.getInstance();  
		//new Date().getTime();这个是获得当前电脑的时间，你也可以换成一个随意的时间
		c.setTimeInMillis(new Date().getTime());
		c.add(Calendar.DATE, days);//天后的日期
		Date date= new Date(c.getTimeInMillis()); //将c转换成Date
		String date1 =  formatDate.format(date);
		if ("0".equals(date1.substring(0,1))){
			return date1.substring(1);
		}
		return date1;
	}

	public static boolean compareDate(String begins){
		boolean databoolean = false;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		try {
			long begin = df.parse(begins).getTime();
			long nowtime = new Date().getTime();
			int dateday = (int)(nowtime - begin) / (1000 * 60 * 60 * 24);
			if(dateday>14||dateday==14){
				databoolean = true;
			}
		} catch (Exception e) {
			Log.i("Exception", e.getMessage()+"");
			e.printStackTrace();
		}
		return databoolean;
	}

	public static boolean isMthUserExpire(String exptime) {
		// TODO Auto-generated method stub
		if (exptime != null){
			int iExptime = Integer.parseInt(exptime);
			if (iExptime <= 5){
				return true;
			}
		}
		return false;
	}

	/**
	 * 计算传入时间与本地时间差值；
	 * 
	 * @param time
	 * @return
	 */
	public static Long getDifferenceTime(Long time) {
		Long now = System.currentTimeMillis();
		return time - now;
	}

	/**
	 * 判断断网时间是否超过五分钟
	 * @param context
	 * @return
	 */
	public static Boolean isOffFiveMinutes(Context context){
		Long now = System.currentTimeMillis();
		com.zld.lib.util.SharedPreferencesUtils.setParam(
				context.getApplicationContext(), "zld_config", "netoff", System.currentTimeMillis());
		Long netOffTime = SharedPreferencesUtils.getParam(
				context.getApplicationContext(), "zld_config", "netoff", 0L);
		if ((now - netOffTime) >1000*60*5) {
			return true;
		}
		return false;
	}
	public static String getNowTime(){
		SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
//		String times = timef.format(System.currentTimeMillis());
		Date date = new Date();
		String result = timef.format(date);
		return result;
	}
	public static String getNowTimeMIN(){
		SimpleDateFormat timef = new SimpleDateFormat("yyyy-MM-dd HH:mm"); 
//		String times = timef.format(System.currentTimeMillis());
		Date date = new Date();
		String result = timef.format(date);
		return result;
	}
}

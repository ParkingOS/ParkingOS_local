package com.mserver.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

public class TimeTools {

	private static Logger logger = Logger.getLogger(TimeTools.class);
	
	private static GregorianCalendar gCalendar = null;

	private static SimpleDateFormat dateFormat = null;

	public static Date getDateFromString(String str, String pattern) throws ParseException {
		return new SimpleDateFormat(pattern).parse(str);
	}

	// 时间格式数组
	private static String[] formatArray = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm",
			"yyyy-MM-dd HH:mm:ss", "yy-MM-dd HH:mm", "yyyyMMdd HH:mm", "yyyy-MM-dd HH" };

	// 检测一个时间格式是否为合法格式
	private static boolean isRightFormat(String formatStr) {
		boolean isRight = false;
		int j = formatArray.length;
		for (int i = 0; i < j; i++) {
			if (formatArray[i].equalsIgnoreCase(formatStr)) {
				isRight = true;
				break;
			}
		}
		return isRight;
	}

	public static Long getLongMilliSecondFromStrDate(String strDate, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);

		long millSeconds = new GregorianCalendar().getTimeInMillis();
		try {
			millSeconds = sdf.parse(strDate).getTime();
		} catch (Exception e) {
			// logger.error("---------get seconds error:"+e.getMessage());
		}
		return new Long(millSeconds);
	}

	/**
	 * @param strDate
	 * @return 根据字符串时间得到相应毫秒数
	 */
	public static Long getLongMilliSecondFrom_HHMMDD(String strDate) {
		return getLongMilliSecondFromStrDate(strDate, "yyyy-MM-dd");
	}

	/**
	 * @param strDate
	 * @return 根据字符串时间得到相应秒数
	 */
	public static Long getLongMilliSecondFrom_HHMMDDHHmmss(String strDate) {
		return getLongMilliSecondFromStrDate(strDate, "yyyy-MM-dd HH:mm:ss") / 1000;
	}

	public static String checkMounth(String endDateSelect) {
		String year = endDateSelect.split("-")[0];
		String mounth = endDateSelect.split("-")[1];
		if (mounth.equals("1") || mounth.equals("3") || mounth.equals("5") || mounth.equals("7")
				|| mounth.equals("8") || mounth.equals("10") || mounth.equals("12"))
			return "-31";
		else if (mounth.equals("4") || mounth.equals("6") || mounth.equals("9")
				|| mounth.equals("11"))
			return "-30";
		else {
			Integer yInteger = Integer.parseInt(year);
			// if(year.equals("2012")||year.equals("2016")||year.equals("2020")||
			// year.equals("2024")||year.equals("2028")||year.equals("2008"))
			if ((yInteger % 4 == 0 && yInteger % 100 != 0) || yInteger % 400 == 0)// 润年判断
				return "-29";
			else
				return "-28";
		}

	}

	/**
	 * @return 得到当前时间的秒数(long型)
	 */
	public static long getlongMilliSeconds() {
		return new java.util.Date().getTime();
	}

	/**
	 * @return 得到当前时间的毫秒数(Long型)
	 */
	public static Long getLongMilliSeconds() {
		long d = new java.util.Date().getTime();
		return new Long(d / 1000);
	}

	public static Long getLongSeconds() {
		long d = new java.util.Date().getTime();
		return new Long(d);
	}

	/**
	 * @param milliSeconds
	 *            毫秒数
	 * @return 格式化后的时间字符串 yyyy-MM-dd HH:mm:ss
	 */
	public static String getTime_yyyyMMdd_HHmmss(Long milliSeconds) {

		return secondsToDateStr(milliSeconds, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * @param milliSeconds
	 *            毫秒数
	 * @return 格式化后的时间字符串 yyyy-MM-dd HH:mm:ss
	 */
	public static String getTime_yyyyMMdd_HHmm(Long milliSeconds) {

		return secondsToDateStr(milliSeconds, "yyyy-MM-dd HH:mm");
	}

	/**
	 * @param milliSeconds
	 *            毫秒数
	 * @return 格式化后的时间字符串 yy-MM-dd HH:mm
	 */
	public static String getTime_yyMMdd_HHmm(Long milliSeconds) {

		return secondsToDateStr(milliSeconds, "yy-MM-dd HH:mm");
	}
	
	public static String getTime_yyyyMMdd_HH(Long milliSeconds) {
		return secondsToDateStr(milliSeconds, "yyyy-MM-dd HH");
	}
	
	/**
	 * @param milliSeconds
	 *            毫秒数
	 * @return 格式化后的时间字符串 yyyy-MM-dd
	 */
	public static String getTimeStr_yyyy_MM_dd(Long milliSeconds) {

		return secondsToDateStr(milliSeconds, "yyyy-MM-dd");
	}

	/**
	 * @return 当前日期的字符串 yyyy-MM-dd 格式
	 */
	public static String getDate_YY_MM_DD() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		return dateFormat.format(new java.util.Date());

	}

	/**
	 * @return 当前日期的字符串 yyyy/M/d 格式
	 */
	public static String getDate_YY_M_D() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/M/d");
		return dateFormat.format(new java.util.Date());

	}

	public static Date str2Date(String date) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		Date ddate = new Date();
		try {
			ddate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return ddate;
	}

	/**
	 * @todo 将数值时间格式化为字符串
	 * @param milliSeconds
	 * @param formatStr
	 * @return
	 */
	public static String secondsToDateStr(Long milliSeconds, String formatStr) {

		if (milliSeconds == null)
			return "";
		if (isRightFormat(formatStr) == false) {
			formatStr = "yyyy-MM-dd HH:mm:ss";
		}
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(formatStr);

			if (milliSeconds.longValue() > 1) {
				GregorianCalendar gCalendar = new GregorianCalendar();
				gCalendar.setTimeInMillis(milliSeconds.longValue());
				return dateFormat.format(gCalendar.getTime());
			} else {
				return "";
			}
		} catch (Exception e) {
			return "";
		}

	}
 
	// 得到明天时间
	public static String getTomorrowday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +1);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getTwoLaterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +2);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getThirdLaterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +3);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getForthLaterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +4);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getFiveLaterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +5);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getSixLaterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +6);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getSevenLaterday() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +7);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		return strStart;
	}

	public static String getCoutomday(int days) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +days);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 10);//
		return strStart;
	}

	public static Long getStrDateToSecond(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long millSeconds = (new GregorianCalendar()).getTimeInMillis();
		try {
			millSeconds = sdf.parse(strDate).getTime();
		} catch (Exception e) {

		}
		return new Long(millSeconds / 1000);
	}

	public static Long getStrDateToSecond2(String strDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		long millSeconds = (new GregorianCalendar()).getTimeInMillis();
		try {
			millSeconds = sdf.parse(strDate).getTime();
		} catch (Exception e) {

		}
		return new Long(millSeconds);
	}

	// 转换
	public static String secondsToDateStr(Long seconds) {

		String second = "1";
		if (seconds.equals("") && seconds == null) {
			seconds = new Long(1);
		}
		try {

			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			gCalendar = new GregorianCalendar();
			gCalendar.setTimeInMillis(seconds.longValue() * 1000);
			second = dateFormat.format(gCalendar.getTime());
		} catch (Exception e) {
			second = "1";

		}
		return second;
	}

	public static String MillsecondsToDateStr(Long seconds) {

		String second = "1";
		if (seconds.equals("") && seconds == null) {
			seconds = new Long(1);
		}
		try {

			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			gCalendar = new GregorianCalendar();
			gCalendar.setTimeInMillis(seconds.longValue());
			second = dateFormat.format(gCalendar.getTime());
		} catch (Exception e) {
			second = "1";

		}
		return second;
	}

	public static Long getDatestart() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -2);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		Long State = getLongMilliSecondFrom_HHMMDD(strStart);
		return State;
	}

	public static Long getDateend() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, +2);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String mDateTime = formatter.format(c.getTime());
		String strStart = mDateTime.substring(0, 19);//
		Long State = getLongMilliSecondFrom_HHMMDD(strStart);
		return State;
	}

	public static Long getToDayBeginTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(new Date());
		today = today.substring(0, 10) + " 00:00:00";
		return getStrDateToSecond(today);
	}

	/**
	 * 计算时长
	 * 
	 * @param seconds
	 *            （秒）
	 * @return HH:mm:ss exp："00:00:00"
	 */
	public static String getShiChangString(Long seconds) {
		StringBuffer shichang = null;
		if (seconds != null) {
			if (seconds > 0) {
				shichang = new StringBuffer("");
				int hour = (int) (seconds / 3600);
				if (hour > 0) {
					if (hour > 9)
						shichang.append(hour + ":");
					else
						shichang.append("0" + hour + ":");
				} else {
					shichang.append("00:");
				}
				int minute = (int) (seconds % 3600) / 60;
				if (minute > 0) {
					if (minute > 9)
						shichang.append(minute + ":");
					else
						shichang.append("0" + minute + ":");
				} else {
					shichang.append("00:");
				}
				int second = (int) (seconds % 3600) % 60;
				if (second > 0) {
					if (second > 9)
						shichang.append(second);
					else
						shichang.append("0" + second);
				} else {
					shichang.append("00");
				}
			} else {
				return "00:00:00";
			}
		} else {
			return "";
		}
		return shichang.toString();
	}

	/*
	 * 返回录音下载的时间格式：2013-03-13_145012
	 */
	public static String getRecordTime(long time) {
		String datestr = secondsToDateStr(time);
		datestr = datestr.replace(" ", "_").replaceAll(":", "");
		return datestr;
	}

	private static String dtime = "";

	public static String getdate1()// 获得精确到日的当前日期
	{
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(gCalendar.getTime());
		return dtime;
	}

	public static String gettime1()// 获得精确到秒的当前日期
	{
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(gCalendar.getTime());
		return dtime;
	}

	public static long getlongtime()// 获得当前时间的毫秒数
	{
		java.util.Date nows = new java.util.Date();
		long d = 0;
		d = nows.getTime();
		return d;
	}

	public static long getSeconds() {
		return new Long((new GregorianCalendar().getTimeInMillis()) / 1000);
	}

	public static String getdate()// 获得精确到日的当前日期 用于oracle
	{
		dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(gCalendar.getTime());
		return dtime;
	}

	public static String gettime()// 获得精确到秒的当前日期 用于oracle
	{
		try {
			dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			gCalendar = new GregorianCalendar();
			dtime = dateFormat.format(gCalendar.getTime());
		} catch (Exception ex) {

		}
		return dtime;
	}

	/*
	 * @author: yangzi
	 * @fun : format the date
	 */
	public static String dateFormat(Date myDate) {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(myDate);
		return dtime;

	}

	/**
	 * @author : yangzi
	 * @function :得到精确到分钟的时间
	 * @param myDate
	 * @return
	 * @date: 2006-9-22
	 */
	public static String dateFormat(Date myDate, String strFormat) {
		dateFormat = new SimpleDateFormat(strFormat);
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(myDate);
		return dtime;

	}

	public static String dateFormat() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(gCalendar.getTime());
		return dtime;

	}

	/**
	 * 功能：指定日期的基础上增减日期（年、月、日、时、分、秒）
	 * 
	 * @param millis
	 *            //指定日期
	 * @param amount
	 *            //增量（正数或负数）
	 * @param field
	 *            //年、月、日、时、分、秒
	 * @return long //返回毫秒级
	 */
	public static long getMillis(long millis, int amount, int field) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		switch (field) {
		case 10:// 加年
			cal.add(Calendar.YEAR, amount);
			break;
		case 20:// 加月
			cal.add(Calendar.MONTH, amount);
			break;
		case 30:// 加日
			cal.add(Calendar.DATE, amount);
			break;
		case 40:// 加时
			cal.add(Calendar.HOUR, amount);
			break;
		case 50:// 加分
			cal.add(Calendar.MINUTE, amount);
			break;
		case 60:// 加秒
			cal.add(Calendar.SECOND, amount);
		default:// 默认加天
			cal.add(Calendar.DATE, amount);
		}
		return cal.getTime().getTime();
	}

	public static boolean compareStringTime(String begindateStr, String enddateStr) {
		if (begindateStr == null || begindateStr.equals(""))
			return false;
		if (enddateStr == null || enddateStr.equals(""))
			return false;
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		Date dateA = null;
		Date dateB = null;
		try {
			dateA = sdf1.parse(begindateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			dateB = sdf1.parse(enddateStr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (dateA.before(dateB)) {
			// begindateStr 小于 enddateStr 返回false
			return false;
		} else {
			// begindateStr 大于 enddateStr 返回true
			return true;
		}
	}

	public static String formatMs(long ms, int flag) {// 将毫秒数换算成x天x时x分x秒x毫秒
		int ss = 1000;
		int mi = ss * 60;
		int hh = mi * 60;
		int dd = hh * 24;

		long day = 0;
		long hour = 0;
		long minute1 = 0;
		long minute2 = 0;
		long second = 0;
		switch (flag) {
		case 0:// 将毫秒转为秒
			minute1 = (ms / ss) / 60;
			minute2 = (ms / ss) % 60;
			return minute1 + "分" + minute2 + "秒";
		default:
			break;
		}

		return "";
	}

	public static String getDateStrC(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
		return format.format(date);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	public static long dateFormat(String isFlag) {
		dateFormat = new SimpleDateFormat("yyMMddHHmm");
		gCalendar = new GregorianCalendar();
		dtime = dateFormat.format(gCalendar.getTime());
		return (new Long(dtime)).longValue();
	}

	// ***************************************************************************

	/**
	* @author yangzi
	* @version 2007-5-26 下午04:00:27
	* @todo 将字符串时间转日期（date）
	* @param strDate
	* @return  
	*/
	public static Date getDateFromStr(String strDate){
		  SimpleDateFormat   sdf   =   new   SimpleDateFormat("yyyy-MM-dd");   
		    
		 // Calendar   calendar   =   new   GregorianCalendar();  
		  Date   date = null;
		  try{
			  date   =   sdf.parse(strDate);   
		  }catch(Exception e){
			  
		  }
		  
		  return date;
	}
	
	
	/**
	* @author yangzi
	* @version 2007-5-28 上午11:57:41
	* @todo 根据字符串时间返回秒数
	* @param strDate
	* @param format
	* @return  
	*/
	public static Long getStrDateToSecond(String strDate,String format){ 
		  SimpleDateFormat   sdf   =   new   SimpleDateFormat(format); 
		 
		  long millSeconds = new GregorianCalendar().getTimeInMillis();
		  try{			  
			  millSeconds =sdf.parse(strDate).getTime();
		  }catch(Exception e){
			  logger.error("---------get seconds error:"+e.getMessage());
		  }		  
		  return new Long(millSeconds/1000);
	}
	
	
	/**
	* @author yangzi
	* @version 2007-5-26 下午04:04:35
	* @todo 返回有字符串时间对应的秒数
	* @param strDate
	* @return  
	*/
	public static Long getSecondsFromStrDate(String strDate){
		
		Long seconds =null;
		try{
			Date   date =getDateFromStr( strDate);
			
			 seconds = new Long(date.getTime()/1000);
		}catch(Exception e){
			
		}

		return seconds ;
		
	}
	
	
	

	/**
	 * @author yangzi
	 * @version 2007-5-20 下午02:55:48
	 * @todo 返回当天 某时刻的秒数
	 * @param hh
	 *            小时
	 * @param mm
	 *            分钟
	 * @param ss
	 *            秒
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Long getLongSecond(int hh, int mm, int ss) {
		Date rightNow = new Date();

		if (hh > 0 && hh < 23)
			rightNow.setHours(hh);
		if (mm > 0 && mm < 60)
			rightNow.setMinutes(mm);
		if (ss > 0 && ss < 60)
			rightNow.setSeconds(ss);

		return new Long(rightNow.getTime() / 1000);
	}

	/**
	 * @todo 返回当前时间 yyyy-MM-dd HH:mm:ss
	 * @return 2007-4-12
	 */
	// public static Date getRightDate()//获得精确到秒的当前日期
	// {
	// // dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	// // gCalendar=new GregorianCalendar();
	// // dtime=dateFormat.format(gCalendar.getTime());
	// // System.out.print(new Date(dtime));
	// return new Date();
	// }


	/**
	 * @return 本年开始一天的秒数 2007-4-12
	 */
	@SuppressWarnings("deprecation")
	public static long getYearStartSeconds() {
		// Calendar rightNow = Calendar.getInstance();
		Date rightNow = new Date();
		rightNow.setMonth(0);
		rightNow.setDate(1);
		rightNow.setHours(0);
		rightNow.setMinutes(0);
		rightNow.setSeconds(0);
		// System.out.print(rightNow);
		return rightNow.getTime() / 1000;
	}

	/**
	 * @返回当前月第一天据1970-01-01 00:00:00 的秒数 2007-4-12
	 */
	@SuppressWarnings("deprecation")
	public static long getMonthStartSeconds() {
		// Calendar rightNow = Calendar.getInstance();
		Date rightNow = new Date();
		rightNow.setDate(1);
		rightNow.setHours(0);
		rightNow.setMinutes(0);
		rightNow.setSeconds(0);
		// System.out.println(rightNow);
		// System.out.println(rightNow.getTime()/1000);
		return rightNow.getTime() / 1000;
	}

	/**
	 * @return 本周一的秒数 2007-4-12
	 */
	@SuppressWarnings("deprecation")
	public static long getWeekStartSeconds() {
		Date rightNow = new Date();
		rightNow.setHours(0);
		rightNow.setMinutes(0);
		rightNow.setSeconds(0);

		int days = rightNow.getDay();
		if (days == 0)
			days = 7;
		long reValue = rightNow.getTime() - (days - 1) * 24 * 60 * 60 * 1000;

		// System.out.println(reValue);
		// System.out.println(new Date(reValue));
		// System.out.println("1176048000");
		// System.out.println(reValue/1000-1176048000);
		// System.out.println(24*60*60*7);
		return reValue / 1000;
	}
	public static String getTimeYYYYMMDDHHMMSS()
	{
		String s = gettime();
		s = s.replaceAll("-", "").replaceAll(":", "").replaceAll(" ","").trim();
		return s;
	}
	/**
	 * 获得传入日期的零点秒值
	 * miliseconds毫秒数
	 */
    public static Long getBeginTime(Long miliseconds){
    	Date date=new Date(miliseconds);
		SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time=formatter.format(date);
    	String day = time.substring(0,10)+ " 00:00:00";
    	return getStrDateToSecond(day);
    }
}

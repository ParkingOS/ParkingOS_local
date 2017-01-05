/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年4月15日 
 * 
 *******************************************************************************/ 
package com.zld.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * <pre>
 * 功能说明: 
 * 日期:	2015年4月15日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年4月15日
 * </pre>
 */
public class AppInfoUtil {
	public static final String TAG = "AppInfoUtil";
	private static Build build;

	/**
	 * 功能说明：获取设备型号
	 */
	@SuppressWarnings("static-access")
	public static String getEquipmentModel() {
		if(build == null){
			build = new Build();
		}
		return build.MODEL;
	}

	/** 
	 * 获取当前应用程序的版本号versionCode
	 * @return
	 */
	public static String getVersionCode(Activity activity) {
		try {
			PackageManager manager = activity.getPackageManager();
			PackageInfo info = manager.getPackageInfo(activity.getPackageName(), 0);
			return String.valueOf(info.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	/**
	 * 获取当前应用程序的版本号versionName
	 * @return
	 */
	public static String getVersionName(Context context) {
		try {
			PackageManager manager = context.getPackageManager();
			PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
			return String.valueOf(info.versionName);
		} catch (Exception e) {
			e.printStackTrace();
			return "版本号未知";
		}
	}

	public static void displayBriefMemory(Context context) {        
		final ActivityManager activityManager = (ActivityManager)
				context.getSystemService(Context.ACTIVITY_SERVICE);    
		ActivityManager.MemoryInfo   info = new ActivityManager.MemoryInfo();   
		activityManager.getMemoryInfo(info);    
		Log.i(TAG,"系统剩余内存:"+(info.availMem >> 10)+"k");   
		Log.i(TAG,"系统是否处于低内存运行："+info.lowMemory);
		Log.i(TAG,"当系统剩余内存低于"+info.threshold+"时就看成低内存运行");
	}
	 
	public static long getAvailMemory() {// 获取android当前可用内存大小 
		ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
		long memSize = memoryInfo.availMem;
//		String leftMemSize = Formatter.formatFileSize(context, memSize);
		return memSize;
	}

	 public static long getTotalRam(){
		 FileInputStream fis = null;
		 BufferedReader br = null;
		 try {
			 File file = new File("/proc/meminfo");
			 fis = new FileInputStream(file);
		     br = new BufferedReader(new InputStreamReader(fis));
		     String totalRam = br.readLine();
		     StringBuffer sb = new StringBuffer();
		     char[] cs = totalRam.toCharArray();
		     for (char c : cs) {
		         if(c>='0' && c<='9'){
		              sb.append(c);
		          }
		      }
		      long result = Long.parseLong(sb.toString())*1024;
	          return result;
		   } catch (Exception e) {
		       e.printStackTrace();
		       return 0;
		   }finally{
			   if(fis!=null){
				   try {
					fis.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			   }
			   if(br!=null){
				   try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			   }
		   }
	}	
	    /**
	     * 获取手机内部剩余存储空间
	     * 
	     * @return
	     */
	    public static long getAvailableInternalMemorySize() {
	        File path = Environment.getDataDirectory();
	        StatFs stat = new StatFs(path.getPath());
	        long blockSize = stat.getBlockSize();
	        long availableBlocks = stat.getAvailableBlocks();
	        return availableBlocks * blockSize/1024/1024;
	    }

	    /**
	     * 获取手机内部总的存储空间
	     * 
	     * @return
	     */
	    public static long getTotalInternalMemorySize() {
	        File path = Environment.getDataDirectory();
	        StatFs stat = new StatFs(path.getPath());
	        long blockSize = stat.getBlockSize();
	        long totalBlocks = stat.getBlockCount();
	        return totalBlocks * blockSize/1024/1024;
	    }
	    
	    /** 获取系统总内存
	     * 
	     * @param context 可传入应用程序上下文。
	     * @return 总内存大单位为B。
	     */
	    public static long getTotalMemorySize(Context context) {
	        String dir = "/proc/meminfo";
	        try {
	            FileReader fr = new FileReader(dir);
	            BufferedReader br = new BufferedReader(fr, 2048);
	            String memoryLine = br.readLine();
	            String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
	            br.close();
	            return Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l;
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return 0;
	    }

	    /**
	     * 获取当前可用内存，返回数据以字节为单位。
	     * 
	     * @param context 可传入应用程序上下文。
	     * @return 当前可用内存单位为B。
	     */
	    public static long getAvailableMemory(Context context) {
	        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
	        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
	        am.getMemoryInfo(memoryInfo);
	        return memoryInfo.availMem;
	    }

}

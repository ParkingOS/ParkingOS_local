package com.zld.lib.util;

import java.util.Set;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SharedPreferencesUtils {

	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	final static String regularEx = "|";

	public SharedPreferencesUtils() {
		super();
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("static-access")
	@SuppressLint("CommitPrefEdits")
	public SharedPreferencesUtils(Context context, String file) {
		sp = context.getSharedPreferences(file, context.MODE_PRIVATE);
		editor = sp.edit();
	}

	public static void delete(Context context,String file,String key){
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}

	public static void setParam(Context context, String file, String key, String values){
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, values);
		editor.commit();
	}

	public static void setParam(Context context, String file, String key, int values){
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putInt(key, values);
		editor.commit();
	}

	public static void setParam(Context context, String file, String key,
			Long differenceTime) {
		// TODO Auto-generated method stub
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = sp.edit();
		editor.putLong(key, differenceTime);
		editor.commit();
	}

	public static void setParam(Context context, String file, String key, boolean values){
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_MULTI_PROCESS);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(key, values);
		editor.commit();
	}

	public static String getParam(Context context, String file, String key, String defaultValues){
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_MULTI_PROCESS);
		return sp.getString(key, defaultValues);
	}

	public static int getParam(Context context, String file, String key, int defaultValues){
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		return sp.getInt(key, defaultValues);
	}

	public static long getParam(Context context, String file,String key, long defaultValues) {
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		return sp.getLong(key, defaultValues);
	}
	
	/**
	 * 
	 * @param context
	 * @param file
	 * @param key
	 * @param defaultValues 没有此属性默认返回defaultValues
	 * @return
	 */
	public static boolean getParam(Context context, String file, String key, boolean defaultValues){
		//Context.MODE_MULTI_PROCESS属性 是为了让出入口界面中入口Service进程能访问到
		SharedPreferences sp = context.getSharedPreferences(file, Context.MODE_MULTI_PROCESS);
		return sp.getBoolean(key, defaultValues);
	}

	// 用户的密码
	public void setPasswd(String passwd) {
		editor.putString("passwd", passwd);
		editor.commit();
	}

	public String getPasswd() {
		return sp.getString("passwd", "");
	}

	// 用户的账号
	public void setAccount(String account) {
		editor.putString("account", account);
		editor.commit();
	}

	public String getAccount() {
		return sp.getString("account", "");
	}

	// 用户的角色
	public void setRole(String role) {
		editor.putString("role", role);
		editor.commit();
	}

	public String getRole() {
		return sp.getString("role", "");
	}

	// 保存余额信息
	public void setBanlance(String banlance) {
		editor.putString("banlance", banlance);
		editor.commit();
	}

	public void clear(){
		editor.clear();
		editor.commit();
	}
	
	public void remove(String key){
		editor.remove(key);
		editor.commit();
	}

	@SuppressLint("NewApi") public static Set<String> getStringSet(SharedPreferences prefs, String key,
			Set<String> defValues) {
		String str = prefs.getString(key, "");
		Log.e("SharedPreferencesHandler", "sp中存的String值是"+str);
		if (!str.isEmpty()) {
			String[] values = str.split("");
			Log.e("SharedPreferencesHandler", "sp中存的String去空格后的数组"+values[0].toString());
			if (defValues == null | defValues.isEmpty()) {
				defValues.clear();
				String strs = "";
				for (String value : values) {
					if (!value.isEmpty() && !value.equals(regularEx)) {
						strs = strs + value;
					}else {
						defValues.add(strs);  
						strs = "";
					}

				}
			}
		}
		return defValues;
	}

	public static SharedPreferences.Editor putStringSet(SharedPreferences.Editor ed, String key, Set<String> values) {
		String str = "";
		if (values != null | !values.isEmpty()) {
			Object[] objects = values.toArray();
			for (Object obj : objects) {
				str += obj.toString();
				str += regularEx;
			}
			ed.putString(key, str);
		}
		return ed;
	}
}

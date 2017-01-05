package com.zld.lib.util;

import java.lang.reflect.Method;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class InputUtil {

	/**
	 * 隐藏系统弹出的键盘,显示出光标
	 * @param activity
	 * @param editText
	 */
	public static void hideTypewriting(Activity activity,EditText editText) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		// Android.edittext点击时,隐藏系统弹出的键盘,显示出光标
		// 3.0以下版本可以用editText.setInputType(InputType.TYPE_NULL)来实现。
		// 3.0以上版本除了调用隐藏方法:setShowSoftInputOnFocus(false)
		int sdkInt = Build.VERSION.SDK_INT;// 16 -- 4.1系统
		if (sdkInt >= 11) {
			Class<EditText> cls = EditText.class;
			try {
				Method setShowSoftInputOnFocus = cls.getMethod(
						"setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus.setAccessible(false);
				setShowSoftInputOnFocus.invoke(editText, false);
				setShowSoftInputOnFocus.invoke(editText, false);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			editText.setInputType(InputType.TYPE_NULL);
		}
	}

	/**
	 * 隐藏系统弹出的键盘,显示出光标
	 * @param activity
	 * @param editText
	 */
	public static void showTypewriting(Activity activity,EditText editText) {

		InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
		// Android.edittext点击时,隐藏系统弹出的键盘,显示出光标
		// 3.0以下版本可以用editText.setInputType(InputType.TYPE_NULL)来实现。
		// 3.0以上版本除了调用隐藏方法:setShowSoftInputOnFocus(false)
		int sdkInt = Build.VERSION.SDK_INT;// 16 -- 4.1系统
		if (sdkInt >= 11) {
			Class<EditText> cls = EditText.class;
			try {
				Method setShowSoftInputOnFocus = cls.getMethod(
						"setShowSoftInputOnFocus", boolean.class);
				setShowSoftInputOnFocus.setAccessible(true);
				setShowSoftInputOnFocus.invoke(editText, true);
				setShowSoftInputOnFocus.invoke(editText, true);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		else {
			editText.setInputType(InputType.TYPE_NULL);
		}
	}

	public static void closeInputMethod(Context context) {
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
		if(imm.isActive()){  
			//			imm.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
			View currentFocus = ((Activity) context).getCurrentFocus();
			if(currentFocus != null){
				IBinder binder = currentFocus.getWindowToken();
				if(binder != null){
					imm.hideSoftInputFromWindow(binder,0);  
				}
			}
		}
	}
}

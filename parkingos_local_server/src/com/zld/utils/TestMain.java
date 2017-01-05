package com.zld.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Properties;

import com.ibatis.common.resources.Resources;

public class TestMain {
 
 //根据key读取value
 public static String readValue(String filePath,String key) {
  Properties props = new Properties();
        try {
    	 File file = Resources.getResourceAsFile(filePath);
    	 props.load(new FileInputStream(file));
         String value = props.getProperty (key);
            System.out.println(key+value);
            return value;
        } catch (Exception e) {
         e.printStackTrace();
         return null;
        }
 }
 
// public static String updateValue(String filePath,String key) {
//	  Properties props = new Properties();
//	        try {
//	    	 File file = Resources.getResourceAsFile(filePath);
//	    	 props.load(new FileInputStream(file));
//	         String value = props.getProperty (key);
//	            System.out.println(key+value);
//	            props.setProperty(key, "3333");
//	            OutputStream fos = new FileOutputStream(filePath);
//	            props.setProperty(parameterName, parameterValue);
//	            //以适合使用 load 方法加载到 Properties 表中的格式，
//	            //将此 Properties 表中的属性列表（键和元素对）写入输出流
//	            props.store(fos, "Update '" + parameterName + "' value");
//	            fos.flush();
//	            fos.close();
//	            return value;
//	        } catch (Exception e) {
//	         e.printStackTrace();
//	         return null;
//	        }
//	 }

    //写入properties信息
    public static void writeProperties(String filePath,String parameterName,String parameterValue) {
     Properties prop = new Properties();
     try {
    	 File file = Resources.getResourceAsFile(filePath);
    	 prop.load(new FileInputStream(file));
            //调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
            //强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
            OutputStream fos = new FileOutputStream(file);
            System.out.println(prop.getProperty(parameterName));
            prop.setProperty(parameterName, parameterValue);
            //以适合使用 load 方法加载到 Properties 表中的格式，
            //将此 Properties 表中的属性列表（键和元素对）写入输出流
            System.out.println(prop.getProperty(parameterName));
            prop.store(fos, "Update '" + parameterName + "' value");
            fos.flush();
            fos.close();
        } catch (IOException e) {
         System.err.println("Visit "+filePath+" for updating "+parameterName+" value error");
        }
    }

    public static void main(String[] args) {
     readValue("info.properties","url");
        writeProperties("info.properties","SYNCTO","33333");
//        readProperties("info.properties" );
        System.out.println("OK");
    } 
}
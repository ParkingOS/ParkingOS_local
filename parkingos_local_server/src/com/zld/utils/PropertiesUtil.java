package com.zld.utils;

import java.io.FileInputStream;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.IOException;

import java.util.Properties;





public class PropertiesUtil {





private static Properties prop = new Properties();

private final static String file = "F:\\work\\workspace\\myeclipse\\zldlocal\\WebRoot\\WEB-INF\\classes\\2.properties";

static{

try {

prop.load(new FileInputStream(file));

} catch (FileNotFoundException e) {

e.printStackTrace();

} catch (IOException e) {

e.printStackTrace();

}

}



public static String getProperty(String key){

return prop.getProperty(key);

}







public static void setProper(String key,String value){

/**

* 将文件加载到内存中，在内存中修改key对应的value值，再将文件保存

*/

try {

prop.setProperty(key, value);

FileOutputStream fos = new FileOutputStream(file);

prop.store(fos, null);

fos.close();



} catch (FileNotFoundException e) {

e.printStackTrace();

} catch (IOException e) {

e.printStackTrace();

}



}



public static void main(String[] args) {

System.out.println("修改前key为startTime的value的值"+PropertiesUtil.getProperty("comid"));

PropertiesUtil.setProper("comid", "1000");

System.out.println("修改后key为startTime的value的值"+PropertiesUtil.getProperty("comid"));

}

}

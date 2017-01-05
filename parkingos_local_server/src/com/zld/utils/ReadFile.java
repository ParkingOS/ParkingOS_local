package com.zld.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class ReadFile {

	public static List<Object[]>  praseFile() {

		String fileName = "c:\\info.txt";
		Map<String, String> map = new HashMap<String, String>();
		InputStream input = null;
		List<Object[]> values = null;
		try {
			File file = new File(fileName);
			input = new BufferedInputStream(new FileInputStream(file));
			InputStreamReader reader = new InputStreamReader(input, "gb2312");
			BufferedReader bufReader = new BufferedReader(reader);
			String str = null;
			Map<String, String> infoMap = new HashMap<String, String>();
			Object[] objects = null;
			values=new ArrayList<Object[]>();
			while ((str = bufReader.readLine()) != null) {
				while(str.indexOf("id")!=-1){
					objects = new Object[5];
					int b = str.indexOf("id");
					int e = str.indexOf("label");
					
					String l_l  = str.substring(b+3,e);
					str = str.substring(e);
					
					b = str.indexOf("label");
					e = str.indexOf("id");
					if(b==-1||e==-1)
						break;
					String name = str.substring(b+6,e);
					str = str.substring(e);
					objects[0]=Double.valueOf(l_l.split(",")[0].trim());
					objects[1]=Double.valueOf(l_l.split(",")[1].trim());
					objects[2]=name.split(" ")[0].trim();
					objects[3]=name.split(" ")[0].trim();
					objects[4]=1;
					values.add(objects);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return values;
	}
	public static void main(String[] args) {
		List<Object[]> valueList =praseFile();
		for(Object[] objects : valueList){
			System.out.println("经："+objects[0]+",纬："+objects[1]+",名称："+objects[3]+",名称："+objects[2]+",类型:"+objects[4]);
		}
	}
}

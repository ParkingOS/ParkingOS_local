package com.zld.lib.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LedStringUtils {

	/**
	 * BE A9
	 * @param str
	 */
	public static byte  stringToByte(String str){
		String[] splitString = str.split("");
		int parseInt = Integer.parseInt(splitString[1],16);
		int parseInt2 = Integer.parseInt(splitString[2],16);
		int i = parseInt * 16+parseInt2;
		return (byte)i;
	}

	/**
	 * 将 UID转为集合
	 * [1, 1, 1, 8, 1, 3, 6, 1, 7, 7]//111813617
	 * @param str
	 * @return
	 */
	public static List<String> transList(String str){
		String[] split = str.split("");
		List<String> list = Arrays.asList(split);
		List<String> newList = new ArrayList<String>();
		int j=0;
		for(int i = 1; i <list.size();i++){
			newList.add(j, list.get(i));
			j++;
		}
		/*Log.e(TAG,"分割编码字符："+newList.toString());*/
		return newList;
	}

	/**
	 * 将显示内容转为集合
	 * %ae%a9%0e8888// ae  a9  0e  08 08 08 08
	 * @param str
	 * @return
	 */
	public static List<String> transContentList(String str) {
		List<String> list = transList(str);
		List<String> hehe = new ArrayList<String>();
		int j = 0;
		for(int i = 0;i < list.size(); i++){
			if(list.get(i).equals("%")){
				String ss = list.get(i+1)+list.get(i+2);
				hehe.add(j, ss);
				i=i+2;
			}else{
				/*String ss = ""+(30+Integer.parseInt(list.get(i)));*/
				String ss = changeInt(list.get(i));
				hehe.add(j, ss);
			}
			j++;
		}
		return hehe;
	}

	/**
	 * 内容中大小写的处理
	 * @param str
	 * @return
	 */
	public static String changeInt(String str){
		char charAt = str.charAt(0);
		int a = 0;
		//是否是小写
		if(!Character.isLowerCase(charAt)){
			a = (charAt-'A')+65;
		}else{
			a = (charAt-'a')+97;
		}
		return Integer.toHexString(a);
	}

	/**
	 * 数组转为ArrayList
	 * @param showtext2
	 * @return
	 */
	public static ArrayList<Byte> asArrayList(byte[] showtext2){
		ArrayList<Byte> list = new ArrayList<Byte>();
		for(int i=0;i<showtext2.length;i++){
			list.add(showtext2[i]);
		}
		return list;
	}

	/**
	 * 数组转为ArrayList
	 * @param list
	 * @return
	 */
	public static byte[] asByteList(ArrayList<Byte> list){
		int size = list.size();
		byte[] byteList = new byte[size];
		for(int i=0;i<size;i++){
			byteList[i] = list.get(i);
		}
		return byteList;
	}
	/**
	 * 打印显示发送信息
	 * @param item
	 * @param buf
	 */
	public static void dumpMemory(String item, byte [] buf){
		@SuppressWarnings("unused")
		int lines = buf.length % 8 == 0 ? buf.length/8 : buf.length/8 + 1;
		int j = 0;
		int k = 0;
		int i = 0;
		byte c ;
		int WID = 16;
		System.out.println(item + "--" + buf.length);
		while(j*WID < buf.length){
			System.out.printf(" %04X: ", j*WID);
			for(i = 0; i < WID; i++){
				if((i + j * WID)>=buf.length) break;
				c = buf[i + j * WID];
				System.out.printf("%02X ",c);
				if((i+1) % 8 == 0) System.out.print(" ");
			}
			for(k=i; k<WID; k++){
				System.out.printf(" ");
				if((k+1) % 8 == 0) System.out.printf(" ");
			}
			System.out.printf(" ");
			for(i=0; i<WID; i++){
				if((i+j*WID) >= buf.length) break;
				c = buf[i+j*WID];
				if(c >= 0x30 && c <= 0x7a){
					System.out.printf("%c",c);
				}else{
					System.out.printf("%c",'.');
				}
			}
			System.out.printf("\n");
			j++;
		}
	}
}

/*******************************************************************************
 * Copyright (c) 2015 by ehoo Corporation all right reserved.
 * 2015年3月16日 
 * 
 *******************************************************************************/ 
package com.zld.decode;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.R.string;
import android.util.Log;

import com.zld.lib.util.LedControl;
import com.zld.lib.util.LedStringUtils;
import com.zld.ui.ZldNewActivity;

/**
 * <pre>
 * 功能说明: 
 * 日期:	2015年3月16日
 * 开发者:	HZC
 * 
 * 历史记录
 *    修改内容：
 *    修改人员：
 *    修改日期： 2015年3月16日
 * </pre>
 */
public class LedServerRunnable implements Runnable{

	private Socket socket;
	private String ledip;
	public LedServerRunnable(String ledip,Socket socket) {
		// TODO Auto-generated constructor stub
		this.socket = socket;
		this.ledip = ledip;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try{
			InputStream inputStream = socket.getInputStream();
			byte data[] = new byte[1024*4];
			int i = 0;
			int time = -1;
			while((i = inputStream.read(data)) !=1 ){
				String string = bcd2Str(data);
				Log.e("LedServerRunnable", "服务端接收到客户端发送过来的信息："+string);
				//根据固定Ip地址,给对应的客户端,发送返回信息,以维护心跳包
				String cur = string.substring(16, 18);
				Boolean result = false;
				if (time == -1) {
					byte[] login = LedStringUtils.asByteList(LedControl.getLedinstance().setLoginTime());
					result = LedControl.getLedinstance().sendLedData(ledip,login);
					time = 1;
				}
				if (cur.equals("91")) {
					result = true;
				}else if (cur.equals("61")) {
					byte[] login = LedStringUtils.asByteList(LedControl.getLedinstance().setLoginTime());
					result = LedControl.getLedinstance().sendLedData(ledip,login);
				}
				
				Log.e("LedServerRunnable", "心跳包result："+result+",ledip:"+ledip);
				//上传led状态
//				ZldNewActivity.instance.uploadLEDState(ledip, "1");
			}
		}catch(Exception e){
			Log.e("LedServerRunnable", "服务端接收信息异常："+e.getMessage());
			Log.e("LedServerRunnable", "心跳包result异常"+ledip);
			/*LedControl.getLedinstance().saveString("服务端接收信息异常："+e.getMessage()+"\n");*/
			LedControl.getLedinstance().close();
		}
	}
	public String bcd2Str(byte[] b) {
		if (b == null) {
			return null;
		}
		char HEX_DIGITS[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		StringBuilder sb = new StringBuilder(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_DIGITS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_DIGITS[b[i] & 0x0f]);
		}

		return sb.toString();
	}	
}

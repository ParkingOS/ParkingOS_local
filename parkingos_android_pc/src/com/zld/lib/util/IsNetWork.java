package com.zld.lib.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zld.lib.constant.Constant;

public class IsNetWork {
	
	static Thread thread;
	static boolean bTheadRun = true;

	/**
	 * 判断手机是否与互联网连接；
	 * @param context
	 * @return
	 */
	public static boolean IsHaveInternet(final Context context) {
		try {
			ConnectivityManager manger = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);

			WifiManager mWifiManager = (WifiManager) context 
					.getSystemService(Context.WIFI_SERVICE); 
			NetworkInfo info = manger.getActiveNetworkInfo();
			
//			String result = info.toString();  
//			StringBuffer sb = new StringBuffer();
//			sb.append("\n");
//			sb.append(TimeTypeUtil.getNowTime());
//			sb.append(result);  
//			sb.append("--mWifiManager.pingSupplicant():");
//			sb.append(mWifiManager.pingSupplicant());
//			sb.append("--info.isConnected():");
//			sb.append(info.isConnected());
//			sb.append("\n");
//			try {  
//				String fileName = FileUtil.getSDCardPath()
//						+ "/tcb"+ "/" +"log.txt";
//				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
//					String path = FileUtil.getSDCardPath()+ "/tcb/";  
//					File dir = new File(path);  
//					if (!dir.exists()) {  
//						dir.mkdirs();  
//					}  
//					FileOutputStream fos = new FileOutputStream(fileName,true);  
//					fos.write(sb.toString().getBytes());  
//					fos.close();  
//				}  
//			} catch (Exception e) {  
//				e.printStackTrace(); 
//			}  

			return (info != null && info.isConnected()
					&&info.isAvailable()&&isNetworkAvailable(context));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/** 
	 * 网络已经连接，然后去判断是wifi连接还是GPRS连接 
	 * 设置一些自己的逻辑调用 
	 */  
	public static boolean isNetworkAvailable(final Context context){  
//		ConnectivityManager manager = (ConnectivityManager) context 
//				.getSystemService(Context.CONNECTIVITY_SERVICE); 
//		State wifi = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();  
//		if(wifi == State.CONNECTED || wifi == State.CONNECTING){  
//			return true;
//		}
		return true;  
	}  

	/**
	 * Ping 服务器 平均延迟低于350 返回true
	 * @return
	 */
	static boolean result = false;

	public static void pintUitl(final String ipAddr){

		thread=new Thread(new Runnable(){  
			@SuppressWarnings("static-access")
			@Override  
			public void run() {  
				while(bTheadRun){
					Process process = null;  
					try {  
						// ping 一次   4秒  24字节
						process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 1 -s 24 "+ipAddr);  
						InputStream input = process.getInputStream();  
						BufferedReader in = new BufferedReader(new InputStreamReader(input));  
						StringBuffer buffer = new StringBuffer();  
						String line = "";  
						while ((line = in.readLine()) != null){  
								buffer.append(line);  
								Message msg = new Message();  
								Bundle bundle = new Bundle();  
								bundle.putString("result", line);  
								msg.setData(bundle);  
								System.out.println("Ping ceshi "+line);
								break;
						}  

					} catch (IOException e) {  
						e.printStackTrace();  
					}   
					try {
						thread.sleep(60*1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}    
			}
		});  
		thread.start();  
	}
	
	public static boolean ping() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection conn = null;
				try {
					/*登录时输入ip则Ping本地,默认Ping线上*/
						URL url = new URL(Constant.PING_TEST_LOCAL);
						conn = (HttpURLConnection) url.openConnection();
						conn.setConnectTimeout(5000);
						conn.setReadTimeout(8000);
						conn.setDoInput(true);
						conn.setRequestMethod("GET");
						conn.setRequestProperty("Content-Type", "text/html");
						conn.setRequestProperty("Accept-Charset", "utf-8");
						conn.setRequestProperty("contentType", "utf-8");
						conn.connect();
						if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
							result = true;
							Log.e("wangluo判断","DetectionInternet是true是:"+url);
						}else{
							int a = conn.getContentLength();
							result = false;
							Log.e("wangluo判断","DetectionInternet是false是:"+url);
						}
//					}
					
				}catch (Exception e) {
					result = false;
	                HttpGet httpGet=new HttpGet(Constant.PING_TEST_LOCAL);
	                HttpResponse response;
					try {
						response = new DefaultHttpClient().execute(httpGet);
						if(response.getStatusLine().getStatusCode()==200){  
		                	result = true;   
		                }
					} catch (ClientProtocolException e1) {
						e1.printStackTrace();
					} catch (IOException e1) {
						e1.printStackTrace();
					}  
					Log.e("wangluo判断","DetectionInternet的Exception是result:"+result);
				}finally{
					conn.disconnect();
				}
			}
		}).start();
		return result;
	}
}

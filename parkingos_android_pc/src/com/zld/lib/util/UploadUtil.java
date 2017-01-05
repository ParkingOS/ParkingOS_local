package com.zld.lib.util;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.zld.bean.AppInfo;
import com.zld.lib.constant.Constant;
import com.zld.ui.ZldNewActivity;

import android.util.Log;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadUtil {
	private static final String TAG = "UploadUtil";
	private static final int TIME_OUT = 10 * 1000; // 超时时间
	private static final String CHARSET = "utf-8"; // 设置编码

	/**
	 * android上传文件到服务器
	 * 
	 * @param file
	 *            需要上传的文件
	 * @param RequestURL
	 *            请求的rul
	 * @return 返回响应的内容
	 */
	public static String uploadFile(InputStream is, String RequestURL) {
		String result = null;
		String BOUNDARY = UUID.randomUUID().toString(); // 边界标识 随机生成
		String PREFIX = "--", LINE_END = "\r\n";
		String CONTENT_TYPE = "multipart/form-data"; // 内容类型

		try {
			URL url = new URL(RequestURL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIME_OUT);
			conn.setConnectTimeout(TIME_OUT);
			conn.setDoInput(true); // 允许输入流
			conn.setDoOutput(true); // 允许输出流
			conn.setUseCaches(false); // 不允许使用缓存
			conn.setRequestMethod("POST"); // 请求方式
			conn.setRequestProperty("Charset", CHARSET); // 设置编码
			conn.setRequestProperty("connection", "keep-alive");
			conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
			Log.i(TAG, "-->>requestURL:" + RequestURL);
			if (is != null) {
				/**
				 * 当文件不为空，把文件包装并且上传
				 */
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				StringBuffer sb = new StringBuffer();
				sb.append(PREFIX);
				sb.append(BOUNDARY);
				sb.append(LINE_END);
				/**
				 * 这里重点注意： name里面的值为服务器端需要key 只有这个key 才可以得到对应的文件
				 * filename是文件的名字，包含后缀名的 比如:abc.png
				 */
				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\"" + "zhenlaidian.jpg" + "\""
						+ LINE_END);
				sb.append("Content-Type: application/octet-stream; charset=" + CHARSET + LINE_END);
				sb.append(LINE_END);
				dos.write(sb.toString().getBytes());
				byte[] bytes = new byte[1024];
				int len = 0;
				while ((len = is.read(bytes)) != -1) {
					dos.write(bytes, 0, len);
				}
				is.close();
				dos.write(LINE_END.getBytes());
				byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
				dos.write(end_data);
				dos.flush();
				/**
				 * 获取响应码 200=成功 当响应成功，获取响应的流
				 */
				int res = conn.getResponseCode();
				Log.e(TAG, "response code:" + res);
				if (res == 200) {
					Log.e(TAG, "request success");
					InputStream input = conn.getInputStream();
					StringBuffer sb1 = new StringBuffer();
					int ss;
					while ((ss = input.read()) != -1) {
						sb1.append((char) ss);
					}
					result = sb1.toString();
					Log.e(TAG, "result : " + result);
				} else {
					Log.e(TAG, "request error");
				}
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 上传电话记录
	 * 
	 * @param pathurl
	 * @return
	 */
	public static String uploadRecord(String pathurl) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(pathurl);
		String result = null;
		try {
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = EntityUtils.toString(response.getEntity());
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	public static String doGet(String url) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet httpGet = new HttpGet(url);
		String result = null;
		try {
			System.out.println("-->url=" + url);
			HttpResponse response = httpClient.execute(httpGet);
			if (response.getStatusLine().getStatusCode() == 200) {
				result = new String(EntityUtils.toByteArray(response.getEntity()), "GBK");// .getContentCharSet(response.getEntity());
				System.out.println("-->url=" + url + ",result=" + result);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return result;
	}

	/**
	 * 上传文件
	 */
	public static void uploadFile(final InputStream bitmapToInputStream) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				// String request =
				// "http://192.168.199.122/zld/collectorrequest.do?action=uplogfile";
				// String url = request + "&"
				// + "token=" + AppInfo.getInstance().getToken();
				String url = Constant.requestUrl + "collectorrequest.do?action=uplogfile&" + "token="
						+ AppInfo.getInstance().getToken();
				Log.e(TAG, "请求的url-->>" + url);
				String result = UploadUtil.uploadFile(bitmapToInputStream, url);
				Log.e(TAG, "上传文件的返回结果是-->>" + result);
				// Map<String, String> resultMap =
				// StringUtils.getMapForJson(result);
				// Message msg = new Message();
				// if (resultMap != null) {
				//
				// }
			}
		}.start();
	}
	
	public static void testUploadFile()  {  
		String url = Constant.requestUrl + "collectorrequest.do?action=uplogfile&" + "token="
				+ AppInfo.getInstance().getToken();
        //创建OkHttpClient对象  
        OkHttpClient mOkHttpClient = new OkHttpClient();  
        File file=null;
		try {
			file = FileUtil.createSDFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
  
        //application/octet-stream 表示类型是二进制流，不知文件具体类型  
        RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);  
  
        RequestBody requestBody = new MultipartBody.Builder()
        		.setType(MultipartBody.FORM) 
                .addPart(Headers.of(  
                        "Content-Disposition",  
                        "form-data; name=\"username\""),  
                        RequestBody.create(null, "***"))  
                .addPart(Headers.of(  
                        "Content-Disposition",  
                        "form-data; name=\"img\";filename=\"zhenlaidian.jpg\""), fileBody)
//                .addFormDataPart("Content-Disposition",  
//                        "form-data; name=\"img\";filename=\"zhenlaidian.jpg\"")
//                .addFormDataPart("Content-Type", "application/octet-stream; charset=utf-8")
                .build();  
//        "Content-Disposition: form-data; name=\"img\"; filename=\"" + "zhenlaidian.jpg" + "\""
//		+ LINE_END
        Request request = new Request.Builder()  
                .url(url)  
                .post(requestBody)  
                .build();  
  
        Call call = mOkHttpClient.newCall(request);  
        call.enqueue(new Callback()  
        {  
			@Override
			public void onFailure(Call arg0, IOException arg1) {
				// TODO Auto-generated method stub
				Log.e(TAG, "fail ");
			}

			@Override
			public void onResponse(Call arg0, Response arg1) throws IOException {
				// TODO Auto-generated method stub
				Log.e(TAG, "response "+arg1.body().string());
			}  
        });  
    }  
}

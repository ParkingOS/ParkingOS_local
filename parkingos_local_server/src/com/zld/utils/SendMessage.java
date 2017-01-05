package com.zld.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SendMessage {

	public  Integer sendMessageToCarOwer(String mobile){
		Integer code = new Random(System.currentTimeMillis()).nextInt(10000);
		String time ="";// TimeTools.gettime().substring(11,16);
		if(code<99)
			code=code*100;
		if(code<999)
			code =code*10;
		String result = sendMessage(mobile, "您本次的验证码:"+code+" "+time+"【停车宝】");//sendMsg_ManDao_Http(mobile,code);
		//System.out.println("======手机号："+mobile+"=========短信发送结果"+result);
		if(result.equals("0"))
			return code;
		return null;
	}
	public  Integer getCode(){
		Integer code = new Random(System.currentTimeMillis()).nextInt(10000);
		if(code<99)
			code=code*100;
		if(code<999)
			code =code*10;
		return code;
	}
	
	public static  String sendMessage(String mobile,String code){
		//http://sdk.entinfo.cn:8060/webservice.asmx/SendSMS?sn=WEB-XJG-010-00051&pwd=791445&mobile=15801482643&content=测试【停车宝】
		//String url ="http://211.147.224.154/cgi-bin/sendsms";//
		//D4D714306327249005D7C3DE885A1051
		String url ="http://sdk.entinfo.cn:8060/webservice.asmx/SendSMS";//
		//?username=tq&password=123456&msgtype=1&to="+mobile+"&text="+code;
		String result = null;
		url +="?sn=WEB-XJG-010-00051&pwd=791445&mobile="+mobile;
		//url +="?username=tq&password=123456&msgtype=1&to="+mobile;
		try {
			url +="&content="+URLEncoder.encode(code,"gb2312");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
		result = new HttpProxy().doGet(url);
		/*Map<String, String> params = new HashMap<String, String>();
		params.put("username", "tq");
		params.put("password", "123456");
		params.put("msgtype", "1");
		params.put("to", mobile);
		try {
			params.put("text",URLEncoder.encode(code+"","gb2312"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		result = new HttpProxy().doPost(url, params);*/
		//return doGet(url);
		System.err.println("发送短信：内容："+code+",接收人："+mobile+",结果:"+result);
		if(result!=null&&result.indexOf("成功")!=-1)
			return "0";
		return "-1";
	}
	//群发短信
	public  static String sendMultiMessage(String mobiles,String message){
		//http://sdk.entinfo.cn:8060/webservice.asmx/SendSMS?sn=WEB-XJG-010-00051&pwd=791445&mobile=15801482643&content=测试【停车宝】
		//String url ="http://211.147.224.154:18013/cgi-bin/sendsms";//
		//String url ="http://211.147.239.62:9088/cgi-bin/sendsms";//
		//String url="http://www.oa-sms.com/sendSms.action";
		//String url ="http://sdk.entinfo.cn:8060/webservice.asmx/mt";//
		String url ="http://sdk2.entinfo.cn:8061/mdsmssend.ashx";//
		
		String sn = "WEB-CSL-010-00199";
		String ps = "285776";
		String result = null;
		String content = message;
		String password = "";
		try {
			//content =URLEncoder.encode(message,"gb2312");
			content =URLEncoder.encode(content,"utf-8");
			password = StringUtils.MD5(sn+ps).toUpperCase();
		} catch (Exception e) {
			// TODO: handle exception
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("sn", sn);
		params.put("pwd", password);
		params.put("mobile", mobiles);
		params.put("Content", content);
		params.put("Ext", "");
		params.put("stime", "");
		params.put("Rrid", "");
		try {
			result = new HttpProxy().doPost(url, params);
			//result = send(param, url, "utf-8",  "utf-8");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		result = new HttpProxy().doPost(url, params);
		//System.out.println(">>>>>>>>>>>>>>>群发接收人："+mobiles);
		//System.out.println(">>>>>>>>>>>>>>>群发短信结果："+result);
		System.err.println(">>>>>>>>>>>>>>>发送短信：内容："+message+",接收人："+mobiles+",结果:"+result+",请求:"+url);
		//if(result.equals("0"))
		return "1";
		//return "-1";
	}
	
	public  String send(String params, String strurl,String encode,String decode) throws Exception {
		// TODO Auto-generated method stub
		HttpURLConnection url_con = null;
		String responseContent = null;
		URL url = new URL(strurl);
		url_con = (HttpURLConnection) url.openConnection();
		url_con.setRequestMethod("POST");
		url_con.setConnectTimeout(10000);//
		url_con.setReadTimeout(10000);//
		url_con.setDoOutput(true);
		byte[] b = params.toString().getBytes(encode);
		url_con.getOutputStream().write(b, 0, b.length);
		url_con.getOutputStream().flush();
		url_con.getOutputStream().close();
		int rescode = url_con.getResponseCode();
		responseContent = rescode+"";
		InputStream in = null;
		try {
			in = url_con.getInputStream();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			in=url_con.getErrorStream();
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(in,decode));
		String tempLine = rd.readLine();
		StringBuffer tempStr = new StringBuffer();
		String crlf=System.getProperty("line.separator");
		while (tempLine != null)
		{
			tempStr.append(tempLine);
			tempStr.append(crlf);
			tempLine = rd.readLine();
		}
		responseContent = tempStr.toString();
		rd.close();
		in.close();
		// Thread.sleep(5000);
		return responseContent;
	}
	public static void main(String[] args) {
		String mesg = "今晚用停车宝支付停车费，省心省力更！省！钱！立即在“我的账户”查看所领停车券，停车宝伴您出行最后一站。下载地址：www.tingchebao.com 【停车宝】 ";
		sendMessage("15801482643", mesg);
		//		Integer code = new Random(System.currentTimeMillis()).nextInt(10000);
//		String time = TimeTools.gettime().substring(11,16);
//		if(code<99)
//			code=code*100;
//		if(code<999)
//			code =code*10;
//		System.out.println(code);
//		String mesg ="您的手机号：1364 121 2226，已被监控到发送了不文明信息，我们将继续监控，如果再有不良信息，我们将给您停机，并移交司法部门进行取证调查，" +
//				"由此所带来的后果，将由您来承担。【中国信息监控】";
//		String result =sendMessage("13641212226",mesg);
//		System.out.println(result);

//

//		String message ="尊敬的合作伙伴您好，车主(手机：18518132466)已通过停车宝购买贵车场包月服务10个月，费用3990.00元，您可以在后台查看相应信息。"+
//						"车主将凭短信前来换取月卡，请备好相应月卡，谢谢。客服：01053618108 【停车宝】";
		//System.err.println(sendMessage("13718451896",message));
//		System.out.println(sendMessage("15801482643",message));
//		String message ="【停车宝】下周一（12月22日）起，对于不支持手机支付的停车场，将不再累计积分发放奖品。诚邀您支持支付，每笔交易奖励两元。欢迎联系010-56450585洽谈支付。";
//		String mobiles = ZldTest.anlysisPhoneLocal();
//		System.out.println(sendMultiMessage(mobiles,message));
	}
	
/*	public static String sendMsg_ManDao_Http (String mobile,Integer code){
		String result="";
		String strurl="http://sdk.entinfo.cn:8060/z_mdsmssend.aspx";
		String user= "SDK-BBX-010-14147";
		String pass = "F475168E9BC3D3CB275BF8E684CD680A";
//		String strurl="http://sdk2.entinfo.cn/z_send.aspx";
//		String user= "SDK-BBX-010-14147";
//		String pass = "332100";
//		String user= "SDK-BBX-010-14146";
//		String pass = "371571";
//		String user= "SDK-BBX-010-19035";
//		String pass = "c(32adb(";
		
		String params = "sn="+user+"&pwd="+pass+"&mobile="+mobile+"&content=验证码："+code+","+TimeTools.gettime()+"【真来电】";
		try {
			result = submit(params,strurl, "GBK","GBK");
			result=result.trim();
		} catch (Exception e) {
			e.printStackTrace();
			result = "-7";//异常
		}
		return result;
	}
	private static String submit(String params, String strurl,String encode,String decode) throws Exception {
		// TODO Auto-generated method stub
		HttpURLConnection url_con = null;
		String responseContent = null;
		URL url = new URL(strurl);
		url_con = (HttpURLConnection) url.openConnection();
		url_con.setRequestMethod("POST");
		url_con.setConnectTimeout(10000);//
		url_con.setReadTimeout(10000);//
		url_con.setDoOutput(true);
		byte[] b = params.toString().getBytes(encode);
		url_con.getOutputStream().write(b, 0, b.length);
		url_con.getOutputStream().flush();
		url_con.getOutputStream().close();
		int rescode = url_con.getResponseCode();
		responseContent = rescode+"";
		InputStream in = null;
		try {
			in = url_con.getInputStream();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			in=url_con.getErrorStream();
		}
		BufferedReader rd = new BufferedReader(new InputStreamReader(in,decode));
		String tempLine = rd.readLine();
		StringBuffer tempStr = new StringBuffer();
		String crlf=System.getProperty("line.separator");
		while (tempLine != null)
		{
			tempStr.append(tempLine);
			tempStr.append(crlf);
			tempLine = rd.readLine();
		}
		responseContent = tempStr.toString();
		rd.close();
		in.close();
		// Thread.sleep(5000);
		return responseContent;
	}
	
	*/
}

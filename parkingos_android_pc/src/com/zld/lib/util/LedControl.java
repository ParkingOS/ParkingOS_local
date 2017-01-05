package com.zld.lib.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.annotation.SuppressLint;
import android.graphics.drawable.Animatable;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView.BufferType;

import com.zld.decode.LedServerRunnable;

/**
 * 平板作为服务器端连接控制卡--登录
 * 下发内码文字和其他内容
 * @author HZC
 */
@SuppressLint("HandlerLeak")
public class LedControl{

	private static final String TAG = "LedControl";
	private static LedControl ledControl = new LedControl();

	private ServerSocket serverSocket;
	private HashMap<String, Socket> socketMap = new HashMap<String, Socket>();

	//登录控制卡指令
	public static byte[] login = {
		(byte) 0xfe,0x5c,0x4b,(byte) 0x89, //包头
		0x2a,0x00,0x00,0x00, //总长
		0x62, //消息类型
		0x00,0x00,0x00,0x00, //ID
		0x17,0x00,0x00,0x00, //数据长度
		0x31, //请求结果（1=通过、0=拒绝）18
		0x23,0x32,0x30,0x30,0x38,0x30,0x32,0x32,0x39,0x30,0x35,0x31,0x31,0x30,0x34,0x31,0x35, //时间17
		0x23,0x30,0x35,0x30, //心跳包时间 50秒
		0x23,(byte) 0xff,(byte) 0xff, //包尾7
	};

	//将内码文字UID为：095223906的内码文字内容改为1234567890
	public static byte[] showtext = {
		(byte) 0xfe,0x5c,0x4b,(byte) 0x89,
		0x5e,0x00,0x00,0x00,//共94=5E字节//第4位
		0x31,0x00,0x00,(byte) 0x9e,(byte) 0xe4,
		0x4b,0x00,0x00,0x00,//数据长度75=4B字节//第13位
		0x30,0x39,0x35,0x32,0x32,0x33,0x39,0x30,0x36,//UID//第17位开始//第25位结束
		0x2c,// 分隔符‘,‘
		0x01,//移动方式,第27位
		0x01,//移动速度,第28位
		0x01,//停留时间,第29位
		0x30,0x31,0x30,0x31,0x30,0x31,0x39,0x39,0x31,0x32,0x33,0x31,
		0x13,0x00,0x00,0x00,//素材属性长度
		0x55,(byte) 0xaa,0x00,0x00,0x37,0x31,0x31,0x31,
		0x32,//屏幕双基色；若改为双基色应为31,第54位
		0x31,0x00,0x00,
		0x08,0x00,//修改屏幕宽度//第58位开始//实际屏幕/8= 写入屏宽
		0x10,0x00,//修改屏幕高度//第60位开始//实际屏幕取16进制 = 写入屏高
		0x01,//颜色	 //第62位开始//颜色（高四位=数字颜色，低四位=后缀颜色） 1=红色  2=绿色  3=黄色
		0x11,//字体字号//第63位开始//高四位=字体       低四位=字号
		//字体：（从1开始，依次为：宋体、楷体、黑体、隶书、行书）;
		//字号：（从0开始，依次为12*12、16*16、24*24、32*32、48*48、64*64、80*80）;
		0x00,
		0x14,0x00,0x00,0x00,//修改内码文字内容长度（不含此四字节）//第65位开始
		//0x31,0x32,0x33,0x34,0x35,0x36,0x37,0x38,0x39,0x30,//修改的内码文字内容//第69位开始//第78位结束
		(byte) 0xff,0x00,0x01,0x00,0x01,0x00,0x01,0x00,0x10,
		0x48,0x2d,0x31,0x2c,(byte) 0xff,(byte) 0xff//共93位
	};

	@SuppressWarnings("unused")
	private Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			Log.e(TAG,"LED重新连接");
			startLedConn();
		}
	};

	@SuppressLint("HandlerLeak")
	public static LedControl getLedinstance(){
		return ledControl;
	}

	private LedControl() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * 连接LED,获取Led请求登录的信息,并回应
	 * @return
	 */
	public void startLedConn(){ 
		new Thread(new Runnable(){
			@Override  
			public void run() {
				// TODO Auto-generated method stub  
				service();
			}
		}).start();
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
public void wirteLog(byte[] buffer){
	
	String fileName = FileUtil.getSDCardPath()
			+ "/tcb"+ "/" +"LEDlog.txt";
	if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {  
		String path = FileUtil.getSDCardPath()+ "/tcb/";  
		File dir = new File(path);  
		if (!dir.exists()) {  
			dir.mkdirs();  
		}  
		
		try {
			FileOutputStream fos = new FileOutputStream(fileName,true);  
			fos.write(bcd2Str(buffer).getBytes());
			String huan = "\n";
			fos.write(huan.toString().getBytes());
			fos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		 
	}  
}
	/**
	 * 根据传递的ledip获取不同的socket,发送LED数据
	 * @param ledip 
	 * @param cmd
	 * @return
	 * @throws IOException
	 */		
	public boolean sendLedData(String ledip, byte cmd[]){
//		wirteLog(cmd);
		// TODO Auto-generated method stub
		Socket socket = null;
		if(socketMap != null&&socketMap.size()!=0){
			socket = socketMap.get(ledip);
			Log.e(TAG, "需要发送到的:"+ledip+"---需要发送到的Socket："+socket);
			if(socket != null){
				try{
					OutputStream out = socket.getOutputStream();
					if (out == null){
						return false;
					}
					out.write(cmd);
					out.flush();
					return true;
				}catch(Exception e){
					Log.e(TAG, "客户端发送信息异常："+e.getMessage());
					close();
					return false;
				}
			}
		}
		return false;
	}
	public static ArrayList<Byte> setLoginTime(){
		ArrayList<Byte> list = null;
		list = LedStringUtils.asArrayList(login);
		SimpleDateFormat dateYear = new SimpleDateFormat("yyyyMMdd");//设置日期格式
		String timeyear = dateYear.format(new Date());// new Date()为获取当前系统时间
		SimpleDateFormat dateHour = new SimpleDateFormat("HHmmss");//设置日期格式
		String timehour = dateHour.format(new Date());// new Date()为获取当前系统时间
		Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w<1) {
			w=7;
		}
        String allTime = timeyear+"0"+String.valueOf(w)+timehour;
		int index = 19;
		for (int i = 0; i < allTime.length(); i++) {
			int value = Integer.valueOf(allTime.substring(i, i+1));
			list.set(index, (byte)value);
			index ++;
		}
		return list;
	}
	/**
	 * 采用内码文字素材
	 * Byte数组转集合，改变Byte
	 * UID = "111813617";
	 * content = "京E88888"
	 * width=屏宽
	 * height=屏高
	 * color = 1=红色  2=绿色  3=黄色
	 * fontSize = 1字体
	 * wordSize = 1字号
	 * @param UID
	 * @param content
	 */
	public static ArrayList<Byte> change(String UID,String content,int width,int height,
			String color,String fontSize,String wordSize,String ledmovemode){
		int allSize = 0;
		int dataSize = 0;
		int contentSize = 0;
		ArrayList<Byte> list = null;
		List<String> transContentList = null;
		try {
			/*Byte数组转集合*/
			list = LedStringUtils.asArrayList(showtext); 
			String encode = URLEncoder.encode(content,"gb2312");
			/*显示内容字节集合*/
			transContentList = LedStringUtils.transContentList(encode);
			contentSize = transContentList.size();
			allSize = contentSize+84;
			dataSize = contentSize+65;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*UID拆分成集合*/
		List<String> transList = LedStringUtils.transList(UID);
		for(int i = 0;i < list.size();i++){
			if(i == 4){//修改总长度
				list.set(4, (byte)(allSize));
			}
			if(i == 13){//修改数据长度
				list.set(13, (byte)dataSize);
			}
			if(i == 17){//修改UID j<8
				for(int j=0;j<transList.size();j++){
					list.set(17+j, (byte)(48+Integer.parseInt(transList.get(j))));
					i++;
				}
			}
			if(i == 27){//移动方式
				/*Log.e(TAG,"移动方式："+ledmovemode);*/
				list.set(27, (byte)Integer.parseInt(ledmovemode));
			}
			if(i == 28){//移动速度
				/*Log.e(TAG,"移动速度："+ledmovemode);*/
				list.set(28, (byte)Integer.parseInt(ledmovemode));
			}
			if(i == 29){//停留时间
				/*Log.e(TAG,"停留时间："+ledmovemode);*/
				list.set(29, (byte)Integer.parseInt(ledmovemode));
			}
			if(i == 58){//修改屏幕宽度
				/*Log.e(TAG,"width:"+width+"  height:"+height);*/
				list.set(58, (byte)(width/8));
			}
			if(i == 60){//修改屏幕高度
				list.set(60, (byte)height);
			}
			if(i == 62){//字体颜色
				/*Log.e(TAG,"字体颜色："+color);*/
				list.set(62, (byte)(Integer.parseInt(color+1)));
			}
			/*if(i == 63){//字体字号 31  25 
				System.out.println("字体字号："+fontSize+wordSize+"---"+Integer.parseInt(fontSize+wordSize));
				list.set(63, (byte)(Integer.parseInt(fontSize+wordSize)+6));
						}*/
			if(i == 65){//修改内码文字内容长度*/
				list.set(65, (byte)(contentSize+10));
			}
			if(i == 68){//修改的内码文字内容*/
				for(int k=0;k<contentSize;k++){
					list.add(69+k, (byte)(LedStringUtils.stringToByte(transContentList.get(k))));
				}
			}
		}
		return list;
	}
	
	/**
	 * 通过LED控制卡控制音频卡播报
	 * @param content
	 * @return
	 */
	public ArrayList<Byte> change(String content,int cmd){
		int allSize = 0;
		int dataSize = 0;
		int contentSize = 0;
		ArrayList<Byte> list = null;
		List<String> transContentList = null;
		/*Byte数组转集合*/
		if (cmd == 1) {
			list = LedStringUtils.asArrayList(SpeakerControl.cmd); 
		}else {
			list = LedStringUtils.asArrayList(SpeakerControl.cmd2); 
		}
		
		try {
			String encode = URLEncoder.encode(content,"gb2312");
			//内容 集合
			transContentList = LedStringUtils.transContentList(encode);
			contentSize = transContentList.size();
			allSize = contentSize+26;
			dataSize = contentSize+7;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i < list.size();i++){
			if(i == 4){//修改总长度
				list.set(4, (byte)(allSize));
			}
			if(i == 13){//修改数据长度
				list.set(13, (byte)dataSize);
			}
			if(i == 19){//修改内码文字内容长度
				list.set(19, (byte)(contentSize+2));
			}
			if(i == 22){//修改的内码文字内容
				for(int k=0;k<contentSize;k++){
					list.add(22+k, (byte)(LedStringUtils.stringToByte(transContentList.get(k))));
				}
			}
		}
		LedStringUtils.dumpMemory("LedControl", LedStringUtils.asByteList(list));
		return list;
	}

	public void service(){
		try{
			Log.e(TAG, "开启服务连接");
			while(true){
				if(serverSocket == null){
					serverSocket = new ServerSocket(8888);
				}
				Socket socket = serverSocket.accept();
				Log.e(TAG, "有新设备连接");
				String inetAddress = socket.getInetAddress().toString();
				String address = inetAddress.subSequence(1, inetAddress.length()).toString();
				if(null != socketMap&&!socketMap.containsKey(address)){
					socketMap.put(address, socket);
				}
				Log.e(TAG,"接收客户端Socket："+socket.toString());
				Thread workThread = new Thread(new LedServerRunnable(address,socket));
				workThread.start();
			}
		}catch(IOException e){
			e.printStackTrace();
			Log.e(TAG,"ServerSocket在accept时有误："+e.getMessage());
		}
	}

	public void close(){
		try {
			if(socketMap != null){
				for (Entry<String, Socket> entry : socketMap.entrySet()) {
					if(null != entry.getValue()&&entry.getValue().isConnected()){
						/*entry.getValue().setSoLinger(true, 3000);*/
						Log.e(TAG,"是否已关闭："+entry.getValue().isClosed());
						if(!entry.getValue().isClosed()){
							entry.getValue().close();
							Log.e(TAG, "Map的Socket先关闭");
						}
					}
				}
			}
			socketMap.clear();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			Log.e(TAG, "关闭Socket后的socketMap："+socketMap);
//			Message message = new Message();
//			message.what = 1;
//			Log.e(TAG, "ServerScoket30秒后重启");
//			handler.sendMessageDelayed(message, 30000);
		}
	}
	
	public void destory(){
		Log.e(TAG,"LedSocket:destory");
		try {
			if(socketMap != null){
				for (Entry<String, Socket> entry : socketMap.entrySet()) {
					if(null != entry.getValue()&&entry.getValue().isConnected()){
						Log.e(TAG,"Activity在Destory时关闭Socket是否已关闭："+entry.getValue().isClosed());
						if(!entry.getValue().isClosed()){
							entry.getValue().close();
							Log.e(TAG, "Map的Socket先关闭");
						}
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**采用实时采集素材*/
	public static ArrayList<Byte> changeshow(String UID,String content,int width,int height,
			String color,String fontSize,String wordSize,String ledmovemode){
		int allSize = 0;
		int dataSize = 0;
		int contentSize = 0;
		ArrayList<Byte> list = null;
		List<String> transContentList = null;
		try {
			/*模板内容Byte数组转集合*/
			list = LedStringUtils.asArrayList(intimeshow); 
			/*显示内容*/
			String encode = URLEncoder.encode(content,"gb2312");
			/*显示内容字节集合*/
			transContentList = LedStringUtils.transContentList(encode);
			/*显示内容字节集合的长度*/
			contentSize = transContentList.size();
			/*总长*/
			allSize = contentSize+24;
			/*数据长度*/
			dataSize = contentSize+5;
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(int i = 0;i < list.size();i++){
			if(i == 4){//修改总长度
				list.set(4, (byte)(allSize));
			}
			if(i == 13){//修改数据长度
				list.set(13, (byte)(dataSize+6));
			}
			if(i == 17){//修改种类编号
				list.set(17, (byte)Integer.parseInt(UID));
			}
			if(i == 19){//字体颜色--未测试
				if (color.equals("0")) {
					list.set(19, (byte)(0x11));
				}else if (color.equals("1")) {
					list.set(19, (byte)(0x22));
				}else if (color.equals("2")) {
					list.set(19, (byte)(0x33));
				}

			}
//			if(i == 19){//字体字号 31  25 
//				System.out.println("字体字号："+
//				fontSize+wordSize+"---"+Integer.parseInt(fontSize+wordSize));
//				list.set(20, (byte)(Integer.parseInt(fontSize+wordSize)+6));
//			}
			if(i == 21){//修改本项数据长度
				list.set(21, (byte)contentSize);
			}
			if(i == 22){//修改的实时采集内容*/
				for(int k=0;k<contentSize;k++){
					list.add(22+k, (byte)(LedStringUtils.stringToByte(transContentList.get(k))));
				}
			}

		}
		LedStringUtils.dumpMemory("yuyin", LedStringUtils.asByteList(list));
		return list;
	}

	//将实时采集种类型号为：41的实时采集内容为2013;
	public static byte[] intimeshow = {
		(byte) 0xfe,0x5c,0x4b,(byte) 0x89,//包头
		0x1c,0x00,0x00,0x00,//数据总长
		0x65,//消息类型
		(byte) 0x92,0x79,(byte) 0x95,0x72,//报文ID
		0x09,0x00,0x00,0x00,//数据长度
		0x29,//种类编号
		0x00,//闪烁
		0x11,//颜色
		0x11,//字体
		0x04,//内容数据长度
		/*0x32,0x30,0x31,0x33,//显示内容*/	
		(byte) 0xff,(byte) 0xff
	};
	
	/**采用点播素材*/
	public static ArrayList<Byte> trafficLightBlue(){
		ArrayList<Byte> list = null;
		list = LedStringUtils.asArrayList(demandBlue); 
		
		LedStringUtils.dumpMemory("yuyin", LedStringUtils.asByteList(list));
		return list;
	}
	/**采用点播素材*/
	public static ArrayList<Byte> trafficLightRed(){
		ArrayList<Byte> list = null;
		list = LedStringUtils.asArrayList(demandRed); 
		
		LedStringUtils.dumpMemory("yuyin", LedStringUtils.asByteList(list));
		return list;
	}
	
	// 点播类型
	public static byte[] demandRed = {
			(byte)0xfe,0x5c,0x4b,(byte) 0x89, // 前导标识
			0x20,0x00,0x00,0x00,//数据总长
			0x67,  // 消息类型             
			(byte) 0x99,0x43,(byte) 0x02,0x34,//消息id
			0x0d,0x00,0x00,0x00,//具体指令长度 
			0x01, (byte) 0xFE, 			//数据项数量及反码
			0x00, 			//更新时刻
			0x00, 0x00, 			//保留
			0x00, 			//区域号
			0x00, 0x00, 			//图片起始序号
			0x01, 0x00, 			//图片数量
			0x09, 0x00, (byte) 0xff, 		//移动方式
			(byte) 0xFF,(byte) 0xFF			//包尾
	};
	// 点播类型
		public static byte[] demandBlue = {
				(byte)0xfe,0x5c,0x4b,(byte) 0x89, // 前导标识
				0x20,0x00,0x00,0x00,//数据总长
				0x67,  // 消息类型             
				(byte) 0x99,0x43,(byte) 0x02,0x34,//消息id
				0x0d,0x00,0x00,0x00,//具体指令长度 
				0x01, (byte) 0xFE, 			//数据项数量及反码
				0x00, 			//更新时刻
				0x00, 0x00, 			//保留
				0x00, 			//区域号
				0x01, 0x00, 			//图片起始序号
				0x01, 0x00, 			//图片数量
				0x09, 0x00, (byte) 0xff, 		//移动方式
				(byte) 0xFF,(byte) 0xFF			//包尾
		};
}


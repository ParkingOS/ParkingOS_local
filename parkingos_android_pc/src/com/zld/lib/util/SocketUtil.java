package com.zld.lib.util;

import com.zld.bean.MyLedInfo;

/**
 * 开启连接道闸和LED屏的Socket
 * @param controlip 道闸ip
 * @param serial 道闸端口
 * @author HZC
 */
public class SocketUtil {

	private static final String TAG = "SocketUtil";

	public SocketUtil() {
		super();
	}

	/**
	 * 发送LED显示内容
	 * @param myLedInfo
	 * @param uid
	 * @param content
	 * @param cashOrderData
	 * @param playPeaker
	 */
	@SuppressWarnings("unused")
	public void sendLedData(MyLedInfo myLedInfo, final String uid,
			String content,String cashOrderData, boolean playPeaker) {
		try {
//			Log.e(TAG, "显示LEDInfo："+myLedInfo.toString()+"/n"+
//					"播报内容："+cashOrderData+"playPeaker");
			if(myLedInfo != null){
				byte[] speaker = null;
				String ledIp = myLedInfo.getLedip();
				String ledtypeface = myLedInfo.getTypeface();
				String ledtypesize = myLedInfo.getTypesize();
				String ledmovemode = myLedInfo.getMovemode();
				String ledshowcolor = myLedInfo.getShowcolor();
				int width = Integer.parseInt(myLedInfo.getWidth());
				int height = Integer.parseInt(myLedInfo.getHeight());
				int rsport = Integer.parseInt(myLedInfo.getRsport());
				if(ledshowcolor != null&&ledtypeface != null&&ledtypesize != null){
					byte[] cmd_one = null;
					if(null != uid&&uid.equals("42")||uid.equals("41")){
						cmd_one = LedStringUtils.asByteList(
								LedControl.changeshow(uid,content, width, 
										height,ledshowcolor,ledtypeface,ledtypesize,ledmovemode));
					}else{
						cmd_one = LedStringUtils.asByteList(
								LedControl.change(uid,content, width, 
										height,ledshowcolor,ledtypeface,ledtypesize,ledmovemode));
					}
					if (playPeaker == true){
						if (cashOrderData != null){
							speaker = LedStringUtils.asByteList(
									LedControl.getLedinstance().change(cashOrderData,rsport));
						}else{
							speaker = LedStringUtils.asByteList(
									LedControl.getLedinstance().change(content,rsport));
						}
						
						LedControl.getLedinstance().sendLedData(ledIp,speaker);
					} 
					LedControl.getLedinstance().sendLedData(ledIp,cmd_one);
					
				}
			}else {
				String ledIp = uid;
				byte[] cmd_one1 = LedStringUtils.asByteList(
						LedControl.trafficLightBlue());
				LedControl.getLedinstance().sendLedData(ledIp,cmd_one1);
				new Thread(new Runnable(){   
		            // 五秒后转为红灯
					    public void run(){   
					        try {
								Thread.sleep(5000);
								String ledIp = uid;
								byte[] cmd_one1 = LedStringUtils.asByteList(
										LedControl.trafficLightRed());
								LedControl.getLedinstance().sendLedData(ledIp,cmd_one1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}   
					    }   
					}).start();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

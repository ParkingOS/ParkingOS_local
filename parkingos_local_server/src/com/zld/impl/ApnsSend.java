package com.zld.impl;

import java.util.ArrayList;
import java.util.List;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;

public class ApnsSend
{/*
    public static void main(String[] args) throws Exception
    {
        String deviceToken = "b9ae03f915a1f74d2281bd81109b4e3fbd417a6cfbd4f579f892b37b0f25ed69";
        String alert = "我的push测试";//push的内容
        int badge = 1;//图标小红圈的数值
        String sound = "default";//铃音

        List<String> tokens = new ArrayList<String>();
        tokens.add(deviceToken);
        String certificatePath = "C:\\Users\\Administrator\\Desktop\\apns-dev-cert.p12";
        String certificatePassword = "tingchebao";//此处注意导出的证书密码不能为空因为空密码会报错
        boolean sendCount = true;

        try
        {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(alert); // 消息内容
            payLoad.addBadge(badge); // iphone应用图标上小红圈上的数值
            if (!StringUtils.isBlank(sound))
            {
                payLoad.addSound(sound);//铃音
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, true));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // 发送push消息
            if (sendCount)
            {
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                notifications.add(notification);
            }
            else
            {
                List<Device> device = new ArrayList<Device>();
                for (String token : tokens)
                {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            int successful = successfulNotifications.size();
            pushManager.stopConnection();
            System.out.println(failed+":"+successful);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    */
    
    public static void main(String[] args) throws Exception
    {
       /* String deviceToken = "b9ae03f915a1f74d2281bd81109b4e3fbd417a6cfbd4f579f892b37b0f25ed69";//iphone手机获取的token
        String alert = "我的push测试";//push的内容
        int badge = 100;//图标小红圈的数值
        String sound = "default";//铃音
        sendTiangouAPNS(deviceToken, alert, badge, sound, "10001");*/
        try {
            String deviceToken = "b9ae03f915a1f74d2281bd81109b4e3fbd417a6cfbd4f579f892b37b0f25ed69";
            //被推送的iphone应用程序标示符      
           // PropertyConfigurator.configure("bin/log4j.properties");
          //  Logger console = Logger.getLogger(ApnsSend.class);
           // String mesgString = "{\"mtype\":\"0\",\"msgid\":\"1\",\"info\":{\"total\":\"0.0\",\"parkname\":\"加密车场\",\"address\":\"北京市海淀区上地三街9号-d座\",\"etime\":\"1414218585\",\"state\":\"0\",\"btime\":\"1414218585\",\"parkid\":\"1475\",\"orderid\":\"176729\"}}";
    		
            PayLoad payLoad = new PayLoad();
            payLoad.addAlert("测试消息");
            payLoad.addBadge(1);
            payLoad.addSound("default");
            payLoad.addCustomDictionary("payload", "3339900");
            
            PushNotificationManager pushManager = PushNotificationManager.getInstance();
            String device = ""+System.currentTimeMillis();
			pushManager.addDevice(device, deviceToken);

            String host= "gateway.push.apple.com";  //苹果推送服务器
           // String host= "gateway.sandbox.push.apple.com";  //测试用的苹果推送服务器
            int port = 2195;
            
            String certificatePath = "C:\\Users\\Administrator\\Desktop\\apns-dev-cert.p12"; //刚才在mac系统下导出的证书
              
            String certificatePassword= "tingchebao";
            
            pushManager.initializeConnection(host, port, certificatePath,certificatePassword, SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);
              
            //Send Push
            Device client = pushManager.getDevice(device);
            pushManager.sendNotification(client, payLoad); 
            pushManager.stopConnection();
            pushManager.removeDevice(device);
            System.out.println("push succeed!");
        }
        catch (Exception e) {
            e.printStackTrace();
            System.out.println("e.getMessage() = " + e.getMessage());
        }
           
    }
    public static void sendTiangouAPNS(String deviceToken, String message, int badge, String sound, String ab)
    {
        List<String> tokens = new ArrayList<String>();
        tokens.add(deviceToken);
       // String certificatePath = "";
        String certificatePath = "C:\\Users\\Administrator\\Desktop\\apns-dev-cert.p12";
        String certificatePassword = "tingchebao";//此处注意导出的证书密码不能为空因为空密码会报错
      //  String certificatePassword = "123456";//此处注意导出的证书密码不能为空因为空密码会报错
       // new ApnsSend().sendpush(tokens, message, badge, sound, certificatePath, certificatePassword, true);
    }
    /**
     * apple的推送方法
     * @param tokens iphone手机获取的token
     * @param message 推送消息的内容
     * @param count 应用图标上小红圈上的数值
     * @param sound 声音
     * @param ab 系统
     * @param certificatePath 证书路径
     * @param certificatePassword 证书密码
     * @param sendCount 单发还是群发 true：单发 false：群发
     */
   /* private void sendpush(List<String> tokens, String message, int badge, String sound, String certificatePath, String certificatePassword, boolean sendCount)
    {
        try
        {
            PushNotificationPayload payLoad = new PushNotificationPayload();
            payLoad.addAlert(message); // 消息内容
            payLoad.addBadge(badge); // iphone应用图标上小红圈上的数值
            if (!StringUtils.isBlank(sound))
            {
                payLoad.addSound(sound);//铃音
            }
            PushNotificationManager pushManager = new PushNotificationManager();
            //true：表示的是产品发布推送服务 false：表示的是产品测试推送服务
            pushManager.initializeConnection(new AppleNotificationServerBasicImpl(certificatePath, certificatePassword, false));
            List<PushedNotification> notifications = new ArrayList<PushedNotification>();
            // 发送push消息
            if (sendCount)
            {
               // log.debug("--------------------------apple 推送 单-------");
                Device device = new BasicDevice();
                device.setToken(tokens.get(0));
                PushedNotification notification = pushManager.sendNotification(device, payLoad, true);
                notifications.add(notification);
            }
            else
            {
               // log.debug("--------------------------apple 推送 群-------");
                List<Device> device = new ArrayList<Device>();
                for (String token : tokens)
                {
                    device.add(new BasicDevice(token));
                }
                notifications = pushManager.sendNotifications(payLoad, device);
            }
            List<PushedNotification> failedNotifications = PushedNotification.findFailedNotifications(notifications);
            List<PushedNotification> successfulNotifications = PushedNotification.findSuccessfulNotifications(notifications);
            int failed = failedNotifications.size();
            int successful = successfulNotifications.size();
            // pushManager.stopConnection();
            System.out.println(failed+":"+successful);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }*/

}

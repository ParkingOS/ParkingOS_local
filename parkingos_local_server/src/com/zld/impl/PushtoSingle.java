package com.zld.impl;

import java.io.IOException;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.SingleMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.APNTemplate;
import com.gexin.rp.sdk.template.TransmissionTemplate;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.TimeTools;

/**
 * 推送消息工具(个推)
 * 
 * @author Administrator
 * 
 */

@Repository
public class PushtoSingle {
	@Autowired
	private DataBaseService daService;

	public static void main(String[] args) throws Exception {
		/*
		 * IGtPush push = new IGtPush(host, appkey, master); push.connect();
		 * 
		 * TransmissionTemplate template = TransmissionTemplateDemo();
		 * SingleMessage message = new SingleMessage();
		 * message.setOffline(true); //离线有效时间，单位为毫秒，可选
		 * message.setOfflineExpireTime(24 * 3600 * 1000);
		 * message.setData(template);
		 * 
		 * // List targets = new ArrayList(); Target target1 = new Target(); //
		 * Target target2 = new Target();
		 * 
		 * target1.setAppId(appId); target1.setClientId(CID);
		 * 
		 * IPushResult ret = push.pushMessageToSingle(message, target1);
		 * System.out.println(ret.getResponse().toString());
		 */
		// /////////ios
		// //////////ios

		// System.out.println(AjaxUtil.decodeUTF8("%E8%AE%A98%E7%82%B9%E8%A1%A560%25 "));
	}

	public static TransmissionTemplate TransmissionTemplateDemo() {
		return null;
	}

	// 发送消息android
	public String sendSingle(String cid, String mesg) {
		return null;
	}

	// 发送消息ios
	public String sendIOSmessage(Long uid, String cid, String mesg) {
		/*
		 * IGtPush p = new IGtPush(host, appkey, master); APNTemplate template =
		 * new APNTemplate(); template.setPushInfo("", 1, mesg, "defalut");
		 * 
		 * SingleMessage SingleMessage = new SingleMessage();
		 * SingleMessage.setData(template); //单推 IPushResult ret =
		 * p.pushAPNMessageToSingle(appId, cid, SingleMessage);
		 * System.err.println(ret.getResponse());
		 */

		return null;
	}

	// 向苹果消息服务器发消息
	public void sendMessageByApns(Long uid, String mesg, String cid) {
	}
}

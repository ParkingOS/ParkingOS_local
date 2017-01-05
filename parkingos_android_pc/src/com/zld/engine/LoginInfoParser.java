package com.zld.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.zld.bean.LoginInfo;

public class LoginInfoParser {

	public static LoginInfo getLoginInfo(InputStream is) throws Exception {
		System.out.println("¿ªÊ¼½âÎö.......");
		XmlPullParser parser =XmlPullParserFactory. newInstance().newPullParser();
		LoginInfo info = new LoginInfo();
		parser.setInput(is, "gb2312");
		int type = parser.getEventType();

		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("token".equals(parser.getName())) {
					String token = parser.nextText();
					info.setToken(token);
				} else if ("name".equals(parser.getName())) {
					String name = parser.nextText();
					info.setName(name);
				} else if ("info".equals(parser.getName())) {
					String infos = parser.nextText();
					info.setInfo(infos);
				} else if ("role".equals(parser.getName())) {
					String role = parser.nextText();
					info.setRole(role);
				} else if ("logontime".equals(parser.getName())) {
					String logontime = parser.nextText();
					info.setLogontime(logontime);
				}else if ("state".equals(parser.getName())) {
					String state = parser.nextText();
					info.setState(state);
				}
			}
			type = parser.next();
		}
		return info;
	}

}

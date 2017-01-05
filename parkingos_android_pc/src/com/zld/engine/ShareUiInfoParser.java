package com.zld.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.zld.bean.ShaerUiInfo;

//<?xml version="1.0" encoding="gb2312"?><content><total>26</total><free>17</free><busy>9</busy></content>
public class ShareUiInfoParser {
	public static ShaerUiInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = XmlPullParserFactory. newInstance().newPullParser();
		ShaerUiInfo info = new ShaerUiInfo();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();

		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if ("total".equals(parser.getName())) {
					String total = parser.nextText();
					info.setTotal(total);
				} else if ("free".equals(parser.getName())) {
					String free = parser.nextText();
					info.setFree(free);
				} else if ("busy".equals(parser.getName())) {
					String busy = parser.nextText();
					info.setBusy(busy);
				}else if ("result".equals(parser.getName())) {
					String result = parser.nextText();
					info.setResult(result);
				}
				break;
			}

			type = parser.next();
		}
		return info;
	}
}

package com.zld.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import com.zld.bean.UpdataInfo;



public class UpdataInfoParser {

	/**
	 * 
	 * @param is
	 *            ½âÎöµÄxmlµÄinputstream
	 * @return updateinfo
	 */
	public static UpdataInfo getUpdataInfo(InputStream is) throws Exception {
		XmlPullParser parser = XmlPullParserFactory. newInstance().newPullParser();
		UpdataInfo info = new UpdataInfo();
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();

		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					String version = parser.nextText();
					info.setVersion(version);
				}else if("description".equals(parser.getName())){
					String description = parser.nextText();
					info.setDescription(description);
				}else if("apkurl".equals(parser.getName())){
					String apkurl = parser.nextText();
					info.setApkurl(apkurl);
				}else if("versionbeta".equals(parser.getName())){
					String versionBeta = parser.nextText();
					info.setVersionBeta(versionBeta);
				}else if("descriptionbeta".equals(parser.getName())){
					String descriptionBeta = parser.nextText();
					info.setDescriptionBeta(descriptionBeta);
				}else if("apkurlbeta".equals(parser.getName())){
					String apkurlBeta = parser.nextText();
					info.setApkurlBeta(apkurlBeta);
				}
				break;

			}

			type = parser.next();
		}
		return info;
	}

}

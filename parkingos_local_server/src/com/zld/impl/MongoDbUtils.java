package com.zld.impl;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * ²Ù×÷MongoDb
 * @author Laoyao
 * @date 20131025
 */
@Service
public class MongoDbUtils {
	
	private Logger logger = Logger.getLogger(MongoDbUtils.class);
	
	public List<String> getParkPicUrls(Long uin){
		List<String> result =new ArrayList<String>();
		DB db = MongoClientFactory.getInstance().getMongoDBBuilder("zld");//
		DBCollection mdb = db.getCollection("parkuser_pics");
		DBCursor dbCursor = mdb.find(new BasicDBObject("uin", uin), new BasicDBObject("filename", true));
		while(dbCursor.hasNext()){
			DBObject dbObject = dbCursor.next();
			result.add(dbObject.get("filename")+"");
		}
		return result;
	}

	public byte[] getParkPic(String id,String dbName){
		DB db = MongoClientFactory.getInstance().getMongoDBBuilder("zld");//
		DBCollection collection = db.getCollection(dbName);
		BasicDBObject document = new BasicDBObject();
		document.put("filename", id);
		//document.put("uin", uin);
		DBObject obj = collection.findOne(document);
		if(obj == null){
			db = MongoDBFactory.getInstance().getMongoDBBuilder("zld");//
			collection = db.getCollection(dbName);
			document = new BasicDBObject();
			document.put("filename", id);
			//document.put("uin", uin);
			obj = collection.findOne(document);
		}
		if(obj == null){
			return null;
		}
		db.requestDone();
		return (byte[])obj.get("content");
	}
}

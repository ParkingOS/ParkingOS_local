package com.zld.impl;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.mongodb.DB;
import com.mongodb.MongoClient;
import com.mongodb.MongoOptions;
import com.mongodb.ReadPreference;
import com.mongodb.ServerAddress;
import com.zld.CustomDefind;


/**mongodb≈‰÷√**/
public class MongoClientFactory {
	
	//private Logger logger = Logger.getLogger(MongoClientFactory.class);

	private HashMap<String, DB> mongodbmap = new HashMap<String, DB>();
	private MongoClientFactory() {
	}

	public static MongoClientFactory getInstance() {
		return MongoClientFactoryHolder.INSTANCE;
	}

	private static class MongoClientFactoryHolder {

		private static final MongoClientFactory INSTANCE = new MongoClientFactory();
	}

	private static MongoClient mongoClient = null;
	static {
		try {
			mongoClient = new MongoClient(setAddresses());
			mongoClient.setReadPreference(ReadPreference.secondaryPreferred());
			MongoOptions mongoOptions = mongoClient.getMongoOptions();
			mongoOptions.setConnectionsPerHost(500);
			mongoOptions.setConnectTimeout(3000);
			mongoOptions.setThreadsAllowedToBlockForConnectionMultiplier(2);
			System.out.println("mongodb init over...");
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}

	public DB getMongoDBBuilder(String dbName) {
		DB db = null;
		if(dbName !=null && !"".equals(dbName)) {
			db  = mongodbmap.get(dbName);
			if(db == null) {
				db = buildMongoDb(dbName);
			}
		}
		return db;
	}

	private synchronized DB buildMongoDb(String dbName) {
		DB db = mongodbmap.get(dbName);
		if(db == null) {
			db = mongoClient.getDB(dbName);
			mongodbmap.put(dbName, db);
		}
		return db;
	}
	
	static List<ServerAddress> setAddresses(){
		List<ServerAddress> list=new ArrayList<ServerAddress>();
		String address = CustomDefind.MONGOADDRESS;
		System.err.println(">>>>>>>>>>init mongodb:"+address);
		try {
			if(address!=null){
				String [] adds = address.split(",");
				for(String add: adds){
					list.add(new ServerAddress(add.split(":")[0],Integer.parseInt(add.split(":")[1])));
				}
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return list;
	}
}

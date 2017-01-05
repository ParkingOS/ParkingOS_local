package com.zld.schedule;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.sun.management.OperatingSystemMXBean;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.HttpProxy;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class SyncFromLineSchedule implements Runnable {
	
	DataBaseService dataBaseService;
	
	public SyncFromLineSchedule(DataBaseService dataBaseService ){
		this.dataBaseService = dataBaseService;
	}
	ExecutorService executor =Executors.newFixedThreadPool(7);//newSingleThreadExecutor();

	private static Logger log = Logger.getLogger(SyncFromLineSchedule.class);
	
	public void run() {
		log.info("***********==========开始本地同步任务"+TimeTools.gettime()+"=============*********************");
		FutureTask<String> future1 = null;
		FutureTask<String> future2 = null;
		FutureTask<String> future3 = null;
		FutureTask<String> future4 = null;
		FutureTask<String> future5 = null;
		FutureTask<String> future6 = null;
		try {
			//获取token 
			String token = "";
			Map map = dataBaseService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
			if(map!=null&&map.get("token")!=null){
				token = map.get("token")+"";
			}
//			if(token==null){
//				HttpProxy httpProxy = new HttpProxy();
//		   	 	//获取token
//				String mes = AESEncryptor.encrypt("0123456789ABCDEFtingcaidfjalsjffdaslfkdafjdaljdf", CustomDefind.SECRET+":"+CustomDefind.COMID);
//		   	 	token = httpProxy.doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getToken&mes="+mes);
////		   	 	token = httpProxy.doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getToken&appid="+CustomDefind.COMID+"&secret"+CustomDefind.SECRET);
//		   	 	if(token!=null&&!token.equals("")){
//					int r = dataBaseService.update("update sync_time_tb set token=? ", new Object[]{token});
//				}
//			}
			final String tk = token;
			
			future1 = new FutureTask<String>(new Callable<String>() {//创建一个future任务用于去同步线上车场的修改信息
					public String call() throws Exception {
						Map map = dataBaseService.getMap("select * from sync_time_tb where id = ?", new Object[]{1});//id为1的是同步车场相关信息的最大id
						Long maxid = 0L;
//						Long curTime = System.currentTimeMillis()/1000;
						if(map!=null&&map.get("maxid")!=null){
							maxid = Long.valueOf(map.get("maxid")+"");
						}else{
							dataBaseService.update("insert into sync_time_tb values(?,?)", new Object[]{1,0});
						}
						File file = new File(CustomDefind.TOMCAT);  
				        long totalSpace = file.getTotalSpace();  
				        long freeSpace = file.getFreeSpace();  
				        long usedSpace = totalSpace - freeSpace;  
				  
				        long total = (totalSpace / 1024 / 1024 / 1024); 
						long use = (usedSpace / 1024 / 1024 / 1024);
						double cpu  = 0.0;
						String mes = null;
						String lineTxt = "10";
						InputStreamReader read = null;
						File version = new File(CustomDefind.TOMCATHOMT+"version\\version.txt");
			   	 		if(version.isFile() && version.exists()){ //判断文件是否存在
			   	 			read = new InputStreamReader(
				            new FileInputStream(version));//考虑到编码格式
				            BufferedReader bufferedReader = new BufferedReader(read);
				            lineTxt = bufferedReader.readLine();
				            read.close();
			   	 		}
						try{
							mes = getEMS();
							Runtime rt=Runtime.getRuntime();
							cpu = getCpuRatioForWindows();
						}catch (Exception e) {
							log.info("cpu获取失败");
						}finally{
							if(read!=null)
								read.close();
						}
//						File version = new File("c:\\tomcat\\version\\version.txt");
//			   	 		if(version.isFile() && version.exists()){ //判断文件是否存在
//			   	 			read = new InputStreamReader(
//				            new FileInputStream(version));//考虑到编码格式
//				            BufferedReader bufferedReader = new BufferedReader(read);
//				            lineTxt = bufferedReader.readLine();
//				            read.close();
//			   	 		}
						//请求获取线上更改的信息
						log.info("syncinfopool>>"+CustomDefind.DOMAIN+"/syncInter.do?action=syncinfopool&comid="+CustomDefind.COMID+"&maxid="+maxid+"&harddisk="+use+"/"+total+"&cpu="+cpu+"&memory="+mes+"&version="+lineTxt+"&token="+tk);
						String result = new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=syncinfopool&comid="+CustomDefind.COMID+"&maxid="+maxid+"&harddisk="+use+"/"+total+"&cpu="+cpu+"&memory="+mes+"&version="+lineTxt+"&token="+tk);
						log.info("syncinfopool>>"+result);
						JSONObject jo = null;
						if(result!=null&&result.length()>1){
							jo  = JSONObject.fromObject(result);
							log.info("syncinfopool>>"+jo);
						}
						if(jo!=null&&!jo.isEmpty()){
							try {
								Iterator it = jo.keySet().iterator();//keys();  
					            while (it.hasNext()) {  
					                String key = (String) it.next();  
					                if(!"maxid".equals(key)){
					                	 String value = jo.getString(key);  
							             syncUtil(value, key);
					                }
					            }
					            if(jo.containsKey("maxid")&&jo.get("maxid")!=null){
									maxid = Long.valueOf(jo.get("maxid")+"");
								}
								if(maxid>0)
									dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{maxid,1});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						return result;
//						try {
//							if(jo.containsKey("com_led_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("com_led_tb");
//									syncUtil("com_led_tb", ja, new String[]{"id","ledip","ledport","leduid","movemode","movespeed","dwelltime","ledcolor","showcolor","typeface","typesize","matercont","passid","width","height","type","rsport"},new int[]{4,12,12,12,4,4,4,4,4,4,4,12,4,4,4,4,4});
//									
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("com_led_tb");
//									syncUtil("com_led_tb", ja, new String[]{"id","ledip","ledport","leduid","movemode","movespeed","dwelltime","ledcolor","showcolor","typeface","typesize","matercont","passid","width","height","type","rsport"},new int[]{4,12,12,12,4,4,4,4,4,4,4,12,4,4,4,4,4});
//									
//								}
//							}if(jo.containsKey("com_pass_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("com_pass_tb");
//									syncUtil("com_pass_tb", ja, new String[]{"id","worksite_id","passname","passtype","description","comid"},new int[]{4,4,12,12,12,4,});
//									
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("com_pass_tb");
//									syncUtil("com_pass_tb", ja, new String[]{"id","worksite_id","passname","passtype","description","comid"},new int[]{4,4,12,12,12,4,});
//									
//								}
//							}if(jo.containsKey("com_worksite_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("com_worksite_tb");
//									syncUtil("com_worksite_tb", ja, new String[]{"id","comid","worksite_name","description","net_type"},new int[]{4,4,12,12,4,});
//									
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("com_worksite_tb");
//									syncUtil("com_worksite_tb", ja, new String[]{"id","comid","worksite_name","description","net_type"},new int[]{4,4,12,12,4,});
//									
//								}
//							}if(jo.containsKey("price_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("price_tb");
//									syncUtil("price_tb", ja, new String[]{"id","comid","price","state","unit","pay_type","create_time","b_time","e_time","is_sale",
//											"first_times","fprice","countless","free_time","fpay_type","isnight","isedit","car_type","is_fulldaytime","total24"},
//											new int[]{4,4,3,4,4,4,4,4,4,4,4,3,4,4,4,4,4,4,4,3});
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("price_tb");
//									syncUtil("price_tb", ja, new String[]{"id","comid","price","state","unit","pay_type","create_time","b_time","e_time","is_sale",
//											"first_times","fprice","countless","free_time","fpay_type","isnight","isedit","car_type","is_fulldaytime"},
//											new int[]{4,4,3,4,4,4,4,4,4,4,4,3,4,4,4,4,4,4,4});
//								}
//								
//							}if(jo.containsKey("product_package_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("product_package_tb");
//									syncUtil("product_package_tb", ja, new String[]{"id","p_name","b_time","e_time","remain_number",
//											"state","comid","price","bmin","emin","resume","old_price","type","reserved","limitday"},
//											new int[]{4,12,4,4,3,4,4,3,4,4,12,3,4,4,4});
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("product_package_tb");
//									syncUtil("product_package_tb", ja, new String[]{"id","p_name","b_time","e_time","remain_number",
//											"state","comid","price","bmin","emin","resume","old_price","type","reserved","limitday"},
//											new int[]{4,12,4,4,3,4,4,3,4,4,12,3,4,4,4});
//								}
//								
//							}if(jo.containsKey("com_info_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("com_info_tb");
//									syncUtil("com_info_tb", ja, new String[]{"id","company_name","phone","fax","address","zipcode","homepage","resume","create_time","longitude",
//											"latitude","parking_type","parking_total","share_number","auto_order","mobile","total_money","money",
//											"property","type","stop_type","update_time","state","biz_id","uid","city","recom_code","nfc","etc","book","navi",
//											"monthlypay","isnight","isfixed","iscancel","firstprovince","mcompany","record_number","epay","is_hasparker","isview",
//											"remarks","invalid_order","fixed_pass_time","car_type","passfree","activity","activity_content","upload_uin","minprice_unit",
//											"isshowepay","ishidehdbutton","entry_set","entry_month2_set"},
//											new int[]{4,12,12,12,12,12,12,12,4,3,3,4,4,4,4,12,3,3,12,4,4,
//											4,4,4,4,4,4,4,4,4,4,4,4,4,4,12,12,12,4,4,4,12,4,4,4,4,4,12,4,3,4,4,3,3});
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja= jo.getJSONArray("com_info_tb");
//									syncUtil("com_info_tb", ja, new String[]{"id","company_name","phone","fax","address","zipcode","homepage","resume","create_time","longitude",
//											"latitude","parking_type","parking_total","share_number","auto_order","mobile","total_money","money",
//											"property","type","stop_type","update_time","state","biz_id","uid","city","recom_code","nfc","etc","book","navi",
//											"monthlypay","isnight","isfixed","iscancel","firstprovince","mcompany","record_number","epay","is_hasparker","isview",
//											"remarks","invalid_order","fixed_pass_time","car_type","passfree","activity","activity_content","upload_uin","minprice_unit",
//											"isshowepay","ishidehdbutton","entry_set","entry_month2_set"},
//											new int[]{4,12,12,12,12,12,12,12,4,3,3,4,4,4,4,12,3,3,12,4,4,
//											4,4,4,4,4,4,4,4,4,4,4,4,4,4,12,12,12,4,4,4,12,4,4,4,4,4,12,4,3,4,4,3,3});
//								}
//							}if(jo.containsKey("com_camera_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("com_camera_tb");
//									syncUtil("com_camera_tb", ja, new String[]{"id","camera_name","ip","port","cusername","cpassword","manufacturer","passid"},
//											new int[]{4,12,12,12,12,12,12,4});
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("com_camera_tb");
//									syncUtil("com_camera_tb", ja, new String[]{"id","camera_name","ip","port","cusername","cpassword","manufacturer","passid"},
//											new int[]{4,12,12,12,12,12,12,4});
//								}
//								
//							}if(jo.containsKey("carower_product")){
//
//								try{
//									JSONObject ja = jo.getJSONObject("carower_product");
//									syncUtil("carower_product", ja, new String[]{"id","pid","uin","create_time","b_time","e_time","total","remark","name","address"},
//											new int[]{4,4,4,4,4,4,3,12,12,12});
//								}catch (Exception e) {
////									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("carower_product");
//									syncUtil("carower_product", ja, new String[]{"id","pid","uin","create_time","b_time","e_time","total","remark","name","address"},
//											new int[]{4,4,4,4,4,4,3,12,12,12});
//								}
//								
//							}if(jo.containsKey("user_info_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("user_info_tb");
//									syncUtil("user_info_tb", ja, new String[]{"id","nickname","password","strid","email","phone","mobile","address",
//											"resume",
//											"reg_time","comid","auth_flag","balance","state",
//											"md5pass","cid","media","isview","collector_pics","imei","client_type","version","wxp_openid","wx_name","wx_imgurl"},
//											//"shop_id","credit_limit","is_auth","reward_score","firstorderquota","rewardquota","recommendquota","ticketquota"},
//											new int[]{4,12,12,12,12,12,12,12,12,4,4,4,3,4,12,12,
//											4,4,4,12,4,12,12,12,12});
//								}catch (Exception e) {
//									JSONArray ja = jo.getJSONArray("user_info_tb");
//									syncUtil("user_info_tb", ja, new String[]{"id","nickname","password","strid","email","phone","mobile","address",
//											"resume",
//											"reg_time","comid","auth_flag","balance","state",
//											"md5pass","cid","media","isview","collector_pics","imei","client_type","version","wxp_openid","wx_name","wx_imgurl"},
//											//"shop_id","credit_limit","is_auth","reward_score","firstorderquota","rewardquota","recommendquota","ticketquota"},
//											new int[]{4,12,12,12,12,12,12,12,12,4,4,4,3,4,12,12,
//											4,4,4,12,4,12,12,12,12});
//								}
//								
//
//							}if(jo.containsKey("car_info_tb")){
//
//								try{
//									JSONObject ja = jo.getJSONObject("car_info_tb");
//									syncUtil("car_info_tb", ja, new String[]{"id","uin","car_number","state","create_time","is_auth","is_comuse","pic_url1",
//											"pic_url2","remark"},
//											new int[]{4,4,12,4,4,4,4,12,12,12});
//								}catch (Exception e) {
//									JSONArray ja = jo.getJSONArray("car_info_tb");
//									syncUtil("car_info_tb", ja, new String[]{"id","uin","car_number","state","create_time","is_auth","is_comuse","pic_url1",
//											"pic_url2","remark"},
//											new int[]{4,4,12,4,4,4,4,12,12,12});
//								}
//							}if(jo.containsKey("price_assist_tb")){
//
//								try{
//									JSONObject ja = jo.getJSONObject("price_assist_tb");
//									syncUtil("price_assist_tb", ja, new String[]{"id","comid","type","assist_unit","assist_price"},
//											new int[]{4,4,4,4,3});
//								}catch (Exception e) {
//									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("price_assist_tb");
//									syncUtil("price_assist_tb", ja, new String[]{"id","comid","type","assist_unit","assist_price"},
//											new int[]{4,4,4,4,3});
//								}
//								
//							}if(jo.containsKey("car_type_tb")){
//
//								try{
//									JSONObject ja = jo.getJSONObject("car_type_tb");
//									syncUtil("car_type_tb", ja, new String[]{"id","comid","name","sort"},
//											new int[]{4,4,12,4});
//								}catch (Exception e) {
//									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("car_type_tb");
//									syncUtil("car_type_tb", ja, new String[]{"id","comid","name","sort"},
//											new int[]{4,4,12,4});
//								}
//								
//							}
//							if(jo.containsKey("free_reasons_tb")){
//								try{
//									JSONObject ja = jo.getJSONObject("free_reasons_tb");
//									syncUtil("free_reasons_tb", ja, new String[]{"id","comid","name","sort"},
//											new int[]{4,4,12,4});
//								}catch (Exception e) {
//									e.printStackTrace();
//									JSONArray ja = jo.getJSONArray("free_reasons_tb");
//									syncUtil("free_reasons_tb", ja, new String[]{"id","comid","name","sort"},
//											new int[]{4,4,12,4});
//								}
//								
//							}
//							return null;
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
						
					}
			    });
			future2 = new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					String result = new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=syncprepay&comid="+CustomDefind.COMID+"&token="+tk);
					log.info("syncprepay result:"+result);
					if(result!=null&&result.startsWith("[")&&result.length()>2){
						JSONArray ja = JSONArray.fromObject(result);
						for (Object object : ja) {
							Map map = (Map)object;
							int r = dataBaseService.update("update order_tb set uin = ? ,total=? ,pay_type=?,pre_state=? where line_id =? ",new Object[]{Long.parseLong(map.get("uin")+""),
									Double.parseDouble(map.get("total")+""),Integer.parseInt(map.get("pay_type")+""),1,Long.parseLong(map.get("id")+"")});
							if(r!=1){
								JSONObject jo = JSONObject.fromObject(object);
								String ret = addOrder(jo);
								if(ret.startsWith("1")&&ret.split("_").length==2){
									URL url = new URL(CustomDefind.DOMAIN+"/carpicsup.do"+ "?" + "action=downloadpic&comid="+CustomDefind.COMID+"&orderid="+jo.getLong("id")+"&type=0"+"&token="+tk);
					    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					    			conn.setReadTimeout(4*1000);
					    			conn.setConnectTimeout(4*1000);
					    			InputStream inputStream = conn.getInputStream();
	//				    			System.out.println(conn.getResponseMessage());//ok
					    			String f = CustomDefind.PIC+getCollectionName(jo.getLong("create_time")*1000);
					    	 	    File file = new File(f);
					    	 	    if(!file.exists()){
					    	 	    	file.mkdirs();
					    	 	    }
					    	 	    String picurl = "\\"+ret.split("_")[1] + 0 + ".jpg";
					    	 	    String fileName = f+picurl;
					    	 	    inputstreamtofile(inputStream, new File(fileName));
								}
							}
							new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=completesyncprepay&orderid="+map.get("id")+"&token="+tk);
						}
					}
					return null;
				}
			});
			future3 = new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					Map m = dataBaseService.getMap("select * from sync_time_tb where id = ?", new Object[]{2});
					Long maxid = 0L;
//					Long curTime = System.currentTimeMillis()/1000;
					if(m!=null&&m.get("maxid")!=null){
						maxid = Long.valueOf(m.get("maxid")+"");
					}else{
						dataBaseService.update("insert into sync_time_tb values(?,?)", new Object[]{2,0});
					}
					String result = new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=syncswitchorder&comid="+CustomDefind.COMID+"&maxid="+maxid+"&token="+tk);
					log.info("syncswitchorder result:"+result);
					if(result!=null&&result.startsWith("[")&&result.length()>2){
						JSONArray ja = JSONArray.fromObject(result);
						for (Object object : ja) {
							JSONObject jo = JSONObject.fromObject(object);
							String r = addOrder(jo);
							if(r.startsWith("1")){
								long curid = jo.getLong("id");
								dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{curid,2});
								if(r.split("_").length==2){
									long dur = System.currentTimeMillis()/1000-jo.getLong("create_time");
									if(dur<=3*24*3600){
										log.info("begin download pic  id:"+r.split("_")[1]);
//										String ret = new HttpProxy().doGet(Constants.Domain+"/carpicsup.do?action=downloadpic&comid="+CustomDefind.COMID+"&orderid="+jo.getLong("id")+"&type=0");
										URL url = new URL(CustomDefind.DOMAIN+"/carpicsup.do"+ "?" + "action=downloadpic&comid="+CustomDefind.COMID+"&orderid="+jo.getLong("id")+"&type=0"+"&token="+tk);
										HttpURLConnection conn = (HttpURLConnection) url.openConnection();
										conn.setReadTimeout(4*1000);
										conn.setConnectTimeout(4*1000);
										InputStream inputStream = conn.getInputStream();
//					    				System.out.println(conn.getResponseMessage());//ok
										String f = CustomDefind.PIC+getCollectionName(jo.getLong("create_time")*1000);
										File file = new File(f);
										if(!file.exists()){
											file.mkdirs();
										}
										String picurl = "\\"+r.split("_")[1] + 0 + ".jpg";
										String fileName = f+picurl;
										inputstreamtofile(inputStream, new File(fileName));
									}
								}
							}
							
						}
					}
					return null;
				}
			});
			future4 = new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					Map m = dataBaseService.getMap("select * from sync_time_tb where id = ?", new Object[]{3});
					Long maxid = 0L;
//					Long curTime = System.currentTimeMillis()/1000;
					if(m!=null&&m.get("maxid")!=null){//maxid为时间
						maxid = Long.valueOf(m.get("maxid")+"");
					}else{
						dataBaseService.update("insert into sync_time_tb values(?,?)", new Object[]{3,maxid});
					}
					String result = new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=synclinecomplete&comid="+CustomDefind.COMID+"&maxtime="+maxid+"&token="+tk);
					log.info("synclinecomplete result:"+result);
					if(result!=null&&result.startsWith("[")&&result.length()>2){
						JSONArray ja = JSONArray.fromObject(result);
						for (Object object : ja) {
							JSONObject jo = JSONObject.fromObject(object);
							int r = completeOrder(jo);
//							if(r==1){
								long end_time = jo.getLong("end_time");
								dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{end_time,3});
//							}
							
						}
					}
					return null;
				}
			});
			future5 = new FutureTask<String>(new Callable<String>() {//同步减免券信息

				public String call() throws Exception {
					String result = new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=syncTicket&comid="+CustomDefind.COMID+"&token="+tk);
					log.info("syncTicket result:"+result);
					if(result!=null&&result.startsWith("[")&&result.length()>2){
						JSONArray ja = JSONArray.fromObject(result);
						for (int i = 0; i < ja.size(); i++) {
							JSONObject jo =  ja.getJSONObject(i);
							ArrayList parm = new ArrayList();
							parm.add(jo.getLong("id"));
							Long count = dataBaseService.getCount("select count(lineid) from ticket_tb where lineid=?",parm);
							if(count==null || count<1){
								StringBuffer insertsql = new StringBuffer();
								StringBuffer valuesql = new StringBuffer();
								ArrayList list = new ArrayList();
								insertsql.append("insert into ticket_tb(");
								valuesql.append(" values (");
								if(jo.get("create_time")!=null&&!"null".equals(jo.getString("create_time"))){
									insertsql.append("create_time,");
									valuesql.append("?,");
									list.add(jo.getLong("create_time"));
								}
								if(jo.get("limit_day")!=null&&!"null".equals(jo.getString("limit_day"))){
									insertsql.append("limit_day,");
									valuesql.append("?,");
									list.add(jo.getLong("limit_day"));
								}
								if(jo.get("money")!=null&&!"null".equals(jo.getString("money"))){
									insertsql.append("money,");
									valuesql.append("?,");
									list.add(jo.getLong("money"));
								}
								if(jo.get("state")!=null&&!"null".equals(jo.getString("state"))){
									insertsql.append("state,");
									valuesql.append("?,");
									list.add(jo.getLong("state"));
								}
								if(jo.get("uin")!=null&&!"null".equals(jo.getString("uin"))){
									insertsql.append("uin,");
									valuesql.append("?,");
									list.add(jo.getLong("uin"));
								}
								if(jo.get("comid")!=null&&!"null".equals(jo.getString("comid"))){
									insertsql.append("comid,");
									valuesql.append("?,");
									list.add(jo.getLong("comid"));
								}
								if(jo.get("utime")!=null&&!"null".equals(jo.getString("utime"))){
									insertsql.append("utime,");
									valuesql.append("?,");
									list.add(jo.getLong("utime"));
								}
								if(jo.get("umoney")!=null&&!"null".equals(jo.getString("umoney"))){
									insertsql.append("umoney,");
									valuesql.append("?,");
									list.add(jo.getDouble("umoney"));
								}
								if(jo.get("type")!=null&&!"null".equals(jo.getString("type"))){
									insertsql.append("type,");
									valuesql.append("?,");
									list.add(jo.getLong("type"));
								}
								if(jo.get("orderid")!=null&&!"null".equals(jo.getString("orderid"))){
									insertsql.append("lineorderid,");
									valuesql.append("?,");
									list.add(jo.getLong("orderid"));
									Map map = dataBaseService.getMap("select id from order_tb where line_id=?", new Object[]{jo.getLong("orderid")});
									if(map!=null){
										insertsql.append("orderid,");
										valuesql.append("?,");
										list.add(Long.valueOf(map.get("id")+""));
									}
								}
								if(jo.get("bmoney")!=null&&!"null".equals(jo.getString("bmoney"))){
									insertsql.append("bmoney,");
									valuesql.append("?,");
									list.add(jo.getDouble("bmoney"));
								}
								if(jo.get("id")!=null&&!"null".equals(jo.getString("id"))){
									insertsql.append("lineid,");
									valuesql.append("?,");
									list.add(jo.getDouble("id"));
								}
								insertsql.append("sync_state,");
								valuesql.append("?,");
								list.add(0);
								String sql = insertsql.substring(0, insertsql.length()-1)+")"+valuesql.substring(0, valuesql.length()-1)+")";
								int r = dataBaseService.update(sql, list);
								if(r==1){
									new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=completesyncticket&ticketid="+jo.getLong("id")+"&token="+tk);
								}
							}else{
								new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=completesyncticket&ticketid="+jo.getLong("id")+"&token="+tk);
							}
						}
					}
					return null;
				}
			});
			future6 = new FutureTask<String>(new Callable<String>() {
				public String call() throws Exception {
					String result = new HttpProxy().doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getsubstation&comid="+CustomDefind.COMID+"&token="+tk);
					log.info("getsubstation result:"+result);
					if(result!=null&&result.length()>0){
						Long count = Long.parseLong(result);
						dataBaseService.update("update com_info_tb set substation = ? where id = ? ", new Object[]{count,Long.parseLong(CustomDefind.COMID+"")});
					}
					return null;
				}
			});
			executor.submit(future1);
			executor.submit(future2);
			executor.submit(future3);
			executor.submit(future4);
			executor.submit(future5);
			executor.submit(future6);
			Thread.sleep(20000);
			} catch (Exception e1) {
				log.info(e1.getMessage());
			}finally{
				if(future1!=null)
					future1.cancel(true);
				if(future2!=null)
					future2.cancel(true);
				if(future3!=null)
					future3.cancel(true);
				if(future4!=null)
					future4.cancel(true);
				if(future5!=null)
					future5.cancel(true);
				if(future6!=null)
					future6.cancel(true);
//				executor.shutdownNow();
			}
	}
//	public static void main(String[] args) {
//		String result = new HttpProxy().doGet(Constants.Domain+"/syncInter.do?action=syncinfopool&comid=1749");
//		log.info(result);
//		JSONObject jo  = JSONObject.fromObject(result);
//		if(jo.containsKey("com_led_tb")){
//			JSONArray ja= jo.getJSONArray("com_led_tb");
////			syncUtil("com_led_tb", ja, new String[]{"id","ledip","ledport","leduid","movemode","movespeed","dwelltime","ledcolor","showcolor","typeface","typesize","matercont","passid"},new int[]{4,12,12,12,4,4,4,4,4,4,4,12,4});
//		}
//		System.out.println(jo);
//	}
	public void sync2Local(){
		
	}

	private String addOrder(JSONObject jo) {
		log.info("begin add order"+jo);
		 Long nextid = dataBaseService.getLong(
					"SELECT nextval('seq_order_tb'::REGCLASS) AS newid", null);
//		 state,end_time,auto_pay,pay_type,nfc_uuid" +
//	 		",c_type,uid,car_number,imei,pid,car_type,pre_state,in_passid,out_passid)"
		 StringBuffer insertsql = new StringBuffer("insert into order_tb(id,comid,uin,");//order by o.end_time desc
		 StringBuffer valuesql = new StringBuffer("?,?,?,?,?,?,?,?,");
		 ArrayList list = new ArrayList();
		 list.add(nextid);
		 list.add(jo.getLong("comid"));
		 Long uin = jo.getLong("uin");
//		 list.add(jo.getLong("uin"));
		 Long createtime = null;
		 String carnumber = null;
		 if(!"null".equals(jo.getString("create_time"))){
			 valuesql.append("?,");
			 insertsql.append("create_time,");
			 createtime = jo.getLong("create_time");
			 list.add(createtime);
		 }
		 if(!"null".equals(jo.getString("car_number"))){
			 valuesql.append("?,");
			 insertsql.append("car_number,");
			 carnumber = jo.getString("car_number");
			 list.add(carnumber);
		 }
		 if(createtime!=null&&carnumber!=null){
			int ret = dataBaseService.update("update order_tb set end_time=?,total=?,state=?,pay_type=? where car_number =? and state=? and create_time<? ", new Object[]{System.currentTimeMillis() / 1000, 0d, 1, 1, carnumber, 0,createtime });
			log.info("0元结算订单ret:"+ret);
			List lists =  dataBaseService.getAll("select * from order_tb where car_number=? and create_time = ? ", new Object[]{carnumber,createtime});
			System.out.println("list.size:"+lists.size());
			if(lists!=null&& lists.size()>0){//如果线上服务器和本地上传的订单时间车牌相同则本地0元结算
				return "1";
			}
		 }
		 list.add(2,uin);
		 if(!"null".equals(jo.getString("total"))&&!"价格未知".equals(jo.getString("total"))){
			 valuesql.append("?,");
			 insertsql.append("total,");
			 list.add(jo.getDouble("total"));
		 }
		 insertsql.append("state,");
		 list.add(jo.getLong("state"));
		 if(!"null".equals(jo.getString("end_time"))&&!"0".equals(jo.getString("end_time"))){
			 valuesql.append("?,");
			 insertsql.append("end_time,");
			 list.add(jo.getLong("end_time"));
		 }
		 insertsql.append("auto_pay,");
		 list.add(jo.getLong("auto_pay"));
		 insertsql.append("pay_type,");
		 list.add(jo.getLong("pay_type"));
		 if(!"null".equals(jo.getString("nfc_uuid"))){
			 valuesql.append("?,");
			 insertsql.append("nfc_uuid,");
			 list.add(jo.getString("nfc_uuid"));
		 }
		 insertsql.append("c_type,");
		 list.add(jo.getLong("c_type"));
		 insertsql.append("uid,");
		 list.add(jo.getLong("uid"));
		 if(!"null".equals(jo.getString("imei"))){
			 insertsql.append("imei,");
			 valuesql.append("?,");
			 list.add(jo.getString("imei"));
		 }
		 insertsql.append("pid,");
		 insertsql.append("car_type,");
		 insertsql.append("pre_state,");
		 insertsql.append("in_passid,");
		 valuesql.append("?,?,?,?,?,?,?,?,?");
//		 sync_state integer DEFAULT 0, -- 0:等待上传，1:生成上传完成，2:结算上传完成
//		  line_id bigint, -- 线上id
//		  uploadinpic_state integer DEFAULT 0, -- 0：待上传   1:上传成功
		 insertsql.append("out_passid,type,sync_state,line_id,uploadinpic_state) values ("+valuesql+")");
		 list.add(jo.getLong("pid"));
		 list.add(jo.getLong("car_type"));
		 list.add(jo.getLong("pre_state"));
		 list.add(jo.getString("in_passid").equals("null")?-1:jo.getLong("in_passid"));
		 list.add(jo.getString("out_passid").equals("null")?-1:jo.getLong("out_passid"));
		 list.add(3);//type   本地化订单
		 list.add(1);
		 list.add(jo.getLong("id"));
		 list.add(1);
		 System.out.println(insertsql.toString()+":"+list.toArray().toString());
		 int insert = dataBaseService.update(insertsql.toString(), list.toArray());
		 log.info("本地生成订单插入ret:"+insert+",orderid:"+nextid);
		
		return insert+"_"+nextid;
		
	}
	//结算订单
	private int completeOrder(JSONObject jo){
		long lineid = jo.getLong("id");
		int ret = dataBaseService.update("update order_tb set uin = ? ,end_time=?,total=? ,state=?,pay_type=?,uid=?,sync_state=? where line_id =? and sync_state<>? and pay_type<>? ",new Object[]{jo.getInt("uin"),jo.getLong("end_time"),
				jo.getDouble("total"),1,jo.getInt("pay_type"),jo.getLong("uid"),2,lineid,4,8});
		if(ret==1){
			Map map = dataBaseService.getMap("select * from order_tb where line_id = ? ", new Object[]{lineid});
			if(map!=null && map.get("id")!=null){
				long id = Long.parseLong(map.get("id")+"");
				if(jo.getInt("pay_type")==1){
					Long c = dataBaseService.getLong("select count(*) from parkuser_cash_tb where orderid = ?", new Object[]{id});
					 if(c!=null&&c<1){
						 int r = dataBaseService.update("insert into parkuser_cash_tb(uin,amount,orderid,create_time) values(?,?,?,?)", new Object[]{jo.getLong("uid"),jo.getDouble("total"),id,jo.getLong("end_time")});
						 log.info("写现金收费记录ret:"+r+",orderid:"+id+",amount:"+jo.getDouble("total")+",生成现金收费记录ret:"+r);
					 }else{
						 log.info("已有现金记录：orderid:"+id+",amount:"+jo.getDouble("total"));
					 }
				}else if(jo.getInt("pay_type")==2){
					Long c = dataBaseService.getLong("select count(*) from parkuser_account_tb where orderid = ?", new Object[]{id});
					 if(c!=null&&c<1){
						 int d = dataBaseService.update("delete from parkuser_cash_tb where orderid = ?", new Object[]{id});
						 int r = dataBaseService.update("insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)", new Object[]{jo.getLong("uid"),jo.getDouble("total"),0,System.currentTimeMillis()/1000,"停车费_"+jo.getString("car_number"),4,id});
						 log.info("sync line to local epay ret:"+r+",orderid:"+lineid+",amount:"+jo.getDouble("total")+",生成电子收费记录ret:"+r+",删除现金支付记录d："+d);
					 }else{
						 log.info("已有电子支付记录：orderid:"+lineid+",amount:"+jo.getDouble("total"));
					 }
				}
			}
		}
		return ret; 
 	}
	public void syncUtil(String tablename , JSONArray ja,String[] fileds,int[] columnTypes){
		try {
			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo  = ja.getJSONObject(i);
//				Object[] obj = new Object[fileds.length];
				ArrayList<Object> list = new ArrayList<Object>();
				int operate = jo.getInt("operate");
				StringBuffer insert = new StringBuffer("insert into "+tablename+"(");
				StringBuffer insertvalue = new StringBuffer(" values(");
				StringBuffer update = new StringBuffer("update "+tablename+" set ");
				StringBuffer delete = new StringBuffer("delete from "+tablename+" where id = ? ");
				String sql = null;
				if(operate==2){
					list.add(jo.getLong("id"));
				}else if(operate==1){
//					StringBuffer va = new StringBuffer(" values(");
					for (int j = 1; j< fileds.length; j++) {
						String filed = fileds[j];
						String s = jo.getString(filed);
						if(s==null)
							continue;
						if(s.equals("null"))
							jo.put(filed, "");
						try {
							if(columnTypes[j]==4){
								Long value = jo.getLong(filed);
								list.add(value);
							}else if(columnTypes[j]==12){
								String value = jo.getString(filed);
								list.add(value);
							}else if(columnTypes[j]==3){
								Double value = jo.getDouble(filed);
								list.add(value);
							}
							update.append(filed+"=?,");
						} catch (Exception e) {
							continue;
						}
						
//						va.append("?,");
					}
					if(update.length()<20)
						continue;
				}else if(operate==0){
//					StringBuffer va = new StringBuffer(" values(");
					for (int j = 0; j< fileds.length; j++) {
						String filed = fileds[j];
						String s = jo.getString(filed);
						if(s==null||s.equals("null")||s.equals(""))
							continue;
						try {
							if(columnTypes[j]==4){
								Long value = jo.getLong(filed);
								list.add(value);
							}else if(columnTypes[j]==12){
								String value = jo.getString(filed);
								list.add(value);
							}else if(columnTypes[j]==3){
								Double value = jo.getDouble(filed);
								list.add(value);
							}
							insert.append(filed+",");
							insertvalue.append("?,");
						} catch (Exception e) {
							
						}
						
//						va.append("?,");
					}
					if(insert.length()<20)
						continue;
				}
				if(operate==1){
					sql = update.substring(0,update.length()-1)+" where id = ?";
					list.add(jo.getLong("id"));
				}else if(operate==0){
					sql = insert.substring(0,insert.length()-1)+")"+insertvalue.substring(0,insertvalue.length()-1)+")";//+") "+va.substring(0,va.length()-1)+")";
				}else if(operate==2){
					sql = delete.toString();
				}
//				String updatesql = update.substring(0,update.length()-1)+" where id = ?";//+") "+va.substring(0,va.length()-1)+")";
//				String insertsql = insert.substring(0,update.length()-1)+")";
//				if(columnTypes[0]==4){
//					obj[fileds.length-1] = jo.getLong(fileds[0]);
//				}
				int r = 0;
				try{
					r = dataBaseService.update(sql, list);
				}catch (Exception e) {
					e.printStackTrace();
					if(e.getMessage().endsWith("存在")){
						if(tablename.equals("car_info_tb")){
							dataBaseService.update("delete from "+tablename+" where car_number = ?", new Object[]{jo.getString("car_number")});
						}else{
							dataBaseService.update("delete from "+tablename+" where id = ?", new Object[]{jo.getLong("id")});
						}
						r = dataBaseService.update(sql, list);
					}
				}
				if(r>0){
					dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{Long.valueOf(jo.get("op_id")+""),1});
				}if(r==0&&operate==2){
					dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{Long.valueOf(jo.get("op_id")+""),1});
				}
				log.info(r);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 同步
	 * @param tablename 表
	 * @param ja json数据
	 * @param fileds  字段
	 * @param columnTypes   字段类型
	 */
	public void syncUtil(String tablename , JSONObject ja,String[] fileds,int[] columnTypes){
		try {
//			for (int i = 0; i < ja.size(); i++) {
				JSONObject jo  = ja;//.getJSONObject(i);
//				Object[] obj = new Object[fileds.length];
				ArrayList<Object> list = new ArrayList<Object>();
				int operate = jo.getInt("operate");
				StringBuffer insert = new StringBuffer("insert into "+tablename+"(");
				StringBuffer insertvalue = new StringBuffer(" values(");
				StringBuffer update = new StringBuffer("update "+tablename+" set ");
				StringBuffer delete = new StringBuffer("delete from "+tablename+" where id = ? ");
				String sql = null;
				if(operate==2){
//					if(jo.size()==2)
						list.add(jo.getLong("id"));
//					else{
//						
//					}
				}else if(operate==1){
//					StringBuffer va = new StringBuffer(" values(");
					for (int j = 1; j< fileds.length; j++) {
						String filed = fileds[j];
						String s = jo.getString(filed);
						if(s==null)
							continue;
						if(s.equals("null"))
							jo.put(filed, "");
						try {
							if(columnTypes[j]==4){
								Long value = jo.getLong(filed);
								list.add(value);
							}else if(columnTypes[j]==12){
								String value = jo.getString(filed);
								list.add(value);
							}else if(columnTypes[j]==3){
								Double value = jo.getDouble(filed);
								list.add(value);
							}
							update.append(filed+"=?,");
						} catch (Exception e) {
							continue;
						}
//						va.append("?,");
					}
					if(update.length()<20)
						return;
				}else if(operate==0){
//					StringBuffer va = new StringBuffer(" values(");
					for (int j = 0; j< fileds.length; j++) {
						String filed = fileds[j];
						String s = jo.getString(filed);
						if(s==null||s.equals("null")||s.equals(""))
							continue;
						try {
							if(columnTypes[j]==4){
								Long value = jo.getLong(filed);
								list.add(value);
							}else if(columnTypes[j]==12){
								String value = jo.getString(filed);
								list.add(value);
							}else if(columnTypes[j]==3){
								Double value = jo.getDouble(filed);
								list.add(value);
							}
							insert.append(filed+",");
							insertvalue.append("?,");
						} catch (Exception e) {
							continue;
						}
//						va.append("?,");
					}
					if(insert.length()<20)
						return;
				}
				if(operate==1){
					sql = update.substring(0,update.length()-1)+" where id = ?";
					list.add(ja.getLong("id"));
				}else if(operate==0){
					sql = insert.substring(0,insert.length()-1)+")"+insertvalue.substring(0,insertvalue.length()-1)+")";//+") "+va.substring(0,va.length()-1)+")";
				}else if(operate==2){
					sql = delete.toString();
				}
//				String updatesql = update.substring(0,update.length()-1)+" where id = ?";//+") "+va.substring(0,va.length()-1)+")";
//				String insertsql = insert.substring(0,update.length()-1)+")";
//				if(columnTypes[0]==4){
//					obj[fileds.length-1] = jo.getLong(fileds[0]);
//				}
				int r = 0;
				try{
					r = dataBaseService.update(sql, list);
				}catch (Exception e) {
					e.printStackTrace();
					if(operate==0){
						if(e.getMessage().endsWith("存在")){
							if(tablename.equals("car_info_tb")){
								dataBaseService.update("delete from "+tablename+" where car_number = ?", new Object[]{jo.getString("car_number")});
							}else{
								dataBaseService.update("delete from "+tablename+" where id = ?", new Object[]{jo.getLong("id")});
							}
							r = dataBaseService.update(sql, list);
						}
					}
				}
				if(r>0){
					dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{Long.valueOf(ja.get("op_id")+""),1});
				}if(r==0&&operate==2){
					dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{Long.valueOf(ja.get("op_id")+""),1});
				}
				log.info(r);
//			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	  /**  
     * 获得CPU使用率.  
     * @return 返回cpu使用率  
     * @author GuoHuang  
     */   
    private double getCpuRatioForWindows() {   
        try {   
            String procCmd = System.getenv("windir")   
                    + "\\system32\\wbem\\wmic.exe process get Caption,CommandLine,"   
                    + "KernelModeTime,ReadOperationCount,ThreadCount,UserModeTime,WriteOperationCount";   
            // 取进程信息   
            long[] c0 = readCpu(Runtime.getRuntime().exec(procCmd));   
            Thread.sleep(30);   
            long[] c1 = readCpu(Runtime.getRuntime().exec(procCmd));   
            if (c0 != null &&c1 != null) {   
                long idletime = c1[0] - c0[0];   
                long busytime = c1[1] - c0[1];   
                return Double.valueOf(   
                        100 * (busytime) / (busytime + idletime))   
                        .doubleValue();   
            } else {   
                return 0.0;   
            }   
        } catch (Exception ex) {   
            ex.printStackTrace();   
            return 0.0;   
        }   
    }   
  
    /**       
 
* 读取CPU信息.  
     * @param proc  
     * @return  
     * @author GuoHuang  
     */   
    private long[] readCpu(final Process proc) {   
        long[] retn = new long[2];   
        try {   
            proc.getOutputStream().close();   
            InputStreamReader ir = new InputStreamReader(proc.getInputStream());   
            LineNumberReader input = new LineNumberReader(ir);   
            String line = input.readLine();   
            if (line == null || line.length() < 10) {   
                return null;   
            }   
            int capidx = line.indexOf("Caption");   
            int cmdidx = line.indexOf("CommandLine");   
            int rocidx = line.indexOf("ReadOperationCount");   
            int umtidx = line.indexOf("UserModeTime");   
            int kmtidx = line.indexOf("KernelModeTime");   
            int wocidx = line.indexOf("WriteOperationCount");   
            long idletime = 0;   
            long kneltime = 0;   
            long usertime = 0;   
            while ((line = input.readLine()) != null) {   
                if (line.length() < wocidx) {   
                    continue;   
                }   
                // 字段出现顺序：Caption,CommandLine,KernelModeTime,ReadOperationCount,   
                // ThreadCount,UserModeTime,WriteOperation   
                String caption = Bytes.substring(line, capidx, cmdidx - 1)   
                        .trim();   
                String cmd = Bytes.substring(line, cmdidx, kmtidx - 1).trim();   
                if (cmd.indexOf("wmic.exe") >= 0) {   
                    continue;   
                }   
                // log.info("line="+line);   
                if (caption.equals("System Idle Process")   
                        || caption.equals("System")) {   
                    idletime += Long.valueOf(   
                            Bytes.substring(line, kmtidx, rocidx - 1).trim())   
                            .longValue();   
                    idletime += Long.valueOf(   
                            Bytes.substring(line, umtidx, wocidx - 1).trim())   
                            .longValue();   
                    continue;   
                }   
  
                kneltime += Long.valueOf(
                        Bytes.substring(line, kmtidx, rocidx - 1).trim())
                        .longValue();
                usertime += Long.valueOf(
                        Bytes.substring(line, umtidx, wocidx - 1).trim())
                        .longValue();
            }   
            retn[0] = idletime;   
            retn[1] = kneltime + usertime;   
            return retn;   
        } catch (Exception ex) {   
            ex.printStackTrace();   
        } finally {   
            try {   
                proc.getInputStream().close();   
            } catch (Exception e) {   
                e.printStackTrace();   
            }   
        }   
        return null;   
    }   

    public static String getEMS() {
    	OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();  
    	long t = osmb.getTotalPhysicalMemorySize() / 1024/1024;
    	long f = osmb.getFreePhysicalMemorySize() / 1024/1024;
    	double use = StringUtils.formatDouble((t-f)/(t/1.0)*100);
        return use+"";
	}
    public static String getCollectionName(Long milliSeconds) {
		String date =TimeTools.getTimeStr_yyyy_MM_dd(milliSeconds);
		String[] strdate = date.split("-");
//		int str = (Integer.parseInt(strdate[2]))/3;
		return strdate[0]+strdate[1]+strdate[2];
	}
    public static void inputstreamtofile(InputStream ins,File file) throws IOException{
    	OutputStream os = null;
    	try {
	    	os = new FileOutputStream(file);
	    	int bytesRead = 0;
	    	byte[] buffer = new byte[8192];
	    	while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
	    		os.write(buffer, 0, bytesRead);
	    	}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(os!=null){
//				os.flush();
				os.close();
			}if(ins!=null){
				ins.close();
			}
		}
    	
    	}
	/**
	 * 根据表获取所有的字段名和字段类型
	 * @param tablename
	 * @return
	 */
	public Map getColumns(String tablename){
		HashMap<String, String> hashMap = new HashMap<String,String>();
		List list = dataBaseService.getAll("select column_name,data_type from information_schema.columns where table_schema='public' and table_name= ? ", new Object[]{tablename});
		for (Object object : list) {
			Map map = (Map)object;
			hashMap.put(map.get("column_name")+"",map.get("data_type")+"");
		}
		return hashMap;
	}
	 /**
	  * 同步工具类 
	  * @param dataStr 数据
	  * @param tableName 表名
	  */
	private int syncUtil(String dataStr,String tableName) {
		int r =0;
		try{
//			HttpProxy proxy = new HttpProxy();
			String result = dataStr;//proxy.doGetInit(CustomDefind.DOMAIN+"/localinit.do?comid="+CustomDefind.COMID+"&action="+actionName);
			JSONObject jo = new JSONObject();
			Map<String,String> columnsList = getColumns(tableName);
			if(result!=null&&result.length()>2){
				if(result.startsWith("{")){
					jo = jo.fromObject(result);
					r += initData(jo,columnsList,tableName);
				}else{
					JSONArray ja = new JSONArray();
					ja = ja.fromObject(result);
					for (int i = 0; i < ja.size(); i++) {
						jo = ja.getJSONObject(i);
						r += initData(jo,columnsList,tableName);
					}
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
		return r;
	}
	/**
	 * 初始化数据  拼接sql执行
	 * @param comJo  数据
	 * @param columnsList
	 * @param tablename
	 */
	private int initData(JSONObject comJo, Map<String,String> columnsList,String tablename) {
		String sql = "";
		int operate = comJo.getInt("operate");
		StringBuffer insert = new StringBuffer("insert into "+tablename+"(");
		StringBuffer insertvalue = new StringBuffer(" values(");
		StringBuffer update = new StringBuffer("update "+tablename+" set ");
		StringBuffer delete = new StringBuffer("delete from "+tablename+" where id = ? ");
		StringBuffer insertsql = new StringBuffer("insert into "+tablename +" (");
		StringBuffer valuesql = new StringBuffer(" values(");
		ArrayList values = new ArrayList();
		if(operate==2){
			if(!tablename.equals("car_number_type_tb")) {
				sql = delete.toString();
			}else{
				sql = "delete from "+tablename+" where line_id = ? ";
			}
			values.add(comJo.getLong("id"));
		}else if(operate==0){
			for (Map.Entry<String, String> entry : columnsList.entrySet()) {
				try{
					if((comJo.get(entry.getKey())!=null&&!"null".equals(comJo.getString(entry.getKey())))||(tablename.equals("car_number_type_tb")&&("line_id".equals(entry.getKey())||"sync_state".equals(entry.getKey())))){
						if(!tablename.equals("car_number_type_tb")){
							insertsql.append(entry.getKey()+",");
							valuesql.append("?,");
						}else{
							if(entry.getKey().equals("id")){
								insertsql.append("id,");
								valuesql.append("?,");
								Long id = dataBaseService.getLong(
										"SELECT nextval('seq_car_number_type_tb'::REGCLASS) AS newid", null);
								values.add(id);
								continue;
							}
							if(entry.getKey().equals("line_id")){
								insertsql.append("line_id,");
								valuesql.append("?,");
								values.add(comJo.getLong("id"));
								continue;
							}
							if(entry.getKey().equals("sync_state")){
								insertsql.append("sync_state,");
								valuesql.append("?,");
								values.add(1);
								continue;
							}
							insertsql.append(entry.getKey()+",");
							valuesql.append("?,");
						}
						if(entry.getValue().startsWith("bigint")){
							values.add(comJo.getLong(entry.getKey()));
						}else if(entry.getValue().startsWith("numeric")){
							values.add(comJo.getDouble(entry.getKey()));
						}else if(entry.getValue().startsWith("integer")){
							values.add(comJo.getInt(entry.getKey()));
						}else if(entry.getValue().startsWith("charact")){
							values.add(comJo.getString(entry.getKey()));
						}
					}
				}catch (Exception e) {
					System.out.println("0-----------------"+e.getMessage());
				}
			}
			if(insertsql.toString().endsWith(",")&&valuesql.toString().endsWith(",")){
				sql = insertsql.substring(0,insertsql.length()-1)+") "+valuesql.substring(0,valuesql.length()-1)+")";
			}
		}else if(operate==1){
			for (Map.Entry<String, String> entry : columnsList.entrySet()) {
				try{
					if(comJo.get(entry.getKey())!=null&&!"null".equals(comJo.getString(entry.getKey()))){
						if(tablename.equals("car_number_type_tb")&&entry.getKey().equals("id")){
							continue;
						}
						if(entry.getValue().startsWith("bigint")) {
							values.add(comJo.getLong(entry.getKey()));
						}else if(entry.getValue().startsWith("numeric")) {
							values.add(comJo.getDouble(entry.getKey()));
						}else if(entry.getValue().startsWith("integer")) {
							values.add(comJo.getInt(entry.getKey()));
						}else if(entry.getValue().startsWith("charact")){
							values.add(comJo.getString(entry.getKey()));
						}
						update.append(entry.getKey()+"=?,");
					}
				}catch (Exception e) {
					System.out.println("0-----------------"+e.getMessage());
				}
			}
			if(update.toString().endsWith(",")){
				if(tablename.equals("car_number_type_tb")){
					sql = update.substring(0,update.length()-1)+" where line_id = ?";
				}else{
					sql = update.substring(0,update.length()-1)+" where id = ?";
				}
				values.add(comJo.getLong("id"));
			}
		}
		
		int r = 0;
		try{
			r = dataBaseService.update(sql, values);
		}catch (Exception e) {
			if(e.getMessage().endsWith("存在")){
				try{
					if(tablename.equals("car_info_tb")){
						dataBaseService.update("delete from "+tablename +" where car_number = ?", new Object[]{comJo.getString("car_number")});
					}else{
						dataBaseService.update("delete from "+tablename +" where id = ?", new Object[]{comJo.getLong("id")});
					}
				}catch (Exception e1){

				}
				r = dataBaseService.update(sql, values);
			}
		}
		log.info("opid:"+comJo.get("op_id")+""+",result:"+r+",sql:"+sql+",values:"+values.toArray().toString());
		try {
			if(r>0){
				dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{Long.valueOf(comJo.get("op_id")+""),1});
			}if(r==0&&operate==2){
				dataBaseService.update("update sync_time_tb set maxid =? where id = ?", new Object[]{Long.valueOf(comJo.get("op_id")+""),1});
			}
		} catch (Exception e) {
			log.info(e.getMessage());
		}
		return r;
	}

}

class Bytes {   
    public static String substring(String src, int start_idx, int end_idx){   
        byte[] b = src.getBytes();   
        String tgt = "";   
        for(int i=start_idx; i<=end_idx; i++){   
            tgt +=(char)b[i];   
        }   
        return tgt;   
    }  
    
}
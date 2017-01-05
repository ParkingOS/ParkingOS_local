package com.zld.schedule;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.log4j.Logger;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.HttpProxy;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class SyncToLineSchedule implements Runnable {
	
	DataBaseService daService;
	
	public SyncToLineSchedule(DataBaseService dataBaseService ){
		this.daService = dataBaseService;
	}
	ExecutorService executor =Executors.newFixedThreadPool(6);

	private static Logger log = Logger.getLogger(SyncToLineSchedule.class);
	public void run() {
		log.info("***********==========开始本地上传任务"+TimeTools.gettime()+"=============*********************");
		FutureTask<String> future1 = null;
		FutureTask<String> future2 = null;
		FutureTask<String> future3 = null;
		FutureTask<String> future4 = null;
//		FutureTask<String> future5 = null;
		try{
			String token = "";
			Map map = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
			if(map!=null&&map.get("token")!=null){
				token = map.get("token")+"";
			}
//			if(token==null){
//				HttpProxy httpProxy = new HttpProxy(。);
//		   	 	//获取token
//		   	 	String mes = AESEncryptor.encrypt("0123456789ABCDEFtingcaidfjalsjffdaslfkdafjdaljdf", CustomDefind.SECRET+":"+CustomDefind.COMID);
//		   	 	token = httpProxy.doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getToken&mes="+mes);
////		   	 	token = httpProxy.doGet(CustomDefind.DOMAIN+"/syncInter.do?action=getToken&appid="+CustomDefind.COMID+"&secret"+CustomDefind.SECRET);
//		   	 if(token!=null&&!token.equals("")){
//					int r = daService.update("update sync_time_tb set token=? ", new Object[]{token});
//				}
//			}
			final String tk = token;
			
		//上传订单	
		future1 =
		       new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					 List lists = daService.getAll("select * from order_tb where sync_state = 0 or (state=1 and sync_state<>2) order by id desc ",null,1,15);
					 String ret = null;
					 if(lists!=null&&lists.size()>0){
						 String order = AjaxUtil.encodeUTF8(StringUtils.createJson(lists));
		            	 HttpProxy httpProxy = new HttpProxy();
		            	 Map parammap = new HashMap();
		            	 parammap.put("order", order);
						 log.error("上传的订单："+order);
		            	 try {
		            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadOrder2Line&token="+tk, parammap);
		            		 if(ret==null){
		            			 return null;
		            		 }
		            		 System.out.println(ret);
		            		 String[] strs = ret.split(",");
		            		 for (int i = 0; i < strs.length; i++) {
								if(strs[i]!=null){
									if(strs[i].startsWith("1")){
										String rets[] = strs[i].split("_");
										if(rets.length==4){
											int r = daService.update("update order_tb set uin = ?,sync_state=?,line_id=? where id = ?", new Object[]{Long.valueOf(rets[1]),1,Long.valueOf(rets[2]),Long.valueOf(rets[3])});
											daService.update("update ticket_tb set lineorderid = ? where orderid = ?", new Object[]{Long.valueOf(rets[2]),Long.valueOf(rets[3])});
										}else if(rets.length==2){
											Map map = daService.getMap("select line_id,end_time,pay_type from order_tb where id = ?", new Object[]{Long.valueOf(rets[1])});
											int r = 0;
											if(map!=null&&map.get("end_time")!=null&&map.get("pay_type")!=null){
												if(Long.parseLong(map.get("pay_type")+"")!=8||(System.currentTimeMillis()/1000-Long.parseLong(map.get("end_time")+""))>60){
													r = daService.update("update order_tb set sync_state=? where id = ? ", new Object[]{2,Long.valueOf(rets[1])});
												}
											}
											if(r==1){
												if(map!=null&&map.get("line_id")!=null){
													System.out
															.println("kaishishangchuanticket");
													daService.update("update ticket_tb set lineorderid = ? where orderid = ?", new Object[]{Long.valueOf(map.get("line_id")+""),Long.valueOf(rets[1])});
												}
											}
										}
									}else if(strs[i].startsWith("0")){
										String rets[] = strs[i].split("_");
										if(rets.length==4){
											int r = daService.update("update order_tb set uin = ?,sync_state=?,line_id=? where id = ?", new Object[]{Long.valueOf(rets[1]),1,Long.valueOf(rets[2]),Long.valueOf(rets[3])});
										}if(rets.length==2){
											int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{1,Long.valueOf(rets[1])});
										}
									}else if(strs[i].startsWith("2")){//线上现金结算  线下免费
										String rets[] = strs[i].split("_");
										if(rets.length==2){
											int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{2,Long.valueOf(rets[1])});
										}
									}
								}
							}
							} catch (Exception e) {
								e.printStackTrace();
							}
					 }
					return ret;  
				}	
		    });
		//上次上班记录
		future2 =
		       new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					 List lists = daService.getAll("select * from parkuser_work_record_tb where sync_state = 0",null,1,1);
					 String ret = null;
					 if(lists!=null&&lists.size()>0){
						 String order = StringUtils.createJson((Map)lists.get(0));
		            	 HttpProxy httpProxy = new HttpProxy();
		            	 Map parammap = new HashMap();
		            	 parammap.put("work", order);
		            	 try {
		            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadWork2Line&token="+tk, parammap);
		            		 log.error(">>>>>>uploadWork2Line,ret:"+ret);
		            		 String[] strs = ret.split(",");
		            		 for (int i = 0; i < strs.length; i++) {
								if(strs[i]!=null){
//									if(ret.startsWith("1")){
										int r = daService.update("update parkuser_work_record_tb set sync_state=?,line_id=? where id = ?", new Object[]{1,Long.parseLong(ret.split("_")[2]+""),Long.parseLong(ret.split("_")[1]+"")});
//									}
								}
							}
							} catch (Exception e) {
								e.printStackTrace();
							}
							System.out.println(ret);
					 }
					 upload2ticket(tk);
					return ret;  
				}	
		    });
		//上传图片
		future3 =
		       new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					String ret = null;
					try {
						 List lists = daService.getAll("select * from order_tb where (uploadinpic_state = 0 or (uploadoutpic_state=0 and state=1)) and line_id > 1 ",null,1,3);
						 if(lists!=null&&lists.size()>0){
							 for (Object object : lists) {
								Map map = (Map)object;
								Long comid = (Long)map.get("comid");
								Long orderid = (Long)map.get("id");
								if(Long.parseLong(map.get("uploadinpic_state")+"")==0){
									String fn = CustomDefind.PIC+getCollectionName((Long)map.get("create_time")*1000)+"\\"+orderid+0+".jpg";
									uploadpic2line(fn, comid, orderid, 0L);
								}if(Long.parseLong(map.get("uploadoutpic_state")+"")==0&&Long.parseLong(map.get("state")+"")==1){
									String fn = CustomDefind.PIC+getCollectionName((Long)map.get("end_time")*1000)+"\\"+orderid+1+".jpg";
									uploadpic2line(fn, comid, orderid, 1L);
								}
							}
						 }} catch (Exception e) {
								e.printStackTrace();
							}
				try {
					List lilists = daService.getAll("select * from lift_rod_tb where sync_state = 1 and line_id >0 and img is not null order by id desc ",null,1,8);
					 if(lilists!=null&&lilists.size()>0){
						 for (Object object : lilists) {
							 Map map = (Map)object;
							 try {
									Object img = map.get("img");
									String fn = CustomDefind.PIC+getCollectionName((Long)map.get("ctime")*1000)+"\\"+img;
									System.out.println(fn);
									uploadliftrodpic2line(fn, map.get("line_id")+"",map.get("ctime")+"",tk);
							} catch (Exception e) {
								daService.update("update lift_rod_tb set sync_state = ? where line_id = ?", new Object[]{2,Long.parseLong(map.get("line_id")+"")});
							}
						}
					 }
					} catch (Exception e) {
						e.printStackTrace();
					}
					return ret;  
				}
		    });
		//上传抬杆记录
		future4 =
		       new FutureTask<String>(new Callable<String>() {

				public String call() throws Exception {
					List lists = daService.getAll("select * from lift_rod_tb where sync_state = 0 ",null,1,8);
					 String ret = null;
					 if(lists!=null&&lists.size()>0){
						 String liftrod = AjaxUtil.encodeUTF8(StringUtils.createJson(lists));
		            	 HttpProxy httpProxy = new HttpProxy();
		            	 Map parammap = new HashMap();
		            	 parammap.put("liftrod", liftrod);
		            	 try {
		            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadliftrod&token="+tk, parammap);
		            		 if(ret==null){
		            			 return null;
		            		 }
		            		 String[] strs = ret.split(",");
		            		 for (int i = 0; i < strs.length; i++) {
								if(strs[i]!=null){
									if(strs[i].startsWith("1")){
										String rets[] = strs[i].split("_");
										int r = daService.update("update lift_rod_tb set sync_state=?,line_id=? where id = ?", new Object[]{1,Long.valueOf(rets[1]),Long.valueOf(rets[2])});
									}
								}
							}
		            	 } catch (Exception e) {
								e.printStackTrace();
							}
		            	
					 }
					//上传车牌对应车型
					List cntList = daService.getAll("select * from car_number_type_tb where sync_state = 0 ",null,1,10);
					if(cntList!=null&&cntList.size()>0){
						String liftrod = AjaxUtil.encodeUTF8(StringUtils.createJson(cntList));
						HttpProxy httpProxy = new HttpProxy();
						Map parammap = new HashMap();
						parammap.put("carnumbertype", liftrod);
						try {
							ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadnumbertype&token="+tk, parammap);
							if(ret==null){
								return null;
							}
							String[] strs = ret.split(",");
							for (int i = 0; i < strs.length; i++) {
								String result = strs[i];
								if(result!=null){
									if(result.startsWith("1")){
										String[] rets = result.split("_");
										if(rets.length==3){
											daService.update("update car_number_type_tb set sync_state=?,line_id=? where id = ?", new Object[]{1,Long.valueOf(rets[1]),Long.valueOf(rets[2])});
										}
										if(rets.length==4){
											daService.update("update car_number_type_tb set sync_state=? where id = ?", new Object[]{1,Long.valueOf(rets[1]),Long.valueOf(rets[2])});
										}
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					return null;  
				}	
		    });
		
		executor.submit(future1);
		executor.submit(future2);
		executor.submit(future3);
		executor.submit(future4);
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
//		executor.shutdownNow();
	}
	}
	
	private void upload2ticket(String tk){
		ArrayList list = new ArrayList();
		 list.add(1);
		 list.add(0);
		 list.add(-1);
		 List lists = daService.getAll("select * from ticket_tb where state=? and sync_state=? and lineorderid >?",list,1,10);
		 String ret = null;
		 if(lists!=null&&lists.size()>0){
			 String ticket = AjaxUtil.encodeUTF8(StringUtils.createJson(lists));
	       	 HttpProxy httpProxy = new HttpProxy();
	       	 Map parammap = new HashMap();
	       	 parammap.put("ticket", ticket);
	       	 try {
	       		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadTicket2Line&token="+tk, parammap);
	       		 if(ret!=null){
	       			 String[] strs = ret.split(",");
	           		 for (int i = 0; i < strs.length; i++) {
	    					if(strs[i]!=null){
	    						if(strs[i].startsWith("1")){
	    							String rets[] = strs[i].split("_");
	    							int r = daService.update("update ticket_tb set sync_state=? where id = ?", new Object[]{1,Long.valueOf(rets[1])});
	    						}
	    					}
	    				}
    				} 
       		 }catch (Exception e) {
					e.printStackTrace();
				}
		 }
	}
	private void uploadpic2line(String fn,Long comid,Long orderid,Long type) throws Exception{
		System.out.println("-----------------------------------");
		InputStream is = null;
		try {
			File f = new File(fn);
			is = new FileInputStream(f);
//    		map.put("content",list.get(1));
    		Map map = daService.getMap("select line_id from order_tb where id = ?", new Object[]{orderid});
    		System.out.println(orderid);
    		if(map!=null&&map.get("line_id")!=null){
    			Long line_id = Long.parseLong(map.get("line_id")+"");
//    			Long Line_preorderid = daService.getLong("select line_id from order_tb where id = ?", new Object[]{preorderid});
        		String params = "action=uploadpic&comid="
    				+ comid + "&orderid=" + line_id + "&lefttop=0"
    				+ "&rightbottom=0"  + "&type=" + type
    				+ "&width=0"  + "&height=0" ;
//    			String rets = requestLine("http://192.168.199.251/zldline/syncInter.do"+ "?" + request.getQueryString(),null);
    			URL url = new URL(CustomDefind.DOMAIN+"/carpicsup.do"+ "?" + params);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			String  BOUNDARY =  UUID.randomUUID().toString();
    			conn.setReadTimeout(4*1000);
    			conn.setConnectTimeout(4*1000);
    			conn.setDoInput(true);  //允许输入流
    			conn.setDoOutput(true); //允许输出流
    			conn.setUseCaches(false);  //不允许使用缓存
    			conn.setRequestMethod("POST");  //请求方式
    			conn.setRequestProperty("Charset", "utf-8");  //设置编码
    			conn.setRequestProperty("connection", "keep-alive");   
    			conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" +BOUNDARY ); 
//    			InputStream is = null;
    			if(is != null)
    			{
    				/**
    				 * 当文件不为空，把文件包装并且上传
    				 */
    				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
    				StringBuffer sb = new StringBuffer();
    				sb.append("--");
    				sb.append(BOUNDARY);
    				sb.append("\r\n");
    				/**
    				 * 这里重点注意：
    				 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
    				 * filename是文件的名字，包含后缀名的   比如:abc.png  
    				 */
    				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+"zhenlaidian.jpg"+"\""+"\r\n"); 
    				sb.append("Content-Type: application/octet-stream; charset="+"utf-8"+"\r\n");
    				sb.append("\r\n");
    				dos.write(sb.toString().getBytes());
    				byte[] bytes = new byte[1024];
    				int len = 0;
    				while((len=is.read(bytes))!=-1)
    				{
    					dos.write(bytes, 0, len);
    				}
    				is.close();
    				dos.write("\r\n".getBytes());
    				byte[] end_data = ("--"+BOUNDARY+"--"+"\r\n").getBytes();
    				dos.write(end_data);
    				dos.flush();
    				/**
    				 * 获取响应码  200=成功
    				 * 当响应成功，获取响应的流  
    				 */
    				int res = conn.getResponseCode();  
    				String msg = conn.getResponseMessage();
    				System.out.println(res+":"+msg);
    				if(res==200){
    					log.info("图片上传success  orderid"+orderid);
    					if(type==0){
    						int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{1,orderid});
    					}else if(type==1){
    						int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{1,orderid});
    					}
    				}else{
    					if(type==0){
    						int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{0,orderid});
    					}else if(type==1){
    						int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{0,orderid});
    					}
    				}
    			}
    		}
		}catch (FileNotFoundException e) {
			if(type==0){
				int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{1,orderid});
			}else if(type==1){
				int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{1,orderid});
			}
		}catch (Exception e) {
			if(type==0){
				int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{0,orderid});
			}else if(type==1){
				int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{0,orderid});
			}
			e.printStackTrace();
		}finally{
			if(is!=null)
				is.close();
		}
//    		
	}
	public static String getCollectionName(Long milliSeconds) {
		String date =TimeTools.getTimeStr_yyyy_MM_dd(milliSeconds);
		String[] strdate = date.split("-");
//		int str = (Integer.parseInt(strdate[2]))/3;
		return strdate[0]+strdate[1]+strdate[2];
	}
	
	private void uploadliftrodpic2line(String file,String lrid,String ctime,String token) throws Exception{
		System.out.println("-----------------------------------");
		InputStream is = null;
		try {
			File f = new File(file);
			if(!f.exists()){
				//todo图片不存在时
				daService.update("update lift_rod_tb set sync_state = ? where line_id = ?", new Object[]{2,Long.parseLong(lrid)});
			}
			is = new FileInputStream(f);
//    		map.put("content",list.get(1));
//    			Long Line_preorderid = daService.getLong("select line_id from order_tb where id = ?", new Object[]{preorderid});
        		String params = "action=uploadliftrodpic&lrid="+lrid+"&ctime="+ctime+"&token="+token ;
//    			String rets = requestLine("http://192.168.199.251/zldline/syncInter.do"+ "?" + request.getQueryString(),null);
    			URL url = new URL(CustomDefind.DOMAIN+"/syncInter.do"+ "?" + params);
    			System.out.println(CustomDefind.DOMAIN+"/syncInter.do"+ "?" + params);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			String  BOUNDARY =  UUID.randomUUID().toString();
    			conn.setReadTimeout(4*1000);
    			conn.setConnectTimeout(4*1000);
    			conn.setDoInput(true);  //允许输入流
    			conn.setDoOutput(true); //允许输出流
    			conn.setUseCaches(false);  //不允许使用缓存
    			conn.setRequestMethod("POST");  //请求方式
    			conn.setRequestProperty("Charset", "utf-8");  //设置编码
    			conn.setRequestProperty("connection", "keep-alive");   
    			conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" +BOUNDARY ); 
//    			InputStream is = null;
    			if(is != null)
    			{
    				/**
    				 * 当文件不为空，把文件包装并且上传
    				 */
    				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
    				StringBuffer sb = new StringBuffer();
    				sb.append("--");
    				sb.append(BOUNDARY);
    				sb.append("\r\n");
    				/**
    				 * 这里重点注意：
    				 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
    				 * filename是文件的名字，包含后缀名的   比如:abc.png  
    				 */
    				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+"zhenlaidian.jpg"+"\""+"\r\n"); 
    				sb.append("Content-Type: application/octet-stream; charset="+"utf-8"+"\r\n");
    				sb.append("\r\n");
    				dos.write(sb.toString().getBytes());
    				byte[] bytes = new byte[1024];
    				int len = 0;
    				while((len=is.read(bytes))!=-1)
    				{
    					dos.write(bytes, 0, len);
    				}
    				is.close();
    				dos.write("\r\n".getBytes());
    				byte[] end_data = ("--"+BOUNDARY+"--"+"\r\n").getBytes();
    				dos.write(end_data);
    				dos.flush();
    				/**
    				 * 获取响应码  200=成功
    				 * 当响应成功，获取响应的流  
    				 */
    				int res = conn.getResponseCode();  
    				String msg = conn.getResponseMessage();
    				System.out.println(res+":"+msg);
    				if(res==200){
    					log.info("抬杆图片上传成功");
    					daService.update("update lift_rod_tb set sync_state = ? where line_id = ?", new Object[]{2,Long.parseLong(lrid)});
    				}
    			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(is!=null)
				is.close();
		}
//    		
	}
}

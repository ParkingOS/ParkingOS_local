package com.zld.struts.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.Check;
import com.zld.utils.HttpProxy;
import com.zld.utils.RequestUtil;
import com.zld.utils.TimeTools;

public class CarPicsUploadAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(CarPicsUploadAction.class);
	/*
	 * 上传照牌图片
	 */
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long orderid = RequestUtil.getLong(request, "orderid", -1L);
		Long comid = RequestUtil.getLong(request, "comid", -1L);
		//type区分是入口还是出口，入口为0，出口为1
		Long type = RequestUtil.getLong(request, "type", -1L);
		if(comid==-1 || orderid == -1 || type == -1){
			AjaxUtil.ajaxOutput(response, "-1");
			return null;
		}
		if(action.equals("uploadpic")){
			String lefttop = RequestUtil.processParams(request, "lefttop");
			String rightbottom = RequestUtil.processParams(request, "rightbottom");
			String width = RequestUtil.processParams(request, "width");
			String height = RequestUtil.processParams(request, "height");
			Long preorderid = RequestUtil.getLong(request, "preorderid", -1L);//未结算的订单id
			List list = uploadCarPics2Mongodb(request, comid, orderid, lefttop, rightbottom,width,height,type);
			System.out.println("----------------"+list.size());
			String result = list.get(0)+"";
			//把该照片作为未结算订单的出口照片
			if(preorderid != -1){
				result = uploadCarPics2Mongodb(request, comid, preorderid, lefttop, rightbottom, width, height, 1L).get(0)+"";
			}
			AjaxUtil.ajaxOutput(response, result);
			//上传到线上mongodb
			if(list.size()==2){
				publicMethods.uploadpic2line(list.get(1)+"",comid, orderid, lefttop, rightbottom, width, height,type);
			}
			//http://127.0.0.1/zld/carpicsup.do?action=uploadpic&comid=0&orderid=238705&type=0
		}else if(action.equals("downloadpic")){
			downloadCarPics(orderid, type,request,response);
			//http://118.192.88.90:8080/zld/carpicsup.do?action=downloadpic&comid=0&orderid=238747&type=0
		}else if(action.equals("downloadlogpic")){
//			Map map = daService.getMap("select * from com_info_tb where id = ? ", new Object[]{comid});
//			if(map!=null&&map.get("chanid")!=null){
//				Long chanid = (Long)map.get("chanid");
//				Map LogoMap = daService.getMap("select * from logo_tb where type = ? and orgid = ?", new Object[]{0,chanid});
//				if(LogoMap!=null&&LogoMap.get("url_sec")!=null){
//					String logourl = LogoMap.get("url_sec")+"";
//					String fname = "停车宝";
//					if(LogoMap.get("name")!=null){
//						fname = LogoMap.get("name")+"";
//					}
//					downloadLOGOPics(logourl,fname,request,response);
//				}
//				
//			}
			Boolean hasLOGO = getLog(response);
			logger.info("hasLogo:"+hasLOGO);
			if(!hasLOGO){
				String url = CustomDefind.DOMAIN+request.getRequestURI().substring(4)+"?"+request.getQueryString(); 
				getLOGFromLine(url);
			}
			getLog(response);
		}
		return null;
	}
	
	private List uploadCarPics2Mongodb (HttpServletRequest request,Long comid,Long orderid,String lefttop,String rightbottom,String width,String height,Long type) throws Exception{
		logger.info("begin upload picture....");
		Map<String, String> extMap = new HashMap<String, String>();
	    extMap.put(".jpg", "image/jpeg");
	    extMap.put(".jpeg", "image/jpeg");
	    extMap.put(".png", "image/png");
	    extMap.put(".gif", "image/gif");
		request.setCharacterEncoding("UTF-8"); // 设置处理请求参数的编码格式
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // 建立FileItemFactory对象
		factory.setSizeThreshold(16*4096*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 分析请求，并得到上传文件的FileItem对象
		upload.setSizeMax(16*4096*1024);
		List list = new ArrayList();
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			list.add("-1");
			return list;
		}
		String filename = ""; // 上传文件保存到服务器的文件名
		InputStream is = null; // 当前上传文件的InputStream对象
		FileOutputStream outer = null;
		// 循环处理上传文件
		String comId = "";
		String orderId = "";
		for (FileItem item : items){
			// 处理普通的表单域
			if (item.isFormField()){
				if(item.getFieldName().equals("comid")){
					if(!item.getString().equals(""))
						comId = item.getString("UTF-8");
				}else if(item.getFieldName().equals("orderid")){
					if(!item.getString().equals("")){
						orderId = item.getString("UTF-8");
					}
				}
				
			}else if (item.getName() != null && !item.getName().equals("")){// 处理上传文件
				// 从客户端发送过来的上传文件路径中截取文件名
				logger.info(item.getName());
				filename = item.getName().substring(
						item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // 得到上传文件的InputStream对象
				
			}
		}
		if(comid==null&&(comId.equals("")||!Check.isLong(comId))){
			list.add("-1");
			return list;
		}
		if(orderid == null && (orderId.equals("") || !Check.isLong(orderId))){
			list.add("-1");
			return list;
		}
		String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// 扩展名
		String picurl = "\\"+orderid + type + file_ext;
		BufferedInputStream in = null;  
		ByteArrayOutputStream byteout =null;
		byte[] content = null;
		String fileName = null;
	    try {
	    	in = new BufferedInputStream(is);   
	    	byteout = new ByteArrayOutputStream(1024);        	       
		      
	 	    byte[] temp = new byte[1024];        
	 	    int bytesize = 0;        
	 	    while ((bytesize = in.read(temp)) != -1) {        
	 	          byteout.write(temp, 0, bytesize);        
	 	    }        
	 	      
	 	    content = byteout.toByteArray(); 
	 	    String f = CustomDefind.PIC+getCollectionName(System.currentTimeMillis());
	 	    File file = new File(f);
	 	    if(!file.exists()){
	 	    	file.mkdirs();
	 	    }
	 	    fileName = f+picurl;
	 	    outer = new FileOutputStream(fileName);  
	        outer.write(content);  
	        outer.close();  
	 	    
//	 	    DB mydb = MongoClientFactory.getInstance().getMongoDBBuilder("zld");
//		    mydb.requestStart();
//		    DBCollection collection = mydb.getCollection("car_pics"+getCollectionName(System.currentTimeMillis()));
//		  //  DBCollection collection = mydb.getCollection("records_test");
//			  
//			BasicDBObject document = new BasicDBObject();
//			document.put("comid",  comid);
//			document.put("orderid", orderid);
//			document.put("gate", type);
//			document.put("ctime",  System.currentTimeMillis()/1000);
//			document.put("type", extMap.get(file_ext));
//			document.put("content", content);
//			document.put("filename", picurl);
//			  //开始事务
//			mydb.requestStart();
//			collection.insert(document);
//			  //结束事务
//			mydb.requestDone();
			in.close();        
		    is.close();
		    byteout.close();
		    List<Object> params = new ArrayList<Object>();
		    params.add(orderid);
		    params.add(type);
		    String sql = "select count(*) from car_picturs_tb where orderid=? and pictype=?";
		    Long count = 0L;
		    count = daService.getCount(sql, params);
		    if(count > 0){
		    	sql = "update car_picturs_tb set create_time=?,lefttop=?,rightbottom=?,width=?,height=? where orderid=? and pictype=?";
		    	daService.update(sql, new Object[]{System.currentTimeMillis()/1000,lefttop,rightbottom,width,height,orderid,type});
		    }else{
		    	sql = "insert into car_picturs_tb(orderid,pictype,create_time,lefttop,rightbottom,width,height) values(?,?,?,?,?,?,?)";
		    	daService.update(sql, new Object[]{orderid,type,System.currentTimeMillis()/1000,lefttop,rightbottom,width,height});
		    }
		} catch (Exception e) {
			e.printStackTrace();
			list.add("-1");
			return list;
		}finally{
			if(outer!=null)
				outer.close();
			if(in!=null)
				in.close();
			if(byteout!=null)
				byteout.close();
			if(is!=null)
				is.close();
		}
		list.add("1");
		if(fileName!=null)
			list.add(fileName);
		return list;
	}
	
	private void downloadCarPics (Long orderid,Long type,HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.info("download from mongodb....");
		System.err.println("downloadCarPics from mongodb file:orderid="+orderid+"type="+type);
		if(orderid!=null && type !=null){
			Map ordermap = daService.getMap("select create_time,end_time from order_tb where id = ?", new Object[]{orderid});
			Long btime = System.currentTimeMillis();
			if(ordermap!=null){
				if(type==0&&ordermap.get("create_time")!=null&&!ordermap.get("create_time").equals("null")){
					btime = Long.valueOf(ordermap.get("create_time")+"")*1000L;
				}
				if(type==1&&ordermap.get("end_time")!=null&&!ordermap.get("end_time").equals("null")){
					btime = Long.valueOf(ordermap.get("end_time")+"")*1000L;
				}
			}
//			DB db = MongoClientFactory.getInstance().getMongoDBBuilder("zld");//
//			DBCollection collection = db.getCollection("car_pics"+getCollectionName(btime));
//			BasicDBObject document = new BasicDBObject();
//			BasicDBObject condation = new BasicDBObject();
//			document.put("orderid", orderid);
//			document.put("gate", type);
//			//按生成时间查最近的数据
//			condation.put("ctime", -1);
//			DBCursor objs = collection.find(document).sort(condation).limit(1);
//			DBObject obj = null;
//			while (objs.hasNext()) {
//				obj = objs.next();
//			}
//			if(obj==null){
//				db = MongoDBFactory.getInstance().getMongoDBBuilder("zld");//
//				collection = db.getCollection("car_pics"+getCollectionName(btime));
//				document = new BasicDBObject();
//				condation = new BasicDBObject();
//				document.put("orderid", orderid);
//				document.put("gate", type);
//				//按生成时间查最近的数据
//				condation.put("ctime", -1);
//				objs = collection.find(document).sort(condation).limit(1);
//				while (objs.hasNext()) {
//					obj = objs.next();
//				}
//				logger.info("download carpics from oldmongodb...."+orderid+","+type+","+obj);
//			}
//			if(obj == null){
//				AjaxUtil.ajaxOutput(response, "");
//				return;
//			}
//	        outer.read(b)
//	        outer.close(); 
//			byte[] content = (byte[])obj.get("content");
//			db.requestDone();
			response.setDateHeader("Expires", System.currentTimeMillis()+4*60*60*1000);
			//response.setStatus(httpc);
			Calendar c = Calendar.getInstance();
			c.set(1970, 1, 1, 1, 1, 1);
			response.setHeader("Last-Modified", c.getTime().toString());
//			response.setContentLength(inputStream.read());
			response.setContentType("image/jpeg");
		    OutputStream o = response.getOutputStream();
	        byte[] buffer = new byte[1024];
	        int i = -1;
	        String f = CustomDefind.PIC+getCollectionName(btime);
			String picurl = "\\"+orderid + type + ".jpg";
			FileInputStream inputStream = null;
			try{
				inputStream = new FileInputStream(f+picurl); 
				 while ((i = inputStream.read(buffer)) != -1) {
			          o.write(buffer, 0, i);
			        }
			        inputStream.close();
				    o.flush();
				    o.close();
				    response.flushBuffer();
			}catch (Exception e) {
				return;
			}finally{
				if(inputStream!=null){
					inputStream.close();
				}
				if(o!=null){
					o.close();
				}
			}
	       
		    //response.reset();
		    System.out.println("mongdb over.....");
		}else {
			response.sendRedirect("http://sysimages.tq.cn/images/webchat_101001/common/kefu.png");
		}
	}
	public static String getCollectionName(Long milliSeconds) {
		String date =TimeTools.getTimeStr_yyyy_MM_dd(milliSeconds);
		String[] strdate = date.split("-");
//		int str = (Integer.parseInt(strdate[2]))/3;
		return strdate[0]+strdate[1]+strdate[2];
	}
//	public String getCollectionName1() {
//		
//		String[] strdate = date.split("-");
//		int str = (Integer.parseInt(strdate[2]))/3;
//		if(str!=0){
//			str = str-1;
//		}else{
//			int month = Integer.parseInt(strdate[1]);
//			if(month!=1){
//				month = month-1;
//			}else{
//				int year = Integer.parseInt(strdate[0]);
//				
//			}
//		}
//		return strdate[0]+strdate[1]+str;
//	}
	public static void main(String[] args) {
		System.out.println(TimeTools.getTimeStr_yyyy_MM_dd(System.currentTimeMillis()));
		System.out.println(getCollectionName(System.currentTimeMillis()));
	}
	public String requestLine(final String url,Map map){
//	    ExecutorService executor = Executors.newSingleThreadExecutor();  
//	    FutureTask<String> future =  
//	           new FutureTask<String>(new Callable<String>() {//使用Callable接口作为构造参数  
//	             public String call() {
	            	 HttpProxy httpProxy = new HttpProxy();
	            	 String ret = null;
	            	 try {
	            		 ret = httpProxy.doPost(url,map);
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.out.println("tongbufanhui:"+ret);
					return ret;  
//	           }});  
//	    executor.execute(future);  
//	    try {  
//	        String result = future.get(5000, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果  
//	        System.out.println(result);
//	        if(result!=null&&result.startsWith("1")){
//	        }
//	        return result;
//	    } catch (InterruptedException e) {  
//	    	future.cancel(true);  
//	    } catch (ExecutionException e) {  
//	    	future.cancel(true);  
//	    } catch (TimeoutException e) { 
//	    	future.cancel(true);  
//	    }catch (Exception e) { 
//	    	future.cancel(true);  
//	    } finally {  
//	        executor.shutdown();  
//	    }
//		return null;  
	}
	/**
	 * 从网络获取log图片并保存
	 * @param path请求地址
	 * @return
	 * @throws IOException 异常后再次获取下
	 */
	public static void getLOGFromLine(String path) throws IOException{
	    URL url = new URL(path);
	    InputStream inputStream = null;
	    OutputStream os = null;
	    try {
	    	HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	  	    conn.setConnectTimeout(5000);
	  	    conn.setRequestMethod("GET");
	  	    conn.connect();
	  	    if(conn.getResponseCode() == 200){
	  	    	inputStream = conn.getInputStream();
	  	    	String filename = conn.getHeaderField("Content-disposition");
	  	    	if(filename!=null){
	  	    		filename = URLDecoder.decode(filename, "UTF-8");
	  	    		filename = filename.substring(21);
	  	    	}
	  	    	String f = CustomDefind.TOMCATHOMT+"//logo";
				File file = new File(f);
	  		    if(!file.exists()){
	  		    	file.mkdirs();
	  		    }
			   os = new FileOutputStream(f+"//"+filename);
			   int bytesRead = 0;
			   byte[] buffer = new byte[8192];
			   while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
			    os.write(buffer, 0, bytesRead);
			   }
			   os.flush();
	  	    } 
	    }catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(inputStream!=null){
				inputStream.close();
			}
			if(os!=null){
				os.close();
			}
		}
	}
	public Boolean getLog(HttpServletResponse response) throws IOException{
		String f = CustomDefind.TOMCATHOMT+"//logo";
		File file = new File(f);
		if(!file.exists()){
			file.mkdirs();
		}
		File[] files = file.listFiles();
		Boolean hasLOGO = false;
		if(files!=null&&files.length>0){
			for (int i = 0; i < files.length; i++) {
				if(files[i].isFile()&&files[i].getName().endsWith(".jpg")){
					FileInputStream inputStream = null;
					OutputStream o = null;
					try{
						response.setDateHeader("Expires", System.currentTimeMillis()+12*60*60*1000);
						response.setContentType("image/jpeg");
						response.setHeader("Content-disposition", "attachment; filename="
								+ URLEncoder.encode(files[i].getName(), "UTF-8"));
						byte[] buffer = new byte[1024];
						o = response.getOutputStream();
						inputStream = new FileInputStream(files[i].getPath()); 
						 while ((i = inputStream.read(buffer)) != -1) {
					          o.write(buffer, 0, i);
					        }
					        inputStream.close();
						    o.flush();
						    hasLOGO = true;
						    o.close();
						    response.flushBuffer();
					}catch (Exception e) {
						e.printStackTrace();
					}finally{
						if(inputStream!=null){
							inputStream.close();
						}
						if(o!=null){
							o.close();
						}
					}
					break;
				}
			}
		}
		return hasLOGO;
	}
}

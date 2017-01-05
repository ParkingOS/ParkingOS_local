package com.zld.struts.request;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.Check;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

public class ParkEditAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;

	
	private Logger logger = Logger.getLogger(ParkEditAction.class);
	/**
	 * 车场端设置
	 */
	@SuppressWarnings({ "rawtypes"})
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comId = RequestUtil.getLong(request, "comid", -1L);
		if(comId==-1){
			AjaxUtil.ajaxOutput(response, "-1");
			return null;
		}
		if(action.equals("queryprice")){//取停车场数据，登录后加载，传入时间，初始登录没有时间
			Map<String,Object> map = getPrice(comId);
			Integer isNight = (Integer)daService.getObject("select isnight from com_info_Tb where id=?", new Object[]{comId}, Integer.class);
			map.put("isnight", isNight);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://127.0.0.1/zld/parkedit.do?action=queryprice&comid=3
		}else if(action.equals("addprice")){
			Long comid = RequestUtil.getLong(request, "comid", -1L);
			if(comid==-1){
				AjaxUtil.ajaxOutput(response, "-3");
				return null;
			}
			//时段
			Integer btime = RequestUtil.getInteger(request, "b_time", 7);
			Integer etime = RequestUtil.getInteger(request, "e_time", 21);
			//白天价格
			Double price = RequestUtil.getDouble(request, "price", 0d);
			Double fprice = RequestUtil.getDouble(request, "fprice", 0d);
			Integer ftime = RequestUtil.getInteger(request, "first_times", 0);
			Integer countless = RequestUtil.getInteger(request, "countless", 0);
			Integer unit = RequestUtil.getInteger(request, "unit", 0);
			Integer pay_type = RequestUtil.getInteger(request, "pay_type", 0);
			Integer free_time = RequestUtil.getInteger(request, "free_time", 0);//免费时长，单位:分钟
			Integer fpay_type = RequestUtil.getInteger(request, "fpay_type", 0);//超免费时长计费方式，1:免费 ，0:收费
			Integer isnight = RequestUtil.getInteger(request, "isnight", 0);//夜晚停车，0:支持，1不支持
			//夜晚价格
			Integer nftime = RequestUtil.getInteger(request, "nfirst_times", 0);
			Integer ncountless = RequestUtil.getInteger(request, "ncountless", 0);
			Double nfprice = RequestUtil.getDouble(request, "nfprice", 0d);
			Integer npay_type = RequestUtil.getInteger(request, "npay_type", 0);
			Integer nfree_time = RequestUtil.getInteger(request, "nfree_time", 0);//免费时长，单位:分钟
			Integer nfpay_type = RequestUtil.getInteger(request, "nfpay_type", 0);//超免费时长计费方式，1:免费 ，0:收费
			Integer nunit = RequestUtil.getInteger(request, "nunit", 0);
			Double nprice = RequestUtil.getDouble(request, "nprice", 0d);
			
			int result = daService.update("insert into price_tb (comid,price,unit,pay_type,b_time,e_time,first_times,fprice,free_time,fpay_type,countless,create_time) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?) ", new Object[]{comid,price,unit,pay_type,btime,etime,ftime,fprice,free_time,fpay_type,countless,System.currentTimeMillis()/1000});
			if(isnight==0)//支持夜间停车
				daService.update("insert into     price_tb (comid,price,unit,pay_type,b_time,e_time,first_times,fprice,free_time,fpay_type,countless,create_time) " +
					"values(?,?,?,?,?,?,?,?,?,?,?,?) ", new Object[]{comid,nprice,nunit,npay_type,etime,btime,nftime,nfprice,nfree_time,nfpay_type,ncountless,System.currentTimeMillis()/1000});
			if(comid!=-1)
				daService.update("update com_info_tb set isnight=? where id =?",new Object[]{isnight,comid});
			if(result==1)
				AjaxUtil.ajaxOutput(response, "1");
			else 
				AjaxUtil.ajaxOutput(response, "-2");
			// http:192.168.1.148/zld/parkedit.do?action=addprice
			// retrun -1,车场编号不合法，1添加成功，-2添加失败
		}else if(action.equals("editprice")){
			// http:192.168.1.148/zld/parkedit.do?action=editprice&comid=&id=&nid=
			Long comid = RequestUtil.getLong(request, "comid", -1L);
			if(comid==-1){
				AjaxUtil.ajaxOutput(response, "-3");
				return null;
			}
			//价格编号
			Long id = RequestUtil.getLong(request, "id", -1L);
			Long nid = RequestUtil.getLong(request, "nid", -1L);
			//时段
			Integer btime = RequestUtil.getInteger(request, "b_time", 7);
			Integer etime = RequestUtil.getInteger(request, "e_time", 21);
			//白天价格
			Double price = RequestUtil.getDouble(request, "price", 0d);
			Double fprice = RequestUtil.getDouble(request, "fprice", 0d);
			Integer ftime = RequestUtil.getInteger(request, "first_times", 0);
			Integer countless = RequestUtil.getInteger(request, "countless", 0);
			Integer unit = RequestUtil.getInteger(request, "unit", 0);
			Integer pay_type = RequestUtil.getInteger(request, "pay_type", 0);
			Integer free_time = RequestUtil.getInteger(request, "free_time", 0);//免费时长，单位:分钟
			Integer fpay_type = RequestUtil.getInteger(request, "fpay_type", 0);//超免费时长计费方式，1:免费 ，0:收费
			Integer isnight = RequestUtil.getInteger(request, "isnight", 0);//夜晚停车，0:支持，1不支持
			
			//夜晚价格
			Integer nftime = RequestUtil.getInteger(request, "nfirst_times", 0);
			Integer ncountless = RequestUtil.getInteger(request, "ncountless", 0);
			Double nfprice = RequestUtil.getDouble(request, "nfprice", 0d);
			Integer npay_type = RequestUtil.getInteger(request, "npay_type", 0);
			Integer nfree_time = RequestUtil.getInteger(request, "nfree_time", 0);//免费时长，单位:分钟
			Integer nfpay_type = RequestUtil.getInteger(request, "nfpay_type", 0);//超免费时长计费方式，1:免费 ，0:收费
			Integer nunit = RequestUtil.getInteger(request, "nunit", 0);
			Double nprice = RequestUtil.getDouble(request, "nprice", 0d);
			
			int result = daService.update("update  price_tb  set price =?,unit=?,pay_type=?,b_time=?," +
					"e_time=?,first_times=?,countless=?,fprice=?,free_time=?,fpay_type=? where id=? and comid=?", 
					new Object[]{price,unit,pay_type,btime,etime,ftime,countless,fprice,free_time,fpay_type,id,comid});
			if(isnight==0)//支持夜间停车
				daService.update("update  price_tb  set price =?,unit=?,pay_type=?,b_time=?," +
					"e_time=?,first_times=?,countless=?,fprice=?,free_time=?,fpay_type=? where id=? and comid=?", 
					new Object[]{nprice,nunit,npay_type,etime,btime,nftime,ncountless,nfprice,nfree_time,nfpay_type,nid,comid});
			if(comid!=-1)
				daService.update("update com_info_tb set isnight=? where id =?",new Object[]{isnight,comid});
			
			if(result==1){
				AjaxUtil.ajaxOutput(response, "1");
				 //SystemMemcachee.PriceMap.remove(comid);
				//logger.info(comid+"从缓存中清除价格....");
			}
			else 
				AjaxUtil.ajaxOutput(response, "-2");
			System.err.println(result);
		}else if(action.equals("uploadpic")){
			//String result = uploadParkPics2Mongodb(request,comId);
			String picurl = publicMethods.uploadPicToMongodb(request, comId, "park_pics");
			int result= daService.update("insert into com_picturs_tb(comid,picurl,create_time)" +
		  	    		"values(?,?,?) ", new Object[]{comId,"parkpics/"+picurl,System.currentTimeMillis()/1000});
			/*Double longitude =RequestUtil.getDouble(request, "longitude",0.0);
			Double latitude =RequestUtil.getDouble(request, "latitude",0.0);
			logger.info("更新经纬度:"+longitude+","+latitude);
			if(longitude!=0&&latitude!=0){
				int re = daService.update("update com_info_tb set longitude=?,latitude=? where id = ?",
						new Object[]{longitude,latitude,comId});
				logger.info("更新经纬度:"+re);
			}*/
			AjaxUtil.ajaxOutput(response, result+"");
			// http:192.168.1.148/zld/parkedit.do?action=uploadpic
		}
		return null;
	}
	/*
	private String uploadParkPics2Mongodb (HttpServletRequest request,Long comid) throws Exception{
		logger.info("begin upload picture....");
		Map<String, String> extMap = new HashMap<String, String>();
	    extMap.put(".jpg", "image/jpeg");
	    extMap.put(".jpeg", "image/jpeg");
	    extMap.put(".png", "image/png");
	    extMap.put(".gif", "image/gif");
		//String path ="D:/yxd/tomcat6/tomcat7/webapps/zld/parkpics/";
		request.setCharacterEncoding("UTF-8"); // 设置处理请求参数的编码格式
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // 建立FileItemFactory对象
		factory.setSizeThreshold(16*4096*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 分析请求，并得到上传文件的FileItem对象
		upload.setSizeMax(16*4096*1024);
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			return "-1";
		}
		String filename = ""; // 上传文件保存到服务器的文件名
		InputStream is = null; // 当前上传文件的InputStream对象
		// 循环处理上传文件
		String comId = "";
		for (FileItem item : items){
			// 处理普通的表单域
			if (item.isFormField()){
				if(item.getFieldName().equals("comid")){
					if(!item.getString().equals(""))
						comId = item.getString("UTF-8");
				}
				
			}else if (item.getName() != null && !item.getName().equals("")){// 处理上传文件
				// 从客户端发送过来的上传文件路径中截取文件名
				logger.info(item.getName());
				filename = item.getName().substring(
						item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // 得到上传文件的InputStream对象
				
			}
		}
		if(comid==null&&(comId.equals("")||!Check.isLong(comId)))
			return "-1";
		String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// 扩展名
		String picurl = comid+"_"+System.currentTimeMillis()/1000+file_ext;
		BufferedInputStream in = null;  
		ByteArrayOutputStream byteout =null;
	    try {
	    	in = new BufferedInputStream(is);   
	    	byteout = new ByteArrayOutputStream(1024);        	       
		      
	 	    byte[] temp = new byte[1024];        
	 	    int bytesize = 0;        
	 	    while ((bytesize = in.read(temp)) != -1) {        
	 	          byteout.write(temp, 0, bytesize);        
	 	    }        
	 	      
	 	    byte[] content = byteout.toByteArray(); 
	 	    DB mydb = MongoClientFactory.getInstance().getMongoDBBuilder("zld");
		    mydb.requestStart();
			  
		    DBCollection collection = mydb.getCollection("park_pics");
		  //  DBCollection collection = mydb.getCollection("records_test");
			  
			BasicDBObject document = new BasicDBObject();
			document.put("comid",  comid);
			document.put("ctime",  System.currentTimeMillis()/1000);
			document.put("type", extMap.get(file_ext));
			document.put("content", content);
			document.put("filename", picurl);
			  //开始事务
			mydb.requestStart();
			collection.insert(document);
			  //结束事务
			mydb.requestDone();
			in.close();        
		    is.close();
		    byteout.close();
	  	  
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}finally{
			if(in!=null)
				in.close();
			if(byteout!=null)
				byteout.close();
			if(is!=null)
				is.close();
		}
	  
		return "1";
	}*/
	
	@SuppressWarnings("unchecked")
	private String uploadPicture(HttpServletRequest request,Long comid) throws Exception{
		logger.info("begin upload picture....");
		String path= "/data/jtom/webapps/zld/parkpics/";
		//String path ="D:/yxd/tomcat6/tomcat7/webapps/zld/parkpics/";
		request.setCharacterEncoding("UTF-8"); // 设置处理请求参数的编码格式
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // 建立FileItemFactory对象
		factory.setSizeThreshold(16*4096*1024);
		factory.setRepository(new File(path));
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 分析请求，并得到上传文件的FileItem对象
		upload.setSizeMax(16*4096*1024);
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			return "-1";
		}
		String filename = ""; // 上传文件保存到服务器的文件名
		InputStream is = null; // 当前上传文件的InputStream对象
		// 循环处理上传文件
		String comId = "";
		for (FileItem item : items){
			// 处理普通的表单域
			if (item.isFormField()){
				if(item.getFieldName().equals("comid")){
					if(!item.getString().equals(""))
						comId = item.getString("UTF-8");
				}
				
			}else if (item.getName() != null && !item.getName().equals("")){// 处理上传文件
				// 从客户端发送过来的上传文件路径中截取文件名
				logger.info(item.getName());
				filename = item.getName().substring(
						item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // 得到上传文件的InputStream对象
				
			}
		}
		if(comid==null&&(comId.equals("")||!Check.isLong(comId)))
			return "-1";
		String picurl = comid+"_"+System.currentTimeMillis()/1000+filename.substring(filename.lastIndexOf("."));
		String fileName = path+picurl;
		BufferedInputStream in = null;       
	    OutputStream bos = null;
	    try {
	    	in = new BufferedInputStream(is);   
	    	bos = new FileOutputStream(new File(fileName));
	  	    byte[] temp = new byte[1024];        
	  	    int bytesize = 0;        
	  	    while ((bytesize = in.read(temp)) != -1) {        
	  	    	bos.write(temp, 0, bytesize);        
	  	    }        
	  	    bos.flush();
	  	    bos.close();
	  	    in.close();  
	  	    daService.update("insert into com_picturs_tb(comid,picurl,create_time)" +
	  	    		"values(?,?,?) ", new Object[]{comid,"parkpics/"+picurl,System.currentTimeMillis()/1000});
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}finally{
			if(bos!=null)
				bos.close();
			if(in!=null)
				in.close();
		}
	  
		return "1";
	}

	private Map getPrice(Long comId){
		Map dayMap=null;//日间策略
		Map nigthMap=null;//夜间策略
		List<Map> priceList=daService.getAll("select id,price,unit,pay_type,b_time,e_time,first_times,fprice,countless,fpay_type,free_time from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{comId,0,0});
		if(priceList!=null&&priceList.size()>0){
			dayMap= priceList.get(0);
			boolean pm1 = false;//找到map1,必须是结束时间大于开始时间
			boolean pm2 = false;//找到map2
			if(priceList.size()>1){
				for(Map map : priceList){
					if(pm1&&pm2)
						break;
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime==null||etime==null)
						continue;
					if(etime>btime){
						if(!pm1){
							dayMap = map;
							pm1=true;
						}
					}else {
						if(!pm2){
							nigthMap=map;
							pm2=true;
						}
					}
				}
			}
		}else {
			return new HashMap<String, Object>();
		}
		if(nigthMap!=null&&dayMap!=null){
			dayMap.put("nid", nigthMap.get("id"));
			dayMap.put("nprice", nigthMap.get("price"));
			dayMap.put("nunit", nigthMap.get("unit"));
			dayMap.put("nfirst_times", nigthMap.get("first_times"));
			dayMap.put("ncountless", nigthMap.get("countless"));
			dayMap.put("npay_type", nigthMap.get("pay_type"));
			dayMap.put("nfpay_type", nigthMap.get("fpay_type"));
			dayMap.put("nfree_time", nigthMap.get("free_time"));
			dayMap.put("nfprice", nigthMap.get("fprice"));
		}
		else if(dayMap!=null){
			dayMap.put("nprice","-1");
			dayMap.put("nid", "-1");
			dayMap.put("nunit","-1");
		}
		else {
			return new HashMap<String, Object>();
		}
		return dayMap;
	}
}
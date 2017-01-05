package com.zld.struts.request;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zld.AjaxUtil;
import com.zld.impl.MongoClientFactory;
import com.zld.impl.MongoDbUtils;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.RequestUtil;
import com.zld.utils.SendMessage;
import com.zld.utils.StringUtils;


/**
 * 注册停车场，收费员
 * @author Administrator
 * 2014-12-23 
 */
public class RegisterParkAction extends Action {

	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private MongoDbUtils mongoDbUtils;
	@Autowired
	private LogService logService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(ParkCollectorLoginAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.getString(request, "action");
		String mobile = RequestUtil.getString(request, "mobile");
		if(action.equals("getcode")){//获取验证码
			//查询该手机号是否注册过管理员
			Map parkadminMap = daService.getMap("select id,state,recom_code from user_info_tb where mobile=? and auth_flag= ? ",
					new Object[]{mobile,1});
			if(parkadminMap != null){
				Integer pstate = (Integer)parkadminMap.get("state");
				Long puin = (Long)parkadminMap.get("id");
				if(pstate==0){//状态:正常用户
					//删除已经保存但没有验证过的验证码（已无效的验证码）
					daService.update("delete from verification_code_tb where uin =?",new Object[]{puin});
					SendMessage.sendMultiMessage(mobile, "您的手机号已注册过收费员，账号："+puin+",请直接用此账号登录 【停车宝】 ");
					AjaxUtil.ajaxOutput(response, "-4");//已注册,正常用户
					return null;
				}else if(pstate==1){//状态:禁用状态
					//删除已经保存但没有验证过的验证码（已无效的验证码）
					daService.update("delete from verification_code_tb where uin =?",new Object[]{puin});
					SendMessage.sendMultiMessage(mobile, "您的手机号已注册过收费员，账号："+puin+",目前已禁用，请联系停车宝客服010-53618108 【停车宝】");
					AjaxUtil.ajaxOutput(response, "-5");//已注册，但已禁用
					return null;
				}
			}
			//查询手机号存在的收费员
			Long uin= null;
			Map userMap = daService.getMap("select id,state,recom_code from user_info_tb where mobile=? and auth_flag= ? ",
					new Object[]{mobile,2});
			Integer state = 0;
			if(userMap!=null&&userMap.get("id")!=null){
				state=(Integer)userMap.get("state");
				uin = (Long)userMap.get("id");
			}
			int r = 0;
			if(uin==null||uin==-1){//不存在收费员，注册收费员
				uin= daService.getkey("seq_user_info_tb");
				r = createCollectorInfo(request, uin);
				if(r!=1){
					AjaxUtil.ajaxOutput(response, "-1");//注册失败
					return null;
				}
			}else {
				if(userMap.get("recom_code") ==null){
					if(state==0){//状态:正常用户
						//删除已经保存但没有验证过的验证码（已无效的验证码）
						daService.update("delete from verification_code_tb where uin =?",new Object[]{uin});
						SendMessage.sendMultiMessage(mobile, "您的手机号已注册过收费员，账号："+uin+",请直接用此账号登录 【停车宝】 ");
						AjaxUtil.ajaxOutput(response, "-4");//已注册,正常用户
						return null;
					}else if(state==1){//状态:禁用状态
						//删除已经保存但没有验证过的验证码（已无效的验证码）
						daService.update("delete from verification_code_tb where uin =?",new Object[]{uin});
						SendMessage.sendMultiMessage(mobile, "您的手机号已注册过收费员，账号："+uin+",目前已禁用，请联系停车宝客服010-53618108 【停车宝】");
						AjaxUtil.ajaxOutput(response, "-5");//已注册，但已禁用
						return null;
					}else if(state != 0 && state != 1){
						Long count = daService.getLong("select count(*) from parkuser_account_tb where uin=? and type=? and target=? ", new Object[]{uin,0,3});
						if(count > 0){//客户端注册，无推荐码
							AjaxUtil.ajaxOutput(response, "-6");
							return null;
						}
					}
				}else{
					daService.update("delete from verification_code_tb where uin =?",new Object[]{uin});
					SendMessage.sendMultiMessage(mobile, "您的手机号已注册过收费员，账号："+uin+",请直接用此账号登录  【停车宝】");
					AjaxUtil.ajaxOutput(response, "-6");//已注册,正常用户
					return null;
				}
				//等待审核状态，可以继续发送验证码
			}
			//发送并返回验证码
//			if(!publicMethods.isCanSendShortMesg(mobile)){
//				AjaxUtil.ajaxOutput(response, "-2");
//				return null;
//			}
			Integer code = new SendMessage().sendMessageToCarOwer(mobile);
			if(code!=null){
				logger.info("code:"+code+",mobile:"+mobile);
				//保存验证码
				r =daService.update("insert into verification_code_tb (verification_code,uin,create_time,state)" +
						" values (?,?,?,?)", new Object[]{code,uin,System.currentTimeMillis()/1000,0});
				if(r==1){
					AjaxUtil.ajaxOutput(response, "1");//成功发送并保存验证码
				}else{
					AjaxUtil.ajaxOutput(response, "-2");//验证码保存失败
				}
			}else {
				AjaxUtil.ajaxOutput(response, "-3");//发送验证码失败
			}
			//http://192.168.199.240/zld/regparker.do?action=getcode&mobile=15801482643
		}else if(action.equals("validcode")){//验证发回的验证码
			String vcode =RequestUtil.processParams(request, "code");
			String sql = "select * from user_info_tb where mobile=? and auth_flag=?";
			Map user = daService.getPojo(sql, new Object[]{mobile,2});
			if(user==null){
				AjaxUtil.ajaxOutput(response, "-2");//用户不存在
				return null;
			}
			Long uin = Long.valueOf(user.get("id")+"");
			Map verificationMap = daService.getPojo("select verification_code from verification_code_tb" +
					" where uin=? and state=? ", new Object[]{uin,0});
			if(verificationMap==null){
				AjaxUtil.ajaxOutput(response, "-3");//未产生验证码
				return null;
			}
			String code = verificationMap.get("verification_code").toString();
			if(code.equals(vcode)){//验证码匹配成功
				//删除验证码表
				daService.update("delete from verification_code_tb where uin =?",new Object[]{uin});
				//更新车主状态 ，在线，保存登录时间
				daService.update("update user_info_tb set online_flag=? ,logon_time=? where id=?", new Object[]{22,System.currentTimeMillis()/1000,uin});
				AjaxUtil.ajaxOutput(response, "1");//验证码匹配成功
			}else{
				AjaxUtil.ajaxOutput(response, "-1");//验证码匹配失败
			}
			//http://192.168.199.240/zld/regparker.do?action=validcode&code=1580&mobile=15801482643
		}
		else if(action.equals("getmesgcode")){//获取发短信验证码和发送地址号
			Map<String,Object> infoMap = new HashMap();//返回值
			//查询手机号存在的收费员
			Long uin= null;
			if(!mobile.equals("")){
				Map userMap  = daService.getMap("select id  from user_info_tb where mobile=? and auth_flag= ?",
						new Object[]{mobile,2});
				if(userMap!=null&&userMap.get("id")!=null)
					uin = (Long)userMap.get("id");
			}else {
				infoMap.put("mesg", "-1");
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//注册失败
				return null;
			}
					
			int r = 0;
			if(uin==null||uin==-1){//不存在收费员，注册收费员
				uin= daService.getkey("seq_user_info_tb");
				r = createCollectorInfo(request, uin);
				if(r!=1){
					infoMap.put("mesg", "-1");
					AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//注册失败
					return null;
				}
			}else {
				//删除已经保存但没有验证过的验证码（已无效的验证码）
				daService.update("delete from verification_code_tb where uin =?",new Object[]{uin});
			}
			//生成六位验证码
			Integer code = new Random(System.currentTimeMillis()).nextInt(1000000);
			if(code<99)
				code=code*10000;
			if(code<999)
				code =code*1000;
			if(code<9999)
				code = code*100;
			if(code<99999)
				code = code*10;
			logger.info("code:"+code+",mobile:"+mobile);
			
			//保存验证码
			r =daService.update("insert into verification_code_tb (verification_code,uin,create_time,state)" +
					" values (?,?,?,?)", new Object[]{code,uin,System.currentTimeMillis()/1000,0});
			if(r==1){//成功生成验证码
				infoMap.put("mesg", "1");
				infoMap.put("code", code+"00");
				infoMap.put("tomobile", getToMobile(mobile));
			}else{//生成验证码失败
				infoMap.put("mesg", "-2");
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			//http://192.168.199.240/zld/regparker.do?action=getmesgcode&mobile=15801482643
		}else if(action.equals("uploadpic")){
			Long uin= null;
			Map userMap = daService.getMap("select id,state,collector_pics from user_info_tb where mobile=? and auth_flag= ? ",
					new Object[]{mobile,2});
			Integer state = -1;
			if(userMap!=null&&userMap.get("id")!=null){
				state=(Integer)userMap.get("state");
				uin = (Long)userMap.get("id");
			}
			String ret = "0";
			if(uin !=null&&state==2){
				ret=publicMethods.uploadPicToMongodb(request, uin, "parkuser_pics");//uploadParkPics2Mongodb(request, uin);
			}
			if(!ret.equals("-1")){
				Integer collector_pics = (Integer)userMap.get("collector_pics");
				collector_pics += 1;
				ret = daService.update("update user_info_tb set collector_pics=? where id=? ", new Object[]{collector_pics,uin})+"";
			}
			AjaxUtil.ajaxOutput(response, ret + "");
		}else if(action.equals("find")){
			mongoDbUtils.getParkPicUrls(1197L);
			//http://192.168.199.240/zld/regparker.do?action=find&mobile=15801482643
		}else if(action.equals("toregpage")){
			String recomcode = RequestUtil.processParams(request, "recomcode");
			request.setAttribute("recomcode", recomcode);
			return mapping.findForward("collectorreg");
		}else if(action.equals("collectorreg")){//处理车主或收费员推荐收费员
			//处理车主或收费员账号，根据auth_flag查出是车主或收费员（4车主，2或1车场收费员）
			Long pid = RequestUtil.getLong(request, "recomcode", -1L);//推荐人的推荐码
			String nickname = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));//收费员名称
			if(pid == -1 || nickname.equals("") || mobile.equals("")){//网络延迟,数据没有提交过来
				return mapping.findForward("error");
			}
			String sql = "select * from user_info_tb where mobile=? and auth_flag=?";
			Map user = daService.getPojo(sql, new Object[]{mobile,2});
			Long nid = null;
			String oldpass = "";
			if(user != null){
				nid = (Long)user.get("id");
				oldpass = (String)user.get("password");
			}else {
				return mapping.findForward("error");
			}
			//刷新页面防止重复返现
			if(user.get("recom_code") != null){
				//停车费金额
				Double nowbalance = Double.valueOf(user.get("balance")+"");
				request.setAttribute("balance", nowbalance);
				String epaysql = "select sum(order_total) epaymoney from order_message_tb where uin=? and state=?";
				Map<String, Object> map = daService.getMap(epaysql, new Object[]{nid,2});
				Double epay = 0.00d;
				if(map.get("epaymoney") != null){
					epay = Double.valueOf(map.get("epaymoney") + "");
				}
				request.setAttribute("epaymoney", epay);
				//返现金额
				String backsql = "select sum(amount) backmoney from parkuser_account_tb where uin=? and type=? and target=? ";
				Map<String, Object> backmap = daService.getMap(backsql, new Object[]{nid,0,3});
				Double backmoney = 0.00d;
				if(backmap.get("backmoney") != null){
					backmoney = Double.valueOf(backmap.get("backmoney") + "");
				}
				request.setAttribute("backmoney", backmoney);
				request.setAttribute("nickname", nickname);
				request.setAttribute("uin", nid);
				return mapping.findForward("collect");
			}
			Double nbalance = Double.valueOf(user.get("balance")+"") + 10.00d;
			//4车主，2或1车场收费员
			Long auth_flag = null;
			String recmobile = null;
			Map recomuser = daService.getPojo("select * from user_info_tb where id=?", new Object[]{pid});
			if(recomuser == null){
				return mapping.findForward("error");
			}else{
				auth_flag = (Long)recomuser.get("auth_flag");
				if(recomuser.get("mobile") != null){
					recmobile = (String)recomuser.get("mobile");
				}
			}
			
			boolean b = false;
			List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
			
			logger.info ("推荐收费员注册，返现...");
			//写推荐记录
			Map<String, Object> recomsqlMap = new HashMap<String, Object>();
			recomsqlMap.put("sql", "insert into recommend_tb(pid,nid,type,state,create_time) values(?,?,?,?,?)");
			recomsqlMap.put("values", new Object[]{pid,nid,1,0,System.currentTimeMillis()/1000});
			sqlMaps.add(recomsqlMap);
			
			//收费员注册成功后充值10元
			Map<String, Object> colAccountsqlMap = new HashMap<String, Object>();
			colAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
			colAccountsqlMap.put("values", new Object[]{nid,10.00,0,System.currentTimeMillis()/1000,"收费员注册成功,返现10元",3});
			sqlMaps.add(colAccountsqlMap);
			
			//更新被推荐的收费员名字和推荐码,充值10元
			Map<String, Object> usersqlMap = new HashMap<String, Object>();
			usersqlMap.put("sql", "update user_info_tb set recom_code=?,nickname=?,balance=? where id=? ");
			usersqlMap.put("values", new Object[]{pid,nickname,nbalance,nid});
			sqlMaps.add(usersqlMap);
			
			//车主推荐车场时
			if(auth_flag==4){
				//车主返5元
				Double pbalance = Double.valueOf(recomuser.get("balance")+"") + 5.00d;
				
				Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
				userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
				userAccountsqlMap.put("values", new Object[]{pid,5.00,0,System.currentTimeMillis()/1000,"推荐收费员成功,返现5元",8});
				sqlMaps.add(userAccountsqlMap);
				
				Map<String, Object> recomusersqlMap = new HashMap<String, Object>();
				recomusersqlMap.put("sql", "update user_info_tb set balance=? where id=? ");
				recomusersqlMap.put("values", new Object[]{pbalance,pid});
				sqlMaps.add(recomusersqlMap);
			}
			try {
				b= daService.bathUpdate(sqlMaps);
				if(b){//消息提醒
					if(auth_flag==4)
						logService.insertUserMesg(6, pid, "您推荐了收费员"+nickname+"，获得5元奖励，审核成功后将再返25元奖励。", "推荐提醒");
					else {
						//推荐车主，收费员积1分
						/*Map userMap = daService.getMap("select comid from user_info_Tb where id = ? ", new Object[]{pid});
						if(userMap!=null){
							logService.updateScroe(5, pid, (Long)userMap.get("comid"));
						}*/
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("推荐收费员注册,返现...", e);
				return mapping.findForward("error");
			}
			if(!b){
				return mapping.findForward("error");
			}
			request.setAttribute("uin", nid);
			request.setAttribute("nickname", nickname);
			request.setAttribute("balance", nbalance);
			request.setAttribute("epaymoney", 0.00d);
			request.setAttribute("backmoney", 0.00d);
			String parkappUrl = "http://t.cn/RZ2wZd1 ";
			String message = "您的停车宝账户已经注册成功，账号："+nid+"，初始密码为"+oldpass+"。下载停车宝App有更多惊喜"+parkappUrl+" 【停车宝】";
			SendMessage.sendMultiMessage(mobile, message);
			String msg ="";
			if(auth_flag==4){
				msg="停车宝小伙伴您好，您推荐的收费员"+nickname+"已完成注册，5元奖金已到账，另25元将于收费员审核完成后到账 【停车宝】";
			}else {
				msg="停车宝小伙伴您好，您推荐的收费员"+nickname+"已完成注册，停车宝审核完成后您的30元奖励到账 【停车宝】";
			}
			if(recmobile != null){
				SendMessage.sendMultiMessage(recmobile, msg);
			}
			return mapping.findForward("collect");
		}else if(action.equals("download")){
			return mapping.findForward("download");
		}else if(action.equals("getbalance")){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			if(uin == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			//查询余额
			String sql = "select balance from user_info_tb where id=?";
			Map<String, Object> map = new HashMap<String, Object>();
			map = daService.getMap(sql, new Object[]{uin});
			Double balance = 0.00d;
			if(map != null){
				balance = Double.valueOf(map.get("balance") + "");
			}
			infoMap.put("balance", String.format("%.2f",balance));
			//查询支付的金额
			sql = "select sum(order_total) epaymoney from order_message_tb where uin=? and state=?";
			Map<String, Object> epaymap = new HashMap<String, Object>();
			epaymap = daService.getMap(sql, new Object[]{uin,2});
			Double epay = 0.00d;
			if(epaymap.get("epaymoney") != null){
				epay = Double.valueOf(epaymap.get("epaymoney") + "");
			}
			infoMap.put("epaymoney", String.format("%.2f",epay));
			//查询停车宝返现
			sql = "select sum(amount) backmoney from parkuser_account_tb where uin=? and type=? and target=? ";
			Map<String, Object> backmap = new HashMap<String, Object>();
			backmap = daService.getMap(sql, new Object[]{uin,0,3});
			Double backmoney = 0.00d;
			if(backmap.get("backmoney") != null){
				backmoney = Double.valueOf(backmap.get("backmoney") + "");
			}
			infoMap.put("backmoney", String.format("%.2f",backmoney));
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
		}
		return null;
	}
	
	private int createCollectorInfo(HttpServletRequest request,Long uin){
		String mobile =RequestUtil.processParams(request, "mobile");
		if(mobile.equals("")) mobile=null;
		Long time = System.currentTimeMillis()/1000;
		
		String strid ="zld"+uin;
		String md5pass = StringUtils.getParkUserPass();//mobile.substring(5);
		String password  = md5pass;
		try {
			md5pass = StringUtils.MD5(md5pass);
			md5pass = StringUtils.MD5(md5pass+"zldtingchebao201410092009");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//新建收费员，未审核状态
		String sql= "insert into user_info_tb (id,nickname,password,strid," +
				"reg_time,mobile,auth_flag,comid,md5pass,state) " +
				"values (?,?,?,?,?,?,?,?,?,?)";
		Object[] values= new Object[]{uin,"收费员",password,strid,
				time,mobile,2,1,md5pass,2};
		
		int r = daService.update(sql,values);
		return r;
	}
	private String  getToMobile(String mobile){
		//移动 联通  106901336275
		//电信是1069004270441
		//中国电信：133,153,177,180,181,189
		if(mobile.startsWith("133")||mobile.startsWith("153")
				||mobile.startsWith("177")||mobile.startsWith("180")
				||mobile.startsWith("181")||mobile.startsWith("189")
				||mobile.startsWith("170"))
			return "1069004270441";
		return "106901336275";
	}
	
/*
	private Integer uploadParkPics2Mongodb (HttpServletRequest request,Long uin) throws Exception{
		logger.info("begin upload regist picture....");
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
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			return -1;
		}
		String filename = ""; // 上传文件保存到服务器的文件名
		InputStream is = null; // 当前上传文件的InputStream对象
		// 循环处理上传文件
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
				filename = item.getName().substring(item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // 得到上传文件的InputStream对象
				
			}
		}
		String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// 扩展名
		String picurl = uin+"_"+System.currentTimeMillis()+file_ext;
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
			  
		    DBCollection collection = mydb.getCollection("parkuser_pics");
		  //  DBCollection collection = mydb.getCollection("records_test");
			  
			BasicDBObject document = new BasicDBObject();
			document.put("uin",  uin);
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
			return -1;
		}finally{
			if(in!=null)
				in.close();
			if(byteout!=null)
				byteout.close();
			if(is!=null)
				is.close();
		}
	  
		return 1;
	}*/
	
	
}

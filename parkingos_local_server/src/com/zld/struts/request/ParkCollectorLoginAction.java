package com.zld.struts.request;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.HttpProxy;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
/**
 * 停车场收费员登录 
 * @author Administrator
 *
 */
public class ParkCollectorLoginAction extends Action{
	
	@Autowired
	private DataBaseService daService;
//	@Autowired 
//	private MemcacheUtils memcacheUtils;
	private Logger logger = Logger.getLogger(ParkCollectorLoginAction.class);
	//http://127.0.0.1/zld/collectorlogin.do?username=1000005&action=forpass
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		String out = RequestUtil.processParams(request, "out");
		Map<String,Object> infoMap = new HashMap<String, Object>();
		Integer userState = 0;
		logger.info("action:"+action);
		if(action.equals("forpass")){//找回密码，发送到收费员的注册手机上
			String userId = RequestUtil.processParams(request, "username");
			Map userMap = daService.getPojo("select id,password,mobile from user_info_tb where id=?",
					new Object[]{Long.valueOf(userId)});
			if(userMap!=null){
				String mobile = (String)userMap.get("mobile");
				logger.equals(mobile);
//				if(mobile==null||"".equals(mobile)){
					AjaxUtil.ajaxOutput(response, "{\"info\":\"您注册帐号时没有填写手机，请联系客服人员！\"}");
//				}else if(Check.checkPhone(mobile,"m")){
//					String _mString = mobile.substring(0,3)+"****"+mobile.substring(7);
//					//SendMessage.sendMessage(mobile,(String)userMap.get("password"));
//					AjaxUtil.ajaxOutput(response, "{\"info\":\"密码已通过短信发送到您注册的手机上["+_mString+"]，请查收！【停车宝】\"}");
//				}else {
//					AjaxUtil.ajaxOutput(response, "{\"info\":\"您注册的手机号不合法！\"}");
//				}
			}else {
				AjaxUtil.ajaxOutput(response, "{\"info\":\"帐号不存在！\"}");
			}
			return null;
		}else if(action.equals("editpass")){//修改密码，发送到收费员的注册手机上
			//http://127.0.0.1/zld/collectorlogin.do?username=1000005&action=editpass&oldpass=&newpass=
			Long userId = RequestUtil.getLong(request, "username",-1L);
			String oldPass = RequestUtil.processParams(request, "oldpass");
			String newPass = RequestUtil.processParams(request, "newpass");
			if(oldPass.length()<32){
				oldPass =StringUtils.MD5(oldPass);
				oldPass = StringUtils.MD5(oldPass+"zldtingchebao201410092009");
			}
			Long count  = daService.getLong("select count(*) from user_info_tb where id=? and md5pass=? ", 
					new Object[]{userId,oldPass});
			int result = 0;
			if(newPass.length()<32){
				newPass =StringUtils.MD5(newPass);
				newPass = StringUtils.MD5(newPass+"zldtingchebao201410092009");
			}
			if(count>0){
				result = daService.update("update user_info_tb set md5pass=? where id=? ",
						new Object[]{newPass,userId});
			}else
				result = -1;
			logger.info("oldpass:"+oldPass+",newpass:"+newPass);
			AjaxUtil.ajaxOutput(response, result+"");
			return null;
		}else if(action.equals("editname")){
			String name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			int result = 0;
			if(uin!=-1){
				result = daService.update("update user_info_tb set nickname=? where id=? ",
						new Object[]{name,uin});
			}
			AjaxUtil.ajaxOutput(response, result+"");
			return null;
			//http://192.168.199.240/zld/collectorlogin.do?action=editname&name=1020005&uin=1000005
		}else if(action.equals("editphone")){
			String mobile = RequestUtil.processParams(request, "mobile");
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			int result = 0;
			if(uin!=-1){
				result = daService.update("update user_info_tb set  mobile=? where id=? ",
						new Object[]{mobile,uin});
			}
			AjaxUtil.ajaxOutput(response, result+"");
			return null;
			//http://192.168.199.240/zld/collectorlogin.do?action=editphone&mobile=18003005000&uin=1000005
		}
		//http://127.0.0.1/zld/collectorlogin.do?username=1000005&password=123456
		//http://127.0.0.1/zld/collectorlogin.do?username=10700&password=123456&out=json
		Map limitmap = daService.getMap("select * from sync_time_tb where id = ?", new Object[]{4});//id为4的时候的maxid
		int workret = 0;
		if(limitmap!=null&&limitmap.get("maxid")!=null){
			try {
				if(Long.parseLong(limitmap.get("maxid")+"")<System.currentTimeMillis()/1000){
					infoMap.put("info", "授权已到期");
				}else{
					String username =RequestUtil.processParams(request, "username");
					String pass =RequestUtil.processParams(request, "password");
					String version = RequestUtil.getString(request, "version");
					logger.info("user:"+username+",pass:"+pass);
					String sql = "select * from user_info_tb where id=? and md5pass=?";// and auth_flag=?";
					if(pass.length()<32){
						//md5密码 ，生成规则：原密码md5后，加上'zldtingchebao201410092009'再次md5
						pass =StringUtils.MD5(pass);
						pass = StringUtils.MD5(pass +"zldtingchebao201410092009");
					}
					if(!StringUtils.isNumber(username)){
						infoMap.put("info", "fail");
						AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
						return null;
					}
					Map user = daService.getPojo(sql, new Object[]{Long.valueOf(username),pass});//,ZLDType.ZLD_COLLECTOR_ROLE});
					//logger.info(user);
					if(user==null){
						infoMap.put("info", "用户名或密码错误");
					}else {
						Long comId = (Long)user.get("comid");
						Map comMap = daService.getMap("select * from com_info_tb where id =?", new Object[]{comId});
						//String comPassInfo =isPassCom(comMap);
						Integer state = (Integer)comMap.get("state");
						userState = (Integer)user.get("state");//-- 0:正常，1：禁用，2：待审核，3：待补充，4：待跟进，5无价值
						if(state==1){
							infoMap.put("info", "车场已删除");
						}else if(state==2&&userState<2){
							infoMap.put("info", "车场未审核");
						}
//						else if(userState>0){
//							infoMap.put("info", "您的账户异常，不能登录!");
//						}
						else{
							Long uin = (Long)user.get("id");
							String token = StringUtils.MD5(username+pass+System.currentTimeMillis());
							infoMap.put("info", "success");
							infoMap.put("token", token);
							infoMap.put("role", user.get("auth_flag"));
							infoMap.put("iscancel", comMap.get("iscancel"));
							infoMap.put("name", user.get("nickname"));
							infoMap.put("comid", user.get("comid"));
							infoMap.put("mobile", user.get("mobile"));
							infoMap.put("state", userState);//0正常用户，1禁用，2审核中
							infoMap.put("nfc", comMap.get("nfc"));//0正常用户，1禁用，2审核中
							infoMap.put("cname", comMap.get("company_name"));
							int isshowepay = 0;
							Map com = daService.getMap( "select isshowepay from com_info_tb where id=? and isshowepay=?",new Object[]{Long.valueOf(user.get("comid")+""),1});
							if(com!=null&&com.get("isshowepay")!=null){
								isshowepay = 1;
							}
							infoMap.put("isshowepay", isshowepay);
							infoMap.put("logontime", System.currentTimeMillis()/1000);//20150618加上传入登录时间
							Integer etc = (Integer)comMap.get("etc");
							if(etc!=null&&etc==1){//车场设置了ibeacon，查询所有的工作站
								List<Map<String, Object>> workList = daService.getAll("select id,worksite_name from " +
										"com_worksite_tb where comid = ? ", new Object[]{comId});
								if(workList!=null&&!workList.isEmpty()){
									infoMap.put("worksite",StringUtils.createJson(workList));
								}
							}
							infoMap.put("etc", etc);// 0:不支持，1:Ibeacon 2:通道照牌 3:手机照牌
							String pid = CustomDefind.CUSTOMPARKIDS;
							if(pid.equals(comId.toString())){//定制车场，不需要扫牌设置
								infoMap.put("swipe",0);//去掉扫牌设置
							}else {
								infoMap.put("swipe",1);//保留扫牌设置
							}
							infoMap.put("notemsg", "");
							Long authFlag = (Long)user.get("auth_flag");
							infoMap.put("authflag",authFlag);
							infoMap.put("qr", getQrCode(uin));
							System.out.println(infoMap);
							String oldtoken = doSaveSession(uin,comId,token,version);
							if(authFlag==13){
								daService.update("update user_info_Tb set logon_time=?,online_flag=? where id=? ",
										new Object[]{System.currentTimeMillis()/1000,23,user.get("id")});
							}else {
								daService.update("update user_info_Tb set logon_time=? where id=? ",
										new Object[]{System.currentTimeMillis()/1000,user.get("id")});
							}
							Long worksiteid = RequestUtil.getLong(request, "worksite_id", -1L);
							long endtime = System.currentTimeMillis()/1000;
//							SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:00");
//							String d = sdf.format(System.currentTimeMillis());
//							long endtime = sdf.parse(d).getTime()/1000;
//							if(etc == 2&&userState==0){//通道扫牌车场才有
								logger.error("收费员登陆>>>>>>>>>>>>>工作站："+worksiteid);
								if(worksiteid != -1L){//岗亭工作站，
									List<Map<String, Object>> ortherUids = daService.getAll("select uid from parkuser_work_record_tb where " +
											" worksite_id = ? and end_time is null and uid!=?", new Object[] { worksiteid, uin });
									if(ortherUids!=null&&!ortherUids.isEmpty()){
										for(Map<String, Object> map: ortherUids){
											Long ouid = (Long)map.get("uid");
											logger.error("collectorlogin>>>>>:强制下班uid:"+ouid);
											doDeleteToken(ouid,comId);
										}
										int r = daService.update("update parkuser_work_record_tb set end_time=? where worksite_id = ? and end_time is null and uid!=?",
												new Object[] { endtime, worksiteid, uin });
										logger.error("collectorlogin>>>>>:强制下班worksiteid:"+worksiteid+",r:"+r);
									}
								}
								Map usr = daService.getPojo(
										"select * from parkuser_work_record_tb where  worksite_id<>? and end_time is null and uid=?",
										new Object[] {worksiteid, uin });
								if(usr!=null){
									daService.update("update parkuser_work_record_tb set end_time=? where id = ?",
											new Object[]{endtime,(Long)usr.get("id")});
									logger.error((Long)usr.get("uid")+"登陆工作站，旧工作站下班...");
								}
								Long count = daService.getLong(
										"select count(*) from parkuser_work_record_tb where worksite_id =? and end_time is null and uid=?",
										new Object[] {worksiteid, uin });
								if(count == 0){//上班
									workret = daService.update("insert into parkuser_work_record_tb(start_time,uid,worksite_id) values(?,?,?)",
											new Object[]{endtime,uin,worksiteid});
									logger.error("collectorlogin>>>>>:通道车场，上班：uid:"+uin+",worksiteid:"+worksiteid+",r:"+workret);
								}else{
									workret=1;
								}
//							}		
							
							logger.info(username+"登录成功...");
							//存入memcached缓存
//							Map<String,String >  parkTokenCacheMap =memcacheUtils.doMapStringStringCache("parkuser_token", null, null);
//							if(parkTokenCacheMap!=null){
//								parkTokenCacheMap.put(token, uin+"_"+comId);
//								if(oldtoken!=null){ 
//									logger.info("....delete oldtoken:"+oldtoken+","+parkTokenCacheMap.remove(oldtoken));
//								}
//								memcacheUtils.doMapStringStringCache("parkuser_token", parkTokenCacheMap, "update");
//							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.info(e.getMessage());
			}
			
		}
		
		if(out.equals("json")){
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
		}else {
			if(workret==0){
				if(!infoMap.containsKey("info"))
				infoMap.put("info","上班失败，请重新登陆");
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
		}
		//System.out.println(infoMap);
		return null;
	}
	/**
	 * 保存token到数据库中
	 * @param uin
	 * @param comid
	 * @param token
	 */
	private String doSaveSession(Long uin,Long comid,String token,String version ){
		//先删除收费员上次登录时的token
		int r = daService.update("delete from user_session_tb where uin=? ", new Object[]{uin});
		logger.error(uin+"上岗,删除原token:"+r);
		//先查询收费员上次登录时的token
		//Map<String, Object> map =null;// daService.getMap("select token from user_session_tb where uin=? ", new Object[]{uin});
		//String oldtoken =null;
//		if(map!=null){
//			oldtoken = (String)map.get("token");
//			daService.update("update user_session_tb set token=? ,create_time=?,comid=? where uin=? ", 
//					new Object[]{token,System.currentTimeMillis()/1000,comid,uin});
//		}else {
			//保存本次登录的token
		r=daService.update("insert into user_session_tb (comid,uin,token,create_time,version) " +
				"values (?,?,?,?,?)", 
				new Object[]{comid,uin,token,System.currentTimeMillis()/1000,version});
		logger.error(uin+"上岗,保存token:"+r);
//		}
		
		return "";//oldtoken;
	}
	
	public static void main(String[] args) {
		String pass = "276766";
		try {
			pass =StringUtils.MD5(pass);
			pass = StringUtils.MD5(pass +"zldtingchebao201410092009");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.err.println(pass);
	}
	
	private String getQrCode(Long uin){
		Map qrMap = daService.getMap("select code from qr_code_tb where uid=? and type=?  ", new Object[]{uin,1});
		String code = "";
		if(qrMap!=null){
			code = (String)qrMap.get("code");
		}
		if(code==null||code.trim().equals("")){
			Long newId = daService.getkey("seq_qr_code_tb");
			String codes[] = StringUtils.getGRCode(new Long[]{newId});
			if(codes!=null&&codes.length>0){
				code = codes[0];
				int ret = daService.update("insert into qr_code_tb(id,ctime,type,code,uid,isuse) values(?,?,?,?,?,?)",
						new Object[]{newId,System.currentTimeMillis()/1000,1,code,uin,1});
				logger.info(">>>>new qrcode:uin:"+uin+",qrcode:"+code+",ret:"+ret);
			}
		}
		if(code!=null&&code.trim().length()==19)
			code = "qr/c/"+code.trim();
		return code;
	}
	public void uploadWork(Long id ){
		 Map map = daService.getMap("select * from parkuser_work_record_tb where id = ?",new Object[]{id});
		 String ret = null;
		 if(map!=null&&map.size()>0){
			 String work = StringUtils.createJson(map);
	       	 HttpProxy httpProxy = new HttpProxy();
	       	 Map parammap = new HashMap();
	       	 parammap.put("work", work);
	       	 try {
	       		String token = null;
     			Map session = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
     			if(session!=null&&session.get("token")!=null){
     				token = session.get("token")+"";
     			}
     			 parammap.put("token", token);
	       		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadWork2Line", parammap);
	       		 
					if(ret!=null){
						if(ret.startsWith("1")){
							int r = daService.update("update parkuser_work_record_tb set sync_state=? ,line_id = ? where id = ?", new Object[]{1,Long.parseLong(ret.split("_")[2]+""),id});
						}else{
							int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
						}
					}else{
						int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
					}
				} catch (Exception e) {
					int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
					e.printStackTrace();
				}
				System.out.println(ret);
			 }
	}
	
	//删除工作上其它收费员的TOKEN
	private void doDeleteToken(Long uin,Long comId) {
		String token = "zldtokenvoid"+System.currentTimeMillis();
		daService.update("update user_session_tb set token=? ,create_time=?,comid=? where uin=? ", 
				new Object[]{token,System.currentTimeMillis()/1000,comId,uin});
		logger.error("collectorlogin>>>>>:强制下班，更新TOKEN，uid:"+uin);
	}
}
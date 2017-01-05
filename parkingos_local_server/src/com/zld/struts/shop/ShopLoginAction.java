package com.zld.struts.shop;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.Check;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

public class ShopLoginAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(ShopLoginAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String,Object> infoMap = new HashMap<String, Object>();
		String action = RequestUtil.processParams(request, "action");
		logger.info("action:"+action);
		if(action.equals("forpass")){//找回密码，发送到注册手机上
			String userId = RequestUtil.processParams(request, "username");
			Map userMap = daService.getPojo("select id,password,mobile from user_info_tb where id=?",
					new Object[]{Long.valueOf(userId)});
			if(userMap!=null){
				String mobile = (String)userMap.get("mobile");
				logger.equals(mobile);
				if(mobile==null||"".equals(mobile)){
					AjaxUtil.ajaxOutput(response, "{\"info\":\"您注册帐号时没有填写手机，请联系客服人员！\"}");
				}else if(Check.checkPhone(mobile,"m")){
					String _mString = mobile.substring(0,3)+"****"+mobile.substring(7);
					//SendMessage.sendMessage((String)userMap.get("mobile"),(String)userMap.get("password"));
					AjaxUtil.ajaxOutput(response, "{\"info\":\"密码已通过短信发送到您注册的手机上["+_mString+"]，请查收！【停车宝】\"}");
				}else {
					AjaxUtil.ajaxOutput(response, "{\"info\":\"您注册的手机号不合法！\"}");
				}
			}else {
				AjaxUtil.ajaxOutput(response, "{\"info\":\"帐号不存在！\"}");
			}
			return null;
		}else if(action.equals("editpass")){//修改密码，发送到注册手机上
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
		}
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
			Long uin = (Long)user.get("id");
			String token = StringUtils.MD5(username+pass+System.currentTimeMillis());
			infoMap.put("info", "success");
			infoMap.put("token", token);
			infoMap.put("role", user.get("auth_flag"));
			infoMap.put("name", user.get("nickname"));
			infoMap.put("shop_id", user.get("shop_id"));
			doSaveSession(uin,token,version);
			daService.update("update user_info_Tb set logon_time=? where id=?",
					new Object[]{System.currentTimeMillis()/1000,user.get("id")});
			logger.info(username+"登录成功...");
		}
		AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
		return null;
		//http://192.168.199.239/zld/shoplogin.do?username=21629&password=111111
	}
	
	/**
	 * 保存token到数据库中
	 * @param uin
	 * @param token
	 */
	private void doSaveSession(Long uin,String token,String version ){
		//先删除市场专员上次登录时的token
		daService.update("delete from user_session_tb where uin=? ", new Object[]{uin});
		//保存本次登录的token
		daService.update("insert into user_session_tb (uin,token,create_time,version) " +
				"values (?,?,?,?)", 
				new Object[]{uin,token,System.currentTimeMillis()/1000,version});
	}
	
}

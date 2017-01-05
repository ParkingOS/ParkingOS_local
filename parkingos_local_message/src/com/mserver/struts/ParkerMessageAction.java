package com.mserver.struts;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.mserver.AjaxUtil;
import com.mserver.service.PgOnlyReadService;
import com.mserver.utils.RequestUtil;

/**
 * 处理车主请求消息服务
 * 
 * @author Administrator
 * 
 */
public class ParkerMessageAction extends Action {

	@Autowired
	private PgOnlyReadService pOnlyReadService;
	
	private Logger logger = Logger.getLogger(ParkerMessageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String mobile = RequestUtil.processParams(request, "mobile");
		String action = RequestUtil.processParams(request, "action");
		logger.error("收费员收消息...手机："+mobile+" ,action："+action);
		if (mobile == null || "null".equals(mobile) || "".equals(mobile)) {
			AjaxUtil.ajaxOutput(response, "{}");
			return null;
		}
		if(action.equals("checkcode")){//取登陆结果，手机验证码收不到时，手机发送信息到第三方，第三方调用停车宝，判断是否是手机用户本人登录
			Map userMap = pOnlyReadService.getMap("select * from user_info_tb where mobile=?" +
					" and auth_flag=?", new Object[]{mobile,2});
			if(userMap!=null){
				Long online= (Long)userMap.get("online_flag");
				Long ltime = (Long)userMap.get("logon_time");
				Long ntime = System.currentTimeMillis()/1000;
				if(online!=null&&ntime-ltime<200){//第三方已经回调过请求
					if(online==10){//登录失败
						logger.error(">>>>>>>>>>>>>收费员："+mobile+",checkcode:-1,登录失败");
						AjaxUtil.ajaxOutput(response, "-1}");
					}else if(online==22){
						logger.error(">>>>>>>>>>>>>收费员："+mobile+",checkcode:1,登录成功");
						AjaxUtil.ajaxOutput(response, "1");
					}
				}else {
					AjaxUtil.ajaxOutput(response, "0");
				}
			}
		}
		return null;
	}
}
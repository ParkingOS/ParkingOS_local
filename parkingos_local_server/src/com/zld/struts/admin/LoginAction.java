package com.zld.struts.admin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.Check;
import com.zld.utils.RequestUtil;
import com.zld.utils.ZLDType;
/**
 * 登录，总管理员，停车场后台管理员，财务等角色可以登录 
 * @author Administrator
 *
 */
public class LoginAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	
	private Logger logger = Logger.getLogger(LoginAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String username =RequestUtil.processParams(request, "username");
		String pass =RequestUtil.processParams(request, "pass");
		String sql = "select * from user_info_tb where ";//";
		Object [] values = null;
		if(Check.checkUin(username)){
			values=new Object[]{Long.valueOf(username),pass};
			sql+=" id=? and password=?" ;
		}else{
			values=new Object[]{username,pass};
			sql +=" strid=? and password=? ";
		}
		String target = "success";
		Map user = daService.getPojo(sql, values);
		if(user==null){
			request.setAttribute("errormessage", "帐号或密码不正确!");
			request.setAttribute("username", username);
			return mapping.findForward("fail");
		}
		if("admin".equals(username)){
//			request.getSession().setAttribute("comid",user.get("comid"));
			request.getSession().setAttribute("userinfo",user);
			request.getSession().setAttribute("loginuin",user.get("id"));
			request.getSession().setAttribute("userid", username);
			request.getSession().setAttribute("nickname", user.get("nickname"));
			return mapping.findForward("config");
		}
		Long role = Long.valueOf(user.get("auth_flag").toString());
		//role: 0总管理员，1停车场后台管理员 ，2车场收费员，3财务，4车主  5市场专员 6录入员
		if(role.intValue()==ZLDType.ZLD_COLLECTOR_ROLE||role.intValue()==ZLDType.ZLD_CAROWER_ROLE||role.intValue() == ZLDType.ZLD_KEYMEN){//车场收费员及车主不能登录后台
			request.setAttribute("errormessage", "没有查询后台数据权限，请联系管理员!");
			target="fail";
		}else if(role.intValue()==ZLDType.ZLD_PARKADMIN_ROLE){
			target ="parkmanage";
			Long count = pgOnlyReadService.getLong(
							"select count(id) from com_info_tb where state=? and id=? ",
							new Object[] { 0, user.get("comid") });
			if(count == 0){
				request.setAttribute("errormessage", "车场不存在或者车场未通过审核!");
				target="fail";
			}
		}else if(role.intValue()==ZLDType.ZLD_ACCOUNTANT_ROLE){
			target ="finance";
		}else if(role.intValue()==ZLDType.ZLD_CARDOPERATOR){
			target ="cardoperator";
		}else if(role.intValue()==ZLDType.ZLD_MARKETER){//市场专员 登录后台
			request.getSession().setAttribute("marketerid",user.get("id"));
			target ="marketer";
		}else if(role.intValue()==ZLDType.ZLD_RECORDER||role.intValue()==ZLDType.ZLD_KEFU||role.intValue()==ZLDType.ZLD_QUERYKEFU){
			target = "recorder";
		}
		request.getSession().setAttribute("role",role );
		request.getSession().setAttribute("comid",user.get("comid"));
		request.getSession().setAttribute("userinfo",user);
		request.getSession().setAttribute("loginuin",user.get("id"));
		request.getSession().setAttribute("userid", username);
		request.getSession().setAttribute("nickname", user.get("nickname"));
		
//		List<Object[]> valuesList = ReadFile.praseFile();
//		int result = daService.bathInsert("insert into com_info_tb (longitude,latitude,company_name,address,type) values(?,?,?,?,?)",
//				valuesList, new int[]{3,3,12,12,4});
//		System.out.println(result);
		return mapping.findForward(target);
	}

}
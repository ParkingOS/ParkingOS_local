package com.zld.struts.parkadmin;

import java.util.ArrayList;
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
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
/**
 * 停车场后台管理员登录后，管理员工，员工分为收费员和财务
 * @author Administrator
 *
 */
public class MemberManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(MemberManageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(comid==0)
			comid = RequestUtil.getLong(request, "comid", 0L);
		if(action.equals("")){
			request.setAttribute("comid", comid);
			return mapping.findForward("list");
		}else if(action.equals("adminlist")){
			request.setAttribute("comid", comid);
			return mapping.findForward("adminlist");
		}else if(action.equals("quickquery")){
			String sql = "select * from user_info_tb where comid=? and auth_flag>? and state=0 ";
			String countSql = "select count(*) from user_info_tb  where comid=? and auth_flag>? and state=0 ";
			Long count = daService.getLong(countSql,new Object[]{comid,0});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getPage(sql, null, 1, 20);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(comid);
			params.add(0);
			if(count>0){
				list = daService.getAll(sql+ " order by id desc",params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from user_info_tb where comid=? and auth_flag>? and state=0 ";
			String countSql = "select count(*) from user_info_tb where  comid=? and auth_flag>? and state=0 " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo base = new SqlInfo("1=1", new Object[]{comid,0});
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"user_info");
			Object[] values = null;
			List<Object> params = null;
			if(sqlInfo!=null){
				sqlInfo = SqlInfo.joinSqlInfo(base,sqlInfo, 2);
				countSql+=" and "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				values = sqlInfo.getValues();
				params= sqlInfo.getParams();
			}else {
				values = base.getValues();
				params= base.getParams();
			}
			//System.out.println(sqlInfo);
			Long count= daService.getLong(countSql, values);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+ " order by id desc", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			int result = createMember(request);
			if(result==1)
				AjaxUtil.ajaxOutput(response, "1");
			else {
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("edit")){
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String strid =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "strid"));
			String phone =RequestUtil.processParams(request, "phone");
			String mobile =RequestUtil.processParams(request, "mobile");
			Integer auth_flag = RequestUtil.getInteger(request, "auth_flag", -1);
			Integer isview = RequestUtil.getInteger(request, "isview", -1);
			Long id =RequestUtil.getLong(request, "id", -1L);
			Long count = daService.getLong("select count(*) from user_info_tb where mobile=? and auth_flag=? and id<>? ", new Object[]{mobile,auth_flag,id});
			if(count > 0){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String sql = "update user_info_tb set nickname=?,strid=?,phone=?,mobile=?,auth_flag=?,isview=? where id=? ";
			int result = daService.update(sql, new Object[]{nickname,strid,phone,mobile,auth_flag,isview,Long.valueOf(id)});
			if(result == 1){
				Long total = daService.getLong("select count(*) from user_info_tb where auth_flag=? and comid=? ", new Object[]{1,comid});
				if(total > 1){//一个车场有多个管理员，提醒一下
					result = 2;
				}
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("adminedit")){
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String strid =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "strid"));
			String phone =RequestUtil.processParams(request, "phone");
			String mobile =RequestUtil.processParams(request, "mobile");
			double firstorderquota =Double.parseDouble(RequestUtil.processParams(request, "firstorderquota")+"");
			double rewardquota =Double.parseDouble(RequestUtil.processParams(request, "rewardquota")+"");
			double recommendquota =Double.parseDouble(RequestUtil.processParams(request, "recommendquota")+"");
			double ticketquota =Double.parseDouble(RequestUtil.processParams(request, "ticketquota")+"");
			Integer auth_flag = RequestUtil.getInteger(request, "auth_flag", -1);
			Integer isview = RequestUtil.getInteger(request, "isview", -1);
			Long id =RequestUtil.getLong(request, "id", -1L);
//			Long count = daService.getLong("select count(*) from user_info_tb where mobile=? and auth_flag=? and id<>? ", new Object[]{mobile,auth_flag,id});
//			if(count > 0){
//				AjaxUtil.ajaxOutput(response, "-1");
//				return null;
//			}
			String sql = "update user_info_tb set nickname=?,strid=?,phone=?,mobile=?,auth_flag=?,isview=?,firstorderquota=?,rewardquota=?,recommendquota=?,ticketquota=? where id=? ";
			int result = daService.update(sql, new Object[]{nickname,strid,phone,mobile,auth_flag,isview,firstorderquota,rewardquota,recommendquota,ticketquota,Long.valueOf(id)});
			if(result == 1){
				Long total = daService.getLong("select count(*) from user_info_tb where auth_flag=? and comid=? ", new Object[]{1,comid});
				if(total > 1){//一个车场有多个管理员，提醒一下
					result = 2;
				}
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("editpass")){
			String nickname = request.getSession().getAttribute("loginuin")+"";
			String uin =RequestUtil.processParams(request, "id");
			String sql = "update user_info_tb set password =? ,md5pass=? where id =?";
			String newPass = RequestUtil.processParams(request, "newpass");
			String confirmPass = RequestUtil.processParams(request, "confirmpass");
			String md5pass = newPass;
			try {
				md5pass = StringUtils.MD5(newPass);
				md5pass = StringUtils.MD5(md5pass+"zldtingchebao201410092009");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(newPass.length()<6){
				AjaxUtil.ajaxOutput(response, "密码长度小于6位，请重新输入！");
			}else if(newPass.equals(confirmPass)){
				Object [] values = new Object[]{newPass,md5pass,Long.valueOf(uin)};
				int result = daService.update(sql, values);
				logger.info("parkadmin or admin:"+nickname+" edit parkuser:"+uin+",password:"+newPass);
				AjaxUtil.ajaxOutput(response, result+"");
			}else {
				AjaxUtil.ajaxOutput(response, "两次密码输入不一致，请重新输入！");
			}
			return null;
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "selids");
			String sql = "update user_info_tb set state =?  where id =?";
			Object [] values = new Object[]{1,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("check")){
			String strid = RequestUtil.processParams(request, "value");
			String sql = "select count(*) from user_info_tb where strid =?";
			Long result = daService.getLong(sql, new Object[]{strid});
			if(result>0)
				AjaxUtil.ajaxOutput(response, "1");
			else {
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("isview")){//是否可以收费
			Long id =RequestUtil.getLong(request, "id",-1L);
			Integer isview =RequestUtil.getInteger(request, "isview",-1);
			int ret = 0;
			if(id!=-1&&isview!=-1){
				ret = daService.update("update user_info_tb set isview=? where id =?", new Object[]{isview,id});
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}
		return null;
	}


	//注册停车场收费员帐号
	@SuppressWarnings({ "rawtypes" })
	private int createMember(HttpServletRequest request){
		String strid =RequestUtil.processParams(request, "strid");
		String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
		String phone =RequestUtil.processParams(request, "phone");
		String mobile =RequestUtil.processParams(request, "mobile");
		Long role =RequestUtil.getLong(request, "auth_flag", 2L);
		if(role==-1) role =2L;
		if(nickname.equals("")) nickname=null;
		if(phone.equals("")) phone=null;
		if(mobile.equals("")) mobile=null;
		Map adminMap = (Map) request.getSession().getAttribute("userinfo");
		Long time = System.currentTimeMillis()/1000;
		if(!checkStrid(strid))
			return 0;
		Long comId = (Long)request.getSession().getAttribute("comid");
		if(comId==0)
			comId = RequestUtil.getLong(request, "comid", 0L);
		//用户表
		String sql="insert into user_info_tb (nickname,password,strid," +
				"address,reg_time,mobile,phone,auth_flag,comid) " +
				"values (?,?,?,?,?,?,?,?,?)";
		Object [] values= new Object[]{nickname,strid,strid,
				adminMap.get("address"),time,mobile,phone,role,comId};
		int r = daService.update(sql, values);
		return r;
	}
	/*//注册停车场收费员帐号
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private boolean createMember(HttpServletRequest request){
		String strid =RequestUtil.processParams(request, "strid");
		String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
		String phone =RequestUtil.processParams(request, "phone");
		String mobile =RequestUtil.processParams(request, "mobile");
		String role =RequestUtil.processParams(request, "other_flag");
		if(role.equals("")||role.equals("-1"))
			role="2";//默认为收费员
		if(nickname.equals("")) nickname=null;
		if(phone.equals("")) phone=null;
		if(mobile.equals("")) mobile=null;
		Map adminMap = (Map) request.getSession().getAttribute("amdininfo");
		Long time = System.currentTimeMillis()/1000;
		if(!checkStrid(strid))
			return false;
		Long departId = Long.valueOf(adminMap.get("department_id")+"");
		Long uin =  daService.getLong("SELECT nextval('seq_uin'::REGCLASS) AS newid", null);
		Long comId = (Long)request.getSession().getAttribute("company_id");
		List<Map> sqlsList = new ArrayList<Map>();
		//用户表
		Map userMap = new HashMap();
		userMap.put("sql", "insert into user_info_tb (uin,nickname,password,strid,company,address,reg_time,department_id,mobile,phone,other_flag,company_id) " +
				"values (?,?,?,?,?,?,?,?,?,?,?,?)");
		userMap.put("values", new Object[]{uin,nickname,"666666",strid,adminMap.get("company"),adminMap.get("address"),time,departId,mobile,phone,Long.valueOf(role),comId});
		//online表
		Map onlineMap = new HashMap();
		
		onlineMap.put("sql", "insert into user_online_tb (uin,strid,nickname,main_flag,stradmstrid)" +
				"values(?,?,?,?,?)");
		onlineMap.put("values", new Object[]{uin,strid,nickname,new Integer(11536276),adminMap.get("strid")});
		//fuwu表
		Map fuwuMap = new HashMap();
		fuwuMap.put("sql", "insert into fuwu (uin,strid,xzmp,huihua,mail,sib,opendate,openyear,if_tryout,opentype,admopenusers,fftype) " +
				"values(?,?,?,?,?,?,?,?,?,?,?,?)");
		fuwuMap.put("values", new Object[]{uin,strid,1,1,1,1,new Timestamp(time*1000),0,100,0,0,0});
		
		sqlsList.add(userMap);
		sqlsList.add(onlineMap);
		sqlsList.add(fuwuMap);
		boolean r = daService.bathUpdate(sqlsList);
		return r;
	}*/


	private boolean checkStrid(String strid){
		String sql = "select count(*) from user_info_tb where strid =?";
		Long result = daService.getLong(sql, new Object[]{strid});
		if(result>0){
			return false;
		}
		return true;
		
	}
}
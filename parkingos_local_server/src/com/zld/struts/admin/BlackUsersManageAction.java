package com.zld.struts.admin;

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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * 黑名单管理，在总管理员后台
 * @author Administrator
 *
 */
public class BlackUsersManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(BlackUsersManageAction.class);
	@Autowired
	private PublicMethods publicMethods;
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
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select b.*,u.mobile from zld_black_tb b left join user_info_Tb u on b.uin=u.id  ";
			String countSql = "select count(b.id) from zld_black_tb b left join user_info_Tb u on b.uin=u.id " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo base = new SqlInfo("1=1", new Object[]{5,0});
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"black_tb","b",new String[]{"mobile"});
			SqlInfo ssqlInfo = getSuperSqlInfo(request);
			Object[] values = null;
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				if(ssqlInfo!=null)
					sqlInfo = SqlInfo.joinSqlInfo(sqlInfo,ssqlInfo, 2);
				countSql+=" where  "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}else if(ssqlInfo!=null){
				countSql+=" where "+ ssqlInfo.getSql();
				sql +=" where "+ssqlInfo.getSql();
				params= ssqlInfo.getParams();
			}
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by b.id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			Integer state = RequestUtil.getInteger(request, "state", -1);
			String sql = "update zld_black_tb set state=?,utime=? where id =?";
			int result =0;
			if(state==0)
				state=1;
			else if(state==1)
				state=0;
			else {
				AjaxUtil.ajaxOutput(response, "-1");
			}
			Object [] values = new Object[]{state,System.currentTimeMillis()/1000,id};
			if(id!=-1)
				result = daService.update(sql, values);
			if(result==1){//清除缓存中的车主
//				List<Long> blackUsers = memcacheUtils.doListLongCache("zld_black_users", null, null);
//				if(blackUsers!=null){
//					if(state==1&&blackUsers.contains(uin)){
//						blackUsers.remove(uin);
//						memcacheUtils.doListLongCache("zld_black_users", blackUsers, "update");
//					}else if(state==0&&!blackUsers.contains(uin)){
//						blackUsers = new ArrayList<Long>();
//						if(!publicMethods.isAuthUser(uin)){
//							blackUsers.add(uin);
//							memcacheUtils.doListLongCache("zld_black_users", blackUsers, "update");
//						}else{
//							logger.info("车主uin:"+uin+"是认证用户，不加入黑名单");
//						}
//					}
//				}
//				List<Long> whiteUsers = memcacheUtils.doListLongCache("zld_white_users", null, null);
//				if(whiteUsers!=null){
//					if(state==1&&!whiteUsers.contains(uin)){
//						whiteUsers.add(uin);
//						memcacheUtils.doListLongCache("zld_white_users", whiteUsers, "update");
//					}else if(state==0&&whiteUsers.contains(uin)){
//						whiteUsers.remove(uin);
//						memcacheUtils.doListLongCache("zld_white_users", whiteUsers, "update");
//					}
//				}
//				logger.info("edit blackusers:"+whiteUsers);
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("reload")){//重置黑名单缓存
			List<Map<String, Object>> list = daService.getAll("select uin from zld_black_tb where state =?", new Object[]{0});
			if(list!=null&&!list.isEmpty()){
				List<Long> users = new ArrayList<Long>();
				for(Map<String, Object> map :list){
					long uin = Long.parseLong(map.get("uin")+"");
					if(!publicMethods.isAuthUser(uin)){
						users.add(uin);
					}else{
						logger.info("车主uin:"+uin+"是认证用户，不加入黑名单");
					}
				}
//				memcacheUtils.doListLongCache("zld_black_users", users, "update");
				AjaxUtil.ajaxOutput(response, "重置了"+list.size()+"条黑名单");
				logger.info("reload blackusers:"+users);
			}else {
				AjaxUtil.ajaxOutput(response, "没有黑名单");
			}
		}else if(action.equals("reloadwhite")){//重置白名单 缓存
			List<Map<String, Object>> list = daService.getAll("select uin from zld_black_tb where state =?", new Object[]{1});
			if(list!=null&&!list.isEmpty()){
				List<Long> users = new ArrayList<Long>();
				for(Map<String, Object> map :list){
					users.add((Long)map.get("uin"));
				}
//				memcacheUtils.doListLongCache("zld_white_users", users, "update");
				AjaxUtil.ajaxOutput(response, "重置了"+list.size()+"条白名单");
				logger.info("reload blackusers:"+users);
			}else {
				AjaxUtil.ajaxOutput(response, "没有白名单");
			}
		}else if(action.equals("viewwhitememcache")){//查看白名单 
			List<Long> blackUsers = null;//memcacheUtils.doListLongCache("zld_white_users", null, null);
			if(blackUsers!=null)
				AjaxUtil.ajaxOutput(response, blackUsers.size()+":"+blackUsers.toString());
			else {
				AjaxUtil.ajaxOutput(response, "[]");
			}
		}
		else if(action.equals("viewmemcache")){//查看黑名单
			List<Long> blackUsers = null;//memcacheUtils.doListLongCache("zld_black_users", null, null);
			if(blackUsers!=null)
				AjaxUtil.ajaxOutput(response, blackUsers.size()+":"+blackUsers.toString());
			else {
				AjaxUtil.ajaxOutput(response, "[]");
			}
		}
		return null;
	}
	
	private SqlInfo getSuperSqlInfo(HttpServletRequest request){
		String mobile = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "mobile"));
		SqlInfo sqlInfo = null;
		if(!mobile.equals("")){
			sqlInfo = new SqlInfo(" u.mobile like ? ",new Object[]{"%"+mobile+"%"});
		}
		return sqlInfo;
	}
}
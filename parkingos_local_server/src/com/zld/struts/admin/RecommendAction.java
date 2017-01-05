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
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;

public class RecommendAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(RecommendAction.class);
	
	/*
	 * 推荐查询
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comId = (Long)request.getSession().getAttribute("comid");
		if(comId == null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select r.*,u.auth_flag from recommend_tb r left join user_info_tb u on r.pid=u.id where u.auth_flag is not null order by create_time desc  ";
			String countSql = "select count(*) from recommend_tb r left join user_info_tb u on r.pid=u.id where u.auth_flag is not null " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			list = daService.getAll(sql, params, pageNum, pageSize);
			//把管理员和收费员统一划为收费员
			for(Map<String, Object> map : list){
				Long auth_flag = (Long)map.get("auth_flag");
				if(auth_flag == 1){
					map.put("auth_flag", 2);
				}
			}
			Long count = daService.getLong(countSql, new Object[]{});
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("highquery")){
			String sql = "select r.*,u.auth_flag from recommend_tb r left join user_info_tb u on r.pid=u.id where u.auth_flag is not null ";
			String countSql = "select count(*) from recommend_tb r left join user_info_tb u on r.pid=u.id where u.auth_flag is not null " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"recommend_tb","r",new String[]{"auth_flag"});
			Long auth_flag = RequestUtil.getLong(request, "auth_flag_start", -1L);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				countSql+= " and " + sqlInfo.getSql();
				sql += " and " + sqlInfo.getSql();
				params = sqlInfo.getParams();
			}
			if(auth_flag != -1){
				//收费员和管理员合并在一起
				if(auth_flag == 2){
					countSql +=" and (u.auth_flag=? or u.auth_flag=?) ";
					sql += " and (u.auth_flag=? or u.auth_flag=?) ";
					params.add(1);
					params.add(2);
				}else{
					countSql += " and u.auth_flag=? ";
					sql += " and u.auth_flag=? ";
					params.add(auth_flag);
				}
			}
			sql += " order by r.create_time desc ";
			Long count = daService.getCount(countSql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				//把管理员和收费员统一划为收费员
				for(Map<String, Object> map : list){
					Long role = (Long)map.get("auth_flag");
					if(role == 1){
						map.put("auth_flag", 2);
					}
				}
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
}

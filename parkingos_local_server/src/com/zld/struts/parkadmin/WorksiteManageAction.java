package com.zld.struts.parkadmin;

import java.util.ArrayList;
import java.util.List;

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

public class WorksiteManageAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(WorksiteManageAction.class);
	
	/*
	 * 工作站设置
	 */
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid == null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			request.setAttribute("comid", comid);
			return mapping.findForward("list");
		}else if(action.equals("worksitequery")){
			String sql = "select * from com_worksite_tb where comid=?";
			String countsql = "select count(1) from com_worksite_tb where comid=?";
			Long count = daService.getLong(countsql, new Object[]{comid});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(comid);
			if(count>0){
				list = daService.getAll(sql+ " order by id desc",params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			logger.info("insert into com_worksite_tb....");
			String worksite_name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "worksite_name"));
			String description = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "description"));
			Integer net_type = RequestUtil.getInteger(request, "net_type", 0);//默认是网络状况是0：流量
			if(worksite_name.equals("")) worksite_name = null;
			if(description.equals("")) description = null;
			String sql = "insert into com_worksite_tb(comid,worksite_name,description,net_type) values(?,?,?,?)";
			int r = daService.update(sql, new Object[]{comid,worksite_name,description,net_type});
			if(r == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("edit")){
			logger.info("update com_worksite_tb....");
			Long id =RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String worksite_name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "worksite_name"));
			String description = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "description"));
			Integer net_type = RequestUtil.getInteger(request, "net_type", 0);//默认是网络状况是0：流量
			String sql = "update com_worksite_tb set worksite_name=?,description=?,net_type=? where id=?";
			int r = daService.update(sql, new Object[]{worksite_name,description,net_type,id});
			if(r == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("delete")){
			logger.info("delete from com_worksite_tb....");
			String id =RequestUtil.processParams(request, "selids");
			String sql = "delete from com_worksite_tb where id=?";
			int r = daService.update(sql, new Object[]{Long.valueOf(id)});
			if(r == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}
		return null;
	}
}

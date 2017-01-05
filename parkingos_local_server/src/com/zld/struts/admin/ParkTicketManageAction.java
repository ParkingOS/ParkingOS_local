package com.zld.struts.admin;

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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * 泊车点管理，在总管理员后台
 * @author Administrator
 *
 */
public class ParkTicketManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;

	
	private Logger logger = Logger.getLogger(ParkTicketManageAction.class);
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
			String sql = "select * from park_ticket_tb ";
			String countSql = "select count(ID) from park_ticket_tb " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"park_ticket");
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				countSql+=" where  "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Long parkid = RequestUtil.getLong(request, "comid", -1L);
			Integer tnumber= RequestUtil.getInteger(request, "tnumber", 0);
			Integer exptime= RequestUtil.getInteger(request, "exptime", 0);
			Integer haveget= RequestUtil.getInteger(request, "haveget", 0);
			Double money = RequestUtil.getDouble(request, "money", 0d);
			int ret = daService.update("update park_ticket_tb set comid=?,tnumber=?,exptime=?, haveget=?" +
					",money=? where id =?", 
					new Object[]{parkid,tnumber,exptime,haveget,money,id});
			AjaxUtil.ajaxOutput(response, ret+"");
			
		}else if(action.equals("create")){
			Long parkid = RequestUtil.getLong(request, "comid", -1L);
			Integer tnumber= RequestUtil.getInteger(request, "tnumber", 0);
			Integer exptime= RequestUtil.getInteger(request, "exptime", 0);
			Integer haveget= RequestUtil.getInteger(request, "haveget", 0);
			Double money = RequestUtil.getDouble(request, "money", 0d);
			int ret = daService.update("insert into park_ticket_tb (comid,tnumber,exptime,haveget,money)" +
					" values(?,?,?,?,?)",
					new Object[]{parkid,tnumber,exptime,haveget,money});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int ret = daService.update("delete from park_ticket_tb where id=?", new Object[]{id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}
		return null;
	}
	
}
package com.zld.struts.parkadmin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

public class AccountManageAction extends Action{

	@Autowired
	private DataBaseService daService;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		Integer role = RequestUtil.getInteger(request, "role",-1);
		request.setAttribute("role", role);
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			Map comMap = daService.getMap("select total_money,money from com_info_tb where id =?",new Object[]{comid});
			if(comMap!=null){
				request.setAttribute("total", comMap.get("total_money"));
				request.setAttribute("money", comMap.get("money"));
			}
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select * from money_record_tb where comid=? ";
			String countSql = "select count(*) from money_record_tb where comid=? ";
			Long count = daService.getLong(countSql,new Object[]{comid});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			List<Object> params = new ArrayList<Object>();
			params.add(comid);
			if(count>0){
				list = daService.getAll(sql+" order by id desc",params, pageNum, pageSize);
			}
			if(list!=null)setCompany(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}
		/*else if(action.equals("query")){
			String sql = "select * from money_record_tb  where comid=? ";
			String countSql = "select count(*) from money_record_tb where comid=? ";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"money_record");
			Object[] values = null;
			List<Object> params = null;
			if(sqlInfo!=null){
				countSql+=" and  "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				values = sqlInfo.getValues();
				params = sqlInfo.getParams();
			}
			params.add(0,comid);
			//System.out.println(sqlInfo);
			Long count= daService.getLong(countSql, values);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc", params, pageNum, pageSize);
			}
			if(list!=null)setCompany(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}*/
		else if(action.equals("query")){
			String sql = "select * from park_account_tb  where comid=? ";
			String countSql = "select count(*) from park_account_tb where comid=? ";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"park_account");
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				countSql+=" and  "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				params = sqlInfo.getParams();
			}
			params.add(0,comid);
			String orderby="id";
			String sort="desc";
			String orderfield = RequestUtil.processParams(request, "orderfield");
			String reqorderby = RequestUtil.processParams(request, "orderby");
			if(StringUtils.isNotNull(orderfield))
				orderby=orderfield;
			if(StringUtils.isNotNull(orderfield))
				sort=reqorderby;
			//System.out.println(sqlInfo);
			sql+="order by "+orderby+" "+sort;
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			if(list!=null)setCompany(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}
		return null;
	}
	
	private void setCompany(List<Map> list){
		for(Map m: list){
//			Integer type = (Integer) m.get("type");
			/*if(type==0)
				m.put("recharge", m.get("amount"));
			else if(type==1)
				m.put("consum", m.get("amount"));
			else if(type==2)
				m.put("withdraw", m.get("amount"));
			if(m.get("comid")!=null){
				Long comId = (Long) m.get("comid");
				String company = (String)daService.getObject("select company_name from com_info_tb where id=?",
						new Object[]{comId}, String.class);
				m.put("company",company);
			}*/
			if(m.get("uid")!=null){
				Map userMap = daService.getMap("select nickname from user_info_tb where id=? ",new Object[]{m.get("uid")});
				if(userMap!=null){
					m.put("uid", userMap.get("nickname"));
				}
			}
			if(m.get("type")!=null){
				Integer type = (Integer)m.get("type");
				if(type==1){
					m.put("amount", "<font color='red'>-"+m.get("amount")+"</font>");
				}
			}
		}
	}
	
}

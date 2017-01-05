package com.zld.struts.admin.account;

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

public class ParkAcountAction extends Action{

	@Autowired
	private DataBaseService daService;
	
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
		}else if(action.equals("quickquery")){
			String sql = "select * from money_record_tb ";
			String countSql = "select count(*) from money_record_tb";
			Long count = daService.getLong(countSql,null);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc",null, pageNum, pageSize);
			}
			if(list!=null)setCompany(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from money_record_tb ";
			String countSql = "select count(*) from money_record_tb";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"money_record");
			Object[] values = null;
			List<Object> params = null;
			if(sqlInfo!=null){
				countSql+=" where  "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				values = sqlInfo.getValues();
				params = sqlInfo.getParams();
			}
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
		}
		return null;
	}
	
	private void setCompany(List<Map> list){
		for(Map m: list){
			Integer type = (Integer) m.get("type");
			if(type==0)
				m.put("recharge", m.get("amount"));
			else if(type==1)
				m.put("consum", m.get("amount"));
			else if(type==2)
				m.put("withdraw", m.get("amount"));
			if(m.get("comid")!=null){
				Long comId = (Long) m.get("comid");
				Map comMap = daService.getMap("select company_name from com_info_tb where id=?",
						new Object[]{comId});
				if(comMap!=null)
				m.put("company",comMap.get("company_name"));
			}
			if(m.get("uin")!=null){
				Map userMap = daService.getMap("select u.mobile from user_info_tb u where u.id=? ",new Object[]{m.get("uin")});
				Map carNumberMap = daService.getMap("select c.car_number from car_info_tb c where c.uin=? ",new Object[]{m.get("uin")});
				if(userMap!=null){
					m.put("mobile", userMap.get("mobile"));
				}
				if(carNumberMap!=null)
					m.put("carnumber", carNumberMap.get("car_number"));
					
			}
		}
	}
	
}

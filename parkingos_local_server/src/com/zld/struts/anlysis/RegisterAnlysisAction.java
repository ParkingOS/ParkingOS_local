package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
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
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class RegisterAnlysisAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}else if(action.equals("")){
			String monday = StringUtils.getMondayOfThisWeek();
			request.setAttribute("btime", monday);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("trend");
		}else if(action.equals("querytrend")){
			String sql = "select reg_count atotal,carnumber_count ctotal,reg_time " +
			" from register_anlysis_tb ";
			List list = null;
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e = TimeTools.getLongMilliSecondFrom_HHMMDD(etime)/1000;
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			sqlInfo =new SqlInfo(" reg_time between ? and ?",
					new Object[]{b,e});
			sql +=" where "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			list = daService.getAllMap(sql +" order by reg_time ",params);
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				Long reg_time = (Long)map.get("reg_time");
				map.put("time",TimeTools.getTimeStr_yyyy_MM_dd(reg_time*1000));
			}
			String json = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
}

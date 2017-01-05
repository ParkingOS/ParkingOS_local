package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
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
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class HasPickerAnlysisAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("trend");
		}else if(action.equals("querytrend")){
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			if(btime.equals(""))
				btime = nowtime;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(btime+" 23:59:59");
			String sql = "select anlysis_time,total from hasparker_anlysis_tb where anlysis_time between ? and ? order by anlysis_time ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{b,e});
			for(int i=0;i<list.size();i++){
				Map<String,Object> map = list.get(i);
				Long anlysis_time = (Long)map.get("anlysis_time");
				map.put("time",TimeTools.getTime_yyyyMMdd_HHmm(anlysis_time*1000).substring(11));
			}
			String json = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
}

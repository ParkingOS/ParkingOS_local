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

/**
 * 停车场车位分享使用统计
 * @author Administrator
 *
 */
public class ParkLaLaAnlysisAction extends Action {

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
			List<Map<String, Object>>  list = daService.getAll("select distinct p.comid,c.company_name from park_anlysis_tb p," +
					"com_info_tb c where p.comid = c.id", null);	
			String json = "[";
			if(list!=null){
				for(Map<String, Object> map :list){
					if(!json.equals("["))
						json +=",";
					json +="{\"id\":"+map.get("comid")+",\"pname\":\""+map.get("company_name")+"\"}";
				}
			}
			json +="]";
			request.setAttribute("comlist", json);
			return mapping.findForward("success");
		}else if(action.equals("parkidle")){//具体停车场的空闲趋势
			Long cidLong=RequestUtil.getLong(request, "comid", -1L);
			String pname = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "pname"));
			request.setAttribute("_comid", cidLong);
			request.setAttribute("pname", pname);
			return mapping.findForward("parkidle");
		}else if(action.equals("query")){
			//String monday = StringUtils.getMondayOfThisWeek();
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			//String nowtime= df2.format(System.currentTimeMillis());
			String sql = "select used_count used,share_count shared,create_time,comid " +
					" from park_anlysis_tb ";
			List list = null;//daService.getPage(sql, null, 1, 20);
			String qdate=RequestUtil.processParams(request, "qdate");
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long _comid = RequestUtil.getLong(request, "comid", -1L);
			if(btime.equals(""))
				btime = "00:00:00";
			if(etime.equals(""))
				etime = "23:59:59";
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			Long b = TimeTools.getToDayBeginTime();
			Long e =System.currentTimeMillis()/1000;
			if(!btime.equals("")&&!etime.equals("")){
				 b =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(qdate+" "+btime);
				 e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(qdate+" "+etime);
			}
			sqlInfo =new SqlInfo(" create_time between ? and ?  and comid=? ",
					new Object[]{b,e,_comid});//c_type 0:NFC,1:IBeacon
			sql +=" where "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			list = daService.getAllMap(sql +" order by create_time ",params);
			Long count = daService.getLong("select parking_total from com_info_tb where id=?", new Object[]{_comid});
			String json = getJson(list,count);
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}
		return null;
	}
	
	
	private String getJson(List list,Long count){
		//create_time,comid,share_count,used_count
		/*
		 * [{"total":100,"share":80,"free":30,"time":"09:15"},
	             {"total":100,"share":50,"free":40,"time":" "}]
		 */
		for(int i=0;i<list.size();i++){
			Map map = (Map)list.get(i);
			Long create_time = (Long)map.get("create_time");
			Long comid=(Long)map.get("comid");
			if(i==0){
				Integer share = (Integer)map.get("shared");
				if(share==null||share==0){
					Map<String, Object> lastMap = daService.getMap("select share_count from park_anlysis_tb where comid=? and create_time" +
							"<? and share_count >? order by create_time desc limit ?", new Object[]{comid,create_time,0,1});
					if(lastMap!=null&&lastMap.get("share_count")!=null)
						map.put("shared", lastMap.get("share_count"));
				}
			}
			map.put("total", count);
			map.put("time",TimeTools.getTime_yyyyMMdd_HHmm(create_time*1000).substring(11));
			map.remove("create_time");
			map.remove("comid");
		}
		return StringUtils.createJson(list);
	}
}
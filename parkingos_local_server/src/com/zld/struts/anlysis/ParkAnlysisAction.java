package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibatis.sqlmap.engine.mapping.sql.Sql;
import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class ParkAnlysisAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		Integer role = RequestUtil.getInteger(request, "role",-1);
		request.setAttribute("role", role);
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}else if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long b = 0L;
			if(!btime.equals("")){
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			}
			if(etime.equals("")){
				etime = nowtime;
			}
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			List<Object> params = new ArrayList<Object>();
			String sql = "select count(*) total,sum(parking_total) parking_total,parking_type from com_info_tb where isfixed=? and epay=? and state!=? ";
			params.add(1);
			params.add(1);
			params.add(1);
			if(b != 0){
				sql += " and fixed_pass_time between ? and ? ";
				params.add(b);
				params.add(e);
			}
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			if(uid != -1){
				sql += " and uid=? ";
				params.add(uid);
			}
			sql += " group by parking_type order by parking_type desc ";
			list = daService.getAllMap(sql,params);
			Long dimian_dixia = 0L;//地上/地下
			Long zhandao = 0L;//占道类型的车场
			Long dixia = 0L;//地下类型的车场
			Long dimian = 0L;//地上停车场
			Long parking_total = 0L;//车位总数
			for(Map<String, Object> map : list){
				Integer parking_type = (Integer)map.get("parking_type");
				Long total = (Long)map.get("total");
				Long parking = (Long)map.get("parking_total");
				if(parking_type == 3){
					dimian_dixia = total;
				}else if(parking_type == 2){//占道
					zhandao = total;
				}else if(parking_type == 1){//地下
					dixia = total;
				}else if(parking_type == 0){
					dimian = total;
				}
				parking_total += parking;
			}
			Long total = zhandao + dixia + dimian + dimian_dixia;
			String zhandao_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(zhandao)/StringUtils.formatDouble(total))*100)+"%";
			String dixia_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(dixia)/StringUtils.formatDouble(total))*100)+"%";
			String dimian_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(dimian)/StringUtils.formatDouble(total))*100)+"%";
			String dimian_dixia_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(dimian_dixia)/StringUtils.formatDouble(total))*100)+"%";
			params.clear();
			String sql1 = "select id from com_info_tb where isfixed=? and epay=? and state!=? ";
			params.add(1);
			params.add(1);
			params.add(1);
			if(uid != -1){
				sql1 += " and uid=?";
				params.add(uid);
			}
			String sql2 = "select count(*) ctotal from user_info_tb  where comid in ("+sql1+") and (auth_flag=? or auth_flag=?) and reg_time between ? and ? and state=? ";
			params.add(1);
			params.add(2);
			params.add(b);
			params.add(e);
			params.add(0);
			//已校验的可支付车场时间段内的注册数
			Long ctotal = daService.getCount(sql2, params);
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.put("park_total", total);
			newMap.put("parking_total", parking_total);
			newMap.put("ctotal", ctotal);
			newMap.put("zhandao_percent", zhandao_percent);
			newMap.put("dixia_percent", dixia_percent);
			newMap.put("dimian_percent", dimian_percent);
			newMap.put("dimian_dixia_percent", dimian_dixia_percent);
			newMap.put("id", 0);
			list2.add(newMap);
			int count = list2!=null?list2.size():0;
			String json = JsonUtil.Map2Json(list2,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
}

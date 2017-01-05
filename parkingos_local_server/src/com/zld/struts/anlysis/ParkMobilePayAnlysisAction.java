package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class ParkMobilePayAnlysisAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("btime", df2.format(System.currentTimeMillis()));
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			Map<String, Object> map = new HashMap<String, Object>();
			map = getTotal(request);
			request.setAttribute("count", map.get("count"));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Integer city = RequestUtil.getInteger(request, "city", -1);//-1全部 0：北京 1：济南
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String sql = "select count(1) scount,sum(total) total,comid from order_tb where total>=? and state=? and pay_type=? and c_type !=? and end_time between ? and ? ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			if(btime.equals(""))
				btime = nowtime;
			if(etime.equals(""))
				etime = nowtime;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			List<Object> params = new ArrayList<Object>();
			params.add(1);
			params.add(1);
			params.add(2);
			params.add(4);
			params.add(b);
			params.add(e);
			Long role = (Long)request.getSession().getAttribute("role");
			if(role==5){//市场专员，仅查询自己的车场手机支付情况
				Long loginUin = (Long)request.getSession().getAttribute("loginuin");
				sql +=" and comid in(select id from com_info_tb where uid=?) "; 
				params.add(loginUin);
			}
			if(city > 0){
				sql += " and comid in(select id from com_info_tb where city=? )";
				params.add(city);
			}
			list1 = daService.getAllMap(sql +" group by comid ",params);
			params.clear();
			sql = "select c.id,count(a.*) atotal,sum(a.amount) tamount from user_account_tb a left join user_info_tb u on a.uid=u.id left join com_info_tb c on u.comid=c.id where  a.target=? and a.uid>? and a.create_time between ? and ? ";
			params.add(1);
			params.add(0);
			params.add(b);
			params.add(e);
			if(role==5){//市场专员，仅查询自己的车场直付情况
				Long loginUin = (Long)request.getSession().getAttribute("loginuin");
				sql +=" and c.id in(select id from com_info_tb where uid=?) "; 
				params.add(loginUin);
			}
			if(city > 0){
				sql += " and c.id in (select id from com_info_tb where city=?) ";
				params.add(city);
			}
			list2 = daService.getAllMap(sql + " group by c.id ", params);
			setAllInfo(list1, list2);
			if(uid == -1){//全部数据
				setName(list1);
			}else{//某一个市场专员下的车场数据
				list1 = setNameByMarketer(list1, uid);
			}
			Collections.sort(list1, new ListSort());
			int count = list1!=null?list1.size():0;
			String json = JsonUtil.Map2Json(list1,1,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("mpaydetail")){
			request.setAttribute("parkid", RequestUtil.processParams(request, "parkid"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			return mapping.findForward("mpaydetail");
		}else if(action.equals("dpaydetail")){
			request.setAttribute("parkid", RequestUtil.processParams(request, "parkid"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			return mapping.findForward("dpaydetail");
		}else if(action.equals("mobilepaydetail")){
			Long parkid = RequestUtil.getLong(request, "parkid", -1L);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String btime = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "btime"));
			String etime = RequestUtil.processParams(request, "etime");
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll("select * from order_tb where comid=? and end_time between ? and ? and state=? and pay_type=? and c_type !=? and total>=? order by end_time desc ", new Object[]{parkid,b,e,1,2,4,1});
			setName(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("directpaydetail")){
			Long id = RequestUtil.getLong(request, "parkid", -1L);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String btime = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "btime"));
			String etime = RequestUtil.processParams(request, "etime");
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll("select a.id,a.uin,a.amount,a.create_time,a.pay_type,u.nickname from user_account_tb a left join user_info_tb u on a.uid=u.id left join com_info_tb c on u.comid=c.id " +
					"where a.target=? and a.uid>? and c.id=? and a.create_time between ? and ? order by a.create_time desc ", new Object[]{1,0,id,b,e});
			setInfo(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("trend")){
			Long cidLong=RequestUtil.getLong(request, "comid", -1L);
			String pname = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "pname"));
			request.setAttribute("_comid", cidLong);
			request.setAttribute("pname", pname);
			String monday = StringUtils.getMondayOfThisWeek();
			request.setAttribute("btime", monday);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("trend");
		}else if(action.equals("querytrend")){
			String sql = "select mobilepay_count total,create_time,comid " +
			" from mobilepay_anlysis_tb ";
			List list = null;
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long _comid = RequestUtil.getLong(request, "comid", -1L);
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e = TimeTools.getLongMilliSecondFrom_HHMMDD(etime)/1000;
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			sqlInfo =new SqlInfo(" create_time between ? and ?  and comid=? ",
					new Object[]{b,e,_comid});
			sql +=" where "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			list = daService.getAllMap(sql +" order by create_time ",params);
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				Long create_time = (Long)map.get("create_time");
				map.put("time",TimeTools.getTimeStr_yyyy_MM_dd(create_time*1000));
			}
			String json = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("totaltrend")){
			String monday = StringUtils.getMondayOfThisWeek();
			request.setAttribute("btime", monday);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("totaltrend");
		}else if(action.equals("querytotal")){
			String sql = "select sum(mobilepay_count) total,create_time " +
			" from mobilepay_anlysis_tb ";
			List list = null;
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e = TimeTools.getLongMilliSecondFrom_HHMMDD(etime)/1000;
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			sqlInfo =new SqlInfo(" create_time between ? and ? group by create_time",
					new Object[]{b,e});
			sql +=" where "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			list = daService.getAllMap(sql +" order by create_time ",params);
			//直付统计
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			sql = "select total dtotal,create_time from directpay_anlysis_tb where create_time between ? and ? order by create_time ";
			list2 = daService.getAll(sql, new Object[]{b,e});
			setList(list, list2);
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				Long create_time = (Long)map.get("create_time");
				map.put("time",TimeTools.getTimeStr_yyyy_MM_dd(create_time*1000));
			}
			String json = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("gettotal")){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			infoMap =getTotal(request);
			String json = StringUtils.createJson(infoMap);
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
	
	private Map<String, Object> getTotal(HttpServletRequest request){
		Integer city = RequestUtil.getInteger(request, "city", -1);
		Long uid = RequestUtil.getLong(request, "uid", -1L);
		SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
		String nowtime= df2.format(System.currentTimeMillis());
		String btime = RequestUtil.processParams(request, "btime");
		String etime = RequestUtil.processParams(request, "etime");
		if(btime.equals(""))
			btime = nowtime;
		if(etime.equals(""))
			etime = nowtime;
		Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
		Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
		List<Object> params = new ArrayList<Object>();
		Long count = 0L;
		String sql1 = "select count(o.*) from order_tb o,com_info_tb c where o.comid=c.id and o.total>=? and o.state=? and o.pay_type=? and o.end_time between ? and ? and c_type!=? ";
		params.add(1);
		params.add(1);
		params.add(2);
		params.add(b);
		params.add(e);
		params.add(4);
		if(uid != -1){
			sql1 += " and c.uid=? ";
			params.add(uid);
		}
		Long role = (Long)request.getSession().getAttribute("role");
		if(role==5){//市场专员，仅查询自己的车场手机支付情况
			Long loginUin = (Long)request.getSession().getAttribute("loginuin");
			sql1 +=" and c.id in(select id from com_info_tb where uid=?) "; 
			params.add(loginUin);
		}
		if(city > 0){
			sql1 += " and c.id in(select id from com_info_tb where city=?)";
			params.add(city);
		}
		Long scount = daService.getCount(sql1, params);
		String sql2 = "select count(a.*) atotal from user_account_tb a left join user_info_tb u on a.uid=u.id left join com_info_tb c on u.comid=c.id where a.uid>? and a.target=? and a.create_time between ? and ? ";
		params.clear();
		params.add(0);
		params.add(1);
		params.add(b);
		params.add(e);
		if(uid != -1){
			sql2 += " and c.uid=? ";
			params.add(uid);
		}
		if(role==5){//市场专员，仅查询自己的车场手机支付情况
			Long loginUin = (Long)request.getSession().getAttribute("loginuin");
			sql2 +=" and c.id in(select id from com_info_tb where uid=?) "; 
			params.add(loginUin);
		}
		if(city > 0){
			sql2 += " and c.id in(select id from com_info_tb where city=?)";
			params.add(city);
		}
		Long dcount = daService.getCount(sql2, params);
		count = scount + dcount;
		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("count", count);
		return infoMap;
	}
	
	private void setList(List<Map<String, Object>> list1,List<Map<String, Object>> list2){
		List<Object> times = new ArrayList<Object>();
		for(Map<String, Object> map : list1){
			map.put("dtotal", 0);
			times.add(map.get("create_time"));
		}
		for(Map<String, Object> map : list2){
			Long create_time = (Long)map.get("create_time");
			if(times.contains(create_time)){
				for(Map<String,Object> map2 : list1){
					Long time = (Long)map2.get("create_time");
					if(create_time.intValue() == time.intValue()){
						map2.put("dtotal", map.get("dtotal"));
						break;
					}
				}
			}else{
				map.put("total", 0);
				list1.add(map);
			}
		}
	}
	
	private void setAllInfo(List<Map<String,Object>> list1,List<Map<String, Object>> list2){
		List<Object> comids = new ArrayList<Object>();
		for(Map<String, Object> map : list1){
			comids.add(map.get("comid"));
			map.put("alltotal", map.get("scount"));
		}
		for(Map<String, Object> map : list2){
			Long alltotal = 0L;
			Long id = (Long)map.get("id");
			Long dtotal = 0L;
			if(map.get("atotal") != null){
				dtotal = (Long)map.get("atotal");
			}
			if(comids.contains(id)){
				for(Map<String, Object> map2 : list1){
					if(map2.get("alltotal") != null){
						alltotal = (Long)map2.get("alltotal");
					}
					Long comid = (Long)map2.get("comid");
					if(id.intValue() == comid.intValue()){
						map2.put("dtotal", dtotal);
						map2.put("damount", map.get("tamount"));
						map2.put("alltotal", alltotal + dtotal);
						break;
					}
				}
			}else{
				Map<String, Object> newMap = new HashMap<String, Object>();
				if(id == null){
					id = -1L;
				}
				newMap.put("comid", id);
				newMap.put("dtotal", map.get("atotal"));
				newMap.put("damount", map.get("tamount"));
				newMap.put("alltotal", alltotal+dtotal);
				list1.add(newMap);
			}
		}
	}
	private void setName(List list){
		List<Object> comids = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				comids.add(map.get("comid"));
			}
		}
		if(!comids.isEmpty()){
			String preParams  ="";
			for(Object comid : comids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select c.id,c.company_name,u.nickname " +
					"from com_info_tb  c,user_info_tb u " +
					" where c.uid=u.id and  c.id in ("+preParams+") ", comids);
			if(!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long comid=(Long)map1.get("comid");
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("id");
						if(comid.intValue()==uin.intValue()){
							map1.put("cname", map.get("company_name"));
							map1.put("uname", map.get("nickname"));
							break;
						}
					}
				}
			}
		}
	}
	
	private List<Map<String,Object>> setNameByMarketer(List<Map<String, Object>> list, Long uid){
		List<Map<String, Object>> newList = new ArrayList<Map<String,Object>>();
		List<Object> comids = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				comids.add(map.get("comid"));
			}
		}
		if(!comids.isEmpty()){
			String preParams  ="";
			for(Object comid : comids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Object> params = new ArrayList<Object>();
			params.addAll(comids);
			params.add(uid);
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select c.id,c.company_name,u.nickname " +
					"from com_info_tb  c,user_info_tb u " +
					" where c.uid=u.id and  c.id in ("+preParams+") and c.uid=? ", params);
			if(!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long comid=(Long)map1.get("comid");
					for(Map<String,Object> map: resultList){
						Long id = (Long)map.get("id");
						if(comid.intValue()==id.intValue()){
							map1.put("cname", map.get("company_name"));
							map1.put("uname", map.get("nickname"));
							newList.add(map1);
							break;
						}
					}
				}
			}
		}
		return newList;
	}
	
	private void setInfo(List<Map<String, Object>> list){
		List<Object> uins = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				uins.add(map.get("uin"));
			}
		}
		if(!uins.isEmpty()){
			String preParams  ="";
			for(Object uin : uins){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select u.id,u.mobile,c.car_number from user_info_tb u,car_info_tb c where u.id=c.uin and u.id in ("+preParams+") ", uins);
			for(Map<String, Object> map : list){
				Long uin = (Long)map.get("uin");
				for(Map<String, Object> map2 : resultList){
					Long id = (Long)map2.get("id");
					if(id.intValue() == uin.intValue()){
						map.put("car_number", map2.get("car_number"));
						map.put("mobile", map2.get("mobile"));
						break;
					}
				}
			}
		}
	}
	
	class ListSort implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Long b1 = (Long)o1.get("alltotal");
			if(b1 == null) b1 = 0L;
			Long b2 = (Long)o2.get("alltotal");
			if(b2 == null) b2 = 0L;
			return b2.compareTo(b1);
		}
		
	}
}

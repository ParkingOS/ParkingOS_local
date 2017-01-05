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

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class VisitAnlysisAction extends Action {
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
			request.setAttribute("etime", df2.format(System.currentTimeMillis()));
			request.setAttribute("btime", df2.format(System.currentTimeMillis()));
			Map map = (Map)request.getSession().getAttribute("userinfo");
			request.setAttribute("auth_flag", map.get("auth_flag"));
			request.setAttribute("department_id", map.get("department_id"));
			return mapping.findForward("visit");
		}else if(action.equals("query")){
			String monday = StringUtils.getMondayOfThisWeek();
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String departvalue = RequestUtil.processParams(request, "departvalue");
			Long department_id = -1L;
			if(!departvalue.equals("") && !departvalue.equals("15")){
				if(departvalue.contains("_")){//总管理员
					String[] node = departvalue.split("_");
					String pid = node[0];
					String nid = node[1];
					String sql = "select id from department_tb where pid=? and nid=?";
					Map<String, Object> map = new HashMap<String, Object>();
					map = daService.getMap(sql, new Object[]{Long.parseLong(pid),Long.parseLong(nid)});
					if(map != null){
						department_id = (Long)map.get("id");
					}
				}else{//部门经理
					department_id = Long.parseLong(departvalue);
				}
			}
			if(btime.equals(""))
				btime = nowtime;
			if(etime.equals(""))
				etime = nowtime;
			String sql = "select count(1) total,uid from visit_info_tb";
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			if(!btime.equals("")&&!etime.equals("")){
				Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
				sqlInfo =new SqlInfo(" create_time between ? and ? ",
						new Object[]{b,e});//state=1已支付;pay_type=2手机支付
			}
			sql +=" where "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAllMap(sql + " group by uid order by total", params);
			list = setName(list,department_id);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"uid");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("detail")){
			request.setAttribute("uid", RequestUtil.processParams(request, "uid"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			request.setAttribute("total", RequestUtil.processParams(request, "total"));
			return mapping.findForward("detail");
		}else if(action.equals("visitdetail")){
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String btime = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "btime"));
			String etime = RequestUtil.processParams(request, "etime");
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select * from visit_info_tb where create_time between ? and ? and state=? and uid=?";
			list = daService.getAll(sql, new Object[]{b,e,0,uid});
			list = setName(list,-1L);
			setOtherInfo(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			json = StringUtils.replaceEnter(json);
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
	
	private List<Map<String, Object>> setName(List<Map<String, Object>> list,Long department_id){
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		List<Object> uids = new ArrayList<Object>();
		List<Object> params = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				uids.add(map.get("uid"));
			}
		}
		if(!uids.isEmpty()){
			String preParams  ="";
			for(Object uid : uids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			String sql = null;
			params.addAll(uids);
			if(department_id == -1){
				sql = "select u.id,u.nickname,d.dname from user_info_tb u left join department_tb d on u.department_id=d.id where u.id in ("+preParams+")";
			}else{
				params.add(department_id);
				sql = "select u.id,u.nickname,d.dname from user_info_tb u,department_tb d where u.department_id=d.id and u.id in ("+preParams+") and department_id=?";
			}
			resultList = daService.getAllMap(sql, params);
			for(Map<String, Object> map : list){
				Long uid = (Long)map.get("uid");
				for(Map<String, Object> map2 : resultList){
					Long id = (Long)map2.get("id");
					if(uid.intValue() == id.intValue()){
						Map<String, Object> map3 = new HashMap<String, Object>();
						map3 = map;
						map3.put("nickname", map2.get("nickname"));
						map3.put("dname", map2.get("dname"));
						list2.add(map3);
					}
				}
			}
		}
		return list2;
	}
	
	private void setOtherInfo(List<Map<String, Object>> list){
		List<Object> uids = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				uids.add(map.get("contacts"));
			}
		}
		if(!uids.isEmpty()){
			String preParams  ="";
			for(Object uid : uids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select u.id,u.nickname,u.mobile,c.company_name from user_info_tb u,com_info_tb c where u.comid=c.id and u.id in ("+preParams+")", uids);
			if(!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long uid = (Long)map1.get("contacts");
					for(Map<String, Object> map : resultList){
						Long id = (Long)map.get("id");
						if(uid.intValue() == id.intValue()){
							map1.put("contact", map.get("nickname"));
							map1.put("mobile", map.get("mobile"));
							map1.put("company_name", map.get("company_name"));
							break;
						}
					}
				}
			}
		}
	}
}

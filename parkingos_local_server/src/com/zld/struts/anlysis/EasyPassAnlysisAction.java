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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class EasyPassAnlysisAction extends Action {
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
			String monday = StringUtils.getMondayOfThisWeek();
			request.setAttribute("btime", monday);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("list");
		}if(action.equals("query")){
			String monday = StringUtils.getMondayOfThisWeek();
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select count(*) total,comid from com_nfc_tb";
			if(btime.equals(""))
				btime = monday;
			if(etime.equals(""))
				etime = nowtime;
			SqlInfo sqlInfo =null;
			List<Object> params = new ArrayList<Object>();
			if(!btime.equals("")&&!etime.equals("")){
				Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
				sqlInfo =new SqlInfo(" update_time between ? and ?  ",
						new Object[]{b,e});
			}
			sql +=" where comid>0 and uin>0 and "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			list = daService.getAllMap(sql +" group by comid order by total desc ",params);
			setName(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("detail")){
			request.setAttribute("parkid", RequestUtil.processParams(request, "parkid"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			request.setAttribute("total", RequestUtil.getLong(request, "total", 0L));
			return mapping.findForward("detail");
		}else if(action.equals("easypassdetail")){
			Long parkid = RequestUtil.getLong(request, "parkid", -1L);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String btime = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "btime"));
			String etime = RequestUtil.processParams(request, "etime");
			SqlInfo sqlInfo =null;
			List<Object> params = new ArrayList<Object>();
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			if(parkid == -1){
				AjaxUtil.ajaxOutput(response, "{\"page\":1,\"total\":0,\"rows\":[]}");
				return null;
			}else{
				String sql = "select n.comid,n.nfc_uuid,n.create_time,n.update_time,n.state,c.car_number from com_nfc_tb n left join car_info_tb c on n.uin=c.uin where n.uin>0 and n.comid=? and update_time between ? and ? ";
				list = daService.getAll(sql, new Object[]{parkid,b,e});
				setName(list);
				int count = list!=null?list.size():0;
				String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"comid");
				AjaxUtil.ajaxOutput(response, json);
			}
		}
		return null;
	}	
	
	private void setName(List list){
		List<Object> comids = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
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
			List<Map<String, Object>> resultList = daService.getAllMap("select c.id,u,nickname,c.company_name from user_info_tb u,com_info_tb c where u.id=c.uid and c.id in ("+preParams+")", comids);
			if(resultList!=null&&!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long comid=(Long)map1.get("comid");
					for(Map<String,Object> map: resultList){
						Long id = (Long)map.get("id");
						if(comid.intValue()==id.intValue()){
							map1.put("company_name", map.get("company_name"));
							map1.put("nickname", map.get("nickname"));
							break;
						}
					}
				}
			}
		}
	}
}

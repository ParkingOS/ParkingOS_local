package com.zld.struts.admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.ExportExcelUtil;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class TicketManageAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(TicketManageAction.class);
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
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			String sql = "select t.*,c.car_number from ticket_tb t left join car_info_tb c on t.uin=c.uin order by limit_day desc ";
			String countsql = "select count(*) from ticket_tb ";
			Long count = daService.getCount(countsql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List ret = query(request);
			List list = (List) ret.get(0);
			Long count =  (Long) ret.get(1);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if (action.equals("exportExcel")){
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List ret = query(request);
			List arrayList = (List) ret.get(0);
			if (ret.size()>0) {
				if(Long.parseLong(ret.get(1)+"")>65535){
					AjaxUtil.ajaxOutput(response,  "数据太大");
					return null;
				}
			}
			List<Map<String, Object>> list = (List<Map<String, Object>>) arrayList;
			List<List<String>> bodyList = new ArrayList<List<String>>();
			String [] heards = null;
			if(list!=null&&list.size()>0){
				//setComName(list);id__money__umoney__limit_day__uin__state__car_number__type
				String [] f = new String[]{"id","money","umoney","limit_day","utime","uin","state","car_number","type"};
				heards = new String[]{"编号","金额","抵扣金额","到期时间","使用时间","状态","车主账户","车牌号","停车券类型"};
				for(Map<String, Object> map : list){
					List<String> values = new ArrayList<String>();
					for(String field : f){
						if("state".equals(field)){
							if(Integer.parseInt(map.get(field)+"")==1){
								values.add("已使用");
							}else{
								values.add("未使用");
							}
						}else if("type".equals(field)){
							if(Integer.parseInt(map.get(field)+"")==0){
								values.add("普通停车券");
							}else if(Integer.parseInt(map.get(field)+"")==1){
								values.add("专用停车券");
							}else if(Integer.parseInt(map.get(field)+"")==2){
								values.add("微信打折券");
							}else if(Integer.parseInt(map.get(field)+"")==3){
								values.add("减时券");
							}else if(Integer.parseInt(map.get(field)+"")==4){
								values.add("全免券");
							}
						}else if("limit_day".equals(field)||"utime".equals(field)){
							values.add(TimeTools.getTime_yyyyMMdd_HHmmss(Long.parseLong(map.get(field)+"")*1000));
						}else{
							values.add(map.get(field)+"");
						}
					}
					bodyList.add(values);
				}
			}
			String fname = "停车券" + com.zld.utils.TimeTools.getDate_YY_MM_DD();
			fname = StringUtils.encodingFileName(fname);
			java.io.OutputStream os;
			try {
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ fname + ".xls");
				response.setContentType("application/x-download");
				os = response.getOutputStream();
				ExportExcelUtil importExcel = new ExportExcelUtil("停车券",
						heards, bodyList);
				importExcel.createExcelFile(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}
	private List query(HttpServletRequest request){
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
		String car_number = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number"));
		List<Object> ret = new ArrayList<Object>();
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		List<Object> params = new ArrayList<Object>();
		String sql = "select t.* from(select t1.*,c.car_number from ticket_tb t1 left join car_info_tb c on t1.uin=c.uin )t where 1=1 ";
		String countsql = "select count(*) from ticket_tb t where 1=1  ";
		SqlInfo sqlInfo = RequestUtil.customSearch(request,"ticket_tb","t",new String[]{"car_number"});
		if(sqlInfo != null){
			sql += " and " + sqlInfo.getSql();
			countsql += " and " + sqlInfo.getSql();
			params.addAll(sqlInfo.getParams());
		}
		if(!car_number.equals("")){
			car_number = "%" + car_number + "%";
			sql += " and c.car_number like ? ";
			countsql += " and c.car_number like ? ";
			params.add(car_number);
		}
		sql += " order by t.limit_day desc ";
		Long count = daService.getCount(countsql, params);
		list = daService.getAll(sql, params, pageNum, pageSize);
		ret.add(list);
		ret.add(count);
		return ret;
	}
}

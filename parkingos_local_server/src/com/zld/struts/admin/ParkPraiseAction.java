package com.zld.struts.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;

public class ParkPraiseAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(ParkPraiseAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comId = (Long)request.getSession().getAttribute("comid");
		if(comId == null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql1 = "select count(1) pcount,comid,praise from com_praise_tb group by comid,praise order by praise desc";
			String sql2 = "select count(1) ccount,comid from com_comment_tb group by comid";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			list1 = daService.getAll(sql1, new Object[]{});
			list2 = daService.getAll(sql2, new Object[]{});
			list1 = setList(list1);
			getAllPark(list1, list2);
			setParkName(list1);
			Collections.sort(list1, new ListSort());
			int count = list1!=null?list1.size():0;
			String json = JsonUtil.Map2Json(list1,pageNum,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql1 = "select count(p.*) pcount,p.comid,p.praise from com_praise_tb p left join com_info_tb c on p.comid=c.id ";
			String sql2 = "select count(p.*) ccount,p.comid from com_comment_tb p left join com_info_tb c on p.comid=c.id ";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"com_praise_tb","p",new String[]{"company_name"});
			String company_name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
			List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			
			if(sqlInfo!=null){
				sql1 +=" where "+sqlInfo.getSql();
				sql2 +=" where "+sqlInfo.getSql();
				params = sqlInfo.getParams();
				if(!company_name.equals("")){
					company_name = "%" + company_name + "%";
					sql1 += " and c.company_name like ?";
					sql2 += " and c.company_name like ?";
					params.add(company_name);
				}
			}else{
				if(!company_name.equals("")){
					company_name = "%" + company_name + "%";
					sql1 += " where c.company_name like ?";
					sql2 += " where c.company_name like ?";
					params.add(company_name);
				}
			}
			sql1 += " group by p.comid,p.praise order by p.praise desc";
			sql2 += " group by p.comid";
			list1 = daService.getAllMap(sql1, params);
			list1 = setList(list1);
			list2 = daService.getAllMap(sql2, params);
			getAllPark(list1, list2);
			setParkName(list1);
			Collections.sort(list1, new ListSort());
			int count = list1!=null?list1.size():0;
			String json = JsonUtil.Map2Json(list1,pageNum,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}if(action.equals("detail")){
			request.setAttribute("parkid", RequestUtil.processParams(request, "parkid"));
			String type = RequestUtil.processParams(request, "type");
			request.setAttribute("otype", type);
			if(type.equals("c")){//评论列表
				return mapping.findForward("commentdetail");
			}else{
				return mapping.findForward("praisedetail");
			}
		}else if(action.equals("parkdetail")){
			Long parkid = RequestUtil.getLong(request, "parkid", -1L);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Map<String, Object>> list1 = new ArrayList<Map<String,Object>>();
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String type = RequestUtil.processParams(request, "otype");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String sql1 = "select comid,uin,count(*) mcount from com_comment_tb group by comid,uin";
			list1 = daService.getAll(sql1, new Object[]{});
			String sql2 = "select p.comid,ca.car_number,u.mobile,p.create_time,p.uin,p.praise from com_praise_tb p left join user_info_tb u on u.id=p.uin left join car_info_tb ca on ca.uin=p.uin where p.comid=? and p.praise=? order by p.create_time desc";
			if(type.equals("b")){
				list = daService.getAll(sql2, new Object[]{parkid,0});
			}else{
				list = daService.getAll(sql2, new Object[]{parkid,1});
			}
			setComment(list, list1);
			setParkName(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"uin");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("commentlist")){
			Long parkid = RequestUtil.getLong(request, "parkid", -1L);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String sql = "select cm.comid,cm.comment,cm.create_time,u.mobile,ca.car_number from com_comment_tb cm left join user_info_tb u on u.id=cm.uin left join car_info_tb ca on ca.uin=cm.uin where cm.comid=? order by cm.create_time desc";
			list = daService.getAll(sql, new Object[]{parkid});
			setParkName(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("comment")){
			request.setAttribute("parkid", RequestUtil.processParams(request, "parkid"));
			request.setAttribute("uin", RequestUtil.processParams(request, "uin"));
			return mapping.findForward("commentlist");
		}else if(action.equals("commentdetail")){
			Long parkid = RequestUtil.getLong(request, "parkid", -1L);
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			String sql = "select cm.comid,cm.comment,cm.create_time,u.mobile,ca.car_number from com_comment_tb cm left join user_info_tb u on u.id=cm.uin left join car_info_tb ca on ca.uin=cm.uin where cm.comid=? and cm.uin=? order by cm.create_time desc";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			list = daService.getAll(sql, new Object[]{parkid,uin});
			setParkName(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
	
	private void getAllPark(List<Map<String, Object>> list1,List<Map<String, Object>> list2){
		for(Map<String, Object> map : list2){
			Long comid = (Long)map.get("comid");
			Boolean b = false;
			for(Map<String, Object> map2 : list1){
				Long comId = (Long)map2.get("comid");
				if(comid.intValue() == comId.intValue()){
					map2.put("ccount", map.get("ccount"));
					b = true;
					break;
				}
			}
			if(!b){
				list1.add(map);
			}
		}
	}
	
	private void setParkName(List<Map<String, Object>> list){
		List<Object> comids = new ArrayList<Object>();
		if(!list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				comids.add(map.get("comid"));
			}
		}
		if(!comids.isEmpty()){
			String preParams  ="";
			for(Object uid : comids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select id,company_name from com_info_tb where id in ("+preParams+")", comids);
			for(Map<String, Object> map : list){
				Long comid = (Long)map.get("comid");
				for(Map<String, Object> map2 : resultList){
					Long id = (Long)map2.get("id");
					if(comid.intValue() == id.intValue()){
						map.put("company_name", map2.get("company_name"));
						break;
					}
				}
			}
		}
	}
	
	private List<Map<String, Object>> setList(List<Map<String, Object>> list){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		List<Long> comidList = new ArrayList<Long>();
		for(Map<String, Object> map : list){
			Long comId = (Long)map.get("comid");
			Integer praise = (Integer)map.get("praise");
			if(praise == 1){//好评
				comidList.add(comId);
				map.put("zcount", map.get("pcount"));
				result.add(map);
			}else{//差评
				if(comidList.contains(comId)){
					for(Map<String, Object> map2 : result){
						Long cid = (Long)map2.get("comid");
						if(comId.intValue() == cid.intValue()){
							map2.put("bcount", map.get("pcount"));
							break;
						}
					}
				}else{
					comidList.add(comId);
					map.put("bcount", map.get("pcount"));
					result.add(map);
				}
			}
		}
		return result;
	}
	
	private void setComment(List<Map<String, Object>> list1,List<Map<String,Object>> list2){
		if(!list1.isEmpty()){
			for(Map<String, Object> map : list1){
				Long comid = (Long)map.get("comid");
				Long uin = (Long)map.get("uin");
				for(Map<String, Object> map2 : list2){
					Long cid = (Long)map2.get("comid");
					Long uid = (Long)map2.get("uin");
					if(comid.intValue() == cid.intValue() && uin.intValue() == uid.intValue()){
						map.put("mcount", map2.get("mcount"));
						break;
					}
				}
			}	
		}
	}
	
	class ListSort implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Long b1 = (Long)o1.get("bcount");
			if(b1 == null) b1 = 0L;
			Long b2 = (Long)o2.get("bcount");
			if(b2 == null) b2 = 0L;
			return b2.compareTo(b1);
		}
		
	}
}

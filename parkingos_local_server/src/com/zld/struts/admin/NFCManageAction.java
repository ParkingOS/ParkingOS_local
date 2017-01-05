package com.zld.struts.admin;

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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * NFC管理，在总管理员后台
 * @author Administrator
 *
 */
public class NFCManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(NFCManageAction.class);

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
			String sql = "select * from com_nfc_tb  ";
			String countSql = "select count(*) from com_nfc_tb ";
			Long count = daService.getLong(countSql,null);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql +" order by id desc", null, pageNum, pageSize);
			}
			list = setList(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from com_nfc_tb ";
			String countSql = "select count(*) from com_nfc_tb " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"nfc_tb");
			List<Object> params = null;
			if(sqlInfo!=null){
				countSql+=" where "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params = sqlInfo.getParams();
			}
			//System.out.println(sqlInfo);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc", params, pageNum, pageSize);
			}
			list = setList(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			String result = createNfc(request);
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("edit")){
			Long comId = RequestUtil.getLong(request, "comid", -1L);
			Long state = RequestUtil.getLong(request, "state", -1L);
			String nfc_uuid =RequestUtil.processParams(request, "nfc_uuid");
			String id =RequestUtil.processParams(request, "id");
			String sql = "update com_nfc_tb set comid=?,nfc_uuid=?,state=? where id=?";
			Object [] values = new Object[]{comId,nfc_uuid,state,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("modify")){
			Long id = RequestUtil.getLong(request, "selids", -1L);
			Integer state = RequestUtil.getInteger(request, "state", 0);
			if(state==0)//0可用，1禁用，为0时是要改为禁用，为1时是要改为禁用，在这里反转 一下。
				state=1;
			else if(state==1)
				state=0;
			String sql = "update com_nfc_tb set state=? where id =?";
			Object [] values = new Object[]{state,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("sutong")){
			//速通卡用户查询
			String sql = "select cn.*,ci.car_number carnumber from com_nfc_tb cn left join car_info_tb ci on cn.uin=ci.uin where cn.uin>0";
			String countSql = "select count(*) from com_nfc_tb where uin>0";
			Long count = daService.getLong(countSql,null);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql +" order by cn.id desc", null, pageNum, pageSize);
			}
			list = setList(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}
		return null;
	}

	private String createNfc(HttpServletRequest request) {
		Long comId = RequestUtil.getLong(request, "comid", -1L);
		Long state = RequestUtil.getLong(request, "state", -1L);
		String nfc_uuid =RequestUtil.processParams(request, "nfc_uuid");
		Long time = System.currentTimeMillis()/1000;
		String sql = "insert into  com_nfc_tb (comid,nfc_uuid,create_time,state) values" +
				"(?,?,?,?)";
		Object [] values = new Object[]{comId,nfc_uuid,time,state};
		int result = daService.update(sql, values);
		return result+"";
	}
	
	private List<Map<String, Object>> setList(List<Map<String, Object>> lists){
		List<Object> comids = new ArrayList<Object>();
		List<Object> uids = new ArrayList<Object>();
		List<Object> uins = new ArrayList<Object>();
		if(lists != null && !lists.isEmpty()){
			for(Map<String,Object> map : lists){
				comids.add(map.get("comid"));
				uids.add(map.get("uid"));
				uins.add(map.get("uin"));
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
			resultList = daService.getAllMap("select id,company_name from com_info_tb where id in ("+preParams+") ", comids);
			if(!resultList.isEmpty()){
				for(Map map1 : lists){
					for(Map map : resultList){
						if(map.get("id") != null){
							Long comid = (Long)map.get("id");
							if(map1.get("comid") != null && map1.get("comid").equals(comid)){
								map1.put("company_name", map.get("company_name"));
								break;
							}
						}else{
							continue;
						}
					}
				}
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
			resultList = daService.getAllMap("select id,nickname from user_info_tb where id in ("+preParams+") ", uids);
			if(!resultList.isEmpty()){
				for(Map map1 : lists){
					for(Map map : resultList){
						if(map.get("id") != null){
							Long uid = (Long)map.get("id");
							if(map1.get("uid") != null && map1.get("uid").equals(uid)){
								map1.put("nickname", map.get("nickname"));
								break;
							}
						}else{
							continue;
						}
					}
				}
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
			resultList = daService.getAllMap("select u.id,car_number from user_info_tb u,car_info_tb c where u.id=c.uin and u.id in ("+preParams+") ", uins);
			if(!resultList.isEmpty()){
				for(Map map1 : lists){
					for(Map map : resultList){
						if(map.get("id") != null){
							Long uin = (Long)map.get("id");
							if(map1.get("uin") != null && map1.get("uin").equals(uin)){
								map1.put("car_number", map.get("car_number"));
								break;
							}
						}else {
							continue;
						}
					}
				}
			}
		}
		return lists;
	}
	

}
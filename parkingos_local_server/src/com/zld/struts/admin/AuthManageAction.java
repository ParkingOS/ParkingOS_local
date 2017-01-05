package com.zld.struts.admin;

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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import common.Logger;

public class AuthManageAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	private Logger logger = Logger.getLogger(AuthManageAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comId = (Long)request.getSession().getAttribute("comid");
		if(comId == null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = setAuthNode();
			String json = StringUtils.createJson(list);
			request.setAttribute("roles", json);
			return mapping.findForward("auth");
		}else if(action.equals("authsetting")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String sql = "select auth from role_auth_tb where role_id=?";
			Map map = pgOnlyReadService.getMap(sql, new Object[]{id});
			String jsonData = "";
			String auth = "[]";
			if(map != null){
				auth = (String) map.get("auth");
			}
			jsonData = checkAuth(auth);
			request.setAttribute("id", id);
			request.setAttribute("jsonData", jsonData);
			return mapping.findForward("authsetting");
		}else if(action.equals("saveauth")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}
			String auth = RequestUtil.processParams(request, "auth");
			String sql = "select count(1) from role_auth_tb where role_id=?";
			Long count = pgOnlyReadService.getLong(sql, new Object[]{id});
			if(count>0){
				sql = "update role_auth_tb set auth=? where role_id=?";
				int result = daService.update(sql, new Object[]{auth,id});
				if(result>0){
					AjaxUtil.ajaxOutput(response, "1");
				}else{
					AjaxUtil.ajaxOutput(response, "0");
				}
			}else{
				sql = "insert into role_auth_tb(role_id,auth) values(?,?)";
				int result = daService.update(sql, new Object[]{id,auth});
				if(result>0){
					AjaxUtil.ajaxOutput(response, "1");
				}else{
					AjaxUtil.ajaxOutput(response, "0");
				}
			}
		}else if(action.equals("rolesetting")){
			String sql = "select * from user_role_tb order by roleid";
			List<Map<String, Object>> list = pgOnlyReadService.getAll(sql, new Object[]{});
			list = setRoleNode(list);
			String json = StringUtils.createJson(list);
			request.setAttribute("roles", json);
			return mapping.findForward("rolesetting");
		}else if(action.equals("rolemanage")){
			Long roleid = RequestUtil.getLong(request, "roleid", -1L);
			String rolename = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "rolename"));
			if(roleid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Object> params = new ArrayList<Object>();
			params.add(roleid);
			String sql = "select count(*) from user_role_tb where roleid=?";
			Long count = daService.getCount(sql, params);
			if(count > 0){
				sql = "update user_role_tb set role_name=? where roleid=?";
				int result = daService.update(sql, new Object[]{rolename,roleid});
				AjaxUtil.ajaxOutput(response, result+"");
			}else{
				sql = "insert into user_role_tb(roleid,role_name,state) values(?,?,?)";
				int result = daService.update(sql, new Object[]{roleid,rolename,0});
				AjaxUtil.ajaxOutput(response, result+"");
			}
		}else if(action.equals("removerole")){
			Long roleid = RequestUtil.getLong(request, "roleid", -1L);
			if(roleid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String sql = "update user_role_tb set state=? where roleid=?";
			int result = daService.update(sql, new Object[]{1,roleid});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("departmentsetting")){
			String sql = "select * from department_tb order by id";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{});
			list = setDepartmentNode(list);
			String json = StringUtils.createJson(list);
			request.setAttribute("deparments", json);
			return mapping.findForward("department");
		}else if(action.equals("authmanage")){
			String sql = "select * from auth_tb";
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{});
			Map<String, Object> parent = new HashMap<String, Object>();
			parent.put("id", 1);
			parent.put("pId", 0);
			parent.put("name", "功能权限");
			parent.put("open", true);
			parent.put("isParent", true);
			list2.add(parent);
			
			for(Map<String, Object> map : list){
				Map<String, Object> newMap = new HashMap<String, Object>();
				Long nid = (Long)map.get("nid");
				Long pid = (Long)map.get("pid");
				Boolean isparent = (Boolean)map.get("isparent");
				Integer state = (Integer)map.get("state");
				newMap.put("id", nid);
				newMap.put("pId", pid);
				newMap.put("isParent", isparent);
				newMap.put("name", map.get("nname"));
				newMap.put("open", true);
				if(state.equals(1)){
					newMap.put("isHidden", true);
				}
				list2.add(newMap);
			}
			String json = StringUtils.createJson(list2);
			request.setAttribute("auths", json);
			return mapping.findForward("authmanage");
		}else if(action.equals("authmanagesave")){
			Long nodeid = RequestUtil.getLong(request, "nodeid", -1L);
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			String nodename = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nodename"));
			String isparent = RequestUtil.processParams(request, "isparent");
			Boolean isParent = false;
			if(isparent.equals("true")){
				isParent = true;
			}
			if(nodeid == -1 || pid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Object> params = new ArrayList<Object>();
			params.add(nodeid);
			params.add(pid);
			String sql = "select count(1) from auth_tb where nid=? and pid=?";
			Long count = daService.getCount(sql, params);
			if(count > 0){
				sql = "update auth_tb set nname=? where nid=? and pid=?";
				int result = daService.update(sql, new Object[]{nodename,nodeid,pid});
				AjaxUtil.ajaxOutput(response, result+"");
			}else{
				sql = "insert into auth_tb(nid,pid,nname,isparent,state) values(?,?,?,?,?)";
				int result = daService.update(sql, new Object[]{nodeid,pid,nodename,isParent,0});
				AjaxUtil.ajaxOutput(response, result+"");
			}
		}else if(action.equals("removeauth")){
			Long nodeid = RequestUtil.getLong(request, "nodeid", -1L);
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			if(nodeid == -1 || pid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String sql = "update auth_tb set state=? where nid=? and pid=?";
			int result = daService.update(sql, new Object[]{1,nodeid,pid});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("departmentsave")){
			Long nodeid = RequestUtil.getLong(request, "nodeid", -1L);
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			String nodename = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nodename"));
			String isparent = RequestUtil.processParams(request, "isparent");
			Boolean isParent = false;
			if(isparent.equals("true")){
				isParent = true;
			}
			if(nodeid == -1 || pid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Object> params = new ArrayList<Object>();
			params.add(nodeid);
			params.add(pid);
			String sql = "select count(1) from department_tb where nid=? and pid=?";
			Long count = daService.getCount(sql, params);
			if(count > 0){
				sql = "update department_tb set dname=? where nid=? and pid=?";
				int result = daService.update(sql, new Object[]{nodename,nodeid,pid});
				AjaxUtil.ajaxOutput(response, result+"");
			}else{
				sql = "insert into department_tb(nid,pid,dname,isparent,state) values(?,?,?,?,?)";
				int result = daService.update(sql, new Object[]{nodeid,pid,nodename,isParent,0});
				AjaxUtil.ajaxOutput(response, result+"");
			}
		}else if(action.equals("removedepartment")){
			Long nodeid = RequestUtil.getLong(request, "nodeid", -1L);
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			if(nodeid == -1 || pid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String sql = "update department_tb set state=? where nid=? and pid=?";
			int result = daService.update(sql, new Object[]{1,nodeid,pid});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("person")){
			String rolevalue = RequestUtil.processParams(request, "rolevalue");
			String departvalue = RequestUtil.processParams(request, "departvalue");
			String sql = "select id,nickname,auth_flag,department_id from user_info_tb where auth_flag!=? and state=? and comid=? ";
			String sqlcount = "select count(1) from user_info_tb where auth_flag!=? and state=? and comid=? ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			SqlInfo sqlInfo = null;
			List<Object> params = new ArrayList<Object>();
			params.add(4);
			params.add(0);
			params.add(0);
			if(rolevalue != null && !rolevalue.equals("")){
				rolevalue = rolevalue.substring(2);
				Long roleid = Long.parseLong(rolevalue);
				sqlInfo = new SqlInfo(" and auth_flag=?", new Object[]{roleid});
			}else if(departvalue != null && !departvalue.equals("")){
				if(departvalue.contains("_")){
					String[] node = departvalue.split("_");
					String pid = node[0];
					String nid = node[1];
					String sqldepart = "select id from department_tb where nid=? and pid=?";
					Map<String, Object> map = new HashMap<String, Object>();
					map = pgOnlyReadService.getMap(sqldepart, new Object[]{Long.parseLong(nid),Long.parseLong(pid)});
					if(map != null){
						Long department_id = (Long)map.get("id");
						sqlInfo = new SqlInfo(" and department_id=?", new Object[]{department_id});
					}
				}
			}
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			if(sqlInfo != null){
				params.addAll(sqlInfo.getParams());
				sql =sql + sqlInfo.getSql() + " order by id";
				sqlcount = sqlcount + sqlInfo.getSql();
			}else{
				sql += " order by id";
			}
			Long count = daService.getCount(sqlcount, params);
			if(count > 0){
				list = pgOnlyReadService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.AuthMap2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("getrolename")){
			Long roleid = RequestUtil.getLong(request, "roleid", -1L);
			if(roleid == -1){
				AjaxUtil.ajaxOutput(response, "");
				return null;
			}
			String sql = "select role_name from user_role_tb where roleid=?";
			Map<String, Object> map = new HashMap<String, Object>();
			map = pgOnlyReadService.getMap(sql, new Object[]{roleid});
			if(map != null){
				AjaxUtil.ajaxOutput(response, map.get("role_name")+"");
			}
		}else if(action.equals("getdepartmentname")){
			Long departmentid = RequestUtil.getLong(request, "departmentid", -1L);
			if(departmentid == -1){
				AjaxUtil.ajaxOutput(response, "");
				return null;
			}
			String sql = "select dname from department_tb where id=?";
			Map<String, Object> map = new HashMap<String, Object>();
			map = pgOnlyReadService.getMap(sql, new Object[]{departmentid});
			if(map != null){
				AjaxUtil.ajaxOutput(response, map.get("dname")+"");
			}
		}else if(action.equals("personmanage")){
			return mapping.findForward("personmanage");
		}else if(action.equals("getroles")){
			String sql = "select * from user_role_tb where state=? order by roleid";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{0});
			StringBuffer jsonData = new StringBuffer();
			jsonData.append("{\"root_role\":{\"id\":\"role\",\"name\":\"所有角色\"},");
			for(Map<String, Object> map : list){
				Long roleid = (Long)map.get("roleid");
				String rolename = (String)map.get("role_name");
				jsonData.append("\"role_"+roleid+"\":{\"id\":\""+roleid+"\",\"name\":\""+rolename+"\"},");
			}
			String value = jsonData.toString();
			if(value.endsWith(","))
				value = value.substring(0,value.length()-1)+"}";
			AjaxUtil.ajaxOutput(response,value);
		}else if(action.equals("getdepartments")){
			String sql = "select * from department_tb where state=?";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{0});
			StringBuffer jsonData = new StringBuffer();
			jsonData.append("{\"root_15\":{\"id\":\"15\",\"name\":\"所有部门\"},");
			for(Map<String, Object> map : list){
				Long nid = (Long)map.get("nid");
				Long pid = (Long)map.get("pid");
				String dname = (String)map.get("dname");
				jsonData.append("\""+pid+"_"+nid+"\":{\"id\":\""+pid+"_"+nid+"\",\"name\":\""+dname+"\"},");
			}
			String value = jsonData.toString();
			if(value.endsWith(","))
				value = value.substring(0,value.length()-1)+"}";
			AjaxUtil.ajaxOutput(response,value);
		}else if(action.equals("personedit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
			}
			String auth_flag = RequestUtil.processParams(request, "auth_flag");
			String department_id = RequestUtil.processParams(request, "department_id");
			if(auth_flag != null && !auth_flag.equals("role")){
				String sql = "update user_info_tb set auth_flag=? where id=?";
				int result = daService.update(sql, new Object[]{Long.parseLong(auth_flag),id});
				if(result == 0){
					AjaxUtil.ajaxOutput(response, "-1");
					return  null;
				}
			}
			if(!department_id.equals("15")){
				if(department_id.contains("_")){
					String[] ids = department_id.split("_");
					String pid = ids[0];
					String nid = ids[1];
					String sql = "select id,isparent from department_tb where pid=? and nid=?";
					Map<String, Object> map = new HashMap<String, Object>();
					map = pgOnlyReadService.getMap(sql, new Object[]{Long.parseLong(pid),Long.parseLong(nid)});
					if(map != null){
						Boolean isparent = (Boolean)map.get("isparent");
						if(!isparent){
							List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
							Map<String, Object> userinfoMap = new HashMap<String, Object>();
							userinfoMap.put("sql", "update user_info_tb set department_id=? where id=? ");
							userinfoMap.put("values", new Object[]{map.get("id"),id});
							bathSql.add(userinfoMap);
							
							Map<String, Object> dataauthMap = new HashMap<String, Object>();
							dataauthMap.put("sql", "update dataauth_tb set department_id=? where authorizer=? ");
							dataauthMap.put("values", new Object[]{map.get("id"),id});
							bathSql.add(dataauthMap);
							
							boolean b = daService.bathUpdate2(bathSql);
							if(!b){
								AjaxUtil.ajaxOutput(response, "-1");
								return  null;
							}
						}
					}
				}
			}
			AjaxUtil.ajaxOutput(response, "1");
		}else if(action.equals("getselroles")){
			String sql = "select * from user_role_tb order by roleid";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{});
			Map<String, Object> parent = new HashMap<String, Object>();
			parent.put("id", 1);
			parent.put("pId", 0);
			parent.put("name", "角色列表");
			parent.put("open", true);
			parent.put("isParent", true);
			list2.add(parent);
			for(Map<String, Object> map : list){
				Map<String, Object> newMap = new HashMap<String, Object>();
				Integer state = (Integer)map.get("state");
				if(!state.equals(1)){
					newMap.put("id", "00"+map.get("roleid"));
					newMap.put("pId", 1);
					newMap.put("name", map.get("role_name"));
					list2.add(newMap);
				}
			}
			String json = StringUtils.createJson(list2);
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("getseldeparts")){
			String sql = "select * from department_tb where state=? order by id";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{0});
			list = setDepartmentNode(list);
			String json = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("todepartpage")){
			Long uin = RequestUtil.getLong(request, "id", -1L);
			String sql = "select * from department_tb where state=? order by id";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = pgOnlyReadService.getAll(sql, new Object[]{0});
			list = setDepartmentNode(list);
			String json = StringUtils.createJson(list);
			request.setAttribute("deparments", json);
			request.setAttribute("uin", uin);
			return mapping.findForward("todepartpage");
		}else if(action.equals("todmempage")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			Long nid = RequestUtil.getLong(request, "nid", -1L);
			if(uin == -1 || pid == -1 || nid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
			}
			String sql = "select id from department_tb where pid=? and nid=? ";
			Map<String, Object> map = pgOnlyReadService.getMap(sql, new Object[]{pid,nid});
			if(map == null){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			request.setAttribute("uin", uin);
			request.setAttribute("departid", map.get("id"));
			return mapping.findForward("todmempage");
		}else if(action.equals("departmembers")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			Long departid = RequestUtil.getLong(request, "departid", -1L);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			if(uin == -1 || departid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
			}
			String sql = "select u.id,u.nickname,r.role_name from user_info_tb u,user_role_tb r where u.auth_flag=r.roleid and u.department_id=? order by u.id ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			params.add(departid);
			list = pgOnlyReadService.getAllMap(sql, params);
			setChecked(list, uin);
			int count = list!=null?list.size():0;
			String json = JsonUtil.AuthMap2Json(list,pageNum,count, fieldsstr,"id");
			request.setAttribute("departid", departid);
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("dataauth")){
			String ids = AjaxUtil.decodeUTF8(RequestUtil.processParams(request,"ids"));
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			Long departid = RequestUtil.getLong(request, "departid", -1L);
			if (uin == -1 || departid == -1) {
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Object[]> values = new ArrayList<Object[]>();
			List<Object> params = new ArrayList<Object>();
			if (!ids.equals("")) {
				String[] authorizers = ids.split(",");
				for (int i = 0; i < authorizers.length; i++) {
					params.add(Long.parseLong(authorizers[i]));
				}
			}
			String preParams = "";
			for (Object authorizer : params) {
				if (preParams.equals("")) {
					preParams = "?";
				} else {
					preParams += ",?";
				}
				Object[] v = new Object[] { uin, authorizer, departid };
				values.add(v);
			}
			int result = daService.update("delete from dataauth_tb where authorizee=? and department_id=? ", new Object[]{uin,departid});
			int r = daService.bathInsert("insert into dataauth_tb(authorizee,authorizer,department_id) values(?,?,?) ",values, new int[] { 4, 4, 4 });
			logger.info("被授权人uin:" + uin + ",授权人：" + ids);
			AjaxUtil.ajaxOutput(response, "1");
		}
		return null;
	}
	
	private String checkAuth(String auth) throws JSONException{
		String sql = "select * from auth_tb";
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		List<Map<String,Object>> list2 = new ArrayList<Map<String,Object>>();
		JSONArray jsonArray = new JSONArray(auth);
		Map<String, Object> parent = new HashMap<String, Object>();
		parent.put("id", 1);
		parent.put("pId", 0);
		parent.put("name", "功能权限");
		parent.put("open", true);
		parent.put("isParent", true);
		if(jsonArray.length() > 0){
			parent.put("checked", true);
		}
		list2.add(parent);
		list = pgOnlyReadService.getAll(sql, new Object[]{});
		for(Map<String, Object> map : list){
			Map<String, Object> newMap = new HashMap<String, Object>();
			Long nid = (Long)map.get("nid");
			Long pid = (Long)map.get("pid");
			Integer state = (Integer)map.get("state");
			Boolean isparent = (Boolean)map.get("isparent");
			for(int i=0;i<jsonArray.length();i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				Long id = jsonObject.getLong("nid");
				Long pId = jsonObject.getLong("pid");
				if(id.equals(nid) && pId.equals(pid)){
					newMap.put("checked", true);
					break;	
				}
			}
			newMap.put("open", true);
			newMap.put("name", map.get("nname") + "(ID:" + map.get("id") + ")");
			newMap.put("id", map.get("nid"));
			newMap.put("pId", map.get("pid"));
			newMap.put("isParent", isparent);
			if(state.equals(1)){
				newMap.put("isHidden", true);
			}
			list2.add(newMap);
		}
		String json = StringUtils.createJson(list2);
		return json;
	}
	
	private List<Map<String, Object>> setDepartmentNode(List<Map<String, Object>> list){
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> parent = new HashMap<String, Object>();
		parent.put("id", 15);
		parent.put("pId", 1);
		parent.put("open", true);
		parent.put("name", "所有部门");
		parent.put("isParent", true);
		list2.add(parent);
		for(Map<String, Object> map2 : list){
			Map<String, Object> map3 = new HashMap<String, Object>();
			Boolean isparent = (Boolean)map2.get("isparent");
			Integer state = (Integer)map2.get("state");
			map3.put("id", map2.get("nid"));
			map3.put("pId", map2.get("pid"));
			map3.put("open", true);
			map3.put("isParent", isparent);
			if(state.equals(1)){
				map3.put("isHidden", true);
			}
			map3.put("name", map2.get("dname"));
			list2.add(map3);
		}
		return list2;
	}
	
	private List setAuthNode(){
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		String sql = "select * from user_role_tb order by roleid";
		list = pgOnlyReadService.getAll(sql, new Object[]{});
		List<Map<String, Object>> list3 = new ArrayList<Map<String,Object>>();
		String sql3 = "select * from department_tb";
		list3 = pgOnlyReadService.getAll(sql3, new Object[]{});
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> parent = new HashMap<String, Object>();
		parent.put("id", 1);
		parent.put("pId", 0);
		parent.put("open", true);
		parent.put("name", "权限系统");
		list2.add(parent);
		Map<String, Object> childen1 = new HashMap<String, Object>();
		childen1.put("id", 11);
		childen1.put("pId", 1);
		childen1.put("open", true);
		childen1.put("name", "角色管理");
		list2.add(childen1);
		Map<String, Object> childen2 = new HashMap<String, Object>();
		childen2.put("id", 12);
		childen2.put("pId", 1);
		childen2.put("open", true);
		childen2.put("name", "部门管理");
		list2.add(childen2);
		Map<String, Object> childen3 = new HashMap<String, Object>();
		childen3.put("id", 13);
		childen3.put("pId", 1);
		childen3.put("open", true);
		childen3.put("name", "功能权限管理");
		list2.add(childen3);
		Map<String, Object> childen5 = new HashMap<String, Object>();
		childen5.put("id", 15);
		childen5.put("pId", 1);
		childen5.put("open", true);
		childen5.put("name", "人员设置");
		list2.add(childen5);
		Map<String, Object> childen4 = new HashMap<String, Object>();
		childen4.put("id", 14);
		childen4.put("pId", 1);
		childen4.put("name", "角色权限");
		list2.add(childen4);
		for(Map<String, Object> map2 : list){
			Map<String, Object> map3 = new HashMap<String, Object>();
			Integer state = (Integer) map2.get("state");
			map3.put("id", "14"+map2.get("roleid"));
			map3.put("pId", 14);
			map3.put("name", map2.get("role_name"));
			if(state.equals(1)){
				map3.put("isHidden", true);
			}
			list2.add(map3);
		}
		
		return list2;
	}
	
	private List setRoleNode(List<Map<String, Object>> list){
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		Map<String, Object> parent = new HashMap<String, Object>();
		parent.put("id", 1);
		parent.put("pId", 0);
		parent.put("open", true);
		parent.put("name", "角色管理");
		parent.put("isParent", true);
		list2.add(parent);
		for(Map<String, Object> map2 : list){
			Map<String, Object> map3 = new HashMap<String, Object>();
			Integer state = (Integer)map2.get("state");
			map3.put("id", "10"+map2.get("roleid"));
			map3.put("pId", 1);
			map3.put("name", map2.get("role_name") + "(ID:" + map2.get("roleid") + ")");
			if(state.equals(1)){
				map3.put("isHidden", true);
			}
			list2.add(map3);
		}
		return list2;
	}
	
	private void setChecked(List<Map<String, Object>> list, Long uin){
		if(!list.isEmpty()){
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = pgOnlyReadService.getAll("select id,authorizer from dataauth_tb where authorizee=? ", new Object[]{uin});
			for(Map<String, Object> map : list){
				Long id = (Long)map.get("id");
				map.put("checked", "notchecked");
				for(Map<String, Object> map2 : resultList){
					Long authorizer = (Long)map2.get("authorizer");
					if(id.intValue() == authorizer.intValue()){
						map.put("checked", "ischecked");
						break;
					}
				}
			}
		}
	}
}

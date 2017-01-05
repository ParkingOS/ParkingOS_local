package com.zld.struts.marketer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import com.zld.service.PgOnlyReadService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class VisitManageActoin extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	private Logger logger = Logger.getLogger(VisitManageActoin.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String action = RequestUtil.processParams(request, "action");
		String token =RequestUtil.processParams(request, "token");
		Map<String,Object> infoMap  = new HashMap<String, Object>();
		if(token==null||"null".equals(token)||"".equals(token)){
			AjaxUtil.ajaxOutput(response, "-2");
			return null;
		}
		Long uid = validToken(token);
		if(uid == null){
			AjaxUtil.ajaxOutput(response, "-2");
			return null;
		}
		if(action.equals("create")){
			//拜访记录ID
			Long visitid = RequestUtil.getLong(request, "visitid", -1L);
			//联系人ID
			Long contacts = RequestUtil.getLong(request, "id", -1L);
			if(contacts == -1 || visitid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String content = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "content"));
			String address = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
			if(content.equals("")) content = null;
			if(address.equals("")) address = null;
			String sql = "insert into visit_info_tb(id,address,visit_content,uid,contacts,create_time,state) values(?,?,?,?,?,?,?)";
			int result = daService.update(sql, new Object[]{visitid,address,content,uid,contacts,System.currentTimeMillis()/1000,0});
			AjaxUtil.ajaxOutput(response, result+"");
			//http://192.168.199.239/zld/visit.do?action=create&content=&address&contacts=&token=&visitid=
		}else if(action.equals("edit")){
			Long visitid = RequestUtil.getLong(request, "id", -1L);
			if(visitid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String content = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "content"));
			String sql = "update visit_info_tb set visit_content=?,create_time=? where id=?";
			int result = daService.update(sql, new Object[]{content,System.currentTimeMillis()/1000,visitid});
			AjaxUtil.ajaxOutput(response, result+"");
			//http://192.168.199.239/zld/visit.do?action=edit&content=&id=&token=
		}else if(action.equals("query")){
			Long contacts = RequestUtil.getLong(request, "id", -1L);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			String sql = "select id,visit_content,create_time,contacts,address from visit_info_tb where state=? and contacts=? ";
			String sqlcount = "select count(*) from visit_info_tb where state=? and contacts=? ";
			List<Object> params = new ArrayList<Object>();
			params.add(0);
			params.add(contacts);
			sql += " order by create_time desc";
			Long count = daService.getCount(sqlcount, params);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, params, pageNum, pageSize);
			setName(list);
			Map<String,Object> infomap  = new HashMap<String, Object>();
			infomap.put("total", count);
			infomap.put("cell", StringUtils.createJson(list));
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infomap));
			//http://192.168.199.239/zld/visit.do?action=query&token=e6c435a27cf1f4a11d11c56d0cebc614
		}else if(action.equals("delete")){
			Long visitid = RequestUtil.getLong(request, "id", -1L);
			String sql = "update visit_info_tb set state=? where id=?";
			int result = daService.update(sql, new Object[]{1,visitid});
			AjaxUtil.ajaxOutput(response, result+"");
			//http://192.168.199.239/zld/visit.do?action=delete&token=&id=
		}else if(action.equals("querytoday")){
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long beginTime = TimeTools.getLongMilliSecondFrom_HHMMDD(nowtime)/1000;
			Long endTime =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long groupuid = RequestUtil.getLong(request, "uid", -1L);//组内成员
			String sql = "select id,visit_content,create_time,contacts,address from visit_info_tb where uid=? and state=? and create_time between ? and ? order by create_time desc ";
			String sqlcount = "select count(*) from visit_info_tb where uid=? and state=? and create_time between ? and ? ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			params.add(groupuid);
			params.add(0);
			params.add(beginTime);
			params.add(endTime);
			Long count = daService.getCount(sqlcount, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setName(list);
			}
			Map<String,Object> infomap  = new HashMap<String, Object>();
			infomap.put("total", count);
			infomap.put("cell", StringUtils.createJson(list));
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infomap));
		}
		//http://192.168.199.239/zld/visit.do?action=querytoday&token=
		return null;
	}
	
	/**
	 * 验证token是否有效
	 * @param token
	 * @return uin
	 */
	private Long validToken(String token) {
		Map tokenMap = daService.getMap("select * from user_session_tb where token=?", new Object[]{token});
 		Long uin = null;
		if(tokenMap!=null&&tokenMap.get("uin")!=null){
			uin = (Long) tokenMap.get("uin");
		}
		return uin;
	}
	
	private void setName(List<Map<String, Object>> list){
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
			resultList = daService.getAllMap("select u.id,u.nickname,c.company_name from com_info_tb c,user_info_tb u where c.id=u.comid and u.id in ("+preParams+")", uids);
			if(!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long uid = (Long)map1.get("contacts");
					for(Map<String, Object> map : resultList){
						Long id = (Long)map.get("id");
						if(uid.intValue() == id.intValue()){
							map1.put("nickname", map.get("nickname"));
							map1.put("company_name", map.get("company_name"));
							break;
						}
					}
				}
			}
		}
	}
}

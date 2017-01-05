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
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;

public class AccountManageAction extends Action{

	@Autowired
	private DataBaseService daService;
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	@Autowired
	private LogService logService;
	
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
			request.setAttribute("hbonus", "0");
////			String key = memcacheUtils.readHBonusCache();
//			if(key!=null&&key.equals("1"))
//				request.setAttribute("hbonus", "1");
//			String cmesg = memcacheUtils.doStringCache("collectormesg_swith", null, null);
//			//System.err.println(">>>>>收费员消息开头:"+cmesg);
//			if(cmesg!=null&&cmesg.equals("1"))
//				request.setAttribute("cmesg", "1");
//			else {
//				request.setAttribute("cmesg", "0");
//			}
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select * from money_record_tb ";
			String countSql = "select count(*) from money_record_tb";
			Long count = daService.getLong(countSql,null);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc",null, pageNum, pageSize);
			}
			if(list!=null)setCompany(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql1 = "select m.* from money_record_tb m ";
			String sql2 = "select m.* from money_record_tb m,user_info_tb u ";
			String sql3 = "select m.* from money_record_tb m,car_info_tb c ";
			String sql4 = "select m.* from money_record_tb m,user_info_tb u,car_info_tb c";
			String countSql1 = "select count(m.*) from money_record_tb m ";
			String countSql2 = "select count(m.*) from money_record_tb m,user_info_tb u";
			String countSql3 = "select count(m.*) from money_record_tb m,car_info_tb c";
			String countSql4 = "select count(m.*) from money_record_tb m,user_info_tb u,car_info_tb c";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String mobile = RequestUtil.processParams(request, "mobile");
			String car_number = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number"));
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"money_record","m",new String[]{"mobile","car_number"});
			List<Object> params = new ArrayList<Object>();
			List list = new ArrayList();
			Long count = 0L;
			if(sqlInfo!=null){
				if(!mobile.equals("") && !car_number.equals("")){
					mobile = "%" + mobile + "%";
					car_number = "%" + car_number + "%";
					countSql4 += " where m.uin=u.id and m.uin=c.uin and u.mobile like ? and c.car_number like ?";
					sql4 += " where m.uin=u.id and m.uin=c.uin and u.mobile like ? and c.car_number like ? ";
					params.add(mobile);
					params.add(car_number);
					countSql4 += " and " + sqlInfo.getSql();
					sql4 += " and " + sqlInfo.getSql();
					params.addAll(sqlInfo.getParams());
					count = daService.getCount(countSql4, params);
					list = daService.getAll(sql4, params, pageNum, pageSize);
				}else if(!mobile.equals("")){
					mobile = "%" + mobile + "%";
					countSql2 += " where m.uin=u.id and u.mobile like ? ";
					sql2 += " where m.uin=u.id and u.mobile like ? ";
					params.add(mobile);
					countSql2 += " and " + sqlInfo.getSql();
					sql2 += " and " + sqlInfo.getSql();
					params.addAll(sqlInfo.getParams());
					count = daService.getCount(countSql2, params);
					list = daService.getAll(sql2, params, pageNum, pageSize);
				}else if(!car_number.equals("")){
					car_number = "%" + car_number + "%";
					countSql3 += " where m.uin=c.uin and c.car_number like ? ";
					sql3 += " where m.uin=c.uin and c.car_number like ? ";
					params.add(car_number);
					countSql3 += " and " + sqlInfo.getSql();
					sql3 += " and " + sqlInfo.getSql();
					params.addAll(sqlInfo.getParams());
					count = daService.getCount(countSql3, params);
					list = daService.getAll(sql3, params, pageNum, pageSize);
				}else {
					countSql1+=" where "+ sqlInfo.getSql();
					sql1 +=" where "+sqlInfo.getSql();
					params = sqlInfo.getParams();
					count = daService.getCount(countSql1, params);
					list = daService.getAll(sql1, params, pageNum, pageSize);
				}
			}
			if(!list.isEmpty())setCompany(list);
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("hbonous")){//写入节日红包开关
			String hbonus = RequestUtil.getString(request, "hbonus");
//			if(hbonus.equals("1")){
//				memcacheUtils.doStringCache("hbonus_swith", "0", "update");
//			}else {
//				memcacheUtils.doStringCache("hbonus_swith", "1", "update");
//			}
			AjaxUtil.ajaxOutput(response, "1");
		}else if(action.equals("cmessage")){//收费员消息开关
			String cmesg = RequestUtil.getString(request, "cmesg");
//			if(cmesg.equals("1")){
//				memcacheUtils.doStringCache("collectormesg_swith", "0", "update");
//			}else {
//				memcacheUtils.doStringCache("collectormesg_swith", "1", "update");
//				notice();
//			}
			AjaxUtil.ajaxOutput(response, "1");
		}
		return null;
	}
	
	private void notice(){
		List<Map<String, Object>> parkerList = daService.getAll("select id from user_info_tb where (auth_flag=? or auth_flag=?) and state=? ",
						new Object[] { 1, 2, 0 });
		
		Map<String, Object> infoMap = new HashMap<String, Object>();
		if(parkerList != null){
			List<Object> uinList = new ArrayList<Object>();
			for(Map<String, Object> map : parkerList){
				uinList.add(map.get("id"));
			}
			infoMap.put("uins", uinList);
//			logService.insertParkUserMesg(6, infoMap);
		}
	}
	
	private void setCompany(List<Map> list){
		for(Map m: list){
			Integer type = (Integer) m.get("type");
			if(type==0)
				m.put("recharge", m.get("amount"));
			else if(type==1)
				m.put("consum", m.get("amount"));
			else if(type==2)
				m.put("withdraw", m.get("amount"));
			if(m.get("comid")!=null){
				Long comId = (Long) m.get("comid");
				Map comMap = daService.getMap("select company_name from com_info_tb where id=?",
						new Object[]{comId});
				if(comMap!=null)
				m.put("company_name",comMap.get("company_name"));
			}
			if(m.get("uin")!=null){
				Map userMap = daService.getMap("select u.mobile from user_info_tb u where u.id=? ",new Object[]{m.get("uin")});
				Map carNumberMap = daService.getMap("select c.car_number from car_info_tb c where c.uin=? ",new Object[]{m.get("uin")});
				if(userMap!=null){
					m.put("mobile", userMap.get("mobile"));
				}
				if(carNumberMap!=null)
					m.put("car_number", carNumberMap.get("car_number"));
					
			}
		}
	}
	
}

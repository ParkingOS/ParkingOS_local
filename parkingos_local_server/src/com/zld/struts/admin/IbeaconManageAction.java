package com.zld.struts.admin;

import java.util.ArrayList;
import java.util.List;

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
 * 泊车点管理，在总管理员后台
 * @author Administrator
 *
 */
public class IbeaconManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(IbeaconManageAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		Long parkid = RequestUtil.getLong(request, "parkid", -1L);
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			request.setAttribute("parkid", parkid);
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select * from  area_ibeacon_tb ";
			String countSql = "select count(ID) from area_ibeacon_tb " ;
			List<Object> params = new ArrayList<Object>();
			if(parkid!=-1){
				countSql +=" where comid=? ";
				sql +=" where comid=? ";
				params.add(parkid);
			}
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"ibeacon_tb");
			if(sqlInfo!=null){
				countSql+=" where  "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}
			
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			System.err.println(json);
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String ibcid = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "ibcid"));
			Long _comid = RequestUtil.getLong(request, "comid", -1L);
			Long pass = RequestUtil.getLong(request, "pass", -1L);
			Integer major = RequestUtil.getInteger(request, "major", -1);
			Integer minor = RequestUtil.getInteger(request, "minor", -1);
			Double lng = RequestUtil.getDouble(request, "lng", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			int ret = daService.update("update area_ibeacon_tb set ibcid=?,comid=?,pass=?,major=?,minor=?,lng=?,lat=? where id =? ",
					new Object[]{ibcid,_comid,pass,major,minor,lng,lat,id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("create")){
			String ibcid = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "ibcid"));
			Double lng = RequestUtil.getDouble(request, "lng", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			Long _comid = RequestUtil.getLong(request, "comid", -1L);
			Long pass = RequestUtil.getLong(request, "pass", -1L);
			Integer major = RequestUtil.getInteger(request, "major", -1);
			Integer minor = RequestUtil.getInteger(request, "minor", -1);
			Long ntime = System.currentTimeMillis()/1000;
			int ret = daService.update("insert into area_ibeacon_tb (ibcid,lng,lat,comid,major," +
					"minor,reg_time,state,pass) values(?,?,?,?,?,?,?,?,?)",
					new Object[]{ibcid,lng,lat,_comid,major,minor,ntime,1,pass});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int ret = daService.update("delete from car_stops_tb where id=?", new Object[]{id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}
		return null;
	}
	
	/*private SqlInfo getSuperSqlInfo(HttpServletRequest request){
		String name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "comid"));
		SqlInfo sqlInfo1 = null;
		if(!name.equals("")){
			sqlInfo1 = new SqlInfo(" c.company_name like ? ",new Object[]{"%"+name+"%"});
		}
		return sqlInfo1;
	}
	*/
}
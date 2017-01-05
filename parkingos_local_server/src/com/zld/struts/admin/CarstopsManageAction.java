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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * 泊车点管理，在总管理员后台
 * @author Administrator
 *
 */
public class CarstopsManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(CarstopsManageAction.class);
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
		}else if(action.equals("query")){
			String sql = "select * from car_stops_tb ";
			String countSql = "select count(ID) from car_stops_tb " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"car_stops");
			List<Object> params = new ArrayList<Object>();
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
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String local = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "address")).trim();
			String name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "name")).trim();
			String resume = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "resume")).trim();
			Double lng = RequestUtil.getDouble(request, "longitude", 0d);
			Double lat = RequestUtil.getDouble(request, "latitude", 0d);
			Double sp = RequestUtil.getDouble(request, "start_price", 0d);
			Double np = RequestUtil.getDouble(request, "next_price", 0d);
			Double mp = RequestUtil.getDouble(request, "max_price", 0d);
			Integer city = RequestUtil.getInteger(request, "city", 110000);
			int ret = daService.update("update car_stops_tb set max_price=?,next_price=?,start_price=?, longitude=?" +
					",latitude=?,name=?,address=?,utime=?,resume=?,city=? where id =?", 
					new Object[]{mp,np,sp,lng,lat,name,local,System.currentTimeMillis()/1000,resume,city,id});
			AjaxUtil.ajaxOutput(response, ret+"");
			
		}else if(action.equals("create")){
			String local = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "address"));
			String name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "name"));
			Double lng = RequestUtil.getDouble(request, "longitude", 0d);
			Double lat = RequestUtil.getDouble(request, "latitude", 0d);
			Double sp = RequestUtil.getDouble(request, "start_price", 0d);
			Double np = RequestUtil.getDouble(request, "next_price", 0d);
			Double mp = RequestUtil.getDouble(request, "max_price", 0d);
			Integer city = RequestUtil.getInteger(request, "city", 110000);
			String resume = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "resume"));
			String loginUser = (String)request.getSession().getAttribute("nickname");
			Long ntime = System.currentTimeMillis()/1000;
			int ret = daService.update("insert into car_stops_tb (name,longitude,latitude,start_price,next_price," +
					"max_price,ctime,utime,creator,resume,address,city) values(?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[]{name,lng,lat,sp,np,mp,ntime,ntime,loginUser,resume,local,city});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int ret = daService.update("delete from car_stops_tb where id=?", new Object[]{id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("uploadpic")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String table = RequestUtil.getString(request, "table");
			if(id!=-1&&!"".equals(table)){
				String picurl = publicMethods.uploadPicToMongodb(request, id, table);
				int ret = 0;
				if(picurl!=null&&!"".equals(picurl)){
					ret = daService.update("update car_stops_tb set pic=? where id = ? ", new Object[]{picurl,id});
				}
				if(ret==1)
					request.setAttribute("result", "上传成功，请关闭当前窗口!");
				else 
					request.setAttribute("result", "上传失败!");
			}else {
				request.setAttribute("result", "上传失败!");
			}
			return mapping.findForward("uploadret");
		}
		return null;
	}
	
}
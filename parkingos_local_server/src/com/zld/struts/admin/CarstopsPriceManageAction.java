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
 * 泊车点价格管理，在总管理员后台
 * @author Administrator
 *
 */
public class CarstopsPriceManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	
	/**
	 * 价格策略：
		    临时停车：38元首三小时，超出后10元一小时
		    长期停车：10元每小时，40元包天
	          优惠活动：   
		    临时停车：首次停车10元首3小时，超出后每小时10元  
		    长期停车：每周首单1元钱
	 */
	private Logger logger = Logger.getLogger(CarstopsPriceManageAction.class);
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
			String sql = "select * from carstops_price_tb ";
			String countSql = "select count(ID) from carstops_price_tb  " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"carstops_price");
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
			Double first_price = RequestUtil.getDouble(request, "first_price", 0d);
			Double next_price = RequestUtil.getDouble(request, "next_price", 0d);
			Double top_price = RequestUtil.getDouble(request, "top_price", 0d);
			Double fav_price = RequestUtil.getDouble(request, "fav_price", 0d);
			Long cid = RequestUtil.getLong(request, "cid", -1L);
			Integer first_unit = RequestUtil.getInteger(request, "first_unit", 0);
			Integer fav_unit = RequestUtil.getInteger(request, "fav_unit", 0);
			Integer next_unit = RequestUtil.getInteger(request, "next_unit", 0);
			Integer type = RequestUtil.getInteger(request, "type", 0);
			String resume = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "resume"));
			Long ntime = System.currentTimeMillis()/1000;
			int ret = daService.update("update carstops_price_tb set cid=?,utime=?,resume=?,first_price=?," +
					"next_price=?,next_unit=?,top_price=?,fav_price=?,first_unit=?,fav_unit=?,type=? where id =?", 
					new Object[]{cid,ntime,resume,first_price,next_price,next_unit,top_price,fav_price,first_unit,fav_unit,type,id});
			AjaxUtil.ajaxOutput(response, ret+"");
			
		}else if(action.equals("create")){
			Double first_price = RequestUtil.getDouble(request, "first_price", 0d);
			Double next_price = RequestUtil.getDouble(request, "next_price", 0d);
			Double top_price = RequestUtil.getDouble(request, "top_price", 0d);
			Double fav_price = RequestUtil.getDouble(request, "fav_price", 0d);
			Long cid = RequestUtil.getLong(request, "cid", -1L);
			Integer first_unit = RequestUtil.getInteger(request, "first_unit", 0);
			Integer fav_unit = RequestUtil.getInteger(request, "fav_unit", 0);
			Integer next_unit = RequestUtil.getInteger(request, "next_unit", 0);
			Integer type = RequestUtil.getInteger(request, "type", 0);
			String resume = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "resume"));
			String loginUser = (String)request.getSession().getAttribute("nickname");
			Long ntime = System.currentTimeMillis()/1000;
			int ret = daService.update("insert into carstops_price_tb (cid,ctime,utime,creator,resume,first_price,next_price,next_unit," +
					"top_price,fav_price,first_unit,fav_unit,type) values(?,?,?,?,?,?,?,?,?,?,?,?,?)",
					new Object[]{cid,ntime,ntime,loginUser,resume,first_price,next_price,next_unit,top_price,fav_price,first_unit,fav_unit,type});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int ret = daService.update("delete from carstops_price_tb where id=?", new Object[]{id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("getcids")){
			List<Map> tradsList = daService.getAll("select id,name from car_stops_tb where state =? ",new Object[]{0});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("name")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}
		return null;
	}
	
}
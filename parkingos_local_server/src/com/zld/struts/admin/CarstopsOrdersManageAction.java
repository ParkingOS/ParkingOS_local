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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.TimeTools;
/**
 * 泊车点管理，在总管理员后台
 * @author Administrator
 *
 */
public class CarstopsOrdersManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(CarstopsOrdersManageAction.class);
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
			String sql = "select * from carstop_order_tb ";
			String countSql = "select count(ID) from carstop_order_tb " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"carstops_order");
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
		}else if(action.equals("getuids")){
			List<Map> tradsList = daService.getAll("select id,nickname from user_info_tb where auth_flag =? and state=?  ",new Object[]{13,0});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("nickname")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("paymoney")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Integer state = RequestUtil.getInteger(request, "state", -1);
			Double amount = RequestUtil.getDouble(request, "amount", 0d);
			int ret = 0;
			if(id!=-1){
				if(state==7)
					ret = daService.update("update carstop_order_tb set state=? where id =? ", new Object[]{8,id});
				else {
					Long end = System.currentTimeMillis()/1000;
					ret = daService.update("update carstop_order_tb set state=?,etime=?,end_time=?,euid=buid,amount=?, pay_type=?" +
							" where id =? ", new Object[]{8,end,end,amount,0,id});
				}
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("getmoney")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Map<String, Object> cotMap = daService.getMap("select * from carstop_order_tb where id =? ", new Object[]{id});
			Long cid = (Long)cotMap.get("cid");
			Long start = (Long)cotMap.get("btime");
			Long end = System.currentTimeMillis()/1000;
			Long uin = (Long)cotMap.get("uin");
			Long create_time = (Long)cotMap.get("start_time");
			double price = getPrice(cid, start, end, uin, create_time);
			AjaxUtil.ajaxOutput(response, price+"");
		}
		return null;
	}
	
	/**
	 * 计算价格
	 * @param cid
	 * @param start
	 * @param end
	 * @return
	 */
	private Double getPrice (Long cid,Long start,Long end,Long uin,Long create_time){
		Map cspriceMap = daService.getMap("select * from carstops_price_tb where cid=? order by ctime limit ?", new Object[]{cid,1});
		Integer ptype = 0;//泊车点价格类型    0临停 1常停
		Double price = 0d;
		Long dur = (end-start)/60;//时长，分钟
		if(dur==0)
			dur=1L;
		logger.info("时长:"+dur);
		if(cspriceMap!=null){
			ptype = (Integer) cspriceMap.get("type");
			//看看是不是符合优惠
			Map coMap = daService.getMap("select max(start_time) ctime from carstop_order_tb where uin =? and start_time !=?", new Object[]{uin,create_time});
			boolean isfirst = false;//是否是首次停车
			boolean ismfirst = false;//是否是本周首次停车
			if(ptype==0){//临停 
				Long ctime = (Long)coMap.get("ctime");
				if(ctime==null)
					isfirst=true;
			}else if(ptype==1){//常停
				if(coMap!=null){
					Long ctime = (Long)coMap.get("ctime");
					Long ntime = TimeTools.getWeekStartSeconds();
					if(ctime==null||ntime>ctime)
						ismfirst=true;
				}
			}
			
			if(ptype==0){//临停    XX元首三小时，超出后X元一小时  首次停车X元首X小时，超出后X元一小时
				Double first_price = Double.valueOf(cspriceMap.get("first_price")+"");
				Double next_price = Double.valueOf(cspriceMap.get("next_price")+"");
				Double fav_price = Double.valueOf(cspriceMap.get("fav_price")+"");
				Integer first_unit =(Integer)cspriceMap.get("first_unit");
				Integer next_unit =(Integer)cspriceMap.get("next_unit");
				Integer fav_unit =(Integer)cspriceMap.get("fav_unit");
				if(isfirst){//是第一次停车，按优惠价格走
					first_price = fav_price;
					first_unit = fav_unit;
				}
				if(dur>first_unit){
					dur  = dur-first_unit;
					if(dur%next_unit!=0)
						price =first_price+(dur/next_unit)*next_price +next_price;
					else {
						price =first_price+(dur/next_unit)*next_price;
					}
				}else {
					price=first_price;
				}
			}else if(ptype==1){//X元每小时，XX元包天  每周首单X元钱
				Double next_price = Double.valueOf(cspriceMap.get("next_price")+"");
				Double top_price = Double.valueOf(cspriceMap.get("top_price")+"");//最高价
				Double fav_price = Double.valueOf(cspriceMap.get("fav_price")+"");//优惠价
				Integer next_unit =(Integer)cspriceMap.get("next_unit");
			
				if(dur%next_unit!=0)
					price =(dur/next_unit)*next_price +next_price;
				else {
					price =(dur/next_unit)*next_price;
				}
				
				if(ismfirst){
					if(fav_price>0&&fav_price<price)//如果优惠价大于当前价格，取当前价
						price=fav_price;
				}
				if(price>top_price&&top_price>0)//如果价格大于最高价，取最高价
					price=top_price;
			}
		}
		return price;
	}
	
}
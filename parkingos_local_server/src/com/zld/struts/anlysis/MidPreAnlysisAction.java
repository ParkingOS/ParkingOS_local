package com.zld.struts.anlysis;

import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.Hash;
import com.zld.AjaxUtil;
import com.zld.impl.MongoClientFactory;
import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class MidPreAnlysisAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	
	private Logger logger = Logger.getLogger(MidPreAnlysisAction.class);
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
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("btime", df2.format(System.currentTimeMillis()));
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String sql = "select count(p.*) pcount,sum(p.amount) pamount,u.id,o.pay_type, u.nickname from parkuser_cash_tb p,user_info_tb u,order_tb o " +
					"where p.uin=u.id and p.orderid=o.id and p.type=? and u.comid=? and p.create_time between ? and ? group by u.id,o.pay_type ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			if(btime.equals(""))
				btime = nowtime;
			if(etime.equals(""))
				etime = nowtime;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			List<Object> params = new ArrayList<Object>();
			params.add(1);
			params.add(comid);
			params.add(b);
			params.add(e);
			list = pgOnlyReadService.getAllMap(sql, params);
			list = setPayType(list);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("detail")){
			request.setAttribute("uin", RequestUtil.processParams(request, "uin"));
			request.setAttribute("type", RequestUtil.processParams(request, "type"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			return mapping.findForward("detail");
		}else if(action.equals("querydetail")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			Integer type = RequestUtil.getInteger(request, "type", 4);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			if(btime.equals("")){
				btime = nowtime;
			}
			if(etime.equals(""))
				etime = nowtime;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			if(btime.length() > 11){
				b = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(btime);
			}
			if(etime.length() > 11){
				e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime);
			}
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			String sql = "select o.*,p.amount,p.create_time pre_time from order_tb o,parkuser_cash_tb p where o.id=p.orderid and p.type=? and o.pay_type=? and p.uin=? and p.create_time between ? and ? order by create_time desc ";
			String sqlcount = "select count(p.*) from order_tb o,parkuser_cash_tb p where o.id=p.orderid and p.type=? and o.pay_type=? and p.uin=? and p.create_time between ? and ? ";
			params.add(1);
			params.add(type);
			params.add(uin);
			params.add(b);
			params.add(e);
			Long count = pgOnlyReadService.getCount(sqlcount, params);
			if(count > 0){
				list = pgOnlyReadService.getAll(sql, params, pageNum, pageSize);
				setList(list);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			System.out.println(json);
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("downloadpic")){
			Long ticketid = RequestUtil.getLong(request, "shopticket_id", -1L);
			downloadticketPics(ticketid, request, response);
		}else if(action.equals("work")){
			request.setAttribute("uin", RequestUtil.processParams(request, "uin"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			return mapping.findForward("workdetail");
		}else if(action.equals("workdetail")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			if(btime.equals(""))
				btime = nowtime;
			if(etime.equals(""))
				etime = nowtime;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			List<Map<String, Object>> workList = new ArrayList<Map<String,Object>>();
			workList = pgOnlyReadService
					.getAll("select * from parkuser_work_record_tb where ((start_time between ? and ? or end_time between ? and ? ) or (start_time < ? and (end_time > ? or end_time is null))) and worksite_id=? and uid=?",
							new Object[] { b, e, b, e, b, e, -1, uin });
			setWorkList(workList);
			int count = workList!=null?workList.size():0;
			String json = JsonUtil.Map2Json(workList,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
	
	private void setWorkList(List<Map<String, Object>> list){
		for(Map<String, Object> map : list){
			Long uid = (Long)map.get("uid");
			Long start_time = (Long)map.get("start_time");
			Long end_time = System.currentTimeMillis()/1000;
			if(map.get("end_time") != null){
				end_time = (Long)map.get("end_time");
			}
			String sql = "select count(p.*) pcount,sum(p.amount) pamount,o.pay_type,p.uin id from parkuser_cash_tb p,order_tb o "
					+ "where p.orderid=o.id and p.uin=? and p.type=? and p.create_time between ? and ? group by o.pay_type,p.uin ";
			List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
			list2 = pgOnlyReadService.getAll(sql, new Object[]{uid, 1, start_time, end_time});
			list2 = setPayType(list2);
			if(list2.size() > 0){
				Map<String, Object> map2 = list2.get(0);
				map.put("cashcount", map2.get("cashcount"));
				map.put("cashamount", map2.get("cashamount"));
				map.put("upaycount", map2.get("upaycount"));
				map.put("upayamount", map2.get("upayamount"));
				map.put("cardcount", map2.get("cardcount"));
				map.put("cardamount", map2.get("cardamount"));
			}
		}
	}
	private List<Map<String, Object>> setPayType(List<Map<String, Object>> list){
		List<Object> uins = new ArrayList<Object>();
		List<Map<String, Object>> rList = new ArrayList<Map<String,Object>>();
		for(Map<String, Object> map : list){
			Long uin = (Long)map.get("id");
			Integer pay_type = (Integer)map.get("pay_type");
			if(uins.contains(uin)){
				for(Map<String, Object> map2 : rList){
					Long id = (Long)map2.get("id");
					if(uin.intValue() == id.intValue()){
						if(pay_type == 4){//现金预支付
							map2.put("cashcount", map.get("pcount"));
							map2.put("cashamount", map.get("pamount"));
						}else if(pay_type == 5){//银联卡预支付
							map2.put("upaycount", map.get("pcount"));
							map2.put("upayamount", map.get("pamount"));
						}else if(pay_type == 6){//商家卡预支付
							map2.put("cardcount", map.get("pcount"));
							map2.put("cardamount", map.get("pamount"));
						}
						break;
					}
				}
			}else{
				uins.add(uin);
				if(pay_type == 4){//现金预支付
					map.put("cashcount", map.get("pcount"));
					map.put("cashamount", map.get("pamount"));
				}else if(pay_type == 5){//银联卡预支付
					map.put("upaycount", map.get("pcount"));
					map.put("upayamount", map.get("pamount"));
				}else if(pay_type == 6){//商家卡预支付
					map.put("cardcount", map.get("pcount"));
					map.put("cardamount", map.get("pamount"));
				}
				rList.add(map);
			}
		}
		
		return rList;
	}
	
	private void setList(List<Map<String, Object>> list){
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				Long id = (Long)map.get("id");
				Long create_time = (Long)map.get("create_time");
				Long end_time = System.currentTimeMillis()/1000;
				if(map.get("end_time") != null){
					end_time = (Long)map.get("end_time");
				}
				map.put("park_time", StringUtils.getTimeString(create_time, end_time));
				Map<String, Object> ticketMap = pgOnlyReadService
						.getMap("select * from ticket_tb where orderid=? and (type=? or type=?) ",
								new Object[] { id, 3, 4 });
				if(ticketMap != null){
					map.put("ticketid", ticketMap.get("id"));
					map.put("umoney", ticketMap.get("umoney"));
					map.put("ticket_type", ticketMap.get("type"));
					map.put("ticket_money", ticketMap.get("money"));
				}
			}
		}
	}
	
	private void downloadticketPics (Long ticketid,HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.info("download ticketPics from mongodb....");
		System.err.println("downloadticketPics from mongodb file:ticketid="+ticketid);
		if(ticketid!=null){
			DB db = MongoClientFactory.getInstance().getMongoDBBuilder("zld");//
			DBCollection collection = db.getCollection("ticket_pics");
			BasicDBObject document = new BasicDBObject();
			BasicDBObject condation = new BasicDBObject();
			document.put("ticketid", ticketid);
			//按生成时间查最近的数据
			condation.put("ctime", -1);
			DBCursor objs = collection.find(document).sort(condation).limit(1);
			DBObject obj = null;
			while (objs.hasNext()) {
				obj = objs.next();
			}
			if(obj == null){
				AjaxUtil.ajaxOutput(response, "");
				return;
			}
			byte[] content = (byte[])obj.get("content");
			db.requestDone();
			response.setDateHeader("Expires", System.currentTimeMillis()+4*60*60*1000);
			//response.setStatus(httpc);
			Calendar c = Calendar.getInstance();
			c.set(1970, 1, 1, 1, 1, 1);
			response.setHeader("Last-Modified", c.getTime().toString());
			response.setContentLength(content.length);
			response.setContentType("image/jpeg");
		    OutputStream o = response.getOutputStream();
		    o.write(content);
		    o.flush();
		    o.close();
		    response.flushBuffer();
		    //response.reset();
		    System.out.println("mongdb over.....");
		}else {
			AjaxUtil.ajaxOutput(response, "-1");
		}
	}
}

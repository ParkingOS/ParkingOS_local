package com.zld.struts.request;

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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class MidPrepayAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(MidPrepayAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		String token =RequestUtil.processParams(request, "token");
		Long comid = RequestUtil.getLong(request, "comid", -1L);
		Map<String,Object> infomap  = new HashMap<String, Object>();
		if(token==null||"null".equals(token)||"".equals(token)){
			infomap.put("result", "fail");
			infomap.put("message", "token无效!");
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infomap));
			return null;
		}
		Long uin = validToken(token);
		if(uin == null){
			infomap.put("result", "fail");
			infomap.put("message", "token无效!");
			return null;
		}
		if(action.equals("midorder")){//客户端中央预支付
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);//订单id
			Long ntime = System.currentTimeMillis()/1000;
			Integer leaving_time = RequestUtil.getInteger(request, "leaving_time", 15);//离场时间
			logger.info("中央现金预支付,midorder>>>>orderid:"+orderid+",ntime:"+ntime+",leaving_time:"+leaving_time+",comid:"+comid);
			Map<String, Object> infoMap = new HashMap<String, Object>();
			if(orderid == -1){
				infoMap.put("result", -1);
				return null;
			}
			Map<String, Object> orderMap = daService.getMap(
					"select * from order_tb where id=? ",
					new Object[] { orderid });
			if(orderMap != null){
				Double total = 0d;
				Double atotal = 0d;
				Double distotal = 0d;
				
				infoMap.put("pretotal", orderMap.get("total"));
				
				Long create_time = (Long)orderMap.get("create_time");
				infoMap.put("start_time", create_time);
				infoMap.put("parktime", StringUtils.getTimeString(create_time, ntime));
				Long end_time = ntime + leaving_time * 60;
				
				Integer car_type = (Integer)orderMap.get("car_type");//0：通用，1：小车，2：大车
				Integer pid = (Integer)orderMap.get("pid");
				infoMap.put("pay_type", orderMap.get("pay_type"));
				if(pid>-1){
					atotal = Double.valueOf(publicMethods.getCustomPrice(create_time, end_time, pid));
				}else {
					atotal = Double.valueOf(publicMethods.getPrice(create_time, end_time, comid, car_type));
				}
				Map<String, Object> shopticketMap = daService
						.getMap("select * from ticket_tb where (type=? or type=?) and orderid=? ",
								new Object[] { 3, 4, orderMap.get("id") });
				if(shopticketMap != null){
					Integer ticket_type = (Integer)shopticketMap.get("type");
					Integer money = (Integer)shopticketMap.get("money");
					Double ticket_umoney = Double.valueOf(shopticketMap.get("umoney") + "");
					if(ticket_type == 3){//商户优惠券
						if(create_time + money * 60 * 60 > end_time){
							distotal = atotal;
						}else{
							end_time = end_time - money * 60 *60;
							
							if(pid>-1){
								total = Double.valueOf(publicMethods.getCustomPrice(create_time, end_time, pid));
							}else {
								total = Double.valueOf(publicMethods.getPrice(create_time, end_time, comid, car_type));
							}
							if(atotal > total){
								distotal = StringUtils.formatDouble(atotal - total);
							}
						}
					}else if(ticket_type == 4){
						distotal = atotal;
					}
					infoMap.put("ticket_type", ticket_type);
					infoMap.put("distime", money);
					infoMap.put("distotal", distotal);
					infoMap.put("shopticket_id", shopticketMap.get("id"));
					infoMap.put("ticket_umoney", ticket_umoney);
					logger.info("该订单已绑定减免券，shopticketid:"+shopticketMap.get("id")+",orderid:"+orderid+",ticket_type:"+ticket_type+",distotal:"+distotal);
				}
				infoMap.put("atotal", atotal);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
				
				logger.info("订单详情，midorder>>>>orderid:"+orderid+",pretotal:"+orderMap.get("total")+",comid:"+comid+",atotal:"+atotal+",car_number:"+orderMap.get("car_number"));
				//http://192.168.199.239/zld/midprepay.do?action=midorder&orderid=787825&leaving_time=15&token=5286f078c6d2ecde9b30929f77771149&comid=1197
			}
		}else if(action.equals("midsweep")){
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);//订单id
			Long shopticket_id = RequestUtil.getLong(request, "shopticket_id", -1L);//扫停车券时的停车券id
			Integer distime = RequestUtil.getInteger(request, "distime", 0);//打印的优惠券额度
			Integer ticket_type = RequestUtil.getInteger(request, "ticket_type", 3);//停车券类型
			Long ntime = System.currentTimeMillis()/1000;
			if(shopticket_id == -1){
				logger.info("中央现金预支付手输减免券midsweep,orderid:"+orderid+",comid:"+comid+",distime:"+distime+",ticket_type:"+ticket_type+",shopticket_id:"+shopticket_id);
			}else{
				logger.info("中央现金预支付扫减免券midsweep,orderid:"+orderid+",comid:"+comid+",shopticket_id:"+shopticket_id);
			}
			if(ticket_type == 4){//全免券
				distime = 0;
			}
			Map<String, Object> infoMap = new HashMap<String, Object>();
			if(orderid == -1){
				infoMap.put("result", -1);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
				return null;
			}
			Map<String, Object> orderMap = daService.getMap("select * from order_tb where id=? ", new Object[]{orderid});
			if(orderMap == null){
				infoMap.put("result", -1);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
				return null;
			}
			if(shopticket_id == -1){
				shopticket_id = daService.getLong("SELECT nextval('seq_ticket_tb'::REGCLASS) AS newid", null);
				SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
				String nowtime= df2.format(System.currentTimeMillis());
				Long limitday =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
				int r = daService.update("insert into ticket_tb(id,create_time,limit_day,money,state,comid,type,shop_id,orderid) values(?,?,?,?,?,?,?,?,?)",
								new Object[] {shopticket_id, System.currentTimeMillis() / 1000, limitday, distime, 0, orderMap.get("comid"), ticket_type, -1L, orderid });
				
				logger.info("手输减免券和订单关联结果，orderid:"+orderid+",shopticket_id:"+shopticket_id+",r:"+r);
			}else{
				Map<String, Object> shopticketMap = daService.getMap(
						"select * from ticket_tb where id=? and state=? ",
						new Object[] { shopticket_id, 0 });
				if(shopticketMap != null){
					ticket_type = (Integer)shopticketMap.get("type");
					Long limit_day = (Long)shopticketMap.get("limit_day");
					distime = (Integer)shopticketMap.get("money");
					if(limit_day < ntime){//已过期
						infoMap.put("result", -2);
						logger.info("扫减免券码，该减免券已过期，orderid:"+orderid+",comid:"+comid+",shopticket_id:"+shopticket_id);
						AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
						return null;
					}
					if(shopticketMap.get("orderid") != null){
						Long oid = (Long)shopticketMap.get("orderid");
						if(oid != orderid){//已经和别的订单关联
							infoMap.put("result", -3);
							logger.info("扫减免券码，该减免券已和别的订单绑定，orderid:"+orderid+",绑定的订单：oid:"+oid+",comid:"+comid+",shopticket_id:"+shopticket_id);
							AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
							return null;
						}
					}
					
					int r = daService.update("update ticket_tb set orderid=? where id=? ",
							new Object[] { orderMap.get("id"), shopticketMap.get("id") });
					logger.info("该商户优惠券和订单绑定，orderid："+orderMap.get("id")+",ticketid:"+shopticketMap.get("id"));
				}else{
					infoMap.put("result", -3);
					AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
					logger.info("扫减免券码，该减免券已被使用，orderid:"+orderid+",comid:"+comid+",shopticket_id:"+shopticket_id);
					return null;
				}
				logger.info("扫减免券码，orderid:"+orderid+",comid:"+comid+",shopticket_id:"+shopticket_id+",ticket_type:"+ticket_type+",distime:"+distime);
			}
			infoMap.put("result", "1");
			infoMap.put("shopticket_id", shopticket_id);
			logger.info("减免返回结果：orderid:"+orderid+",ticket_type:"+ticket_type+",comid:"+comid);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			//http://192.168.199.239/zld/midprepay.do?action=midsweep&token=d3f259da3219fc71e2237b3d72e53e82&orderid=787826&distime=2&ticket_type=4
		}else if(action.equals("midprepay")){//预支付订单
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			Double atotal = RequestUtil.getDouble(request, "atotal", 0d);//总金额
			Double distotal = RequestUtil.getDouble(request, "distotal", 0d);//减免券金额
			Integer ptype = RequestUtil.getInteger(request, "ptype", 0);//支付方式
			logger.info("中央现金预支付midprepay，orderid:"+orderid+",atotal:"+atotal+",distotal:"+distotal+",ptype:"+ptype);
			Map<String, Object> infoMap = new HashMap<String, Object>();
			if(orderid == -1 || atotal == 0){
				infoMap.put("result", -1);
				return null;
			}
			Map<String, Object> orderMap = daService.getMap(
					"select * from order_tb where id=? ",
					new Object[] { orderid });
			if(orderMap == null){
				infoMap.put("result", -1);
				return null;
			}
			Long count = daService.getLong("select count(*) from order_tb where total>? and id=? ", new Object[]{0, orderid});
			if(count == 0){
				List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
				
				//更新订单状态，收费成功
				Map<String, Object> orderSqlMap = new HashMap<String, Object>();
				orderSqlMap.put("sql", "update order_tb set total=?,pay_type=? where id=?");
				orderSqlMap.put("values", new Object[]{atotal,ptype,orderid});
				bathSql.add(orderSqlMap);
				//商户优惠券更新
				Map<String, Object> shopticketsqlMap = new HashMap<String, Object>();
				shopticketsqlMap.put("sql", "update ticket_tb set umoney=?, state=?,utime=? where (type=? or type=?) and orderid=? ");
				shopticketsqlMap.put("values", new Object[]{distotal, 1, System.currentTimeMillis()/1000, 3, 4, orderid});
				bathSql.add(shopticketsqlMap);
				
				//收现金记录
				Map<String, Object> cashsqlMap = new HashMap<String, Object>();
				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
				cashsqlMap.put("values", new Object[]{uin, atotal - distotal, 1, orderid, System.currentTimeMillis()/1000});
				bathSql.add(cashsqlMap);
				
				boolean result= daService.bathUpdate2(bathSql);
				logger.info("预支付订单结果：orderid:"+orderid+",comid:"+comid+",r:"+result);
				if(result){
					infoMap.put("result", 1);
				}else{
					infoMap.put("result", -1);
				}
			}else{
				infoMap.put("result", 1);
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
			//http://192.168.199.239/zld/midprepay.do?action=midprepay&token=d3f259da3219fc71e2237b3d72e53e82&orderid=787825&atotal=58&distotal=0&ptype=5
		}else if(action.equals("midcarnum")){
			String carNumber = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "car_number"));
			carNumber = carNumber.trim().toUpperCase().trim();
			carNumber = "%" + carNumber + "%";
			List<Map<String, Object>> orderList = new ArrayList<Map<String,Object>>();
			orderList = pgOnlyReadService
					.getAll("select id,car_number from order_tb where car_number like ? and state=? and comid=? ",
							new Object[] { carNumber, 0, comid });
			String result = StringUtils.createJson(orderList);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/midprepay.do?action=midcarnum&token=&car_number=&comid=
		}else if(action.equals("getcash")){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			Map<String, Object> map = pgOnlyReadService
					.getMap("select * from parkuser_work_record_tb where uid=? and worksite_id=? and end_time is null order by start_time desc limit ? ",
							new Object[] { uin, -1, 1 });
			if(map == null){
				logger.info("getcash>>>>无值班信息uin:"+uin);
				infoMap.put("result", -1);
			}else{
				Double cash = 0d;
				Long start_time = (Long)map.get("start_time");
				Map<String, Object> cashMap = daService
						.getMap("select sum(c.amount) amount from parkuser_cash_tb c, order_tb o where c.orderid=o.id and c.type=? and c.create_time >=? and o.pay_type=? and c.uin=? ",
								new Object[] { 1, start_time, 4, uin });
				if(cashMap != null && cashMap.get("amount") != null){
					cash = Double.valueOf(cashMap.get("amount") + "");
				}
				infoMap.put("result", 1);
				infoMap.put("cash", cash);
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			//http://192.168.199.239/zld/midprepay.do?action=getcash&token=
		}
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
}

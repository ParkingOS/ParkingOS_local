package com.zld.struts.shop;

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
import com.zld.struts.marketer.VisitManageActoin;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class TicketManageAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	private Logger logger = Logger.getLogger(TicketManageAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		String token =RequestUtil.processParams(request, "token");
		Long shop_id = RequestUtil.getLong(request, "shop_id", -1L);
		Map<String,Object> infoMap  = new HashMap<String, Object>();
		if(token==null||"null".equals(token)||"".equals(token)){
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
		}
		Long uin = validToken(token);
		if(uin == null){
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			return null;
		}
		if(action.equals("create")){
			Integer time = RequestUtil.getInteger(request, "time", 0);
			Integer type = RequestUtil.getInteger(request, "type", 3);
			Map<String, Object> rMap = new HashMap<String, Object>();
			if(shop_id == -1){
				rMap.put("result", -1);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(rMap));
				return null;
			}
			logger.info(">>>>>>>>>>打印优惠券，优惠券类型type:"+type+",优惠额度time："+time+",商户shop_id:"+shop_id);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(StringUtils.getFistdayOfMonth())/1000;
			Long etime =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
			Map<String, Object> shopMap = daService.getMap(
					"select * from shop_tb where id=? ",
					new Object[] { shop_id });
			Integer ticket_limit = (Integer)shopMap.get("ticket_limit");
			Integer ticketfree_limit = (Integer)shopMap.get("ticketfree_limit");
			
			Map<String, Object> map = getlimit(shop_id, btime, etime);
			Long mtotal = 0L;//优惠券已打印额度
			Long ecount = 0L;//全免券打印数量
			if(map.get("mtotal") != null){
				mtotal = (Long)map.get("mtotal");
			}
			if(map.get("ecount") != null){
				ecount = (Long)map.get("ecount");
			}
			if(type == 3){//优惠券
				if(ticket_limit < (mtotal + time)){
					logger.info("优惠券额度已用完，已使用额度mtotal"+mtotal+",优惠券额度time："+time+",商户优惠券额度上限ticket_limit："+ticket_limit+",商户shop_id:"+shop_id);
					rMap.put("result", -2);
					AjaxUtil.ajaxOutput(response, StringUtils.createJson(rMap));
					return null;
				}
			}else if(type == 4){//全免券
				time = 0;//全免券
				if(ticketfree_limit <= ecount){
					logger.info("全免券额度已用完，已使用额度ecount"+ecount+",商户全免券额度上限ticketfree_limit："+ticketfree_limit+",商户shop_id:"+shop_id);
					rMap.put("result", -2);
					AjaxUtil.ajaxOutput(response, StringUtils.createJson(rMap));
					return null;
				}
			}
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			Long ticketid = daService.getkey("seq_ticket_tb");
			String code = null;
			Long ticketids[] = new Long[]{ticketid};
			String []codes = StringUtils.getGRCode(ticketids);
			if(codes.length > 0){
				code = codes[0];
			}
			if(code != null){
				Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
				ticketSqlMap.put("sql", "insert into ticket_tb(id,create_time,limit_day,money,state,comid,type,shop_id) values(?,?,?,?,?,?,?,?)");
				ticketSqlMap.put("values", new Object[]{ticketid, System.currentTimeMillis()/1000, etime, time, 0, shopMap.get("comid"),type, shop_id});
				bathSql.add(ticketSqlMap);
				
				Map<String, Object> qrcodeSqlMap = new HashMap<String, Object>();
				qrcodeSqlMap.put("sql", "insert into qr_code_tb(ctime,type,code,ticketid,comid) values(?,?,?,?,?)");
				qrcodeSqlMap.put("values", new Object[]{System.currentTimeMillis()/1000, 5, code, ticketid, shopMap.get("comid")});
				bathSql.add(qrcodeSqlMap);
			}
			
			boolean b = daService.bathUpdate(bathSql);
			logger.info("打印优惠券结果b："+b+",商户shop_id:"+shop_id);
			if(b){
				rMap.put("result", code);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(rMap));
				return null;
			}else{
				rMap.put("result", -1);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(rMap));
				return null;
			}
			//http://127.0.0.1/zld/shopticket.do?action=create&shop_id=4&time=2&type=4&token=69213a6a50aff2b402a1dd13149a7c44
		}else if(action.equals("query")){
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			if(btime.equals(""))
				btime = nowtime;
			if(etime.equals(""))
				etime = nowtime;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Integer type = RequestUtil.getInteger(request, "type", -1);
			List<Object> params = new ArrayList<Object>();
			String sql = "select * from ticket_tb where shop_id=? and create_time between ? and ? ";
			String sqlcount = "select count(*) from ticket_tb where shop_id=? and create_time between ? and ? ";
			params.add(shop_id);
			params.add(b);
			params.add(e);
			if(type != -1){
				sql += " and type=? ";
				sqlcount += " and type=? ";
				params.add(type);
			}
			sql += " order by create_time desc ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Long count = daService.getCount(sqlcount, params);
			list = daService.getAll(sql, params, pageNum, pageSize);
			Map<String, Object> map = getlimit(shop_id, b, e);
			Long mcount = 0L;//优惠券已打印数量
			Long mtotal = 0L;//优惠券已打印额度
			Long ecount = 0L;//全免券打印数量
			if(map.get("mcount") != null){
				mcount = (Long)map.get("mcount");
				mtotal = (Long)map.get("mtotal");
			}
			if(map.get("ecount") != null){
				ecount = (Long)map.get("ecount");
			}
			Map<String,Object> infomap  = new HashMap<String, Object>();
			infomap.put("total", count);
			infomap.put("mcount", mcount);
			infomap.put("mtotal", mtotal);
			infomap.put("ecount", ecount);
			infomap.put("cell", StringUtils.createJson(list));
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infomap));
			//http://127.0.0.1/zld/shopticket.do?action=query&shop_id=4&type=4&btime=2015-05-01&etime=2015-06-01&token=69213a6a50aff2b402a1dd13149a7c44
		}else if(action.equals("getinfo")){
			Map<String, Object> shopMap = daService.getMap(
					"select * from shop_tb where id=? ",
					new Object[] { shop_id });
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(StringUtils.getFistdayOfMonth())/1000;
			Long etime =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
			Map<String, Object> map = getlimit(shop_id, btime, etime);
			Long mcount = 0L;//优惠券已打印数量
			Long mtotal = 0L;//优惠券已打印额度
			Long ecount = 0L;//全免券打印数量
			if(map.get("mcount") != null){
				mcount = (Long)map.get("mcount");
				mtotal = (Long)map.get("mtotal");
			}
			if(map.get("ecount") != null){
				ecount = (Long)map.get("ecount");
			}
			shopMap.put("mcount", mcount);
			shopMap.put("mtotal", mtotal);
			shopMap.put("ecount", ecount);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(shopMap));
			//http://127.0.0.1/zld/shopticket.do?action=getinfo&shop_id=4&token=69213a6a50aff2b402a1dd13149a7c44
		}else if(action.equals("changeinfo")){
			String description = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "description"));
			int r = daService.update("update shop_tb set description=? where id=? ",
					new Object[] { description, shop_id });
			AjaxUtil.ajaxOutput(response, r+"");
			//http://127.0.0.1/zld/shopticket.do?action=changeinfo&shop_id=4&token=69213a6a50aff2b402a1dd13149a7c44&description=
		}
		return null;
	}
	
	private Map<String, Object> getlimit(Long shop_id, Long btime, Long etime){
		Map<String, Object> infoMap = new HashMap<String, Object>();
		//一个月内打印的额度
		List<Map<String, Object>> list = pgOnlyReadService
				.getAll("select sum(money) mtotal,count(*) mcount,type from ticket_tb where shop_id=? and (type=? or type=?) and create_time between ? and ? group by type ",
						new Object[] { shop_id, 3, 4, btime ,etime });
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				Integer type = (Integer)map.get("type");
				if(type == 3){
					infoMap.put("mtotal", map.get("mtotal"));
					infoMap.put("mcount", map.get("mcount"));
				}else if(type == 4){
					infoMap.put("ecount", map.get("mcount"));
				}
			}
		}
		return infoMap;
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

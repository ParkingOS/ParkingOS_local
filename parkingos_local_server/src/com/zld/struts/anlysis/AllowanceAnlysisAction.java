package com.zld.struts.anlysis;

import java.text.DecimalFormat;
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
import com.zld.service.PgOnlyReadService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class AllowanceAnlysisAction extends Action {
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	private Logger logger = Logger.getLogger(AllowanceAnlysisAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}else if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Integer city_b = RequestUtil.getInteger(request, "city_b", 0);
			Integer city_e = RequestUtil.getInteger(request, "city_e", 659004);
			Long b = 0L;
			if(!btime.equals("")){
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			}
			if(etime.equals("")){
				etime = nowtime;
			}
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			//车场拉拉奖
			List<Object> params = new ArrayList<Object>();
			params.add(0);
			params.add("%停车宝排行榜周奖%");
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			Map<String, Object> map = pgOnlyReadService
					.getMap("select sum(amount) plala from park_account_tb where type=? and remark like ? and create_time between ? and ? and comid in (select id from com_info_tb where city between ? and ?) ",
							params);
			Double plala = StringUtils.formatDouble(map.get("plala"));
			//收费员拉拉奖
			params.clear();
			params.add(0);
			params.add("%停车宝排行榜周奖%");
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			Map<String, Object> map1 = pgOnlyReadService
					.getMap("select sum(p.amount) ulala from parkuser_account_tb p,user_info_tb u where p.uin=u.id and p.type=? and p.remark like ? and p.create_time between ? and ? and u.comid in (select id from com_info_tb where city between ? and ?) ",
							params);
			Double ulala = StringUtils.formatDouble(map1.get("ulala"));
			//拉拉奖额度
			Double lala = plala + ulala;
			//手机支付停车费
			params.clear();
			params.add(4);
			params.add(1);
			params.add(1);
			params.add(2);
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			Map<String, Object> map2 = pgOnlyReadService
					.getMap("select sum(total) mtotal from order_tb where c_type!=? and total>=? and state=? and pay_type=? and end_time between ? and ? and comid in (select id from com_info_tb where city between ? and ?) ",
							params);
			Double mtotal = StringUtils.formatDouble(map2.get("mtotal"));
			//直付停车费
			params.clear();
			params.add(0);
			params.add(1);
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			Map<String, Object> map3 = pgOnlyReadService
					.getMap("select sum(a.amount) ztotal from user_account_tb a,user_info_tb u where a.uid=u.id and a.uid>? and a.target=? and a.create_time between ? and ? and u.comid in (select id from com_info_tb where city between ? and ?) ",
							params);
			//直付停车费
			Double ztotal = StringUtils.formatDouble(map3.get("ztotal"));
			//实收停车费
			Double total = mtotal + ztotal;
			
			
			//代金券
			params.clear();
			params.add(0);
			params.add(1);
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			List<Map<String, Object>> ticketList = new ArrayList<Map<String,Object>>();
			Map<String, Object> rewordticketList = new HashMap<String,Object>();
			ticketList = pgOnlyReadService
					.getAllMap("select sum(umoney) ttotal,type,money from ticket_tb where orderid>? and state=? and utime between ? and ? and comid in (select id from com_info_tb where city between ? and ?) group by type,money ",
							params);
			params.clear();
////			params.add(0);
			params.add(1);
			params.add(b);
			params.add(e);
//			params.add(city_b);
//			params.add(city_e);
			rewordticketList = pgOnlyReadService
			.getMap("select sum(umoney) ttotal from ticket_tb t,parkuser_reward_tb p where t.id = p.ticket_id and t.state=? and p.ctime between ? and ?",
					params);
//			Double tcbttotal = 0d;
			Double tcb0ttotal = 0d;
			Double tcb1ttotal = 0d;
			Double rewardttotal = 0d;
			Double wx3ttotal = 0d;
			Double wx5ttotal = 0d;
			for(Map<String, Object> map4 : ticketList){
				Integer type = (Integer)map4.get("type");
				Integer money = (Integer)map4.get("money");
				Double tttotal = Double.valueOf(map4.get("ttotal") + "");
				if(type == 0){
					tcb0ttotal += tttotal;
				}else if(type == 1){
					tcb1ttotal += tttotal;
				}else if(type == 2){
					if(money == 3){
						wx3ttotal += tttotal;
					}else if(money == 5){
						wx5ttotal += tttotal;
					}
				}
			}
			if(rewordticketList!=null&&rewordticketList.get("ttotal")!=null){
				rewardttotal = Double.parseDouble(rewordticketList.get("ttotal")+"");
			}
			Double  ttotal = wx3ttotal + wx5ttotal+tcb0ttotal+tcb1ttotal+rewardttotal;
			//车场补贴
			params.clear();
			params.add(2);
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			Map<String, Object> map5 = pgOnlyReadService
					.getMap("select sum(amount) pb from park_account_tb where type=? and create_time between ? and ? and comid in (select id from com_info_tb where city between ? and ?) ",
							params);
			Double pb = StringUtils.formatDouble(map5.get("pb"));
			//收费员补贴
			params.clear();
			params.add(0);
			params.add(3);
			params.add(b);
			params.add(e);
			params.add(city_b);
			params.add(city_e);
			Map<String, Object> map6 = pgOnlyReadService
					.getMap("select sum(p.amount) ub from parkuser_account_tb p,user_info_tb u where p.uin=u.id and p.type=? and p.target=? and p.create_time between ? and ? and u.comid in (select id from com_info_tb where city between ? and ?) ",
							params);
			Double ub = StringUtils.formatDouble(map6.get("ub"));
			//车场补贴额
			Double btotal = pb + ub;
			//全部金额
			Double alltotal = lala + total + btotal;
			//拉拉奖的百分比
			String lala_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(lala)/StringUtils.formatDouble(alltotal))*100)+"%";
			//实收停车费百分比
			String parking_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(total-ttotal)/StringUtils.formatDouble(alltotal))*100)+"%";
			//停车券补贴额百分比
			String ticket_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(ttotal)/StringUtils.formatDouble(alltotal))*100)+"%";
			//车场补贴额百分比
			String park_percent = String.format("%.2f", StringUtils.formatDouble(StringUtils.formatDouble(btotal)/StringUtils.formatDouble(alltotal))*100)+"%";
			
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<String, Object> newMap = new HashMap<String, Object>();
			newMap.put("lala", lala);
			newMap.put("parking", total);
			newMap.put("ticket", ttotal);
			newMap.put("allowance", btotal);
			newMap.put("lala_percent", lala_percent);
			newMap.put("parking_percent", parking_percent);
			newMap.put("ticket_percent", ticket_percent);
			newMap.put("allowance_percent", park_percent);
			newMap.put("tcb0ttotal", tcb0ttotal);
			newMap.put("tcb1ttotal", tcb1ttotal);
			newMap.put("rewardttotal", rewardttotal);
			newMap.put("wx3ttotal", wx3ttotal);
			newMap.put("wx5ttotal", wx5ttotal);
			newMap.put("id", 0);
			list.add(newMap);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
	
	private double getWxAllowance(List<Map<String, Object>> orderList){
		double allowance = 0d;
		DecimalFormat dFormat = new DecimalFormat("#.00");
		if(orderList!= null && !orderList.isEmpty()){
			for(Map<String, Object> map : orderList){
				if(map.get("total") != null){
					Double total = Double.valueOf(map.get("total") + "");
					if(total >24){
						allowance += 12d;
					}else{
						allowance += Double.valueOf(dFormat.format(total*0.5));
					}
				}
			}
		}
		allowance = Double.valueOf(dFormat.format(allowance));
		return allowance;
	}
}

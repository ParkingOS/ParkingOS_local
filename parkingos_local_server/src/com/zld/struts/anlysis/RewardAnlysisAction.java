package com.zld.struts.anlysis;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

/**
 * 打赏统计
 * @author Administrator
 *
 */
public class RewardAnlysisAction extends Action {

	@Autowired
	private DataBaseService daService;
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
			String monday = StringUtils.getMondayOfThisWeek();
			request.setAttribute("btime", monday);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String monday = StringUtils.getMondayOfThisWeek();
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String sql = "select sum(money) money,count(distinct uin) count,uid,comid from parkuser_reward_tb  ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getPage(sql, null, 1, 20);
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			if(btime.equals(""))
				btime = monday;
			if(etime.equals(""))
				etime = nowtime;
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			if(!btime.equals("")&&!etime.equals("")){
				Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
				sqlInfo =new SqlInfo(" ctime between ? and ?  ",
						new Object[]{b,e});//c_type 0:NFC,1:IBeacon 2:照牌
			}
			
			sql +=" where "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			
			list = daService.getAllMap(sql +" group by uid,comid order by money desc ",params);
			setName(list,btime,etime);
//			if(list!=null)
//				list = setList(list);
			int count = list!=null?list.size():0;
			//Collections.sort(list, new ListSort());
			//排序,按金额由大到小
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"comid");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("detail")){
			request.setAttribute("parkid", RequestUtil.processParams(request, "parkid"));
			request.setAttribute("btime", RequestUtil.processParams(request, "btime"));
			request.setAttribute("etime", RequestUtil.processParams(request, "etime"));
			request.setAttribute("otype", RequestUtil.processParams(request, "otype"));
			request.setAttribute("total", RequestUtil.getDouble(request, "total", 0d));
			return mapping.findForward("detail");
		}else if(action.equals("parkdetail")){
			Long parkid = RequestUtil.getLong(request, "parkid", -1L);
			List<Map<String, Object>> list = null;
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String btime = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "btime"));
			String etime = RequestUtil.processParams(request, "etime");
			String otype = RequestUtil.processParams(request, "otype");
			Long state = 1L;//已支付
			Integer c_type = 0;//NFC
			if(otype.equals("cn")){
				state = 0L;
			}else if(otype.equals("hz")){
				c_type = 2;
			}else if(otype.equals("cz")){
				state = 0L;
				c_type = 2;
			}else if(otype.equals("e")){
				state = 2L;
			}
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			Long b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			if(parkid!=-1){
				if(!otype.equals("e")){
					list = daService.getAll("select c.company_name cname,o.total,o.create_time,o.end_time,o.nfc_uuid,o.car_number carnumber from parkuser_reward_tb o,com_info_tb c where o.comid=c.id " +
							"and o.comid=?  and o.create_time between ? and ? and o.c_type=? and o.state=? order by o.id desc ",
							new Object[]{parkid,b,e,c_type,state} );
				}else{
					list = daService.getAll("select c.company_name cname,o.total,o.create_time,o.end_time,o.nfc_uuid,o.car_number carnumber from parkuser_reward_tb o,com_info_tb c where o.comid=c.id " +
							"and o.comid=?  and o.create_time between ? and ? and o.state=? order by o.id desc ",
							new Object[]{parkid,b,e,state} );
				}
				int count = list!=null?list.size():0;
				String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"comid");
				AjaxUtil.ajaxOutput(response, json);
				return null;
			}else {
				AjaxUtil.ajaxOutput(response, "{\"page\":1,\"total\":0,\"rows\":[]}");
			}
		}
		return null;
	}
	
	private void setName(List list,String btime,String etime){
		List<Object> uins = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				uins.add(map.get("comid"));
			}
		}
		if(!uins.isEmpty()){
			String preParams  ="";
			for(Object uin : uins){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = daService.getAllMap("select c.id,c.company_name,u.nickname ,u.mobile " +
					"from com_info_tb  c,user_info_tb u " +
					" where c.id=u.comid and  c.id in ("+preParams+") ", uins);
			if(resultList!=null&&!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long comid=(Long)map1.get("comid");
					map1.put("ctime", btime+"-"+etime);
					Double money = StringUtils.formatDouble(map1.get("money"));
					Long count = (Long)map1.get("count");
					map1.put("scroe", 1);
					if(money>0&&count>0)
						map1.put("scroe", StringUtils.formatDouble(money*count).intValue());
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("id");
						if(comid.intValue()==uin.intValue()){
							map1.put("comid", map.get("company_name"));
							map1.put("name", map.get("nickname"));
							map1.put("mobile", map.get("mobile"));
							break;
						}
					}
				}
			}
		}
	}
	
/*	private List<Map<String, Object>> setList(List<Map<String, Object>> lists){
		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
		List<Long> comidList = new ArrayList<Long>();
		for(Map<String, Object> map :lists){
			Long comId = (Long)map.get("comid");
			Integer state = (Integer)map.get("state");
			Integer ctype = (Integer)map.get("c_type");
			if(state == 2){//逃单
				if(comidList.contains(comId)){
					for(Map<String, Object> eMap : result){
						Long cid = (Long)eMap.get("comid");
						if(cid.intValue() == comId.intValue()){
							Long eorder = (Long)eMap.get("eorder");
							eorder += (Long)map.get("scount");//逃单数量不区分c_type
							eMap.put("eorder", eorder.intValue());
						}
					}
				}else{
					comidList.add(comId);
					map.put("eorder", map.get("scount"));
					result.add(map);
				}
			}else if(state==1){//历史订单
				if(comidList.contains(comId)){
					for(Map<String, Object> hMap : result){
						Long cid = (Long)hMap.get("comid");
						if(cid.intValue() == comId.intValue()){
							if(ctype.equals(0)){//NFC
								hMap.put("hncount", map.get("scount"));//NFC历史订单数量
								hMap.put("ntotal", map.get("total"));//NFC结算金额
							}else{
								hMap.put("hzcount", map.get("scount"));//照牌历史订单数量
								hMap.put("ztotal", map.get("total"));//照牌结算金额
							}
							if(hMap.get("ctotal") != null){
								Double ctotal = Double.valueOf((String)(hMap.get("ctotal") + ""));
								Double total = Double.valueOf((String)(map.get("total") + ""));
								if(ctotal < total){
									hMap.put("ctotal", total);
								}
							}else{
								hMap.put("ctotal", map.get("total"));
							}
							break;
						}
					}
				}else{
					comidList.add(comId);
					if(ctype.equals(0)){//NFC
						map.put("hncount", map.get("scount"));//NFC历史订单数量
						map.put("ntotal", map.get("total"));//NFC结算金额
					}else{
						map.put("hzcount", map.get("scount"));//照牌历史订单数量
						map.put("ztotal", map.get("total"));//照牌结算金额
					}
					map.put("ctotal", map.get("total"));//用于比较大小排序的数值
					map.put("eorder", null);
					result.add(map);
				}
			}else {//当前订单，未结算的
				if(comidList.contains(comId)){
					for(Map<String, Object> dMap : result){
						Long cid = (Long)dMap.get("comid");
						if(cid.intValue()==comId.intValue()){
							if(ctype.equals(0)){//NFC
								dMap.put("cncount", map.get("scount"));//NFC当前订单数量
							}else{
								dMap.put("czcount", map.get("scount"));//照牌当前订单数量
							}
							break;
						}
					}
				}else {
					map.put("corder", map.get("scount"));
					if(ctype.equals(0)){//NFC
						map.put("cncount", map.get("scount"));//NFC当前订单数量
					}else{
						map.put("czcount", map.get("scount"));//照牌当前订单数量
					}
					map.put("eorder", null);
					map.put("hncount", null);
					map.put("hzcount", null);
					result.add(map);
				}
			}
		}
		return result ;
	}*/
/*	
	class ListSort implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			BigDecimal b1 = new BigDecimal(0);
			BigDecimal b2 = new BigDecimal(0);
			if(o1.get("ctotal") != null){
				if(o1.get("ctotal") instanceof Double){
					Double ctotal = (Double)o1.get("ctotal");
					b1 = b1.valueOf(ctotal);
				}else{
					b1 = (BigDecimal)o1.get("ctotal");
				}
			}
			if(o2.get("ctotal") != null){
				if(o2.get("ctotal") instanceof Double){
					Double ctotal = (Double)o2.get("ctotal");
					b2 = b2.valueOf(ctotal);
				}else{
					b2 = (BigDecimal)o2.get("ctotal");
				}
			}
			return b2.compareTo(b1);
		}
		
	}
*/	
}
package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.TimeTools;

public class ChargeAnlysisAction extends Action {
	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long b = 0L;
			if(!btime.equals("")){
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
			}
			if(etime.equals("")){
				etime = nowtime;
			}
			Long e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			String sql = "select uin,pay_type,sum(amount) money,count(*) atotal from user_account_tb where type=? and (pay_type=? or pay_type=? or pay_type=?) and create_time between ? and ? group by pay_type,uin order by pay_type desc";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{0,1,2,9,b,e});
			Map<String, Object> map = new HashMap<String, Object>();
			map = setList(list);
			map.put("id", 0);
			list.clear();
			list.add(map);
			int count = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}
		return null;
	}
	
	private Map<String, Object> setList(List<Map<String, Object>> list){
		Map<String, Object> newMap = new HashMap<String, Object>();
		List<Object> wxuins = new ArrayList<Object>();
		List<Object> wxpuins = new ArrayList<Object>();
		List<Object> zfbuins = new ArrayList<Object>();
		List<Object> bothuins = new ArrayList<Object>();
		Long wxcount = 0L;
		Long wxpcount = 0L;
		Long zfbcount = 0L;
		Double wxamount = 0d;
		Double wxpamount = 0d;
		Double zfbamount = 0d;
		for(Map<String, Object> map : list){
			Integer pay_type = (Integer)map.get("pay_type");
			Long uin = (Long)map.get("uin");
			Long atotal = (Long)map.get("atotal");
			Double money = Double.valueOf(map.get("money")+"");
			if(pay_type == 9){
				wxpcount += atotal;
				wxpamount += money;
				wxpuins.add(uin);
			}else if(pay_type == 2){
				wxcount += atotal;
				wxamount += money;
				wxuins.add(uin);
			}else{
				zfbcount += atotal;
				zfbamount += money;
				zfbuins.add(uin);
				if(wxuins.contains(uin)){
					bothuins.add(uin);
				}
			}
		}
		newMap.put("wxcount", wxcount);
		newMap.put("zfbcount", zfbcount);
		newMap.put("wxpcount", wxpcount);
		newMap.put("wxamount", String.format("%.2f",wxamount));
		newMap.put("wxpamount", String.format("%.2f",wxpamount));
		newMap.put("zfbamount", String.format("%.2f",zfbamount));
		newMap.put("tcount", wxcount + zfbcount + wxpcount);
		newMap.put("tamount", String.format("%.2f",wxamount + zfbamount + wxpamount));
		newMap.put("wxvip", wxuins.size());
		newMap.put("wxpvip", wxpuins.size());
		newMap.put("zfbvip", zfbuins.size());
		newMap.put("bothvip", bothuins.size());
		//×ÜÓà¶î
		Map<String,Object> map = new HashMap<String, Object>();
		map = daService.getMap("select sum(balance) residuemoney from user_info_tb where auth_flag=?", new Object[]{4});
		newMap.put("residuemoney", map.get("residuemoney"));
		return newMap;
	}
}

package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
 * NFC卡使用统计
 * @author Administrator
 *
 */
public class ProductanlysisAction extends Action {

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
			String monday = StringUtils.getFistdayOfMonth();
			request.setAttribute("btime", monday);
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String monday = StringUtils.getFistdayOfMonth();
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			String type = RequestUtil.processParams(request, "type");
			String sql = "select c.*,p.price,p.p_name from carower_product c ,product_package_tb p where c.pid =p.id and p.comid=?    ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getPage(sql, null, 1, 20);
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			String carnumber = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "carnumber"));
			String mobile  = RequestUtil.processParams(request, "mobile");
			if(btime.equals(""))
				btime = monday;
			if(etime.equals(""))
				etime = nowtime;
			SqlInfo sqlInfo =null;
			List<Object> params = null;
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000;
			 if(type.equals("tomonth")){
				b=TimeTools.getMonthStartSeconds();
				sqlInfo =new SqlInfo(" create_time between ? and ? ",
						new Object[]{b,e});
			}else if(!btime.equals("")&&!etime.equals("")){
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
				sqlInfo =new SqlInfo(" create_time between ? and ? ",
						new Object[]{b,e});
			}
			sql +=" and "+sqlInfo.getSql();
			params= sqlInfo.getParams();
			params.add(0,comid);
			
			list = daService.getAllMap(sql +"  order by create_time desc ",params);
			int count = list!=null?list.size():0;
			
			//String tc = "0_0_0_0";
			//if(list!=null)
				// tc=setName(list,dstr);
			Double tmoney=setList(list);
			list=filterList(list,carnumber,mobile);
			
			//Double all = Double.valueOf(tc.split("_")[0]);
			String money = "总金额："+StringUtils.formatDouble(tmoney);//"总订单数："+tc.split("_")[1]+"，总结算金额："+StringUtils.formatDouble(all)+"元，其中现金支付："+StringUtils.formatDouble(tmoney)+"元，停车宝支付 ："+StringUtils.formatDouble((all-tmoney))+"元";
			String json = JsonUtil.anlysisMap2Json(list,1,count, fieldsstr,"uin",money);
			System.out.println(json);
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}
		return null;
	}
	
	private List<Map> filterList(List<Map> list,String carNumber,String mobile){
		List<Map> nlist = new ArrayList<Map>();
		if(carNumber.equals("")&&mobile.equals(""))
			return list;
		else {
			List<Map> list1 = new ArrayList<Map>();
			boolean isFilterCarNo = false;
			for(Map map : list){
				if(!carNumber.equals("")){//过滤车牌号
					isFilterCarNo = true;
					String carno = (String)map.get("carnumber");
					if(carno!=null&&carno.indexOf(carNumber)!=-1)
						list1.add(map);
				}
			}
			if(!isFilterCarNo)
				list1 = list;
			if(!list1.isEmpty()&&!mobile.equals("")){
				for(Map map2: list1){
					if(!mobile.equals("")){//过滤手机号
						String _moblie = (String)map2.get("mobile");
						if(_moblie!=null&&_moblie.indexOf(mobile)!=-1)
							nlist.add(map2);
					}
				}
			}else {
				nlist = list1;
			}
		}
		return nlist;
	}
	
	private Double setList(List list){
		Double total =0d;
		List<Object> uins = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				uins.add(map.get("uin"));
				total += Double.valueOf(map.get("total")+"");
				Long btime =(Long) map.get("b_time");
				Long etime =(Long) map.get("e_time");
				map.put("exprise", TimeTools.getTimeStr_yyyy_MM_dd(btime*1000)+" 至   "+TimeTools.getTimeStr_yyyy_MM_dd(etime*1000));
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
			List<Map<String, Object>> resultList = daService.getAllMap("select id,mobile  " +
					"from user_info_tb " +
					" where id in ("+preParams+") ", uins);
			if(resultList!=null&&!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("id");
						if(map1.get("uin").equals(uin)){
							map1.put("mobile", map.get("mobile"));
							break;
						}
					}
				}
			}
			resultList = daService.getAllMap("select uin,car_number  " +
					"from car_info_tb " +
					" where uin in ("+preParams+") ", uins);
			if(resultList!=null&&!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("uin");
						if(map1.get("uin").equals(uin)){
							map1.put("carnumber", map.get("car_number"));
							break;
						}
					}
				}
			}
			
		}
		return total;
	}
}
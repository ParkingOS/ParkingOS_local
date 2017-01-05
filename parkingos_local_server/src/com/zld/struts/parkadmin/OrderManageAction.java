package com.zld.struts.parkadmin;

import java.io.IOException;
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
import com.zld.utils.ExportExcelUtil;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
import com.zld.utils.ZLDType;
/**
 * 停车场后台管理员登录后，查看订单，不能修改和删除
 * @author Administrator
 *
 */
public class OrderManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(OrderManageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		Integer role = RequestUtil.getInteger(request, "role",-1);
		request.setAttribute("role", role);
		if(comid==null||comid==-1){
			response.sendRedirect("login.do");
			return null;
		}
		int total = 0;
		int month = 0;
		int parktotal = 0;
		int blank = 0;
		Map allmap = daService.getMap("select count(*)total from order_tb where comid = ? and state=?", new Object[]{comid,0});
		Map monthmap = daService.getMap("select count(*)total from order_tb where comid = ? and state=? and c_type=?", new Object[]{comid,0,5});
		Map cominfo = daService.getMap("select share_number from com_info_tb where id = ? ", new Object[]{comid});
		if(allmap!=null&&allmap.get("total")!=null)
			total = Integer.valueOf(allmap.get("total")+"");
		if(monthmap!=null&&monthmap.get("total")!=null)
			month = Integer.valueOf(monthmap.get("total")+"");
		if(cominfo!=null&&cominfo.get("share_number")!=null)
			parktotal = Integer.valueOf(cominfo.get("share_number")+"");
		blank = parktotal-total;
		if(blank<=0)
			blank=0;
		request.setAttribute("parkinfo",  AjaxUtil.decodeUTF8("车位统计:场内停车"+total+"辆,其中月卡车"+month+"辆,临停车"+(total-month)+"辆,空车位"+blank+"辆"));
		if(action.equals("")){
			request.setAttribute("comid", comid);
			return mapping.findForward("list");
		}else if(action.equals("query")){
			List arrayList = query(request,comid);
			List list = (List<Map<String, Object>>) arrayList.get(0);
			Integer pageNum = (Integer) arrayList.get(1);
			long count = Long.valueOf(arrayList.get(2)+"");
			String fieldsstr = arrayList.get(3)+"";
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("exportExcel")){
			Map uin = (Map)request.getSession().getAttribute("userinfo");
			if(uin!=null&&uin.get("auth_flag")!=null){
				if(Integer.valueOf(uin.get("auth_flag")+"")==ZLDType.ZLD_ACCOUNTANT_ROLE||Integer.valueOf(uin.get("auth_flag")+"")==ZLDType.ZLD_CARDOPERATOR){
					String ret = "没有权限导出订单数据";
					logger.info(">>>>"+ret);
					AjaxUtil.ajaxOutput(response,ret);
					return null;
				}
			}
			List arrayList = query(request,comid);
			List<Map<String, Object>> list = (List<Map<String, Object>>) arrayList.get(0);
			List<List<String>> bodyList = new ArrayList<List<String>>();
			String [] heards = null;
			if(list!=null&&list.size()>0){
				//setComName(list);
				String [] f = new String[]{"id","c_type","car_number","create_time","end_time","duration","pay_type","total","uid","state","in_passid","out_passid"};
				heards = new String[]{"编号","进场方式","车牌号","进场时间","出场时间","时长","支付方式","金额","收款人","状态","进场通道","出场通道"};
				for(Map<String, Object> map : list){
					List<String> values = new ArrayList<String>();
					for(String field : f){
						if("uid".equals(field)){
							values.add(getUinName(Long.valueOf(map.get(field)+"")));
						}else if("c_type".equals(field)){
							switch(Integer.valueOf(map.get(field)+"")){//0:NFC,1:IBeacon,2:照牌   3通道照牌 4直付 5月卡用户
							case 0:values.add("NFC刷卡");break;
							case 1:values.add("Ibeacon");break;
							case 2:values.add("手机扫牌");break;
							case 3:values.add("通道扫牌");break;
							case 4:values.add("直付");break;
							case 5:values.add("月卡");break;
							default:values.add("");
							}
						}else if("duration".equals(field)){
							Long start = (Long)map.get("create_time");
							Long end = (Long)map.get("end_time");
							if(start!=null&&end!=null){
								values.add(StringUtils.getTimeString(start, end));
							}else{
								values.add("");
							}
						}else if("pay_type".equals(field)){
							switch(Integer.valueOf(map.get(field)+"")){//0:NFC,1:IBeacon,2:照牌   3通道照牌 4直付 5月卡用户
							case 0:values.add("账户支付");break;
							case 1:values.add("现金支付");break;
							case 2:values.add("手机支付");break;
							case 3:values.add("包月");break;
							case 4:values.add("中央预支付现金");break;
							case 5:values.add("中央预支付银联卡");break;
							case 6:values.add("中央预支付商家卡");break;
							case 8:values.add("免费");break;
							default:values.add("");
							}
						}else if("state".equals(field)){
							switch(Integer.valueOf(map.get(field)+"")){//0:NFC,1:IBeacon,2:照牌   3通道照牌 4直付 5月卡用户
							case 0:values.add("未支付");break;
							case 1:values.add("已支付");break;
							case 2:values.add("逃单");break;
							default:values.add("");
							}
						}else if("in_passid".equals(field)||"out_passid".equals(field)){
							if(map.get(field)!=null){
								String sql = "select passname from com_pass_tb where comid=? and id = ?";
								Map m = daService.getPojo(sql, new Object[]{comid,Integer.valueOf(map.get(field)+"")});
								if(m!=null){
									values.add(m.get("passname")+"");
								}else{
									values.add("");
								}
							}else{
								values.add("");
							}
						}else{
							if("create_time".equals(field)||"end_time".equals(field)){
								if(map.get(field)!=null){
									values.add(TimeTools.getTime_yyyyMMdd_HHmmss(Long.valueOf((map.get(field)+""))*1000));
								}else{
									values.add("null");
								}
							}else{
								values.add(map.get(field)+"");
							}
						}
					}
					bodyList.add(values);
				}
			}
			String fname = "订单数据" + com.zld.utils.TimeTools.getDate_YY_MM_DD();
			fname = StringUtils.encodingFileName(fname);
			java.io.OutputStream os;
			try {
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ fname + ".xls");
				response.setContentType("application/x-download");
				os = response.getOutputStream();
				ExportExcelUtil importExcel = new ExportExcelUtil("订单数据",
						heards, bodyList);
				importExcel.createExcelFile(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
//			String json = "";
//			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("completezeroorder")){
			String ids =RequestUtil.processParams(request, "ids");
			int ret = 0;
			if(StringUtils.isNotNull(ids)){
				String[] idsarr = ids.split(",");
				long etime = System.currentTimeMillis()/1000;
				for (int i = 0; i < idsarr.length; i++) {
					long id = Long.valueOf(idsarr[i]);
					//非月卡
					ret=daService.update("update order_tb set total=?,pay_type=?,end_time=?,state=? where id=? and state=? and c_type<>?", new Object[]{0.0,1,etime,1,id,0,5});
					if(ret==1){
						logger.info("后台0元结算非月卡订单："+id +",结算方式pay_type：1");
					}
					//月卡
					ret=daService.update("update order_tb set total=?,pay_type=?,end_time=?,state=? where id=? and state=? and c_type=?", new Object[]{0.0,3,etime,1,id,0,5});
					if(ret==1){
						logger.info("后台0元结算非月卡订单："+id +",结算方式pay_type：3");
					}
				}
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("edit")){
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String strid =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "strid"));
			String phone =RequestUtil.processParams(request, "phone");
			String mobile =RequestUtil.processParams(request, "mobile");
			String id =RequestUtil.processParams(request, "id");
			String sql = "update order_tb set nickname=?,strid=?,phone=?,mobile=? where uin=?";
			Object [] values = new Object[]{nickname,strid,phone,mobile,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "selids");
			String sql = "delete from user_info where id =?";
			Object [] values = new Object[]{Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("carpics")){
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			request.setAttribute("orderid", orderid);
			return mapping.findForward("carpics");
		}else if(action.equals("getalluser")){
			List<Map> tradsList = daService.getAll("select id,nickname from user_info_tb where comid=? and auth_flag in(?,?)",
					new Object[]{comid,1,2});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("nickname")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getfreereasons")){
			List<Map> tradsList = daService.getAll("select id,name from free_reasons_tb where comid=? ",
					new Object[]{comid});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"\"}";
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
	private String getUinName(Long uin) {
		Map list = daService.getPojo("select * from user_info_tb where id =?  ",new Object[]{uin});
		String uinName = "";
		if(list!=null&&list.get("nickname")!=null){
			uinName = list.get("nickname")+"";
		}
		return uinName;
	}
	private List query(HttpServletRequest request,long comid){
		ArrayList arrayList = new ArrayList();
		String orderfield = RequestUtil.processParams(request, "orderfield");
		String orderby = RequestUtil.processParams(request, "orderby");
		if(orderfield.equals("")){
			orderfield = "end_time";
		}
		if(orderby.equals("")){
			orderby = "desc";
		}
		String sql = "select * from order_tb where comid=?  ";
		String countSql = "select count(*) from order_tb where  comid=?  " ;
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
		String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
		SqlInfo base = new SqlInfo("1=1", new Object[]{comid});
		SqlInfo sqlInfo = RequestUtil.customSearch(request,"order_tb");
		Object[] values = null;
		List<Object> params =null;
		if(sqlInfo!=null){
			sqlInfo = SqlInfo.joinSqlInfo(base,sqlInfo, 2);
			countSql+=" and "+ sqlInfo.getSql();
			sql +=" and "+sqlInfo.getSql();
			values = sqlInfo.getValues();
			params = sqlInfo.getParams();
		}else {
			values = base.getValues();
			params= base.getParams();
		}
		
		sql += " order by " + orderfield + " " + orderby;
		//System.out.println(sqlInfo);
		Long count= daService.getLong(countSql, values);
		List list = null;//daService.getPage(sql, null, 1, 20);
		if(count>0){
			list = daService.getAll(sql, params, pageNum, pageSize);
		}
		arrayList.add(list);
		arrayList.add(pageNum);
		arrayList.add(count);
		arrayList.add(fieldsstr);
		return arrayList;
	}
}
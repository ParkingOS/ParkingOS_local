package com.zld.struts.request;

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
import com.zld.utils.Check;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SendMessage;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class RecommendAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(ParkCollectorLoginAction.class);
	
	

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		String from = RequestUtil.processParams(request, "from");
		Long type = RequestUtil.getLong(request, "type", -1L);
		Long uin = RequestUtil.getLong(request, "uin", -1L);
		if(comid==null&&!action.equals("recom")&&!action.equals("toregpage")&&!from.equals("client")){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select * from recom_tb  where create_time between ? and ? ";
			String countSql = "select count(*) from recom_tb  where create_time between ? and ? ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String week = RequestUtil.processParams(request, "week");
			List<Object> params = new ArrayList<Object>();
			String monday = StringUtils.getMondayOfThisWeek();
			Long btime =TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(monday+" 00:00:00");
			Long etime = System.currentTimeMillis()/1000;
			if(week.equals("last")){//上周
				params.add(btime-7*24*60*60);
				params.add(btime);
			}else {//本周
				params.add(btime);
				params.add(etime);
			}
			if(from.equals("client")){//客户端请求
				sql+=" and type=? ";
				countSql +=" and type=? ";
				params.add(type);
			}
			List list = null;//daService.getPage(sql, null, 1, 20);
			Long count = null;//daService.getCount(countSql,params);
//			if(count>0){
			list = daService.getAll(sql+" order by id desc",params, pageNum,pageSize);
			count = list!=null?list.size():0L;
//			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from recom_tb  where create_time between ? and ?  ";
			String countSql = "select count(*) from recom_tb  where create_time between ? and ?  " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			String week = RequestUtil.processParams(request, "week");
			List<Object> params = new ArrayList<Object>();
			String monday = StringUtils.getMondayOfThisWeek();
			Long btime =TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(monday+" 00:00:00");
			Long etime = System.currentTimeMillis()/1000;
			if(week.equals("last")){//上周
				params.add(btime-7*24*60*60);
				params.add(btime);
			}else if(week.equals("current")){//本周
				params.add(btime);
				params.add(etime);
			}else{
				sql = "select * from recom_tb";
				countSql = "select count(*) from recom_tb ";
			}
			if(from.equals("client")){//客户端请求
				sql = "select mobile,create_time from recom_tb  where create_time between ? and ?  ";
				countSql = "select count(*) from recom_tb where create_time between ? and ? ";
				params.clear();
				params.add(btime-7*24*60*60);
				params.add(etime);
				sql+=" and type=? and uin=? and state=?";
				countSql +=" and type=? and uin=? and state=? ";
				params.add(type);
				params.add(uin);
				params.add(1);
			}
//			SqlInfo sqlInfo = RequestUtil.customSearch(request,"recom_tb");
//			
//			Object[] values = null;
//			List<Object> params = null;
//			if(sqlInfo!=null){
//				countSql+=" where "+ sqlInfo.getSql();
//				sql +=" where "+sqlInfo.getSql();
//				values = sqlInfo.getValues();
//				params=sqlInfo.getParams();
//			}
			//System.out.println(sqlInfo);
			Long count= 0L;//daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(week.equals("")){
				count = daService.getCount(countSql, params);
			}
			list = daService.getAll(sql+" order by id desc", params, pageNum, pageSize);
			if(count!=null&&count==0){
				count=list!=null?list.size():0l;
			}
			if(from.equals("client")){
				setList(list, btime,request,response);
				return null;
			}else {
				String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
				AjaxUtil.ajaxOutput(response, json);
			}
			//http://127.0.0.1/zld/recommend.do?action=query&from=client&type=0&week=current&uin=1000005
			return null;
		}else if(action.equals("highquery")){
			String sql = "select * from recom_tb ";
			String sqlcount = "select count(1) from recom_tb ";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"recom_tb");
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				sqlcount+=" where "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params = sqlInfo.getParams();
			}
			Long count= daService.getCount(sqlcount, params);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			if(count>0){
				list = daService.getAll(sql+" order by id desc", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("recom")){//客户端推荐用户或停车场
			String userappUrl = "  http://www.tingchebao.com";
			//String userappUrl = "  http://d.tingchebao.com/download/tingchebao.apk";
			String parkappUrl = "  http://d.tingchebao.com/downfiles/tingchebao_biz.apk";
			Long recomType = RequestUtil.getLong(request, "recom_type",-1L);
			String mobile = RequestUtil.processParams(request, "mobile");
//			Long uin = RequestUtil.getLong(request, "uin", -1L);
			int result = 0;
			if(uin!=-1&&recomType!=-1&&Check.checkPhone(mobile, "m")){
				//查询是否有已注册的手机号
				Map userMap = daService.getPojo("select * from user_info_tb where mobile=? and auth_flag=?",
						new Object[]{mobile,4});
				if(userMap!=null&&!userMap.isEmpty()){
					AjaxUtil.ajaxOutput(response, "-1");
					return null;
				}
				String sql  = "insert into recom_tb (mobile,uin,type,state,create_time)" +
						" values (?,?,?,?,?)" ;
				Object[] values = null;
				if(recomType==0){//推荐车主，给车主发短信
					values = new Object[]{mobile,uin,recomType,0,System.currentTimeMillis()/1000};
				}else if(recomType==1){//推荐车场，给收费员发短信
					values = new Object[]{mobile,uin,recomType,0,System.currentTimeMillis()/1000};
				}
				try {
					result = daService.update(sql, values);
				} catch (Exception e) {
					if(e.getMessage().indexOf("recom_tb_mobile_key")!=-1){
						result =-1;
					}
				}
				if(result==1){//向推荐的手机号发短信。
					if(recomType==0){
						//String pname=(String)daService.getObject("select nickname from user_info_Tb where id=?", new Object[]{uin}, String.class);
						//String message = "用了停车宝，你好，我也好！ --停车员"+pname+"，推荐你下载找车位神器停车宝："+userappUrl+"   【停车宝】";
						String message = "停车宝下载："+userappUrl+"   【停车宝】";
						SendMessage.sendMultiMessage(mobile, message);
					}else {
						String message = "下载停车宝车场版：  "+parkappUrl+"   【停车宝】";
						SendMessage.sendMultiMessage(mobile, message);
					}
				}
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}
	
	private void setList (List<Map<String, Object>> list,Long week,
			HttpServletRequest request,HttpServletResponse response){
		Integer tweek = 0;
		Integer lweek = 0;
		if(list!=null&&list.size()>0){
			for(Map<String, Object> map : list){
				Long ctime = (Long)map.get("create_time");
				if(ctime<week){
					lweek+=5;
				}else {
					tweek+=5;
				}
				map.put("create_time", TimeTools.getTime_yyyyMMdd_HHmm(ctime*1000));
			}
		}
		try {
			AjaxUtil.ajaxOutput(response, "{\"tweek\":\""+tweek+"\",\"lweek\":\""+lweek+"\",\"total\":\""+(lweek+tweek)+"\"," +
					"\"items\":"+StringUtils.createJson(list)+"}");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/*//注册收费员信息
	@SuppressWarnings({ "rawtypes" })
	private int createCollectorInfo(HttpServletRequest request,Long uin, String mobile){
		Long time = System.currentTimeMillis()/1000;
		String strid = uin+"";
		String password = mobile.substring(5);
		//用户表
		String sql= "insert into user_info_tb (id,nickname,password,strid," +
				"reg_time,mobile,auth_flag,comid,state ) " +
				"values (?,?,?,?,?,?,?,?,?)";
		Object[] values= new Object[]{uin,"收费员",password,strid,time,mobile,2,1,2};
		
		int r = daService.update(sql,values);
		return r;
	}*/
	
	private String  getToMobile(String mobile){
		//移动 联通  106901336275
		//电信是1069004270441
		//中国电信：133,153,177,180,181,189
		if(mobile.startsWith("133")||mobile.startsWith("153")
				||mobile.startsWith("177")||mobile.startsWith("180")
				||mobile.startsWith("181")||mobile.startsWith("189")
				||mobile.startsWith("170"))
			return "1069004270441";
		return "106901336275";
	}
}

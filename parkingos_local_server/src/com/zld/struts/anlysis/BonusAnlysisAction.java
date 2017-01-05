package com.zld.struts.anlysis;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

/**
 * 红包使用使用统计
 * @author Administrator
 *
 */
public class BonusAnlysisAction extends Action {

	@Autowired
	private DataBaseService service;
	@Autowired 
	private PgOnlyReadService readService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		if(action.equals("")){
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			request.setAttribute("btime",  df2.format(System.currentTimeMillis()-24*60*60*1000));
			request.setAttribute("etime",  df2.format(System.currentTimeMillis()-24*60*60*1000));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select * from reg_anlysis_tb where ctime between ? and ? ";
			List list = null;//daService.getPage(sql, null, 1, 20);
			Integer media=RequestUtil.getInteger(request, "media", 0);
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			String fieldsstr = RequestUtil.getString(request, "fieldsstr");
			List<Object> params = new ArrayList<Object>();
			Long b =TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(btime+" 00:00:00");
			Long e =TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 00:00:00");
			params.add(b);
			params.add(e);
			if(media!=0){
				sql +=" and atype= ?";
				params.add(media);
			}
			list = readService.getAllMap(sql +" order by atype desc ",params);
			String json = getJson(list,fieldsstr);
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Integer amount=RequestUtil.getInteger(request, "amount", 0);
			Integer pv_number=RequestUtil.getInteger(request, "pv_number", 0);
			Integer hit_number=RequestUtil.getInteger(request, "hit_number", 0);
			Integer down_num=RequestUtil.getInteger(request, "down_num", 0);
			Long id  = RequestUtil.getLong(request, "id", -1L);
			int ret = 0;
			if(id!=-1&&(amount!=0||pv_number!=0||hit_number!=0||down_num!=0))
				ret = service.update("update reg_anlysis_tb set amount=?,pv_number=?" +
						",hit_number=?,down_num=? where id =?",
						new Object[]{amount,pv_number,hit_number,down_num,id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("detail")){
			//action=detail&c=961&type=1000&d=2015-07-03&r=0.9697699934718748
			Integer atype = RequestUtil.getInteger(request, "type", -1);
			Integer count = RequestUtil.getInteger(request, "c", -1);
//			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
//			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String date = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "d"));
			System.err.println(atype+":"+TimeTools.getLongMilliSecondFrom_HHMMDD(date)+":"+count);
			Long qdate = TimeTools.getLongMilliSecondFrom_HHMMDD(date)/1000;
			List<Map<String, Object>> list = null;
					/*readService.getAll(
					"select uc.id,uc.amount,uc.target,uc.remark,uc.create_time," +
					"ci.car_number,ui.nickname" +
					" from user_account_tb uc,car_info_tb ci,user_info_Tb ui" +
					   "  where uc.uin in(select id from user_info_Tb " +
						"where reg_time  between ?  and ? and auth_flag=? and media=? ) " +
						"and uc.uin=ci.uin and uc.uid=ui.id and uc.type=? and uc.create_time between ? and ? order by uc.create_time",
					new Object[]{qdate,qdate+24*60*60,4,atype,1,qdate,qdate+24*60*60});*/
			String sql = "select u.id,u.uin,u.amount,u.target,u.remark,u.create_time,u.uid,c.car_number" +
					" from user_account_tb u left join car_info_tb c on u.uin = c.uin where u.uin in" +
					"(select id from user_info_tb where reg_time between ? and ? and media=? )" +
					" and u.type=? and u.create_time between ? and ? and u.remark like ? order by u.create_time ";
			list = readService.getAll(sql, new Object[]{qdate,qdate+24*60*60,atype,1,qdate,qdate+24*60*60,"停车费%"});
			
			//{"page":1,"total":2,"rows": [{"id":"1","cell":["通话中","业务咨询"]},{"id":"2","cell":["接入中","售后服务"]}]};
			String result = "{\"page\":1,\"total\":0,\"rows\": []}";
			if(list!=null&&list.size()>0){
				result = "{\"page\":1,\"total\":"+list.size()+",\"rows\": [datas]}";
				String datas = "";
				Set<Long> set = new HashSet<Long>();
				int i=0;
				for(Map<String, Object> map : list){
					Long ctime = (Long)map.get("create_time");
					String cdate =TimeTools.getTime_yyyyMMdd_HHmmss(ctime*1000);
					String carNumber = (String)map.get("car_number");
					Long uin = (Long)map.get("uin");
					if(carNumber==null||"null".equals(carNumber))
						carNumber=""+uin;
					Long uid = (Long)map.get("uid");
					String uname = "";
					if(uid!=null&&uid!=-1)
						uname=""+uid;
					if(set.add(uin)){
						if(datas.length()>1)
							datas+=",";
						datas +="{\"id\":"+map.get("id")+",\"cell\":[\""+carNumber+"\",\""+cdate+"\",\""+map.get("remark")+"\",\""+map.get("amount")+"\",\""+map.get("target")+"\",\""+uname+"\"]}";
						i++;
					}
				}
				System.err.println("search size:"+list.size()+",realy size:"+i);
				result = result.replace("datas", datas);
			}
			request.setAttribute("details", result);
			return mapping.findForward("detail");
		}
		return null;
	}
	
	
	private String getJson(List list,String fieldsstr){
		String json = "{\"page\":1,\"total\":0,\"rows\":[]}";
		if(list != null && !list.isEmpty()) {
			StringBuffer sb = new StringBuffer("");
			sb.append("{\"page\":"+ 1 +",\"total\":"+list.size()+",\"rows\": [");
			if(!"".equals(fieldsstr)) {
				String[] fieldsstrArray = fieldsstr.split("\\_\\_");
				for (int i = 0; i < list.size(); i++) {
					if(i != 0)
						sb.append(",");
					sb.append(Map2Json((Map)list.get(i), fieldsstrArray,"id") + "]}");
				}
			}
			sb.append("]}");
			json = sb.toString();
		}
		return json;
		//atype__ctime__amount__pv_number__hit_number__bonus_num__down_num__reg_num__order_num__rp__hp__dp__tp
	}
	
	public static String Map2Json(Map map,String[] fieldsstrArray,String id) {
		StringBuffer json = new StringBuffer("");
		if(map != null) {
			json.append("{\"id\":\""+map.get(id)+"\",\"cell\":[");
			for (int j = 0; j < fieldsstrArray.length; j++) {
				String fieldName = fieldsstrArray[j];
				String _filedStr = "";
				Object _filedObject = map.get(fieldName);
				_filedStr = _filedObject==null?"":_filedObject.toString();
				if(fieldName.equals("ctime")){
					if(_filedStr.length()>5){//
						_filedStr = TimeTools.getTime_yyyyMMdd_HHmmss(Long.valueOf(_filedStr)*1000);
						_filedStr = _filedStr.substring(0,10);
					}
				}else if(fieldName.equals("rp")){//注册成本
					Object pvalue = map.get("amount");
					Object regNum = map.get("reg_num");
					Double pDouble = StringUtils.formatDouble(pvalue);
					Double regDouble = StringUtils.formatDouble(regNum);
					if(regDouble>0&&pDouble>0){
						_filedStr= StringUtils.formatDouble(StringUtils.formatDouble(pvalue)/StringUtils.formatDouble(regNum))+"";
					}
				}else if(fieldName.equals("tp")){//车牌转化率
					Object pvalue = map.get("reg_num");
					Object regNum = map.get("hit_number");
					Double pDouble = StringUtils.formatDouble(pvalue);
					Double regDouble = StringUtils.formatDouble(regNum);
					if(regDouble>0&&pDouble>0){
						_filedStr= StringUtils.formatDouble(StringUtils.formatDouble(pvalue)/StringUtils.formatDouble(regNum))*100+"%";
					}
				}else if(fieldName.equals("hp")){//红包率
					Object pvalue = map.get("bonus_num");
					Object regNum = map.get("hit_number");
					Double pDouble = StringUtils.formatDouble(pvalue);
					Double regDouble = StringUtils.formatDouble(regNum);
					if(regDouble>0&&pDouble>0){
						_filedStr= StringUtils.formatDouble(StringUtils.formatDouble(pvalue)/StringUtils.formatDouble(regNum))*100+"%";
					}
				}else if(fieldName.equals("dp")){//下载率
					Object pvalue = map.get("down_num");
					Object regNum = map.get("bonus_num");
					Double pDouble = StringUtils.formatDouble(pvalue);
					Double regDouble = StringUtils.formatDouble(regNum);
					if(regDouble>0&&pDouble>0){
						_filedStr= StringUtils.formatDouble(StringUtils.formatDouble(pvalue)/StringUtils.formatDouble(regNum))*100+"%";
					}
				}
				if(j == fieldsstrArray.length -1)
					json.append("\""+ _filedStr +"\"");
				else
					json.append("\""+ _filedStr +"\",");
			}
		}
		return json.toString();
	}
}
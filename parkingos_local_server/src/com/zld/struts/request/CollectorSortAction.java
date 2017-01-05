package com.zld.struts.request;

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
import com.zld.service.LogService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
/**
 * 收费员分享车位排行
 * @author Administrator
 *
 */
public class CollectorSortAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;
	@Autowired
	private PgOnlyReadService onlyReadService;
	
	private Logger logger = Logger.getLogger(CollectorSortAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		String type=RequestUtil.processParams(request, "type");
		if(comid==null&&type.equals("")){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("query")){
			//客户端接口：http://127.0.0.1/zld/collectorsort.do?action=query&type=client&week=
			String week = RequestUtil.processParams(request, "week");
			String month = RequestUtil.processParams(request, "month");
			String sql = "select sum(lala_scroe+nfc_score+praise_scroe+pai_score+online_scroe+recom_scroe) share_time,uin from collector_scroe_tb where create_time between ? and ? group by uin order by share_time desc limit ? ";//=? order by share_time desc ";
			//String sql = "select count(id) share_time,uin from collector_sort where create_time between ? and ? group by uin order by share_time desc limit ?";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
//			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
//			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			String monday = StringUtils.getMondayOfThisWeek();
			String firstDayOfMonth = StringUtils.getFistdayOfMonth();
			String LastfirstDayOfMonth = StringUtils.getLastFistdayOfMonth();
			Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
			Long etime = System.currentTimeMillis()/1000;
			if(month.equals("tomonth")){//本月
				btime = TimeTools.getLongMilliSecondFrom_HHMMDD(firstDayOfMonth)/1000;
				etime = System.currentTimeMillis()/1000;
			}else if(month.equals("last")){//上月
				btime = TimeTools.getLongMilliSecondFrom_HHMMDD(LastfirstDayOfMonth)/1000;
				etime = TimeTools.getLongMilliSecondFrom_HHMMDD(firstDayOfMonth)/1000-1;
			}else if(week.equals("last")){//上周积分
				etime = TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
				btime = etime-7*24*60*60;
				etime = etime-1;
			}else {//本周积分
				btime =TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
			}
			params.add(btime);
			params.add(etime);
			params.add(600);
			List list = null;//daService.getPage(sql, null, 1, 20);
			list = onlyReadService.getAll(sql, params, 0, 0);
			if(type.equals("client")){
				List<Map<String, Object >> tempList = setName(list,0);
				setSort(tempList);
				AjaxUtil.ajaxOutput(response,StringUtils.createJson(tempList));
			}else {
				List<Map<String, Object >> temp=setName(list,1);
				setSort(temp);
				String json = JsonUtil.Map2Json(temp,1,temp.size(), fieldsstr,"uin");
				AjaxUtil.ajaxOutput(response, json);
			}
			return null;
		}else if(action.equals("detail")){
			String ptype = RequestUtil.processParams(request, "ptype");
			String monday = StringUtils.getMondayOfThisWeek();
			String firstDayOfMonth = StringUtils.getFistdayOfMonth();
			String LastfirstDayOfMonth = StringUtils.getLastFistdayOfMonth();
			Long btime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(monday+" 00:00:00");
			Long etime = System.currentTimeMillis()/1000;
			if(ptype.equals("tomonth")){//本月
				btime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(firstDayOfMonth+" 00:00:00");
				etime = System.currentTimeMillis()/1000;
			}else if(ptype.equals("lastmonth")){//上月
				btime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(LastfirstDayOfMonth+" 00:00:00");
				etime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(firstDayOfMonth+" 00:00:00")-1;
			}else if(ptype.equals("lastweek")){//上周
				etime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(monday+" 00:00:00");
				btime = etime-7*24*60*60;
				etime = etime-1;
			}else {//本周
				btime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(monday+" 00:00:00");
				etime = System.currentTimeMillis()/1000;
			}
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			String sql = "select * from collector_scroe_tb where create_time between ? and ? and uin =? order by create_time ";
			List<Object> params = new ArrayList<Object>();
			params.add(btime);
			params.add(etime);
			params.add(pid);
			List list = null;//daService.getPage(sql, null, 1, 20);
			list = onlyReadService.getAll(sql, params, 0, 0);
			String user= AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "uid"));
			String parker= AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "parker"));
			String t1 = TimeTools.getTimeStr_yyyy_MM_dd(btime*1000);
			String t2 = TimeTools.getTimeStr_yyyy_MM_dd(etime*1000);
			request.setAttribute("details",setResult(list,btime,etime));
			request.setAttribute("tips", " "+t1+"至"+t2+"，停车员："+parker+"，市场专员 ："+user);
			return mapping.findForward("detail");
		}else if(action.equals("sendmoney")){//发放奖金
			Long uin = RequestUtil.getLong(request, "uid", -1L);
			Double money = RequestUtil.getDouble(request, "money", 0.0d);
			Long toweekbegin = TimeTools.getWeekStartSeconds();
			Long count = onlyReadService.getLong("select count(*) from parkuser_account_tb" +
					" where uin=? and  create_time>? and target=? and remark=? ",
					new Object[]{uin,toweekbegin,3,"停车宝排行榜周奖"}); 
			String result = "";
			if(count>0){
				result="已发放过，请不要重复发放！";
			}else {
				if(uin!=-1&&money!=0){
					Map userMap = onlyReadService.getMap("select comid from user_info_tb where id=? ", new Object[]{uin});
					Long comId = null;
					count = onlyReadService.getLong("select count(*) from park_account_tb" +
							" where uid=? and  create_time>? and type=? and remark=? ",
							new Object[]{uin,toweekbegin,0,"停车宝排行榜周奖"}); 
					if(count>0){
						result="已发放过，请不要重复发放！";
						AjaxUtil.ajaxOutput(response, ""+result);
						return null;
					}
					Integer payto=null;
					if(userMap!=null&&userMap.get("comid")!=null){
						comId = (Long)userMap.get("comid");
						//查询收费设定 mtype:0:停车费,1:预订费,2:停车宝返现
						Map msetMap = onlyReadService.getMap("select giveto from money_set_tb where comid=? and mtype=? ",
								new Object[]{comId,3});
						if(msetMap!=null)
							payto = (Integer)msetMap.get("giveto");
					}
					
					List<Map<String,Object>> sqlMap =new ArrayList<Map<String,Object>>();
					Map<String,Object> userSql = new HashMap<String, Object>();
					Map<String,Object> accountSql = new HashMap<String, Object>();
					
					
					if(payto!=null&&payto==0){//周奖给公司
						//更新停车场
						userSql.put("sql", "update com_info_tb set money=money+?,total_money=total_money+? where id=? ");
						userSql.put("values", new Object[]{money,money,comId});
						accountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) values(?,?,?,?,?,?,?)");
						accountSql.put("values", new Object[]{comId,money,0,System.currentTimeMillis()/1000,"停车宝排行榜周奖",uin,6});
						sqlMap.add(userSql);
						sqlMap.add(accountSql);
					}else {//周奖收费员
						//更新用户表及用户账户表
						userSql.put("sql", "update user_info_tb set balance=balance+? where id=? ");
						userSql.put("values", new Object[]{money,uin});
						accountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
						accountSql.put("values", new Object[]{uin,money,0,System.currentTimeMillis()/1000,"停车宝排行榜周奖",3});
						sqlMap.add(userSql);
						sqlMap.add(accountSql);
					}
					boolean ret  =false;
					if(sqlMap.size()>0){
						ret= daService.bathUpdate(sqlMap);
					}
					if(ret){
						result="1";
						if(payto==null||payto==1)
							logService.insertParkUserMesg(2, uin, "您上周停车宝周奖"+money+"元已到账，请查收。", "周奖到账通知");
					}
				}
			}
			AjaxUtil.ajaxOutput(response, ""+result);
		}
		return null;
	}
	
	private void setSort(List list){
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				map.put("sort", i+1);
			}
		}
	}
	
	private String setResult (List<Map<String, Object>> lists,Long start,Long end){
		String result = "{\"page\":1,\"total\":"+lists.size()+",\"rows\":[]}";
		String data = "[";
		/*int day =24*60*60;
		Map<Long,Integer> dMap = new HashMap<Long, Integer>();
		for(Map<String, Object> map : lists){
			Long createTime = (Long)map.get("create_time");
			Long du = createTime-start;
			int t = du.intValue()/day;
			//System.out.println(t);
			Long key = start+t*day;
			if(dMap.get(key)==null){
				dMap.put(key, 1);
			}else {
				int times = dMap.get(key);
				dMap.put(key, times+1);
			}
		}
		List<Map.Entry<Long, Integer>> mapList = new ArrayList<Map.Entry<Long,Integer>>(dMap.entrySet());
		Collections.sort(mapList, new Comparator<Map.Entry<Long,Integer>>(){ 
			   public int compare(Map.Entry<Long,Integer> mapping1,Map.Entry<Long,Integer> mapping2){
			     return mapping1.getKey().compareTo(mapping2.getKey()); 
			   } 
			  }); 
		
		for(Map.Entry<Long, Integer> mlist : mapList){
			String skey = TimeTools.getTimeStr_yyyy_MM_dd(mlist.getKey()*1000);
			if(data.equals("[")){
				data +="{\"id\":"+mlist.getValue()+",\"cell\":[\""+skey+"\",\""+dMap.get(mlist.getKey())+"\"]}";
			}else {
				data +=",{\"id\":"+mlist.getValue()+",\"cell\":[\""+skey+"\",\""+dMap.get(mlist.getKey())+"\"]}";
			}
		}*/
		for(Map<String, Object> map : lists){
			Long ctime = (Long)map.get("create_time");
			String time = TimeTools.getTimeStr_yyyy_MM_dd(ctime*1000);
			if(data.equals("[")){
				data +="{\"id\":"+map.get("id")+",\"cell\":[\""+time+"\",\""+map.get("lala_scroe")+"\",\""+map.get("nfc_score")+"\",\""+map.get("pai_score")+"\",\""+map.get("praise_scroe")+"\",\""+map.get("online_scroe")+"\",\""+map.get("recom_scroe")+"\"]}";
			}else {
				data +=",{\"id\":"+map.get("id")+",\"cell\":[\""+time+"\",\""+map.get("lala_scroe")+"\",\""+map.get("nfc_score")+"\",\""+map.get("pai_score")+"\",\""+map.get("praise_scroe")+"\",\""+map.get("online_scroe")+"\",\""+map.get("recom_scroe")+"\"]}";
			}
		}
		data +="]";
		result = result.replace("[]", data);
		return result;
	}
	
	private List<Map<String, Object >> setName(List list,int type){
		List<Map<String, Object >> templiList = new ArrayList<Map<String, Object >>();
		List<Object> uins = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				if(map.get("uin")!=null){
					uins.add(map.get("uin"));
				}
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
			uins.add(0);
			List<Map<String, Object>> resultList = onlyReadService.getAllMap("select u.id,u.mobile ,u.nickname as uname,c.company_name cname ," +
					"c.uid from user_info_tb u,com_info_tb c" +
					" where u.comid=c.id and  u.id in ("+preParams+")  and c.state=?", uins);
			
			Map<String ,Object> markerMap = new HashMap<String ,Object>();
			if(resultList!=null&&!resultList.isEmpty()){
				
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("id");
						if(map1.get("uin").equals(uin)){
							templiList.add(map1);
							String name  = (String)map.get("uname");
							if(name == null || name.equals("")){
								name = "无";
							}else if(name.length() > 1&&type==0){
								//姓名显示最后一个字，其他字用*代替
								String hidename = "";
								for(int j=0;j<name.length()-1;j++){
									hidename += "*";
								}
								hidename += name.substring(name.length() - 1, name.length());
								name = hidename;
							}
							map1.put("nickname", name);
							String cname = (String)map.get("cname");
							if(cname.length() > 1&&type==0){
								String hidecname = "";
								for(int j=0;j<cname.length()-2;j++){
									hidecname += "*";
								}
								hidecname =cname.substring(0, 1) +hidecname + cname.substring(cname.length()-1, cname.length());
								cname = hidecname;
							}
							map1.put("cname", cname);
							Long uid = (Long)map.get("uid");
							if(type==1){
								map1.put("mobile", map.get("mobile"));
								map1.put("uid",uid);
							}
							if(uid!=null&&uid>1000)
								markerMap.put(map.get("uid")+"", "1");
							break;
						}
					}
				}
			}
			if(type==1){
				preParams="";
				uins.clear();
				for(String key : markerMap.keySet()){
					if(preParams.equals(""))
						preParams ="?";
					else
						preParams += ",?";
					uins.add(Long.valueOf(key));
				}
				
				List<Map<String, Object>> markerList = null;
				if(!preParams.equals(""))
					markerList=onlyReadService.getAllMap("select id,nickname from user_info_tb where id in("+preParams+")",uins);
				
				if(markerList!=null&&!markerList.isEmpty()){
					
					for(int i=0;i<templiList.size();i++){
						Map map1 = (Map)templiList.get(i);
						for(Map<String,Object> map: markerList){
							Long uin = (Long)map.get("id");
							if(map1.get("uid").equals(uin)){
								if(type==1){
									map1.put("uid",map.get("nickname")==null?"无":map.get("nickname"));
								}
								break;
							}
						}
					}
				}
			}
		}
		//System.err.println(list.size()+":"+templiList.size());
		return templiList;
	}

}
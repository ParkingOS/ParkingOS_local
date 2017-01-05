package com.zld.struts.admin;

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
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.GetLocalCode;
import com.zld.utils.ParkingMap;
import com.zld.utils.RequestUtil;
import com.zld.utils.TimeTools;


public class GetDatas extends Action{
	
	@Autowired
	DataBaseService dataBaseService;

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		String userId = (String)request.getSession().getAttribute("userid");
		if(userId==null&&action.indexOf("lott")==-1){
			response.sendRedirect("login.do");
			return null;
		}
		 if(action.equals("markets")){
			List<Map> tradsList = dataBaseService.getAll("select id,nickname from user_info_tb where state =? and comid=? and (auth_flag=? or auth_flag=?) ",
					new Object[]{0,0,5,11});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("nickname")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getpark")){
			Long id =RequestUtil.getLong(request, "id", -1L);
			List<Map> tradsList = dataBaseService.getAll("select id,company_name from com_info_tb  where uid =? ",
					new Object[]{id});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("company_name")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getuser")){
			Long id =RequestUtil.getLong(request, "id", -1L);
			
			List<Map> tradsList = dataBaseService.getAll("select id,nickname from user_info_tb where comid=? ",
					new Object[]{id});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("nickname")+"\"}";
				}
			}
			result+="]";
			result = result.replace("null", "");
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getvalue")){
			String type = RequestUtil.getString(request, "type");
			Long id =RequestUtil.getLong(request, "id", -1L); 
			String name =id+"";
			if(type.equals("parkname")){
				name = ParkingMap.getParkName(id);
				if(name==null){
					name = (String)dataBaseService.getObject("select company_name from com_info_tb where id =?", new Object[]{id}, String.class);
					ParkingMap.putParkName(id, name);
				}
			}else if(type.equals("parkername")){
				name = ParkingMap.getUserName(id);
				if(name==null){
					name = (String)dataBaseService.getObject("select nickname from user_info_tb where id =?", new Object[]{id}, String.class);
					ParkingMap.putUserName(id, name);
				}
			}
			if(name!=null)
				AjaxUtil.ajaxOutput(response, name);
			else {
				AjaxUtil.ajaxOutput(response, "");
			}
		}else if(action.equals("getpname")){
			Long comid = RequestUtil.getLong(request, "comid", -1L);
			List<Map>  pList = null;
			String result = "[{\"value_no\":\"\",\"value_name\":\"请选择\"}";
			if(comid!=-1){
				Long ntime = System.currentTimeMillis()/1000;
				pList = dataBaseService.getAll("select id,p_name from product_package_tb where comid=?" +
						" and limitday >? and state=? ", new Object[]{comid,ntime+30*24*60*60,0});
				if(pList!=null&&pList.size()>0){
					for(Map map : pList){
						result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("p_name")+"\"}";
					}
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getpass")){//获取通道列表
			Long worksite_id = RequestUtil.getLong(request, "id", -1L);
			String sql = "select * from com_pass_tb where worksite_id=?";
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			list = dataBaseService.getAll(sql, new Object[]{worksite_id});
			String result = "[";
			if(!list.isEmpty()){
				for(Map map : list){
					result+="{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("passname")+"\"},";
				}
				result = result.substring(0, result.length()-1);
			}
			result += "]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getcompass")){//获取车场所有通道列表
			Long comid = RequestUtil.getLong(request, "comid", -1L);
			String sql = "select * from com_pass_tb where comid=?";
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			list = dataBaseService.getAll(sql, new Object[]{comid});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(!list.isEmpty()){
				for(Map map : list){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("passname")+"\"}";
				}
			}
			result += "]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("addlott")){//保存中奖
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Integer lott = RequestUtil.getInteger(request, "lott", -1);
			Long uin = dataBaseService.getLong("select uin from order_tb where id=?",new Object[]{orderId});
			//是否是
			Long count  = dataBaseService.getLong("select count(Id) from lottery_tb where orderid=? and lottery_result>?",
					new Object[]{orderId,-1});
			int ret =0;
			if(count>0){//未设置过奖品
				ret=-1;
			}else {
				List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
				//写入用户明细
				Map<String, Object> userAccountSqlMap = new HashMap<String, Object>();
				//更新抽奖结果
				Map<String, Object> lotterySqlMap = new HashMap<String, Object>();
				//更新车主账户
				Map<String, Object> userSqlMap = new HashMap<String, Object>();
				userSqlMap.put("sql", "update user_info_tb set balance = balance+? where id=? ");
				userSqlMap.put("values", new Object[]{lott+1,uin});
				if(lott<3)
					bathSql.add(userSqlMap);
				
				userAccountSqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
				userAccountSqlMap.put("values",  new Object[]{uin,lott+1,0,System.currentTimeMillis()/1000,"充值",8});
				if(lott<3)
					bathSql.add(userAccountSqlMap);
				
				lotterySqlMap.put("sql", "update lottery_tb set lottery_result = ? ,create_time=? where orderid=?");
				lotterySqlMap.put("values", new Object[]{lott,System.currentTimeMillis()/1000,orderId});
				bathSql.add(lotterySqlMap);
				//批量更新
				boolean result = dataBaseService.bathUpdate(bathSql);
				if(result)
					ret=1;
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("lottery")){//查询是否可以抽奖
			Long orderId = RequestUtil.getLong(request, "id", -1L);
			Map orderMap = dataBaseService.getMap("select uin from order_tb where id  = ? ", new Object[]{orderId});
			Long uin = -1L;
			if(orderMap!=null&&orderMap.get("uin")!=null)
				uin= (Long)orderMap.get("uin");
			Long count = dataBaseService.getLong("select count(id) from lottery_tb where uin =? and create_time>? and lottery_result>? ", 
					new Object[]{uin,TimeTools.getToDayBeginTime(),0});
			if(count>0){//今日已抽奖
				count=0L;
				//System.out.println(">>>>>uin:"+uin+",今天已抽过奖!");
			}else {
				count  = dataBaseService.getLong("select count(Id) from lottery_tb where orderid=? and lottery_result<?",
						new Object[]{orderId,0});
				/*if(count<1){
					System.out.println(">>>>>orderid:"+orderId+",今天已抽过奖!");
				}else {
					System.out.println(">>>>>orderid:"+orderId+",今天可以过奖!");
				}*/
			}
			System.out.println(">>>>orderid:"+orderId);
			AjaxUtil.ajaxOutput(response, count+"");
			//http://192.168.199.240/zld/getdata.do?action=lottery&id=386
		}else if(action.equals("getbonustypes")){
			List<Map> tradsList = dataBaseService.getAll("select id,name from bonus_type_tb where state =? order by id ",
					new Object[]{1});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("name")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		} else if (action.equals("getauditors")) {
			List<Map<String, Object>> list = dataBaseService
					.getAll("select id,nickname from user_info_tb where (auth_flag=? or auth_flag=?) and state=? ",
							new Object[] {0, 7, 0 });
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(list != null && !list.isEmpty()){
				for(Map<String, Object> map : list){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("nickname")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getcompass")){
			String id = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "id"));
			String sql = "select c.id,c.passname,w.worksite_name from com_pass_tb c left join com_worksite_tb w on c.worksite_id=w.id ";
			List<Object> params  = new ArrayList<Object>();
			if(!id.equals("")){
				params.add(Long.valueOf(id));
				sql +=" where c.comid=? ";
			}
			List<Map> tradsList = dataBaseService.getAllMap(sql,params);
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("passname")+"("+map.get("worksite_name")+")\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getIbeaconPark")){
			List<Map> tradsList = dataBaseService.getAll("select id,company_name from com_info_tb where state=? and etc =?",new Object[]{0,1});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("company_name")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getWorksitePark")){
			List<Map> tradsList = dataBaseService.getAll("select id,company_name from com_info_tb where state=? and id in(select comid from com_worksite_tb)",new Object[]{0});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("company_name")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getworksite")){
			Long comid = RequestUtil.getLong(request, "id", -1L);
			List<Map> tradsList =null;
			if(comid>0)
				tradsList = dataBaseService.getAll("select id,worksite_name from com_worksite_tb where comid =?",new Object[]{comid});
			else
				tradsList = dataBaseService.getAll("select id,worksite_name from com_worksite_tb ", null);
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+=",{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("worksite_name")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getcity")){
			Map<Integer , String> localDataMap = GetLocalCode.localDataMap;
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"全部\"}";
			if(localDataMap != null){
				String city = CustomDefind.getValue("CITY");
				if(city != null){
					String cities[] = city.split(",");
					for(int i=0; i<cities.length; i++){
						result+=",{\"value_no\":\""+cities[i]+"\",\"value_name\":\""+localDataMap.get(Integer.valueOf(cities[i]))+"\"}";
					}
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}
		return null;
	}

}

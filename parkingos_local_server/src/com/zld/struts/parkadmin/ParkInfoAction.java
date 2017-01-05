package com.zld.struts.parkadmin;

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
import com.zld.utils.RequestUtil;
import com.zld.utils.ZLDType;
/**
 * 停车场修改
 * @author Administrator
 *
 */
public class ParkInfoAction extends Action {

	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
//		Long id = RequestUtil.getLong(request, "id", -1L);
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(action.equals("")){
			Map<String, Object> comMap = daService.getPojo("select * from com_info_tb  where id=?",new Object[]{comid});
			StringBuffer comBuffer = new StringBuffer("[");
			for (String  key : comMap.keySet()) {
				comBuffer.append("{\"name\":\""+key+"\",\"value\":\""+comMap.get(key)+"\"},");
			}
			String result = comBuffer.toString();
			result = result.substring(0,result.length()-1)+"]";
			request.setAttribute("cominfo", result);
			return mapping.findForward("success");
		}else if(action.equals("edit")){
			String company =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
			String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
			String phone =RequestUtil.processParams(request, "phone");
			String mobile =RequestUtil.processParams(request, "mobile");
			String property =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "property"));
			String id =RequestUtil.processParams(request, "id");
			Integer type = RequestUtil.getInteger(request, "type", 0);
			Integer parking_type = RequestUtil.getInteger(request, "parking_type", 0);
			Integer parking_total = RequestUtil.getInteger(request, "parking_total", 0);
			Integer share_number = RequestUtil.getInteger(request, "share_number", 0);
			Integer nfc = RequestUtil.getInteger(request, "nfc", 0);
			Integer etc = RequestUtil.getInteger(request, "etc", 0);
			Integer book = RequestUtil.getInteger(request, "book", 0);
			Integer navi = RequestUtil.getInteger(request, "navi", 0);
			Integer monthlypay = RequestUtil.getInteger(request, "monthlypay", 0);
			Integer epay = RequestUtil.getInteger(request, "epay", 0);
			Integer isnight = RequestUtil.getInteger(request, "isnight", 0);//夜晚停车，0:支持，1不支持
			String firstprovince =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "firstprovince"));
			Long invalid_order = RequestUtil.getLong(request, "invalid_order", 0L);
			Integer car_type = RequestUtil.getInteger(request, "car_type", 0);//是否区分大小车，0:不区分，1：区分
			Integer passfree = RequestUtil.getInteger(request, "passfree", 0);
			Integer ishidehdbutton = RequestUtil.getInteger(request, "ishidehdbutton", 1);
			String sql = "update com_info_tb set company_name=?,address=?,phone=?,mobile=?,property=?," +
					"parking_total=?,share_number=?,parking_type=?,type=?,update_time=?,nfc=?,etc=?,book=?,navi=?,monthlypay=?" +
					",isnight=?,firstprovince=?,epay=?,invalid_order=?,car_type=?,passfree=?,ishidehdbutton=? where id=?";
			Object [] values = new Object[]{company,address,phone,mobile,property,parking_total,share_number,parking_type,type,
					System.currentTimeMillis()/1000,nfc,etc,book,navi,monthlypay,isnight,firstprovince,epay,invalid_order,car_type,passfree,ishidehdbutton,Long.valueOf(id)};
			int result = daService.update(sql, values);
//			if(result==1){
//				Map comMap = daService.getMap("select * from com_info_tb where id=?", new Object[]{Long.valueOf(id)});
//				ParkingMap.updateParkingMap(comMap);
//			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("editcontactor")){
			String mobile =RequestUtil.processParams(request, "mobile");
			String strid =RequestUtil.processParams(request, "strid");
			String pass =RequestUtil.processParams(request, "pass");
			if(pass.equals(""))
				pass  = strid;
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String sql = "update user_info_tb set strid=?,password=?,mobile=?,nickname=? where comid=? and auth_flag=?";
			int result = daService.update(sql, new Object[]{strid,pass,mobile,nickname,comid,ZLDType.ZLD_PARKADMIN_ROLE});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("withdraw")){
			Double money = RequestUtil.getDouble(request, "money", 0d);
			//检查帐户是否已绑定
			List<Map> accList = daService.getAll("select id,type from com_account_tb where comid =? and type in(?,?) and state =? order by id desc",
					new Object[]{comid,0,2,0});
			Long accId = null;
			Integer type =0;
			if(accList!=null&&!accList.isEmpty()){
				accId = null;
				for(Map m: accList){
					type = (Integer)m.get("type");
					if(type!=null&&type==2){
						accId =  (Long)m.get("id");	
						break;
					}
				}
				if(accId==null)
					accId=(Long)accList.get(0).get("id");
			}
			if(accId!=null&&accId>0){
				boolean result =false;
				if(money>0){
					Map comMap = daService.getMap("select money,company_name from com_info_Tb where id=? ", new Object[]{comid});
					Double balance = Double.valueOf(comMap.get("money")+"");
					String name = (String)comMap.get("company_name");
					Long uin = (Long)request.getSession().getAttribute("loginuin");
					if(money<=balance){//提现金额不大于余额
						//扣除帐号余额//写提现申请表
						List<Map<String, Object>> sqlList = new ArrayList<Map<String,Object>>();
						Map<String, Object> comSqlMap = new HashMap<String, Object>();
						comSqlMap.put("sql", "update com_info_tb set money = money-? where id= ?");
						comSqlMap.put("values", new Object[]{money,comid});
						Map<String, Object> withdrawSqlMap = new HashMap<String, Object>();
						withdrawSqlMap.put("sql", "insert into withdrawer_tb  (comid,amount,create_time,acc_id,uin,wtype) values(?,?,?,?,?,?)");
						withdrawSqlMap.put("values", new Object[]{comid,money,System.currentTimeMillis()/1000,accId,uin,type});
						Map<String, Object> moneySqlMap = new HashMap<String, Object>();
						moneySqlMap.put("sql", "insert into money_record_tb (comid,amount,create_time,type,remark) values(?,?,?,?,?)");
						moneySqlMap.put("values", new Object[]{comid,money,System.currentTimeMillis()/1000,2,name+"提现申请"});
						
						Map<String, Object> parkAccountSqlMap = new HashMap<String, Object>();
						//Object uid = request.getSession().getAttribute("loginuin");
						parkAccountSqlMap.put("sql", "insert into park_account_tb (comid,amount,create_time,type,remark,uid,source) values(?,?,?,?,?,?,?)");
						parkAccountSqlMap.put("values", new Object[]{comid,money,System.currentTimeMillis()/1000,1,"提现申请",uin,5});
						
						sqlList.add(comSqlMap);
						sqlList.add(withdrawSqlMap);
						sqlList.add(moneySqlMap);
						sqlList.add(parkAccountSqlMap);
						result = daService.bathUpdate(sqlList);
					}
					if(result)
						AjaxUtil.ajaxOutput(response, "1");
					else {
						AjaxUtil.ajaxOutput(response, "0");
					}
				}
			}else {
				AjaxUtil.ajaxOutput(response, "-1");
			}
		}
		return null;
	}	
}

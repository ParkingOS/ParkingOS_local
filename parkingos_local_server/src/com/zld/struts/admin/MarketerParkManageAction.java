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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.GetLocalCode;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.ZLDType;
/**
 * 总管理员   停车场注册修改删除等
 * @author Administrator
 *
 */
public class MarketerParkManageAction extends Action {

	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Integer state = RequestUtil.getInteger(request, "state", 0);
		Long marketerId = (Long)request.getSession().getAttribute("marketerid");
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select c.*,t.strid,t.nickname,t.mobile cmobile,t.password from com_info_tb c,user_info_tb t " +
					"where c.type=? and t.comid=c.id and t.auth_flag=? and c.state=? and c.uid=? ";
			String countSql = "select count(*) from com_info_tb where type=? and state=? and uid=?";
			Long count = daService.getLong(countSql,new Object[]{0,state,marketerId});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getPage(sql, null, 1, 20);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(0);
			params.add(ZLDType.ZLD_PARKADMIN_ROLE);
			params.add(state);
			params.add(marketerId);
			if(count>0){
				list = daService.getAll(sql +" order by id desc ",params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select c.* from com_info_tb c" +
					" where c.type=? and c.state=? and c.uid=?";
			String countSql = "select count(*) from com_info_tb c where c.type=? and state=?  and uid=?";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"com_info","c.",null);
			List<Object> params = null;
			if(sqlInfo!=null){
				countSql+=" and "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}else {
				params=new ArrayList<Object>();
			}
			params.add(0,marketerId);
			params.add(0,state);
			params.add(0,0);
			//System.out.println(sqlInfo);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			Integer result = createAdmin(request);
			if(result == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else {
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("edit")){	
			String company =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
			String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
			String phone =RequestUtil.processParams(request, "phone");
			String mobile =RequestUtil.processParams(request, "mobile");
			String property =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "property"));
			String id =RequestUtil.processParams(request, "id");
			Integer stop_type = RequestUtil.getInteger(request, "stop_type", 0);
			Integer share_number = RequestUtil.getInteger(request, "share_number", 0);
			Integer parking_type = RequestUtil.getInteger(request, "parking_type", 0);
			Integer parking_total = RequestUtil.getInteger(request, "parking_total", 0);
			Integer city = RequestUtil.getInteger(request, "city", 0);
			Integer uid = RequestUtil.getInteger(request, "uid", 0);
			Integer biz_id = RequestUtil.getInteger(request, "biz_id", 0);
			Integer nfc = RequestUtil.getInteger(request, "nfc", 0);
			Integer etc = RequestUtil.getInteger(request, "etc", 0);
			Integer book = RequestUtil.getInteger(request, "book", 0);
			Integer navi = RequestUtil.getInteger(request, "navi", 0);
			Double longitude =RequestUtil.getDouble(request, "longitude",0.0);
			Double latitude =RequestUtil.getDouble(request, "latitude",0.0);
			Integer monthlypay = RequestUtil.getInteger(request, "monthlypay", 0);
			if(state==-1)
				state=0;
			String fields = "company_name=?,address=?,phone=?,mobile=?,property=?,parking_total=?," +
					"parking_type=?,type=?,share_number=?,update_time=?,state=?,uid=?,biz_id=?," +
					"nfc=?,etc=?,book=?,navi=?,monthlypay=?,longitude=?,latitude=? ";
			Object [] values = null;
			if(city!=0){
				fields+=",city=? ";
				values =new Object[]{company,address,phone,mobile,property,parking_total,parking_type,stop_type,share_number,
						System.currentTimeMillis()/1000,state,uid,biz_id,etc,nfc,book,navi,monthlypay,longitude,latitude,city,Long.valueOf(id)};
			}else {
				values =new Object[]{company,address,phone,mobile,property,parking_total,parking_type,stop_type,share_number,
						System.currentTimeMillis()/1000,state,uid,biz_id,etc,nfc,book,navi,monthlypay,longitude,latitude,Long.valueOf(id)};
			}
			String sql = "update com_info_tb set "+fields+" where id=?";
			int result = daService.update(sql, values);
//			if(result==1&&city>0){
//				publicMethods.setCityCache(Long.valueOf(id),city);
//			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("editcontactor")){
			String mobile =RequestUtil.processParams(request, "mobile");
			String strid =RequestUtil.processParams(request, "strid");
			String pass =RequestUtil.processParams(request, "pass");
			if(pass.equals(""))
				pass = strid;
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			Long comid =RequestUtil.getLong(request, "comid", -1L);
			String sql = "update user_info_tb set strid=?,password=?,mobile=?,nickname=? where comid=? and auth_flag=?";
			int result = daService.update(sql, new Object[]{strid,pass,mobile,nickname,comid,ZLDType.ZLD_PARKADMIN_ROLE});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "selids");
			String sql = "update com_info_tb set state=?,update_time=? where id =?";
			Object [] values = new Object[]{1,System.currentTimeMillis()/1000,Long.valueOf(id)};
			int result = daService.update(sql, values);
//			if(result==1)
//				ParkingMap.deleteParkingMap(Long.valueOf(id));
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("check")){
			String strid = RequestUtil.processParams(request, "value");
			String sql = "select count(*) from user_info_tb where strid =?";
			Long result = daService.getLong(sql, new Object[]{strid});
			if(result>0)
				AjaxUtil.ajaxOutput(response, "1");
			else {
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("localdata")){//地区信息
			AjaxUtil.ajaxOutput(response,GetLocalCode.getLocalData());
		}else if(action.equals("getlocalbycode")){
			Integer code = RequestUtil.getInteger(request, "code", 0);
			String local = GetLocalCode.localDataMap.get(code);
			if(local==null||local.equals("null")){
				AjaxUtil.ajaxOutput(response,"");
				return null;
			}
			if(code%100!=0)
				local =GetLocalCode.localDataMap.get((code/100)*100)+local;
			if(code%10000!=0)
				local =GetLocalCode.localDataMap.get((code/10000)*10000)+local;
			AjaxUtil.ajaxOutput(response,local);
		}else if(action.equals("getmarketers")){
			List<Map> tradsList = daService.getAll("select * from user_info_tb where id=? ",
					new Object[]{marketerId});
			String result = "[";
			if(tradsList!=null&&tradsList.size()>0){
				for(Map map : tradsList){
					result+="{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("nickname")+"\"}";
				}
			}
			result+="]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getbizs")){
			List<Map> tradsList = daService.getAll("select * from bizcircle_tb where state =?",
					new Object[]{0});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"请选择\"}";
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

	//注册停车场管理员帐号
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Integer createAdmin(HttpServletRequest request){
	
		Long time = System.currentTimeMillis()/1000;
		String company =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
		System.out.println(company);
		String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
		String phone =RequestUtil.processParams(request, "phone");
		String mobile =RequestUtil.processParams(request, "mobile");
		String longitude =RequestUtil.processParams(request, "longitude");
		String latitude =RequestUtil.processParams(request, "latitude");
		String property =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "property"));
		Integer parking_type =RequestUtil.getInteger(request, "parking_type", 0);
		Integer parking_total =RequestUtil.getInteger(request, "parking_total", 0);
		Integer type = RequestUtil.getInteger(request, "type", 0);
		Integer city = RequestUtil.getInteger(request, "city", 0);
		Integer biz_id = RequestUtil.getInteger(request, "biz_id", 0);
		Integer uid = RequestUtil.getInteger(request, "uid", 0);
		Integer nfc = RequestUtil.getInteger(request, "nfc", 0);
		Integer etc = RequestUtil.getInteger(request, "etc", 0);
		Integer book = RequestUtil.getInteger(request, "book", 0);
		Integer navi = RequestUtil.getInteger(request, "navi", 0);
		Integer monthlypay = RequestUtil.getInteger(request, "monthlypay", 0);
		Long comId = daService.getLong("SELECT nextval('seq_com_info_tb'::REGCLASS) AS newid",null);
		
		List<Map> sqlsList = new ArrayList<Map>();
		Map comMap = new HashMap();
		//String share_number =RequestUtil.processParams(request, "share_number");
		String comsql = "insert into com_info_tb(id,company_name,address,mobile,phone,create_time," +
				"property,parking_type,parking_total,longitude,latitude,type,update_time,city,uid,biz_id,nfc,etc,book,navi,monthlypay)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] comvalues = new Object[]{comId,company,address,mobile,phone,time,
				property,parking_type,parking_total,Double.valueOf(longitude),Double.valueOf(latitude),
				type,time,city,uid,biz_id,nfc,etc,book,navi,monthlypay};
		comMap.put("sql", comsql);
		comMap.put("values", comvalues);
		sqlsList.add(comMap);
		
		boolean r =  daService.bathUpdate(sqlsList);
		if(r){
//			if(city>0)
//				publicMethods.setCityCache(Long.valueOf(comId),city);
			return 1;
		}
		else {
			return -1;
		}
	}
}

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
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * 总管理员   免费停车场注册修改删除等
 * @author Administrator
 *
 */
public class FreeParkManageAction extends Action {

	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select * from com_info_tb where type=? ";
			String countSql = "select count(*) from com_info_tb where type=?";
			Long count = daService.getLong(countSql,new Object[]{1});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getPage(sql, null, 1, 20);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(1);
			if(count>0){
				list = daService.getAll(sql +" order by id desc ",params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from com_info_tb where type=? ";
			String countSql = "select count(*) from com_info_tb where type=?";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"com_info");
			List<Object> params = null;
			if(sqlInfo!=null){
				countSql+=" and  "+ sqlInfo.getSql();
				sql +=" and " +sqlInfo.getSql();
				params= sqlInfo.getParams();
			}else {
				params = new ArrayList<Object>();
			}
			params.add(0,1);
			//System.out.println(sqlInfo);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			Long comId = createParking(request);
			if(comId!=null){
//				Map comMap = daService.getMap("select * from com_info_tb where id=?", new Object[]{comId});
//				ParkingMap.addParkingMap(comMap);
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
			Integer type = RequestUtil.getInteger(request, "type", 0);
			Integer parking_type = RequestUtil.getInteger(request, "parking_type", 0);
			Integer parking_total = RequestUtil.getInteger(request, "parking_total", 0);
			String sql = "update com_info_tb set company_name=?,address=?,phone=?,mobile=?,property=?,parking_total=?,parking_type=?,type=?,update_time=? where id=?";
			Object [] values = new Object[]{company,address,phone,mobile,property,parking_total,parking_type,type,
					System.currentTimeMillis()/1000,Long.valueOf(id)};
			int result = daService.update(sql, values);
//			if(result==1){
//				Map comMap = daService.getMap("select * from com_info_tb where id=?", new Object[]{Long.valueOf(id)});
//				ParkingMap.updateParkingMap(comMap);
//			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "selids");
			String sql = "update com_info_tb set state=?,update_time=? where id =?";
			Object [] values = new Object[]{1,System.currentTimeMillis()/1000,Long.valueOf(id)};
			int result = daService.update(sql, values);
//			if(result==1)
//				ParkingMap.deleteParkingMap(Long.valueOf(id));
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}

	//注册停车场管理员帐号
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Long createParking(HttpServletRequest request){
	
		Long time = System.currentTimeMillis()/1000;
		String company =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
		String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
		String phone =RequestUtil.processParams(request, "phone");
		String mobile =RequestUtil.processParams(request, "mobile");
		String cmobile =RequestUtil.processParams(request, "cmobile");
		String longitude =RequestUtil.processParams(request, "longitude");
		String latitude =RequestUtil.processParams(request, "latitude");
		String property =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "property"));
		Integer parking_type =RequestUtil.getInteger(request, "parking_type", 0);
		Integer parking_total =RequestUtil.getInteger(request, "parking_total", 0);
//		Integer type = RequestUtil.getInteger(request, "type", 1);
		if(cmobile.equals("")){
			if(!mobile.equals(""))
				cmobile=mobile;
			else {
				cmobile=null;
			}
		}
		Long comId = daService.getLong("SELECT nextval('seq_com_info_tb'::REGCLASS) AS newid",null);
		
		List<Map> sqlsList = new ArrayList<Map>();
		Map comMap = new HashMap();
		//String share_number =RequestUtil.processParams(request, "share_number");
		String comsql = "insert into com_info_tb(id,company_name,address,mobile,phone,create_time," +
				"property,parking_type,parking_total,longitude,latitude,type,update_time)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] comvalues = new Object[]{comId,company,address,mobile,phone,time,
				property,parking_type,parking_total,Double.valueOf(longitude),Double.valueOf(latitude),1,time};
		comMap.put("sql", comsql);
		comMap.put("values", comvalues);
		
		sqlsList.add(comMap);
		
		boolean r = daService.bathUpdate(sqlsList);
		if(r)
			return comId;
		else {
			return null;
		}
	}
}

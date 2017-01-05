package com.zld.struts.parkadmin;

import java.util.ArrayList;
import java.util.List;

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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.TimeTools;
/**
 * 停车场后台管理员登录后，查看订单，不能修改和删除
 * @author Administrator
 *
 */
public class PackageManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(PackageManageAction.class);

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
		if(comid==0)
			comid = RequestUtil.getLong(request, "comid", 0L);
		if(action.equals("")){
			request.setAttribute("comid", comid);
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select * from product_package_tb where comid=? ";
			String countSql = "select count(*) from product_package_tb  where comid=? ";
			Long count = daService.getLong(countSql,new Object[]{comid});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List list = null;//daService.getAll(sql, null, 1, 20);
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(comid);
			if(count>0){
				list = daService.getAll(sql+ " order by id desc", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from product_package_tb where comid=?  ";
			String countSql = "select count(*) from product_package_tb where  comid=?  " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo base = new SqlInfo("1=1", new Object[]{comid});
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"product_package_tb");
			Object[] values = null;
			List<Object> params = null;
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
			//System.out.println(sqlInfo);
			Long count= daService.getLong(countSql, values);
			List list = null;//daService.getAll(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+ " order by id desc", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			Integer b_time =RequestUtil.getInteger(request, "b_time", 8);
			Integer e_time =RequestUtil.getInteger(request, "e_time", 20);
			Integer bmin =RequestUtil.getInteger(request, "bmin", 0);
			Integer emin =RequestUtil.getInteger(request, "emin", 0);
			Integer type =RequestUtil.getInteger(request, "type", 0);
			String limitday = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "limitday"));
			Long lday = System.currentTimeMillis()/1000+3*30*24*60*60;//默认三十天
			if(!limitday.equals("")){
				lday=TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(limitday+" 23:59:59");
			}	
			String resume =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "resume"));
			Integer remain_number =RequestUtil.getInteger(request, "remain_number", 8);
			Double price =RequestUtil.getDouble(request, "price",0d);
			Double oprice =RequestUtil.getDouble(request, "old_price",0d);
			String p_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "p_name"));
			if(type==0){//全天包月
				b_time=0;
				e_time=24;
				bmin=0;
				emin=0;
			}else if(type==1){//夜间包月
				if(e_time>=b_time){
					b_time = 21;
					e_time=7;
				}
			}else if(type==2){
				if(e_time<=b_time){
					b_time = 7;
					e_time=21;
				}
			}
			String sql = "insert into  product_package_tb (b_time,e_time,price,old_price,p_name, comid,remain_number,bmin,emin,limitday,resume,type) values" +
					"(?,?,?,?,?,?,?,?,?,?,?,?)";
			Object [] values = new Object[]{b_time,e_time,price,oprice,p_name,comid,remain_number,bmin,emin,lday,resume,type};
			int result = daService.update(sql, values);
			if(result==1){//添加成功后，更新车场支持包月功能 
				daService.update(" update com_info_tb set monthlypay=? where id=?",new Object[]{1,comid});
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("edit")){
			String id =RequestUtil.processParams(request, "id");
			Integer b_time = RequestUtil.getInteger(request, "b_time", 8);
			Integer e_time =RequestUtil.getInteger(request, "e_time",20);
			Double price =RequestUtil.getDouble(request, "price",0.0);
			Double oprice =RequestUtil.getDouble(request, "old_price",0d);
			Integer remain_number =RequestUtil.getInteger(request, "remain_number",0);
			Integer bmin =RequestUtil.getInteger(request, "bmin", 0);
			Integer emin =RequestUtil.getInteger(request, "emin", 0);
			Integer type =RequestUtil.getInteger(request, "type", 0);
			String limitday = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "limitday"));
			Long lday = System.currentTimeMillis()/1000+30*24*60*60;//默认三十天
			if(!limitday.equals("")){
				lday=TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(limitday+" 00:00:00");
			}	
			String resume =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "resume"));
			String p_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "p_name"));
			Integer state =RequestUtil.getInteger(request, "state", -1);
			String sql = "update product_package_tb set p_name=?,b_time=?,e_time=?,price=?, old_price=?," +
					"remain_number=? ,bmin=?,emin=?, limitday = ?,type=?, resume=?,state=? where id=?";
			Object [] values = new Object[]{p_name,b_time,e_time,price,oprice,remain_number,
					bmin,emin,lday,type,resume,state,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "id");
			String sql = "delete from product_package_tb where id =?";
			Object [] values = new Object[]{Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}

}
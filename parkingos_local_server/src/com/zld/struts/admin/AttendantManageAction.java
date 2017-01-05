package com.zld.struts.admin;

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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * 泊车点管理，在总管理员后台
 * @author Administrator
 *
 */
public class AttendantManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(AttendantManageAction.class);
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
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select u.id,u.nickname,u.comid,u.reg_time,u.logon_time,p.resume,p.utime,p.driver_year,p.state,p.pic_url,p.driver_pic from user_info_tb u left join user_pic_tb p on u.id=p.uin where u.auth_flag=?  ";
			String countSql = "select count(ID) from user_info_tb where auth_flag=? " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"user_info","u",new String[]{"state","driver_year"});
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				countSql+=" and  "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}
			params.add(13);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by u.id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			System.out.println(json);
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Integer state = RequestUtil.getInteger(request, "state", 0);
			Integer driverYear = RequestUtil.getInteger(request, "driver_year", 0);
			String resume = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "resume"));
			Long count = daService.getLong("select count(ID) from user_pic_tb where uin =? ", new Object[]{id});
			int ret =0;
			if(count==0){
				ret = daService.update("insert into user_pic_tb (ctime,uin,state,driver_year,utime,resume) values(?,?,?,?,?,?)", 
						new Object[]{System.currentTimeMillis()/1000,id,0,driverYear,System.currentTimeMillis()/1000,resume});
			}else {
				ret = daService.update("update user_pic_tb  set state=?,ctime=?,driver_year=?,resume=? where uin =?", 
						new Object[]{state,System.currentTimeMillis()/1000,driverYear,resume,id});
			}
			AjaxUtil.ajaxOutput(response, ret+"");
			
		}else if(action.equals("create")){
			int ret =0;
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int ret = daService.update("delete from user_pic_tb where uin=?", new Object[]{id});
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("uploadpic")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String table = RequestUtil.getString(request, "table");
			Integer type = RequestUtil.getInteger(request, "type", 0);
			if(id!=-1&&!"".equals(table)){
				String picurl = publicMethods.uploadPicToMongodb(request, id, table);
				int ret = 0;
				if(picurl!=null&&!"".equals(picurl)){
					if(type==0){//上传头像
						ret = daService.update("update user_pic_tb set pic_url=? where uin = ? ", new Object[]{picurl,id});
						if(ret==-1){
							daService.update("insert into user_pic_tb (uin,pic_url,state,ctime) values(?,?,?,?) ", 
									new Object[]{id,picurl,0,System.currentTimeMillis()/1000});
						}
					}else if(type==1){//上传证件照
						ret = daService.update("update user_pic_tb set driver_pic=? where uin = ? ", new Object[]{picurl,id});
						if(ret==-1){
							daService.update("insert into user_pic_tb (uin,driver_pic,state,ctime) values(?,?,?,?) ", 
									new Object[]{id,picurl,0,System.currentTimeMillis()/1000});
						}
					}
				}
				if(ret==1)
					request.setAttribute("result", "上传成功，请关闭当前窗口!");
				else 
					request.setAttribute("result", "上传失败!");
			}else {
				request.setAttribute("result", "上传失败!");
			}
			return mapping.findForward("uploadret");
		}
		return null;
	}
}
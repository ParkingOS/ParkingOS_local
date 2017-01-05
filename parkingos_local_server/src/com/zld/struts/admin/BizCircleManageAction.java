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
import com.zld.service.DataBaseService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
/**
 * 商圈管理，在总管理员后台
 * @author Administrator
 *
 */
public class BizCircleManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(BizCircleManageAction.class);

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
		}else if(action.equals("quickquery")){
			String sql = "select * from bizcircle_tb where  state =?  ";
			String countSql = "select count(*) from bizcircle_tb  where  state =? ";
			Long count = daService.getLong(countSql,new Object[]{0});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			List<Object> params = new ArrayList<Object>();
			params.add(0);
			if(count>0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select * from bizcircle_tb where  state =? ";
			String countSql = "select count(*) from bizcircle_tb where  state =? " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo base = new SqlInfo("1=1", new Object[]{0});
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"user_info");
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
				params = base.getParams();
			}
			Long count= daService.getLong(countSql, values);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			int result = createBizCricle(request);
			if(result==1)
				AjaxUtil.ajaxOutput(response, "1");
			else {
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("edit")){
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			String resume =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "resume"));
			Integer state = RequestUtil.getInteger(request, "state", 0);
			String id =RequestUtil.processParams(request, "id");
			String sql = "update bizcircle_tb set name=?,resume=?,state=? where id=?";
			Object [] values = new Object[]{name,resume,state,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "selids", -1L);
			Integer state = RequestUtil.getInteger(request, "state", 0);
			if(state==0)//0可用，1禁用，为0时是要改为禁用，为1时是要改为禁用，在这里反转 一下。
				state=1;
			else if(state==1)
				state=0;
			String sql = "update bizcircle_tb set state=? where id =?";
			Object [] values = new Object[]{state,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}
	//注册商圈
	@SuppressWarnings({ "rawtypes" })
	private int createBizCricle(HttpServletRequest request){
		String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
		String resume =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "resume"));
		//用户表
		String sql="insert into bizcircle_tb (name,resume,create_time) " +
				"values (?,?,?)";
		Object [] values= new Object[]{name,resume,System.currentTimeMillis()/1000};
		int r = daService.update(sql, values);
		return r;
	}
}
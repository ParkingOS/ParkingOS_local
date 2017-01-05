package com.zld.struts.admin;

import java.util.List;

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


/**
 * 金额设定
 * @author Administrator
 *
 */
public class MoneySetAction extends Action{

	@Autowired
	private DataBaseService daService;
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = RequestUtil.getLong(request, "comid", -1L);
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			request.setAttribute("comid", request.getParameter("comid"));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select * from money_set_tb where comid=?  ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			//System.out.println(sqlInfo);
			List list = daService.getAll(sql+" order by id desc",new Object[]{comid});
			int count =0;
			if(list!=null)
				count = list.size();
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){//添加帐号
			Integer mtype = RequestUtil.getInteger(request, "mtype", 0);
			Integer giveto = RequestUtil.getInteger(request, "giveto", 0);
			int result=0;
			try {
				result = daService.update("insert into money_set_tb (comid,mtype,giveto)" +
							" values(?,?,?)",
							new Object[]{comid,mtype,giveto});
			} catch (Exception e) {
				if(e.getMessage().indexOf("money_set_tb_comid_mtype_key")!=-1)
					result=-2;
				//e.printStackTrace();
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Integer mtype = RequestUtil.getInteger(request, "mtype", 0);
			Integer giveto = RequestUtil.getInteger(request, "giveto", 0);
			int	result = daService.update("update money_set_tb set mtype =?,giveto=? where id=?",
					new Object[]{mtype,giveto,id});
			AjaxUtil.ajaxOutput(response, ""+result);
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int	result = daService.update("delete from  money_set_tb  where id=?",
					new Object[]{id});
			AjaxUtil.ajaxOutput(response, ""+result);
		}
		return null;
	}
	
}

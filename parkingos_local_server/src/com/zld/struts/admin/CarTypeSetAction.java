package com.zld.struts.admin;

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


/**
 * 车型设定
 * @author Administrator
 *
 */
public class CarTypeSetAction extends Action{

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
			Map<String, Object> comInfoMap = daService.getMap("select car_type from com_info_tb where id =? ", new Object[]{comid});
			request.setAttribute("cartype",comInfoMap.get("car_type"));
			request.setAttribute("comid", request.getParameter("comid"));
			return mapping.findForward("list");
		}else if(action.equals("query")){
			String sql = "select * from car_type_tb where comid=?  ";
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			//System.out.println(sqlInfo);
			List list = daService.getAll(sql+" order by sort,id desc",new Object[]{comid});
			int count =0;
			if(list!=null)
				count = list.size();
			String json = JsonUtil.Map2Json(list,1,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){//添加帐号
			String name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "name"));
			Integer sort = RequestUtil.getInteger(request, "sort", 0);
			int result=0;
			try {
				result = daService.update("insert into car_type_tb (comid,name,sort)" +
							" values(?,?,?)",
							new Object[]{comid,name,sort});
			} catch (Exception e) {
				if(e.getMessage().indexOf("car_type_tb_comid_mtype_key")!=-1)
					result=-2;
				//e.printStackTrace();
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("edit")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String name = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "name"));
			Integer sort = RequestUtil.getInteger(request, "sort", 0);
			int	result = daService.update("update car_type_tb set name =?,sort=? where id=?",
					new Object[]{name,sort,id});
			AjaxUtil.ajaxOutput(response, ""+result);
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			int	result = daService.update("delete from  car_type_tb  where id=?",
					new Object[]{id});
			AjaxUtil.ajaxOutput(response, ""+result);
		}else if(action.equals("setusecartype")){
			Integer carType = RequestUtil.getInteger(request, "cartype", 0);
			if(carType==0)
				carType=1;
			else {
				carType=0;
			}
			int	result = daService.update("update com_info_tb set car_type =? where id=?",
					new Object[]{carType,comid});
			AjaxUtil.ajaxOutput(response, ""+result);
		}
		return null;
	}
	
}

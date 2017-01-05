package com.zld.struts.admin;

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
import com.zld.utils.RequestUtil;

public class ProvinceSettingAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(ProvinceSettingAction.class);
	
	/*
	 * ¹¤×÷Õ¾ÉèÖÃ
	 */
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comId = (Long)request.getSession().getAttribute("comid");
		if(comId == null){
			response.sendRedirect("login.do");
			return null;
		}
		Long comid = RequestUtil.getLong(request, "comid", -1L);
		if(action.equals("")){
			request.setAttribute("comid", comid);
			return mapping.findForward("list");
		}else if(action.equals("firstprovince")){
			String firstprovince = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "param"));
			String sql = "update com_info_tb set firstprovince=? where id=?";
			int re = daService.update(sql, new Object[]{firstprovince,comid});
			if(re == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("getprovince")){
			String sql = "select firstprovince from com_info_tb where id=?";
			Map<String, Object> map = daService.getMap(sql, new Object[]{comid});
			if(map == null || map.get("firstprovince") == null){
				AjaxUtil.ajaxOutput(response, "");
			}else{
				AjaxUtil.ajaxOutput(response, (String)map.get("firstprovince"));
			}
		}
		return null;
	}
}

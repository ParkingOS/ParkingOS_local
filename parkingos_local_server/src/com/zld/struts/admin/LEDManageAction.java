package com.zld.struts.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;

public class LEDManageAction extends Action {
	@Autowired
	private DataBaseService daService;

	private Logger logger = Logger.getLogger(LEDManageAction.class);

	/*
	 * LED…Ë÷√
	 */
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
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
		}else if(action.equals("query")){
			String sql = "select l.*,cp.worksite_id from com_led_tb l,com_pass_tb cp where l.passid=cp.id and cp.comid=? order by id";
			String sqlcount = "select count(1) from com_led_tb l,com_pass_tb cp where l.passid=cp.id and cp.comid=?";
			Long count = daService.getLong(sqlcount, new Object[]{comid});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(comid);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("getworksites")){
			String sql = "select * from com_worksite_tb where comid=?";
			List<Map> list = daService.getAll(sql, new Object[]{comid});
			String result = "[{\"value_no\":\"-1\",\"value_name\":\"«Î—°‘Ò\"},";
			if(!list.isEmpty()){
				for(Map map : list){
					result+="{\"value_no\":\""+map.get("id")+"\",\"value_name\":\""+map.get("worksite_name")+"\"},";
				}
				result = result.substring(0, result.length()-1);
			}
			result += "]";
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("getname")){
			Long passid = RequestUtil.getLong(request, "passid", -1L);
			String sql = "select passname from com_pass_tb where id=?";
			Map<String, Object> map = new HashMap<String, Object>();
			map = daService.getMap(sql, new Object[]{passid});
			AjaxUtil.ajaxOutput(response, map.get("passname")+"");
		}else if(action.equals("create")){
			String ledip = RequestUtil.processParams(request, "ledip");
			String ledport = RequestUtil.processParams(request, "ledport");
			String leduid = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "leduid"));
			Integer movemode = RequestUtil.getInteger(request, "movemode", -1);
			Integer movespeed = RequestUtil.getInteger(request, "movespeed", -1);
			Long dwelltime = RequestUtil.getLong(request, "dwelltime", -1L);
			Integer ledcolor = RequestUtil.getInteger(request, "ledcolor", -1);
			Integer showcolor = RequestUtil.getInteger(request, "showcolor", -1);
			Integer typeface = RequestUtil.getInteger(request, "typeface", -1);
			Integer typesize = RequestUtil.getInteger(request, "typesize", -1);
			String matercont = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "matercont"));
			Long passid = RequestUtil.getLong(request, "passid", -1L);
			if(passid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			if(movemode == -1) movemode = null;
			if(movespeed == -1) movespeed = null;
			if(dwelltime == -1) dwelltime = null;
			if(ledcolor == -1) ledcolor = null;
			if(showcolor == -1) showcolor = null;
			if(typeface == -1) typeface = null;
			if(typesize == -1) typesize = null;
			//ÃÌº”
			String sql = "insert into com_led_tb(passid,ledip,ledport,leduid,movemode,movespeed,dwelltime,ledcolor,showcolor,typeface,typesize,matercont) values(?,?,?,?,?,?,?,?,?,?,?,?)";
			int re = daService.update(sql, new Object[]{passid,ledip,ledport,leduid,movemode,movespeed,dwelltime,ledcolor,showcolor,typeface,typesize,matercont});
			if(re == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("edit")){
			String ledip = RequestUtil.processParams(request, "ledip");
			String ledport = RequestUtil.processParams(request, "ledport");
			String leduid = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "leduid"));
			Integer movemode = RequestUtil.getInteger(request, "movemode", -1);
			Integer movespeed = RequestUtil.getInteger(request, "movespeed", -1);
			Long dwelltime = RequestUtil.getLong(request, "dwelltime", -1L);
			Integer ledcolor = RequestUtil.getInteger(request, "ledcolor", -1);
			Integer showcolor = RequestUtil.getInteger(request, "showcolor", -1);
			Integer typeface = RequestUtil.getInteger(request, "typeface", -1);
			Integer typesize = RequestUtil.getInteger(request, "typesize", -1);
			String matercont = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "matercont"));
			Long passid = RequestUtil.getLong(request, "passid", -1L);
			Long ledid = RequestUtil.getLong(request, "id", -1L);
			if(passid == -1 || ledid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			if(movemode == -1) movemode = null;
			if(movespeed == -1) movespeed = null;
			if(dwelltime == -1) dwelltime = null;
			if(ledcolor == -1) ledcolor = null;
			if(showcolor == -1) showcolor = null;
			if(typeface == -1) typeface = null;
			if(typesize == -1) typesize = null;
			//±‡º≠
			String sql = "update com_led_tb set ledip=?,ledport=?,leduid=?,movemode=?,movespeed=?,dwelltime=?,ledcolor=?,showcolor=?,typeface=?,typesize=?,matercont=?,passid=? where id=?";
			int re = daService.update(sql, new Object[]{ledip,ledport,leduid,movemode,movespeed,dwelltime,ledcolor,showcolor,typeface,typesize,matercont,passid,ledid});
			if(re == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}else if(action.equals("delete")){
			Long cameraid = RequestUtil.getLong(request, "selids", -1L);
			String sql = "delete from com_led_tb where id=?";
			int result = daService.update(sql, new Object[]{cameraid});
			if(result == 1){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
		}
		return null;
	}
}

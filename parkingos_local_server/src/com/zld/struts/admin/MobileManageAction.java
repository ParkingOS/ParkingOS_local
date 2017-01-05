package com.zld.struts.admin;

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
 * NFC管理，在总管理员后台
 * @author Administrator
 *
 */
public class MobileManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(MobileManageAction.class);

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
			String sql = "select * from mobile_tb ";
			String countSql = "select count(*) from mobile_tb " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"mobile_tb");
			List<Object> params = null;
			if(sqlInfo!=null){
				countSql+=" where "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params = sqlInfo.getParams();
			}
			//System.out.println(sqlInfo);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("create")){
			String result = createNfc(request);
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("edit")){
			/* distru_date bigint, -- 分配时间
			  uid bigint, -- 市场专员
			  comid bigint, -- 停车场
			  uin bigint, -- 车场签收人帐号*/
			Long id =RequestUtil.getLong(request, "id",-1L);
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Long _comid = RequestUtil.getLong(request, "comid", -1L);
			String num =RequestUtil.processParams(request, "num");
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			String mode = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "mode"));
			String sql = "update mobile_tb set uid=?,comid=?,uin=?,distru_date=?,num=?,mode=? where id=?";
			Object [] values = new Object[]{uid,_comid,uin,System.currentTimeMillis()/1000,num,mode,id};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}

	private String createNfc(HttpServletRequest request) {
		/**
		 *  id bigint NOT NULL,
			  imei character varying(25), -- 手机串号
			  num character varying(15), -- 手机号码
			  mode character varying(100), -- 手机型号
			  price numeric(5,2), -- 价格
			  create_tiime bigint, -- 入库时间
			  editor character varying(50), -- 入库人
			  distru_date bigint, -- 分配时间
			  uid bigint, -- 市场专员
			  comid bigint, -- 停车场
			  uin bigint, -- 车场签收人帐号
			  money_3 numeric(5,2), -- 近三日结算金额
			  order_3 integer, -- 近三日订单数量
			  CONSTRAINT mobile_tb_pkey PRIMARY KEY (id)
  
		 */
		String imei =RequestUtil.processParams(request, "imei");
		String mode =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "mode"));
		String editor =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "editor"));
		String num =RequestUtil.processParams(request, "num");
		Double price = RequestUtil.getDouble(request, "price", 0d);
		Long time = System.currentTimeMillis()/1000;
		String sql = "insert into  mobile_tb (imei,num,mode,editor,price,create_time) values" +
				"(?,?,?,?,?,?)";
		Object [] values = new Object[]{imei,num,mode,editor,price,time};
		int result = daService.update(sql, values);
		return result+"";
	}
	

}
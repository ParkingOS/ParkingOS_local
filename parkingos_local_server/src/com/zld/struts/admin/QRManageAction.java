package com.zld.struts.admin;

import java.io.IOException;
import java.util.ArrayList;
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
import com.zld.utils.ExportExcelUtil;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SqlInfo;
import com.zld.utils.StringUtils;
/**
 * 泊车点管理，在总管理员后台
 * @author Administrator
 *
 */
public class QRManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(QRManageAction.class);
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
			String sql = "select * from qr_code_tb ";
			String countSql = "select count(ID) from qr_code_tb " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"qr_code");
			List<Object> params = new ArrayList<Object>();
			if(sqlInfo!=null){
				countSql+=" where  "+ sqlInfo.getSql();
				sql +=" where "+sqlInfo.getSql();
				params= sqlInfo.getParams();
			}
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("create")){
			Integer type  =RequestUtil.getInteger(request, "type", -1);
			Integer count = RequestUtil.getInteger(request, "count", 0);
			String []codes=null;
			Long ids[]=null;
			if(count>0){
				ids=new Long[count];
				for(int i=0;i<count;i++){
					Long newId = daService.getkey("seq_qr_code_tb");
					ids[i]=newId;
				}
				codes = StringUtils.getGRCode(ids);
			}
			Long ntime = System.currentTimeMillis()/1000;
			String sql = "";
			List<Object[]> values=null;
			int ret = 0;
			int []arrTypes = null;
			if(ids!=null&&ids.length>0&&type==0){//加NFC二维码
				sql = "insert into qr_code_tb(id,ctime,type,code) values(?,?,?,?)";
				values = new ArrayList<Object[]>();
				for(int i=0;i<codes.length;i++){
					Object []v = new Object[]{ids[i],ntime,type,codes[i]};
					values.add(v);
				}
				
				arrTypes = new int[]{4,4,4,12};
			}else if(ids!=null&&ids.length>0&&type==2){//车位二维码
				Long wid = RequestUtil.getLong(request, "wid", -1L);
				Long _comid = RequestUtil.getLong(request, "comid", -1L);
				sql = "insert into qr_code_tb(id,ctime,type,code,wid,comid) values(?,?,?,?,?,?)";
				values = new ArrayList<Object[]>();
				for(int i=0;i<codes.length;i++){
					Object []v = new Object[]{ids[i],ntime,type,codes[i],wid,_comid};
					values.add(v);
				}
				arrTypes = new int[]{4,4,4,12,4,4};
			}else if(ids!=null&&ids.length>0&&type==3){//泊车员二维码
				sql = "insert into qr_code_tb(id,ctime,type,code,uid) values(?,?,?,?,?)";
				Long uid = RequestUtil.getLong(request, "uid", -1L);
				ret = daService.update(sql, new Object[]{ids[0],ntime,type,codes[0],uid});
			}else if(ids!= null && ids.length>0&&type == 4){
				sql = "insert into qr_code_tb(id,ctime,type,code,comid) values(?,?,?,?,?) ";
				Long _comid = RequestUtil.getLong(request, "comid", -1L);
				ret = daService.update(sql, new Object[]{ids[0], ntime, type, codes[0], _comid});
			}
			
			if(values!=null&&values.size()>0){
				ret = daService.bathInsert(sql, values, arrTypes);
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("excle")){
			String [] heards = new String[]{"二维码","类型"};
			List<List<String>> bodyList = new ArrayList<List<String>>();
			
			Integer type  =RequestUtil.getInteger(request, "type_start", -1);
			List<Map<String, Object>> list = daService.getAll("select type,comid,code from qr_code_tb where state = ? and type=? " +
					"and isuse=? ", new Object[]{0,type,0});
			if(list!=null&&list.size()>0){
				//setComName(list);
				String [] f = new String[]{"code","type"};
				if(type==2){
					heards = new String[]{"二维码","类型","车场编号 "};
					f = new String[]{"code","type","comid"};
				}
				for(Map<String, Object> map : list){
					List<String> values = new ArrayList<String>();
					for(String field : f){
						if(field.equals("code"))
							values.add("http://s.tingchebao.com/zld/qr/c/"+map.get(field));
						else if(field.equals("type")){
							Integer t = (Integer)map.get(field);
							if(t==0)
								values.add("NFC二维码");
							else if(t==2){
								values.add("车位二维码");
							}else if(t==3){
								values.add("泊车员");
							}else {
								values.add("收费员");
							}
								
						}else {
							values.add(map.get(field)+"");
						}
					}
					bodyList.add(values);
				}
			}
			String fname = "二维码" + com.zld.utils.TimeTools.getDate_YY_MM_DD();
			fname = StringUtils.encodingFileName(fname);
			java.io.OutputStream os;
			try {
				os = response.getOutputStream();
				response.reset();
				response.setHeader("Content-disposition", "attachment; filename="
						+ fname + ".xls");
				ExportExcelUtil importExcel = new ExportExcelUtil("提现申请",
						heards, bodyList);
				importExcel.createExcelFile(os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else if(action.equals("isuse")){
			Long bid = RequestUtil.getLong(request, "bid", 0L);
			Long eid = RequestUtil.getLong(request, "eid", 0L);
			Integer isuse = RequestUtil.getInteger(request, "isuse", -1);
			int ret = 0;
			if(bid>0&&eid>0&&isuse>-1){
				ret = daService.update("update qr_code_tb set isuse=? where id between ? and ? ", new Object[]{isuse,bid,eid});
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}
		return null;
	}
	
}
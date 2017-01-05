package com.zld.struts.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.HttpProxy;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

public class WorksiteInfoAction extends Action {

	@Autowired
	private DataBaseService daService;
	
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	private Logger logger = Logger.getLogger(WorksiteInfoAction.class);
	/**
	 * 获取工作站和通道等信息
	 */
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comId = RequestUtil.getLong(request, "comid", -1L);
		if(comId == -1){
			AjaxUtil.ajaxOutput(response, "-1");
			return null;
		}
		if(action.equals("queryworksite")){
			String sql = "select * from com_worksite_tb where comid=? order by id desc";
			List<Object> values = new ArrayList<Object>();
			values.add(comId);
			List<Map<String,Object>> list = null;
			list = daService.getAllMap(sql, values);
			setWorksiteType(list);
			String result = StringUtils.createJson(list);
			result=result.replace("null", "-");
			AjaxUtil.ajaxOutput(response, result);
			//http://118.192.88.90:8080/zld/worksiteinfo.do?comid=3&action=queryworksite
		}else if(action.equals("querypass")){
			Long worksite_id = RequestUtil.getLong(request, "worksite_id", -1L);
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			logger.error(request.getRequestURI()+action+",uid:"+uid+",worksite:"+worksite_id);
			String sql = "select * from com_pass_tb where worksite_id=? and comid=? order by id desc";
			List<Object> values = new ArrayList<Object>();
			values.add(worksite_id);
			values.add(comId);
			List<Map<String,Object>> list = null;
			list = daService.getAllMap(sql, values);
			if(uid.longValue()!=-1){
				long endtime = System.currentTimeMillis()/1000;
				
				List<Map<String, Object>> ortherUids = daService.getAll("select uid from parkuser_work_record_tb where " +
						" worksite_id = ? and end_time is null and uid!=?", new Object[] { worksite_id, uid });
				if(ortherUids!=null&&!ortherUids.isEmpty()){
					for(Map<String, Object> map: ortherUids){
						Long ouid = (Long)map.get("uid");
						logger.error("collectorlogin>>>>>:强制下班uid:"+ouid);
						doDeleteToken(ouid,comId);
					}
					int r = daService.update("update parkuser_work_record_tb set end_time=? where worksite_id = ? and end_time is null and uid!=?",
							new Object[] { endtime, worksite_id, uid });
					logger.error("collectorlogin>>>>>:强制下班worksiteid:"+worksite_id+",r:"+r);
				}
				
				//Map map = daService.getPojo("select * from parkuser_work_record_tb where worksite_id = ? and end_time is null", new Object[]{worksite_id});
				Map user = daService.getPojo("select * from parkuser_work_record_tb where uid = ?  and end_time is null", new Object[]{uid});
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:00");
//				String d = sdf.format(System.currentTimeMillis());
//				long endtime = sdf.parse(d).getTime()/1000;
				if(user!=null){
					daService.update("update parkuser_work_record_tb set end_time=? where id = ?",
							new Object[]{endtime,(Long)user.get("id")});
					logger.error((Long)user.get("uid")+"切换工作站，旧工作站下班...");
				}
		
//				if(map!=null){
//					daService.update("update parkuser_work_record_tb set end_time=? where id = ?",
//							new Object[]{endtime,(Long)map.get("id")});
//					logger.error((Long)map.get("uid")+"被强制下班...");
//				}
				
				daService.update("insert into parkuser_work_record_tb(start_time,worksite_id,uid) values(?,?,?)",
						new Object[]{endtime,worksite_id,uid});
				logger.error(uid+"在新工作站上班...");
			}
			String result = StringUtils.createJson(list);
			result.replace("null", "-");
			AjaxUtil.ajaxOutput(response, result);
			//http://118.192.88.90:8080/zld/worksiteinfo.do?worksite_id=3&action=querypass&comid=3
		}else if(action.equals("getbrake")){
			Long passid = RequestUtil.getLong(request, "passid", -1L);
			String sql = "select * from com_brake_tb where passid=?";
			Map<String, Object> map = new HashMap<String, Object>();
			map = daService.getMap(sql, new Object[]{passid});
			String result = StringUtils.createJson(map);
			result.replace("null", "-");
			AjaxUtil.ajaxOutput(response, result);
			//http://192.168.199.239/zld/worksiteinfo.do?action=getbrake&passid=1&comid=3
		}else if(action.equals("querycamera")){
			Long passid = RequestUtil.getLong(request, "passid", -1L);
			String sql = "select c.*,p.passtype from com_camera_tb c,com_pass_tb p where c.passid=p.id and passid=?";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{passid});
			String result = StringUtils.createJson(list);
			result.replace("null", "-");
			AjaxUtil.ajaxOutput(response, result);
			//http://192.168.199.239/zld/worksiteinfo.do?action=querycamera&passid=1&comid=3
		}else if(action.equals("queryled")){
			Long passid = RequestUtil.getLong(request, "passid", -1L);
			String sql = "select l.*,p.passtype from com_led_tb l,com_pass_tb p where l.passid=p.id and passid=?";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{passid});
			String result = StringUtils.createJson(list);
			result.replace("null", "-");
			AjaxUtil.ajaxOutput(response, result);
			//http://192.168.199.239/zld/worksiteinfo.do?action=queryled&passid=2&comid=1749
		}else if(action.equals("getpassinfo")){
			Long worksite_id = RequestUtil.getLong(request, "worksite_id", -1L);
			if(worksite_id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			Map<String, Object> worksiteMap = pgOnlyReadService.getMap(
					"select * from com_worksite_tb where id=? ",
					new Object[] { worksite_id });
			if(worksiteMap == null){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Map<String, Object>> passList = new ArrayList<Map<String,Object>>();
			passList = pgOnlyReadService.getAll(
					"select * from com_pass_tb where worksite_id=? ",
					new Object[] { worksite_id });
			for(Map<String, Object> map : passList){
				Long passid = (Long)map.get("id");
				List<Map<String, Object>> cameraList = new ArrayList<Map<String,Object>>();
				List<Map<String, Object>> ledList = new ArrayList<Map<String,Object>>();
				cameraList = pgOnlyReadService.getAll(
						"select * from com_camera_tb where passid=? ",
						new Object[] { passid });
				ledList = pgOnlyReadService.getAll(
						"select * from com_led_tb where passid=? ",
						new Object[] { passid });
				map.put("cameras",toJsonArray(cameraList));
				map.put("leds",toJsonArray(ledList));
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson2(passList));
			//http://192.168.199.239/zld/worksiteinfo.do?action=getpassinfo&worksite_id=4&comid=1749
		}else if ("testHelpPage".equals(action)) {
			return mapping.findForward("list");
			//http://192.168.199.152/zld/worksiteinfo.do?action=testHelpPage
		}else if ("testHelp".equals(action)) {
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			int r = 0;
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			if(orderid!=-1L){
				String s = "";
				ArrayList list = new ArrayList();
				if(StringUtils.isNotNull(btime)){
					Long start = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(btime);
					s = " create_time=?";
					list.add(start);
				}
//				if(StringUtils.isNotNull(etime)){
//					Long end = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime);
//					if(s.endsWith(",")){
//						s = " create_time=? ,end_time = ? ";
//					}else{
//						s = " end_time = ? ";
//					}
//					list.add(end);
//				}
				list.add(orderid);
				r = daService.update("update order_tb set "+s+" where id = ?", list);
				
			}
			AjaxUtil.ajaxOutput(response, r+"");
			//http://192.168.199.152/zld/worksiteinfo.do?action=testHelpPage
		}
		return null;
	}
	
	private JSONArray toJsonArray(List<Map<String, Object>> list){
		JSONArray jsonArray = new JSONArray();
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				JSONObject jsonObject = new JSONObject();
				for(String key : map.keySet()){
					jsonObject.put(key, map.get(key));
				}
				jsonArray.add(jsonObject);
			}
		}
		return jsonArray;
	}
	
	private void setWorksiteType(List<Map<String, Object>> list){
		String sql = "select count(id) total,passtype from com_pass_tb where worksite_id=? group by passtype order by passtype desc ";
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				Integer worksite_type = -1;
				Long id = (Long)map.get("id");
				List<Map<String, Object>> passList = pgOnlyReadService.getAll(sql, new Object[]{id});
				if(passList != null && !passList.isEmpty()){
					for(Map<String, Object> map2 : passList){
						Integer passtype = Integer.valueOf((String)map2.get("passtype"));
						if(passtype == 2){//出入口
							worksite_type = 2;
							break;
						}else if(passtype == 1){//出口
							worksite_type = 1;
						}else if(passtype == 0){//入口
							if(worksite_type == 1){
								worksite_type = 2;
								break;
							}else{
								worksite_type = 0;
							}
						}
					}
				}
				map.put("worksite_type", worksite_type);
			}
		}
		
	}
	public void uploadWork(Long id ){
		 Map map = daService.getMap("select * from parkuser_work_record_tb where id = ?",new Object[]{id});
		 String ret = null;
		 if(map!=null&&map.size()>0){
			 String work = StringUtils.createJson(map);
	       	 HttpProxy httpProxy = new HttpProxy();
	       	 Map parammap = new HashMap();
	       	 parammap.put("work", work);
	       	 try {
	       		String token = null;
     			Map session = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
     			if(session!=null&&session.get("token")!=null){
     				token = session.get("token")+"";
     			}
     			 parammap.put("token", token);
	       		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadWork2Line", parammap);
	       		 
					if(ret!=null){
						if(ret.startsWith("1")){
							int r = daService.update("update parkuser_work_record_tb set sync_state=?,line_id=? where id = ?", new Object[]{1,Long.parseLong(ret.split("_")[2]+""),id});
						}else{
							int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
						}
					}else{
						int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
					}
				} catch (Exception e) {
					int r = daService.update("update parkuser_work_record_tb set sync_state=? where id = ?", new Object[]{0,id});
					e.printStackTrace();
				}
				System.out.println(ret);
			 }
	}
	
	//删除工作上其它收费员的TOKEN
		private void doDeleteToken(Long uin,Long comId) {
			String token = "zldtokenvoid"+System.currentTimeMillis();
			daService.update("update user_session_tb set token=? ,create_time=?,comid=? where uin=? ", 
					new Object[]{token,System.currentTimeMillis()/1000,comId,uin});
			logger.error("collectorlogin>>>>>:强制下班，更新TOKEN，uid:"+uin);
		}
}

package com.zld.struts.admin;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
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
import com.zld.impl.MongoDbUtils;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SendMessage;
import com.zld.utils.SqlInfo;
import com.zld.utils.TimeTools;
/**
 * 收费员管理，在总管理员后台
 * @author Administrator
 *
 */
public class CollectorManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private MongoDbUtils mongoDbUtils;
	@Autowired
	private LogService logService;
	private Logger logger = Logger.getLogger(CollectorManageAction.class);

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
		Map userMap = (Map) request.getSession().getAttribute("userinfo");
		//登录者ID
		Long ownerId = (Long)userMap.get("id");
		if(action.equals("")){
			return mapping.findForward("list");
		}else if(action.equals("quickquery")){
			String sql = "select t.*,c.company_name comname,r.role_name from user_info_tb t left join com_info_tb c on t.comid=c.id left join user_role_tb r on t.auth_flag=r.roleid where (t.auth_flag=? or t.auth_flag=?) and comid>0 order by t.reg_time desc ";
			String countSql = "select count(*) from user_info_tb  where (auth_flag=? or auth_flag=?) and comid>0 ";
			Long count = daService.getLong(countSql,new Object[]{2,1});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			List<Object> params = new ArrayList<Object>();
			params.add(2);
			params.add(1);
			if(count>0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			String sql = "select t.*,c.company_name comname,r.role_name from user_info_tb t left join com_info_tb c on t.comid=c.id left join user_role_tb r on t.auth_flag=r.roleid where (t.auth_flag=? or t.auth_flag=?) and comid>0 ";
			String countSql = "select count(t.*) from user_info_tb t where (t.auth_flag=? or t.auth_flag=?) and comid>0 " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo base = new SqlInfo("1=1", new Object[]{2,1});
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"user_info","t",new String[]{});
			Integer recommend = RequestUtil.getInteger(request, "recommend_start", -1);
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
			//推荐审核通过的
			if(recommend != -1){
				sql += " and collector_auditor is not null ";
				countSql += " and collector_auditor is not null ";
			}
			sql += " order by t.reg_time desc";
			//System.out.println(sqlInfo);
			Long count= daService.getLong(countSql, values);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("edit")){
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String strid =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "strid"));
			String phone =RequestUtil.processParams(request, "phone");
			String mobile =RequestUtil.processParams(request, "mobile");
			String id =RequestUtil.processParams(request, "id");
			String sql = "update user_info_tb set nickname=?,strid=?,phone=?,mobile=? where id=?";
			Object [] values = new Object[]{nickname,strid,phone,mobile,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Integer state = RequestUtil.getInteger(request, "state", 0);
			if(state==0)//0可用，1禁用，为0时是要改为禁用，为1时是要改为禁用，在这里反转 一下。
				state=1;
			else if(state==1)
				state=0;
			String sql = "update user_info_tb set state=? where id =?";
			Object [] values = new Object[]{state,Long.valueOf(id)};
			int result = daService.update(sql, values);
			if(result==1&&state==1)
				logService.updateSysLog(comid,request.getSession().getAttribute("loginuin")+"","禁用了停车场人员,编号："+id, 204);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delcollector")){
			Long id = RequestUtil.getLong(request, "selids", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
			}
			int result = daService.update("delete from user_info_tb where id=? ", new Object[]{id});
			AjaxUtil.ajaxOutput(response, result + "");
		}else if(action.equals("cominfo")){
			Integer uin = RequestUtil.getInteger(request, "id", -1);
			if(uin!=-1){
				Map userInfo = daService.getPojo("select comid from user_info_Tb where id=?", new Object[]{uin});
				if(userInfo!=null&&userInfo.get("comid")!=null){
					Map cominfoMap =  daService.getPojo("select * from com_info_tb  where id=? ", new Object[]{userInfo.get("comid")});
					request.setAttribute("cominfo", cominfoMap);
				}
			}
			return mapping.findForward("cominfo");
			
		}else if(action.equals("sendmesg")){
			String ids =  AjaxUtil.decodeUTF8(RequestUtil.getString(request, "ids"));
			String message = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "message"));
			List<Object> params = new ArrayList<Object>();
			String cids []= ids.split(",");
			String paramssimp = "";
			for(String id : cids){
				paramssimp +=",?";
				params.add(Long.valueOf(id));
			}
			paramssimp = paramssimp.substring(1);
			List<Map<String, Object>> list = daService.getAllMap("select mobile from user_info_tb where id in("+paramssimp+")",params);
			String mobiles="13860132164,15375242041,18510341966";//群发手机号，最多100个，以空格隔开
			int i=0;
			if(list!=null&!list.isEmpty()){
				for(Map<String, Object> map :list){
					mobiles += ","+map.get("mobile");
					if(i>1000)
						break;
				}
			}
			new SendMessage().sendMultiMessage(mobiles, message+"【停车宝】");
			AjaxUtil.ajaxOutput(response, "1");
		}else if(action.equals("withdraw")){
			String sql = "select t.*,c.company_name comname,r.role_name from user_info_tb t left join com_info_tb c on t.comid=c.id left join user_role_tb r on t.auth_flag=r.roleid where (t.auth_flag=? or t.auth_flag=?) and comid>0 ";
			String countSql = "select count(*) from user_info_tb  where (auth_flag=? or auth_flag=?) and comid>0 ";
			Long count = daService.getLong(countSql,new Object[]{2,1});
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			List<Object> params = new ArrayList<Object>();
			params.add(2);
			params.add(1);
			if(count>0){
				list = daService.getAllMap(sql, params);
				list = setList(list);
				Collections.sort(list, new ListSort());
			}
			int count1 = list!=null?list.size():0;
			String json = JsonUtil.Map2Json(list,pageNum,count1, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
		}else if(action.equals("validateuser")){//等审核停车员
			return mapping.findForward("vusers");
		}else if(action.equals("vquery")){
			String sql = "select * from user_info_tb where (state=? or state=? or state=? or state=?) and (auth_flag=? or auth_flag=?) ";
			String countSql = "select count(ID) from user_info_tb where (state=? or state=? or state=? or state=?) and (auth_flag=? or auth_flag=?) " ;
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"user_info");
			List<Object> params = new ArrayList<Object>();
			params.add(2);//新增
			params.add(3);//待补充
			params.add(4);//待跟进
			params.add(5);//无价值
			params.add(1);
			params.add(2);
			if(sqlInfo!=null){
				countSql+=" and "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				params.addAll(sqlInfo.getParams());
			}
			sql += " order by id desc";
			//System.out.println(sqlInfo);
			Long count= daService.getCount(countSql, params);
			List list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("vuser")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			request.setAttribute("uin", id);
			//取审核图片
			List<String> files = new ArrayList<String>();
			try {
				files = mongoDbUtils.getParkPicUrls(id);
			} catch (Exception e) {
				// TODO: handle exception
				logger.info("获取审核图片异常", e);
				AjaxUtil.ajaxOutput(response, "-1");
			}
			System.err.println(files);
			request.setAttribute("pics", files);
			
			String nickname = "";
			Integer ustate = 2;
			Map<String, Object> map = daService.getMap("select * from user_info_tb where id=? ", new Object[]{id});
			if(map != null){
				if(map.get("nickname") != null){
					nickname = (String)map.get("nickname");
				}
				if(map.get("mobile") != null){
					request.setAttribute("umobile", map.get("mobile"));
				}
				ustate = (Integer)map.get("state");
			}
			//取车场信息
			Map<String, Object> comMap = daService.getPojo("select c.* from com_info_tb c,user_info_tb u where u.comid=c.id and u.id=?",new Object[]{id});
			StringBuffer comBuffer = new StringBuffer("[");
			if(comMap != null){
				Long comId = (Long)comMap.get("id");
				if(comId == 1){
					Long newComId = daService.getLong("SELECT nextval('seq_com_info_tb'::REGCLASS) AS newid",null);
					//默认车场
					for (String  key : comMap.keySet()) {
						if(key.equals("id")){//先把comid传过去，页面的原因
							comBuffer.append("{\"name\":\""+key+"\",\"value\":\""+newComId+"\"},");
						}else{
							comBuffer.append("{\"name\":\""+key+"\",\"value\":\"\"},");
						}
					}
				}else{
					for (String  key : comMap.keySet()) {
						Object value = null;
						if(comMap.get(key) == null){
							value = "";
						}else{
							value = comMap.get(key);
						}
						comBuffer.append("{\"name\":\""+key+"\",\"value\":\""+value+"\"},");
					}
				}
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			//车场已存在时用于填写已存在的车场ID
			comBuffer.append("{\"name\":\"comid\",\"value\":\"\"},");
			//添加收费员姓名
			comBuffer.append("{\"name\":\"nickname\",\"value\":\""+nickname+"\"},");
			//添加审核字段,默认未审核
			comBuffer.append("{\"name\":\"ustate\",\"value\":\""+ustate+"\"},");
			//收费员备注
			comBuffer.append("{\"name\":\"visit_content\",\"value\":\"\"},");
			
			String result = comBuffer.toString();
			result = result.substring(0,result.length()-1)+"]";
			request.setAttribute("cominfo", result);
			return mapping.findForward("vuser");
		}else if(action.equals("getpic")){
			String fname = RequestUtil.getString(request, "id");
			String dbName = RequestUtil.getString(request, "db");
			if(dbName.equals(""))
				dbName = "parkuser_pics";
			byte [] content = mongoDbUtils.getParkPic(fname,dbName);
			response.setDateHeader("Expires", System.currentTimeMillis()+4*60*60*1000);
			//response.setStatus(httpc);
			Calendar c = Calendar.getInstance();
			c.set(1970, 1, 1, 1, 1, 1);
			response.setHeader("Last-Modified", c.getTime().toString());
			response.setContentLength(content.length);
			response.setContentType("image/jpeg");
			System.err.println(content.length);
		    OutputStream o = response.getOutputStream();
		    o.write(content);
		    o.flush();
		    o.close();
		    response.flushBuffer();
		}else if(action.equals("checkcollector")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			if(uin == -1){
				AjaxUtil.ajaxOutput(response, "-1");
			}
			//已存在车场的id
			Long comId = RequestUtil.getLong(request, "comid", -1L);
			//车场不存在，创建车场
			Long id =RequestUtil.getLong(request, "id", -1L);
			String company_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
			Double longitude =RequestUtil.getDouble(request, "longitude",0d);
			Double latitude =RequestUtil.getDouble(request, "latitude",0d);
			Integer parking_total = RequestUtil.getInteger(request, "parking_total", 0);
			String mcompany =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "mcompany"));
			Integer stop_type = RequestUtil.getInteger(request, "stop_type", 0);
			String record_number = RequestUtil.processParams(request, "record_number");
			Integer epay = RequestUtil.getInteger(request, "epay", 0);
			
			//备注
			String visit_content = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "visit_content"));
			//市场专员
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			
			//审核状态,默认未审核
			Integer ustate = RequestUtil.getInteger(request, "ustate", 2);
			if(company_name.equals("")) company_name = null;
			if(nickname.equals("")) nickname = null;
			if(address.equals("")) address = null;
			if(mcompany.equals("")) mcompany = null;
			if(record_number.equals("")) record_number = null;
			//被推荐的收费员信息
			Map<String, Object> nusermap = new HashMap<String, Object>();
			nusermap = daService.getMap("select * from user_info_tb where id=?", new Object[]{uin});
			if(nusermap != null){
				Integer state = (Integer)nusermap.get("state");
				//已审核
				if(state == 0){
					AjaxUtil.ajaxOutput(response, "-2");
				}
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
			}
			//state=5无价值，不创建车场，不返现
			if(ustate == 5){
				int result = daService.update("update user_info_tb set state=? where id=?", new Object[]{5,uin});
				if(!visit_content.equals("") && result == 1){
					result = daService.update("insert into visit_info_tb(uid,contacts,state,create_time,visit_content) values(?,?,?,?,?)", new Object[]{uid,uin,0,System.currentTimeMillis()/1000,visit_content});
				}
				AjaxUtil.ajaxOutput(response, result + "");
				return null;
			}
			
			List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
			if(comId != -1){
				//车场已存在，把收费员加入已存在的车场
				logger.info ("车场已存在，加入已存在的车场并返现...");
				//填写人员信息
				Map<String, Object> nusersqlMap = new HashMap<String, Object>();
				nusersqlMap.put("sql", "update user_info_tb set comid=?,nickname=?,state=?,auth_flag=?,collector_auditor=? where id=? ");
				nusersqlMap.put("values", new Object[]{comId,nickname,0,2,ownerId,uin});
				sqlMaps.add(nusersqlMap);
			}else if(id != -1){
				Long ncomid = (Long)nusermap.get("comid");
				if(ncomid == 1){//待审核，未保存过
					Long count = daService.getLong("select count(*) from com_info_tb where longitude=? and latitude=?",
							new Object[]{longitude,latitude});
					if(count > 0){//经纬度重复了
						AjaxUtil.ajaxOutput(response, "-3");
						return null;
					}
					//车场审核状态
					Integer cstate = 2;
					if(ustate == 0){
						cstate = 0;
					}
					Map<String, Object> comsqlMap = new HashMap<String, Object>();
					comsqlMap.put("sql", "insert into com_info_tb(id,company_name,address,create_time,parking_total,longitude,latitude,stop_type,record_number,mcompany,epay,uid,state) values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
					comsqlMap.put("values", new Object[]{id,company_name,address,System.currentTimeMillis()/1000,parking_total,longitude,latitude,stop_type,record_number,mcompany,epay,uid,cstate});//com_info_tb :state 0可用，2待审核
					sqlMaps.add(comsqlMap);
					
					//填写人员信息
					Map<String, Object> nusersqlMap = new HashMap<String, Object>();
					nusersqlMap.put("sql", "update user_info_tb set comid=?,nickname=?,state=?,collector_auditor=?,auth_flag=? where id=? ");
					nusersqlMap.put("values", new Object[]{id,nickname,ustate,ownerId,2,uin});
					sqlMaps.add(nusersqlMap);
				}else{//待审核，保存过（创建过车场）
					//车场审核状态
					Integer cstate = 2;
					if(ustate == 0){
						cstate = 0;
					}
					Long count = daService.getLong("select count(*) from com_info_tb where longitude=? and latitude=? and id<>? ",
							new Object[]{longitude,latitude,id});
					if(count > 0){//经纬度重复了
						AjaxUtil.ajaxOutput(response, "-3");
						return null;
					}
					
					Map<String, Object> comsqlMap = new HashMap<String, Object>();
					comsqlMap.put("sql", "update com_info_tb set company_name=?,address=?,parking_total=?,longitude=?,latitude=?,stop_type=?,record_number=?,mcompany=?,epay=?,uid=?,state=? where id=?");
					comsqlMap.put("values", new Object[]{company_name,address,parking_total,longitude,latitude,stop_type,record_number,mcompany,epay,uid,cstate,id});//com_info_tb :state 0可用，2待审核
					sqlMaps.add(comsqlMap);
					
					//填写人员信息
					Map<String, Object> nusersqlMap = new HashMap<String, Object>();
					nusersqlMap.put("sql", "update user_info_tb set nickname=?,state=?,collector_auditor=? where id=? ");
					nusersqlMap.put("values", new Object[]{nickname,ustate,ownerId,uin});
					sqlMaps.add(nusersqlMap);
				}
				if(!visit_content.equals("")){
					//写备注记录
					Map<String, Object> visitsqlMap = new HashMap<String, Object>();
					visitsqlMap.put("sql", "insert into visit_info_tb(uid,contacts,state,create_time,visit_content) values(?,?,?,?,?)");
					visitsqlMap.put("values", new Object[]{uid,uin,0,System.currentTimeMillis()/1000,visit_content});
					sqlMaps.add(visitsqlMap);
				}
			}
			String pmsg = "";
			String pmobile = "";
			String nmobile = "";
			String nmsg = "恭喜您"+nickname+"，您已经通过审核，您的账号是："+uin+"，密码是:"+nusermap.get("password")+"【停车宝】";
			if(nusermap.get("mobile") != null){
				nmobile = (String)nusermap.get("mobile");
			}
			//审核通过，给推荐人返现
			if(nusermap.get("recom_code") != null && (comId != -1 || (comId == -1 && ustate == 0))){
				Long pid = (Long)nusermap.get("recom_code");
				Map<String, Object> pusermap = daService.getMap("select * from user_info_tb where id=?", new Object[]{pid});
				if(pusermap != null){
					Double newbalance = Double.valueOf(pusermap.get("balance") + "");
					Double recharge = 0.00d;
					Long auth_flag = (Long)pusermap.get("auth_flag");
					if(auth_flag == 4){
						//车主推荐的收费员，返剩余的25元
						recharge = 25.00d;
						newbalance += recharge;
						//车主账户明细
						Map<String, Object> puserAccountsqlMap = new HashMap<String, Object>();
						puserAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
						puserAccountsqlMap.put("values", new Object[]{pid,recharge,0,System.currentTimeMillis()/1000,"推荐的收费员审核通过，返现",8});
						sqlMaps.add(puserAccountsqlMap);
						
						Map<String, Object> puserMessagelMap = new HashMap<String, Object>();
						puserMessagelMap.put("sql", "insert into user_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)");
						puserMessagelMap.put("values", new Object[]{6,System.currentTimeMillis()/1000,pid,"推荐提醒","您推荐的收费员"+nickname+"成功通过审核，您获得25元奖励。"});
						sqlMaps.add(puserMessagelMap);
						
						
					}else if(auth_flag == 1 || auth_flag == 2){
						//车场推荐的收费员，返30元
						recharge = 30.00d;
						newbalance += recharge;
						//收费员账户明细
						Map<String, Object> puserAccountsqlMap = new HashMap<String, Object>();
						puserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
						puserAccountsqlMap.put("values", new Object[]{pid,recharge,0,System.currentTimeMillis()/1000,"推荐的收费员审核通过，返现",3});
						sqlMaps.add(puserAccountsqlMap);
						
						Map<String, Object> puserMessagelMap = new HashMap<String, Object>();
						puserMessagelMap.put("sql", "insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)");
						puserMessagelMap.put("values", new Object[]{6,System.currentTimeMillis()/1000,pid,"推荐提醒","您推荐的收费员"+nickname+"成功通过审核，您获得30元奖励。"});
						sqlMaps.add(puserMessagelMap);
						
					}
					
					Map<String, Object> recomsqlMap = new HashMap<String, Object>();
					recomsqlMap.put("sql", "update recommend_tb set state=? where pid=? and nid=? ");
					recomsqlMap.put("values", new Object[]{1,pid,uin});
					sqlMaps.add(recomsqlMap);
					
					Map<String, Object> pusersqlMap = new HashMap<String, Object>();
					pusersqlMap.put("sql", "update user_info_tb set balance=? where id=? ");
					pusersqlMap.put("values", new Object[]{newbalance,pid});
					sqlMaps.add(pusersqlMap);
					pmsg = "停车宝小伙伴您好，您推荐的收费员"+nickname+"已审核通过，"+recharge+"元已到帐【停车宝】";
					if(pusermap.get("mobile") != null){
						pmobile = (String)pusermap.get("mobile");
					}
				}
			}
			boolean b = daService.bathUpdate(sqlMaps);
			if(b){
				AjaxUtil.ajaxOutput(response, "1");
				if(comId != -1 || (comId == -1 && ustate == 0)){//审核通过发短信
					if(!nmobile.equals("")){
						SendMessage.sendMessage(nmobile, nmsg);
					}
					if(!pmobile.equals("") && !pmsg.equals("")){
						SendMessage.sendMessage(pmobile, pmsg);
					}
				}
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
			}
		}else if(action.equals("getremarks")){
			Long uin = RequestUtil.getLong(request, "id", -1L);
			request.setAttribute("uin", uin);
			return mapping.findForward("remarks");
		}else if(action.equals("remarks")){
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			if(uin == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			String sql = "select * from visit_info_tb where contacts=? order by create_time desc ";
			String countsql = "select count(*) from visit_info_tb where contacts=? ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List<Object> params = new ArrayList<Object>();
			params.add(uin);
			Long count = daService.getCount(countsql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("useraccount")){
			List<Map<String, Object>> list = daService.getAll("select ca.id,ca.uin,ca.ctime,ca.utime,ca.state,ca.pic_name," +
					"ui.nickname,ci.company_name,ca.auditor from collector_account_pic_tb ca,user_info_tb ui,com_info_tb ci " +
					"where ca.uin=ui.id and ca.comid=ci.id  order by ca.state,ca.id desc",new Object[]{});
			String result = "{\"page\":1,\"total\":0,\"rows\": []}";
			if(list!=null&&list.size()>0){
				result = "{\"page\":1,\"total\":"+list.size()+",\"rows\": [datas]}";
				String datas = "";
				for(Map<String, Object> map : list){
					Long ctime = (Long)map.get("ctime");
					String cdate =TimeTools.getTime_yyyyMMdd_HHmmss(ctime*1000);
					Long utime = (Long)map.get("utime");
					String udate = "";
					if(utime!=null&&utime>0)
						udate = TimeTools.getTime_yyyyMMdd_HHmmss(utime*1000);
					if(datas.length()>1)
						datas+=",";
					datas +="{\"id\":"+map.get("id")+",\"cell\":[\""+map.get("id")+"\",\""+map.get("company_name")+"\",\""+map.get("nickname")+"\",\""+map.get("uin")+"\",\""+map.get("state")+"\",\""+cdate+"\",\""+udate+"\",\""+map.get("pic_name")+"\",\""+map.get("auditor")+"\"]}";
				}
				result = result.replace("datas", datas);
			}
			request.setAttribute("datalist", result);
			return mapping.findForward("useraccount");
		}else if(action.equals("adduseracc")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Map<String, Object> pucMap = daService.getMap("select uin,pic_name from collector_account_pic_tb where id =? ", new Object[]{id});
			Map<String, Object> puserMap = daService.getMap("select * from com_account_tb where uin=? and type=? ", new Object[]{pucMap.get("uin"),1});
			if(puserMap!=null){
				request.setAttribute("name", puserMap.get("name"));
				request.setAttribute("card_number", puserMap.get("card_number"));
				request.setAttribute("bank_name",puserMap.get("bank_name"));
				request.setAttribute("area", puserMap.get("area"));
				request.setAttribute("bank_pint", puserMap.get("bank_pint"));
				request.setAttribute("user_id",puserMap.get("user_id"));
			}else {
				puserMap = daService.getMap("select nickname from user_info_tb where id =? ", new Object[]{pucMap.get("uin")});
				if(puserMap!=null)
					request.setAttribute("name", puserMap.get("nickname"));
			}
			request.setAttribute("picurl", pucMap.get("pic_name"));
			request.setAttribute("uin", pucMap.get("uin"));
			request.setAttribute("id",id);
			return mapping.findForward("adduseraccount");
		}else if(action.equals("editpuseracc")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Integer state = RequestUtil.getInteger(request, "state", -1);
			int ret = 0;
			if(id!=-1&&state!=-1){
				ret = daService.update("update collector_account_pic_tb set state =? where id =? ", new Object[]{state,id});
			}
			logger.info("编辑账户结果 ："+ret);
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("editquota")){
			String id =RequestUtil.processParams(request, "id");
			Double firstorderquota =RequestUtil.getDouble(request, "firstorderquota",8d);
			Double rewardquota =RequestUtil.getDouble(request, "rewardquota",2d);
			Double recommendquota =RequestUtil.getDouble(request, "recommendquota",5d);
			Double ticketquota =RequestUtil.getDouble(request, "ticketquota",-1d);
			String sql = "update user_info_tb set firstorderquota=?,rewardquota=?,recommendquota=?,ticketquota=? where id=?";
			Object [] values = new Object[]{firstorderquota,rewardquota,recommendquota,ticketquota,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
	}
	
	//提现过的收费员
	private List<Map<String, Object>> setList(List<Map<String, Object>> list){
		List<Map<String, Object>> list2 = new ArrayList<Map<String,Object>>();
		List<Object> uins = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				uins.add(map.get("id"));
			}
		}
		if(!uins.isEmpty()){
			String preParams  ="";
			for(Object uin : uins){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select uin,count(*) total from withdrawer_tb where state=3 and uin in ("+preParams+") group by uin", uins);
			if(!resultList.isEmpty()){
				for(Map<String, Object> map : resultList){
					Long uin = (Long)map.get("uin");
					Long total = (Long)map.get("total");
					if(total.intValue() > 0){
						for(Map<String, Object> map3 : list){
							Long id = (Long)map3.get("id");
							if(uin.intValue() == id.intValue()){
								list2.add(map3);
								break;
							}
						}
					}
				}
			}
		}
		return list2;
	}
	
	class ListSort implements Comparator<Map<String, Object>>{

		public int compare(Map<String, Object> o1, Map<String, Object> o2) {
			// TODO Auto-generated method stub
			Long b1 = (Long)o1.get("reg_time");
			if(b1 == null) b1 = 0L;
			Long b2 = (Long)o2.get("reg_time");
			if(b2 == null) b2 = 0L;
			return b2.compareTo(b1);
		}
		
	}
}
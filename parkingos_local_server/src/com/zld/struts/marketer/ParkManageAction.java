package com.zld.struts.marketer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.Check;
import com.zld.utils.GetLocalCode;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
public class ParkManageAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	@Autowired
	private PgOnlyReadService pgOnlyReadService;
	
	@Autowired
	private PublicMethods publicMethods;
	
	@Autowired
	private LogService logService;
	private Logger logger = Logger.getLogger(ParkManageAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception{
		String action = RequestUtil.processParams(request, "action");
		String token =RequestUtil.processParams(request, "token");
		Integer state = RequestUtil.getInteger(request, "state", -1);
		Long comId = RequestUtil.getLong(request, "comid", -1L);
		Map<String,Object> infoMap  = new HashMap<String, Object>();
		if(token==null||"null".equals(token)||"".equals(token)){
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
		}
		Long uid = validToken(token);
		if(uid == null){
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			return null;
		}
		if(action.equals("create")){
			Double longitude =RequestUtil.getDouble(request, "longitude",0d);
			Double latitude =RequestUtil.getDouble(request, "latitude",0d);
			Long count = daService.getLong("select count(*) from com_info_tb where longitude=? and latitude=?",
					new Object[]{longitude,latitude});
			if(count>0){//经纬度重复了
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			Integer result = createAdmin(request,uid);
			String log = "拜访达人客户端新建了停车场,"+result;
			if(result == 1){
				AjaxUtil.ajaxOutput(response, "1");
				logService.updateSysLog(comId, uid.toString(),log, 100);
			}else {
				AjaxUtil.ajaxOutput(response, "{\"info\":\"fail\"}");
			}
			
			//http://192.168.199.239/zld/parkmanage.do?action=create&token=e6c435a27cf1f4a11d11c56d0cebc614&company_name="我的测试停车场"&longitude=116.316884&latitude=39.990120
		}else if(action.equals("edit")){//客户端修改	
			String company =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
			String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
			String phone =RequestUtil.processParams(request, "phone");
			//String mobile =RequestUtil.processParams(request, "mobile");
			String id =RequestUtil.processParams(request, "id");
			Integer isfixed = RequestUtil.getInteger(request, "isfixed", 0);
			Integer isview = RequestUtil.getInteger(request, "isview", -1);
			Integer stop_type = RequestUtil.getInteger(request, "stop_type", 0);
			Integer parking_type = RequestUtil.getInteger(request, "parking_type", 0);
			Integer parking_total = RequestUtil.getInteger(request, "parking_total", 0);
			Double longitude =RequestUtil.getDouble(request, "longitude",0.0);
			Double latitude =RequestUtil.getDouble(request, "latitude",0.0);
			Integer city = RequestUtil.getInteger(request, "city", 0);
			String resume = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "resume"));
			String remarks = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "remarks"));
			String mcompany =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "mcompany"));//经营公司
			String record_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "record_number"));//备案号
			Integer activity = RequestUtil.getInteger(request, "activity", 0);//车场活动：0 没有活动 1申请活动 2:申请通过
			String activity_content = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "activity_content"));//活动内容
			if(activity_content.equals("")) activity_content = null;
			//share_number = getShareNumber(Long.valueOf(id), share_number);
			if(state==-1)
				state=0;
			if(!Check.checkPhone(phone) && !Check.checkMobile(phone)) phone = null;
			//检查经纬度
			Long count = daService.getLong("select count(*) from com_info_tb where longitude=? and latitude=? and id<>? ",
					new Object[]{longitude,latitude,Long.valueOf(id)});
			if(count > 0){//经纬度重复了
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			//备注字段有可能很长，直接在内容后面添加，因为初始值是null，必须做判断
			Long rcount = daService.getLong("select count(*) from com_info_tb where remarks is null and id=?", new Object[]{Long.valueOf(id)});
			String remarksString = "remarks=?";
			if(rcount == 0){
				remarksString = "remarks=remarks||?";
				if(!remarks.equals("")){
					remarks = "," + remarks;
				}
			}
			Object[] values = new Object[]{company,address,phone,parking_total,parking_type,stop_type,
					System.currentTimeMillis()/1000,longitude,latitude,resume,remarks,mcompany,record_number,isfixed,state,isview,activity,activity_content,city,Long.valueOf(id)};
			String sql = "update com_info_tb set company_name=?,address=?,phone=?,parking_total=?,parking_type=?,stop_type=?," +
					"update_time=? ,longitude=?,latitude=?,resume=?,"+remarksString+",mcompany=?,record_number=?,isfixed=?,state=?,isview=?,activity=?,activity_content=?,city=? where id=? ";
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
			String log = "客户端修改了停车场,车场编号："+id+"修改人："+uid;
			logService.updateSysLog(Long.valueOf(id), uid.toString(),log+"("+sql+",params:"+StringUtils.objArry2String(values)+")", 101);
			//http://192.168.199.239/zld/parkmanage.do?action=edit&id=694&token=68e6c58a77e37a866f81a7c9325247b3&company_name=中关村SOHO&longitude=116.316173&latitude=39.989740&resume=测试数据
		}else if(action.equals("delete")){
			String sql = "update com_info_tb set state=?,update_time=? where id =?";
			Object [] values = new Object[]{1,System.currentTimeMillis()/1000,Long.valueOf(comId)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
			logService.updateSysLog(comId, uid.toString(),"删除了停车场，编号："+comId, 102);
			//http://192.168.199.239/zld/parkmanage.do?action=delete&comid=1757&token=e6c435a27cf1f4a11d11c56d0cebc614
		}else if(action.equals("query")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			List<Object> params = new ArrayList<Object>();
			Integer epay = RequestUtil.getInteger(request, "epay", -1);
			Integer isfixed = RequestUtil.getInteger(request, "isfixed", -1);
			Long groupuid = RequestUtil.getLong(request, "uid", -1L);//组内成员
			String sql = "select id,company_name,state,create_time,parking_type,mcompany,record_number,isfixed,longitude,latitude,isview from com_info_tb where uid=? and state !=? ";
			String sqlcount = "select count(*) from com_info_tb where uid=? and state !=? ";
			params.add(groupuid);
			params.add(1);
			if(state != -1){
				sql += " and state=? ";
				sqlcount += " and state=? ";
				params.add(state);
			}
			if(epay != -1){
				sql += " and epay=? ";
				sqlcount += " and epay=? ";
				params.add(epay);
			}
			if(isfixed != -1){
				sql += " and isfixed=? ";
				sqlcount += " and isfixed=? ";
				params.add(isfixed);
			}
			sql += " order by create_time desc";
			Long count = daService.getCount(sqlcount, params);
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, params, pageNum, pageSize);
			Map<String,Object> infomap  = new HashMap<String, Object>();
			infomap.put("total", count);
			infomap.put("cell", StringUtils.createJson(list));
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infomap));
			//http://192.168.199.239/zld/parkmanage.do?action=query&token=e22d5ce5dbe8785565fe511074598495
		}else if(action.equals("quickquery")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			List<Object> params = new ArrayList<Object>();
			String company_name = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
			Integer epay = RequestUtil.getInteger(request, "epay", -1);
			Integer is_hasparker = RequestUtil.getInteger(request, "is_hasparker", -1);
			Integer isfixed = RequestUtil.getInteger(request, "isfixed", -1);
			Long biz_id = RequestUtil.getLong(request, "biz_id", -1L);
			Long groupuid = RequestUtil.getLong(request, "uid", -1L);//组内成员
			company_name = "%" + company_name + "%";
			String sql = "select id,company_name,state,create_time,parking_type,mcompany,record_number,isfixed,longitude,latitude,isview from com_info_tb where state!=? and company_name like ? ";
			params.add(1);
			params.add(company_name);
			if(epay != -1){
				sql += " and epay=? ";
				params.add(epay);
			}
			if(is_hasparker != -1){
				sql += " and is_hasparker=? ";
				params.add(is_hasparker);
			}
			if(isfixed != -1){
				sql += " and isfixed=? ";
				params.add(isfixed);
			}
			if(biz_id != -1){
				sql += " and biz_id=? ";
				params.add(biz_id);
			}
			if(groupuid == -1){
				List<Object> uidList = new ArrayList<Object>();
				uidList = publicMethods.getDataAuth(uid);
				String preParams  ="";
				if(!uidList.isEmpty()){
					for(Object o : uidList){
						if(preParams.equals(""))
							preParams ="?";
						else
							preParams += ",?";
					}
				}
				sql += " and uid in ("+preParams+") ";
				params.addAll(uidList);
			}else{
				sql += " and uid=? ";
				params.add(groupuid);
			}
			sql += " order by create_time desc ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, params, pageNum, pageSize);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			//http://192.168.199.239/zld/parkmanage.do?action=quickquery&token=e6c435a27cf1f4a11d11c56d0cebc614&company_name=交道口
		}else if(action.equals("detail")){
			String sql = "select * from com_info_tb where id=?";
			Map<String, Object> map = new HashMap<String, Object>();
			if(comId == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			map = daService.getMap(sql, new Object[]{comId});
			getOrderCount(map);
			//返车场图片
			Map<String, Object> picMap = pgOnlyReadService
					.getMap("select picurl from com_picturs_tb where comid=? order by create_time desc limit 1",
							new Object[] { comId });
			if(picMap != null){
				map.put("picurl", picMap.get("picurl"));
			} 
			Map<String, Object> codeMap = pgOnlyReadService
					.getMap("select code from qr_code_tb where comid=? and type=? and state=? order by ctime desc limit ? ",
							new Object[] { comId, 4, 0, 1 });
			if(codeMap != null && codeMap.get("code") != null){
				map.put("code", codeMap.get("code"));
			}
			//			if(map != null){
//				getParkPics(map);
//			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://192.168.199.239/zld/parkmanage.do?action=detail&token=a6fa297872e1bfb00c0b11f124cda122&comid=1477
		}else if(action.equals("addcontact")){
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String mobile =RequestUtil.processParams(request, "mobile");
			Long role =RequestUtil.getLong(request, "auth_flag", 2L);
			if(nickname.equals("")) nickname=null;
			if(mobile.equals("")) mobile=null;
			Long time = System.currentTimeMillis()/1000;
			String strid = System.currentTimeMillis()+"";
			String password = new Random().nextInt(100000)+"";
			if(!checkStrid(strid)){
				AjaxUtil.ajaxOutput(response, "{\"info\":\"fail\"}");
				return null;
			}
			String sqlcount = "select count(1) from user_info_tb where auth_flag=? and mobile=?";
			List<Object> params = new ArrayList<Object>();
			params.add(role);
			params.add(mobile);
			Long count = daService.getCount(sqlcount, params);
			if(count > 0){
				AjaxUtil.ajaxOutput(response, "-2");
				return null;
			}
			//用户表
			String sql="insert into user_info_tb (nickname,password,strid," +
					"reg_time,mobile,auth_flag,comid) " +
					"values (?,?,?,?,?,?,?)";
			Object [] values= new Object[]{nickname,password,strid,
					time,mobile,role,comId};
			int result = daService.update(sql, values);
			if(result==1)
				AjaxUtil.ajaxOutput(response, "1");
			else {
				AjaxUtil.ajaxOutput(response, "0");
			}
			logService.updateSysLog(comId, uid.toString(),"创建了停车员", 202);
			//http://192.168.199.239/zld/parkmanage.do?action=addcontact&token=e6c435a27cf1f4a11d11c56d0cebc614&comid=1477&strid="1416826942457"&nickname=王海祥&auth_flag=9&address=北京
		}else if(action.equals("editcontact")){
			String nickname =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "nickname"));
			String mobile =RequestUtil.processParams(request, "mobile");
			Integer isview = RequestUtil.getInteger(request, "isview", -1);
			Long id =RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			Long role =RequestUtil.getLong(request, "auth_flag", 2L);
			if(nickname.equals("")) nickname=null;
			if(mobile.equals("")) mobile=null;
			//检查是否有重复的
			String sqlcount = "select count(1) from user_info_tb where auth_flag=? and mobile=? and id!=?";
			Long count = daService.getLong(sqlcount, new Object[]{role,mobile,id});
			if(count > 0){
				AjaxUtil.ajaxOutput(response, "-2");
				return null;
			}
			String sql = "update user_info_tb set nickname=?,mobile=?,auth_flag=?,isview=? where id=? ";
			int result = daService.update(sql, new Object[]{nickname,mobile,role,isview,id});
			AjaxUtil.ajaxOutput(response, result+"");
			logService.updateSysLog(comId, uid.toString(),"修改了停车场人员,编号："+id, 203);
			//http://192.168.199.239/zld/parkmanage.do?action=editcontact&id=12583&token=e6c435a27cf1f4a11d11c56d0cebc614&comid=1477&strid="1416826942457"&nickname=王海祥&auth_flag=9&address=北京
		}else if(action.equals("deletecontact")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			if(id == -1){
				AjaxUtil.ajaxOutput(response, "{\"info\":\"fail\"}");
				return null;
			}
			String sql = "update user_info_tb set state=? where id=?";
			int result = daService.update(sql, new Object[]{1,id});
			AjaxUtil.ajaxOutput(response, result+"");
			logService.updateSysLog(comId, uid.toString(),"禁用了停车场人员,编号："+id, 204);
			//http://192.168.199.239/zld/parkmanage.do?action=deletecontact&id=12583&token=e6c435a27cf1f4a11d11c56d0cebc614
		}else if(action.equals("querycontact")){
			Map comMap = daService.getMap("select isfixed from com_info_Tb where id = ? ", new Object[]{comId});
			Integer isFixed =(Integer) comMap.get("isfixed");
			String sql = "select id,nickname,auth_flag,mobile from user_info_tb where comid=? and state=? order by reg_time desc";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{comId,0});
			if(list!=null&&list.size()>0){
				for(Map<String, Object> map : list){
					map.put("isfix", isFixed);
				}
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			//http://192.168.199.240/zld/parkmanage.do?action=querycontact&comid=1477&token=e6c435a27cf1f4a11d11c56d0cebc614
		}else if(action.equals("contactdetail")){
			Long monday = TimeTools.getLongMilliSecondFrom_HHMMDD(StringUtils.getMondayOfThisWeek())/1000;
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long etime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
			Long id = RequestUtil.getLong(request, "id", -1L);
			String sql = "select u.*,c.company_name from user_info_tb u left join com_info_tb c on u.comid=c.id where u.id=?";
			Map<String, Object> map = new HashMap<String, Object>();
			map = daService.getMap(sql, new Object[]{id});
			sql = "select sum(lala_scroe)+sum(nfc_score)+sum(praise_scroe)+sum(pai_score)+sum(online_scroe)+sum(recom_scroe) score from collector_scroe_tb where uin=? and create_time between ? and ? ";
			Map<String, Object> curMap = new HashMap<String, Object>();
			//本周积分
			curMap = daService.getMap(sql, new Object[]{id,monday,etime});
			Map<String, Object> lastMap = new HashMap<String, Object>();
			lastMap = daService.getMap(sql, new Object[]{id,monday-7*24*60*60,monday-1});
			map.put("curweekscore", curMap.get("score"));
			map.put("lastweekscore", lastMap.get("score"));
			map.put("qr", getQrCode(id));
			//设置提现参数
			getWithdraw(map);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://192.168.199.239/zld/parkmanage.do?action=contactdetail&id=11354&token=ed7efd992edb35fa7ce594d12b910ad1
		}else if(action.equals("modifypw")){
			Long cid = RequestUtil.getLong(request, "id", -1L);
			String password = RequestUtil.processParams(request, "password");
			if(cid == -1){
				AjaxUtil.ajaxOutput(response, "-2");
				return null;
			}
			String md5pass = password;
			if(md5pass.length()<32){
				md5pass =StringUtils.MD5(md5pass);
				md5pass = StringUtils.MD5(md5pass+"zldtingchebao201410092009");
			}
			int result = daService.update("update user_info_tb set password=?,md5pass=? where id=?", new Object[]{password,md5pass,cid});
			if(result == 1){
				logger.info("市场专员:"+uid+"修改了联系人"+cid+"的密码，新密码:"+password+",时间："+TimeTools.getTime_yyyyMMdd_HHmmss(System.currentTimeMillis()));
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "0");
			}
			logService.updateSysLog(comId, uid.toString(),"修改了停车场人员密码,编号："+cid, 206);
			//http://192.168.199.239/zld/parkmanage.do?action=modifypw&token=e6c435a27cf1f4a11d11c56d0cebc614&id=&password=
		}else if(action.equals("bizquery")){
			String sql = "select id,name from bizcircle_tb where state=? order by id";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{0});
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			//http://192.168.199.239/zld/parkmanage.do?action=bizquery&token=e458de677cfdc0e029736b0023e04555
		}else if(action.equals("getcity")){
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Map<Integer , String> localDataMap = GetLocalCode.localDataMap;
			String city = CustomDefind.getValue("CITY");
			if(localDataMap != null && city != null){
				String cities[] = city.split(",");
				for(int i=0;i<cities.length;i++){
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("id", cities[i]);
					map.put("name", localDataMap.get(Integer.valueOf(cities[i])));
					list.add(map);
				}
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			return null;
			//http://127.0.0.1/zld/parkmanage.do?action=getcity&token=e458de677cfdc0e029736b0023e04555
		}else if(action.equals("editadmin")){
			Long admin_uid = RequestUtil.getLong(request, "uid", -1L);
			int ret =0;
			if(admin_uid!=-1){
				ret = daService.update("update user_info_tb set auth_flag=? where comid=(select comid from user_info_Tb where id=?) and auth_flag=? ", new Object[]{2,admin_uid,1});
				logger.info(admin_uid+"edit all user to collector:"+ret);
				ret = daService.update("update user_info_tb set auth_flag=? where id =? ", new Object[]{1,admin_uid});
				logger.info(admin_uid+"edit  user to admin:"+ret);
			}
			AjaxUtil.ajaxOutput(response, ret+"");
			//http://192.168.199.240/zld/parkmanage.do?action=editadmin&uid=&token=e6c435a27cf1f4a11d11c56d0cebc614
		}
		return null;
	}
	
	/**
	 * 验证token是否有效
	 * @param token
	 * @return uin
	 */
	private Long validToken(String token) {
		Map tokenMap = daService.getMap("select * from user_session_tb where token=?", new Object[]{token});
 		Long uin = null;
		if(tokenMap!=null&&tokenMap.get("uin")!=null){
			uin = (Long) tokenMap.get("uin");
		}
		return uin;
	}
	
	//注册停车场管理员帐号
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Integer createAdmin(HttpServletRequest request,Long uid){
 		Long time = System.currentTimeMillis()/1000;
		//车场信息
		String company =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "company_name"));
		company = company.replace("\r", "").replace("\n", "");
		String address =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "address"));
		address = address.replace("\r", "").replace("\n", "");
		String phone =RequestUtil.processParams(request, "phone");
		String mobile =RequestUtil.processParams(request, "mobile");
		String longitude =RequestUtil.processParams(request, "longitude");
		String latitude =RequestUtil.processParams(request, "latitude");
		Integer parking_type =RequestUtil.getInteger(request, "parking_type", 0);
		Integer parking_total =RequestUtil.getInteger(request, "parking_total", 0);
		Integer type = RequestUtil.getInteger(request, "type", 0);
		Integer city = RequestUtil.getInteger(request, "city", 0);
		Long biz_id = RequestUtil.getLong(request, "biz_id", -1L);
		Integer nfc = RequestUtil.getInteger(request, "nfc", 0);
		Integer etc = RequestUtil.getInteger(request, "etc", 0);
		Integer book = RequestUtil.getInteger(request, "book", 0);
		Integer navi = RequestUtil.getInteger(request, "navi", 0);
		Integer monthlypay = RequestUtil.getInteger(request, "monthlypay", 0);
		Integer isnight = RequestUtil.getInteger(request, "isnight", 0);//夜晚停车，0:支持，1不支持
		String mcompany =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "mcompany"));//经营公司
		String record_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "record_number"));//备案号
		String resume = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "resume"));
		String remarks = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "remarks"));
		Integer epay = RequestUtil.getInteger(request, "epay", 0);
		Integer activity = RequestUtil.getInteger(request, "activity", 0);//车场活动：0 没有活动 1申请活动 2:申请通过
		String activity_content = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "activity_content"));//活动内容
		if(activity_content.equals("")) activity_content = null;
		if(resume.equals("")) resume = null;
		if(remarks.equals("")) remarks = null;
		if(!Check.checkPhone(phone) && !Check.checkMobile(phone)) phone = null;
		Long comId = daService.getLong("SELECT nextval('seq_com_info_tb'::REGCLASS) AS newid",null);
		
		List<Map> sqlsList = new ArrayList<Map>();
		Map comMap = new HashMap();
		//String share_number =RequestUtil.processParams(request, "share_number");
		String comsql = "insert into com_info_tb(id,company_name,address,mobile,phone,create_time," +
				"parking_type,parking_total,longitude,latitude,type,update_time,city,uid,biz_id,nfc,etc,book,navi,monthlypay,isnight,mcompany,record_number,resume,remarks,epay,activity,activity_content)" +
				" values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		Object[] comvalues = new Object[]{comId,company,address,mobile,phone,time,parking_type,parking_total,Double.valueOf(longitude),Double.valueOf(latitude),type,time,city,uid,biz_id,
				nfc,etc,book,navi,monthlypay,isnight,mcompany,record_number,resume,remarks,epay,activity,activity_content};
		comMap.put("sql", comsql);
		comMap.put("values", comvalues);
		sqlsList.add(comMap);
		
		boolean r =  daService.bathUpdate(sqlsList);
		if(r){
//			if(city>0)
//				publicMethods.setCityCache(Long.valueOf(comId),city);
			return 1;
		}
		else {
			return -1;
		}
	}
	
	private boolean checkStrid(String strid){
		String sql = "select count(*) from user_info_tb where strid =?";
		Long result = daService.getLong(sql, new Object[]{strid});
		if(result>0){
			return false;
		}
		return true;
		
	}
	
	private void getParkPics(Map<String, Object> map){
		String sql = "select picurl from com_picturs_tb where comid=? order by create_time desc limit 1";
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = daService.getAll(sql, new Object[]{map.get("id")});
		if(!list.isEmpty()){
			Map<String, Object> map1 = list.get(0);
			map.put("picurl", map1.get("picurl"));
		}else{
			map.put("picurl", null);
		}
	}
	
	//提现过的收费员
	private void getWithdraw(Map<String, Object> map){
		Long count = daService.getLong("select count(*) total from withdrawer_tb where state=3 and uin=?", new Object[]{map.get("id")});
		if(count > 0){
			map.put("withdraw", 1);
		}else{
			map.put("withdraw", 0);
		}
	}
	
	//获取车场昨日的订单量
	private void getOrderCount(Map<String, Object> map){
		Long comid = (Long)map.get("id");
		Long todaybeigintime = TimeTools.getToDayBeginTime();//今天的开始时间
		String ordersql = "select count(id) from order_tb where c_type!=? and total>=? and state=? and pay_type=? and end_time between ? and ? and comid=? ";
		Long ordercount = pgOnlyReadService.getLong(ordersql, new Object[] {4, 1,
				1, 2, todaybeigintime - 24 * 60 * 60, todaybeigintime - 1, comid});
		String directsql = "select count(a.id) from user_account_tb a left join user_info_tb u on a.uid=u.id left join com_info_tb c on u.comid=c.id where a.target=? and a.uid>? and a.create_time between ? and ? and c.id=? ";
		Long directcount = pgOnlyReadService.getLong(directsql, new Object[] {
				1, 0, todaybeigintime - 24 * 60 * 60, todaybeigintime - 1,
				comid });
		map.put("ordercount", ordercount);
		map.put("directcount", directcount);
	}
	
	private String getQrCode(Long uin){
		Map qrMap = daService.getMap("select code from qr_code_tb where uid=? and type=? ", new Object[]{uin,1});
		String code = "";
		if(qrMap!=null){
			code = (String)qrMap.get("code");
		}
		if(code==null||code.trim().equals("")){
			Long newId = daService.getkey("seq_qr_code_tb");
			String codes[] = StringUtils.getGRCode(new Long[]{newId});
			if(codes!=null&&codes.length>0){
				code = codes[0];
				int ret = daService.update("insert into qr_code_tb(id,ctime,type,code,uid,isuse) values(?,?,?,?,?,?)",
						new Object[]{newId,System.currentTimeMillis()/1000,1,code,uin,1});
				logger.info(">>>>new qrcode:uin:"+uin+",qrcode:"+code+",ret:"+ret);
			}
		}
		if(code!=null&&code.trim().length()==19)
			code = "qr/c/"+code.trim();
		return code;
	}
}

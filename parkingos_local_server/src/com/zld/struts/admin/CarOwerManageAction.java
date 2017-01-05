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
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;

import pay.Constants;

import com.zld.AjaxUtil;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.Check;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.SendMessage;
import com.zld.utils.SqlInfo;
/**
 * 车主管理（客户管理 ） ，在总管理员后台
 * @author Administrator
 *
 */
public class CarOwerManageAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PgOnlyReadService pService;
	@Autowired
	private LogService logService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(CarOwerManageAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		String kefu = RequestUtil.processParams(request, "kefu");//从多客服系统直接登录的
		if(comid==null && kefu.equals("")){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("")){
			Integer type = RequestUtil.getInteger(request, "type", -2);
			if(type!=-2){
				request.setAttribute("atype", type);
				return mapping.findForward("authusers");
			}else {
				return mapping.findForward("list");
			}
		}else if(action.equals("quickquery")){
			
			String sql = "select u.*,c.car_number,c.is_comuse,c.is_auth,c.remark " +
					"from user_info_tb u left join car_info_tb c on u.id=c.uin where auth_flag=? ";
			String countSql = "select count(u.*) " +
					"from user_info_tb u left join car_info_tb c on u.id=c.uin where auth_flag=? ";
			List<Object> params = new ArrayList<Object>();
			params.add(4);
			
			Long count = daService.getCount(countSql,params);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			List list = null;//daService.getPage(sql, null, 1, 20);
			
			if(count>0){
				list = daService.getAll(sql+" order by id desc",params, pageNum,pageSize);
			}
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
			return null;
		}else if(action.equals("query")){
			//不包含车牌查询的sql
			String sql = "select u.*,c.car_number,c.pic_url1,c.pic_url2,c.is_auth isauth from user_info_tb u " +
					"left join car_info_tb c on u.id=c.uin where auth_flag=? ";
			String countSql = "select count(u.*) from user_info_tb u " +
					"left join car_info_tb c on u.id=c.uin where auth_flag=? ";
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			SqlInfo base = new SqlInfo("1=1", new Object[]{4});
			SqlInfo sqlInfo = RequestUtil.customSearch(request,"user_info","u",new String[]{"car_number","pic_url1","pic_url2"});
			String car_number = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "car_number"));
			//是否有车牌，0代表有车牌
			Integer hascarnum = RequestUtil.getInteger(request, "hascarnum_start", -1);
			//停车券
			Integer ticket_state = RequestUtil.getInteger(request, "ticket_state_start", -1);
			List<Object> params = null;
			if(sqlInfo!=null){
				sqlInfo = SqlInfo.joinSqlInfo(base,sqlInfo, 2);
				countSql+=" and "+ sqlInfo.getSql();
				sql +=" and "+sqlInfo.getSql();
				params = sqlInfo.getParams();
			}else {
				params = base.getParams();
			}
			if(!car_number.equals("")){
				sql += " and c.car_number like ? ";
				countSql += " and c.car_number like ? ";
				car_number = "%" + car_number + "%";
				params.add(car_number);
			}
			if(hascarnum == 0){
				sql += " and c.car_number is not null ";
				countSql += " and c.car_number is not null ";
			}else if(hascarnum==1){
				sql += " and c.car_number is  null ";
				countSql += " and c.car_number is  null ";
			}
			if(ticket_state != -1){
				Long nowtime = System.currentTimeMillis()/1000;
				if(ticket_state == 0){
					sql += " and u.id in (select uin from ticket_view where limittime<?) ";
					countSql += " and u.id in (select uin from ticket_view where limittime<?) ";
					params.add(nowtime);
				}else if(ticket_state == 1){
					sql += " and u.id in (select uin from ticket_view where limittime between ? and ?) ";
					countSql += " and u.id in (select uin from ticket_view where limittime between ? and ?) ";
					params.add(nowtime);
					params.add(nowtime + 3*24*60*60);
				}
			}
			Long count = count= daService.getCount(countSql, params);
			List list = null;
			if(count>0){
				list = daService.getAll(sql+" order by id desc", params, pageNum, pageSize);
				System.out.print(params);
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
			String sql = "update user_info_tb set nickname=?,strid=?,phone=?,mobile=? where uin=?";
			Object [] values = new Object[]{nickname,strid,phone,mobile,Long.valueOf(id)};
			int result = daService.update(sql, values);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("delete")){
			String id =RequestUtil.processParams(request, "selids");
			String sql = "delete from user_info_tb where id =? ";
			Object [] values = new Object[]{Long.valueOf(id)};
			int result = daService.update(sql, values);
			sql = "delete from car_info_tb where uin=? ";
			result = daService.update(sql, new Object[]{Long.valueOf(id)});
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("carnumber")){
			Integer uin = RequestUtil.getInteger(request, "id", -1);
			if(uin!=-1){
				List carList = daService.getAll("select c.car_number,u.nickname,u.mobile from car_info_tb c,user_info_tb u where c.uin=u.id and c.uin=?", new Object[]{uin});
				if(carList!=null&&!carList.isEmpty()){
					List<Object> list = new ArrayList<Object>();
					String name = "";
					String mobile = "";
					for(int i=0;i<carList.size();i++){
						Map map = (Map)carList.get(i);
						list.add(map.get("car_number"));
						if(i==0){
							name =(String) map.get("nickname");
							mobile = (String) map.get("mobile");
						}
					}
					request.setAttribute("name", name);
					request.setAttribute("mobile", mobile);
					request.setAttribute("list", list);
				}
				
				return mapping.findForward("carlist");
			}
		}else if(action.equals("orderinfo")){
			Integer uin = RequestUtil.getInteger(request, "id", -1);
			if(uin!=-1){
				List orderList = daService.getAll("select * from order_tb where uin=? ", new Object[]{uin});
				if(orderList!=null&&!orderList.isEmpty()){
					List<Object> list = new ArrayList<Object>();
					String name = "";
					String mobile = "";
					for(int i=0;i<orderList.size();i++){
						Map map = (Map)orderList.get(i);
						list.add(map.get("comid"));
					}
					request.setAttribute("list", list);
				}
				return mapping.findForward("orderlist");
			}
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
			//System.out.println(list);
			String mobiles="13860132164,15375242041,18510341966";//群发手机号，最多100个，以空格隔开
			int i=0;
			if(list!=null&!list.isEmpty()){
				for(Map<String, Object> map :list){
					mobiles += ","+map.get("mobile");
					if(i>1000)
						break;
				}
			}
			//mobiles = mobiles.substring(1);
			//mobiles = "15375242041,15801482643,15801270154,18511462902,13860132164,18510341966,13910181815,18201517240";
			//mobiles = "15801482643";
			new SendMessage().sendMultiMessage(mobiles, message+"【停车宝】");
			AjaxUtil.ajaxOutput(response, "1");
		}else if(action.equals("deleteuser")){
			String mobiles[] = request.getParameterValues("mobiles");
			if(mobiles!=null&&mobiles.length>0){
				for(String s : mobiles){
					if(Check.checkMobile(s)){
						//删除车牌
						int ret = daService.update("delete from car_info_Tb where uin = (select id from user_info_tb where mobile=? and auth_flag=?)", new Object[]{s,4});
						logger.info(">>>>>>删除测试账户:"+s+" 车牌result:"+ret);
						//删除红包
						ret = daService.update("delete from bonus_record_tb where mobile=? ",new Object[]{s});
						logger.info(">>>>>>删除测试账户:"+s+" 红包result:"+ret);
						 //删除停车券
						ret = daService.update("delete from ticket_tb where uin = (select id from user_info_tb where mobile=? and auth_flag=?)", new Object[]{s,4});
						logger.info(">>>>>>删除测试账户:"+s+" 停车券result:"+ret);
						//删除车主
						ret = daService.update("delete from user_info_tb where mobile=? ",new Object[]{s});
					    logger.info(">>>>>>删除测试账户:"+s+" 车主result:"+ret);
					    AjaxUtil.ajaxOutput(response, ret+"");
					}
				}
			}
		}else if(action.equals("auth")){//查询已或未审核车主
			Integer type = RequestUtil.getInteger(request, "type", -1);
			request.setAttribute("atype", type);
			List<Object> params = new ArrayList<Object>();
		
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "rp", 20);
			String fieldsstr = RequestUtil.processParams(request, "fieldsstr");
			params.add(4);
			params.add(type);
			String sql = "select c.id,u.id as uin,u.mobile,u.reg_time,c.car_number,c.remark,c.pic_url1,c.pic_url2,c.create_time " +
					"from car_info_tb  c left join user_info_tb u on u.id=c.uin where u.auth_flag=? and c.is_auth=? ";
			String countSql = "select count(c.*) " +
					"from car_info_tb  c left join user_info_tb u on u.id=c.uin where u.auth_flag=? and c.is_auth=?";
			Long count = daService.getCount(countSql,params);
			
			List<Map<String, Object>> list = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				list = daService.getAll(sql+" order by c.id desc, c.uin ",params, pageNum,pageSize);
			}
			
			//String result = "{\"page\":1,\"total\":0,\"rows\": []}";
			String json = JsonUtil.Map2Json(list,pageNum,count, fieldsstr,"id");
			AjaxUtil.ajaxOutput(response, json);
//			if(list!=null&&list.size()>0){
//				result = "{\"page\":"+pageNum+",\"total\":"+count+",\"rows\": [datas]}";
//				String datas = "";
//				for(Map<String, Object> map : list){
//					Long utime = (Long)map.get("create_time");
//					Long rtime = (Long)map.get("reg_time");
//					String uptime = "";
//					if(utime!=null)
//						uptime =TimeTools.getTime_yyyyMMdd_HHmmss(utime*1000);
//					String rdate =TimeTools.getTime_yyyyMMdd_HHmmss(rtime*1000);
//					if(datas.length()>1)
//						datas+=",";
//					datas +="{\"id\":"+map.get("id")+",\"cell\":[\""+map.get("uin")+"\",\""+map.get("mobile")+"\",\""+rdate+"\",\""+map.get("car_number")+"\"," +
//							"\""+map.get("pic_url1")+"\",\""+map.get("pic_url2")+"\",\""+uptime+"\",\""+map.get("remark")+"\"]}";
//				}
//				result = result.replace("datas", datas.replace("null", ""));
//			}
			return null;
		//	request.setAttribute("datas", result);
			//return mapping.findForward("authusers");
		}else if(action.equals("preauthuser")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			if(id!=-1){
				request.setAttribute("id", id);
				Map cMap = daService.getMap("select * from car_info_tb where id =? ", new Object[]{id});
				if(cMap!=null&&!cMap.isEmpty()){
					request.setAttribute("url1",cMap.get("pic_url1") );
					request.setAttribute("url2",cMap.get("pic_url2") );
					request.setAttribute("remark",cMap.get("remark") );
					request.setAttribute("carnumber",cMap.get("car_number") );
					request.setAttribute("uin",cMap.get("uin") );
				}
			}
			return mapping.findForward("preauthuser");
		}else if(action.equals("authuser")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			Long uin = RequestUtil.getLong(request, "uin", -1L);
			Integer isAuth = RequestUtil.getInteger(request, "isauth", -1);
			String remark =AjaxUtil.decodeUTF8(RequestUtil.getString(request, "remark"));
			String carNumber =AjaxUtil.decodeUTF8(RequestUtil.getString(request, "car_number"));
		//	System.out.println(carNumber);
			Map carMap = null;
			if(id==-1&&!carNumber.equals("")){
				carMap = daService.getMap("select id,car_number from car_info_tb where car_number =?", new Object[]{carNumber});
				id = (Long)carMap.get("id");
			}else {
				carMap = daService.getMap("select car_number from car_info_tb where id =?", new Object[]{id});
			}
			int ret =0;
			logger.info(uin+","+id+",认证，isauth="+isAuth+",remark="+remark);
			if(carMap!=null&&!carMap.isEmpty()){
				ret = daService.update("update car_info_tb set is_auth=? ,remark=? where id =? ", new Object[]{isAuth,remark,id});
				logger.info(uin+","+id+",更新车牌认证状态，isauth="+isAuth+",remark="+remark+",ret="+ret);
				if(ret==1&&isAuth==1){//处理信用额度
					Map userMap = daService.getMap("select is_auth from user_info_tb where id =? ", new Object[]{uin});
					if(userMap!=null){
						Integer is_auth = (Integer)userMap.get("is_auth");
						if(is_auth==0){//第一次验证车牌通过，加30元信用额度，并标志为已认证车主
							ret = daService.update("update user_info_Tb set is_auth=? ,credit_limit=? where id=? ", new Object[]{1,30,uin});
							logger.info("车主"+uin+"认证通过，返30元的信用额度。。："+ret+",处理推荐奖");
							handlerecommend(uin);
						}
					}
					//处理返红包
					Long bid =daService.getkey("seq_order_ticket_tb");
					String sql = "insert into order_ticket_tb (id,uin,order_id,money,bnum,ctime,exptime,bwords,type) values(?,?,?,?,?,?,?,?,?)";
					Long ctime = System.currentTimeMillis()/1000;
					Long exptime = ctime + 24*60*60;
					Object []values = new Object[]{bid,uin,-1,36,21,ctime,exptime,"我在停车宝上传了行驶证，通过了认证，获得了1246特殊策略红包一个。耗耐心，慎抢",3};
					ret  = daService.update(sql,values);
					if(ret != 1){
						bid = -1L;
					}
					logger.info("车主"+uin+"认证通过，返红包21/36。。："+ret);
					logService.insertUserMesg(1, uin, "恭喜您获得认证大礼包", "红包提醒");
					if(remark.equals(""))
						remark="符合规范";
					sendMessage("恭喜您车牌【"+carMap.get("car_number")+"】认证审核通过", uin,remark,1, bid);
				}else {
					if(!remark.equals(""))
						sendMessage("您的车牌【"+carMap.get("car_number")+"】没有通过审核", uin,remark,0, -1L);
				}
			}
			AjaxUtil.ajaxOutput(response, ret+"");
		}else if(action.equals("kefuauthuser")){
			String mobile = RequestUtil.processParams(request, "mobile");
			String carnumber = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "carnumber"));
			logger.info("客服处理争议车牌，kefu handle disputed carnumber>>>mobile:"+mobile+",carnumber:"+carnumber+",kefu:"+kefu);
			if(!mobile.equals("") && !carnumber.equals("") && !kefu.equals("")){
				Map<String, Object> carMap = pService.getMap(
						"select * from car_info_tb where car_number=? ",
						new Object[] { carnumber });
				
				Map<String, Object> userMap = pService
						.getMap("select * from user_info_tb where mobile=? and auth_flag=? ",
								new Object[] { mobile, 4 });
				
				if(userMap != null){
					Long id = (Long)userMap.get("id");
					Integer is_auth = (Integer)userMap.get("is_auth");
					if(carMap != null){
						Long uin = (Long)carMap.get("uin");
						if(uin.intValue() == id.intValue()){
							AjaxUtil.ajaxOutput(response, "-3");
							logger.info("该车牌本来就属于该手机号,不再做处理kefu handle disputed carnumber>>>id:"+id+",uin:"+uin+",isauth:"+is_auth+",carnumber:"+carnumber+",kefu:"+kefu+",mobile:"+mobile);
							return null;
						}else{
							logger.info("该车牌不属于该手机号,开始处理kefu handle disputed carnumber>>>id:"+id+",uin:"+uin+",isauth:"+is_auth+",carnumber:"+carnumber+",kefu:"+kefu+",mobile:"+mobile);
							Long count = pService.getLong("select count(id) from car_info_tb where uin=? ",
											new Object[] { id });
							if(count > 2){
								AjaxUtil.ajaxOutput(response, "-5");
								logger.info("改手机号已经存在3个以上车牌,不作处理kefu handle disputed carnumber>>>id:"+id+",uin:"+uin+",isauth:"+is_auth+",carnumber:"+carnumber+",kefu:"+kefu+",mobile:"+mobile+",count:"+count);
								return null;
							}else{
								List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
								
								Map<String, Object> docarMap = new HashMap<String, Object>();
								//停车场账户返现
								docarMap.put("sql", "update car_info_tb set uin=?,remark=?,is_auth=? where car_number=? ");
								docarMap.put("values", new Object[]{id,kefu,1,carnumber});
								bathSql.add(docarMap);
								
								if(is_auth == 0){
									Map<String, Object> douserMap = new HashMap<String, Object>();
									//停车场账户返现
									douserMap.put("sql", "update user_info_tb set is_auth=?,credit_limit=? where id=? ");
									douserMap.put("values", new Object[]{1, 30, id});
									bathSql.add(douserMap);
								}
								
								Long ctime = System.currentTimeMillis()/1000;
								Long exptime = ctime + 24*60*60;
								Long bid =daService.getkey("seq_order_ticket_tb");
								Map<String, Object> bonusMap = new HashMap<String, Object>();
								//停车场账户返现
								bonusMap.put("sql", "insert into order_ticket_tb (id,uin,order_id,money,bnum,ctime,exptime,bwords,type) values(?,?,?,?,?,?,?,?,?)");
								bonusMap.put("values", new Object[]{bid,id,-1,36,21,ctime,exptime,"我在停车宝上传了行驶证，通过了认证，获得了1246特殊策略红包一个。耗耐心，慎抢",3});
								bathSql.add(bonusMap);
								
								boolean b = daService.bathUpdate(bathSql);
								logger.info("kefu handle disputed carnumber>>>id:"+id+",uin:"+uin+",isauth:"+is_auth+",carnumber:"+carnumber+",mobile:"+mobile+",kefu:"+kefu+"b:"+b);
								if(b){
									logService.insertUserMesg(1, id, "恭喜您获得认证大礼包", "红包提醒");
									sendMessage("恭喜您车牌【"+carMap.get("car_number")+"】认证审核通过", id,"符合规范",1, bid);
									
									if(is_auth == 0){
										logger.info("该手机号原来是未认证，现在认证了，处理推荐逻辑kefu handle disputed carnumber>>>id:"+id+",uin:"+uin+",isauth:"+is_auth+",carnumber:"+carnumber+",kefu:"+kefu+",mobile:"+mobile);
										handlerecommend(id);
									}
									
									logger.info("给原来的车主发信息提示填写真实车牌号kefu handle disputed carnumber>>>id:"+id+",uin:"+uin+",isauth:"+is_auth+",carnumber:"+carnumber+",kefu:"+kefu+",mobile:"+mobile);
									sendMsgToPreUser(uin, carnumber);
									
									AjaxUtil.ajaxOutput(response, "1");
									return null;
								}
							}
						}
					}else{
						logger.info("该车牌号未注册,不作处理kefu handle disputed carnumber>>>mobile:"+mobile+",carnumber:"+carnumber+",kefu:"+kefu);
						AjaxUtil.ajaxOutput(response, "-4");
						return null;
					}
				}else{
					logger.info("该手机号未注册,不作处理kefu handle disputed carnumber>>>mobile:"+mobile+",carnumber:"+carnumber+",kefu:"+kefu);
					AjaxUtil.ajaxOutput(response, "-2");
					return null;
				}
			}
			logger.info("手机号、车牌号、客服信息不全,不作处理kefu handle disputed carnumber>>>mobile:"+mobile+",carnumber:"+carnumber+",kefu:"+kefu);
			AjaxUtil.ajaxOutput(response, "-1");
		}
		return null;
	}
	
	private void sendMsgToPreUser(Long uin, String carnumber){
		Map<String, Object> userMap = pService.getMap("select * from user_info_tb where id=? ", new Object[]{uin});
		if(userMap != null){
			String mobile = (String)userMap.get("mobile");
			String msg = "停车宝用户您好，您的车牌"+carnumber+"经认证属于其他车主，请重新上传您的真实车牌，认证通过可享受更多优惠【停车宝】";
			SendMessage.sendMultiMessage(mobile, msg);
		}
	}
	
	private void handlerecommend(Long uin){
		boolean isBlack = true;//publicMethods.isBlackUser(uin);
		if(!isBlack){
			logger.info("该车主不在黑名单内，uin:"+uin);
			Map<String, Object> userMap = pService.getMap("select * from user_info_tb where id=? ", new Object[]{uin});
			if(userMap != null){
				String openid = null;
				if(userMap.get("wxp_openid") != null){
					openid = (String)userMap.get("wxp_openid");
				}
				List<Object> params = new ArrayList<Object>();
				String sql = "select count(*) from recommend_tb where nid=? ";
				params.add(uin);
				if(openid != null){
					sql += " or openid=? ";
					params.add(openid);
				}
				Long count = pService.getCount(sql, params);
				if(count == 0){
					logger.info("该车主之前没有被推荐过uin:"+uin+",openid:"+openid);
					Map<String, Object> ticketMap = pService.getMap("select r.uin,r.create_time from ticket_tb t,reward_account_tb r where t.id=r.ticket_id and t.uin=? and t.type=? order by r.create_time limit ? ",
									new Object[] { uin, 1, 1 });
					Long uid = -1L;
					Long create_time = 0L;
					if(ticketMap != null){
						uid = (Long)ticketMap.get("uin");
						create_time = (Long)ticketMap.get("create_time");
						logger.info("最早的一笔领取专用券，uin:"+uin+",uid:"+uid+",create_time:"+create_time);
					}
					Map<String, Object> bonusMap = pService
							.getMap("select r.uin,o.ttime from order_ticket_detail_tb o,reward_account_tb r where o.otid=r.orderticket_id and o.uin=? and r.type=? and r.target=? order by o.ttime limit ? ",
									new Object[] { uin, 1, 1, 1 });
					if(bonusMap != null){
						Long ttime = (Long)bonusMap.get("ttime");
						logger.info("最早的一笔领取专用券红包，uin:"+uin+",uid:"+uid+",ttime:"+ttime);
						if(ttime.intValue() < create_time.intValue()){
							uid = (Long)bonusMap.get("uin");
							logger.info("最早的是领取专用券红包，uin:"+uin+",uid:"+uid+",ttime:"+ttime);
						}
					}
					
					if(uid != -1){
						logger.info("开始出处理推荐逻辑，uin:"+uin+",uid:"+uid);
						Double money = 5d;//默认返5块
						Map uidMap =daService.getMap("select recommendquota from user_info_Tb where id =? and (auth_flag=? or auth_flag=?) and state=? ", new Object[]{uid,1,2, 0});
						if(uidMap!=null){
							money = Double.valueOf(uidMap.get("recommendquota") + "");
							logger.info("该收费员的推荐奖额度是："+money+",uid:"+uid+",uin:"+uin);
							
							List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
							Long comId = -1L;
							Map comMap = daService.getPojo("select comid from user_info_tb where id=? and state=? ",new Object[] {uid,0});
							Map msetMap =null;
							Integer giveMoneyTo = null;//查询收费设定 mtype:0:公司账户，1：个人账户'
							if(comMap!=null && comMap.get("comid") != null){
								comId =(Long)comMap.get("comid");
								if(!publicMethods.isBlackParkUser(comId, false)){
									msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
											new Object[]{comId,4});
									
									if(msetMap!=null){
										giveMoneyTo =(Integer)msetMap.get("giveto");
									}
									if(giveMoneyTo!=null&&giveMoneyTo==0){//返现给停车场账户
										Map<String, Object> comqlMap = new HashMap<String, Object>();
										//停车场账户返现
										comqlMap.put("sql", "update com_info_tb set total_money=total_money+?,money=money+?  where id=? ");
										comqlMap.put("values", new Object[]{money,money,comId});
										bathSql.add(comqlMap);
										
										//写入停车场账户明细
										Map<String, Object> parkAccountMap = new HashMap<String, Object>();
										parkAccountMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) " +
												"values(?,?,?,?,?,?,?)");
										parkAccountMap.put("values", new Object[]{comId,money,0,System.currentTimeMillis()/1000,"推荐奖励",uid,3});
										bathSql.add(parkAccountMap);
										logger.info("推荐奖励给停车场uin:"+uin+",uid:"+uid+",comid:"+comId);
										
									}else {//返现给收费员账户
										Map<String, Object> usersqlMap = new HashMap<String, Object>();
										//收费员账户返现
										usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
										usersqlMap.put("values", new Object[]{money,uid});
										bathSql.add(usersqlMap);
										
										//写入收费员账户明细
										Map<String, Object> parkuserAccountMap = new HashMap<String, Object>();
										parkuserAccountMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) " +
												"values(?,?,?,?,?,?)");
										parkuserAccountMap.put("values", new Object[]{uid,money,0,System.currentTimeMillis()/1000,"推荐奖励",3});
										bathSql.add(parkuserAccountMap);
										
										logger.info("推荐奖励给收费员uin:"+uin+",uid:"+uid+",comid:"+comId);
									}
									//更新推荐记录
									Map<String, Object> recomsqlMap = new HashMap<String, Object>();
									recomsqlMap.put("sql", "insert into recommend_tb(pid,nid,type,state,create_time,openid,money) values(?,?,?,?,?,?,?)");
									recomsqlMap.put("values", new Object[]{uid,uin,0,1,System.currentTimeMillis()/1000,openid,money});
									bathSql.add(recomsqlMap);
									
									boolean b = daService.bathUpdate(bathSql);
									logger.info("推荐奖处理结果uin:"+uin+",uid:"+uid+",comid:"+comId+",b:"+b);
									if(b){
										Long ntime = System.currentTimeMillis()/1000;
//										logService.insertParkUserMessage(comId,2,uid,userMap.get("mobile")+"",uin,money, "", 0,ntime,ntime+10,8);
										logger.info("发消息>>>uin:"+uin+",uid:"+uid+",comid:"+comId);
									}
								}else{
									logger.info("该车场在黑名单内，推荐奖取消uin:"+uin+",uid:"+uid+"comid:"+comId);
								}
							}else{
								logger.info("未找到正常状态车场，uin:"+uin+",uid:"+uid);
							}
						}else{
							logger.info("未找到正常状态收费员，uin:"+uin+",uid:"+uid);
						}
					}
				}else{
					logger.info("该车主之前被推荐过,不再处理uin:"+uin+",openid:"+openid);
				}
			}
		}else{
			logger.info("该车主在黑名单内，uin:"+uin);
		}
	}
	
	private void sendMessage(String content,Long uin,String remark,int type, Long bonusid){
		Map userMap = daService.getMap("select mobile,wxp_openid from user_info_tb where id = ?", new Object[]{uin});
		if(userMap!=null){
			String mobile = (String)userMap.get("mobile");
			String openid = (String)userMap.get("wxp_openid");
			if(!mobile.equals("")&&Check.checkMobile(mobile)){//手机号不为空时，发短信通知
				if(type==1){
					SendMessage.sendMultiMessage(mobile, content+" 【停车宝】");
				}else {
					SendMessage.sendMultiMessage(mobile, content+"，原因："+remark+" 【停车宝】");
				}
			}
			if(openid!=null&&!openid.equals("")){
				Map<String, String> baseinfo = new HashMap<String, String>();
				String wxremark = "回复'认证'，了解认证详情。";
				String wxremark_color = "#000000";
				String url = "http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=208679773&idx=1&sn=43d1fe06680c90efb11444f8b72bdff2#rd";
				if(bonusid != -1){
					wxremark = "恭喜您获得了1246特殊策略红包一个，耗耐心，慎抢呦~~~";
					wxremark_color = "#FF0000";
					url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpublic.do?action=balancepayinfo&openid="+openid+"&bonusid="+bonusid+"&bonus_type=0&notice_type=4";
				}
				
				baseinfo.put("url", url);
				baseinfo.put("openid", openid);
				baseinfo.put("top_color", "#000000");
				baseinfo.put("templeteid", Constants.WXPUBLIC_AUDITRESULT_ID);
				
				List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
				Map<String, String> keyword1 = new HashMap<String, String>();
				keyword1.put("keyword", "first");
				keyword1.put("value", content);
				keyword1.put("color", "#000000");
				
				Map<String, String> keyword2 = new HashMap<String, String>();
				keyword2.put("keyword", "keyword1");
				if(type==0){
					keyword2.put("value", "失败");
					keyword2.put("color", "#FF0000");
				}
				else {
					keyword2.put("value", "成功");
					keyword2.put("color", "#00FF00");
				}
				
				
				Map<String, String> keyword3 = new HashMap<String, String>();
				keyword3.put("keyword", "keyword2");
				if(type==0){
					keyword3.put("value", remark);
				}else {
					keyword3.put("value", "符合规范");
				}
				keyword3.put("color", "#000000");
				
				Map<String, String> keyword4 = new HashMap<String, String>();
				keyword4.put("keyword", "remark");
				
				keyword4.put("value", wxremark);
				keyword4.put("color", wxremark_color);
				
				orderinfo.add(keyword1);orderinfo.add(keyword2);
				orderinfo.add(keyword3);orderinfo.add(keyword4);
				
//				try {
////					publicMethods.sendWXTempleteMsg(baseinfo, orderinfo);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
			}
		}
	}
	
	private void setCarNumber(List list){
		List<Object> uins = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
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
			List<Map<String, Object>> resultList = daService.getAllMap("select car_number,uin from car_info_tb  where  uin in ("+preParams+") ", uins);
			if(resultList!=null&&!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long comid=(Long)map1.get("id");
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("uin");
						if(comid.intValue()==uin.intValue()){
							map1.put("carnumber", map.get("car_number"));
							break;
						}
					}
				}
			}
		}
	}
	

}
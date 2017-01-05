package com.zld.struts.request;

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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

/**
 * 车主发送uuid，后台处理订单的生成及完成订单
 * 处理手机上传Ibeacon及订单生成 和处理
 * 
 * @author Laoyao
 * @date 2014-05-12
 */
public class IbeaconHandleAction extends Action {


	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;

	@Autowired
	private PublicMethods publicMethods;
	
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	
	private Logger logger = Logger.getLogger(IbeaconHandleAction.class);
	
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		
		Integer major = RequestUtil.getInteger(request, "major", -1);
		Integer minor = RequestUtil.getInteger(request, "minor", -1);
		
		String result = "";
		// 车主手机号码
		String mobile = RequestUtil.processParams(request, "mobile");
		Long uin = null;
//		if(!mobile.equals("")){
//			Map userMap = getUserMap(mobile,null);
//			if(userMap==null){
//				result = "{\"inout\":\"0\",\"uid\":\"-1\"}";
//				AjaxUtil.ajaxOutput(response, result);
//				return null;
//			}else {
//				uin = (Long)userMap.get("id");
//			}
//		}
		
		logger.info(">>>action:"+action+",major:"+major+",minor:"+minor+",mobile:"+mobile+",uin:"+uin);
		/**
		 * 车主摇一遮,传入Ibeacon的参数，查询此设备是出口还是入口还是出入口通用
		 * 入口：查是否有未结算的订单:
		 *    有：返回订单信息
		 *    没有：生成订单
		 * 出口：查询是否有未结算的订单：
		 * 	      有：结算订单
		 *    没有：调用直付
		 * 入口和出口用一个：
		 * 	有订单结算，无订单生成订单，再次摇1分钟以内走直付
		 * **/
		if (action.equals("ibcincom")) {// 提交ibeacon uuid,查询停车场信息
			// ibeacon uuid
			Map cominfo = daService.getPojo("select * from area_ibeacon_tb where major=? and minor=? ",new Object[] { major,minor });
			result = "{\"inout\":\"-1\",\"uid\":\"-1\",\"orderid\":\"0\"}";
			if(cominfo!=null){
				Long pass = (Long) cominfo.get("pass");// 出入场标志 0入口，1出口 2不区分 
				Long comid = (Long) cominfo.get("comid");// 公司（停车场）ID
				String inOut = "";
				Map passMap = daService.getMap("select passtype,worksite_id from com_pass_tb where id=?", new Object[]{pass});
				Long worksiteId = null;
				Long uid = -1L;
				if(passMap!=null){
					inOut = (String) passMap.get("passtype");
					worksiteId = (Long)passMap.get("worksite_id");
				}
				//查询是否有未结算的订单
				Long orderId = 0L;
				if(worksiteId!=null){
					Map useWorkSiteMap = daService.getMap("select uin from user_worksite_tb where worksite_id=? ", new Object[]{worksiteId});
					if(useWorkSiteMap!=null){
						uid = (Long)useWorkSiteMap.get("uin");
					}
					orderId = daService.getLong("select max(id) from order_tb where comid=? and uin=?  and state=? ",
							new Object[]{comid,uin,0});
				}
				result = "{\"inout\":\""+inOut+"\",\"uid\":\""+uid+"\",\"orderid\":\""+orderId+"\"}";
				if(inOut.equals("1")&&orderId==0){
					Map uMap = daService.getMap("select u.nickname,c.company_name from user_info_Tb u left join com_info_Tb c " +
							"on u.comid = c.id where u.id =? ", new Object[]{uid});
					if(uMap!=null&&!uMap.isEmpty()){
						result = "{\"inout\":\""+inOut+"\",\"uid\":\""+uid+"\",\"orderid\":\""+orderId+"\",\"name\":\""+uMap.get("nickname")+"\",\"parkname\":\""+uMap.get("company_name")+"\"}";
					}else {
						result = "{\"inout\":\"1\",\"uid\":\"-1\",\"orderid\":\"0\"}";
					}
				}
			}
			//http://s.tingchebao.com/zld/ibeaconhandle.do?major=0&minor=1&action=ibcincom&mobile=15801482643
		} else if (action.equals("addorder")) {// 生成订单
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Map cominfo = daService.getPojo("select comid from area_ibeacon_tb where major=? and minor=? ",new Object[] { major,minor });
			if(cominfo!=null){
				Long comid = (Long) cominfo.get("comid");// 公司（停车场）ID
				result =addOrder(uin,comid,uid,major+"_"+minor);
			}
			//infoMap = addOrder(request);
			// 余额查询，不足10元提醒
		} else if (action.equals("doorder")) {// 车主提交结算订单请求
			//infoMap = balance(request);
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			if(uid==-1||uid==0){
				result= "{\"result\":\"0\",\"info\":\"结算失败，没有收费员在岗！\"}";
			}else {
				result = doPreOrder(uid,orderid);
			}
			//http://s.tingchebao.com/zld/ibeaconhandle.do?action=doorder&uid=orderid=
		} else if(action.equals("payorder")){//收费员结算订单
			Long id = RequestUtil.getLong(request, "id", -1L);
			Double price = RequestUtil.getDouble(request, "total", 0d);
//			result = doOrder(id,price);
			//http://s.tingchebao.com/zld/ibeaconhandle.do?action=payorder&id=&total=
			
		}else if(action.equals("clearmem")){
//			Integer type = RequestUtil.getInteger(request, "type", 0);
//			result = "清除失败";
//			if(type==0){//清除停车券使用次数 
//				Map<Long ,String> map = memcacheUtils.doMapLongStringCache("usetickets_times", null, null);
//				//System.err.println(map);
//				//logger.info(">>>>update>>> uin:"+uin+",map:"+ map);
//				if(map!=null){
//					if(map.get(uin)!=null){
//						map.remove(uin);
//						memcacheUtils.doMapLongStringCache("usetickets_times", map, "update");
//						logger.info(">>>>>>>>>>已清除停车券使用次数,手机："+mobile);
//						result = "清除成功";
//					}
//				}
//			}else if(type==1){//清除红包缓存
//				Map<Long ,String> map = memcacheUtils.doMapLongStringCache("backtickets_times", null, null);
//				//System.err.println(map);
//				//logger.info(">>>>update>>> uin:"+uin+",map:"+ map);
//				if(map!=null){
//					if(map.get(uin)!=null){
//						map.remove(uin);
//						memcacheUtils.doMapLongStringCache("backtickets_times", map, "update");
//						logger.info(">>>>>>>>>>已清除红包缓存,手机："+mobile);
//						result = "清除成功";
//					}
//				}
//			}else if(type==2){//清除打赏缓存
//				Map<Long ,Long> map = memcacheUtils.doMapLongLongCache("reward_userticket_cache", null, null);
//				//System.err.println(map);
//				//logger.info(">>>>update>>> uin:"+uin+",map:"+ map);
//				if(map!=null){
//					if(map.get(uin)!=null){
//						map.remove(uin);
//						memcacheUtils.doMapLongLongCache("reward_userticket_cache", map, "update");
//						logger.info(">>>>>>>>>>已清除打赏缓存,手机："+mobile);
//						result = "清除成功";
//					}
//				}
//			}
		}
		logger.info(result);
		AjaxUtil.ajaxOutput(response, result);
		return null;
	}
	
	private String doPreOrder(Long uid,Long orderid) {
		logger.info("uid:"+uid+",orderid:"+orderid);
		Map orderMap = daService.getPojo("select * from order_tb where id=? ",new Object[] {orderid});
		if (orderMap != null && orderMap.get("state") != null) {// 存在未结算的订单，正常提交完成订单
			//判断状态，是否是未结算
			//Long uin =(Long)orderMap.get("uin");
			Integer state = (Integer)orderMap.get("state");
			Long start = (Long)orderMap.get("create_time");
			Long comid = (Long)orderMap.get("comid");
			Integer car_type = (Integer)orderMap.get("car_type");//0：通用，1：小车，2：大车
			String carNumber = (String)orderMap.get("car_number");
			Long orderId = (Long)orderMap.get("id");
			Long etime = System.currentTimeMillis()/1000;
			String duration = StringUtils.getTimeString(start, etime);
			Long uin = (Long)orderMap.get("uin");
			//已完成过订单，返回错误消息
			if(state==1){
				return "{\"result\":\"2\",\"info\":\"已支付过，不能重复支付\"}";
			}else {
				daService.update("update order_tb set uid = ? where id   = ?", new Object[]{uid,orderid});
			}
			//查订单金额
			String price = publicMethods.getPrice(start,etime, comid, car_type);
			Double totalmoney = Double.valueOf(price);
			Double ticketMoney = 0d;
			Double balance = 0d;
			//查可用的券
			Map ticketMap = null;
//			Long time = System.currentTimeMillis()/1000;
//			if(memcacheUtils.readUseTicketCache(uin))//每天使用不超过三次
//				ticketMap= publicMethods.useTickets(uin, totalmoney,comid,uid,0);
			if(ticketMap!=null)
				ticketMoney = Double.valueOf(ticketMap.get("money")+"");
			//查用户余额
			Map userMap = daService.getMap("select balance from user_info_Tb where id =?", new Object[]{uin});
			if(userMap!=null)
				balance = Double.valueOf(userMap.get("balance")+"");
			else {
				return "{\"result\":\"0\",\"info\":\"账户异常请稍候重试!\"}";
			}
			if(totalmoney>(ticketMoney+balance)){
				return "{\"result\":\"3\",\"info\":\"余额不足，请先充值!\"}";
			}
			//给收费员发消息
//			logService.insertParkUserMessage(comid, 1, uid,carNumber, orderId, StringUtils.formatDouble(price),duration, 0, start, etime,0);
			return "{\"result\":\"1\",\"info\":\"收费员正在结算\"}";
		} else {// 错误，没有可结算的订单
			return "{\"result\":\"0\",\"info\":\"没有可结算的订单\"}";
		}
	}

	
//	private String doOrder(Long id,Double totalmoney) {
//		Map orderMap = daService.getPojo("select * from order_tb where id=? ",new Object[] {id});
//		if (orderMap != null && orderMap.get("state") != null) {// 存在未结算的订单，正常提交完成订单
//			//判断状态，是否是未结算
//			double balance=0d;
//			Long uin =(Long)orderMap.get("uin");
//			Map userMap = getUserMap(null,uin);
//			balance = Double.valueOf(userMap.get("balance")+"");
//			Integer state = (Integer)orderMap.get("state");
//			Long start = (Long)orderMap.get("create_time");
//			Long comid = (Long)orderMap.get("comid");
//			Long uid = (Long)orderMap.get("uid");
//			String carNumber = publicMethods.getCarNumber(uin);
//			//已完成过订单，返回错误消息
//			if(state==1){
//				return "{\"result\":\"2\",\"info\":\"已支付过，不能重复支付\"}";
//			}
//			/*//判断是否是自动支付
//			Integer autoCash=0;
//			Map upMap = daService.getPojo("select auto_cash,limit_money from user_profile_tb where uin =?", new Object[]{uin});
//			Integer limitMoney = 0;
//			if(upMap!=null&&upMap.get("auto_cash")!=null){
//				autoCash= (Integer)upMap.get("auto_cash");
//				limitMoney = (Integer)upMap.get("limit_money");
//			}
//			//未设置自动支付
//			if(autoCash==0){
//				return "{\"result\":\"0\",\"info\":\"您未设置自动支付\"}";
//			}*/
//			
//			//查可用停车券
//			Map ticketMap = null;
////			Long time = System.currentTimeMillis()/1000;
////			if(memcacheUtils.readUseTicketCache(uin))//每天使用不超过三次
////				ticketMap= publicMethods.useTickets(uin, totalmoney,comid,uid,0);
////					daService.getMap("select * from ticket_tb where uin = ? and limit_day > ? " +
////						"and state=? order by money desc limit ? ",
////						new Object[]{uin,time,0,1});
//			Double tickMoney = 0d;//可用停车券金额
//			Long ticketId = null;//停车券ID
//			if(ticketMap!=null){
//				tickMoney = StringUtils.formatDouble(ticketMap.get("money"));
//				ticketId = (Long)ticketMap.get("id");
//			}
//			
//			boolean isupmoney=false;//是否可超过自动支付限额
////			if(limitMoney!=null){
////				if(limitMoney==-1||limitMoney>=totalmoney-tickMoney)//如果是不限或大于支付金额，可自动支付 
////					isupmoney=false;
////			}
//			//订单金额超出自动支付限额
//			if(isupmoney){
//				//给车主发消息
//				logService.insertMessage(comid, -1, uin,carNumber, id,totalmoney,"", 0,start,System.currentTimeMillis()/1000,9);
//				return "{\"result\":\"0\",\"info\":\"订单金额超出自动支付限额\"}";
//			}
//			//余额不足
//			if(balance+tickMoney<totalmoney){
//				//给车主发消息
//				logService.insertMessage(comid, -1, uin,carNumber, id,totalmoney,"", 0,start,System.currentTimeMillis()/1000,9);
//				return "{\"result\":\"0\",\"info\":\"余额不足\"}";
//			}
//			//支付订单
//			String comName = "";
//			Map comMap = daService.getMap("select company_name from com_info_tb where id=? ",new Object[]{comid});
//			if(comMap!=null)
//				comName = (String)comMap.get("company_name");
//			String ret =  payIbeaconOrder(start, totalmoney, (Long)orderMap.get("id"), tickMoney, comid, uin, uid, ticketId, comName, carNumber);
//			//发消息给车主
//			
//			if(ret.equals("1")){
//				return "{\"result\":\"1\",\"info\":\"结算成功\"}";
//			}else {
//				return "{\"result\":\"0\",\"info\":\"结算失败\"}";
//			}
//		} else {// 错误，没有可结算的订单
//			return "{\"result\":\"0\",\"info\":\"没有可结算的订单\"}";
//		}
//	}

	
	private String addOrder(Long uin, Long comid, Long uid, String uuid) {
		//查询车牌号
		String carNumber=publicMethods.getCarNumber(uin);
		if(carNumber.equals("车牌号未知"))
			carNumber = "";
		Map orderMap = daService.getPojo("select * from order_tb where comid=? and uin=? and state=? ",new Object[] { comid, uin, 0});
		int result =0;
		if (orderMap != null && orderMap.get("state") != null) {// 已经存在了订单
			return "{\"result\":\"0\",\"info\":\"已存在未结算的订单，不能生成进场订单\"}";
		} else {// 创建新订单
			result = daService.update("insert into order_tb(create_time,comid,uin,state,c_type,car_number,uid,nfc_uuid) values(?,?,?,?,?,?,?,?)",
					new Object[] {System.currentTimeMillis()/1000, comid, uin, 0 ,1,carNumber,uid,uuid});
			String ret = "欢迎进入停车场";
			if(result!=1)
				ret = "生成订单错误，进场失败";
			//doInOrder(infoMap, comid, uin, balance, carNumber);
			return "{\"result\":\""+result+"\",\"info\":\""+ret+"\"}";
		}
	}

	/**
	 * 根据手机传入的uuid处理是新建或完成订单
	 * @param mobile
	 * @param uuid
	 * @return
	 */
	
	/**
	 * 支付Ibeacon订单
	 * @param start 订单开始时间
	 * @param total 总金额
	 * @param orderId 订单编号
	 * @param ticketMoney 券金额
	 * @param comid 车场编号 
	 * @param uin 车主账号
	 * @param uid 收费员账号
	 * @param ticketId 券编号 
	 * @param comName 车场名称 
	 * @param carNumber 车牌号
	 * @return
	 */
//	private String payIbeaconOrder(Long start,Double total,Long orderId,Double ticketMoney,
//			Long comid,Long uin,Long uid,Long ticketId,String comName,String carNumber){
//
//		Long ntime = System.currentTimeMillis()/1000;
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新订单状态，收费成功
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//收费员账户
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//车主账户
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//车主账户加停车券
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//收费员余额
//		Map<String, Object> parkusersqlMap =new HashMap<String, Object>();
//		//使用停车券更新
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//停车宝账户扣停车券金额
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		
//		Long etime = System.currentTimeMillis()/1000;
////		if(start!=null&&start==etime)
////			etime = etime+60;
//		orderSqlMap.put("sql", "update order_tb set state =?,pay_type=?, end_time=?,total=? where id=?");
//		orderSqlMap.put("values", new Object[]{1,2,etime,total,orderId});
//		bathSql.add(orderSqlMap);
//		
//		userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){//使用停车券，给车主账户先充值
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,"停车券充值",7});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"停车费-"+comName,0});
//		bathSql.add(userAccountsqlMap);
//		
//
//		parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//		parkusersqlMap.put("values", new Object[]{total,uid});
//		bathSql.add(parkusersqlMap);
//		
//		parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
//		parkuserAccountsqlMap.put("values", new Object[]{uid,total,0,ntime,"停车费_"+carNumber,4});
//		bathSql.add(parkuserAccountsqlMap);
//		
//		//优惠券使用后，更新券状态，添加停车宝账户支付记录
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comid,System.currentTimeMillis()/1000,ticketMoney,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype) values(?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"，车主"+carNumber+"，使用停车代金券",0});
//			bathSql.add(tingchebaoAccountsqlMap);
////			memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info(">>>>>>>>>>>>>>>支付 ："+result);
//		if(result){
//			//处理返现（车场），返券（车主）
//			/* 每次用余额或微信或支付宝支付1元以上的完成的，补贴车场2元，补贴车主3元的停车券，
//			 * 车场返现不限(同一车主每日只能返3次)，
//			 * 车主每日返券限3张券
//			 * 每个车主每天使用停车券不超过3单 */
//			try {
//				//查是不是黑名单内
//				boolean isBlack=true;
////				List<Long> blackUserList = memcacheUtils.doListLongCache("zld_black_users", null, null);
////				if(blackUserList==null||!blackUserList.contains(uin)){//不在黑名单中可以处理推荐返现
////					isBlack=false;
////				}
////				if(!isBlack){
////					if(total>=1//&&memcacheUtils.readBackMoneyCache(comid+"_"+uin)){//可以给车场返现 
////						boolean isset = false;
////						boolean isCanBackMoney = publicMethods.isCanBackMoney(comid);//是否是济南车场
////						if(isCanBackMoney){
////							List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
////							Map<String, Object> userInfoSql = new HashMap<String, Object>();
////							Map<String, Object> parkAccountSql = new HashMap<String, Object>();
////							
////							userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
////							userInfoSql.put("values",new Object[]{2.0,uid});
////							insertSqlList.add(userInfoSql);
////							
////							parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) values(?,?,?,?,?,?)");
////							parkAccountSql.put("values",new Object[]{uid,2.0,0,ntime,"停车宝返现",3});
////							insertSqlList.add(parkAccountSql);
////							
////							isset = daService.bathUpdate(insertSqlList);
////							if(isset){
////								memcacheUtils.updateBackMoneyCache(comid+"_"+uin);
////							}
////						}
////					}else {
////						logger.info(">>>>>>>>>返现超过3次..."+comid+"_"+uin);
////					}
////				}else {
////					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不给车场返现 ......");
////				}
//////				if(total-ticketMoney>=1){
////				if(!isBlack){
////					publicMethods.backTicket(total-ticketMoney, orderId, uin,comid,"");
////				}else { 
////					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不能返红包......");
////				}
////				//给车主发消息
////				logService.insertMessage(comid, 2, uin,carNumber, orderId,total,"", 0,start,etime,9);
////				//logService.insertMessage(comid, 2, uid,carNumber, orderId,total,"", 0,start,etime,0);
////				return "1";
////			}catch (Exception e) {
////				//给车主发消息
////				logService.insertMessage(comid, -1, uin,carNumber, orderId,total,"", 0,start,etime,9);
////				//logService.insertMessage(comid, -1, uid,carNumber, orderId,total,"", 0,start,etime,0);
////				e.printStackTrace();
////				return "0";
////			}
////		}else {
////			//给车主发消息
////			logService.insertMessage(comid, -1, uin,carNumber, orderId,total,"", 0,start,etime,9);
////			//logService.insertMessage(comid, -1, uid,carNumber, orderId,total,"", 0,start,etime,0);
////			return  "0";
//		}
//	}
//	
//	/**
//	 * 验证车主手机
//	 * @param response
//	 * @param mobile
//	 * @return
//	 */
//	private Map getUserMap(String mobile,Long id) {
//		Map userMap =null;
//		if(mobile!=null){
//			userMap = daService.getPojo(
//					"select id,balance from user_info_Tb where mobile=? and auth_flag= ?",
//					new Object[] { mobile ,4});
//			
//		}else if(id!=null){
//			userMap = daService.getPojo(
//					"select id,balance from user_info_Tb where id=?",
//					new Object[] {id});
//		}
//		if (userMap != null && userMap.get("id") != null)
//			return userMap;
//		return null;
//	}
//	
}

package com.zld.impl;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import pay.Constants;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.Check;
import com.zld.utils.CountPrice;
import com.zld.utils.HttpProxy;
import com.zld.utils.SendMessage;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
import com.zld.utils.ZLDType;


/**
 * memcached工具，购买包月产品，支付订单，查询车牌号码 
 * @author Administrator
 *
 */

@Repository
public class PublicMethods {

	
	private Logger logger = Logger.getLogger(PublicMethods.class);
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	@Autowired
	private CommonMethods methods;
	

	/**
	 * 购买停车券
	 * @param uin   车主账户
	 * @param value 购买金额
	 * @param number购买数量
	 * @param ptype 支付类型 0余额 1支付宝 2微信，4余额+支付宝,5余额+微信
	 * @return 
	 */
//	public int buyTickets(Long uin, Integer value, Integer number,Integer ptype) {
//		logger.info("buyticket>>>uin:"+uin+",value"+value+",number:"+number+",ptype:"+ptype);
//		boolean isAuth = isAuthUser(uin);
//		//折扣
//		Double discount = Double.valueOf(CustomDefind.getValue("NOAUTHDISCOUNT"));
//		if(isAuth){
//			discount=Double.valueOf(CustomDefind.getValue("AUTHDISCOUNT"));
//		}
//		logger.info("buyticket>>>uin:"+uin+",discount"+discount);
//		 //账户余额支付
//		Double balance =null;
//		Map userMap = null;
//		//车主真实账户余额
//		userMap = daService.getPojo("select balance,wxp_openid from user_info_tb where id =?",	new Object[]{uin});
//		if(userMap!=null&&userMap.get("balance")!=null){
//			balance = Double.valueOf(userMap.get("balance")+"");
//		}
//		//每张应付金额
//		Double etotal =  StringUtils.formatDouble(value*discount);
//		//应付金额
//		Double total = StringUtils.formatDouble(etotal*number);
//		logger.info(uin+",balance:"+balance+",total:"+total);
//		logger.info("buyticket>>>uin:"+uin+",discount"+discount+",total:"+total+",balance:"+balance);
//		if(total>balance){//余额不足
//			logger.info(uin+",balance:"+balance+",total:"+total+",余额不足");
//			return -1;
//		}
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//更新用户余额
//		Map<String, Object> userAccSqlMap = new HashMap<String, Object>();
//		
//		Long ntime = System.currentTimeMillis()/1000;
//		Long ttime = TimeTools.getToDayBeginTime();
//	    userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		userSqlMap.put("values", new Object[]{total,uin});
//		bathSql.add(userSqlMap);
//		
//		userAccSqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,target) values(?,?,?,?,?,?,?)");
//		userAccSqlMap.put("values", new Object[]{uin,total,1,ntime,"购买停车券("+number+"张"+value+"元)",ptype,2});
//		bathSql.add(userAccSqlMap);
//		if(number > 0){
//			for(int i=0;i<number;i++){
//				Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
//				ticketSqlMap.put("sql", "insert into ticket_tb (create_time,limit_day,money,pmoney,state,uin,type,resources) values(?,?,?,?,?,?,?,?)" );
//				ticketSqlMap.put("values",new Object[]{ttime,ttime+31*24*60*60-1,value,etotal,0,uin,0,1});
//				bathSql.add(ticketSqlMap);
//			}
//		}
//		boolean result = daService.bathUpdate(bathSql);
//		logger.info("uin:"+uin+",value:"+value+",number:"+number+",result:"+result);
//		if(result){
//			return 1;
//		} else {
//			return 0;
//		}
//	}
	
	
	/**
	 * 车主打赏收费员
	 * @param uin
	 * @param uid
	 * @param orderId
	 * @param ticketId
	 * @param money
	 * @param comId
	 * @param ptype 0余额，1支付宝，2微信，4余额+支付宝,5余额+微信,7停车券 
	 * @return
	 */
//	public int doparkUserReward(Long uin,Long uid,Long orderId,Long ticketId,Double money,Integer ptype,Integer bind_flag) {
//		logger.info("doparkUserReward>>>uin:"+uin+",uid:"+uid+",orderid:"+orderId+",money:"+money+",ptype"+ptype+",bind_flag:"+bind_flag);
//		Long comId = daService.getLong("select comid from user_info_tb where id=? ", new Object[]{uid});
//		//查停车券金额
//		Long count = daService.getLong("select count(id) from parkuser_reward_tb where uin=? and order_id=? ", new Object[]{uin,orderId});
//		if(count>0){
//			logger.info("已打赏过>>>uin:"+uin+",orderid:"+orderId+",uid:"+uid);
//			//已打赏过
//			return -2;
//		}
//		Long ntime = System.currentTimeMillis()/1000;
//		Double ticketMoney=0.0;
//		if(ticketId != null && ticketId>0){
//			ticketMoney = getTicketMoney(ticketId, 4, uid, money, 2, comId, orderId);
//		}
//		logger.info("uin:"+uin+",uid:"+uid+",orderid:"+orderId+",ticketMoney:"+ticketMoney+",money:"+money+",ticketid:"+ticketId);
//		//查用户余额
//		Map<String, Object> userMap = null;
//		Double ubalance =null;
//		//车主真实账户余额
//		if(bind_flag == 1){
//			userMap = daService.getPojo("select balance from user_info_tb where id =?",	new Object[]{uin});
//		}else{
//			userMap = daService.getPojo("select balance from wxp_user_tb where uin=? ", new Object[]{uin});
//		}
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			logger.info(":uin:"+uin+",uid:"+uid+",orderid:"+orderId+",ubalance:"+ubalance+",ticketMoney:"+ticketMoney);
//			ubalance +=ticketMoney;//用户余额加上优惠券金额
//		}
//		if(ubalance==null||ubalance<money){//帐户余额不足
//			logger.info("打赏账户余额不足，账户余额："+ubalance+",打赏费金额："+money+",uin:"+uin+",orderid:"+orderId+",ticketMoney:"+ticketMoney);
//			return -1;
//		}
//		logger.info("uin:"+uin+",orderid:"+orderId+",uid:"+uid+",ticketMoney:"+ticketMoney);
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//停车券
//		Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//用户账户
//		Map<String, Object> userAccSqlMap = new HashMap<String, Object>();
//		//收费员余额
//		Map<String, Object> parkuserSqlMap = new HashMap<String, Object>();
//		//收费员账户
//		Map<String, Object> parkuserAccSqlMap = new HashMap<String, Object>();
//		//停车宝账户
//		Map<String, Object> tingchebaoAccountsqlMap = new HashMap<String, Object>();
//		//打赏记录
//		Map<String, Object> prakuserRewardsqlMap = new HashMap<String, Object>();
//		
//		Map<String, Object> userTicketAccountsqlMap = new HashMap<String, Object>();
//		//打赏积分
//		Map<String, Object> rewardsqlMap = new HashMap<String, Object>();
//		//打赏积分明细
//		Map<String, Object> rewardAccountsqlMap = new HashMap<String, Object>();
//		
//		String carNumber = getCarNumber(uin);
//		if(ticketMoney>0){//用了停车券
//			ticketSqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,orderid=? where id=?");
//			ticketSqlMap.put("values", new Object[]{1,comId,ntime,ticketMoney,orderId,ticketId});
//			bathSql.add(ticketSqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,"车主"+carNumber+"，使用停车代金券打赏收费员"+uid,7,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
//		}
//		if(money>ticketMoney){//要车主余额支付
//			if(bind_flag == 1){
//				userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//			}else{
//				userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//			}
//			userSqlMap.put("values", new Object[]{money-ticketMoney,uin});
//			bathSql.add(userSqlMap);
//		}
//		
//		userAccSqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//		userAccSqlMap.put("values", new Object[]{uin,money,1,ntime,"打赏收费员-"+uid,ptype,orderId});
//		bathSql.add(userAccSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null){//使用停车券，给车主账户先充值
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,"打赏-停车券充值",7,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		//更新收费员账户
//		parkuserSqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//		parkuserSqlMap.put("values", new Object[]{money,uid});
//		bathSql.add(parkuserSqlMap);
//		
//		parkuserAccSqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//		parkuserAccSqlMap.put("values", new Object[]{uid,money,0,ntime,"打赏费_"+carNumber,4,orderId});
//		bathSql.add(parkuserAccSqlMap);
//		
//		Long rewardId = daService.getkey("seq_parkuser_reward_tb");
//		prakuserRewardsqlMap.put("sql", "insert into parkuser_reward_tb(id,uin,uid,money,ctime,comid,order_id,ticket_id) values(?,?,?,?,?,?,?,?)");
//		prakuserRewardsqlMap.put("values", new Object[]{rewardId,uin,uid,money,ntime,comId,orderId,ticketId});
//		bathSql.add(prakuserRewardsqlMap);
//		
//		//打赏积分
//		Long btime = TimeTools.getToDayBeginTime();
//		Long rewardCount = daService.getLong("select count(id) from parkuser_reward_tb where uid=? and ctime>=? ",
//				new Object[] { uid, btime });
//		Map<String, Object> tscoreMap = daService.getMap("select sum(score) tscore from reward_account_tb where type=? and create_time>? and uin=? ", new Object[]{0, btime, uid});
//		Double tscore = 0d;
//		Double rscore = (rewardCount+1)*money;
//		if(tscoreMap != null && tscoreMap.get("tscore") != null){
//			tscore = Double.valueOf(tscoreMap.get("tscore") + "");
//		}
//		logger.info("收费员今日积分收入：uid:"+uid+",tscore:"+tscore+",本次积分:"+rscore+",rewardCount:"+rewardCount);
//		if(tscore < 5000){//每天最多收入5000积分
//			if(tscore + rscore > 5000){
//				rscore = 5000 - tscore;
//				logger.info("今日积分已经达上限，tscore:"+tscore+",rscore:"+rscore+",uid:"+uid);
//			}
//			rewardsqlMap.put("sql", "update user_info_tb set reward_score=reward_score+? where id=? ");
//			rewardsqlMap.put("values", new Object[]{rscore, uid});
//			bathSql.add(rewardsqlMap);
//			
//			rewardAccountsqlMap.put("sql", "insert into reward_account_tb(uin,score,type,create_time,target,reward_id,remark) values(?,?,?,?,?,?,?) ");
//			rewardAccountsqlMap.put("values", new Object[]{uid, rscore, 0, ntime, 0, rewardId,"打赏 "+carNumber});
//			bathSql.add(rewardAccountsqlMap);
//		}
//		boolean result = daService.bathUpdate(bathSql);
//		logger.info("uin:"+uin+",uid:"+uid+",orderid:"+orderId+",result:"+result);
//		if(result){
//			if(ticketId > 0){//缓存处理
//				Map<Long, Long> tcacheMap = memcacheUtils.doMapLongLongCache("reward_userticket_cache", null, null);
//				Long ttime = TimeTools.getToDayBeginTime();
//				if(tcacheMap!=null){
//					tcacheMap.put(uin, ttime);
//				}else {
//					tcacheMap = new HashMap<Long, Long>();
//					tcacheMap.put(uin, ttime);
//				}
//				memcacheUtils.doMapLongLongCache("reward_userticket_cache", tcacheMap, "update");
//			}
//			
//			if(ticketMoney > 0){//更新每日补贴上限
////				updateAllowCache(comId, ticketId, ticketMoney);
//				logger.info("update allowance today>>>uin:"+uin+",orderid:"+orderId+",ticketMoney:"+ticketMoney);
//			}
//			return 1;
//		} else {
//			return 0;
//		}
//	}
	
	/**
	 * 
	 * @param uin 用户帐号
	 * @param pid 产品编号　
	 * @param number 购买数量
	 * @param start 开始时间，格式：20140815
	 * @param ptype :0余额，1支付宝，2微信，3网银，4余额+支付宝,5余额+微信,6余额+网银
	 * @return  0失败,1成功　-1余额不足
	 */
	public int buyProducts(Long uin,Map productMap,Integer number,String start,int ptype){
//		Map productMap = daService.getPojo("select * from product_package_tb where id=? and state=? and remain_number>?",
//				new Object[]{pid,0,0});
//		if(productMap==null||productMap.isEmpty())
//			return 0;
		Long pid = (Long)productMap.get("id");
		//1查询余额
		BigDecimal _balance  = (BigDecimal)daService.getObject("select balance from user_info_Tb where id=?",
				new Object[]{uin}, BigDecimal.class);
		Double balance = 0d;
		if(_balance!=null)
			balance = _balance.doubleValue();
		logger.info("当前客户余额："+balance);
		Double price = Double.valueOf(productMap.get("price")+"");
		//logger.info("产品价格:"+price);
		//2扣用户金额
		//3加入停车场帐号金额
		//登记用户包月产品
		logger.info("产品价格:"+price);
		
		Long comid = (Long)productMap.get("comid");
		
		boolean b = false;
		Double total = number*price;
		String time = start.substring(0,4)+"-"+start.substring(4,6)+"-"+start.substring(6);
		Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(time);
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(btime);
		calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+Integer.valueOf(number));
		Long etime = calendar.getTimeInMillis();
		if(balance>=total){//余额可以购买产品
			List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
			logger.info ("余额可以购买产品...");
			
			Map<String, Object> usersqlMap = new HashMap<String, Object>();
			usersqlMap.put("sql", "update user_info_tb set balance = balance-? where id=? ");
			usersqlMap.put("values", new Object[]{total,uin});
			sqlMaps.add(usersqlMap);
			
			Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
			userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
			userAccountsqlMap.put("values", new Object[]{uin,total,1,System.currentTimeMillis()/1000,"购买-"+productMap.get("p_name"),ptype});
			sqlMaps.add(userAccountsqlMap);
			
			Map<String, Object> comsqlMap = new HashMap<String, Object>();
			comsqlMap.put("sql", "update com_info_tb set money=money+?,total_money=total_money+? where id=? ");
			comsqlMap.put("values", new Object[]{total,total,comid});
			sqlMaps.add(comsqlMap);
			
			Map<String, Object> buysqlMap = new HashMap<String, Object>();
			buysqlMap.put("sql", "insert into carower_product(pid,uin,create_time,b_time,e_time,total) values(?,?,?,?,?,?)");
			buysqlMap.put("values", new Object[]{pid,uin,System.currentTimeMillis()/1000,btime/1000,etime/1000-1,total});
			sqlMaps.add(buysqlMap);
			
			Map<String, Object> ppSqlMap =new HashMap<String, Object>();
			ppSqlMap.put("sql", "update product_package_tb set remain_number=remain_number-? where id=?");
			ppSqlMap.put("values", new Object[]{1,pid});
			sqlMaps.add(ppSqlMap);
			
			b= daService.bathUpdate(sqlMaps);
			logger.info("购买产品成功...");
		}else 
			return -1;
		//4写用户扣费日志
		//5写车场充值日志
		if(b){//购买成功
			Map comMap = daService.getMap("select company_name from com_info_tb where id = ?", new Object[]{productMap.get("comid")});
			daService.update( "insert into money_record_tb(comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)", 
					new Object[]{productMap.get("comid"),System.currentTimeMillis()/1000,total,uin,
					ZLDType.MONEY_CONSUM,comMap.get("company_name")+" 购买-"+productMap.get("p_name"),ptype});
			logger.info("充值日志写入成功...");
			//发送短信 ,车场管理员及车主;
			
			Map userMap1 = daService.getMap("select mobile from user_info_tb where id=? ",new Object[]{uin});
			Map userMap2 = daService.getMap("select mobile,nickname from user_info_tb where comid=? and auth_flag=? limit ?", new Object[]{productMap.get("comid"),1,1});
			
			String umobile = userMap1.get("mobile")==null?"":userMap1.get("mobile")+"";//(String)daService.getObject("select mobile from user_info_tb where id=? ",new Object[]{uin},String.class);
			String pmobile = userMap2.get("mobile")==null?"":userMap2.get("mobile")+"";//(String)daService.getObject("select mobile from user_info_tb where comid=? and auth_flag=? ", new Object[]{productMap.get("comid"),1},String.class);
			String puserName = userMap2.get("nickname")==null?"":userMap2.get("nickname")+"";
			
			String exprise = "";
			//List userList = daService.getAll("select mobile,nickname,id from user_info_tb where (comid=? or id=?) ", new Object[]{uin});
			
			if(!umobile.equals(""))
				exprise = TimeTools.getTimeStr_yyyy_MM_dd(btime)+"至"+TimeTools.getTimeStr_yyyy_MM_dd(etime);
			String carNumber ="";
			if(!umobile.equals("")||!pmobile.equals(""))
				carNumber = getCarNumber(uin);//(String)daService.getObject("select id,car_number from car_info_tb where uin=? ", new Object[]{uin},String.class);
			//开始发短信
			if(!umobile.equals("")&&Check.checkMobile(umobile));
//				SendMessage.sendMessage(umobile, "尊敬的"+carNumber+"车主您好，您已通过停车宝购买"+comMap.get("company_name")+"包月服务，费用"+total+"元，有效期"+exprise+
//						"，您可以凭此短信到"+comMap.get("company_name")+"换取相应月卡。客服：01053618108 【停车宝】");
				SendMessage.sendMultiMessage(umobile, "尊敬的"+carNumber+"车主您好，您已通过停车宝购买"+comMap.get("company_name")+"包月服务，费用"+total+"元，有效期"+exprise+
						"，您可以凭此短信到"+comMap.get("company_name")+"换取相应月卡。确定月卡办理手续和时间可以提前和该车场负责人"+puserName+"(手机："+pmobile+")联系，其他疑问可咨询停车宝客服：01053618108 【停车宝】");
				
				
			if(!pmobile.equals("")&&Check.checkMobile(pmobile))
				SendMessage.sendMultiMessage(pmobile,"尊敬的合作伙伴您好，车主"+carNumber+"(手机："+umobile+")已通过停车宝购买贵车场包月服务1个月，费用"+total+"元，您可以在后台查看相应信息。"+
						"车主将凭短信前来换取月卡，您可以提前与之联系确认相应信息，并备好相应月卡，谢谢。客服：01053618108 【停车宝】");
				
//				SendMessage.sendMessage(pmobile, "尊敬的合作伙伴您好，车主"+carNumber+"已通过停车宝购买贵车场包月服务1个月，费用"+total+"元，您可以在后台查看相应信息。"+
//						"车主将凭短信前来换取月卡，请备好相应月卡，谢谢。客服：01053618108 【停车宝】");
			return 1;
		}
		return 0;
	}
	
	
	/**
	 * 
	 * @param orderId 订单编号
	 * @param total 总价
	 * @param uin 车主帐号
	 * @param type 支付方式：0余额,1现金,2手机
	 * @param ptype :0余额，1支付宝，2微信，3网银，4余额+支付宝,5余额+微信,6余额+网银
	 * @param ticketId:停车券编号 
	 * @return 0:失败　 5:成功 
	 *  -7:订单收费错误
	 *  -8:订单已结算，不能重启结算
	 *  -9:订单不存在
	 *  -10:停车场不存在
	 *  -12：余额不足 
	 *  -13:停车券使用超过3次
	 */
//	public int payOrder(Map orderMap,Double total,Long uin,Integer type,int ptype,Long ticketId,String wxp_orderid){
//		Long count = daService.getLong(
//				"select count(*) from user_info_tb where id=? ",
//				new Object[] { uin });
//		Integer bind_flag = 1;//默认绑定了账户 
//		if(count == 0){
//			bind_flag = 0;//没有绑定账户
//		}
//		Long comid = null;
//		Long uid = null;
//		Integer state = null;
//		String comName = "";
//		if(orderMap!=null){
//			state = (Integer)orderMap.get("state");
//			comid = (Long)orderMap.get("comid");
//			uid = (Long)orderMap.get("uid");
//			//查询公司信息
//			Map<String, Object> comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comid});
//			
//			if(comMap == null){
//				logService.insertUserMesg(0, uin, "停车场已不存在，请联系停车宝客服", "支付失败提醒");
//				return -10;
//			}
//			if(comMap != null && comMap.get("company_name") != null){
//				comName = (String)comMap.get("company_name");
//			}
//			if(state==1){//已完成过订单，返回错误消息
//				logger.info("payOrder>>>>>:订单结算过了orderid:"+orderMap.get("id")+",uin:"+uin+",c_type:"+orderMap.get("c_type")+",pay_type:"+orderMap.get("pay_type"));
//				if(type==2||type==0){//手机或余额支付，只更改订单的支付方式
//					total = Double.valueOf(orderMap.get("total")+"");
//					//处理现金明细
//					Integer pay_type = (Integer)orderMap.get("pay_type");
//					if(pay_type == 1){
//						int r = daService.update("update parkuser_cash_tb set amount=? where orderid=? and type=? ",
//										new Object[] { 0, orderMap.get("id"), 0 });
//						logger.info("payOrder>>>>>订单现金结算的，现在电子结算，把之前岗亭的现金明细金额设为0，orderid:"+orderMap.get("id")+",r:"+r);
//					}
//				}else {
//					logService.insertUserMesg(0, uin, comName+"，停车费"+total+"元，订单已支付，不能重复支付", "支付失败提醒");
//					return -8;
//				}
//			}
//			//duration =StringUtils.getTimeString(start, end);
//		}else {//订单不存在 ，返回错误消息
//			logService.insertUserMesg(0, uin, comName+"，停车费"+total+"元，订单不存在", "支付失败提醒");
//			return -9;
//		}
//		Long orderId = (Long)orderMap.get("id");
//		Integer payType = (Integer)orderMap.get("pay_type");
//		Long ntime = System.currentTimeMillis()/1000;
//		logger.info(">>>>>>>>>>ticket:"+ticketId+">>>>>>>>原订单支付方式："+payType);
//		if(payType!=null&&payType==2){//已支付，不能重复支付
//			logger.info("payOrder>>>>订单已经电子支付过了，不能重复支付，orderid:"+orderId+",uin:"+uin);
//			logService.insertUserMesg(0, uin, "订单已支付，不能重复支付", "支付失败提醒");
//			return -8;
//		}
//		//优惠券金额
//		Double ticketMoney = 0d;
//		Integer ticket_type = 7;//7：停车券，11：微信打折券
//		String ticket_dp = "停车券充值";
//		if(ticketId != null &&  ticketId == -100){//微信五折券
//			ticketMoney = getDisTicketMoney(uin, uid, total);
//			ticket_type = 11;
//			ticket_dp = "微信打折券充值";
//			logger.info("orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney);
//		}else if(ticketId != null && ticketId > 0){
//			ticketMoney = getTicketMoney(ticketId, 2, uid, total, 2, comid, orderId);
//		}
//		logger.info("orderid:"+orderId+",uin:"+uin+",ticketid:"+ticketId+",ticketMoney:"+ticketMoney);
//		Double ubalance =null;
//	    //账户余额支付
//		Map<String, Object> userMap = null;
//		if(bind_flag == 1){//已绑定账户
//			//车主真实账户余额
//			userMap = daService.getPojo("select balance,wxp_openid from user_info_tb where id =?",	new Object[]{uin});
//		}else{//未绑定账户
//			//虚拟账户余额
//			userMap = daService.getPojo("select balance,openid from wxp_user_tb where uin =?",	new Object[]{uin});
//		}
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			ubalance +=ticketMoney;//用户余额加上优惠券金额
//		}
//		if(ubalance==null||ubalance<total){//帐户余额不足
//			logger.info("余额不足payOrder>>>orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney+",balance:"+userMap.get("balance")+",total:"+total);
//			logService.insertUserMesg(0, uin, comName+"，停车费"+total+"元，余额不足("+ubalance+")", "支付失败提醒");
//			return -12;
//		}
//		String carNumber=null;//车主车牌号
//		Map carNuberMap = daService.getPojo("select * from car_info_tb where uin=? and state=?", 
//				new Object[]{uin,1});
//		if(carNuberMap!=null&&carNuberMap.get("car_number")!=null)
//			carNumber = (String)carNuberMap.get("car_number");
//		
//		//查询收费设定 mtype:0:停车费,1:预订费,2:停车宝返现
//		Map msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//				new Object[]{comid,0});
//		Integer giveTo =null;
//		if(msetMap!=null)
//			giveTo =(Integer)msetMap.get("giveto");
//		logger.info(">>>>>>"+msetMap+">>>>>giveto:"+giveTo+"comid:"+comid+",uin:"+uid);
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新订单状态，收费成功
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//更新停车场余额
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//消费流水
//		Map<String, Object> consumptionSqlMap = new HashMap<String, Object>();
//		//收费员账户
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//车主账户
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//车场账户
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
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
//		orderSqlMap.put("values", new Object[]{1,type,etime,total,orderId});
//		bathSql.add(orderSqlMap);
//		
//		//扣除车主账户余额
//		if(bind_flag == 1){
//			userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		}else{
//			userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//		}
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		
//		
//		consumptionSqlMap.put("sql", "insert into  money_record_tb  (comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)");
//		consumptionSqlMap.put("values", new Object[]{comid,ntime,total,uin,ZLDType.MONEY_CONSUM,"停车费-"+comName,ptype});
//		bathSql.add(consumptionSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null){//使用停车券，给车主账户先充值
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,ticket_dp,ticket_type,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"停车费-"+comName,ptype,orderId});
//		bathSql.add(userAccountsqlMap);
//		
//		//不设置默认给个人账户20141120孙总要求修改
//		if(giveTo!=null&&giveTo==0){//写入公司账户
//			comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
//			comSqlMap.put("values", new Object[]{total,total,comid});
//			bathSql.add(comSqlMap);
//			
//			parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//			parkAccountsqlMap.put("values",  new Object[]{comid,total,0,ntime,"停车费_"+orderMap.get("car_number"),orderMap.get("uid"),0,orderId});
//			bathSql.add(parkAccountsqlMap);
//		}else {//写入个人账户
//			parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//			parkusersqlMap.put("values", new Object[]{total,orderMap.get("uid")});
//			bathSql.add(parkusersqlMap);
//			
//			parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//			parkuserAccountsqlMap.put("values", new Object[]{orderMap.get("uid"),total,0,ntime,"停车费_"+orderMap.get("car_number"),4,orderId});
//			bathSql.add(parkuserAccountsqlMap);
//		}
//		//优惠券使用后，更新券状态，添加停车宝账户支付记录
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,orderid=?,wxp_orderid=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comid,System.currentTimeMillis()/1000,ticketMoney,orderId,wxp_orderid,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"，车主"+orderMap.get("car_number")+"，使用停车代金券",0,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
////			memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info(">>>>>>>>>>>>>>>支付 ："+result);
//		if(result){//结算成功，处理返券及返现 
//			//处理返现（车场），返券（车主）
//			/* 每次用余额或微信或支付宝支付1元以上的完成的，补贴车场2元，补贴车主3元的停车券，
//			 * 车场返现不限(同一车主每日只能返3次)，
//			 * 车主每日返券限3张券
//			 * 每个车主每天使用停车券不超过3单 */
//			try {
//				boolean isBlack = isBlackUser(uin);
//				if(!isBlackUser(uin)){
//					if(total>=1){//&&memcacheUtils.readBackMoneyCache(orderMap.get("comid")+"_"+uin)){//可以给车场返现 
//						boolean isCanBackMoney = isCanBackMoney(comid);//是否是济南车场
//						Double backmoney = getBackMoney();
//						logger.info("payorder>>>>>:orderid:"+orderId+",backmoney:"+backmoney+",isCanBackMoney:"+isCanBackMoney);
//						if(isCanBackMoney && backmoney > 0){
//							boolean isset = false;
//							Integer giveMoneyTo = null;
//							msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comid,2});
//							if(msetMap!=null)
//								giveMoneyTo =(Integer)msetMap.get("giveto");
//							if(giveMoneyTo!=null&&giveMoneyTo==0){//返现给车场账户
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> comInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								comInfoSql.put("sql", "update com_info_tb set money=money+?, total_money=total_money+? where id=?");
//								comInfoSql.put("values",new Object[]{backmoney,backmoney,comid});
//								parkAccountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{orderMap.get("comid"),backmoney,2,ntime,"停车宝返现",uid,1,orderId});
//								insertSqlList.add(comInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}else {//返现给收费员账户
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> userInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
//								userInfoSql.put("values",new Object[]{backmoney,uid});
//								parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{uid,backmoney,0,ntime,"停车宝返现",3,orderId});
//								insertSqlList.add(userInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}
//							logger.info(">>>>>>>>>>>>停车宝返现给"+giveMoneyTo+",结果："+isset+",更新缓存 ");
//							if(isset){
////								memcacheUtils.updateBackMoneyCache(orderMap.get("comid")+"_"+uin);
//							}
//						}
//					}else {
//						logger.info(">>>>>total:"+total+">>>>返现超过1次..."+orderMap.get("comid")+"_"+uin);
//					}
//				}else {
//					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不给车场返现 ......");
//				}
//			
//				try {
//					//处理5折券，在停车券表不存在 ，插入一条记录，方便对账
//					if(ticket_type==11&&ticketId<0&&ticketMoney>0){
//						int ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type,orderid,utime,umoney,wxp_orderid)" +
//								" values(?,?,?,?,?,?,?,?,?,?,?) ",
//								new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+24*60*60-1,5,1,uin,comid,2,orderId,ntime,ticketMoney,wxp_orderid});
////						memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//						logger.info(">>>pay order ,没有五折券时，写一条记录，结果:"+ret);
//					}
//				} catch (Exception e) {
//					logger.info(">>>pay order error,写入五折券错误："+e.getMessage());
//				}
//				
//				if(ticketMoney > 0){
////					updateAllowCache(comid, ticketId, ticketMoney);
//					logger.info("update allowance cache>>>uin:"+uin+",ticketMoney:"+ticketMoney);
//				}
//				
//				if(!isBlack){
//					backTicket(total-ticketMoney, orderId, uin,comid,wxp_orderid);
//				}else {
//					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不给红包 ......");
//				}
//				if(total>=1){
//					handleRecommendCode(uin,isBlack);
//				}
//				//写系统日志 
//				String time = TimeTools.gettime();
//				if(state!=null&&state==0){
//					logService.updateOrderLog(comid,uin,time+",帐号："+uin+",车牌："+carNumber+",停车收费："+total+",停车场："+comName,1);
//				}
//			} catch (Exception e) {
//				logger.info(">>>>>>>>>>停车宝返现、停车宝返券失败！...............");
//				e.printStackTrace();
//			}
//			
//			try {//直付完成后发送消息
//				String openid = "";
//				String url = "";
//				if(bind_flag == 1){
//					openid = (String)userMap.get("wxp_openid");
//					url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toaccountdetail&openid="+openid;
//				} else {
//					openid = (String)userMap.get("openid");
//					url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
//				}
//				if(openid!=null&&openid.length()>10 && (ptype == 10 || ptype == 9)){
//					logger.info(">>>订单支付成功，通过微信发消息给车主...orderpaymsg:openid:"+openid+",uin："+uin);
//					Map<String, String> baseinfo = new HashMap<String, String>();
//					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//					String remark = "点击详情查看账户明细！";
//					String remark_color = "#000000";
//					Map bMap  =daService.getMap("select * from order_ticket_tb where uin=? and  order_id=? and ctime>? order by ctime desc limit ?",
//							new Object[]{uin,orderId,System.currentTimeMillis()/1000-5*60, 1});//五分钟前的红包
//					
//					if(bMap!=null&&bMap.get("id")!=null){
//						Integer bonus_type = 0;//0:普通订单红包，1：微信折扣红包
//						if(bMap.get("type")!= null && (Integer)bMap.get("type") == 1){
//							bonus_type = 1;//微信打折红包
//						}
//						if(bonus_type == 1){
//							remark = "恭喜您获得"+bMap.get("bnum")+"个微信"+bMap.get("money")+"折券礼包，点击分享吧！";
//						}else{
//							remark = "恭喜您获得"+bMap.get("bnum")+"个共"+bMap.get("money")+"元停车券礼包，点击分享吧！";
//						}
//						remark_color = "#FF0000";
//						Integer first_flag = 0;
//						Long first = daService.getLong("select count(*) from user_account_tb where uin=? and type=? ", new Object[]{uin, 1});
//						logger.info("是否是首单支付>>>>orderid:"+orderId+",uin:"+uin+",openid:"+openid+",first:"+first+",time:"+System.currentTimeMillis()/1000);
//						if(first == 1){
//							first_flag = 1;
//						}
//						url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpublic.do?action=balancepayinfo&openid="+openid+"&money="+total+"&bonusid="+bMap.get("id")+"&bonus_type="+bonus_type+"&orderid="+orderId+"&paytype=1"+"&first_flag="+first_flag;
//					}
//					Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{uid});
//					String first = "您在"+comName+"向收费员"+uidMap.get("nickname")+"付费成功！";
//					baseinfo.put("url", url);
//					baseinfo.put("openid", openid);
//					baseinfo.put("top_color", "#000000");
//					baseinfo.put("templeteid", Constants.WXPUBLIC_SUCCESS_NOTIFYMSG_ID);
//					Map<String, String> keyword1 = new HashMap<String, String>();
//					keyword1.put("keyword", "orderMoneySum");
//					keyword1.put("value", total+"元");
//					keyword1.put("color", "#000000");
//					orderinfo.add(keyword1);
//					Map<String, String> keyword2 = new HashMap<String, String>();
//					keyword2.put("keyword", "orderProductName");
//					keyword2.put("value", "停车费");
//					keyword2.put("color", "#000000");
//					orderinfo.add(keyword2);
//					Map<String, String> keyword3 = new HashMap<String, String>();
//					keyword3.put("keyword", "Remark");
//					keyword3.put("value", remark);
//					keyword3.put("color", remark_color);
//					orderinfo.add(keyword3);
//					Map<String, String> keyword4 = new HashMap<String, String>();
//					keyword4.put("keyword", "first");
//					keyword4.put("value", first);
//					keyword4.put("color", "#000000");
//					orderinfo.add(keyword4);
//					sendWXTempleteMsg(baseinfo, orderinfo);
//					
//					sendBounsMessage(openid,uid,2d,orderId ,uin);//发打赏消息
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			//更新缓存
//			
//			//写消息表
////			logService.doMessage(comid,type,uin,carNumber,orderId,total,duration,0,
////					(Long)orderMap.get("create_time"),ntime);
//			//更新订单消息中的状态 
////			daService.update("update order_message_tb set state=? where orderid=?", 
////					new Object[]{1,orderId});
//			return 5;
//		}else {
//			return -7;
//		}
//	}
	
	/**
	 * 订单预支付
	 * @param orderid 订单编号
	 * @param total 预支付金额
	 * @param uin 车主账号
	 * @param ticketId 停车券号
	 * @param bind_flag 0：未绑定账户 1：绑定了账户
	 * @param ptype 支付类型  0余额，1支付宝，2微信，3网银，4余额+支付宝,5余额+微信,6余额+网银 ,7停车宝充值,8活动奖励,9微信公众号,10余额+微信公众号
	 */
	
//	public int prepay(Map orderMap, Double total, Long uin, Long ticketId, Integer ptype, Integer bind_flag, String wxp_orderid){
//		logger.info(">>>>>>>>>>>>>>>>>进入预支付：orderid"+orderMap.get("id")+",uin:"+uin+",已经预支付的金额:"+orderMap.get("total"));
//		DecimalFormat dFormat = new DecimalFormat("#.00");
//		Long comid = null;
//		Integer state = null;//(Integer)orderMap.get("state");
//		String comName = "";
//		Long uid =null;
//		if(orderMap!=null){
//			state = (Integer)orderMap.get("state");
//			comid = (Long)orderMap.get("comid");
//			uid  = (Long)orderMap.get("uid");
//			//查询公司信息
//			Map comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comid});
//			if(comMap!=null){
//				comName=(String)comMap.get("company_name");
//			}else {//订单不存在 ，返回错误消息
//				logger.info(">>>>>>>>>>>>>>停车场已不存在，支付失败>>>>>>>>>>>>");
//				logService.insertUserMesg(0, uin, "停车场已不存在，请联系停车宝客服", "支付失败提醒");
//				return -10;
//			}
//			
//			if(state==1){//已完成过订单，返回错误消息
//				logger.info(">>>>>>>>>>>>>该订单已支付，不能重复支付，支付失败，orderid:"+orderMap.get("id"));
//				
//				try {//直付完成后发送消息
//					String openid = "";
//					String url = "";
//					Map userMap = daService.getMap("select wxp_openid from user_info_Tb where id = ? ", new Object[]{uin});
//					if(userMap!=null){
//						logger.info(">>>>>>>>>已经绑定账户，uin："+uin);
//						openid = (String)userMap.get("wxp_openid");
//						url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toaccountdetail&openid="+openid;
//					} else {
//						logger.info(">>>>>>>>>>>未绑定账户，uin："+uin);
//						userMap = daService.getMap("select openid from wxp_user_Tb where uin= ? ", new Object[]{uin});
//						if(userMap!=null){
//							openid = (String)userMap.get("openid");
//							url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
//						}	
//					}
//					
//					if(openid!=null&&openid.length()>10 && (ptype == 10 || ptype == 9)){
//						logger.info(">>>预支付失败，因为订单已结算，通过微信发消息给车主...directpaymsg:openid:"+openid+",uin："+uin+",orderid:"+orderMap.get("id")+",预支付金额total:"+total);
//						
//						Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{orderMap.get("uid")});
//						String first = "您在"+comMap.get("company_name")+"向收费员"+uidMap.get("nickname")+"付费失败！";
//						Map<String, String> baseinfo = new HashMap<String, String>();
//						List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//						baseinfo.put("url", url);
//						baseinfo.put("openid", openid);
//						baseinfo.put("top_color", "#000000");
//						baseinfo.put("templeteid", Constants.WXPUBLIC_FAIL_NOTIFYMSG_ID);
//						Map<String, String> keyword1 = new HashMap<String, String>();
//						keyword1.put("keyword", "keyword1");
//						keyword1.put("value", total+"元");
//						keyword1.put("color", "#000000");
//						orderinfo.add(keyword1);
//						Map<String, String> keyword2 = new HashMap<String, String>();
//						keyword2.put("keyword", "keyword2");
//						keyword2.put("value", "停车费");
//						keyword2.put("color", "#000000");
//						orderinfo.add(keyword2);
//						Map<String, String> keyword3 = new HashMap<String, String>();
//						keyword3.put("keyword", "keyword3");
//						keyword3.put("value", "该订单预支付前已结算，支付的停车费"+total+"元已返还到您的停车宝账户！");
//						keyword3.put("color", "#FF0000");
//						orderinfo.add(keyword3);
//						Map<String, String> keyword4 = new HashMap<String, String>();
//						keyword4.put("keyword", "remark");
//						keyword4.put("value", "点击详情查账户明细！");
//						keyword4.put("color", "#000000");
//						orderinfo.add(keyword4);
//						Map<String, String> keyword5 = new HashMap<String, String>();
//						keyword5.put("keyword", "first");
//						keyword5.put("value", first);
//						keyword5.put("color", "#000000");
//						orderinfo.add(keyword5);
//						sendWXTempleteMsg(baseinfo, orderinfo);
//					}
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//				
//				logService.insertUserMesg(0, uin, comName+"，停车费"+total+"元，订单已支付，不能重复支付", "支付失败提醒");
//				return -8;
//			}
//			//duration =StringUtils.getTimeString(start, end);
//		}else {//订单不存在 ，返回错误消息
//			logger.info(">>>>>>>>>>>>>>订单不存在>>>>>>>>>>>>>>");
//			logService.insertUserMesg(0, uin, comName+"，停车费"+total+"元，订单不存在", "支付失败提醒");
//			return -9;
//		}
//		
//		Long orderId = (Long)orderMap.get("id");
//		Integer payType = (Integer)orderMap.get("pay_type");
//		Long ntime = System.currentTimeMillis()/1000;
//		logger.info(">>>>>>>>>>ticket:"+ticketId+">>>>>>>>原订单支付方式："+payType);
//		if(payType!=null&&payType==2){//已支付，不能重复支付
//			logger.info(">>>>>>>>>>>已支付不能重复支付，payType:"+payType);
//			logService.insertUserMesg(0, uin, "订单已支付，不能重复支付", "支付失败提醒");
//			return -8;
//		}//优惠券金额
//		Double ticketMoney = 0d;
//		Integer ticket_type = 7;//7：停车券，11：微信打折券
//		String ticket_dp = "停车券充值";
//		if(ticketId!= null && ticketId == -100){//微信五折券
//			ticketMoney = getDisTicketMoney(uin, uid, total);
//			ticket_type = 11;
//			ticket_dp = "微信打折券充值";
//			logger.info("orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney);
//		}else if(ticketId!=null&&ticketId>0){
//			ticketMoney = getTicketMoney(ticketId, 2, uid, total, 2, comid, orderId);
//		}
//		ticketMoney = Double.valueOf(dFormat.format(ticketMoney));
//		logger.info("orderid:"+orderId+",uin:"+uin+",ticketid:"+ticketId+",ticketMoney:"+ticketMoney);
//		Double ubalance =null;
//		Map userMap = null;
//		if(bind_flag == 0){
//			//账户余额支付
//			userMap = daService.getPojo("select balance from wxp_user_tb where uin =?", new Object[]{uin});
//		}else{
//			//账户余额支付
//			userMap = daService.getPojo("select balance from user_info_tb where id =?", new Object[]{uin});
//		}
//	    
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			ubalance +=ticketMoney;//用户余额加上优惠券金额
//		}
//		if(ubalance==null||ubalance<total){//帐户余额不足
//			logger.info("预支付停车费余额不足,uin:"+uin+",用户余额:"+userMap.get("balance")+",停车费金额:"+total+",orderid:"+orderId+",ticketMoney:"+ticketMoney);
//			logService.insertUserMesg(0, uin, comName+"，停车费"+total+"元，余额不足("+ubalance+")", "支付失败提醒");
//			return -12;
//		}
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新订单状态，收费成功
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//消费流水
//		Map<String, Object> consumptionSqlMap = new HashMap<String, Object>();
//		//车主账户
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//车主账户加停车券
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//使用停车券更新
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//停车宝账户扣停车券金额
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		//预支付状态
//		Map<String, Object> prestatesqlMap = new HashMap<String, Object>();
//		
//		Double ntotal = 0d;
//		if(orderMap.get("total") != null){
//			ntotal = Double.valueOf(orderMap.get("total") + "");
//		}
//		ntotal += total;//预支付总金额
//		
//		orderSqlMap.put("sql", "update order_tb set total=?,uin=? where id=?");
//		orderSqlMap.put("values", new Object[]{ntotal,uin,orderId});
//		bathSql.add(orderSqlMap);
//		if(bind_flag == 0){//未绑定账户情况下
//			userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//		}else{//真实帐户
//			userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		}
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		
//		consumptionSqlMap.put("sql", "insert into  money_record_tb  (comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)");
//		consumptionSqlMap.put("values", new Object[]{comid,ntime,total,uin,ZLDType.MONEY_CONSUM,"预支付停车费-"+comName,ptype});
//		bathSql.add(consumptionSqlMap);
//		
//		if(ticketMoney>0&&ticketId!=null){//使用停车券，给车主账户先充值
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,ticket_dp,ticket_type,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,uid,orderid) values(?,?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"预支付停车费-"+comName,ptype,orderMap.get("uid"),orderId});
//		bathSql.add(userAccountsqlMap);
//		
//		//优惠券使用后，更新券状态，添加停车宝账户支付记录
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,orderid=?,wxp_orderid=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comid,System.currentTimeMillis()/1000,ticketMoney,orderId,wxp_orderid,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"，车主"+orderMap.get("car_number")+"，使用停车代金券",0,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
////			memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//		}
//		
//		prestatesqlMap.put("sql", "update order_tb set pre_state=? where id=? ");
//		prestatesqlMap.put("values", new Object[]{0,orderId});
//		bathSql.add(prestatesqlMap);
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info("预支付 结果："+result+",orderid:"+orderId+",uin:"+uin);
//		if(!result){
//			return -7;
//		}else {
//			//处理5折券，在停车券表不存在 ，插入一条记录，方便对账
//			try {
//				int ret =0;
//				if(ticketId==-101&&ticketMoney>0){
//					ret = daService.update("update ticket_tb set umoney = umoney+?,wxp_orderid=? where type=? and orderid=? ", new Object[]{ticketMoney,wxp_orderid,2,orderId});
//					logger.info(">>>prepay order ,更新五折券使用金额，ticketmoney="+ticketMoney+",orderid="+orderId+",结果:"+ret);
//				}else if(ticketId==-100&&ticketMoney>0){
//					ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type,orderid,utime,umoney,wxp_orderid)" +
//							" values(?,?,?,?,?,?,?,?,?,?,?) ",
//							new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+24*60*60-1,5,1,uin,comid,2,orderId,ntime,ticketMoney,wxp_orderid});
////					memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//					logger.info(">>>prepay order ,没有五折券时，写一条记录，结果:"+ret);
//				}
//			} catch (Exception e) {
//				logger.info(">>>prepay 写停车券错误 ：error:"+e.getMessage());
//			}
//			
//			if(ticketMoney > 0){
////				updateAllowCache(comid, ticketId, ticketMoney);
//				logger.info("update allowance cache>>>uin:"+uin+",ticketMoney:"+ticketMoney);
//			}
//		}
//		return 1;
//	}
	
	
	/**
	 * 处理预付费订单
	 * @param orderMap 订单
	 * @param total 实收金额
	 * @return 0失败 1成功
	 */
//	public Integer doPrePayOrder(Map orderMap,Double total){
//		logger.info(">>>>>>>>>>>处理预付费订单，orderid:"+orderMap.get("id")+",预支付金额："+orderMap.get("total")+",uin:"+orderMap.get("uin")+",停车费金额："+total);
//		Long comid = null;
//		Integer state = null;
//		String comName = "";
//		
//		comid = (Long)orderMap.get("comid");
//		Long uin = (Long)orderMap.get("uin");
//		Long uid = (Long)orderMap.get("uid");
//		Long orderId = (Long)orderMap.get("id");
//		String carNumber = (String)orderMap.get("car_number");
//		if(carNumber==null||"".equals(carNumber))
//			carNumber = uin+"";
//		Double prefee = StringUtils.formatDouble(orderMap.get("total"));//预付费金额
//		Long ntime = System.currentTimeMillis()/1000;
//		
//		//查询收费设定 mtype:0:停车费,1:预订费,2:停车宝返现
//		Map msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//				new Object[]{comid,0});
//		Integer giveTo =null;
//		if(msetMap!=null)
//			giveTo =(Integer)msetMap.get("giveto");
//		logger.info(">>>>>>"+msetMap+">>>>>giveto:"+giveTo+"comid:"+comid+",uin:"+uid);
//		
//		//查询公司信息
//		Map<String, Object> comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comid});
//		if(comMap != null && comMap.get("company_name") != null){
//			comName = (String)comMap.get("company_name");
//		}
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新订单状态，收费成功
//		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
//		//更新停车场余额
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//消费流水
//		Map<String, Object> consumptionSqlMap = new HashMap<String, Object>();
//		//收费员账户
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//车场账户
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
//		//车场账户1
//		Map<String, Object> cashsqlMap =new HashMap<String, Object>();
//		//收费员余额
//		Map<String, Object> parkusersqlMap =new HashMap<String, Object>();
//		
//		Long etime = System.currentTimeMillis()/1000;
////		if(start!=null&&start==etime)
////			etime = etime+60;
//		orderSqlMap.put("sql", "update order_tb set state =?,pay_type=?, end_time=?,total=? where id=?");
//		orderSqlMap.put("values", new Object[]{1,2,etime,total,orderId});
//		bathSql.add(orderSqlMap);
//		
//		consumptionSqlMap.put("sql", "insert into  money_record_tb  (comid,create_time,amount,uin,type,remark,pay_type) values (?,?,?,?,?,?,?)");
//		consumptionSqlMap.put("values", new Object[]{comid,ntime,total,uin,ZLDType.MONEY_CONSUM,"停车费-"+comName,2});
//		bathSql.add(consumptionSqlMap);
//		
//		//不设置默认给个人账户20141120孙总要求修改
////		if(giveTo!=null&&giveTo==0){//写入公司账户
////			if(prefee<total){//支付金额不足时，加一笔现金收取
////				parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
////				parkAccountsqlMap.put("values",  new Object[]{comid,prefee,0,ntime,"停车费_"+carNumber,orderMap.get("uid"),0,orderId});
////				bathSql.add(parkAccountsqlMap);
////				
////				comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
////				comSqlMap.put("values", new Object[]{prefee,prefee,comid});
////				bathSql.add(comSqlMap);
////				
////				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
////				cashsqlMap.put("values",  new Object[]{uid,(total-prefee),0,orderId,ntime});
////				bathSql.add(cashsqlMap);
////				
////			}else {
////				parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
////				parkAccountsqlMap.put("values",  new Object[]{comid,total,0,ntime,"停车费_"+carNumber,orderMap.get("uid"),0,orderId});
////				bathSql.add(parkAccountsqlMap);
////				
////				comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
////				comSqlMap.put("values", new Object[]{total,total,comid});
////				bathSql.add(comSqlMap);
////			}
////			
////		}else {//写入个人账户
//			
//			if(prefee<total){//支付金额不足时，加一笔现金收取
//				parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//				parkuserAccountsqlMap.put("values", new Object[]{orderMap.get("uid"),prefee,0,ntime,"停车费_"+carNumber,4,orderId});
//				bathSql.add(parkuserAccountsqlMap);
//				
//				parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//				parkusersqlMap.put("values", new Object[]{prefee,orderMap.get("uid")});
//				bathSql.add(parkusersqlMap);
//				
//				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
//				cashsqlMap.put("values", new Object[]{uid,(total-prefee),0,orderId,ntime});
//				bathSql.add(cashsqlMap);
//				
//			}else {
//				parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//				parkuserAccountsqlMap.put("values", new Object[]{orderMap.get("uid"),total,0,ntime,"停车费_"+carNumber,4,orderId});
//				bathSql.add(parkuserAccountsqlMap);
//				
//				parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//				parkusersqlMap.put("values", new Object[]{total,orderMap.get("uid")});
//				bathSql.add(parkusersqlMap);
//			}
//			
////		}
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info("预支付结算结果 ："+result+",orderid:"+orderId+",uin:"+uin);
//		if(result){//结算成功，处理返券及返现 
//			Double back = 0d;
//			Double tcbback = 0d;
//			if(prefee>total){//多余金额退回车主微信钱包
//				logger.info("预支付金额大于停车费金额,预支付金额："+prefee+",停车费金额："+total+",orderid:"+orderId+",uin:"+uin);
//				List<Map<String, Object>> backSqlList = new ArrayList<Map<String,Object>>();
//				DecimalFormat dFormat = new DecimalFormat("#.00");
//				//如果用过三折券，就一直用三折券
//				Map<String, Object> ticketMap = daService.getMap(
//						"select * from ticket_tb where orderid=? order by utime limit ?",
//						new Object[] { orderId,1});
//				if(ticketMap != null){
//					Long ticketId = (Long)ticketMap.get("id");
//					logger.info("使用过券，ticketid:"+ticketId+",orderid="+orderId+",uin:"+uin);
//					Double umoney = Double.valueOf(ticketMap.get("umoney")+"");
//					umoney = Double.valueOf(dFormat.format(umoney));
//					Double preupay = Double.valueOf(dFormat.format(prefee - umoney));
//					logger.info("预支付金额prefee："+prefee+",使用券的金额umoney："+umoney+",车主实际支付的金额："+preupay+",orderid:"+orderId);
//					Double tmoney = 0d;
//					Integer type = (Integer)ticketMap.get("type");
//					if(type == 0 || type == 1){//代金券
//						tmoney = getTicketMoney(ticketId, 2, uid, total, 2, comid, orderId);
//						logger.info("orderid:"+orderId+",uin:"+uin+",tmoney:"+tmoney);
//					}else if(type == 2){
//						tmoney = getDisTicketMoney(uin, uid, total);
//						logger.info("orderid:"+orderId+",uin:"+uin);
//					}
//					Double upay = Double.valueOf(dFormat.format(total - tmoney));
//					logger.info("实际停车费total:"+total+",实际停车费应该打折的金额tmoney:"+tmoney+",实际停车费车主实际应该支付的金额upay："+upay+",orderid:"+orderId);
//					if(preupay > upay){
//						back = Double.valueOf(dFormat.format(preupay - upay));
//						logger.info("preupay:"+preupay+",upay:"+upay+",orderid:"+orderId+",uin:"+uin);
//					}
//					if(umoney > tmoney){
//						tcbback = Double.valueOf(dFormat.format(umoney - tmoney));
//					}
//					int r = daService.update("update ticket_tb set bmoney = ? where id=? ", new Object[]{tmoney, ticketMap.get("id")});
//				}else{
//					logger.info("没有使用过券orderid:"+orderId+",uin:"+uin);
//					back = Double.valueOf(dFormat.format(prefee - total));
//				}
//				logger.info("预支付退还金额:"+back+",停车券返款金额：tcbback:"+tcbback);
//				if(back > 0){
//					Long count = daService.getLong("select count(*) from user_info_tb where id=? ", new Object[]{uin});
//					Map<String, Object> usersqlMap = new HashMap<String, Object>();
//					if(count > 0){//真实帐户
//						usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
//						usersqlMap.put("values", new Object[]{back,uin});
//						backSqlList.add(usersqlMap);
//					}else{//虚拟账户
//						usersqlMap.put("sql", "update wxp_user_tb set balance=balance+? where uin=? ");
//						usersqlMap.put("values", new Object[]{back,uin});
//						backSqlList.add(usersqlMap);
//					}
//					Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
//					userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//					userAccountsqlMap.put("values", new Object[]{uin,back,0,System.currentTimeMillis() / 1000 - 2,"预支付返款", 12, orderId });
//					backSqlList.add(userAccountsqlMap);
//					if(tcbback > 0){
//						Map<String, Object> tcbbacksqlMap = new HashMap<String, Object>();
//						tcbbacksqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//						tcbbacksqlMap.put("values", new Object[]{tcbback,0,ntime,"停车券返款金额",6,orderId});
//						backSqlList.add(tcbbacksqlMap);
//					}
//					
//					boolean b = daService.bathUpdate(backSqlList);
//					logger.info("预支付返款结果："+b+",orderid:"+orderId+",uin:"+uin);
//				}else{
//					logger.info("退还金额back小于0，orderid："+orderId+",uin:"+uin);
//				}
//			}
//			try {
//				boolean isBlack = isBlackUser(uin);
//				if(!isBlackUser(uin)){
//					if(total>=1){//&&memcacheUtils.readBackMoneyCache(orderMap.get("comid")+"_"+uin)){//可以给车场返现 
//						boolean isCanBackMoney = isCanBackMoney(comid);//是否是济南车场
//						Double backmoney = getBackMoney();
//						logger.info("payorder>>>>>:orderid:"+orderId+",backmoney:"+backmoney+",isCanBackMoney:"+isCanBackMoney);
//						if(isCanBackMoney && backmoney > 0){
//							boolean isset = false;
//							Integer giveMoneyTo = null;
//							msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comid,2});
//							if(msetMap!=null)
//								giveMoneyTo =(Integer)msetMap.get("giveto");
//							if(giveMoneyTo!=null&&giveMoneyTo==0){//返现给车场账户
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> comInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								comInfoSql.put("sql", "update com_info_tb set money=money+?, total_money=total_money+? where id=?");
//								comInfoSql.put("values",new Object[]{backmoney,backmoney,comid});
//								parkAccountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{orderMap.get("comid"),backmoney,2,ntime,"停车宝返现",uid,1,orderId});
//								insertSqlList.add(comInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}else {//返现给收费员账户
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> userInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
//								userInfoSql.put("values",new Object[]{backmoney,uid});
//								parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{uid,backmoney,0,ntime,"停车宝返现",3,orderId});
//								insertSqlList.add(userInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}
//							logger.info(">>>>>>>>>>>>停车宝返现给"+giveMoneyTo+",结果："+isset+",更新缓存 ");
//							if(isset){
////								memcacheUtils.updateBackMoneyCache(orderMap.get("comid")+"_"+uin);
//							}
//						}
//					}else {
//						logger.info(">>>>>total:"+total+">>>>返现超过1次..."+orderMap.get("comid")+"_"+uin);
//					}
//				}else {
//					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不给车场返现 ......");
//				}
//				if(!isBlack){
//					Double ticket_money = 0d;
//					Map<String, Object> ticketMap = daService.getMap(
//							"select * from ticket_tb where orderid=? and type<=? order by utime limit ?",
//							new Object[] { orderId,2,1});
//					if(ticketMap != null){
//						if(ticketMap.get("bmoney") != null){
//							ticket_money = Double.valueOf(ticketMap.get("bmoney") + "");
//						}else if(ticketMap.get("umoney") != null){
//							ticket_money = Double.valueOf(ticketMap.get("umoney") + "");
//						}
//					}
//					logger.info("doprepayorder>>>>>:orderid:"+orderId+",ticket_money:"+ticket_money);
//					backTicket(total - ticket_money, orderId, uin,comid,"");
////					if(total>=1)
////						updateSorce(start, etime, cType, uid, comid);
//				}else {
//					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不给红包 ......");
//				}
//				//写系统日志 
//				String time = TimeTools.gettime();
//				if(state!=null&&state==0){
//					logService.updateOrderLog(comid,uin,time+",帐号："+uin+",车牌："+carNumber+",停车收费："+total+",停车场："+comName,1);
//				}
//				
//				//[您在XX泊车点支付成功,10.0元,泊车费,点击查看订单详情]
//				
//				String openid = "";
//				Map userMap = daService.getMap("select wxp_openid from user_info_Tb where id = ? ", new Object[]{uin});
//				if(userMap!=null)
//					openid = (String)userMap.get("wxp_openid");
//				else {
//					userMap = daService.getMap("select openid from wxp_user_Tb where uin= ? ", new Object[]{uin});
//					if(userMap!=null)
//						openid = (String)userMap.get("openid");
//				}
//				if(openid!=null&&openid.length()>10){
//					logger.info(">>>支付成功，通过微信发消息给车主...openid:"+openid);
//					Map bMap  =daService.getMap("select * from order_ticket_tb where uin=? and  order_id=? and ctime>? order by ctime desc limit ?",
//							new Object[]{uin,orderId,System.currentTimeMillis()/1000-5*60, 1});//五分钟前的红包
//					
//					Map<String, String> baseinfo = new HashMap<String, String>();
//					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//					String first = "预支付"+prefee+"元";
//					String orderMoneySum = total +"元";
//					String orderMoneySum_color = "#000000";
//					String orderProductName = "停车费";
//					String orderProductName_color = "#000000";
//					String remark = "点击查看订单详情";
//					String remark_color = "#000000";
//					if(prefee<total){
//						orderMoneySum = "停车费"+total+"元";
//						orderProductName = "还需支付"+StringUtils.formatDouble(total-prefee)+"元";
//						orderProductName_color = "#FF0000";
//					}else if(prefee > total && back > 0){
//						orderProductName_color = "#FF0000";
//						orderProductName = "多出的预付金额"+back + "元(按优惠券折扣后)已返还您的账户中";
//					}
//					Integer first_flag = 0;//是否是首单支付
//					if(bMap != null){
//						remark_color = "#FF0000";
//						remark = "恭喜您获得"+bMap.get("bnum")+"个共"+bMap.get("money")+"元停车券礼包，点击分享吧！";
//						
//						Long count = daService.getLong("select count(*) from user_account_tb where uin=? and type=? ", new Object[]{uin, 1});
//						logger.info("是否是首单支付>>>>orderid:"+orderId+",uin:"+uin+",openid:"+openid+",count:"+count+",time:"+System.currentTimeMillis()/1000);
//						if(count == 1){
//							first_flag = 1;
//						}
//					}
//					String url =  "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/carinter.do?action=orderdetail&prepay="+prefee+"&orderid="+orderId+"&back="+back+"&first_flag="+first_flag;
//					baseinfo.put("url", url);
//					baseinfo.put("openid", openid);
//					baseinfo.put("top_color", "#000000");
//					baseinfo.put("templeteid", Constants.WXPUBLIC_SUCCESS_NOTIFYMSG_ID);
//					Map<String, String> keyword1 = new HashMap<String, String>();
//					keyword1.put("keyword", "orderMoneySum");
//					keyword1.put("value", orderMoneySum);
//					keyword1.put("color", orderMoneySum_color);
//					orderinfo.add(keyword1);
//					Map<String, String> keyword2 = new HashMap<String, String>();
//					keyword2.put("keyword", "orderProductName");
//					keyword2.put("value", orderProductName);
//					keyword2.put("color", orderProductName_color);
//					orderinfo.add(keyword2);
//					Map<String, String> keyword3 = new HashMap<String, String>();
//					keyword3.put("keyword", "Remark");
//					keyword3.put("value", remark);
//					keyword3.put("color", remark_color);
//					orderinfo.add(keyword3);
//					Map<String, String> keyword4 = new HashMap<String, String>();
//					keyword4.put("keyword", "first");
//					keyword4.put("value", first);
//					keyword4.put("color", "#000000");
//					orderinfo.add(keyword4);
//					sendWXTempleteMsg(baseinfo, orderinfo);
//					
//					sendBounsMessage(openid,uid,2d,orderId ,uin);//发打赏消息
//				}
//				
//			} catch (Exception e) {
//				logger.info(">>>>>>>>>>停车宝返现、停车宝返券失败！...............");
//				e.printStackTrace();
//			}
//			
//			return 1;
//		}else {
//			return -1;
//		}
//	}
	
	/**
	 * 处理预付费订单
	 * @param orderMap 订单
	 * @param total 实收金额
	 * @return 0失败 1成功
	 */
	public Map<String, Object> doMidPayOrder(Map<String, Object> orderMap, Double total, Long uid){
		logger.info("中央现金预支付结算doMidPayOrder，orderid:"+orderMap.get("id")+",预支付金额："+orderMap.get("total")+",uin:"+orderMap.get("uin")+",停车费金额：total:"+total+",car_number:"+orderMap.get("car_number"));
		Double prefee = Double.valueOf(orderMap.get("total") + "");
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long orderid = (Long)orderMap.get("id");
		Long comid = (Long)orderMap.get("comid");
		Integer state = (Integer)orderMap.get("state");
		Long create_time = (Long)orderMap.get("create_time");
		Integer car_type = (Integer)orderMap.get("car_type");//0：通用，1：小车，2：大车
		Integer pid = (Integer)orderMap.get("pid");
		if(state == 1){
			logger.info("doMidPayOrder>>>>orderid:"+orderid+",订单已支付，返回！");
			resultMap.put("result", -1);
			return resultMap;
		}
		Long ntime = System.currentTimeMillis()/1000;
		
		//检查减免券使用情况
		Double distotal = 0d;
		Double umoney = 0d;
		Map<String, Object> shopticketMap = daService
				.getMap("select * from ticket_tb where (type=? or type=?) and orderid=? ",
						new Object[] { 3, 4, orderMap.get("id") });
		if(shopticketMap != null){
			Integer type = (Integer)shopticketMap.get("type");
			Integer money = (Integer)shopticketMap.get("money");
			umoney = Double.valueOf(shopticketMap.get("umoney") + "");
			Long end_time = ntime;
			logger.info("doMidPayOrder>>>>>:orderid:"+orderid+",shopticketid:"+shopticketMap.get("id")+",type:"+type+",umoney:"+umoney);
			if(type == 4){//全免
				distotal = total;
				logger.info("doMidPayOrder>>>>全免券:orderid:"+orderid+",distotal:"+distotal);
			}else if(type == 3){
				if(create_time + money * 60 * 60 > end_time){
					distotal = total;
					logger.info("doMidPayOrder>>>>减时券:orderid:"+orderid+",distotal:"+distotal);
				}else{
					end_time = end_time - money * 60 *60;
					Double dtotal = 0d;
					if(pid>-1){
						dtotal = Double.valueOf(getCustomPrice(create_time, end_time, pid));
					}else {
						dtotal = Double.valueOf(getPrice(create_time, end_time, comid, car_type));
					}
					if(total > dtotal){
						distotal = StringUtils.formatDouble(total - dtotal);
					}
					logger.info("doMidPayOrder>>>>减时券:orderid:"+orderid+",distotal="+distotal);
				}
			}
			resultMap.put("ticket_type", type);
		}
		resultMap.put("distotal", distotal);
		
		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
		//更新订单状态，收费成功
		Map<String, Object> orderSqlMap = new HashMap<String, Object>();
		//更新用券金额
		Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
		
		orderSqlMap.put("sql", "update order_tb set state=?,total=?,end_time=? where id=?");
		orderSqlMap.put("values", new Object[]{1,total,System.currentTimeMillis()/1000,orderid});
		bathSql.add(orderSqlMap);
		if(shopticketMap != null){
			ticketSqlMap.put("sql", "update ticket_tb set bmoney=? where id=?");
			ticketSqlMap.put("values", new Object[]{distotal, shopticketMap.get("id")});
			bathSql.add(ticketSqlMap);
		}
		prefee = StringUtils.formatDouble(prefee - umoney + distotal);//车主实际预支付金额
		logger.info("doMidPayOrder>>>>>:重新计算后的预支付金额prefee:"+prefee+",orderid:"+orderid);
		resultMap.put("prefee", prefee);
		if(prefee < total){
			//收现金记录
			Map<String, Object> cashsqlMap = new HashMap<String, Object>();
			cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
			cashsqlMap.put("values", new Object[]{uid, total - prefee, 0, orderid, System.currentTimeMillis()/1000});
			bathSql.add(cashsqlMap);
		}
		boolean result= daService.bathUpdate(bathSql);
		logger.info("doMidPayOrder>>>>,orderid:"+orderid+",result:"+result);
		if(result){
			resultMap.put("result", 1);
			return resultMap;
		}
		resultMap.put("result", -1);
		return resultMap;
	}
	
//	private boolean backWeixinTicket(Double money, Long orderId, Long uin){
//		Integer bonus = 5;//5折
//		if(money>=1&&memcacheUtils.readBackTicketCache(uin)){//一天只返一次红包
//			String sql = "insert into order_ticket_tb (uin,order_id,money,bnum,ctime,exptime,bwords,type) values(?,?,?,?,?,?,?,?)";
//			Object []values = null;
//			Long ctime = System.currentTimeMillis()/1000;
//			Long exptime = ctime + 24*60*60;
//			values = new Object[]{uin,orderId,bonus,5,ctime,exptime,"微信支付打折券",1};
//			logger.info(">>>>>微信预支付红包,5张"+bonus+"折券...");
//			int ret = daService.update(sql, values);
//			logger.info(">>>>>微信预支付红包 ret :"+ret);
//			if(ret==1){
//				memcacheUtils.updateBackTicketCache(uin);
//				return true;
//			}
//		}else {
//			if(money< 1){
//				logger.info(">>>>>>>>支付金额小于1元，不返红包>>>>>>uin:"+uin+",orderid:"+orderId+",money:"+money);
//			}else if(!memcacheUtils.readBackTicketCache(uin)){
//				logger.info(">>>>>>>>一天只返一次红包，已经返过，不返红包>>>>>>uin:"+uin+",orderid:"+orderId+",money:"+money);
//			}
//			logger.info(">>>>>微信支付红包,已经发过，不发了.....");
//		}
//		return false;
//		
//	}


	/**
	 * 直接向收费员付款
	 * @param comId 车场编号
	 * @param total 支付金额
	 * @param uin 车主账号
	 * @param uid 收费员账号
	 * @param ticketId 停车券号
	 * @param bind_flag 0：未绑定账户 1：绑定了账户
	 * @param ptype 支付类型  0余额，1支付宝，2微信，3网银，4余额+支付宝,5余额+微信,6余额+网银 ,7停车宝充值,8活动奖励,9微信公众号,10余额+微信公众号
	 * @return 0:失败　 5:成功 
	 *  -12：余额不足 
	 *  -13:停车券使用超过3次
	 */
//	public int epay(Long comId,Double total,Long uin,Long uid,Long ticketId,String carNumber,Integer ptype, Integer bind_flag,Long orderId, String wxp_orderid){
//		
//		Long ntime = System.currentTimeMillis()/1000;
//		logger.info(">>>>>>>>>>epay,ticket:"+ticketId+",uin:"+uin+",uid:"+uid);
//		//优惠券金额
//		Double ticketMoney = 0d;
//		Integer ticket_type = 7;//7：停车券，11：微信打折券
//		String ticket_dp = "停车券充值";
//		if(ticketId !=null && ticketId == -100){//微信五折券
//			ticketMoney = getDisTicketMoney(uin, uid, total);
//			ticket_type = 11;
//			ticket_dp = "微信打折券充值";
//			logger.info("orderid:"+orderId+",uin:"+uin+",ticketMoney:"+ticketMoney);
//		}else if(ticketId != null && ticketId > 0){
//			ticketMoney = getTicketMoney(ticketId, 3, uid, total, 2, comId, orderId);
//		}
//		logger.info("orderid:"+orderId+",uin:"+uin+",ticketid:"+ticketId+",ticketMoney:"+ticketMoney);
//		Map userMap = null;
//		Double ubalance =null;
//		if(bind_flag == 1){//已绑定账户
//			//车主真实账户余额
//			userMap = daService.getPojo("select balance from user_info_tb where id =?",	new Object[]{uin});
//		}else{//未绑定账户
//			//虚拟账户余额
//			userMap = daService.getPojo("select balance from wxp_user_tb where uin =?",	new Object[]{uin});
//		}
//		
//		if(userMap!=null&&userMap.get("balance")!=null){
//			ubalance = Double.valueOf(userMap.get("balance")+"");
//			ubalance +=ticketMoney;//用户余额加上优惠券金额
//		}
//		logger.info("ticket money："+ticketMoney+",uin:"+uin+",orderid:"+orderId+",balance:"+userMap.get("balance")+",total:"+total);
//		if(ubalance==null||ubalance<total){//帐户余额不足
//			logger.info("直付账户余额不足，账户余额："+ubalance+",停车费金额："+total+",uin:"+uin);
//			return -12;
//		}
//		
//		Map<String, Object> comMap = daService.getMap("select company_name,city from com_info_tb where id=?",  new Object[]{comId});
//		String comName = "停车场";
//		if(comMap!=null&&comMap.get("company_name")!=null)
//			comName = (String)comMap.get("company_name");
//		else {
//			return -10;
//		}
//		//查询收费设定 mtype:0:停车费,1:预订费,2:停车宝返现
//		Map msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//				new Object[]{comId,0});
//		Integer giveTo =null;
//		if(msetMap!=null)
//			giveTo =(Integer)msetMap.get("giveto");
//		logger.info(">>>>>>"+msetMap+">>>>>giveto:"+giveTo+"comid:"+comId+",uin:"+uid);
//		
//		if("车牌号未知".equals(carNumber))
//			carNumber = uin+"";
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//更新停车场余额
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//收费员账户
//		Map<String, Object> parkuserAccountsqlMap =new HashMap<String, Object>();
//		//车主账户
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//车场账户
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
//		//车主账户加停车券
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//收费员余额
//		Map<String, Object> parkusersqlMap =new HashMap<String, Object>();
//		//使用停车券更新
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//停车宝账户扣停车券金额
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		
//		//扣除车主账户余额
//		if(bind_flag == 1){
//			userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		}else{
//			userSqlMap.put("sql", "update wxp_user_tb  set balance =balance-? where uin=?");
//		}
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		//车主账户优惠券充值
//		if(ticketMoney>0&&ticketId!=null){//使用停车券，给车主账户先充值
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,ticket_dp,ticket_type,orderId});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		//车主账户支付停车费明细
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,uid,target,orderid) values(?,?,?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"停车费-"+comName,ptype,uid,1,orderId});
//		bathSql.add(userAccountsqlMap);
//
//		//不设置默认给个人账户20141120孙总要求修改
//		if(giveTo!=null&&giveTo==0){//写入公司账户
//			comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
//			comSqlMap.put("values", new Object[]{total,total,comId});
//			bathSql.add(comSqlMap);
//			
//			parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//			parkAccountsqlMap.put("values",  new Object[]{comId,total,0,ntime,"停车费_"+carNumber,uid,0,orderId});
//			bathSql.add(parkAccountsqlMap);
//		}else {//写入个人账户
////			//停车费写入收费员账户
//			parkusersqlMap.put("sql", "update user_info_tb  set balance =balance+? where id=?");
//			parkusersqlMap.put("values", new Object[]{total,uid});
//			bathSql.add(parkusersqlMap);
//			//收费员账户收费明细
//			parkuserAccountsqlMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//			parkuserAccountsqlMap.put("values", new Object[]{uid,total,0,ntime,"停车费_"+carNumber,4,orderId});
//			bathSql.add(parkuserAccountsqlMap);
//		}
//		
//		//优惠券使用后，更新券状态，添加停车宝账户支付记录
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=?,wxp_orderid=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comId,System.currentTimeMillis()/1000,ticketMoney,wxp_orderid,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,comName+"，车主"+carNumber+"，使用停车代金券",0,orderId});
//			bathSql.add(tingchebaoAccountsqlMap);
//			memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info("直付结果 ："+result+",uin:"+uin+",orderid:"+orderId);
//		if(result){//结算成功，处理返券及返现 
//			//处理返现（车场），返券（车主）
//			/* 每次用余额或微信或支付宝支付1元以上的完成的，补贴车场2元，补贴车主3元的停车券，
//			 * 车场返现不限(同一车主每日只能返3次)，
//			 * 车主每日返券限3张券
//			 * 每个车主每天使用停车券不超过3单 */
//			try {
//				boolean isBlack = isBlackUser(uin);
//				double ownpay = total-ticketMoney;
//				if(!isBlack){
//					if(ownpay>=1&&memcacheUtils.readBackMoneyCache(comId+"_"+uin)){//可以给车场返现 
//						boolean isCanBackMoney = isCanBackMoney(comId);//是否是济南车场
//						Double backmoney = getBackMoney();
//						logger.info("epay>>>>>uin:"+uin+",backmoney:"+backmoney+",isCanBackMoney:"+isCanBackMoney);
//						if(isCanBackMoney && backmoney > 0){
//							//查返现设置
//							Integer giveMoneyTo = null;//查询收费设定 mtype:0:停车费,1:预订费,2:停车宝返现
//							msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comId,2});
//							if(msetMap!=null)
//								giveMoneyTo =(Integer)msetMap.get("giveto");
//							boolean isset =false;
//							if(giveMoneyTo!=null&&giveMoneyTo==0){//返现给车场账户
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> comInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								comInfoSql.put("sql", "update com_info_tb set money=money+?, total_money=total_money+? where id=?");
//								comInfoSql.put("values",new Object[]{backmoney,backmoney,comId});
//								parkAccountSql.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source,orderid) values(?,?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{comId,backmoney,2,ntime,"停车宝返现",uid,1,orderId});
//								insertSqlList.add(comInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}else {//返现给收费员账户
//								List<Map<String, Object>> insertSqlList = new ArrayList<Map<String,Object>>();
//								Map<String, Object> userInfoSql = new HashMap<String, Object>();
//								Map<String, Object> parkAccountSql = new HashMap<String, Object>();
//								//返现给收费员
//								userInfoSql.put("sql", "update user_info_tb set balance=balance+? where id=?");
//								userInfoSql.put("values",new Object[]{backmoney,uid});
//								//返现给收费员明细
//								parkAccountSql.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)");
//								parkAccountSql.put("values",new Object[]{uid,backmoney,0,ntime,"停车宝返现",3,orderId});
//								
//								insertSqlList.add(userInfoSql);
//								insertSqlList.add(parkAccountSql);
//								isset = daService.bathUpdate(insertSqlList);
//							}
//							logger.info(">>>>>>>>>>>>停车宝返现给收费员,结果："+isset+",更新缓存 ");
//							if(isset){
//								memcacheUtils.updateBackMoneyCache(comId+"_"+uin);
//							}
//						}
//					}else {
//						logger.info(">>>>>total:"+total+">>>>返现超过1次..."+comId+"_"+uin);
//					}
//				}else {
//					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不给车场返现 ......");
//				}
//				if(!isBlack){
//					backTicket(total-ticketMoney, orderId, uin,comId,wxp_orderid);
////					if(ownpay>=1)
////						updateSorce(ntime, ntime+16*60, 0, uid, comId);
//				}else {
//					logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不能返红包......");
//				}
//				if(ownpay>=1)
//					handleRecommendCode(uin,isBlack);
//			} catch (Exception e) {
//				logger.info(">>>>>>>>>>停车宝返现、停车宝返券失败！...............");
//				e.printStackTrace();
//			}
//			
//			//写一条订单记录
//			try {
//				//新建一条订单，预取订单编号 ,直付操作成功后，写入订单表
//				int orderRet = daService.update("insert into order_tb(id,create_time,comid,uin,total,state,end_time,pay_type," +
//						"c_type,uid,car_number) values(?,?,?,?,?,?,?,?,?,?,?)", 
//						new Object[]{orderId,ntime,comId,uin,total,1,ntime+60,2,4,uid,carNumber});
//				
//				logger.info(">>>epay,写入订单..."+orderRet);
//				//处理5折券，在停车券表不存在 ，插入一条记录，方便对账
//				if(ticket_type==11&&ticketId<0&&ticketMoney>0){
//					int ret = ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type,orderid,utime,umoney,wxp_orderid)" +
//							" values(?,?,?,?,?,?,?,?,?,?,?) ",
//							new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+24*60*60-1,5,1,uin,comId,2,orderId,ntime,ticketMoney,wxp_orderid});
//					memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//					logger.info(">>>epay  ,没有五折券时，写一条记录，结果:"+ret);
//				}else if(ticketId!=null&&ticketId>0&&ticketMoney>0){
//					int ret  = daService.update("update ticket_tb  set orderid=? where id=?", new Object[]{orderId,ticketId});
//				}
//			} catch (Exception e) {
//				logger.info(">>>>>epay error:写入订单或写入停车券错误..."+e.getMessage());
//			}
//			
//			if(ticketMoney > 0){
////				updateAllowCache(comId, ticketId, ticketMoney);
//				logger.info("update allowance cache>>>uin:"+uin+",ticketMoney:"+ticketMoney);
//			}
//			try {//直付完成后发送消息
//				String openid = "";
//				String url = "";
//				userMap = daService.getMap("select wxp_openid from user_info_Tb where id = ? ", new Object[]{uin});
//				if(userMap!=null){
//					logger.info(">>>>>>>>>已经绑定账户，uin："+uin);
//					openid = (String)userMap.get("wxp_openid");
//					url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toaccountdetail&openid="+openid;
//				} else {
//					logger.info(">>>>>>>>>>>未绑定账户，uin："+uin);
//					userMap = daService.getMap("select openid from wxp_user_Tb where uin= ? ", new Object[]{uin});
//					if(userMap!=null){
//						openid = (String)userMap.get("openid");
//						url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
//					}	
//				}
//				if(openid!=null&&openid.length()>10 && (ptype == 10 || ptype == 9 || ptype == 0) ){
//					logger.info(">>>直付成功，通过微信发消息给车主...directpaymsg:openid:"+openid+",uin："+uin);
//					Map<String, String> baseinfo = new HashMap<String, String>();
//					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//					Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{uid});
//					String first = "您在"+comMap.get("company_name")+"向收费员"+uidMap.get("nickname")+"付费成功！";
//					String remark = "点击详情查账户明细！";
//					String remark_color = "#000000";
//					//查询订单红包
//					Map bMap  =daService.getMap("select * from order_ticket_tb where uin=? and  order_id=? ",
//							new Object[]{uin,orderId});//五分钟前的红包
//					
//					if(bMap!=null&&bMap.get("id")!=null){
//						Integer bonus_type = 0;//0:普通订单红包，1：微信折扣红包
//						if(bMap.get("type")!= null && (Integer)bMap.get("type") == 1){
//							bonus_type = 1;//微信打折红包
//						}
//						if(bonus_type == 1){
//							remark = "恭喜您获得"+bMap.get("bnum")+"个微信"+bMap.get("money")+"折券礼包，点击分享吧！";
//						}else{
//							remark = "恭喜您获得"+bMap.get("bnum")+"个共"+bMap.get("money")+"元 停车券礼包，点击分享吧！";
//						}
//						remark_color = "#FF0000";
//						
//						Integer first_flag = 0;
//						Long count = daService.getLong("select count(*) from user_account_tb where uin=? and type=? ", new Object[]{uin, 1});
//						logger.info("是否是首单支付>>>>orderid:"+orderId+",uin:"+uin+",openid:"+openid+",count:"+count+",time:"+System.currentTimeMillis()/1000);
//						if(count == 1){
//							first_flag = 1;
//						}
//						url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpublic.do?action=balancepayinfo&openid="+openid+"&money="+total+"&bonusid="+bMap.get("id")+"&bonus_type="+bonus_type+"&first_flag="+first_flag;
//					}
//					baseinfo.put("url", url);
//					baseinfo.put("openid", openid);
//					baseinfo.put("top_color", "#000000");
//					baseinfo.put("templeteid", Constants.WXPUBLIC_SUCCESS_NOTIFYMSG_ID);
//					Map<String, String> keyword1 = new HashMap<String, String>();
//					keyword1.put("keyword", "orderMoneySum");
//					keyword1.put("value", total+"元");
//					keyword1.put("color", "#000000");
//					orderinfo.add(keyword1);
//					Map<String, String> keyword2 = new HashMap<String, String>();
//					keyword2.put("keyword", "orderProductName");
//					keyword2.put("value", "停车费");
//					keyword2.put("color", "#000000");
//					orderinfo.add(keyword2);
//					Map<String, String> keyword3 = new HashMap<String, String>();
//					keyword3.put("keyword", "Remark");
//					keyword3.put("value", remark);
//					keyword3.put("color", remark_color);
//					orderinfo.add(keyword3);
//					Map<String, String> keyword4 = new HashMap<String, String>();
//					keyword4.put("keyword", "first");
//					keyword4.put("value", first);
//					keyword4.put("color", "#000000");
//					orderinfo.add(keyword4);
//					sendWXTempleteMsg(baseinfo, orderinfo);
//					
//					sendBounsMessage(openid,uid,2d,orderId,uin);//发打赏消息
//				}
//			} catch (Exception e) {
//				// TODO: handle exception
//			}
//			return 5;
//		}else {
//			return -7;
//		}
//	}
	/**
	 * 查车牌号
	 * @param uin
	 * @return
	 */
	public String getCarNumber(Long uin){
		String carNumber="车牌号未知";//车主车牌号
		Map carNuberMap = daService.getPojo("select car_number from car_info_tb where uin=? and state=?  ", 
				new Object[]{uin,1});
		if(carNuberMap!=null&&carNuberMap.get("car_number")!=null&&!carNuberMap.get("car_number").toString().equals(""))
			carNumber = (String)carNuberMap.get("car_number");
		return carNumber;
	}
	
	
	public Map getPriceMap(Long comid){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		//开始小时
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{comid,0,0});
		if(priceList==null||priceList.size()==0){//没有按时段策略
			//查按次策略
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{comid,0,1});
			if(priceList==null||priceList.size()==0){//没有按次策略，返回提示
				return null;
			}else {//有按次策略，直接返回一次的收费
				 return priceList.get(0);
			}
			//发短信给管理员，通过设置好价格
		}else {//从按时段价格策略中分拣出日间和夜间收费策略
			if(priceList.size()>0){
				for(Map map : priceList){
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime<etime){//日间
						if(bhour>=btime&&bhour<etime)
							return map;
					}else {
						if((bhour>=btime&&bhour<24)||(bhour>=0&&bhour<etime))
							return map;
					}
				}
			}
		}
		return null;
	}
	/**
	 * 计算订单金额
	 * @param start
	 * @param end
	 * @param comId
	 * @param car_type 0：通用，1：小车，2：大车
	 * @return 订单金额_是否优惠
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public  String getPrice(Long start,Long end,Long comId,Integer car_type){
//		String pid = CustomDefind.CUSTOMPARKIDS;
//		if(pid.equals(comId.toString())){//定制价格策略
//			return "待结算";
//		}
//		
		if(car_type == 0){//0:通用
			Long count = daService.getLong("select count(*) from com_info_tb where id=? and car_type=?", new Object[]{comId,1});
			if(count > 0){//区分大小车
				car_type = 1;//默认成小车计费策略
			}
		}
		Map priceMap1=null;
		Map priceMap2=null;
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? and car_type=? order by id desc", new Object[]{comId,0,0,car_type});
		if(priceList==null||priceList.size()==0){
			//查按次策略
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? and car_type=? order by id desc", new Object[]{comId,0,1,car_type});
			if(priceList==null||priceList.size()==0){//没有任何策略
				return "0.0";
			}else {//有按次策略，返回N次的收费
				Map timeMap =priceList.get(0);
				Object ounit  = timeMap.get("unit");
				Double total = Double.valueOf(timeMap.get("price")+"");
				try {
					if(ounit!=null){
						Integer unit = Integer.valueOf(ounit.toString());
						if(unit>0){
							Long du = (end-start)/60;//时长秒
							int times = du.intValue()/unit;
							if(du%unit!=0)
								times +=1;
							total = times*total;
							
						}
					}
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}
				return StringUtils.formatDouble(total)+"";
			}
		}else {
			priceMap1=priceList.get(0);
			boolean pm1 = false;//找到map1,必须是结束时间大于开始时间
			boolean pm2 = false;//找到map2
			Integer payType = (Integer)priceMap1.get("pay_type");
			if(payType==0&&priceList.size()>1){
				for(Map map : priceList){
					if(pm1&&pm2)
						break;
					payType = (Integer)map.get("pay_type");
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(payType==0&&etime>btime){
						if(!pm1){
							priceMap1 = map;
							pm1=true;
						}else {
							priceMap2=map;
							pm2=true;
						}
					}else {
						if(!pm2){
							priceMap2=map;
							pm2=true;
						}
					}
				}
			}
		}
		double minPriceUnit = getminPriceUnit(comId);
		Map assistMap = daService.getMap("select * from price_assist_tb where comid = ? and type = ?", new Object[]{comId,0});
		Map orderInfp = CountPrice.getAccount(start, end, priceMap1, priceMap2,minPriceUnit,assistMap);
		
		//Double count= StringUtils.getAccount(start, end, priceMap1, priceMap2);
		return StringUtils.formatDouble(orderInfp.get("collect"))+"";	
	}
	/**
	 * 
	 * @param start
	 * @param end
	 * @param pid 计费方式：0按时(0.5/15分钟)，1按次（12小时内10元,前1/30min，后每小时1元）
	 * @return
	 */
	public String getCustomPrice(Long start,Long end,Integer pid) {
		/**一元/半小时     12小时内封顶10元。12小时候，每加一小时，加一元。*/
		logger.info(">>>>>>定制价格车场,pid(0按时(0.5/15分钟)，1按次（12小时内10元,前1/30min，后每小时1元）)="+pid);
		Long duration = (end-start)/60;//分钟
		Long hour = duration/(60);//小时数;
		if(pid==0){
			Long t = duration/15;
			if(duration%15!=0)
				t= t+1;
			return StringUtils.formatDouble(t*0.5)+"";
		}else if(pid==1){
			if(duration%60!=0)
				hour = hour+1;
			if(hour<12){
				if(hour<6){
					Long tLong = duration/30;
					if(duration%30!=0)
						tLong += 1L;
					return StringUtils.formatDouble(tLong)+"";
				}
				else 
					return 10.0+"";
			}else {
				return 10.0+(hour-12)+"";
			}
		}else {
			return "0";
		}
	}


	//@SuppressWarnings({ "rawtypes", "unchecked" })
	public String handleOrder(Long comId,Map orderMap) throws Exception{
		Map dayMap=null;//日间策略
		Map nigthMap=null;//夜间策略
		//按时段价格策略
		List<Map<String ,Object>> priceList=null;//SystemMemcachee.getPriceByComid(comId);
		priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{comId,0,0});
		Long ntime = System.currentTimeMillis()/1000;
		if(priceList==null||priceList.size()==0){//没有按时段策略
			//查按次策略
			priceList=daService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{comId,0,1});
			Long btLong = (Long)orderMap.get("create_time");
			String btime = TimeTools.getTime_MMdd_HHmm(btLong*1000).substring(6);
			String etime = TimeTools.getTime_MMdd_HHmm(ntime*1000).substring(6);
			Map<String, Object> orMap=new HashMap<String, Object>();
			Long start = (Long)orderMap.get("create_time");
			Long end = ntime;
			orMap.put("btime", btime);
			orMap.put("etime", etime);
			orMap.put("duration", StringUtils.getTimeString(start, end));
			orMap.put("orderid", orderMap.get("id"));
			orMap.put("carnumber",orderMap.get("car_number")==null?"车牌号未知": orderMap.get("car_number"));
			orMap.put("handcash", "0");
			orMap.put("uin", orderMap.get("uin"));
			if(priceList==null||priceList.size()==0){//没有按次策略，返回提示
				//返回给收费员，手工输入价格
				orMap.put("total", "0.00");
				orMap.put("collect", "0.00");
				orMap.put("handcash", "1");
			}else {//有按次策略，直接返回一次的收费
				Map timeMap =priceList.get(0);
				Object ounit  = timeMap.get("unit");
//				orMap.put("btime", btime);
//				orMap.put("etime", etime);
//				orMap.put("duration", StringUtils.getTimeString(start, end));
//				orMap.put("orderid", orderMap.get("id"));
//				orMap.put("carnumber",orderMap.get("car_number")==null?"车牌号未知": orderMap.get("car_number"));
//				
				orMap.put("collect", timeMap.get("price"));
				orMap.put("total", timeMap.get("price"));
				if(ounit!=null){
					Integer unit = Integer.valueOf(ounit.toString());
					if(unit>0){
						Long du = (end-start)/60;//时长秒
						int times = du.intValue()/unit;
						if(du%unit!=0)
							times +=1;
						double total = times*Double.valueOf(timeMap.get("price")+"");
						orMap.put("collect", total);
						orMap.put("total", total);
					}
				}
			}
			return StringUtils.createJson(orMap);
		}else {//从按时段价格策略中分拣出日间和夜间收费策略
			dayMap= priceList.get(0);
			boolean pm1 = false;//找到map1,必须是结束时间大于开始时间
			boolean pm2 = false;//找到map2
			if(priceList.size()>1){
				for(Map map : priceList){
					if(pm1&&pm2)
						break;
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime==null||etime==null)
						continue;
					if(etime>btime){
						if(!pm1){
							dayMap = map;
							pm1=true;
						}
					}else {
						if(!pm2){
							nigthMap=map;
							pm2=true;
						}
					}
				}
			}
		}
		double minPriceUnit = getminPriceUnit(comId);
		
		Map assistMap = daService.getMap("select * from price_assist_tb where comid = ? and type = ?", new Object[]{comId,0});
		
		Map<String, Object> orMap=CountPrice.getAccount((Long)orderMap.get("create_time"),ntime, dayMap, nigthMap,minPriceUnit,assistMap);
		orMap.put("orderid", orderMap.get("id"));
		orMap.put("uin", orderMap.get("uin"));
		String hascard = "1";//是否有车牌
		String carNumber = (String)orderMap.get("car_number");
		if(carNumber==null||carNumber.toString().trim().equals("")){
			carNumber="车牌号未知";
			hascard = "0";
		}
		orMap.put("carnumber",carNumber);
		orMap.put("hascard", hascard);
		orMap.put("handcash", "0");
		orMap.put("car_type", orderMap.get("car_type"));
		logger.info("结算订单，返回："+orMap);
		return StringUtils.createJson(orMap);	
	}
	
	/**
	 * 支持多价格策略//20141118
	 * //V1115以上版本实现包月产品及多价格策略的支持
	 * @param comId
	 * @param orderMap
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public String getOrderPrice(Long comId,Map orderMap) throws Exception{
		Long uin = (Long) orderMap.get("uin");
		Double pretotal = StringUtils.formatDouble(orderMap.get("total"));// 预支付金额
		// Integer preState =(Integer)orderMap.get("pre_state");//预支付状态
		// ,1正在预支付,2等待车主完成预支付
		// System.err.println("预支付 ："+pretotal);
		Long ntime = System.currentTimeMillis() / 1000;
		Map<String, Object> orMap = new HashMap<String, Object>();
		Long btLong = (Long) orderMap.get("create_time");
		// if(ntime>btLong){
		//			
		// }else {
		// ntime = ntime +60;
		// }
		Integer cType = (Integer) orderMap.get("c_type");// 进场方式
		// ，0:NFC,1:IBeacon,2:照牌
		// 3通道照牌 4直付 5月卡用户
		// 6车位二维码7月卡第二辆车
		orMap.put("ctype", cType);
		String btimestr = TimeTools.getTime_MMdd_HHmm(btLong * 1000);
		String etimestr = TimeTools.getTime_MMdd_HHmm(ntime * 1000);
		String btime = btimestr.substring(6);
		String etime = etimestr.substring(6);
		Long start = (Long) orderMap.get("create_time");
		Integer pid = (Integer) orderMap.get("pid");// 计费方式：0按次(0.5/h)，1按时（12小时内10元，后每小时1元）
		Integer type = (Integer) orderMap.get("type");
		Integer state = (Integer) orderMap.get("state");
		Long end = ntime;
		String hascard = "1";// 是否有车牌
		// 查询车牌号
		String carNumber = (String) orderMap.get("car_number");
		if (carNumber == null || carNumber.toString().trim().equals("")) {
			carNumber = null;
			if (uin != null)
				carNumber = getCarNumber(uin);
			if (carNumber == null) {
				carNumber = "车牌号未知";
				hascard = "0";
			}
		}
		orMap.put("carnumber", carNumber);

		List<Map<String, Object>> cardList = daService.getAll(
				"select car_number from car_info_Tb where uin=? ",
				new Object[] { uin });
		if (cardList != null && cardList.size() > 0) {
			String cards = "";
			for (Map<String, Object> cMap : cardList) {
				cards += ",\"" + cMap.get("car_number") + "\"";
			}
			cards = cards.substring(1);
			orMap.put("cards", "[" + cards + "]");
		} else {
			orMap.put("cards", "[]");
		}
		Integer isfast = (Integer) orderMap.get("type");
		if (isfast != null && isfast == 2) {// 第三方卡号生成的订单,车牌号应该写第三方卡号
			String cardno = (String) orderMap.get("nfc_uuid");
			if (cardno != null && cardno.indexOf("_") != -1)
				orMap.put("carnumber", cardno
						.substring(cardno.indexOf("_") + 1));
		}
		orMap.put("hascard", hascard);
		orMap.put("handcash", "0");
		orMap.put("btime", btime);
		orMap.put("etime", etime);
		orMap.put("btimestr", btimestr);
		orMap.put("etimestr", etimestr);
		orMap.put("duration", StringUtils.getTimeString(start, end));
		orMap.put("orderid", orderMap.get("id"));
		// orMap.put("carnumber",orderMap.get("car_number")==null?"车牌号未知":
		// orderMap.get("car_number"));
		orMap.put("uin", orderMap.get("uin"));
		orMap.put("total", "0.00");
		orMap.put("collect", "0.00");
		orMap.put("handcash", "1");
		orMap.put("isedit", 0);
		orMap.put("car_type", orderMap.get("car_type"));
		orMap.put("prepay", pretotal);
		orMap.put("isfast", type);
		// String pid = CustomDefind.CUSTOMPARKIDS;

		if (pid != null && pid > -1) {// 定制价格策略
			// orMap.put("collect0", getCustomPrice(start, end, pid));
			orMap.put("handcash", "0");
			// Long duration = (end-start)/60;//分钟
			// Long t = duration/15;
			// if(duration%15!=0)
			// t= t+1;
			orMap.put("collect", getCustomPrice(start, end, pid));
			// logger.error("结算订单，返回："+orMap);
			return StringUtils.createJson(orMap);
		}
		// 先判断月卡
		if (uin != null && uin != -1 && cType == 5) {
			Map<String, Object> pMap = daService
					.getMap(
							"select c.e_time limitday from product_package_tb p,"
									+ "carower_product c where c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? order by c.id desc limit ?",
							new Object[] { comId, uin, ntime, 1 });
			if (pMap != null && !pMap.isEmpty()) {
				// System.out.println(pMap);
				Long limitDay = (Long) pMap.get("limitday");
				Long day = (limitDay - ntime) / (24 * 60 * 60) + 1;
				orMap.put("limitday", day + "");
				orMap.put("handcash", "2");
				logger.error("结算订单，返回：" + orMap);
				return StringUtils.createJson(orMap);
			}
		}
		Integer car_type = (Integer) orderMap.get("car_type");
		if (car_type == 0) {// 0:通用
			Long count = daService
					.getLong(
							"select count(*) from com_info_tb where id=? and car_type=?",
							new Object[] { comId, 1 });
			if (count > 0) {// 区分大小车
				car_type = 1;// 默认成小车计费策略
			}
		}
		Map dayMap = null;// 日间策略
		Map nigthMap = null;// 夜间策略
		// 按时段价格策略
		List<Map<String, Object>> priceList1 = daService
				.getAll(
						"select * from price_tb where comid=? "
								+ "and state=? and pay_type=? and car_type=? order by id desc",
						new Object[] { comId, 0, 0, car_type });
		// 查按次策略
		List<Map<String, Object>> priceList2 = daService
				.getAll(
						"select * from price_tb where comid=? "
								+ "and state=? and pay_type=? and car_type=? order by id desc",
						new Object[] { comId, 0, 1, car_type });
		// boolean isHasTimePrice=false;//是否有按次价格
		if (priceList2 != null && !priceList2.isEmpty()) {// 按次策略
			int i = 0;
			String total0 = "";
			String total1 = "[";
			for (Map<String, Object> timeMap : priceList2) {
				Object ounit = timeMap.get("unit");
				String total = timeMap.get("price") + "";
				if (ounit != null) {
					Integer unit = Integer.valueOf(ounit.toString());
					if (unit > 0) {
						Long du = (end - start) / 60;// 时长秒
						int times = du.intValue() / unit;
						if (du % unit != 0)
							times += 1;
						total = StringUtils.formatDouble(times
								* Double.valueOf(timeMap.get("price") + ""))
								+ "";
					}
				}
				if (i == 0) {
					total0 = total;
					total1 += total;
				} else {
					total1 += "," + total;
				}
				i++;
			}
			total1 += "]";
			orMap.put("collect0", total0);
			orMap.put("collect1", total1);
			orMap.put("handcash", "0");
			// isHasTimePrice = true;
		}
		boolean isHasDatePrice = false;// 是否有按时段价格
		if (priceList1 != null && !priceList1.isEmpty()) {// 从按时段价格策略中分拣出日间和夜间收费策略
			dayMap = priceList1.get(0);
			boolean pm1 = false;// 找到map1,必须是结束时间大于开始时间
			boolean pm2 = false;// 找到map2
			Integer isEdit = 0;// 是否可编辑价格，目前只对日间按时价格生效,0否，1是，默认0
			if (priceList1.size() > 1) {
				for (Map map : priceList1) {
					if (pm1 && pm2)
						break;
					Integer pbtime = (Integer) map.get("b_time");
					Integer petime = (Integer) map.get("e_time");
					if (btime == null || etime == null)
						continue;
					if (petime > pbtime) {
						if (!pm1) {
							dayMap = map;
							isEdit = (Integer) map.get("isedit");
							pm1 = true;
						}
					} else {
						if (!pm2) {
							nigthMap = map;
							pm2 = true;
						}
					}
				}
			}
			double minPriceUnit = getminPriceUnit(comId);
			Long end_time = ntime;
			if (state == 1) {
				end_time = (Long) orderMap.get("end_time");
			}
			Map assistMap = daService
					.getMap(
							"select * from price_assist_tb where comid = ? and type = ?",
							new Object[] { comId, 0 });
			Map<String, Object> oMap = null;
			if ((uin != null && uin != -1 && (cType == 8||cType==5))) {
				String sqlparm = "";
				List comsList = daService.getAll("select * from com_info_tb where pid = ?",new Object[]{comId});
				Long parcomid = daService.getLong("select pid from com_info_tb where id = ?", new Object[]{comId});
				String sql = "";
				Object[] parm = null;
				int j=0;
				if(parcomid!=null&&parcomid>0){
					parm = new Object[comsList.size()+6];
					parm[0] = parcomid;
					sql += " or p.comid = ? ";
					j=1;
				}else{
					parm = new Object[comsList.size()+5];
				}
				parm[0+j] = comId;
				for (int i = 1; i < comsList.size()+1; i++) {
					long comidoth = Long.parseLong(((Map)comsList.get(i-1)).get("id")+"");
					parm[i+j] = comidoth;
					sql += " or p.comid = ? ";
				}
				parm[comsList.size()+1+j] = uin;
				parm[comsList.size()+2+j] = 1;
				parm[comsList.size()+3+j] = ntime;
				parm[comsList.size()+4+j] = ntime;
				List<Map<String, Object>> list = daService
						.getAll("select p.b_time,p.e_time,p.bmin,p.emin,p.type,c.b_time bt,c.e_time et from product_package_tb p,"
										+ "carower_product c where c.pid=p.id and (p.comid=? "+sqlparm+")and c.uin=? and p.state<? and c.e_time>? and c.b_time<? order by c.id desc ",
								new Object[] { comId, uin, 1, ntime, ntime });
				if (list != null && list.size() > 0) {
					oMap = monthPrice(orderMap, list, end_time, dayMap,
							nigthMap, assistMap);
					double collect = Double.parseDouble(oMap.get("collect")+"");
					if(minPriceUnit!=0.00){
						collect = CountPrice.dealPrice(collect,minPriceUnit);
						oMap.put("collect",collect);
					}
				} else {
					oMap = CountPrice.getAccount((Long) orderMap.get("create_time"), end_time, dayMap, nigthMap,minPriceUnit, assistMap);
				}
			} else {
				oMap = CountPrice.getAccount(
						(Long) orderMap.get("create_time"), end_time, dayMap,
						nigthMap, minPriceUnit, assistMap);
			}
			// orMap.put("total", oMap.get("total"));
			// if(isHasTimePrice){
			// orMap.put("collect0", orMap.get("collect"));
			// }else {
			// }
			orMap.put("collect", oMap.get("collect"));
			orMap.put("isedit", isEdit);
			orMap.put("handcash", "0");
			isHasDatePrice = true;
		}

		if (!isHasDatePrice) {// 没有按时段价格
			orMap.put("collect", orMap.get("collect0"));
			orMap.remove("collect0");
		}

		// orMap.put("prestate", preState);

		// logger.error("结算订单，返回："+orMap);
		return StringUtils.createJson(orMap);
	}
	//所有lala请求调用此方法，可以避免一秒内拉几次都计入成绩
	/*public  boolean isCanLaLa(Integer number,Long uin,Long time) throws Exception{
		//logger.info("lala scroe ---uin:"+uin+",sharenumber:"+number+",time:"+TimeTools.getTime_yyyyMMdd_HHmmss(time*1000));
		Map<Long, Long> lalaMap = memcacheUtils.doMapLongLongCache("zld_lala_time_cache",null, null);
		String lastDate = "";
		boolean isLalaScore=true;
		if(lalaMap!=null){
			Long lastTime = lalaMap.get(uin);
			//logger.info("lala scroe ---uin:"+uin+",sharenumber:"+number+",cache time:"+lastTime);
			if(lastTime!=null){
				lastDate=TimeTools.getTime_yyyyMMdd_HHmmss(lastTime*1000);
				if(time<lastTime+15*60){
					isLalaScore=false;
					ParkingMap.setLastLalaTime(uin, lastTime);//同步时间到本地缓存
				}
			}
		}else {
//				logger.info("error, no memcached ！！！please check memcached ip config........");
			lalaMap=new HashMap<Long, Long>();
		}
		if(isLalaScore){
			lalaMap.put(uin, time);
			ParkingMap.setLastLalaTime(uin, time);//同步时间到本地缓存
			memcacheUtils.doMapLongLongCache("zld_lala_time_cache", lalaMap, "update");
		}
		logger.info("lala scroe ---return :"+isLalaScore+"---uin:"+uin+",sharenumber:"+number+",time:"+TimeTools.getTime_yyyyMMdd_HHmmss(time*1000)+",lastTime:"+lastDate);
		return isLalaScore;
	}*/
	/**
	 * 从缓存中取速通卡用户
	 * @param uuid
	 * @return
	 */
//	public Long getUinByUUID(String uuid){
//		Long uin = memcacheUtils.getUinUuid(uuid);
//		if(uin!=null&&uin==-1){//未初始化缓存 
//			logger.info("初始化速通卡用户.....");
//			List<Map<String, Object>> list = daService.getAll("select nfc_uuid,uin from com_nfc_tb where uin>?",new Object[]{0});
//			logger.info(">>>>>>>>>>>>>>>初始化绑定NFC用户数："+list.size());
//			Map<String, Long> uinUuidMap = new HashMap<String, Long>();
//			if(list!=null&&list.size()>0){
//				for(Map<String, Object> map : list){
//					uinUuidMap.put(map.get("nfc_uuid")+"",(Long)map.get("uin"));
//				}
//				uin = uinUuidMap.get(uuid);
//				logger.info("缓存速通卡用户.....size:"+uinUuidMap.size());
//				memcacheUtils.setUinUuid(uinUuidMap);
//			}
//		}
//		return uin;
//	}
	/**
	 * 更新速通卡缓存 
	 * @param uuid
	 * @param uin
	 */
	public void updateUinUuidMap(String uuid,Long uin){
//		Map<String,Long> uuidUinMap = memcacheUtils.doUinUuidCache("uuid_uin_map", null, null);
//		if(uuidUinMap!=null){
//			logger.info("更新速通卡缓存 ...");
//			uuidUinMap.put(uuid, uin);
//			memcacheUtils.setUinUuid(uuidUinMap);
//		}else {
			logger.info("初始化速通卡用户.....");
			List<Map<String, Object>> list = daService.getAll("select nfc_uuid,uin from com_nfc_tb where uin>?",new Object[]{0});
			logger.info(">>>>>>>>>>>>>>>初始化绑定NFC用户数："+list.size());
			Map<String, Long> uinUuidMap = new HashMap<String, Long>();
			if(list!=null&&list.size()>0){
				for(Map<String, Object> map : list){
					uinUuidMap.put(map.get("nfc_uuid")+"",(Long)map.get("uin"));
				}
				//uinUuidMap.put(uuid, uin);
				logger.info("缓存速通卡用户.....size:"+uinUuidMap.size());
//				memcacheUtils.setUinUuid(uinUuidMap);
			}
//		}
	}
	
	public int backNewUserTickets(Long ntime,Long key){
		return 0;//2015-03-10，开卡及领红包时不再先写入停车券，登录时判断黑名单后添加停车券
	}
	
	//获取角色的功能权限
	public List<Object> getAuthByRole(Long roleid) throws JSONException{
		String auth = "[]";
		List<Object> authids = new ArrayList<Object>();
		Map<String, Object> map = daService.getMap("select * from role_auth_tb where role_id=? ", new Object[]{roleid});
		if(map != null){
			auth = (String) map.get("auth");
		}
		JSONArray jsonArray = new JSONArray(auth);
		for(int i=0;i<jsonArray.length();i++){
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			Long nid = jsonObject.getLong("nid");
			Long pId = jsonObject.getLong("pid");
			Map<String, Object> map2 = daService.getMap("select id from auth_tb where nid=? and pid=? ", new Object[]{nid,pId});
			if(map2 != null){
				authids.add(map2.get("id"));
			}
		}
		return authids;
	}
	
	//获取数据权限
	public List<Object> getDataAuth(Long id){
		List<Object> params = new ArrayList<Object>();
		params.add(id);//自己
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		list = daService.getAll("select authorizer from dataauth_tb where authorizee=? order by authorizer desc ", new Object[]{id});
		for(Map<String, Object> map : list){
			Long authorizer = (Long)map.get("authorizer");
			if(!params.contains(authorizer)){
				params.add(authorizer);
			}
		}
		return params;
	}
	/**
	 * 根据车牌,车场编号查询是否是月卡
	 * @param carNumber
	 * @return
	 */
//	public boolean isMonthUser(Long uin,Long comId){
//		//先判断月卡
//	/*	if(carNumber==null||"".equals(carNumber))
//			return false;
//		Long uin  = null;
//		
//		Map carMap = daService.getMap("select uin from car_info_tb where car_number=?", new Object[]{carNumber});
//		
//		if(carMap==null||carMap.get("uin")==null){
//			return false;
//		}
//		uin=(Long) carMap.get("uin");*/
//		if(uin!=null&&uin!=-1){
//			Long ntime = System.currentTimeMillis()/1000;
//			Map<String, Object> pMap = daService.getMap("select p.b_time,p.e_time,p.type from product_package_tb p," +
//					"carower_product c where c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? and c.b_time<? order by c.id desc limit ?", 
//					new Object[]{comId,uin,ntime,ntime,1});
//			if(pMap!=null&&!pMap.isEmpty()){
//				//System.out.println(pMap);
//				Integer b_time = (Integer)pMap.get("b_time");
//				Integer e_time = (Integer)pMap.get("e_time");
//				Calendar c = Calendar.getInstance();
//				Integer hour = c.get(Calendar.HOUR_OF_DAY);
//				Integer type = (Integer)pMap.get("type");//0:全天 1夜间 2日间
//				boolean isVip = false;
//				if(type==0){//0:全天 1夜间 2日间
//					logger.info("全天包月用户，uin："+uin);
//					isVip = true;
//				}else if(type==2){//0:全天 1夜间 2日间
//					if(hour>=b_time&&hour<=e_time){
//						logger.info("日间包月用户，uin："+uin);
//						isVip = true;
//					}
//				}else if(type==1){//0:全天 1夜间 2日间
//					if(hour<=e_time||hour>=b_time){
//						logger.info("夜间包月用户，uin："+uin);
//						isVip = true;
//					}
//				}
//				return isVip;
//			}
//		}
//		return false;
//	}
	public List isMonthUser(Long uin,Long comId){
		Map<String, Object> rMap = new HashMap<String, Object>();
		Integer count = 0;
		ArrayList rList = new ArrayList();
		if (uin != null && uin != -1) {
			Long ntime = System.currentTimeMillis() / 1000;
			// 20160303加上套餐状态 禁用的话就不是月卡
//			List<Map<String, Object>> list = daService
//					.getAll(
//							"select p.b_time,p.e_time,p.type from product_package_tb p,"
//									+ "carower_product c where p.state<? and c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? and c.b_time<? order by c.id desc ",
//							new Object[] { 1, comId, uin, ntime, ntime });
			// ArrayList<Integer> arrayList = new ArrayList<Integer>();
			String sql = "select p.b_time,p.e_time,c.e_time etime, c.b_time btime,p.type,p.scope,p.comid from product_package_tb p,"
					+ "carower_product c where c.pid=p.id and (p.comid=? ";
			List comsList = daService.getAll("select * from com_info_tb where pid = ?",new Object[]{comId});
			Long parcomid = daService.getLong("select pid from com_info_tb where id = ?",new Object[]{comId});

			Object[] parm = null;
			int j=0;
			if(parcomid!=null&&parcomid>0){
				parm = new Object[comsList.size()+4];
				parm[0] = parcomid;
				sql += " or p.comid = ? ";
				j=1;
			}else{
				parm = new Object[comsList.size()+3];
			}
			parm[0+j] = comId;
			for (int i = 1; i < comsList.size()+1; i++) {
				long comidoth = Long.parseLong(((Map)comsList.get(i-1)).get("id")+"");
				parm[i+j] = comidoth;
				sql += " or p.comid = ? ";
			}
			parm[comsList.size()+1+j] = 1;
			parm[comsList.size()+2+j] = uin;
			List<Map<String, Object>> list = daService.getAll(sql+")and p.state<?  and c.uin=? order by c.id desc ",
					parm);
			logger.error(sql+")and p.state<?  and c.uin=? order by c.id desc "+","+parm);
			// ArrayList<Integer> arrayList = new ArrayList<Integer>();
			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
			if (list != null && !list.isEmpty()) {
				for (Map<String, Object> pMap : list) {
					// System.out.println(pMap);
					Long scope = (Long) pMap.get("scope");
					long comid = Long.parseLong(pMap.get("comid")+"");
					if(scope==0&&comid!=comId.longValue()){
						continue;
					}
					if(scope==0&&comid==parcomid.longValue()){
						continue;
					}
					Integer b_time = (Integer) pMap.get("b_time");
					Integer e_time = (Integer) pMap.get("e_time");
					Calendar c = Calendar.getInstance();
					Integer hour = c.get(Calendar.HOUR_OF_DAY);
					Integer type = (Integer) pMap.get("type");// 0:全天 1夜间 2日间
					Long btime = (Long) pMap.get("btime");
					Long etime = (Long) pMap.get("etime");
					if(ntime<btime||ntime>etime){
						if (map.containsKey(3)) {
							Integer value = map.get(3) + 1;
							map.put(3, value);
						} else {
							map.put(3, 1);
						}
						continue;
					}
					boolean isVip = false;
					if (type == 0) {// 0:全天 1夜间 2日间
						logger.error("全天包月用户，uin：" + uin);
						isVip = true;
						if (map.containsKey(0)) {
							Integer value = map.get(0) + 1;
							map.put(0, value);
						} else {
							map.put(0, 1);
						}
					} else if (type == 2) {// 0:全天 1夜间 2日间
						// if(hour>=b_time&&hour<=e_time){
						logger.error("日间包月用户，uin：" + uin);
						isVip = true;
						if (map.containsKey(2)) {
							Integer value = map.get(2) + 1;
							map.put(2, value);
						} else {
							map.put(2, 1);
						}
						// }
					} else if (type == 1) {// 0:全天 1夜间 2日间
						// if(hour<=e_time||hour>=b_time){
						logger.error("夜间包月用户，uin：" + uin);
						isVip = true;
						if (map.containsKey(1)) {
							Integer value = map.get(1) + 1;
							map.put(1, value);
						} else {
							map.put(1, 1);
						}
						// }
					}

					if (isVip) {
						count++;
					}
				}
			}
			rList.add(map);
			rList.add(count);
		}
		logger.error("check monthusers>>>uin:" + uin + ",count:" + count);
		return rList;
	}
	
	/**
	 * 取可用停车券，未认证车主最多使用3元券。 停车场专用停车券优先
	 * @param uin
	 * @param fee
	 * @param type: //0更早版本，不返比订单金额大的券，1老版本，自动选券时，返回普通券的最高抵扣金额，2新版本，根据券类型返回最高抵扣金额
	 * @param comId
	 * @return
	 */
	
	//http://127.0.0.1/zld/carowner.do?action=getaccount&mobile=13641309140&total=10&uid=21694&utype=1
	public  Map<String, Object> getTickets(Long uin,Double fee,Long comId,Integer type,Long uid){
		Map<String, Object> map=null;
		Integer limit = CustomDefind.getUseMoney(fee,0);
		Double splimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
		if(type==0){//0更早版本，不返比订单金额大的券，1老版本，自动选券时，返回普通券的最高抵扣金额，2新版本，根据券类型返回最高抵扣金额
			 logger.info("uin:"+uin+",comid:"+comId+",type:"+type+",fee:"+fee);
//			 map= useTickets(uin, fee, comId,uid,type);
		}else {
			logger.info("uin:"+uin+",comid:"+comId+",type:"+type+",fee:"+fee+",uselimit:"+limit);
			map = methods.getTickets(uin,fee,comId,uid);
			if(map!=null){
				Integer ttype = (Integer)map.get("type");
				Integer res = (Integer)map.get("resources");
				if(ttype==1||res==1)//普通券
					map.put("limit",StringUtils.formatDouble(fee-splimit));
				else {//车场专用券
					map.put("limit",limit);
				}
			}
		}
		logger.info("uin:"+uin+",comid:"+comId+",fee:"+fee+",map:"+map);
		if(map!=null){//查一下有没有相同金额的车场专用券
			Integer money = (Integer)map.get("money");
			Long limitday=(Long)map.get("limit_day");
			Integer ttype = (Integer)map.get("type");
			Integer res = (Integer)map.get("resources");
			if(ttype!=1&&res!=1){
				Map<String, Object> map1 = daService.getMap("select * from ticket_tb where comid=? and state=? and uin=? and  money=? and type=? and limit_day >=?  ",
						new Object[]{comId,0,uin,money,1,limitday});
				logger.info("uin:"+uin+",comid:"+comId+",fee:"+fee+",map1:"+map1);
				if(map1!=null&&!map1.isEmpty()){
					if(type==1)
						map1.put("limit",StringUtils.formatDouble(fee-splimit));
					return map1;
				}
			}
		}
		return map;
	}
	
	/**
	 * 取可用停车券
	 * @param uin    车主账户
	 * @param total  订单金额
	 * @param comId  车场编号 
	 * @param uid    收费员编号
	 * @param utype  0更早版本，不返比订单金额大的券，1老版本，自动选券时，返回普通券的最高抵扣金额，2新版本，根据券类型返回最高抵扣金额
	 * @return   可用停车券
	 */
//	public Map<String, Object> useTickets(Long uin,Double total,Long comId,Long uid,Integer utype){
//		//查出所有可用的券
//		//Long ntime = System.currentTimeMillis()/1000;
//		Integer limit = CustomDefind.getUseMoney(total,0);
////		boolean blackuser = isBlackUser(uin);
//		boolean blackparkuser = isBlackParkUser(comId, false);
//		boolean isauth = isAuthUser(uin);
//		if(!isauth){
//			if(blackuser||blackparkuser){
//				if(blackuser){
//					logger.info("车主在黑名单内uin:"+uin+",fee:"+total+",comid:"+comId);
//				}
//				if(blackparkuser){
//					logger.info("车场在黑名单内uin:"+uin+",fee:"+total+",comid:"+comId);
//				}
//				return null;
//			}
//		}else{
//			logger.info("车主uin:"+uin+"是认证车主，用券不判断是否是黑名单，车场是否黑名单。");
//		}
//		double ticketquota=-1;
//		if(uid!=-1){
//			Map usrMap =daService.getMap("select ticketquota from user_info_Tb where id =? and ticketquota<>?", new Object[]{uid,-1});
//			if(usrMap!=null){
//				ticketquota = Double.parseDouble(usrMap.get("ticketquota")+"");
//				logger.info("该收费员:"+uid+"的用券额度是："+ticketquota+"，(-1代表没限制)");
//			}
//		}
//		//所有可用停车券
//		List<Map<String,Object>> allTickets =methods.getUseTickets(uin, total);
//		Map<String, Object> ticketMap=null;
//		logger.info(allTickets);
//		if(allTickets!=null&&!allTickets.isEmpty()){
//			double spr_abs = 100;             //专用券抵扣金额与支付金额的差值
//			Integer spr_money_limit_abs=100;  //专用券抵扣金额与券金额的差值
//			double comm_abs = 100;            //普通券抵扣金额与券金额金额的差值
//			Integer comm_money_limit_abs=100; //普通券抵扣金额与券金额的差值
//			double buy_abs = 100;             //购买券抵扣金额与支付金额的差值
//			Integer buy_money_limit_abs=100;  //购买券抵扣金额与券金额的差值
//			Integer comm_index=-1;  //普通券索引      
//			Integer spr_index=-1;  //专用券索引
//			Integer buy_index=-1;  //购券索引
//			Integer comm_money=0;  //普通券抵扣金额
//			Integer spr_money=0;   //专用券抵扣金额
//			Integer buy_money=0;   //购券券抵扣金额
//			Integer index=-1;      //遍历索引
//			Integer spr_ticket_money=0;  //专用券金额
//			Integer buy_ticket_money=0;  //购券券金额
//			Integer comm_ticket_money=0; //普通券金额
//			for(Map<String,Object> map: allTickets){
//				Long cid = (Long)map.get("comid");//公司编号
//				Integer money = (Integer)map.get("money");
//				Integer type=(Integer)map.get("type");
//				Integer useLimit = (Integer)map.get("limit");
//				Integer res = (Integer)map.get("resources");
//				index ++;
//				if(utype==0&&money>=limit){//0更早版本，不返抵扣金额比订单金额大的券
//					continue;
//				}
//				if(type==1&&cid!=null&&!cid.equals(comId)){//非此车场的专用券不返
//					continue;
//				}
//				if(ticketquota!=-1&&ticketquota>money){//券额大于收费员用券最高金额，不返回
//					continue;
//				}
//				if(!isauth&&money>1){//非认证车主不能使用大于1元以上的券
//					continue;
//				}
//				if(useLimit==0){//抵扣金额为0
//					continue;
//				}
//				if(money>Math.ceil(total)&&res==1){//购买的券额大于订单金额
//					continue;
//				}
//				if(type==1){//专用券，取最小支付金额与抵扣金额差额
//					double abs = total-useLimit;   
//					Integer mlabs = money-useLimit;
//					if(spr_abs>abs){
//						spr_abs =abs;
//						spr_money_limit_abs=mlabs;
//						spr_index=index;  //保存索引
//						spr_money=useLimit; //保存抵扣金额
//						spr_ticket_money=money;//保存券金额
//					}else if(spr_abs==abs&&spr_money_limit_abs>mlabs){//当前支付金额与抵扣金额差值与上一张券一样时，取券金额与抵扣金额差值最小的
//						spr_index=index;
//						spr_money=useLimit;
//						spr_ticket_money=money;
//					}
//				}else {
//					if(res==1){//购买券
//						double abs = total-useLimit;
//						Integer mlabs = money-useLimit;
//						if(buy_abs>abs){
//							buy_abs =abs;
//							buy_money_limit_abs=mlabs;
//							buy_index=index;
//							buy_money=useLimit;
//							buy_ticket_money=money;
//						}else if(buy_abs==abs&&buy_money_limit_abs>mlabs){
//							buy_index=index;
//							buy_money=useLimit;
//							buy_ticket_money=money;
//						}
//						map.put("isbuy", "1");
//					}else {//普通券
//						double abs = total-useLimit;
//						Integer mlabs = money-useLimit;
//						if(comm_abs>abs){
//							comm_abs =abs;
//							comm_money_limit_abs=mlabs;
//							comm_index=index;
//							comm_money=useLimit;
//							comm_ticket_money=money;
//						}else if(comm_abs==abs&&comm_money_limit_abs>mlabs){
//							comm_index=index;
//							comm_money=useLimit;
//							comm_ticket_money=money;
//						}
//					}
//				}
//			}
//			logger.info(spr_index+":"+spr_money+":"+spr_ticket_money+","+buy_index+":"+buy_money+":"+
//						buy_ticket_money+","+comm_index+":"+comm_money+":"+comm_ticket_money);
//			if(spr_money>=comm_money&&spr_money>=buy_money){//根据抵扣金额，选最大的，优先选专用券
//				if(spr_money==buy_money){//专用券和购买券抵扣金额相同时，选券面金额小的
//					if(spr_ticket_money>buy_ticket_money){
//						ticketMap=allTickets.get(buy_index);
//					}
//				}
//				if(spr_money==comm_money){//专用券和普通券抵扣金额相同时，选券面金额小的
//					if(spr_ticket_money>comm_ticket_money){
//						ticketMap=allTickets.get(comm_index);
//					}
//				}
//				if(ticketMap==null&&spr_index>-1){
//					ticketMap=allTickets.get(spr_index);
//				}
//				if(utype<2&&ticketMap!=null)//老版本返回普通券的抵扣上限，防止支付失败
//					ticketMap.put("limit", limit<1?1:limit);
//			}else if(comm_money>=buy_money&&comm_index>-1){//根据抵扣金额，选最大的，没有专用券时优先选普通券
//				if(buy_money==comm_money){
//					if(comm_ticket_money>buy_ticket_money){
//						ticketMap=allTickets.get(buy_index);
//					}
//				}else {
//					ticketMap=allTickets.get(comm_index);
//				}
//				if(utype<2&&ticketMap!=null)
//					ticketMap.put("limit", limit<1?1:limit);
//			}else if(buy_index!=-1){
//				ticketMap=allTickets.get(buy_index);
//				if(utype<2&&ticketMap!=null)
//					ticketMap.put("limit", limit<1?1:limit);
//			}
//			logger.info("uin:"+uin+",total:"+total+",comid:"+comId+",uid:"+uid+",utype:"+utype+"选券结果："+ticketMap);
//		}
//		return ticketMap;
//	}
	/**
	 * 取可用停车券，未认证车主最多使用3元券。
	 * @param uin
	 * @param fee
	 * @return
	 */
	/*@SuppressWarnings("unchecked")
	public Map<String, Object> useTickets(Long uin,Double fee,Long comId,Long uid){
		//查出所有可用的券
		//Long ntime = System.currentTimeMillis()/1000;
		Integer limit = CustomDefind.getUseMoney(fee,0);
		Double splimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
		boolean blackuser = isBlackUser(uin);
		boolean blackparkuser = isBlackParkUser(comId, false);
		boolean isauth = isAuthUser(uin);
		if(!isauth){
			if(blackuser||blackparkuser){
				if(blackuser){
					logger.info("车主在黑名单内uin:"+uin+",fee:"+fee+",comid:"+comId);
				}
				if(blackparkuser){
					logger.info("车场在黑名单内uin:"+uin+",fee:"+fee+",comid:"+comId);
				}
				return null;
			}
		}else{
			logger.info("车主uin:"+uin+"是认证车主，用券不判断是否是黑名单，车场是否黑名单。");
		}
		List<Map<String, Object>> list = null;
		double ticketquota=-1;
		if(uid!=-1){
			Map usrMap =daService.getMap("select ticketquota from user_info_Tb where id =? and ticketquota<>?", new Object[]{uid,-1});
			if(usrMap!=null){
				ticketquota = Double.parseDouble(usrMap.get("ticketquota")+"");
				logger.info("该收费员:"+uid+"的用券额度是："+ticketquota+"，(-1代表没限制)");
			}
		}
		if(!isauth){//未认证车主最多使用2元券。
			double noAuth = 1.0;//未认证车主最高试用noAuth(2)元券,以后改动这个值就ok
			if(ticketquota>=0&&ticketquota<=noAuth){
//				ticketquota = ticketquota+1;
			}else{
				ticketquota=noAuth;//未认证车主最多使用2元券
			}
			list=	daService.getAll("select * from ticket_tb where uin = ? " +
					"and state=? and limit_day>=? and type<? and money<?  order by limit_day",
					new Object[]{uin,0,TimeTools.getToDayBeginTime(),2,ticketquota+1});
		}else {
			list  = daService.getAll("select * from ticket_tb where uin = ? " +
					"and state=? and limit_day>=? and type<? order by limit_day",
					new Object[]{uin,0,TimeTools.getToDayBeginTime(),2});
		}
		logger.info("uin:"+uin+",fee:"+fee+",comid:"+comId+",today:"+TimeTools.getToDayBeginTime());
		if(list!=null&&!list.isEmpty()){
			List<String> _over3day_moneys = new ArrayList<String>();
			int i=0;
			for(Map<String, Object> map : list){
				Double money = Double.valueOf(map.get("money")+"");
				//Long limit_day = (Long)map.get("limit_day");
				Long tcomid = (Long)map.get("comid");
				Integer type = (Integer)map.get("type");
//				logger.info("ticket>>>uin:"+uin+",comId:"+comId+",tcomid:"+tcomid+",type:"+type+",ticketid:"+map.get("id"));
				if(comId!=null&&comId!=-1&&tcomid!=null&&type == 1){
					if(comId.intValue()!=tcomid.intValue()){
						logger.info(">>>>get ticket:不是这个车场的停车券，不能用....comId:"+comId+",tcomid:"+tcomid+",uin:"+uin);
						i++;
						continue;
					}
				}
				Integer res = (Integer)map.get("resources");
				if(limit==0&&res==0&&type==0){//支付金额小于3元，不先普通券
					i++;					
					continue;
				}
				if(type==1||res==1){
					limit=Double.valueOf((fee-splimit)).intValue();
				}else {
					limit= CustomDefind.getUseMoney(fee,0);
				}
				map.put("isbuy", res);
				if(money>limit.intValue()){
					i++;
					continue;
				}else if(limit.intValue()==money){//券值+1元 等于 支付金额时直接返回
					return map;
				}
				//判断 是否 有 不是该车场的专用券
				
				map.remove("comid");
				//map.remove("limit_day");
				_over3day_moneys.add(i+"_"+Math.abs(limit-money));
				i++;
			}
			if(_over3day_moneys.size()>0){//3天以上停车券与停车费的绝对值分析 ，取绝对值最小的
				int sk = 0;//保存index
				double sv=0;//保存最小值
				int index = 0;
				for(String s : _over3day_moneys){
					int k = Integer.valueOf(s.split("_")[0]);
					double v = Double.valueOf(s.split("_")[1]);
					if(index==0){
						sk=k;
						sv = v;
					}else {
						if(sv>v){
							sk=k;
							sv = v;
						}
					}
					index++;
				}
				logger.info("uin:"+uin+",comid:"+comId+",sk:"+sk);
				return list.get(sk);
			}
		}else{
			logger.info("未选到券uin:"+uin+",comid:"+comId+",fee:"+fee);
		}
		return null;
	}*/
	//订单返拼手气红包
//	public void backTicket(double money,Long orderId,Long uin,Long comid,String openid){
//		Long ctime = System.currentTimeMillis()/1000;
//		//车场专用券
//		Map btcomMap = daService.getMap("select * from park_ticket_tb where comid=? ", new Object[]{comid});
//		if(money>=1&&btcomMap!=null){
//			logger.info(">>>>back ticket comid="+comid+",有专用停车券");
//			Integer num = (Integer)btcomMap.get("tnumber");
//			Integer exptime = (Integer)btcomMap.get("exptime");
//			Integer haveget = (Integer)btcomMap.get("haveget");
//			Long btid = (Long)btcomMap.get("id");
//			Double amount =StringUtils.formatDouble(btcomMap.get("money"));
//			if(haveget<num){//还可以领
//				int ret = daService.update("update park_ticket_tb set haveget=? where id = ? and tnumber>=? ",  new Object[]{haveget+1,btid,haveget+1});
//				if(ret==1){
//					ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,comid,type) values(?,?,?,?,?,?,?) ",
//							new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+(exptime+1)*24*60*60-1,amount,0,uin,comid,1});
//					logger.info(">>>>back ticket comid="+comid+",专用停车券：金额 ："+amount+",总数量:"+num+",已使用:"+(haveget+1)+"，用户："+uin+",领券结果:"+ret);
//				}
//			}
//		}
//		
//		if(money>=1&&memcacheUtils.readBackTicketCache(uin)){//一天只返一次红包
//			String sql = "insert into order_ticket_tb (uin,order_id,money,bnum,ctime,exptime,bwords) values(?,?,?,?,?,?,?)";
//			Object []values = null;
//			String content= CustomDefind.getValue("DESCRIPTION");
//			Long exptime = ctime + 24*60*60;
//			Long count = daService.getLong("select count(*) from user_account_tb where type=? and uin=? ", new Object[]{1, uin});
//			if(count == 1){//车主首笔支付返特殊大礼包
//				values = new Object[]{uin,orderId,36,18,ctime,exptime,content};
//				logger.info(">>>>>车主首笔消费，返18个红包36元...");
//			}else if(money>=1&&money<10){//手机支付大于1元小于10元，返3个红包共8元
//				values = new Object[]{uin,orderId,18,12,ctime,exptime,content};
//				logger.info(">>>>>订单返拼手气红包,大于1元小于10元，返3个红包共8元...");
//			}else if(money>=10){// 手机支付大于10元，返8个红包共18元
//				values = new Object[]{uin,orderId,28,20,ctime,exptime,content};
//				logger.info(">>>>>订单返拼手气红包,大于10元，返8个红包共18元...");
//			}
//			else {
//				logger.info(">>>>>订单返拼手气红包,不足一元，不玩了.....");
//				return;
//			}
//			int ret = daService.update(sql, values);
//			logger.info(">>>>>订单返拼手气红包,money :"+money+" ret :"+ret);
//			if(ret==1)
//				memcacheUtils.updateBackTicketCache(uin);
//			/*if(openid==null){//客户端支付，立即返三元停车券。openid为空时，是客户端支付
//				ret = daService.update( "insert into ticket_tb (create_time,limit_day,money,state,uin,type) values(?,?,?,?,?,?) ",
//						new Object[]{TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+16*24*60*60-1,3,0,uin,0});
//				logger.info(">>>>back ticket 客户端支付：金额 ：3,用户："+uin+",领券结果:"+ret);
//				logService.insertUserMesg(5, uin, "恭喜您获得一张三元停车券", "停车券提醒");
//			}*/
//		}else {
//			logger.info(">>>>>订单返拼手气红包,已经发过，不发了.....");
//		}
//		
//	}
	
	/*
	 * 获取微信公众号的基本access_token
	 */
//	public String getWXPAccessToken(){
//		//String access_token ="notoken";// memcacheUtils.getWXPublicToken();
//		String access_token = memcacheUtils.getWXPublicToken();
//		if(access_token.equals("notoken")){
//			String url = Constants.WXPUBLIC_GETTOKEN_URL;
//			//从weixin接口取access_token
//			String result = new HttpProxy().doGet(url);
//			logger.info("wxpublic_access_token json:"+result);
////			access_token = JsonUtil.getJsonValue(result, "access_token");//result.substring(17,result.indexOf(",")-1);
//			logger.info("wxpublic_access_token:"+access_token);
//			//保存到缓存 
////			memcacheUtils.setWXPublicToken(access_token);
//		}
//		logger.info("微信公众号access_token："+access_token);
//		return access_token;
//	}
	
	/*
	 * 获取微信公众号jsapi_ticket
	 */
//	public String getJsapi_ticket() throws JDOMException, IOException{
//		//String jsapi_ticket ="no_jsapi_ticket";// memcacheUtils.getJsapi_ticket();
//		String jsapi_ticket =memcacheUtils.getJsapi_ticket();
//		if(jsapi_ticket.equals("no_jsapi_ticket")){
//			String access_token = getWXPAccessToken();
//			String jsapi_ticket_url = "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token="+access_token+"&type=jsapi";
////			String result = CommonUtil.httpsRequest(jsapi_ticket_url, "GET", null);
////			net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
////			jsapi_ticket = jsonObject.getString("ticket");
////			logger.info("wxpublic jsapi_ticket:"+jsapi_ticket);
////			//保存到缓存
////			memcacheUtils.setJsapi_ticket(jsapi_ticket);
//		}
//		return jsapi_ticket;
//	}
	
	/*
	 * 微信公众号获取短链
	 */
//	public String getShortUrl(String longurl) throws JDOMException, IOException{
//		String short_url = null;
//		String access_token = getWXPAccessToken();
//		String params = "{\"action\":\"long2short\",\"long_url\":\""+longurl+"\"}";
//		String url = "https://api.weixin.qq.com/cgi-bin/shorturl?access_token="+access_token;
////		String result = CommonUtil.httpsRequest(url, "POST", params);
////		net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(result);
////		Integer errcode = (Integer)jsonObject.get("errcode");
////		if(errcode == 0){
////			short_url = (String)jsonObject.get("short_url");
////		}
//		return short_url;
//	}

//	public boolean isBlackUser(Long uin){
//		List<Long> blackUserList = memcacheUtils.doListLongCache("zld_black_users", null, null);
////		logger.info(">>>zld black users :"+blackUserList);
//		//是否在黑名单 
//		boolean isBlack = true;
//		if(blackUserList==null||!blackUserList.contains(uin))//不在黑名单中可以处理推荐返现
//			isBlack=false;
////		if(blackUserList!=null&&blackUserList.size()>5)
////			clearBlackUser();
//		return isBlack;
//	}
	//判断车场是否在黑名单内，uid是收费员时，isparkuser传true,uid为车场时，isparkuser传false
	public boolean isBlackParkUser(long uid,Boolean isparkuser){
		boolean isBlack = false;
		String parkback = CustomDefind.getValue("PARKBACK");
		if(StringUtils.isNotNull(parkback)){
			String []str = parkback.split(",");
			if(isparkuser){
				long count = 0;
				for (String string : str) {
					count += daService.getLong("select count(*) from user_info_tb where id=? and comid =?", new Object[]{uid,Long.parseLong(string)});
					if(count>0){
						isBlack=true;
						logger.info("收费员uid:"+uid+"所在的车场在黑名单内，所有返现,推荐奖,所有券取消");
						break;
					}
				}
			}else{
				for (String string : str) {
					if(Long.parseLong(string)==uid){
						isBlack=true;
						logger.info("车场:"+uid+"在黑名单内，所有返现,推荐奖,所有券取消");
						break;
					}
				}
			}
		}
		logger.info("判断车场或者收费员（所在的车场）是否在黑名单中："+isBlack);
		return isBlack;
	}
	
	public boolean isAuthUser(long uin){
		Map userMap = daService.getMap("select is_auth from user_info_tb where id =? ", new Object[]{uin});
		Integer isAuth = 0;
		if(userMap!=null&&userMap.get("is_auth")!=null)
			isAuth=(Integer)userMap.get("is_auth");
		boolean ret = isAuth==1?true:false;
		logger.info("uin:"+uin+"是否是认证用户ret:"+ret+"(true:认证用户，false:不是认证用户)");
		return ret;
	}
	
//	public void clearBlackUser(){
//		List<Long> blackUserList = memcacheUtils.doListLongCache("zld_black_users", null, null);
//		//logger.info(">>>zld black users :"+blackUserList);
//		if(blackUserList!=null){//不在黑名单中可以处理推荐返现
//			blackUserList=new ArrayList<Long>();
//			memcacheUtils.doListLongCache("zld_black_users", blackUserList, "update");
//		}
//	}
	/**
	 * 上传照片
	 * @param request
	 * @param uin
	 * @return
	 * @throws Exception
	 */
	public String uploadPicToMongodb (HttpServletRequest request,Long uin,String table) throws Exception{
		logger.info(">>>>>begin upload order picture....");
		Map<String, String> extMap = new HashMap<String, String>();
	    extMap.put(".jpg", "image/jpeg");
	    extMap.put(".jpeg", "image/jpeg");
	    extMap.put(".png", "image/png");
	    extMap.put(".gif", "image/gif");
		request.setCharacterEncoding("UTF-8"); // 设置处理请求参数的编码格式
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // 建立FileItemFactory对象
		factory.setSizeThreshold(16*4096*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 分析请求，并得到上传文件的FileItem对象
		upload.setSizeMax(16*4096*1024);
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			return "-1";
		}
		String filename = ""; // 上传文件保存到服务器的文件名
		InputStream is = null; // 当前上传文件的InputStream对象
		// 循环处理上传文件
		for (FileItem item : items){
			// 处理普通的表单域
			if (item.isFormField()){
				/*if(item.getFieldName().equals("comid")){
					if(!item.getString().equals(""))
						comId = item.getString("UTF-8");
				}*/
				
			}else if (item.getName() != null && !item.getName().equals("")){// 处理上传文件
				// 从客户端发送过来的上传文件路径中截取文件名
				logger.info(item.getName());
				filename = item.getName().substring(item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // 得到上传文件的InputStream对象
				
			}
		}
		String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// 扩展名
		String picurl = uin+"_"+System.currentTimeMillis()+file_ext;
		BufferedInputStream in = null;  
		ByteArrayOutputStream byteout =null;
	    try {
	    	in = new BufferedInputStream(is);   
	    	byteout = new ByteArrayOutputStream(1024);        	       
		      
	 	    byte[] temp = new byte[1024];        
	 	    int bytesize = 0;        
	 	    while ((bytesize = in.read(temp)) != -1) {        
	 	          byteout.write(temp, 0, bytesize);        
	 	    }        
	 	      
	 	    byte[] content = byteout.toByteArray(); 
	 	    DB mydb = MongoClientFactory.getInstance().getMongoDBBuilder("zld");
		    mydb.requestStart();
			  
		    DBCollection collection = mydb.getCollection(table);
		  //  DBCollection collection = mydb.getCollection("records_test");
			  
			BasicDBObject document = new BasicDBObject();
			document.put("uin",  uin);
			document.put("ctime",  System.currentTimeMillis()/1000);
			document.put("type", extMap.get(file_ext));
			document.put("content", content);
			document.put("filename", picurl);
			  //开始事务
			mydb.requestStart();
			collection.insert(document);
			  //结束事务
			mydb.requestDone();
			in.close();        
		    is.close();
		    byteout.close();
		    logger.info(">>>>上传图片完成 .....");
		} catch (Exception e) {
			e.printStackTrace();
			return "-1";
		}finally{
			if(in!=null)
				in.close();
			if(byteout!=null)
				byteout.close();
			if(is!=null)
				is.close();
		}
	    
		return picurl;
	}

/**
 * 余额支付泊车费
 * @param id
 * @param total
 * @param ticketId 优惠券编号 
 * @return
 */
//	public int payCarStopOrder(Long id,Double total,Long ticketId) {
//		Long ntime = System.currentTimeMillis()/1000;
//		
//		//查车主，泊车员，泊车点编号，车牌号
//		Map cotMap = daService.getMap("select cid,uin,euid,car_number from carstop_order_tb where id=?  ", new Object[]{id});
//		Long uin =(Long) cotMap.get("uin");
//		Long uid =(Long) cotMap.get("euid");
//		Long cid =(Long) cotMap.get("cid");
//		String carNumber = (String)cotMap.get("car_number");
//		
//		//泊车员所属,comid为0是停车宝泊车员，为其它时则为对应的停车场
//		Long comId  = -1L;
//		Map userMap=daService.getMap("select comid from user_info_Tb where id =?", new Object[]{uid});
//		if(userMap!=null){
//			comId = (Long)userMap.get("comid");
//		}
//		//查泊车点名称 
//		Map csMap = daService.getMap("select name from car_stops_tb where id=? ", new Object[]{cid});
//		String comName ="";
//		if(csMap!=null)
//			comName = (String)csMap.get("name");
//		//查停车券
//		Double ticketMoney = 0d;
//		if(ticketId!=null&&ticketId>0){
//			if(!memcacheUtils.readUseTicketCache(uin))//超过使用3次，返回不成功!
//				return -13;
//			Map ticketMap = daService.getMap("select money,type from ticket_tb where limit_day>=? and id=? and state=?",
//					new Object[]{TimeTools.getToDayBeginTime(),ticketId,0});
//			if(ticketMap!=null&&ticketMap.get("money")!=null&&Check.isDouble(ticketMap.get("money")+"")){
//				Integer type = (Integer)ticketMap.get("type");
//				if(type!=null&&type==2){//济南市场微信专用券
//					ticketMoney = Double.valueOf(ticketMap.get("money")+"");
//					ticketMoney = (10-ticketMoney)*total*0.1;
//				}else {
//					ticketMoney = Double.valueOf(ticketMap.get("money")+"");
//				}
//			}
//		}
//		
//		if(ticketMoney>total){//优惠券金额大于支付金额
//			ticketMoney=total;
//		}else {//车主账户余额加上优惠券金额
//			Double ubalance =null;
//			//车主账户余额
//			userMap = daService.getPojo("select balance from user_info_tb where id =?",	new Object[]{uin});
//			if(userMap!=null&&userMap.get("balance")!=null){
//				ubalance = Double.valueOf(userMap.get("balance")+"");
//				ubalance +=ticketMoney;//用户余额加上优惠券金额
//			}
//			logger.info(">>>>>>>>>>>>>>>>>>ticket money："+ticketMoney);
//			if(ubalance==null||ubalance<total){//帐户余额不足
//				return -12;
//			}
//		}
//		logger.info(">>>>>>>>>>carstoporder,comid:"+comId+",ticket:"+ticketId+",uin:"+uin+",uid:"+uid);
//		
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		//更新用户余额
//		Map<String, Object> userSqlMap = new HashMap<String, Object>();
//		//更新停车场余额
//	    Map<String, Object> comSqlMap = new HashMap<String, Object>();
//		//车主账户
//		Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//		//车场账户
//		Map<String, Object> parkAccountsqlMap =new HashMap<String, Object>();
//		//车主账户加停车券
//		Map<String, Object> userTicketAccountsqlMap =new HashMap<String, Object>();
//		//使用停车券更新
//		Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//		//停车宝账户扣停车券金额
//		Map<String, Object> tingchebaoAccountsqlMap =new HashMap<String, Object>();
//		
//		//扣除车主账户余额
//		userSqlMap.put("sql", "update user_info_tb  set balance =balance-? where id=?");
//		userSqlMap.put("values", new Object[]{total-ticketMoney,uin});
//		if(total-ticketMoney>0)
//			bathSql.add(userSqlMap);
//		//车主账户优惠券充值
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){//使用停车券，给车主账户先充值
//			userTicketAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type) values(?,?,?,?,?,?)");
//			userTicketAccountsqlMap.put("values", new Object[]{uin,ticketMoney,0,ntime-1,"停车券充值",7});
//			bathSql.add(userTicketAccountsqlMap);
//		}
//		//车主账户支付停车费明细
//		userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,uid,target) values(?,?,?,?,?,?,?,?)");
//		userAccountsqlMap.put("values", new Object[]{uin,total,1,ntime,"泊车费-"+comName,0,uid,1});
//		bathSql.add(userAccountsqlMap);
//
//		//不设置默认给个人账户20141120孙总要求修改
//		if(comId!=0){//写入公司账户
//			comSqlMap.put("sql", "update com_info_tb  set total_money =total_money+?,money=money+? where id=?");
//			comSqlMap.put("values", new Object[]{total,total,comId});
//			bathSql.add(comSqlMap);
//			
//			parkAccountsqlMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) values(?,?,?,?,?,?,?)");
//			parkAccountsqlMap.put("values",  new Object[]{comId,total,0,ntime,"泊车费_"+carNumber,uid,2});
//			bathSql.add(parkAccountsqlMap);
//		}else {//写入停车场账户
//			parkAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype) values(?,?,?,?,?)");
//			parkAccountsqlMap.put("values", new Object[]{total,1,ntime,"泊车费-车主"+carNumber,5});
//			bathSql.add(parkAccountsqlMap);
//		}
//		
//		//优惠券使用后，更新券状态，添加停车宝账户支付记录
//		if(ticketMoney>0&&ticketId!=null&&ticketId>0){
//			ticketsqlMap.put("sql", "update ticket_tb  set state=?,comid=?,utime=?,umoney=? where id=?");
//			ticketsqlMap.put("values", new Object[]{1,comId,System.currentTimeMillis()/1000,ticketMoney,ticketId});
//			bathSql.add(ticketsqlMap);
//			
//			tingchebaoAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype) values(?,?,?,?,?)");
//			tingchebaoAccountsqlMap.put("values", new Object[]{ticketMoney,1,ntime,"车主"+carNumber+"，使用停车代金券",0});
//			bathSql.add(tingchebaoAccountsqlMap);
//			memcacheUtils.updateUseTicketCache(uin);//用券缓存使用券次数
//		}
//		
//		boolean result= daService.bathUpdate(bathSql);
//		logger.info(">>>>>>>>>>>>>>>支付 ："+result);
//		if(result){//结算成功，处理返券及返现 
//			//处理返现（车场），返券（车主）
//			/* 每次用余额或微信或支付宝支付1元以上的完成的，补贴车场2元，补贴车主3元的停车券，
//			 * 车场返现不限(同一车主每日只能返3次)，
//			 * 车主每日返券限3张券
//			 * 每个车主每天使用停车券不超过3单 */
//			boolean isBlack = isBlackUser(uin);
//			if(!isBlack){
//				backTicket(total-ticketMoney, 997L, uin,comId,"");
//			}else {
//				logger.info(">>>>>black>>>>车主："+uin+",在黑名单内，不能返红包......");
//			}
//			if(total>=1)
//				handleRecommendCode(uin,isBlack);
//			return 5;
//		}else {
//			return -7;
//		}
//	}
	
	/*
	 * 微信公众号摇一摇绑定账号
	 */
//	public int sharkbinduser(String openid, Long uin, Long bind_count){//摇一摇绑定账号
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		Map<String, Object> wxuserMap = daService.getMap(
//				"select * from wxp_user_tb where openid=? ",
//				new Object[] { openid });//未绑定账户
//		logger.info(">>>>>>>>>>>>>进入绑定微信公众号>>>>>>>>>>>>,openid:"+openid);
//		if(wxuserMap != null){//使用过摇一摇直付
//			logger.info(">>>>>>>>>>>>>>>>>把虚拟账户的信息转移到绑定的真实帐户里>>>>>>>>>>>>>，虚拟账号uin："+wxuserMap.get("uin")+",真实账号uin:"+uin);
//			logger.info(">>>>>>>>>>>处理虚拟账户的推荐逻辑>>>>>>>>>>>>>>>>>>");
////			handleWxRecommendCode((Long)wxuserMap.get("uin"), bind_count);
//			Double wx_balance = 0d;//虚拟账户里的余额
//			if(wxuserMap.get("balance") != null){
//				 wx_balance = Double.valueOf(wxuserMap.get("balance") + "");
//			}
//			/*Map<String, Object> recomsqlMap =new HashMap<String, Object>();
//			recomsqlMap.put("sql", "update recommend_tb set nid=? where nid=?");
//			recomsqlMap.put("values", new Object[]{ uin, wxuserMap.get("uin") });
//			bathSql.add(recomsqlMap);*/
//			
//			Map<String, Object> trueUsersqlMap =new HashMap<String, Object>();
//			trueUsersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=?");
//			trueUsersqlMap.put("values", new Object[]{ wx_balance, uin });
//			bathSql.add(trueUsersqlMap);
//			
//			Map<String, Object> userAccountsqlMap =new HashMap<String, Object>();
//			userAccountsqlMap.put("sql", "update user_account_tb set uin=? where uin=?");
//			userAccountsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(userAccountsqlMap);
//			
//			//order_ticket_tb
//			Map<String, Object> orderTicketsqlMap =new HashMap<String, Object>();
//			orderTicketsqlMap.put("sql", "update order_ticket_tb set uin=? where uin=?");
//			orderTicketsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(orderTicketsqlMap);
//			
//			//ticket_tb
//			Map<String, Object> ticketsqlMap =new HashMap<String, Object>();
//			ticketsqlMap.put("sql", "update ticket_tb set uin=? where uin=?");
//			ticketsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(ticketsqlMap);
//			
//			//微信公众号用户表
//			Map<String, Object> wxusersqlMap =new HashMap<String, Object>();
//			wxusersqlMap.put("sql", "delete from wxp_user_tb where openid=?");
//			wxusersqlMap.put("values", new Object[]{ openid });
//			bathSql.add(wxusersqlMap);
//			
//			//用户帐户表
//			Map<String, Object> userPayAccountsqlMap =new HashMap<String, Object>();
//			userPayAccountsqlMap.put("sql", "update user_payaccount_tb set uin=? where uin=?");
//			userPayAccountsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(userPayAccountsqlMap);
//			
//			//日志表
//			Map<String, Object> logsqlMap =new HashMap<String, Object>();
//			logsqlMap.put("sql", "update alipay_log set uin=? where uin=?");
//			logsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(logsqlMap);
//			
//			Map<String, Object> ordersqlMap = new HashMap<String, Object>();
//			ordersqlMap.put("sql", "update order_tb set uin=? where uin=? ");
//			ordersqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(ordersqlMap);
//			
//			Map<String, Object> rewardsqlMap = new HashMap<String, Object>();
//			rewardsqlMap.put("sql", "update parkuser_reward_tb set uin=? where uin=? ");
//			rewardsqlMap.put("values", new Object[]{uin, wxuserMap.get("uin")});
//			bathSql.add(rewardsqlMap);
//			
//			Integer addcar_flag = 0;//不添加车牌号
//			if(wxuserMap.get("car_number") != null){
//				Long count = daService.getLong("select count(*) from car_info_tb where car_number=? ",
//						new Object[] { wxuserMap.get("car_number") });
//				if(count == 0){
//					addcar_flag = 1;//添加车牌号
//					Map<String, Object> carsqlMap = new HashMap<String, Object>();
//					carsqlMap.put("sql", "insert into car_info_Tb (uin,car_number) values(?,?)");
//					carsqlMap.put("values", new Object[]{uin, wxuserMap.get("car_number")});
//					bathSql.add(carsqlMap);
//				}
//			}
//			
//			
//			boolean b = daService.bathUpdate2(bathSql);
//			if(b){
//				b = memcacheUtils.readUseTicketCache((Long)wxuserMap.get("uin"));
//				
//				if(!b){
//					memcacheUtils.updateUseTicketCache(uin);
//					logger.info(">>>>>>>>>>>>>>>微信公众号绑定账户，该虚拟账号今天已用过券，绑定的真实账号写到缓存里，虚拟账号："+wxuserMap.get("uin")+"真实账号："+uin);
//				}else{
//					logger.info(">>>>>>>>>>>>>>>微信公众号绑定账户，该虚拟账号今天没有用过券，虚拟账号："+wxuserMap.get("uin")+"真实账号："+uin);
//				}
//				
//				if(addcar_flag == 1){
//					logger.info(">>>>>>>>>>>虚拟账号里有常用车牌号，并且之前车主没有注册车牌，把该车牌注册为用户车牌，虚拟账号："+wxuserMap.get("uin")+",真实账号："+uin+",车牌号："+wxuserMap.get("car_number"));
//					Map<String, Object> userMap = daService.getMap(
//							"select mobile from user_info_tb where id=? ",
//							new Object[] { uin });
//					if(userMap != null){
//						logger.info(">>>>>>>>>>处理添加车牌号后的查询礼包逻辑,虚拟账号："+wxuserMap.get("uin")+",真实账号："+uin+",车牌号："+wxuserMap.get("car_number"));
//						methods.checkBonus((String)userMap.get("mobile"), uin);
//					}
//				}
//				return 1;
//			}
//		}
//		return 0;
//	}
	
//	public int handleWxRecommendCode(Long nid, Long bind_count){
//		logger.info("handleWxRecommendCode>>>>>被推荐人nid:"+nid);
//		Map<String, Object> userMap = daService.getMap("select wxp_openid from user_info_tb where id=? ", new Object[]{nid});
//		
//		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
//		List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
//		list = daService.getAll("select * from recommend_tb where (nid=? or openid=?) and type=? and state=? ",
//						new Object[] { nid,userMap.get("wxp_openid"), 0, 0 });
//		Long count = daService.getLong("select count(*) from car_info_tb where uin=? and state=? ", new Object[]{nid, 1});
//		boolean isBlack = isBlackUser(nid);
//		if(list.isEmpty()){
//			logger.info("handleWxRecommendCode>>>>>该用户没有被推荐记录,被推荐人nid:"+nid);
//			return 1;
//		}else{
//			logger.info("handleWxRecommendCode>>>>>该用户有被推荐记录,被推荐人uin:"+nid);
//			if(bind_count == 0){
//				logger.info("handleWxRecommendCode>>>>>开始处理成功推荐逻辑，给收费员返钱,被推荐人uin:"+nid);
//			}else{
//				logger.info("handleWxRecommendCode>>>>>该条推荐记录无效,被推荐人uin:"+nid);
//			}
//			if(isBlack){
//				logger.info("handleWxRecommendCode>>>>>该用户在黑名单里，推荐失效，不返钱uin:"+nid);
//			}
//		}
//		if(count > 0){
//			logger.info("handleWxRecommendCode>>>>>:有车牌count:"+count+",uin:"+nid);
//			for(Map<String, Object> map : list){
//				Double money = 5d;//默认返5块
//				Long uid = -1L;
//				Integer parker_flag = 0;//0:非收费员推荐，1收费员推荐
//				if(map.get("pid") != null){
//					uid = (Long)map.get("pid");
//					Map usrMap =daService.getMap("select recommendquota from user_info_Tb where id =? ", new Object[]{uid});
//					if(usrMap!=null){
//						money = StringUtils.formatDouble(Double.parseDouble(usrMap.get("recommendquota")+""));
//						logger.info("该收费员的推荐奖额度是："+money);
//					}
//					boolean isParkBlack = isBlackParkUser(uid,true);
//					if(isParkBlack)
////						continue;
//						return 0;
//					Long count1 = daService.getLong(
//									"select count(*) from user_info_tb where id=? and (auth_flag=? or auth_flag=?) ",
//									new Object[] { uid, 1, 2 });
//					if(count1 > 0){
//						parker_flag = 1;//是收费员推荐的
//					}
//				}
//				if(map.get("money") != null && Double.valueOf(map.get("money") + "")>0){
//					money = Double.valueOf(map.get("money") + "");
//				}
//				if(parker_flag == 1 && count> 0){
//					if(bind_count == 0 && !isBlack ){
//						Long comId = -1L;
//						Map comMap = daService.getPojo("select comid from user_info_tb where id=?  ",new Object[] {uid});
//						Map msetMap =null;
//						Integer giveMoneyTo = null;//查询收费设定 mtype:0:公司账户，1：个人账户'
//						if(comMap!=null){
//							comId =(Long)comMap.get("comid");
//							if(comId!=null&&comId>0);
//								msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
//									new Object[]{comId,4});
//						}
//						if(msetMap!=null)
//							giveMoneyTo =(Integer)msetMap.get("giveto");
//						if(giveMoneyTo!=null&&giveMoneyTo==0&&comId!=null&&comId>0){//返现给停车场账户
//							Map<String, Object> comqlMap = new HashMap<String, Object>();
//							//停车场账户返现
//							comqlMap.put("sql", "update com_info_tb set total_money=total_money+?,money=money+?  where id=? ");
//							comqlMap.put("values", new Object[]{money,money,comId});
//							bathSql.add(comqlMap);
//							
//							//写入停车场账户明细
//							Map<String, Object> parkAccountMap = new HashMap<String, Object>();
//							parkAccountMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) " +
//									"values(?,?,?,?,?,?,?)");
//							parkAccountMap.put("values", new Object[]{comId,money,0,System.currentTimeMillis()/1000,"推荐奖励",uid,3});
//							bathSql.add(parkAccountMap);
//							logger.info(uid+">>>推荐奖励给停车场");
//							
//						}else {//返现给收费员账户
//							Map<String, Object> usersqlMap = new HashMap<String, Object>();
//							//收费员账户返现
//							usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
//							usersqlMap.put("values", new Object[]{money,uid});
//							bathSql.add(usersqlMap);
//							
//							//写入收费员账户明细
//							Map<String, Object> parkuserAccountMap = new HashMap<String, Object>();
//							parkuserAccountMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) " +
//									"values(?,?,?,?,?,?)");
//							parkuserAccountMap.put("values", new Object[]{uid,money,0,System.currentTimeMillis()/1000,"推荐奖励",3});
//							bathSql.add(parkuserAccountMap);
//						}
//						//更新推荐记录
//						Map<String, Object> recomsqlMap = new HashMap<String, Object>();
//						recomsqlMap.put("sql", "update recommend_tb set state=? where (nid=? or openid=?) and pid=?");
//						recomsqlMap.put("values", new Object[]{1,nid,userMap.get("wxp_openid"),uid});
//						bathSql.add(recomsqlMap);
//					}else{
//						//更新推荐记录
//						Map<String, Object> recomsqlMap = new HashMap<String, Object>();
//						recomsqlMap.put("sql", "update recommend_tb set state=? where (nid=? or openid=?) and pid=?");
//						recomsqlMap.put("values", new Object[]{3,nid,userMap.get("wxp_openid"),uid});
//						bathSql.add(recomsqlMap);
//					}
//				}
//			}
//			boolean b = false;
//			if(!bathSql.isEmpty()){
//				b = daService.bathUpdate2(bathSql);
//			}
//			
//			if(b){
//				logger.info("handleWxRecommendCode>>>>>推荐逻辑处理成功,被推荐人uin:"+nid);
//			}else{
//				logger.info("handleWxRecommendCode>>>>>推荐逻辑处理失败uin:"+nid);
//			}
//			
//			if(b){
//				return 1;
//			}else{
//				return 0;
//			}
//		}else{
//			logger.info("handleWxRecommendCode>>>>>:无车牌count:"+count+",uin:"+nid);
//			return 0;
//		}
//	}
	
	/**
	 * 取停车场
	 * @param lat
	 * @param lon
	 * @param payable是否可支付
	 * @return 2000以内的停车场
	 */
	public List<Map<String, Object>> getPark2kmList(Double lat,Double lon,Integer payable){
//		payable=1;//强制过滤不可支付车场
		double lon1 = 0.023482756;
		double lat1 = 0.017978752;
		String sql = "select id,company_name as name,longitude lng,latitude lat,parking_total total,share_number," +
				"address addr,phone,monthlypay,epay,type,isfixed from com_info_tb where longitude between ? and ? " +
				"and latitude between ? and ? and state=? and isview=? ";//and isfixed=? ";
		List<Object> params = new ArrayList<Object>();
		params.add(lon-lon1);
		params.add(lon+lon1);
		params.add(lat-lat1);
		params.add(lat+lat1);
		params.add(0);
		params.add(1);
	//	params.add(1);
//		if(payable==1){
//			sql +=" and isfixed=? and epay=? ";
//			params.add(1);
//			params.add(1);
//		}
		List list = null;//daService.getPage(sql, null, 1, 20);
		list = daService.getAll(sql, params, 0, 0);
		return list;
	}
	
	/**
	 * 领取红包注册用户
	 * @param mobile 手机号
	 * @param media 媒体来源 
	 * @param getcode 是否获取验证码
	 * @return
	 */
	public Long regUser(String mobile,Long media,Long uid,boolean getcode){
		Long uin = daService.getkey("seq_user_info_tb");
		Long ntime = System.currentTimeMillis()/1000;
		String strid = "zlduser"+uin;
		//用户表
		String sql= "insert into user_info_tb (id,nickname,password,strid," +
				"reg_time,mobile,auth_flag,comid,media,recom_code) " +
				"values (?,?,?,?,?,?,?,?,?,?)";
		Object[] values= new Object[]{uin,"车主",strid,strid,ntime,mobile,4,0,media.intValue(),uid};
		//2015-03-10，开卡及领红包时不再先写入停车券，登录时判断黑名单后添加停车券
		/*if(media==8||media==7){//7"融360",8"车生活"
			String tsql = "insert into ticket_tb (create_time,limit_day,money,state,uin) values(?,?,?,?,?) ";
			List<Object[]> insertvalues = new ArrayList<Object[]>();
			//Long ntime = System.currentTimeMillis()/1000;
			Object[] v1 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v2 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v3 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v4 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v5 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v6 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v7 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v8 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v9 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			Object[] v10 = new Object[]{ntime,ntime+15*24*60*60,10,0,uin};
			insertvalues.add(v1);insertvalues.add(v2);insertvalues.add(v3);insertvalues.add(v4);insertvalues.add(v5);
			insertvalues.add(v6);insertvalues.add(v7);insertvalues.add(v8);insertvalues.add(v9);insertvalues.add(v10);
			int result= daService.bathInsert(tsql, insertvalues, new int[]{4,4,4,4,4});
			if(result>0){
				logService.insertUserMesg(1, uin, "恭喜您获得十张10元停车券!", "红包提醒");
			}
		}else {*/
			int ts = backNewUserTickets(ntime, uin);
			logger.info("领取红包注册用户，返券："+ts+",不直接写入停车券表中，登录时验证是否是黑名单后返还");
//		}
		int r = daService.update(sql,values);
		logger.info("领取红包注册用户，注册结果："+r);
		if(r==1){
			//注册成功，查一下是否是收费员推荐
			if(media==999&&uid>0){
				Map userMap = daService.getMap("select comid from user_info_Tb where id =? and auth_flag in(?,?) and state=?", 
						new Object[]{uid,1,2,0}) ;
				Long comId =null;
				if(userMap!=null){
					comId =(Long)userMap.get("comid");
				}
				if(comId!=null&&comId>0){
					int rem = daService.update("insert into recommend_tb (pid,nid,type,state,create_time) values(?,?,?,?,?)",
							new Object[]{uid,uin,0,0,System.currentTimeMillis()/1000});
//					if(uid!=null&&comId!=null)
//						logService.updateScroe(5, uid, comId);//推荐车主，得1积分 
					//int backmoney = daService.update("update user_info_tb set balance=balance+5 where id=?", new Object[]{uid});
					logger.info("收费员推荐车主，通过领取红包注册成功,推荐记录："+rem);
				}else {
					logger.info("收费员推荐车主，通过领取红包注册成功，但推荐的收费员不存在:"+uid);
				}
			}
			
			
			int	eb = daService.update("insert into user_profile_tb (uin,low_recharge,limit_money,auto_cash," +
					"create_time,update_time) values(?,?,?,?,?,?)", 
					new Object[]{uin,10,25,1,ntime,ntime});
			logger.info("收费员推荐车主，自动支付设置:"+eb);
			if(!getcode){
				//注册成功，发送短信
				String mesg ="五笔电子停车费，三笔来自停车宝。停车天天有优惠，8元钱停5次车，下载地址： http://t.cn/RZJ4UAv 【停车宝】";
				SendMessage.sendMultiMessage(mobile, mesg);
			}
			return uin;
		}
		return -1L;
	}
	
	/**
	 * 处理收费员推荐返现，需消费一笔，且支付余额1元以上，不在黑名单内的车主，才返现给收费员
	 * @param uin  车主
	 */
	private void handleRecommendCode(Long uin,boolean isBlack){
		Long recom_code = null;
		Map recomMap = daService.getMap("select pid from recommend_tb where nid=? and state=? and type=? ", new Object[]{uin,0,0});
		if(recomMap==null||recomMap.isEmpty()){//没有相关车主未处理的推荐，直接返回
			logger.info(">>>>>>>>>>handle recommend,error: no pid ,uin:"+uin);
			return ;
		}else {
			recom_code = (Long )recomMap.get("pid");
		}
		//logger.info();
		Map usrMap = daService.getMap("select recom_code from user_info_tb where id=?", new Object[]{uin});
		if(usrMap == null){//车主是虚拟账户，还未绑定账户
			return;
		}
		logger.info(">>>>>>>>>>handle recommend"+usrMap);
		Long uid = (Long)usrMap.get("recom_code");
		if(recom_code==null||uid==null||recom_code.intValue()!=uid.intValue()||isBlackParkUser(recom_code,true)){
			logger.info(">>>>>>>>>>handle recommend,error:  recomCode:"+recom_code+",uid:"+uid);
			return ;
		}
		usrMap =daService.getMap("select auth_flag,mobile,recommendquota from user_info_Tb where id =? ", new Object[]{uid});
		logger.info(">>>>>>>>>>handle recommend"+usrMap);
		String mobile = "";
		
		//推荐人角色
		Long auth_flag = null;
		Double recommendquota = 5d;
		if(usrMap!=null){
			auth_flag = (Long) usrMap.get("auth_flag");
			mobile = (String)usrMap.get("mobile");
			recommendquota = StringUtils.formatDouble(Double.parseDouble(usrMap.get("recommendquota")+""));
			logger.info("该收费员的推荐奖额度是："+recommendquota);
		}
		
		if(isBlack){
			String mobile_end = mobile.substring(7);
			int result =daService.update("insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
					new Object[]{0,System.currentTimeMillis()/1000, uid, "推荐提醒", "你推荐的车主（手机尾号"+mobile_end+"），账户有刷单嫌疑，奖励取消。"} );
			int result1 = daService.update("update recommend_tb set state=? where nid=? and pid=?", new Object[]{2,uin,uid});
			logger.info(">>>>>>>>>奖励车场收费员 ，被推荐的车主在黑名单中，奖励取消:发消息："+result+"，推荐更新为黑名单："+result1);
			return ;
		}
		
		//是收费员推荐的车主，目前没有车主推荐车主的记录
		if(auth_flag!=null&&(auth_flag==1||auth_flag==2)){
			Long count  = daService.getLong("select count(ID) from recommend_tb where nid=? and pid=? and state=? and type=?", new Object[]{uin,uid,0,0});
			//推荐类型.0：车主，1:车场
			logger.info("is recom:"+count);
			if(count!=null&&count>0){//被推荐过，推荐人的奖励没有支付//奖励车场收费员账号5元
				List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
				Long comId = -1L;
				Map comMap = daService.getPojo("select comid from user_info_tb where id=?  ",new Object[] {uid});
				Map msetMap =null;
				Integer giveMoneyTo = null;//查询收费设定 mtype:0:公司账户，1：个人账户'
				if(comMap!=null){
					comId =(Long)comMap.get("comid");
					if(comId!=null&&comId>0);
						msetMap = daService.getPojo("select giveto from money_set_tb where comid=? and mtype=? ",
							new Object[]{comId,4});
				}
				if(msetMap!=null)
					giveMoneyTo =(Integer)msetMap.get("giveto");
				if(comId!=null&&comId>0&&giveMoneyTo!=null&&giveMoneyTo==0){//返现给停车场账户
					Map<String, Object> comqlMap = new HashMap<String, Object>();
					//停车场账户返现
					comqlMap.put("sql", "update com_info_tb set total_money=total_money+?,money=money+?  where id=? ");
					comqlMap.put("values", new Object[]{recommendquota,recommendquota,comId});
					sqlMaps.add(comqlMap);
					
					//写入停车场账户明细
					Map<String, Object> parkAccountMap = new HashMap<String, Object>();
					parkAccountMap.put("sql", "insert into park_account_tb(comid,amount,type,create_time,remark,uid,source) " +
							"values(?,?,?,?,?,?,?)");
					parkAccountMap.put("values", new Object[]{comId,recommendquota,0,System.currentTimeMillis()/1000,"推荐奖励",uid,3});
					sqlMaps.add(parkAccountMap);
					logger.info(uid+">>>推荐奖励给停车场");
				}else {
					Map<String, Object> usersqlMap = new HashMap<String, Object>();
					//收费员账户加5元
					usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
					usersqlMap.put("values", new Object[]{recommendquota,uid});
					sqlMaps.add(usersqlMap);
				
					//写入收费员账户明细
					Map<String, Object> parkuserAccountMap = new HashMap<String, Object>();
					parkuserAccountMap.put("sql", "insert into parkuser_account_tb(uin,amount,type,create_time,remark,target) " +
							"values(?,?,?,?,?,?)");
					parkuserAccountMap.put("values", new Object[]{uid,recommendquota,0,System.currentTimeMillis()/1000,"推荐奖励",3});
					sqlMaps.add(parkuserAccountMap);
					
				}
				//更新推荐记录
				Map<String, Object> recomsqlMap = new HashMap<String, Object>();
				recomsqlMap.put("sql", "update recommend_tb set state=?,money=? where nid=? and pid=?");
				recomsqlMap.put("values", new Object[]{1,recommendquota,uin,uid});
				sqlMaps.add(recomsqlMap);
				
				logger.info(count);
				boolean ret = daService.bathUpdate(sqlMaps);
				if(ret){//写入收费员消息表
					
					String mobile_end = mobile.substring(7);
					int result =daService.update("insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
							new Object[]{0,System.currentTimeMillis()/1000, uid, "推荐提醒", "你推荐的车主（手机尾号"+mobile_end+"）注册成功，你获得"+recommendquota+"元奖励。"} );
					logger.info(">>>>>>>>>奖励车场收费员推荐车主"+recommendquota+"元消息:"+result);
				}
				logger.info(">>>>>>>>>奖励车场收费员推荐车主"+recommendquota+"元："+ret);
			}
		}else {
			logger.info(uid);
		}
	}
	
//	public String getCollectMesgSwith(){
//		String swith = memcacheUtils.doStringCache("collectormesg_swith", null, null);
//		if(swith==null)
//			return "0";
//		return swith;
//	}
	
	/*
	 * 获取jssdk接口注入权限验证信息
	 */
//	public Map<String, String> getJssdkApiSign(HttpServletRequest request) throws JDOMException, IOException{
//		Map<String, String> ret = new HashMap<String, String>();
//		String jsapi_ticket = getJsapi_ticket();//jsapi_ticket是公众号用于调用微信JS接口的临时票据
//		String request_url = request.getRequestURL().toString();//当前请求的路径
//		String request_params = request.getQueryString();//请求的参数
//		if(request_params != null){
//			request_url += "?" + request_params;
//		}
////		ret = PayCommonUtil.sign(jsapi_ticket, request_url);
//		return ret;
//	}
	
	/*public void updateSorceq(Long btime,Long etime,Integer cType,Long uid,Long comId){
		if(cType!=null&&btime!=null&&(etime-btime>=15*60)){//订单时长超过15分钟，可以计一次0.2的积分
			if(cType==0)//NFC积分  ( 刷NFC卡，或扫牌生成有效订单，非在线支付积0.01分，在线支付超过一元，积2分。)
				logService.updateScroe(2, uid,comId);
			else if(cType==2||cType==3)//扫牌或照牌积分 
				logService.updateScroe(4, uid,comId);
		}
	}*/
	
//	public void setCityCache(Long comid,Integer city){
//		Map<Long, Integer> map = memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", null, null);
//		if(map==null||map.size()<20){
//			List<Map<String, Object>> idlist= daService.getAll("select id from com_info_tb where city between ? and ? ", new Object[]{370100,370199});
//			if(idlist!=null){
//				if(map==null)
//					map = new HashMap<Long, Integer>();
//				for(Map<String, Object> maps: idlist){
//					Long id = (Long)maps.get("id");
//					if(id!=null)
//						map.put(id, 1);
//				}
//				logger.info(">>>>>jinan city cache size:"+map.size());
//				memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//			}
//		}else {
//			if(city>=110000&&city<120000){
//				if(map!=null&&map.containsKey(comid)){
//					map.remove(comid);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//				}
//			}else {
//				if(!map.containsKey(comid)){
//					map.put(comid, 0);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//				}
//			}
//		}
//	}
	
	public boolean isCanBackMoney(Long comid){
		logger.info(">>>所有车场不返现");
		return false;
//		Map<Long, Integer> map = memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", null, null);
//		if(map==null){
//			logger.info(">>>没有非北京车场，直接返现...");
//			return true;
//		}else {
//			logger.info(">>>非北京车场缓存为:"+map.size()+",comid:"+comid);
//			if(map.containsKey(comid)){
//				/*Integer times = map.get(comid);
//				Integer rand = RandomUtils.nextInt(10);//在0-9中取随机数，为8时返现
//				if(rand==8){//返现
//					times = times+1;
//					map.put(comid, times);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//					logger.info(">>>济南车场，不符合返现计数,下个计数："+times+"，缓存为:"+map);
//					return true;
//				}else {
//					return false;
//				}
//				logger.info(">>>济南车场，返现计数:"+times);
//				if(times%10==0){
//					logger.info(">>>济南车场，符合返现计数:"+times);
//					return true;
//				}else {
//					times = times+1;
//					map.put(comid, times);
//					memcacheUtils.doMapLongIntegerCache("comid_backmoney_cache", map, "update");
//					logger.info(">>>济南车场，不符合返现计数,下个计数："+times+"，缓存为:"+map);
//					return false;
//				}*/
//				return false;
//			}else {
//				logger.info(">>>不在非北京车场缓存内，直接返现...");
//				return true;	
//			}
//		}
	}
	
	/*
	 * 发送打赏消息给车主
	 */
//	public void sendBounsMessage(String openid,Long uid, Double total,Long orderid ,Long uin){
//		try {
//			Map uidMap = daService.getMap("select nickname from user_info_tb where id=? ", new Object[]{uid});
//			Map<String, String> baseinfo = new HashMap<String, String>();
//			List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
//			String url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpfast.do?action=toreward&uid="+uid+"&orderid="+orderid+"&openid="+openid;
//			String remark = "点击详情，用券打赏！";
//			String remark_color = "#FF0000";
//			baseinfo.put("url", url);
//			baseinfo.put("openid", openid);
//			baseinfo.put("top_color", "#000000");
//			baseinfo.put("templeteid", Constants.WXPUBLIC_BONUS_NOTIFYMSG_ID);
//			Map<String, String> keyword1 = new HashMap<String, String>();
//			keyword1.put("keyword", "keyword1");
//			keyword1.put("value", uidMap.get("nickname") + "("+uid+")");
//			keyword1.put("color", "#000000");
//			orderinfo.add(keyword1);
//			Map<String, String> keyword2 = new HashMap<String, String>();
//			keyword2.put("keyword", "keyword2");
//			keyword2.put("value", "使用停车宝计费收费");
//			keyword2.put("color", "#000000");
//			orderinfo.add(keyword2);
//			Map<String, String> keyword3 = new HashMap<String, String>();
//			keyword3.put("keyword", "remark");
//			keyword3.put("value", remark);
//			keyword3.put("color", remark_color);
//			orderinfo.add(keyword3);
//			Map<String, String> keyword4 = new HashMap<String, String>();
//			keyword4.put("keyword", "first");
//			keyword4.put("value", "如果对本次收费员服务满意，你可以使用停车券打赏收费员！");
//			keyword4.put("color", "#000000");
//			orderinfo.add(keyword4);
//			Long count = daService.getLong("select count(*) from parkuser_reward_tb where uin=? and ctime>=? and uid=? ",
//					new Object[] { uin, TimeTools.getToDayBeginTime(), uid });
//			logger.info("同一车主对同一收费员的今日打赏次数：uid:"+uid+",uin:"+uin+",openid:"+openid+",orderid:"+orderid+",count:"+count);
//			if(count == 0){
//				sendWXTempleteMsg(baseinfo, orderinfo);
//			}
//		} catch (Exception e) {
//			// TODO: handle exception
//			e.printStackTrace();
//		}
//	}
	
	/*
	 * 消息模板公用方法 
	 */
//	public void sendWXTempleteMsg(Map<String, String> baseinfo,List<Map<String, String>> orderinfo) throws JSONException{
//		//推送模板消息
//		JSONObject msgObject = new JSONObject();
//		JSONObject dataObject = new JSONObject();
//		for(Map<String, String> map : orderinfo){
//			JSONObject keynote = new JSONObject();
//			keynote.put("value", map.get("value"));
//			keynote.put("color", map.get("color"));
//			dataObject.put(map.get("keyword"), keynote);
//		}
//		msgObject.put("touser", baseinfo.get("openid"));
//		msgObject.put("template_id", baseinfo.get("templeteid"));//
//		msgObject.put("url", baseinfo.get("url"));
//	    msgObject.put("topcolor", baseinfo.get("top_color"));
//		msgObject.put("data", dataObject);
//		String accesstoken = getWXPAccessToken();
//		String msg = msgObject.toString();
////		PayCommonUtil.sendMessage(msg, accesstoken);
//	}
	
	private Double getBackMoney(){
		Double moneys[] = new Double[]{0d,1d,2d,3d,4d};
		Integer rand = RandomUtils.nextInt(5);
		return moneys[rand];
	}
	
	private double getminPriceUnit(Long comId){
		Map com =daService.getPojo("select * from com_info_tb where id=? "
				, new Object[]{comId});
		double minPriceUnit = Double.valueOf(com.get("minprice_unit")+"");
		return minPriceUnit;
	}
	
//	public boolean isCanSendShortMesg(String mobile){
//		Map<String, String> sendCache = memcacheUtils.doMapStringStringCache("verification_code_cache", null, null);
//		Long ttime = TimeTools.getToDayBeginTime();
//		//System.err.println(sendCache);
//		if(sendCache==null){
//			sendCache = new HashMap<String, String>();
//			sendCache.put(mobile, ttime+"_"+1);
//		}else {
//			String value = sendCache.get(mobile);
//			if(value!=null&&value.indexOf("_")!=-1){
//				String dayt[] =value.split("_");
//				Long time= Long.valueOf(dayt[0]);
//				if(time.equals(ttime)){
//					Integer times = Integer.valueOf(dayt[1]);
//					if(times>9){
//						logger.info(mobile+"发送验证码超过10次");
//						return false;
//					}else {
//						value = time+"_"+(times+1);
//						sendCache.put(mobile,value);
//					}
//				}else {
//					sendCache.put(mobile, ttime+"_"+1);
//				}
//			}else {
//				sendCache.put(mobile, ttime+"_"+1);
//			}
//		}
//		 memcacheUtils.doMapStringStringCache("verification_code_cache", sendCache, "update");
//		return true;
//	}

	/**
	 * 计算停车券最高抵扣金额
	 * @param type 0打赏，1支付
	 * @param uin  车主账户
	 * @param uid  收费员账户
	 * @param total  停车费金额
	 * @return 抵扣金额
	 */
	public Double useTicket(Long uin,Long uid,Integer type,Double total){
		boolean isAuth = isAuthUser(uin);
		Double maxMoney = 1.0;//未认证，最高抵一元
		if(type==0&&isAuth){//0打赏
			Double rewardquota = 2.0;
			Map user = daService.getMap("select rewardquota from user_info_tb where id = ?", new Object[]{uid});
			if(user!=null&&user.get("rewardquota")!=null)
				rewardquota =StringUtils.formatDouble(user.get("rewardquota"));
			if(rewardquota>total)//收费员最高打赏设置金额大于打赏金额时，用券最高设置为打赏金额
				rewardquota= total;
			maxMoney= rewardquota;
		}else if(type==1&&isAuth){//1支付
			//支付金额与停车券金额差值，如抵扣金额与订单金额的差值为1，则订单为10元时，券最高抵扣9元，
			//20元的券也只能抵扣9元，如果为2，则最高抵扣8元
			Double uselimit = StringUtils.formatDouble(CustomDefind.getValue("TICKET_LIMIT"));
			maxMoney= total-uselimit;
		}
		return maxMoney;
	}
	
	/**
	 * 获取代金券抵扣金额
	 * @param ticketId
	 * @param ptype 0账户充值；1包月产品；2停车费结算；3直付;4打赏 5购买停车券
	 * @param uid
	 * @param total 金额
	 * @param utype 0普通选券（默认）1可用大于最大抵扣金额的停车券
	 * @param comid
	 * @param orderId
	 * @return
	 */
	private Double getTicketMoney(Long ticketId, Integer ptype, Long uid, Double total, Integer utype, Long comid, Long orderId){
		Double ticketMoney = 0d;
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		Map<String, Object> ticketMap = daService.getMap("select * from ticket_tb where id=? ", new Object[]{ ticketId });
		logger.info("orderid:"+orderId+",ticketid:"+ticketId+",ticketMap:"+ticketMap);
		if(ticketMap != null){
			list.add(ticketMap);
			list = methods.chooseTicketByLevel(list, ptype, uid, total, utype, comid, orderId);
			ticketMap = list.get(0);
			ticketMoney = Double.valueOf(ticketMap.get("limit") + "");
			logger.info("orderid:"+orderId+",ticketMap:"+ticketMap);
		}
		return ticketMoney;
	}
	
	/**
	 * 获取打折券抵扣金额
	 * @param uin
	 * @param uid
	 * @param total
	 * @return
	 */
	private Double getDisTicketMoney(Long uin, Long uid, Double total){
		Double ticketMoney = 0d;
		Map<String, Object> ticketMap = methods.chooseDistotalTicket(uin, uid, total);
		if(ticketMap != null){
			ticketMoney = StringUtils.formatDouble(ticketMap.get("money"));
		}
		return ticketMoney;
	}
	public void uploadpic2line(String fn,Long comid,Long orderid,String lefttop,String rightbottom,String width,String height,Long type) throws Exception{
		System.out.println("-----------------------------------");
		InputStream is = null;
		try {
			File f = new File(fn);
			is = new FileInputStream(f);
//    		map.put("content",list.get(1));
    		Map map = daService.getMap("select line_id from order_tb where id = ?", new Object[]{orderid});
    		System.out.println(orderid);
    		if(map!=null&&map.get("line_id")!=null){
    			Long line_id = Long.parseLong(map.get("line_id")+"");
//    			Long Line_preorderid = daService.getLong("select line_id from order_tb where id = ?", new Object[]{preorderid});
        		String params = "action=uploadpic&comid="
    				+ comid + "&orderid=" + line_id + "&lefttop=" + lefttop
    				+ "&rightbottom=" + rightbottom + "&type=" + type
    				+ "&width=" + width + "&height=" + height;
//    			String rets = requestLine("http://192.168.199.251/zldline/syncInter.do"+ "?" + request.getQueryString(),null);
    			URL url = new URL(CustomDefind.DOMAIN+"/carpicsup.do"+ "?" + params);
    			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    			String  BOUNDARY =  UUID.randomUUID().toString();
    			conn.setReadTimeout(4*1000);
    			conn.setConnectTimeout(4*1000);
    			conn.setDoInput(true);  //允许输入流
    			conn.setDoOutput(true); //允许输出流
    			conn.setUseCaches(false);  //不允许使用缓存
    			conn.setRequestMethod("POST");  //请求方式
    			conn.setRequestProperty("Charset", "utf-8");  //设置编码
    			conn.setRequestProperty("connection", "keep-alive");   
    			conn.setRequestProperty("Content-Type", "multipart/form-data" + ";boundary=" +BOUNDARY ); 
//    			InputStream is = null;
    			if(is != null)
    			{
    				/**
    				 * 当文件不为空，把文件包装并且上传
    				 */
    				DataOutputStream dos = new DataOutputStream( conn.getOutputStream());
    				StringBuffer sb = new StringBuffer();
    				sb.append("--");
    				sb.append(BOUNDARY);
    				sb.append("\r\n");
    				/**
    				 * 这里重点注意：
    				 * name里面的值为服务器端需要key   只有这个key 才可以得到对应的文件
    				 * filename是文件的名字，包含后缀名的   比如:abc.png  
    				 */
    				sb.append("Content-Disposition: form-data; name=\"img\"; filename=\""+"zhenlaidian.jpg"+"\""+"\r\n"); 
    				sb.append("Content-Type: application/octet-stream; charset="+"utf-8"+"\r\n");
    				sb.append("\r\n");
    				dos.write(sb.toString().getBytes());
    				byte[] bytes = new byte[1024];
    				int len = 0;
    				while((len=is.read(bytes))!=-1)
    				{
    					dos.write(bytes, 0, len);
    				}
    				is.close();
    				dos.write("\r\n".getBytes());
    				byte[] end_data = ("--"+BOUNDARY+"--"+"\r\n").getBytes();
    				dos.write(end_data);
    				dos.flush();
    				/**
    				 * 获取响应码  200=成功
    				 * 当响应成功，获取响应的流  
    				 */
    				int res = conn.getResponseCode();  
    				String msg = conn.getResponseMessage();
    				if(res==200){
    					if(type==0){
    						int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{1,orderid});
    					}else if(type==1){
    						int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{1,orderid});
    					}
    					System.out.println("uploadpic success----------------");
    				}else{
    					if(type==0){
    						int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{0,orderid});
    					}else if(type==1){
    						int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{0,orderid});
    					}
    				}
    			}
    		}
		} catch (Exception e) {
			if(type==0){
				int r = daService.update("update order_tb set uploadinpic_state=? where id = ?", new Object[]{0,orderid});
			}else if(type==1){
				int r = daService.update("update order_tb set uploadoutpic_state=? where id = ?", new Object[]{0,orderid});
			}
			e.printStackTrace();
		}finally{
			if(is!=null)
				is.close();
		}
//    		
	}
	public void uploadOrder2Line(final Long orderid,final Integer type,Integer sync_state){
//	    ExecutorService executor = Executors.newSingleThreadExecutor();  
//	    FutureTask<String> future =  
//	           new FutureTask<String>(new Callable<String>() {//使用Callable接口作为构造参数  
//	             public String call() {
//	            	 System.out.println(orderid);
	            	 Map map = daService.getMap("select * from order_tb where id = ?",new Object[]{orderid});
	            	 if(type==3&&(map==null||map.get("line_id")==null)){
	            		 int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{0,orderid});
	            	 }else{
	            		 String order = AjaxUtil.encodeUTF8(StringUtils.createJson(map));
		            	 HttpProxy httpProxy = new HttpProxy();
		            	 Map parammap = new HashMap();
		            	 parammap.put("order", order);
		            	 String ret = null;
		            	 try {
		            		String token = null;
		         			Map session = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
		         			if(session!=null&&session.get("token")!=null){
		         				token = session.get("token")+"";
		         			}
		         			 parammap.put("token", token);
		            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadOrder2Line&type="+type, parammap);
		            		 System.out.println("上传订单："+ret);
			         	        if(ret!=null&&ret.startsWith("1")){
			         	        	String rets[] = ret.split("_");
			         	        	if(sync_state==1){
			         	        		int r = daService.update("update order_tb set uin = ?,sync_state=?,line_id=? where id = ?", new Object[]{Long.valueOf(ret.split("_")[1]),1,Long.valueOf(ret.split("_")[2]),orderid});
			         	        		daService.update("update ticket_tb set lineorderid = ? where orderid = ?", new Object[]{Long.valueOf(rets[2]),Long.valueOf(rets[3])});
			         	        	}else if(sync_state==2){
			         	        		int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{sync_state,orderid});
			         	        		Map map2 = daService.getMap("select line_id from order_tb where id = ?", new Object[]{Long.valueOf(rets[1])});
										if(map2!=null&&map2.get("line_id")!=null){
											daService.update("update ticket_tb set lineorderid = ? where orderid = ?", new Object[]{Long.valueOf(map.get("line_id")+""),Long.valueOf(rets[1])});
											String tk = "";
											Map m = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
											if(m!=null&&m.get("token")!=null){
												token = m.get("token")+"";
											}
											upload2ticket(tk);
										}
			         	        	}
			         	        }else{
			         	        	int r = daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{0,orderid});
			         	        	}
							} catch (Exception e) {
								int r = daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{0,orderid});
								e.printStackTrace();
							}
	            	 }
//					return ret;  
//	           }});  
//	    executor.execute(future);  
//	    try {  
//	        String result = future.get(3000, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果  
//	        if(result!=null&&result.startsWith("1")){
//	        	int r = daService.update("update order_tb set sync_state=? where id = ?", new Object[]{2,orderid});
//	        	System.out.println(r);
//	        }
//	    } catch (InterruptedException e) {  
//	    	future.cancel(true);  
//	    } catch (ExecutionException e) {  
//	    	future.cancel(true);  
//	    } catch (TimeoutException e) {  
//	    	future.cancel(true);  
//	    } finally {  
//	        executor.shutdown();  
//	    }  
	}
	
//	private void updateAllowCache(Long comid,Long ticketId, Double ticketMoney){
//		logger.info("updateAllowCache>>>ticketId:"+ticketId+",ticketMoney:"+ticketMoney+",comid:"+comid);
//		if(ticketMoney > 0){
//			Double tcballow = ticketMoney;//停车宝补贴的部分
//			if(ticketId != null && ticketId > 0){
//				Map<String, Object> ticketMap = daService.getMap(
//						"select * from ticket_tb where id=? ",
//						new Object[] { ticketId });
//				Integer type = (Integer)ticketMap.get("type");
//				Integer resources = (Integer)ticketMap.get("resources");
//				if(type == 0 && resources == 1){//购买券
//					if(ticketMap.get("pmoney") != null){
//						Double pmoney = Double.valueOf(ticketMap.get("pmoney") + "");
//						logger.info("updateAllowCache>>>ticketId:"+ticketId+",ticketMoney:"+ticketMoney);
//						if(ticketMoney > pmoney){
//							tcballow = ticketMoney - pmoney;
//						}else{
//							tcballow = 0d;
//						}
//					}
//				}
//			}
//			logger.info("updateAllowCache>>>ticketId:"+ticketId+",tcballow:"+tcballow);
//			memcacheUtils.updateAllowanceCache(tcballow);
//			memcacheUtils.updateAllowCacheByPark(comid, tcballow);
//		}
//	}
	private void upload2ticket(String tk){
		ArrayList list = new ArrayList();
		 list.add(0);
		 list.add(-1);
		 List lists = daService.getAll("select * from ticket_tb where sync_state=? and lineorderid >?",list,1,10);
		 String ret = null;
		 if(lists!=null&&lists.size()>0){
			 String ticket = AjaxUtil.encodeUTF8(StringUtils.createJson(lists));
	       	 HttpProxy httpProxy = new HttpProxy();
	       	 Map parammap = new HashMap();
	       	 parammap.put("ticket", ticket);
	       	 try {
	       		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadTicket2Line&token="+tk, parammap);
	       		 if(ret!=null){
	       			 String[] strs = ret.split(",");
	           		 for (int i = 0; i < strs.length; i++) {
	    					if(strs[i]!=null){
	    						if(strs[i].startsWith("1")){
	    							String rets[] = strs[i].split("_");
	    							int r = daService.update("update ticket_tb set sync_state=? where id = ?", new Object[]{1,Long.valueOf(rets[1])});
	    						}
	    					}
	    				}
    				} 
       		 }catch (Exception e) {
					e.printStackTrace();
				}
		 }
	}
	public void updateShopTicket(Long orderid, Long uin){
		int r = daService.update("update ticket_tb set state=?,uin=?,utime=?,sync_state=? where orderid=? ", 
				new Object[]{1, uin, System.currentTimeMillis()/1000,0, orderid});
	}
	/**
	 * 计算分段月卡价格
	 */
	public Map monthPrice(Map orderMap, List<Map<String, Object>> list,
			Long end_time, Map dayMap, Map nigthMap, Map assistMap) {
		logger.error("计算分段月卡价格");
		// oMap=CountPrice.getAccount((Long)orderMap.get("create_time"),end_time,
		// dayMap, nigthMap,minPriceUnit,assistMap);
		Long create_time = (Long) orderMap.get("create_time");
		Map oMap = new HashMap();
		// {total=2.0, duration=24分钟, etime=15:50, btime=15:26, collect=2.0,
		// discount=0}
		// oMap.put("collect", 2.0);
		// oMap.put("collect", 2.0);
		// oMap.put("collect", 2.0);
		// oMap.put("collect", 2.0);
		double total = 0;
		if (list != null && list.size() > 0) {
			Map<String, Object> pMap = list.get(0);
			Integer b_time = (Integer) pMap.get("b_time");// 套餐每天开始的小时
			Integer e_time = (Integer) pMap.get("e_time");// 套餐每天结束的小时
			Integer bmin = (Integer) pMap.get("bmin");// 套餐每天开始的分钟
			Integer emin = (Integer) pMap.get("emin");// 套餐每天结束的分钟
			Integer type = (Integer) pMap.get("type");// 套餐每天结束的分钟
			Long bt = (Long) pMap.get("bt");// 套餐开始的时间
			Long et = (Long) pMap.get("et");// 套餐结束的时间
			long durday = 0;
			long times = 0;
			boolean frist = true;
			// if(create_time<bt){//包月开始前进场的车
			// //计算套餐开始前的价格
			// if(end_time>bt){
			// oMap=CountPrice.getAccount(create_time,bt, dayMap,
			// nigthMap,0,assistMap);
			// if(oMap!=null&&oMap.get("collect")!=null){
			// total+=Double.parseDouble(oMap.get("collect")+"");
			// }
			// //将免费时长置为0 优惠时长去掉
			// if(dayMap!=null){
			// dayMap.put("first_times", 0);
			// dayMap.put("fprice", 0);
			// dayMap.put("free_time", 0);
			// }
			// if(nigthMap!=null){
			// nigthMap.put("first_times", 0);
			// nigthMap.put("fprice", 0);
			// nigthMap.put("free_time", 0);
			// }
			// frist = false;
			// //将时间开始点挪到套餐开始时
			// create_time = bt;
			// }else{
			// oMap=CountPrice.getAccount(create_time,bt, dayMap,
			// nigthMap,0,assistMap);
			// return oMap;
			// }
			// }else{
			// create_time = et;
			// }
			// if(end_time>et){
			// //计算套餐结束后的价格，
			// oMap=CountPrice.getAccount(bt,end_time, dayMap, nigthMap,0,null);
			// if(oMap!=null&&oMap.get("collect")!=null){
			// total+=Double.parseDouble(oMap.get("collect")+"");
			// }
			// //将免费时长置为0 优惠时长去掉
			// if(dayMap!=null){
			// dayMap.put("first_times", 0);
			// dayMap.put("fprice", 0);
			// dayMap.put("free_time", 0);
			// }
			// if(nigthMap!=null){
			// nigthMap.put("first_times", 0);
			// nigthMap.put("fprice", 0);
			// nigthMap.put("free_time", 0);
			// }
			// //将时间结束时间点挪到套餐结束时
			// end_time = et;
			// }
			//判断有没有封顶价
//			double totalday = 0;
			Object dtotal24 = -1;
			if (dayMap != null)
				dtotal24 = dayMap.get("total24");
			double total24 = -1;
			Boolean b = StringUtils.isDouble(dtotal24 + "");
			if (b) {
				total24 = Double.parseDouble(dtotal24 + "");
			}
			if(b&&total24>0){//有封顶价
				if ((end_time - create_time) % (24 * 3600) == 0) {
					times = (end_time - create_time) / (24 * 3600);
				} else {
					times = (end_time - create_time) / (24 * 3600) + 1;
				}
				for (int i = 1; i <= times; i++) {// 每一次封顶一次
					double totalday = 0;
//					Object dtotal24 = -1;
//					if (dayMap != null)
//						dtotal24 = dayMap.get("total24");
//					double total24 = -1;
//					Boolean b = StringUtils.isDouble(dtotal24 + "");
//					if (b) {
//						total24 = Double.parseDouble(dtotal24 + "");
//					}
					if (i == times) {
						ArrayList<String> monthPrice = monthTimes(create_time,
								end_time, b_time, e_time, bmin, emin, type);
						for(int j = 0;j<monthPrice.size();j++) {
							String tmp = monthPrice.get(j);
							String[] tmpArr = tmp.split("_");
							if (tmpArr.length == 2) {
								Long startTime = Long.parseLong(tmpArr[0]);
								Long endTime = Long.parseLong(tmpArr[1]);
								if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//由于之前的分段凌晨24点自动会分段，不满足特殊的要求   
									if(j+1<monthPrice.size()){
										String tmp2 = monthPrice.get(j+1);
										String[] tmpArr2 = tmp2.split("_");
										if (tmpArr2.length == 2) {
											endTime = Long.parseLong(tmpArr2[1]);
										}
										j++;
									}
								}
								if (frist) {
									// CountPrice.getAccount(start, end, dayMap,
									// nightMap, minPriceUnit, assistPrice)
									oMap = CountPrice
											.getAccount(startTime, endTime, dayMap,
													nigthMap, 0, assistMap);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
									frist = false;
									// 将免费时长置为0 优惠时长去掉
									if (dayMap != null) {
										dayMap.put("first_times", 0);
										dayMap.put("fprice", 0);
										dayMap.put("free_time", 0);
									}
									if (nigthMap != null) {
										nigthMap.put("first_times", 0);
										nigthMap.put("fprice", 0);
										nigthMap.put("free_time", 0);
									}
								} else {
									oMap = CountPrice.getAccount(startTime,
											endTime, dayMap, nigthMap, 0, null);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
								}
							}
						}
					} else {
						ArrayList<String> monthPrice = monthTimes(create_time,
								create_time + 24 * 3600, b_time, e_time, bmin,
								emin, type);
						create_time = create_time + 24 * 3600;
//						for (String str : monthPrice) {
						for(int j = 0;j<monthPrice.size();j++) {
							String tmp = monthPrice.get(j);
							String[] tmpArr = tmp.split("_");
							if (tmpArr.length == 2) {
								Long startTime = Long.parseLong(tmpArr[0]);
								Long endTime = Long.parseLong(tmpArr[1]);
								if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//由于之前的分段凌晨24点自动会分段，不满足特殊的要求   
									if(j+1<monthPrice.size()){
										String tmp2 = monthPrice.get(j+1);
										String[] tmpArr2 = tmp2.split("_");
										if (tmpArr2.length == 2) {
											endTime = Long.parseLong(tmpArr2[1]);
										}
										j++;
									}
								}
								if (frist) {
									// CountPrice.getAccount(start, end, dayMap,
									// nightMap, minPriceUnit, assistPrice)
									oMap = CountPrice
											.getAccount(startTime, endTime, dayMap,
													nigthMap, 0, assistMap);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
									frist = false;
									// 将免费时长置为0 优惠时长去掉
									if (dayMap != null) {
										dayMap.put("first_times", 0);
										dayMap.put("fprice", 0);
										dayMap.put("free_time", 0);
									}
									if (nigthMap != null) {
										nigthMap.put("first_times", 0);
										nigthMap.put("fprice", 0);
										nigthMap.put("free_time", 0);
									}
								} else {
									oMap = CountPrice.getAccount(startTime,
											endTime, dayMap, nigthMap, 0, null);
									if (oMap != null && oMap.get("collect") != null) {
										totalday += Double.parseDouble(oMap
												.get("collect")
												+ "");
									}
								}
							}
						}
					}
					if (total24 >0&&totalday > total24) {// 封顶
						total += total24;
					}else{
						total += totalday;
					}
				}
			}else{
				ArrayList<String> monthPrice = monthTimes(create_time,
						end_time, b_time, e_time, bmin,
						emin, type);
				create_time = create_time + 24 * 3600;
//				for (String str : monthPrice) {
				for(int j = 0;j<monthPrice.size();j++) {
					String tmp = monthPrice.get(j);
					String[] tmpArr = tmp.split("_");
					if (tmpArr.length == 2) {
						Long startTime = Long.parseLong(tmpArr[0]);
						Long endTime = Long.parseLong(tmpArr[1]);
						if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//由于之前的分段凌晨24点自动会分段，不满足特殊的要求   
							if(j+1<monthPrice.size()){
								String tmp2 = monthPrice.get(j+1);
								String[] tmpArr2 = tmp2.split("_");
								if (tmpArr2.length == 2) {
									endTime = Long.parseLong(tmpArr2[1]);
								}
								j++;
							}
						}
						if (frist) {
							// CountPrice.getAccount(start, end, dayMap,
							// nightMap, minPriceUnit, assistPrice)
							oMap = CountPrice
									.getAccount(startTime, endTime, dayMap,
											nigthMap, 0, assistMap);
							if (oMap != null && oMap.get("collect") != null) {
								total += Double.parseDouble(oMap
										.get("collect")
										+ "");
							}
							frist = false;
							// 将免费时长置为0 优惠时长去掉
							if (dayMap != null) {
								dayMap.put("first_times", 0);
								dayMap.put("fprice", 0);
								dayMap.put("free_time", 0);
							}
							if (nigthMap != null) {
								nigthMap.put("first_times", 0);
								nigthMap.put("fprice", 0);
								nigthMap.put("free_time", 0);
							}
						} else {
							oMap = CountPrice.getAccount(startTime,
									endTime, dayMap, nigthMap, 0, null);
							if (oMap != null && oMap.get("collect") != null) {
								total += Double.parseDouble(oMap
										.get("collect")
										+ "");
							}
						}
					}
				}
			}
		}
		oMap.put("collect", total);
		return oMap;
	}
	//上面没有封顶家的方法，暂时没用  下次做的时候可能用到
	private Double noTotal24(Long create_time, Long end_time, Integer b_time, Integer e_time, Integer bmin, Integer emin, Integer type, Map dayMap, Map nigthMap, Map assistMap){
		Map oMap = new HashMap();
		double total = 0;
		boolean frist = true;
		ArrayList<String> monthPrice = monthTimes(create_time,end_time, b_time, e_time, bmin,emin, type);
		create_time = create_time + 24 * 3600;
//		for (String str : monthPrice) {
		for(int j = 0;j<monthPrice.size();j++) {
			String tmp = monthPrice.get(j);
			String[] tmpArr = tmp.split("_");
			if (tmpArr.length == 2) {
				Long startTime = Long.parseLong(tmpArr[0]);
				Long endTime = Long.parseLong(tmpArr[1]);
				if(TimeTools.getTime_yyyyMMdd_HHmmss(endTime*1000).endsWith("00:00:00")){//由于之前的分段凌晨24点自动会分段，不满足特殊的要求   
					if(j+1<monthPrice.size()){
						String tmp2 = monthPrice.get(j+1);
						String[] tmpArr2 = tmp2.split("_");
						if (tmpArr2.length == 2) {
							endTime = Long.parseLong(tmpArr2[1]);
						}
						j++;
					}
				}
				if (frist) {
					// CountPrice.getAccount(start, end, dayMap,
					// nightMap, minPriceUnit, assistPrice)
					oMap = CountPrice
							.getAccount(startTime, endTime, dayMap,
									nigthMap, 0, assistMap);
					if (oMap != null && oMap.get("collect") != null) {
						total += Double.parseDouble(oMap
								.get("collect")
								+ "");
					}
					frist = false;
					// 将免费时长置为0 优惠时长去掉
					if (dayMap != null) {
						dayMap.put("first_times", 0);
						dayMap.put("fprice", 0);
						dayMap.put("free_time", 0);
					}
					if (nigthMap != null) {
						nigthMap.put("first_times", 0);
						nigthMap.put("fprice", 0);
						nigthMap.put("free_time", 0);
					}
				} else {
					oMap = CountPrice.getAccount(startTime,
							endTime, dayMap, nigthMap, 0, null);
					if (oMap != null && oMap.get("collect") != null) {
						total += Double.parseDouble(oMap
								.get("collect")
								+ "");
					}
				}
			}
		}
		return total;
	}
	/**
	 * 分段月卡分段
	 * 
	 * @param create_time
	 * @param end_time
	 * @param btime
	 * @param etime
	 * @param bmin
	 * @param emin
	 * @param type
	 * @return
	 */
	public static ArrayList<String> monthTimes(Long create_time, Long end_time,
			Integer btime, Integer etime, Integer bmin, Integer emin,
			Integer type) {
		ArrayList<String> resultList = new ArrayList<String>();
		if (create_time < end_time) {
			Integer b_time = btime;// (Integer)pMap.get("b_time");
			Integer e_time = etime;// (Integer)pMap.get("e_time");
			long durday = 0;
			// 计算垮了几天
			long b = create_time;
			while (b < end_time) {
				long a = getToDayBeginTime(b) + 3600 * 24;
				durday += 1;
				b = a;
			}
			Long todayb = getToDayBeginTime(create_time);// 当天开始凌晨那一秒
			Long todaypb = todayb + 60 * 60 * b_time + bmin * 60;// 当天套餐开始
			long todaype = todayb + 60 * 60 * e_time + emin * 60;// 当天套餐结束
			Integer packagetype = type;// (Integer)pMap.get("type");//0:全天 1夜间
			// 2日间
			if (type == 1) {
				todaype = todayb + 3600 * 24 + 60 * 60 * e_time + emin * 60;
			}
			for (int i = 1; i <= durday; i++) {// 多少天就循环多少次，每天计算2次价格
				if (packagetype == 2) {// 1夜间 2日间
					if (create_time < todaypb) {// 套餐开始时前进来的
						if (i == durday) {
							if (end_time > todaypb) {
								// todo计算开始时间到套餐开始时间
								System.out
										.println("计算时间段1:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(create_time * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
								resultList.add(create_time + "_" + todaypb);
								if (end_time > todaype) {
									// todo计算套餐结束到end_time
									System.out
											.println("计算时间段2:"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(todaype * 1000)
													+ "--->"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(todaype + "_" + end_time);
								}
							} else {
								// todo计算开始时间到结束时间
								System.out
										.println("计算时间段3:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(create_time * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(end_time * 1000));
								resultList.add(create_time + "_" + end_time);
							}
						} else {
							// todo计算开始时间到套餐开始时
							System.out
									.println("计算时间段4:"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss(create_time * 1000)
											+ "--->"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
							resultList.add(create_time + "_" + todaypb);
							// todo计算套餐结束时到24点
							System.out
									.println("计算时间段5:"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss(todaype * 1000)
											+ "--->"
											+ TimeTools
													.getTime_yyyyMMdd_HHmmss((todayb + 24 * 3600) * 1000));
							resultList
									.add(todaype + "_" + (todayb + 24 * 3600));
							create_time = todayb + 24 * 3600;
							todayb = todayb + 24 * 3600;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						}
					} else {// 套餐开始时后进来的
						if (i == durday) {
							// todo计算开始时间到套餐开始时间
							if (end_time > todaype) {
								if (create_time>todaype) {
									// todo计算套餐结束到end_time
									System.out
											.println("计算时间段6:"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(create_time * 1000)
													+ "--->"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(create_time + "_" + end_time);
								}else{
									// todo计算套餐结束到end_time
									System.out
											.println("计算时间段7:"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(todaype * 1000)
													+ "--->"
													+ TimeTools
															.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(todaype + "_" + end_time);
								}
							}
						} else {
							// todo计算开始时间到
							// System.out.println("计算时间段3:"+todaype+"--->23:59:59");
							if (create_time > todaype) {
								System.out
										.println("计算时间段8:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(create_time * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss((todayb + 24 * 3600) * 1000));
								resultList.add(create_time + "_"
										+ (todayb + 24 * 3600));
							} else {
								System.out
										.println("计算时间段9:"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss(todaype * 1000)
												+ "--->"
												+ TimeTools
														.getTime_yyyyMMdd_HHmmss((todayb + 24 * 3600) * 1000));
								resultList.add(todaype + "_"
										+ (todayb + 24 * 3600));
							}
							create_time = todayb + 24 * 3600;
							todayb = todayb + 24 * 3600;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						}
					}
				} else if (packagetype == 1) {// 0:全天 1夜间 2日间
					if (end_time > create_time) {
						if (create_time < todaypb) {// 套餐开始前进来的
							if(create_time<todaype-24*3600){
								create_time = todaype-24*3600;
							}
							// 计算开始时间到套餐开始
							if (i == durday) {
								if(end_time>todaypb){
									System.out.println("计算时间段1:"+TimeTools.getTime_yyyyMMdd_HHmmss(create_time * 1000)+ "--->"+ TimeTools.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
									resultList.add(create_time + "_" + todaypb);
								}else{
									System.out.println("计算时间段2:"+TimeTools.getTime_yyyyMMdd_HHmmss(create_time * 1000)+ "--->"+ TimeTools.getTime_yyyyMMdd_HHmmss(end_time * 1000));
									resultList.add(create_time + "_" + end_time);
								}
							} else {
								System.out.println("计算时间段3:"+ TimeTools.getTime_yyyyMMdd_HHmmss(create_time * 1000)+ "--->"+ TimeTools.getTime_yyyyMMdd_HHmmss(todaypb * 1000));
								resultList.add(create_time + "_" + todaypb);
							}
							create_time = todaype;
							todayb = todaype;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						} else {// 套餐开始后进来的，这是直接将指针都指向第二天的结束点
							create_time = todaype;
							todayb = todaype;
							todaypb = (todaypb + 24 * 3600);
							todaype = (todaype + 24 * 3600);
						}
					}
				}
			}
		}
		return resultList;

	}
	public static Long getToDayBeginTime(long date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String today = sdf.format(new Date(date * 1000));
		today = today.substring(0, 10) + " 00:00:00";
		long millSeconds = (new GregorianCalendar()).getTimeInMillis();
		try {
			millSeconds = sdf.parse(today).getTime();
		} catch (Exception e) {

		}
		return new Long(millSeconds / 1000);
	}
}

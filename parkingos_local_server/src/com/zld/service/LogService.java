package com.zld.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.PublicMethods;
import com.zld.impl.PushtoSingle;
import com.zld.utils.HttpProxy;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

@Service
public class LogService {

	
	@Autowired
	private DataBaseService databasedao;
	@Autowired
	private PushtoSingle pushtoSingle;
//	@Autowired
//	private MemcacheUtils memcacheUtils;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(LogService.class);
	/**
	 * 
	 * @param dService
	 * @param comid
	 * @param uin
	 * @param log
	 * @type:
	 * 0:创建订单，1：结算订单,2:优惠，3:现金收费
	 */
	public void updateOrderLog(Long comid,Long uin,String log,Integer type){
		databasedao.update("insert into order_log_tb (comid,uin,create_time,log,type) values (?,?,?,?,?)", 
				new Object[]{comid,uin,System.currentTimeMillis()/1000,log,type});
	}
	/**
	 * 
	 * @param dService
	 * @param comid
	 * @param uin
	 * @param log
	 * @param type 
	 * 100:创建停车场，101：修改停车场，102：删除停车场，
	 * 201：修改管理员，202：创建停车员，203：修改停车员，204：禁用停车员，
	 * 205：删除停车员，206：修改密码，207：添加价格，208：修改价格，209：删除价格，210：添加包月产品，211：删除包月，
	 * 300：添加市场专员，301修改市场专员，302删除市场专员，
	 * 400：添加商圈，401：编辑商圈，402：删除商圈
	 */
	public void updateSysLog(Long comid,String uid,String log,Integer type){
		databasedao.update("insert into user_log_tb (comid,uid,create_time,logs,type) values (?,?,?,?,?)", 
				new Object[]{comid,uid,System.currentTimeMillis()/1000,log,type});
	}
	/**
	 * 分享日志表
	 * @param comid 停车场编号
	 * @param uin 收费员或管理员号
	 * @param numer 分享数
	 */
	public void updateShareLog(Long comid,Long uin,Integer numer){
		databasedao.update("insert into share_log_tb (comid,uin,create_time,s_number) values (?,?,?,?)", 
				new Object[]{comid,uin,System.currentTimeMillis()/1000,numer});
	}
	
	/**
	 * //写车主消息
	 * @param comId
	 * @param state  ---- 0:未结算，1：待支付，2：支付完成, -1:支付失败
	 * @param uin 帐号（用户或收费员）
	 * @param body 车牌号
	 * @param orderId 订单编号
	 * @param total 金额
	 * @param duration 时长
	 * @param issale 是否打折，0否 1:是
	 * @param btime 开始时间UTC
	 * @param etime 结束时间UTC
	 * @mtype  0:订单消息，1：车位预定消息  2:充值购买产品  3直付订单消息（收费员用） 4Ibeacon解绑消息(收费员)  9Ibeacon支付消息
	 */
	public void insertMessage(Long comId,Integer state,Long uin,String body,Long orderId,
			Double total,String duration,Integer isSale,Long  btime,Long etime,Integer mtype){
		Long id = databasedao.getLong("SELECT nextval('seq_order_message_tb'::REGCLASS) AS newid", null);
		int result = databasedao.update("insert into order_message_tb (id,comid,state,uin,create_time,car_number," +
				"orderid,order_total,duartion,is_sale,btime,etime,message_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?)", 
				new Object[]{id,comId,state,uin,System.currentTimeMillis()/1000,body,
				orderId,total,duration,isSale,btime,etime,mtype});
		if(result==1){//消息写库成功
			//查用户角色及登录ID,用于通过个推发送消息
			String cid  = "";
			Map userMap = databasedao.getPojo("select cid from user_info_tb where id =? and auth_flag=?",new Object[]{uin,4});
			if(userMap!=null&&userMap.get("cid")!=null){
				cid =(String) userMap.get("cid");
			}
			//车主,发消息
			Map messageMap = databasedao.getPojo("select * from order_message_tb where id=?", new Object[]{id});
			String ret = getMessage(uin,messageMap);
			if(cid!=null&&cid.length()>10){
				if(ret!=null&&!"".equals(ret)){
					if(cid.length()>32){//IOS消息
						pushtoSingle.sendMessageByApns(uin,ret, cid);
					}else {//android消息
						pushtoSingle.sendSingle(cid, ret);
					}
				}
			}
			/*if(state!=1){//不是待支付消息，写入缓存，2.0版本后消息从缓存读取
				Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("caruser_messages", null, null);
				if(messCacheMap==null)
					messCacheMap = new HashMap<Long, String>();
				messCacheMap.put(uin, ret);
				memcacheUtils.doMapLongStringCache("caruser_messages", messCacheMap, "update");
			}*/
//			uploadOrder2Line(id);
		}
	}
	/**
	 * //写收费员消息，（收费员定时取消息）
	 * @param comId
	 * @param state  ---- 0:未结算，1：待支付，2：支付完成, -1:支付失败
	 * @param uin 帐号（用户或收费员）
	 * @param body 车牌号
	 * @param orderId 订单编号
	 * @param total 金额
	 * @param duration 时长
	 * @param issale 是否打折，0否 1:是
	 * @param btime 开始时间UTC
	 * @param etime 结束时间UTC
	 * @mtype  0:订单消息，1：车位预定消息  2:充值购买产品  3直付订单消息（收费员用） 4Ibeacon解绑消息(收费员) 5:打赏消息 6首页通知消息  7车主领券消息 8推荐奖到帐通知9Ibeacon支付消息
	 */
//	public void insertParkUserMessage(Long comId,Integer state,Long uin,String body,Long orderId,
//			Double total,String duration,Integer isSale,Long  btime,Long etime,Integer mtype){
//		Long id = databasedao.getLong("SELECT nextval('seq_order_message_tb'::REGCLASS) AS newid", null);
//		int result = databasedao.update("insert into order_message_tb (id,comid,state,uin,create_time,car_number," +
//				"orderid,order_total,duartion,is_sale,btime,etime,message_type) values (?,?,?,?,?,?,?,?,?,?,?,?,?)", 
//				new Object[]{id,comId,state,uin,System.currentTimeMillis()/1000,body,
//				orderId,total,duration,isSale,btime,etime,mtype});
//		if(result==1){//消息写库成功
//			//写入收费员消息缓存
//			String ret ="{}";
//			if(mtype==3){//泊车消息
//				ret = "{\"mtype\":"+mtype+",\"info\":{\"orderid\":\""+orderId+"\""+
//						",\"carnumber\":\""+body+"\",\"duration\":\""+duration+"\"," +
//						"\"state\":\""+state+"\"}}";
//			}else if(mtype==4){//收费员离开工作站通知
//				ret = "{\"mtype\":"+mtype+",\"info\":{}}";
//			}else if(mtype==5){//收费员打赏通知
//				int limit = 0;
//				if(comId == -2){
//					limit = 1;
//				}
//				int fivelimit = 0;
//				Long count = databasedao.getLong("select count(*) from reward_account_tb r,ticket_tb t where r.ticket_id=t.id and r.type=? and r.target=? and r.create_time>? and t.money=? and r.uin=? ",
//						new Object[] { 1, 2, TimeTools.getToDayBeginTime(), 5, uin });
//				if(count >= 10){
//					fivelimit = 1;
//				}
//				Long fivescore = 20 * (count + 1);
//				ret = "{\"mtype\":"+mtype+",\"info\":{\"carnumber\":\""+body+"\",\"uin\":\""+orderId+"\",\"rcount\":\""+duration+"\",\"total\":\""+total+"\",\"limit\":\""+limit+"\",\"fivelimit\":\""+fivelimit+"\",\"fivescore\":\""+fivescore+"\"}}";
//				System.out.println("rewardmessage：ret:"+ret);
//			}else if(mtype==8){//推荐奖到帐通知
//				ret = "{\"mtype\":"+mtype+",\"info\":{\"mobile\":\""+body+"\",\"uin\":\""+orderId+"\",\"total\":\""+total+"\"}}";
//			}else {
//				Map<String, Object> infomMap = new HashMap<String, Object>();
//				infomMap.put("btime",TimeTools.getTime_yyMMdd_HHmm(btime*1000).substring(9));
//				infomMap.put("etime", TimeTools.getTime_yyMMdd_HHmm(etime*1000).substring(9));
//				infomMap.put("carnumber", body);
//				infomMap.put("duration",duration);
//				infomMap.put("total",total);
//				infomMap.put("state",state);//0:未支付 1：已支付 
//				infomMap.put("orderid",orderId);
//				String json = StringUtils.createJson(infomMap);
//				ret= "{\"mtype\":"+mtype+",\"info\":"+json+"}";
//			}
//			databasedao.update("update order_message_tb set already_read =? where id=?", new Object[]{1,id});
//			Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("parkuser_messages", null, null);
//			if(messCacheMap==null)
//				messCacheMap = new HashMap<Long, String>();
//			messCacheMap.put(uin, ret);
//			memcacheUtils.doMapLongStringCache("parkuser_messages", messCacheMap, "update");
//		}
//	}
	
	
	/**
	 * 写入消息缓存，在客户端读取(定时取消息)
	 * @param type 6首页弹出消息 7:车主领券通知
	 * @param uin  用户/收费员帐号
	 * @param infoMap  其他信息
	 */
//	public void insertParkUserMesg(Integer type, Map<String, Object> infoMap){
//		Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("parkuser_messages", null, null);
//		if(messCacheMap==null){
//			messCacheMap = new HashMap<Long, String>();
//		}
//		String ret ="{}";
//		if(type == 6){
//			ret = "{\"mtype\":"+type+",\"info\":{}}";
//			List<Object> uins = new ArrayList<Object>();
//			if(infoMap.get("uins") != null){
//				uins = (List)infoMap.get("uins");
//			}
//			for(Object object : uins){
//				Long uin = (Long)object;
//				messCacheMap.put(uin, ret);
//			}
//			System.out.print("notice msg>>>type:"+type+",通知人数:"+uins.size());
//		}else if(type == 7){
//			ret = "{\"mtype\":"+type+",\"info\":{\"carnumber\":\""+infoMap.get("carnumber")+"\",\"score\":\""+infoMap.get("score")+"\",\"tmoney\":\""+infoMap.get("tmoney")+"\"}}";
//			Long uin = (Long)infoMap.get("uin");
//			messCacheMap.put(uin, ret);
//			System.out.print("take ticket msg>>>type:"+type+",uid:"+uin+",carnumber:"+infoMap.get("carnumber"));
//		}
//		memcacheUtils.doMapLongStringCache("parkuser_messages", messCacheMap, "update");
//	}
	
	/**
	 * 写入系统消息表，在客户端读取
	 * @param type 0 支付失败提醒1 红包提醒2 自动支付提醒3 注册提醒 4停车入场提醒5活动提醒6 推荐消息7收款提醒 8充值消息
	 * @param uin  用户/收费员帐号
	 * @param content 内容
	 * @param title 标题
	 * @return 影响数据库记录数
	 */
	public int insertUserMesg(Integer type,Long uin,String content,String title){
		int ret =databasedao.update("insert into user_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
				new Object[]{type,System.currentTimeMillis()/1000,uin,title,content} );
		return ret;
	}
	
	
	/**
	 * 写入系统消息表，在客户端读取
	 * @param type 0 支付失败提醒1 红包提醒2 自动支付提醒3 注册提醒 4停车入场提醒5活动提醒6 推荐消息7收款提醒 8充值消息
	 * @param uin  用户/收费员帐号
	 * @param content 内容
	 * @param title 标题
	 * @return 影响数据库记录数
	 */
	public int insertParkUserMesg(Integer type,Long uin,String content,String title){
		int ret =databasedao.update("insert into parkuser_message_tb(type,ctime,uin,title,content) values(?,?,?,?,?)",
				new Object[]{type,System.currentTimeMillis()/1000,uin,title,content} );
		return ret;
	}
	
	/**
	 * 写用户支付账户信息
	 * @param type  0:支付宝，1:微信
	 * @param uin  车主账号
	 * @param account 车主支付账号
	 * @return
	 */
//	public int insertUserAccountMesg(Integer type,Long uin,String account){
//		int ret =databasedao.update("insert into user_payaccount_tb(type,ctime,uin,account) values(?,?,?,?)",
//				new Object[]{type,System.currentTimeMillis()/1000,uin,account} );
//		if(ret==1){//已写入
//			if(!publicMethods.isAuthUser(uin)){
//				List list = databasedao.getAll("select distinct uin from user_payaccount_tb where account=? ", new Object[]{account});
//				if(list!=null&&list.size()>2){//该账户已为三人以上充值，当前的车主置为黑名单
//					//写入黑名单表
//					Long ntime = System.currentTimeMillis()/1000;
//					try {
//						String atype = "支付宝";
//						if(type==1){
//							atype="微信";
//						}else if(type == 2){
//							atype = "微信公众号";
//						}
//						List<Long> whiteUsers = memcacheUtils.doListLongCache("zld_white_users", null, null);
//						if(whiteUsers==null||!whiteUsers.contains(uin)){
//							List<Long> blackUsers = memcacheUtils.doListLongCache("zld_black_users", null, null);
//							if(!blackUsers.contains(uin)){
//								ret = databasedao.update("insert into zld_black_tb(ctime,utime,uin,state,remark) values(?,?,?,?,?)",
//										new Object[]{ntime,ntime,uin,0,"充值账户("+atype+")为多个账户充值 :"+account});
//								System.out.println(">>>充值加入黑名单,uin:"+uin+",account:"+account+"，结果 ："+ret);
//							}
//							if(ret==1){
//								//放入黑名单缓存
//								//System.err.println(">>>zld black users :"+blackUsers);
//								if(blackUsers==null){
//									blackUsers = new ArrayList<Long>();
//									blackUsers.add(uin);
//									memcacheUtils.doListLongCache("zld_black_users", blackUsers, "update");
//								}else {
//									if(!blackUsers.contains(uin)){
//										blackUsers.add(uin);
//										memcacheUtils.doListLongCache("zld_black_users", blackUsers, "update");
//									}
//								}
//							}
//						}else{
//							System.out.println(">>zld_white_tb>>>>>uin:"+uin+",account:"+account+"，在白名单中，不处理:"+whiteUsers);
//						}
//					} catch (Exception e) {
//						System.out.println(">>>充值加入黑名单错误,uin:"+uin+",account:"+account+"，已经存在！");
//						e.printStackTrace();
//					}
//				}
//			}else{
//				logger.info("LogService>>>>insertUserAccountMesg>>>当前充值车主是认证用户，不去判断该账户是否为三人以上充值");
//			}
//		}
//		return ret;
//	}
	
	/**
	 * @param type 类型，1:拉拉(积一分)，2:NFC（积1） 3:差评（扣10分）,4:照牌结算(积1分),5推荐积分 
	 * @param uin 收费员帐号
	 * @param btime 开始时间，每天仅生成一条记录
	 */
	public void updateScroe(int type,Long uin,Long comId){
		
		/*Long endtime=1424016000L;//2月16日停止
		Long begintime = 1425225600L;//3月2日开始
		Long time = System.currentTimeMillis()/1000;
		if(time>endtime&&time<begintime){//停止积分 
			System.err.println(">>>停止积分！");
			return ;
		}*/
		if(uin==null)
			return;
		if(type<1||type>5)
			return ;
		//System.out.println(">>>>>>>>>>照拍积分 ："+type);
	//	String monday = StringUtils.getMondayOfThisWeek();
		Long btime = TimeTools.getToDayBeginTime();//getLongMilliSecondFrom_HHMMDD(monday)/1000;
//		if(btime==null){
//			btime = TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
//		}
		Long t1 = 1419177600L;
		Long nt = System.currentTimeMillis()/1000;
		//System.out.println(">>>>>>>积分:comid="+comId+",时间 :"+nt+",开始时间："+t1);
		if(comId!=null&&comId>0){//推荐时不判断是否支付电子支付 
			//if(nt>t1){
			//if(type<5){
				Integer epay = 0;
				Map comMap = databasedao.getPojo("select epay from com_info_tb where id=? ", new Object[]{comId});
				if(comMap!=null&&comMap.get("epay")!=null)
					epay= (Integer)comMap.get("epay");
				//System.out.println(">>>>>积分是否有效="+epay);
				if(epay==0){
					System.out.println(">>>>>不支持电子支付，不积分........");
					return ;
				}
			//}
			//}
		}else {
			return ;
		}
		
		Long count = databasedao.getLong("select count(*) from collector_scroe_tb where create_time=?" +
				" and uin=? ", new Object[]{btime,uin});
		String sql ="";// "insert into collector_scroe_tb (uin,lala_scroe,nfc_score,praise_scroe,create_time) values (?,?,?,?,?)";
		Object values[]= null;
		
		if(count>0){//更新积分
			sql = "update collector_scroe_tb "; 
			if(type==1){
				sql +=" set lala_scroe=lala_scroe+?";
				values=new Object[]{0.1,uin,btime};
			}else if(type==2){
				sql +=" set nfc_score=nfc_score+?";
				values=new Object[]{2,uin,btime};
			}else if(type==3){
				sql +=" set praise_scroe=praise_scroe+?";
				values=new Object[]{-10,uin,btime};
			}else if(type==4){
				sql +=" set pai_score=pai_score+?";
				values=new Object[]{2,uin,btime};
			}else if(type==5){
				sql +=" set recom_scroe=recom_scroe+?";
				values=new Object[]{1,uin,btime};
			}else if(type==6){
				sql +=" set nfc_score=nfc_score+?";
				values=new Object[]{0.01,uin,btime};
			}else if(type==7){
				sql +=" set pai_score=pai_score+?";
				values=new Object[]{0.01,uin,btime};
			}
			sql +=" where uin=? and create_time=? ";
		}else {//新建积分
			sql =  "insert into collector_scroe_tb (uin,lala_scroe,nfc_score,praise_scroe,create_time,pai_score,recom_scroe) values (?,?,?,?,?,?,?)";
			if(type==1){
				values=new Object[]{uin,0.1,0d,0,btime,0,0};
			}else if(type==2){
				values=new Object[]{uin,0,2d,0,btime,0,0};
			}else if(type==3){
				values=new Object[]{uin,0,0d,-10,btime,0,0};
			}else if(type==4){
				values=new Object[]{uin,0,0d,0,btime,2,0};
			}else if(type==5){
				values=new Object[]{uin,0,0d,0,btime,0,1};
			}else if(type==6){
				values=new Object[]{uin,0,0.01d,0,btime,0,0};
			}else if(type==7){
				values=new Object[]{uin,0,0d,0,btime,0.01,0};
			}
		}
		databasedao.update(sql, values);
	}
	
	//取消息
	private String getMessage(Long uin,Map messageMap){
		String result = "";
		if (messageMap != null && !messageMap.isEmpty()) {
			Integer mtype =(Integer) messageMap.get("message_type");
			if(mtype==null){
				return result;
			}
			Map<String, Object> infomMap = new HashMap<String, Object>();
			if(mtype==2){//充值，购买产品消息
				infomMap.put("result",  messageMap.get("state"));
				infomMap.put("errmsg",  messageMap.get("duartion"));
				infomMap.put("bonusid", messageMap.get("orderid"));
			}else if(mtype==0||mtype==9){
				Long comId = (Long)messageMap.get("comid");
				Long orderId = (Long)messageMap.get("orderid");
				Map<String, Object> comMap = databasedao.getPojo("select company_name from com_info_tb where id=?", new Object[]{comId});
				String cname ="";
				if(comMap!=null&&comMap.get("company_name")!=null)
					cname = (String)comMap.get("company_name");
				//String cname = (String)databasedao.getObject("select company_name from com_info_tb where id=?",new Object[]{comId}, String.class);
				infomMap.put("parkname",cname);
				infomMap.put("btime", messageMap.get("btime"));
				infomMap.put("etime", messageMap.get("etime"));
				infomMap.put("total", messageMap.get("order_total"));
				infomMap.put("state", messageMap.get("state"));//0:未支付 1：已支付 
				infomMap.put("orderid",orderId);
				//红包
				Long count = getBonusId(uin, orderId);
				if(count!=null&&count>0){
					infomMap.put("bonusid", count);
				}
			}
			String json =StringUtils.createJson(infomMap);
			result =  "{\"mtype\":\""+messageMap.get("message_type")+"\",\"msgid\":\""+messageMap.get("id")+"\",\"info\":"+json+"}";
		}
		return result;
	}
	
	
	private Long getBonusId(Long uin,Long orderId){
		Long count = null;
		//红包
//		Map bMap  =pOnlyReadService.getMap("select id from bouns_tb where uin=? and order_id=? and ctime > ? ",
//				new Object[]{uin,orderId,TimeTools.getToDayBeginTime()});
		Map bMap  = null;
		if(orderId==997||orderId==998||orderId==-1){
			bMap= databasedao.getMap("select id,btime from order_ticket_tb where uin=? and order_id=? and ctime > ? order by id desc limit ?",
					new Object[]{uin,orderId,TimeTools.getToDayBeginTime(),1});
			if(bMap!=null){
				Long btime = (Long)bMap.get("btime");
				if(btime!=null&&btime>10000){//已经分享过，不再分享
					bMap=null;
				}
			}
		}else {
			bMap =databasedao.getMap("select id from order_ticket_tb where uin=? and order_id=? and ctime > ? ",
					new Object[]{uin,orderId,TimeTools.getToDayBeginTime()});
		}
		if(bMap!=null&&bMap.get("id")!=null)
			count = (Long)bMap.get("id");
		
		return count;
	}
	public void uploadOrder2Line(final Long orderid){
	    ExecutorService executor = Executors.newSingleThreadExecutor();  
	    FutureTask<String> future =  
	           new FutureTask<String>(new Callable<String>() {//使用Callable接口作为构造参数  
	             public String call() {
	            	 System.out.println(orderid);
	            	 Map map = databasedao.getMap("select * from order_message_tb where id = ?",new Object[]{orderid});
	            	 String order = AjaxUtil.encodeUTF8(StringUtils.createJson(map));
	            	 HttpProxy httpProxy = new HttpProxy();
	            	 Map parammap = new HashMap();
	            	 parammap.put("order_message", order);
	            	 String ret = null;
	            	 try {
	            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadOrderMsg2Line&type=1", parammap);
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						System.out.println("tongbufanhui:"+ret);
					return ret;  
	           }});  
	    executor.execute(future);  
	    try {  
	        String result = future.get(3000, TimeUnit.MILLISECONDS); //取得结果，同时设置超时执行时间为5秒。同样可以用future.get()，不设置执行超时时间取得结果  
	        if(result!=null&&"1".equals(result)){
	        	
	        }else{
	        }
	    } catch (InterruptedException e) {  
	    	future.cancel(true);  
	    } catch (ExecutionException e) {  
	    	future.cancel(true);  
	    } catch (TimeoutException e) { 
	    	future.cancel(true);  
	    }catch (Exception e) { 
	    	future.cancel(true);  
	    } finally {  
	        executor.shutdown();  
	    }  
	}
}

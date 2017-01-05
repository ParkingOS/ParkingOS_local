package com.zld.struts.request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.CommonMethods;
import com.zld.impl.PublicMethods;
import com.zld.impl.PushtoSingle;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.utils.Check;
import com.zld.utils.HttpProxy;
import com.zld.utils.JsonUtil;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;

/**
 * 车辆照牌时生成及查询订单处理,
 * 设置，处理，查询 逃单
 * @author Administrator
 *
 */
public class ParkerOrderCreatorAction extends Action{
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PushtoSingle pushtoSingle;
	@Autowired
	private PublicMethods publicMethods;
	@Autowired
	private LogService logService;
	@Autowired
	private CommonMethods commonMethods;
	
	
	private Logger logger = Logger.getLogger(ParkerOrderCreatorAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		/**
		 * 2015-5-16 laoyao
		 * 进场时查是否是月卡用户，如果是多车牌用户，只要车场中已经有一个该用户的车进场时，这个用户下的其它车进场时，按临
		 * 停车辆处理
		 */
		String action = RequestUtil.getString(request, "action");
		Long comId = RequestUtil.getLong(request, "comid", -1L);
		String carNumber = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "carnumber"));
		carNumber = carNumber.trim().toUpperCase().trim();
		logger.info("action:"+action+",comid:"+comId);
		if(comId==-1){//车场编号错误
			AjaxUtil.ajaxOutput(response, "-1");
			return null;
		}
		if(action.equals("preaddorder")){
			Map limitmap = daService.getMap("select * from sync_time_tb where id = ?", new Object[]{4});//id为4的时候的maxid
			if(limitmap!=null&&limitmap.get("maxid")!=null){
				if(Long.parseLong(limitmap.get("maxid")+"")<System.currentTimeMillis()/1000){
					return null;
				}
			}
			String result = "0";
			Integer ctype = RequestUtil.getInteger(request, "through", 2);//2手机扫牌，3通道照牌
			Integer from = RequestUtil.getInteger(request, "from", -1);//通道扫牌参数：0:通道扫牌自动生成订单，1：补录车牌生成订单
			Integer car_type = RequestUtil.getInteger(request, "car_type", 0);//0：通用，1：小车，2：大车
			Long in_passid = RequestUtil.getLong(request, "passid", -1L);//入口通道id
			System.err.println("________++++入口通道："+in_passid);
			Integer add_carnumber = 0;
			if(from == 1){
				add_carnumber = 1;//补录车牌
			}
			Long neworderid = null;
			Long preorderid = null;//未结算的订单id
			int own = 0;//该车牌在自己车场的逃单数量
			int other=0;//该车牌在别的车场的逃单数量
			Integer ismonthuser = 0;//月卡用户
			boolean isupload = true;
			//查询这个车牌有没有未结算订单
			Long count = daService.getLong("select count(*) from order_tb where comid=? and  car_number =? and state=?", new Object[]{comId,carNumber,0});
			if(count>0 && ctype != 3){//ctype=3表示是通道扫牌生成订单
				result ="-2";
			}else {
				//查询这个车牌有没有逃单
				List<Map<String, Object>> escpedList = daService.getAll("select comid from no_payment_tb where state=? and car_number=? ",
						new Object[]{0,carNumber});
				if(escpedList!=null){
					for(Map<String, Object> map : escpedList){
						Long cid = (Long)map.get("comid");
						if(cid!=null&&cid.intValue()==comId.intValue())
							own++;
						else
							other++;
					}
				}
				if(own==0&&other==0){//生成订单
					String imei  =  RequestUtil.getString(request, "imei");
					Long uid = RequestUtil.getLong(request, "uid", -1L);
					if(!carNumber.equals("")){
						//查车主账户
//						Map carinfoMap = daService.getMap("select uin from  car_info_tb where car_number = ?", new Object[]{carNumber});
//						Long uin =-1L;
//						String cid = "";//客户端登录后，上传的个推消息编号
//						if(carinfoMap!=null&&carinfoMap.get("uin")!=null){
//							uin = (Long)carinfoMap.get("uin");
//							if(uin!=null&&uin>0){
//								Map userMap = daService.getMap("select cid from user_info_Tb where id=? ", new Object[]{uin});
//								if(userMap!=null&&userMap.get("cid")!=null){
//									cid = (String)userMap.get("cid");
//								}
//							}
//						}
						/**
						 * 20160324书瑜要求改成用车牌查月卡省份不参与   多个结果的话谁是月卡uin是谁  车牌更改成对应的车牌
						 */
						Map<Integer, Integer> map = new HashMap<Integer, Integer>();//月卡信息
						Integer monthcount = 0;//月卡数
						String subCar = carNumber.startsWith("无")?carNumber:"%"+carNumber.substring(1);
						List<Map> carinfoList = daService.getAll("select uin,car_number from  car_info_tb where car_number like ?", new Object[]{subCar});
						Long uin =-1L;
						String cid = "";//客户端登录后，上传的个推消息编号
						String monthcar_number = "";
						for (Iterator iterator = carinfoList.iterator(); iterator
								.hasNext();) {
							Map uinmap = (Map) iterator.next();
							uin = (Long)uinmap.get("uin");
							List rlist= publicMethods.isMonthUser(uin, comId);
							if(rlist!=null&&rlist.size()==2){
								uin = (Long)uinmap.get("uin");
								monthcar_number = uinmap.get("car_number")+"";
								monthcount = Integer.parseInt(rlist.get(1)+"");
								map = (Map<Integer, Integer>)rlist.get(0);
								logger.error("preaddorder>>>>判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin+",car_number:"+monthcar_number);
								if(monthcount>0&&map.size()==0){
									break;
								}
							}
						}
						if(monthcount==0&&map.size()<1){
							uin = -1L;
						}else{
							carNumber = monthcar_number;
						}
						if(uin!=null&&uin>0){
							Map userMap = daService.getMap("select cid from user_info_Tb where id=? ", new Object[]{uin});
							if(userMap!=null&&userMap.get("cid")!=null){
								cid = (String)userMap.get("cid");
							}
						}
						if(ctype == 3){//通道扫牌自动结算未结算订单，置为免费
							Map<String, Object> orderMap = daService.getMap("select * from order_tb where comid=? and  car_number =? and state=?", new Object[]{comId,carNumber,0});
							if(orderMap!=null&&orderMap.get("id")!=null){
								preorderid = (Long)orderMap.get("id");
								autoCompleteOrder(comId, carNumber, uin, uid);
							}
						}
						if(car_type==-1) {
							Map carNumbertType = daService.getMap("select typeid from car_number_type_tb where car_number = ? and comid=?", new Object[]{carNumber,comId});
							if (carNumbertType != null && carNumbertType.get("typeid") != null) {
								car_type = Integer.parseInt(carNumbertType.get("typeid") + "");
							}
							if(car_type==-1){//取默认车型
								List<Map<String, Object>> allCarTypes = commonMethods.getCarType(comId);
								if(!allCarTypes.isEmpty()){
									try {
										car_type = Integer.valueOf(allCarTypes.get(0).get("value_no")+"");
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
						}
						//查询是否已购买这个车场的月卡
						logger.info("preaddorder>>>uin:"+uin);
						Map comMap = daService.getMap("select entry_set,entry_month2_set from com_info_tb where id = ? ", new Object[]{comId});
						Integer entry_set = 0;
						Integer entry_month2_set = 0;
						if(comMap!=null&&comMap.get("entry_set")!=null&&comMap.get("entry_month2_set")!=null){
							entry_set = Integer.valueOf(comMap.get("entry_set")+"");
							entry_month2_set = Integer.valueOf(comMap.get("entry_month2_set")+"");
						}
						if(in_passid!=-1) {
							Map passMap = daService.getMap("select month_set, month2_set from com_pass_tb where id = ? ", new Object[]{in_passid});
							if (passMap != null && passMap.get("month_set") != null) {
								int month_set = Integer.parseInt(passMap.get("month_set") + "");
								int month2_set = Integer.parseInt(passMap.get("month2_set") + "");
								if(month_set!=-1){
									if(month_set==1){
										entry_set = 1;
									}else{
										entry_set = 0;
									}
								}
								if(month2_set!=-1){
									if(month2_set==1){
										entry_month2_set = 1;
									}else{
										entry_month2_set = 0;
									}
								}
							}
						}
						if(uin!=null&&uin!=-1){
//							Integer monthcount = 0;
//							List rlist= publicMethods.isMonthUser(uin, comId);
//							Map<Integer, Integer> map = new HashMap<Integer, Integer>();
//							if(rlist!=null&&rlist.size()==2){
//								monthcount = Integer.parseInt(rlist.get(1)+"");
//								map = (Map<Integer, Integer>)rlist.get(0);
//							}
							logger.error("preaddorder>>>>判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin);
							Long ordercount = 0L;
							if(monthcount > 0){
								Integer type = 5;
								if(map!=null&map.size()>0){
									int allday = 0;
									int subsection = 0;
									if(map.containsKey(0)){
										allday = map.get(0);
									}
									if(map.containsKey(1)){
										subsection += map.get(1);
									}
									if(map.containsKey(2)){
										subsection += map.get(2);
									}
									logger.error("preaddorder>>>>办理的全天包月套餐个数，allday:"+allday+"办理的分段包月套餐个数,subsection:"+subsection);
									if(allday>0){//如果有全天包月
										if(subsection>0){//全天包月加上分段月卡    办理了多个月卡
											Long ordercount1  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
													new Object[]{uin,comId,0,5});
											Long ordercount2  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
													new Object[]{uin,comId,0,8});
											ordercount = ordercount1+ordercount2;
											if(ordercount1<allday){
												type=5;
											}else{
												type=8;
											}
										}else{
											ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
													new Object[]{uin,comId,0,5});
											if(ordercount<allday){
												type=5;
											}
										}
									}else{
										ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
												new Object[]{uin,comId,0,8});
										type = 8;
									}
								}
//								Long ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
//										new Object[]{uin,comId,0,5});
								logger.info("preaddorder>>>>判断是否是第一个车牌入场，第一个记为月卡用户,uin:"+uin+",ordercount:"+ordercount);
								if(ordercount < monthcount){
									logger.error("preaddorder>>>>是月卡用户，uin:"+uin+",type:"+type);
									ctype = type;//第一个车牌入场，记为月卡用户
								}else{
									if(entry_month2_set==1){
										logger.info("preaddorder>>>月卡非第一辆车禁止入内  uin:"+uin);
										AjaxUtil.ajaxOutput(response, "{\"info\":\"-4\"}");//设置了月卡第二辆车禁止入内
										return null;
									}
									logger.info("preaddorder>>>uin:"+uin+",月卡第"+(ordercount+1)+"辆车入场");
									ctype=7;//月卡用户非第一辆车入场
								}
//									AjaxUtil.ajaxOutput(response, "{\"info\":\"-3\"}");//设置了非月卡禁止入内
//									return null;
							}else{
								if(entry_set==1){
									if(map!=null&map.size()>0) {
										if (map.containsKey(3)) {
											int outdate = map.get(3);
											if(outdate>0){
												logger.error("preaddorder>>>月卡车过期禁止入内  uin:"+uin);
												AjaxUtil.ajaxOutput(response, "{\"info\":\"-5\"}");//设置了非月卡禁止入内
												return null;
											}
										}
									}
									logger.info("preaddorder>>>非月卡车禁止入内  uin:"+uin);
									AjaxUtil.ajaxOutput(response, "{\"info\":\"-3\"}");//设置了非月卡禁止入内
									return null;
								}
							}
						}else{
							if(entry_set==1){
								AjaxUtil.ajaxOutput(response, "{\"info\":\"-3\"}");//设置了非月卡禁止入内
								return null;	
							}
						}
						//生成订单
						neworderid = daService.getkey("seq_order_tb");
						try {
							result = daService.update("insert into order_tb(id,create_time,uin,comid,c_type,uid,car_number,state,imei,car_type,in_passid) values" +
									"(?,?,?,?,?,?,?,?,?,?,?)", new Object[]{neworderid,System.currentTimeMillis()/1000,uin,comId,ctype,uid,carNumber,0,imei,car_type,in_passid})+"";
							logger.info("preaddorder>>>orderid:"+neworderid+",uin:"+uin+",进场通道："+in_passid);
							if(add_carnumber == 1){//补录车牌记录
								int r = daService.update("insert into order_attach_tb(add_carnumber,order_id) values(?,?)", new Object[]{1, neworderid});
							}
						}catch (Exception e){
							result = 1+"";
							isupload = false;
							e.getMessage();
						}
					}
				}
			}
			
			
			result = "{\"info\":\""+result+"\",\"own\":\""+own+"\",\"other\":\""+other+"\",\"orderid\":\""+neworderid+"\",\"ismonthuser\":\""+ismonthuser+"\",\"preorderid\":\""+preorderid+"\"}";
			AjaxUtil.ajaxOutput(response, result);
			final Long id = neworderid;
			if(isupload){
				new Thread(new Runnable() {
					public void run() {
						publicMethods.uploadOrder2Line(id,1,1);
					}
				}).start();
			}
//				int r = daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{0,neworderid});
			//http://192.168.199.240/zld/cobp.do?action=preaddorder&comid=10&uid=1000028&carnumber=aaabebdd	
		}else if(action.equals("getcartype")){
			//http://192.168.199.240/zld/cobp.do?action=getcartype&comid=1197
			List<Map<String, Object>> retList = commonMethods.getCarType(comId);
			String result = StringUtils.getJson(retList);
			result = result.replace("value_no", "id").replace("value_name", "name");
			AjaxUtil.ajaxOutput(response, result);
		}
		if(action.equals("addorder")){//生成订单
			Long uid = RequestUtil.getLong(request, "uid", -1L);
			Integer ctype = RequestUtil.getInteger(request, "through", 2);//2手机扫牌，3通道照牌
			String out = RequestUtil.processParams(request, "out");
			Integer from = RequestUtil.getInteger(request, "from", -1);//通道扫牌参数：0:通道扫牌自动生成订单，1：补录车牌生成订单
			Integer car_type = RequestUtil.getInteger(request, "car_type", 0);//0：通用，1：小车，2：大车
			Long in_passid = RequestUtil.getLong(request, "passid", -1L);//入口通道id
			Integer isfast = RequestUtil.getInteger(request, "isfast", 0);//是否是 1车牌极速通订单 2第三方卡号
			String cardno = RequestUtil.getString(request, "cardno");//第三方NFC卡号
			Integer add_carnumber = 0;
			if(from == 1){
				add_carnumber = 1;//补录车牌
			}
			if(car_type==-1){
				List<Map<String, Object>> allCarTypes = commonMethods.getCarType(comId);
				if(!allCarTypes.isEmpty()){
					try {
						car_type = Integer.valueOf(allCarTypes.get(0).get("value_no")+"");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			/*
			  create_time bigint,
			  comid bigint NOT NULL,
			  uin bigint NOT NULL,
			  total numeric(30,2),
			  state integer, -- 0未支付 1已支付 2逃单
			  end_time bigint,
			  auto_pay integer DEFAULT 0, -- 自动结算，0：否，1：是
			  pay_type integer DEFAULT 0, -- 0:帐户支付,1:现金支付,2:手机支付
			  nfc_uuid character varying(36),
			  c_type integer DEFAULT 1, -- 0:NFC,1:IBeacon 2:carnumber 照牌
			  uid bigint DEFAULT (-1), -- 收费员帐号
			  car_number character varying(50), -- 车牌号
			 */
			String imei  =  RequestUtil.getString(request, "imei");
			Long count = daService.getLong("select count(*) from order_tb where comid=? and  car_number =? and state=?", new Object[]{comId,carNumber,0});
			String result = "0";
			Long neworderid = null;
			Long preorderid = null;//未结算的订单id
			if(count==0 ||ctype == 3 ){//ctype=3通道扫牌入场，自动结算掉未结算的订单，并生成新订单
				if(!carNumber.equals("")){
					Map carinfoMap = daService.getMap("select uin from  car_info_tb where car_number = ?", new Object[]{carNumber});
					Long uin =-1L;
					if(carinfoMap!=null&&carinfoMap.get("uin")!=null)
						uin = (Long)carinfoMap.get("uin");
					if(count > 0 && ctype == 3){//通道扫牌自动结算未结算订单，置为免费
						Map<String, Object> map = daService.getMap("select * from order_tb where comid=? and  car_number =? and state=?", new Object[]{comId,carNumber,0});
						preorderid = (Long)map.get("id");
						autoCompleteOrder(comId, carNumber, uin, uid);
					}
					
					//查询是否已购买这个车场的月卡
					logger.info("addorder>>>>uin:"+uin);
					if(uin!=null&&uin!=-1){
//						Integer monthcount = publicMethods.isMonthUser(uin, comId);
//						logger.error("preaddorder>>>>判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin);
//						if(monthcount > 0){
//							Long ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
//									new Object[]{uin,comId,0,5});
//							logger.error("addorder>>>>判断是否是第一个车牌入场，第一个记为月卡用户,uin:"+uin+",ordercount:"+ordercount+",monthcount:"+monthcount);
//							if(ordercount < monthcount){
//								logger.error("addorder>>>>是月卡用户，uin:"+uin);
//								ctype = 5;//第一个车牌入场，记为月卡用户
						Integer monthcount = 0;
						List rlist= publicMethods.isMonthUser(uin, comId);
						Map<Integer, Integer> map = new HashMap<Integer, Integer>();
						if(rlist!=null&&rlist.size()==2){
							monthcount = Integer.parseInt(rlist.get(1)+"");
							map = (Map<Integer, Integer>)rlist.get(0);
						}
						logger.error("preaddorder>>>>判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin);
						Long ordercount = 0L;
						if(monthcount > 0){
							Integer type = 5;
							if(map!=null&map.size()>0){
								int allday = 0;
								int subsection = 0;
								if(map.containsKey(0)){
									allday = map.get(0);
								}
								if(map.containsKey(1)){
									subsection += map.get(1);
								}
								if(map.containsKey(2)){
									subsection += map.get(2);
								}
								logger.error("preaddorder>>>>办理的全天包月套餐个数，allday:"+allday+"办理的分段包月套餐个数,subsection:"+subsection);
								if(allday>0){//如果有全天包月
									if(subsection>0){//全天包月加上分段月卡    办理了多个月卡
										Long ordercount1  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
												new Object[]{uin,comId,0,5});
										Long ordercount2  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
												new Object[]{uin,comId,0,8});
										ordercount = ordercount1+ordercount2;
										if(ordercount1<allday){
											type=5;
										}else{
											type=8;
										}
									}else{
										ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
												new Object[]{uin,comId,0,5});
										if(ordercount<allday){
											type=5;
										}
									}
								}else{
									ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
											new Object[]{uin,comId,0,8});
									type = 8;
								}
							}
//							Long ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
//									new Object[]{uin,comId,0,5});
							logger.error("preaddorder>>>>判断是否是第一个车牌入场，第一个记为月卡用户,uin:"+uin+",ordercount:"+ordercount+",monthcount:"+monthcount);
							if(ordercount < monthcount){
								logger.error("preaddorder>>>>是月卡用户，uin:"+uin+",type:"+type);
								ctype = type;//第一个车牌入场，记为月卡用户
							}
						}
					}
					
					neworderid = daService.getkey("seq_order_tb");
					result = daService.update("insert into order_tb(id,create_time,uin,comid,c_type,uid,car_number,state,imei,car_type,in_passid,type) values" +
							"(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{neworderid,System.currentTimeMillis()/1000,uin,comId,ctype,uid,carNumber,0,imei,car_type,in_passid,isfast})+"";
					logger.info("addorder>>>orderid:"+neworderid+",uin:"+uin+"进场通道in_passid:"+in_passid);
					if(add_carnumber == 1){//补录车牌记录
						int r = daService.update("insert into order_attach_tb(add_carnumber,order_id) values(?,?)", new Object[]{1, neworderid});
					}
					if(uin!=null&&uin>0){
						String cid = "";//客户端登录后，上传的个推消息编号
						Map userMap = daService.getMap("select cid from user_info_Tb where id=? ", new Object[]{uin});
						if(userMap!=null&&userMap.get("cid")!=null){
							cid = (String)userMap.get("cid");
							if(result.equals("1")&&!cid.equals(""))
								doMessage(request, uin, cid);
						}
					}
					if(uin!=null&&uin!=-1){
						String cname = (String) daService.getObject("select company_name from com_info_tb where id=?",
								new Object[] { comId},String.class);
						//logService.insertParkUserMesg(1, uid, "", "");
						String throughType = "扫牌入场";
						if(ctype==3)
							throughType = "通道"+throughType;
						logService.insertUserMesg(4, uin, "您已进入"+cname+"，入场方式："+throughType+"。", "入场提醒");
					}
				}else if(isfast==2){//第三方NFC卡号
					if(!cardno.equals("")){
						String uuid = comId+"_"+cardno;
						/*Long ncount  = daService.getLong("select count(*) from com_nfc_tb where nfc_uuid=? and state=?", 
								new Object[]{uuid,0});
						if(ncount==0){
							logger.info("极速通刷卡...卡号："+uuid+",未注册....");
							result = "{\"info\":\""+cardno+",未注册！"+"\",\"orderid\":\"-1\",\"preorderid\":\"-1\"}";
							AjaxUtil.ajaxOutput(response, result);
							return null;
						}*/
						//查询是否有订单
						logger.info("极速通刷卡...卡号："+uuid);
						Map orderMap = daService.getMap("select * from order_tb where comid=? and nfc_uuid=? and state=?", 
								new Object[]{comId,uuid,0});
						//有订单，全部结算了
						if(orderMap!=null&&orderMap.get("comid")!=null){//有订单，结算
							int ret = daService.update("update order_tb set end_time=?,total=?,state=?,pay_type=?,uid=? where comid=? and nfc_uuid =? and state=? ",
									new Object[]{System.currentTimeMillis()/1000, 0d, 1, 1, uid, comId, uuid, 0 });
							logger.info("极速通刷卡...卡号："+uuid+", 进场存在订单，自动结算，数量："+ret);
						}
						neworderid = daService.getkey("seq_order_tb");
						result = daService.update("insert into order_tb(id,create_time,uin,comid,c_type,uid,nfc_uuid,state,imei,car_type,in_passid,type) values" +
								"(?,?,?,?,?,?,?,?,?,?,?,?)", new Object[]{neworderid,System.currentTimeMillis()/1000,-1L,comId,ctype,uid,uuid,0,imei,car_type,in_passid,isfast})+"";
						logger.info("极速通刷卡 addorder>>>orderid:"+neworderid+",uid:"+uid+",ret:"+result);
						
					}
				}
			}else {
				result =carNumber+",存在未结算的订单，请先结算!";
			}
			if(out.equals("json")){
				result = "{\"info\":\""+result+"\",\"orderid\":\""+neworderid+"\",\"preorderid\":\""+preorderid+"\"}";
			}
			AjaxUtil.ajaxOutput(response, result);
			//http://192.168.199.240/zld/cobp.do?action=addorder&comid=10&uid=1000028&carnumber=aaabebdd&out	
		}else if(action.equals("catorder")){
			String ptype = RequestUtil.getString(request, "ptype");//V1115版本以上加这个参数，以实现包月产品及多价格策略的支持
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>ptype:"+ptype);
			Long id = RequestUtil.getLong(request, "orderid", -1L);
			//查询是否有订单
			Map orderMap = daService.getPojo("select * from order_tb where id=? ", new Object[]{id});
			Integer state = (Integer)orderMap.get("state");
			Double pretotal = StringUtils.formatDouble(orderMap.get("total"));// 预支付金额
			if(pretotal>0){
				logger.error("订单已有结算金额"+orderMap);
				daService.update("udate order_tb set state=?,pay_type=?,total=? where id =? ", new Object[]{0,0,0.0,id});
				orderMap.put("state", 0);
				orderMap.put("total", 0.0);
			}
			if(orderMap!=null&&orderMap.get("comid")!=null){//有订单，结算
				String result="-2";
				try {
					//V1115版本以上加这个参数，以实现包月产品及多价格策略的支持
					if(ptype.equals("1"))
						result = publicMethods.getOrderPrice(comId, orderMap);
					else {//老客户端价格
						result = publicMethods.handleOrder(comId, orderMap);
					}
					JSONObject jsonObject = JSONObject.fromObject(result);
					Map<String, Object> rMap = new HashMap<String, Object>();
					Iterator it = jsonObject.keys();
					while(it.hasNext()){
						String key = (String) it.next();  
		                String value = jsonObject.getString(key);
		                rMap.put(key, value);
					}
					Long etime = System.currentTimeMillis()/1000;
					if(state == 1){
						etime = (Long)orderMap.get("end_time");
					}
					Map map = commonMethods.getOrderInfo(id, -1L, etime);
					logger.info("orderid:"+id+",result:"+result+",map:"+map);
					if(map != null){
						Double beforetotal = Double.valueOf(map.get("beforetotal") + "");
						Double aftertotal = Double.valueOf(map.get("aftertotal") + "");
						Long shopticketid = (Long)map.get("shopticketid");
						
						rMap.put("collect", aftertotal);
						rMap.put("befcollect", beforetotal);
						rMap.put("distotal", beforetotal>aftertotal? StringUtils.formatDouble(beforetotal- aftertotal) :0d);
						rMap.put("shopticketid", shopticketid);
						rMap.put("tickettype", map.get("tickettype"));
						rMap.put("tickettime", map.get("tickettime"));
					}
					result = StringUtils.createJson(rMap);
				} catch (Exception e) {
					logger.info(">>>>>>>>>>结算订单错误，订单编号："+orderMap.get("id")+",comid:"+comId);
					e.printStackTrace();
				}
				result = result.substring(0,result.length()-1)+",\"state\":\""+(orderMap.get("state")==null?"0":orderMap.get("state"))+"\"}";
				logger.info(">>>>>>>>>>结算订单，订单编号："+orderMap.get("id")+",comid:"+comId+",result:"+result);
				AjaxUtil.ajaxOutput(response,result);
			}
			//http://192.168.199.240/zld/cobp.do?action=catorder&orderid=786702&comid=3&ptype=1
		}else if(action.equals("escape")){//处理置为逃单124610
			//http://192.168.199.240/zld/cobp.do?action=escape&orderid=124614&comid=10&total=100.98
			Long id = RequestUtil.getLong(request, "orderid", -1L);
			Double total = RequestUtil.getDouble(request, "total", 0.0D);
			/*
			 *   id bigint,
				  create_time bigint,
				  end_time bigint,
				  order_id bigint,
				  car_number character varying(20), -- 车牌号
				  state integer DEFAULT 0, -- 状态 0未处理，1已处理
				  uin bigint DEFAULT (-1), -- 车主帐号
				  comid bigint -- 车场编号
			 */
			Map orderMap = daService.getMap("select * from order_tb where id=?", new Object[]{id});
			int result =0;
			if(orderMap!=null){
				Long time = System.currentTimeMillis()/1000;
				try {
					result = daService.update("insert into no_payment_tb (create_time,end_time,order_id,total,car_number,uin,comid)" +
							"values(?,?,?,?,?,?,?)", 	new Object[]{orderMap.get("create_time"),time,orderMap.get("id"),total
							,orderMap.get("car_number"),orderMap.get("uin"),orderMap.get("comid")});
				} catch (Exception e) {
					result =-1;
					e.printStackTrace();
				}
				logger.info("逃单写表："+result);
				if(result!=-1)//state=2
					result = daService.update("update order_tb set state=?,end_time=?,total=? where id =?",
							new Object[]{2,System.currentTimeMillis()/1000,total,id});
			}
			AjaxUtil.ajaxOutput(response, result+"");
			
		}else if(action.equals("handleescorder")){//结算逃单
			Long order_id = RequestUtil.getLong(request, "orderid", -1L);
			//Long id = RequestUtil.getLong(request, "id", -1L);
			Double total = RequestUtil.getDouble(request, "total", 0.0D);
			int result =0;
			result =daService.update("delete from no_payment_tb where order_id =? ", new Object[]{order_id});
			result = daService.update("update order_tb set state=?,total=? where id =?",
					new Object[]{1,total,order_id});
			AjaxUtil.ajaxOutput(response, result+"");
			//http://192.168.199.240/zld/cobp.do?action=handleescorder&orderid=99&comid=&total=
		}else if(action.equals("getcurrorder")){//查询离场订单
//			List<Map<String, Object>> orderList = daService.getAll("select * from order_tb where comid=? and " +
//					"c_type=? and car_number like ?", new Object[]{comId,2,"%"+carNumber+"%"});
			//http://192.168.199.240/zld/cobp.do?action=getcurrorder&comid=1197
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Integer ctype = RequestUtil.getInteger(request, "through", 2);
			//System.out.println(">>>>>"+request.getParameterMap());
			logger.info("through"+ctype);
			List<Object> params = new ArrayList<Object>();
			params.add(comId);
			params.add(0);
			params.add(4);
			params.add(0);
			
			Long _total = daService.getCount("select count(*) from order_tb where comid=? and " +
					"c_type not in(?,?) and state=? ",params);
			List<Map> list = daService.getAll("select * from order_tb where comid=? and " +
					"c_type not in(?,?)  and state= ? order by create_time desc ",params, pageNum, pageSize);
			logger.info("currentorder total:"+_total);
			
			setPicParams(list);
			List<Map<String, Object>> infoMaps = new ArrayList<Map<String,Object>>();
			Integer ismonthuser = 0;//判断是否月卡用户
			if(list!=null&&list.size()>0){
				for(Map map : list){
					Map<String, Object> info = new HashMap<String, Object>();
					Long uid = (Long)map.get("uin");
					Integer c_type = (Integer)map.get("c_type");//进场方式 ，0:NFC，2:照牌    3:通道照牌进场 4直付 5月卡用户
					//String carNumber = "车牌号未知";
					if(map.get("car_number")!=null&&!"".equals((String)map.get("car_number"))){
						carNumber = (String)map.get("car_number");
					}else {
						if(uid!=-1){
							carNumber=(String)daService.getObject("select car_number from car_info_tb where uin=? and state=?",new Object[]{uid,1}, String.class);
						}
					}
					Integer isfast = (Integer)map.get("type");
					if(isfast==2){
						String cardno = (String) map.get("nfc_uuid");
						if(cardno!=null&&cardno.indexOf("_")!=-1)
							info.put("carnumber", cardno.substring(cardno.indexOf("_")+1));
					}else {
						info.put("carnumber", map.get("car_number")==null?"车牌号未知": map.get("car_number"));
					}
					info.put("isfast", isfast);
					Long start= (Long)map.get("create_time");
					info.put("id", map.get("id"));
					info.put("uin", map.get("uin"));
					info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
					if(c_type==1){//ibeacon入场，给出价格
						Long comid = (Long)map.get("comid");
						Integer car_type = (Integer)map.get("car_type");//0：通用，1：小车，2：大车
						String price = publicMethods.getPrice(start,System.currentTimeMillis()/1000, comid, car_type);
						info.put("total", StringUtils.formatDouble(price));
						info.put("duration", StringUtils.getTimeString(start, System.currentTimeMillis()/1000));
					}
					//车牌照片参数设置（HD版需要）
					info.put("lefttop", map.get("lefttop"));
					info.put("rightbottom", map.get("rightbottom"));
					info.put("width", map.get("width"));
					info.put("height", map.get("height"));
					info.put("lineid", map.get("line_id"));//
					info.put("ctype", c_type);
					//判断是否是月卡用户
					if(c_type==5){
						ismonthuser = 1;//是月卡用户
					}else 
						ismonthuser=0;
					info.put("ismonthuser", ismonthuser);
					infoMaps.add(info);
				}
			}
			String result = "";
			result = "{\"count\":"+_total+",\"info\":"+StringUtils.createJson(infoMaps)+"}";
			logger.info(result);
			AjaxUtil.ajaxOutput(response,result);;
		}else if(action.equals("queryorder")){//查询订单,照牌的订单在出场时，根据输入的数字查询订单
			logger.error("进入车牌查询"+carNumber);
//			List<Map<String, Object>> orderList = daService.getAll("select * from order_tb where comid=? and " +
//			"c_type=? and car_number like ?", new Object[]{comId,2,"%"+carNumber+"%"});
			//http://192.168.199.240/zld/cobp.do?action=queryorder&comid=3&uid=100005&carnumber=aaabb
			Long btime = System.currentTimeMillis();
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Integer ctype = RequestUtil.getInteger(request, "through", 2);
			//System.out.println(">>>>>"+request.getParameterMap());
			Integer search = RequestUtil.getInteger(request, "search", 0);//0:模糊查询,1:完全匹配  2:hd出口拍照完全匹配+模糊匹配
			Long time = System.currentTimeMillis()/1000- 30 * 86400;
			/*
			 * 两秒内同一个车牌只能查一次
			 */
			/*Long _ntime = btime/1000;
			logger.error("原查询车牌缓存："+CustomDefind.CARLIENCE);
			if(CustomDefind.getCarMap(carNumber)!=null){
				Long pretime = CustomDefind.getCarMap(carNumber);
				if(pretime!=null&&_ntime-pretime<3){
					CustomDefind.setCarMap(carNumber, _ntime);
					String _result = "{\"count\":0,\"price\":0,\"isauto\":0,\"info\":[{\"ismonthuser\":\"0\",\"exptime\":\"-1\"}]}";
					logger.error("车牌查询过快，返回空.....");
					AjaxUtil.ajaxOutput(response, _result);
					return null;
				}
			}
			CustomDefind.setCarMap(carNumber, _ntime);
			CustomDefind.clearCarMap();
			logger.error("现查询车牌缓存："+CustomDefind.CARLIENCE);*/
			/*
			 * 两秒内同一个车牌只能查一次，完毕 
			 */
			logger.error(ctype);
			int auto = 1;//1代表自动结算
			List<Object> params = new ArrayList<Object>();
			params.add(time);
			params.add(comId);
//			params.add(0);
//			params.add(4);
			if(carNumber.length()>5){
				carNumber = carNumber.substring(1);
			}
			if(search == 1){
				params.add("%"+carNumber);
//				params.add(carNumber);
			}else{
				params.add("%"+carNumber+"%");
			}
			params.add(0);
			Long uin =-1L;
			Integer monthcount = 0;
			logger.error("carNumber:"+carNumber+",search:"+search);
			List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
			List<Map> listcount = new ArrayList<Map>();
//			Long _total = daService.getCount("select count(*) from order_tb where comid=? and car_number like ? and state=? "
//					//"and c_type not in(?,?)   "
//					, params);
			list  = daService.getAll("select * from order_tb where create_time>? and comid=? and car_number like ? and state=? order by create_time desc" 
					//"c_type not in(?,?) "
					,params, pageNum, pageSize);
			Long _total = list.size()+0L;
			logger.error("query 1 time:"+(System.currentTimeMillis()-btime));
			
			if(search==2&&list.size()==0&&carNumber.length()>5){//辽AB1234全部匹配没有
				//20160323
				List<Map> carinfoList = daService.getAll("select uin from  car_info_tb where car_number like ?", new Object[]{"%"+carNumber});
				String cid = "";//客户端登录后，上传的个推消息编号
				Integer allday = 0;
				if(carinfoList!=null&&carinfoList.size()>0){
					for (Map map : carinfoList) {
						uin = Long.parseLong(map.get("uin")+"");
						List rlist = publicMethods.isMonthUser(uin, comId);
						Map<Integer, Integer> rmap = new HashMap<Integer, Integer>();
						if(rlist!=null&&rlist.size()==2){
							monthcount  = Integer.parseInt(rlist.get(1)+"");
							rmap = (Map<Integer, Integer>)rlist.get(0);
							logger.error(">>>>queryorder:判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin);
							if(rmap.containsKey(0)){
								allday = rmap.get(0);
							}
							break;
						}
					}
				}
				logger.error("come in query month,allday:"+allday);
				if(allday==0){
					auto = 0;
					uin = -1L;
					logger.error("in like query 进入模糊查询");
					//AB1234匹配
					params = params.subList(0, 2);
					System.out.println("1%"+carNumber.substring(1,carNumber.length())+"%");
					params.add(2,"%"+carNumber.substring(1,carNumber.length())+"%");
					params.add(0);
//					listcount = daService.getAllMap("select * from order_tb where comid=? and " +
//							"c_type not in(?,?) and car_number like ? and state= ? ",params
//							);
//					_total+=listcount.size();
					list = daService.getAll("select * from order_tb where create_time>? and comid=? and " +
							" car_number like ? and state= ? order by create_time desc",params, pageNum, pageSize);
					_total+=list.size();
					//如果有订单查询是否是这个车场的月卡  是的话改为月卡进场
					
					if(list.size()==0){//AB1234匹配没有
						auto = 0;
						//AB123||B1234匹配
						params = params.subList(0, 2);
						params.add(2,"%"+carNumber.substring(1,carNumber.length()-1)+"%");
						params.add(3,"%"+carNumber.substring(2)+"%");
						params.add(0);
//						listcount = daService.getAllMap("select * from order_tb where comid=? and " +
//								"c_type not in(?,?) and (car_number like ? or car_number like ?) and state= ? ",params);
//						_total+=listcount.size();
						list = daService.getAll("select * from order_tb where create_time>? and comid=? and " +
								"(car_number like ? or car_number like ?) and state= ? order by create_time desc",params, pageNum, pageSize);
						_total+=list.size();
						if(list.size()==0){//AB123||B1234没用匹配
							//"AB12"、"B123"、"1234"匹配
							params = params.subList(0, 2);
							params.add(2,"%"+carNumber.substring(0,carNumber.length()-3)+"%");
							params.add(3,"%"+carNumber.substring(1,carNumber.length()-2)+"%");
							params.add(4,"%"+carNumber.substring(2,carNumber.length()-1)+"%");
							params.add(5,"%"+carNumber.substring(3)+"%");
							params.add(0);
//							listcount = daService.getAllMap("select * from order_tb where comid=? and " +
//									"c_type not in(?,?) and (car_number like ? or car_number like ? or car_number like ? ) and state= ? ",params);
//							
//							_total+=listcount.size();
							list = daService.getAll("select * from order_tb where create_time>? and comid=? and " +
									"(car_number like ? or car_number like ? or car_number like ? or car_number like ? ) and state= ? order by create_time desc",params, pageNum, pageSize);
							_total+=list.size();
//							if(list.size()==0){
//								//“AB1”、“B12”、“123”、“234”
//								params = params.subList(0, 1);
//								params.add(1,"%"+carNumber.substring(1,carNumber.length()-3)+"%");
//								params.add(2,"%"+carNumber.substring(2,carNumber.length()-2)+"%");
//								params.add(3,"%"+carNumber.substring(3,carNumber.length()-1)+"%");
//								params.add(4,"%"+carNumber.substring(4)+"%");
//								params.add(0);
////								listcount = daService.getAllMap("select * from order_tb where comid=? and " +
////										"c_type not in(?,?) and (car_number like ? or car_number like ? or car_number like ? or car_number like ?) and state= ? ",params);
////								_total+=listcount.size();
//								list = onlyReadService.getAll("select * from order_tb where comid=? and " +
//										"(car_number like ? or car_number like ? or car_number like ? or car_number like ?) and state= ? order by create_time desc",params, pageNum, pageSize);
//								_total+=list.size();
//							}
						}
					}
					logger.error("query 2 time:"+(System.currentTimeMillis()-btime));
				}
			}
			//按第三方卡号在NFC字段查询，
//			params.add(1,2);//过滤是第三方卡号生成的订单
//			params.remove(params.size()-1);
//			params.remove(params.size()-1);
			params.clear();
			params.add(comId);
			params.add(2);
			params.add(0);
			params.add(4);
			if(search == 1){
				params.add(carNumber);
			}else{
				params.add("%"+carNumber+"%");
			}
			params.add(0);
//			Long _total2 = daService.getLong("select count(*) from order_tb where comid=? and type=? and  " +
//					"c_type not in(?,?)  and nfc_uuid like ? and state=? ", new Object[]{comId,2,0,4,"%"+carNumber+"%",0});
//			List<Map<String,Object>> list2 = daService.getAll("select * from order_tb where comid=? and type=? and  " +
//					"c_type not in(?,?) and nfc_uuid like ? and state= ? ",params, pageNum, pageSize);
//			logger.error("query 2 time:"+(System.currentTimeMillis()-btime));
//			_total+=_total2;
//			if(!list2.isEmpty())
//				list.addAll(list2);
//			if(list.size()>1){
//				auto=0;
//			}
			setPicParams(list);
			logger.error("currentorder:"+_total);
			List<Map<String, Object>> infoMaps = new ArrayList<Map<String,Object>>();
			Double ptotal = 0d;
			Integer ismonthuser = 0;//判断是否月卡用户
			if(list!=null&&list.size()>0){
				for(Map map : list){
					Map<String, Object> info = new HashMap<String, Object>();
					Long uid = (Long)map.get("uin");
					
					Integer isfast = (Integer)map.get("type");
					if(isfast==2){
						String cardno = (String) map.get("nfc_uuid");
						if(cardno!=null&&cardno.indexOf("_")!=-1)
							info.put("carnumber", cardno.substring(cardno.indexOf("_")+1));
					}else {
						info.put("carnumber", map.get("car_number")==null?"车牌号未知": map.get("car_number"));
					}
					info.put("isfast", isfast);
					Long start= (Long)map.get("create_time");
					Long end= System.currentTimeMillis()/1000;
					Integer car_type = (Integer)map.get("car_type");
					info.put("id", map.get("id"));
					
					info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
					info.put("uin", map.get("uin"));
					//车牌照片参数设置（HD版需要）
					info.put("lefttop", map.get("lefttop"));
					info.put("rightbottom", map.get("rightbottom"));
					info.put("width", map.get("width"));
					info.put("height", map.get("height"));
					//判断是否是月卡用户
					Integer c_type = (Integer)map.get("c_type");//进场方式 ，0:NFC，2:照牌    3:通道照牌进场 4直付 5月卡用户
					if(c_type!=null&&c_type==5){
						ismonthuser = 1;//是月卡用户
						String exptime = getlimitday((Long)map.get("uin"), comId);
						if(exptime != null){
							info.put("exptime", exptime);
						}
						if(c_type==1){
							String total = publicMethods.getPrice(start, end, comId,car_type);
							if(Check.isDouble(total))
								ptotal +=StringUtils.formatDouble(total);
							info.put("total",total);
							info.put("duration", "已停 "+StringUtils.getTimeString(start,end));
						}
					}else {
						ismonthuser=0;//非月卡用户
					}
					info.put("ismonthuser", ismonthuser);
					info.put("car_type", map.get("car_type"));
					info.put("ctype", map.get("c_type"));
					infoMaps.add(info);
				}
			}else if(ctype == 3){//通道扫牌
				logger.error("query 3 time:"+(System.currentTimeMillis()-btime));
//				Map carMap =null ;//daService.getMap("select uin from car_info_tb where car_number=? ", new Object[]{carNumber});
				Map<String, Object> info = new HashMap<String, Object>();
				Integer e_time = -1;
////			Integer monthcount = publicMethods.isMonthUser((Long)carMap.get("uin"), comId);
	//			List rlist = publicMethods.isMonthUser((Long)carMap.get("uin"), comId);
	//			Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	//			if(rlist!=null&&rlist.size()==2){
	//				monthcount = Integer.parseInt(rlist.get(1)+"");
	//				map = (Map<Integer, Integer>)rlist.get(0);
	//			}
				if(uin!=-1L){
					Long ntime = System.currentTimeMillis()/1000;
					Map<String, Object> pMap = daService.getMap("select p.b_time,p.e_time,p.type from product_package_tb p," +
							"carower_product c where c.pid=p.id and p.comid=? and c.uin=? and c.e_time>? order by c.id desc limit ?", 
							new Object[]{comId,uin,ntime,1});
					if(pMap!=null&&!pMap.isEmpty())
						e_time =  (Integer)pMap.get("e_time");
						
					if(monthcount > 0){
						ismonthuser = 1;//是月卡用户
					}
				}
				info.put("exptime", e_time);	
				info.put("ismonthuser", ismonthuser);
				infoMaps.add(info);
				logger.error("query 2 time:"+(System.currentTimeMillis()-btime));
			}
			String result = "";
			result = "{\"count\":"+_total+",\"price\":"+ptotal+",\"isauto\":"+auto+",\"info\":"+StringUtils.createJson(infoMaps)+"}";
			Long durtime = System.currentTimeMillis()-btime;
			logger.error("queryorder:"+result+",queryorder time:"+durtime);
			AjaxUtil.ajaxOutput(response,result);;
		}else if(action.equals("queryescorder")){
			String type = RequestUtil.getString(request, "type");
			String sql = "select p.id,p.order_id,p.create_time,p.end_time ," +
					"p.comid,p.total,p.car_number,c.company_name parkname from no_payment_tb p,com_info_tb c   " +
					"where p.comid=c.id and p.comid=? ";
			List<Object> params = new ArrayList<Object>();
			params.add(comId);
			if(type.equals("month")){
				sql +=" and p.create_time > ?";
				params.add(TimeTools.getMonthStartSeconds());
			}else if(type.equals("week")){
				sql +=" and p.create_time >? ";
				params.add(TimeTools.getWeekStartSeconds());
			}
			List<Map<String, Object>> oList = daService.getAllMap(sql, params);
			System.out.println(oList);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(oList));
			//http://192.168.199.240/zld/cobp.do?action=queryescorder&comid=10&type=
		}else if(action.equals("viewescdetail")){
			if(carNumber.equals("")){
				AjaxUtil.ajaxOutput(response, "[]");
				return null;
			}
			String sql = "select p.id,p.order_id,p.create_time,p.end_time ," +
					"p.comid,p.total,p.car_number,c.company_name parkname from no_payment_tb p,com_info_tb c   " +
					"where p.comid=c.id and p.car_number=? ";
			List<Object> params = new ArrayList<Object>();
			params.add(carNumber);
			List<Map<String, Object>> oList = daService.getAllMap(sql, params);
			System.out.println(oList);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(oList));
			//http://192.168.199.240/zld/cobp.do?action=viewescdetail&comid=10&carnumber=
		}else if(action.equals("addcarnumber")){
			Integer ctype = RequestUtil.getInteger(request, "through", -1);
			Long order_id = RequestUtil.getLong(request, "orderid", -1L);
			//http://192.168.199.240/zld/cobp.do?action=addcarnumber&comid=10&orderid=&carnumber=
			Map carinfoMap = daService.getMap("select uin from  car_info_tb where car_number = ?", new Object[]{carNumber});
			Long count = daService.getLong("select count(id) from order_tb where comid=? and car_number=? and state=? ", 
					new Object[]{comId,carNumber,0});
			if(count>0){//相同的车牌已在本车场存在订单
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}
			Long uin =-1L;
			int result  = -1;
			if(carinfoMap!=null&&carinfoMap.get("uin")!=null)
				uin = (Long)carinfoMap.get("uin");
			String sql = "update order_tb set uin=?,car_number=? ";
			List<Object> params = new ArrayList<Object>();
			params.add(uin);
			params.add(carNumber);
			int car_type = -1;
			Map carNumbertType = daService.getMap("select typeid from car_number_type_tb where car_number = ?", new Object[]{carNumber});
			if(carNumbertType!=null&&carNumbertType.get("typeid")!=null){
				car_type = Integer.parseInt(carNumbertType.get("typeid")+"");
			}
			if(car_type!=-1){
				sql += ",car_type=? ";
				params.add(car_type);
			}
			if(ctype == 3){//通道扫牌更正车牌号，auto_pay=2
				Map<String, Object> map = new HashMap<String, Object>();
				map = daService.getMap("select car_number from order_tb where id=? ", new Object[]{order_id});
				Long c = daService.getLong("select count(*) from order_attach_tb where order_id=? ", new Object[]{order_id});
				if(c > 0){
					daService.update("update order_attach_tb set change_carnumber=?,old_carnumber=? where order_id=? ", new Object[]{1, map.get("car_number"), order_id});
				}else{
					daService.update("insert into order_attach_tb(change_carnumber,old_carnumber,order_id) values(?,?,?)", new Object[]{1, map.get("car_number"), order_id});
				}
//				Integer monthcount = publicMethods.isMonthUser(uin, comId);
//				logger.error("addcarnumber>>>>判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin);
//				Long ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
//						new Object[]{uin,comId,0,5});
//				if(b&&ordercount==0){
	//				sql += " ,c_type=? ";
	//				params.add(5);
	//			}else{
	//				sql += " ,c_type=? ";
	//				params.add(3);
	//			}
				Integer monthcount = 0;
				List rlist= publicMethods.isMonthUser(uin, comId);
				Map<Integer, Integer> mapCount = new HashMap<Integer, Integer>();
				if(rlist!=null&&rlist.size()==2){
					monthcount = Integer.parseInt(rlist.get(1)+"");
					mapCount = (Map<Integer, Integer>)rlist.get(0);
				}
				logger.error("preaddorder>>>>判断是否是月卡用户：monthcount："+monthcount+",uin:"+uin);
				Long ordercount = 0L;
				Integer type = 5;
				if(monthcount > 0){
					if(mapCount!=null&mapCount.size()>0){
						int allday = 0;
						int subsection = 0;
						if(mapCount.containsKey(0)){
							allday = mapCount.get(0);
						}
						if(mapCount.containsKey(1)){
							subsection += mapCount.get(1);
						}
						if(mapCount.containsKey(2)){
							subsection += mapCount.get(2);
						}
						logger.error("preaddorder>>>>办理的全天包月套餐个数，allday:"+allday+"办理的分段包月套餐个数,subsection:"+subsection);
						if(allday>0){//如果有全天包月
							if(subsection>0){//全天包月加上分段月卡    办理了多个月卡
								Long ordercount1  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
										new Object[]{uin,comId,0,5});
								Long ordercount2  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
										new Object[]{uin,comId,0,8});
								ordercount = ordercount1+ordercount2;
								if(ordercount1<allday){
									type=5;
								}else{
									type=8;
								}
							}else{
								ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
										new Object[]{uin,comId,0,5});
								if(ordercount<allday){
									type=5;
								}
							}
						}else{
							ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
									new Object[]{uin,comId,0,8});
							type = 8;
						}
					}
//					Long ordercount  = daService.getLong("select count(id) from order_tb where uin=? and comid =? and state=? and c_type=?",
//							new Object[]{uin,comId,0,5});
					logger.error("preaddorder>>>>判断是否是第一个车牌入场，第一个记为月卡用户,uin:"+uin+",ordercount:"+ordercount+",monthcount:"+monthcount);
//					if(ordercount < monthcount){
//						logger.error("preaddorder>>>>是月卡用户，uin:"+uin+",type:"+type);
//						ctype = type;//第一个车牌入场，记为月卡用户
				}
				Map comMap = daService.getMap("select * from com_info_tb where id = ? ", new Object[]{comId});
				if(monthcount > 0){
					if(ordercount < monthcount){
						sql += " ,c_type=? ";
//						params.add(5);
						params.add(type);
					}else{
						if(comMap!=null&&comMap.get("entry_month2_set")!=null){
							Integer entry_month2_set = Integer.valueOf(comMap.get("entry_month2_set")+"");
							if(entry_month2_set==1){
								AjaxUtil.ajaxOutput(response, "{\"info\":\"-4\"}");//设置了月卡第二辆车禁止入内
								return null;
							}
						}
						sql += " ,c_type=? ";
						params.add(7);
					}
				}else{
					if(comMap!=null&&comMap.get("entry_set")!=null){
						Integer entry_set = Integer.valueOf(comMap.get("entry_set")+"");
						if(entry_set==1){
							if(mapCount!=null&mapCount.size()>0) {
								if (mapCount.containsKey(3)) {
									int outdate = mapCount.get(3);
									if(outdate>0){
										logger.error("preaddorder>>>月卡车过期禁止入内  uin:"+uin);
										AjaxUtil.ajaxOutput(response, "{\"info\":\"-5\"}");//设置了非月卡禁止入内
										return null;
									}
								}
							}
							AjaxUtil.ajaxOutput(response, "{\"info\":\"-3\"}");//设置了非月卡禁止入内
							return null;
						}
					}
					sql += " ,c_type=? ";
					params.add(3);
				}
			}
			sql += " where id=? ";
			params.add(order_id);
			result = daService.update(sql, params);
			if (result==1) {
				publicMethods.uploadOrder2Line(order_id,4,1);
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("changecartype")){//改变大小车类型
			Integer car_type = RequestUtil.getInteger(request, "car_type", 0);//0：通用，1：小车，2：大车
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			if(orderid == -1){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			Map orderMap = daService.getPojo("select * from order_tb where id=?  ", new Object[]{orderid});
			Integer state = (Integer)orderMap.get("state");
			Long comid = (Long)orderMap.get("comid");
			String car_number = orderMap.get("car_number")+"";
			int result =0;
			if(state==1){
				orderMap.put("car_type", car_type);
				String ret =publicMethods.getOrderPrice(comId, orderMap);
				String total = JsonUtil.getJsonValue(ret, "collect");
				result=daService.update("update order_tb set total=?,car_type=?,sync_state=? where id =? ", 
						new Object[]{StringUtils.formatDouble(total),car_type,0,orderid});
				if(result==1){
					daService.update("update parkuser_cash_tb set amount=? where orderid =? ", new Object[]{StringUtils.formatDouble(total),orderid});
				}
			}else {
				result=daService.update("update order_tb set car_type=?,sync_state=? where state=? and id=? ", new Object[]{car_type,0,0,orderid});
			}
			Map carNumbertType = daService.getMap("select * from car_number_type_tb where car_number = ?", new Object[]{car_number});
			if(carNumbertType!=null&&carNumbertType.get("id")!=null){
				daService.update("update car_number_type_tb set typeid=?,update_time=?,sync_state=? where id = ? ",new Object[]{car_type,System.currentTimeMillis()/1000,0,Long.parseLong(carNumbertType.get("id")+"")});
			}else{
				daService.update("insert into car_number_type_tb(comid,car_number,typeid,update_time) values (?,?,?,?) ",new Object[]{comid,car_number,car_type,System.currentTimeMillis()/1000});
			}
//			int result = daService.update("update order_tb set car_type=?,sync_state=? where state=? and id=? ", new Object[]{car_type,0,0,orderid});
			AjaxUtil.ajaxOutput(response, result + "");
			//http://192.168.199.239/zld/cobp.do?action=changecartype&orderid=&car_type=
		}else if(action.equals("line2local")){//电子支付完成时客户端通知本地去同步线上（线上订单已结算，本地还没结算）
			//http://192.168.199.251/zld/cobp.do?action=line2local&orderid=?
			Long orderid = RequestUtil.getLong(request, "orderid", -1L);
			logger.info("line2locaL******************************************orderid:"+orderid);
			if(orderid!=-1){
				String token = null;
     			Map session = daService.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
     			if(session!=null&&session.get("token")!=null){
     				token = session.get("token")+"";
     			}
				String ret = requestLine(CustomDefind.DOMAIN+"/syncInter.do?action=syncOrder&orderid="+orderid+"&token="+token);
				AjaxUtil.ajaxOutput(response, ret);
			}
		}
		return null;
	}
	public String requestLine(String url){
		 HttpProxy httpProxy = new HttpProxy();
		 String ret = null;
		 Integer update = 0;
		 try {
			 ret = httpProxy.doGet(url);
			 logger.info(ret);
			 if(ret.startsWith("{")&&ret.length()>2){
				 JSONObject jo = JSONObject.fromObject(ret);
				 StringBuffer insertsql = new StringBuffer("update order_tb set");//order by o.end_time desc
				 ArrayList list = new ArrayList();
				 Long createtime = null;
				 String carnumber = null;
				 if(!"null".equals(jo.getString("car_number"))){
					 insertsql.append(" car_number=?,");
					 list.add(jo.getString("car_number"));
					 carnumber = jo.getString("car_number");
				 }
				 if(!"null".equals(jo.getString("total"))){
					 insertsql.append(" total=?,");
					 list.add(jo.getDouble("total"));
				 }
				 if(!"null".equals(jo.getString("state"))){
					 insertsql.append(" state=?,");
					 list.add(jo.getLong("state"));
				 }
				 if(!"null".equals(jo.getString("end_time"))){
					 insertsql.append(" end_time=?,");
					 list.add(jo.getLong("end_time"));
				 }
				 if(!"null".equals(jo.getString("pay_type"))){
					 insertsql.append(" pay_type=?,");
					 list.add(jo.getInt("pay_type"));
				 }
				 if(!"null".equals(jo.getString("uid"))){
					 insertsql.append(" uid=?,");
					 list.add(jo.getLong("uid"));
				 }
				 insertsql.append(" out_passid=?");
				 list.add(jo.getString("out_passid").equals("null")?-1:jo.getLong("out_passid"));
				 String sql = insertsql+" where line_id = ?";
				 Long lineid = jo.getLong("id");
				 list.add(lineid);
				 update = daService.update(sql, list.toArray());
				 if(update==1){
					 if(!"null".equals(jo.getString("total"))&&jo.getInt("pay_type")!=8&&jo.getInt("c_type")!=5){
						 Map map = daService.getMap("select * from order_tb where line_id = ? ", new Object[]{lineid});
						 if(map!=null && map.get("id")!=null){
							 long id = Long.parseLong(map.get("id")+"");
							 Long c = daService.getLong("select count(*) from parkuser_account_tb where orderid = ?", new Object[]{id});
							 if(c!=null&&c<1){
								 int d = daService.update("delete from parkuser_cash_tb where orderid = ?", new Object[]{id});
								 int r = daService.update("insert into parkuser_account_tb(uin,amount,type,create_time,remark,target,orderid) values(?,?,?,?,?,?,?)", new Object[]{jo.getLong("uid"),jo.getDouble("total"),0,System.currentTimeMillis()/1000,"停车费_"+jo.getString("car_number"),4,id});
								 logger.info("sync line to local epay ret:"+r+",orderid:"+lineid+",amount:"+jo.getDouble("total")+",生成电子收费记录ret:"+r+",删除现金支付记录d："+d);
							 }else{
								 logger.info("已有电子支付记录：orderid:"+lineid+",amount:"+jo.getDouble("total"));
							 }
						 }
					 }else{
						 logger.info("价格格式错误或者月卡或者免费不写电子支付记录orderid:"+lineid);
					 }
				 }
				 logger.info("本地结算订单更新ret:"+update+",orderid:"+lineid);
			 }
			} catch (Exception e) {
				e.printStackTrace();
			}
		return update+"";  
	}
	private String getlimitday(Long uin, Long comId){
		Map<String, Object> proMap = daService
				.getMap("select c.* from carower_product c,product_package_tb p where c.pid=p.id and p.comid=? and c.uin=? order by c.e_time desc limit ?",
						new Object[] { comId, uin, 1 });
		Long ntime = System.currentTimeMillis()/1000;
		if(proMap != null){
			Long e_time = (Long)proMap.get("e_time");
			if(e_time > ntime){
				String exptime = StringUtils.getDayString(ntime, e_time);
				return exptime;
			}
		}
		return null;
	}
	
	private void autoCompleteOrder(Long comId, String carNumber, Long uin, Long uid){
		List<Map<String , Object>> sqlMaps = new ArrayList<Map<String,Object>>();
		//月卡重复入场以月卡方式结算
		int ret = daService.update("update order_tb set end_time=?,total=?,state=?,pay_type=?,uin=?,uid=?,sync_state=0,isclick=? where comid=? and car_number =? and state=? and c_type=?", new Object[]{System.currentTimeMillis() / 1000, 0d, 1, 3, uin, uid,0, comId, carNumber, 0,5 });
		logger.info("月卡重复入场0元结算ret:"+ret);
		//非月卡重复入场以现金0元方式结算
		ret += daService.update("update order_tb set end_time=?,total=?,state=?,pay_type=?,uin=?,uid=?,sync_state=0 where comid=? and car_number =? and state=? and c_type <>?",  new Object[]{System.currentTimeMillis() / 1000, 0d, 1, 1, uin, uid, comId, carNumber, 0,5 });
		logger.info("非月卡重复入场0元结算ret:"+ret);
		if(ret>0){
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll("select id from order_tb where comid=? and car_number =? and state=? ", new Object[]{comId,carNumber,0});
			for(Map<String, Object> map : list){
				Map<String, Object> orderattachsqlMap = new HashMap<String, Object>();
				Long id = (Long)map.get("id");
				Long r = daService.getLong("select count(*) from order_attach_tb where order_id=? ", new Object[]{id});
				if(r > 0){
					orderattachsqlMap.put("sql", "update order_attach_tb set settle_type=? where order_id=? ");
					orderattachsqlMap.put("values", new Object[]{1, id});
				}else{
					orderattachsqlMap.put("sql", "insert into order_attach_tb(settle_type,order_id) values(?,?) ");
					orderattachsqlMap.put("values", new Object[]{1, id});
				}
				sqlMaps.add(orderattachsqlMap);
			}
			//入口若生成订单时候，发现这个车牌还有为结算订单，需要先判断这个未结算订单的生成时间距现在有没有大于10分钟，若大于，则以零元结算掉，重新生成订单，并将垃圾订单数-1；假如时间小于10分钟，则认为是车辆还没有进场，客户端重复生成的订单，前面订单已零元结算掉，并且重新生成订单，但垃圾订单数不变
			Long count = 0L;
			List<Map<String, Object>> list2 = daService.getAll("select create_time from order_tb where comid=? and  car_number =? and state=?", new Object[]{comId,carNumber,0});
			if(list2 != null){
				Long current_time = System.currentTimeMillis()/1000;
				for(Map<String, Object> map : list2){
					Long create_time = (Long)map.get("create_time");
					if(current_time - create_time > 10*60){
						count++;
					}
				}
			}
			//未结算的垃圾订单
			Map<String, Object> rubbishsqlMap = new HashMap<String, Object>();
			rubbishsqlMap.put("sql", "update com_info_tb set invalid_order=invalid_order-? where id=? ");
			rubbishsqlMap.put("values", new Object[]{count, comId});
			sqlMaps.add(rubbishsqlMap);
			
			daService.bathUpdate(sqlMaps);
		}
	}
	
	/**
	 * 当前订单，正在停车的订单，没有或只有一条
	 * @param request
	 * @param uin
	 * @return
	 */
	private void doMessage(HttpServletRequest request,Long uin,String cid){
		Map<String, Object> infoMap = new HashMap<String, Object>();
		Map orderMap = daService.getPojo("select o.create_time,o.id,o.comid,o.car_type,c.company_name,c.address,o.state from order_tb o,com_info_tb c where o.comid=c.id and o.uin=? and o.state=?",
				new Object[]{uin,0});
		if(orderMap!=null){
			Long btime = (Long)orderMap.get("create_time");
			Long end = System.currentTimeMillis()/1000;
			Long comId = (Long)orderMap.get("comid");
			Integer car_type = (Integer)orderMap.get("car_type");
			infoMap.put("total",publicMethods.getPrice(btime, end, comId,car_type));
			//infoMap.put("duration", "已停"+(end-btime)/(60*60)+"时"+((end-btime)/60)%60+"分");
			infoMap.put("btime", btime);//TimeTools.getTime_yyyyMMdd_HHmm(btime*1000).substring(10));
			infoMap.put("etime",end);// TimeTools.getTime_yyyyMMdd_HHmm(end*1000).substring(10));
			infoMap.put("parkname", orderMap.get("company_name"));
			infoMap.put("address", orderMap.get("address"));
			infoMap.put("orderid", orderMap.get("id"));
			infoMap.put("state",orderMap.get("state"));
			infoMap.put("parkid", comId);
			//infoMap.put("begintime", TimeTools.getTimeStr_yyyy_MM_dd(btime*1000));
		}else {
			//{ "info": { "address": "嘉华大厦A座1205", "btime": "123204432", "etime": "123214432", 
			//"orderid": "134123413512341", "parkid": "542452", "parkname": "嘉华大厦停车场", "state": "0", "total": "500" }
			//, "msgid": 123446 ,"mtype": "0" }
//			infoMap.put("info", "-1");
//			infoMap.put("message", "没有订单");
		}
		
		//发送消息给车主，已生成进场订单
		String content =StringUtils.createJson(infoMap);
		content = "{\"mtype\":\"0\",\"msgid\":\"\",\"info\":"+content+"}";
		//{taskId=OSS-1021_p1rP5EZdhu8sqrxDiMoeD6, result=ok, status=successed_offline}
		String ret ="";
		if(cid!=null&&cid.length()==32){
			ret =pushtoSingle.sendSingle(cid, content);
		}else {
			 pushtoSingle.sendMessageByApns(uin, content, cid);
		}
	}
	/*
	 * 设置车牌照片参数
	 */
	private void setPicParams(List list){
		List<Object> orderids = new ArrayList<Object>();
		if(list != null && !list.isEmpty()){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				orderids.add(map.get("id"));
			}
		}
		if(!orderids.isEmpty()){
			String preParams  ="";
			for(Object orderid : orderids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap("select * from car_picturs_tb where orderid in ("+preParams+") order by pictype", orderids);
			if(!resultList.isEmpty()){
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					Long id=(Long)map1.get("id");
					for(Map<String,Object> map: resultList){
						Long orderid = (Long)map.get("orderid");
						if(id.intValue()==orderid.intValue()){
							Integer pictype = (Integer)map.get("pictype");
							if(pictype == 0){
								map1.put("lefttop", map.get("lefttop"));
								map1.put("rightbottom", map.get("rightbottom"));
								map1.put("width", map.get("width"));
								map1.put("height", map.get("height"));
								break;
							}
						}
					}
				}
			}
		}
	}
//	public void uploadOrder2Line(final Long orderid){
//	            	 String ret = null;
//	            	 try {
//	            		 Map map = daService.getMap("select * from order_tb where id = ?",new Object[]{orderid});
//		            	 String order = AjaxUtil.encodeUTF8(StringUtils.createJson(map));
//		            	 HttpProxy httpProxy = new HttpProxy();
//		            	 Map parammap = new HashMap();
//		            	 parammap.put("order", order);
//	            		 ret = httpProxy.doPost(Constants.Domain+"/syncInter.do?action=uploadOrder2Line&type=1", parammap);
//	            		 System.out.println(ret);
//	         	        if(ret!=null&&ret.startsWith("1")){
//	         	        	int r = daService.update("update order_tb set uin = ?,sync_state=?,line_id=? where id = ?", new Object[]{Long.valueOf(ret.split("_")[1]),1,Long.valueOf(ret.split("_")[2]),orderid});
//	         	        	System.out.println(r);
//	         	        }
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//						System.out.println("tongbufanhui:"+ret);
//	}
}

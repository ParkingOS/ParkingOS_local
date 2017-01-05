package com.zld.struts.request;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import pay.Constants;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.impl.CommonMethods;
import com.zld.impl.MongoClientFactory;
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.service.LogService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.HttpProxy;
import com.zld.utils.OrderSortCompare;
import com.zld.utils.ParkingMap;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
/**
 * 停车场收费员请求处理，分享车位，打折处理等
 * @author Administrator
 *
 */
public class CollectorRequestAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private LogService logService;
	@Autowired
	private PublicMethods publicMethods;
	@Autowired
	private PgOnlyReadService pService;
	@Autowired 
	private CommonMethods commonMethods;
	
	private Logger logger = Logger.getLogger(CollectorRequestAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String token =RequestUtil.processParams(request, "token");
		String action =RequestUtil.processParams(request, "action");
		String out= RequestUtil.processParams(request, "out");
		Map<String,Object> infoMap = new HashMap<String, Object>();
		Long comId =null;
		Long uin = null;
		response.setContentType("application/json");
		Long authFlag = 0L;
		if(token.equals("")){
			infoMap.put("info", "no token");
		}else {
			Map comMap = daService.getPojo("select * from user_session_tb where token=?", new Object[]{token});
			if(comMap!=null&&comMap.get("comid")!=null){
				comId=(Long)comMap.get("comid");
				uin =(Long) comMap.get("uin");
				authFlag = daService.getLong("select auth_flag from user_info_tb where id =? ", new Object[]{uin});
			}else {
				infoMap.put("info", "token is invalid");
			}
		}
		logger.info("token="+token+",comid="+comId+",action="+action+",uin="+uin);
		/*token为空或token无效时，返回错误		 */
		if(token.equals("")||comId==null||uin==null){
			if(out.equals("json"))
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			else
				AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
			return null;
			
		}
		if(action.equals("myinfo")){
			AjaxUtil.ajaxOutput(response, myInfo(uin));
			return null;
			//test:http://127.0.0.1/zld/collectorrequest.do?action=myinfo&token=0dc591f7ddda2d6fb73cd8c2b4e4a372
		}else if(action.equals("comparks")){
			//http://127.0.0.1/zld/collectorrequest.do?action=comparks&out=josn&token=5f0c0edb1cc891ac9c3fa248a28c14d5
			String result =getComParks(comId);
			result = result.replace("null", "");
			AjaxUtil.ajaxOutput(response, result);
		}else if(action.equals("autoup")){//自动抬杆
			String ret = autoUp(request,comId,uin);
			AjaxUtil.ajaxOutput(response, ret);
			//http://127.0.0.1/zld/collectorrequest.do?action=autoup&price=&carnumber=&token=0dc591f7ddda2d6fb73cd8c2b4e4a372
			return null;
		}else if(action.equals("uplogfile")){
			AjaxUtil.ajaxOutput(response,uploadPadLogFile(request,comId));
			return null;
		}else if(action.equals("uplogs")){//上传平板日志
			//http://127.0.0.1/zld/collectorrequest.do?action=uplogs&token=&logs=111222frfewae
			String logs = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "logs"));
			logger.error("uplogs:"+logs);
			Writer writer = null;
			try {
				writer = new BufferedWriter(new FileWriter(new File("c:\\padlogs.txt"),true));
				writer.write(logs+"\n");
				writer.flush();
				writer.close();
				logger.error("uplogs:写入成功");
				AjaxUtil.ajaxOutput(response, "1");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				if(writer!=null)
					writer.close();
			}
			AjaxUtil.ajaxOutput(response, "1");
			return null;
		}
		else if(action.equals("toshare")){//分享车位
			Integer number = RequestUtil.getInteger(request, "s_number", -1);
			boolean isCanLalaRecord = ParkingMap.isCanRecordLaLa(uin);
			if(number!=-1){
				doShare(comId, uin,number,infoMap,isCanLalaRecord);
			}else {
				infoMap.put("info", "fail");
				infoMap.put("message", "分享数量不合法!");
			}
			//test:http://127.0.0.1/zld/collectorrequest.do?action=toshare&token=d450ea04d67bf0b428ea1204675d5b53&s_number=800

		}else if(action.equals("tosale")){//打折处理
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Integer houer = RequestUtil.getInteger(request, "hour", 0);
			if(orderId!=-1&&houer>0){
				doSale(comId, uin,houer, orderId,infoMap);
			}else {
				infoMap.put("info", "错误：没有订单编号或优惠小时!");
			}
			//test:http://127.0.0.1/zld/collectorrequest.do?action=tosale&token=d450ea04d67bf0b428ea1204675d5b53&orderid=1&hour=1
		}else if(action.equals("orderdetail")){//订单详情
			orderDetail(request,comId,infoMap);
			//test:http://127.0.0.1/zld/collectorrequest.do?action=orderdetail&token=d450ea04d67bf0b428ea1204675d5b53&orderid=151
		}else if(action.equals("currorders")){//当前订单
			String result = currOrders(request,uin,comId,out,infoMap);
			System.out.println(result);
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//test:http://127.0.0.1/zld/collectorrequest.do?action=currorders&token=4bad81d8d7993446265a155318182dee&page=1&size=10&out=json
		}else if(action.equals("orderhistory")){//历史订单
			String result = orderHistory(request,comId,out);
			AjaxUtil.ajaxOutput(response, result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=orderhistory&day=last&uid=10828&ptype=0&token=ec5c8185dae6f48c03c43785fe17be22&uin=10824&page=1&size=10&out=json
		}else if(action.equals("ordercash")){//现金收费
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Double total = RequestUtil.getDouble(request, "total", 0d);
			String imei  =  RequestUtil.getString(request, "imei");
			logger.info("ordercash>>>>>:orderid:"+orderId);
			if(orderId!=-1){
				Map orderMap = daService.getPojo("select * from order_tb where id=?", new Object[]{orderId});
				if(orderMap.get("total")!=null) {
					Double prepay = StringUtils.formatDouble(orderMap.get("total"));
					boolean result = prepayRefund(orderMap,prepay);
					//预支付金额退款
					logger.info("现金结算预付款退款:"+result+",orderid:"+orderId);
				}
				
				List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
				//更新订单状态，收费成功
				Map<String, Object> orderSqlMap = new HashMap<String, Object>();
				orderSqlMap.put("sql", "update order_tb set state=?,total=?,end_time=?,pay_type=?,imei=?,uid=? where id=?");
				orderSqlMap.put("values", new Object[]{1,total,System.currentTimeMillis()/1000,1,imei,uin,orderId});
				bathSql.add(orderSqlMap);
				
				//现金明细
				Map<String, Object> cashsqlMap =new HashMap<String, Object>();
				cashsqlMap.put("sql", "insert into parkuser_cash_tb(uin,amount,type,orderid,create_time) values(?,?,?,?,?)");
				cashsqlMap.put("values",  new Object[]{uin,total,0,orderId,System.currentTimeMillis()/1000});
				bathSql.add(cashsqlMap);
				
				boolean b = daService.bathUpdate(bathSql);
				logger.info("ordercash>>>>ordreid:"+orderId+",b:"+b);
				if(b){
					infoMap.put("info", "现金收费成功!");
					//更新订单消息中的状态 
					daService.update("update order_message_tb set state=? where orderid=?", 
							new Object[]{2,orderId});
					if(out.equals("json")){
						AjaxUtil.ajaxOutput(response, "1");
						return null;
					}
				}else {
					infoMap.put("info", "现金收费失败!");
					if(out.equals("json")){
						AjaxUtil.ajaxOutput(response, "-1");
						return null;
					}
				}
			}
			//test:http://127.0.0.1/zld/collectorrequest.do?action=ordercash&token=5286f078c6d2ecde9b30929f77771149&orderid=787824
		}else if(action.equals("freeorder")){//HD版，免费放行
			Long orderId = RequestUtil.getLong(request, "orderid", -1L);
			Long out_passid = RequestUtil.getLong(request, "passid", -1L);//出口通道id
			Long isPolice = RequestUtil.getLong(request, "isPolice", -1L);//是否军警车
			Long freereasons = RequestUtil.getLong(request, "freereasons", -1L);//免费原因
			if(orderId != -1){
				logger.info("收费员："+uin+"把订单："+orderId+"置为免费放行，ispolice:"+isPolice);
				int result =0;
				Map map = daService.getPojo("select * from order_tb where id=? ", new Object[]{orderId});
				logger.info(map);
				if(isPolice==1){
					result = daService.update("update order_tb set total=?,state=?,end_time=?,out_passid=?,uid=?,freereasons=? where id=? ", new Object[]{0,1,System.currentTimeMillis()/1000,out_passid,uin,freereasons,orderId});
				}else{
					//计算应收价格
					Integer pid = (Integer)map.get("pid");
					Integer car_type = (Integer)map.get("car_type");//0：通用，1：小车，2：大车
					Long start= (Long)map.get("create_time");
					Long end =  System.currentTimeMillis()/1000;
					Double total  = 0d;
					if(map.get("end_time") != null){
						end = (Long)map.get("end_time");
					}
					Map ordermap = commonMethods.getOrderInfo(orderId, -1L, end);
					total = Double.valueOf(ordermap.get("aftertotal") + "");
					logger.info(total);
					result = daService.update("update order_tb set total=?,state=?,end_time=?,pay_type=?,out_passid=?,uid=?,freereasons=? where id=? and state=? ", new Object[]{total,1,System.currentTimeMillis()/1000,8,out_passid,uin,freereasons,orderId,1});
				}
				logger.info("&&&&&&"+result);
				int r = daService.update("update parkuser_cash_tb set amount=? where orderid=? and type=? ",
						new Object[] { 0, orderId, 0 });
				logger.info("freeorder>>>>现金收费明细置为0，orderid:"+orderId+",r:"+r);
				if(result == 1){
					AjaxUtil.ajaxOutput(response, "1");
				}else{
					AjaxUtil.ajaxOutput(response, "0");
				}
				logger.info("免费返回了。。。。");
				if(result==1){
					logger.info("返回了在上传。。。。");
					if(map.get("line_id")!=null)
//						new Thread(){
//							@Override
//							public void run() {
//								publicMethods.uploadOrder2Line(orderId,2,2);
//							};
//						}.start();
							
						daService.update("update order_tb set sync_state=? where id = ? and sync_state<3", new Object[]{4,orderId});//免费的sync_state设置成4   避免先上传再免费把免费的标记覆盖掉了
//						publicMethods.uploadOrder2Line(orderId,2,2);
				}
			}
			//http://192.168.199.239/zld/collectorrequest.do?action=freeorder&token=7d4860ef99bd70d5c91af535bb2c5065&orderid=1
		}else if(action.equals("cominfo")){//公司信息
			Map comMap = daService.getPojo("select * from com_info_tb where id=?", new Object[]{comId});
			List<Map<String, Object>> picMap = daService.getAll("select picurl from com_picturs_tb where comid=? order by id desc limit ? ",
					new Object[]{comId,1});
			String picUrls = "";
			if(picMap!=null&&!picMap.isEmpty()){
				for(Map<String, Object> map : picMap){
					picUrls +=map.get("picurl")+";";
				}
				if(picUrls.endsWith(";"))
					picUrls = picUrls.substring(0,picUrls.length()-1);
			}
			if(comMap!=null&&comMap.get("id")!=null){
				String mobile = (String)comMap.get("mobile");
				String phone = (String)comMap.get("phone");
				Integer city = (Integer)comMap.get("city");
				if(phone==null||phone.equals(""))
					phone = mobile;
				Map priceMap = getPriceMap(comId);
				String timeBetween = "";
				Double price = 0d;
				if(priceMap!=null){
					Integer payType = (Integer)priceMap.get("pay_type");
					if(payType==0){
						Integer start = (Integer)priceMap.get("b_time");
						Integer end = (Integer)priceMap.get("e_time");
						if(start<10&&end<10)
							timeBetween = "0"+start+":00-0"+end+":00";
						else if(start<10&&end>9){
							timeBetween = "0"+start+":00-"+end+":00";
						}else if(start>9){
							timeBetween = start+":00-"+end+":00";
						}
					}else {
						timeBetween = "00:00-24:00";
					}
					if(priceMap.get("price")!=null)
						price = Double.valueOf(priceMap.get("price")+"");
				}
				Integer parkType = (Integer)comMap.get("parking_type");
				parkType = parkType==null?0:parkType;
				String ptype = "地面";
				if(parkType==1)
					ptype="地下";
				else if(parkType==2){
					ptype="占道";
				}
				Integer stopType = (Integer)comMap.get("stop_type");
				String sType = "平面排列";
				if(stopType==1)
					sType="立体排列";
				infoMap.put("name", comMap.get("company_name"));
				infoMap.put("address", comMap.get("address"));
				infoMap.put("parkingtotal", comMap.get("parking_total"));
				infoMap.put("parktype",ptype);
				infoMap.put("phone", phone);
				infoMap.put("timebet", timeBetween);
				infoMap.put("price", price);
				infoMap.put("stoptype", sType);
				infoMap.put("service", "人工服务");
				infoMap.put("id", comId);
				infoMap.put("resume", comMap.get("resume")==null?"":comMap.get("resume"));
				infoMap.put("longitude", comMap.get("longitude"));
				infoMap.put("latitude", comMap.get("latitude"));
				infoMap.put("isfixed", comMap.get("isfixed"));
				infoMap.put("picurls",picUrls);
				List<Map<String, Object>> carTypeList = commonMethods.getCarType(comId);
				String carTypes = StringUtils.createJson(carTypeList);
				carTypes = carTypes.replace("value_no", "id").replace("value_name", "name");
				infoMap.put("car_type", comMap.get("car_type"));
				infoMap.put("allCarTypes", carTypes);
				infoMap.put("passfree", comMap.get("passfree"));
				infoMap.put("ishdmoney", comMap.get("ishdmoney"));
				infoMap.put("ishidehdbutton", comMap.get("ishidehdbutton"));
				infoMap.put("currentTimeMillis", System.currentTimeMillis());
//				infoMap.put("issuplocal", comMap.get("issup_local"));
				infoMap.put("issuplocal", 0);
				infoMap.put("fullset",comMap.get("full_set"));//车位已满能否进场
				infoMap.put("leaveset",comMap.get("leave_set"));//车场识别识别抬杆设置  （有的月卡车场没人收费（不收费））
				List list = daService.getAll("select id as value_no,name as value_name from free_reasons_tb where comid=? order by sort , id desc ", new Object[]{comId});
				infoMap.put("freereasons",list);
				infoMap.put("liftreason",getLiftReason(comId));
				String swith="1publicMethods.getCollectMesgSwith()";
				if("1".equals(swith)){
					if(city!=null&&city==110000)//0605仅通知北京的收费员
						infoMap.put("mesgurl", "collectmesg.png");
					else {//通知济南以外的收费员
						infoMap.put("mesgurl", "collectmesg_jn.png");
					}
				}
			}else {
				infoMap.put("info", "token is invalid");
			}
			//System.err.println(infoMap);
			//test:http://127.0.0.1/zld/collectorrequest.do?action=cominfo&token=85cd74cfe5b40b57ae04f9d2b8e24e15&out=json
		}else if(action.equals("corder")){//一键查询
			/*
			 * 车场里目前停了多少车：（当前订单数量）
				今日已离场车场：（今日历史订单数量）
				今日已经收到金额：（今日历史订单总金额）
			 */
			Long btime = TimeTools.getToDayBeginTime();
			Long etime = System.currentTimeMillis()/1000;
			Long ccount =0L;//当前订单数
			int ocount =0;//已结算订单数
			Long tcount = 0L;//今日当前订单数
			Double total =0d;
			List<Map<String, Object>> orderList = daService.getAll("select  total,state from order_tb where comid=? and end_time between ? and ? and state=? ",
					new Object[]{comId,btime,etime,1});
			if(orderList!=null){
				ocount=orderList.size();
				for(Map<String,Object> map: orderList){
					total += Double.valueOf(map.get("total")+"");
				}
			}
			ccount = daService.getLong("select count(*) from order_tb where comid=? and state=? ",
					new Object[]{comId,0});
			tcount = daService.getLong("select count(1) from order_tb where comid=? and state=? " +
					"and create_time between ? and ?", new Object[]{comId,0,btime,etime});
			AjaxUtil.ajaxOutput(response, "{\"ccount\":\""+ccount+"\",\"ocount\":\""+ocount+"\",\"tcount\":\""+tcount+"\",\"total\":\""+StringUtils.formatDouble(total)+"\"}");		
			//test:http://127.0.0.1/zld/collectorrequest.do?action=corder&token=b4e6727f914157c8745f6f2c023c8c96
			return null;
		}
		else if(action.equals("score")){//请求积分
			//test:http://127.0.0.1/zld/collectorrequest.do?action=score&token=2f73bdccceecbbf436b61aca464af21b&week=toweek&detail=   toweek&detail=total
			//detail:  toweek:本周积分 ,total:总积分 ,空：查询排行
			//week:周，last:上周排行，toweek：本周排行 ,空：本周
			
			AjaxUtil.ajaxOutput(response, "[]");
			return null;
			
			/*String type = RequestUtil.processParams(request, "week");
			String detail = RequestUtil.processParams(request, "detail");
			Long btime =TimeTools.getLongMilliSecondFrom_HHMMDD(StringUtils.getMondayOfThisWeek())/1000;
			Long etime=System.currentTimeMillis()/1000;
			if(detail.equals("")){
				if(type.equals("last")){
					etime = btime;
					btime = btime-7*24*60*60;
				}
				List scroeList = daService.getAll("select sum(lala_scroe+nfc_score+praise_scroe+pai_score+online_scroe+recom_scroe) score,uin from collector_scroe_tb where create_time between ? and ? group by uin order by score desc",
						new Object[]{btime,etime-1});
				List<Map<String, Object>> resultList = setScroeList(scroeList);
				setSort(resultList);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(resultList));
				return null;
			}else if(detail.equals("toweek")){//本周积分详情
				infoMap = daService.getMap("select sum(lala_scroe) lala_scroe,sum(nfc_score) nfc_score,sum(praise_scroe) praise_scroe," +
						"sum(pai_score) pai_score ,sum(online_scroe) online_scroe,uin,sum(recom_scroe) recom_scroe from collector_scroe_tb where uin=? and create_time between ? and ? group by uin",
						new Object[]{uin,btime,etime});
				if(infoMap!=null){
					Long ls = (Long)infoMap.get("lala_scroe");
					Long ss = (Long)infoMap.get("praise_scroe");
					Double ps = StringUtils.formatDouble(infoMap.get("pai_score"));
					Long rs = (Long)infoMap.get("recom_scroe");
					Double os = 0d;
					Double ns = 0d;//
					if(infoMap.get("online_scroe")!=null)
						os = Double.valueOf(infoMap.get("online_scroe")+"");
					if(infoMap.get("nfc_score")!=null)
						ns = Double.valueOf(infoMap.get("nfc_score")+"");
					if(ls==null)
						ls =0L;
					if(ss==null)
						ss=0L;
					infoMap.put("lala_scroe", ls);
					infoMap.put("nfc_score", StringUtils.formatDouble(ns));
					infoMap.put("praise_scroe", ss);
					infoMap.put("sign_score", ps);
					infoMap.put("online_scroe", os);
					infoMap.put("recom_scroe", rs);
					infoMap.put("score", StringUtils.formatDouble(ls+ss+ns+os+rs+ps));
					infoMap.put("cashscore", 0);//已兑换积分，***预留接口****
				}
			}else if(detail.equals("total")){//历史积分详情
				infoMap = daService.getMap("select sum(lala_scroe) lala_scroe,sum(nfc_score) nfc_score," +
						"sum(praise_scroe) praise_scroe,sum(pai_score) pai_score ,sum(online_scroe) online_scroe,uin,sum(recom_scroe) recom_scroe " +
						" from collector_scroe_tb where uin=? group by uin", new Object[]{uin});
				if(infoMap!=null){
					Long ls = (Long)infoMap.get("lala_scroe");
					Long ss = (Long)infoMap.get("praise_scroe");
					Double ps = StringUtils.formatDouble(infoMap.get("pai_score"));
					Long rs = (Long)infoMap.get("recom_scroe");
					Double os = 0d;
					Double ns = 0d;//
					if(infoMap.get("online_scroe")!=null)
						os = Double.valueOf(infoMap.get("online_scroe")+"");
					if(infoMap.get("nfc_score")!=null)
						ns = Double.valueOf(infoMap.get("nfc_score")+"");
					if(ls==null)
						ls =0L;
					if(ss==null)
						ss=0L;
					infoMap.put("lala_scroe", ls);
					infoMap.put("nfc_score", StringUtils.formatDouble(ns));
					infoMap.put("praise_scroe", ss);
					infoMap.put("sign_score", ps);
					infoMap.put("online_scroe", os);
					infoMap.put("recom_scroe", rs);
					infoMap.put("score", StringUtils.formatDouble(ls+ss+ns+os+rs+ps));
					infoMap.put("cashscore", 0);//已兑换积分，***预留接口****
				}
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;*/
			//test:http://127.0.0.1/zld/collectorrequest.do?action=score&token=2f73bdccceecbbf436b61aca464af21b&week=toweek&detail=to   toweek&detail=total
			//(toweek,total)
			
		}else if(action.equals("getparkaccount")){
			
			Map parkAccountMap  = daService.getMap("select sum(amount) amount from park_account_tb where create_time>? and uid =? and type=? ", 
					new Object[]{TimeTools.getToDayBeginTime(),uin,0});
			Double total = 0d;
			if(parkAccountMap!=null){
				total =StringUtils.formatDouble(parkAccountMap.get("amount"));
			}
			AjaxUtil.ajaxOutput(response, ""+total);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=getparkaccount&token=4182e6ad895208c3d4829d447e0c61b7
		}else if(action.equals("getpadetail")){//账户明细
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long stype=RequestUtil.getLong(request, "stype", -1L);//0:收入，1提现
			String sql = "select create_time ,remark r,amount money,type mtype  from park_account_tb where comid=?";
			String countSql = "select count(id)  from park_account_tb where comid=?";
			List<Object> params = new ArrayList<Object>();
			params.add(comId);
			
			if(stype>-1){
				if(stype==1){//提现
					sql +=" and type=? ";
					countSql +=" and type=? ";
					params.add(stype);
				}else {//收入或停车宝返现 
					sql +=" and type in(?,?) ";
					countSql +=" and type in(?,?) ";
					params.add(stype);
					params.add(2L);
				}
			}
			Long count= daService.getCount(countSql, params);
			List pamList = null;//daService.getPage(sql, null, 1, 20);
			if(count>0){
				pamList = daService.getAll(sql+" order by id desc ", params, pageNum, pageSize);
			}
			
			if(pamList!=null&&!pamList.isEmpty()){
				for(int i=0;i<pamList.size();i++){
					Map map = (Map)pamList.get(i);
					Integer type = (Integer)map.get("mtype");
					String remark = (String)map.get("r");
					if(type==0){
						if(remark.indexOf("_")!=-1){
							map.put("note", remark.split("_")[0]);
							map.put("target", remark.split("_")[1]);
						}
					}else if(type==1){
						map.put("note", "提现");
						map.put("target", "银行卡");
					}else if(type==2){
						map.put("note", "返现");
						map.put("target", "停车宝");
						map.put("mtype", 1);
					}
					map.remove("r");
				}
			}
			String reslut =  "{\"count\":"+count+",\"info\":"+StringUtils.createJson(pamList)+"}";
			AjaxUtil.ajaxOutput(response, reslut);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=getpadetail&token=c5ea6e5fd0acdf97a262f7f86c31f3ae
		}else if(action.equals("withdraw")){//车场提现请求
			//http://192.168.199.240/zld/collectorrequest.do?action=withdraw&uid=10343&comid=858&money=20
			Double money = RequestUtil.getDouble(request, "money", 0d);
			Long count = daService.getLong("select count(*) from park_account_tb where comid= ? and create_time>? and type=?  ", 
					new Object[]{comId,TimeTools.getToDayBeginTime(),1}) ;
			if(count>2){//每天只能三次
				AjaxUtil.ajaxOutput(response, "{\"result\":-2,\"times\":"+count+"}");
				return null;
			}
			
			List<Map> accList = daService.getAll("select id,type from com_account_tb where comid =? and type in(?,?) and state =? order by id desc",
					new Object[]{comId,0,2,0});
			Long accId = null;
			Integer type =0;
			if(accList!=null&&!accList.isEmpty()){
				accId = null;
				for(Map m: accList){
					type = (Integer)m.get("type");
					if(type!=null&&type==2){
						accId =  (Long)m.get("id");	
						break;
					}
				}
				if(accId==null)
					accId=(Long)accList.get(0).get("id");
			}else{
				//没有设置银行账户
				AjaxUtil.ajaxOutput(response, "{\"result\":-1,\"times\":0}");
				return null;
			}
			//提现操作
			boolean result =false;
			if(money>0){
				Map userMap = daService.getMap("select money from com_info_Tb where id=? ", new Object[]{comId});
				//用户余额
				Double balance =StringUtils.formatDouble(userMap.get("money"));
				if(money<=balance){//提现金额不大于余额
					//扣除帐号余额//写提现申请表
					List<Map<String, Object>> sqlList = new ArrayList<Map<String,Object>>();
					Map<String, Object> userSqlMap = new HashMap<String, Object>();
					userSqlMap.put("sql", "update com_info_Tb set money = money-? where id= ?");
					userSqlMap.put("values", new Object[]{money,comId});
					Map<String, Object> withdrawSqlMap = new HashMap<String, Object>();
					withdrawSqlMap.put("sql", "insert into withdrawer_tb  (comid,amount,create_time,acc_id,uin,wtype) values(?,?,?,?,?,?)");
					withdrawSqlMap.put("values", new Object[]{comId,money,System.currentTimeMillis()/1000,accId,uin,type});
					Map<String, Object> moneySqlMap = new HashMap<String, Object>();
					moneySqlMap.put("sql", "insert into park_account_tb (comid,amount,create_time,type,remark,uid,source) values(?,?,?,?,?,?,?)");
					moneySqlMap.put("values", new Object[]{comId,money,System.currentTimeMillis()/1000,1,"提现",uin,5});
					sqlList.add(userSqlMap);
					sqlList.add(withdrawSqlMap);
					sqlList.add(moneySqlMap);
					result = daService.bathUpdate(sqlList);
				}
				if(result)
					AjaxUtil.ajaxOutput(response, "{\"result\":1,\"times\":"+count+"}");
				else {
					AjaxUtil.ajaxOutput(response,"{\"result\":0,\"times\":"+count+"}");
				}
			}
		}else if(action.equals("getpaccount")){//查询停车场账户总额
			Map comMap = daService.getMap("select money from com_info_tb where id=?", new Object[]{comId});
			Double total = 0d;
			if(comMap!=null){
				total = StringUtils.formatDouble(comMap.get("money"));
			}
			AjaxUtil.ajaxOutput(response, total+"");
			//http://127.0.0.1/zld/collectorrequest.do?action=getpaccount&token=17ad4f0a3cbdce40c56595f00d7666bc
			return null;
		}else if(action.equals("getparkbank")){
			Map comaMap  = daService.getMap("select id,card_number,name,mobile,bank_name,area,bank_pint,user_id from com_account_tb where comid=? and type=? order by id desc ",new Object[]{comId,0});
			String ret =StringUtils.createJson(comaMap);
			AjaxUtil.ajaxOutput(response, ret.replace("null", ""));
			//http://127.0.0.1/zld/collectorrequest.do?action=getparkbank&token=17ad4f0a3cbdce40c56595f00d7666bc
			return null;
		}else if(action.equals("addparkbank")){
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
//			Long uin =RequestUtil.getLong(request, "uin",-1L);
			String card_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "card_number"));
			String mobile =RequestUtil.processParams(request, "mobile");
			String userId =RequestUtil.processParams(request, "user_id");
			String bank_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_name"));
			String area =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "area"));
			String bank_point =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_pint"));
			int result = 0;
			if(!card_number.equals("")&&!mobile.equals("")&&!bank_name.equals("")){
				result = daService.update("insert into com_account_tb (comid,uin,name,card_number,mobile," +
						"bank_name,atype,area,bank_pint,type,state,user_id)" +
						" values(?,?,?,?,?,?,?,?,?,?,?,?)",
						new Object[]{comId,uin,name,card_number,mobile,bank_name,0,area,bank_point,0,0,userId});
			}
			//http://127.0.0.1/zld/collectorrequest.do?action=addparkbank&token=aa9a48d2f41bb2722f29c8714cbc754c
			//&name=&card_number=&mobile=&bank_name=&area=&bank_point=&atype=&note=
			logger.info(result);
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("editpbank")){
			Long id = RequestUtil.getLong(request, "id", -1L);
			String name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "name"));
			String card_number =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "card_number"));
			String mobile =RequestUtil.processParams(request, "mobile");
			String bank_name =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_name"));
			String area =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "area"));
			String bank_point =AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "bank_pint"));
			String userId =RequestUtil.processParams(request, "user_id");
			Integer atype = RequestUtil.getInteger(request, "atype", 0);//0银行卡，1支付宝，2微信
			int result = 0;
			if(!card_number.equals("")&&!mobile.equals("")&&!bank_name.equals("")&&id!=-1){
				result = daService.update("update com_account_tb set name=?,card_number=?,mobile=?,bank_name=?," +
						"area=?,bank_pint=?,atype=?,user_id=? where id = ? and type=? ",
						new Object[]{name,card_number,mobile,bank_name,area,bank_point,atype,userId,id,0});
			}
			//http://127.0.0.1/zld/collectorrequest.do?action=editpbank&token=aa9a48d2f41bb2722f29c8714cbc754c
			//&name=&card_number=&mobile=&bank_name=&area=&bank_point=&atype=&note=user_id=&id=
			AjaxUtil.ajaxOutput(response, result+"");
		}else if(action.equals("uploadll")){//upload lat and lon, 收费员上传经纬度
			Double lon = RequestUtil.getDouble(request, "lon", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			if(lat==0||lon==0){
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}
			Map comMap = daService.getMap("select longitude,latitude from com_info_Tb where id =? ", new Object[]{comId});
			Double lon1 = Double.valueOf(comMap.get("longitude")+"");
			Double lat1 = Double.valueOf(comMap.get("latitude")+"");
			Double distance = StringUtils.distance(lon, lat, lon1, lat1);
			Integer isOnseat = 0;
			if(distance<500){//在车场500米范围内时，认为在位。
				isOnseat = 1;
			}
			Long ntime = System.currentTimeMillis()/1000;
			logger.info(">>>>>parkuser distance,uin:"+uin+",dis:"+distance+",authflag:"+authFlag);
			//更新收费员在位信息，23表示在位
			if(authFlag!=13){//泊车员上传不改状态
				daService.update("update user_info_tb set online_flag =? where id=? ", new Object[]{22+isOnseat,uin});
			}
			//写入位置上传日志 
			int result = daService.update("insert into user_local_tb (uid,lon,lat,distance,is_onseat,ctime) values(?,?,?,?,?,?)",
					new Object[]{uin,lon,lat,distance,isOnseat,ntime});
			Long count = daService.getLong("select count(id) from user_info_Tb where comid =? and online_flag=? ", new Object[]{comId,23});
			if(count>0){//有收费员在位,更新车场是否有收费员在位标志
				daService.update("update com_info_tb set is_hasparker=?, update_time=? where id = ? and is_hasparker=? ", new Object[]{1,ntime,comId,0});
			}else {
				daService.update("update com_info_tb set is_hasparker=?, update_time=? where id = ? and is_hasparker=?", new Object[]{0,ntime,comId,1});
			}
			AjaxUtil.ajaxOutput(response, ""+result);
			//http://127.0.0.1/zld/collectorrequest.do?action=uploadll&token=aa9a48d2f41bb2722f29c8714cbc754c&lon=&lat=
			return null;
		}else if(action.equals("reguser")){//收费员推荐车主
			String carNumber =AjaxUtil.decodeUTF8( RequestUtil.getString(request, "carnumber"));
			carNumber = carNumber.toUpperCase().trim();
			carNumber = carNumber.replace("I", "1").replace("O", "0");
			String mobile = RequestUtil.getString(request, "mobile");
			if(!carNumber.equals("")){
				if(mobile.equals("")){//验证车牌号
					Long count = daService.getLong("select count(id) from car_info_tb where car_number=?", new Object[]{carNumber});
					if(count>0){
						AjaxUtil.ajaxOutput(response, "-1");
						return null;
					}
				}else {//注册车主，同时只验证手机号
					Long count = daService.getLong("select count(id) from user_info_tb where mobile=? and auth_flag=? ", new Object[]{mobile,4});
					if(count>0){
						AjaxUtil.ajaxOutput(response, "-1");
						return null;
					}
					//写用户数据
					List<Map<String, Object>> sqlList = new ArrayList<Map<String,Object>>();
					//用户信息
					Map<String, Object> userSqlMap = new HashMap<String, Object>();
					//下一个用户账号
					Long key = daService.getkey("seq_user_info_tb");
					userSqlMap.put("sql", "insert into user_info_tb (id,nickname,strid,mobile,reg_time,comid,auth_flag,recom_code,media) " +
							"values(?,?,?,?,?,?,?,?,?)");
					userSqlMap.put("values", new Object[]{key,"车主","zlduser"+key,mobile,System.currentTimeMillis()/1000,0,4,uin,999});
					sqlList.add(userSqlMap);
					//车牌信息
					Map<String, Object> carSqlMap = new HashMap<String, Object>();
					carSqlMap.put("sql", "insert into car_info_tb(uin,car_number) values(?,?)");
					carSqlMap.put("values", new Object[]{key,carNumber});
					sqlList.add(carSqlMap);
					//推荐信息
					Map<String, Object> recomSqlMap = new HashMap<String, Object>();
					recomSqlMap.put("sql", "insert into recommend_tb (pid,nid,type,state,create_time) values(?,?,?,?,?)");
					recomSqlMap.put("values", new Object[]{uin,key,0,0,System.currentTimeMillis()/1000});
					sqlList.add(recomSqlMap);
					
					boolean ret = daService.bathUpdate(sqlList);
					if(!ret){
						AjaxUtil.ajaxOutput(response, "-2");
						return null;
					}else {//发给车主30元停车券
						//推荐车主，收费员积1分
						//logService.updateScroe(5, uin, comId);
						Long ntime = System.currentTimeMillis()/1000;
						int result=publicMethods.backNewUserTickets(ntime, key);// daService.bathInsert(tsql, values, new int[]{4,4,4,4,4});
						if(result==0){
							String bsql = "insert into bonus_record_tb (bid,ctime,mobile,state,amount) values(?,?,?,?,?) ";
							Object [] values = new Object[]{999,ntime,mobile,0,10};//登记为未领取红包，登录时写入停车券表（判断是否是黑名单后）
							logger.info(">>>>>>>>收费员推荐车主("+mobile+")，发30元停车券，写入红包记录表，登录时返还："+daService.update(bsql,values));
						
						}
						int	eb = daService.update("insert into user_profile_tb (uin,low_recharge,limit_money,auto_cash," +
								"create_time,update_time) values(?,?,?,?,?,?)", 
								new Object[]{key,10,25,1,ntime,ntime});
						
						logger.info("账户:"+uin+",手机："+mobile+",新注册用户(车场收费员推荐)，写入红包停车券"+result+"条,自动支付写入："+eb);
						String mesg ="五笔电子停车费，三笔来自停车宝。停车天天有优惠，8元钱停5次车，下载地址： http://t.cn/RZJ4UAv 【停车宝】";
//						SendMessage.sendMultiMessage(mobile, mesg);
					}
				}
			}else {
				AjaxUtil.ajaxOutput(response, "0");
				return null;
			}
			AjaxUtil.ajaxOutput(response, "1");
			//http://127.0.0.1/zld/collectorrequest.do?action=reguser&token=6ed161cde6c7149de49d72719f2eb39b&mobile=15801482645&carnumber=123456
			return null;
		}else if(action.equals("regcolmsg")){//短信推荐车场
			System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>短信推荐车场+"+uin);
			Long tid = daService.getkey("seq_transfer_url_tb");
			//String url = "http://192.168.199.240/zld/turl?p="+tid;
			String url = "http://s.tingchebao.com/zld/turl?p="+tid;
			int result = daService.update("insert into transfer_url_tb(id,url,ctime,state) values (?,?,?,?)",
					new Object[]{tid,"regparker.do?action=toregpage&recomcode="+uin,
							System.currentTimeMillis()/1000,0});
			
			if(result!=1)
				url="推荐失败!";
			if(out.equals("json")){
				AjaxUtil.ajaxOutput(response, "{\"url\":\""+url+"\"}");
			}else {
				AjaxUtil.ajaxOutput(response,url);
			}
			//http://127.0.0.1/zld/collectorrequest.do?action=regcarmsg&token=6ed161cde6c7149de49d72719f2eb39b
			return null;
		}else if(action.equals("recominfo")){//车场收费员推荐记录
			Integer rtype = RequestUtil.getInteger(request, "type", 0);//0:车主，1:车场
			List<Map<String, Object>> list =null;
			if(rtype==0){
				list = daService.getAll("select c.nid,u.mobile uin,c.state,c.money from recommend_tb c left join user_info_tb u on c.nid=u.id where pid=? and c.type=? order by c.id desc ",new Object[]{uin,rtype});
			}else {
				list  =daService.getAll("select nid uin,state,money from recommend_tb where pid=? and type=? order by id desc",new Object[]{uin,rtype});
			}
			if(list!=null&&!list.isEmpty()){
				for(Map<String, Object> map :list){
					Integer state = (Integer)map.get("state");
					if(state==null||state!=1)
						continue;
					Double money = StringUtils.formatDouble(map.get("money"));
					if(rtype==0&&money==0)
						map.put("money", 5);
					else if(rtype==1&&money==0){
						map.put("money", 30);
					}
				}
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			//http://127.0.0.1/zld/collectorrequest.do?action=recominfo&token=40ffacdad78acf0c43e0aabae9712602
			return null;
		}else if(action.equals("getmesg")){
			Long maxid = RequestUtil.getLong(request, "maxid", -1L);
			Integer page = RequestUtil.getInteger(request, "page", 1);
			if(maxid>-1){
				Long count = daService.getLong("select count(ID) from parkuser_message_tb where uin=? and id>?", new Object[]{uin,maxid});
				AjaxUtil.ajaxOutput(response, count+"");
			}else{
				List<Object> params = new ArrayList<Object>();
				params.add(uin);
				List<Map<String, Object>> list = daService.getAll("select * from parkuser_message_tb where uin=? order by id desc",
						params,page,10);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
			}
			//http://127.0.0.1/zld/carowner.do?action=getmesg&token=&page=-1&maxid=0
			return null;
		}else if(action.equals("getincome")){//照牌收费员一段时间内现金收入金额和手机收入金额
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long logonTime = RequestUtil.getLong(request, "logontime", -1L);//20150618加上传入登录时间 
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long b = System.currentTimeMillis()/1000;
			Long e =b;
			if(logonTime!=-1){
				b = logonTime;
				//logger.info(b);
				b = (logonTime/60)*60;
			}else {
				if(btime.equals("")){
					btime = nowtime;
				}
				if(etime.equals("")){
					etime = nowtime;
				}
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			}
			//logger.info(b);
			String sql = "select sum(total) money,pay_type from order_tb where uid=? and c_type=? and state=? and end_time between ? and ? group by pay_type order by pay_type desc ";
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			list = daService.getAll(sql, new Object[]{uin,3,1,b,e});
			Map<String, Object> map = new HashMap<String, Object>();
			for(Map<String, Object> map2 : list){
				Integer pay_type = (Integer)map2.get("pay_type");
				if(pay_type == 2){//手机支付
					map.put("mobilepay", map2.get("money"));
				}else if(pay_type == 1){//现金支付
					map.put("cashpay", map2.get("money"));
				}
			}
			//logger.info(map);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://127.0.0.1/zld/collectorrequest.do?action=getincome&token=15d1bb15b8dcb99aa7dbe0adc9797162&btime=2012-12-28
		}else if(action.equals("getnewincome")){//照牌收费员一段时间内现金收入金额和手机收入金额
			String btime = RequestUtil.processParams(request, "btime");
			String etime = RequestUtil.processParams(request, "etime");
			Long logonTime = RequestUtil.getLong(request, "logontime", -1L);//20150618加上传入登录时间 
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			Long worksiteid = RequestUtil.getLong(request, "worksite_id",-1L);
			Long comid = RequestUtil.getLong(request, "comid",-1L);
			String nowtime= df2.format(System.currentTimeMillis());
			Long b = System.currentTimeMillis()/1000;
			Long e =b;
			if(logonTime!=-1){
				b = logonTime;
				//logger.info(b);
				b = (logonTime/60)*60;
			}else {
				if(btime.equals("")){
					btime = nowtime;
				}
				if(etime.equals("")){
					etime = nowtime;
				}
				b = TimeTools.getLongMilliSecondFrom_HHMMDD(btime)/1000;
				e =  TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(etime+" 23:59:59");
			}
			Map<String, Object> map = new HashMap<String, Object>();
//			if(worksiteid!=-1){
				Map ret = daService.getMap(
						"select * from parkuser_work_record_tb where end_time is null and uid=? and worksite_id = ?",
						new Object[] {uin,worksiteid});
				if(ret!=null){
					b = Long.valueOf(ret.get("start_time")+"");
					map.put("start_time", b);
					if(ret.get("end_time")==null){
						e=Long.MAX_VALUE;
					}else{
						e=Long.valueOf(ret.get("end_time")+"");
					}
				}else{
					Long bLong = System.currentTimeMillis()/1000;
					int workret = daService.update("insert into parkuser_work_record_tb(start_time,uid,worksite_id) values(?,?,?)",
							new Object[]{bLong,uin,worksiteid});
					if(workret==1){
						map.put("start_time", b);
					}
					logger.error("collectorlogin>>>>>:通道车场，上班：uid:"+uin+",worksiteid:"+worksiteid+",r:"+workret);
				}
//			}
			Map cash = daService.getMap("select sum(b.amount)money from order_tb a,parkuser_cash_tb b where  a.end_time between" +
					" ? and ? and a.state=? and a.uid=? and a.id=b.orderid and b.type=?",new Object[]{b,e,1,uin,0});
			Double money =0d;
			if(cash!=null&&cash.get("money")!=null){
				money = Double.valueOf(cash.get("money")+"");
			}
			map.put("cashpay", StringUtils.formatDouble(money));
			Double pmoney = 0d;
			
			Map park = daService.getMap( "select sum(amount) total from park_account_tb where create_time between ? and ? " +
					" and type= ? and source=? and uid=? and comid=? ",new Object[]{b,e,0,0,uin,comid});
			if(park!=null&&park.get("total")!=null)
				pmoney += Double.valueOf(park.get("total")+"");
			
			Map parkuser = daService.getMap( "select sum(amount) total from parkuser_account_tb where create_time between ? and ? " +
					" and type= ? and uin = ? and target =?",new Object[]{b,e,0,uin,4});
			if(parkuser!=null&&parkuser.get("total")!=null)
				pmoney += Double.valueOf(parkuser.get("total")+"");
			
			map.put("mobilepay", StringUtils.formatDouble(pmoney));
			
			//logger.info(map);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(map));
			//http://127.0.0.1/zld/collectorrequest.do?action=getincome&token=15d1bb15b8dcb99aa7dbe0adc9797162&btime=2012-12-28
		}else if(action.equals("querycarpics")){//车场一个月的车牌缓存
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select distinct(car_number) from order_tb where comid=? and c_type=? and create_time between ? and ? ";
			SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd");
			String nowtime= df2.format(System.currentTimeMillis());
			Long endTime = TimeTools.getLongMilliSecondFrom_HHMMDDHHmmss(nowtime+" 23:59:59");
			Long beginTime = endTime - 30*24*60*60;
			list = daService.getAll(sql, new Object[]{comId,2,beginTime,endTime});
			String result = StringUtils.createJson(list);
			AjaxUtil.ajaxOutput(response, result);
			return null;
			//http://192.168.10.239/zld/collectorrequest.do?action=querycarpics&token=
		}else if(action.equals("incomanly")){//收入统计
			//0自己,1车场
			Integer acctype = RequestUtil.getInteger(request, "acctype", 0);
			//0停车费，1返现 ，2奖金,3 全部
			Integer income = RequestUtil.getInteger(request, "incom", 0);
			//0今天，1昨天，2本周，3本月
			Integer datetype = RequestUtil.getInteger(request, "datetype", 0);
			Integer page = RequestUtil.getInteger(request, "page", 1);
			page = page<1?1:page;
			List<Object> params = new ArrayList<Object>();
			
			String sql = "";
			String totalSql = "";
			if(acctype==0){//0自己,1车场
				sql +="select amount money,type mtype,create_time," +
						"remark note,target from parkuser_account_tb where uin=? ";
				totalSql = "select sum(amount) total from parkuser_account_tb where uin=?";
				params.add(uin);
			}else if(acctype==1){
				sql +=" select create_time ,remark r,amount money,type mtype  from park_account_tb where comid=? ";
				totalSql = "select sum(amount) total from park_account_tb where comid=?";
				params.add(comId);
			}
			if(income==0){//0停车费
				if(acctype==0){//0自己,1车场
					sql +=" and type=? and target=? ";
					totalSql +=" and type=? and target=? ";
					params.add(0);
					params.add(4);
				}else if(acctype==1){
					sql +=" and type= ? ";
					totalSql +=" and type= ? ";
					params.add(0);
				}
			}else if(income==1){//1返现 
				if(acctype==0){//0自己,1车场
					sql +=" and type=? and target=? and amount =? ";
					totalSql +=" and type=? and target=? and amount =? ";
					params.add(0);
					params.add(3);
					params.add(2d);
				}else if(acctype==1){
					sql +=" and type= ? ";
					totalSql +=" and type= ? ";
					params.add(2);
				}
			}else if(income==2){//2奖金
				if(acctype==0){//0自己,1车场
					sql +=" and type=? and target=? and amount >? ";
					totalSql +=" and type=? and target=? and amount >? ";
					params.add(0);
					params.add(3);
					params.add(2d);
				}else if(acctype==1){
					sql +=" and type= ? ";
					totalSql +=" and type= ? ";
					params.add(3);
				}
			}
			
			Long btime = TimeTools.getToDayBeginTime();
			Long etime = btime+24*60*60;
			if(datetype==1){
				etime = btime ;
				btime = btime-24*60*60;
			}else if(datetype==2){
				btime = TimeTools.getWeekStartSeconds();
			}else if(datetype==3){
				btime = TimeTools.getMonthStartSeconds();
			}
			sql +=" and create_time between ? and ? order by create_time desc";
			totalSql +=" and create_time between ? and ? ";
			params.add(btime);
			params.add(etime);
//			System.out.println(sql);
//			System.out.println(totalSql);
			System.err.println(">>>>>>incomanly:"+sql+":"+params);
			Map totalMap = daService.getMap(totalSql, params);
			List reslutList = daService.getAll(sql, params,page,20);	
			setAccountList(reslutList,acctype);
			String total = totalMap.get("total")+"";
			if(total.equals("null"))
				total = "0.0";
			String reslut =  "{\"total\":\""+total+"\",\"info\":"+StringUtils.createJson(reslutList)+"}";
			System.err.println(reslut);
			AjaxUtil.ajaxOutput(response, reslut);
			return null;
			//http://192.168.199.240/zld/collectorrequest.do?action=incomanly&acctype=1&incom=2&datetype=2&page=1&token=6d5d6a1bd45b5dafd2294e99cf9c91c9
		}else if(action.equals("invalidorders")){
			Long invalid_order = RequestUtil.getLong(request, "invalid_order", 0L);
			int result = daService.update("update com_info_tb set invalid_order=invalid_order+? where id=?", new Object[]{invalid_order, comId});
			AjaxUtil.ajaxOutput(response, result + "");
			return null;
			//http://192.168.199.239/zld/collectorrequest.do?action=invalidorders&invalid_order=-1&token=198f697eb27de5515e91a70d1f64cec7
		}else if(action.equals("bindworksite")){//收费员绑定工作站
			Long wid = RequestUtil.getLong(request, "wid", -1L);
			logger.info(">>>>disbind,wid:"+wid);
			out="json";
			int ret =0;
			if(uin!=-1){
				if(wid==-1){//解绑
					ret = daService.update("delete from user_worksite_tb where uin = ? ", new Object[]{uin});
					logger.info(">>>>disbind  收费员解绑   worksite,user:"+uin+"ret:"+ret);
					ret = 1;
				}else {//绑定
					//绑定前先从其它工作站上下岗
					ret = daService.update("delete from user_worksite_tb where uin = ?  ", new Object[]{uin});
					logger.info(">>>>bind 收费员上岗，删除原来在的工作站:"+ret);
					//删除原绑定收费员
					Map oldMap = daService.getMap("select uin from user_worksite_tb where worksite_id=? ", new Object[]{wid});
					if(oldMap!=null){
						ret = daService.update("delete from user_worksite_tb where worksite_id = ?  ", new Object[]{wid});
						if(ret>0){
							Long uid =(Long)oldMap.get("uin");
							if(uid!=null&&uid>0)
								ret = daService.update("insert into order_message_tb(message_type,state,uin)" +
									" values(?,?,?)", new Object[]{4,0,uid});//发消息给收费员，通知其已不在岗
							logger.info(">>>>disbind 收费员上岗，原收费员下岗  worksite,delete old user:"+uid+",ret:"+ret);
						}
					}
					//绑定收费员
					ret = daService.update("insert into user_worksite_tb (worksite_id,uin) values(?,?)",new Object[]{wid,uin});
					logger.info(">>>>bind worksite,收费员上岗  bind new user:"+uin+", ret="+ret);
				}
			}
			infoMap.put("result", ret+"");
			//collectorrequest.do?action=bindworksite&wid=&token=198f697eb27de5515e91a70d1f64cec7
		}else if(action.equals("gooffwork")){
			//收费员下班
			Long  worksiteid =RequestUtil.getLong(request, "worksiteid",-1L);
			long endtime = System.currentTimeMillis() / 1000;
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:00");
//			String d = sdf.format(System.currentTimeMillis());
//			long endtime = sdf.parse(d).getTime()/1000;
			List user = daService.getAll("select * from parkuser_work_record_tb where worksite_id=? and uid = ? and end_time is null", new Object[] {
					worksiteid, uin });
			int result = daService
					.update("update parkuser_work_record_tb set end_time=? where worksite_id = ? and uid = ? and end_time is null",
							new Object[] { endtime,
									worksiteid, uin });
			if(result==1){
				uploadWork((Long)((Map)user.get(0)).get("id"));
			}
			logger.info("gooffwork>>>>>下班result："+result+",uin:"+uin+",worksiteid:"+worksiteid);
			if(result > 0){
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
			}
			return null;
		}else if(action.equals("akeycheckaccount")){
			String ret = "{";
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000+60;
			Double parkmoney = 0d;
			Double parkusermoney = 0d;
			Double cashmoney = 0d;
			Long ordertotal = 0L;
			Long epayordertotal = 0L;
			Double ordertotalmoney = 0d;
			Double epaytotalmoney = 0d;
			//一键对账   1:车场管理员    2收费员
			Map park = daService.getMap( "select sum(amount) total from park_account_tb where create_time between ? and ? " +
					" and type <> ? and uid=? and comid=? ",new Object[]{b,e,1,uin,comId});
			if(park!=null&&park.get("total")!=null)
				parkmoney = Double.valueOf(park.get("total")+"");//车场账户收入（不计来源）
			
			Map parkuser = daService.getMap( "select sum(amount) total from parkuser_account_tb where create_time between ? and ? " +
					" and type= ? and uin = ? ",new Object[]{b,e,0,uin});
			if(parkuser!=null&&parkuser.get("total")!=null)
				parkusermoney = Double.valueOf(parkuser.get("total")+"");//收费员账户收入（不计来源）
			
			Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
					" and uin=? ",new Object[]{b,e,uin});
			if(cash!=null&&cash.get("total")!=null)
				cashmoney = Double.valueOf(cash.get("total")+"");//收费员现金收入
			Map ordertotalMap = daService.getMap( "select count(*) scount,sum(total) total from order_tb where end_time between ? and ? " +
					" and uid=? and state=?",new Object[]{b,e,uin,1});//总的订单
			if(ordertotalMap!=null){
				if(ordertotalMap.get("total")!=null)
					ordertotalmoney = Double.valueOf(ordertotalMap.get("total")+"");
				if(ordertotalMap.get("scount")!=null)
					ordertotal = Long.valueOf(ordertotalMap.get("scount")+"");
			}
			Map epayordertotalMap = daService.getMap( "select count(*) scount,sum(total) total from order_tb where end_time between ? and ? " +
					" and uid=? and c_type=? and state=?",new Object[]{b,e,uin,4,1});//直付订单
			if(epayordertotalMap!=null){
				if(epayordertotalMap.get("total")!=null)
					epaytotalmoney = Double.valueOf(epayordertotalMap.get("total")+"");
				if(epayordertotalMap.get("scount")!=null)
					epayordertotal = Long.valueOf(epayordertotalMap.get("scount")+"");
			}
			ret+="\"totalmoney\":\""+StringUtils.formatDouble((parkmoney+parkusermoney+cashmoney))+"\",\"mobilemoney\":\""+StringUtils.formatDouble((parkmoney+parkusermoney))+
			"\",\"cashmoney\":\""+StringUtils.formatDouble(cashmoney)+"\",\"mycount\":\""+StringUtils.formatDouble(parkusermoney)+"\",\"parkaccout\":\""+StringUtils.formatDouble(parkmoney)+"\",\"timeordercount\":\""+
			(ordertotal-epayordertotal)+"\",\"timeordermoney\":\""+StringUtils.formatDouble((ordertotalmoney-epaytotalmoney))+
			"\",\"epayordercount\":\""+epayordertotal+"\",\"epaymoney\":\""+StringUtils.formatDouble(epaytotalmoney)+"\"}";
			logger.info("akeycheckaccount>>>："+ret);
			AjaxUtil.ajaxOutput(response, ret);
			return null;
		}else if(action.equals("getparkdetail")){
			//管理员查看整个车场的停车费明细
			String ret="{";
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000;
//			long b=1436544000;,e=1435916665;
			Double mmoney = 0d;
			Double cashmoney = 0d;
			Double total = 0d;
			if(authFlag==1){
				ArrayList list1 = new ArrayList();
				ArrayList list2 = new ArrayList();
				list1.add(b);
				list1.add(e);
				list1.add(0);
				list1.add(0);
				list1.add(comId);
				list2.add(b);
				list2.add(e);
				list2.add(0);
				list2.add(4);
				list2.add("停车费%");
				list2.add(comId);
				List park = daService.getAllMap( "select b.nickname, sum(a.amount) total,a.uid from park_account_tb a,user_info_tb b where a.create_time between ? and ? " +
						" and a.type= ? and a.source=? and a.comid=? and a.uid=b.id group by a.uid,b.nickname ",list1);//车场账户停车费
				
				List parkuser = daService.getAllMap( "select b.nickname,sum(a.amount) total,a.uin uid from parkuser_account_tb a,user_info_tb b where a.create_time between ? and ? " +
						" and a.type= ? and a.target=? and a.remark like ? and a.uin=b.id and a.uin in (select id from user_info_tb where comid=?) group by a.uin,b.nickname",list2);//收费员账户停车费
				TreeSet<Long> set = new TreeSet<Long>();
				if(park!=null&&park.size()>0){
					if(parkuser!=null&&parkuser.size()>0)
						park.addAll(parkuser);
					for (int i = 0; i < park.size(); i++) {
//						System.out.println(park.size());
						Map obj1 = (Map)park.get(i);
						Long id1 = Long.valueOf(obj1.get("uid")+"");
						set.add(id1);
						Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
								" and uin=? ",new Object[]{b,e,id1});
						if(cash!=null&&cash.get("total")!=null){
							double cmoney = Double.valueOf(cash.get("total")+"");
							cashmoney+=cmoney;
							obj1.put("cash",StringUtils.formatDouble(cmoney ));//收费员现金收入
						}else{
							obj1.put("cash",0.00);//收费员现金收入
						}
						double ummoney = Double.valueOf(obj1.get("total")+"");
						mmoney+=ummoney;
						for (int j = i+1; j < park.size(); j++) {
//							System.out.println(park.size());
							Map obj2 = (Map)park.get(j);
							long id2 = Long.valueOf(obj2.get("uid")+"");
							if(id1==id2){
								double total1 =Double.valueOf(obj2.get("total")+"");
								mmoney+=total1;
								obj1.put("total", StringUtils.formatDouble(total1+ummoney));
								park.remove(j);
							}
						}
					}
				}else{
					park = parkuser;
					for (Object object : parkuser) {
						Map obj = (Map)object;
						Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
								" and uin=? ",new Object[]{b,e,Long.valueOf(obj.get("uid")+"")});
						set.add(Long.valueOf(obj.get("uid")+""));
						if(cash!=null&&cash.get("total")!=null){
							double cmoney = Double.valueOf(cash.get("total")+"");
							cashmoney+=cmoney;
						}
						if(obj.get("total")!=null)
							mmoney+=Double.valueOf(obj.get("total")+"");
					}
				}
				List user = daService.getAll( "select id, nickname from user_info_tb where comid=?",new Object[]{comId});
				for (Object object : user) {
					Map obj = (Map)object;
					if(set.add(Long.valueOf(obj.get("id")+""))){
						Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
								" and uin=? ",new Object[]{b,e,Long.valueOf(obj.get("id")+"")});
						if(cash!=null&&cash.get("total")!=null){
							double cmoney = Double.valueOf(cash.get("total")+"");
							cashmoney+=cmoney;
							Map tmap = new TreeMap();
							tmap.put("nickname",obj.get("nickname"));
							tmap.put("total",0.0);
							tmap.put("uid",obj.get("id"));
							tmap.put("cash",StringUtils.formatDouble(cmoney ));//收费员现金收入
							park.add(tmap);
						}
					}
				}
				total=cashmoney+mmoney;
				String detail = StringUtils.createJson(park);
				ret+="\"total\":\""+StringUtils.formatDouble(total)+"\",\"mmoeny\":\""+StringUtils.formatDouble(mmoney)+
				"\",\"cashmoney\":\""+StringUtils.formatDouble(cashmoney)+"\",\"detail\":"+detail+"}";
			}else{
				//你没有权限查看
				AjaxUtil.ajaxOutput(response, "-1");
			}
			logger.info("getparkdetail>>>>："+ret);
			AjaxUtil.ajaxOutput(response, ret);
			return null;
		}else if(action.equals("countprice")){
			Long btime = RequestUtil.getLong(request, "btime", -1L);
			Long etime = RequestUtil.getLong(request, "etime", -1L);
			Map<String,Object> info = new HashMap<String,Object>();
			String ret = publicMethods.getPrice(btime, etime, comId, 0);
			info.put("total", ret);
			ret = StringUtils.createJson(info);
			AjaxUtil.ajaxOutput(response, ret);
			return null;
		}else if(action.equals("rewardscore")){
			Double remainscore = 0d;//剩余积分
			Long rank = 0L;//排行榜
			Double todayscore = 0d;//今日积分
			Long btime = TimeTools.getToDayBeginTime();
			Map<String, Object> scoreMap = daService
					.getMap("select reward_score from user_info_tb where id=? ",
							new Object[] { uin });
			if(scoreMap != null){
				remainscore = Double.valueOf(scoreMap.get("reward_score") + "");
			}
			Long scoreCount = daService.getLong("select count(*) from reward_account_tb where create_time> ? and type=? and uin=? ",
							new Object[] { btime, 0, uin });
			if(scoreCount > 0){
				List<Map<String, Object>> scoreList = daService
						.getAll("select uin,sum(score) score from reward_account_tb where create_time> ? and type=? group by uin order by score desc ",
								new Object[] { btime, 0 });
				for(Map<String, Object> map : scoreList){
					Long uid = (Long)map.get("uin");
					rank++;
					if(uid.intValue() == uin.intValue()){
						todayscore = Double.valueOf(map.get("score") + "");
						break;
					}
				}
			}
			infoMap.put("todayscore", todayscore);
			infoMap.put("rank", rank);
			infoMap.put("remainscore", remainscore);
			infoMap.put("ticketurl", "http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=208427604&idx=1&sn=a3de34b678869c4bbe54547396fcb2a3#rd");
			infoMap.put("scoreurl", "http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=208445618&idx=1&sn=b4d99d5233921ae53c847165c62dec2b#rd");
			AjaxUtil.ajaxOutput(response,StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rewardscore&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("rscorerank")){//积分排行榜
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long btime = TimeTools.getToDayBeginTime();
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select uin,sum(score) score from reward_account_tb where create_time>=? and type=? group by uin order by score desc ";
			String countsql = "select count(distinct uin) from reward_account_tb where create_time>=? and type=? ";
			params.add(btime);
			params.add(0);
			Long total = daService.getCount(countsql, params);
			if(total > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setinfo(list, pageNum, pageSize);
			}
			String result = "{\"count\":"+total+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rscorerank&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("rewardrank")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String monday = StringUtils.getMondayOfThisWeek();
			Long btime = TimeTools.getLongMilliSecondFrom_HHMMDD(monday)/1000;
			String sql = "select uid uin,sum(money) money from parkuser_reward_tb where ctime>=? group by uid order by money desc ";
			String countsql = "select count(distinct uid) from parkuser_reward_tb where ctime>=? ";
			params.add(btime);
			Long total = daService.getCount(countsql, params);
			if(total > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setinfo(list, pageNum, pageSize);
			}
			String result = "{\"count\":"+total+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rewardrank&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("bonusinfo")){
			String bonusinfo = CustomDefind.SENDTICKET;
			JSONArray jsonArray = JSONArray.fromObject(bonusinfo);
			for(int i=0; i<jsonArray.size(); i++){
				JSONObject jsonObject = jsonArray.getJSONObject(i);
				int type = jsonObject.getInt("type");
				int bmoney = jsonObject.getInt("bmoney");
				double score = jsonObject.getDouble("score");
				if(type == 1 && bmoney == 5){
					Long btime = TimeTools.getToDayBeginTime();
					Long count = daService.getLong("select count(*) from reward_account_tb r,ticket_tb t where r.ticket_id=t.id and r.type=? and r.target=? and r.create_time>? and t.money=? and r.uin=? ",
									new Object[] { 1, 2, btime, 5, uin });
					score = score * (count + 1);
					logger.info("今日发的五元券个数count:"+count+",uid:"+uin+",today:"+btime+",下一个花费积分：score："+score);
					jsonObject.put("score", score);
					if(count >=10){
						jsonObject.put("limit", 1);
					}else{
						jsonObject.put("limit", 0);
					}
					break;
				}
			}
			logger.info("bonusinfo:"+jsonArray.toString()+",uin:"+uin);
			AjaxUtil.ajaxOutput(response,jsonArray.toString());
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=bonusinfo&token=67579fd93b96ad32ced2584b54d8454f
		}else if(action.equals("sendticket")){
			Integer bmoney = RequestUtil.getInteger(request, "bmoney", 0);//金额
			Double score = RequestUtil.getDouble(request, "score", 0d);//消耗积分
			String uins = RequestUtil.processParams(request, "uins");//车主账号
			logger.info("sendticket>>>收费员:"+uin+",bmoney:"+bmoney+",score:"+score+",uins:"+uins);
			String ids[] = uins.split(",");
			if(ids.length == 0 || uins.length() == 0){
				AjaxUtil.ajaxOutput(response, "-2");//未选择车主
				return null;
			}
			Long ctime = System.currentTimeMillis()/1000;
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Map<String, Object> comMap = daService.getMap(
					"select company_name from com_info_tb where id=? ",
					new Object[] { comId });
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			//积分账户
			Map<String, Object> scoreSqlMap = new HashMap<String, Object>();
			Long exptime = ctime + 24*60*60;
			for(int i = 0; i<ids.length; i++){
				//写券
				Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
				//积分明细
				Map<String, Object> scoreAccountSqlMap = new HashMap<String, Object>();
				
				Long cuin = Long.valueOf(ids[i]);
				String carNumber = publicMethods.getCarNumber(cuin);
				Long ticketId = daService.getkey("seq_ticket_tb");
				
				ticketSqlMap.put("sql", "insert into ticket_tb (id,create_time,limit_day,money,state,uin,type,comid) values(?,?,?,?,?,?,?,?)");
				ticketSqlMap.put("values", new Object[]{ticketId,TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+16*24*60*60-1,bmoney,0,cuin,1,comId});
				bathSql.add(ticketSqlMap);
				
				scoreAccountSqlMap.put("sql", "insert into reward_account_tb (uin,score,type,create_time,remark,target,ticket_id) values(?,?,?,?,?,?,?)");
				scoreAccountSqlMap.put("values", new Object[]{uin,score,1,ctime,"停车券 "+carNumber,2,ticketId});
				bathSql.add(scoreAccountSqlMap);
			}
			Double allscore = StringUtils.formatDouble(score * ids.length);
			logger.info("sendticket>>>收费员:"+uin+",allscore:"+allscore);
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < allscore){
				AjaxUtil.ajaxOutput(response, "-3");//积分不足
				logger.info("sendticket>>>打赏积分不足，收费员:"+uin+",allscore:"+allscore+",reward_score:"+reward_score);
				return null;
			}
			if(allscore > 0 && bathSql.size() > 0){
				scoreSqlMap.put("sql", "update user_info_tb set reward_score=reward_score-? where id=? ");
				scoreSqlMap.put("values", new Object[]{allscore, uin});
				bathSql.add(scoreSqlMap);
			}
			boolean b = daService.bathUpdate(bathSql);
			logger.info("sendticket>>>收费员："+uin+",b:"+b);
			if(b){
				for(int i = 0;i<ids.length; i++){
					Long cuin = Long.valueOf(ids[i]);
					logService.insertUserMesg(5, cuin,"我是停车费收费员" + userMap.get("nickname") + "，给您赠送"
									+ bmoney + "元" + comMap.get("company_name")
									+ "专用券，邀您来我车场停车。", "停车券提醒");
				}
				sendWXMsg(ids, userMap, comMap, bmoney);
				AjaxUtil.ajaxOutput(response, "1");
			}else{
				AjaxUtil.ajaxOutput(response, "-1");
			}
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=sendticket&token=5286f078c6d2ecde9b30929f77771149&bmoney=3&score=1&uins=21616,21577,21554
		}else if(action.equals("sendbonus")){
			Integer bmoney = RequestUtil.getInteger(request, "bmoney", 0);//金额
			Integer bnum = RequestUtil.getInteger(request, "bnum", 0);//个数
			Double score = RequestUtil.getDouble(request, "score", 0d);//消耗积分
			logger.info("sendbonus>>>收费员："+uin+",bmoney:"+bmoney+",bnum:"+bnum+",score:"+score);
			Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comId});
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < score){
				infoMap.put("result", -3);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//积分不足
				logger.info("sendticket>>>打赏积分不足，收费员:"+uin+",score:"+score+",reward_score:"+reward_score);
				return null;
			}
			Long ctime = System.currentTimeMillis()/1000;
			Long exptime = ctime + 24*60*60;
			Long bonusId = daService.getkey("seq_order_ticket_tb");
			int result = daService.update("insert into order_ticket_tb (id,uin,order_id,money,bnum,ctime,exptime,bwords,type) values(?,?,?,?,?,?,?,?,?)",
							new Object[] { bonusId, uin, -1, bmoney, bnum, ctime, exptime, "祝您一路发发发!", 2 });
			logger.info("sendbonus>>>:收费员"+uin+",result:"+result);
			if(result == 1){
				infoMap.put("result", 1);
				infoMap.put("bonusid", bonusId);
				infoMap.put("cname", comMap.get("company_name"));
			}else{
				infoMap.put("result", -1);
			}
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=sendbonus&token=5286f078c6d2ecde9b30929f77771149&bmoney=12&bnum=8&score=1
		}else if(action.equals("sendsuccess")){
			Long bonusId = RequestUtil.getLong(request, "bonusid", -1L);
			Double score = RequestUtil.getDouble(request, "score", 15d);
			Long ctime = System.currentTimeMillis()/1000;
			logger.info("sendsuccess>>>红包发送成功回调:bonusid:"+bonusId+",uin:"+uin+",score:"+score);
			if(bonusId != -1){
				Long count = daService.getLong("select count(*) from reward_account_tb where orderticket_id=? ", new Object[]{bonusId});
				logger.info("sendsuccess>>>红包发送成功回调:bonusid:"+bonusId+",uin:"+uin+",count:"+count+",score:"+score);
				if(count == 0){
					Map<String, Object> userMap = daService.getMap(
							"select id,nickname,reward_score from user_info_tb where id=? ",
							new Object[] { uin });
					Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
					logger.info("sendsuccess>>>红包发送成功回调:bonusid:"+bonusId+",uin:"+uin+",score:"+score+",剩余积分reward_score:"+reward_score+",此次消耗积分score:"+score);
					if(reward_score > score){
						List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
						//积分账户
						Map<String, Object> scoreSqlMap = new HashMap<String, Object>();
						//积分明细
						Map<String, Object> scoreAccountSqlMap = new HashMap<String, Object>();
						
						scoreAccountSqlMap.put("sql", "insert into reward_account_tb (uin,score,type,create_time,remark,target,orderticket_id) values(?,?,?,?,?,?,?)");
						scoreAccountSqlMap.put("values", new Object[]{uin,score,1,ctime,"红包 ",1,bonusId});
						bathSql.add(scoreAccountSqlMap);
						
						scoreSqlMap.put("sql", "update user_info_tb set reward_score=reward_score-? where id=? ");
						scoreSqlMap.put("values", new Object[]{score, uin});
						bathSql.add(scoreSqlMap);
						boolean b = daService.bathUpdate(bathSql);
						logger.info("sendsuccess>>>红包发送成功回调:bonusid:"+bonusId+",uin:"+uin+",b:"+b);
						if(b){
							AjaxUtil.ajaxOutput(response, "1");
							return null;
						}
					}
				}
			}
			AjaxUtil.ajaxOutput(response, "-1");
			return null;
		}else if(action.equals("rewardlist")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			Long btime = TimeTools.getToDayBeginTime() - 6 * 24 * 60 * 60;
			params.add(uin);
			params.add(btime);
			String sql = "select uin,count(*) rcount,sum(money) rmoney from parkuser_reward_tb where uid=? and ctime>? group by uin order by rcount desc";
			String countsql = "select count(distinct uin) from parkuser_reward_tb where uid=? and ctime>? ";
			Long count = daService.getCount(countsql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setCarNumber(list);
			}
			String result = "{\"count\":"+count+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=rewardlist&token=116a87809926db5c477a9a1a58488ec1
		}else if(action.equals("parkinglist")){
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			Long btime = TimeTools.getToDayBeginTime() - 6* 24 * 60 *60;
			List<Object> params = new ArrayList<Object>();
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			String sql = "select uin,count(*) pcount from order_tb where state=? and uid=? and end_time>? and uin is not null group by uin order by pcount desc";
			String countsql = "select count(distinct uin) from order_tb where state=? and uid=? and end_time>? and uin is not null ";
			params.add(1);
			params.add(uin);
			params.add(btime);
			Long count = daService.getCount(countsql, params);
			if(count > 0){
				list = daService.getAll(sql, params, pageNum, pageSize);
				setCarNumber(list);
			}
			String result = "{\"count\":"+count+",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response,result);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=parkinglist&token=116a87809926db5c477a9a1a58488ec1
		}else if(action.equals("sweepticket")){
			Double score = RequestUtil.getDouble(request, "score", 0d);//消耗积分
			Integer bmoney = RequestUtil.getInteger(request, "bmoney", 0);//金额
			Long ticketId = daService.getkey("seq_ticket_tb");
			logger.info("sweepticket>>>收费员："+uin+",score:"+score+",bmoney:"+bmoney+",ticketId:"+ticketId);
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comId});
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < score){
				infoMap.put("result", -3);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//积分不足
				logger.info("sendticket>>>打赏积分不足，收费员:"+uin+",score:"+score+",reward_score:"+reward_score);
				return null;
			}
			Long ctime = System.currentTimeMillis()/1000;
			String code = null;
			Long ticketids[] = new Long[]{ticketId};
			String []codes = StringUtils.getGRCode(ticketids);
			if(codes.length > 0){
				code = codes[0];
			}
			logger.info("sweepticket>>>收费员："+uin+",code:"+code);
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			//二维码
			Map<String, Object> codeSqlMap = new HashMap<String, Object>();
			
			Map<String, Object> ticketSqlMap = new HashMap<String, Object>();
			
			codeSqlMap.put("sql", "insert into qr_code_tb(comid,uid,ctime,type,state,code,isuse,ticketid,score) values(?,?,?,?,?,?,?,?,?)");
			codeSqlMap.put("values", new Object[] { comId, uin, ctime, 6, 0, code, 1, ticketId, score });
			bathSql.add(codeSqlMap);
			
			ticketSqlMap.put("sql", "insert into ticket_tb(id,create_time,limit_day,money,state,comid,type) values(?,?,?,?,?,?,?)");
			ticketSqlMap.put("values", new Object[] {ticketId, TimeTools.getToDayBeginTime(),TimeTools.getToDayBeginTime()+16*24*60*60-1, bmoney, 0, comId, 1});
			bathSql.add(ticketSqlMap);
			
			boolean b = daService.bathUpdate(bathSql);
			logger.info("sweepticket>>>收费员："+uin+",ticketId:"+ticketId+",code:"+code+",b:"+b);
			if(b){
				String url = "http://"+Constants.WXPUBLIC_S_DOMAIN+"/zld/qr/c/"+code;
				infoMap.put("result", 1);
				infoMap.put("code", url);
				infoMap.put("ticketid", ticketId);
				infoMap.put("cname", comMap.get("company_name"));
			}else{
				infoMap.put("result", -1);
			}
			AjaxUtil.ajaxOutput(response,StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=sweepticket&bmoney=3&score=1&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("deductscore")){//往微信里发送，用户点击领取
			Double score = RequestUtil.getDouble(request, "score", 0d);//消耗积分
			Long ticketid = RequestUtil.getLong(request, "ticketid", -1L);
			logger.info("ticketid:"+ticketid+",score:"+ticketid+",uin:"+uin);
			if(score == 0 || ticketid == -1){
				infoMap.put("result", -1);
			}
			Map<String, Object> userMap = daService.getMap(
					"select id,nickname,reward_score from user_info_tb where id=? ",
					new Object[] { uin });
			Map<String, Object> comMap = daService.getMap("select company_name from com_info_tb where id=? ", new Object[]{comId});
			Double reward_score = Double.valueOf(userMap.get("reward_score") + "");
			if(reward_score < score){
				infoMap.put("result", -3);
				AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));//积分不足
				logger.info("deductscore>>>打赏积分不足，收费员:"+uin+",score:"+score+",reward_score:"+reward_score);
				return null;
			}
			List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
			
			Map<String, Object> scoreSqlMap = new HashMap<String, Object>();
			//积分明细
			Map<String, Object> scoreAccountSqlMap = new HashMap<String, Object>();
			
			scoreAccountSqlMap.put("sql", "insert into reward_account_tb (uin,score,type,create_time,remark,target,ticket_id) values(?,?,?,?,?,?,?)");
			scoreAccountSqlMap.put("values", new Object[]{uin,score,1,System.currentTimeMillis()/1000,"停车券 用户微信点击领取",2,ticketid});
			bathSql.add(scoreAccountSqlMap);
			
			scoreSqlMap.put("sql", "update user_info_tb set reward_score=reward_score-? where id=? ");
			scoreSqlMap.put("values", new Object[]{score, uin});
			bathSql.add(scoreSqlMap);
			
			boolean b = daService.bathUpdate(bathSql);
			logger.info("uin:"+uin+",b:"+b);
			if(b){
				infoMap.put("result", 1);
			}
			AjaxUtil.ajaxOutput(response,StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=deductscore&score=1&ticketid=&token=
		}else if(action.equals("todayaccount")){//今日账户、积分、打赏查询
			Long b = TimeTools.getToDayBeginTime();
			Long e = System.currentTimeMillis()/1000+60;
			Double parkmoney = 0d;
			Double parkusermoney = 0d;
//			Double cashmoney = 0d;
			Double rewardmoney = 0d;
			Double todayscore = 0d;//剩余积分
			Long todayin = 0L;//今日入场车辆
			Long todayout = 0L;//今日出场车辆
			//一键对账   1:车场管理员    2收费员
			Map park = daService.getMap( "select sum(amount) total from park_account_tb where create_time between ? and ? " +
					" and type <> ? and uid=? and comid=? ",new Object[]{b,e,1,uin,comId});
			if(park!=null&&park.get("total")!=null){
				parkmoney = Double.valueOf(park.get("total")+"");//车场账户收入（不计来源）
			}
			
			Map parkuser = daService.getMap( "select sum(amount) total from parkuser_account_tb where create_time between ? and ? " +
					" and type= ? and uin = ? ",new Object[]{b,e,0,uin});
			if(parkuser!=null&&parkuser.get("total")!=null){
				parkusermoney = Double.valueOf(parkuser.get("total")+"");//收费员账户收入（不计来源）
			}
			
			/*Map cash = daService.getMap( "select sum(amount) total from parkuser_cash_tb where create_time between ? and ? " +
					" and uin=? ",new Object[]{b,e,uin});
			if(cash!=null&&cash.get("total")!=null){
				cashmoney = Double.valueOf(cash.get("total")+"");//收费员现金收入
			}*/
			
			Map reward = daService.getMap("select sum(money) total from parkuser_reward_tb where ctime between ? and ? and uid=? ",
							new Object[] { b, e, uin });
			if(reward != null && reward.get("total") != null){
				rewardmoney = Double.valueOf(reward.get("total") + "");
			}
			
			Map score = daService.getMap("select reward_score from user_info_tb where id=? ", new Object[] { uin });
			if(score != null && score.get("reward_score") != null){
				todayscore = Double.valueOf(score.get("reward_score") + "");
			}
			
			todayin = daService.getLong("select count(1) from order_tb where comid=? " +
					"and create_time between ? and ?", new Object[]{comId,b,e});
			
			todayout = daService.getLong("select count(1) from order_tb where comid=? and state=? " +
					"and end_time between ? and ?", new Object[]{comId,1,b,e});
			
			infoMap.put("mobilemoney", StringUtils.formatDouble(parkmoney + parkusermoney));
			infoMap.put("rewardmoney", StringUtils.formatDouble(rewardmoney));
			infoMap.put("todayscore", StringUtils.formatDouble(todayscore));
			infoMap.put("todayin", todayin);
			infoMap.put("todayout", todayout);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=todayaccount&token=5286f078c6d2ecde9b30929f77771149
		}else if(action.equals("remainscore")){
			Double todayscore = 0d;//剩余积分
			Map score = daService.getMap("select reward_score from user_info_tb where id=? ", new Object[] { uin });
			if(score != null && score.get("reward_score") != null){
				todayscore = Double.valueOf(score.get("reward_score") + "");
			}
			infoMap.put("score", todayscore);
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
			return null;
		}else if(action.equals("queryaccount")){//根据车牌查询在该车场的账户明细
			String carnumber = AjaxUtil.decodeUTF8(RequestUtil.processParams(request, "carnumber"));
			Integer pageNum = RequestUtil.getInteger(request, "page", 1);
			Integer pageSize = RequestUtil.getInteger(request, "size", 20);
			if(carnumber.equals("")){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			Long ntime = System.currentTimeMillis()/1000;
			Map<String, Object> carMap = pService.getMap(
					"select uin from car_info_tb where car_number=? ",
					new Object[] { carnumber });
			if(carMap == null || carMap.get("uin") == null){
				AjaxUtil.ajaxOutput(response, "-1");
				return null;
			}
			List<Map<String, Object>> carList = pService
					.getAll("select car_number from car_info_tb where uin=? and state=? ",
							new Object[] { carMap.get("uin"), 1 });
			String cnum = "该车主有"+carList.size()+"个车牌:/n";
			for(int i = 0; i<carList.size(); i++){
				Map<String, Object> map = carList.get(i);
				if(i == 0){
					cnum += map.get("car_number");
				}else{
					cnum += "," + map.get("car_number");
				}
			}
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			List<Object> params = new ArrayList<Object>();
			String sql = "select a.*,o.car_number carnumber from parkuser_account_tb a,order_tb o where a.orderid=o.id and o.uid=? and o.uin=? and a.type=? and a.create_time between ? and ? order by a.create_time desc";
			String sqlcount = "select count(a.*) from parkuser_account_tb a,order_tb o where a.orderid=o.id and o.uid=? and o.uin=? and a.type=? and a.create_time between ? and ? ";
			params.add(uin);
			params.add(carMap.get("uin"));
			params.add(0);
			params.add(ntime - 30*24*60*60);
			params.add(ntime);
			Long count = pService.getCount(sqlcount, params);
			if(count > 0){
				list = pService.getAll(sql, params, pageNum, pageSize);
				setRemark(list);
			}
			String reslut =  "{\"count\":"+count+",\"carinfo\":\""+cnum+"\",\"info\":"+StringUtils.createJson(list)+"}";
			AjaxUtil.ajaxOutput(response, reslut);
			return null;
			//http://127.0.0.1/zld/collectorrequest.do?action=queryaccount&token=2dd4b1b320225dfd4fc44ad6b53fa734&carnumber=京QLL122
		}else if(action.equals("posincome")){//pos机生成订单
			AjaxUtil.ajaxOutput(response, posIncome(request,comId,uin));
			//http://127.0.0.1/zld/collectorrequest.do?action=posincome&token=2dd4b1b320225dfd4fc44ad6b53fa734&carnumber=京QLL122
			
		}else if(action.equals("liftrodrecord")){//抬杆记录
			String result = liftRod(request,uin,comId);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/collectorrequest.do?action=liftrodrecord&token=d481a6fb58e758c3f0ef9aa7c4bdff29&passid=13
		}else if(action.equals("liftrodreason")){//抬杆记录，更新原因
			String result = liftRodReason(request);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/collectorrequest.do?action=liftrodreason&token=d481a6fb58e758c3f0ef9aa7c4bdff29&lrid=3&reason=1
		}else if(action.equals("liftroduppic")){//抬杆记录，上传图片
			String result = liftRodPic(request);
			AjaxUtil.ajaxOutput(response, result);
			//http://127.0.0.1/zld/collectorrequest.do?action=liftroduppic&token=a0b952263fbb0a264194a1443c71174d&lrid=3
		}
		
		if(out.equals("json")){
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
		}else
			AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
		return null;
	}
	
	private String liftRodReason(HttpServletRequest request) {
		Integer reason = RequestUtil.getInteger(request, "reason", -1);
		Long lrid = RequestUtil.getLong(request, "lrid", -1L);
		if(lrid==-1){
			return  "{result:-1,errmsg:\"订单编号为空！\"}";
		}
		String sql = "update lift_rod_tb set reason=? where id=?";
		int ret = daService.update(sql, new Object[]{reason,lrid});
		logger.info(">>>>>>>>>>lrid:"+lrid+",reason:"+reason+",update lift_rod_tb,ret:"+ret);
		return  "{result:\""+ret+"\",errmsg:\"操作成功！\"}";
	}

	private String liftRod(HttpServletRequest request, Long uin, Long comId) {
		Long key = daService.getkey("seq_lift_rod_tb");
		Long pass_id = RequestUtil.getLong(request, "passid", -1L);//入口通道id
		Integer reason = RequestUtil.getInteger(request, "reason", -1);
		String sql = "insert into lift_rod_tb (id,comid,uin,ctime,pass_id,reason) values(?,?,?,?,?,?)";
		int ret = daService.update(sql, new Object[]{key,comId,uin,System.currentTimeMillis()/1000,pass_id,reason});
		logger.info(">>>>>>>>>>"+comId+","+uin+",upload lift rod,insert into db ret:"+ret);
		if(ret==1)
			return  "{\"result\":\""+ret+"\",\"errmsg\":\"操作成功！\",lrid:\""+key+"\"}";
		else {
			return  "{\"result\":\""+ret+"\",\"errmsg\":\"操作失败！\"}";
		}
	}

	//上传抬杆记录
	private String liftRodPic(HttpServletRequest request) throws Exception{
		Long ntime = System.currentTimeMillis()/1000;
		Long lrid = RequestUtil.getLong(request, "lrid", -1L);
		logger.info("begin upload lift rod picture....lrid:"+lrid);
		Map<String, String> extMap = new HashMap<String, String>();
	    extMap.put(".jpg", "image/jpeg");
	    extMap.put(".jpeg", "image/jpeg");
	    extMap.put(".png", "image/png");
	    extMap.put(".gif", "image/gif");
	    extMap.put(".webp", "image/webp");
		if(lrid==-1){
			return  "{\"result\":\"-1\",\"errmsg\":\"订单编号为空！\"}";
		}
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
			return "{\"result\":\"1\",\"errmsg\":\"车牌保存成功！\"}";
		}
		String filename = ""; // 上传文件保存到服务器的文件名
		InputStream is = null; // 当前上传文件的InputStream对象
		FileOutputStream outer = null;
		// 循环处理上传文件
		for (FileItem item : items){
			// 处理普通的表单域
			if (!item.isFormField()){
				// 从客户端发送过来的上传文件路径中截取文件名
				filename = item.getName().substring(item.getName().lastIndexOf("\\")+1);
				is = item.getInputStream(); // 得到上传文件的InputStream对象
				logger.info("filename:"+item.getName()+",stream:"+is);
			}else{
				continue;
			}
			String file_ext =filename.substring(filename.lastIndexOf(".")).toLowerCase();// 扩展名
			String picurl = lrid + "_"+ System.currentTimeMillis()/1000 + file_ext;
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
				
				 String f = CustomDefind.PIC+getCollectionName(System.currentTimeMillis());
			 	    File file = new File(f);
			 	    if(!file.exists()){
			 	    	file.mkdirs();
			 	    }
			 	    String fileName = f+"\\"+picurl;
			 	    outer = new FileOutputStream(fileName);  
			        outer.write(content);  
			        outer.close(); 
				 
			        
			        
//				DB mydb = MongoClientFactory.getInstance().getMongoDBBuilder("zld");
//				mydb.requestStart();
//				
//				DBCollection collection = mydb.getCollection("lift_rod_pics");
//				//  DBCollection collection = mydb.getCollection("records_test");
//				
//				BasicDBObject document = new BasicDBObject();
//				document.put("lrid", lrid);
//				document.put("ctime", ntime);
//				document.put("type", extMap.get(file_ext));
//				document.put("content", content);
//				document.put("filename", picurl);
//				//开始事务
//				//结束事务
//				mydb.requestStart();
//				collection.insert(document);
//				//结束事务
//				mydb.requestDone();
				in.close();        
				is.close();
				byteout.close();
				String sql = "update lift_rod_tb set img=?,sync_state = ? where id =?";
				int ret = daService.update(sql, new Object[]{picurl,0,lrid});
				logger.info(">>>>>>>>>>orderId:"+lrid+",filename:"+picurl+", update lift_rod_tb, ret:"+ret);
			} catch (Exception e) {
				e.printStackTrace();
				return "{\"result\":\"0\",\"errmsg\":\"图片上传失败！\"}";
			}finally{
				if(outer!=null)
					outer.close();
				if(in!=null)
					in.close();
				if(byteout!=null)
					byteout.close();
				if(is!=null)
					is.close();
			}
		}
		return "{\"result\":\"1\",\"errmsg\":\"上传成功！\"}";
	}
	public static String getCollectionName(Long milliSeconds) {
		String date =TimeTools.getTimeStr_yyyy_MM_dd(milliSeconds);
		String[] strdate = date.split("-");
//		int str = (Integer.parseInt(strdate[2]))/3;
		return strdate[0]+strdate[1]+strdate[2];
	}
	//pos机生成订单
	private String posIncome(HttpServletRequest request,Long comId,Long uid) {
		String carNumber=AjaxUtil.decodeUTF8(RequestUtil.getString(request, "carnumber"));
		Long uin =-1L;
		if(!carNumber.equals("")){//处理传入的车牌号
			Map carMap = daService.getMap("select * from car_info_tb where car_number=? and state=? ", new Object[]{carNumber,1});
			if(carMap!=null&&carMap.get("uin")!=null)
				uin = (Long)carMap.get("uin");
		}else {
			carNumber=null;
		}
		Long ctime = System.currentTimeMillis()/1000;
		String btime = TimeTools.getTime_MMdd_HHmm(ctime*1000);
		String imei  =  RequestUtil.getString(request, "imei");
		String codes[] = StringUtils.getGRCode(new Long[]{123456L});
		Long orderId = daService.getkey("seq_order_tb");
		logger.info("posaddorder,uid:"+uid+",comid:"+comId+",carnumber:"+carNumber+",uuid:"+codes[0]);
		int result = daService.update("insert into order_tb (id,comid,uin,state,create_time,nfc_uuid,c_type,uid,imei,car_number) " +
				"values(?,?,?,?,?,?,?,?,?,?)",new Object[]{orderId,comId,uin,0,ctime,codes[0],0,uid,imei,carNumber});
		if(result==1){
			return "{\"result\":\"1\",\"errmsg\":\"进场成功，正在打印进场凭条...\",\"qrcode\":\"qr/c/"+codes[0]+"\",\"orderid\":\""+orderId+"\",\"btime\":\""+btime+"\"}";
		}
		return "{\"result\":\"0\",\"errmsg\":\"进场错误，请重新操作\",\"qrcode\":\"\",\"orderid\":\"\"}";
	}


	private String currOrders(HttpServletRequest request,Long uin,Long comId,String out,
			Map<String, Object> infoMap) {
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "size", 20);
		List<Object> params = new ArrayList<Object>();
		params.add(0);
		params.add(comId);
		Long _total = daService.getLong("select count(*) from order_tb where state=? and comid=? ", 
				new Object[]{0,comId});
		//查停车订单
		List<Map<String,Object>> list = daService.getAll("select * from order_tb where state=? and comid=? order by id desc ",//and create_time>?",
				params, pageNum, pageSize);
		
		//查泊车订单
		List<Map<String,Object>> csList =null;// daService.getAll("select c.id,c.state,c.buid,c.euid,c.car_number,c.btime,c.start_time,t.next_price,t.max_price  " +
				//"from carstop_order_tb c left join car_stops_tb t on c.cid = t.id where (c.buid=? and c.state in(?,?)) or (c.euid=? and c.state in(?,?)) ",
				//new Object[]{uin,1,2,uin,5,6});
		
		
		//logger.info("currentorder:"+_total);
		List<Map<String, Object>> infoMaps = new ArrayList<Map<String,Object>>();
		Double ptotal = 0d;
		Long end=System.currentTimeMillis()/1000;
		if(list!=null&&list.size()>0){
			for(Map map : list){
				Map<String, Object> info = new HashMap<String, Object>();
				Long uid = (Long)map.get("uin");
				String carNumber = "车牌号未知";
				if(map.get("car_number")!=null&&!"".equals((String)map.get("car_number"))){
					carNumber = (String)map.get("car_number");
				}else {
					if(uid!=-1){
						carNumber = publicMethods.getCarNumber(uid);
					}
				}
				info.put("carnumber", carNumber);
				Long start= (Long)map.get("create_time");
				
				Integer pid = (Integer)map.get("pid");
				Integer car_type = (Integer)map.get("car_type");//0：通用，1：小车，2：大车
				end = System.currentTimeMillis()/1000;
				if(pid>-1){
					info.put("total",publicMethods.getCustomPrice(start, end, pid));
				}else {
					info.put("total",publicMethods.getPrice(start, end, comId, car_type));	
				}
				info.put("id", map.get("id"));
				info.put("type", "order");
				info.put("state","-1");
				info.put("duration", "已停 "+StringUtils.getTimeString(start,end));
				info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
				infoMaps.add(info);
			}
		}
		
		if(csList!=null&&!csList.isEmpty()){
			for(Map<String, Object> map : csList){
				Map<String, Object> info = new HashMap<String, Object>();
				info.put("id", map.get("id"));
				Long start = (Long)map.get("btime");
				Integer state = (Integer)map.get("state");
				if(state>2){
					info.put("duration", "已停 "+StringUtils.getTimeString(start,end));
					info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
					Double nprice = Double.valueOf(map.get("next_price")+"");//时价
					Object tp = map.get("max_price");//最高价
					Double tprice =-1d;
					if(tp!=null)
						tprice = Double.valueOf(tp.toString());
					Long h = StringUtils.getHour(start, end);
					Double total = StringUtils.formatDouble(h*nprice);
					if(tprice!=-1&&total>tprice)
						total = tprice;
					info.put("total",total);
				}else {
					start = (Long)map.get("start_time");
					info.put("total","0.0");
					info.put("btime",TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
					info.put("duration","正在接车");
				}
				info.put("carnumber", map.get("car_number"));
				info.put("state", map.get("state"));
				infoMaps.add(info);
			}
		}
		Collections.sort(infoMaps,new OrderSortCompare());
		
		String result = "";
		ptotal = StringUtils.formatDouble(ptotal);
		if(out.equals("json")){
			result = "{\"count\":"+_total+",\"price\":"+ptotal+",\"info\":"+StringUtils.createJson(infoMaps)+"}";
		}else {
			result = StringUtils.createXML(infoMaps,_total);
		}
		return result;
	}


	private void orderDetail(HttpServletRequest request,Long comId,
			Map<String, Object> infoMap) {

		Long orderId = RequestUtil.getLong(request, "orderid", -1L);
		if(orderId!=-1){
			Map orderMap = daService.getPojo("select * from order_tb where id=?", new Object[]{orderId});
			Long start= (Long)orderMap.get("create_time");
			Long end= System.currentTimeMillis()/1000;
			Integer car_type = (Integer)orderMap.get("car_type");//0：通用，1：小车，2：大车
			if(orderMap.get("end_time")!=null)
				end = (Long)orderMap.get("end_time");
			Integer state = (Integer)orderMap.get("state");
			String _state="未结算";
			if(state==1)
				_state="已结算";
			Long uid = (Long)orderMap.get("uin");
			Map userMap = daService.getMap("select mobile from user_info_Tb where id=?", new Object[]{uid});
			
			String mobile = "";
			if(userMap!=null&&userMap.get("mobile")!=null){
				mobile = userMap.get("mobile")+"";
			}
			if(orderMap!=null&&Integer.valueOf(orderMap.get("c_type")+"")==4){
				infoMap.put("showepay", "直接支付");
			}
			
			String carNumber =orderMap.get("car_number")+"";
			 if(StringUtils.isNumber(carNumber)){
			    	carNumber = "车牌号未知";
			    }
			if(carNumber.equals("null")||carNumber.equals("")){
				carNumber =publicMethods.getCarNumber(uid);
			}
			if("".equals(carNumber.trim())||"车牌号未知".equals(carNumber.trim()))
				carNumber ="null";
			if(orderMap.get("total")!=null)
				infoMap.put("prepay", StringUtils.formatDouble(orderMap.get("total")));
			Integer pid = (Integer)orderMap.get("pid");
			if(pid>-1){
				infoMap.put("total",publicMethods.getCustomPrice(start, end, pid));
			}else {
				infoMap.put("total",publicMethods.getPrice(start, end, comId, car_type));	
			}
			if(orderMap.get("state")!=null&&Integer.valueOf(orderMap.get("state")+"")==1){
				infoMap.put("total", StringUtils.formatDouble(orderMap.get("total")));
			}
			if(orderMap.get("c_type")!=null&&Integer.valueOf(orderMap.get("c_type")+"")==4){
				infoMap.put("total", StringUtils.formatDouble(orderMap.get("total")));
			}
			infoMap.put("orderid", orderId);
			infoMap.put("begin", start);
			infoMap.put("end", end);
			infoMap.put("state",_state);
			infoMap.put("mobile", mobile);
			infoMap.put("carnumber", carNumber);
		}else {
			infoMap.put("info", "无此订单信息");
		}
	}

	private void setRemark(List<Map<String, Object>> list){
		if(list != null && !list.isEmpty()){
			for(Map<String, Object> map : list){
				if(map.get("remark") != null){
					String remark = (String)map.get("remark");
					remark = remark.split("_")[0];
					map.put("remark", remark);
				}
			}
		}
	}
	
	/**
	 * 查询车位信息
	 * @param comId
	 * @return
	 */
	private String getComParks(Long comId) {
		daService.update("update com_park_tb set state =?,order_id=? where order_id in (select id from order_tb where state=? and id in(select order_id from com_park_tb where comid=?)) ", new Object[]{0,-1L,1,comId});
		List<Map<String, Object>> list = daService.getAll("select c.cid,c.state,o.id orderid,o.car_number,o.create_time btime,o.uin, o.end_time etime " +
				"from com_park_tb c left join order_tb o on c.order_id = o.id where c.comid=? order by c.id", new Object[]{comId});
		if(list!=null&&!list.isEmpty()){
			return StringUtils.createJson(list);
		}
		return "{}";
	}

	private void setCarNumber(List<Map<String, Object>> list){
		List<Object> uins = new ArrayList<Object>();
		for(Map<String, Object> map : list){
			uins.add(map.get("uin"));
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
			resultList = daService.getAllMap("select u.id,car_number from user_info_tb u left join car_info_tb c on u.id=c.uin where u.id in ("
									+ preParams + ")", uins);
			List<Object> binduins = new ArrayList<Object>();
			List<Object> nobinduins = new ArrayList<Object>();
			for(Map<String, Object> map : resultList){
				Long uin = (Long)map.get("id");
				if(!binduins.contains(uin)){
					for(Map<String, Object> map2 : list){
						Long id = (Long)map2.get("uin");
						if(uin.intValue() == id.intValue()){
							if(map.get("car_number") != null){
								map2.put("carnumber", map.get("car_number"));
							}
						}
					}
					binduins.add(uin);
				}
			}
		}
	}
	
	private void sendWXMsg(String[] ids, Map userMap,Map comMap,Integer money){
		Long exptime = TimeTools.getToDayBeginTime()+16*24*60*60;
		String exp = TimeTools.getTimeStr_yyyy_MM_dd(exptime * 1000);
		List<Object> uins = new ArrayList<Object>();
		List<Map<String, Object>> openids = new ArrayList<Map<String, Object>>();
		
		for(int i=0;i<ids.length; i++){
			Long uin = Long.valueOf(ids[i]);
			uins.add(uin);
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
			List<Object> binduins = new ArrayList<Object>();
			List<Object> nobinduins = new ArrayList<Object>();//虚拟账户
			resultList = daService.getAllMap(
					"select id,wxp_openid from user_info_tb where id in (" + preParams + ") ", uins);
			for(Map<String, Object> map : resultList){
				Map<String, Object> map2 = new HashMap<String, Object>();
				Long uin = (Long)map.get("id");
				if(map.get("wxp_openid") != null){
					map2.put("openid", map.get("wxp_openid"));
					map2.put("bindflag", 1);
					openids.add(map2);
				}
				binduins.add(uin);
			}
			for(Object object: uins){
				if(!binduins.contains(object)){
					nobinduins.add(object);
				}
			}
			logger.info("sendWXMsg>>>虚拟账户："+nobinduins.toString());
			if(!nobinduins.isEmpty()){
				preParams  ="";
				for(Object uin : nobinduins){
					if(preParams.equals(""))
						preParams ="?";
					else
						preParams += ",?";
				}
				resultList = daService.getAllMap(
						"select openid from wxp_user_tb where uin in (" + preParams + ") ", nobinduins);
				for(Map<String, Object> map : resultList){
					Map<String, Object> map2 = new HashMap<String, Object>();
					if(map.get("openid") != null){
						map2.put("openid", map.get("openid"));
						map2.put("bindflag", 0);
						openids.add(map2);
					}
				}
			}
			logger.info("sendWXMsg>>>:发消息的openid:"+openids.toString());
			if(openids.size() > 0){
				for(Map<String, Object> map : openids){
					try {
						String openid = (String)map.get("openid");
						Integer bindflag = (Integer)map.get("bindflag");
						
						String url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=toticketpage&openid="+openid;
						if(bindflag == 0){
							url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Constants.WXPUBLIC_APPID+"&redirect_uri=http%3a%2f%2f"+Constants.WXPUBLIC_REDIRECTURL+"%2fzld%2fwxpaccount.do&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
						}
						Map<String, String> baseinfo = new HashMap<String, String>();
						List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
						String first = "恭喜获得收费员"+userMap.get("nickname")+"("+userMap.get("id")+")赠送的"+comMap.get("company_name")+"专用券";
						String remark = "点击查看详情！";
						String remark_color = "#000000";
						baseinfo.put("url", url);
						baseinfo.put("openid", openid);
						baseinfo.put("top_color", "#000000");
						baseinfo.put("templeteid", Constants.WXPUBLIC_TICKET_ID);
						Map<String, String> keyword1 = new HashMap<String, String>();
						keyword1.put("keyword", "coupon");
						keyword1.put("value", money+"元");
						keyword1.put("color", "#000000");
						orderinfo.add(keyword1);
						Map<String, String> keyword2 = new HashMap<String, String>();
						keyword2.put("keyword", "expDate");
						keyword2.put("value", exp);
						keyword2.put("color", "#000000");
						orderinfo.add(keyword2);
						Map<String, String> keyword3 = new HashMap<String, String>();
						keyword3.put("keyword", "remark");
						keyword3.put("value", remark);
						keyword3.put("color", remark_color);
						orderinfo.add(keyword3);
						Map<String, String> keyword4 = new HashMap<String, String>();
						keyword4.put("keyword", "first");
						keyword4.put("value", first);
						keyword4.put("color", "#000000");
						orderinfo.add(keyword4);
//						publicMethods.sendWXTempleteMsg(baseinfo, orderinfo);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
					}
					
				}
			}
		}
	}
	
	private void setinfo(List<Map<String, Object>> list,Integer pageNum,Integer pageSize){
		List<Object> uids = new ArrayList<Object>();
		Integer sort = (pageNum - 1)*pageSize;//排行
		for(Map<String, Object> map : list){
			Long uin = (Long)map.get("uin");
			uids.add(uin);
			
			sort++;
			map.put("sort", sort);
		}
		if(!uids.isEmpty()){
			String preParams  ="";
			for(Object uid : uids){
				if(preParams.equals(""))
					preParams ="?";
				else
					preParams += ",?";
			}
			List<Map<String, Object>> resultList = new ArrayList<Map<String,Object>>();
			resultList = daService.getAllMap(
							"select u.id,nickname,company_name from user_info_tb u,com_info_tb c where u.comid=c.id and u.id in ("
									+ preParams + ") ", uids);
			for(Map<String, Object> map : resultList){
				Long id = (Long)map.get("id");
				String nickname = null;
				String cname = null;
				if(map.get("nickname") != null && ((String)map.get("nickname")).length() > 0){
					nickname = ((String)map.get("nickname")).substring(0, 1);
					for(int i=1;i<((String)map.get("nickname")).length();i++){
						nickname += "*";
					}
				}
				if(map.get("company_name") != null && ((String)map.get("company_name")).length() > 0){
					cname = ((String)map.get("company_name")).substring(0, 1);
					cname += "****停车场";
				}
				for(Map<String, Object> map2: list){
					Long uid = (Long)map2.get("uin");
					if(id.intValue() == uid.intValue()){
						map2.put("nickname", nickname);
						map2.put("cname", cname);
					}
				}
			}
		}
	}
	private String autoUp(HttpServletRequest request,Long comId,Long uid) {
		String carNumber = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "carnumber"));
		String cardno = AjaxUtil.decodeUTF8(RequestUtil.getString(request, "cardno"));
		//处理流程：根据车牌查订单：
		/*
		 * 1:有订单，查是否有预支付 ：
		 * 		（）有预付，查金额是否充足，
		 * 			（）充足：返回：{state:1,orderid,btime,etime,carnumber,duration,total}
		 * 			（）不足：返回   {state:2,prefee,total,collect}
		 * 		（）无预付，查是否是会员
		 * 			（）会员 ：余额是否充足
		 * 				（）充足，是否自动结算  
		 * 					（）是：返回：{state:1,orderid,btime,etime,carnumber,duration,total}
		 * 					（）否：收现金返回：{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 				（）不充足 ： 收现金 返回：{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 			（）非会员 :收现金：返回：{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 2：无订单，生成订单，
		 * 		（）会员 ：余额是否充足
		 * 				（）充足，是否自动结算  
		 * 					（）是：返回：{state:1,orderid,btime,etime,carnumber,duration,total}
		 * 					（）否：收现金返回：{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 				（）不充足 ： 收现金 返回：{state:0,orderid,btime,etime,carnumber,duration,total}
		 * 		（）非会员 :收现金：返回：{state:0,orderid,btime,etime,carnumber,duration,total}
		 *
		 */
		Double price = RequestUtil.getDouble(request, "price", 0d);
		//System.out.println(carNumber);
		String result = "{}";
		//生成订单并结算
		if(comId==null||uid==null||uid==-1||comId==-1){
			result="{\"state\":\"-3\",\"errmsg\":\"没有停车场或收费员信息，请重新登录!\"}";
			return result;
		}
		Long uin = -1L;
		if(!carNumber.equals("")){
			Map carMap = daService.getMap("select uin from car_info_tb where car_number=?", new Object[]{carNumber});
			if(carMap!=null)
				uin = (Long)carMap.get("uin");
		}
		boolean isvip = true;//会员
		if(uin==null||uin==-1) {
			result="{\"state\":\"-1\",\"errmsg\":\"车主未注册!\",\"orderid\":\"\"}";
			isvip=false;
			uin = -1L;
		}
		//查订单:
		Map<String,Object> orderMap =null;
		Long orderId = null;
		boolean isOrder= false;
		if("".equals(carNumber)&&!"".equals(cardno)){//极速第三方卡结算
			String uuid = comId+"_"+cardno;
			Long ncount  = daService.getLong("select count(*) from com_nfc_tb where nfc_uuid=? and state=?", 
					new Object[]{uuid,0});
			if(ncount==0){
				logger.info("极速通刷卡...卡号："+uuid+",未注册....");
				result="{\"state\":\"-10\",\"errmsg\":\"卡号没有注册!\",\"orderid\":\"-1\"}";
			}
			orderMap = daService.getMap("select * from order_tb where comid=? and nfc_uuid=? and state=? ", new Object[]{comId,uuid,0});
			if(orderMap==null||orderMap.isEmpty()){
				if(price<0){
					result="{\"state\":\"-2\",\"errmsg\":\"价格不对:"+price+"!\",\"orderid\":\"-1\"}";
				}else {
					//生成订单
					Long ntime = System.currentTimeMillis()/1000;
					orderId = daService.getkey("seq_order_tb");
					int ret = daService.update("insert into order_tb (id,create_time,end_time,comid,uin,state,pay_type,c_type,uid,nfc_uuid,type,total) values(?,?,?,?,?,?,?,?,?,?,?,?)", 
							new Object[]{orderId,ntime,ntime+60,comId,uin,1,1,3,uid,uuid,2,0.0});
					if(ret!=1){//订单写入出错
						result="{\"state\":\"-4\",\"errmsg\":\"生成订单失败!\",\"orderid\":\""+orderId+"\"}";
					}
					if(ncount>0)
						return "{\"state\":\"-11\",\"errmsg\":\"车主未预支付!\",\"orderid\":\"\"}";
				}
			}else {
				orderId = (Long)orderMap.get("id");
				Double prePay = StringUtils.formatDouble(orderMap.get("total"));
				uin = (Long)orderMap.get("uin");
				Long ouid = (Long)orderMap.get("uid");
				logger.info("orderid:"+orderId+",prepay:"+prePay+",uin:"+uin+",total:"+price);
				if(uid!=null&&ouid!=null&&!ouid.equals(uid)){
					daService.update("update order_tb set uid=? where id =? ", new Object[]{uid,orderId});
				}
				if(prePay>0){//有预支付 
					Integer ret = 1;//publicMethods.doPrePayOrder(orderMap, price);
					if(ret==1){//支付成功
						if(prePay>=price){//余额充足
							orderMap = daService.getMap("select * from order_tb where id=? ", new Object[]{orderId});
							result=getThirdCardOrderInfo(orderMap);//"{\"state\":\"1\"}";//{state:1,orderid,btime,etime,carnumber,duration,total}
						}else {
							result="{\"state\":\"2\",\"prefee\":\""+prePay+"\",\"total\":\""+price+"\",\"collect\":\""+StringUtils.formatDouble((price-prePay))+"\"}";
						}
					}
				}else {
					if(!isvip){
						daService.update("update order_tb set state=? ,total=?,end_time=?,pay_type=? where id = ? ", new Object[]{1,price,System.currentTimeMillis()/1000,1,orderId});
						if(ncount>0)
							return "{\"state\":\"-11\",\"errmsg\":\"车主未预支付!\",\"orderid\":\"\"}";
					}
				}
			}
			
		}else {//极速通照牌结算
			orderMap = daService.getMap("select * from order_tb where comid=? and car_number=? and state=? ", new Object[]{comId,carNumber,0});
			if(orderMap!=null){//有订单
				orderId = (Long)orderMap.get("id");
				Double prePay = StringUtils.formatDouble(orderMap.get("total"));
				logger.info("极速通>>>>orderid:"+orderId+",uin:"+uin+",prePay:"+prePay+",price:"+price+",isvip:"+isvip);
				uin = (Long)orderMap.get("uin");
				Long ouid = (Long)orderMap.get("uid");
				if(uid!=null&&ouid!=null&&!ouid.equals(uid)){
					daService.update("update order_tb set uid=? where id =? ", new Object[]{uid,orderId});
				}
				if(prePay>0){//有预支付 
					Integer ret = 1;//publicMethods.doPrePayOrder(orderMap, price);
					if(ret==1){//支付成功
						if(prePay>=price){//余额充足
							orderMap = daService.getMap("select * from order_tb where id=? ", new Object[]{orderId});
							result=getOrderInfo(orderMap);//"{\"state\":\"1\"}";//{state:1,orderid,btime,etime,carnumber,duration,total}
						}else {
							result="{\"state\":\"2\",\"prefee\":\""+prePay+"\",\"total\":\""+price+"\",\"collect\":\""+StringUtils.formatDouble((price-prePay))+"\"}";
						}
					}
					return result;
				}else {//无预支付
					isOrder= true;
				}
				if(!isvip){
					daService.update("update order_tb set state=? ,total=?,end_time=?,pay_type=? where id = ? ", new Object[]{1,price,System.currentTimeMillis()/1000,1,orderId});
					return result;
				}
			}else{//无订单
				//查车主
				if(price<0){
					result="{\"state\":\"-2\",\"errmsg\":\"价格不对:"+price+"!\",\"orderid\":\""+orderId+"\"}";
				}else {
					//生成订单
					Long ntime = System.currentTimeMillis()/1000;
					orderId = daService.getkey("seq_order_tb");
					String sql = "insert into order_tb (id,create_time,comid,uin,state,c_type,uid,car_number,type) values(?,?,?,?,?,?,?,?,?)";
					Object [] values =new Object[]{orderId,ntime,comId,uin,0,3,uid,carNumber,1};
					if(uin==-1){//非会员
						sql ="insert into order_tb (id,create_time,comid,uin,state,total,end_time,pay_type,c_type,uid,car_number,type) values(?,?,?,?,?,?,?,?,?,?,?,?)";
						values =new Object[]{orderId,ntime,comId,uin,1,price,ntime+60,1,3,uid,carNumber,1};
					}
					int ret = daService.update(sql, values)	;
					if(ret==1){//订单已写入
						isOrder = true;
					}else {
						result="{\"state\":\"-4\",\"errmsg\":\"生成订单失败!\",\"orderid\":\""+orderId+"\"}";
						return result;
					}
				}
			}
			if(isOrder&&uin!=-1){//有订单要支付 --->>>>
				//查可用停车券
				Map tempMap = null;//publicMethods.useTickets(uin, price, comId,uid,0);
				Long ticketId = null;
				if(tempMap!=null){
					ticketId = (Long)tempMap.get("id");
				}
				//查当前订单
				tempMap = daService.getMap("select * from order_tb where id =? ", new Object[]{orderId});
				
				//自动支付设置
				int isautopay = isAutoPay(uin,price);
				if(isautopay==-1){//车主未设置自动支付
					result="{\"state\":\"-8\",\"errmsg\":\"车主未设置自动支付!\",\"orderid\":\""+orderId+"\",\"carnumber\":\""+carNumber+"\",\"total\":\""+price+"\"}";
					daService.update("update order_tb set state=? ,total=?,pay_type=?,end_time=?  where id = ? ", new Object[]{1,price,1,System.currentTimeMillis()/1000,orderId});
					return result;
				}else if(isautopay==-2){//订单金额超出自动支付限额
					result="{\"state\":\"-9\",\"errmsg\":\"订单金额超出自动支付限额!\",\"orderid\":\""+orderId+"\",\"carnumber\":\""+carNumber+"\",\"total\":\""+price+"\"}";
					daService.update("update order_tb set state=? ,total=?,pay_type=?,end_time=?  where id = ? ", new Object[]{1,price,1,System.currentTimeMillis()/1000,orderId});
					return result;
				}
				//结算订单
				int re = 5;//publicMethods.payOrder(tempMap, price, uin, 2,0,ticketId,null);
				logger.info(">>>>>>>>>>>>车主账户支付 ："+re+",orderid:"+orderId);
				if(re==5){//结算成功
					tempMap = daService.getMap("select * from order_tb where id =? ", new Object[]{orderId});
					result=getOrderInfo(tempMap);//"{\"state\":\"1\",\"errmsg\":\"订单支付成功!\"}";//{state:1,orderid,btime,etime,carnumber,duration,total}
				}else{
					switch (re) {
					case -8://已支付，不能重复支付
						result="{\"state\":\"-5\",\"errmsg\":\"已支付，不能重复支付!\",\"orderid\":\""+orderId+"\"}";
						break;
					case -7://支付失败
						result="{\"state\":\"-6\",\"errmsg\":\"支付失败!\",\"orderid\":\""+orderId+"\"}";						
						break;
					case -12://余额不足
						result="{\"state\":\"-7\",\"errmsg\":\"余额不足!\",\"orderid\":\""+orderId+"\",\"carnumber\":\""+carNumber+"\",\"total\":\""+price+"\"}";
						daService.update("update order_tb set state=? ,total=?,pay_type=?,end_time=?  where id = ? ", new Object[]{1,price,1,System.currentTimeMillis()/1000,orderId});
						break;
					default:
						result="{\"state\":\"-6\",\"errmsg\":\"支付失败!\",\"orderid\":\""+orderId+"\"}";						
						break;
					}
				}
			}
		}
		//预支付不足：result="{\"result\":\"2\",\"prefee\":\""+prefee+"\",\"total\":\""+money+"\",\"collect\":\""+(money-prefee)+"\"}";
		//结算成功：{"total":"79.4","duration":"5天 18小时24分钟","carnumber":"京AFY123","etime":"10:38","state":"2","btime":"16:14","orderid":"786636"} 
		if(result.equals("{}"))
			result="{\"state\":\"-6\",\"errmsg\":\"支付失败!\",\"orderid\":\""+orderId+"\"}";
		logger.info(">>>>>>极速通:"+result);
		return result;
	}
	/**是否自动支付***/
	private int isAutoPay(Long uin, Double price) {
		//查车主配置，是否设置了自动支付。没有配置时，默认25元以下自动支付 
		Integer autoCash=1;
		Map upMap = daService.getPojo("select auto_cash,limit_money from user_profile_tb where uin =?", new Object[]{uin});
		Integer limitMoney =25;
		if(upMap!=null&&upMap.get("auto_cash")!=null){//车主有自动支付设置
			autoCash= (Integer)upMap.get("auto_cash");
			limitMoney = (Integer)upMap.get("limit_money");
			if(autoCash!=null&&autoCash==1){//设置了自动支付
				if(limitMoney==-1)//不限上金额
					return 1;
				else if(price>limitMoney){//订单金额超出了自动支付限额
					return -2;
				}
			}else//设置了不自动支付
				return -1;
		}
		//车主没有自动支付设置，返回可支付
		return 1;
	}
	private String getOrderInfo(Map orderMap){
		Long btime = (Long)orderMap.get("create_time");
		Long etime = (Long)orderMap.get("end_time");
		String dur = StringUtils.getTimeString(btime,etime);
		String bt = TimeTools.getTime_yyyyMMdd_HHmm(btime*1000).substring(11);
		String et = TimeTools.getTime_yyyyMMdd_HHmm(etime*1000).substring(11);
		String ret = "{\"state\":\"1\",\"orderid\":\""+orderMap.get("id")+"\",\"btime\":\""+bt+"\",\"etime\":\""+et+"\"," +
				"\"carnumber\":\""+orderMap.get("car_number")+"\",\"duration\":\""+dur+"\",\"total\":\""+orderMap.get("total")+"\"}";
		return ret;
	}
	
	private String getThirdCardOrderInfo(Map orderMap){
		Long btime = (Long)orderMap.get("create_time");
		Long etime = (Long)orderMap.get("end_time");
		String dur = StringUtils.getTimeString(btime,etime);
		String bt = TimeTools.getTime_yyyyMMdd_HHmm(btime*1000).substring(11);
		String et = TimeTools.getTime_yyyyMMdd_HHmm(etime*1000).substring(11);
		String uuid = (String)orderMap.get("nfc_uuid");
		if(uuid!=null&&uuid.indexOf("_")!=-1)
			uuid = uuid.split("_")[1];
		else {
			uuid = "";
		}
		String ret = "{\"state\":\"1\",\"orderid\":\""+orderMap.get("id")+"\",\"btime\":\""+bt+"\",\"etime\":\""+et+"\"," +
				"\"carnumber\":\""+uuid+"\",\"duration\":\""+dur+"\",\"total\":\""+orderMap.get("total")+"\"}";
		return ret;
	}

	
	/**
	 * 分享车位
	 * @param comId 停车场编号
	 * @param uin 客户编号 
	 * @param number 分享数
	 * @param infoMap 返回结果
	 */
	private void doShare(Long comId,Long uin,Integer number,Map<String,Object> infoMap,boolean isCanLalaRecord){
		//更新公司表中停车场的分享数量，
		if(comId!=null&&uin!=null){
			int result = daService.update("update com_info_tb set share_number =?,update_time=? where id=?",
					new Object[]{number,System.currentTimeMillis()/1000,comId});
			//计算返回可用数量
			if(result==1){
//				if(isCanLalaRecord)
//					doCollectorSort(number,uin,comId);
				//查询当前未结算的订单数，（真来电已占车位数）
				Long count = daService.getLong("select count(*) from order_tb where comid=? and state=? ",//and create_time>?",
						new Object[]{comId,0});//,TimeTools.getToDayBeginTime()});
				infoMap.put("info", "success");
				infoMap.put("busy", count+"");
				logService.updateShareLog(comId, uin, number);
			}else {
				infoMap.put("info", "fail");
				infoMap.put("message", "分享车位失败，请稍候再试!");
			}
		}else {
			infoMap.put("info", "fail");
			infoMap.put("message", "公司或员工不合法!");
		}
	}
	
	/*private  void doCollectorSort(Integer number,Long uin,Long comId){
		
		Long time = System.currentTimeMillis()/1000;
		boolean isLala  = false;
		try {
			isLala = publicMethods.isCanLaLa(number, uin, time);
		} catch (Exception e) {
			logger.info("memcacahe error:"+e.getMessage());
			isLala=ParkingMap.isCanRecordLaLa(uin);
		}
		if(isLala){
			logService.updateScroe(1, uin,comId);
		}
	}*/
	
	/**
	 * 打折处理
	 * @param comId 停车场编号
	 * @param uin 客户编号 
	 * @param hour  优惠小时 
	 * @param orderId 订单编号
	 * @param infoMap 返回结果
	 */
	private void doSale(Long comId,Long uin,Integer hour,Long orderId,Map<String,Object> infoMap){
		//更新订单表的金额，停车场的总额及余额，车主的余额,
		Map orderMap = daService.getPojo("select * from order_tb where id=?", new Object[]{orderId});
		if(orderMap!=null){
			Long cid  = (Long)orderMap.get("comid");
			Long uid = (Long)orderMap.get("uin");
			if(cid.intValue()==comId.intValue()){//验证订单是否正确 
				Double total = getPrice(hour, comId);
				//更新订单金额
				List<Map<String, Object>> bathSql = new ArrayList<Map<String,Object>>();
				Map<String, Object> orderSqlMap = new HashMap<String, Object>();
				orderSqlMap.put("sql", "update order_tb set total = total-? where id =?");
				orderSqlMap.put("values", new Object[]{total,orderId});
				bathSql.add(orderSqlMap);
				//更新停车场总额及余额
				Map<String, Object> comSqlMap = new HashMap<String, Object>();
				comSqlMap.put("sql", "update com_info_tb set " +
						"total_money=total_money-? ,money=money-? where id=?");
				comSqlMap.put("values", new Object[]{total,total,comId});
				bathSql.add(comSqlMap);
				//更新车主余额
				Map<String, Object> userSqlMap = new HashMap<String, Object>();
				userSqlMap.put("sql", "update user_info_Tb set balance = balance+? where id =?");
				userSqlMap.put("values", new Object[]{total,uid});
				bathSql.add(userSqlMap);
				boolean result = daService.bathUpdate(bathSql);
				if(result){//更新余额成功时，返回消息，并写系统日志
					infoMap.put("info", "success");
					infoMap.put("message", "优惠成功!");
					//写系统日志
					doLog(comId, uin, TimeTools.gettime()+",优惠了订单，编号："+orderId+",优惠金额："+total,2);
					//写系统消息，车主可以通过刷新消息取到
					doMessage(uid, TimeTools.gettime()+",您的订单(编号："+orderId+")优惠了"+total+"元,已经更新了你的余额，请查收。");
				}else {
					infoMap.put("info", "fail");
					infoMap.put("message", "优惠失败，请稍候重试!");
				}
			}
		}else{
			infoMap.put("info", "fail");
			infoMap.put("message", "参数有误!");
		}
			
		//添加消费流水
		//写打折日志 
	}

	private Double getPrice (Integer hour,Long comId){
		//计算优惠金额
		Map priceMap = daService.getPojo("select * from price_tb where comid=?" +
				" and state=? order by id desc",new Object[]{comId,1});
		Double price = 0d;
		if(priceMap!=null){
			Integer payType = (Integer)priceMap.get("pay_type");
			price = Double.valueOf(priceMap.get("price")+"");
			switch (payType) {
			case 0://分段
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
				calendar.setTimeInMillis(System.currentTimeMillis());
				//开始小时
				int nhour = calendar.get(Calendar.HOUR_OF_DAY);
				Integer bTime = (Integer)priceMap.get("b_time");
				Integer eTime = (Integer)priceMap.get("e_time");
				//当前时间在分段区间内
				if(nhour>bTime&&nhour<eTime)
					price = price*hour;
				break;
			case 2://按时间单位
				Integer unit = (Integer)priceMap.get("unit");
				price = hour*60/unit*price;
				break;		
			default:
				break;
			}
		}
		return price;
		
	}
	/**
	 * //写系统消息，（收费员定时取消息）
	 * @param uin
	 * @param body
	 */
	private void doMessage(Long uin,String body){
		daService.update("insert into message_tb (type,uin,create_time,content,state) values (?,?,?,?,?)", 
				new Object[]{1,uin,System.currentTimeMillis()/1000,body,0});
	}
	/*
	 * 写系统日志 
	 */
	private void doLog(Long comid,Long uin,String log,Integer type){
		logService.updateOrderLog(comid, uin, log, type);
	}
	/**
	 * 计算订单金额
	 * @param start
	 * @param end
	 * @param comId
	 * @return 订单金额_是否优惠
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map getPriceMap(Long comId){
		Map priceMap1=null;
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? order by id desc", new Object[]{comId,0});
		if(priceList==null||priceList.size()==0){
			//发短信给管理员，通过设置好价格
		}else {
			priceMap1=priceList.get(0);
			boolean pm1 = false;//找到map1,必须是结束时间大于开始时间
			Integer payType = (Integer)priceMap1.get("pay_type");
			if(payType==0&&priceList.size()>1){
				for(Map map : priceList){
					if(pm1)
						break;
					payType = (Integer)map.get("pay_type");
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(payType==0&&etime>btime){
						if(!pm1){
							priceMap1 = map;
							pm1=true;
						}
					}
				}
			}
		}
		return priceMap1;	
	}
	
	private List<Map<String, Object >> setScroeList(List<Map> list){
		List<Map<String, Object >> templiList = new ArrayList<Map<String, Object >>();
		List<Object> uins = new ArrayList<Object>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				if(map.get("uin")!=null){
					Long uin = (Long)map.get("uin");
//					if(!uins.contains(uin))
						uins.add(uin);
				}
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
			uins.add(0);
			List<Map<String, Object>> resultList = daService.getAllMap("select u.id,u.mobile ,u.nickname as uname,c.company_name cname ," +
					"c.uid from user_info_tb u,com_info_tb c" +
					" where u.comid=c.id and  u.id in ("+preParams+")  and c.state=?", uins);
			
			//Map<String ,Object> markerMap = new HashMap<String ,Object>();
			if(resultList!=null&&!resultList.isEmpty()){
				
				for(int i=0;i<list.size();i++){
					Map map1 = (Map)list.get(i);
					for(Map<String,Object> map: resultList){
						Long uin = (Long)map.get("id");
						if(map1.get("uin").equals(uin)){
							templiList.add(map1);
							map1.put("nickname", "-");
							String cname = (String)map.get("cname");
							if(cname.length() > 1){
								String hidecname = "***";
								/*for(int j=0;j<cname.length()-2;j++){
									hidecname += "*";
								}*/
								hidecname =cname.substring(0, 1) +hidecname + cname.substring(cname.length()-1, cname.length());
								cname = hidecname;
							}
							map1.put("cname", cname);
							map1.put("score", StringUtils.formatDouble(map1.get("score")));
							break;
						}
					}
				}
			}
		}
		return templiList;
	}
	
	private String myInfo(Long uin){
		Map userMap = daService.getMap("select id, nickname,auth_flag,mobile from user_info_tb where id=?",new Object[]{uin});
		String info="";
		if(userMap!=null){
			Long count = daService.getLong("select Count(id) from collector_account_pic_tb where uin=? and state=? ", new Object[]{uin,0});
			Long role = (Long)userMap.get("auth_flag");
			String _role = "收费员";
			if(role==1)
				_role = "管理员";
			return "{\"name\":\""+userMap.get("nickname")+"\",\"uin\":\""+userMap.get("id")+
					"\",\"role\":\""+_role+"\",\"mobile\":\""+userMap.get("mobile")+"\",\"pic\":\""+count+"\"}";
		}
		return "{}";
	}
	
	private void setSort(List list){
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				Map map = (Map)list.get(i);
				map.put("sort", i+1);
			}
		}
	}
	
	private void setAccountList (List<Map<String, Object>> list,Integer ptype){
		if(list!=null&&!list.isEmpty()){
			if(ptype==0){
				for(Map<String, Object> map :list){
					Integer target = (Integer)map.get("target");
					if(target!=null){
						switch (target) {
						case 0:
							map.put("target", "银行卡");
							break;
						case 1:
							map.put("target", "支付宝");					
							break;
						case 2:
							map.put("target", "微信");
							break;
						case 3:
							map.put("target", "停车宝");
							break;
						case 4:
							String note = (String)map.get("note");
							String [] notes  = note.split("_");
							map.put("note",notes[0]);
							if(notes.length==2)
								map.put("target", notes[1]);
							else
								map.put("target","");
							break;
						default:
							break;
						}
					}
				}
			}else if(ptype==1){
				if(list!=null&&!list.isEmpty()){
					for(int i=0;i<list.size();i++){
						Map map = (Map)list.get(i);
						Integer type = (Integer)map.get("mtype");
						String remark = (String)map.get("r");
						if(type==0){
							if(remark.indexOf("_")!=-1){
								map.put("note", remark.split("_")[0]);
								map.put("target", remark.split("_")[1]);
							}
						}else if(type==1){
							map.put("note", "提现");
							map.put("target", "银行卡");
						}else if(type==2){
							map.put("note", "返现");
							map.put("target", "停车宝");
						}
						map.remove("r");
					}
				}
			}
			
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
	/**
	 * 
	 * @param orderMap   订单
	 * @param prepaymoney   预支付金额
	 * @return
	 */
	private boolean prepayRefund(Map orderMap , Double prepaymoney){
		Long orderId = (Long)orderMap.get("id");
		Map<String, Object> ticketMap = daService.getMap(
				"select * from ticket_tb where orderid=? order by utime limit ?",
				new Object[] { orderId,1});
		DecimalFormat dFormat = new DecimalFormat("#.00");
		Double back = 0.0;
		List<Map<String, Object>> backSqlList = new ArrayList<Map<String,Object>>();
		if(ticketMap != null){
			logger.info(">>>>>>>>>>>>使用过券，ticketid:"+ticketMap.get("id")+",orderid="+orderId);
			Integer money = (Integer)ticketMap.get("money");
			Double umoney = Double.valueOf(ticketMap.get("umoney")+"");
			umoney = Double.valueOf(dFormat.format(umoney));
			back = Double.valueOf(dFormat.format(prepaymoney - umoney));
			logger.info(">>>>>>>>>>>预支付金额prefee："+prepaymoney+",使用券的金额umoney："+umoney+",应退款金额："+back+",orderid:"+orderId);
			Map<String, Object> tcbAccountsqlMap = new HashMap<String, Object>();
			tcbAccountsqlMap.put("sql", "insert into tingchebao_account_tb(amount,type,create_time,remark,utype,orderid) values(?,?,?,?,?,?)");
			tcbAccountsqlMap.put("values", new Object[]{umoney,0,System.currentTimeMillis() / 1000 ,"停车券返款金额", 6, orderId });
			backSqlList.add(tcbAccountsqlMap);
		}else{
			logger.info(">>>>>>>>>>>>没有使用过券>>>>>>>>>>>>>orderid:"+orderId);
			back = Double.valueOf(dFormat.format(prepaymoney));
		}
		Long uin = (Long)orderMap.get("uin");
		if(back > 0){
			Map count = daService.getPojo("select * from user_info_tb where id=? ", new Object[]{uin});
			Map<String, Object> usersqlMap = new HashMap<String, Object>();
			if(count != null){//真实帐户
				usersqlMap.put("sql", "update user_info_tb set balance=balance+? where id=? ");
				usersqlMap.put("values", new Object[]{back,uin});
				backSqlList.add(usersqlMap);
			}else{//虚拟账户
				usersqlMap.put("sql", "update wxp_user_tb set balance=balance+? where uin=? ");
				usersqlMap.put("values", new Object[]{back,uin});
				backSqlList.add(usersqlMap);
			}
			Map<String, Object> userAccountsqlMap = new HashMap<String, Object>();
			userAccountsqlMap.put("sql", "insert into user_account_tb(uin,amount,type,create_time,remark,pay_type,orderid) values(?,?,?,?,?,?,?)");
			userAccountsqlMap.put("values", new Object[]{uin,back,0,System.currentTimeMillis() / 1000 - 2,"现金结算预支付预支付返款", 12, orderId });
			backSqlList.add(userAccountsqlMap);
			boolean b = daService.bathUpdate(backSqlList);
			logger.info(">>>>>>>>>>预支付返款结果："+b+",orderid:"+orderId);
			try {
				String openid = "";
				if(count!=null)
					openid = count.get("wxp_openid")+"";
				if(!StringUtils.isNotNull(openid)){
					Map wx = daService.getPojo("select * from wxp_user_tb where uin=? ", new Object[]{uin});
					openid = wx.get("openid")+"";
				}
				if(!openid.equals("")){
					logger.info(">>>>>>>>>>>预支付后现金结算订单退回预支付款   微信推消息,uin:"+uin+",openid:"+openid);
					String first = "因现金结算，预支付退款";
					Map<String, String> baseinfo = new HashMap<String, String>();
					List<Map<String, String>> orderinfo = new ArrayList<Map<String,String>>();
					String url = "http://"+Constants.WXPUBLIC_REDIRECTURL+"/zld/wxpaccount.do?action=balance&openid="+openid;
					baseinfo.put("url", url);
					baseinfo.put("openid", openid);
					baseinfo.put("top_color", "#000000");
					baseinfo.put("templeteid", Constants.WXPUBLIC_BACK_NOTIFYMSG_ID);
					Map<String, String> keyword1 = new HashMap<String, String>();
					keyword1.put("keyword", "orderProductPrice");
					keyword1.put("value",back+"元");
					keyword1.put("color", "#000000");
					orderinfo.add(keyword1);
					Map<String, String> keyword2 = new HashMap<String, String>();
					keyword2.put("keyword", "orderProductName");
					keyword2.put("value", "预支付退款");
					keyword2.put("color", "#000000");
					orderinfo.add(keyword2);
					Map<String, String> keyword3 = new HashMap<String, String>();
					keyword3.put("keyword", "orderName");
					keyword3.put("value", orderId+"");
					keyword3.put("color", "#000000");
					orderinfo.add(keyword3);
					Map<String, String> keyword4 = new HashMap<String, String>();
					keyword4.put("keyword", "Remark");
					keyword4.put("value", "点击详情查账户余额！");
					keyword4.put("color", "#000000");
					orderinfo.add(keyword4);
					Map<String, String> keyword5 = new HashMap<String, String>();
					keyword5.put("keyword", "first");
					keyword5.put("value", first);
					keyword5.put("color", "#000000");
					orderinfo.add(keyword5);
//					publicMethods.sendWXTempleteMsg(baseinfo, orderinfo);
				}
			} catch (Exception e) {
				logger.info("退回成功，消息发送失败");
				e.printStackTrace();
				return true;
			}
			logger.info("退回成功 ....");	
			
			return true;
		}else{
			logger.info(">>>>>>>>>>>>>>>退还金额back小于0，orderid："+orderId);
			return false;
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
	private String getLiftReason(Long comid) {
		String reason = CustomDefind.getValue("LIFTRODREASON" + comid);
		String ret = "[";
		if(reason!=null){
			String res[] = reason.split("\\|");
			for(int i=0;i<res.length;i++){
				ret+="{value_no:"+i+",value_name:\""+res[i]+"\"},";
			}
		}
		if(ret.endsWith(","))
			ret = ret.substring(0,ret.length()-1);
		ret +="]";
		return ret;
	}
	/**
	 * 查询历史订单
	 * @param request
	 * @param comId
	 * @param out
	 * @return
	 */
	private String orderHistory(HttpServletRequest request,Long comId,String out) {
		Map<String, Object> infoMap = new HashMap<String, Object>();
		String result="";
		Integer pageNum = RequestUtil.getInteger(request, "page", 1);
		Integer pageSize = RequestUtil.getInteger(request, "size", 20);
		Long _uid = RequestUtil.getLong(request, "uid", -1L);
		String day = RequestUtil.processParams(request, "day");
		String ptype = RequestUtil.getString(request, "ptype");//支付方式
		List<Object> params = new ArrayList<Object>();
		params.add(0);
		params.add(comId);
//		Map com = daService.getMap( "select isshowepay from com_info_tb where id=? and isshowepay=?",new Object[]{comId,1});
//		if(com!=null&&com.get("isshowepay")!=null){
//			params.add(5);//直付订单不返回
//		}else{
//			params.add(4);//直付订单不返回
//		}
//		params.add(5);//修改目前月卡订单不显示
		String countSql = "select count(*) from order_tb where state>? and comid=? ";
		String sql = "select * from order_tb where state>?  and comid=?  ";//order by id desc ";
		String priceSql = "select sum(total) total,uid from order_tb where state>?  and comid=? ";
//		String countSql = "select count(*) from order_tb where state>? and comid=? and c_type<? ";
//		String sql = "select * from order_tb where state>?  and comid=? and c_type<?  ";//order by id desc ";
//		String priceSql = "select sum(total) total,uid from order_tb where state>?  and comid=? and c_type<? ";
		Long time = TimeTools.getToDayBeginTime();
		if(_uid!=-1){
			sql +=" and uid=? and end_time between ? and ?";
			countSql+=" and uid=? and end_time between ? and ?";
			priceSql +=" and uid=? and end_time between ? and ?";
			params.add(_uid);
			Long btime = time;
			if(day.equals("last")){
				params.add(btime-24*60*60);
				params.add(btime);
			}else {
				params.add(btime);
				params.add(btime+24*60*60);
			}
			if(ptype.equals("2")){//手机支付
				sql +=" and pay_type=? ";
				countSql+=" and pay_type=? ";
				priceSql +=" and pay_type=? ";
				params.add(2);
			}else if(ptype.equals("3")){//包月支付
				sql +=" and pay_type=? ";
				countSql+=" and pay_type=? ";
				priceSql +=" and pay_type=? ";
				params.add(3);
			}else if(ptype.equals("4")){//直付订单
				sql +=" and c_type=? ";
				countSql+=" and c_type=? ";
				priceSql +=" and c_type=? ";
				params.add(4);
			}
		}
		Long _total = daService.getCount(countSql,params);
		Object totalPrice = "0";
		Map pMap  = daService.getMap(priceSql+" group by uid ", params);
		if(pMap!=null&&pMap.get("total")!=null){
			totalPrice=pMap.get("total");
		}
		List<Map> list = daService.getAll(sql +" order by end_time desc ",// and create_time>?",
				params, pageNum, pageSize);
		logger.error("historyorder:"+_total+",totalprice:"+totalPrice);
		setPicParams(list);
		Integer ismonthuser = 0;//判断是否月卡用户
		if(list!=null&&list.size()>0){
			List<Map<String, Object>> infoMaps = new ArrayList<Map<String,Object>>();
			for(Map map : list){
				Map<String, Object> info = new HashMap<String, Object>();
				Long uid = (Long)map.get("uin");
				info.put("uin", map.get("uin"));
				String carNumber = "车牌号未知";
				if(map.get("car_number")!=null&&!"".equals((String)map.get("car_number"))){
					carNumber = map.get("car_number")+"";
					if(StringUtils.isNumber(carNumber)){
						carNumber = "车牌号未知";
					}
				}else {
					if(uid!=-1){
						carNumber = publicMethods.getCarNumber(uid);
					}
				}
				info.put("carnumber", carNumber);
				Long start= (Long)map.get("create_time");
				Long end= (Long)map.get("end_time");
				Double total =StringUtils.formatDouble(map.get("total"));// countPrice(start, end, comId);
				info.put("total", StringUtils.formatDouble(total));
				info.put("id", map.get("id"));
				info.put("state", map.get("state"));
				info.put("ptype", map.get("pay_type"));
				if(map.get("c_type")!=null&&Integer.valueOf(map.get("c_type")+"")==4){
					info.put("duration", "直接支付");
				}else {
					info.put("duration", "停车 "+StringUtils.getTimeString(start,end));
				}
				info.put("btime", TimeTools.getTime_yyyyMMdd_HHmm(start*1000));
				//判断是否是月卡用户
//				boolean b = publicMethods.isMonthUser(uid, comId);
				info.put("ctype", map.get("c_type"));
				if(Long.parseLong(map.get("c_type")+"")==5){
					ismonthuser = 1;//是月卡用户
				}else{
					ismonthuser = 0;//不是月卡用户
				}
				info.put("ismonthuser", ismonthuser);
				info.put("car_type", map.get("car_type"));
				//车牌照片参数设置（HD版需要）
				info.put("lefttop", map.get("lefttop"));
				info.put("rightbottom", map.get("rightbottom"));
				info.put("width", map.get("width"));
				info.put("height", map.get("height"));
				infoMaps.add(info);
			}
			if(out.equals("json")){
				result = "{\"count\":"+_total+",\"price\":"+totalPrice+",\"info\":"+StringUtils.createJson(infoMaps)+"}";
			}else {
				result = StringUtils.createXML(infoMaps,_total);
			}
		}else {
			infoMap.put("info", "没有记录");
			result = StringUtils.createJson(infoMap);
		}
		return result;
	}
	
	public String uploadPadLogFile (HttpServletRequest request,Long comid) throws Exception{
		logger.error(">>>>>begin upload logfile....");
		request.setCharacterEncoding("UTF-8"); // 设置处理请求参数的编码格式
		DiskFileItemFactory  factory = new DiskFileItemFactory(); // 建立FileItemFactory对象
		factory.setSizeThreshold(64*4096*1024);
		ServletFileUpload upload = new ServletFileUpload(factory);
		// 分析请求，并得到上传文件的FileItem对象
		upload.setSizeMax(64*4096*1024);
		List<FileItem> items = null;
		try {
			items =upload.parseRequest(request);
		} catch (FileUploadException e) {
			e.printStackTrace();
			 return "{\"state\":\"0\"}";
		}
		InputStream is = null; // 当前上传文件的InputStream对象
		// 循环处理上传文件
		File filedir = new File("c:\\padlogs");
		if(!filedir.exists()){
			filedir.mkdir();
		}
		String filename = "c:\\padlogs\\padlog_"+System.currentTimeMillis()+".txt";
//		File file = new File(filename);
//		if(!file.exists()){
//			file.createNewFile();
//		}
		try {
			for (FileItem item : items){
				if(!item.isFormField()){  
                    //写入文件  
                   // File file = new File(filename);  
                    item.write(new File(filename));  
                    logger.error(">>>>> upload logfile over....");
                    return "{\"state\":\"1\"}";
                }
            }//end of for  
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		daService.update("update com_info_tb set navi=? where id = ? ", new Object[]{1,comid});
		return "{\"state\":\"0\"}";
	}
}

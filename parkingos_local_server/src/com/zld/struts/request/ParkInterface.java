package com.zld.struts.request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

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
import com.zld.service.DataBaseService;
import com.zld.service.PgOnlyReadService;
import com.zld.utils.HttpProxy;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
import com.zld.utils.TimeTools;
/**
 * 车主2.0接口
 * @author Administrator
 * 20150415
 */
public class ParkInterface extends Action {
	
	@Autowired
	private PgOnlyReadService onlyService;
	@Autowired
	private DataBaseService service;
	private Logger logger = Logger.getLogger(ParkInterface.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.getString(request, "action");
		if(action.equals("uploadledstate")){
			AjaxUtil.ajaxOutput(response,"1");
			
			return null;
		}
		if(action.equals("parks")){
			String ret = getParks(request);
			logger.info(ret);
			AjaxUtil.ajaxOutput(response, ret);
			//	http://118.192.85.142:8080/zld/parkinter.do?action=parks&lng=116.322747&lat=39.989056
			
		}else if(action.equals("parkdetail")){//读取评价
			String result = getDetail(request);
			//读取停车场详情   http://127.0.0.1/zld/parkinter.do?action=parkdetail&comid=3
			AjaxUtil.ajaxOutput(response,result);
		}else if(action.equals("getcomment")){
			//读取停车场评论   http://127.0.0.1/zld/parkinter.do?action=getcomment&comid=1197&page=2
			AjaxUtil.ajaxOutput(response, getComments(request));
		}else if(action.equals("uploadcamerastate")){
			//上传摄像头状态   http://127.0.0.1/zld/parkinter.do?action=uploadcamerastate&cameraid=3&state=0
			String result = uploadcamerastate(request);
			AjaxUtil.ajaxOutput(response,result);
		}else if(action.equals("uploadbrakestate")){
			//上传道闸状态   http://127.0.0.1/zld/parkinter.do?action=uploadbrakestate&cameraid=3&state=0
			String result = uploadbrakestate(request);
			AjaxUtil.ajaxOutput(response,result);
		}else if(action.equals("uploadledstate")){
			//上传led状态   http://127.0.0.1/zld/parkinter.do?action=uploadledstate&cameraid=3&state=0
			String result = uploadledstate(request);
			AjaxUtil.ajaxOutput(response,result);
		} 
		return null;
	}
	/**
	 * 上传led状态
	 * @param request
	 * @return
	 */
	private String uploadledstate(HttpServletRequest request) {
		final Long ledid = RequestUtil.getLong(request, "ledid", -1L);
		final Long state= RequestUtil.getLong(request, "state", -1L);//
		int res = 0;
		final long upload_time = System.currentTimeMillis()/1000;
		if(state!=-1&&ledid!=-1){
			res = service.update("update  com_led_tb set state=?,upload_time=? where id=? ", new Object[]{state,upload_time,ledid});
		} 
		logger.info("uploadledstate ledid"+ledid+",state:"+state+",res:"+res);
		/*if(res == 1){
			new Thread(new Runnable() {
				public void run() {
					String token = "";
					Map map = service.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
					if(map!=null&&map.get("token")!=null){
						token = map.get("token")+"";
					}
					final String tk = token;
		            	 HttpProxy httpProxy = new HttpProxy();
		            	 Map<String,String> parammap = new HashMap<String,String>();
		            	 parammap.put("id", ledid+"");
		            	 parammap.put("state",state+"");
		            	 parammap.put("upload_time", upload_time+"");
		            	 String ret = null;
		            	 try {
		            		 ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadled&token="+tk, parammap);
//		            		 if(ret==null){
//		            			 return;
//		            		 }
//		            		 String[] strs = ret.split(",");
//		            		 for (int i = 0; i < strs.length; i++) {
//								if(strs[i]!=null){
//									if(strs[i].startsWith("1")){
//										String rets[] = strs[i].split("_");
//										int r = service.update("update com_led_tb set sync_state=? where id = ? and upload_time=? ", new Object[]{1,Long.valueOf(rets[1]),Long.valueOf(rets[2])});
//									}
//								}
//							}
		            	 } catch (Exception e) {
								e.printStackTrace();
							}
					 }
			}).start();
		}*/
		return res+"";
	}
	/**
	 * 上传道闸状态
	 * @param request
	 * @return
	 */
	private String uploadbrakestate(HttpServletRequest request) {
		final Long passid = RequestUtil.getLong(request, "passid", -1L);
//		Long carmera_id = RequestUtil.getLong(request, "cameraid", -1L);
		final Long state= (RequestUtil.getLong(request, "state", -2L)+1);//状态是0（故障）和1
		int res = 0;
		final long upload_time = System.currentTimeMillis()/1000;
		if(state!=-1&&passid!=-1){
			res = service.update("update  com_brake_tb set state=?,upload_time=? where passid=? ", new Object[]{state,upload_time,passid});
			if(res==0){
				res = service.update("insert into com_brake_tb(passid,state,upload_time) values (?,?,?) ", new Object[]{passid,state,System.currentTimeMillis()/1000});
			}
		}
		logger.info("uploadbrakestate passid"+passid+",state:"+state+",res:"+res);
		/*if(res==1){
			new Thread(new Runnable() {
				public void run() {
					String token = "";
					Map map = service.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
					if(map!=null&&map.get("token")!=null){
						token = map.get("token")+"";
					}
					final String tk = token;
	            	HttpProxy httpProxy = new HttpProxy();
	            	Map<String,String> parammap = new HashMap<String,String>();
	            	parammap.put("passid", passid+"");
	            	parammap.put("state", state+"");
	            	parammap.put("upload_time", upload_time+"");
	            	String ret = null;
	            	try {
	            		ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadbrake&token="+tk, parammap);
//	            		if(ret==null){
//	            			return;
//	            		}
//	            		String[] strs = ret.split(",");
//	            		for (int i = 0; i < strs.length; i++) {
//						if(strs[i]!=null){
//							if(strs[i].startsWith("1")){
//								String rets[] = strs[i].split("_");
//								int r = service.update("update com_brake_tb set sync_state=? where id = ? and upload_time=? ", new Object[]{1,Long.valueOf(rets[1]),Long.valueOf(rets[2])});
//							}
//						 }
//					   }
	            	} catch (Exception e) {
							e.printStackTrace();
						}
				 }
			}).start();
		}*/
		return res+"";
	}
	/**
	 * 上传摄像头状态
	 * @param request
	 * @return
	 */
	private String uploadcamerastate(HttpServletRequest request) {
		logger.info("uploadcamerastate id");
		final Long id = RequestUtil.getLong(request, "cameraid",-1L);
		final Long state= RequestUtil.getLong(request, "state", -1L);
		int res = 0;
		final long upload_time = System.currentTimeMillis()/1000;
		if(state!=-1&&id!=-1){
			res = service.update("update com_camera_tb set state = ? ,upload_time = ? where id=?", new Object[]{state,upload_time,id});
		}
		logger.info("uploadcamerastate id:"+id+",state:"+state+",res:"+res);
		/*if(res==1){
			new Thread(new Runnable() {
				public void run() {
					String token = "";
					Map map = service.getMap("select * from  sync_time_tb where id = ? ", new Object[]{1});
					if(map!=null&&map.get("token")!=null){
						token = map.get("token")+"";
					}
					final String tk = token;
	            	HttpProxy httpProxy = new HttpProxy();
	            	Map<String,String> parammap = new HashMap<String,String>();
	            	parammap.put("id", id+"");
	            	parammap.put("state", state+"");
	            	parammap.put("upload_time", upload_time+"");
	            	String ret = null;
	            	try {
	            		ret = httpProxy.doPost(CustomDefind.DOMAIN+"/syncInter.do?action=uploadcamera&token="+tk, parammap);
//	            		if(ret==null){
//	            			return;
//	            		}
//	            		String[] strs = ret.split(",");
//	            		for (int i = 0; i < strs.length; i++) {
//	            			if(strs[i]!=null){
//								if(strs[i].startsWith("1")){
//									String rets[] = strs[i].split("_");
//									int r = service.update("update com_camera_tb set sync_state=? where id = ? and upload_time=? ", new Object[]{1,Long.valueOf(rets[1]),Long.valueOf(rets[2])});
//								}
//							}
//						}
	            	 } catch (Exception e) {
							e.printStackTrace();
	            	 	}
				}
			}).start();
		}*/
		return res+"";
	}
	private String getComments(HttpServletRequest request) {
		Integer page = RequestUtil.getInteger(request, "page", 1);
		Long comId= RequestUtil.getLong(request, "comid", -1L);
		List<Map<String, Object>> comList =onlyService.getPage("select * from com_comment_tb where comid=? order by id desc",
				new Object[]{comId},page,20);
		List<Map<String, Object>> resultMap = new ArrayList<Map<String,Object>>();
		if(comList!=null&&comList.size()>0){
			for(Map<String, Object> map : comList){
				Map<String, Object> iMap = new HashMap<String, Object>();
				Long createTime = (Long)map.get("create_time");
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(createTime*1000);
				String times = TimeTools.getTime_MMdd_HHmm(createTime*1000);
				Long uid = (Long)map.get("uin");
				iMap.put("parkId",comId);// 评价的车场ID
				iMap.put("date", times.substring(0,5));// 评价日期：7-24
				iMap.put("week", "星期"+StringUtils.getWeek(calendar.get(Calendar.DAY_OF_WEEK)));//评价日期是星期几：星期四
				iMap.put("time", times.substring(6));// 评价的车场ID
				iMap.put("info",  map.get("comment"));//评价内容：巴拉巴拉一大串废话。。。
				iMap.put("user", getCarNumber(uid));// 评价者：车主（车牌号：京A***A111）
				resultMap.add(iMap);
			}
			return StringUtils.createJson(resultMap);
		}
		return "[]";
	}
	private String getDetail(HttpServletRequest request) {
		Long pid = RequestUtil.getLong(request, "comid", -1L);
		logger.info("comid:"+pid);
		String result = "{}";
		if(pid!=null&&pid>0){
			Map<String,Object> comMap = onlyService.getMap("select id,longitude lng,latitude lat,epay,company_name as name,mobile phone," +
					"parking_total as total,address addr,remarks as desc " +
					"from com_info_tb where id =?", new Object[]{pid});
			
			//查图片
			Map<String,Object> picMap = onlyService.getMap("select picurl from com_picturs_tb where comid=? order by id desc limit ?",
					new Object[]{pid,1});
			String picUrls = "";
			if(picMap!=null&&!picMap.isEmpty()){
				picUrls=(String)picMap.get("picurl");
			}
			//查空闲车位数
			Integer total = (Integer)comMap.get("total");
			Long orderCount = onlyService.getLong("select count(id) from order_tb where comid=? and state=? ", new Object[]{pid,0}) ;
			total = total-orderCount.intValue();
			if(total<0)
				total =0;
			//查价格
			String price = getPrice(pid);
			
			comMap.put("free", total);
			comMap.put("price", price);
			comMap.put("photo_url", "[\""+picUrls+"\"]");
//			result="[\""+comMap.get("id")+"\",\""+comMap.get("company_name")+"\"" +
//					",\""+comMap.get("longitude")+"\",\""+comMap.get("latitude")+"\"" +
//					",\""+total+"\",\""+price+"\"" +
//					",\""+comMap.get("parking_total")+"\",\""+comMap.get("address")+"\"" +
//					",\""+comMap.get("mobile")+"\",\""+comMap.get("epay")+"\"" +
//					",\""+comMap.get("remarks")+"\",[\""+picUrls+"\"]]";
			result = StringUtils.createJson(comMap);
		}
		return result.replace("null", "");
	}

	private String getParks(HttpServletRequest request) {
		Double lon = RequestUtil.getDouble(request, "lng", 0d);
		Double lat = RequestUtil.getDouble(request, "lat", 0d);
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
		List<Map<String, Object>> list = null;//daService.getPage(sql, null, 1, 20);
		list = onlyService.getAll(sql, params, 0, 0);
		Map<Long, Integer> shareNumMap = new HashMap<Long, Integer>();
		String preIds = "";
		List<Long> pids = new ArrayList<Long>();
		if(list!=null&&list.size()>0){
			for(Map<String, Object> map :list){
				String cname = (String)map.get("name");
				if(cname!=null){
					cname = cname.replace("\r", "").replace("\n", "").replace("\"", "").replace("“", "");
					map.put("name", cname);
				}
				Integer type = (Integer)map.get("type");
				pids.add((Long)map.get("id"));
				preIds +="?,";
				shareNumMap.put((Long)map.get("id"), (Integer)map.get("share_number"));
				//查询价格
				//Integer type = (Integer)map.get("type");
				if(type==0)//收费，查价格
					map.put("price", getPrice((Long)map.get("id")));
				else {//免费，返回-1
					map.put("price", "-1");
				}
			}
		}
		//查询空闲车位
		if(!shareNumMap.isEmpty()){
			params.clear();
			if(preIds.endsWith(","))
				preIds = preIds.substring(0,preIds.length()-1);
			params.add(0);
			params.addAll(pids);
			List<Map<String, Object>> busyMaps = onlyService.getAllMap("select count(ID) count,comid from order_tb " +
					"where state=? and comid in("+preIds+") group by comid", params);
			if(busyMaps!=null){
				for(Map<String, Object> bmap : busyMaps){
					Long comId = (Long)bmap.get("comid");
					Long count = (Long)bmap.get("count");
					Long scount = (shareNumMap.get(comId)-count);
					shareNumMap.put(comId,scount.intValue());
				}
			}
			for(Long comidLong : shareNumMap.keySet()){
				for(Map<String, Object> map :list){
					Long cid = (Long)map.get("id");
					if(comidLong.intValue()==cid.intValue()){
						map.put("free", shareNumMap.get(comidLong));
					}
				}
			}
		}
		String result = StringUtils.createJson(list);
		return result;
	}
	
	/**
	 * 取首小时价格
	 * @param parkId
	 * @return
	 */
	private String getPrice(Long parkId){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		//开始小时
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);
		List<Map<String,Object>> priceList=onlyService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,0});
		if(priceList==null||priceList.size()==0){//没有按时段策略
			//查按次策略
			priceList=onlyService.getAll("select * from price_tb where comid=? " +
					"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,1});
			if(priceList==null||priceList.size()==0){//没有按次策略，返回提示
				return "0元/次";
			}else {//有按次策略，直接返回一次的收费
				Map timeMap =priceList.get(0);
				Integer unit = (Integer)timeMap.get("unit");
				if(unit!=null&&unit>0){
					if(unit>60){
						String t = "";
						if(unit%60==0)
							t = unit/60+"小时";
						else
							t = unit/60+"小时 "+unit%60+"分钟";
						return timeMap.get("price")+"元/"+t;
					}else {
						return timeMap.get("price")+"元/"+unit+"分钟";
					}
				}else {
					return timeMap.get("price")+"元/次";
				}
			}
			//发短信给管理员，通过设置好价格
		}else {//从按时段价格策略中分拣出日间和夜间收费策略
			if(priceList.size()>0){
				//logger.info(priceList);
				for(Map map : priceList){
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					Double price = Double.valueOf(map.get("price")+"");
					Double fprice = Double.valueOf(map.get("fprice")+"");
					Integer ftime = (Integer)map.get("first_times");
					if(ftime!=null&&ftime>0){
						if(fprice>0)
							price = fprice;
					}
					if(btime<etime){//日间 
						if(bhour>=btime&&bhour<etime){
							return price+"元/"+map.get("unit")+"分钟";
						}
					}else {
						if(bhour>=btime||bhour<etime){
							return price+"元/"+map.get("unit")+"分钟";
						}
					}
				}
			}
		}
		return "0.0元/小时";
	}
	/**
	 * 查车牌号
	 * @param uin
	 * @return
	 */
	public String getCarNumber(Long uin){
		String carNumber="车牌号未知";//车主车牌号
		Map carNuberMap = onlyService.getPojo("select car_number from car_info_tb where uin=? and state=?  ", 
				new Object[]{uin,1});
		if(carNuberMap!=null&&carNuberMap.get("car_number")!=null&&!carNuberMap.get("car_number").toString().equals(""))
			carNumber = (String)carNuberMap.get("car_number");
		return carNumber;
	}	
}

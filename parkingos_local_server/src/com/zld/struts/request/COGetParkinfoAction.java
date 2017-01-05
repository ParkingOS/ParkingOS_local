package com.zld.struts.request;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

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
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

public class COGetParkinfoAction extends Action{
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	
	private Logger logger = Logger.getLogger(COGetParkinfoAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.getString(request, "action");
		
		if(action.equals("getproducts")){//包月产品
			String pids = RequestUtil.getString(request, "parkids");
			String mobile = RequestUtil.getString(request, "mobile");
			Double lon = RequestUtil.getDouble(request, "lon", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			List<Map<String, Object>> infoMList = new ArrayList<Map<String,Object>>();
			if(!pids.equals("")){
				String [] parkIds = pids.split(",");
				int length = pids.split(",").length;
				String preParams = "";
				Object [] values = new Object[length+3];
				values[0]=0;
				Calendar c = Calendar.getInstance();
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
				values[1]=c.getTimeInMillis()/1000;
				values[2]=0;
				for(int i=0;i<length;i++){
					if(i!=0)
						preParams +=",";
					preParams +="?";
					values[i+3]=Long.valueOf(parkIds[i]);
				}
				List<Map> productList = daService.getAll("select p.* ,c.company_name,c.longitude,c.latitude from product_package_tb p,com_info_tb c" +
						" where p.comid = c.id and p.state =? and p.limitday>? and p.remain_number>? and comid in("+preParams+")",values);
				Long uin = null;
				if(!mobile.equals("")){
					Map userMap = daService.getPojo("select * from user_info_Tb where mobile=? and auth_flag=? ", new Object[]{mobile,4});
					if(userMap!=null)
						uin =(Long) userMap.get("id");
				}
				List<Map> myProductId=null;
				if(uin!=null){
					myProductId= daService.getAll("select pid from carower_product where uin=?  ", new Object[]{uin});
				}
				String json = createJson(productList, myProductId, lat, lon);
				AjaxUtil.ajaxOutput(response, json);
			}
			//http://192.168.199.240/zld/getpark.do?lon=0&lat=0&action=getproducts&parkids=3,1327&mobile=15801482643
		}else if(action.equals("getpdetail")){//包月产品详情
			Long pid = RequestUtil.getLong(request, "pid", -1L);
			String mobile = RequestUtil.getString(request, "mobile");
			Map productMap = daService.getMap("select comid,resume,limitday from product_package_tb where id=? ",new Object[]{pid});
			Long comid  = -1L;
			String resume = "";
			Long limitday = System.currentTimeMillis()/1000+365*24*60*60;
			if(productMap!=null){
				comid = (Long)productMap.get("comid");
				resume = productMap.get("resume")+"";
				resume = resume.equals("null")?"":resume;
				Long lday = (Long)productMap.get("limitday");
				if(lday!=null&&lday>0)
					limitday = lday;
			}
			String result  = "{\"resume\":\"\",\"parkinfo\":{}}";
			if(comid!=-1){//仅返回停车场的信息，产品信息在上一个接口中已经给了
				/*
				 * "id":"121"
				"address":"dfadfadfadfasf",
				"mobile":"djfalsdfa",
				"praiseNum":"22",
				"disparageNum":"23",
				"commentnum":"234",
				"picurls",["picurls/kkll.jpg"],
				"resume":"很好"
				 */
				//查停车场信息
				Map parkMap = daService.getMap("select * from com_info_tb where id=?", new Object[]{comid});
				
				if(parkMap!=null){
					
					//查评价
					int praiseNum=0;
					int disparageNum=0;
					//查评价表，统计赞和贬数量
					List<Map<String, Object>> praiseMap = daService.getAll("select * from com_praise_tb where comid=?",new Object[]{comid});
					//logger.info(praiseMap);
					if(praiseMap!=null&&!praiseMap.isEmpty()){
						for(Map<String, Object> map :praiseMap){
							Long uid = (Long) map.get("uin");
							Integer praise = (Integer)map.get("praise");
							if(praise==0)
								disparageNum++;
							else
								praiseNum++;
						}
					}
					//查询帐号
					Long uin = null;
					if(!mobile.equals("")){
						Map userMap = daService.getPojo("select * from user_info_Tb where mobile=? and auth_flag=? ", new Object[]{mobile,4});
						if(userMap!=null)
							uin =(Long) userMap.get("id");
					}
					//查询是否已购买
					Long count = daService.getLong("select count(*) from carower_product where uin=? and pid=? ", new Object[]{uin,pid});
					//查评论数
					Long commonNum = daService.getLong("select count(*) from com_comment_tb where comid=?", new Object[]{comid});
					
					result = "{\"resume\":\""+resume+"\",\"limitday\":\""
							+limitday+"\",\"isbuy\":\""+count+"\",\"parkinfo\":{";
					result +="\"company_name\":\""+parkMap.get("company_name")+"\"";
					result +=",\"address\":\""+parkMap.get("address")+"\"";
					result +=",\"mobile\":\""+parkMap.get("mobile")+"\"";
					result +=",\"id\":\""+comid+"\"";
					result +=",\"praiseNum\":\""+praiseNum+"\"";
					result +=",\"disparageNum\":\""+disparageNum+"\"";
					result +=",\"longitude\":\""+parkMap.get("longitude")+"\"";
					result +=",\"latitude\":\""+parkMap.get("latitude")+"\"";
					result +=",\"commentnum\":\""+commonNum+"\"}}";
					//result +=",\"resume\":\""+resume+"\"}}";
					
				}
			}
			//http://192.168.199.240/zld/getpark.do?action=getpdetail&pid=5&mobile=15375242041
			AjaxUtil.ajaxOutput(response, result);
		}
		else if(action.equals("prepark")){//预定车位
			
		}else if(action.equals("getlocal")){//根据经纬度取车场信息
			//预定到达时间,0：当前位置，其它是预计到达搜索位置的时间
			Integer time = RequestUtil.getInteger(request, "time", 0);
			Double lon = RequestUtil.getDouble(request, "lon", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			Integer payable = RequestUtil.getInteger(request, "payable", 0);//0不分是否可支付，1返回可支付的车场
			String dist ="500";
			if(lon==0||lat==0)
				return null;
			//500米经纬度偏移量
	//		double lon1 = 0.009536;
	//		double lat1 = 0.007232;
			//取符合条件(500M)的停车场
			List<Map<String, Object>> list = getParkList(lat,lon,payable);
			
//			if(list==null||list.isEmpty()){
//				dist="1000";
//				list = getPark1kmList(lat, lon,payable);
//			}
			
			
			String info = "{}";//"[";
			double d = 100d;
			Integer total = 0;
			String parkName ="";
			double slon = 0.0;
			double slat = 0.0; 
			Integer epay=0;
			Long parkId=-1L;
			Integer snumber = 0;
			Integer isMonthPay=0;
			List<Object> monthList = new ArrayList<Object>();
			List<Object> bookList = new ArrayList<Object>();
			List<Object> parkIds = new ArrayList<Object>();
			String preParams = "";
			
			
			if(list!=null&&list.size()>0){
				info ="{\"dist\":\""+dist+"\",";
				for(int i=0;i<list.size();i++){
					Map map =(Map) list.get(i);
					parkIds.add(map.get("id"));
					Integer monthlypay =(Integer)map.get("monthlypay");
					Integer book =(Integer)map.get("book");
					if(monthlypay==1)
						monthList.add(map.get("id"));
					if(book==1)
						bookList.add(map.get("id")+"");
					preParams+=",?";
					total +=(Integer)map.get("share_number");
					double lon2 = Double.valueOf(map.get("longitude")+"");
					double lat2 = Double.valueOf(map.get("latitude")+"");
					double distance = StringUtils.distanceByLnglat(lon,lat,lon2,lat2);
					if(distance<d){
						d=distance;
						parkName = (String)map.get("company_name");
						slon = lon2;
						slat=  lat2;
						epay = (Integer)map.get("epay");
						parkId = (Long)map.get("id");
						isMonthPay = (Integer)map.get("monthlypay");
						snumber =(Integer)map.get("share_number");
					}
				}
				if(preParams.startsWith(","))
					preParams = preParams.substring(1);
				List<Object> plist = new ArrayList<Object>();
				Calendar c = Calendar.getInstance();
				c.set(Calendar.MONTH, c.get(Calendar.MONTH)+1);
				plist.add(c.getTimeInMillis()/1000);
				plist.add(0);
				plist.addAll(parkIds);
				//查询有效包月产品的车场
				List productParkList = daService.getAllMap("select count(*) count ,comid from product_package_tb where limitday > ? and remain_number>? and comid in("+preParams+")group by comid ", plist);
				
				parkIds.add(0,0);
				
				int b = 2;//充足
				Long snum  =0L;
				snum  = daService.getLong("select count(*) count  from order_tb where state=? and comid =? ",new Object[]{0,parkId});
				if(time==0){//当前位置
					//查所有车场占用数
					Long unumber  = daService.getCount("select count(*) count  from order_tb where state=? and comid in("+preParams+")",parkIds);
					Double dnum = Double.valueOf(total-unumber)/Double.valueOf(total);
					if(dnum<0.2&&dnum>=0.05)
						b=1;//较少
					else if(dnum<0.05)
						b=0;//紧张
				}else{//预计时间后的车位
					Long ntime = System.currentTimeMillis()/1000;
					ntime  = ntime +time*60;
					ntime = ntime - ntime%(15*60) + (15*60);
					Long etime = ntime - 30*24*60*60;
					List<Object> tList = new ArrayList<Object>();
					String pt = "";
					for(Long i =ntime;i>etime; i=i-24*60*60){
						tList.add(i);
						if(pt.equals(""))
							pt +="?";
						else
							pt +=",?";
					}
					parkIds.remove(0);
					tList.addAll(parkIds);
					List paList = daService.getAllMap("select * from park_anlysis_tb " +
							" where create_time in("+pt+") and comid in("+preParams+") order by create_time ,comid",tList);
//					logger.info(paList);
//					anlyParkFree(paList);
//					logger.info(paList);
					b = anlyParkFree(paList);
				}
				//logger.info(parkIds);
				
				String monthids="[]";
				if(productParkList!=null&&!productParkList.isEmpty()){
					monthids = "[";
					for(int k =0;k<productParkList.size();k++){
						Map pMap = (Map)productParkList.get(k);
						int count = Integer.valueOf(pMap.get("count")+"");
						if(count>0){
							if(!monthids.equals("["))
								monthids +=",";
							monthids +=pMap.get("comid");
						}
					}
					monthids +="]";
				}
//				if(monthList.size()>0){
//					monthids = "[";
//					for(Object id :  monthList){
//						if(!monthids.equals("["))
//							monthids +=",";
//						monthids +=id;
//					}
//					monthids +="]";
//				}
				String bookids="[]";
				if(bookList.size()>0){
					bookids = "[";
					for(Object id :  bookList){
						if(!bookids.equals("["))
							bookids +=",";
						bookids +=id;
					}
					bookids +="]";
				}
				
				String price = getPrice(parkId);
				Long free=(snumber-snum);
				if(free<0)
					free=0L;
				info +="\"freeinfo\":\""+b+"\",\"monthids\":"+monthids+",\"bookids\":"+bookids+",\"suggest\":\""+parkName+"\",\"snumber\":\""+free+"\",\"lon\":\""+slon+"\",\"lat\":\""+slat+"\",\"id\":\""+parkId+"\",\"price\":\""+price+"\",\"epay\":\""+epay+"\",\"monthlypay\":\""+isMonthPay+"\"}";
			}
			//info +="]";
			logger.info(">>>>payable:"+payable+">>>>"+info);
			AjaxUtil.ajaxOutput(response, info);
			//http://192.168.199.240/zld/getpark.do?lon=116.31363&lat=40.041917&action=getlocal&time=24
		}   //http://192.168.199.240/zld/getpark.do?action=getlocal&lon=116.31354&lat=39.989200&time=24
		else if(action.equals("get2kpark")){
			Double lon = RequestUtil.getDouble(request, "lng", 0d);
			Double lat = RequestUtil.getDouble(request, "lat", 0d);
			Integer payable = RequestUtil.getInteger(request, "payable", 0);//0不分是否可支付，1返回可支付的车场
			List<Map<String, Object>> list = publicMethods.getPark2kmList(lat,lon,payable);
			//System.out.println(list.size()+",payable:"+payable);
			List<Long> pids = new ArrayList<Long>();
			Map<Long, Integer> shareNumMap = new HashMap<Long, Integer>();
			String preIds = "";
			double d  =0d;
			Long suggestId = 0L;
			if(list!=null&&list.size()>0){
				logger.info(">>>>>>>>>get2kpark,lng="+lon+",lat="+lat+",return size:"+list.size());
				for(Map<String, Object> map :list){
					String cname = (String)map.get("name");
					if(cname!=null){
						cname = cname.replace("\r", "").replace("\n", "").replace("\"", "").replace("“", "");
						map.put("name", cname);
					}
					Integer epay = (Integer)map.get("epay");
					Integer type = (Integer)map.get("type");
					Integer isfixed = (Integer)map.get("isfixed");
					map.put("epay", epay*isfixed);
					map.remove("isfixed");
					
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
					map.remove("share_number");
					if(payable==1){//返回可支付的车场
						if(type==1||epay==0||isfixed==0)
							continue;
					}
					/*else {
						if(isfixed==0)
							continue;
					}*/
					double lon2 = Double.valueOf(map.get("lng")+"");
					double lat2 = Double.valueOf(map.get("lat")+"");
					double distance = StringUtils.distanceByLnglat(lon,lat,lon2,lat2);
					//System.out.println(distance+":id:"+map.get("id")+":"+map.get("name"));
					if(d==0||distance<d){
						d=distance;
						suggestId= (Long)map.get("id");
					}
					
				}
			}
			//查询空闲车位
			if(!shareNumMap.isEmpty()){
				List<Object> params = new ArrayList<Object>();
				if(preIds.endsWith(","))
					preIds = preIds.substring(0,preIds.length()-1);
				params.add(0);
				params.addAll(pids);
				List<Map<String, Object>> busyMaps = daService.getAllMap("select count(ID) count,comid from order_tb " +
						"where state=? and comid in("+preIds+") group by comid", params);
				if(busyMaps!=null){
					for(Map<String, Object> bmap : busyMaps){
						Long comId = (Long)bmap.get("comid");
						Long count = (Long)bmap.get("count");
						Long scount = (shareNumMap.get(comId)-count);
						scount = scount<0?1:scount;
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
			//daService.getLong("select count(*) count  from order_tb where state=? and comid =? ",new Object[]{0,parkId});
			String reslut = StringUtils.createJson(list);
			reslut = reslut.replace("null", "");
			int lack = 1;
			if(list!=null&&list.size()>5)
				lack=0;
			reslut ="{\"suggid\":\""+suggestId+"\",\"lack\":\""+lack+"\",\"data\":"+reslut+"}";
			//logger.info(reslut);
			AjaxUtil.ajaxOutput(response, reslut);
			//http://192.168.199.240/zld/getpark.do?action=get2kpark&lng=116.322747&lat=39.989056
			
		}
		return null;
	}
	
	private String createJson(List<Map> productList,List<Map> myProductId,Double lat,Double lon ){
		Map<Long, Map<String, Object>> comMap = new HashMap<Long, Map<String,Object>>();
		for(Map map :productList){
			Map<String, Object> infoMap = new HashMap<String, Object>();
			Double _lat = Double.valueOf(map.get("latitude")+"");
			Double _lon = Double.valueOf(map.get("longitude")+"");
			double distance = -1d;
			if(lat!=0&&lon!=0){
				distance = StringUtils.distanceByLnglat(lon,lat,_lon,_lat);
			}
			Long pid = (Long)map.get("id");
			String isBuy = "0";
			if(myProductId!=null&&myProductId.size()>0){
				for(Map map2: myProductId){
					Long _pid = (Long)map2.get("pid");
					if(pid.intValue()==_pid.intValue()){
						isBuy="1";
						break;
					}
				}
			}
			String bmin = map.get("bmin")+"";
			String emin = map.get("emin")+"";
			if(bmin.equals("null")||bmin.equals("0"))
				bmin="00";
			if(emin.equals("null")||emin.equals("0"))
				emin = "00";
			Long comid = (Long)map.get("comid"); 
			//查图片
			List<Map<String, Object>> picMap = daService.getAll("select picurl from com_picturs_tb where comid=? order by id desc ",
					new Object[]{comid});
			String picUrls = "[";
			if(picMap!=null&&!picMap.isEmpty()){
				for(Map<String, Object> pmap : picMap){
					if(picUrls.equals("["))
						picUrls += "\""+pmap.get("picurl")+"\"";
					else {
						picUrls += ",\""+pmap.get("picurl")+"\"";
					}
				}
			}
			picUrls += "]";
			
			infoMap.put("id",pid);
			infoMap.put("name", map.get("p_name"));//产品名称
			infoMap.put("price",map.get("price"));//现价
			infoMap.put("price0",map.get("old_price"));//原价
			infoMap.put("limittime", map.get("b_time")+":"+bmin+"-"+map.get("e_time")+":"+emin);//有效时段
			infoMap.put("number",map.get("remain_number"));
			infoMap.put("limitday",map.get("limitday")==null?(System.currentTimeMillis()/1000+365*24*60*60):map.get("limitday"));
			infoMap.put("isbuy", isBuy);//是否已购买，0否，1是
			infoMap.put("type", map.get("type"));//-- 0:全天，1夜间，2日间
			infoMap.put("reserved", map.get("reserved"));// 是否固定车位：0不固定；1固定
			infoMap.put("photoUrl", picUrls);//图片数组
			
			String monthinfo = StringUtils.createJson(infoMap);
			if(comMap.containsKey(comid)){
				Map<String, Object> map2 = comMap.get(comid);
				String minfo = (String)map2.get("monthProducts");
				minfo = minfo.substring(0,minfo.length()-1);
				minfo +=","+monthinfo+"]";
				map2.put("monthProducts", minfo);
			}else {
				Map<String, Object> map2 =new HashMap<String, Object>();
				map2.put("company_name", map.get("company_name"));
				map2.put("distance", StringUtils.formatDouble(distance));
				map2.put("monthProducts","["+ monthinfo+"]");
				comMap.put(comid, map2);
			}
			
			//infoMap.put("resume", map.get("resume"));
		}
		List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
		for(Long key : comMap.keySet()){
			list.add(comMap.get(key));
		}
		
		Collections.sort(list,new LocalSort());
		return StringUtils.getJson(list);
	//[{"company_name": "嘉华大厦停车场","distance":"300","monthProducts": 
	//[{"id": "12","limittime": "00:00-24:00","name": "xxx全天包月",
    //"price": "300","price0": "400","type": "0"}]},{}]
	//	return null;
	}
	
	
	
	private int anlyParkFree(List list){
		for(int i=0;i<list.size();i++){
			Map map =(Map)list.get(i);
			Integer share = (Integer)map.get("share_count");
			
			if(share==0){
				Long ctime = (Long)map.get("create_time");
				Long comid = (Long)map.get("comid");
				Map<String, Object> lastMap = daService.getMap("select share_count from park_anlysis_tb where comid=? and create_time" +
						"<? and create_time >? and share_count >? order by create_time desc limit ?", new Object[]{comid,ctime,ctime-24*60*60,0,1});
				if(lastMap!=null&&lastMap.get("share_count")!=null)
					map.put("share_count", lastMap.get("share_count"));
			}
			//map.put("free", Integer.valueOf(map.get("share_count")+"")-Integer.valueOf(map.get("used_count")+""));
		}
		int total =0;
		int used = 0;
		for(int j=0;j<list.size();j++){
			Map map =(Map)list.get(j);
			Integer share = (Integer)map.get("share_count");
			Integer use = (Integer)map.get("used_count");
			if(share>0){
				total+=share;
				used +=use;
			}
		}
		Double dnum = Double.valueOf(total-used)/Double.valueOf(total);
		if(dnum<0.2&&dnum>=0.05)
			return 1;//较少
		else if(dnum<0.05)
			return 0;//紧张
		return 2;
		
		
	}
	
	/**
	 * 取停车场
	 * @param lat
	 * @param lon
	 * @return 500以内的停车场
	 */
	private List<Map<String, Object>> getParkList(Double lat,Double lon,int payable){
//		payable=1;//强制过滤不可支付车场
//		double lon1 = 0.008036;
//		double lat1 = 0.005032; 
		double lon1 = 0.009446;
		double lat1 = 0.007232;
		String sql = "select * from com_info_tb where longitude between ? and ? and latitude between ? and ? and state=? and isview=?";
		List<Object> params = new ArrayList<Object>();
		params.add(lon-lon1);
		params.add(lon+lon1);
		params.add(lat-lat1);
		params.add(lat+lat1);
		params.add(0);
		params.add(1);
		
		if(payable==1){
			sql +=" and is_hasparker=? and epay=? ";
			params.add(1);
			params.add(1);
		}
		
		List list = null;//daService.getPage(sql, null, 1, 20);
		list = daService.getAll(sql, params, 0, 0);
		return list;
	}
	

	/**
	 * 取停车场
	 * @param lat
	 * @param lon
	 * @return 1000以内的停车场
	 */
	private List<Map<String, Object>> getPark1kmList(Double lat,Double lon,int payable){
//		payable=1;//强制过滤不可支付车场
		double lon1 = 0.009446*2;
		double lat1 = 0.007232*2;
		String sql = "select * from com_info_tb where longitude between ? and ? and latitude between ? and ? and state=? and isview=?";
		List<Object> params = new ArrayList<Object>();
		params.add(lon-lon1);
		params.add(lon+lon1);
		params.add(lat-lat1);
		params.add(lat+lat1);
		params.add(0);
		params.add(1);
		if(payable==1){
			sql +=" and is_hasparker=? and epay=? ";
			params.add(1);
			params.add(1);
		}
		List list = null;//daService.getPage(sql, null, 1, 20);
		list = daService.getAll(sql, params, 0, 0);
		return list;
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
		List<Map> priceList=daService.getAll("select * from price_tb where comid=? " +
				"and state=? and pay_type=? order by id desc", new Object[]{parkId,0,0});
		if(priceList==null||priceList.size()==0){//没有按时段策略
			//查按次策略
			priceList=daService.getAll("select * from price_tb where comid=? " +
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
		

}

class LocalSort implements Comparator<Map<String, Object>>{
	public int compare(Map<String, Object> o1, Map<String, Object> o2) {
		Double d1 = Double.valueOf(o1.get("distance")+"")*1000;
		Double d2 = Double.valueOf(o2.get("distance")+"")*1000;
		return d1.intValue()-d2.intValue();
	}
	
}

package com.zld.struts.request;

import java.util.ArrayList;
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
import com.zld.impl.PublicMethods;
import com.zld.service.DataBaseService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;
/**
 * 根据经纬度计算周围500米范围内停车场
 * @author Administrator
 *
 */
public class GetParkByLLAction extends Action{
	
	@Autowired
	private DataBaseService daService;
	@Autowired
	private PublicMethods publicMethods;
	private Logger logger = Logger.getLogger(GetParkByLLAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Double lon = RequestUtil.getDouble(request, "lon", 0d);
		Double lat = RequestUtil.getDouble(request, "lat", 0d);
		if(lon==0||lat==0)
			return null;
		//500米经纬度偏移量
//		double lon1 = 0.009536;
//		double lat1 = 0.007232; 
		double lon1 = 0.008036;
		double lat1 = 0.005032; 
		String sql = "select * from com_info_tb where longitude between ? and ? and latitude between ? and ? and state=?";
		List<Object> params = new ArrayList<Object>();
		params.add(lon-lon1);
		params.add(lon+lon1);
		params.add(lat-lat1);
		params.add(lat+lat1);
		params.add(0);
		List list = null;//daService.getPage(sql, null, 1, 20);
		list = daService.getAll(sql, params, 0, 0);
		String info = "{}";//"[";
		double d = 100d;
		Integer total = 0;
		String parkName ="";
		double slon = 0.0;
		double slat = 0.0; 
		Long parkId=-1L;
		Integer snumber = 0;
		if(list!=null&&list.size()>0){
			info ="{\"count\":\""+list.size()+"\",";
			for(int i=0;i<list.size();i++){
				Map map =(Map) list.get(i);
				total +=(Integer)map.get("share_number");
				double lon2 = Double.valueOf(map.get("longitude")+"");
				double lat2 = Double.valueOf(map.get("latitude")+"");
				double distance = StringUtils.distanceByLnglat(lon,lat,lon2,lat2);
				if(distance<d){
					d=distance;
					parkName = (String)map.get("company_name");
					slon = lon2;
					slat=  lat2;
					parkId = (Long)map.get("id");
					snumber =(Integer)map.get("share_number");
				}
			}
			Long unumber  = daService.getLong("select count(*) count  from order_tb where state=? and comid =?",new Object[]{0,parkId});
			
			//String price = getPrice(parkId);
			
			Map priceMap = publicMethods.getPriceMap(parkId);
			String _price = "0";
			if(priceMap!=null){
				int pay_type = (Integer)priceMap.get("pay_type");
				Double price = Double.valueOf(priceMap.get("price")+"");
				_price = price+"元/次";
				Integer unit = (Integer)priceMap.get("unit");
				if(pay_type==0){//按时段
					_price =price+"元/"+unit+"分钟";
				}else {
					if(unit!=null&&unit>0){
						if(unit>60){
							String t = "";
							if(unit%60==0)
								t = unit/60+"小时";
							else
								t = unit/60+"小时 "+unit%60+"分钟";
							_price =priceMap.get("price")+"元/"+t;
						}else {
							_price = priceMap.get("price")+"元/"+unit+"分钟";
						}
					}else {
						_price = priceMap.get("price")+"元/次";
					}
				}
			}
			Long free=(snumber-unumber);
			if(free<0)
				free=0L;
			info +="\"total\":\""+total+"\",\"suggest\":\""+parkName+"\",\"snumber\":\""+free+"\",\"lon\":\""+slon+"\",\"lat\":\""+slat+"\",\"id\":\""+parkId+"\",\"price\":\""+_price+"\"}";
		}
		
		//info +="]";
		//System.out.println(info);
		AjaxUtil.ajaxOutput(response, info);
		//http://127.0.0.1/zld/searchpark.do?lon=116.318512&lat=40.042214
		return null;
	}
	/**
	 * 取首小时价格
	 * @param parkId
	 * @return
	 */
	/*private String getPrice(Long parkId){
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
				return timeMap.get("price")+"元/次";
			}
			//发短信给管理员，通过设置好价格
		}else {//从按时段价格策略中分拣出日间和夜间收费策略
			if(priceList.size()>0){
				System.out.println(priceList);
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
	}*/
/*	private String getPrice(Long parkId){
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
				return timeMap.get("price")+"/元次";
			}
			//发短信给管理员，通过设置好价格
		}else {//从按时段价格策略中分拣出日间和夜间收费策略
			if(priceList.size()>0){
				//System.out.println(priceList);
				for(Map map : priceList){
					Integer btime = (Integer)map.get("b_time");
					Integer etime = (Integer)map.get("e_time");
					if(btime<etime){//日间 
						if(bhour>=btime&&bhour<=etime){
							
							return map.get("price")+"元/"+map.get("unit")+"分钟";
						}
					}else {
						if(bhour>btime||bhour<etime){
							return map.get("price")+"元/"+map.get("unit")+"分钟";
						}
					}
				}
			}
		}
		return "0.0元/小时";
	}*/

}
package com.zld.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import com.zld.CustomDefind;

public class CountPrice {
	
	
	/**
	 * 开封火车站计价
	 * 3-7  5元
	 * 7-22 5元
	 * 22-3   10元
	 */
	public static Map<String, Object> getPrice(Long start,Long end){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long dur = end-start;
		Long s = start;
		Long e = end;
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Integer sh = calendar.get(Calendar.HOUR_OF_DAY);//订单开始小时
		
		Double price =0.0;
		
		Long t1 = 4*60*60L; //3-7  5
		Long t2 = 15*60*60L; //7-22 5
		Long t3 = 5*60*60L; //22-3 10

		Long day = (end-start)/(24*60*60);
		if(day>0){//整天数，每天20元
			price = day*20.0;
			end = end - day*24*60*60;
		}
		if(sh<3){//0-2开始
			calendar.set(Calendar.HOUR_OF_DAY, 3);
			Long tb = calendar.getTimeInMillis()/1000;
			price +=10.0;
			if(end>tb){//结束时间大于7点
				start=tb;
				price+=5.0;
				if(end-start>t1){
					start = start+t1;
					price +=5.0;
					if(end-start>t2){
						price +=10.0;
					}
				}
			}
		}else if(sh>=22){//22开始
			calendar.set(Calendar.HOUR_OF_DAY, 3);
			calendar.add(Calendar.DAY_OF_YEAR, 1);
			Long tb = calendar.getTimeInMillis()/1000;
			price +=10.0;
			if(end>tb){//结束时间大于7点
				start=tb;
				price+=5.0;
				if(end-start>t1){
					start = start+t1;
					price +=5.0;
					if(end-start>t2){
						price +=10.0;
					}
				}
			}
		}else if(sh>=3&&sh<7){//在3-7点之间
			calendar.set(Calendar.HOUR_OF_DAY,7);
			Long tb = calendar.getTimeInMillis()/1000;
			price +=5.0;
			if(end>tb){
				start=tb;
				price+=5.0;
				if(end-start>t2){
					price+=10.0;
					start = start+t2;
					if(end-start>t3)
						price +=5.0;
				}
			}
			
		}else {//7-22
			calendar.set(Calendar.HOUR_OF_DAY, 22);
			Long tb = calendar.getTimeInMillis()/1000;
			price +=5.0;
			if(end>tb){
				start=tb;
				price+=10.0;
				if(end-start>t3){
					price+=5.0;
					start = start+t3;
					if(end-start>t1)
						price +=5.0;
				}
			}
		}
		
		resultMap.put("total", StringUtils.formatDouble((price)));
		resultMap.put("discount",0);//折扣
		resultMap.put("duration", StringUtils.getTimeString(dur));
		resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(s*1000).substring(6));
		resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(e*1000).substring(6));
		resultMap.put("collect", StringUtils.formatDouble(price));
		System.err.println("three>>>"+TimeTools.getTime_yyyyMMdd_HHmmss(s*1000)+"  ,end:"+TimeTools.getTime_yyyyMMdd_HHmmss(e*1000)+" ,result:"+resultMap);
		return resultMap;
	}
	

	/**
	 * 计算停车金额
	 * @param start 开始utc时间
	 * @param end 结束utc时间
	 * @param priceMap 时段计费1
	 * @param priceMap2 时段计费2 //分段计费时必须有，没有时，计费1变为全天的
	 * @return
	 */
	public static Map<String, Object> getAccount(Long start,Long end,Map dayMap,Map nightMap,double minPriceUnit,Map assistPrice){
//		if(dayMap!=null){
//			return getTaiZhouPrice2(start, end,dayMap,assistPrice);
//		}
		Long dur = end-start;
		double total = 0d;
		Object dtotal24 = -1;
		Object ntotal24 = -1;
		if(dayMap!=null)
			dtotal24 = dayMap.get("total24");
		if(nightMap!=null)
			ntotal24= nightMap.get("total24");
		double total24 = -1;
		Boolean b= StringUtils.isDouble(dtotal24+"");
		if(b){
			total24 = Double.parseDouble(dtotal24+"");
		}
		if(total24<=0){
			System.out.println(ntotal24);
			b= StringUtils.isDouble(ntotal24+"");
			if(b){
				total24 = Double.parseDouble(ntotal24+"");
			}
		}
		if(b&&total24>0){
			if(end-start>24*3600){
				long end1 =start+24*3600;
				Double d = Double.parseDouble(getAccount24(start, end1, dayMap, nightMap, 0.0, assistPrice).get("collect")+"");
				start = end1;
				if(dayMap!=null){
					dayMap.put("first_times", 0);
					dayMap.put("fprice", 0);
					dayMap.put("free_time", 0);
				}
				if(nightMap!=null){
					nightMap.put("first_times", 0);
					nightMap.put("fprice", 0);
					nightMap.put("free_time", 0);
				}
				if(d>total24){//第一个24（包含首优惠）>封顶价 那么后面的每个24都只收封顶价
					total+=total24;
					Long e = (end - start)/(24*3600);
					total+=(e*total24);
					start = end1+e*24*3600;
				}else{
					total+=d;
					long times = (end-start)/(24*3600);
					if(times>0){
						end1=start+24*3600;
						Double d2 = Double.parseDouble(getAccount24(start, end1, dayMap, nightMap, 0.0, assistPrice).get("collect")+"");
						if(d2>total24){
							total+=(total24*times);
						}else{
							total+=(d2*times);
						}
						start = start+24*3600*times;
					}
				}
			}
		}
		Double d = 0.0;
		if(end>start)
			d = Double.parseDouble(getAccount24(start, end, dayMap, nightMap, 0.0, assistPrice).get("collect")+"");
		if(total24>0){
			total += d>total24?total24:d;
		}else{
			total+=d;
		}
		if(minPriceUnit!=0.00){
			total = dealPrice(total,minPriceUnit);
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		resultMap.put("total", StringUtils.formatDouble((total)));
		resultMap.put("discount",0);//折扣
		resultMap.put("duration", StringUtils.getTimeString(dur));
		resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
		resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
		resultMap.put("collect", StringUtils.formatDouble(total));
		return resultMap;
	}
	/**
	 * 台州计价分支2：
	 * 进场免费3分钟，20分钟内5元，4小时内10元，8小时内15元，12小时内20元，16小时内25元，20小时内30元，24小时内35元；
	 * 超过24小时，计价35+同上费率标准
	 * @param start
	 * @param end
	 * @param dayMap
	 * @param assistPrice
	 * @return
	 */
	private static Map<String, Object> getTaiZhouPrice2(Long start, Long end,Map dayMap,Map assistPrice) {
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Double total24 = StringUtils.formatDouble(dayMap.get("total24"));//一天的封顶价格，到24点
		Double price = 0.0;//总价格
		Long dur = end-start;
		Long days = dur/(24*3600);
		start = start + days*24*3600;
		price = days*total24;
		price +=getAccountTaizhou2(start, end, dayMap, assistPrice);

		resultMap.put("total", StringUtils.formatDouble((price)));
		resultMap.put("discount",0);//折扣
		resultMap.put("duration", StringUtils.getTimeString(end-start));
		resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
		resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
		resultMap.put("collect", StringUtils.formatDouble(price));
		System.err.println(">>>"+dayMap+",begin:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+"  ,end:"+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000)+"  ,result:"+resultMap);
		return resultMap;
	}

	private static Double getAccountTaizhou2(Long start, Long end, Map dayMap,Map assistPrice) {
		Double assPrice =0.0;
		Double total24 = StringUtils.formatDouble(dayMap.get("total24"));//一天的封顶价格，到24点
		Integer dft = (Integer)dayMap.get("free_time"); // 日间免费时长
		Double  dayPirce = Double.valueOf(dayMap.get("price")+"");//日间价格
		Integer dayUnit = (Integer) dayMap.get("unit");//日间计费单位
		Integer dfpt = (Integer)dayMap.get("fpay_type");//超过免费时长 1免费 0收费
		Integer countless=(Integer)dayMap.get("countless");//零头计费时长
		Long dur = (end-start)/60;//分钟数
		if(dur<=dft)//小于免费时长，返回 0；
			return assPrice;
		else{//大于 免费时长
			if(dfpt==1)//免费，减去免费时长
				start = start+dft;
		}
		if(assistPrice!=null){
			Long unit = Long.valueOf(assistPrice.get("assist_unit")+"");
			if(dur<=unit){
//				end = end -unit;
				assPrice =StringUtils.formatDouble(assistPrice.get("assist_price")+"");
				return assPrice;
			}else if(dur>unit){
				assPrice =StringUtils.formatDouble(assistPrice.get("assist_price")+"");
			}
		}
		Double price =assPrice;//总价格
		price +=(dur/dayUnit)*dayPirce;
		if(dur%dayUnit>countless)
			price +=dayPirce;
		price = price>total24?total24:price;
		return price;
	}
	public static Map<String, Object> getAccount24(Long start,Long end,Map dayMap,Map nightMap,double minPriceUnit,Map assistPrice){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Double price = 0D;//返回的总价
		Double price1 = null;//辅助总价，不为null则这个价格是最终价格
		Double price2 = 0D;//超过辅助价格时长的价格，最后加上
		Double  dayPirce = Double.valueOf(dayMap.get("price")+"");//日间价格
		Integer dayUnit = (Integer) dayMap.get("unit");//日间计费单位
		Integer dftime = (Integer) dayMap.get("first_times");//日间首优惠时长
		Double  dfprice = Double.valueOf(dayMap.get("fprice")+"");//日间首优惠时长价格
		Integer dft = (Integer)dayMap.get("free_time"); // 日间免费时长
		Integer dfpt = (Integer)dayMap.get("fpay_type");//超过免费时长 1免费 0收费
		Integer isFullDayTime =(Integer)dayMap.get("is_fulldaytime");// 是否补足日间时长 0补全（默认）1不补全
		
		//开封火车站计价
		if(dayMap!=null){
			Long cid = (Long)dayMap.get("comid");
			String threePrice = CustomDefind.getValue("THREEPRICE");
			if(cid!=null&&threePrice!=null&&cid.equals(Long.valueOf(threePrice)))
				return getPrice(start, end);
		}
		
		if(dayMap!=null&&nightMap!=null){//辅助价格只支持全天价格
		
		}else{
			if(assistPrice!=null){//有辅助价格
				Long unit = Long.valueOf(assistPrice.get("assist_unit")+"");
				Long dur = (end-start)/60;
				assistPrice.get("assist_price");
				if(dfpt==1)//超过免费时长免费
					unit = unit+dft;
				if(dur>dft&&dur<=unit){//停车时长大于免费时长并且停车时长小于辅助时长
					price1 = Double.valueOf(assistPrice.get("assist_price")+"");
				}else if(dur<=dft){//停车时长小于免费时长
					price1 = 0D;
				}else{//停车时长大于辅助时长（每*分钟*元）
					price2=Double.valueOf(assistPrice.get("assist_price")+"");
					start = start+unit*60;
					dft = 0;
					dftime=0;
					dfprice=0D;
				}
			}
		}
		//默认夜间的参数与日间一样
		Double  nigthPrice = dayPirce;//夜间间价格
		Integer nightUnit = dayUnit;
		Integer nftime = dftime;//夜间首优惠时长
		Double  nfprice =dfprice;//夜间首优惠时长价格
		Integer nft = dft;// 夜间免费时长
		Integer nfpt = dfpt;// 1免费 0收费
		//Integer isFullNightTime =isFullDayTime;// 是否补足夜间时长 0补全（默认）1不补全
		
		Integer btime = (Integer)dayMap.get("b_time");
		Integer etime = (Integer)dayMap.get("e_time");
		
		if(nightMap==null){//没有夜间价格策略时，日间收费时段为全天
//			btime=0;
//			etime=24;
			nigthPrice = 0.0d;//dayPirce;
			nightUnit= dayUnit ;
			nfprice=0.0d;
			nft =0;
		}else {//当前只支持两个时段情况，第一个阶段必须是未时间大于时间 ，而第二个时段是第一个时段的补集,不需要起止时间
			nightUnit=(Integer) nightMap.get("unit");//夜间计费单位
			nigthPrice = Double.valueOf(nightMap.get("price")+"");
			nftime = (Integer) nightMap.get("first_times");//夜间 首优惠时段
			nfprice =Double.valueOf(nightMap.get("fprice")+"");//夜间 首优惠价格
			nft = (Integer)nightMap.get("free_time");// 夜间免费时长
			nfpt = (Integer)nightMap.get("fpay_type");;//超过免费时长 1免费 0收费
			//isFullNightTime =(Integer)nightMap.get("is_fulldaytime");// 是否补足夜间时长 0补全（默认）1不补全
		}
		
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
//		if(calendar.get(Calendar.SECOND)>0)
//			calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
//		calendar.set(Calendar.SECOND, 0);
//		start = calendar.getTimeInMillis()/1000;
		Integer sh = calendar.get(Calendar.HOUR_OF_DAY);//订单开始小时
		
		//结束时间如果有秒，取分钟整数
		calendar.setTimeInMillis(end*1000);
//		calendar.set(Calendar.SECOND, 0);
//		end = calendar.getTimeInMillis()/1000;
		Integer eh = calendar.get(Calendar.HOUR_OF_DAY);//订单结束小时
		//日间价格开始时间
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.HOUR_OF_DAY, btime);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Long ts = calendar.getTimeInMillis()/1000;
		
		//价格当日结束时间
		calendar.set(Calendar.HOUR_OF_DAY, etime);
		Long te = calendar.getTimeInMillis()/1000;
		
		//停车实际结束天的价格结束时间
		calendar.setTimeInMillis(end*1000);
		calendar.set(Calendar.HOUR_OF_DAY, etime);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Long te0 = calendar.getTimeInMillis()/1000;
		
		//停车实际结束天的价格开始时间
		calendar.setTimeInMillis(end*1000);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, btime);
		Long ts0 = calendar.getTimeInMillis()/1000;
		
		Integer btype = 1;//开始在日间
		Integer etype =1;//结束在日间
		
		if(sh<btime)//价格时间 7-21，订单开始小时<7
			btype=0;//开始在夜间第一段
		if(sh>=etime)//订单开始小时>=21
			btype=2;////开始在夜间第二段
		
		if(eh<btime)//价格时间 7-21，结束开始小时<7
			etype=0;//结束在夜间第一段
		if(eh>=etime)//结束开始小时>=21
			etype=2;//结束在夜间第二段
		
		List<Long> dayTimes = new ArrayList<Long>();//日间时段
		List<Long> nightTimes = new ArrayList<Long>();//夜间时段
		
		Long days = (te0-te)/(24*60*60);//订单天数
		int dayHours = etime-btime;
		int nightHours=24-dayHours;
		Long dur = end-start;
		//计价策略：日间 7-21 夜间 21-7 
		if(btype==0){//进场时间在夜间第一时段 0-7
			if(etype==0){//出场时间在夜间第一时段 0-7
				if(days==0)	{//出场时间与进场时间是同一天 
					if(dur<60)
						dur=60L;
					nightTimes.add(dur);
				}else {//出场时间比进场时间大一天以上
					nightTimes.add(ts-start);
					nightTimes.add((days-1)*(nightHours)*60*60);
					nightTimes.add(end-(te0-24*60*60));
					dayTimes.add(days*(te-ts));
				}
				//测试通过
			}else if(etype==1){////出场时间在夜间第二时段 7-21
				nightTimes.add(ts-start);
				dayTimes.add(days*(dayHours*60*60));
				nightTimes.add(days*(nightHours)*60*60);
				dayTimes.add(end-ts0);
				//测试通过
			}else if(etype==2){//出场时间在夜间第三时段 21-24
				nightTimes.add(ts-start);
				nightTimes.add((days)*(nightHours)*60*60);
				nightTimes.add(end-te0);
				dayTimes.add((days+1)*(dayHours*60*60));
				//测试通过
			}
		}else if(btype==1){//进场时间在夜间第二时段 7-21
			if(etype==0){//出场时间在夜间第一时段 0-7
				dayTimes.add(te-start);
				nightTimes.add((days-1)*(nightHours)*60*60);
				dayTimes.add((days-1)*(dayHours*60*60));
				nightTimes.add(end-(te0-24*60*60));
				//测试通过
			}else if(etype==1){//出场时间在夜间第二时段 7-21
				if(days==0){
					if(dur<60)
						dur=60L;
					dayTimes.add(dur);
				}else {
					dayTimes.add(te-start);
					dayTimes.add((days-1)*(dayHours*60*60));
					dayTimes.add(end-ts0);
					nightTimes.add(days*(nightHours*60*60));
				}
			}else if(etype==2){//出场时间在夜间第三时段 21-24
				dayTimes.add(te-start);
				dayTimes.add(days*(dayHours*60*60));
				nightTimes.add(days*nightHours*60*60);
				nightTimes.add(end-te0);
				//测试通过
			}
		}else if(btype==2){//进场时间在夜间第三时段 21-24
			if(etype==0){//出场时间在夜间第一时段 0-7
				if(days==1){//出场时间在进场时间的第二天
					if(dur<60)
						dur=60L;
					nightTimes.add(dur);
				}else {//出场时间在进场时间的第三天以上
					nightTimes.add((ts+24*60*60)-start);
					nightTimes.add((days-2)*nightHours*60*60);
					nightTimes.add(end-(te0-24*60*60));
					dayTimes.add((days-1)*dayHours*60*60);
				}
				//测试通过
			}else if(etype==1){//出场时间在夜间第二时段 7-21
				nightTimes.add((ts+24*60*60)-start);
				nightTimes.add((days-1)*nightHours*60*60);
				dayTimes.add((days-1)*dayHours*60*60);
				dayTimes.add(end-ts0);
				//测试通过
			}else if(etype==2){//出场时间在夜间第三时段 21-24
				if(days==0){
					if(dur<60)
						dur=60L;
					nightTimes.add(dur);
				}else{
					nightTimes.add((ts+24*60*60)-start);
					nightTimes.add((days-1)*nightHours*60*60);
					nightTimes.add(end-te0);
					dayTimes.add((days)*dayHours*60*60);
				}
				//测试通过
			}
		}
		//开始计算价格
		if(btype==0||btype==2){//开始时间在夜间
			Long nt = nightTimes.get(0)/60;
			Long dt = 0L;//日间时长的整数倍
			Long nt1 =0L;//夜间时长的整数倍
			//不处理夜间补足时长
			if(etype==0||etype==2){//结束时间在夜间
				Long nt2 =0L;
				if(nightTimes.size()>1){
					nt1 = nightTimes.get(1)/60;
					nt2 = nightTimes.get(2)/60;
				}
				if(dayTimes.size()==1)
					dt = dayTimes.get(0)/60;
				//处理订单结束部分 nt2
				if(nt2>0){
					price =(nt2/nightUnit)*nigthPrice;
					if(nt2%nightUnit!=0)
						price +=nigthPrice;
				}
				//System.out.println("开始结束时间在夜间");
			}else {//结束时间在日间
				nt1 = nightTimes.get(1)/60;
				dt = dayTimes.get(0)/60;
				Long dt1 = dayTimes.get(1)/60;
				//System.out.println("开始时间在夜间，结束时间在日间");
				//处理订单结束部分 dt1
				if(dt1>0){
					price=(dt1/dayUnit)*dayPirce;
					if((dt1%dayUnit!=0))
						price +=dayPirce;
				}
			}
			//处理订单开始部分
			if(nft>0){//夜间免费时长 15分钟 
				if(nt>=nft){
					if(nfpt==1)//超过免费时长 1免费 0收费
						nt = nt - nft;//超出免费，减去免费时长
				}else
					nt =0L;
			}
			if(nt>0&&nftime>0){//夜间首优惠时长
				if(nt>nftime){
					price += (nftime/nightUnit)*nfprice ;
					nt = nt  - nftime;
				}else{
					price += (nt/nightUnit)*nfprice ;
					if(nt%nightUnit!=0)
						price+=nfprice;
					nt=0L;
				}
			}
			if(nt>0){
				price += (nt/nightUnit)*nigthPrice;
				if(nt%nightUnit!=0)
					price+=nigthPrice;
			}
			if(dt>0)
				price +=(dt/dayUnit)*dayPirce;
			if(nt1>0){
				price +=(nt1/nightUnit)*nigthPrice;
				if(nt1%nightUnit!=0)
					price +=nigthPrice;
			}
			
		}else {//开始时间在日间
			Long dt = dayTimes.get(0)/60;
			Long nt = 0L;//夜间的整数倍
			Long dt1 =0L;//日间的整数倍
			//要处理补足日间时长问题//是否补足日间时长 0补全（默认）1不补全
			boolean isFull = false;
			if(isFullDayTime==0&&dt%dayUnit!=0){//需要补全 
				isFull=true;
			}
			if(etype==0||etype==2){//结束时间在夜间
				nt = nightTimes.get(0)/60;
				Long nt1 = nightTimes.get(1)/60;
				if(isFull){//需要补全
					Long d = dayUnit-dt%dayUnit;//补足的分钟数
					if(nt==0){//夜间的整数值
						if(nt1>=d){
							dt +=d;
							nt1 = nt1-d;
						}else {
							dt +=nt1;
							nt1=0L;
						}
					}else{
						dt +=d;
						if(nt1>=d){
							nt1 = nt1-d;
						}else {
							nt = nt -(d-nt1);
							nt1=0L;
						}
					}
				}
				dt1 = dayTimes.get(1)/60;
			//	System.out.println("3");
				//处理夜间结部分 nt1
				if(nt1>0){
					price =(nt1/nightUnit)*nigthPrice;
					if(nt1%nightUnit!=0)
						price +=nigthPrice;
				}
			}else {//结束时间在日间
				Long dt2 =0L;
				if(dayTimes.size()>1){
					dt1 = dayTimes.get(1)/60;
					dt2 = dayTimes.get(2)/60;
					nt = nightTimes.get(0)/60;
					if(isFull){//需要补全
						Long d = dayUnit-dt%dayUnit;//补足的分钟数
						dt +=d;
						if(dt2>d){
							dt2=dt2-d;
						}else {
							nt = nt -(d-dt2);
							dt2=0L;
						}
					}
				}
				//System.out.println("4");
				//处理日间结束部分，有可能没有
				if(dt2>0){
					price=(dt2/dayUnit)*dayPirce;
					if((dt2%dayUnit!=0))
						price +=dayPirce;
				}
			}
			
			if(dft>0){//日间免费时长 15分钟 
				if(dt>=dft){
					if(dfpt==1)
						dt = dt -dft;//超出免费，减去免费时长
				}else {
					dt =0L;
				}
			}
			
			if(dt>0&&dftime>0){
				if(dt>=dftime){//日间首优惠时长
					price += (dftime/dayUnit)*dfprice ;
					dt = dt  - dftime;
				}else {
					price+=(dt/dayUnit)*dfprice ;
					if(dt%dayUnit!=0)
						price+=dfprice;
					dt=0L;
				}
			}
			
			if(dt>0){//日间首时长
				price+=(dt/dayUnit)*dayPirce;
				if(dt%dayUnit!=0)
					price+=dayPirce;
			}	
			
			
			if(dt1>0){
				price +=(dt1/dayUnit)*dayPirce;
				if(dt1%dayUnit!=0)
					price+=dayPirce;
			}
			if(nt>0){
				price +=(nt/nightUnit)*nigthPrice;
				if(nt%nightUnit!=0)
					price+=nigthPrice;
			}
			
		}
	
		//处理全日间时段问题 开始时间0，结束时间24(btime=0,etime=24)
		if(btime==0&&etime==24){
			price=0d;
			Long dayDur = (end-start)/60;
			if(dayDur<1)
				dayDur=1L;
			if(dft>0){//日间免费时长 15分钟 
				if(dayDur>=dft){
					if(dfpt==1)
						dayDur = dayDur -dft;//超出免费，减去免费时长
				}else {
					dayDur =0L;
				}
			}
			
			if(dayDur>0&&dftime>0){
				if(dayDur>=dftime){//日间首优惠时长
					price += (dftime/dayUnit)*dfprice ;
					dayDur = dayDur  - dftime;
				}else {
					price+=(dayDur/dayUnit)*dfprice ;
					if(dayDur%dayUnit!=0)
						price+=dfprice;
					dayDur=0L;
				}
			}
			
			if(dayDur>0){//日间首时长
				price+=(dayDur/dayUnit)*dayPirce;
				if(dayDur%dayUnit!=0)
					price+=dayPirce;
			}	
		}
		if(price1!=null){
			price = price1;
		}
		price = price+price2;
		if(minPriceUnit!=0.00){
			price = dealPrice(price,minPriceUnit);
		}
		
	//	printList(dayTimes);
	//	System.err.println("=============");
	//	printList(nightTimes);
		resultMap.put("total", StringUtils.formatDouble((price)));
		resultMap.put("discount",0);//折扣
		resultMap.put("duration", StringUtils.getTimeString(dur));
		resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
		resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
		resultMap.put("collect", StringUtils.formatDouble(price));
		System.err.println(">>>"+dayMap+",begin:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+"  ,end:"+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000)+"  ,days="+days+",result:"+resultMap);
		return resultMap;
	}
	public static void main(String[] args) {
		Map<String, Object> dayMap = new HashMap<String, Object>();
		Map<String, Object> nightMap = new HashMap<String, Object>();
		dayMap.put("b_time", 7);
		dayMap.put("e_time", 21);
		dayMap.put("price", 1.5);
		dayMap.put("fprice", 1.5);
		nightMap.put("price", 1.5);
		nightMap.put("fprice", 1.5);
		dayMap.put("is_fulldaytime", 0);
		dayMap.put("uint", 15);
		//Long start = 1434753848L;//20150620 06:45:00
		Long start = 1434764700L;//20150620 09:45:00
		//Long start = 1434807900L;//20150620 21:45:00
		//Long end = 1434927300L-24*60*60;//20150622 065500
		Long end =1434938400L+24*60*60;//20150622 8:00:00
		//Long end =1434983400L-2*24*60*60;//20150622 22:30:00
		
		
//		getAccount(start,end,dayMap,nightMap,0.0);
		
	}
	private static void printList(List<Long> list){
		for(Long k:list){
			System.err.println(k/60+"");
		}
	}
	/**
	 * 计算停车金额
	 * @param start 开始utc时间
	 * @param end 结束utc时间
	 * @param priceMap 时段计费1
	 * @param priceMap2 时段计费2 //分段计费时必须有，没有时，计费1变为全天的
	 * @return
	 */
	
	public static Map<String, Object> getAccount1(Long start,Long end,Map dayMap,Map nightMap,double minPriceUnit){
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Double hprice = 0d;//整天时长的收费
		Double price = 0d;//返回的总价
		Double dayPirce = Double.valueOf(dayMap.get("price")+"");//日间价格
		Integer ftime = (Integer) dayMap.get("first_times");//日间首优惠时长
		Double fprice = Double.valueOf(dayMap.get("fprice")+"");//日间首优惠时长价格
		Integer dft = (Integer)dayMap.get("free_time"); // 日间免费时长
		Integer dfpt = (Integer)dayMap.get("fpay_type");// 1免费 0收费
		Integer isFullDayTime =(Integer)dayMap.get("is_fulldaytime");// 是否补足日间时长 0补全（默认）1不补全
		dft = dft==null?0:dft;
		dfpt = dfpt==null?0:dfpt;
		Integer nft = dft;// 夜间免费时长
		Integer nfpt = dfpt;// 1免费 0收费
		Integer nftime = ftime;//夜间首优惠时长
		Double nfprice =fprice;//夜间首优惠时长价格
		Double nigthPrice = null;//夜间间价格
		Long dayDuration =0L;//日间时长
		Long nightDuration =0L;//夜间时长
		Double ymoney=0d;//优惠
		Long oldDuration =(end-start);
//		System.out.println("日间："+dayMap);
//		System.out.println("夜间："+nightMap);
		Integer btime = (Integer)dayMap.get("b_time");
		Integer etime = (Integer)dayMap.get("e_time");
		Integer dayUnit = (Integer) dayMap.get("unit");//日间计费单位
		Integer nightUnit = dayUnit;
		if(nightMap==null){//没有夜间价格策略时，日间收费时段为全天
//			btime=0;
//			etime=24;
			nigthPrice = 0.0d;//dayPirce;
			nightUnit= dayUnit ;
			nfprice=0.0d;
			nft =0;
		}else {//当前只支持两个时段情况，第一个阶段必须是未时间大于时间 ，而第二个时段是第一个时段的补集,不需要起止时间
			nightUnit=(Integer) nightMap.get("unit");//夜间计费单位
			nigthPrice = Double.valueOf(nightMap.get("price")+"");
			nftime = (Integer) nightMap.get("first_times");//首优惠时段
			nfprice =Double.valueOf(nightMap.get("fprice")+"");
			nft = (Integer)nightMap.get("free_time");
			nfpt = (Integer)nightMap.get("fpay_type");
		}
		List<Object> durs = getDurations(start,end,btime,etime);
		Long times = (Long)durs.get(0);
		Long fat = (Long)durs.get(1);//停车时段在  夜间 :0 , 日间：1 ,夜间到日间:2 , 日间到夜间:3,
		
		String days = (String)durs.get(2);//日间时长
		String nights = (String)durs.get(3);//夜间时长
		
		dayDuration =0L; 
		nightDuration =0L;
		Double aprice = 0.0;//额外价格，记录多时段
		if(isFullDayTime!=null&&isFullDayTime==0){//补足日间时长 0
			if(days.indexOf("_")!=-1){
				String []ds = days.split("_");
				dayDuration = (Long.valueOf(ds[0])+Long.valueOf(ds[1]))/60;
			}else {
				dayDuration = (Long.valueOf(days))/60;
			}
			if(nights.indexOf("_")!=-1){
				String []ns = nights.split("_");
				nightDuration = (Long.valueOf(ns[0])+Long.valueOf(ns[1]))/60;
			}else {
				nightDuration = (Long.valueOf(nights))/60;
			}
		}else {//不补足日间时长 0
			if(fat==4){//4day-night-day,
				String []ds = days.split("_");
				Long d1 = Long.valueOf(ds[0])/60;//第一段日间时长
				Long d2 = Long.valueOf(ds[1])/60;//第二段日间时长
				if(d1<dayUnit){//第一段不是时间单位的整数时，加一个日间价格单位
					if(d1>dft){//第一段日间时长大于日间免费时长
						aprice = dayPirce;
					}
					dayDuration = d2;
				}else {
					if(d1%dayUnit!=0)
						aprice=dayPirce;
					dayDuration = (d1-d1%dayUnit)+d2;
				}
				dft=0;//第二段不再处理免费时长
				nightDuration=(Long.valueOf(nights))/60;
			}else if(fat==5){//5night-day-night
				String []ns = nights.split("_");
				Long n1 = Long.valueOf(ns[0])/60;//第一段夜间时长
				Long n2 = Long.valueOf(ns[1])/60;//第二段夜间时长
				
				if(n1<nightUnit){//第一段不是时间单位的整数时，加一个夜间价格单位
					if(n1>nft)
						aprice = nigthPrice;
					nightDuration = n2;
				}else {
					if(n1%nightUnit!=0)
						aprice=nigthPrice;
					nightDuration = (n1-n1%nightUnit)+n2;
				}
				nft=0;
				//nightDuration = n1+n2;
				dayDuration =(Long.valueOf(days))/60;
			}else {
				dayDuration =(Long.valueOf(days))/60;
				nightDuration = (Long.valueOf(nights))/60;
			}
		}
		
		if(times>0){
			Integer t1 = (etime-btime);
			hprice =Double.valueOf(dayPirce*((t1*60)/dayUnit)+nigthPrice*(((24-t1)*60)/nightUnit));
			hprice = Double.valueOf(hprice*times);
		}
		
		//处理免费时长、计算优惠
		if(fat==0||fat==2){//夜间,先处理免费时长,再计算优惠
			//夜间,先处理免费时长
			if(nft>0){
				if(nightDuration<=nft){//小于免费时长，直接免费
					if(nft-nightDuration<dayDuration)//同时再扣除日间的时长，以补足免费时长
						dayDuration = dayDuration-(nft- nightDuration);
					else {//夜间+日间不够免费时间，日间也置为0;
						dayDuration=0L;
					}
					nightDuration=0L;
				}else if(nfpt==1){//大于免费时长，设置为免费时，减去免费时长
					nightDuration = nightDuration-nft;
				}
			}
			//夜间,再计算优惠
			if(nightDuration>0&&nftime>0&&nfprice>0&&nigthPrice>nfprice){
				ymoney = (nftime/nightUnit)*(nigthPrice-nfprice);
				if(nightDuration>nftime)
					ymoney = (nftime/nightDuration)*(nigthPrice-nfprice);
				else {
					ymoney = (nightDuration/nightUnit)*(nigthPrice-nfprice);
					if(nightDuration<nightUnit)
						ymoney = (nigthPrice-nfprice);
				}
			}
			if(ymoney==0&&times>0){
				ymoney = (nftime/nightUnit)*(nigthPrice-nfprice);
			}
		}else {//日间,先处理不足一个计费时长或不是计费时长的整数倍时,再处理免费时长,最后计算优惠
			
			//日间,处理免费时长
			if(dft>0){
				if(dayDuration<=dft){//小免费时长，直接免费
					dayDuration=0L;
					if(dft-dayDuration<nightDuration)//同时再扣除夜间的时长，以补足免费时长
						nightDuration = nightDuration-(dft-dayDuration);
					else {//夜间+日间不够免费时间，夜间也置为0;
						nightDuration=0L;
					}
				}else if(dfpt==1){//大于免费时长，设置为免费时，减去免费时长
					dayDuration = dayDuration-dft;
				}
			}
			if(isFullDayTime!=null&&isFullDayTime==0){//日间时段是否补全,0补全 默认 1不补全，例如济南的车场
				//日间,处理不足一个计费时长或不是计费时长的整数倍时
				if(dayDuration>0){
					if(dayDuration<dayUnit){//日间时长不足一个付费单位，补足一个计费单位，同时夜间时长减少补时部分
						if(nightDuration>(dayUnit-dayDuration)){
							nightDuration = nightDuration -(dayUnit-dayDuration);
							dayDuration = dayUnit.longValue();
						}else {
							dayDuration = dayDuration+nightDuration;
							nightDuration=0L;
						}
					}else if(dayDuration%dayUnit>0){//日间时长不是付费单位的整数，余数补足一个计费单位，同时夜间时长减少补时部分
						Long ld = dayDuration%dayUnit; //余数
						if(nightDuration>(dayUnit-ld)){
							nightDuration = nightDuration -(dayUnit-ld);
							dayDuration = dayDuration + (dayUnit-ld);
						}else {
							dayDuration = dayDuration+nightDuration;
							nightDuration=0L;
						}
					}
				}
			}
			//日间,再计算优惠
			if(times>0){
				ymoney = (ftime/dayUnit)*(dayPirce-fprice);
			}else {
				if(dayDuration>0&&ftime>0&&fprice>0&&dayPirce>fprice){
					if(dayDuration>=ftime){
						ymoney = (ftime/dayUnit)*(dayPirce-fprice);
					}else {
						ymoney = (dayDuration/dayUnit)*(dayPirce-fprice);
						if(dayDuration%dayUnit>0&&(dayDuration/dayUnit)<(ftime/dayUnit))
							ymoney +=(dayPirce-fprice);
						if(dayDuration<dayUnit)
							ymoney = (dayPirce-fprice);
					}
				}
			}
			if(ymoney==0&&times>0){
				ymoney = (ftime/dayUnit)*(dayPirce-fprice);
			}
		}
		//计算总价
		price = (dayDuration/dayUnit)*dayPirce + (nightDuration/nightUnit)*nigthPrice;
		//零头按一个计费单位收费
		if(dayDuration%dayUnit>0)
			price+=dayPirce;
		if(nightDuration%nightUnit>0)
			price +=nigthPrice;
//		if(price==0)
//			price=0.01d;

		price = price+hprice+aprice;
		Double collect = price-ymoney;
		//设置了车场最小价格单位按最小价格单位处理
		if(minPriceUnit!=0.00){
			collect = dealPrice(price-ymoney,minPriceUnit);
			price = dealPrice(price,minPriceUnit);
		}
		resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
		resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
		resultMap.put("total", StringUtils.formatDouble((price)));
		resultMap.put("discount",StringUtils.formatDouble(ymoney));//折扣
		resultMap.put("collect", StringUtils.formatDouble((collect)));
		resultMap.put("duration", StringUtils.getTimeString(oldDuration));
		return resultMap;
	}
	
	public static double dealPrice(double price,double minPriceUnit){
		DecimalFormat dFormat = new DecimalFormat("#0.00");
		String []pricearr = dFormat.format(price).split("\\.");
		if(Double.parseDouble("0."+pricearr[1])>=minPriceUnit){
			price = Double.parseDouble(pricearr[0])+minPriceUnit;
		}else {
			price = Double.parseDouble(pricearr[0]);
		}
		return price;
	}

	/**
	 * 分析停车时长和区间
	 * @param start --停车开始时间
	 * @param end --停车结束时间
	 * @param btime --价格开始小时
	 * @param etime --价格结束小时
	 * @return [天数，停车时段类型(停车时段在  0夜间 , 1日间,2夜间到日间, 3日间到夜间)，日间时长，夜间时长]
	 */
	private static List<Object> getDurations(Long start,Long end,Integer btime,Integer etime){
		List<Object> durList = new ArrayList<Object>();
		Long times =0L;
		//开始时间如果有秒，分钟加1
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		if(calendar.get(Calendar.SECOND)>0)
			calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE)+1);
		calendar.set(Calendar.SECOND, 0);
		start = calendar.getTimeInMillis()/1000;
		//结束时间如果有秒，取分钟整数
		calendar.setTimeInMillis(end*1000);
		calendar.set(Calendar.SECOND, 0);
		end = calendar.getTimeInMillis()/1000;
		//时长
		Long duration = end-start;
		//计算天数
		if(duration>=24*60*60){
			times = duration/(24*60*60);
			start = start + times*24*60*60;
			//System.err.println("times:"+times);
		}
		//[天数，停车时段类型(停车时段在  0夜间 , 1日间,2夜间到日间, 3日间到夜间,4day-night-day,5night-day-night)，日间时长，夜间时长]
		durList.add(times);
		
		//日间价格开始时间
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.HOUR_OF_DAY, btime);
		calendar.set(Calendar.MINUTE, 0);
		Long ts = calendar.getTimeInMillis()/1000;
		//价格结时间
		calendar.set(Calendar.HOUR_OF_DAY, etime);
		calendar.set(Calendar.MINUTE, 0);
		Long te = calendar.getTimeInMillis()/1000;
		//夜间价格开始结束时间
		Long nts = ts+24*60*60;
		Long nte = te+24*60*60;
		//日间时长
		String dduration = "0";
		//夜间时长
		String nduration = "0";
		
//		System.err.println("start:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000));
//		System.err.println("end:"+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
//		System.err.println("ts:"+TimeTools.getTime_yyyyMMdd_HHmmss(ts*1000));
//		System.err.println("te:"+TimeTools.getTime_yyyyMMdd_HHmmss(te*1000));
//		System.err.println("nts:"+TimeTools.getTime_yyyyMMdd_HHmmss(nts*1000));
//		System.err.println("nte:"+TimeTools.getTime_yyyyMMdd_HHmmss(nte*1000));
		
		if(start<ts){//停车开始时间小于价格的开始时间
			if(end<=ts){//停车结束时间也小于价格开始时间，如价格时间 08:00-21:00,停车区间是07:20-07:55
				durList.add(0L);//停车时段在  0夜间 
				nduration =(end-start)+"";//夜间时长为停车开始时间-停车结束时间，而日间时长为0
//				System.err.println("night1 duration:"+nduration);
			}else {//停车结束时间大于价格开始时间
				if(end<=te){//停车结束时间小于价格结束时间， 如价格时间 08:00-21:00,停车区间是07:20-19:55
					durList.add(2L);//停车时段在 2夜间到日间 
					nduration = (ts-start)+"";//日间时长
					dduration = (end-ts)+"";//夜间时长
//					System.err.println("night2 duration:"+nduration);
//					System.err.println("day2 duration:"+dduration);
				}else {//停车结束时间大于价格结束时间，
					if(end<nts){//停车结束时间小于第二天价格开始时间， 如价格时间 08:00-21:00,停车区间是07:20-第二天06:55
						durList.add(5L);//停车时段在 5night-day-night 
						nduration = (ts-start) +"_"+ (end-te);//夜间时间为两段，0800-0720 + 0655-2100
						dduration = (te-ts)+"";//日间时长 2100-0800
//						System.err.println("night3 duration:"+nduration);
//						System.err.println("day3 duration:"+dduration);
					}else {
						durList.add(2L);//停车时段在 2夜间到日间 
					}
				}
			}
		}else if(start<te){//停车开始时间小于价格的结束时间 如：价格时间 08:00-21:00,停车区间是09:20-17:55
			if(end<=te){//停车结束时间小于价格结束时间， 如：价格时间 08:00-21:00,停车区间是09:20-17:55
				durList.add(1L);//停车时段在  1日间 
				dduration = (end-start)+"";//日间时长,1755-0920，夜间时长为0
//				System.err.println("day4 duration:"+dduration);
			}else {//停车结束时间大于价格结束时间,如：价格时间 08:00-21:00,停车区间是09:20-22:55
				//durList.add(3L);//停车时段在  3日间到夜间
				if(end<=nts){//停车结束时间小于第二天价格开始时间， 如价格时间 08:00-21:00,停车区间是09:20-第二天07:55
					durList.add(3L);//停车时段在  3日间到夜间
					nduration =(end-te)+"" ;//夜间时长,0755-2100
					dduration = (te-start)+"";//日间时长,2100-0920
//					System.err.println("night5 duration:"+nduration);
//					System.err.println("day5 duration:"+dduration);
				}else{//停车结束时间大于第二天价格开始时间, 如价格时间 08:00-21:00,停车区间是09:20-第二天08:55
					durList.add(4L);//停车时段在 4day-night-day
					dduration = (te-start)+"_"+(end-nts);//日间时长有两段,2100-0920 + 0855-0800
					nduration = (nts-te)+"";//夜间时长   0800-2100
//					System.err.println("night6 duration:"+nduration);
//					System.err.println("day6 duration:"+dduration+",t1:"+(te-start)+",t2:"+(end-nts));
				}
			}
		}else if(start>=te){//停车开始时间大于或等于价格的结束时间 ,如价格时间 08:00-21:00 停车区间是22:20-第二天06:55
			if(end<=nts){//停车结束时间小于第二天价格开始时间,如价格时间 08:00-21:00 停车区间是22:20-第二天06:55
				durList.add(0L);//停车时段在  0夜间 
				nduration = (end-start)+"";//夜间时长，0655-2220，而日间时长为0
//				System.err.println("night7 duration:"+nduration);
			}else{//停车结束时间大于第二天价格开始时间,如价格时间 08:00-21:00 停车区间是22:20-第二天09:55
				if(end<=nte){//停车结束时间小于或等于第二天价格结时间,如价格时间 08:00-21:00 停车区间是22:20-第二天20:55
					durList.add(2L);//停车时段在 2夜间到日间 
					nduration = (nts-start)+"";//夜间时长,第二天的0800-2220
					dduration = (end-nts)+"";//日间时长,2055-0800
//					System.err.println("night8 duration:"+nduration);
//					System.err.println("day8 duration:"+dduration);
				}else {//停车结束时间大于第二天价格结时间,如价格时间 08:00-21:00 停车区间是22:20-第二天21:55
					durList.add(5L);//停车时段在 5night-day-night 
					nduration = (nts-start) +"_"+(end-nte);//夜间两部分,第二天的0800-2220 + 2155-2100
					dduration = (nte-nts)+"";//日间时长,2100-0800
//					System.err.println("night9 duration:"+nduration);
//					System.err.println("day9 duration:"+dduration);
				}
			}
				
		}
		durList.add(dduration);
		durList.add(nduration);
		//返回:[天数，停车时段类型(停车时段在  0夜间 , 1日间,2夜间到日间, 3日间到夜间)，日间时长，夜间时长]
		return durList;
	}
	
	/**
	 * 计算停车金额
	 * @param start 开始utc时间
	 * @param end 结束utc时间
	 * @param priceMap 时段计费1
	 * @param priceMap2 时段计费2 //分段计费时必须有，没有时，计费1变为全天的
	 * @return
	 */
	/*public static Map<String, Object> getAccount_bak11(Long start,Long end,Map dayMap,Map nightMap){
		
		 *  日间：{price=3.00, unit=30, b_time=8, e_time=18, first_times=60, fprice=2.50, countless=5}
			夜间：{price=2.00, unit=60, b_time=18,e_time= 8,  first_times=0, fprice=0.00, countless=0}
			btime:1405581081,etime:1405581549
		 
		//System.err.println("btime:"+start+",etime:"+end);
		Double hprice = 0d;//整天时长的收费
		Double price = 0d;//返回的总价
		Double dayPirce = null;//日间价格
		Double ymoney=0d;//优惠
		Integer countless = 0;//零头计费时长，单位分钟
		Map<String, Object> resultMap = new HashMap<String, Object>();
		Long duration = (end-start)/60;//停车时长，单位：分钟,只取整数
		Long oldDuration =(end-start);
		//Long allduration=duration;
		if(start!=null&&end!=null&&dayMap!=null){
			//System.err.println("总停车时长："+duration+"分钟");
			dayPirce=Double.valueOf(dayMap.get("price")+"");
			//日间时段1
			Integer btime = (Integer)dayMap.get("b_time");
			Integer etime = (Integer)dayMap.get("e_time");
			Integer dayUnit = (Integer) dayMap.get("unit");//日间计费单位
			countless = (Integer)dayMap.get("countless");
			Integer ftime = (Integer) dayMap.get("first_times");//首优惠时段
			Double fprice = Double.valueOf(dayMap.get("fprice")+"");
			Integer nightUnit =1; 
			Double nigthPrice = 0d;//夜间价格
			Integer nft = 0;
			Integer nfpt =0;
			//没有设置时段2时，时段1是全天
			if(nightMap==null){//没有夜间价格策略时，日间收费时段为全天
				btime=0;
				etime=24;
			}else {//当前只支持两个时段情况，第一个阶段必须是未时间大于时间 ，而第二个时段是第一个时段的补集,不需要起止时间
				nightUnit=(Integer) nightMap.get("unit");//夜间计费单位
				nigthPrice = Double.valueOf(nightMap.get("price")+"");
				nft = (Integer)nightMap.get("free_time");
				nfpt = (Integer)nightMap.get("fpay_type");
				nft = nft==null?0:nft;
				nfpt = nfpt==null?0:nfpt;
			}
			resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
			resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
			Integer dft = (Integer)dayMap.get("free_time");
			Integer dfpt = (Integer)dayMap.get("fpay_type");
			dft = dft==null?0:dft;
			dfpt = dfpt==null?0:dfpt;
			//System.out.println("日间："+dayMap);
			//System.out.println("夜间："+nightMap);
			//根据免费时长及免费规则计算停车开始及结束时间 
			//if(dfpt!=0||dft!=0){//有免费规则时，处理免费及跨昼夜问题
			List<Long> seList = getStart(btime,etime,start,end,dayUnit,dft,nft,dfpt,nfpt);
			if(!seList.isEmpty()){
				start = seList.get(0);
				end = seList.get(1);
			}
			//}else{//处理免费及跨昼夜问题
				
			//}
			duration = (end-start)/60;//停车时长，单位：分钟,只取整数
			//System.err.println("免费时长去除后时长："+duration+"分钟");
			if(end>start){
				//时长超过24小时,先计算多少天及金额
				if(duration>=24*60){
					Long times = duration/(24*60);
					Integer t1 = (etime-btime);
					//hprice =Double.valueOf(t1*dayPirce*(60/dayUnit)+((24-t1)*nigthPrice*60)/nightUnit);
					hprice =Double.valueOf(dayPirce*((t1*60)/dayUnit)+nigthPrice*(((24-t1)*60)/nightUnit));
					hprice = Double.valueOf(hprice*times);
//					resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000));
//					resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
					duration=duration-times*24*60;
					ymoney=(ftime/dayUnit)*(dayPirce-fprice);
				}else {
//					resultMap.put("btime", TimeTools.getTime_MMdd_HHmm(start*1000).substring(6));
//					resultMap.put("etime", TimeTools.getTime_MMdd_HHmm(end*1000).substring(6));
				}
				Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
				calendar.setTimeInMillis(start*1000);
				//开始小时
				int bhour = calendar.get(Calendar.HOUR_OF_DAY);
				int bmin = calendar.get(Calendar.MINUTE);
				calendar.setTimeInMillis(end*1000);
				//结束小时
				int ehour = calendar.get(Calendar.HOUR_OF_DAY);
//				if(ehour==0&&end>start)
//					ehour=24;
				int emin = calendar.get(Calendar.MINUTE);
				//正常时序 8:00-13:00\
				//System.out.println(stopInfo);
				if(ehour>=bhour){
					price = countPrice(bhour,ehour,bmin,emin,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,duration,countless,true);
				}else {//逆时序 21:00 -7:00 分两段计费   21:00-24:00,0:00-7:00
					Long _duration = Long.valueOf((24-bhour-1)*60+(60-bmin));
					price = countPrice(bhour,24,bmin,0,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,_duration,countless,false);
					//分两段计费时，第一段不能计算，把第一段的零头加到第二个时段内 ((24-etime)*60)%nightUnit
					_duration = Long.valueOf(ehour*60+emin)+((24-etime)*60)%nightUnit;
					price +=countPrice(0,ehour,0,emin,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,_duration,countless,true);
				}
				//计算优惠，只计算日间时段内的优惠
				Double _ymoney =countFprice(btime, etime, bhour, bmin, ehour, emin, (dayPirce-fprice), ftime, dayUnit, duration, countless);
				if(ymoney<_ymoney)
					ymoney =_ymoney;
			}
		}
		if(price==0)
			price=0.01d;
		resultMap.put("total", StringUtils.formatDouble((price+hprice)));
		resultMap.put("discount",StringUtils.formatDouble(ymoney));
		resultMap.put("collect", StringUtils.formatDouble(((price+hprice)-ymoney)));
		resultMap.put("duration", StringUtils.getTimeString(oldDuration));
		//System.out.println(resultMap);
		return resultMap;//stopInfo+"，应收："+(price+hprice)+",优惠："+ymoney+",实收："+((price+hprice)-ymoney);
	}
	*//**
	 * @param bhour 停车开始 小时 
	 * @param ehour 停车结束小时 
	 * @param bmin  停车开始分钟 
	 * @param emin  停车结束分钟 
	 * @param btime 日间计费时段 开始小时
	 * @param etime 日间计费时段 结束小时
	 * @param dayPirce 日间计费 单价 
	 * @param nigthPrice 夜间计费 单价  
	 * @param dayUnit 日间计费单位
	 * @param nigthUnit 夜间计费单位
	 * @param duration 停车时长，分钟
	 * @param countless 零头计费时长，单位分钟
	 * @param isFprice 是否计算零头计费时长，分两段计费时，第一段不能计算，把第一段的零头加到第二个时段内
	 * @return 金额
	 * 当前只支持两个时段情况，第一个阶段必须是未时间大于时间 ，而第二个时段是第一个时段的补集,不需要起止时间
	 *//*
	private static Double countPrice(int bhour,int ehour,int bmin,int emin,int btime,
			int etime,double dayPirce,double nigthPrice,Integer dayUnit,
			Integer nigthUnit,Long duration,Integer countless,boolean isFprice){
		Double price = null;
		Double cprice=0d;//零头计费
		
		//ehour 一定是大于bhuour
		if(ehour<=btime||bhour>=etime){//全在第二个计费时间内
			if(ehour==btime){//日间时段7-21，停车：6:30:7:20,夜间单位：120，日间单位：15  ，免费时长：10
				if(bhour==ehour){
					price = (duration/dayUnit)*dayPirce;
					if(duration%dayUnit>countless)//夜间零头计费
						cprice=dayPirce;
				}else {
					duration = duration-emin;
					int dayTimes = emin/dayUnit;//20/15;
					price = (duration/nigthUnit)*nigthPrice+dayTimes*dayPirce;
					if(duration%nigthUnit>countless)//夜间零头计费
						cprice=nigthPrice;
					if(emin!=0&&emin%dayUnit>countless)//日间零头计费   零头时长>日间零头计费时长，零头计费=夜间价格
						cprice +=dayPirce;
				}
			}else {//日间时段7-21，停车：6:30:6:50
				price = (duration/nigthUnit)*nigthPrice;
				if(duration%nigthUnit>countless)//零头时长>零头计费时长，零头计费=夜间价格
					cprice=nigthPrice;
			}
		}else if(bhour<=btime){// 停车开始时间小于或等于日间开始时间 
			if(bhour<btime){//停车开始时间比日间开始时间 小
				if(ehour<etime){//停车结束时间小于日间结束时间，在夜间策略、日间价格策略内
					int nightMin = ((btime-bhour-1)*60+(60-bmin));
					int nightTimes = nightMin/nigthUnit;
					int dayMin = ((ehour-btime)*60+emin);
					int dayTimes = dayMin/dayUnit;
					if(dayMin%dayUnit>countless)
						cprice=dayPirce;
					if(nightMin%nigthUnit>countless)
						cprice+=nigthPrice;
					price = nightTimes*nigthPrice+dayTimes*dayPirce;
				}else {//停车结束时间大于日间结束时间，在夜间策略、日间整个价格策略和 夜间策略内
					int nightMin1 = ((btime-bhour-1)*60+(60-bmin));
					int nightMin2 = ((ehour-etime)*60+emin);
					int nightTimes1 = nightMin1/nigthUnit;
					int dsyTimes = ((etime-btime)*60)/dayUnit;
					int nightTimes2 = nightMin2/nigthUnit;
					if(nightMin1%nigthUnit>countless)
						cprice=nigthPrice;
					if(nightMin2%nigthUnit>countless)
						cprice +=nigthPrice;
					price = nightTimes1*nigthPrice+dsyTimes*dayPirce+nightTimes2*nigthPrice;
				}
			}else {//停车开始时间=日间开始时间 
				if(ehour<etime){//停车结束时间小于日间结束时间，在夜间策略、日间价格策略内
					int dayTimes = duration.intValue()/dayUnit;
					if(duration%dayUnit>countless)
						cprice=dayPirce;
					price =dayTimes*dayPirce;
				}else {//停车结束时间大于日间结束时间，在夜间策略、日间整个价格策略和 夜间策略内
					int dsyTimes = ((etime-btime)*60)/dayUnit;
					if(bhour==btime){
						dsyTimes = ((etime-btime)*60-bmin)/dayUnit;
					}
					if(((etime-btime)*60-bmin)%dayUnit>countless)
						cprice=dayPirce;
					int nightTimes2 = ((ehour-etime)*60+emin)/nigthUnit;
					if( ((ehour-etime)*60+emin)%nigthUnit>countless)
						cprice+=nigthPrice;
					price =dsyTimes*dayPirce+nightTimes2*nigthPrice;
				}
			}
		}else if(bhour>btime){// 停车开始时间比日间开始时间大
			if(ehour<=etime){//当停车结束时间超过日间时段
				if(ehour<etime){//时段7-21，停车19:19-20:12
					int dayTimes = duration.intValue()/dayUnit;
					if(duration%dayUnit>countless)//零头时长>零头计费时长，零头计费=夜间价格
						cprice=dayPirce;
					price = dayTimes*dayPirce;
				}else {//时段7-21，停车19:19-21:12
					duration = duration-emin;
					int dayTimes = duration.intValue()/dayUnit;
					int nightTiimes = emin/nigthUnit;
					if(emin%nigthUnit>countless)//零头时长>零头计费时长，零头计费=夜间价格
						cprice=nigthPrice;
					if(duration%dayUnit>countless)
						cprice+=dayPirce;
					price = dayTimes*dayPirce+nightTiimes*nigthPrice;
				}
			}else {
				int dayDur = ((etime-bhour-1)*60+(60-bmin));
				int dsyTimes = dayDur/dayUnit;
				int nigDur = ((ehour-etime)*60+emin);
				int nightTimes = nigDur/nigthUnit;
				if(emin%nigthUnit>countless)
					cprice=nigthPrice;
				if(dayDur%dayUnit>countless)
					cprice+=dayPirce;
				price = dsyTimes*dayPirce+nightTimes*nigthPrice;
			}
		}
		if(!isFprice)
			cprice=0d;
		//System.out.println("总价："+price+",零头计费:"+cprice);
		return price+cprice;	
	}
	
	private static Double countFprice(int btime,int etime,int bhour,int bmin,int ehour,int emin,
			Double price,int ftime,int dayUnit,Long duration,int countless){
		Double ymoney = 0d;
		//关键是算出日间时段内的时长,夜间不计算优惠
		if(bhour>ehour){//时段7-21，停车23:00-7:05,ftime=30,dayUnit = 15
			if(bhour<etime){
				duration = Long.valueOf((etime-bhour-1)*60+(60-bmin));
			}else if(ehour>=btime){//时段7-21，停车23:49-2:11,ftime=30,dayUnit = 15
				duration=Long.valueOf((ehour-btime)*60+bmin);
			}else {//时段7-21，停车23:49-2:11,ftime=30,dayUnit = 15
				duration=0L;
			}
		}else if(ehour<btime||bhour>=etime){
			return ymoney;
		}else if(ehour==btime){//时段7-21，停车6:10-7:50,ftime=30,dayUnit = 15
			if(ehour>bhour)
				duration = Long.valueOf(emin);
		}else if(ehour>btime){//时段7-21，停车6:10-8:50,ftime=30,dayUnit = 15,countless = 10;
			if(bhour<btime){//时段7-21，停车6:10-8:50,ftime=30,dayUnit = 15,countless = 10;
				duration = duration-((btime-bhour-1)*60+(60-bmin));
			}else if(bhour>=btime){
				if(ehour>=etime){//时段7-21，停车18:10-22:50,ftime=30,dayUnit = 15,countless = 10;
					duration = duration -((ehour-etime)*60+emin);
				}
			}
		}
		//开始计算
		if(duration>ftime){
			ymoney = (ftime/dayUnit)*price;
		}else {
			ymoney = (duration/dayUnit)*price;
			if(duration%dayUnit>countless)
				ymoney +=price;
		}
		return ymoney;
	}
	*//**
	 * @param btime 日间开始时间
	 * @param etime 日间结束时间
	 * @param start 停车开始时间
	 * @param end 停车结束时间 
	 * @param dunit 日间计费单位（分钟） 
	 * @param dft 日间免费时长
	 * @param nft 夜间免费时长
	 * @param dfpt 日间免费时长后是否收费    1免费 0收费
	 * @param nfpt 夜间免费时长后是否收费    1免费 0收费
	 * @return List<停车开始时间，停车结束时间>
	 *//*
	private static List<Long> getStart(Integer btime,Integer etime,Long start,Long end,Integer dunit,
			Integer dft,Integer nft,Integer dfpt,Integer nfpt){
		List<Long> reslut = new ArrayList<Long>();
//		if(dft==0&&nft==0){
//			return reslut;
//		}
			
		Long duration = end-start;//原停车时长 （秒）
		//System.out.println("原开始时间:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+",原结束时间："+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		int bhour = calendar.get(Calendar.HOUR_OF_DAY);//停车开始小时 
		int bmin  = calendar.get(Calendar.MINUTE); //停车开始分钟
		calendar.setTimeInMillis(end*1000);
		int ehour = calendar.get(Calendar.HOUR_OF_DAY);//停车结束小时 
		
		if(start>end)//停车结束时长小停车开始时间，是跨昼夜，只计算第一天的停车费,把停车结束时间设置为当天的24点
			ehour=24;
		
		if(bhour<btime){//停车开始小时 小于 日间开始小时    日间时段7-21，停车6:49-?
			if(ehour<btime){//全在夜间时段内 ---日间时段7-21，停车6:49-6:59
				if(nfpt==1){//夜间免费时长后不收费，停车开始时间向后推一个夜间免费时长单位，停车结束时间不变
					if(nft!=0&&duration<=nft*60){//停车总时长小于或等于免费时长，且不收费，设置成开始时间等于结束时间，这样就不收费了。
						start=end;
					}else {//停车总时长大于了免费时长，停车开始时间向后推一个免费时长.
						start = start+nft*60;
					}
				}else{//夜间免费时长后收费
					if(nft!=0&&duration<=nft*60){//停车总时长小于或等于免费时长，且不收费，设置成开始时间等于结束时间，这样就不收费了。
						start=end;
					}
				}
			}else if(ehour>=btime){//部分在夜间时段内，只算夜间时段内的免费 ---日间时段7-21，停车6:49-7:59
				int nlong = ((btime-bhour-1)*60)+(60-bmin);//夜间时段内的停车时长  ---11分钟
				if(nlong>=nft){//夜间停车时长大于或等于一个夜间免费时长单位
					if(nfpt==1)//夜间免费时长后不收费，停车开始时间向后推一个夜间免费时长单位，停车结束时间不变
						start = start+nft*60;
					else {//夜间免费时长后收费
						if(dft!=0&&duration<=dft*60)//在免费时长内不收费
							start=end;
					}
				}else {//夜间停车时长不够一个计费单位
					if(nfpt==1){
						start =getBtime(start, btime);//夜间免费时长后不收费，停车开始时间从日间开始时间开始，停车结束时间不变
					}else{
						if(dfpt!=0&&duration<=dfpt*60)//在免费时长内不收费
							start=end;
					}
				}
			}
		}else if(bhour>=btime&&bhour<etime){//停车开始小时 大于或等于 日间开始小时   -- 日间时段7-21，停车7:01-?
			if(ehour<etime){//停车结束时间小于日间结束时间且停车结束时间小于日间结束时间，全在日间时段，-- 日间时段7-21，停车7:01-20:30
				if(duration<=dft*60){//停车时长小于或等于日间免费时长
					start = end;
				}else if(duration>=dft*60){//停车时长大于日间免费时长
					if(dfpt==1){//日间免费时长后不收费，停车开始时间向后推一个日间免费时长单位，停车结束时间不变
						start =start +dft*60;
					}else {//日间免费时长后收费
						//还是按照之前的方式计算
					}
				}
			}else if(ehour>=etime){//部分在日间，部分在夜间
				//计算在日间时段内的时长
				int dLong = ((etime-bhour-1)*60)+(60-bmin);
				if(dLong<=dft){//日间时长小于免费时长，去掉这个时间
					start = getBtime(start, etime);
				}else if(dLong>dft){//日间时长大于免费时长
					if(dfpt==1){//免费,停车开始时间向后推一个日间免费时长单位，停车结束时间不变
						start =start +dft*60;
						dLong = dLong-dft;
					}else {//收费
						//还是按照之前的方式计算
					}
					//处理日间不足一个计费单位的问题
					if(dLong<dunit){//停车开始和结束时间向前推一个时间差（日间一个收费单位-日间停车时长）
						start = start -(dunit-dLong)*60;
						end = end -(dunit-dLong)*60;
					}
				}
			}
		}else if(bhour>=etime){//停车开始小时 大于或等于 日间开始小时   -- 日间时段7-21，停车21:01-?
			if(nfpt==1){//夜间免费时长后不收费，停车开始时间向后推一个夜间免费时长单位，停车结束时间不变
				if(nft!=0&&duration<=nft*60){//停车总时长小于或等于免费时长，且不收费，设置成开始时间等于结束时间，这样就不收费了。
					start=end;
				}else {//停车总时长大于了免费时长，停车开始时间向后推一个免费时长.
					start = start+nft*60;
				}
			}else{//夜间免费时长后收费
				if(nft!=0&&duration<=nft*60){//停车总时长小于或等于免费时长，且不收费，设置成开始时间等于结束时间，这样就不收费了。
					start=end;
				}
			}
		}
		if(start>end)
			start=end;
		reslut.add(start);
		reslut.add(end);
		//System.out.println("现开始时间:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+",现结束时间："+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
		return reslut;
	}

	private static Long getBtime(Long start,int bhour){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.HOUR_OF_DAY, bhour);
		calendar.set(Calendar.MINUTE,0);
		return calendar.getTimeInMillis()/1000;
	}*/
	
}

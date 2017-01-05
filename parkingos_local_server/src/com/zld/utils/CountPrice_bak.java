package com.zld.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class CountPrice_bak {
	/**
	 * 计算停车金额
	 * @param start 开始utc时间
	 * @param end 结束utc时间
	 * @param priceMap 时段计费1
	 * @param priceMap2 时段计费2 //分段计费时必须有，没有时，计费1变为全天的
	 * @return
	 */
	public static Map<String, Object> getAccount(Long start,Long end,Map dayMap,Map nightMap){
		/*
		 *  日间：{price=3.00, unit=30, b_time=8, e_time=18, first_times=60, fprice=2.50, countless=5}
			夜间：{price=2.00, unit=60, b_time=18,e_time= 8,  first_times=0, fprice=0.00, countless=0}
			btime:1405581081,etime:1405581549
		 */
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
			Integer nightUnit =null; 
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
			System.out.println("日间："+dayMap);
			System.out.println("夜间："+nightMap);
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
				if(ehour>=bhour&&emin>=bmin){
					price = countPrice(bhour,ehour,bmin,emin,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,duration,countless,true);
				}else {//逆时序 21:00 -7:00 分两段计费   21:00-24:00,0:00-7:00
					Long _duration = Long.valueOf((24-bhour-1)*60+(60-bmin));
					price = countPrice(bhour,24,bmin,0,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,_duration,countless,false);
					//分两段计费时，第一段不能计算，把第一段的零头加到第二个时段内 ((24-etime)*60)%nightUnit
					_duration = Long.valueOf(ehour*60+emin)+((24-etime)*60)%nightUnit;
//					if(_duration>nightUnit)
					price +=countPrice(0,ehour,0,emin,btime,etime,dayPirce,nigthPrice,dayUnit,nightUnit,_duration,countless,true);
				}
				//计算优惠，只计算日间时段内的优惠
				Double _ymoney =countFprice(btime, etime, bhour, bmin, ehour, emin, (dayPirce-fprice), ftime, dayUnit, duration, countless);
				if(ymoney<_ymoney)
					ymoney =_ymoney;
			}
		}
		resultMap.put("total", StringUtils.formatDouble((price+hprice)));
		resultMap.put("discount",StringUtils.formatDouble(ymoney));
		resultMap.put("collect", StringUtils.formatDouble(((price+hprice)-ymoney)));
		resultMap.put("duration", StringUtils.getTimeString(oldDuration));
		System.out.println(resultMap);
		return resultMap;//stopInfo+"，应收："+(price+hprice)+",优惠："+ymoney+",实收："+((price+hprice)-ymoney);
	}
	/**
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
	 */
	private static Double countPrice(int bhour,int ehour,int bmin,int emin,int btime,
			int etime,double dayPirce,double nigthPrice,Integer dayUnit,
			Integer nigthUnit,Long duration,Integer countless,boolean isFprice){
		Double price = null;
		Double dprice=0d;//日间 零头计费
		Double nprice=0d;//夜间零头计费
		//ehour 一定是大于bhuour
		if(ehour<=btime||bhour>=etime){//全在第二个计费时间内
			if(ehour==btime){//日间时段7-21，停车：6:30:7:20,夜间单位：120，日间单位：15  ，免费时长：10
				if(bhour==ehour){
					price = (duration/dayUnit)*dayPirce;
					if(duration%dayUnit>countless)//夜间零头计费
						nprice=dayPirce;
				}else {
					duration = duration-emin;
					int dayTimes = emin/dayUnit;//20/15;
					price = (duration/nigthUnit)*nigthPrice+dayTimes*dayPirce;
					if(duration%nigthUnit>countless)//夜间零头计费
						nprice=nigthPrice;
					if(emin!=0&&emin%dayUnit>countless)//日间零头计费   零头时长>日间零头计费时长，零头计费=夜间价格
						dprice +=dayPirce;
				}
			}else {//日间时段7-21，停车：6:30:6:50
				price = (duration/nigthUnit)*nigthPrice;
				if(duration%nigthUnit>countless)//零头时长>零头计费时长，零头计费=夜间价格
					nprice=nigthPrice;
			}
		}else if(bhour<=btime){// 停车开始时间小于或等于日间开始时间 
			if(bhour<btime){//停车开始时间比日间开始时间 小
				if(ehour<etime){//停车结束时间小于日间结束时间，在夜间策略、日间价格策略内
					int nightMin = ((btime-bhour-1)*60+(60-bmin));
					int nightTimes = nightMin/nigthUnit;
					int dayMin = ((ehour-btime)*60+emin);
					int dayTimes = dayMin/dayUnit;
					if(dayMin%dayUnit>countless)
						dprice=dayPirce;
					if(nightMin%nigthUnit>countless)
						nprice+=nigthPrice;
					price = nightTimes*nigthPrice+dayTimes*dayPirce;
				}else {//停车结束时间大于日间结束时间，在夜间策略、日间整个价格策略和 夜间策略内
					int nightMin1 = ((btime-bhour-1)*60+(60-bmin));
					int nightMin2 = ((ehour-etime)*60+emin);
					int nightTimes1 = nightMin1/nigthUnit;
					int dsyTimes = ((etime-btime)*60)/dayUnit;
					int nightTimes2 = nightMin2/nigthUnit;
					if(nightMin1%nigthUnit>countless)
						nprice=nigthPrice;
					if(nightMin2%nigthUnit>countless)
						nprice +=nigthPrice;
					price = nightTimes1*nigthPrice+dsyTimes*dayPirce+nightTimes2*nigthPrice;
				}
			}else {//停车开始时间=日间开始时间 
				if(ehour<etime){//停车结束时间小于日间结束时间，在夜间策略、日间价格策略内
					int dayTimes = duration.intValue()/dayUnit;
					if(duration%dayUnit>countless)
						dprice=dayPirce;
					price =dayTimes*dayPirce;
				}else {//停车结束时间大于日间结束时间，在夜间策略、日间整个价格策略和 夜间策略内
					int dsyTimes = ((etime-btime)*60)/dayUnit;
					if(bhour==btime){
						dsyTimes = ((etime-btime)*60-bmin)/dayUnit;
					}
					if(((etime-btime)*60-bmin)%dayUnit>countless)
						dprice=dayPirce;
					int nightTimes2 = ((ehour-etime)*60+emin)/nigthUnit;
					if( ((ehour-etime)*60+emin)%nigthUnit>countless)
						nprice+=nigthPrice;
					price =dsyTimes*dayPirce+nightTimes2*nigthPrice;
				}
			}
		}else if(bhour>btime){// 停车开始时间比日间开始时间大
			if(ehour<=etime){//当停车结束时间超过日间时段
				if(ehour<etime){//时段7-21，停车19:19-20:12
					int dayTimes = duration.intValue()/dayUnit;
					if(duration%dayUnit>countless)//零头时长>零头计费时长，零头计费=夜间价格
						dprice=dayPirce;
					price = dayTimes*dayPirce;
				}else {//时段7-21，停车19:19-21:12
					duration = duration-emin;
					int dayTimes = duration.intValue()/dayUnit;
					int nightTiimes = emin/nigthUnit;
					if(emin%nigthUnit>countless)//零头时长>零头计费时长，零头计费=夜间价格
						nprice=nigthPrice;
					if(duration%dayUnit>countless)
						dprice+=dayPirce;
					price = dayTimes*dayPirce+nightTiimes*nigthPrice;
				}
			}else {
				int dayDur = ((etime-bhour-1)*60+(60-bmin));
				int dsyTimes = dayDur/dayUnit;
				int nigDur = ((ehour-etime)*60+emin);
				int nightTimes = nigDur/nigthUnit;
				if(emin%nigthUnit>countless)
					nprice=nigthPrice;
				if(dayDur%dayUnit>countless&&!isFprice)
					dprice+=dayPirce;
				price = dsyTimes*dayPirce+nightTimes*nigthPrice;
			}
		}
//		if(!isFprice)
//			cprice=0d;
		//System.out.println("总价："+price+",零头计费:"+cprice);
		if(isFprice)
			dprice+=nprice;
		return price+dprice;	
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
	/**
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
	 */
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
		System.out.println("现开始时间:"+TimeTools.getTime_yyyyMMdd_HHmmss(start*1000)+",现结束时间："+TimeTools.getTime_yyyyMMdd_HHmmss(end*1000));
		return reslut;
	}

	private static Long getBtime(Long start,int bhour){
		Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
		calendar.setTimeInMillis(start*1000);
		calendar.set(Calendar.HOUR_OF_DAY, bhour);
		calendar.set(Calendar.MINUTE,0);
		return calendar.getTimeInMillis()/1000;
	}
	
}

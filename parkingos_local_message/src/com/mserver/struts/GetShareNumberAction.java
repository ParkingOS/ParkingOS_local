package com.mserver.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.omg.CORBA.Request;
import org.springframework.beans.factory.annotation.Autowired;

import com.mserver.AjaxUtil;
import com.mserver.service.PgOnlyReadService;
import com.mserver.service.PgService;
import com.mserver.utils.Check;
import com.mserver.utils.MemcacheUtils;
import com.mserver.utils.RequestUtil;
import com.mserver.utils.StringUtils;
import com.mserver.utils.TimeTools;

/**
 * 处理收费员请求空闲车位数
 * @author Administrator
 *
 */
public class GetShareNumberAction extends Action{
	
	@Autowired
	private PgOnlyReadService pgService;
	@Autowired
	private PgService dbService;
	@Autowired
	private MemcacheUtils memcacheUtils;
	
	private Logger logger = Logger.getLogger(GetShareNumberAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String token =RequestUtil.processParams(request, "token");
		String out =RequestUtil.processParams(request, "out");
		Long comId =RequestUtil.getLong(request, "comid", null);
		Long type =RequestUtil.getLong(request, "type", -1L);
		Map<String,Object> infoMap  = new HashMap<String, Object>();
		if(comId==null){
			if(token==null||"null".equals(token)||"".equals(token)){
				infoMap.put("result", "fail");
				infoMap.put("message", "token无效!");
				AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap,"0"));
				return null;
			}
			comId = validMemToken(token);
		}
		
		//logger.error("token:"+token);
		//http://127.0.0.1/mserver/getshare.do?token=11495546c0435c064320f9a7cb627065&out=json
		//http://127.0.0.1/mserver/getshare.do?token=0e7c652eddc1db5ac7a6b540359fb17b&out=json
		if(comId!=null){
			if(type==1){//type为1  代表传设备状态
				//报告安卓主机的状态
				Long passid =RequestUtil.getLong(request, "passid", -1L);
				Map passMap = pgService.getMap("select * from com_pass_tb where comid = ? and id = ? ", new Object[]{comId,passid});
				if(passMap!=null&&passMap.get("worksite_id")!=null){
					long workid = Long.parseLong(""+passMap.get("worksite_id"));
					String equipmentmodel =RequestUtil.processParams(request, "equipmentmodel");
					String memoryspace =RequestUtil.processParams(request, "memoryspace");
					String internalspace =RequestUtil.processParams(request, "internalspace");
					int r = dbService.update("update com_worksite_tb set host_name=?,host_memory=?,host_internal=?,upload_time=? where id = ? ", new Object[]{equipmentmodel,memoryspace,internalspace,System.currentTimeMillis()/1000,workid});
					logger.error("upload info passid:"+passid+",equipmentmodel:"+equipmentmodel+",memoryspace:"+memoryspace+",internalspace:"+internalspace+",r:"+r);
				}
			}
			getParkNumber(comId,infoMap);
			
			//处理子车场的车位数
			List<Map<String, Object>> childParks = pgService.getAll("select id from com_info_tb where pid=?", new Object[]{comId});
			if(childParks!=null&&!childParks.isEmpty()){
				Map<String, Object> chlidInfoMap = new HashMap<String, Object>();
				for(Map<String, Object> map : childParks){
					Long cid =(Long) map.get("id");
					getParkNumber(cid, chlidInfoMap);
//					Object total = chlidInfoMap.get("total");
//					if(total!=null&&Check.isNumber(total+"")){
//						infoMap.put("total", Long.valueOf(infoMap.get("total")+"")+Long.valueOf(total+""));
//					}
//					Object free = chlidInfoMap.get("free");
//					if(free!=null&&Check.isNumber(free+"")){
//						infoMap.put("free", Long.valueOf(infoMap.get("free")+"")-Long.valueOf(free+""));
//					}
					Object busy = chlidInfoMap.get("busy");
					if(busy!=null&&Check.isNumber(busy+"")){
						infoMap.put("busy", Long.valueOf(infoMap.get("busy")+"")-Long.valueOf(busy+""));
						infoMap.put("free", Long.valueOf(infoMap.get("free")+"")+Long.valueOf(busy+""));
					}
					chlidInfoMap.clear();
				}
			}
		}else {
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			//AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
		}
		String result = "";
		if(out.equals("json"))
			result= StringUtils.createJson(infoMap);
		else
			result=StringUtils.createXML(infoMap);
		System.out.println(comId+":"+result);
		AjaxUtil.ajaxOutput(response,result);
		return null;
	}
	
	private void getParkNumber(Long comId,Map<String,Object> infoMap){
		try {
			//Long total =0L;
			Long time1 = System.currentTimeMillis()/1000;
			Integer total =0;
			//Long free = 0L;
			Long busy = 0L;
			Map comMap = pgService.getMap("select share_number from com_info_tb where id = ? ", new Object[]{comId});
			busy = pgService.getLong("select count(id) from order_tb where create_time>? and state=?  and comid = ? ",
					new Object[]{System.currentTimeMillis()/1000-2*86400,0,comId}) ;
		/*	Map<String, Object> map = pgService.getMap("select sum(amount) free,sum(total) sharenumber from remain_berth_tb " +
					" where state=? and comid=? ", new Object[]{0, comId}); 
			if(map != null){
				if(map.get("sharenumber") != null){
					total = Long.valueOf(map.get("sharenumber") + "");
				}
				if(map.get("free") != null){
					free = Long.valueOf(map.get("free") + "");
				}
				if(total > free){
					busy = total - free;
				}
			}*/
			total = (Integer) comMap.get("share_number");
			if(busy>total)
				busy=total.longValue();
			infoMap.put("total",total);
			infoMap.put("free", total-busy);
			infoMap.put("busy",busy );
			Long time2 = System.currentTimeMillis()/1000;
			logger.error("getParkNumber>>>comid:"+comId+",infoMap:"+infoMap+"，耗时"+(time2-time1)+"秒");
		} catch (Exception e) {
			logger.error("getParkNumber>>>comid:"+comId+",infoMap:"+infoMap, e);
		}
	}
	
	
	/**
	 * 验证token是否有效
	 * @param token
	 * @return 公司编号
	 */
/*	private Long validToken(String token) {
		Map tokenMap = pgService.getMap("select * from user_session_tb where token=?", new Object[]{token});
 		Long comId = null;
 		Long uin  = null;
		if(tokenMap!=null&&tokenMap.get("comid")!=null){
			comId = (Long) tokenMap.get("comid");
			uin = (Long)tokenMap.get("uin");
		}
		
		return comId;
	}*/
	/**
	 * 从缓存验证token
	 * @param token
	 * @return
	 */
	private Long validMemToken(String token){
		Map<String,String >  parkTokenCacheMap = memcacheUtils.doParkUserTokenCache("parkuser_token", null, null);
//		System.out.println(parkTokenCacheMap);
		Long comId = null;
 		Long uin  = null;
		if(parkTokenCacheMap!=null){
			if(parkTokenCacheMap.get(token)!=null){
				String parkUserToken = parkTokenCacheMap.get(token);
				//logger.error("parkusercache:"+parkUserToken);
				if(parkUserToken!=null&&parkUserToken.indexOf("_")!=-1){
					comId = Long.valueOf(parkUserToken.split("_")[1]);
					uin =  Long.valueOf(parkUserToken.split("_")[0]);
				}
			}else {
				logger.error("parkusercache缓存中没有，从数据中查询：token="+token);
				Map tokenMap = pgService.getMap("select uin,comid from user_session_tb where token=?", new Object[]{token});
				if(tokenMap!=null){
					comId = (Long)tokenMap.get("comid");
					uin = (Long)tokenMap.get("uin");
					parkTokenCacheMap.put(token, uin+"_"+comId);
					memcacheUtils.doParkUserTokenCache("parkuser_token", parkTokenCacheMap, "update");
				}else {
					logger.error("token:"+token+" is invalid");
				}
			}
		}else {
			logger.error("parkusercache为空，从数据中查询：token="+token);
			Map tokenMap = pgService.getMap("select uin,comid from user_session_tb where token=?", new Object[]{token});
			if(tokenMap!=null){
				comId = (Long)tokenMap.get("comid");
				uin = (Long)tokenMap.get("uin");
				parkTokenCacheMap = new HashMap<String, String>();
				parkTokenCacheMap.put(token, uin+"_"+comId);
				memcacheUtils.doParkUserTokenCache("parkuser_token", parkTokenCacheMap, "update");
			}else {
				logger.error("token:"+token+" is invalid");
			}
		}
		return comId;
	}

}
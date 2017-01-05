package com.mserver.struts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.mserver.AjaxUtil;
import com.mserver.service.PgOnlyReadService;
import com.mserver.service.PgService;
import com.mserver.utils.Check;
import com.mserver.utils.MemcacheUtils;
import com.mserver.utils.RequestUtil;

/**
 * 处理收费员请求消息服务
 * @author Administrator
 *
 */
public class MessageServerAction extends Action{
	
	@Autowired
	private PgService pgService;
	@Autowired
	private PgOnlyReadService pOnlyReadService;
	@Autowired
	private MemcacheUtils memcacheUtils;
	private Logger logger = Logger.getLogger(MessageServerHXAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String token =RequestUtil.processParams(request, "token");
		String out = RequestUtil.processParams(request, "out");
		String berthid =RequestUtil.processParams(request, "berthid");
		Map<String,Object> infoMap  = new HashMap<String, Object>();
		Long uin =null;
		Long comId = null;
		if(token==null||"null".equals(token)||"".equals(token)){
			String notoken = RequestUtil.processParams(request, "notoken");
			if("true".equals(notoken)){
				comId = RequestUtil.getLong(request, "comid",-1L);
				uin = RequestUtil.getLong(request, "uin",-1L);
			}else{
				AjaxUtil.ajaxOutput(response,"{\"mtype\":-1}");
				return null;
			}
		}else {
			Map<String,String >  parkTokenCacheMap = memcacheUtils.doParkUserTokenCache("parkuser_token", null, null);
			//System.out.println(parkTokenCacheMap);
			if(parkTokenCacheMap!=null){
				if(parkTokenCacheMap.get(token)!=null){
					String parkUserToken = parkTokenCacheMap.get(token);
					if(parkUserToken.indexOf("_")!=-1){
						try {
							comId = Long.valueOf(parkUserToken.split("_")[1]);
							uin =  Long.valueOf(parkUserToken.split("_")[0]);
						} catch (Exception e) {
							logger.error("parkusercache,读取缓存失败!");
							e.printStackTrace();
						}
					}
				}else {
					//logger.error("parkusercache缓存中没有，从数据中查询：token="+token);
					Map tokenMap = pgService.getMap("select uin,comid,groupid from user_session_tb where token=?", new Object[]{token});
					if(tokenMap!=null){
						comId = (Long)tokenMap.get("comid");
						uin = (Long)tokenMap.get("uin");
						parkTokenCacheMap.put(token, uin+"_"+comId+"_"+tokenMap.get("groupid"));
						memcacheUtils.doParkUserTokenCache("parkuser_token", parkTokenCacheMap, "update");
					}else {
						//logger.error("token:"+token+" is invalid");
					}
				}
			}else {
				logger.error("parkusercache为空，从数据中查询：token="+token);
				Map tokenMap = pgService.getMap("select uin,comid,groupid from user_session_tb where token=?", new Object[]{token});
				parkTokenCacheMap = memcacheUtils.doParkUserTokenCache("parkuser_token", null, null);
				if(tokenMap!=null){
					//System.out.println(parkTokenCacheMap);
					comId = (Long)tokenMap.get("comid");
					uin = (Long)tokenMap.get("uin");
					
					if(parkTokenCacheMap!=null){
						parkTokenCacheMap.put(token, uin+"_"+comId+"_"+tokenMap.get("groupid"));
						memcacheUtils.doParkUserTokenCache("parkuser_token", parkTokenCacheMap, "update");
					}else {
						Long ntime = System.currentTimeMillis()/1000;
						if(ntime%60==0){
							parkTokenCacheMap = new HashMap<String, String>();
							parkTokenCacheMap.put(token,  uin+"_"+comId+"_"+tokenMap.get("groupid"));
							memcacheUtils.doParkUserTokenCache("parkuser_token", parkTokenCacheMap, "update");
						}
					}
				}else {
					logger.error(">>>>>parkusertokenloss读取token缓存失败，不更新....");
				}
			}
		}
		String currId =RequestUtil.processParams(request, "currid");
		Long mesgId =0L;
		if(Check.isLong(currId))
			mesgId=Long.parseLong(currId);
		
		//http://127.0.0.1/mserver/getmesg.do?token=f9e3f98c8f53d5fcbdd2a6a54bbbc9d7&currid=1019&out=json
		updateHeartBeat(uin);//更新心跳
		if(out.equals("json")){
			if(uin!=null){
				String ret = "{}";
				try {
					ret = processMessage(uin,berthid);
				} catch (Exception e) {
					ret = "{}";
					e.printStackTrace();
				}
				if(!ret.equals("{}"))
					logger.error("uid:"+uin+",berthid:"+berthid+",getmessage:"+ret);
				AjaxUtil.ajaxOutput(response,ret);
			}else
				AjaxUtil.ajaxOutput(response,"{\"mtype\":-1}");
		}
		/*else {
			if(comId!=null){
				String sql = "select * from order_message_tb where comid=? and state=? and id>? and create_time >? order by id desc";
				List<Map> messageList = pOnlyReadService.getAll(sql, new Object[]{comId,0,mesgId,System.currentTimeMillis()/1000-24*60*60});
				List<Map<String , Object >> infoList = new ArrayList<Map<String,Object>>();
				if(messageList!=null&&messageList.size()>0){
					for(Map map : messageList){
						Map<String , Object > infomMap	 = new HashMap<String, Object>();
						Long btime = System.currentTimeMillis()/1000;
						if(map.get("btime")!=null)
							btime = (Long)map.get("btime");
						Long etime = System.currentTimeMillis()/1000;
						if(map.get("etime")!=null)
							etime = (Long)map.get("etime");
						
						infomMap.put("state", map.get("state"));
						infomMap.put("carnumber", map.get("car_number"));
						infomMap.put("orderid", map.get("orderid"));
						infomMap.put("total", map.get("order_total"));
						infomMap.put("btime",TimeTools.getTime_yyMMdd_HHmm(btime*1000).substring(9));
						infomMap.put("etime", TimeTools.getTime_yyMMdd_HHmm(etime*1000).substring(9));
						////infomMap.put("duration", map.get("duartion"));
						infomMap.put("issale", map.get("is_sale"));
						infomMap.put("id", map.get("id"));
						infomMap.put("mtype", "1");
						infoList.add(infomMap);
					}
					if(out.equals("json"))
						AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoList));
					else
						AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoList,"1"));
				}else {
					infoMap.put("result", "0");
					infoMap.put("message", "无消息");
					if(out.equals("json"))
						AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
					else
						AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap,"0"));
				}
			}else {
				infoMap.put("result", "fail");
				infoMap.put("message", "token无效!");
				if(out.equals("json"))
					AjaxUtil.ajaxOutput(response, StringUtils.createJson(infoMap));
				else
					AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap,"0"));
			}
		}*/
		return null;
	}
	
	private void updateHeartBeat(Long uin){
		try {
			Long ntime = System.currentTimeMillis()/1000;
			if(uin != null && uin > 0){
				memcacheUtils.doParkerTokentimCache("parker_token"+uin, ntime, "update");
			}
		} catch (Exception e) {
			logger.error("updateHeartBeat>>>uin:"+uin, e);
		}
	}
	
	private String processMessage (Long uin,String berthid) throws Exception{
		String result = "{}";
		if (uin != null) {
			Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("parkuser_messages", null, null);
			if(messCacheMap!=null){
				result= messCacheMap.get(uin);
				if(result==null){
					result = "{}";
				}else {
					messCacheMap.remove(uin);
					memcacheUtils.doMapLongStringCache("parkuser_messages", messCacheMap, "update");
				}
			}
		}
		if("{}".equals(result)){
			if(Check.isNumber(berthid)){
				Long bid = Long.valueOf(berthid);
				Map<Long, String> messCacheMap = memcacheUtils.doMapLongStringCache("parkuser_messages", null, null);
				if(messCacheMap!=null){
					result= messCacheMap.get(bid);
					if(result==null){
						result = "{}";
					}else {
						messCacheMap.remove(bid);
						memcacheUtils.doMapLongStringCache("parkuser_messages", messCacheMap, "update");
					}
				}
			}
		}
		/*if(!"{}".equals(result)){
			System.err.println(uin+",message--->>>:"+result);
			try {
				Integer mtype = getJsonIntegerValue(result, "mtype");
				if(mtype!=null&&mtype==3){
					JSONObject info = getJsonObjectValue(result, "info");
					String orderId = info.getString("orderid");
					int ret = pgService.update("update order_message_tb set already_read =? where orderid=?", new Object[]{1,Long.valueOf(orderId)});
					logger.error(">>>泊车接车更新消息，订单："+orderId+",ret="+ret);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}*/
		return result;
		/*	String sql = "select * from order_message_tb where uin=? and already_read =? order by id desc limit ?";
			Map messageMap = pOnlyReadService.getMap(sql, new Object[] { uin,0, 1 });
			if (messageMap != null && !messageMap.isEmpty()) {
				Integer mtype = (Integer)messageMap.get("message_type");
				if(mtype==3){//泊车消息
					//System.out.println(messageMap);
				    pgService.update("update order_message_tb set already_read =? where id=?", new Object[]{1,messageMap.get("id")});
					result = "{\"mtype\":"+mtype+",\"info\":{\"orderid\":\""+messageMap.get("orderid")+"\""+
							",\"carnumber\":\""+messageMap.get("car_number")+"\",\"duration\":\""+messageMap.get("duartion")+"\"," +
							"\"state\":\""+messageMap.get("state")+"\"}}";
				}else if(mtype==4){//收费员离开工作站通知
					 pgService.update("update order_message_tb set already_read =? where id=?", new Object[]{1,messageMap.get("id")});
						result = "{\"mtype\":"+mtype+",\"info\":{}}";
				}else {
					Long orderId = (Long)messageMap.get("orderid");
					Long btime = (Long)messageMap.get("btime");
					Long etime = (Long)messageMap.get("etime");
					Map<String, Object> infomMap = new HashMap<String, Object>();
					infomMap.put("btime",TimeTools.getTime_yyMMdd_HHmm(btime*1000).substring(9));
					infomMap.put("etime", TimeTools.getTime_yyMMdd_HHmm(etime*1000).substring(9));
					infomMap.put("carnumber", messageMap.get("car_number"));
					infomMap.put("duration",messageMap.get("duartion"));
					infomMap.put("total", messageMap.get("order_total"));
					infomMap.put("state", messageMap.get("state"));//0:未支付 1：已支付 
					infomMap.put("orderid",orderId);
					//infomMap.put("mtype",messageMap.get("message_type"));//0订单消息，1预定车位
					String json = StringUtils.createJson(infomMap);
					//更新消息为已读状态
					pgService.update("update order_message_tb set already_read =? where id=?", new Object[]{1,messageMap.get("id")});
					//logger.error("收费员收到消息："+uin+"{\"mtype\":"+messageMap.get("message_type")+",\"info\":"+json+"}");
					result= "{\"mtype\":"+mtype+",\"info\":"+json+"}";
				}
			}
		}
		if(!result.equals("{}"))
			logger.error(">>>>收费员消息："+uin+","+result);
		return result;*/
	}
	  
	/**
	 * 验证token是否有效
	 * @param token
	 * @return 公司编号
	 */
	private Long validToken(String token) {
		Map tokenMap = pOnlyReadService.getMap("select * from user_session_tb where token=?", new Object[]{token});
 		Long comId = null;
		if(tokenMap!=null&&tokenMap.get("comid")!=null){
			comId = (Long) tokenMap.get("comid");
		}
		return comId;
	}
	/**
	 * 验证token是否有效
	 * @param token
	 * @return 公司编号
	 */
	private Long getUinFormToken(String token) {
		Map tokenMap = pOnlyReadService.getMap("select * from user_session_tb where token=?", new Object[]{token});
 		Long comId = null;
		if(tokenMap!=null&&tokenMap.get("uin")!=null){
			comId = (Long) tokenMap.get("uin");
		}
		return comId;
	}
	
	/**
	 * json分析
	 * @param rescontent
	 * @param key
	 * @return
	 */
	private  Integer getJsonIntegerValue(String rescontent, String key) {
		JSONObject jsonObject;
		Integer v = null;
		try {
			jsonObject = new JSONObject(rescontent);
			v = jsonObject.getInt(key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return v;
	}
	/**
	 * json分析
	 * @param rescontent
	 * @param key
	 * @return
	 */
	private  String getJsonValue(String rescontent, String key) {
		JSONObject jsonObject;
		String v = null;
		try {
			jsonObject = new JSONObject(rescontent);
			v = jsonObject.getString(key)+"";
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return v;
	}
	/**
	 * json分析
	 * @param rescontent
	 * @param key
	 * @return
	 */
	private  JSONObject getJsonObjectValue(String rescontent, String key) {
		JSONObject jsonObject;
		JSONObject v = null;
		try {
			jsonObject = new JSONObject(rescontent);
			v = jsonObject.getJSONObject(key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return v;
	}
}
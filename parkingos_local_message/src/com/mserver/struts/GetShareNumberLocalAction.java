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
import org.springframework.beans.factory.annotation.Autowired;

import com.mserver.AjaxUtil;
import com.mserver.service.PgOnlyReadService;
import com.mserver.service.PgService;
import com.mserver.utils.HttpProxy;
import com.mserver.utils.RequestUtil;
import com.mserver.utils.StringUtils;
import com.mserver.utils.TimeTools;

public class GetShareNumberLocalAction extends Action {

	@Autowired
	private PgOnlyReadService pgService;

	@Autowired
	private PgService dbService;

	private Logger logger = Logger.getLogger(GetShareNumberAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String token = RequestUtil.processParams(request, "token");
		String out = RequestUtil.processParams(request, "out");
		Long comId = RequestUtil.getLong(request, "comid", null);
		Long type = RequestUtil.getLong(request, "type", Long.valueOf(-1L));
		Map<String, Object> infoMap = new HashMap<String, Object>();

		if ((token == null) || ("null".equals(token)) || ("".equals(token))) {
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap, "0"));
			return null;
		}
		comId = validMemToken(token);

		if (comId != null) {
			if (type.longValue() == 1L) {
				Long passid = RequestUtil.getLong(request, "passid",
						Long.valueOf(-1L));
				Map passMap = this.pgService
						.getMap("select * from com_pass_tb where comid = ? and id = ? ",
								new Object[] { comId, passid });
				if ((passMap != null) && (passMap.get("worksite_id") != null)) {
					final long workid = Long.parseLong(passMap
							.get("worksite_id") + "");
					final String equipmentmodel = RequestUtil.processParams(
							request, "equipmentmodel");
					final String memoryspace = RequestUtil.processParams(
							request, "memoryspace");
					final String internalspace = RequestUtil.processParams(
							request, "internalspace");
					final long upload_time = System.currentTimeMillis() / 1000L;
					int r = this.dbService
							.update("update com_worksite_tb set host_name=?,host_memory=?,host_internal=?,upload_time=? where id = ? ",
									new Object[] { equipmentmodel, memoryspace,
											internalspace,
											Long.valueOf(upload_time),
											Long.valueOf(workid) });
					this.logger.error("upload info passid:" + passid
							+ ",equipmentmodel:" + equipmentmodel
							+ ",memoryspace:" + memoryspace + ",internalspace:"
							+ internalspace + ",r:" + r);
					if (r == 1) {
						new Thread(new Runnable() {
							public void run() {
								String token = "";
								Map map = dbService
										.getMap("select * from  sync_time_tb where id = ? ",
												new Object[] { Integer
														.valueOf(1) });
								if ((map != null) && (map.get("token") != null)) {
									token = (String) map.get("token");
								}
								String tk = token;
								HttpProxy httpProxy = new HttpProxy();
								Map<String, String> parammap = new HashMap<String, String>();
								parammap.put("worksite_id", workid + "");
								parammap.put("equipmentmodel", equipmentmodel);
								parammap.put("memoryspace", memoryspace);
								parammap.put("internalspace", internalspace
										+ "");
								parammap.put("upload_time", upload_time + "");
								String ret = null;
								try {
									String url = "http://s.tingchebao.com/zld/syncInter.do?action=uploadworksite&token=";
									System.out
											.println("*******************************************************:"
													+ url);
									ret = httpProxy.doPost(url + tk, parammap);
									logger.error(ret);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						}).start();
					}
				}
			}
			getParkNumber(comId, infoMap);

			Long childParkCount = this.pgService.getLong(
					"select substation from com_info_tb where id=?",
					new Object[] { comId });
			if ((childParkCount != null) && (childParkCount.longValue() > 0L)) {
				String result = "";
				HttpProxy httpProxy = new HttpProxy();
				Map<String, String> parammap = new HashMap<String, String>();
				parammap.put("token", token);
				parammap.put("out", out);
				parammap.put("comid", comId+"");
				String url = "http://s.tingchebao.com/mserver/getshare.do?";
				result = httpProxy.doPost(url, parammap, Integer.valueOf(2500));
				if ((result == null) || (result.equals(""))) {
					if (out.equals("json"))
						result = StringUtils.createJson(infoMap);
					else
						result = StringUtils.createXML(infoMap);
				}
				logger.error(result);
				AjaxUtil.ajaxOutput(response, result);
				return null;
			}

		} else {
			infoMap.put("result", "fail");
			infoMap.put("message", "token无效!");
			AjaxUtil.ajaxOutput(response, StringUtils.createXML(infoMap));
		}
		String result = "";
		if (out.equals("json"))
			result = StringUtils.createJson(infoMap);
		else
			result = StringUtils.createXML(infoMap);
		System.out.println(result);
		AjaxUtil.ajaxOutput(response, result);
		return null;
	}

	private void getParkNumber(Long comId, Map<String, Object> infoMap) {
		Integer shareNumber = Integer.valueOf(0);

		long time2 = TimeTools.getBeginTime(
				Long.valueOf(System.currentTimeMillis() - 172800000L))
				.longValue();
		long time16 = TimeTools.getBeginTime(
				Long.valueOf(System.currentTimeMillis() - 1382400000L))
				.longValue();
		String sql = "select count(ID) from order_tb where comid=? and create_time>? and state=? ";
		Long count = this.pgService
				.getLong(sql, new Object[] { comId, Long.valueOf(time2),
						Integer.valueOf(0) });
		String sql1 = "select count(ID) from order_tb where comid=? and create_time>? and create_time<? and state=? ";
		Long count1 = this.pgService.getLong(sql1,
				new Object[] { comId, Long.valueOf(time16),
						Long.valueOf(time2), Integer.valueOf(0) });
		int invalid = (int) (count1.longValue() * 2L / 14L);

		Map map = this.pgService
				.getMap("select share_number,invalid_order from com_info_tb where id=?",
						new Object[] { comId });
		Long invalid_order = (Long) map.get("invalid_order");
		shareNumber = (Integer) map.get("share_number");

		infoMap.put("total", shareNumber);
		infoMap.put(
				"free",
				Long.valueOf(shareNumber.intValue() - count.longValue()
						- invalid < 0L ? 0L : shareNumber.intValue()
						- count.longValue() - invalid));
		infoMap.put("busy",
				Long.valueOf(count.longValue() - invalid_order.longValue()));
	}

	private Long validMemToken(String token) {
		Long comId = null;
		logger.error("parkusercache为空，从数据中查询：token=" + token);
		Map tokenMap = this.pgService.getMap(
				"select uin,comid from user_session_tb where token=?",
				new Object[] { token });
		if (tokenMap != null) {
			comId = (Long) tokenMap.get("comid");
		} else {
			this.logger.error("token:" + token + " is invalid");
		}
		return comId;
	}
}
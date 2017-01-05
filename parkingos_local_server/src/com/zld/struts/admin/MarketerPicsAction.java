package com.zld.struts.admin;

import java.io.OutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.zld.AjaxUtil;
import com.zld.impl.MongoClientFactory;
import com.zld.service.DataBaseService;
import com.zld.utils.RequestUtil;

public class MarketerPicsAction extends Action {
	@Autowired
	private DataBaseService daService;
	
	private Logger logger = Logger.getLogger(MarketerPicsAction.class);
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		String action = RequestUtil.processParams(request, "action");
		Long comid = (Long)request.getSession().getAttribute("comid");
		if(comid==null){
			response.sendRedirect("login.do");
			return null;
		}
		if(action.equals("downloadvisitpics")){
			Long visitid = RequestUtil.getLong(request, "id", -1L);
			downloadVisitPics(visitid, request, response);
		}
		return null;
	}
	
	private void downloadVisitPics (Long visitid,HttpServletRequest request,HttpServletResponse response) throws Exception{
		logger.info("download visitPics from mongodb....");
		System.err.println("downloadVisitPics from mongodb file:visitid="+visitid);
		if(visitid!=null){
			DB db = MongoClientFactory.getInstance().getMongoDBBuilder("zld");//
			DBCollection collection = db.getCollection("visit_pics");
			BasicDBObject document = new BasicDBObject();
			BasicDBObject condation = new BasicDBObject();
			document.put("visitid", visitid);
			//按生成时间查最近的数据
			condation.put("ctime", -1);
			DBCursor objs = collection.find(document).sort(condation).limit(1);
			DBObject obj = null;
			while (objs.hasNext()) {
				obj = objs.next();
			}
			if(obj == null){
				AjaxUtil.ajaxOutput(response, "");
				return;
			}
			byte[] content = (byte[])obj.get("content");
			db.requestDone();
			response.setDateHeader("Expires", System.currentTimeMillis()+4*60*60*1000);
			//response.setStatus(httpc);
			Calendar c = Calendar.getInstance();
			c.set(1970, 1, 1, 1, 1, 1);
			response.setHeader("Last-Modified", c.getTime().toString());
			response.setContentLength(content.length);
			response.setContentType("image/jpeg");
		    OutputStream o = response.getOutputStream();
		    o.write(content);
		    o.flush();
		    o.close();
		    response.flushBuffer();
		    //response.reset();
		    System.out.println("mongdb over.....");
		}else {
			AjaxUtil.ajaxOutput(response, "-1");
		}
	}
}

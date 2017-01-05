package com.zld.struts.admin;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.zld.impl.MongoClientFactory;


/**
 * 处理车场照片，从mongodb读取
 * @author Administrator
 *
 */
@Path("/")
public class DownloadFilter {
	
	
	/**
	 * http://127.0.0.1/zld/parkpics/1200_1408443658.jpeg
	 */
	@GET
	@Path("/{picurl}")
	@Produces(MediaType.TEXT_HTML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)       
	public void getLogoFile(@PathParam("picurl") String facklink,@Context HttpServletRequest request, @Context HttpServletResponse response)
			throws IOException {
		System.err.println("mongodb file:"+facklink);
		if(facklink!=null){
			DB db = MongoClientFactory.getInstance().getMongoDBBuilder("zld");//
			DBCollection collection = db.getCollection("park_pics");
			BasicDBObject document = new BasicDBObject();
			document.put("filename", facklink);
			DBObject obj = collection.findOne(document);
			if(obj == null){
				response.sendRedirect("http://sysimages.tq.cn/images/webchat_101001/common/kefu.png");
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
			response.sendRedirect("http://sysimages.tq.cn/images/webchat_101001/common/kefu.png");
		}
	}

}

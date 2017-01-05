package com.zld.struts.admin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.zld.AjaxUtil;
import com.zld.service.DataBaseService;
import com.zld.utils.RequestUtil;

public class TranferUrlAction extends HttpServlet {
	
	DataBaseService dataBaseService =null;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2049754133208266760L;

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//System.err.println(dataBaseService);
		Long id =RequestUtil.getLong(request, "p", -1L);
		if(id!=-1){
			Map tranMap = dataBaseService.getMap("select * from transfer_url_tb where id=? ", new Object[]{id});
			if(tranMap!=null){
				Integer state = (Integer)tranMap.get("state");
				if(state!=null&&state==0){
					String url = (String)tranMap.get("url");
					response.sendRedirect(url);
					return;
				}
			}
		}
		AjaxUtil.ajaxOutput(response, "-1");
	}
	
	@Override
	public void init() throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		dataBaseService= (DataBaseService) ctx.getBean("dataBaseService");
		//启动定时任务
		//System.err.println(">>>>>init:database:"+dataBaseService);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		//dataBaseService=null;
		//System.err.println(">>>>>destroy serlvet:"+dataBaseService);
		super.destroy();
	}

}

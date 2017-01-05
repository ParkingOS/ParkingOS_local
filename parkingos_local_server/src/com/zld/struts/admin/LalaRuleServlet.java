package com.zld.struts.admin;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zld.AjaxUtil;

public class LalaRuleServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1844412464330025572L;

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

		File file = new File(getServletContext().getRealPath("/")+"/lalarule.jsp");
		//System.out.println(getServletContext().getRealPath("/")+"lalarule.jsp");
		if(file.exists()){
			//System.err.println(file.lastModified());
			//System.err.println(getServletContext().getRealPath("/"));
			AjaxUtil.ajaxOutput(response, file.lastModified()+"");
		}else {
			AjaxUtil.ajaxOutput(response, "0");
		}
	}

}

package com.mserver.schedule;

import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * 每个月的第一天统计分红情况
 */

public class TaskServlet extends HttpServlet {

	Logger logger = Logger.getLogger(this.getClass());

	public static Timer timer = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = 9122771659780215777L;

	@Override
	public void init() throws ServletException {
		ApplicationContext ctx = WebApplicationContextUtils
				.getWebApplicationContext(getServletContext());
		//啟動分紅計算任務
		new TimerManager(ctx);
	}

}

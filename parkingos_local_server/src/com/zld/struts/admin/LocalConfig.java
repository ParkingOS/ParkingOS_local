package com.zld.struts.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibatis.common.resources.Resources;
import com.zld.AjaxUtil;
import com.zld.CustomDefind;
import com.zld.service.DataBaseService;
import com.zld.utils.RequestUtil;
import com.zld.utils.StringUtils;

public class LocalConfig extends Action{
	@Autowired
	private DataBaseService daService;
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		String action = RequestUtil.processParams(request, "action");
		if("".equals(action)){
			return mapping.findForward("configpage");
		}else if ("query".equals(action)) {
			String comid = readValue("config.properties", "COMID");
			String secret = readValue("config.properties", "SECRET");
			StringBuffer comBuffer = new StringBuffer("[");
				comBuffer.append("{\"name\":\"comid\",\"value\":\""+comid+"\"},");
				comBuffer.append("{\"name\":\"secret\",\"value\":\""+secret+"\"},");
			String result = comBuffer.toString();
			result = result.substring(0,result.length()-1)+"]";
			request.setAttribute("cominfo", result);
			
			return mapping.findForward("list");
		}else if ("edit".equals(action)) {
			String comid =RequestUtil.processParams(request, "comid");
			String secret =RequestUtil.processParams(request, "secret");
			setProper("COMID",comid);
			setProper("SECRET",secret);
		}else if ("editpasspage".equals(action)) {
			return mapping.findForward("passmanage");
		}else if ("queryadmin".equals(action)) {
			List list = daService.getAll("select id,nickname,strid from user_info_tb where nickname = ? ", new Object[]{"admin"});
			AjaxUtil.ajaxOutput(response, StringUtils.createJson(list));
		}else if ("editadminpass".equals(action)) {
			String pass1 =RequestUtil.processParams(request, "newpass1");
			String pass2 =RequestUtil.processParams(request, "newpass2");
			int result = 0;
			if(pass1!=null&&!pass1.equals("")&&pass2!=null&&!pass2.equals("")){
				if(pass1.equals(pass2)){
					String md5pass = StringUtils.MD5(pass1);
					md5pass = StringUtils.MD5(md5pass+"zldtingchebao201410092009");
					result = daService.update("update user_info_tb set password =? ,md5pass=? where nickname =? ", new Object[]{pass1,md5pass,"admin"});
				}
			}
			AjaxUtil.ajaxOutput(response, result+"");
		}
		return null;
		
	}
	public static String readValue(String filePath,String key) {
	  Properties props = new Properties();
	  try {
	    	 File file = Resources.getResourceAsFile(filePath);
	    	 props.load(new FileInputStream(file));
	         String value = props.getProperty (key);
	         return value;
	        } catch (Exception e) {
	         e.printStackTrace();
	         return null;
	        }   
	 }
	public static void setProper(String key,String value) throws IOException{
		Properties prop = new Properties();
		FileOutputStream fos = null;
		try {
			File file = Resources.getResourceAsFile("config.properties");
	    	prop.load(new FileInputStream(file));
			prop.setProperty(key, value);
			fos = new FileOutputStream(Resources.getResourceAsFile("config.properties"));
			prop.store(fos, null);
			CustomDefind.reSet();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(fos!=null){
				fos.close();
			}
			
		}
		
		
	}
}

package com.mserver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.HttpServletResponse;

public class AjaxUtil {
  
 
  
    /** 
     * 返回AJAX调用结果 String类型
     * @param response
     * @param outputString 
     * @throws java.io.IOException 
     */  
    public static void ajaxOutput(HttpServletResponse response, String outputString) throws IOException {  
        response.setContentType("text/html; charset=gbk");  
        PrintWriter printWriter = response.getWriter();
        printWriter.write(outputString);  
        printWriter.flush();  
        printWriter.close();
    }  
    
    /** 
     * 返回AJAX调用结果 结果类型是INT
     * @param response
     * @param outputInt
     * @throws java.io.IOException
     */  
    public static void ajaxOutputRint(HttpServletResponse response, int outputInt) throws IOException {  
        response.setContentType("text/html; charset=gbk");  
        PrintWriter printWriter = response.getWriter();
        printWriter.write(outputInt);
        printWriter.flush();
        printWriter.close();
    }  
    
    /** 
     *解码Ajax urf-8编码后的url形式中文参数 返回UTF-8结果
     *@param String
     */ 
	public static String decodeUTF8(String someStr) {
		String newStr = null;
		if(someStr!=null&&someStr.equals(""))
			return "";
		if(someStr!=null&&!someStr.equals("")) {
			try {
				newStr = URLDecoder.decode(someStr,"UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return newStr;
	}
}  

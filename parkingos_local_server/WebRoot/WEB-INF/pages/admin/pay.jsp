<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="com.alipay.util.*"%>
<!DOCTYPE HTML PUBLIC
"-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html;
charset=UTF-8">
<title></title>

</head>
<body>
	<%
		String partner = ""; //partner合作伙伴id（必须填写）  
	    String privateKey = "";//partner 的对应交易安全校验码（必须填写）  

	//**********************************************************************************  

	//如果您服务器不支持https交互，可以使用http的验证查询地址        
	//String alipayNotifyURL ="https://www.alipay.com/cooperate/gateway.do?service=notify_verify"  
	String alipayNotifyURL ="http://notify.alipay.com/trade/notify_query.do?partner="  
	                     + partner  +"&notify_id=" +request.getParameter("notify_id");  
	String sign=request.getParameter("sign");  
	            
	//获取支付宝ATN返回结果，true是正确的订单信息，false 是无效的  
	String responseTxt =CheckURL.check(alipayNotifyURL);  
	Map params = newHashMap();  
	//获得POST 过来参数设置到新的params中  
	Map  requestParams = request.getParameterMap();  
	for(Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {  
	    String name =(String) iter.next();  
	    String[] values = (String[])requestParams.get(name);  
	    String valueStr = "";
	    for (int i = 0; i < values.length; i++) {  
			valueStr = (i == values.length - 1) ? valueStr +values[i] : valueStr + values[i] +",";         
	    }  
	    params.put(name,valueStr);  
	}  
	String mysign =com.alipay.util.SignatureHelper_return.sign(params, privateKey);  
	//打印，收到消息比对sign的计算结果和传递来的sign是否匹配  
	//out.println(mysign+"——————–"+sign);  
	if(mysign.equals(request.getParameter("sign")) &&responseTxt.equals("true") ){  
		out.println("交易成功");  
	}else{  
	    out.println("交易失败");  
	}
	%>
</body>

</html>

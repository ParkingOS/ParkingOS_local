<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<title>真来电ETC</title>
</head>
<body>
<div id="content" style="font-size:50px">
<a href="#" onclick="openURL()" >停车宝车主版下载</a>
</div>
<script>

function openURL(){
	if(navigator.userAgent.indexOf("MicroMessenger")!=-1)//微信来的，指向应用宝
		location.href= "http://t.cn/RvbHYkL";
	else
		location.href= "http://d.tingchebao.com/download/tingchebao.apk";
}
openURL();
</script>
</body>


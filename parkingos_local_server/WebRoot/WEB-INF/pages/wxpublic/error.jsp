<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>停车宝</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<style type="text/css">
.error {
	text-align: center;
	margin-top: 60%;
}

.noparkers_error {
	text-align: center;
	margin-top: 40%;
}

.noparkers {
	width: 200px;
	height: 200px;
}
</style>
<script src="js/jquery.js" type="text/javascript">//表格</script>
<script type="text/javascript">
$(document).ready(function() {
	var type = '${type}';
	if(type == "1"){
		document.getElementById("custome_error").style.display = "none";
	}else{
		document.getElementById("noparkers_error").style.display = "none";
	}
});
</script>
</head>
<body style="background-color:#F0F0F0;">
<div id="custome_error" class="error">
<div><img src="images/messager_warning.gif"/></div> 
<div style="margin-top:5px;font-size:15px;">出错了 </div>
</div>
<div id="noparkers_error" class="noparkers_error">
<div><img class="noparkers" src="images/wxpublic/error.png"/></div> 
<div style="margin-top:5px;font-size:15px;color:#B0AEAD">收费员回家吃饭去了~~~</div>
</div>
</body>
</html>

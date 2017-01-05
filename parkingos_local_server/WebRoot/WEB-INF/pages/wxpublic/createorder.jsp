<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>${cname}</title>
<script type="text/javascript">
	javascript: window.history.forward(1);
</script>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=12" />
<script src="js/jquery.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
<style type="text/css">
#scroller li {
    padding:0 10px;
    height:50px;
    line-height:50px;
    border-bottom:1px solid #EBEBEB;
    border-top:1px solid #EBEBEB;
    background-color:white;
    font-size:14px;
}

a{
	text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
	
	position: relative;
	top:-35px;
	left:30px;
}

li{
	border-top: 0;
	margin-top: 5px;
}
.img1{
	width:20px;
	height:20px;
	margin-top:15px;
}
</style>

<style type="text/css">
.carnumber {
	margin-left: 10px;
	border-width: 0;
	font-size: 16px;
}

.wx_pay{
	border-radius:5px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:32px;
	font-size:15px;
	background-color:#04BE02;
	color:white;
	border:0px;
}

.hide {
	display: none;
}

.error {
	color: red;
	font-size: 15px;
}

.company_name {
	color: gray;
    text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
}

input::-webkit-outer-spin-button,input::-webkit-inner-spin-button {
	-webkit-appearance: none;
}

.wxpay-logo {
	content: "";
	background: url(images/wxpublic/wxpay_logo.png) no-repeat;
	width: 71px;
	height: 19px;
	display: inline-block;
	-webkit-background-size: 71px 19px;
	background-size: 71px 19px;
	-moz-transform: translateX(-50%);
	-webkit-transform: translateX(-50%);
	-ms-transform: translateX(-50%);
	transform: translateX(-50%);
	margin: 20px 0 20px 50%;
}

.noorder{
	text-align:center;
	color:red;
	margin-top:55%;
	font-size:16px;
}
</style>
</head>
<body style="background-color:#EEEEEE;">
<div class="valide" id="wrapper" style="margin-top:-45px;">
<form method="post" role="form" action="wxpfast.do?action=createorder" id="payform">
	<div id="scroller">
		<ul id="thelist">
			<li style="margin-top:32px;">
				<div class="company_name"><span>车位编号</span><input type="number" disabled="true" name="fee" class="carnumber" value="${spaceid}" /></div>
			</li>
			<input type="text" name="openid" class="hide" value="${openid}">
			<input type="text" name="codeid" class="hide" value="${codeid}">
			<input type="text" name="uid" class="hide" value="10700">
			
			<input type="button" id="paysubmit" class="wx_pay" onclick='check();' value="确认停车" />
			<div class="wxpay-logo"></div>
			<div style="text-align:center;" id="error" class="error"></div>
		</ul>
	</div>
</form>
</div>
<div class="invalide noorder hide">该车位二维码失效</div>
<div class="used noorder hide">该车位已被占用</div>
<div id="footer"></div>
<script type="text/javascript">
if("${state}" == "2"){
	$(".valide").addClass("hide");
	$(".invalide").removeClass("hide");
}

if("${state}" == "1"){
	$(".valide").addClass("hide");
	$(".used").removeClass("hide");
}

function check(){
	$("#payform")[0].submit();
}
</script>
</body>
</html>

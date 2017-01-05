<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>购买停车券</title>
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
}

a{
	text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
	
	position: relative;
	top:-35px;
	left:30px;
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

.wx_pay {
	border-radius: 3px;
	width: 98%;
	margin-left: 1%;
	height: 40px;
	margin-top: 20px;
	font-size: 15px;
	background-color: #38B074;
	color: white;
	border: 1px solid #F0F0F0;
}

.hide {
	display: none;
}

.error {
	color: red;
	font-size: 15px;
}

.company_name {
	margin-left: 10px;
	text-decoration: none;
	color: #6D6D6D;
	font-size: 16px;
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

.jine{
	width: 75%;
	height: 25px;
	padding-top: 0px;
	border: 0px solid red;
	font-size: 16px;
	-webkit-appearance: none;
	float:right;
	text-align:right;
	margin-top: 12px;
	direction:rtl;
	color:#38B074;
	font-weight:bold;
}
</style>
</head>
<body style="background-color:#EEEEEE;">
<div id="wrapper" style="margin-top:-45px;">
<form method="post" role="form" action="wxpaccount.do?action=buyticket&showwxpaytitle=1" id="payform">
	<div id="scroller">
		<ul id="thelist">
			<li style="margin-top:20px;">
						<div class="company_name">
							<span>金额</span>
							<span style="float:right;color:#C3C3C3;">元</span>
							<select id="ticketmoney" name="ticketmoney" class="jine">
							</select>
						</div></li>
			<li style="margin-top:20px;">
				<div class="company_name"><span>数量</span>
							<span style="float:right;color:#C3C3C3;">张</span>
							<select id="ticketnum" name="ticketnum" class="jine">
								<option selected="selected">1</option>
								<option>2</option>
								<option>3</option>
								<option>4</option>
								<option>5</option>
								<option>10</option>
								<option>15</option>
								<option>20</option>
								<option>50</option>
								<option>99</option>
							</select>
				</div>
			</li>
			<input type="text" name="openid" class="hide" value="${openid}">
			<div>
				<div style="text-align:center;margin-top:20px;font-size:16px;color:gray;">未认证9折，认证后7折</div>
						<div style="text-align:center;margin-top:20px;">
							<span style="font-size:25px;font-weight:bold;color:#38B074;">￥</span>
							<span id="moneyafter" style="font-size:60px;font-weight:bold;color:#38B074;">0.0</span>
							<span style="font-size:25px;font-weight:bold;color:gray;text-decoration:line-through;">￥</span>
							<span id="moneybefore" style="font-size:30px;font-weight:bold;color:gray;text-decoration:line-through;">0.0</span>
						</div>
					</div>
			<input type="button" id="paysubmit" class="wx_pay" onclick='check();' value="购买停车券" />
			<div class="wxpay-logo"></div>
			<div style="text-align:center;" id="error" class="error"></div>
		</ul>
	</div>
</form>
</div>

<div id="footer"></div>
<script type="text/javascript">

	function addticket() {
		var index = 1;
		if ("${isauth}" == "1") {
			index = 20;
		}
		for ( var i = 1; i <= index; i++) {
			jQuery("#ticketmoney").append(
					"<option value='"+i+"'>" + i + "</option>");
		}
	}

	addticket();
	
	function getprice() {
		var ticketmoney = document.getElementById("ticketmoney").value;
		var num = document.getElementById("ticketnum").value;
		jQuery.ajax({
					type : "post",
					url : "wxpaccount.do",
					data : {
						'action' : 'ticketprice',
						'openid' : '${openid}',
						'ticketmoney' : ticketmoney,
						'num' : num,
						'r' : Math.random()
					},
					async : false,
					success : function(result) {
						if (result != "-1") {
							var jsonData = eval("(" + result + ")");
							document.getElementById("moneybefore").innerHTML = jsonData.moneybefore;
							document.getElementById("moneyafter").innerHTML = jsonData.moneyafter;
						}
					}
				});
	}
	getprice();
	
	$("#ticketmoney").bind("change", function() {
		getprice();
	});
	$("#ticketnum").bind("change", function() {
		getprice();
	});

	function check() {
		$("#payform")[0].submit();
	}
</script>
</body>
</html>

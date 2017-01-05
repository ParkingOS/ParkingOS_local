<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>停车券</title>
<script type="text/javascript"> 
		var ua = navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) != "micromessenger"){
			window.location.href = "http://s.tingchebao.com/zld/error.html";
		}
	</script>
    <link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
	<link rel="stylesheet" type="text/css" href="css/list.css?v=7" />
	<script src="js/jquery.js"></script>
	<script src="js/wxpublic/jquery.mobile-1.3.2.min.js"></script>
	<script src="js/wxpublic/iscroll.js"></script>
	<script src="js/wxpublic/ticketlist.js?v=2"></script>
	<style type="text/css">
#scroller .li1 {
	padding: 0 10px;
	height: 90px;
	line-height: 0px;
	background-color: white;
	font-size: 14px;
	margin-top: 20px;
	margin-left: 10px;
	margin-right: 10px;
}

.buy {
	border: 1px solid black;
	width: 35px;
	height: 0px;
	line-height: 25px;
	margin-top: -100px;
	margin-right: -31px;
	-webkit-transform: rotate(45deg);
	float: right;
	border-bottom: 0px solid #FCA11D;
	border-left: 0px solid transparent;
	border-right: 0px solid transparent;
	border-width: 0px 25px 25px 25px;
	text-align: center;
	text-shadow: 0 0px 0 #fff;
	color: white;
	font-size: 13px;
}

.buyused {
	border: 1px solid black;
	width: 35px;
	height: 0px;
	line-height: 25px;
	margin-top: -100px;
	margin-right: -31px;
	-webkit-transform: rotate(45deg);
	float: right;
	border-bottom: 0px solid gray;
	border-left: 0px solid transparent;
	border-right: 0px solid transparent;
	border-width: 0px 25px 25px 25px;
	text-align: center;
	text-shadow: 0 0px 0 #fff;
	color: white;
	font-size: 13px;
}

.li2 {
	border-bottom: 1px solid #ccc;
	padding: 0 10px;
	height: 25px;
	line-height: 0px;
	background-color: white;
	font-size: 14px;
	margin-left: 10px;
	margin-right: 10px;
}

.li2:after {
	display: block;
	content: "";
	height: 10px;
	margin-top: 17px;
	background-color: #EBEBEB;
	background-size: 9px 18px;
	background-image: -webkit-linear-gradient(45deg, white 25%, transparent 25%, transparent),
		-webkit-linear-gradient(-45deg, white 25%, transparent 25%, transparent),
		-webkit-linear-gradient(45deg, transparent 75%, white 75%),
		-webkit-linear-gradient(-45deg, transparent 75%, white 75%);
}

#header {
	position: absolute;
	z-index: 2;
	top: 0;
	left: 0;
	width: 100%;
	height: 45px;
	line-height: 45px;
	background-color: #F3F3F3;
	padding: 0;
	font-size: 20px;
	text-align: center;
}

.money {
	margin-top: 20px;
	font-size: 45px;
	line-height: 93px;
	margin-left: 10px;
	font-weight: bold;
	color: black;
}

.moneyused {
	margin-top: 20px;
	font-size: 45px;
	line-height: 93px;
	margin-left: 10px;
	font-weight: bold;
	color: gray;
}

.fuhao {
	font-size: 15px;
}

.exp {
	margin-top: 2px;
	color: #BD3D3C;
	font-size: 12px;
	margin-left: -15px;
}

.used {
	margin-top: 2px;
	color: #25B5A8;
	font-size: 12px;
	margin-left: -15px;
}

.sel_fee {
	font-size: 11px;
	text-align: center;
	padding-top: 1px;
	padding-bottom: 1px;
	outline: medium;
}

.normal {
	padding-left: 5px;
	padding-right: 5px;
	font-weight: 100;
	color: white;
	text-shadow: 0 0px 0 #fff;
	background-color: #38B074;
}

.normalused {
	padding-left: 5px;
	padding-right: 5px;
	font-weight: 100;
	color: white;
	text-shadow: 0 0px 0 #fff;
	background-color: gray;
}

.zhuan {
	padding-left: 5px;
	padding-right: 5px;
	font-weight: 100;
	color: white;
	text-shadow: 0 0px 0 #fff;
	background-color: #EB886B;
}

.zhuanused {
	padding-left: 5px;
	padding-right: 5px;
	font-weight: 100;
	color: white;
	text-shadow: 0 0px 0 #fff;
	background-color: gray;
}

.moneyouter {
	width: 160px;
	height: 70px;
	margin-top: 15px;
}

.a1 {
	text-decoration: none;
	color: #6D6D6D;
	font-size: 16px;
	position: relative;
	top: -36px;
	left: 90px;
}

.a2 {
	text-decoration: none;
	font-size: 16px;
	position: relative;
	top: 10px;
	left: 30px;
}

.ticketname {
	margin-left: 20px;
	margin-top: -10px;
	color: black;
	font-size: 20px;
	font-weight: 900;
}

.ticketnameused {
	margin-left: 20px;
	margin-top: -10px;
	color: gray;
	font-size: 20px;
	font-weight: 900;
}

.ticketinfo {
	margin-left: 22px;
	margin-top: 25px;
	color: #191F1D;
	font-size: 13px;
}

.ticketinfoused {
	margin-left: 22px;
	margin-top: 25px;
	color: gray;
	font-size: 13px;
}

.ticketlimit {
	margin-left: 22px;
	margin-top: 20px;
	font-size: 11px;
}

.limittime {
	float: right;
	margin-right: 20px;
	margin-top: 10px;
	color: #B3B3B3;
	font-size: 12px;
	font-weight: bold;
}

.line {
	border: 1px solid #38B074;
}

.lineuesd {
	border: 1px solid gray;
}

.useinfo {
	margin-top: 0px;
	color: #B3B3B3;
	font-size: 12px;
	margin-left: -15px;
	font-weight: bold;
}

.useinfoused {
	margin-top: 0px;
	color: #25B5A8;
	font-size: 12px;
	margin-left: -15px;
	font-weight: bold;
}

.useinfoexp {
	margin-top: 0px;
	color: #EB886B;
	font-size: 12px;
	margin-left: -15px;
	font-weight: bold;
}

.bottom {
	background-color: #EBEBEB;
	z-index: 999;
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	height: 70px;
	_position: absolute;
	_top: expression(documentElement.scrollTop +   
		 documentElement.clientHeight-this.offsetHeight);
	overflow: visible;
}

.buyticket {
	border-radius: 3px;
	width: 98%;
	margin-left: 1%;
	height: 40px;
	font-size: 15px;
	background-color: #38B074;
	color: white;
	border: 0px solid #F0F0F0;
	text-align: center;
	line-height: 40px;
	text-shadow: 0 0px 0 #fff;
	margin-top: 5px;
}

.tishi {
	margin-left: 1%;
	font-size: 12px;
	color: #38B074;
	text-shadow: 0 0px 0 #fff;
	font-weight: 900;
	margin-top: 5px;
}

.middle {
	margin-top: 55%;
	color: gray;
	position: relative;
	z-index: 10000;
	text-align: center;
	text-shadow: 0 0px 0 #fff;
	font-size: 14px;
}

.hide{
	display:none;
}
</style>
</head>
<body>
	<div id="wrapper" style="margin-top:-45px;">
		<div id="scroller">
			<div id="pullDown" class="idle">
				<span class="pullDownIcon"></span>
				<span class="pullDownLabel">下拉刷新...</span>
			</div>

			<ul id="thelist">
			<%--<li class="li1"><div class="moneyouter"><span class="moneyused">3<span class="fuhao">元</span></span></div><a class="a1" href="#"><div class="ticketname">普通券</div><div class="ticketinfo">满4元可用</div><div class="ticketlimit"><span class="sel_fee normal">所有停车宝车场可用</span></div></a><div class="buy">购买</div></li>
			<li class="li2"><div class="line"></div><a class="a2" href="#"><div class="useinfo">还剩15天可用</div><div class="limittime">有效期至 2015-09-13</div></a></li>
			
			<li class="li1"><div class="moneyouter"><span class="moneyused">3<span class="fuhao">元</span></span></div><a class="a1" href="#"><div class="ticketname">普通券</div><div class="ticketinfo">满4元可用</div><div class="ticketlimit"><span class="sel_fee normal">所有停车宝车场可用</span></div></a><div class="buy">购买</div></li>
			<li class="li2"><div class="line"></div><a class="a2" href="#"><div class="useinfo">还剩15天可用</div><div class="limittime">有效期至 2015-09-13</div></a></li>
			
			--%></ul>
			<div id="pullUp" class="idle">
				<span class="pullUpIcon"></span>
				<span class="pullUpLabel">上拉加载更多...</span>
			</div>
		</div>
	</div>
	<div class="middle hide">您还没有停车券<br><a style="color:red;" href="http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=208427587&idx=1&sn=6cec3794e585e4d31b5079f919b01614&scene=23&srcid=0906dUsJBRSU82cPCL6ou1kk#rd" onclick="open();">点击这里</a>查看如何获得</div>
	<div class="bottom">
		<div class="tishi">认证用户7折优惠，未认证9折</div>
		<div class="buyticket" onclick="tobuy();">购买停车券</div>
	</div>
	<div id="footer"></div>
	<input id="mobile" type="text" style="display:none;" value="${mobile}"/>
	<input id="ticket_state" type="text" style="display:none;" value="${type}"/>
</body>
<script type="text/javascript">
	function tobuy(){
		window.location.href = "http://${domain}/zld/wxpaccount.do?action=tobuyticket&openid=${openid}";
	}
	
</script>
</html>

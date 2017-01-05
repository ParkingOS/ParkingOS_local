<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>付停车费</title>
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
#scroller .li1 {
    padding:0 10px;
    height:25px;
    line-height:25px;
    background-color:white;
    font-size:14px;
    margin-top:0px;
    font-weight:bold;
}

.li2 {
    padding:0 0px;
    height:170px;
    border-bottom:1px solid #EBEBEB;
    border-top:1px solid #EBEBEB;
    background-color:white;
    font-size:14px;
    margin-top:0px;
    font-weight:bold;
}

a{
	text-decoration:none;
	color:#AEAEAE;
	font-size:14px;
	
	position: relative;
	top:0px;
	left:5px;
}

.img1{
	width:20px;
	height:20px;
	margin-top:15px;
}
.img2{
	width:80px;
	height:80px;
	margin-top:5px;
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
	margin-top: 5%;
}

.discount {
	text-align: center;
	padding-top: 2px;
	padding-bottom: 1px;
	border-radius: 3px;
	background-color: #DD4E44;
	outline: medium;
	margin-left: 5px;
	color: white;
	padding-left: 2px;
	padding-right: 2px;
	font-size: 11px;
}

.ticket {
	text-align: center;
	padding-top: 2px;
	padding-bottom: 1px;
	border-radius: 3px;
	background-color: #38B074;
	outline: medium;
	margin-left: 5px;
	color: white;
	padding-left: 3px;
	padding-right: 3px;
	font-size: 11px;
}

.balance {
	text-align: center;
	padding-top: 2px;
	padding-bottom: 1px;
	border-radius: 3px;
	background-color: #CD9A2D;
	outline: medium;
	margin-left: 5px;
	color: white;
	padding-left: 2px;
	padding-right: 2px;
	font-size: 11px;
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

.info-list:after {
	display: block;
	content: "";
	height: 10px;
	margin-top: 5px;
	background-color: #EBEBEB;
	background-size: 8px 20px;
	background-image: -webkit-linear-gradient(45deg, #FFFFFF 25%, transparent 25%, transparent
		), -webkit-linear-gradient(-45deg, #FFFFFF 25%, transparent 25%,
		transparent),
		-webkit-linear-gradient(45deg, transparent 75%, #FFFFFF 75%),
		-webkit-linear-gradient(-45deg, transparent 75%, #FFFFFF 75%);
}

.top:after {
	display: block;
	content: "";
	height: 10px;
	margin-top: 65px;
	background-color: #EBEBEB;
	background-size: 8px 20px;
	background-image: -webkit-linear-gradient(45deg, #FFFFFF 25%, transparent 25%, transparent
		), -webkit-linear-gradient(-45deg, #FFFFFF 25%, transparent 25%,
		transparent),
		-webkit-linear-gradient(45deg, transparent 75%, #FFFFFF 75%),
		-webkit-linear-gradient(-45deg, transparent 75%, #FFFFFF 75%);
}

.arrow {
	background-image: url(images/wxpublic/arrow.png);
	background-size: 19px 39px;
	background-repeat: no-repeat;
	background-position: right center;
}

.r {
	float: right;
	margin-right: 20px;
}

.othermoney {
	text-align: center;
	margin-top: 5px;
	font-size: 20px;
	text-decoration: line-through;
	font-weight: bold;
	color: gray
}

.needpay {
	text-align: center;
	margin-top: 55px;
	color: #38B074;
	font-size: 50px;
	font-weight: bold;
}

.info-list {
	margin-top: 0px;
	background-color: white;
	padding-top: 15px;
}
</style>

<style type="text/css">
#BgDiv1{background-color:#000; position:absolute; z-index:9999;  display:none;left:0px; top:0px; width:100%; height:100%;opacity: 0.6; filter: alpha(opacity=60);}
.DialogDiv{position:absolute;z-index:99999;}/*配送公告*/
.U-user-login-btn{ display:block; border:none; background:url(images/wxpublic/bg_mb_btn1_1.png) repeat-x; font-size:1em; color:#efefef; line-height:49px; cursor:pointer; height:53px; font-weight:bold;
border-radius:3px;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
 width:100%; box-shadow: 0 1px 4px #cbcacf, 0 0 40px #cbcacf ;}
 .U-user-login-btn:hover, .U-user-login-btn:active{ display:block; border:none; background:url(images/wxpublic/bg_mb_btn1_1_h.png) repeat-x; font-size:1em; color:#efefef; line-height:49px; cursor:pointer; height:53px; font-weight:bold;
border-radius:3px;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
 width:100%; box-shadow: 0 1px 4px #cbcacf, 0 0 40px #cbcacf ;}
.U-user-login-btn2{ display:block; border:none;background:url(images/wxpublic/bg_mb_btn1_1_h.png) repeat-x;   font-size:1em; color:#efefef; line-height:49px; cursor:pointer; font-weight:bold;
border-radius:3px;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
 width:100%; box-shadow: 0 1px 4px #cbcacf, 0 0 40px #cbcacf ;height:53px;}
.U-guodu-box { padding:10px 15px;  background:#3c3c3f; filter:alpha(opacity=90); -moz-opacity:0.9; -khtml-opacity: 0.9; opacity: 0.9;  min-heigh:200px; border-radius:10px;}
.U-guodu-box div{ color:#fff; line-height:20px; font-size:12px; margin:0px auto; height:100%; padding-top:10%; padding-bottom:10%;}

</style>
</head>
<body style="background-color:#EEEEEE;">
<div id="BgDiv1"></div>
<div id="wrapper" style="margin-top:-47px;">
<form method="post" role="form" action="wxpublic.do?action=balancepayinfo" id="payform">
	<div id="scroller">
		<ul id="thelist">
			<li class="li2">
				<div class="needpay"><span style="font-size:20px;">￥</span>${needpay}</div>
				<div class="othermoney hide"><span style="font-size:15px;">￥</span>${total}</div>
			</li>
			<div class="info-list">
			<li class="li1 tcbticket arrow"><a href="${chooseurl}"><div><span>停车券</span><span class="ticket">${ticketdescp}</span><span class="r discountshow">抵扣${discount}元</span></div></a></li>
			<li class="li1 tcbbalance hide"><a href="#"><div><span>余额支付</span><span class="r">${balance_pay}元</span></div></a></li>
			<li class="li1 online hide"><a href="#"><div><span>在线支付</span><span class="r">${wx_pay}元</span></div></a></li>
			</div>
			<ul class="hide">
					<input type="text" name="money" value="${total}">
					<input type="text" name="uid" value="${uid}">
					<input type="text" name="ticketid" value="${ticketid}">
					<input type="text" name="mobile" value="${mobile}">
					<input type="text" name="openid" value="${openid}">
			</ul>
			<input type="button" class="wx_pay" onclick='wxepay();' value="支付" />
			<div class="wxpay-logo"></div>
			<div style="text-align:center;" id="error" class="error"></div>
		</ul>
	</div>
</form>
</div>

<div class="DialogDiv" style="display:none;width:120px;">
		<div class="U-guodu-box" >
			<div>
				<table width="100%" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td style="padding-top: 5px;" align="center"><img id="imginfo" src="images/wxpublic/loading.gif">
						</td>
					</tr>
					<tr>
						<td style="padding-top: 10px;" id="showinfo" valign="middle" align="center">支付中...</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
<script type="text/javascript">
	var wx_pay = "${wx_pay}";//0：没有微信支付，1有微信支付
	var discount = "${discount}";
	var balance_pay = "${balance_pay}";
	var bind_flag = "${bind_flag}";
	discount = parseFloat(discount);
	balance_pay = parseFloat(balance_pay);
	wx_pay = parseFloat(wx_pay);
	if(discount>0){
		$(".othermoney").css("display", "block");
	}
	if(balance_pay > 0){
		$(".tcbbalance").css("display", "block");
	}
	if(wx_pay > 0){
		$(".online").css("display", "block");
	}
	if("${ticketdescp}" == "无可用停车券" || "${ticketdescp}" == "不使用停车券" || "${ticketdescp}" == "超过每日使用次数限制"){
		$(".discountshow").css("display", "none");
	}
	if("${ticketdescp}" == "无可用停车券" || "${ticketdescp}" == "首单5折券" || "${ticketdescp}" == "超过每日使用次数限制"){
			$(".tcbticket").removeClass("arrow");
		}
	function wxepay(){
		if(wx_pay > 0){//调起微信支付
			callpay();
		}else if(bind_flag == "1"){//余额支付
			tcbpay();
		}
	}
</script>
<script language="javascript" type="text/javascript">
	function shade(){
	 	$("#BgDiv1").css({ display: "none", height: $(document).height() });
		var yscroll = document.documentElement.scrollTop;
		var screenx=$(window).width();
		var screeny=$(window).height();
	  	$(".DialogDiv").css("display", "none");
		 $(".DialogDiv").css("top",yscroll+"px");
		 var DialogDiv_width=$(".DialogDiv").width();
		 var DialogDiv_height=$(".DialogDiv").height();
		  $(".DialogDiv").css("left",(screenx/2-DialogDiv_width/2)+"px");
		 $(".DialogDiv").css("top","150px");
		 $("body").css("overflow","hidden");
	 }
	 shade();//进入页面就加载
	 
	 function timer(){
	 	$("#BgDiv1").css("display","block");
		$(".DialogDiv").css("display", "block");
	 	setTimeout("epay()",2000);//两秒后执行
	 }
 </script>
<script type="text/javascript">
	function callpay(){//调起微信支付
		 WeixinJSBridge.invoke('getBrandWCPayRequest',{  
             "appId" : '${appid}',                  //公众号名称，由商户传入  
             "timeStamp":'${timestamp}',          //时间戳，自 1970 年以来的秒数  
             "nonceStr" : '${nonceStr}',         //随机串  
             "package" : '${packagevalue}',      //<span style="font-family:微软雅黑;">商品包信息</span>  
             "signType" : '${signType}',        //微信签名方式:  
             "paySign" : '${paySign}'           //微信签名  
             },function(res){
            	 if(res.err_msg == "get_brand_wcpay_request:ok"){
            		 window.location.href = "wxpublic.do?action=balancepayinfo&openid=${openid}&uid=${uid}&money=${total}&ticketid=${ticketid}&notice_type=0";
            	 }
         });
	}
	
	var index = 0;//防止订单重复提交
	function epay(){//不调起微信支付
		jQuery.ajax({
			type : "post",
			url : "carowner.do",
			data : {
				'mobile' : '${mobile}',
				'action' : 'epay',
				'ticketid' : '${ticketid}',
				'uid' : '${uid}',
				'total' : '${total}',
				'r' : Math.random()
			},
			async : false,
			success : function(result) {
				if(result == "1"){
					$("#payform")[0].submit();
				}else{
					index = 0;
					$("#BgDiv1").css("display", "none");
					$(".DialogDiv").css("display", "none");
					document.getElementById("error").innerHTML = "支付失败";
				}
			}
		});
	}
	
	function tcbpay(){
		index++;//防止重复提交
		if(index <= 1){
			timer();
		}
	}
</script>
</body>
</html>

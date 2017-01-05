<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>订单详情</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=12" />
<script src="js/jquery.js"></script>
<style type="text/css">
* {
	margin: 0;
	padding: 0;
}

a {
	text-decoration: none;
}

img {
	max-width: 100%;
	height: auto;
}

.weixin-tip {
	display:none;
	position: fixed;
	left: 0;
	top: 0;
	bottom: 0;
	background: rgba(0, 0, 0, 0.8);
	filter: alpha(opacity = 80);
	height: 100%;
	width: 100%;
	z-index: 100000;
}

.weixin-tip p {
	text-align: center;
	margin-top: 10%;
	padding: 0 5%;
}

.bonus {
	width: 60px;
	height: 70px;
}
</style>
<style type="text/css">
#scroller .li1 {
    padding:0 10px;
    height:50px;
    line-height:50px;
    background-color:#FFFFFF;
    font-size:15px;
    color:#828282;
    font-weight:bold;
}

.li2 {
    padding:0 10px;
    height:80px;
    line-height:0px;
    background-color:#FFFFFF;
    font-size:15px;
    font-weight:bold;
}

.li3 {
    padding:0 10px;
    height:25px;
    line-height:25px;
    background-color:white;
    font-size:15px;
    margin-top:0px;
    color:#828282;
    font-weight:bold;
}
.img{
	width:60px;
	height:60px;
	margin-top:10px;
}
</style>

<style type="text/css">
* {
	margin: 0px;
	padding: 0px;
}

body {
	font-size: 12px;
	font: Arial, Helvetica, sans-serif;
	margin: 25PX 0PX;
	background: #eee;
}

.wx_name {
	margin-left: 70px;
	color: #101010;
	font-size: 15px;
	margin-top: -30px;
}

#info-list {
	margin-top: 1px;
	padding-top: 15px;
	padding-bottom: 15px;
	background-color: white;
}

.right1{
	float:right;
	margin-right:5px;
}

.red{
	color:red;
}

.green{
	color:green;
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

.hide{
	display:none;
}
</style>
<style>
/* 计费方式弹出层 */
.billing-mask .content {
	background-color: #E7E7E7;
	-webkit-border-radius: 3px;
	border-radius: 8px;
	margin: 20% 30px 0;
	padding-bottom: 14px;
}

.billing-mask .billing-top {
	padding-top: 10px;
	padding-left: 10px;
	background-color: #40A18D;
	border-top-left-radius: 6px;
	border-top-right-radius: 6px;
}

.billing-mask .billing-title {
	padding: 35px 0;
	text-align: center;
	font-size: 18px;
	color: #333333;
	font-weight: 600;
	background-color: #40A18D;
}

.billing-mask .billing-middle {
	padding: 40px 0;
	text-align: center;
	font-size: 18px;
	color: #333333;
	font-weight: 600;
	background-color: #FFFFFF;
}

.billing-mask .billing-bottom {
	padding: 40px 0;
	text-align: center;
	font-size: 18px;
	color: #333333;
	font-weight: 600;
	background-color: #FFFFFF;
}

.billing-mask .billing-list {
	padding: 0 15px;
}

.billing-mask .billing-list .list {
	line-height: 20px;
}

.billing-mask .billing-list .totle-list {
	padding-top: 8px;
	position: relative
}

.billing-mask .billing-list .totle-list:before {
	content: "";
	width: 100%;
	display: block;
	border-top: 1px solid #CECECE;
	position: absolute;
	left: 0;
	top: 4px;
}

.billing-mask .billing-list .green-font {
	color: #22AC38;
}

.billing-mask .rules {
	padding: 0 15px;
	background-color: white;
}

.billing-mask .rules .rules-title {
	font-size: 14px;
	color: #666666;
	padding: 45px 0 12px;
	font-weight: 600;
	text-align: center;
	color: red;
}

.billing-mask .rules .rules-content {
	font-size: 14px;
	color: #666666;
	padding: 0px 0 45px;
	font-weight: 600;
	text-align: center;
	color: gray;
}

.billing-mask .rules .rules-main {
	font-size: 10px;
	color: #999999;
	line-height: 35px;
}

.billing-mask .close-btn {
	margin: 0px 0px -15px;
}

.mask {
	position: fixed;
	background-color: rgba(0, 0, 0, 0.75);
	left: 0;
	top: 0;
	width: 100%;
	height: 100%;
	z-index: 99999;
}

.btn {
	display: block;
	height: 42px;
	line-height: 42px;
	text-align: center;
	border-bottom-left-radius: 8px;
	border-bottom-right-radius: 8px;
	border-top-left-radius: 0px;
	border-top-right-radius: 0px;
	font-size: 18px;
	-webkit-box-sizing: border-box;
	box-sizing: border-box;
	-webkit-tap-highlight-color: rgba(0, 0, 0, 0);
}

.img1 {
	width: 60px;
	height: 60px;
	margin-top:-10px;
}

.img2 {
	width: 15px;
	height: 15px;
}

.bottom {
	background-color: #EBEBEB;
	z-index: 999;
	position: fixed;
	bottom: 0;
	left: 0;
	width: 100%;
	height: 45px;
	_position: absolute;
	_top: expression(documentElement.scrollTop +   
		 documentElement.clientHeight-this.offsetHeight);
	overflow: visible;
}

.reward {
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
}

.rewarded {
	border-radius: 3px;
	width: 98%;
	margin-left: 1%;
	height: 40px;
	font-size: 15px;
	background-color: gray;
	color: white;
	border: 0px solid #F0F0F0;
	text-align: center;
	line-height: 40px;
	text-shadow: 0 0px 0 #fff;
}
</style>
</head>
<body style="background-color:#EEEEEE;">
<div class="weixin-tip">
		<p>
			<img id="android" src="images/wxpublic/mask_android3.png" />
			<img id="ios" style="height:420px;" src="images/wxpublic/mask_ios3.png" />
		</p>
	</div>
<!-- 计费方式弹出层[[ -->
	<div class="mask billing-mask hide">
		<div class="content">
			<div class="billing-top">
				<img class="img2" src="images/wxpublic/close.png" onclick="hidebonus()" />
			</div>
			<div class="billing-title">
				<img class="img1" src="images/wxpublic/ticket_success.png" />
			</div>
			<dl class="rules">
				<dt class="rules-title">恭喜您获得${bonus_bnum}个共${bonus_money}元礼包</dt>
				<dt class="rules-content">分享后领取呦~~~</dt>
			</dl>
			<a href="#" onClick="share()" class="btn btn-white close-btn">分&nbsp;&nbsp;享</a>
		</div>
		
	</div>
	<!-- 计费方式弹出层]] -->
<div id="wrapper" style="margin-top:-25px;">
	<div id="scroller">
		<ul id="thelist">
			<li class="li2"><div onclick="previewpic();"><img class="img" src="${picUrls}" /></div><div class="wx_name">${cname}</div></li>
			<li class="li1" style="margin-top:20px;"><div><span>停车费</span><span class="right1">${total}元</span></div></li>
			<div id="info-list">
			</div>
			<div class="wxpay-logo"></div>
		</ul>
	</div>
</div>
<div class="bottom hide"></div>
</body>
<script type="text/javascript">
	$("#info-list").append('<li class="li3"><div><span>订单编号</span><span class="right1">${orderid}</span></div></li>');
	if("${state}" == "0"){
		$("#info-list").append('<li class="li3"><div><span>订单状态</span><span class="right1 red">未结算</span></div></li>');
	}else{
		$("#info-list").append('<li class="li3"><div><span>订单状态</span><span class="right1 green">已结算</span></div></li>');
	}
	if("${intype}" == "非计时"){
		$("#info-list").append('<li class="li3"><div><span>结算时间</span><span class="right1">${btime}</span></div></li>');
	}else{
		$("#info-list").append('<li class="li3"><div><span>入场时间</span><span class="right1">${btime}</span></div></li>');
		if("${state}" == "0"){
			$("#info-list").append('<li class="li3"><div><span>停车时长</span><span class="right1">${parktime}</span></div></li>');
		}else{
			$("#info-list").append('<li class="li3"><div><span>出场时间</span><span class="right1">${etime}</span></div></li>');
		}
	}
	$("#info-list").append('<li class="li3"><div><span>计时方式</span><span class="right1">${intype}</span></div></li>');
	if("${state}" == "1"){
		$("#info-list").append('<li class="li3"><div><span>结算方式</span><span class="right1">${paytype}</span></div></li>');
	}
	if("${carnumber}" != ""){
		$("#info-list").append('<li class="li3"><div><span>车牌号码</span><span class="right1">${carnumber}</span></div></li>');
	}
	$("#info-list").append('<li class="li3"><div><span>收费人员</span><span class="right1">${nickname}(${uid})</span></div></li>');
	
	if("${state}" == "1"){
		if("${reward_flag}" != ""){
			$(".bottom").removeClass("hide");
			
			if("${reward_flag}" == "0"){
				$(".bottom").append('<div class="reward" onclick="toreward();">打赏</div>');
			}else if("${reward_flag}" == "1"){
				$(".bottom").append('<div class="rewarded">该订单已打赏</div>');
			}else if("${reward_flag}" == "2"){
				$(".bottom").append('<div class="rewarded">今日已打赏过该收费员</div>');
			}
		}
	}
	
	function toreward(){
		window.open("${url}");
	}
</script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
<script type="text/javascript">
wx.config({
    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
    appId: '${appid}', // 必填，公众号的唯一标识
    timestamp:'${timestamp}', // 必填，生成签名的时间戳
    nonceStr: '${nonceStr}', // 必填，生成签名的随机串
    signature: '${signature}',// 必填，签名，见附录1
    jsApiList: [
    	'checkJsApi',
        'onMenuShareTimeline',
        'onMenuShareAppMessage',
        'onMenuShareQQ',
        'onMenuShareWeibo',
        'hideMenuItems',
        'showMenuItems',
        'hideAllNonBaseMenuItem',
        'showAllNonBaseMenuItem',
        'translateVoice',
        'startRecord',
        'stopRecord',
        'onRecordEnd',
        'playVoice',
        'pauseVoice',
        'stopVoice',
        'uploadVoice',
        'downloadVoice',
        'chooseImage',
        'previewImage',
        'uploadImage',
        'downloadImage',
        'getNetworkType',
        'openLocation',
        'getLocation',
        'hideOptionMenu',
        'showOptionMenu',
        'closeWindow',
        'scanQRCode',
        'chooseWXPay',
        'openProductSpecificView',
        'addCard',
        'chooseCard',
        'openCard'
        ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
});
</script>
<script type="text/javascript">
	var bonus_type = "${bonus_type}";
	var first_flag = "${first_flag}";
	var link = "http://${domain}/zld/carowner.do?action=getobonus&id=${bonusid}";
	var imgUrl = "http://${domain}/zld/images/bonus/order_bonu.png";
	var desc = "${desc}";
	var title = "${title}";
	if(first_flag == "1"){
		title = "停车宝新人大礼包";
		imgUrl = "http://${domain}/zld/images/wxpublic/first.png";
		desc = "我是停车宝新用户，获得${bonus_money}元新人礼包，分享给${bonus_bnum}个小伙伴，手快有，手慢无";
	}
	if(bonus_type == "1"){
		link = "http://${domain}/zld/carinter.do?action=getwxbonus&id=${bonusid}";
		imgUrl = "http://${domain}/zld/images/bonus/wx_order_bonu.png";
		desc = "微信支付${bonus_money}折礼包";
	}
	wx.ready(function() {
		function sharetofriend(){//发送给朋友
			wx.onMenuShareAppMessage({
			    title: title, // 分享标题
			    desc: desc, // 分享描述
			    link: link, // 分享链接
			    imgUrl: imgUrl, // 分享图标
			    type: '', // 分享类型,music、video或link，不填默认为link
			    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			    success: function () { 
			        // 用户确认分享后执行的回调函数
			    	wx.closeWindow();
			    },
			    cancel: function () { 
			        // 用户取消分享后执行的回调函数
			    	$(".weixin-tip").hide();
			    }
			});
		}
		
		function sharetocircle(){//发送到朋友圈
			wx.onMenuShareTimeline({
			    title: title, // 分享标题
			    link: link, // 分享链接
			    imgUrl: imgUrl, // 分享图标
			    success: function () { 
			        // 用户确认分享后执行的回调函数
			    	wx.closeWindow();
			    },
			    cancel: function () { 
			        // 用户取消分享后执行的回调函数
			    	$(".weixin-tip").hide();
			    }
			});
		}
		//判断是否可以发送礼包给朋友
		var bonusid = '${bonusid}';//礼包id
		if(bonusid != "-1"){
			$(".mask").removeClass("hide");
			if(bonus_type == "1"){
				$(".rules-title")[0].innerHTML = "恭喜您获得${bonus_bnum}张${bonus_money}折券";
			}else{
				if(first_flag == "1"){
					$(".rules-title")[0].innerHTML = "恭喜您获得${bonus_bnum}个共${bonus_money}元新人礼包";
				}
			}
			sharetofriend();
			sharetocircle();
		}
	});
	
	function previewpic(){
		jQuery.ajax({
			type : "post",
			url : "wxpublic.do",
			data : {
				'action' : 'getparkpic',
				'comid' : '${comid}'
			},
			async : false,
			success : function(result) {
				if(result != ""){
					wx.previewImage({
				      current: result,
				      urls: [
							result
				      ]
					});
				}
			}
		});
	}
</script>
<script type="text/javascript">
	$(document).ready(function() {
		var userAgent = navigator.userAgent.toLowerCase();
		var winHeight = $(window).height();
		if(userAgent.match(/iphone os/i) == "iphone os"){
			document.getElementById("android").style.display = "none";
			$("#ios").css("height",winHeight*0.85);
		}else{
			document.getElementById("ios").style.display = "none";
			$("#android").css("height",winHeight*0.85);
		}
	});
</script>
<script type="text/javascript">
	function share(){
		var winHeight = $(window).height();
		function is_weixin() {
		    var ua = navigator.userAgent.toLowerCase();
		    if (ua.match(/MicroMessenger/i) == "micromessenger") {
		        return true;
		    } else {
		        return false;
		    }
		}
		var isWeixin = is_weixin();
		if(isWeixin){
			$(".weixin-tip").css("height",winHeight);
            $(".weixin-tip").show();
		}
	}
	
	function hidebonus(){
		$(".mask").addClass("hide");
	}
</script>

</html>

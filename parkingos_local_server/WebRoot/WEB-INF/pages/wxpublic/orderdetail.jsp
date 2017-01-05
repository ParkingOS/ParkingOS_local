<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>支付成功</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" href="css/prepay.css?v=2">
<style type="text/css">
	html,body {
		    padding: 0 !important;
			margin: 0 !important;
		    background-color:#ffffff;
		    width:100%;
		    height:100%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-color:#F5F5F5;
		    font-size:14px;
		}
	*{margin:0; padding:0;}
	a{text-decoration: none;}
	img{max-width: 100%; height: auto;}
	.weixin-tip{display: none; position: fixed; left:0; top:0; bottom:0; background: rgba(0,0,0,0.8); filter:alpha(opacity=80);  height: 100%; width: 100%; z-index: 100;}
	.weixin-tip p{text-align: center; margin-top: 10%; padding:0 5%;}
</style>
<style type="text/css">
.error {text-align:center;}
.success{
	width:100px;
	height:100px;
}
.bonus{
	width:60px;
	height:70px;
}

.share{
	border-radius:5px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:3%;
	font-size:15px;
	background-color:#04BE02;
	color:white;
}
</style>
<script src="js/jquery.js"></script>
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
	$(document).ready(function() {
		var userAgent = navigator.userAgent.toLowerCase();
		if(userAgent.match(/iphone os/i) == "iphone os"){
			document.getElementById("android").style.display = "none";
		}else{
			document.getElementById("ios").style.display = "none";
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
</script>
</head>
<body style="background-color:#F0F0F0;">
<div class="weixin-tip">
		<p>
			<img id="android" style="height:450px;" src="images/wxpublic/mask_android.png" />
			<img id="ios" style="height:450px;" src="images/wxpublic/mask_ios.png" />
		</p>
	</div>
<div class="error">
<div id='d3' style=''>
		<div style='text-align:center;font-size:20px;margin-top:20px;'><b>${comname }</b></div>
		<div style="margin-top:10px;">
		<div style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;预付车费</div><div style='width:70;float:right;'><b>${total}元</b>&nbsp;&nbsp;&nbsp;</div></div>
		<div style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;实停车费</div><div style='width:70;float:right;'><b>${prepay}元</b>&nbsp;&nbsp;&nbsp;</div></div>
		<div class="back hide" style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;${back_dp}</div><div style='width:160;float:right;'><b>${addmoney}元</b>&nbsp;&nbsp;&nbsp;</div></div>
		<div style='height:10px;line-height:20px;background-color:#F0F0F0;'>&nbsp;</div>
		
		<div style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;订单编号</div><div style='width:160;float:right;'><b>${orderid}</b>&nbsp;&nbsp;&nbsp;</div></div>
		<div style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;订单状态</div><div style='width:160;float:right;'><b>已结算</b>&nbsp;&nbsp;&nbsp;</div></div>
		<div style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;开始时间</div><div style='width:160;float:right;'><b>${btime}</b>&nbsp;&nbsp;&nbsp;</div></div>
		<div style='height:30px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div><div style='width:70px;float:left;color:#9C9A9B'>&nbsp;&nbsp;&nbsp;出场时间</div><div style='width:160;float:right;'><b>${etime}</b>&nbsp;&nbsp;&nbsp;</div></div>
		</div>
		<div style="text-align:center;margin-top:2%;" class="wxticket3 hide">
			<img style="height:90px;width:150px;" src="images/wxpublic/wxticket.png">
			<div style="margin-top:2%;color:#04BE02;">恭喜获得5张3折券，分享后领取</div>
		</div>
		<div style="text-align:center;margin-top:2%;" class="wxticket5 hide">
			<img style="height:90px;width:150px;" src="images/wxpublic/wxticket5.png">
			<div style="margin-top:2%;color:#04BE02;">恭喜获得5张5折券，分享后领取</div>
		</div>
		<div style="text-align:center;margin-top:2%;" class="tcbticket hide">
			<img style="height:90px;width:150px;" src="images/bonus/quan.png">
			<div style="margin-top:2%;color:#04BE02;">恭喜获得停车券，分享后领取</div>
		</div>
		<div style="text-align:center;margin-top:2%;" class="first_ticket hide">
			<img style="height:100px;width:200px;" src="images/wxpublic/ticket36.png">
			<div style="margin-top:2%;color:#F8B974;">恭喜获得停车宝${bonus_money}元新人礼包，分享后领取</div>
		</div>
		<div class="hide sharebutton">
			<input type="button" class="share" onClick="share()" value="去分享">
		</div>
	</div>
</div>
<script type="text/javascript">
	var addmoney = "${addmoney}";
	addmoney = parseFloat(addmoney);
	if(addmoney > 0){
		$(".back").removeClass("hide");
	}
</script>
<script type="text/javascript">
	var bonus_type = "${bonus_type}";
	var bonusid = "${bonusid}";//礼包id
	var bonus_money = "${bonus_money}";
	var first_flag = "${first_flag}";
	var bonus_bnum = "${bonus_bnum}";
	var link = "http://s.tingchebao.com/zld/carowner.do?action=getobonus&id=${bonusid}";
	var imgUrl = "http://s.tingchebao.com/zld/images/bonus/order_bonu.png";
	var desc = "${desc}";
	var title = "${title}";
	if(first_flag == "1"){
		title = "停车宝新人大礼包";
		imgUrl = "http://s.tingchebao.com/zld/images/wxpublic/first.png";
		desc = "我是停车宝新用户，获得"+bonus_money+"元新人礼包，分享给"+bonus_bnum+"个小伙伴，手快有，手慢无";
	}
	if(bonus_type == "1"){
		link = "http://s.tingchebao.com/zld/carinter.do?action=getwxbonus&id=${bonusid}";
		imgUrl = "http://s.tingchebao.com/zld/images/bonus/wx_order_bonu.png";
		desc = "微信支付"+bonus_money+"折礼包";
	}
	wx.ready(function() {
		function sharetofriend(){
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
		
		if(bonusid != "-1"){
			if(bonus_type == "1"){
				if(bonus_money == "5"){
					$(".wxticket5").removeClass("hide");
				}else if(bonus_money == "3"){
					$(".wxticket3").removeClass("hide");
				}
			}else{
				if(first_flag == "1"){
					$(".first_ticket").removeClass("hide");
				}else{
					$(".tcbticket").removeClass("hide");
				}
			}
			
			$(".sharebutton").removeClass("hide");
			sharetofriend();
			sharetocircle();
		}
	});
</script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<head>
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
		    background-color:#4DD2A7;
		    background-image: url(images/game/game_b.jpg);
		    background-repeat:no-repeat;
		}
		a{text-decoration: none;}
	img{max-width: 100%; height: auto;}
	.weixin-tip{display: none; 
		position: fixed; 
		left:0; top:0; bottom:0; 
		background: rgba(0,0,0,0.8);
		filter:alpha(opacity=80); 
		height: 100%; 
		width: 100%; 
		background-repeat:no-repeat;
		z-index: 100;}
	.weixin-tip p{text-align: center; margin-top: 10%; padding:0 5%;}
		</style>
</head>
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
	wx.ready(function() {
		function sharetofriend(){
			wx.onMenuShareAppMessage({
			    title: '停车宝日破万单，猛送红包快来领', // 分享标题
			    desc: '微信支付3折礼包', // 分享描述
			    link: 'http://yxiudongyeahnet.vicp.cc/zld/carinter.do?action=getwxbonus&id=', // 分享链接
			    imgUrl: 'http://yxiudongyeahnet.vicp.cc/zld/images/bonus/wx_order_bonu.png', // 分享图标
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
			    title: '停车宝日破万单，猛送红包快来领', // 分享标题
			    link: 'http://yxiudongyeahnet.vicp.cc/zld/carinter.do?action=getwxbonus&id=', // 分享链接
			    imgUrl: 'http://yxiudongyeahnet.vicp.cc/zld/images/bonus/wx_order_bonu.png', // 分享图标
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
		//document.getElementById("shareimage").style.display = "";//显示礼包图片
		sharetofriend();
		try{sharetocircle();}catch(e){};
	});
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
<body id='body'>
	<div class="weixin-tip">
		<p>
			<img id="android" style="height:500px;" src="images/wxpublic/mask_android.png" />
			<img id="ios" style="height:500px;" src="images/wxpublic/mask_ios.png" />
		</p>
	</div>
	<div id="sale" style="position:absolute;">
		<img id='saleimg' src='images/game/win.png'/>
	</div>
	<div id="words1" style="position:absolute;color:#FFFFFF" >
		恭喜
	</div>
	<div id="words2" style="position:absolute;color:#FFFFFF" >
		停车挑战满分，停车券番5倍
	</div>
	<div id="words3" style="position:absolute;color:#EDC500" >
		获得100个好友免费游戏特权
	</div>
	<div id="star" style="position:absolute;" >
		<img id='starimg' src='images/game/star.png' />
	</div>
	<div id="words4" style="position:absolute;color:#FFFFFF;text-align:center;width:100%" >
		在${alluser}名车主中，本周排名第${sort}
	</div>
	<div id="share" style="position:absolute;" onclick='share();'>
		<img id='shareimg' src='images/game/sharfriend.png' />
	</div>
</body>
<script>
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;

document.getElementById("sale").style.left=parseInt(w*0.34)+"px";
document.getElementById("sale").style.top=parseInt(h*0.162)+"px";
document.getElementById("saleimg").style.width=parseInt(w*0.32)+"px";

document.getElementById("words1").style.left=parseInt(w*0.422)+"px";
document.getElementById("words1").style.top=parseInt(h*0.492)+"px";
document.getElementById("words1").style.fontSize=parseInt(w*0.082)+"px";

document.getElementById("words2").style.left=parseInt(w*0.180)+"px";
document.getElementById("words2").style.top=parseInt(h*0.562)+"px";
document.getElementById("words2").style.fontSize=parseInt(w*0.052)+"px";

document.getElementById("words4").style.top=parseInt(h*0.622)+"px";
document.getElementById("words4").style.fontSize=parseInt(w*0.032)+"px";

document.getElementById("star").style.left=parseInt(w*0.122)+"px";
document.getElementById("star").style.top=parseInt(h*0.739)+"px";
document.getElementById("starimg").style.width=parseInt(w*0.082)+"px";

document.getElementById("words3").style.left=parseInt(w*0.21)+"px";
document.getElementById("words3").style.top=parseInt(h*0.739)+"px";
document.getElementById("words3").style.fontSize=parseInt(w*0.05)+"px";

document.getElementById("share").style.left=parseInt(w*0.122)+"px";
document.getElementById("share").style.top=parseInt(h*0.81)+"px";
document.getElementById("shareimg").style.width=parseInt(w*0.752)+"px";


</script>
</html>
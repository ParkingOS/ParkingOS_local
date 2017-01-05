<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
	<title>三百多家车场通用的停车券，duang~</title>
	<script type="text/javascript">
		var ua = navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) != "micromessenger"){
			window.location.href = "http://s.tingchebao.com/zld/error.html";
		}
	</script>
	<script src="js/jquery.js" type="text/javascript">//表格</script>
	<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>

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
		    background-image: url(images/bonus/bg.png);
		    background-repeat:no-repeat;
		    -webkit-user-select: none;
		    -moz-user-select: none;
		}
		.logo{
			width:100px;
			height:24px;
			margin:10px;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/logo.png);
		    background-repeat:no-repeat;
		}
		.cloud{
			width:220px;
			height:135px;
			margin:0px auto;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/cloud.png);
		    background-repeat:no-repeat;
		}
		.redmail{
			width:240px;
			height:243px;
			margin:0px auto;
			background-size: 100% 100%;
			background-position:bottom center;
		    background-color:none;
		    background-image: url(images/bonus/redmail.png);
		    background-repeat:no-repeat;
		}
		.word{
			color:#EEB84B;
			margin:0px auto;
			font-weight:700;
			font-size:13px;
			width:240px;
			text-align:center;
			padding-top:60px;
		}
		.phonenumber1{
			margin:22px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		.phonenumber2{
			margin:0px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		
		.telnumber{
			width: 160px;
			min-height: 30px;
			background:#fff;
			border:1px solid #ccc;
			border-right:none;
			border-radius: 5px;
		}
		
		.code{
			width: 100px;
			min-height: 30px;
			background:#fff;
			border:1px solid #ccc;
			border-right:none;
			
			-webkit-border-bottom-left-radius: 5px;
			border-bottom-left-radius: 5px;
			-webkit-border-top-left-radius: 5px;
			border-top-left-radius: 5px;
		}
		.bt{
			margin:40px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		.next{
			text-align:center;
			width: 163px;
			min-height: 30px;
			color:#D28D43;
			font-weight:700;
			font-size:15px;
			background:#F5E758;
			border:1px solid #F9F188;
			border-right:none;
			border-radius: 5px;
		}
		.getcode{
			margin:8px auto 0px auto;
			text-align:center;
			width: 60px;
			min-height: 30px;
			color:#D28D43;
			font-weight:700;
			font-size:16px;
			background:#F5E758;
			border:1px solid #F9F188;
			border-right:none;
			
			-webkit-border-bottom-right-radius: 5px;
			border-bottom-right-radius: 5px;
			-webkit-border-top-right-radius: 5px;
			border-top-right-radius: 5px;
		}
		.attention{
			bottom: 0px;
		    margin: 0 auto;
		    position: fixed;
		}
		.info{
			display:none;
		}
	</style>
	<script>
		function sub(){
			window.open("http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld");
		}
	</script>

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
		function sharetofriend(){//发送给朋友
			wx.onMenuShareAppMessage({
			    title: '三百多家车场通用的停车券，不能错过', // 分享标题
			    desc: '停车宝新人大礼包', // 分享描述
			    link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx08c66cac888faa2a&redirect_uri=http%3a%2f%2fs.tingchebao.com%2fzld%2fwxpaccount.do%3faction%3dregbonus&response_type=code&scope=snsapi_base&state=123#wechat_redirect', // 分享链接
			    imgUrl: 'http://s.tingchebao.com/zld/images/bonus/order_bonu.png', // 分享图标
			    type: '', // 分享类型,music、video或link，不填默认为link
			    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			    success: function () { 
			        // 用户确认分享后执行的回调函数
			    },
			    cancel: function () { 
			        // 用户取消分享后执行的回调函数
			    }
			});
		}
		
		function sharetocircle(){//发送到朋友圈
			wx.onMenuShareTimeline({
			    title: '三百多家车场通用的停车券，不能错过', // 分享标题
			    link: 'https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx08c66cac888faa2a&redirect_uri=http%3a%2f%2fs.tingchebao.com%2fzld%2fwxpaccount.do%3faction%3dregbonus&response_type=code&scope=snsapi_base&state=123#wechat_redirect', // 分享链接
			    imgUrl: 'http://s.tingchebao.com/zld/images/bonus/order_bonu.png', // 分享图标
			    success: function () { 
			        // 用户确认分享后执行的回调函数
			    },
			    cancel: function () { 
			        // 用户取消分享后执行的回调函数
			    }
			});
		}
		
		sharetofriend();
		sharetocircle();
	});
</script>

</head>
<body >
<script type="text/javascript"
src="http://zb.weixin.qq.com/nearbycgi/addcontact/BeaconAddContactJsBridge.js">
</script>
<script type="text/javascript">
function attention(){
	BeaconAddContactJsBridge.ready(function(){
		//判断是否关注
		BeaconAddContactJsBridge.invoke('checkAddContactStatus',{} ,function(apiResult){
			if(apiResult.err_code == 0){
				/* var status = apiResult.data;
				if(status == 1){
					alert('已关注');
				}else{
					alert('未关注');
					//跳转到关注页
				  BeaconAddContactJsBridge.invoke('jumpAddContact');
				} */
				BeaconAddContactJsBridge.invoke('jumpAddContact');
			}else{
//				alert(apiResult.err_msg)
			}
		});
 	});
}
</script>
<div style='display:none'><img src="http://s.tingchebao.com/zld/images/bonus/weixilogo_300.png"/></div>
	<div class="logo"></div>
	<div class="cloud"></div>
	<div class="redmail" id="nobonus">
		<div class="word" id='pword'></div>
		<div class="bt" style="color:#F5E758;font-size:16px;">只有新用户才能领哦～</div>
		<div class="bt"><input type="button" id="colsubmit" onClick="attention()" class="next" value="点击关注我们"></input></div>
	</div>
<!-- 	<div class="attention"><img style="width: 100%; height: 110px;" src="images/wxpublic/attentionus.jpg" /></div> -->

</body>
</html>

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
		.attention{
			bottom: 0px;
		    margin: 0 auto;
		    position: fixed;
		    -webkit-user-select: none;
		    -moz-user-select: none;
		}
		.bt{
			margin:16px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		.bt button{
			width: 163px;
			min-height: 33px;
			color:#D28D43;
			font-weight:700;
			font-size:16px;
			background:#F5E758;
			border:1px solid #F9F188;
			border-radius: 5px;
		}
		.result{
			width:310px;
			height:400px;
			margin:50px auto;
			background-size: 100% 100%;
			background-position:bottom center;
		    background-color:none;
		    background-image: url(images/bonus/ret.png);
		    background-repeat:no-repeat;
		   
		}
		.ticket{
			width:160px;
			height:63px;
			margin:0 auto;
			background-size: 100% 60%;
			background-position:bottom center;
		    background-color:none;
		    background-image: url(images/bonus/amount.png);
		    background-repeat:no-repeat;
			padding-top:65px;
		}
		.ticketmoney{
			width:100%;
			font-weight:700;
			text-align:center;
			color:#cc4544;
			font-size:23px;
			margin-left:22px;
			margin-top:16px;
		}
		.ticketword{
			width:100%;
			text-align:center;
			color:#FF0000;
			font-size:12px;
			padding-top:21px;
		}
		.ticketrword{
			width:100%;
			text-align:center;
			color:#FFFF00;
			font-size:12px;
			padding-top:60px;
		}
		.wtip{
			width:230px;
			height:230px;
			margin:0px auto;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/weixintip.png);
		    background-repeat:no-repeat;
		}
		.wtiparrow{
			width:200px;
			height:200px;
			margin:0px auto;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/wieixin_arrow.png);
		    background-repeat:no-repeat;
		}
	</style>
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
<body>
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
<div>
<div style='display:none'><img src="http://s.tingchebao.com/zld/images/bonus/weixilogo_300.png"/></div>
	<div class="logo"></div>
	<div class="result" id='result'>
		<div class="ticket" id='ticket'>
			<div class="ticketmoney">${amount}</div>
		</div>
		<div class="ticketword" id="pword">恭喜您获得停车宝礼包</br>手机支付停车费优惠更多喔</div>
		<div class="ticketrword">已放入${mobile}账户中</div>
		<div class="bt"><button onclick="attention()">点击关注我们</button></div>
	</div>
	<a href="https://itunes.apple.com/us/app/ting-che-bao-zhao-che-wei/id927898959?l=zh&ls=1&mt=8" id='gotoios'></a>
	<!-- 
	<a href="itms-services://?action=download-manifest&url=https://dn-tingchebao.qbox.me/tingCheBao.plist" id='gotoios'></a>
	 -->
	<!--  <div class="attention"><img style="width: 100%; height: 110px;" src="images/wxpublic/attentionus.jpg" /></div> -->
</body>
<script language="javascript">

function sub(){
	window.open("http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld");
};
var getObj=function (id){return document.getElementById(id) };
function pageCover(){
	var coverW="100%";
	var coverH = "100%";
	var coverIndex = 1;
	var cover = document.createElement("div");
	cover.setAttribute('id','cover');
	var c = cover.style;
	c.display = "block";
	c.visibility = "visible";
	c.width = coverW;
	c.height = coverH;
	c.background ='#000';
	c.position ='absolute';
	cover.zIndex = coverIndex;
	c.top = "0px";
	c.left = "0px";
	c.cursor = "not-allowed";
	c.filter = "alpha(opacity=40)";
	c.opacity ="0.8";
	//c.background="url(images/bonus/wieixin_arrow.png) no-repeat";
	cover.innerHTML = "";
	var dis = document.createElement("div");
	dis.className='wtiparrow';
	cover.appendChild(dis);
	
	var tip = document.createElement("div");
	tip.className='wtip';
	cover.appendChild(tip);
	tip.onclick=function (){cover.style.display='none';};
	
	document.body.appendChild(cover);
	//c.display = "none";
	//c.visibility = "hidden";
	//alert(document.body);
}	
function removetip(){
	document.removeChild(document.getElementById("cover"));
}
</script>
</html>

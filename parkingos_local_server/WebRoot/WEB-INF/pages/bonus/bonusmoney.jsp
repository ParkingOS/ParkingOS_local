<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
	<title>二百多家车场通用的停车券，duang~</title>
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
</head>
<body>
<div>
<div style='display:none'><img src="http://s.tingchebao.com/zld/images/bonus/weixilogo_300.png"/></div>
	<div class="logo"></div>
	<div class="result" id='result'>
		<div class="ticket" id='ticket'>
			<div class="ticketmoney">${amount}</div>
		</div>
		<div class="ticketword" id="pword">恭喜您获得停车宝礼包</br>手机支付停车费优惠更多喔</div>
		<div class="ticketrword">已放入${uphone}账户中</div>
		<div class="bt"><button onclick="sub()">点击下载客户端</button></div>
	</div>
	<a href="http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld" id='gotoios'></a>
	<!-- 
	<a href="itms-services://?action=download-manifest&url=https://dn-tingchebao.qbox.me/tingCheBao.plist" id='gotoios'></a>
	 -->
</body>
<script>

</script>
<script language="javascript">

function sub(){
	window.open("http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld");
};
var type = '${type}';
var getObj=function (id){return document.getElementById(id) };
if(type=='-3'){
	document.getElementById("pword").innerHTML='这是您已领过的停车宝礼包';
}
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
<script>
var imgUrl = 'http://s.tingchebao.com/zld/images/bonus/weixilogo_300.png';  // 分享后展示的一张图片        
var lineLink = 'http://s.tingchebao.com/zld/carowner.do?action=${action}&id=${id}'; // 点击分享后跳转的页面地址        
var descContent = '停车没烦恼，就用停车宝。快速找车位，一键付车费，体验萌萌哒！';  // 分享后的描述信息      
var shareTitle = '二百多家车场通用的停车券，duang~';  // 分享后的标题       
var appid = '';  // 应用id,如果有可以填，没有就留空                
function shareFriend() {            
	WeixinJSBridge.invoke('sendAppMessage',{                
		"appid": appid,                
		"img_url": imgUrl,                
		"img_width": "200",                
		"img_height": "200",                
		"link": lineLink,                
		"desc": descContent,                
		"title": shareTitle            
		}, 
		function(res) {                //_report('send_msg', res.err_msg);  // 这是回调函数，必须注释掉          
		})       
		}        
		function shareTimeline() {            
			WeixinJSBridge.invoke('shareTimeline',{                
				"img_url": imgUrl,               
				"img_width": "200",                
				"img_height": "200",                
				"link": lineLink,                
				"desc": descContent,                
				"title": shareTitle            
				}, function(res) {                   //_report('timeline', res.err_msg); // 这是回调函数，必须注释掉            
				});        
			}        
		function shareWeibo() {            
			WeixinJSBridge.invoke('shareWeibo',{                
				"content": descContent,                
				"url": lineLink,            
				}, function(res) {                //_report('weibo', res.err_msg);            
				});        
		}       
			// 当微信内置浏览器完成内部初始化后会触发WeixinJSBridgeReady事件。        
	document.addEventListener('WeixinJSBridgeReady', 
		function onBridgeReady() {           
			// 发送给好友            
			WeixinJSBridge.on('menu:share:appmessage', 
				function(argv){                
			shareFriend();            
			});            
			// 分享到朋友圈            
			WeixinJSBridge.on('menu:share:timeline', function(argv){
				shareTimeline();            
				});           
			// 分享到微博          
			WeixinJSBridge.on('menu:share:weibo', function(argv){ 
					shareWeibo();            
				});        
		}, 	false);
</script>
</html>

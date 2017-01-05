<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
	<title>三百多家车场通用的停车券，duang~</title>
	<script type="text/javascript" src="mobileRes/iscroll-lite.js"></script>
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
			font-size:16px;
			width:240px;
			text-align:center;
			padding-top:60px;
		}
		.phonenumber{
			margin:22px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		
		.phonenumber input{
			width: 160px;
			min-height: 30px;
			background:#fff;
			border:1px solid #ccc;
			border-right:none;
			border-radius: 5px;
		}
		.bt{
			margin:8px auto 0px auto;
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
			border-right:none;
			border-radius: 5px;
		}
	</style>
</head>
<body>
<div id='wx_pic' style='margin:0 auto;display:none;'>
<img src='images/bonus/weixilogo.png' />
</div>
	<div class="logo"></div>
	<div class="cloud"></div>
	<div class="redmail">
		<div class="word" id ='pword'>恭喜您</br>获得停车券${amount}元</br>已放入您的账户<br/>${uphone}中</div>
		<div class="phonenumber"> </div>
		<div class="bt"><button onclick="sub()">点击下载客户端</button></div>
	</div>
	<a href="http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld" id='gotoios'></a>
</body>
<script>

</script>
<script language="javascript">
function sub(){
	var sourcetag = navigator.userAgent;
	
	if(sourcetag.indexOf("MicroMessenger")!=-1){//来看手机中的微信来，指向应用宝
		if(sourcetag.indexOf("Android")!=-1){//来自android的微信
			window.open("http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld");
		}else if(sourcetag.indexOf("iPhone")!=-1){//来自iphone的微信
			//alert(document.getElementById('gotoios'));
			pageCover();
			document.getElementById('gotoios').click();
		}
	}else 	if(sourcetag.indexOf("Android")!=-1){//来自android的微信
		//if(sourcetag.indexOf("QQ")!=-1){//QQ中的扫描
		//	window.open("http://a.app.qq.com/o/simple.jsp?pkgname=com.tq.zld");
		//}else
		window.open("http://d.tingchebao.com/downfiles/tingchebao.apk");
	}else if(sourcetag.indexOf("iPhone")!=-1){//来自iphone的微信
		
		document.getElementById('gotoios').click();
	}
	
	//location = "http://d.tingchebao.com/downfiles/tingchebao.apk";
};
var type = '${type}';
if(type=='-3'){
	document.getElementById("pword").innerHTML='您已领取过停车券${amount}元</br>已放入您的账户<br/>${uphone}中';
}else if(type=='-4'){
	document.getElementById("pword").innerHTML='手机号不合法!!';
}else if(type=='-1'){
	document.getElementById("pword").innerHTML='礼包已领完!<br/><br/>下次早点下手哦！';
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
</html>

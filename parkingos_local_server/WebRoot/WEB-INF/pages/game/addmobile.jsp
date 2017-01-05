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
<title>领取停车券</title>
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
		.inputnumb{
			background:#000000;
			border:1px solid #CFCFCF;
			border-radius: 5px;
			width:70%;
			margin-top:10%;
			color:#FFFFFF;
		}
		</style>
</head>
<body id='body' >
	<div id="words1" style="width:100%;margin-top:30%;color:#FFFFFF;text-align:center" >
	<img src='images/game/quan2.png' id='quanimg' />
	</div>
	<div id="words2" style="color:#FFFFFF;text-align:center;margin-top:6%;" >
	您得到一张两元游戏专属券
	</div>
	<div id="words3" style="color:yellow;text-align:center;" >
	<img src="images/game/star.png" width='18px;'/>&nbsp;停车挑战成功后，可翻倍
	</div>
	<div id="input" style="text-align:center;width:100%" >
		<input id='mobile' class='inputnumb' value='在此输入手机号后领取' onclick='movepage();'/>
	</div>
	<div  onclick='getticket()' id='cbtn'>
			<img src="images/game/conget.png" alt="" id='btnimg' />
	</div>
</body>
<script>
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;


document.getElementById("words1").style.fontSize=parseInt(w*0.12)+'px';

document.getElementById("words2").style.fontSize=parseInt(w*0.052)+"px";
document.getElementById("words2").style.lineHeight=parseInt(w*0.11)+"px";

document.getElementById("words3").style.fontSize=parseInt(w*0.052)+"px";
document.getElementById("words3").style.lineHeight=parseInt(w*0.11)+"px";

document.getElementById("mobile").style.fontSize=parseInt(w*0.060)+"px";
document.getElementById("mobile").style.height=parseInt(w*0.09)+"px";
document.getElementById("mobile").style.width=parseInt(w*0.6)+"px";

document.getElementById("btnimg").style.width=parseInt(w*0.68)+"px";
document.getElementById("btnimg").style.marginLeft=parseInt(w*0.16)+"px";
document.getElementById("btnimg").style.marginTop=parseInt(w*0.06)+"px";
document.getElementById("cbtn").style.fontSize=parseInt(w*0.052)+"px";
document.getElementById("btnimg").style.height=parseInt(w*0.09)+"px";
//document.getElementById("tdiv").style.height=parseInt(w*0.007)+"px";

function getticket(){
	var mobile=document.getElementById("mobile").value;
	var url = "cargame.do?action=getgameticket&openid=${openid}&otdid=${otdid}&mobile="+mobile;
	//alert(url);
	location = url;
}
function movepage(){
	document.getElementById("mobile").value='';
	document.getElementById("mobile").style.color='#FFFFFF';
	setTimeout(function(){document.getElementById("input").scrollIntoView()},500);
}	
</script>
</html>
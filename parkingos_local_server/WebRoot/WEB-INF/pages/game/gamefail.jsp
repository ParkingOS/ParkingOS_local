<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>挑战失败～～</title>
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
		*{margin:0; padding:0;}
</style>
</head>

<body id='body'>
	<div id="sale" style="position:absolute;">
		<img id='saleimg' src='images/game/fail_dzq.png'  id='ruleimg'/>
	</div>
	<div id="words1" style="position:absolute;color:#FFFFFF" >
	</div>
	<div id="words2" style="position:absolute;color:#EDC500" >
		${score}<span style='font-size:20px;color:#EDC500'>分</span>
	</div>
	
	<!-- <div id="words3" style="position:absolute;color:#EDC500" >
		查看排行榜后，可复活本张停车券
	</div>
	<div id="star" style="position:absolute;" >
		<img id='starimg' src='images/game/star.png' width='140px'/>
	</div>
 -->
	<div id="share" style="position:absolute;" onclick='gameagin();'>
		<img id='shareimg' src='images/game/gameagin.png' width='140px' />
	</div>
</body>
<script>
var type='${ctype}';
var words = "很遗憾，挑战失败，失去本张折扣券";
if(type&&type<2){
	document.getElementById("saleimg").src='images/game/fail_tcq.png';
	words = "很遗憾，挑战失败，失去本张停车券";
}
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;

document.getElementById("sale").style.left=parseInt(w*0.122)+"px";
document.getElementById("sale").style.top=parseInt(h*0.182)+"px";
document.getElementById("saleimg").style.width=parseInt(w*0.74)+"px";

document.getElementById("words1").style.left=parseInt(w*0.18)+"px";
document.getElementById("words1").style.top=parseInt(h*0.382)+"px";
document.getElementById("words1").style.width=parseInt(w*0.682)+"px";
document.getElementById("words1").style.fontSize=parseInt(w*0.042)+"px";
document.getElementById("words1").innerText=words;

document.getElementById("words2").style.left=parseInt(w*0.342)+"px";
document.getElementById("words2").style.top=parseInt(h*0.452)+"px";
document.getElementById("words2").style.fontSize=parseInt(w*0.22)+"px";
/*
document.getElementById("star").style.left=parseInt(w*0.122)+"px";
document.getElementById("star").style.top=parseInt(h*0.682)+"px";
document.getElementById("starimg").style.width=parseInt(w*0.082)+"px";

document.getElementById("words3").style.left=parseInt(w*0.21)+"px";
document.getElementById("words3").style.top=parseInt(h*0.682)+"px";
document.getElementById("words3").style.fontSize=parseInt(w*0.042)+"px";
*/
document.getElementById("share").style.left=parseInt(w*0.122)+"px";
document.getElementById("share").style.top=parseInt(h*0.75)+"px";
document.getElementById("shareimg").style.width=parseInt(w*0.752)+"px";


function viewsort(){
	location="cargame.do?action=sort&uin=${uin}&score=${score}&usercount=${usercount}&sort=${sort}&type=${ctype}&ticketid=${ticketid}";
}

function gameagin(){
	location="cargame.do?action=playagin&uin=${uin}";
}
</script>

</html>
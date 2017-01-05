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
<title>停车游戏</title>
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
		</style>
</head>
<body id='body'>
	<div id="sorry" style="position:absolute;text-align:center;color:#FFFFFF;" >
		抱 歉
	</div>
	<div id="close" style="position:absolute;text-align:center;color:#FFFFFF;" >
		${message}
	</div>
	<img src = 'images/game/att_more.png' id='closimg'  style="position:absolute;" onclick='attention()'/>
</body>
<script>
var w = document.getElementById('body').offsetWidth;
var h = document.getElementById('body').offsetHeight;

document.getElementById("sorry").style.width=parseInt(w*0.82)+"px";
document.getElementById("sorry").style.left=parseInt(w*0.09)+"px";
document.getElementById("sorry").style.top=parseInt(h*0.202)+"px";
document.getElementById("sorry").style.fontSize=parseInt(w*0.152)+"px";

document.getElementById("close").style.width=parseInt(w*0.82)+"px";
document.getElementById("close").style.left=parseInt(w*0.09)+"px";
document.getElementById("close").style.top=parseInt(h*0.352)+"px";
document.getElementById("close").style.fontSize=parseInt(w*0.052)+"px";

document.getElementById('closimg').style.width=parseInt(w*0.72)+"px";
document.getElementById('closimg').style.height=parseInt(w*0.082)+"px";
document.getElementById('closimg').style.top=parseInt(h*0.72)+"px";
document.getElementById('closimg').style.left=parseInt(w*0.14)+"px";

function attention(){
	location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205938292&idx=1&sn=76c6259270d762df187a187fac9e9a8d#rd';
}
</script>
</html>
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
		.confirmbtn{
			background:#FFFFFF;
			border:1px solid #CFCFCF;
			border-radius: 5px;
			margin-top:18%;
			color:#000000;
			text-align:center;
		}
		</style>
</head>
<body id='body' >
	<img src='images/game/quan2.png' id='quanimg' style='position:absolute'/>
	<div id="words1" style="position:absolute;color:#FFFFFF;text-align:center;" >
	您得到一张${money}元游戏专属券
	</div>
	<div id="words2" style="position:absolute;color:yellow;text-align:center;" >
	<img src="images/game/star.png" width='18px;'/>&nbsp;停车挑战成功后，可翻倍</div>
	<img src='images/game/to_game.png'  style='position:absolute' id ='cbtn' onclick='togame();'/>
</body>
<script>
var getobj=function(id){return document.getElementById(id)};
var h = getobj('body').offsetHeight;
var w = getobj('body').offsetWidth;

getobj('quanimg').style.width=parseInt(w*0.62)+"px";
getobj('quanimg').style.left=parseInt(w*0.19)+"px";
getobj('quanimg').style.top=parseInt(h*0.224)+"px";

getobj('words1').style.width=parseInt(w*0.62)+"px";
getobj('words1').style.left=parseInt(w*0.19)+"px";
getobj('words1').style.top=parseInt(h*0.464)+"px";
getobj('words1').style.fontSize=parseInt(w*0.0544)+"px";

getobj('words2').style.width=parseInt(w*0.62)+"px";
getobj('words2').style.left=parseInt(w*0.19)+"px";
getobj('words2').style.top=parseInt(h*0.524)+"px";
getobj('words2').style.fontSize=parseInt(w*0.050)+"px";


getobj("cbtn").style.width=parseInt(w*0.68)+"px";
getobj("cbtn").style.left=parseInt(w*0.16)+"px";
getobj("cbtn").style.top=parseInt(h*0.73)+"px";

function togame(){
	location = "cargame.do?action=pregame&uin=${uin}&id=${ticketid}"
}
</script>
</html>
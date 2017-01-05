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
<title>手机号错误~</title>
<style type="text/css">
		html,body {
		    padding: 0 !important;
			margin: 0 !important;
		    background-color:#999;
		    width:100%;
		    height:100%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
		}
		</style>
</head>
<body id='body'>
	<div id="close" style="position:absolute;top:20%;left:8%;width:60%" >
		<img src = 'images/game/game_error.png' id='closimg'/>
	</div>
	<div id="close" style="position:absolute;top:50%;width:80%;left:10%;color:#FFFFFF;margin:10px auto;font-size:26px;line-height:50px;text-align:center" >
		停车宝温馨提示</br>${message}
	</div>
</body>
<script>
var w = document.getElementById('body').offsetWidth;
document.getElementById("close").style.left=parseInt(w*0.252)+"px";
document.getElementById('closimg').style.width=parseInt(w*0.52)+"px";

</script>
</html>
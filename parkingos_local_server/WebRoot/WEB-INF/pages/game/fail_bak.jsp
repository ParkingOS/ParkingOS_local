<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <meta name="viewport" content="user-scalable=no,target-densitydpi=high-dpi" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>游戏失败～～</title>
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
		    background-image: url(images/game/fail.jpg);
		    background-repeat:no-repeat;
		}
		</style>
</head>
<body id='body' >
	<div id="close" style='position:absolute' onclick="playagin()">
	 <img id='closeimg' src='images/game/agin.png' />
	</div>
</body>
<script>
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
document.getElementById("close").style.left=parseInt(w*0.352)+"px";
document.getElementById("close").style.top=parseInt(h*0.682)+"px";
document.getElementById("closeimg").style.width=parseInt(w*0.32)+"px";

function playagin(){
	location = "cargame.do?action=playagin&uin=${uin}";
}
	
</script>
</html>
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
<title>打灰机了~~</title>
<style type="text/css">
		html,body {
		    padding: 0 !important;
			margin: 0 !important;
		    background-color:#ffffff;
		    width:100%;
		    height:100%;
		    font-family:"微软雅黑";
		    overflow-x:auto;
		     background-size: 100% 100%;
			background-position:top center;
		    background-color:#F0F0F0;
		    background-image: url(images/flygame/${pic});
		    background-repeat:no-repeat;
		}	
	</style>
</head>

<body id='body' >
	<img id="again" style="display:none;position:absolute" src="images/flygame/error_again.png" />
	<img id="close" style="display:none;position:absolute" src="images/flygame/error_close.png" />

<form action="flygame.do" method='post' id='togame'>
		<input type='hidden' id='actions' name='action' value='play'/>
		<input type='hidden' name='tid' value='${tid }'/>
		<input type='hidden' name='uin' value='${uin}'/>
	</form>
</body>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>

<script>
var getobj=function(id){return document.getElementById(id)};
var h = getobj('body').offsetHeight;
var w = getobj('body').offsetWidth;
var acss = getobj('again').style;
acss.top=parseInt(h*0.8)+'px'
acss.left=parseInt(w*0.2)+'px'
acss.width=parseInt(w*0.6)+'px'
getobj('again').onclick=function(){togmame();};

var ccss = getobj('close').style;
ccss.top=parseInt(h*0.8)+'px'
ccss.left=parseInt(w*0.2)+'px'
ccss.width=parseInt(w*0.6)+'px'
getobj('close').onclick=function(){close();};

var type='${type}';
if(type=='1'){
	document.getElementById('again').style.display='';
}else
	document.getElementById('close').style.display='';
	
function togmame(){
	document.getElementById('togame').submit();
}
function close(){
	var sourcetag = navigator.userAgent
	if(navigator.userAgent.indexOf("MicroMessenger")!=-1){
		wx.closeWindow();
	}else if(sourcetag.indexOf("Android")!=-1){//来自android
		window.share.closeWebView();
	}else if(sourcetag.indexOf("iPhone")!=-1){//来自iphone
		//window.share.closeWebView();
		wx.closeWindow();
	}
	//document.getElementById('actions').value='pregame';
	//document.getElementById('togame').submit();
}


</script>
</html>
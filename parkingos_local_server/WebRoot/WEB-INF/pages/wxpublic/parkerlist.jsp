<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>收费员</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=11" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=2" />
<script src="js/wxpublic/jquery.mobile-1.3.2.min.js"></script>
<script src="js/jquery.js"></script>
<script src="js/wxpublic/iscroll.js"></script>
<script src="js/wxpublic/parkerlist.js?v=3"></script>
<style type="text/css">
#scroller li {
    padding:0 10px;
    height:92px;
    line-height:35px;
    background-color:#FFFFFF;
    font-size:14px;
    margin-top:1px;
    overflow: hidden;
}

a{
	position: absolute;
	margin-top: 0;
	margin-left:10px;
	overflow: hidden;
}
img{
	width:50px;
	height:50px;
}

a{
	text-decoration:none;
	color:#6D6D6D;
	width:100%;
	overflow: hidden;
}
.nickname{
	color:#101010;
	font-size:16px;
}

#header {
    position:absolute; z-index:2;
    top:0; left:0;
    width:100%;
    height:45px;
    line-height:45px;
    background-color:#F3F3F3;
    padding:0;
    font-size:20px;
}

.online{
	font-size:11px;
	text-align:center;
	padding-top:0px;
	padding-bottom:0px;
	border-radius:3px;
	background-color:#FFFFFF;
	outline:medium;
	margin-left:5px;
	border:1px solid green;
	color:green;
	padding-left:2px;
	padding-right:2px;
}

.offline{
	font-size:11px;
	text-align:center;
	padding-top:0px;
	padding-bottom:0px;
	border-radius:3px;
	background-color:#FFFFFF;
	outline:medium;
	margin-left:5px;
	border:1px solid red;
	color:red;
	padding-left:2px;
	padding-right:2px;
}

.reward{
	font-size:11px;
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#FEAF0D;
	outline:medium;
	margin-left:2px;
	color:white;
	padding-left:2px;
	padding-right:2px;
}

.fuwu{
	font-size:11px;
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#3A6EA5;
	outline:medium;
	margin-left:2px;
	color:white;
	padding-left:2px;
	padding-right:2px;
}

.rew{
	margin-left:5px;
	font-size:11px;
}

.fuwu1{
	margin-top:-12px;
}

.bianhao{
	float:right;
	margin-right:30px;
	font-size:12px;
}

.hide{
	display:none;
}
</style>
</head>
<body>
	<div id="wrapper" style="margin-top:-45px;">
		<div id="scroller">
			<div id="pullDown" class="loading">
				<span class="pullDownIcon"></span>
				<span class="pullDownLabel">加载中...</span>
			</div>
 			<ul id="thelist" style="margin-top:50px;">
				
			</ul>
			<div id="pullUp" class="idle hide">
				<span class="pullUpIcon"></span>
				<span class="pullUpLabel">上拉加载更多...</span>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	$(document).ready(function() {
		loaded("${comid}","${openid}");
	});
</script>
</html>

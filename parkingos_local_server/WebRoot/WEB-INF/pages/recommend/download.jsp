<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>车场版App</title>
<script src="js/jquery.js" type=text/javascript></script>
<style type="text/css">
	*{margin:0; padding:0;}
	a{text-decoration: none;}
	img{max-width: 100%; height: auto;}
	.weixin-tip{display: none; position: fixed; left:0; top:0; bottom:0; background: rgba(0,0,0,0.8); filter:alpha(opacity=80);  height: 100%; width: 100%; z-index: 100;}
	.weixin-tip p{text-align: center; margin-top: 10%; padding:0 5%;}
</style>
<style type="text/css">
	body {text-align:center;background-color:#F0F0F0;}
	
	.download {
	  	width: 192px;
	  	margin-left:20%;
	  	margin:0px auto;
	  	margin-top:25%;
	}
	.iphone {
		background: url('images/btns.png') no-repeat 0 0;
	  	width: 192px;
	  	height: 48px;
	  	clear: left;
	}
	.iphone:hover {
		background: url('images/btns.png') no-repeat -217px 0;
	}
	.android {
	  margin-top: 25px;
	  background: url('images/btns.png') no-repeat 0 -72px;
	  width: 192px;
	  height: 48px;
	  clear: both;
	}
	.android:hover {
	  background: url('images/btns.png') no-repeat -217px -72px;
	}
	
	div.signInHeader {
		font-size: 20px;
		color: #666;
		margin-top:10%;
	}
	.fixed-bottom{
			    position: fixed; bottom:10px; _top:expression(eval(document.documentElement.scrollTop+document.documentElement.clientHeight-this.offsetHeight-(parseInt(this.currentStyle.marginTop,10)||0)-(parseInt(this.currentStyle.marginBottom,10)||0)-10));right:5px; 
			}
	.fixed{
		    _position: absolute; _bottom: auto;
		}
	.logo {text-align:right;margin-right:5px;}
</style>
</head>
<body>
<!-- 微信浏览器遮罩层 -->
<div class="weixin-tip">
	<p>
		<img src="images/client_menu_icons/wxmask.png" alt="微信打开"/>
	</p>
</div>
<div>
<div class="signInHeader">收费员专用版下载</div>
	<div class="download">
		<a href="itms-services://?action=download-manifest&url=https://dn-tingchebao-chechang.qbox.me/TingCheBao.plist">
		<div class="iphone"></div></a>
		<div class="android" onclick="location.href='http://d.tingchebao.com/downfiles/tingchebao_biz.apk'"></div>
	</div>
<div class="fixed-bottom fixed logo"><img src="images/client_menu_icons/tingchebao.png" style="width:150px;height:40px;"/></div>
</div>
<script type="text/javascript">
	$(window).on("load", function() {
		var winHeight = $(window).height();
		function is_weixin() {
			var ua = navigator.userAgent.toLowerCase();
			if (ua.match(/MicroMessenger/i) == "micromessenger") {
				return true;
			} else {
				return false;
			}
		}
		var isWeixin = is_weixin();
		if (isWeixin) {
			$(".weixin-tip").css("height", winHeight);
			$(".weixin-tip").show();
		}
	})
</script>
</body>
</html>

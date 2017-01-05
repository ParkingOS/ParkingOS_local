<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>附近车场</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=2" />
<script src="js/wxpublic/jquery.mobile-1.3.2.min.js"></script>
<script src="js/jquery.js"></script>
<script src="js/wxpublic/iscroll.js"></script>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=GK54pXUffolC3ijXRIavHS5R"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
<script src="js/wxpublic/parklist.js?v=58"></script>
<style type="text/css">
#scroller .li1 {
    padding:0 10px;
    height:85px;
    line-height:0px;
    background-color:#FFFFFF;
    font-size:14px;
    margin-top:1px;
    overflow: hidden;
}

.li2 {
    padding:0 10px;
    height:50px;
    line-height:0px;
    background-color:#FFFFFF;
    font-size:12px;
    overflow: hidden;
}

.a1{
	text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
	
	position: relative;
	top:-35px;
	left:30px;
}

.a2{
	text-decoration:none;
	color:#6D6D6D;
	
	position: relative;
	top:13px;
	left:30px;
}

</style>
<style type="text/css">
* {
	margin: 0px;
	padding: 0px;
}

body {
	font-size: 12px;
	font: Arial, Helvetica, sans-serif;
	margin: 25PX 0PX;
	background: #eee;
}

.img2{
	width:60px;
	height:60px;
	margin-top:15px;
	position:relative;
	z-index:99999;
}

.cname{
	margin-left:50px;
	margin-top:-12px;
	color:#101010;
	font-size:15px;
}

.distance{
	margin-left:50px;
	margin-top:35px;
	font-size:12px;
	color : gray;
}

.first{
	margin-left:50px;
	margin-top:0px;
	color:gray;
}

.price{
	margin-left:50px;
	margin-top:19px;
	color : gray;
}

.passli {
	background-image: url(images/wxpublic/arrow.png);
	background-size: 19px 39px;
	background-repeat: no-repeat;
	background-position: right center;
}

.epay{
	font-size:11px;
	text-align:center;
	padding-top:1px;
	padding-bottom:0px;
	border-radius:3px;
	background-color:#FFFFFF;
	outline:medium;
	margin-left:5px;
	border:1px solid #38B074;
	color:#38B074;
	padding-left:1px;
	padding-right:1px;
}

.hui{
	font-size:11px;
	text-align:center;
	padding-top:1px;
	padding-bottom:0px;
	border-radius:3px;
	background-color:#FFFFFF;
	outline:medium;
	margin-left:5px;
	border:1px solid red;
	color:red;
	padding-left:1px;
	padding-right:1px;
}

.first_time{
	font-size:11px;
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#FEAF0D;
	outline:medium;
	margin-left:5px;
	color:white;
	padding-left:2px;
	padding-right:2px;
}

.first_price{
	font-size:11px;
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#FC7311;
	outline:medium;
	margin-left:5px;
	color:white;
	padding-left:2px;
	padding-right:2px;
}

.first_cname{
	margin-left:10px;
}

.credit{
	float:right;
	margin-right:20px;
	margin-top: -65px;
	color:#B3B3B3;
	font-size:12px;
	line-height: 60px;
	position: relative;
    z-index: 999999;
}

.hide{
	display : none;
}
</style>
<style type="text/css">
#header {
	position: absolute;
	width: 100%;
	line-height: 45px;
	padding: 0;
	text-align: center;
	
	z-index: 100;
	background-color: #38B074;
	height:45px;
	margin-top: -25px;
}
#suggestId{
	    background-color: #EBEBEB;
    padding-bottom: 0px;
    padding-top: 5px;
    width: 95%;
    padding-left: 10px;
    font-size: 14px;
    color: gray;
    height: 32px;
    margin-top: 4px;
    text-shadow: 0 0px 0 #fff;
    -webkit-appearance: none;
    border: 0px;
}
.tangram-suggestion-main{/*把百度自动搜索的结果显示在最上层,勿动*/
	z-index: 100;
}
</style>
<style type="text/css">
#BgDiv1{background-color:#000; position:absolute; z-index:9999;  display:none;left:0px; top:0px; width:100%; height:100%;opacity: 0.6; filter: alpha(opacity=60);}
.DialogDiv{position:absolute;z-index:99999;}/*配送公告*/
.U-user-login-btn{ display:block; border:none; background:url(images/wxpublic/bg_mb_btn1_1.png) repeat-x; font-size:1em; color:#efefef; line-height:49px; cursor:pointer; height:53px; font-weight:bold;
border-radius:3px;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
 width:100%; box-shadow: 0 1px 4px #cbcacf, 0 0 40px #cbcacf ;}
 .U-user-login-btn:hover, .U-user-login-btn:active{ display:block; border:none; background:url(images/wxpublic/bg_mb_btn1_1_h.png) repeat-x; font-size:1em; color:#efefef; line-height:49px; cursor:pointer; height:53px; font-weight:bold;
border-radius:3px;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
 width:100%; box-shadow: 0 1px 4px #cbcacf, 0 0 40px #cbcacf ;}
.U-user-login-btn2{ display:block; border:none;background:url(images/wxpublic/bg_mb_btn1_1_h.png) repeat-x;   font-size:1em; color:#efefef; line-height:49px; cursor:pointer; font-weight:bold;
border-radius:3px;
-webkit-border-radius: 3px;
-moz-border-radius: 3px;
 width:100%; box-shadow: 0 1px 4px #cbcacf, 0 0 40px #cbcacf ;height:53px;}
.U-guodu-box { padding:10px 15px;  background:#3c3c3f; filter:alpha(opacity=90); -moz-opacity:0.9; -khtml-opacity: 0.9; opacity: 0.9;  min-heigh:200px; border-radius:10px;}
.U-guodu-box div{ color:#fff; line-height:20px; font-size:12px; margin:0px auto; height:100%; padding-top:10%; padding-bottom:10%;}

</style>
</head>
<body>
<div id="header">
	<div class="shipSearch">
		<div id="l-map"></div>
		<div><input type="text" id="suggestId" placeholder="输入停车场地址搜索" /></div>
	</div>
</div>
	<div id="wrapper" style="margin-top:0px;">
		<div id="scroller">
			<div id="pullDown" class="loading hide">
				<span class="pullDownIcon"></span>
				<span class="pullDownLabel">加载中...</span>
			</div>

			<ul id="thelist" style="margin-top:0px;">
			<!-- <li class="li1"><img class="img2" src="" /><a href="http://www.baidu.com" class="a1"><div class="cname">金隅计划大厦</div><div class="distance">1/km<span class="epay">付</span></div></a></li>
			<li class="li1"><img class="img2" src="" /><a href="http://www.baidu.com" class="a1"><div class="cname">金隅计划大厦</div><div class="distance">1/km<span class="epay">付</span></div></a></li>
			<li class="li1"><img class="img2" src="" /><a href="http://www.baidu.com" class="a1"><div class="cname">金隅计划大厦</div><div class="distance">1/km<span class="epay">付</span></div></a></li>
			
			 -->
			</ul>
			<div id="pullUp" class="idle hide">
				<span class="pullUpIcon"></span>
				<span class="pullUpLabel">上拉加载更多...</span>
			</div>
		</div>
	</div>
	<div class="DialogDiv" style="display:none; ">
		<div class="U-guodu-box">
			<div>
				<table width="100%" cellpadding="0" cellspacing="0" border="0">
					<tr>
						<td align="center"><img id="imginfo" src="images/wxpublic/loading.gif">
						</td>
					</tr>
					<tr>
						<td style="padding-top: 10px;" id="showinfo" valign="middle" align="center">加载中，请稍候...</td>
					</tr>
				</table>
			</div>
		</div>
	</div>
</body>
<script type="text/javascript">
	$(document).ready(function() {
		shade();
		getBMapLocation();
	});
	function getBMapLocation(){
		// 百度地图API功能
		var geolocation = new BMap.Geolocation();
		geolocation.getCurrentPosition(function(r) {
			if (this.getStatus() == BMAP_STATUS_SUCCESS) {
//				alert('您的位置：' + r.point.lng + ',' + r.point.lat);
				loaded(r.point.lat,r.point.lng,"${openid}");
			} else {
				alert("请允许获取您的地理位置！");
			}
		}, {
			enableHighAccuracy : true
		})
		//关于状态码
		//BMAP_STATUS_SUCCESS	检索成功。对应数值“0”。
		//BMAP_STATUS_CITY_LIST	城市列表。对应数值“1”。
		//BMAP_STATUS_UNKNOWN_LOCATION	位置结果未知。对应数值“2”。
		//BMAP_STATUS_UNKNOWN_ROUTE	导航结果未知。对应数值“3”。
		//BMAP_STATUS_INVALID_KEY	非法密钥。对应数值“4”。
		//BMAP_STATUS_INVALID_REQUEST	非法请求。对应数值“5”。
		//BMAP_STATUS_PERMISSION_DENIED	没有权限。对应数值“6”。(自 1.1 新增)
		//BMAP_STATUS_SERVICE_UNAVAILABLE	服务不可用。对应数值“7”。(自 1.1 新增)
		//BMAP_STATUS_TIMEOUT	超时。对应数值“8”。(自 1.1 新增)
	}

	// 百度地图API功能
	function G(id) {
		return document.getElementById(id);
	}
	var map = new BMap.Map("l-map");

	var ac = new BMap.Autocomplete(//建立一个自动完成的对象
		{"input" : "suggestId"
	});

	ac.addEventListener("onconfirm", function(e) {//鼠标点击下拉列表后的事件
		shade();
		var _value = e.item.value;
		var myValue = _value.province +  _value.city +  _value.district +  _value.street +  _value.business;
		setPlace(myValue);
	});

	function setPlace(keyword){
		G("thelist").innerHTML = "";
		var localSearch = new BMap.LocalSearch(map);
		localSearch.setSearchCompleteCallback(function (searchResult) {
				var poi = searchResult.getPoi(0).point;
				loaded(poi.lat,poi.lng,"${openid}");
			});
		localSearch.search(keyword);
	}

	function previewpic(comid){
		jQuery.ajax({
			type : "post",
			url : "wxpublic.do",
			data : {
				'action' : 'getparkpic',
				'comid' : comid
			},
			async : false,
			success : function(result) {
				if(result != ""){
					wx.previewImage({
				      current: result,
				      urls: [
							result
				      ]
					});
				}
			}
		});
	}
	
	function shade(){
	 	$("#BgDiv1").css({ display: "block", height: $(document).height() });
		var yscroll = document.documentElement.scrollTop;
		var screenx=$(window).width();
		var screeny=$(window).height();
	  	$(".DialogDiv").css("display", "block");
		 $(".DialogDiv").css("top",yscroll+"px");
		 var DialogDiv_width=$(".DialogDiv").width();
		 var DialogDiv_height=$(".DialogDiv").height();
		  $(".DialogDiv").css("left",(screenx/2-DialogDiv_width/2)+"px")
		 $(".DialogDiv").css("top",(screeny/2-DialogDiv_height/2)+"px")
		 $("body").css("overflow","hidden");
	 }
	
	function showinfo(comid){
		var info = document.getElementById("huiinfo_"+comid);
		if(hasClass(info, "hide")){
			removeClass(info, "hide");
		}else{
			addClass(info, "hide");
		}
	}

	//每次添加一个class
	function addClass(currNode, newClass) {
		var oldClass;
		oldClass = currNode.getAttribute("class")
				|| currNode.getAttribute("className");
		if (oldClass != null) {
			newClass = oldClass + " " + newClass;
		}
		currNode.className = newClass; //IE 和FF都支持
	}

	//每次移除一个class
	function removeClass(currNode, curClass) {
		var oldClass, newClass1 = "";
		oldClass = currNode.getAttribute("class")
				|| currNode.getAttribute("className");
		if (oldClass !== null) {
			oldClass = oldClass.split(" ");
			for ( var i = 0; i < oldClass.length; i++) {
				if (oldClass[i] != curClass) {
					if (newClass1 == "") {
						newClass1 += oldClass[i]
					} else {
						newClass1 += " " + oldClass[i];
					}
				}
			}
		}
		currNode.className = newClass1; //IE 和FF都支持
	}

	//检测是否包含当前class
	function hasClass(currNode, curClass) {
		var oldClass;
		oldClass = currNode.getAttribute("class")
				|| currNode.getAttribute("className");
		if (oldClass !== null) {
			oldClass = oldClass.split(" ");
			for ( var i = 0; i < oldClass.length; i++) {
				if (oldClass[i] == curClass) {
					return true;
				}
			}
		}
		return false;
	}
</script>
</html>

<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>我要代泊</title>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=11" />
<style type="text/css">
#scroller li {
	padding: 0 10px;
	height: 50px;
	line-height: 50px;
	border-bottom: 1px solid #ccc;
	border-top: 1px solid #fff;
	background-color: #fafafa;
	font-size: 14px;
}

li {
	background-image: url(images/wxpublic/arrow.png);
	background-size: 19px 39px;
	background-repeat: no-repeat;
	background-position: right center;
	-webkit-border-bottom-left-radius: 10px;
	border-bottom-left-radius: 10px;
	-webkit-border-bottom-right-radius: 10px;
	border-bottom-right-radius: 10px;
	border-top: 0;
	-webkit-border-top-left-radius: 10px;
	border-top-left-radius: 10px;
	-webkit-border-top-right-radius: 10px;
	border-top-right-radius: 10px;
	margin-top: 5px;
	margin-left: 2%;
	margin-right: 2%;
}


a{
	text-decoration:none;
	color:#6D6D6D;
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
    text-align:center;
}
.cdiv{font-size:12px;line-height:50px;margin:20px auto;width:200px;text-align:center;}

</style>

<script src="js/tq.js" type="text/javascript">//JS</script>

</head>
<body style="background-color:#EEEEEE;">

<div id="" style='color:#747474;font-size:18px;line-height:40px;margin-top:15px;font-weight:700'>
	<div id='message_title'>&nbsp;&nbsp;&nbsp;正在获取您的位置，请稍候...</div>
</div>
<div id="wrapper">
<div id="noparks" style="display:none;width:100%;margin:10px auto;text-align:center;">
	<img class="error" src="images/wxpublic/error.png" width='200px;'/>
	<div style="text-align:center;font-size:16px;color:#B0AEAD;">您所在的城市还没有开通泊车业务！</div>
</div>
<div id="loading" style='width:280px;margin:10px auto'>
<img src="images/wxpublic/busy.gif"  border='0' style='width:275px;height:200px'/>
</br>
</br>
<div style='width:100px;margin:30px auto;font-size:18px;'>正在加载...</div></div>
	<div style="margin-top:5%;" id="scroller">
		<ul id="thelist">&nbsp;
		</ul>
	</div>
</div>
<div id="footer">

</body>
<script type="text/javascript" src="http://api.map.baidu.com/api?v=2.0&ak=GK54pXUffolC3ijXRIavHS5R"></script>

<script type="text/javascript">
//var cartops = ${carstops};
function initCarstops(carstops){
	var cdiv = document.getElementById('thelist');
	if(carstops=='-1'||carstops=='-2'){
		document.getElementById("noparks").style.display='';
		document.getElementById('message_title').innerHTML='';
	}else if(carstops&&carstops.length>0){
		for(var i=0;i<carstops.length;i++){
			cdiv.innerHTML +="<li><a href='#' onclick='viewstops(\""+carstops[i].id+"\")'><div>"+carstops[i].name+"</div></a></li>";
		}
		document.getElementById('message_title').innerHTML='&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;请选择您要去的停车宝服务点';
	}else{
		document.getElementById("noparks").style.display='';
		document.getElementById('message_title').innerHTML='';
	}
		
}
var lng ;
var lat ;
function viewstops(id){
	location='attendant.do?action=stopdetail&id='+id+'&lng='+lng+'&lat='+lat+'&uin=${uin}';
}

function getBMapLocation(){
	// 百度地图API功能
	var geolocation = new BMap.Geolocation();
	geolocation.getCurrentPosition(function(r) {
		if (this.getStatus() == BMAP_STATUS_SUCCESS) {
			//alert('您的位置：' + r.point.lng + ',' + r.point.lat);
			lng= r.point.lng;
			lat = r.point.lat;
			showPosition();
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

function showPosition() {
	//alert(lat+","+lng);
	if(lng){
		loadstops();//initCarstops();
		document.getElementById('loading').style.display='none';
	}
	//postLocation(lat,lon);
}

function showError(error) {
	alert("请允许获取您的地理位置！");
}

function loadstops(){
	var stops = eval(T.A.sendData("attendant.do?action=getstopsbyll&lng="+lng+"&lat="+lat));
	initCarstops(stops);
}


getBMapLocation();

</script>
</html>

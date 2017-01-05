<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>上传认证照片</title>
<script type="text/javascript"> 
		var ua = navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) != "micromessenger"){
			window.location.href = "http://s.tingchebao.com/zld/error.html";
		}
	</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=12" />
<script src="js/jquery.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
<style type="text/css">
#scroller .li1 {
    padding:0 10px;
    height:50px;
    line-height:50px;
    border-bottom:1px solid #EBEBEB;
    border-top:1px solid #EBEBEB;
    background-color:white;
    font-size:14px;
}

.li2 {
    padding:0 10px;
    height:180px;
    border-bottom:1px solid #EBEBEB;
    border-top:1px solid #EBEBEB;
    background-color:white;
    font-size:14px;
    margin-top:20px;
}

a{
	text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
	
	position: relative;
	top:-35px;
	left:30px;
}

li{
	border-top: 0;
	margin-top: 5px;
}
.img1{
	width:20px;
	height:20px;
	margin-top:15px;
}
.img2{
	width:80px;
	height:80px;
	margin-top:5px;
}
</style>

<style type="text/css">
.carnumber{
	margin-left:10px;
	border-width:0;
	font-size:16px;
}

.wx_pay{
	border-radius:3px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:20px;
	font-size:15px;
	background-color:#38B074;
	color:white;
	border: 1px solid #F0F0F0;
}

.wx_public{
	border-radius:3px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:20px;
	font-size:15px;
	background-color:white;
	border:1px solid #00A55D;
}

.picagain{
	width:47%;
	margin-left:2%;
	height:40px;
	margin-top:20px;
	font-size:15px;
	background-color:white;
	border:1px solid #38B074;
	border-bottom-left-radius: 3px;
	border-top-left-radius: 3px;
}
.preview{
	border-bottom-right-radius: 3px;
	border-top-right-radius: 3px;
	width:48%;
	height:40px;
	margin-top:20px;
	font-size:15px;
	background-color:white;
	border:1px solid #38B074;
}

.hide{
	display:none;
}

.error {
	color: red;
	font-size: 15px;
	margin-top:20px;
}
</style>
</head>
<body style="background-color:#EEEEEE;">
<div id="wrapper" style="margin-top:-30px;">
<form method="post" role="form" action="wxpaccount.do?action=tocarnumber&openid=${openid}" id="checkform">
	<div id="scroller">
		<ul id="thelist">
			<li class="li1"><img class="img1" src="images/wxpublic/carnumber1.png" /><a href="#"><div class="company_name"><span>我的车牌</span><input type="text" placeholder="请输入车牌号" maxlength="7" id="carnumber" class="carnumber" value="${carnumber}" /></div></a></li>
			<li class="li2">
				<div style="margin-top:15px;color:#696969;font-size:30px;">上传行驶证照</div>
				<div style="margin-top:25px;color:#B8B8B8;">1.车牌号需清晰可见，其余可挡</div>
				<div style="margin-top:10px;color:#B8B8B8;">2.需拍摄两种不同遮挡方式的照片</div>
				<div style="margin-top:10px;color:#B8B8B8;">3.盗用/伪造行驶证，手机号、帐号永久停用</div>
				<div style="margin-top:10px;color:#38B074;float:right;margin-right:5px;" onclick='example();'>查看示例照片></div>
			</li>
			<input type="button" id="picture" class="wx_pay" onclick='choosepic();' value="拍照" />
			<input type="button" id="picagain" class="picagain hide" onclick='choosepic();' value="重新拍照" />
			<input type="button" id="preview" class="preview hide" onclick='previewpic();' value="预览图片" />
			<input type="button" id="upload" class="wx_public" onclick='check();' value="提交" />
			
			<div style="text-align:center;" id="error" class="error"></div>
		</ul>
	</div>
</form>
</div>

<div id="footer"></div>
<script type="text/javascript">
	wx.config({
	    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
	    appId: '${appid}', // 必填，公众号的唯一标识
	    timestamp:'${timestamp}', // 必填，生成签名的时间戳
	    nonceStr: '${nonceStr}', // 必填，生成签名的随机串
	    signature: '${signature}',// 必填，签名，见附录1
	    jsApiList: [
	    	'checkJsApi',
	        'onMenuShareTimeline',
	        'onMenuShareAppMessage',
	        'onMenuShareQQ',
	        'onMenuShareWeibo',
	        'hideMenuItems',
	        'showMenuItems',
	        'hideAllNonBaseMenuItem',
	        'showAllNonBaseMenuItem',
	        'translateVoice',
	        'startRecord',
	        'stopRecord',
	        'onRecordEnd',
	        'playVoice',
	        'pauseVoice',
	        'stopVoice',
	        'uploadVoice',
	        'downloadVoice',
	        'chooseImage',
	        'previewImage',
	        'uploadImage',
	        'downloadImage',
	        'getNetworkType',
	        'openLocation',
	        'getLocation',
	        'hideOptionMenu',
	        'showOptionMenu',
	        'closeWindow',
	        'scanQRCode',
	        'chooseWXPay',
	        'openProductSpecificView',
	        'addCard',
	        'chooseCard',
	        'openCard'
	        ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
	});
	
	var images = {
	    localId: [],
	    serverId: []
  	};
  	var index = 0;
	function choosepic(){
		wx.chooseImage({
		    count: 1, // 默认9
		    sizeType: ['original', 'compressed'], // 可以指定是原图还是压缩图，默认二者都有
		    sourceType: ['camera'], // 可以指定来源是相册还是相机，默认二者都有
		    success: function (res) {
		        images.localId = res.localIds; // 返回选定照片的本地ID列表，localId可以作为img标签的src属性显示图片
		        for(var i=0; i<images.localId.length; i++){
		        	uploadpic(images.localId[i]);
		        }
		    }
		});
	}
	
	function uploadpic(localid){
		wx.uploadImage({
		    localId: localid, // 需要上传的图片的本地ID，由chooseImage接口获得
		    isShowProgressTips: 1, // 默认为1，显示进度提示
		    success: function (res) {
		    	if(images.serverId.length >= 2){
		    		images.localId =[];
		    		images.serverId = [];
		    	}
		        images.serverId.push(res.serverId);
		        var preview = document.getElementById("preview");
	        	var picagain = document.getElementById("picagain");
	        	var picture = document.getElementById("picture");
	        	var upload = document.getElementById("upload");
		        if(images.serverId.length == 1){
		        	picture.value = "再拍一张";
		        	removeClass(picture, "hide");
		        	addClass(preview, "hide");
		        	addClass(picagain, "hide");
		        	addClass(upload, "hide");
		        }else if(images.serverId.length == 2){
		        	removeClass(preview, "hide");
		        	removeClass(picagain, "hide");
		        	upload.style.backgroundColor = "#38B074";
		        	upload.style.color = "white";
		        	removeClass(upload, "hide");
		        	addClass(picture, "hide");
		        }
		    }
		});
	}
	
	function previewpic(){
		wx.previewImage({
	      current: 'http://${domain}/zld/wxpaccount.do?action=previewpic&serverid='+images.serverId[0],
	      urls: [
	      	'http://${domain}/zld/wxpaccount.do?action=previewpic&serverid='+images.serverId[0],
	        'http://${domain}/zld/wxpaccount.do?action=previewpic&serverid='+images.serverId[1]
	      ]
	    });
	}
	
	function example(){
		wx.previewImage({
	      current: 'http://${domain}/zld/images/wxpublic/tishi.png',
	      urls: [
	      	'http://${domain}/zld/images/wxpublic/tishi.png'
	      ]
	    });
	}
	
	function uploadcnum(carnumber){
		jQuery.ajax({
				type : "post",
				url : "wxpaccount.do",
				data : {
					'openid' : '${openid}',
					'carid' : '${carid}',
					'carnumber' : carnumber,
					'action' : 'upload',
					'serverid' : images.serverId.join(",")
				},
				async : false,
				success : function(result) {
					if(result == "-1"){
						document.getElementById("error").innerHTML = "请重新提交";
					}else if(result == "-2"){
						document.getElementById("error").innerHTML = "该车牌已被注册<br>在公众号内点击【联系客服】解决";
					}else if(result == "-3"){
						document.getElementById("error").innerHTML = "您已注册该车牌";
					}else if(result == "-4"){
						document.getElementById("error").innerHTML = "最多添加三个车牌";
					}else{
						$("#checkform")[0].submit();
					}
				}
			});
	}
	
	function check(){
			var car_number = document.getElementById("carnumber").value;
			car_number = car_number.toUpperCase();
			var city = car_number.charAt(0);
			var array = new Array( "京", "沪", "浙", "苏", "粤", "鲁",
						"晋", "冀", "豫", "川", "渝", "辽", "吉", "黑", "皖", "鄂", "湘", "赣",
						"闽", "陕", "甘", "宁", "蒙", "津", "贵", "云", "桂", "琼", "青", "新",
						"藏", "港", "澳", "使", "军", "空", "海", "北", "沈", "兰","济", "南", "广", "成", "WJ", "警", "消", "边","水", "电", "林", "通" );  
			var m = /^[A-Z]{1}[A-Z_0-9]{5}$/;
			car_number_char = car_number.substr(1);
			if(array.toString().indexOf(city) > -1){
				if(city == "使"){
					m = /^[A-Z_0-9]{6}$/;
				}
				if(!car_number_char.match(m)){
					document.getElementById("error").innerHTML = "车牌号不正确";
					return false;
				}
			}else{
				document.getElementById("error").innerHTML = "车牌号不正确";
				return false;
			}
			car_number = encodeURI(car_number);
			if(images.serverId.length==0 || images.serverId.length==2){
				uploadcnum(car_number);
			}else{
				document.getElementById("error").innerHTML = "需上传两张图片";
			}
		} 
</script>
</body>
<script type="text/javascript">
			//每次添加一个class
			function addClass(currNode, newClass){
		        var oldClass;
		        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
		        if(oldClass !== null) {
				   newClass = oldClass+" "+newClass; 
				}
				currNode.className = newClass; //IE 和FF都支持
    		}
			
			//每次移除一个class
			function removeClass(currNode, curClass){
				var oldClass,newClass1 = "";
		        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
		        if(oldClass !== null) {
				   oldClass = oldClass.split(" ");
				   for(var i=0;i<oldClass.length;i++){
					   if(oldClass[i] != curClass){
						   if(newClass1 == ""){
							   newClass1 += oldClass[i]
						   }else{
							   newClass1 += " " + oldClass[i];
						   }
					   }
				   }
				}
				currNode.className = newClass1; //IE 和FF都支持
			}
			
			//检测是否包含当前class
			function hasClass(currNode, curClass){
				var oldClass;
				oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
				if(oldClass !== null){
					oldClass = oldClass.split(" ");
					for(var i=0;i<oldClass.length;i++){
					   if(oldClass[i] == curClass){
						   return true;
					   }
				   }
				}
				return false;
			}
</script>
</body>
</html>

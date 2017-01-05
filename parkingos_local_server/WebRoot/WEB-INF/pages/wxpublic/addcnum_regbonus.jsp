<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
	<title>三百多家车场通用的停车券，duang~</title>
	<script type="text/javascript">
		var ua = navigator.userAgent.toLowerCase();
		if (ua.match(/MicroMessenger/i) != "micromessenger"){
			window.location.href = "http://s.tingchebao.com/zld/error.html";
		}
	</script>
	<script src="js/jquery.js" type="text/javascript">//表格</script>
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
		    background-image: url(images/bonus/bg.png);
		    background-repeat:no-repeat;
		}
		.logo{
			width:100px;
			height:24px;
			margin:10px;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/logo.png);
		    background-repeat:no-repeat;
		}
		.cloud{
			width:220px;
			height:135px;
			margin:0px auto;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/cloud.png);
		    background-repeat:no-repeat;
		}
		.redmail{
			width:240px;
			height:243px;
			margin:0px auto;
			background-size: 100% 100%;
			background-position:bottom center;
		    background-color:none;
		    background-image: url(images/bonus/redmail.png);
		    background-repeat:no-repeat;
		}
		.word{
			color:#EEB84B;
			margin:0px auto;
			font-weight:700;
			font-size:13px;
			width:240px;
			text-align:center;
			padding-top:60px;
		}
		.phonenumber1{
			margin:22px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		.phonenumber2{
			margin:0px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		
		.telnumber{
			width: 160px;
			min-height: 30px;
			background:#fff;
			border:1px solid #ccc;
			border-right:none;
			border-radius: 5px;
		}
		
		.code{
			width: 100px;
			min-height: 30px;
			background:#fff;
			border:1px solid #ccc;
			border-right:none;
			
			-webkit-border-bottom-left-radius: 5px;
			border-bottom-left-radius: 5px;
			-webkit-border-top-left-radius: 5px;
			border-top-left-radius: 5px;
		}
		.bt{
			margin:8px auto 0px auto;
			width: 240px;
			text-align:center;
		}
		.next{
			text-align:center;
			width: 163px;
			min-height: 30px;
			color:#D28D43;
			font-weight:700;
			font-size:15px;
			background:#F5E758;
			border:1px solid #F9F188;
			border-right:none;
			border-radius: 5px;
		}
		.getcode{
			margin:8px auto 0px auto;
			text-align:center;
			width: 60px;
			min-height: 34px;
			color:#D28D43;
			font-weight:700;
			font-size:15px;
			background:#F5E758;
			border:1px solid #F9F188;
			border-right:none;
			
			-webkit-border-bottom-right-radius: 5px;
			border-bottom-right-radius: 5px;
			-webkit-border-top-right-radius: 5px;
			border-top-right-radius: 5px;
		}
		
		.info{
			display:none;
		}
	</style>
<script type="text/javascript">
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
			document.getElementById("pword").innerHTML = "车牌号不正确";
			return false;
		}
	}else{
		document.getElementById("pword").innerHTML = "车牌号不正确";
		return false;
	}
	car_number = encodeURI(car_number);
	var mobile = document.getElementById("mobile").value;
 	jQuery.ajax({
			type:"post",
			url:"carlogin.do",
			data:{'action':'addcar','mobile':mobile,'carnumber':car_number},
		    async:false,
		    success:function(result){
				if(result == -9){
					document.getElementById("pword").innerHTML = "该车牌号已被注册";
				}else if(result != 3){
						document.getElementById("pword").innerHTML = "绑定车牌失败";
				}else{
					$("#carnumberform")[0].submit();
				}
		      }
		}); 
} 

$(function () {
	$("#colsubmit").bind("click", function (){
		check();
	})
}) 
</script>
</head>
<body >
<div style='display:none'><img src="http://s.tingchebao.com/zld/images/bonus/weixilogo_300.png"/></div>
	<div class="logo"></div>
	<div class="cloud"></div>
	<div class="redmail" id="inputdiv">
		<div class="word" id='pword'>最后一步，输入车牌号</div>
		<form action="wxpaccount.do?action=tobonuspage" method="post" id="carnumberform">
			<input type="text" id="mobile" name="mobile" class="info" value="${mobile}">
			<input type="text" name="type" class="info" value="${type}">
			<div class="phonenumber1" ><input name="carnumber" class="telnumber" maxlength="7" id="carnumber" type="text" placeholder="请输入车牌号" onclick='movepage()'/></div>
			<div class="bt"><input id="colsubmit" class="next" value="打开礼包"></input></div>
		</form>
	</div>
</body>
<script language="javascript">
	function movepage(){
		setTimeout(function(){document.getElementById("inputdiv").scrollIntoView()},500);
	}

</script>
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
</html>

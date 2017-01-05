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
			height:280px;
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
			margin:35px auto 0px auto;
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
			border:0px solid #ccc;
			border-right:none;
			border-radius: 5px;
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
			border:0px solid #F9F188;
			border-right:none;
			border-radius: 5px;
		}
		.code_ios{
			width: 95px;
			min-height: 30px;
			background:#fff;
			border:0px solid #ccc;
			border-right:none;
			
			-webkit-border-bottom-left-radius: 5px;
			border-bottom-left-radius: 5px;
			-webkit-border-top-left-radius: 5px;
			border-top-left-radius: 5px;
		}
		.getcode_ios{
			margin:8px auto 0px auto;
			margin-left:2px;
			text-align:center;
			width: 51px;
			min-height: 28.5px;
			color:#D28D43;
			font-weight:700;
			font-size:14px;
			background:#F5E758;
			border:0px solid #F9F188;
			border-right:none;
			-webkit-border-bottom-right-radius: 5px;
			border-bottom-right-radius: 5px;
			-webkit-border-top-right-radius: 5px;
			border-top-right-radius: 5px;
		}
		
		.code_android{
			width: 100px;
			min-height: 30px;
			background:#fff;
			border:0px solid #ccc;
			border-right:none;
			
			-webkit-border-bottom-left-radius: 5px;
			border-bottom-left-radius: 5px;
			-webkit-border-top-left-radius: 5px;
			border-top-left-radius: 5px;
		}
		.getcode_android{
			margin:8px auto 0px auto;
			text-align:center;
			width: 58px;
			min-height: 30px;
			color:#D28D43;
			font-weight:700;
			font-size:14px;
			background:#F5E758;
			border:0px solid #F9F188;
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
		$(document).ready(function() {
			var userAgent = navigator.userAgent.toLowerCase();
			var code = document.getElementById("code");
			var getcode = document.getElementById("getcode");
			if(userAgent.match(/iphone os/i) == "iphone os"){
				removeClass(code,"code_android");
				removeClass(getcode,"getcode_android");
				addClass(code,"code_ios");
				addClass(getcode,"getcode_ios");
			}else{
				removeClass(code,"code_ios");
				removeClass(getcode,"getcode_ios");
				addClass(code,"code_android");
				addClass(getcode,"getcode_android");
			}
	});
</script>
</head>
<body >
<div style='display:none'><img src="http://s.tingchebao.com/zld/images/bonus/weixilogo_300.png"/></div>
	<div class="logo"></div>
	<div class="cloud"></div>
	<div class="redmail" id="inputdiv">
		<div class="word" id='pword'></div>
		<form action="wxpaccount.do?action=regbonusbind" method="post" id="carownerform">
			<input type="text" name="openid" class="info" value="${openid}">
			<input type="text" id="regtype" name="type" class="info" value="">
			<div class="phonenumber1" ><input name="mobile" class="telnumber" maxlength="11" id="mobile" type="tel" placeholder="请输入手机号" onkeypress="return IsNum(event)" onclick='movepage()'/></div>
			<div class="phonenumber2" ><input name="code" class="code_android" id="code" type="tel" placeholder="请输入验证码" onclick='movepage()'/><input id="getcode" readonly="true" class="getcode_android" value="获取"></input></div>
			<div class="bt"><input id="colsubmit" readonly="true" class="next" value="下一步"></input></div>
		</form>
	</div>
</body>
<script language="javascript">
	function movepage(){
		setTimeout(function(){document.getElementById("inputdiv").scrollIntoView()},500);
	}

</script>
<script type="text/javascript">
function check(){
	var mobile = document.getElementById("mobile").value;
	var m = /^[1][3,4,5,7,8][0-9]{9}$/; 
	if(mobile.length!=11||!mobile.match(m)){
		document.getElementById("pword").innerHTML = "手机号码不正确";
		return false;
	}
	var code = document.getElementById("code").value;
	if(code==""){
		document.getElementById("pword").innerHTML = "请输入验证码";
		return false;
	}
	jQuery.ajax({
			type:"post",
			url:"carlogin.do",
			data:{'action':'validcode','mobile':mobile,'code':code},
		    async:false,
		    success:function(result){
				if(!(result == 1 || result == 2)){
					document.getElementById("pword").innerHTML = "验证码不正确,请重新获取";
				}else{
					$("#carownerform")[0].submit();
				}
		      }
		});
}

function IsNum(e) {
     var k = window.event ? e.keyCode : e.which;
     if (((k >= 48) && (k <= 57)) || k == 8 || k == 0) {
     } else {
     	if (window.event) {
        	window.event.returnValue = false;
        }
        else {
            e.preventDefault(); //for firefox 
        }
      }
} 

$(function () { 
	$("#getcode").bind("click", function () { 
		if(hasClass(document.getElementById("getcode"),"wait")){
			return false;
		}
		var mobile = document.getElementById("mobile").value;
		var m = /^[1][3,4,5,7,8][0-9]{9}$/; 
		if(mobile == ""){
			document.getElementById("pword").innerHTML = "请输入手机号码";
			return false;
		}
		if(mobile.length!=11||!mobile.match(m)){
			document.getElementById("pword").innerHTML = "手机号码不正确";
			return false;
		}
		document.getElementById("getcode").style.background = "#888888";
		addClass(document.getElementById("getcode"),"wait");
		setTimeout(function(){
			document.getElementById("getcode").style.background = "#F5E758";
			removeClass(document.getElementById("getcode"),"wait");
		},60000);
		jQuery.ajax({
			type:"post",
			url:"wxpaccount.do",
			data:{'action':'getcode','mobile':mobile,'media':'${media}','uid':'${uid}'},
		    async:false,
		    success:function(result){
				if(result == "0" || result == "1"){
					document.getElementById("regtype").value = result;
					document.getElementById("pword").innerHTML = "验证码已发送,请注意查收";
				}else{
					document.getElementById("getcode").style.background = "#F5E758";
					removeClass(document.getElementById("getcode"),"wait");
					document.getElementById("pword").innerHTML = "获取验证码失败,请重新获取";
				}
		      }
		});
	}) 
	
	$("#colsubmit").bind("click", function (){
		check();
	})
}) 
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

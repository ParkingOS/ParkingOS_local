<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>收费员注册</title>
<style type="text/css">
	.body {background:#F0F0F0;}
	.collector {width:100%;height:38px;text-indent:10px;font-size:15px;border:1px solid #F0F0F0;margin-top:5px;}
	.code {width:70%;height:38px;text-indent:10px;font-size:15px;border:1px solid #F0F0F0;margin-top:5px;margin-top:5px;}
	.getcode {width:25%;height:41px;border:1px solid #F0F0F0;position:absolute;right:2.5%;margin-top:5px;background-color:#27c766;color:white;font-size:15px;}
	.colsubmit {width:70%;height:41px;border:1px solid #F0F0F0;margin-top:50px;background-color:#27c766;color:white;font-size:15px;}
	.error {color:red;font-size:15px;}
	.info {display:none;}
	.logo {text-align:right;margin-right:5px;}
	.toptitle {color:#666;font-size:20px;}
	.fixed-bottom{
		    position: fixed; bottom:10px; _top:expression(eval(document.documentElement.scrollTop+document.documentElement.clientHeight-this.offsetHeight-(parseInt(this.currentStyle.marginTop,10)||0)-(parseInt(this.currentStyle.marginBottom,10)||0)-10));right:5px; 
		}
	.fixed{
	    _position: absolute; _bottom: auto;
	}
</style>
<script src="js/jquery.js" type="text/javascript">//表格</script>
<script type="text/javascript">
function check(){
	var nickname = document.getElementById("nickname").value;
	nickname = encodeURI(nickname);
	var mobile = document.getElementById("mobile").value;
	if(nickname==""){
		document.getElementById("error").innerHTML = "请输入姓名";
		return false;
	}
	var m = /^[1][3,4,5,7,8][0-9]{9}$/; 
	if(mobile.length!=11||!mobile.match(m)){
		document.getElementById("error").innerHTML = "手机号码不正确";
		return false;
	}
	var code = document.getElementById("code").value;
	if(code==""){
		document.getElementById("error").innerHTML = "请输入验证码";
		return false;
	}
	jQuery.ajax({
			type:"post",
			url:"regparker.do",
			data:{'action':'validcode','mobile':mobile,'code':code},
		    async:false,
		    success:function(result){
				if(result != "1"){
					document.getElementById("error").innerHTML = "验证码不正确,请重新获取";
				}else{
					$("#collectform")[0].submit();
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
			document.getElementById("error").innerHTML = "请输入手机号码";
			return false;
		}
		if(mobile.length!=11||!mobile.match(m)){
			document.getElementById("error").innerHTML = "手机号码不正确";
			return false;
		}
		document.getElementById("getcode").style.background = "#888888";
		addClass(document.getElementById("getcode"),"wait");
		setTimeout(function(){
			document.getElementById("getcode").style.background = "#27c766";
			removeClass(document.getElementById("getcode"),"wait");
		},60000);
		jQuery.ajax({
			type:"post",
			url:"regparker.do",
			data:{'action':'getcode','mobile':mobile},
		    async:false,
		    success:function(result){
				if(result == "1"){
					document.getElementById("error").innerHTML = "验证码已发送,请注意查收";
				}else if(result == "-4" || result == "-5" || result == "-6"){
					document.getElementById("error").innerHTML = "该手机号已经注册过";
				}else{
					document.getElementById("error").innerHTML = "验证码获取失败,请重新获取";
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
		        var oldClass,newClass1;
		        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
		        if(oldClass !== null) {
				   newClass1 = oldClass+" "+newClass; 
				}
				currNode.className = newClass1; //IE 和FF都支持
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
</head>
<body style="background-color:#F0F0F0;">
<div style="width:99%;">
	<div style="text-align:center;margin-top:20px;"><b class='toptitle'>收费员注册</b></div>
	<div style="margin-top: 20px;">
		<form action="regparker.do?action=collectorreg" method="post" id="collectform">
			<input type="text" id="recomcode" name="recomcode" class="collector info" value="${recomcode}">
			<input type="text" placeholder="请输入姓名" id="nickname" name="nickname" class="collector">
			<input type="tel" placeholder="请输入手机号" id="mobile" name="mobile" maxlength="11" class="collector" onkeypress="return IsNum(event)">
			<input type="tel" placeholder="请输入验证码" id="code" class="code"><input type="button" id="getcode" value="获取" class="getcode">
			<div style="text-align:center;">
				<input type="button" id="colsubmit" value="提交" class="colsubmit">
			</div>
		</form>
	</div>
	<div style="text-align:center;margin-top:20px;">
		<span id="error" class="error"></span>
	</div>
	<div class="fixed-bottom fixed logo"><img src="images/client_menu_icons/tingchebao.png" style="width:150px;height:40px;"/></div>
</div>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta content="width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=no" name="viewport">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta content="black" name="apple-mobile-web-app-status-bar-style">
<meta content="telephone=no" name="format-detection">
<meta content="email=no" name="format-detection">
<title>领取停车券</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<script src="js/jquery.js" type="text/javascript"></script>
<link rel="stylesheet" href="css/prepay.css?v=3">
<style type="text/css">
.error {
	color: red;
	font-size: 15px;
	margin-top:5%;
}
.info{
	display:none;
}
input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
    -webkit-appearance: none;
}
.wx_pay{
	border-radius:5px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:5%;
	font-size:15px;
	background-color:#04BE02;
	color:white;
}

.wx_public{
	border-radius:5px;
	width:96%;
	margin-left:2%;
	height:40px;
	margin-top:3%;
	font-size:15px;
	background-color:white;
}
.noorder{
	text-align:center;
	color:red;
	margin-top:55%;
}
</style>
</head>
<body>
	<div class="ticketpics" style="margin-left:5%;margin-top: 20px;">
		<div>
			<img src="images/wxpublic/ticket_limit.png" style="width:80px;height:80px;"/>
			<span style="line-height:45px;">成功领取${nickname}(${uid})的车场专用券</span>
		</div>
	</div>
	
	<section class="main">
		<form method="post" action="" role="form" class="form lpn-form" id="payform">
			<fieldset>
				<div class="input-area">
					<dl class="form-line">
						<dt class="label">专用券金额：</dt>
						<dd class="element lpn-element">
							<span class="arrow-down"><!-- 图标 --></span>

							<input readOnly="true" class="text" style="color:#04BE02;" type="text"  value="${ticket_money}元">
						</dd>
					</dl>
					
					<dl class="form-line">
						<dt class="label">可使用停车场：</dt>
						<dd class="element lpn-element">
							<span class="arrow-down"><!-- 图标 --></span>

							<input readOnly="true" class="text" style="color:#04BE02;" type="text" value="${cname}">
						</dd>
					</dl>
				</div>
				<input type="button" class="wx_pay" onclick='check("t");' value="代金券详情" />
				<input type="button" class="wx_public" onclick='check("p");' value="进入公众号" />
			</fieldset>
		</form>
		<div style="text-align:center;" id="error" class="error"></div>
	</section>
	<section class="noorder hide">
		<div>该二维码已失效</div>
	</section>
	<script type="text/javascript">
		var codeflag = "${codeflag}";//是否绑定账户
		if(codeflag == "1"){
			$(".main").addClass("hide");
			$(".ticketpics").addClass("hide");
			$(".noorder").removeClass("hide");
		}
		
		function check(target){
			if(target == "t"){
				window.open("${url}");
			}else if(target == "p"){
				window.open("http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205938292&idx=1&sn=76c6259270d762df187a187fac9e9a8d#wechat_redirect");
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
</html>

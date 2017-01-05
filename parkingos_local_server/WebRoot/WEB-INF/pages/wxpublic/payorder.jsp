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
<title>当前订单</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<script src="js/jquery.js" type="text/javascript"></script>
<link rel="stylesheet" href="css/prepay.css?v=1">
<style type="text/css">
.error {
	color: red;
	font-size: 15px;
	margin-top:5%;
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
.noorder{
	text-align:center;
	color:red;
	margin-top:55%;
}
</style>

</head>
<body>&nbsp; 
	<!-- 计费方式弹出层[[ -->
	<div class="mask billing-mask hide">
		<div class="content">
			<div class="billing-title">停车费计费方式</div>

			<ul class="info-list billing-list">
				<li class="list"><span class="list-title">第 1 小时收费</span><span class="list-content">10.00 元</span></li>
				<li class="list"><span class="list-title">超出 1 小时收费</span><span class="list-content">1×5.00 元</span></li>
				<li class="list totle-list"><span class="list-title">合计</span><span class="list-content"><span class="green-font">15.00</span> 元</span></li>
			</ul>

			<dl class="rules">
				<dt class="rules-title">收费规则</dt>
				<dd class="rules-main">周一至周四&nbsp;8&nbsp;时至&nbsp;22&nbsp;时及周五&nbsp;8&nbsp;时至&nbsp;18&nbsp;时，5&nbsp;元&nbsp;/&nbsp;小时 ; 周五&nbsp;18&nbsp;时至&nbsp;22&nbsp;时，周六、日及节假日&nbsp;8&nbsp;时至&nbsp;22时，10&nbsp;元&nbsp;/&nbsp;小时; 全周&nbsp;22&nbsp;时至次日&nbsp;8&nbsp;时，4&nbsp;元&nbsp;/&nbsp;小时，最高限价&nbsp;10&nbsp;元; 24&nbsp;小时最高限价&nbsp;80&nbsp;元&nbsp;/&nbsp;天。</dd>
			</dl>

			<a href="#" class="btn btn-white close-btn">关&nbsp;&nbsp;闭</a>
		</div>
		
	</div>
	<!-- 计费方式弹出层]] -->

	<section class="main" id="orderinfo" style="margin-top:-20px;">
		<form method="post" action="wxpfast.do?action=beginprepay" role="form" id="prepayform" class="confirm">
			<fieldset>
			<div class="info-area">	
				<dl class="totle">
					<dt class="totle-title">停车费</dt>
					<dd class="totle-num">￥${total}</dd>
				</dl>
				<ul class="info-list">
					<li class="list"><span class="list-title">已停时长</span><span class="list-content">${parktime}</span></li>
					<li class="list"><span class="list-title">入场时间</span><span class="list-content">${start_time}</span></li>
					<li class="list"><span class="list-title car_number hide">车牌号码</span><span class="list-content">${car_number}</span></li>
				</ul>
				
				<ul class="info-list hide">
					<li class="list"><input id="openid" name="openid" value="${openid}" /></li>
					<li class="list"><input id="orderid" name="orderid" value="${orderid}"></li>
					<li class="list"><input type="text" id="total" name="total" value="${total}"></li>
					<li class="list"><input type="text" name="uid" value="${uid}"></li>
					<li class="list"><input type="text" id="paytype" name="paytype" value="1"></li>
				</ul>
			</div>
			<input type="button" id="wx_pay" class="wx_pay" value="支付">
			<div class="tips"></div>
			</fieldset>
		</form>
		<div style="text-align:center;" id="error" class="error"></div>
		<div class="wxpay-logo"></div>
	</section>
	<section class="noorder hide">
		<div>当前无订单</div>
	</section>
</body>
<script type="text/javascript">
//	alert("进来了");
	var orderid= "${orderid}";
	var bind_flag = "${bind_flag}";
	var car_number="${car_number}";
	var total = "${total}";
//	alert("车牌号:"+car_number);
	if(orderid == "-1"){
		$(".main").addClass("hide");
		$(".noorder").removeClass("hide");
	}
	if(car_number != ""){
		$(".car_number").removeClass("hide");
	}
	$(".wx_pay").bind("click", function(){
		if(hasClass(document.getElementById("wx_pay"),"wait")){
			return;
		}
		addClass(document.getElementById("wx_pay"),"wait");
		$("#prepayform")[0].submit();
	});
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

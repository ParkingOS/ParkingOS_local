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
<link rel="stylesheet" href="css/prepay.css?v=2">
<style type="text/css">
.error {
	color: red;
	font-size: 15px;
	margin-top:5%;
}
.noorder{
	text-align:center;
	color:red;
	margin-top:55%;
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
</style>
 <style type="text/css">
<!--
ul{ margin:0; padding:0; list-style-type:none;} 
ul#navlist{font:12px verdana;padding-bottom: 13px;}
ul#navlist li span{ background: #FBFBFB;}
ul#navlist li{float: left; height: 30px; border: 0px solid red; width: 100%;}
ul#navlist .list1{border-bottom: 1px solid red;width: 100%;margin-bottom: -15px;}
#navlist a{display: block;color: red;text-decoration: none;padding: 8px 5px;width: 100%;text-align: center;}
-->
</style>
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
</head>
<body>
	<!-- 我的车牌[[ -->
		<dl class="my-lpn hide">
			<dt class="title">我的车牌号码</dt>
			<dd class="lpn">${car_number}<a class="change-btn" href="wxpfast.do?action=sweepcom&comid=${comid}&openid=${openid}&bind_flag=${bind_flag}&change=1">修改</a></dd>
		</dl>
		<!-- 我的车牌]] -->
	<section class="main">
		
		<form method="post" action="wxpfast.do?action=beginprepay" role="form" id="prepayform" class="confirm">
			<fieldset>
			<div class="info-area">	
				<dl class="totle" style="border-bottom:0px">
					<dt class="totle-title">预付停车费</dt>
					<dd class="totle-num" style="color:#04BE02;">￥<span id="ttotal">${total}</span></dd>
					<ul class="nfc" id="navlist">
						<li class="list1"></li>
						<li class="list2"><a href=""><span>预付完成后，再将卡给收费员结算，享折扣</span></a></li>
					</ul>
					<div class="nonfc hide" style="border-bottom: 1px solid #E0E0E0;"></div>
				</dl>
				
				<ul class="info-list" style="padding-top:1px;">
					<li class="list prepay hide"><span class="list-title">已预付金额</span><span class="list-content">${pretotal}元</span></li>
					<li class="list"><span class="list-title">已停时长</span><span class="list-content">${parktime}</span></li>
					<li class="list"><span class="list-title">入场时间</span><span class="list-content">${start_time}</span></li>
					<li class="list"><span class="list-title car_number hide">车牌号码</span><span class="list-content">${car_number}</span></li>
				</ul>
				
				<ul class="info-list hide">
					<li class="list"><input id="openid" name="openid" value="${openid}" /></li>
					<li class="list"><input id="ntime" name="ntime" value="${ntime}" /></li>
					<li class="list"><input id="orderid" name="orderid" value="${orderid}"></li>
					<li class="list"><input type="text" id="total" name="total" value="${total}"></li>
					<li class="list"><input type="text" name="uid" value="${uid}"></li>
				</ul>
			</div>
			<div class="leave" style="text-align:center;margin-top:20px;">
				我离到出口缴费还有：<select name="leaving_time"
					style="width:95px;height:25px;padding-top:3px;font-size:17px;padding-left:6px;color:#04BE02;"
					id="leaving_time">
					<option value="0">0分钟</option>
					<option value="5">5分钟</option>
					<option value="10">10分钟</option>
					<option selected="selected" value="15">15分钟</option>
					<option value="30">30分钟</option>
					<option value="60">1小时</option>
					<option value="120">2小时</option>
					<option value="240">4小时</option>
					<option value="360">6小时</option>
					<option value="480">8小时</option>
					<option value="600">10小时</option>
					<option value="720">12小时</option>
				</select>
			</div>
			<%--
			<a href="#" id="wx_pay" onclick='payorder();' class="btn btn-green">确认</a>
			--%>
			<input type="button" id="wx_pay" onclick='payorder();' class="wx_pay" value="去预付">
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

function getprice(){
	var leaving_time = document.getElementById("leaving_time").value;
//	alert("ticketid="+ticketid);
//	alert("订单："+orderid+",ntime:"+ntime+",预计"+leaving_time+"分钟之后离开");
	jQuery.ajax({
		type : "post",
		url : "wxpfast.do",
		data : {
			'orderid' : '${orderid}',
			'ntime' : '${ntime}',
			'leaving_time' : leaving_time,
			'action' : 'getprice'
		},
		async : false,
		success : function(result) {
			if(result == "-1"){//出错了
				document.getElementById("error").innerHTML = "获取停车费失败";
			}
			var jsonData = eval("(" + result + ")");
			var total = jsonData.total;//总金额

			document.getElementById("total").value = total;
			document.getElementById("ttotal").innerHTML = total;
			if(parseFloat(total) <= 0){//停车费0元
//				alert("预支付金额大于总金额");
				$(".wx_pay").addClass("hide");
				addClass(document.getElementById("wx_pay"),"wait");
				document.getElementById("error").innerHTML = leaving_time+"分钟之内离场免费";
				return false;
			}else{
				$(".wx_pay").removeClass("hide");
				removeClass(document.getElementById("wx_pay"),"wait");
				document.getElementById("error").innerHTML = "";
			}
		}
});
}
</script>
<script type="text/javascript">
	var leaving_time = document.getElementById("leaving_time").value;
	leaving_time = parseInt(leaving_time);
	document.getElementById("leaving_time").value = 15;
	if(leaving_time != 15){//页面返回时重新获取15分钟的价格
		getprice();
	}
</script>
<script type="text/javascript">
	var orderid = "${orderid}";
	if(orderid == "-1"){
		$(".main").addClass("hide");
		$(".noorder").removeClass("hide");
	}
	$("#leaving_time").bind("change", function(){
		getprice();
	});
</script>
<script type="text/javascript">
	var type="${type}";
	var bind_flag = "${bind_flag}";
	var car_number="${car_number}";
	var total = "${total}";
	var pre_state = "${pre_state}";
	var sweepcom_flag = "${sweepcom_flag}";
	var addtype = "${addtype}";
	if(sweepcom_flag == "1" && bind_flag == "0" || addtype == "1"){
		$(".my-lpn").removeClass("hide");
		$(".nfc").addClass("hide");
		$(".nonfc").removeClass("hide");
	}
	if(pre_state == "1"){
		addClass(document.getElementById("wx_pay"),"wait");
		$(".wx_pay").addClass("hide");
		$(".leave").addClass("hide");
		$(".prepay").removeClass("hide");
		$(".nfc").addClass("hide");
		$(".nonfc").removeClass("hide");
		document.getElementById("error").innerHTML = "您已预支付过，不能再次预支付";
	}
	
	if(parseFloat(total) <= 0){
		addClass(document.getElementById("wx_pay"),"wait");
		document.getElementById("error").innerHTML = "15分钟之内离场免费";
	}
//	alert("车牌号:"+car_number);
	if(car_number != ""){
		$(".car_number").removeClass("hide");
	}
	if(type != "0"){
		$(".wx_pay").addClass("hide");
		$(".leave").addClass("hide");
		addClass(document.getElementById("wx_pay"),"wait");
		document.getElementById("error").innerHTML = "这不是您的订单";
	}
	
	function payorder(){
		if(hasClass(document.getElementById("wx_pay"),"wait")){
			return;
		}
		addClass(document.getElementById("wx_pay"),"wait");
		$("#prepayform")[0].submit();
	}
</script>

</html>

<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
	<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, minimum-scale=1.0, maximum-scale=1.0"/>
	<title>车主${carnumber}的礼包</title>
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
		    background-repeat:no-repeat;
		}
		._top{
		    width:100%;
		    height:43%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bonus/wx_cai_top.jpg);
		    background-repeat:no-repeat;
		}
		.middle{
			width:100%;
			height:22%;
			 overflow-x:hidden !important;
		}
		.buttom{
			width:100%;
			height:35%;
			background-color:#f2f2f2;
		}
		._logo{
			width:80px;
			height:80px;
			margin:0px auto;
			margin-top:20px;
			background-size: 100% 100%;
			background-position:top center;
		    background-color:none;
		    background-image: url(images/bonus/ob_logo.png);
		    background-repeat:no-repeat;
		}
		._middle{
			margin:0px auto;
			font-weight:500;
			font-size:50px;
			text-align:center;
			padding-top:10px;
			font-weight:700;
			width:150px;
		}
		
		.word{
			margin:10px auto;
			font-size:26px;
			width:100%;
			text-align:center;
			color:#ffffff;
			font-weight:700;
			float:left;
		}
		.wword{
			margin:10px auto;
			font-size:24px;
			width:100%;
			text-align:center;
			color:yellow;
		}
		.word_mobile{
			margin:5px auto;
			font-size:24px;
			width:100%;
			text-align:center;
			color:#ee0000;
		}
		.topword{
			margin:0px auto;
			margin-top:10px;
			font-size:20px;
			width:300px;
			text-align:center;
			color:#fff;
		}
		.infoword{
			margin-top:2px;
			font-size:14px;
			color:#CFCFCF;
			margin-top:6px;
			margin-left:10px
		}
		.yuan{
			font-size:18px;
			float:right;
			margin-top:28px;
			background-size: 100% 100%;
		}
		.tipword{
			font-size:20px;
			margin-top:14px;
			color:#F1AC46;
			font-weight:700;
			text-align:center;
		}
		.quan{
			font-size:18px;
			float:right;
			width:95%;
			height:73px;
			margin-top:3px;
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bonus/in_mobile.png);
		    background-repeat:no-repeat;
		}
		.phonenumber{
			width:100%;
			text-align:center;
		}
		.phonenumber input{
			width: 154px;
			min-height: 30px;
			background:#fff;
			border:1px solid #ccc;
			border-radius: 5px;
			font-size:19px;
			color:#CFCFCF;
		}
	</style>
</head>
<body>
<div id='wx_pic' style='margin:0 auto;display:none;'>
<img src='images/bonus/order_bonu.png' />
</div>
	<div class="_top">
		<div class="topword"></div>
		<div class='_logo'></div>
		<div class="word">车主${carnumber}的礼包   <img src='images/bonus/zhe.png' height='24px' width='24px' valign='center'/></div>
		<div class="wword"><span id='bwords'>微信支付专享礼包</span></div>
	</div>
	<div class="middle"  id='inputdiv'>
		<div class="_middle" >${bonus_money}折券</div>
		<div class="word_mobile">下方框内输入手机号领取</div>
	</div>
	<div class="buttom" >
		<div style='height:1px'></div>
		<div class='infoword'>礼包个数：${haveget}/${bnum}</div>
		<div class="quan" >
			<div style='margin-top:12px;margin-left:10px;float:left;width:23%;'>
				<div class='tipword'>祝</div>
			</div>
			<div style='margin-top:18px;margin-left:10px;float:left;width:37%;'>
				<div class="phonenumber"><input  id="phonenumber" name='phonenumber' value='输入手机号'  type="tel" onclick='movepage()'/></div>
			</div>
			<div style='margin-top:19px;margin-left:10px;float:right;width:20%;' onclick='getbons()'>
				<img src='images/bonus/get_b.png' widht='50px' height='40px' />
			</div>
		</div>
		<!-- <div class="quan" >
			<div style='margin-top:10px;margin-left:20px;'><img src='images/bonus/quan.png' widht='50px' height='50px'/></div>
		</div> -->
	</div>
	<form action="carinter.do?action=getwxbonus&id=${id}" id ="subform" method="post">
		<input type='hidden' id ="operate" name="operate" value="caibonusret"/>
		<input type='hidden' id ="mobile" name="mobile" />
		<input type='hidden' id ="uin" name="uin" value="${uin}"/>
		<input type='hidden' id ="otdid" name="otdid" value="${otdid}"/>
		<input type='hidden' id ="money" name="money" value="${money}"/>
		<input type='hidden' id ="openid" name="openid" value="${openid}"/>
	</form>
</body>
<script>

</script>
<script language="javascript">

function getbons(){//查看礼包详情
	var mobile = document.getElementById("phonenumber").value;
	if(!checkMobile(mobile)){
		alert("手机号不合法!请重新输入");
		return ;
	}
	document.getElementById("mobile").value=mobile;
	document.getElementById("subform").submit();
	//location.href="carowner.do?action=getbonusbymobile&otdid=${otdid}&bid=${bid}&money=${money}&totalmoney=${totalmoney}&bnum=${bnum}&uin=${uin}&mobile="+mobile;
};
function movepage(){
	document.getElementById("phonenumber").value='';
	document.getElementById("phonenumber").style.color='#000000';
	setTimeout(function(){document.getElementById("inputdiv").scrollIntoView()},500);
}
function checkMobile(str) {
   var re = /^[1][3,4,5,7,8]\d{9}$/;
   if (re.test(str)) {
       return true;
   } else {
	   return false;
   }
}
//document.getElementById("phonenumber").focus();
</script>
</html>

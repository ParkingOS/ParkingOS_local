<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>停车宝礼包</title>
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
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bunusimg/bg_fly.png);
		    background-repeat:no-repeat;
		    position:absolute;
		}
		.wordimg{
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bunusimg/words.png);
		    background-repeat:no-repeat;
		    float:left;
		    text-align:center;
		    color:#FFFFFF;
		    position:absolute;
		}
		.ticketimg{
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bunusimg/ticket_l.png);
		    background-repeat:no-repeat;
		    text-align:center;
		    color:#FFFFFF;
		    position:absolute;
		}
		.input_img{
			background:#fff;
			border:1px solid #37b561;
			border-radius: 5px;
			color:#CFCFCF;
			position:absolute;
		}
		.getbtn{
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bunusimg/hit_plan.png);
		    background-repeat:no-repeat;
		 	position:absolute;
		}
		.ticklist{
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bunusimg/quan_fly.png);
		    background-repeat:no-repeat;
		}
	</style>
</head>
<body id='body'>
	<div class="_top" id='top'>
		<div id='head' style='position:absolute;'><img src="images/bunusimg/logo.png" id='headimg'/></div>
		<div id='wordimg' class="wordimg">需要输入手机号才能去打灰机~</div>
		<div id='ticket' class="ticketimg">
			<input id='inputd' class='input_img'  value='输入手机号领取' onclick='movepage();'/>
			<div id='dmoney' style='position:absolute;color:#37b561;display:none'><b>${tipwords}</b></div>
		</div>
		<div id='getbtn' class='getbtn' onclick='getbons();'></div>
		<div id='attbtn' style='position:absolute;''>
			<img src='images/bunusimg/toatt_fly.png' id='attimg'  onclick='attention();'/>
		</div>
		
		
		
	</div>
	<div id='middle' style='position:absolute;'>
		<img id='middleimg' img src='images/bunusimg/reamrk_bg.png'/>
	</div>
	
	<div id='view_detail' style='position:absolute;color:#51bf75'  onclick='view_detail();'>
			查看详情&nbsp;&nbsp;&nbsp;<img src='images/bunusimg/arrow_rig.png' width='7px;' id='arrow_rig' />
    </div>
	
	<div id='buttom' style='position:absolute;'><span>中国最好的停车应用--</span><span style='color:#51bf75;font-weight:700'>停车宝</span></div>
	
	<form action="flygame.do" id ="subform" method="post">
		<input type='hidden'   name="action" value="getticket"/>
		<input type='hidden'  id='mobile' name="mobile" />
		<input type='hidden'   name="openid" value="${openid}"/>
		<input type='hidden'   name="acc_token" value="${acc_token}"/>
	</form>
</body>

<script language="javascript">
var isover='${isover}';
function getobj(id){
	return document.getElementById(id)
}
var h = parseInt(document.getElementById('body').offsetHeight);
var w =  parseInt(document.getElementById('body').offsetWidth);

getobj('top').style.height=parseInt(h*0.42)+'px';
getobj('top').style.width=parseInt(w)+'px';

getobj('head').style.width=parseInt(w*0.06)+'px';
getobj('head').style.left=parseInt(w*0.04)+'px';
getobj('head').style.top=parseInt(w*0.04)+'px';
getobj('headimg').style.width=parseInt(w*0.078)+'px';
var imgurl = '${carowenurl}';
if(imgurl!='')
	getobj('headimg').src= '${carowenurl}';


getobj('wordimg').style.width=parseInt(w*0.75)+'px';
getobj('wordimg').style.height=parseInt(h*0.042)+'px';
getobj('wordimg').style.lineHeight=parseInt(h*0.038)+'px';
getobj('wordimg').style.left=parseInt(w*0.14)+'px';
getobj('wordimg').style.top=parseInt(w*0.04)+'px';

getobj('ticket').style.top=parseInt(h*0.12)+'px';
getobj('ticket').style.left=parseInt(w*0.149)+'px';
getobj('ticket').style.width=parseInt(w*0.55)+'px';
getobj('ticket').style.height=parseInt(h*0.15)+'px';


getobj('inputd').style.left=parseInt(w*0.16)+'px';
getobj('inputd').style.top=parseInt(h*0.045)+'px';
getobj('inputd').style.width=parseInt(w*0.30)+'px';
getobj('inputd').style.height=parseInt(h*0.05)+'px';
getobj('inputd').style.fontSize=parseInt(w*0.043)+'px';
getobj('getbtn').style.width=parseInt(w*0.11)+'px';
getobj('getbtn').style.height=parseInt(h*0.15)+'px';
getobj('getbtn').style.top=parseInt(h*0.12)+'px';
getobj('getbtn').style.right=parseInt(w*0.156)+'px';


getobj('attimg').style.width=parseInt(w*0.70)+'px';
getobj('attbtn').style.height=parseInt(h*0.10)+'px';
getobj('attbtn').style.left=parseInt(w*0.152)+'px';
getobj('attbtn').style.top=parseInt(h*0.295)+'px';

getobj('view_detail').style.left=parseInt(w*0.4)+'px';
getobj('view_detail').style.top=parseInt(h*0.655)+'px';
getobj('view_detail').style.width=parseInt(w*0.30)+'px';
getobj('view_detail').style.height=parseInt(h*0.05)+'px';
getobj('view_detail').style.fontSize=parseInt(w*0.043)+'px';
getobj('view_detail').zIndex=1000;

getobj('middle').style.top=parseInt(h*0.441)+'px';
getobj('middle').style.left=parseInt(w*0.04)+'px'
getobj('middleimg').style.width=parseInt(w*0.92)+'px'


getobj('buttom').style.top=parseInt(h*0.96)+'px';
getobj('buttom').style.width=parseInt(w)+'px';
getobj('buttom').style.fontSize=parseInt(w*0.032)+'px';
getobj('buttom').style.textAlign='center';



function getbons(){//查看礼包详情
	//getobj('getbtn').style.backgroundImage='url(images/bunusimg/toget_b.png)';
	
	var mobile = getobj("inputd").value;
	
	if(!checkMobile(mobile)){
		alert("手机号不合法!请重新输入");
		return ;
	}
	
	getobj("mobile").value=mobile;

	document.getElementById("subform").submit();
};
function movepage(){
	document.getElementById("inputd").value='';
	document.getElementById("inputd").style.color='#000000';
	//setTimeout(function(){document.getElementById("ticket").scrollIntoView()},500);
}
function checkMobile(str) {
   var re = /^[1][3,4,5,7,8]\d{9}$/;
   if (re.test(str)) {
       return true;
   } else {
	   return false;
   }
}
function view_detail(){
	//this.src='images/bunusimg/toatt_b.png';
	//location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205312557&idx=1&sn=1351e6dfc70b2929f11e1fcf21ba8ff0#rd';
	location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=209080514&idx=1&sn=a0951330910bf2c4e41fca840a562d7e#rd';
}

function attention(){
	//this.src='images/bunusimg/toatt_b.png';
	//location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205312557&idx=1&sn=1351e6dfc70b2929f11e1fcf21ba8ff0#rd';
	location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205938292&idx=1&sn=76c6259270d762df187a187fac9e9a8d#rd';
}


</script>
</html>

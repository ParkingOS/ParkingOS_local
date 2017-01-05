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
		    background-image: url(images/bunusimg/bg.jpg);
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
		    background-image: url(images/bunusimg/${tpic}.png);
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
		    background-image: url(images/bunusimg/togame.png);
		    background-repeat:no-repeat;
		 	position:absolute;
		}
		.ticklist{
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bunusimg/quan${fly}.png);
		    background-repeat:no-repeat;
		}
		.div1{float:left}
		.modify{
		 	border: 1px solid rgba(255,255,255,1);
		 	color:#FFFFFF;
		 }
		
	</style>
</head>
<body id='body'>
	<div class="_top" id='top'>
		<div id='head' style='position:absolute;'><img src="images/bunusimg/logo.png" id='headimg'/></div>
		<div id='wordimg' class="wordimg">恭喜您抢到我的${tname}礼包~</div>
		<div id='ticket' class="ticketimg">
			<div id='dmoney' style='position:absolute;color:#37b561'><b>${money} ${mname}</b></div>
			<input id='inputd' class='input_img'  value='输入新的手机号' onclick='movepage();'/>
			<div style='position:absolute;display:none' id='mobiletip'>手机号在下次抢礼包时生效</div>
		</div>
		<div id='getbtn' class='getbtn' onclick='togame();'></div>
		<div id='mesg' style='position:absolute;color:#FFFFFF' >已放入${mobile}账户中&nbsp; <a href='#' class='modify' onclick='preeditphone();'>&nbsp;修改>&nbsp;</a></div>
		<div id='attbtn' style='position:absolute;''>
			<img src='images/bunusimg/toatt${fly}.png' id='attimg'  onclick='attention();'/>
		</div>
	</div>
	<div id='middle' style='position:absolute;'>
		<img id='middleimg' img src='images/bunusimg/ticlist${fly}.png'/>
	</div>
	<div id='buttom'></div>
	<form action="flygame.do" method='post' id='togame'>
		<input type='hidden' id='action' name='action' value='pregame'/>
		<input type='hidden' name='uin' value='${uin}'/>
	</form>
</body>
<script src="js/tq.js?0817" type="text/javascript"></script>
<script language="javascript">


function setobjCss(obj,css){
	obj.style.width=css.width;
	obj.style.top=css.top;
	obj.style.left=css.left;
	obj.style.position=css.position;
	obj.style.height=css.height;
	obj.style.filter=css.filter;
	obj.style.cursor=css.cursor;
	obj.style.background=css.background;
	obj.style.visibility=css.visibility;
	obj.style.display=css.display;
	obj.style.opacity=css.opacity;
	obj.style.textAlign=css.textAlign;
	obj.style.fontSize=css.fontSize;
	obj.style.backgroundColor=css.backgroundColor;
	obj.style.overflowY=css.overflowY;
	obj.style.overflowX=css.overflowX;
	obj.style.borderRadius=css.borderRadius;
	obj.style.lineHeight=css.lineHeight;
	obj.style.border=css.border;
	obj.style.color=css.color;
	obj.style.paddingLeft=css.paddingLeft;
	obj.style.paddingRight=css.paddingRight;
	obj.style.borderBottom=css.borderBottom;
	obj.style.marginLeft=css.marginLeft;
	obj.style.fontWeight=css.fontWeight;
}

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

getobj('dmoney').style.left=parseInt(w*0.1)+'px';
getobj('dmoney').style.top=parseInt(h*0.048)+'px';
getobj('dmoney').style.width=parseInt(w*0.30)+'px';
getobj('dmoney').style.height=parseInt(h*0.05)+'px';
getobj('dmoney').style.fontSize=parseInt(w*0.073)+'px';

getobj('mesg').style.left=parseInt(w*0.3)+'px';
getobj('mesg').style.top=parseInt(h*0.274)+'px';
getobj('mesg').style.width=parseInt(w*0.50)+'px';
getobj('mesg').style.fontSize=parseInt(w*0.032)+'px';

getobj('getbtn').style.width=parseInt(w*0.11)+'px';
getobj('getbtn').style.height=parseInt(h*0.15)+'px';
getobj('getbtn').style.top=parseInt(h*0.12)+'px';
getobj('getbtn').style.right=parseInt(w*0.156)+'px';

getobj('attimg').style.width=parseInt(w*0.70)+'px';
getobj('attimg').style.height=parseInt(w*0.09)+'px';
getobj('attbtn').style.height=parseInt(h*0.091)+'px';
getobj('attbtn').style.left=parseInt(w*0.152)+'px';
getobj('attbtn').style.top=parseInt(h*0.31)+'px';

getobj('middle').style.top=parseInt(h*0.441)+'px';
getobj('middle').style.left=parseInt(w*0.04)+'px'
getobj('middleimg').style.width=parseInt(w*0.92)+'px'

getobj('inputd').style.display='none';
getobj('inputd').style.left=parseInt(w*0.16)+'px';
getobj('inputd').style.top=parseInt(h*0.034)+'px';
getobj('inputd').style.width=parseInt(w*0.30)+'px';
getobj('inputd').style.height=parseInt(h*0.05)+'px';
getobj('inputd').style.fontSize=parseInt(w*0.043)+'px';

getobj('mobiletip').style.left=parseInt(w*0.08)+'px';
getobj('mobiletip').style.top=parseInt(h*0.098)+'px';
getobj('mobiletip').style.width=parseInt(w*0.50)+'px';
getobj('mobiletip').style.height=parseInt(h*0.03)+'px';
getobj('mobiletip').style.fontSize=parseInt(w*0.033)+'px';
getobj('mobiletip').style.color='#${fontColor}';

//加载停车券
var data = eval('${data}');
var lh  = parseInt(h*0.146);
var t = parseInt(h*0.48);
if(data&&data.length>0){
	for(var i=0;i<data.length;i++){
		var dis = document.createElement("div");
		dis.className='ticklist';
		setobjCss(dis,{'top':parseInt(t+i*lh)+'px','left':parseInt(w*0.04)+'px','width':parseInt(w*0.92)+'px','height':(lh-12)+'px',
			'margin':'10px auto','position':'absolute','align':'center'});
		dis.zIndex = 1;
		
		var dimg =document.createElement("img");
		dimg.style.width=parseInt(w*0.12)+'px';
		dimg.style.paddingTop= parseInt(h*0.022)+'px';
		dimg.style.paddingLeft=  parseInt(w*0.04)+'px';
		dimg.src=data[i].wxurl;
		
		dis.appendChild(dimg);
		
		var unamediv = document.createElement("div");
		var unss = unamediv.style;
		unss.position ='absolute';
		unss.top = parseInt(t+i*lh+h*0.02)+'px';
		unss.left = parseInt(w*0.24)+'px';
		unss.width =  parseInt(w*0.7)+'px';
		unss.color =  '#${fontColor}';
		var fsize= parseInt(w*0.05)+'px';
		var f1size= parseInt(w*0.036)+'px';
		var wname = data[i].wxname;
		wname = wname.replace("'","");
		wname = wname.replace('"','');
		unamediv.innerHTML='<span style="font-size:'+fsize+'"><b>'+wname+'</b></span>&nbsp;&nbsp;<span style="font-size:'+f1size+'">'+data[i].ttime+'</font>';
		
		var titlediv = document.createElement("div");
		var tlcss = titlediv.style;
		tlcss.position ='absolute';
		tlcss.top = parseInt(t+i*lh+h*0.07)+'px';
		tlcss.left = parseInt(w*0.24)+'px';
		tlcss.width =  parseInt(w*0.62)+'px';
		tlcss.color =  '#${fontColor}';
		var ssize= parseInt(w*0.044)+'px';
		titlediv.innerHTML='<span style="font-size:'+ssize+'">${bwords}</span>';
		
		var moneydiv = document.createElement("div");
		var mss = moneydiv.style;
		mss.position ='absolute';
		mss.top = parseInt(t+i*lh+h*0.045)+'px';
		mss.left = parseInt(w*0.80)+'px';
		mss.width =  parseInt(w*0.12)+'px';
		mss.color =  '#${fontColor}';
		var msize= parseInt(w*0.044)+'px';
		//mss.font-size= parseInt(w*0.04)+'px';
		moneydiv.innerHTML='<span style="font-size:'+msize+'"><b>'+data[i].amount+' ${mname}</b></span>';
		
		document.body.appendChild(dis);
		document.body.appendChild(unamediv);
		//document.body.appendChild(datediv);
		document.body.appendChild(titlediv);
		document.body.appendChild(moneydiv);
		
		if(i==data.length-1){
			var buttmdiv = document.createElement("div");
			var mbss = buttmdiv.style;
			mbss.position ='absolute';
			mbss.top = parseInt(t+(i+1)*lh)+'px';
			mbss.left = parseInt(w*0.34)+'px';
			mbss.width =  parseInt(w*0.52)+'px';
			mbss.height =  parseInt(lh*0.5)+'px';
			mbss.color =  '#${fontColor}';
			var btsize= parseInt(w*0.038)+'px';
			buttmdiv.innerHTML='<span style="font-size:'+btsize+'">礼包个数：${haveget}/${bnum} ${btotal}</span>';
			
			
			var ggdis = document.createElement("div");
			ggdis.className='ticklist';
			setobjCss(ggdis,{'top':parseInt(t+(i+1.3)*lh)+'px','left':parseInt(w*0.04)+'px','width':parseInt(w*0.92)+'px','height':(lh-12)+'px',
				'margin':'10px auto','position':'absolute','align':'center'});
			ggdis.zIndex = 1;
			document.body.appendChild(ggdis);
			
			var gdimg =document.createElement("img");
			setobjCss(gdimg,{'top':parseInt(t+(i+1.3)*lh+h*0.02)+'px','left':parseInt(w*0.078)+'px','width':parseInt(w*0.12)+'px','position':'absolute'});
			gdimg.src='images/flygame/1818_logo.png';
			
			var gunamediv = document.createElement("div");
			var gunss = gunamediv.style;
			gunss.position ='absolute';
			gunss.top = parseInt(t+(i+1.3)*lh+h*0.02)+'px';
			gunss.left = parseInt(w*0.24)+'px';
			gunss.width =  parseInt(w*0.7)+'px';
			gunss.color =  '#${fontColor}';
			var gfsize= parseInt(w*0.05)+'px';
			var gf1size= parseInt(w*0.036)+'px';
			//gunamediv.innerHTML='<span style="font-size:'+fsize+'"><b>人人车</b></span>&nbsp;&nbsp;<span style="font-size:'+f1size+'">下载立得<font style="font-size:20px;font-weight:700" >5元</font>余额</font>';
			gunamediv.innerHTML='<span style="font-size:'+fsize+'"><b>1818平台</b></span>&nbsp;&nbsp;<span style="font-size:'+f1size+'">[合作推广]</font>';
			
			var gtitlediv = document.createElement("div");
			var gtlcss = gtitlediv.style;
			gtlcss.position ='absolute';
			gtlcss.top = parseInt(t+(i+1.26)*lh+h*0.07)+'px';
			gtlcss.left = parseInt(w*0.24)+'px';
			gtlcss.width =  parseInt(w*0.62)+'px';
			gtlcss.color =  '#${fontColor}';
			var gssize= parseInt(w*0.04)+'px';
			gtitlediv.innerHTML='<span style="font-size:'+gssize+'">108现金，关注投资即可提现</span>';
			
			var godown = document.createElement("span");
			setobjCss(godown,{'top':parseInt(t+(i+1.3)*lh+h*0.02)+'px','left':parseInt(w*0.77)+'px','width':parseInt(w*0.28)+'px',
				'position':'absolute','fontWeight':'600','color':'#FFFFFF','fontSize':parseInt(w*0.053)+'px'});
			var goh=lh*0.5+'px';
			var gow=w*0.16+'px';
			var btp = ${btype};
			var bcolor='#e14800';
			if(btp&&btp==5)
				bcolor='#51bf75';
			godown.innerHTML='<div onclick="godownapp()" style="background-color:'+bcolor+';width:'+gow+';height:'+goh+';border-radius:5px;color:#FFFFFF;line-height:'+goh+';text-align:center;font-weight:700" >关注</div>';
			
			document.body.appendChild(buttmdiv);
			document.body.appendChild(gdimg);
			document.body.appendChild(gunamediv);
			document.body.appendChild(gtitlediv);
			document.body.appendChild(godown);
			
		}
	}
}else{
	var buttmdiv = document.createElement("div");
	var mbss = buttmdiv.style;
	mbss.position ='absolute';
	mbss.top = parseInt(t+(1.2)*lh)+'px';
	mbss.left = parseInt(w*0.34)+'px';
	mbss.width =  parseInt(w*0.52)+'px';
	mbss.height =  parseInt(lh*0.5)+'px';
	mbss.color =  '#${fontColor}';
	var btsize= parseInt(w*0.038)+'px';
	buttmdiv.innerHTML='<span style="font-size:'+btsize+'">礼包个数：${haveget}/${bnum} ${btotal}</span>';
	document.body.appendChild(buttmdiv);
}
var isedit=false;
function preeditphone(){
	if(isedit)
		return ;
	getobj('inputd').style.display='';
	getobj('mobiletip').style.display='';
	getobj('dmoney').style.display='none';
	var pic = '${tpic}';
	pic = 'ticket'+pic.substring(7);
	getobj('ticket').style.backgroundImage='url(images/bunusimg/'+pic+'.png)';
	getobj('getbtn').style.backgroundImage='url(images/bunusimg/eidtphone.png)';
	getobj('getbtn').onclick=function(){editphone();};
	
}
function godownapp(){
	T.A.sendData("flygame.do","POST","action=recordhits&uin=${uin}&gid=3",function(ret){
		if(ret=='1')
			location = 'http://mp.weixin.qq.com/s?__biz=MjM5MjgxOTY4OA==&mid=208360229&idx=1&sn=df9c59a4d36233c65f5fef2baf373141&scene=0#rd';
			//location = 'http://app.renrenche.com/channel/590';
	});
}
function movepage(){
	document.getElementById("inputd").value='';
	document.getElementById("inputd").style.color='#000000';
}

function editphone(){
	var mobile = getobj("inputd").value;
	if(!checkMobile(mobile)){
		alert("手机号不合法!请重新输入");
		return ;
	}
	getobj('getbtn').onclick=function(){return false;};
	isedit=true;
	T.A.sendData("carinter.do?action=editmobile&omobile=${mobile}&nmobile="+mobile,"GET","",
		function(ret){
			//alert(ret);
			getobj('dmoney').style.fontSize=parseInt(w*0.045)+'px';
			getobj('dmoney').style.left=parseInt(w*0.14)+'px';
			getobj('dmoney').style.width=parseInt(w*0.36)+'px';
			getobj('inputd').style.display='none';
			getobj('dmoney').style.display='';
			if(ret=="1"){
				getobj('dmoney').innerHTML='手机号修改成功!';
			}else if(ret=="-1"){
				getobj('dmoney').style.color="#FF0000";
				getobj('dmoney').style.top=parseInt(h*0.030)+'px';
				getobj('dmoney').style.fontSize=parseInt(w*0.038)+'px';
				getobj('dmoney').innerHTML='错误：手机号'+mobile+'已注册过，请用微信号登录直接领取 ';
				getobj('mobiletip').style.display='none';
			}else{
				getobj('dmoney').innerHTML='修改失败!';
			}
		},0,null)
}

function checkMobile(str) {
	   var re = /^[1][3,4,5,7,8]\d{9}$/;
	   if (re.test(str)) {
	       return true;
	   } else {
		   return false;
	   }
	}

function attention(){
	//this.src='images/bunusimg/toatt_b.png';
	location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205938292&idx=1&sn=76c6259270d762df187a187fac9e9a8d#rd';
}
function togame(){
	//getobj('getbtn').style.backgroundImage='url(images/bunusimg/togame_b.png)';
	var tid = '${tid}';
	if(tid==''||tid=='null')
		return;
	location='cargame.do?action=pregame&id=${tid}&uin=${uin}';
}

if(parseInt('${money}')>2){
	//getobj('getbtn').onclick=function(){return false;}
	//try{getobj('getbtn').style.backgroundImage='url(images/bunusimg/goodhand.png)';}catch(e){};
	getobj('getbtn').style.backgroundImage='url(images/bunusimg/hitplan_red.png)';
	getobj('getbtn').onclick=function(){tohitplan();}
}

var btype=${btype}
if(btype&&btype==5){
	//8bd3a3
	getobj('top').style.backgroundImage='url(images/bunusimg/bg_fly.png)';
	getobj('getbtn').style.backgroundImage='url(images/bunusimg/hit_plan.png)';
	getobj('getbtn').onclick=function(){tohitplan();}
}
function tohitplan(){
	document.getElementById("togame").submit();
}
//document.getElementById("phonenumber").focus();
</script>
</html>

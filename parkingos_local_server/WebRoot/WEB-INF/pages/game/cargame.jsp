<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <meta name="viewport" content="user-scalable=no,target-densitydpi=high-dpi" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车游戏</title>
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
		    background-image: url(images/game/car_bg.jpg);
		    background-repeat:no-repeat;
		}
		.coverdiv{
			 padding: 0 !important;
			margin: 0 !important;
		    background-color:#ffffff;
		    width:100%;
		    height:100%;
			overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-color:#4DD2A7;
		    background-image: url(images/game/game_rule.jpg);
		    background-repeat:no-repeat;
		}
		*{margin:0; padding:0;}
		</style>
</head>
<body id='body'  ondblclick="alert('db')">
	<div id="demo" style="position:absolute; left:49%;">
	<img src='images/game/car.png'  id ='carimage'/>
	</div>
	<div id="rule" style="position:absolute;left:18%; top:8%;">
		<!--<a href='#'><img src='images/g_rule.png' width='140px' id='ruleimg'/></a>
		<br/><span id ="pagesize"></span> -->
		<span id ="pageinfo"></span>
	</div>
	<div id="tipwords" style="position:absolute;left:27%; top:35%;"'>
		<span id='carin' style='font-size:40px;color:#FFFFFF;'>汽车正在入场</br></span>
	</div>
	<div id="tipwords2" style="position:absolute;left:25%; top:45%;"'>
		<span id='carspeed' style='font-size:21px;color:#FFFFFF;'>初速度40，每次刹车，减速10</br></span>
	</div>
	<div id="tiptime" style="position:absolute;top:55%;"'>
		<span id='bword' style='font-size:88px;color:#FFFFFF;'></span>
	</div>
	<div id="stopcar" style="position:absolute;left:62%; top:75%;"  >
		<a href='#' onclick='stopcar();'>&nbsp;&nbsp;<img src='images/game/stop.png' width='100px' id='stoporstart'/>&nbsp;&nbsp;</a>
	</div>
	<div id="tipdiv" style="position:absolute;left:65%; top:70%;color:#FFFFFF;font-size:24px;line-height:50px;" >
	</div>
	<div id="worddiv" style="position:absolute;left:17%; top:12%;color:#FFFFFF;font-size:24px;line-height:50px;" >
	<span style=''>当前速度 ：</span><span style='' id='carspeed'></span><br/>
	<span style='' id='htdiv'>剩余时间 ：<span style='' id='havetime'></span></span><br/>
	<span style='display:none' id='sctitle'>您的成绩 ：<span style='' id='youscore'></span></span>
	</div>
</body>
<script>

var demo = document.getElementById('demo');
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
//document.getElementById("pagesize").innerText=w+'x'+h;
document.getElementById("carimage").style.width=parseInt(w*0.04)+"px";
demo.width=parseInt(w*0.04)+"px";
demo.height=parseInt(w*0.1)+"px";

//document.getElementById("ruleimg").style.width=parseInt(w*0.15)+"px";
document.getElementById("stoporstart").style.width=parseInt(w*0.19)+"px";
document.getElementById("worddiv").style.fontSize=parseInt(w*0.035)+"px";
document.getElementById("carin").style.fontSize=parseInt(w*0.077)+"px";
document.getElementById("carspeed").style.fontSize=parseInt(w*0.039)+"px";
document.getElementById("worddiv").style.lineHeight=parseInt(w*0.045)+"px";
document.getElementById("tiptime").style.left=parseInt(w*0.46)+"px";
document.getElementById("demo").style.left=parseInt(w*0.482)+"px";
 //alert(w+","+h);
demo.style.top=parseInt(h-h*0.11)+'px'; 
//document.getElementById("stopcar").onclick=function(){stopcar();};
var delay = h*0.0055;

var speed=4;//初始速度50m/s
var lt = 4;//时限10秒
//小车移动幅度
function rander(){
    //alert( demo.style.top );
    if(lt==0){
    	speed=0; stopcar();
    	return ;
    }
    demo.style.top = parseInt(demo.style.top) - parseInt(speed*delay) + 'px'; //每一帧向右移动1px
   // document.getElementById("pageinfo").innerText="speed="+speed+",time="+lt;
}
//刷新页面数据
function writetip(){
	//document.getElementById("pageinfo").innerText="speed="+speed+",time="+lt;
	document.getElementById('carspeed').innerText=speed*10+"m/s";
} 		
//开始
var stepid;
function gamerun(){
    rander();
    if(parseInt(demo.style.top)>10){
    	stepid=setTimeout(function(){gamerun();},50);
    }else {
    	lt=0; speed=0; writetip();
    }
}
//刹车
function stopcar(){
	if(speed<1)
		return ;
	clearTimeout(stepid);
	speed=speed-1;
	writetip();
	gamerun();
	//alert(speed+","+lt);
}
//计时
var ssid ;
function settime(){
	if(lt==0)
		lt=1;
	lt=lt-1;
	document.getElementById('havetime').innerText=lt+'s';
	//alert(lt);
	//if(lt>0){
	//}
	if(speed==0){
		clearTimeout(ssid);
		lt=0;
		scroe();
	}
	ssid=setTimeout(function(){settime();},1000);
}
//计算成绩
//var sid ;
var istoscore=false;
function scroe(){
	if(istoscore)
		return;
	var to = demo.style.top;
	if(lt==0){
		//clearTimeout(sid);
		//停车
		clearTimeout(stepid);
		//停止计时，
		clearTimeout(ssid);
		var sc = parseInt(to);
		document.getElementById('sctitle').style.display='';
		istoscore=true;
		if(sc<30){
			document.getElementById('youscore').innerText='您已经挂了！';
			toscroe('0');
			return ;
		}
		//alert(sc)
		var ht = parseInt(h*0.1045);
		//demo.style.top = ht+"px";
		var tsc = 100-1*(Math.abs((ht-sc)));
		//alert(ht+","+sc);
		if(tsc<0)
			tsc=0;
		document.getElementById('youscore').innerText=tsc+'分';
		toscroe(tsc);
	}
	//sid= setTimeout(function(){scroe();},1000);
}

//gamerun();//开始
//writetip()//刷新页面数据
//settime();//计时

function start(){
	gamerun();//开始
	writetip()//刷新页面数据
	settime();//计时
	
}

var tim=4;
function load(){
	if(tim==0){
		removetip();
		start();
		document.getElementById('bword').innerHTML='';
		document.getElementById('tipwords').style.display='none';
		document.getElementById('tipwords2').style.display='none';
	}
	else{
		tim =tim-1;
		if(tim==0){
			document.getElementById('tiptime').style.left=parseInt(w*0.36)+"px";
			document.getElementById('bword').innerHTML='GO!';
		}else
			document.getElementById('bword').innerHTML=tim;
		setTimeout(function(){load();},1000);
	}
}

load();
pageCover();

function pageCover(){
	var coverW="100%";
	var coverH = "100%";
	var cover = document.createElement("div");
	cover.setAttribute('id','cover');
	var c = cover.style;
	c.display = "block";
	c.visibility = "visible";
	c.width = coverW;
	c.height = coverH;
	c.background ='#000';
	c.position ='absolute';
	cover.zIndex = 1;
	c.top = "0px";
	c.left = "0px";
	c.cursor = "not-allowed";
	c.filter = "alpha(opacity=40)";
	c.opacity ="0.6";
	cover.appendChild(document.getElementById('tipwords'));
	cover.appendChild(document.getElementById('tipwords2'));
	cover.appendChild(document.getElementById('tiptime'));
	document.body.appendChild(cover);
}	
function removetip(){
	document.body.removeChild(document.getElementById("cover"));
}
function gourl(scroe){
	location='cargame.do?action=scroe&uin=${uin}&id=${id}&scroe='+scroe;
}
function toscroe(scroe){
	setTimeout(function(){gourl(scroe);},600);
}


</script>
</html>
<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <meta name="viewport" content="user-scalable=no,target-densitydpi=high-dpi" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>游戏规则</title>
<style type="text/css">
		html,body {
		    padding: 0 !important;
			margin: 0 !important;
		    background-color:#D9AF7F;
		    width:100%;
		    height:100%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/game/game_b.jpg);
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
		    background-color:#D9AF7F;
		    background-image: url(images/game/rule.png);
		    background-repeat:no-repeat;
		}
		</style>
</head>
<body id='body'  ondblclick="alert('db')">
	<div id="rule" style="position:absolute;left:18%; top:8%;"  onclick='viewrule();'>
		<img src='images/game/g_rule.png' width='140px' id='ruleimg'/>
	</div>
	<div id="carsheng" style="position:absolute;left:64%; top:8%;"  onclick='carsort();'>
		<img src='images/game/carsheng.png' width='140px' id='carimg'/>
	</div>
	<div id="logo" style="position:absolute;left:25%; top:26%;" >
		<img src='images/game/game_logo.png' width='140px' id='logoimg'/>
	</div>
	<div id="begin" style="position:absolute;left:37%; top:68%;">
		<a href='#' onclick='begin();'>&nbsp;<img src='images/game/game_begin.png' width='140px' id='beginimg'/></a>
	</div>
</body>
<script>

var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
//document.getElementById("pagesize").innerText=w+'x'+h;
document.getElementById("ruleimg").style.width=parseInt(w*0.2)+"px";
document.getElementById("carimg").style.width=parseInt(w*0.2)+"px";
document.getElementById("rule").style.left=parseInt(w*0.13)+"px";
document.getElementById("logoimg").style.width=parseInt(w*0.5)+"px";
document.getElementById("beginimg").style.width=parseInt(w*0.2)+"px";
document.getElementById("begin").style.left=parseInt(w*0.41)+"px";
function begin(){
	location='cargame.do?action=game&uin=${uin}&id=${id}';
}
function viewrule(){
	var dis = document.createElement("div");
	dis.setAttribute('id','cover');
	dis.className='coverdiv';
	var cs = dis.style;
	cs.position ='absolute';
	cs.align ='center';
	dis.zIndex = 1;
	dis.onclick=function(){
		document.body.removeChild(document.getElementById("cover"));
	};
	var imgdiv= document.createElement("div");
	//img.src='images/game/close.png';
	var icss = imgdiv.style;
	icss.marginTop=(h*0.742)+"px";
	icss.marginLeft=(w*0.422)+"px";
	//icss.position ='absolute';
	//icss.top=parseInt(w*0.422)+"px";
	//icss.left=parseInt(h*0.682)+"px";
	//icss.width=parseInt(w*0.182)+"px";
	//imgdiv.zIndex=12;
	var imgwidth=parseInt(w*0.182)+"px";
	imgdiv.innerHTML='<img src="images/game/close.png" width='+imgwidth+' />';
	
	dis.appendChild(imgdiv);
	
	document.body.appendChild(dis);
	//alert(dis.click);
}
viewrule();

function carsort(){
	location='cargame.do?action=gamesort&uin=${uin}'
}
//body.onclick=function (){stopcar();}
</script>
</html>
<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
 <meta name="viewport" content="user-scalable=no,target-densitydpi=high-dpi" />
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
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
		    background-image: url(../images/game_b.jpg);
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
		    background-image: url(../images/game_rule.jpg);
		    background-repeat:no-repeat;
		}
		</style>
</head>
<body id='body'  ondblclick="alert('db')">
	<div id="rule" style="position:absolute;left:18%; top:8%;"  onclick='viewrule();'>
		<img src='../images/g_rule.png' width='140px' id='ruleimg'/>
	</div>
	<div id="logo" style="position:absolute;left:25%; top:26%;" >
		<img src='../images/game_logo.png' width='140px' id='logoimg'/>
	</div>
	<div id="begin" style="position:absolute;left:37%; top:68%;"  onclick='begin();'>
		<img src='../images/game_begin.png' width='140px' id='beginimg'/>
	</div>
</body>
<script>

var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
//document.getElementById("pagesize").innerText=w+'x'+h;
document.getElementById("ruleimg").style.width=parseInt(w*0.2)+"px";
document.getElementById("rule").style.left=parseInt(w*0.13)+"px";
document.getElementById("logoimg").style.width=parseInt(w*0.5)+"px";
document.getElementById("beginimg").style.width=parseInt(w*0.2)+"px";
document.getElementById("begin").style.left=parseInt(w*0.41)+"px";
function begin(){
	location='stopcargame.jsp';
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
	document.body.appendChild(dis);
	//alert(dis.click);
}
viewrule();
//body.onclick=function (){stopcar();}
</script>
</html>
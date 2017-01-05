<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312" />
<meta name="viewport" content="width=device-width, initial-scale=0.2, minimum-scale=0.2, maximum-scale=2.0, user-scalable=yes" />
<title>停车宝业务系统</title>
<link href="css/base.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<style>
	html, body{ background-position:0 -65px;margin:0;padding:0}
	#page-tabs {margin: 0px; padding:0px;  overflow: hidden; position: absolute;left:5px;top:6px;height:26px;}
</style>
</head>
<body onload="init()">
<script>
var admin_uin = '${admin_uin}';
var isIE = function(){
	var w = navigator.userAgent.toLowerCase();
	var ieMode = document.documentMode;
	return (/msie/.test(w) && !/opera/.test(w))?(!window.XMLHttpRequest?6:(ieMode?ieMode:7)):false
	//return document.documentMode == 10 ?10:((s =navigator.userAgent.toLowerCase().match(/msie ([\d.]+)/))?  parseInt(s[1]) : false);
};
function gwh(_h) {
	var h,_h=_h?_h:0;
	if (window.innerHeight) {
		h = window.innerHeight;
	}else{
		h = document.documentElement.offsetHeight || document.body.clientHeight || 0;
	};
	if(isIE&&isIE<9)
	{
		h -= 3
	}
	h = h<_h?_h:h;
	return parseInt(h);
}
function gww(_w) {
	var w,_w=_w?_w:0;
	//alert(document.body.clientWidth )
	if (window.innerWidth) {
		w = window.innerWidth;
	}else{
		w = document.documentElement.offsetWidth || document.body.clientWidth || 0;
	};
	w = w<_w?_w:w;
	return parseInt(w);
}
function switchTag(tag,url){
	if(url.indexOf('parkwithdraw')!=-1){
		if('${userid}'!='zldcaiwu'){
			alert('您没有权限!!!');
			return ;
		}
	}
    var menulength= document.getElementsByTagName('li').length;
	if(tag.parentNode.className.indexOf("selected") != -1)return;
	for(var i=0;i<menulength;i++){
		if(document.getElementsByTagName('li')[i].className.indexOf("last") != -1){
			document.getElementsByTagName('li')[i].className='last';
		}else{
    		document.getElementsByTagName('li')[i].className='';
		};
		document.getElementById("iframe-"+(i+1)+"-div").style.display = "none";
		var bb = document.getElementById("iframe-"+(i+1)+"-iframe");
		if(bb != undefined){
    		bb.style.display = "none";
		}
	}
	tag.parentNode.className += ' selected';
	
	var oDiv = document.getElementById(tag.id+"-div");
	var iDiv = document.getElementById(tag.id+"-iframe");
	oDiv.style.display = "block";
	if(iDiv != undefined)
    	iDiv.style.display = "block";
	createIframe(oDiv,tag.id,url);
}

function createIframe(oDiv,id,url){
	var oFrameName = id +"-iframe";
	var iframe =  document.getElementById(oFrameName);//jQuery("#"+tag.id+"-iframe");
	if(iframe == undefined){
		var oFrame = isIE()&&isIE()<8 ? document.createElement("<iframe name=\"" + oFrameName + "\" id=\"" + oFrameName + "\">") : document.createElement("iframe");
		oFrame.name = oFrameName;
		oFrame.setAttribute("frameborder","0");
		oFrame.setAttribute("scrolling","auto");
		oFrame.id = oFrameName;
		oFrame.style.width = "100%";
		//oFrame.style.height = "100%";
		oFrame.style.height	= gwh() - 35 + "px";//document.documentElement.scrollHeight - 30 +"px";
		if(isIE()&&isIE()<11) {
			window.attachEvent("onresize",function(){oFrame.style.height = gwh() - 35 +"px"})
		}else{
    		window.addEventListener("resize",function(){oFrame.style.height = gwh() - 35 +"px"},false)
		};
		
		oFrame.setAttribute("src",url);
		//oDiv.insertBefore(oFrame,oDiv.childNodes[0]);
		oDiv.appendChild(oFrame);
	}
}

</script>
<script language="javascript">
function init(){

}
function  logout(){
	location = 'login.do';
}
</script>
</body>
</html>
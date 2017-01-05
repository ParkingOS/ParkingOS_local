<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>我的账户</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=12" />
<script src="js/jquery.js"></script>
<style type="text/css">
#scroller .li1 {
    padding:0 10px;
    height:50px;
    line-height:50px;
    background-color:#FFFFFF;
    font-size:14px;
    color:#101010;
}

.li2 {
    padding:0 10px;
    height:100px;
    line-height:0px;
    background-color:#FFFFFF;
    font-size:14px;
}

.c1{
	border-top:1px solid #CCCCCC;
}

.c2{
	border-bottom:1px solid #CCCCCC;
}

a{
	text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
	
	position: relative;
	top:-36px;
	left:30px;
}

#header {
    position:absolute; z-index:2;
    top:0; left:0;
    width:100%;
    height:120px;
    line-height:100px;
    background-color:#49B9EE;
    padding:0;
    font-size:20px;
    z-index: -100;
}

.img1{
	width:22px;
	height:22px;
	margin-top:15px;
	margin-left:10px;
}
.img2{
	width:70px;
	height:70px;
	margin-top:15px;
}
</style>

<style type="text/css">
* {
	margin: 0px;
	padding: 0px;
}

body {
	font-size: 12px;
	font: Arial, Helvetica, sans-serif;
	margin: 25PX 0PX;
	background: #eee;
}

.botton {
	color: #F00;
	cursor: pointer;
}

.mybody {
	width: 600px;
	margin: 0 auto;
	height: 1500px;
	border: 1px solid #ccc;
	padding: 20px 25px;
	background: #fff
}

#cwxBg {
	position: absolute;
	display: none;
	background: #000;
	width: 100%;
	height: 100%;
	left: 0px;
	top: 0px;
	z-index: 1000;
}

#cwxWd {
	position: absolute;
	display: none;
	border: 0px solid #CCC;
	padding: 0px;
	background: #FFF;
	z-index: 1500;
	width: 60%;
	height: 100px;
	top: 25%;
}

#cwxCn {
	background: #FFF;
	display: block;
}

.imgd {
	width: 400px;
	height: 300px;
}

.ticket {
	font-size: 15px;
	height:50px;
	margin-left:25px;
	margin-top:2px;
	line-height:50px;
	border:0px solid white;
}

.line {
	width: 90%;
	height: 1px;
	background: #ADADAD;
	margin-left:5%;
}

.wx_name{
	margin-left:50px;
	margin-top:-15px;
	color:#101010;
	font-size:20px;
}

.wx_img{
	margin-left:50px;
	margin-top:35px;
	color : gray;
}

.credit{
	float:right;
	margin-right:20px;
	margin-top: -70px;
	color:#B3B3B3;
	font-size:12px;
}

.passli {
	background-image: url(images/wxpublic/arrow.png);
	background-size: 19px 39px;
	background-repeat: no-repeat;
	background-position: right center;
}

.sel_fee{
	font-size:12px;
	text-align:center;
	padding-top:1px;
	padding-bottom:1px;
	border-radius:5px;
	background-color:#FFFFFF;
	outline:medium;
	margin-left:5px;
}

.nopass{
	border:1px solid #CC3333;
	color:#CC3333;
}

.three{
	padding-left:5px;
	padding-right:5px;
}

.money{
	float:right;
	margin-right:50px;
}

.wx_pay{
	border-radius:5px;
	width:98%;
	margin-left:1%;
	height:40px;
	margin-top:5%;
	font-size:15px;
	background-color:white;
	color:#CC3333;
	border: 1px solid #CC3333;
}
</style>
</head>
<body style="background-color:#EEEEEE;">

<div id="wrapper" style="margin-top:-25px;">
	<div id="scroller">
		<ul id="thelist">
			<li class="li2"><img class="img2" src="${wximg}" /><a href="#"><div class="wx_name">${wxname}</div><div class="wx_img">${mobile}</div><div class="credit">信用额度:${credit}</div></a></li>
			<li class="li1" style="margin-top:20px;"><img class="img1" src="images/wxpublic/carnumber1.png" /><a href="wxpaccount.do?action=tocarnumber&openid=${openid}"><div><span style="color:#101010;margin-left: 15px;">车牌</span></div></a></li>
			<li class="li1" style="margin-top:20px;"><img class="img1" src="images/wxpublic/package.png" /><a href="wxpaccount.do?action=balance&openid=${openid}"><div><span style="color:#101010;margin-left: 15px;">余额</span><span class="money">￥${balance}</span></div></a></li>
			<li class="li1" id="testClick1" style="margin-top:1px;"><img class="img1" src="images/wxpublic/ticketaccount.png" /><a href="#"><div><span style="color:#101010;margin-left: 15px;">停车券<span class='sel_fee three nopass'>${ticket_count}张可用</span></span></div></a></li>
			<li class="li1" style="margin-top:20px;"><img class="img1" src="images/wxpublic/orderdetail.png" /><a href="wxpaccount.do?action=toorderlist&mobile=${mobile}"><div><span style="color:#101010;margin-left: 15px;">订单</span></div></a></li>
			<li class="li1" style="margin-top:20px;"><img class="img1" src="images/wxpublic/dooller.png" /><a href="wxpaccount.do?action=toaccountdetail&openid=${openid}"><div><span style="color:#101010;margin-left: 15px;">账户明细</span></div></a></li>
			
		</ul>
	</div>
</div>
<script type="text/javascript">
function usable(type){
	window.location.href = "wxpaccount.do?action=toticketpage&type="+type+"&openid=${openid}";
}
</script>
    <script type="text/javascript">
		C$('testClick1').onclick = function(){
			var neirong = '<div class="ticket" onClick="usable(0)">可用停车券</div><div class="line"></div><div class="ticket" onClick="usable(1)">已用停车券</div>';
			cwxbox.box.show(neirong,600);
		}
		
		function C$(id){return document.getElementById(id);}

		//定义窗体对象
		var cwxbox = {};
		
		cwxbox.box = function(){
			var bg,wd,cn,ow,oh,o = true,time = null;
			return {
				show:function(c,t,w,h){
					if(o){
						bg = document.createElement('div'); bg.id = 'cwxBg';	
						wd = document.createElement('div'); wd.id = 'cwxWd';
						cn = document.createElement('div'); cn.id = 'cwxCn';
						document.body.appendChild(bg);
						document.body.appendChild(wd);
						wd.appendChild(cn);
						bg.onclick = cwxbox.box.hide;
						window.onresize = this.init;
						window.onscroll = this.scrolls;
						o = false;
					}
					if(w && h){
						var inhtml = '<iframe src="'+ c +'" width="'+ w +'" height="'+ h +'" frameborder="0"></iframe>';
					}else{
						var inhtml	 = c;
					}
					cn.innerHTML = inhtml;
					oh = this.getCss(wd,'offsetHeight');
					ow = this.getCss(wd,'offsetWidth');
					this.init();
					this.alpha(bg,50,1);
					this.drag(wd);
					if(t){
						time = setTimeout(function(){cwxbox.box.hide()},t*1000);
					}
				},
				hide:function(){
					cwxbox.box.alpha(wd,0,-1);
					clearTimeout(time);
				},
				init:function(){
					bg.style.height = cwxbox.page.total(1)+'px';
					bg.style.width = '';
					bg.style.width = cwxbox.page.total(0)+'px';
					var h = (cwxbox.page.height() - oh) /2;
					wd.style.top=(h+cwxbox.page.top())+'px';
					wd.style.left=(cwxbox.page.width() - ow)/2+'px';
				},
				scrolls:function(){
					var h = (cwxbox.page.height() - oh) /2;
					wd.style.top=(h+cwxbox.page.top())+'px';
				},
				alpha:function(e,a,d){
					clearInterval(e.ai);
					if(d==1){
						e.style.opacity=0; 
						e.style.filter='alpha(opacity=0)';
						e.style.display = 'block';
					}
					e.ai = setInterval(function(){cwxbox.box.ta(e,a,d)},40);
				},
				ta:function(e,a,d){
					var anum = Math.round(e.style.opacity*100);
					if(anum == a){
						clearInterval(e.ai);
						if(d == -1){
							e.style.display = 'none';
							if(e == wd){
								this.alpha(bg,0,-1);
							}
						}else{
							if(e == bg){
								this.alpha(wd,100,1);
							}
						}
					}else{
						var n = Math.ceil((anum+((a-anum)*.5)));
						n = n == 1 ? 0 : n;
						e.style.opacity=n/100;
						e.style.filter='alpha(opacity='+n+')';
					}
				},
				getCss:function(e,n){
					var e_style = e.currentStyle ? e.currentStyle : window.getComputedStyle(e,null);
					if(e_style.display === 'none'){
						var clonDom = e.cloneNode(true);
						clonDom.style.cssText = 'position:absolute; display:block; top:-3000px;';
						document.body.appendChild(clonDom);
						var wh = clonDom[n];
						clonDom.parentNode.removeChild(clonDom);
						return wh;
					}
					return e[n];
				},
				drag:function(e){
					var startX,startY,mouse;
					mouse = {
						mouseup:function(){
							if(e.releaseCapture)
							{
								e.onmousemove=null;
								e.onmouseup=null;
								e.releaseCapture();
							}else{
								document.removeEventListener("mousemove",mouse.mousemove,true);
								document.removeEventListener("mouseup",mouse.mouseup,true);
							}
						},
						mousemove:function(ev){
							var oEvent = ev||event;
							e.style.left = oEvent.clientX - startX + "px";  
							e.style.top = oEvent.clientY - startY + "px"; 
						}
					}
					e.onmousedown = function(ev){
						var oEvent = ev||event;
						startX = oEvent.clientX - this.offsetLeft;  
						startY = oEvent.clientY - this.offsetTop;
						if(e.setCapture)
						{
							e.onmousemove= mouse.mousemove;
							e.onmouseup= mouse.mouseup;
							e.setCapture();
						}else{
							document.addEventListener("mousemove",mouse.mousemove,true);
							document.addEventListener("mouseup",mouse.mouseup,true);	
						}
					} 
					
				}
			}
		}()
		
		cwxbox.page = function(){
			return{
				top:function(){return document.documentElement.scrollTop||document.body.scrollTop},
				width:function(){return self.innerWidth||document.documentElement.clientWidth||document.body.clientWidth},
				height:function(){return self.innerHeight||document.documentElement.clientHeight||document.body.clientHeight},
				total:function(d){
					var b=document.body, e=document.documentElement;
					return d?Math.max(Math.max(b.scrollHeight,e.scrollHeight),Math.max(b.clientHeight,e.clientHeight)):
					Math.max(Math.max(b.scrollWidth,e.scrollWidth),Math.max(b.clientWidth,e.clientWidth))
				}
			}	
		}()
    </script>
</body>
</html>

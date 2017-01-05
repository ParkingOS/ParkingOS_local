<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<title>泊车点详情</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
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
    background-color:#F5F5F5;
		}
.content{
	width:100%;
	margin:10px auto;
	}
.button{
	font-size:16px;
	line-height:40px;
	margin:50px auto;
	width:94%;
	color:#FFFFFF;
	background-color:#00CD5E;
	text-align:center;
	border-radius: 5px;
	font-weight:700;}
.cdiv{font-size:16px;
	line-height:40px;
	margin:10px auto;
	width:94%;
	text-align:center;
	border-radius: 5px;
	font-weight:700;}

.tipwords{
	width:94%;
	height: 83px;
	background:#fff;
	font-family:"微软雅黑", sans-serif, Arial, Verdana;
	margin:5px auto;
	border:1px solid #ccc;
	border-radius: 5px;
	text-line:30px;
	line-height:30px;
}
</style>
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
</head>
<body>
<div id="content" class="content" >
<div style="width:94%;margin:10px 3%;">
	<img src='attendant.do?action=getpic&db=carstop_pics&id=${pic}'  style='width:100%;height:40%;'/>
</div>
<div style='width:80%;text-align:center'>&nbsp;&nbsp;</div>
<div  id='carstops' ></div>
<!-- <div style='width:100%;text-align:center'>您距离${name}还有${distance}米<br/></div> -->
<div class='tipwords' ><div id='uids' style='width:90%;margin:10px auto'></div></div>
<div class='button' id='b_stop' onclick="stopcar('${id}')">确认泊车</div>
</div>
</body>
<script type="text/javascript">
var dis = ${distance};
if(dis>4){
	//alert(dis);
	var d = document.getElementById('b_stop');
	d.style.backgroundColor='#999999';
	d.onclick=function(){};
}
var orderid;
function stopcar(id){
	//location = 'attendant.do?action=stopcar&id='+id+'&distance=${distance}&uin=uids=${uids}';
	var url = 'attendant.do?action=stopcar&id='+id+'&uin=${uin}&lng=${lng}&lat=${lat}&orderid=${orderid}';
	var ret = T.A.sendData(url);
	if(parseInt(ret)>1){
		pageCover(ret);
		var d = document.getElementById('b_stop');
		d.style.backgroundColor='#999999';
		d.onclick=function(){};
		orderid=ret;
		getState();
	}else{
		var uidd = document.getElementById('uids');	
		uidd.innerHTML ="泊车员正在忙碌，请稍后再试";
		var d = document.getElementById('b_stop');
		d.style.backgroundColor='#999999';
		d.onclick=function(){};
	}
	//setTimeout(getState,1000);
}
//每秒钟查一下是否有泊车员接单，成功接单后，到接车结果页面

function getState(){
	var ret = T.A.sendData("attendant.do?action=getcarstate&id="+orderid);
	//alert(ret+",id:"+id)
	//状态:0车主泊车请求 1泊车员已响应泊车 2正在泊车  3泊车完成 4车主取车请求 5 泊车员已响应取车 6泊车员正在取车 7等待支付 8支付成功
	if(ret=='0'){
		setTimeout(getState,3000);
	}else if(parseInt(ret)>0){
		//alert('${uin}')
		location = 'attendant.do?action=currorder&uin=${uin}&id='+orderid;
	}
}
function initUids(){
	var hasuid='${online}';
	var isfirst = '${isfirst}';
	var ismfirst = '${ismfirst}';
	var uidd = document.getElementById('uids');
	if(dis>4){
		//var m = k.indexOf(".");
		//var l = k.length;
		//alert(m+","+l)
		//if(l>m+3)
		//	k=k.subString(0,m+2);
		uidd.innerHTML ="<font color='#ff5159'>你距离泊车点还有"+dis+"公里，请到附近再预约泊车服务</font>";
		var d = document.getElementById('b_stop');
		d.style.backgroundColor='#999999';
		d.onclick=function(){};
	}else{
		if(hasuid=='1'){
				uidd.innerHTML ="泊车员百无聊赖，静待君来!";
			if(ismfirst=='1'){
				uidd.innerHTML +="<br/><font color='#ff5159'>本周还有一次${favprice}元泊车的机会呦</font>";
			}else if(isfirst=='1'){
				uidd.innerHTML +="<br/><font color='#ff5159'>首次使用，享受${favprice}元首${favunit}小时泊车！</font>";
			}
		}else{
			uidd.innerHTML ="<font color='#ff5159'>泊车员四脚朝天，尚在翻身中，请稍后再试</font>";
			var d = document.getElementById('b_stop');
			d.style.backgroundColor='#999999';
			d.onclick=function(){};
		}
	}
}
initUids();

function pageCover(){
	var coverW="100%";
	var coverH = "100%";
	var coverIndex = 1;
	var cover = document.createElement("div");
	cover.setAttribute('id','cover');
	var c = cover.style;
	c.display = "block";
	c.visibility = "visible";
	c.width = coverW;
	c.height = coverH;
	c.background ='#000';
	c.position ='absolute';
	cover.zIndex = coverIndex;
	c.top = "0px";
	c.left = "0px";
	c.cursor = "not-allowed";
	c.filter = "alpha(opacity=40)";
	c.opacity ="0.6";
	cover.innerHTML = "";

	var dis = document.createElement("div");
	var cs = dis.style;
	cs.top = "10px";
	cs.left = "10%";
	cs.background='#fff';
	cs.width = '80%';
	cs.height = '100px';
	cs.border='1px solid #000000';
	cs.margin='150px auto';
	cs.position ='absolute';
	cs.borderRadius="8px";
	cs.align ='center';
	dis.zIndex = 1;
	var cdiv = document.createElement("div");
	cdiv.className='cdiv';
	cdiv.innerHTML="请稍候<br/>正在为您安排泊车员...";
	dis.appendChild(cdiv);
	document.body.appendChild(cover);
	document.body.appendChild(dis);
	
}	

function setPrice(){
	var dis = document.createElement("div");
	var cs = dis.style;
	cs.background='#918F90';
	cs.width = "94%";
	cs.height = "28px";
	cs.top = "37%";
	cs.marginLeft = "3%";
	cs.marginRight= "3%";
	cs.position ="absolute";
	cs.align ="center";
	cs.filter = "alpha(opacity=40)";
	cs.opacity ="0.3";
	dis.zIndex = 1;
	//dis.appendChild(cdiv);
	document.body.appendChild(dis);
	var cdiv1 = document.createElement("div");
	var cs1 = cdiv1.style;
	cs1.width = "100%";
	cs1.height = "25px";
	cs1.top = "38%";
	cs1.position ="absolute";
	cs1.align ="center";
	cs1.color="#FFFFFF";
	cdiv1.zIndex=2;
	cdiv1.innerHTML="<div style='text-align:center;color:#FFFFFF;font-weight:700'>价格：${priceInfo}</div>";
	document.body.appendChild(cdiv1);
}
setPrice();

</script>
</html>

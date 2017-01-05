<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<title>当前订单</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<script src="js/tq.js?0817" type="text/javascript">//表格</script>

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
.content{width:350px;margin:20px auto;font-family:"微软雅黑", sans-serif, Arial, Verdana;font-size:15px;line-height:20px;}
.cdiv{font-size:12px;line-height:50px;margin:20px auto;width:200px;text-align:center;}
.tipwords{
			width: 240px;
			height: 70px;
			background:#fff;
			margin:5px auto;
			border:1px solid #ccc;
			border-radius: 5px;
			line-height:35px;
			text-align:center;
			color:#9C9A9B;
}
.tipwords2{
			width: 94%;
			height: 105px;
			background:#fff;
			margin:25px 3%;
			border:1px solid #ccc;
			border-radius: 5px;
			line-height:35px;
			color:#9C9A9B;
			font-weight:700;
}
</style>
</head>
<body>
<div id="content" class="content" >
	<div id='d1' style='display:none'>
		<div style='height:50px;color:#9C9A9B'><b>&nbsp;&nbsp;<span id = 'ostate'></span></b></div>
		<div style='width:80%;margin:10px auto;text-align:center'><img id='upic' src='' border='0' style='width:200px;height:200px' /></div>
		<div style='width:100px;margin:10px auto;text-align:center;color:#9C9A9B;'><b><span id='uname'></span></b></div>
		<div class="tipwords"  id='words'></div>
		<div><div style='width:94%;height:40px;border-radius:2px;margin:40px auto;background:#00CD5E;color:#FFFFFF;text-align:center;line-height:40px;' id='cbutton'></div></div>
		<div style='text-align:center;display:none;font-size:18px;line-height:50px;' id='info'><span >代泊员马上会与您联系</span></div>
	</div>
	<div id='d2' style='display:none'>
		<div style='width:94%;margin:10px 3%;text-align:center'><img id='opic' src='' border='0'  style='width:100%;height:200px'  /></div>
		<div style='width:94%;margin:10px 3%;text-align:center'><img id='oilpic' src='' border='0'  style='width:100%;height:200px'  /></div>
		<div class="tipwords2">
			<div>&nbsp;&nbsp;&nbsp;开始时间：<span id='btime' style='color:#747474'></span></div>
			<div>&nbsp;&nbsp;&nbsp;订单状态：<span id='orstate' style='color:#747474'></span></div>
			<div>&nbsp;&nbsp;&nbsp;当前费用：<span id='price' style='color:#747474'></span></div>
		</div>
		<div ><div style='width:94%;height:40px;border-radius:2px;margin:40px auto;background:#00CD5E;color:#FFFFFF;text-align:center;line-height:40px;' id='abutton' onclick="backcar()"></div></div>
	
	</div>
	<div id='d3' style='display:none'>
		<div style="height:100px; text-align: center; font-weight:700; background-color: rgb(245, 245, 245);"> <br/><font style="font-size: 25px;"><font color='red'>￥</font>${total }</font><br/><font color='#B8B8B8' style="font-size: 15px;" ><br/>${dur}</font>&nbsp;</div>
		<div style='height:40px;background-color:#ECECEC;'></div>
		<div style='height:70px;line-height:30px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div>&nbsp;&nbsp;&nbsp;<font color='#9C9A9B'><b>${name}</b></font><br/>&nbsp;&nbsp;&nbsp;<font color='#B8B8B8' style="font-size: 15px;" >${address }</font></div>
		<div style='height:1px;background-color:#E6E6E6;'></div>
		<div style='height:35px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div>&nbsp;&nbsp;&nbsp;<font color='#9C9A9B'><b>订单编号：${id}</b></div>
		<div style='height:1px;background-color:#E6E6E6;'></div>
		<div style='height:35px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div>&nbsp;&nbsp;&nbsp;<font color='#9C9A9B'><b>订单状态：</b></font><font color='red'>已结算</font></div>
		<div style='height:1px;background-color:#E6E6E6;'></div>
		<div style='height:35px;line-height:20px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div>&nbsp;&nbsp;&nbsp;<font color='#9C9A9B'><b>开始时间：${btime }</b></font></div>
		<div style='height:1px;background-color:#E6E6E6;'></div>
		<div style='height:35px;line-height:30px;background-color:#FFFFFF;'><div style='height:8px;color:#747474'></div>&nbsp;&nbsp;&nbsp;<font color='#9C9A9B'><b>离场时间：${etime }</b></font></div>
		<div style='height:1px;background-color:#E6E6E6;'></div>
	</div>
	<div id='d4' style='display:none;margin:200px auto;width:300px;text-align:center;color:#9C9A9B'><b>您的订单已取消，停车费0元。</b></div>
	<div id='d5' style='display:none;margin:200px auto;width:300px;text-align:center;color:#9C9A9B'><b>您还没有泊车订单哦！！</b></div>
	
	</div>
</body>
<script type="text/javascript">
var orderid;
var state ;
function getState(){
	var ret = T.A.sendData("attendant.do?action=getcarstate&id="+orderid);
	//alert(ret+",id:"+id)
	//状态:0车主泊车请求 1泊车员已响应泊车 2正在泊车  3泊车完成 4车主取车请求 5 泊车员已响应取车 6泊车员正在取车 7等待支付 8支付成功
	var sta ;
	if(ret)
		sta = parseInt(ret);
	if(sta==7){
		location = 'attendant.do?action=pay&uin=${uin}&id='+orderid;
	}else if(sta!=state){
		location = 'attendant.do?action=currorder&uin=${uin}';
	}else if(sta!=-1){
		setTimeout(getState,5000);
	}
}
function cancelorder(){
	var id = data.id;
	var url = 'attendant.do?action=cancelorder&id='+id;
	var ret = T.A.sendData(url);
	if(ret=='1'){
		getobj('d1').style.display='none';
		getobj('d2').style.display='none';
		getobj('d3').style.display='none';
		getobj('d4').style.display='';
		//pageCover("请稍候<br/>您的订单已取消");
		//location = 'attendant.do?action=wantstop';
	}else{
		alert('您的订单取消失败，请重新操作..');
	}
}
var getobj=function (id){return document.getElementById(id)};
var data = ${data};

if(data&&data.state){
	state= data.state;
	orderid = data.id;
	if(state==7){
		location= 'attendant.do?action=pay&uin=${uin}&id='+orderid;
	}
	if(state==-1){
		getobj('d5').style.display='';
	}
	//alert(state);
	//状态:0车主泊车请求 1泊车员已响应泊车 2正在泊车  3泊车完成 4车主取车请求 5 泊车员已响应取车 6泊车员正在取车 7等待支付 8支付成功
	if(state==1||state==2){
		getobj('d1').style.display='';
		if(state==1)
			getobj('ostate').innerText='订单状态 :等待泊车';
		else 
			getobj('ostate').innerText='订单状态 :正在泊车';
		getobj('upic').src='attendant.do?action=getpic&db=carstop_pics&id='+data.upic;
		getobj('uname').innerText=data.uname;
		getobj('words').innerHTML+="<b>近期服务："+data.times+"次 &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;驾龄："+data.dyears+"年</br></b>";
		getobj('words').innerHTML+="<b>电话：<a href='tel:"+data.mobile+"'>"+data.mobile+"</a></b>"
		if(state==1){
			getobj('cbutton').innerHTML='取消订单';
			getobj('cbutton').onclick=function(){cancelorder()};
		}
		else
			getobj('cbutton').style.display='none';
		getobj('d2').style.display='none';
	}else if(state==3||state==4){//泊车完成 
		getobj('d2').style.display='';
		getobj('d1').style.display='none';
		getobj('orstate').innerText='泊车完成';
		getobj('opic').src='attendant.do?action=getpic&db=carstop_pics&id='+data.opic;
		if(data.oilpic!='null'){
			getobj('oilpic').src='attendant.do?action=getpic&db=carstop_pics&id='+data.oilpic;
			//getobj('opic').style.height='160px';
			//getobj('oilpic').style.height='160px';
		}else{
			getobj('oilpic').style.display='none';
		}
		getobj('btime').innerText=data.btime;
		getobj('price').innerText=data.total;
		getobj('abutton').innerHTML='我要取车';
		if(state==4){
			pageCover("请稍候<br/>正在为您安排泊车员...")
		}
	}else if(state==5||state==6){
		getobj('d1').style.display='';
		getobj('upic').src='attendant.do?action=getpic&db=carstop_pics&id='+data.upic;
		getobj('uname').innerText=data.uname;
		getobj('words').innerHTML+="<b>近期服务："+data.times+"次 &nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;驾龄："+data.dyears+"年</br></b>";
		getobj('words').innerHTML+="<b>电话：<a href='tel:"+data.mobile+"'>"+data.mobile+"</a></b>"
		getobj('cbutton').style.display='none';
		if(state==5){
			getobj('info').style.display='';
			getobj('ostate').style.display='none';
		}else {
			getobj('ostate').innerText='订单状态 :正在取车';
		}
		getobj('d2').style.display='none';
	}else if(state==8){//完成支付
		getobj('d3').style.display='';
	}
	if(state!=8&&state!=-1)
		setTimeout(getState,5000);
}
function backcar(){
	var id = data.id;
	var url = 'attendant.do?action=backcar&id='+id+'&uin=${uin}';
	var ret = T.A.sendData(url);
	if(ret=='1'){
		pageCover("请稍候<br/>正在为您安排泊车员...");
		var d = document.getElementById('abutton');
		d.style.backgroundColor='#999999';
		d.onclick=function(){return false;};
		setTimeout(getState,3000);
	}else if(ret=='0'){
		pageCover("泊车员正在忙碌，请稍后再试...");
		var d = document.getElementById('abutton');
		//d.style.backgroundColor='#999999';
		//d.onclick=function(){};
		setTimeout(removetip,2000);
	}else if(ret=='2'){
		pageCover("还车请求失败，请重新下单..");
		var d = document.getElementById('abutton');
		//d.style.backgroundColor='#999999';
		//d.onclick=function(){};
		setTimeout(removetip,2000);
	}else if(ret=='3'){
		pageCover("您没有订单，请关闭页面，或重试!!");
		var d = document.getElementById('abutton');
		d.style.backgroundColor='#999999';
		d.onclick=function(){return false;};
		setTimeout(removetip,2000);
	}
}



function pageCover(ret){
	var dis = document.createElement("div");
	dis.setAttribute('id','cover');
	var cs = dis.style;
	cs.top = "10px";
	cs.left = "10%";
	cs.background='#fff';
	cs.width = '80%';
	cs.height = '150px';
	cs.border='1px solid #000000';
	cs.margin='150px auto';
	cs.position ='absolute';
	cs.align ='center';
	dis.zIndex = 1;
	var cdiv = document.createElement("div");
	cdiv.className='cdiv';
	cdiv.innerHTML=ret;
	dis.appendChild(cdiv);
	document.body.appendChild(dis);
}
function removetip(){
	document.body.removeChild(document.getElementById("cover"));
}

</script>
</html>

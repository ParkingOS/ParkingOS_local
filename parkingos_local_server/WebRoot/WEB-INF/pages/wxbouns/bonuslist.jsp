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
		    height:45%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bonus/wx_cai_top.jpg);
		    background-repeat:no-repeat;
		}
		.buttom{
			width:100%;
			height:35px;
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
			font-size:16px;
			color:#CFCFCF;
			margin-top:6px;
			margin-left:10px;
			margin-buttom:4px;
		}
		.yuan{
			font-size:18px;
			float:right;
			margin-top:28px;
			background-size: 100% 100%;
		}
		
		.quan{
			font-size:18px;
			float:right;
			width:95%;
			height:73px;
			margin-top:3px;
			background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bonus/in_mobile2.png);
		    background-repeat:no-repeat;
		}
		.tipword{font-size:20px;margin-top:14px;color:#FF0000;font-weight:700;text-align:center;}
		.div1{margin-top:12px;margin-left:10px;float:left;width:23%;}
		.div2{'margin-top:18px;margin-left:10px;float:left;width:47%;}
		.div2word{'margin-top:10px;margin-left:10px;}
		.div3{margin-top:18px;margin-left:10px;float:right;width:20%;}
		.wordtime{margin-top:10px;margin-left:6px;color:#CACACA}
		.wordmobile{margin-top:10px;margin-left:5px;color:#353535;font-weight:700;}
		
	</style>
</head>
<body>
<script type="text/javascript"
src="http://zb.weixin.qq.com/nearbycgi/addcontact/BeaconAddContactJsBridge.js">
</script>
<script type="text/javascript">
function attention(){
	BeaconAddContactJsBridge.ready(function(){
		//判断是否关注
		BeaconAddContactJsBridge.invoke('checkAddContactStatus',{} ,function(apiResult){
			if(apiResult.err_code == 0){
				/* var status = apiResult.data;
				if(status == 1){
					alert('已关注');
				}else{
					alert('未关注');
					//跳转到关注页
				  BeaconAddContactJsBridge.invoke('jumpAddContact');
				} */
				BeaconAddContactJsBridge.invoke('jumpAddContact');
			}else{
//				alert(apiResult.err_msg)
			}
		});
 	});
}
</script>
<div id='wx_pic' style='margin:0 auto;display:none;'>
<img src='images/bonus/order_bonu.png' />
</div>
	<div class="_top">
		<div class="topword"></div>
		<div class='_logo'></div>
		<div class="word">车主${carnumber}的礼包   <img src='images/bonus/zhe.png' height='24px' width='24px' valign='center'/></div>
		<div class="wword"><span id='bwords'></span></div>
		<div style='font-size:18px;color:#FF0000;text-align:center;width:100%;margin-top:30px auto'>
			<div style='width:70%;float:left;text-align:right'>
				<br/>微信关注停车宝后生效&nbsp;&nbsp;
			</div>
		 	<div style='float:left;' onclick='attention();'>
				<br/> <img src='images/bonus/go_watch.png' height='24px' width='60px'/> 
		 	</div>
		 </div>
	</div>
	<div class="buttom" id="p_buttom">
		<div style='height:1px'></div>
		<div class='infoword'>礼包个数：${haveget}/${bnum}</div>
		<!--<div class="quan" >
			<div style=''>
				<div class='tipword'>祝</div>
			</div>
			<div style='>
				<div class='tipword'>祝</div>
			</div>
			<div style=''>
				<div class='tipword'>祝</div>
			</div>
		</div>
		 <div class="quan" >
			<div style='margin-top:10px;margin-left:20px;'><img src='images/bonus/quan.png' widht='50px' height='50px'/></div>
		</div> -->
	</div>
</body>
<script>
var data = ${data};
var words = '关注停车宝';
var message = '${message}';
if(message!=''){
	document.getElementById('bwords').innerHTML=message;
}
var wlen = words.length;
if(data&&data.length>0){
	var d = document.getElementById('p_buttom');
	for(var i=0;i<data.length;i++){
		var dis = document.createElement("div");
		dis.className='quan';
		var div1=document.createElement("div");
		var div2=document.createElement("div");
		var div3=document.createElement("div");
		div1.className='div1';
		div2.className='div2';
		div3.className='div3';
		
		var tipword =document.createElement("div");
		tipword.className='tipword';
		var index=i;
		if(i>=wlen)
			index =i-wlen;
		tipword.innerText=words.charAt(index);
		
		var mobilediv =document.createElement("div");
		mobilediv.className='div2word';
		
		var mobile =document.createElement("div");
		mobile.className='div2word';
		mobile.innerHTML=data[i].mobile;
		mobile.className='wordmobile';
		
		var time =document.createElement("div");
		time.className='div2word';
		time.innerHTML=data[i].ttime;
		time.className='wordtime';
		
		mobilediv.appendChild(mobile);
		mobilediv.appendChild(time);
		
		var moneydiv =document.createElement("div");
		//moneydiv.className='tipword';
		moneydiv.innerText=data[i].amount+" 折";
		
		div1.appendChild(tipword);
		div2.appendChild(mobilediv);
		div3.appendChild(moneydiv);
		
		dis.appendChild(div1);
		dis.appendChild(div2);
		dis.appendChild(div3);
		
		d.appendChild(dis);
	}
	d.style.height=(30+(data.length)*76)+"px";
}else{
	var d = document.getElementById('p_buttom');
	var dis = document.createElement("div");
	dis.className='infoword';
	dis.innerHTML="<br/><br/>还没有人抢，赶紧下手吧!!";
	d.appendChild(dis);
	d.style.height=(30+1*76)+"px";
	
}
function attention(){
	//location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205312557&idx=1&sn=1351e6dfc70b2929f11e1fcf21ba8ff0#rd';
	location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205938292&idx=1&sn=76c6259270d762df187a187fac9e9a8d#rd';
}	
</script>
</html>

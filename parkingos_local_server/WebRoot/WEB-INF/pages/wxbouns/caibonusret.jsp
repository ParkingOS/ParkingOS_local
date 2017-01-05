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
		    height:37%;
		    font-family:"微软雅黑";
		    overflow-x:hidden !important;
		    background-size: 100% 100%;
			background-position:top center;
		    background-image: url(images/bonus/wx_cai_top.jpg);
		    background-repeat:no-repeat;
		}
		.middle{
			width:100%;
			height:24%;
			overflow-x:hidden !important;
		}
		.buttom{
			width:100%;
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
		._middle{
			margin:0px auto;
			font-weight:500;
			font-size:50px;
			text-align:center;
			padding-top:10px;
			font-weight:700;
			width:250px;
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
		.word_mobile{
			margin:5px auto;
			font-size:20px;
			width:100%;
			text-align:center;
			color:#B9C6E3;
			height:24px;
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
		.tipword{
			font-size:20px;
			margin-top:14px;
			color:#F1AC46;
			font-weight:700;
			text-align:center;
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
		.tipword{font-size:20px;margin-top:14px;color:#FF0000;font-weight:700;}
		.div1{margin-top:12px;margin-left:10px;float:left;width:23%;}
		.div2{'margin-top:18px;margin-left:10px;float:left;width:47%;}
		.div2word{'margin-top:10px;margin-left:10px;}
		.div3{margin-top:18px;margin-left:10px;float:right;width:20%;}
		.wordtime{margin-top:10px;margin-left:6px;color:#CACACA}
		.wordmobile{margin-top:10px;margin-left:5px;color:#353535;font-weight:700;}
		.ownwordmobile{margin-top:10px;margin-left:5px;color:#FF0000;font-weight:700;}
	</style>
</head>
<body>
<div id='wx_pic' style='margin:0 auto;display:none;'>
<img src='images/bonus/order_bonu.png' />
</div>
	<div class="_top">
		<div class="topword"></div>
		<div class='_logo'></div>
		<div class="word">车主${carnumber}的礼包   <img src='images/bonus/zhe.png' height='24px' width='24px' valign='center'/></div>
		<div class="wword"><span id='bwords'>停车打折爽爽爽</span></div>
	</div>
	<div class="middle"  id='inputdiv'>
		<div class="_middle" >${money}&nbsp;折券 &nbsp;<img src='images/bonus/to_game.png' width='60px' onclick='togame()'/></div>
			<div class="word_mobile">${retwords}</div>
			<div class="word_mobile">
			<div style='width:70%;float:left;text-align:right'>
				微信关注停车宝后生效&nbsp;&nbsp;
			</div>
		 	<div style='float:left;' onclick='attention();'>
				<img src='images/bonus/go_watch.png' height='24px' width='60px'/> 
		 	</div>
			</div>
		</div>
	</div>
	<div class="buttom" id="p_buttom">
		<div style='height:1px'></div>
		<div class='infoword'>礼包个数：${haveget}/${bnum}</div>
		<!--<div class="quan" >
			<div style='margin-top:12px;margin-left:10px;float:left;width:20%;'>
				<div class='tipword'>祝</div>
			</div>
			<div style='margin-top:18px;margin-left:10px;float:left;width:50%;'>
				<div class='tipword'>祝</div>
			</div>
			<div style='margin-top:18px;margin-left:10px;float:right;width:20%;'>
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
var wlen = words.length;
var mobileindex = ${index};
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
		if(mobileindex>-1&&i==mobileindex){
			mobile.className='ownwordmobile';
		}else
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
}

function attention(){
	//this.src='images/bunusimg/toatt_b.png';
	//location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205312557&idx=1&sn=1351e6dfc70b2929f11e1fcf21ba8ff0#rd';
	location='http://mp.weixin.qq.com/s?__biz=MzA4MTAxMzA2Mg==&mid=205938292&idx=1&sn=76c6259270d762df187a187fac9e9a8d#rd';
}

function togame(){
	//alert('togame');
	//getobj('getbtn').style.backgroundImage='url(images/bunusimg/togame_b.png)';
	location='cargame.do?action=pregame&id=${tid}&uin=${uin}';
}
</script>
</html>

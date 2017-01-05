<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>注册成功</title>
<style type="text/css">
.fixed-bottom{
    position: fixed; bottom:10px; _top:expression(eval(document.documentElement.scrollTop+document.documentElement.clientHeight-this.offsetHeight-(parseInt(this.currentStyle.marginTop,10)||0)-(parseInt(this.currentStyle.marginBottom,10)||0)-10));right:5px; 
}
      
.fixed{
    _position: absolute; _bottom: auto;
}
.download {width:70%;height:41px;margin-top:10px;background-color:#27c766;font-size:17px;color:white;}
</style>
<script src="js/jquery.js" type="text/javascript">//表格</script>
<script src="js/qrcode.js" type="text/javascript">//表格</script>
<script src="js/jquery.qrcode.js" type="text/javascript">//表格</script>
<script type="text/javascript"> 
var pid = "${uin}";
var name = "${nickname}";
$(function () { 
	$("#div_div").qrcode(utf16to8("http://www.tingchebao.com?pid="+pid+"&name="+name)); 
}) 
function utf16to8(str) { //转码 
	var out, i, len, c; 
	out = ""; 
	len = str.length; 
	for (i = 0; i < len; i++) { 
		c = str.charCodeAt(i); 
		if ((c >= 0x0001) && (c <= 0x007F)) { 
			out += str.charAt(i); 
		} else if (c > 0x07FF) { 
			out += String.fromCharCode(0xE0 | ((c >> 12) & 0x0F)); 
			out += String.fromCharCode(0x80 | ((c >> 6) & 0x3F)); 
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F)); 
		} else { 
			out += String.fromCharCode(0xC0 | ((c >> 6) & 0x1F)); 
			out += String.fromCharCode(0x80 | ((c >> 0) & 0x3F)); 
		} 
	} 
	return out;
} 

window.setInterval(function(){
	jQuery.ajax({
		type:"post",
		url:"regparker.do?action=getbalance",
		data:{'uin':pid},
        async:false,
        success:function(result){
        	if(result != null && result != ""){
        		var data = eval("("+result+")");
        		//余额
        		var balance = data.balance;
        		//停车费
        		var epaymoney = data.epaymoney;
        		//停车宝返现
        		var backmoney = data.backmoney;
        		var back = document.getElementById("backmoney");
        		//更新前页面显示的返现金额
        		var oldebackmoney = parseFloat(back.innerHTML);
        		//最新的返现金额
    			var newebackmoney = parseFloat(backmoney);
    			var epay = document.getElementById("epaymoney");
    			//更新前页面显示的停车费金额
    			var oldepaymoney = parseFloat(epay.innerHTML);
    			//最新的停车费金额
    			var newepaymoney = parseFloat(epaymoney);
    			if(newebackmoney > oldebackmoney && newepaymoney > oldepaymoney){
    				alert("恭喜您收到"+(newepaymoney - oldepaymoney).toFixed(2) + "元停车费,返现"+(newebackmoney - oldebackmoney).toFixed(2)+"元");
    			}else if(newepaymoney > oldepaymoney){
    				alert("恭喜您收到"+(newepaymoney - oldepaymoney).toFixed(2) + "元停车费");
    			}
    			document.getElementById("epaymoney").innerHTML = newepaymoney;
    			document.getElementById("backmoney").innerHTML = newebackmoney;
    			document.getElementById("balance").innerHTML = balance;
        	}
        }
    });
},5000);
</script>
</head>
<body style="background-color:#F0F0F0;">
<div style="text-align:center;">
<div id="epaymoney" style="display:none;">${epaymoney}</div>
<div id="backmoney" style="display:none;">${backmoney}</div>
<div><img src="images/client_menu_icons/collector.png" style="width:80px;height:80px;"/></div>
<div style="margin-top:2%;font-size:15px;">${nickname}（编号：${uin}）</div>
<div id="div_div" style="margin:auto;margin-top:5%;"></div> 
<div style="margin-top:2%;font-size:15px;">停车宝扫一扫向我支付</div>
<div style="margin-top:8%;">
<div style="font-size:17px;">账户余额：<span style="color:green;" id="balance">${balance}</span>元</div>
<input type="button" id="download" value="下载App快速收费提现" class="download" onclick="location.href='regparker.do?action=download'" >
<div style="margin-top:5px;"><span style="color:green;font-size:15px;">账号密码已发送您的手机</span></div>
</div>
<div class="fixed-bottom fixed" style="text-align:right;font-size:15px;">客服电话：010-53423545</div>
</div>
</body>
</html>

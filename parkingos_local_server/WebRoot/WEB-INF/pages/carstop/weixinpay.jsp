<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>付泊车费</title>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=11" />
<script src="js/jquery.js"></script>
<style type="text/css">
.fee {
	color: #39B388;
	font-size: 18px;
}

.hide {
	display: none;
}

.pay {
	width: 99%;
	height: 41px;
	border: 1px solid #F0F0F0;
	margin-top:70%;
	background-color: #00A55D;
	color: white;
	font-size: 15px;
	
	-webkit-border-bottom-left-radius: 8px;
	border-bottom-left-radius: 8px;
	-webkit-border-bottom-right-radius: 8px;
	border-bottom-right-radius: 8px;
	border-top: 0;
	-webkit-border-top-left-radius: 8px;
	border-top-left-radius: 8px;
	-webkit-border-top-right-radius: 8px;
	border-top-right-radius: 8px;
}
.ticket{
	-webkit-border-bottom-left-radius: 8px;
	border-bottom-left-radius: 8px;
	-webkit-border-bottom-right-radius: 8px;
	border-bottom-right-radius: 8px;
	border-top: 0;
	-webkit-border-top-left-radius: 8px;
	border-top-left-radius: 8px;
	-webkit-border-top-right-radius: 8px;
	border-top-right-radius: 8px;
	
	margin-left:5px;
	background-color:#00A55D;
	color:white;
	padding-left:5px;
	padding-right:5px;
	padding-top:2px;
	padding-bottom:2px;
}

.top{
	font-size : 20px;
	text-align:center;
}

#scroller li {
	padding: 0 10px;
	height: 50px;
	line-height: 50px;
	border-bottom: 1px solid #ccc;
	border-top: 1px solid #fff;
	background-color: #fafafa;
	font-size: 14px;
}

li {
	margin-top: 5px;
	margin-left: 2%;
	margin-right: 2%;
}


a{
	text-decoration:none;
	color:#6D6D6D;
	font-size:16px;
}

#header {
    position:absolute; z-index:2;
    top:0; left:0;
    width:100%;
    height:120px;
    line-height:50px;
    background-color:#F3F3F3;
    padding:0;
    font-size:20px;
    text-align:center;
}
</style>
<script language="javascript">
	var money = '${money}';
	var openid = '${openid}';
	var uid = '${uid}';
	var ticketid = '${ticketid}';
	
	function callpay(){
		 WeixinJSBridge.invoke('getBrandWCPayRequest',{  
             "appId" : '${appid}',                  //公众号名称，由商户传入  
             "timeStamp":'${timestamp}',          //时间戳，自 1970 年以来的秒数  
             "nonceStr" : '${nonceStr}',         //随机串  
             "package" : '${packagevalue}',      //<span style="font-family:微软雅黑;">商品包信息</span>  
             "signType" : '${signType}',        //微信签名方式:  
             "paySign" : '${paySign}'           //微信签名  
             },function(res){
//            	 alert(res.err_msg);
//            	 alert("money:"+money+"  openid:"+openid+"  uid:"+uid+"  ticketid:"+ticketid);
            	 if(res.err_msg == "get_brand_wcpay_request:ok"){
//            		 alert("开始跳转成功页面。。。");
            		 location = 'attendant.do?action=currorder&id=${orderid}';
            	 }
         });
	}
</script>
</head>
<body style="background-color:#EEEEEE;">
<div id="header">
	<div>泊车费</div>
	<div style="font-size:35px;">￥${money}</div>
</div>
<div id="wrapper" style="margin-top:60px;">
	<div style="margin-top:5%;" id="scroller">
		<ul id="thelist">
			<li><span style="font-size:16px;">停车券</span><span class="ticket">${ticket_count}张可用</span><span class="right">${ticket_description}</span></li>
			<li><span style="font-size:16px;">余额支付：</span>￥${balance_pay}</li>
			<li style="font-size:20px;color:#00A55D;">微信支付：￥${wx_pay}</li>
		</ul>
	</div>
	<input type="button" id="wxpay" value="微信支付" onclick="callpay()" class="pay">
</div>
<div id="footer"></div>
</body>
</html>

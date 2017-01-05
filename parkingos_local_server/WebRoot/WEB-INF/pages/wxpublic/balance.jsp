<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>账户充值</title>
<script type="text/javascript">
	var ua = navigator.userAgent.toLowerCase();
	if (ua.match(/MicroMessenger/i) != "micromessenger"){
		window.location.href = "http://s.tingchebao.com/zld/error.html";
	}
</script>
<link rel="stylesheet" type="text/css" href="css/jquery.mobile-1.3.2.min.css?v=1" />
<link rel="stylesheet" type="text/css" href="css/list.css?v=12" />
<script src="js/jquery.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
<style type="text/css">
#scroller .li1 {
    padding:0 10px;
    height:45px;
    line-height:45px;
    background-color:white;
    font-size:14px;
    margin-top:0px;
}

.li2 {
    padding:0 10px;
    height:150px;
    border-bottom:1px solid #EBEBEB;
    border-top:1px solid #EBEBEB;
    background-color:white;
    font-size:14px;
    margin-top:0px;
}

.img1{
	width:20px;
	height:20px;
	margin-top:15px;
}
.img2{
	width:80px;
	height:80px;
	margin-top:5px;
}
</style>

<style type="text/css">
.carnumber{
	margin-left:10px;
	border-width:0;
	font-size:16px;
}

.wx_pay{
	border-radius:3px;
	width:98%;
	margin-left:1%;
	height:40px;
	margin-top:32px;
	font-size:15px;
	background-color:#38B074;
	color:white;
	border: 1px solid #F0F0F0;
}

.hide{
	display:none;
}

.error {
	color: red;
	font-size: 15px;
	margin-top:0px;
}

.discount{
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#DD4E44;
	outline:medium;
	margin-left:5px;
	color:white;
	padding-left:2px;
	padding-right:2px;
	font-size:11px;
}

.ticket{
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#60BD52;
	outline:medium;
	margin-left:5px;
	color:white;
	padding-left:2px;
	padding-right:2px;
	font-size:11px;
}

.balance{
	text-align:center;
	padding-top:2px;
	padding-bottom:1px;
	border-radius:3px;
	background-color:#CD9A2D;
	outline:medium;
	margin-left:5px;
	color:white;
	padding-left:2px;
	padding-right:2px;
	font-size:11px;
}

.wxpay-logo {
	content: "";
	background: url(images/wxpublic/wxpay_logo.png) no-repeat;
	width: 71px;
	height: 19px;
	display: inline-block;
	-webkit-background-size: 71px 19px;
	background-size: 71px 19px;
	-moz-transform: translateX(-50%);
	-webkit-transform: translateX(-50%);
	-ms-transform: translateX(-50%);
	transform: translateX(-50%);
	margin: 20px 0 20px 50%;
}
</style>
<style type="text/css">
	.unselected{
		border:1px solid #38B074;
		color : #38B074;
	}
	.sel_fee{
		font-size:20px;
		text-align:center;
		padding-top:5px;
		padding-bottom:5px;
		border-radius:2px;
		background-color:#FFFFFF;
		outline:medium;
		
	}
	
	.two{
		padding-left:17px;
		padding-right:17px;
	}
	
	.three{
		padding-left:12px;
		padding-right:12px;
	}
	.selected{
		border:1px solid #38B074;
		background-color:#38B074;
		color:white;
	}
	
	input::-webkit-outer-spin-button,
	input::-webkit-inner-spin-button {
    	-webkit-appearance: none;
	}
	
	.company_name{
		text-decoration:none;
		color:#6D6D6D;
		font-size:16px;
	}
</style>
</head>
<body style="background-color:#EEEEEE;">
<div id="wrapper" style="margin-top:-47px;">
<form method="post" role="form" action="">
	<div id="scroller">
		<ul id="thelist">
			<li class="li2">
				<div style="text-align:center;margin-top:40px;font-size:16px;">我的余额</div>
				<div style="text-align:center;margin-top:10px;font-size:35px;"><span style="font-size:25px;">￥</span>${balance}</div>
			</li>
			<li class="li1" style="margin-top:20px;"><div class="company_name"><span>充值金额</span><input type="number" placeholder="认证后充100元送大礼包" id="fee" class="carnumber" name="fee" /></div></li>
			
			<div style="margin-top:32px;">
				<span class="sel_fee unselected two">30</span><span
					class="sel_fee unselected two">50</span><span
					class="sel_fee unselected three">100</span><span
					class="sel_fee unselected three">200</span>
			</div>
			<input type="button" id="paysubmit" class="wx_pay" onclick='check();' value="充值" />
			<div class="wxpay-logo"></div>
			<div style="text-align:center;" id="error" class="error"></div>
		</ul>
	</div>
</form>
</div>
<script type="text/javascript">
	//网页宽度适配
	var width = $(window).width();
	var w = Math.round((width - 56*4)/5);
	var objs = $(".sel_fee");
	for(var i=0;i<objs.length;i++){
		var obj = objs[i];
		obj.style.marginLeft = w + "px";
	}
	
	//选择金额
	$(".sel_fee").bind("click", function(){
		var fee=this.innerHTML;
		for(var i=0;i<objs.length;i++){
			var obj = objs[i];
			removeClass(obj,"selected");
			addClass(obj,"unselected");
		}
		removeClass(this,"unselected");
		addClass(this,"selected");
		
		document.getElementById("fee").value = fee;
	})
</script>
<script type="text/javascript">
	$("#fee").keydown(function (e) {
            numberformat(this);
        });
	
	function check(){
		var fee = document.getElementById("fee").value;
		if(fee == "" || parseFloat(fee) <=0){
			document.getElementById("error").innerHTML = "请输入正确金额";
			return;
		}
		if(hasClass(document.getElementById("paysubmit"),"wait")){
			return false;
		}
		document.getElementById("paysubmit").value = "充值中...";
		addClass(document.getElementById("paysubmit"),"wait");
		charge(fee);//获取微信支付参数
	}
	    
	function numberformat(domInput) {
	    $(domInput).css("ime-mode", "disabled");
	    $(domInput).bind("keypress", function (e) {
	        var code = (e.keyCode ? e.keyCode : e.which);  //兼容火狐 IE   
	        if (!$.browser.msie && (e.keyCode == 0x8))  //火狐下 不能使用退格键  
	        {
	            return;
	        }
	        return code >= 48 && code <= 57 || code == 46;
	    });
	    $(domInput).bind("blur", function () {
	        if (this.value.lastIndexOf(".") == (this.value.length - 1)) {
	            this.value = this.value.substr(0, this.value.length - 1);
	        } else if (isNaN(this.value)) {
	            this.value = " ";
	        }
	    });
	    $(domInput).bind("paste", function () {
	        var s = clipboardData.getData('text');
	        if (!/\D/.test(s));
	        value = s.replace(/^0*/, '');
	        return false;
	    });
	    $(domInput).bind("dragenter", function () {
	        return false;
	    });
	    $(domInput).bind("keyup", function () {
	        this.value = this.value.replace(/[^\d.]/g, "");
	        //必须保证第一个为数字而不是.
	        this.value = this.value.replace(/^\./g, "");
	        //保证只有出现一个.而没有多个.
	        this.value = this.value.replace(/\.{2,}/g, ".");
	        //保证.只出现一次，而不能出现两次以上
	        this.value = this.value.replace(".", "$#$").replace(/\./g, "").replace("$#$", ".");
	      	//只能输入两个小数
	        this.value = this.value.replace(/^(\-)*(\d+)\.(\d\d).*$/,'$1$2.$3'); 
	    });
	}
</script>
<script type="text/javascript">
	function charge(fee){
		jQuery.ajax({
		type : "post",
		url : "wxpaccount.do",
		data : {
			'action' : 'charge',
			'openid' : '${openid}',
			'fee' : fee
		},
		async : false,
		success : function(result) {
			if(result == "-1"){
				document.getElementById("error").innerHTML = "充值失败,请重新操作";
				document.getElementById("paysubmit").value = "充值";
				removeClass(document.getElementById("paysubmit"),"wait");
			}else{
				var jsonData = eval("(" + result + ")");
				callback(jsonData);//调用微信支付
			}
		}
	});
	}
	
	function callback(jsonData){
		WeixinJSBridge.invoke('getBrandWCPayRequest',{  
             "appId" : jsonData.appid,                  //公众号名称，由商户传入  
             "timeStamp":jsonData.timestamp,          //时间戳，自 1970 年以来的秒数  
             "nonceStr" : jsonData.nonceStr,         //随机串  
             "package" : jsonData.packagevalue,      //<span style="font-family:微软雅黑;">商品包信息</span>  
             "signType" : jsonData.signType,        //微信签名方式:  
             "paySign" : jsonData.paySign           //微信签名  
             },function(res){
            	 if(res.err_msg == "get_brand_wcpay_request:ok"){
					 var fee = document.getElementById("fee").value;
            		 window.location.href = "wxpublic.do?action=balancepayinfo&openid="+jsonData.openid+"&money="+fee+"&notice_type=3";
            	 }else{
            	 	document.getElementById("error").innerHTML = "充值失败,请重新操作";
					document.getElementById("paysubmit").value = "充值";
					removeClass(document.getElementById("paysubmit"),"wait");
            	 }
         });
	}
</script>
<script type="text/javascript">
	//每次添加一个class
	function addClass(currNode, newClass){
        var oldClass;
        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
        if(oldClass !== null) {
		   newClass = oldClass+" "+newClass; 
		}
		currNode.className = newClass; //IE 和FF都支持
  		}
	
	//每次移除一个class
	function removeClass(currNode, curClass){
		var oldClass,newClass1 = "";
        oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
        if(oldClass !== null) {
		   oldClass = oldClass.split(" ");
		   for(var i=0;i<oldClass.length;i++){
			   if(oldClass[i] != curClass){
				   if(newClass1 == ""){
					   newClass1 += oldClass[i]
				   }else{
					   newClass1 += " " + oldClass[i];
				   }
			   }
		   }
		}
		currNode.className = newClass1; //IE 和FF都支持
	}
	
	//检测是否包含当前class
	function hasClass(currNode, curClass){
		var oldClass;
		oldClass = currNode.getAttribute("class") || currNode.getAttribute("className");
		if(oldClass !== null){
			oldClass = oldClass.split(" ");
			for(var i=0;i<oldClass.length;i++){
			   if(oldClass[i] == curClass){
				   return true;
			   }
		   }
		}
		return false;
	}
</script>
</body>
</html>

<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>车神榜</title>
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
		    background-image: url(images/game/game_b.jpg);
		    background-repeat:no-repeat;
		}
		*{margin:0; padding:0;}
	a{text-decoration: none;}
	img{max-width: 100%; height: auto;}
	.weixin-tip{display: none; position: fixed; left:0; top:0; bottom:0; background: rgba(0,0,0,0.8); filter:alpha(opacity=80);  height: 100%; width: 100%; z-index: 100;}
	.weixin-tip p{text-align: center; margin-top: 10%; padding:0 5%;}
		</style>
</head>
<script src="js/jquery.js"></script>
<script src="http://res.wx.qq.com/open/js/jweixin-1.0.0.js" type="text/javascript"></script>
<script type="text/javascript">

//alert('menuItem:share:timeline');
wx.config({
    debug: false, // 开启调试模式,调用的所有api的返回值会在客户端alert出来，若要查看传入的参数，可以在pc端打开，参数信息会通过log打出，仅在pc端时才会打印。
    appId: '${appid}', // 必填，公众号的唯一标识
    timestamp:'${timestamp}', // 必填，生成签名的时间戳
    nonceStr: '${nonceStr}', // 必填，生成签名的随机串
    signature: '${signature}',// 必填，签名，见附录1
    jsApiList: [
    	'checkJsApi',
        'onMenuShareTimeline',
        'onMenuShareAppMessage',
        'onMenuShareQQ',
        'onMenuShareWeibo',
        'hideMenuItems',
        'showMenuItems',
        'hideAllNonBaseMenuItem',
        'showAllNonBaseMenuItem',
        'translateVoice',
        'startRecord',
        'stopRecord',
        'onRecordEnd',
        'playVoice',
        'pauseVoice',
        'stopVoice',
        'uploadVoice',
        'downloadVoice',
        'chooseImage',
        'previewImage',
        'uploadImage',
        'downloadImage',
        'getNetworkType',
        'openLocation',
        'getLocation',
        'hideOptionMenu',
        'showOptionMenu',
        'closeWindow',
        'scanQRCode',
        'chooseWXPay',
        'openProductSpecificView',
        'addCard',
        'chooseCard',
        'openCard'
        ] // 必填，需要使用的JS接口列表，所有JS接口列表见附录2
});
wx.hideMenuItems({
    menuList: ['menuItem:share:appMessage','menuItem:share:qq','menuItem:share:weiboApp','menuItem:favorite','menuItem:openWithQQBrowser','menuItem:copyUrl','menuItem:openWithSafari'] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
});
</script>
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script type="text/javascript">
var debuging = false;
var type='${ctype}';
var linkurl="";
var imgUrl = "";
var desc='开了这么久的车，却没完成这个破停车挑战，我也是醉了。';
var url  ='http://s.tingchebao.com/zld/';
if(debuging)
	url  ='http://yxiudongyeahnet.vicp.cc/zld/';
	
linkurl= url +'cargame.do?action=caibouns&id=${bonusid}';
imgUrl = url+'images/game/share_b.jpg';

	wx.ready(function() {
		
		function sharetofriend(){
			wx.onMenuShareAppMessage({
			    title: '停车宝日破万单，猛送红包快来领', // 分享标题
			    desc: desc, // 分享描述
			    link: linkurl, // 分享链接
			    imgUrl: imgUrl, // 分享图标
			    type: '', // 分享类型,music、video或link，不填默认为link
			    dataUrl: '', // 如果type是music或video，则要提供数据链接，默认为空
			    success: function () { 
			        // 用户确认分享后执行的回调函数
			        T.loadTip(1,"分享成功，不是朋友圈，不能复活！",2,"");
			       /**T.A.sendData("cargame.do?action=lifeticket&tid=${ticketid}","get","",
						function deletebackfun(ret){
							t=ret;
							if(ret=="1"){
								T.loadTip(1,"复活成功！",2,"");
								setTimeout(function (){wx.closeWindow(),1000});
							}else{
								T.loadTip(1,ret,2,"");
							}
						}
					);**/
			    },
			    cancel: function () { 
			        // 用户取消分享后执行的回调函数
			    	$(".weixin-tip").hide();
			    }
			});
		}
		
		function sharetocircle(){//发送到朋友圈
			wx.onMenuShareTimeline({
			    title: '开了这么久的车，却没完成这个破停车挑战，我也是醉了。', // 分享标题
			    link: linkurl, // 分享链接
			    imgUrl: imgUrl, // 分享图标
			    success: function () { 
			        // 用户确认分享后执行的回调函数
			    	T.A.sendData("cargame.do?action=lifeticket&tid=${ticketid}","get","",
							function deletebackfun(ret){
								t=ret;
								if(ret=="1"){
									T.loadTip(1,"复活成功！",2,"");
									setTimeout(function (){wx.closeWindow(),1000});
								}else{
									T.loadTip(1,ret,2,"");
								}
							}
						);
			    },
			    cancel: function () { 
			        // 用户取消分享后执行的回调函数
			    	$(".weixin-tip").hide();
			    }
			});
		}
		//判断是否可以发送礼包给朋友
		//document.getElementById("shareimage").style.display = "";//显示礼包图片
		sharetofriend();
		try{sharetocircle();}catch(e){};
	});
	function shareinfo(){
		wx.hideMenuItems({
		    menuList: ['menuItem:share:appMessage','menuItem:share:qq','menuItem:share:weiboApp','menuItem:favorite','menuItem:openWithQQBrowser','menuItem:copyUrl','menuItem:openWithSafari'] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
		});
		//window.share.share("http://www.tingchebao.com","停车宝","测试JS调用","");
		var sourcetag = navigator.userAgent
		//alert(sourcetag);
		
		if(navigator.userAgent.indexOf("MicroMessenger")!=-1){
			var winHeight = $(window).height();
			function is_weixin() {
			    var ua = navigator.userAgent.toLowerCase();
			    if (ua.match(/MicroMessenger/i) == "micromessenger") {
			        return true;
			    } else {
			        return false;
			    }
			}
			var isWeixin = is_weixin();
			if(isWeixin){
				$(".weixin-tip").css("height",winHeight);
	            $(".weixin-tip").show();
			}
		}else if(sourcetag.indexOf("Android")!=-1){//来自android
			window.share.share(linkurl,desc,'停车宝日破万单，猛送红包快来领',imgUrl)
		}else if(sourcetag.indexOf("iPhone")!=-1){//来自iphone
			location = 'http://s.tingchebao.com?desc='+desc+'&title=停车宝日破万单，猛送红包快来领&imgurl='+imgUrl+'&url='+linkurl;
		}
	}
	$(document).ready(function() {
		var userAgent = navigator.userAgent.toLowerCase();
		if(userAgent.match(/iphone os/i) == "iphone os"){
			document.getElementById("android").style.display = "none";
		}else{
			document.getElementById("ios").style.display = "none";
		}
		wx.hideMenuItems({
		    menuList: ['menuItem:share:appMessage','menuItem:share:qq','menuItem:share:weiboApp','menuItem:favorite','menuItem:openWithQQBrowser','menuItem:copyUrl','menuItem:openWithSafari'] // 要隐藏的菜单项，只能隐藏“传播类”和“保护类”按钮，所有menu项见附录3
		});
	});
</script>
<body id='body' >
<div class="weixin-tip">
		<p>
			<img id="android" style="height:500px;" src="images/wxpublic/mask_android1.png" />
			<img id="ios" style="height:500px;" src="images/wxpublic/mask_ios1.png" />
		</p>
	</div>
<div id='win' style='position:absolute'><img src='images/game/win.png' id='winimg'/></div>
<div id='tit' style='color:#f2c115;text-align:center'><b>车神排行榜</b></div>
<div id='line' style='position:absolute'><img src='images/game/line.png' id='lineimg'/></div>
<div id='line2' style='position:absolute'><img src='images/game/line.png' id='lineimg2'/></div>
	<div id="words1" style="position:absolute;color:#FFFFFF" >
		在${usercount}名车主中，未进入排行榜
	</div>
	<div id="words3" style="position:absolute;color:#EDC500" >
		分享到朋友圈，即可复活原停车券
	</div>
	<div id="star" style="position:absolute;" >
		<img id='starimg' src='images/game/star.png' width='140px'/>
	</div>

	<div id="share" style="position:absolute;" onclick='shareinfo();'>
		<img id='shareimg' src='images/game/share.png' width='140px' />
	</div>
</body>
<script>
var h = document.getElementById('body').offsetHeight;
var w = document.getElementById('body').offsetWidth;
var img = document.getElementById('win').style;
img.top=parseInt(h*0.07)+"px";
img.left=parseInt(w*0.43)+"px";
img.width=parseInt(w*0.4)+'px';

var line = document.getElementById('line').style;
line.top=parseInt(h*0.26)+"px";
line.left=parseInt(h*0.10)+"px";

document.getElementById('lineimg').style.width=parseInt(w*0.68)+"px";

document.getElementById('winimg').style.width=parseInt(w*0.15)+"px";
document.getElementById('tit').style.marginTop=parseInt(h*0.21)+"px";
document.getElementById("tit").style.fontSizgamefaile=(w*0.045)+'px';

document.getElementById("words1").style.left=parseInt(w*0.18)+"px";
document.getElementById("words1").style.top=parseInt(h*0.714)+"px";
document.getElementById("words1").style.width=parseInt(w*0.682)+"px";
document.getElementById("words1").style.fontSize=parseInt(w*0.042)+"px";

document.getElementById("star").style.left=parseInt(w*0.122)+"px";
document.getElementById("star").style.top=parseInt(h*0.765)+"px";
document.getElementById("starimg").style.width=parseInt(w*0.060)+"px";

document.getElementById("words3").style.left=parseInt(w*0.22)+"px";
document.getElementById("words3").style.top=parseInt(h*0.767)+"px";
document.getElementById("words3").style.fontSize=parseInt(w*0.04)+"px";

document.getElementById("share").style.left=parseInt(w*0.122)+"px";
document.getElementById("share").style.top=parseInt(h*0.82)+"px";
document.getElementById("shareimg").style.width=parseInt(w*0.752)+"px";

var lh  = parseInt(h*0.04);
var t = parseInt(h*0.27);

var data = eval('${data}');
var ltwoh =t;
var sort=${sort};
if(data){
	for(var i=0;i<data.length;i++){
		var imgurl = 'images/game/wstar.png';
		var coo ='#FFFFFF';
		if(i==0){
			imgurl='images/game/ystar.png';
			coo='#f2c115';
		}else if(i==1){
			coo='#fc7b7f';
			imgurl='images/game/pstar.png';
		}else if(i==2){
			coo='#06b702';
			imgurl='images/game/gstar.png';
		}
		var dis = document.createElement("div");
		var cs = dis.style;
		cs.top = parseInt(t+i*lh)+'px';
		cs.left = parseInt(w*0.18)+'px';
		cs.width =  parseInt(w*0.65)+'px';
		cs.height =  (lh-5)+'px';
		ltwoh =parseInt(t+i*lh); 
		/*if(i>8){
			cs.left = parseInt(w*0.19)+'px';
			cs.width =  parseInt(w*0.74)+'px';
		}*/
		cs.color=coo;
		//cs.height = parseInt(lh)-15+"px";
		cs.margin='10px auto';
		cs.position ='absolute';
		cs.align ='center';
		if((i+1==sort))
			cs.border='1px solid #FFFFFF';
		dis.zIndex = 1;
		var dh  = (lh*0.2)+'px';
		var fs = (lh*0.6)+'px';
		var fss = (lh*0.55)+'px';
		var fw = (lh*6.8)+'px';
		var lw = (lh*1.8)+'px'; 
		var iw =(lh*0.65)+'px';
		var usort = (i+1);
		var carown = data[i].own;
		var wor= usort+'、'+carown;
		if(i==9&&sort>10){
			cs.border='1px solid #FFFFFF';
			usort = sort;
			wor = "您的分数"
		}
		dis.innerHTML='<div style="width:'+fw+';text-align:left;float:left;font-size:'+fs+';" ><img src='+imgurl+' width="'+iw+'"/>&nbsp;&nbsp; '+wor+'</div>'+
				'<div style="width:'+lw+';text-align:center;float:right;font-size:'+fss+'" > '+data[i].score+'分</div>';
		document.body.appendChild(dis);
	}
}

var line2 = document.getElementById('line2').style;
line2.top=parseInt(ltwoh+h*0.064)+"px";
line2.left=parseInt(h*0.10)+"px";

document.getElementById('lineimg2').style.width=parseInt(w*0.67)+"px";
</script>
</html>
<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=1">
<meta content="yes" name="apple-mobile-web-app-capable">
<meta name="apple-mobile-web-app-status-bar-style" content="black">
<meta http-equiv="x-ua-compatible" content="IE=edge">
<title>车主端常见问题</title>
<style type="text/css">
.content{width:300px;margin:10px auto;font-family:"微软雅黑", sans-serif, Arial, Verdana;font-size:12px;line-height:20px;}
.title{width:100px;margin:10px auto;line-height:33px;}
.boot{font-size:10px;line-height:14px;}
a{color:#ff99ff;text-decoration:none;cursor:pointer;}
hr{color:#ffeeff;}
</style>
</head>
<body>
<div id="content" class="content" >
<span style="margin:3px auto;">
</span>
<div style='line-height:30px;font-size:17px;'>
<div id="div1" style='display:none;'>
<b>如何使用手机支付停车费？</b><br/><br/>
 VIP卡会员：进场和出场时刷VIP卡，如果账户内余额充足会自动支付。<br/>普通会员：出场时打开停车宝App收到结算订单，点击支付，如果没有 订单，可以扫描收费员二维码进行支付。</br></div>
 <div id="div2" style='display:none;'>
<b>VIP卡如何申请及使用？</b><br/><br/>
在支持停车宝支付的车场向收费员申请即可免费办理，出入车场刷VIP卡即可快速支付通行。<br/></div>
<div id="div3" style='display:none;'>
<b>账户如何充值？</b><br/><br/>
点击"我的账户" ->"充值"，选择支付宝或微信支付即可完成充值。<br/></div>
<div id="div4" style='display:none;'>
<b>停车券如何使用？</b><br/><br/>
当你用停车宝支付停车费时会默认优先使用停车券，不足部分使用余额支付<br/></div>
<div id="div5" style='display:none;'>
<b>VIP卡丢失怎么办？</b><br/><br/>
如不慎丢失请先在"设置"中关闭"自动支付"，然后去停车场补办新卡即可，老卡自动注销。<br/></div>
<div id="div6" style='display:none;'>
<b>地图上显示的所有车场都可以手机支付么？</b><br/><br/>
地图右上角的可支付图标为绿色打开状态时，地图上显示的车场都是可以手机支付。<br/></div>
<div id="div7" style='display:none;'>
<b>所在车场不支持停车宝支付怎么办？</b><br/><br/>
别着急，我们正火力覆盖。也欢迎您点击"戳我领30元"的图标推荐收费员下载停车宝车场端进行管理收费，推荐成功我们将犒赏您30元停车费。<br/></div>
<div id="div8" style='display:none;'>
<b>停车券如何获取？</b><br/><br/>
除首次注册所赠停车券外，每天的第一笔订单我们会返3元停车券，同时可以获取一次礼包分享机会。<br/></div>
<div><br/><br/><br/><br/>&nbsp;&nbsp;&nbsp;&nbsp;<span  onclick="backhelp();">返回问题列表&nbsp;<img src="images/redo.gif"/> </span></div>
</div>
 
</div>

</body>

<script type="text/javascript">
function getParam(paramName)
{
        paramValue = "";
        isFound = false;
        if (this.location.search.indexOf("?") == 0 && this.location.search.indexOf("=")>1)
        {
            arrSource = unescape(this.location.search).substring(1,this.location.search.length).split("&");
            i = 0;
            while (i < arrSource.length && !isFound)
            {
                if (arrSource[i].indexOf("=") > 0)
                {
                     if (arrSource[i].split("=")[0].toLowerCase()==paramName.toLowerCase())
                     {
                        paramValue = arrSource[i].split("=")[1];
                        isFound = true;
                     }
                }
                i++;
            }   
        }
   return paramValue;
};

var id = getParam("q");

document.getElementById("div"+id).style.display='';

function backhelp(){
	location = "help.jsp"
}
</script>
</html>

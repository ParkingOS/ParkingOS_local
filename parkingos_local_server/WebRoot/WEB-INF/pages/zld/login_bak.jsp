<%@ page language="java" contentType="text/html; charset=gb2312"
	pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<meta http-equiv="keywords" content="呼叫中心,tq,crm,客户管理">
<meta http-equiv="description" content="呼叫中心,tq,crm,客户管理">
<noscript>登录到系统需要启用 JavaScript。当前 Web 浏览器不支持 JavaScript
	或阻止了脚本。</noscript>
<title>停车宝业务系统</title>
<!--勿改-->
<style type="text/css">
html {
	overflow: auto;
}

* {
	line-height: 142%;
}

body {
	color: #000;
	font-family: "Microsoft Yahei", Verdana, Simsun, "Segoe UI",
		"Segoe UI Web Regular", "Segoe UI Symbol", "Helvetica Neue",
		"BBAlpha Sans", "S60 Sans", Arial, "sans-serif";
	font-size: 88%;
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	margin-left: 0px;
	direction: ltr;
	background-color: rgb(255, 255, 255);
}

label {
	color: #000;
	cursor: pointer;
}

input[type=text],input[type=password],input[type=email],input[type=tel]
	{
	ime-mode: inactive;
}

input.ltr_override {
	direction: ltr;
}

input[type=text],input[type=password] {
	width: 302px;
	height: 1.46em;
	padding-top: 4px;
	padding-right: 8px;
	padding-bottom: 4px;
	padding-left: 8px;
}

input[type=text],input[type=password] {
	width: 302px;
}

form {
	margin-top: 0px;
	margin-right: 0px;
	margin-bottom: 0px;
	margin-left: 0px;
}

a,a:visited {
	text-decoration: none;
	cursor: pointer;
	color: #009;
}

a:hover {
	text-decoration: none;
	cursor: pointer;
	color: #009;
}

a:hover:active {
	text-decoration: none;
	cursor: pointer;
	color: #009;
}

.TextBold,.TextSemiBold {
	font-family: "Microsoft Yahei", Verdana, Simsun, "Segoe UI Web Semibold",
		"Segoe UI Web Regular", "Segoe UI", "Segoe UI Symbol",
		"Helvetica Neue", Arial;
	font-weight: bold;
}

input.default {
	cursor: pointer;
	height: 2.14em;
	color: #ffffff;
	font-weight: 700;
	padding-top: 3px;
	padding-right: 12px;
	padding-bottom: 5px;
	padding-left: 12px;
	font-family: "Microsoft Yahei", Verdana, Simsun, "Segoe UI Web Semibold",
		"Segoe UI Web Regular", "Segoe UI", "Segoe UI Symbol",
		"Helvetica Neue", Arial;
	font-size: 100%;
	min-width: 6em;
	background-color: #71A810;
	border: 1px solid #386601;
	border-top-left-radius: 0px;
	border-top-right-radius: 0px;
	-moz-border-radius-topleft: 0px;
	-moz-border-radius-topright: 0px;
	-webkit-border-top-left-radius: 0px;
	-webkit-border-top-right-radius: 0px;
	border-bottom-left-radius: 0px;
	border-bottom-right-radius: 0px;
	-moz-border-radius-bottomleft: 0px;
	-moz-border-radius-bottomright: 0px;
	-webkit-border-bottom-left-radius: 0px;
	-webkit-border-bottom-right-radius: 0px;
	-webkit-background-clip: padding-box;
}

input[type=text],input[type=password] {
	width: 320px;
	color: #212121;
	padding-top: 4px;
	padding-right: 8px;
	padding-bottom: 4px;
	padding-left: 8px;
	font-family: "Microsoft Yahei", Verdana, Simsun, "Segoe UI",
		"Segoe UI Web Regular", "Segoe UI Symbol", "Helvetica Neue",
		"BBAlpha Sans", "S60 Sans", Arial, "sans-serif";
	font-size: 100%;
	border-top-color: rgb(186, 186, 186);
	border-right-color: rgb(186, 186, 186);
	border-bottom-color: rgb(186, 186, 186);
	border-left-color: rgb(186, 186, 186);
	border-top-width: 1px;
	border-right-width: 1px;
	border-bottom-width: 1px;
	border-left-width: 1px;
	border-top-style: solid;
	border-right-style: solid;
	border-bottom-style: solid;
	border-left-style: solid;
	background-color: rgba(255, 255, 255, 0.8);
}

input[type=text],input[type=password] {
	height: 1.57em;
}

input[type=text]:focus,input[type=password]:focus {
	border-top-color: rgb(92, 92, 92);
	border-right-color: rgb(92, 92, 92);
	border-bottom-color: rgb(92, 92, 92);
	border-left-color: rgb(92, 92, 92);
	outline-width: medium;
	outline-style: none;
	outline-color: invert;
}

.errorDiv {
	color: #cc0000;
	line-height: 178%;
	font-size: 86%;
	margin-top: 18px;
	margin-bottom: 12px;
	white-space: normal;
}

div.placeholder {
	color: #666;
	margin-top: 6px;
	margin-left: 9px;
	white-space: nowrap;
	background-color: transparent;
}

div.ltr_override.placeholder {
	text-align: left;
	margin-right: auto;
	margin-left: 9px;
}

div.textbox {
	width: 320px;
}

div.section {
	margin-bottom: 30px;
}

div.row,div.section>div {
	margin-bottom: 8px;
}

div.small.row,div.section>div.small {
	font-size: 86%;
	margin-bottom: 6px;
}

div.label.row,div.section>div.label {
	font-size: 86%;
	margin-bottom: 4px;
}

div.first.errorDiv {
	margin-top: 0px;
}

div.centerParent,td.centerParent {
	text-align: center;
}

div.center,table.center {
	text-align: left;
	margin-right: auto;
	margin-left: auto;
}

div.SignUp {
	margin-top: 50px;
}

div.floatLeft {
	float: left;
}

div.signInHeader {
	margin-left: 50px;
	font-size: 28px;
	color: #666;
}

div.signUpFloat {
	left: 0px;
	bottom: 0px;
	margin-left: 50px;
	position: absolute;
}

div.footer {
	font-size: 86%;
	border-top-color: #ccc;
	border-top-width: 1px;
	border-top-style: solid;
}

table.footer {
	width: 100%;
	padding-top: 10px;
	padding-right: 0px;
	padding-bottom: 10px;
	padding-left: 0px;
}

td.footerspace {
	width: 10px;
}

span.copyright {
	color: #666;
}
</style>
<script language="javascript" type="text/javascript">
if (top.location != self.location) {
    top.location = self.location
};
function writeCookie(key, value, duration, domain, path) {
    value = encodeURIComponent(value);
    if (domain) {
        value += "; domain=" + domain
    }
    if (path) {
        value += "; path=" + path
    }
    if (duration) {
        var date = new Date();
        date.setTime(date.getTime() + duration * 60 * 60 * 1000);
        value += "; expires=" + date.toGMTString()
    }
    document.cookie = key + "=" + value
}
function readCookie(key) {
    var cookieValue = "";
    var search = key + "=";
    if (document.cookie.length > 0) {
        offset = document.cookie.indexOf(search);
        if (offset != -1) {
            offset += search.length;
            end = document.cookie.indexOf(";", offset);
            if (end == -1) {
                end = document.cookie.length
            }
            cookieValue = unescape(document.cookie.substring(offset, end))
        }
    }
    return cookieValue
}
function getObj(id) {
    if (document.getElementById) {
        return document.getElementById(id)
    } else {
        if (document.all) {
            return document.all(id)
        }
    }
}
function SchkRememberMe() {
    if (getObj("AutoLogin").checked == true) {
        getObj("chkRememberMe").checked = true
    }
}
function SAutoLogin() {
    if (getObj("chkRememberMe").checked == false) {
        getObj("AutoLogin").checked = false
    }
}

function SetCheckCookies() {
    var userIdName = getObj("username").value;
    var userIdNamePsw = getObj("pass").value;
    if (getObj("AutoLogin").checked == true) {
        writeCookie("tquin", userIdName, 8760);
        writeCookie("tquinpas", userIdNamePsw, 8760)
    } else {
        if (getObj("chkRememberMe").checked == true) {
            writeCookie("tquin", userIdName, 8760);
            writeCookie("tquinpas", "")
        } else {
            writeCookie("tquin", "");
            writeCookie("tquinpas", "")
        }
    }
}
window.onload = function() {
    getObj("newsFrame").src = "htmls/tqnews.html?" + (new Date().toLocaleDateString());
    var RuserIdName = readCookie("tquin");
    var RuserIdNamePsw = readCookie("tquinpas");
    getObj("username").value = RuserIdName;
    getObj("pass").value = RuserIdNamePsw;
    RuserIdName != "" ? (getObj("chkRememberMe").checked = true, getObj("phholderName").style.display = "none") : getObj("chkRememberMe").checked = false;
    RuserIdNamePsw != "" ? (getObj("AutoLogin").checked = true, getObj("phholderPass").style.display = "none") : getObj("AutoLogin").checked = false;
    if (RuserIdName == "") {
        getObj("username").focus()
    } else {
        if (RuserIdNamePsw == "") {
            getObj("pass").focus()
        } else {}
    }
};
function getDomainName() {
    var host = window.location.hostname;
    var port = window.location.port;
    var domainName = "http://" + host;
    if (port != "") {
        domainName += ":" + port
    }
    getObj("domainName").value = domainName
};


</script>
</head>
<body>
	<div style="height: 40px;"></div>
	<div class="centerParent" style="width: 100%;">
		<div class="center" style="width: 885px;">
			<div class="centerParent">
				<div class="center" style="width: 845px;">
					<div class="floatLeft" style="width: 475px;">
						<div style="width: 475px;">
							<iframe id="newsFrame" width="475" height="460" src=""
								frameborder="0" marginwidth="0" marginheight="0" scrolling="no"></iframe>
						</div>
					</div>
					<div class="floatLeft" style="width: 370px; position: relative;">
						<div style="height: 20px;"></div>
						<div class="signInHeader">欢迎登录停车宝业务系统</div>
						<div style="height:30px;"></div>
						<div class="floatLeft" style="width: 50px; height: 370px;"></div>
						<div class="floatLeft" style="width: 320px;">
							<div id="ieupdatediv"></div>
							<div id="logindiv">
								<form name="loginForm" id="loginForm" method="post"
									action="dologin.do" onsubmit="return submitForm();">
									<input type="hidden" id="domainName" name="domainName" />
									<div class="section">
										<div style="display:block;">
											<div class="errorDiv first" id="error_message">${errormessage }</div>
										</div>
										<div id="Error_Username" style="display:none;">
											<div class="errorDiv first">请输入登录名或数字帐号</div>
										</div>
										<div class="row textbox">
											<div style="width: 100%; position: relative;">
												<input name="username" class="ltr_override" id="username" type="text"
													onfocus="getObj('phholderName').style.display='none'"
													onblur="if(this.value=='')getObj('phholderName').style.display='block'">
												<div id="phholderName" class="phholder"
													style="left: 0px; top: 0px; width: 100%; position: absolute; z-index: 5;">
													<div class="placeholder ltr_override" style="cursor: text;"
														onclick="document.getElementById('username').focus()">登录名或数字帐号</div>
												</div>
											</div>
										</div>
										<div id="Error_Password" style="display:none;">
											<div class="errorDiv">请输入密码</div>
										</div>
										<div class="row textbox">
											<div style="width: 100%; position: relative;">
												<input name="pass" id="pass" type="password"
													autocomplete="off"
													onfocus="getObj('phholderPass').style.display='none'"
													onblur="if(this.value=='')getObj('phholderPass').style.display='block'">
												<div id="phholderPass" class="phholder"
													style="left: 0px; top: 0px; width: 100%; position: absolute; z-index: 5;">
													<div class="placeholder" style="cursor: text;"
														onclick="document.getElementById('passWord').focus()">密码</div>
												</div>
											</div>
										</div>
										<div>
											<input name="chkRememberMe" id="chkRememberMe"
												type="checkbox" onclick="SAutoLogin();"><label
												for="chkRememberMe">记住账户</label><input name="AutoLogin"
												id="AutoLogin" type="checkbox" onclick="SchkRememberMe();"><label
												for="AutoLogin">记住密码</label>
										</div>
									</div>
									<div class="section">
										<input name="button" class="default" hidefocus="true"
											type="submit" value="登 录" onclick='SetCheckCookies()'>
									</div>
									<!--  <div class="section">
										<div class="row small">
											<a target="_blank"
												href="http://s.zhenlaidian.cn/htmls/forgotpass.html">忘记密码?</a>
										</div>
									</div>
								</form>
							</div>
						</div>
						<!-- <div class="SignUp signUpFloat">
							<span>没有呼叫中心帐户?</span><a target="_blank"
								href="http://s.zhenlaidian.cn/regist.do">立即注册</a>
						</div>-->
					</div>
				</div>
			</div>
			<div style="height: 20px; clear: both;"></div>
			<div class="footer centerParent" style="clear: both;">
				<div class="center" style="width: 845px;">
					<table class="footer" cellspacing="0" cellpadding="0">
						<tbody>
							<tr>
								<td align="left"><table cellspacing="0" cellpadding="0">
										<tbody>
											<tr>
												<td style="text-align: left;"><span class="copyright">&copy;
														2014 - 2016 s.zhenlaidian.cn All Rights Reserved</span></td>
												<td style="text-align: left;"><span></span></td>
											</tr>
										</tbody>
									</table></td>
								<td></td>
								<td align="right">
								    <table cellspacing="0" cellpadding="0">
										<tbody>
											<tr>
												<td style="text-align: right;"><a class="footerlink"
													target="_blank" href="http://www.miitbeian.gov.cn/">京ICP备14031523</a></td>
												<td style="text-align: right;">
												</td>
												<td class="footerspace" aria-hidden="true">&nbsp;</td>
												<td class="footerspace" aria-hidden="true">&nbsp;</td>
											</tr>
										</tbody>
									</table>
									
									</td>
							</tr>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
<script language="javascript">
var w = navigator.userAgent.toLowerCase();
var ieMode = document.documentMode;
var iev = (/msie/.test(w) && !/opera/.test(w)) ? (ieMode ? ieMode: (!window.XMLHttpRequest ? 6 : 7)) : false;
if (iev && iev == 6) {
    var updateTip = "<div style=\"width:100%;float:left;text-align:left;\">您的IE浏览器版本过旧，部分功能将无法使用，<b style=\"color:#c00\">请升级浏览器后再登录。</b><br/>推荐您安装<b style=\"color:#c00\">8.0</b>版本的IE浏览器。您可以：<br/><br />1、请升级您的浏览器。通过下列地址下载适合您电脑的版本并安装。<br />下载地址(1)：<a style=\"color:#c00\" href=\"http://download.microsoft.com/download/1/6/1/16174D37-73C1-4F76-A305-902E9D32BAC9/IE8-WindowsXP-x86-CHS.exe\" target=\"_blank\">微软官方下载</a><br />下载地址(2)：<a style=\"color:#c00\" href=\"http://download.pchome.net/internet/browser/browser/down-86392-1.html\" target=\"_blank\">电脑之家在线下载</a><br />下载地址(3)：<a style=\"color:#c00\" href=\"http://dl.pconline.com.cn/html_2/1/104/id=49581&pn=0&linkPage=1.html\" target=\"_blank\">太平洋在线下载</a><br />下载地址(4)：<a style=\"color:#c00\" href=\"http://www.microsoft.com/zh-cn/download/internet-explorer-8-details.aspx\" target=\"_blank\">微软官方下载2</a><br /><br />2、联系技术人员帮您解决。</div>";
    document.getElementById("logindiv").style.display = "none";
    document.getElementById("ieupdatediv").innerHTML = updateTip
};
getDomainName();
var ipseek_url = "http://www.ip.cn/getip.php?action=queryip&ip_url=";
function isNum(ch) {
    if (ch >= "0" && ch <= "9") {
        return true
    }
    return false
}
function isRightNumStr(numStr) {
    var rightFlag = true;
    for (i = 0; i < numStr.length; i++) {
        if (false == isNum(numStr.charAt(i))) {
            rightFlag = false;
            break
        }
    }
    return rightFlag
}
function submitForm() {
    var uin = document.getElementById("username");
    var passWord = document.getElementById("pass");
    if (uin == null || uin.value.length < 1) {
        document.getElementById("Error_Username").style.display = "block";
        uin.focus();
        return false
    } else {
        document.getElementById("Error_Username").style.display = "none"
    }
    if (passWord == null || passWord.value.length < 1) {
        document.getElementById("Error_Password").style.display = "block";
        passWord.focus();
        return false
    } else {
        document.getElementById("Error_Password").style.display = "none"
    }
    return true
}
function queryIp(ip) {
    queryIpFrame.location = "" + ip;
    document.getElementById("queryIpFrameDIV").style.visibility = "visible";
    document.getElementById("queryIpFrameDIV").style.display = ""
}
function forceLogin() {
    document.getElementById("isForceLogin").value = "1";
    document.forms["loginForm"].submit()
};
</script>
</body>
</html>
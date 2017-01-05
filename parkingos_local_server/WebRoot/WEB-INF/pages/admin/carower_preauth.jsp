<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>车牌审核</title>
<link href="css/imagestyle.css" type="text/css" rel="stylesheet" />
<script src="js/jquery.js" type=text/javascript></script>
<script src="js/imagecontrol.js" type=text/javascript></script>
</head>
<body>
<div class="uldiv">
	<div class="scrollcontainer1">
		<ul>
			<li><img src="attendant.do?action=getpic&db=user_dirvier_pics&id=${url1}" width="590px" height="390px"/></li>
			<li><img src="attendant.do?action=getpic&db=user_dirvier_pics&id=${url2}" width="590px" height="390px"/></li>
		</ul>
	</div>
	<div class="btndiv">
		<a class="abtn aleft" href="#left">左移</a>
		<a class="abtn aright" href="#right">右移</a>
	</div>
</div>
<script type="text/javascript">
$(function(){
	$(".uldiv").Xslider({
		unitdisplayed:1,
		numtoMove:1,
		speed:300,
		scrollobjSize:Math.ceil($(".uldiv").find("li").length/1)*600
	})
})
</script>

<div id="alllayout">
	<div style="width:100%;float:left;height:60px;border-bottom:1px solid #ccc" id="top"></div>
	<div style="width:100%;float:left;">
    	<div id="right" style="width:auto;border-left:1px solid #ccc;float:left"></div>
	</div>
</div>	
<div id="loadtip" style="display:none;"></div>
<div id="cover" style="display:none;"></div>
<script src="js/tq.js?08137" type="text/javascript">//基本</script>
<script src="js/tq.public.js?08031" type="text/javascript">//公共</script>
<script src="js/tq.window.js?008136" type="text/javascript">//弹窗</script>
<script src="js/tq.form.js?08301" type="text/javascript">//表单</script>
<script src="js/tq.validata.js?0817" type="text/javascript">//验证</script>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<style type="text/css">
html,body {
	overflow:auto;
}
</style>
</body>
<script type="text/javascript">
//取字段
var Obj = document.getElementById("alllayout");
var topO = document.getElementById("top");
var rightO = document.getElementById("right");

rightO.style.width = T.gww()  + "px";
//rightO.style.height = T.gwh() - 50 + "px";

T.bind(window,"resize",function(){
    rightO.style.width = T.gww() + "px";
   // rightO.style.height = T.gwh() - 50 + "px"
})

var fields = [
		{fieldcnname:"备注",fieldname:"remark",fieldvalue:'${remark}',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"审核状态",fieldname:"isauth",fieldvalue:'',inputtype:"select", noList:[{"value_no":1,"value_name":"审核通过"},{"value_no":-1,"value_name":"未审核通过"},{"value_no":-2,"value_name":"无效车牌"}],twidth:"100" ,height:"",issort:false},
	];
var bHtml = "<div style='margin-top:10px;margin-buttom:20px;width:100%;overflow:hidden;text-align:center;font-size:25px;'>";
bHtml += "车牌号码&nbsp;&nbsp;&nbsp;<font color='red' size='6' ><b>${carnumber}</b></font>";
bHtml += "</div>";
topO.innerHTML=bHtml;

var accountForm =
new TQForm({
	formname: "opconfirm",
	formObj:rightO,
	suburl:"carower.do?action=authuser&id=${id}&uin=${uin}",
	method:"POST",
	Callback:function(f,r,c,o){
		if(c=='1'){
			T.loadTip(1,"保存成功！",3,null);
		}else{
			T.loadTip(1,"操作失败！",3,null);
		}
	},
	formAttr:[{
		formitems:[{kindname:"",kinditemts:fields}]
	}]
});
accountForm.C();
</script>
</html>
<script type="text/javascript">
T.maskTip(0,"","");//加载结束
</script>
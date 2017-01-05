<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>停车员审核</title>
<link href="css/imagestyle.css" type="text/css" rel="stylesheet" />
<script src="js/jquery.js" type=text/javascript></script>
<script src="js/imagecontrol.js" type=text/javascript></script>
</head>
<body>
<div class="uldiv">
	<div class="scrollcontainer">
		<ul>
		<c:forEach items="${pics}" var='pic' >
			<li><img src="collector.do?action=getpic&id=${pic }" width="590px" height="590px"/></li>
		</c:forEach>
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
rightO.style.height = T.gwh() - 50 + "px";

T.bind(window,"resize",function(){
    rightO.style.width = T.gww() + "px";
    rightO.style.height = T.gwh() - 50 + "px"
})

function getMarkets(){
	var childs = eval(T.A.sendData("getdata.do?action=markets"));
	return childs;
}
var marketers = getMarkets();
var fields = [
		{fieldcnname:"车场编号",fieldname:"id",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false,edit:false},
		{fieldcnname:"姓名",fieldname:"nickname",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"车场名称",fieldname:"company_name",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"详细地址",fieldname:"address",fieldvalue:'',inputtype:"showmap", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"经度",fieldname:"longitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",edit:false},
		{fieldcnname:"纬度",fieldname:"latitude",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",edit:false},
		{fieldcnname:"车位总数",fieldname:"parking_total",fieldvalue:'',inputtype:"number", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"所属公司",fieldname:"mcompany",fieldvalue:'',inputtype:"text", twidth:"100" ,height:"",issort:false},
		{fieldcnname:"车位类型",fieldname:"stop_type",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"平面排列"},{"value_no":1,"value_name":"立体排列"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"备案号",fieldname:"record_number",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"电子支付",fieldname:"epay",fieldvalue:'',inputtype:"select", noList:[{"value_no":0,"value_name":"不支持"},{"value_no":1,"value_name":"支持"}],twidth:"100" ,height:"",issort:false},
		{fieldcnname:"市场专员",fieldname:"uid",fieldvalue:'',inputtype:"select",noList:marketers, twidth:"60" ,height:"",issort:false},
		{fieldcnname:"备注",fieldname:"visit_content",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false},
		{fieldcnname:"审核状态",fieldname:"ustate",fieldvalue:'',inputtype:"select", noList:[{"value_no":2,"value_name":"新增"},{"value_no":0,"value_name":"审核通过"},{"value_no":3,"value_name":"待补充"},{"value_no":4,"value_name":"待跟进"},{"value_no":5,"value_name":"无价值"}],twidth:"100" ,height:"",issort:false},
		//10
		{fieldcnname:"车场编号",fieldname:"comid",fieldvalue:'',inputtype:"text", twidth:"200" ,height:"",issort:false}
	];
var uin="${uin}";
var umobile = "${umobile}";
var cominfo= eval('${cominfo}');
var bHtml = "<div style='margin-top:20px;margin-buttom:20px;margin-left:29px;width:595px;overflow:hidden;'>";
bHtml += "电话联系他:<font color='red'>"+umobile+"</font>";
bHtml += "</div>";
topO.innerHTML=bHtml;
function getEditFields(){
	var e_f = [];
	for(var j=0;j<fields.length;j++){
		for(var i=0;i<cominfo.length;i++){
			if(cominfo[i].name==fields[j].fieldname){
				fields[j].fieldvalue=cominfo[i].value;
				e_f.push(fields[j]);
				break;
			}
			if(fields[j].inputtype=='select')
				fields[j].width=200;
		}
	}
	return e_f;
}

function getFields(){
	var fs = getEditFields();
	var mfs = [
		{kindname:"车场不存在，创建新车场",kinditemts:fs.slice(0,14)},
		{kindname:"车场已存在，填写已存在的车场编号",kinditemts:fs.slice(14,15)}
		];
	return mfs;
}
var accountForm =
new TQForm({
	formname: "opconfirm",
	formObj:rightO,
	suburl:"collector.do?action=checkcollector&uin="+uin,
	method:"POST",
	Callback:function(f,r,c,o){
		if(c=='1'){
			T.loadTip(1,"保存成功！",3,null);
			parent.document.getElementById("client_detail_"+uin).style.display="none";
			parent.document.getElementById("cover").style.position="";
			parent._collectorT.M();
		}else if(c=='-2'){
			T.loadTip(1,"该收费员已审核！",3,null);
			parent._collectorT.M();
		}else if(c=='-3'){
			T.loadTip(1,"经纬度重复了！",3,null);
		}else if(c=='-4'){
			T.loadTip(1,"该手机号已经注册过管理员！",3,null);
		}else{
			T.loadTip(1,"操作失败！",3,null);
			parent._collectorT.M();
		}
	},
	formAttr:[{
		formitems:getFields()
	}]
});
accountForm.C();
</script>
</html>
<script type="text/javascript">
T.maskTip(0,"","");//加载结束
</script>
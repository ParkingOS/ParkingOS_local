<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>金额设定</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<script type="text/javascript" src="js/tq.js"></script>
	<script type="text/javascript" src="js/tq.hash.js"></script>
	<script type="text/javascript" src="js/tq.newtree.js"></script>
	<script type="text/javascript" src="js/tq.window.js"></script>
	<script language="javascript" src="js/tq.scrollbar.js"></script>

<style type="text/css">
	 *{font-size: 24px;}
    body{height: 100%;}
    body{background-color:Transparent;}
    input,select{border-radius:10px;}
    #content{ padding: 50px;}
	.tc{text-align:center;margin-top:20px}
	.ntb th,.ntb td{border:none;}
	.w150{width:150px}
	.p10{padding:10px;}
	.w200{width:200px}
	.ml30{margin-left:30px}
	.c{width: 300px;height: 30px;line-height: 30px;margin: 20px;}
 </style>
 <script type="text/javascript">
var parkid = ${parkid};
function change(type){
	type = type==0?1:0;
	var ret = T.A.sendData("parksetting.do?action=setisshow&isshow="+type+"&id="+parkid)
	if(ret==1){
		T.loadTip(1,"修改成功!",2,"");
		location = "parksetting.do?action=parkclientset&id="+parkid;
	}else{
		T.loadTip(1,"修改失败!",2,"");
	}
}
function changecancel(type){
	type = type==0?1:0;
	var ret = T.A.sendData("parksetting.do?action=setcancel&iscancel="+type+"&id="+parkid)
	if(ret==1){
		T.loadTip(1,"修改成功!",2,"");
		location = "parksetting.do?action=parkclientset&id="+parkid;
	}else{
		T.loadTip(1,"修改失败!",2,"");
	}
}
 </script>
</head>
<body>

	<div style="margin-left:30px;margin-top:50px;font-size:24px;">订单结算时是否有取按钮：${mg2}&nbsp;&nbsp;<button onclick='changecancel(${iscancel})'>${bmg2}</button></div>
	<div style="margin-left:30px;margin-top:50px;font-size:24px;">收费员客户端是否显示直付订单：${mg}&nbsp;&nbsp;<button onclick='change(${isshowepay})'>${bmg}</button></div>

</body>
</html>

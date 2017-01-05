<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>拜访记录</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">
<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>
<style>
a { text-decoration:none; color:#f30; }
</style>

</head>
<body>
<div id="visitdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var btime="${btime}";
var etime="${etime}";
var total = '${total}';
var uid = "${uid}";
var tip = "拜访记录";
var _mediaField = [
		{fieldcnname:"拜访记录ID",fieldname:"id",inputtype:"text", twidth:"150" ,issort:false,fhide:true},
		{fieldcnname:"市场专员",fieldname:"nickname",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"停车场",fieldname:"company_name",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"联系人",fieldname:"contact",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"联系人电话",fieldname:"mobile",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"拜访内容",fieldname:"visit_content",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"拜访时间",fieldname:"create_time",inputtype:"text", twidth:"150",issort:false},
		{fieldcnname:"拍照地址",fieldname:"address",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"查看图片",fieldname:"id",inputtype:"text", twidth:"100",issort:false,
			process:function(value,cid,id){
				return "<a href=# onclick=\"viewdetail('hn','"+value+"','"+cid+"')\" style='color:blue'>查看图片</a>";
			}}
	];
var _visitdetailT = new TQTable({
	tabletitle:tip,
	ischeck:false,
	tablename:"visitdetail_tables",
	dataUrl:"visitanlysis.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=visitdetail&uid="+uid+"&btime="+btime+"&etime="+etime,
	tableObj:T("#visitdetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:false
});

function coutomsearch(){
	var html=    "时间："+btime+" 至 "+etime+"，合计：<font color='red'>"+total+"</font> 条";//"&nbsp;&nbsp;";
	return html;
}

function viewdetail(type,value,id){
	var light = parent.parent.document.getElementById('visitpic');
	var childNodes = light.childNodes;
	for(var i=0;i<childNodes.length;i++){
		light.removeChild(childNodes[i]);
	}
	var img1 = new Image();
	img1.src="marketerpics.do?action=downloadvisitpics&id="+value;
	light.appendChild(img1);
	parent.parent.document.getElementById('light').style.display='block';
	parent.parent.document.getElementById('fade').style.display='block'
	return null;
}
_visitdetailT.C();
</script>

</body>
</html>

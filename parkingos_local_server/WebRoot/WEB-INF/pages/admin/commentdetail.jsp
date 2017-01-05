<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>评价列表</title>
<link href="css/tq.css" rel="stylesheet" type="text/css">
<link href="css/iconbuttons.css" rel="stylesheet" type="text/css">

<script src="js/tq.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.public.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.datatable.js?0817" type="text/javascript">//表格</script>
<script src="js/tq.hash.js?0817" type="text/javascript">//哈希</script>
<script src="js/tq.stab.js?0817" type="text/javascript">//切换</script>
<script src="js/My97DatePicker/WdatePicker.js" type="text/javascript">//日期</script>

</head>
<body>
<div id="commentdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >
var parkid='${parkid}';
var uin='${uin}';
var tip = "评论列表";
var _mediaField = [
		{fieldcnname:"编号",fieldname:"comid",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"车场",fieldname:"company_name",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"车主手机号",fieldname:"mobile",inputtype:"text", twidth:"100" ,issort:false},
		{fieldcnname:"车主照牌",fieldname:"car_number",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"评论",fieldname:"comment",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"日期",fieldname:"create_time",inputtype:"text", twidth:"150" ,issort:false}
	];
var _commentdetailT = new TQTable({
	tabletitle:tip,
	ischeck:false,
	tablename:"commentdetail_tables",
	dataUrl:"parkpraise.do",
	iscookcol:false,
	buttons:false,
	quikcsearch:coutomsearch(),
	param:"action=commentdetail&parkid="+parkid+"&uin="+uin,
	tableObj:T("#commentdetailobj"),
	fit:[true,true,true],
	tableitems:_mediaField,
	allowpage:false,
	isoperate:false
});

function coutomsearch(){
	var html=    "";
	return html;
}

_commentdetailT.C();
</script>

</body>
</html>

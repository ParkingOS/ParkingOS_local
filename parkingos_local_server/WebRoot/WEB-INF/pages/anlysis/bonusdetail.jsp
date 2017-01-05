<%@ page language="java" contentType="text/html; charset=gb2312"
    pageEncoding="gb2312"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=gb2312">
<title>NFC记录</title>
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
<div id="bonusdetailobj" style="width:100%;height:100%;margin:0px;"></div>
<script >

var details=${details};//{"page":1,"total":2,"rows": [{"id":"1","cell":["通话中","业务咨询"]},{"id":"2","cell":["接入中","售后服务"]}]};

var _bonusdetailT = new TQTable({
	tabletitle:"分享详情",
	ischeck:false,
	tablename:"bonusdetail_tables",
	dataUrl:"bonusanly.do",
	iscookcol:false,
	dataorign:1,
	hotdata:details,
	quikcsearch:'${tips}',
	dbuttons:false,
	buttons:false,
	searchitem:false,
	param:"action=quickquery",
	tableObj:T("#bonusdetailobj"),
	allowpage:false,
	fit:[true,true,true],
	tableitems: [
		{fieldcnname:"车主",fieldname:"car",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"消费日期",fieldname:"cdate",inputtype:"text", twidth:"150" ,issort:false},
		{fieldcnname:"停车场",fieldname:"cname",inputtype:"text", twidth:"200",issort:false},
		{fieldcnname:"结算金额",fieldname:"total",inputtype:"text", twidth:"100",issort:false},
		{fieldcnname:"支付方式",fieldname:"ptype",inputtype:"select",noList:[{"value_no":0,"value_name":"普通支付"},{"value_no":1,"value_name":"直付给收费员"}], twidth:"200",issort:false},
		{fieldcnname:"收费员",fieldname:"collect",inputtype:"text", twidth:"100",issort:false}
	],
	isoperate:false
});
_bonusdetailT.C();

</script>

</body>
</html>
